/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// -  4.3

import Foundation

public class ProfilingATNSimulator: ParserATNSimulator {
    private(set) var decisions: [DecisionInfo]
    internal var numDecisions: Int = 0

    internal var _sllStopIndex: Int = 0
    internal var _llStopIndex: Int = 0

    internal var currentDecision: Int = 0
    internal var currentState: DFAState?

    /// At the point of LL failover, we record how SLL would resolve the conflict so that
    /// we can determine whether or not a decision / input pair is context-sensitive.
    /// If LL gives a different result than SLL's predicted alternative, we have a
    /// context sensitivity for sure. The converse is not necessarily true, however.
    /// It's possible that after conflict resolution chooses minimum alternatives,
    /// SLL could get the same answer as LL. Regardless of whether or not the result indicates
    /// an ambiguity, it is not treated as a context sensitivity because LL prediction
    /// was not required in order to produce a correct prediction for this decision and input sequence.
    /// It may in fact still be a context sensitivity but we don't know by looking at the
    /// minimum alternatives for the current input.
    internal var conflictingAltResolvedBySLL: Int = 0

    public init(_ parser: Parser) {
        decisions = [DecisionInfo]()

        super.init(parser,
                parser.getInterpreter().atn,
                parser.getInterpreter().decisionToDFA,
                parser.getInterpreter().sharedContextCache!)

        numDecisions = atn.decisionToState.count
        for i in 0..<numDecisions {
            decisions[i] = DecisionInfo(i)
        }


    }

    override
    public func adaptivePredict(_ input: TokenStream, _ decision: Int,_ outerContext: ParserRuleContext?) throws -> Int {
        var outerContext = outerContext
        self._sllStopIndex = -1
        self._llStopIndex = -1
        self.currentDecision = decision
        var start: Int64 = Int64(Date().timeIntervalSince1970) //System.nanoTime(); // expensive but useful info
        var alt: Int = try  super.adaptivePredict(input, decision, outerContext)
        var stop: Int64 = Int64(Date().timeIntervalSince1970)  //System.nanoTime();
        decisions[decision].timeInPrediction += (stop - start)
        decisions[decision].invocations += 1

        var SLL_k: Int64 = _sllStopIndex - _startIndex + 1
        decisions[decision].SLL_TotalLook += SLL_k
        decisions[decision].SLL_MinLook = decisions[decision].SLL_MinLook == 0 ? SLL_k : min(decisions[decision].SLL_MinLook, SLL_k)
        if SLL_k > decisions[decision].SLL_MaxLook {
            decisions[decision].SLL_MaxLook = SLL_k
            decisions[decision].SLL_MaxLookEvent =
                    LookaheadEventInfo(decision, nil, input, _startIndex, _sllStopIndex, false)
        }

        if _llStopIndex >= 0 {
            var LL_k: Int64 = _llStopIndex - _startIndex + 1
            decisions[decision].LL_TotalLook += LL_k
            decisions[decision].LL_MinLook = decisions[decision].LL_MinLook == 0 ? LL_k : min(decisions[decision].LL_MinLook, LL_k)
            if LL_k > decisions[decision].LL_MaxLook {
                decisions[decision].LL_MaxLook = LL_k
                decisions[decision].LL_MaxLookEvent =
                        LookaheadEventInfo(decision, nil, input, _startIndex, _llStopIndex, true)
            }
        }

        defer {
            self.currentDecision = -1
        }
        return alt


    }

    override
    internal func getExistingTargetState(_ previousD: DFAState, _ t: Int) -> DFAState? {
        // this method is called after each time the input position advances
        // during SLL prediction
        _sllStopIndex = _input.index()

        let existingTargetState: DFAState? = super.getExistingTargetState(previousD, t)
        if existingTargetState != nil {
            decisions[currentDecision].SLL_DFATransitions += 1 // count only if we transition over a DFA state
            if existingTargetState == ATNSimulator.ERROR {
                decisions[currentDecision].errors.append(
                ErrorInfo(currentDecision, previousD.configs, _input, _startIndex, _sllStopIndex, false)
                )
            }
        }

        currentState = existingTargetState
        return existingTargetState
    }

    override
    internal func computeTargetState(_ dfa: DFA, _ previousD: DFAState, _ t: Int) throws -> DFAState {
        let state: DFAState = try  super.computeTargetState(dfa, previousD, t)
        currentState = state
        return state
    }

    override
    internal func computeReachSet(_ closure: ATNConfigSet, _ t: Int, _ fullCtx: Bool) throws -> ATNConfigSet? {
        if fullCtx {
            // this method is called after each time the input position advances
            // during full context prediction
            _llStopIndex = _input.index()
        }

        let reachConfigs: ATNConfigSet? = try super.computeReachSet(closure, t, fullCtx)
        if fullCtx {
            decisions[currentDecision].LL_ATNTransitions += 1 // count computation even if error
            if reachConfigs != nil {
            } else {
                // no reach on current lookahead symbol. ERROR.
                // TODO: does not handle delayed errors per getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule()
                decisions[currentDecision].errors.append(
                ErrorInfo(currentDecision, closure, _input, _startIndex, _llStopIndex, true)
                )
            }
        } else {
            decisions[currentDecision].SLL_ATNTransitions += 1
            if reachConfigs != nil {
            } else {
                // no reach on current lookahead symbol. ERROR.
                decisions[currentDecision].errors.append(
                ErrorInfo(currentDecision, closure, _input, _startIndex, _sllStopIndex, false)
                )
            }
        }
        return reachConfigs
    }

    override
    internal func evalSemanticContext(_ pred: SemanticContext, _ parserCallStack: ParserRuleContext, _ alt: Int, _ fullCtx: Bool) throws -> Bool {
        let result: Bool = try super.evalSemanticContext(pred, parserCallStack, alt, fullCtx)
        if !(pred is SemanticContext.PrecedencePredicate) {
            let fullContext: Bool = _llStopIndex >= 0
            let stopIndex: Int = fullContext ? _llStopIndex : _sllStopIndex
            decisions[currentDecision].predicateEvals.append(
            PredicateEvalInfo(currentDecision, _input, _startIndex, stopIndex, pred, result, alt, fullCtx)
            )
        }

        return result
    }

    override
    internal func reportAttemptingFullContext(_ dfa: DFA, _ conflictingAlts: BitSet?, _ configs: ATNConfigSet, _ startIndex: Int, _ stopIndex: Int) throws {
        if let conflictingAlts = conflictingAlts {
            conflictingAltResolvedBySLL = try conflictingAlts.nextSetBit(0)
        } else {
            conflictingAltResolvedBySLL = try configs.getAlts().nextSetBit(0)
        }
        decisions[currentDecision].LL_Fallback += 1
        try super.reportAttemptingFullContext(dfa, conflictingAlts, configs, startIndex, stopIndex)
    }

    override
    internal func reportContextSensitivity(_ dfa: DFA, _ prediction: Int, _ configs: ATNConfigSet, _ startIndex: Int, _ stopIndex: Int) throws {
        if prediction != conflictingAltResolvedBySLL {
            decisions[currentDecision].contextSensitivities.append(
            ContextSensitivityInfo(currentDecision, configs, _input, startIndex, stopIndex)
            )
        }
        try super.reportContextSensitivity(dfa, prediction, configs, startIndex, stopIndex)
    }

    override
    internal func reportAmbiguity(_ dfa: DFA, _ D: DFAState, _ startIndex: Int, _ stopIndex: Int, _ exact: Bool,
                                  _ ambigAlts: BitSet?, _ configs: ATNConfigSet) throws {
        var prediction: Int
        if let ambigAlts = ambigAlts {
            prediction = try ambigAlts.nextSetBit(0)
        } else {
            prediction = try configs.getAlts().nextSetBit(0)
        }
        if configs.fullCtx && prediction != conflictingAltResolvedBySLL {
            // Even though this is an ambiguity we are reporting, we can
            // still detect some context sensitivities.  Both SLL and LL
            // are showing a conflict, hence an ambiguity, but if they resolve
            // to different minimum alternatives we have also identified a
            // context sensitivity.
            decisions[currentDecision].contextSensitivities.append(
            ContextSensitivityInfo(currentDecision, configs, _input, startIndex, stopIndex)
            )
        }
        decisions[currentDecision].ambiguities.append(
        AmbiguityInfo(currentDecision, configs, ambigAlts!,
                _input, startIndex, stopIndex, configs.fullCtx)
        )
        try super.reportAmbiguity(dfa, D, startIndex, stopIndex, exact, ambigAlts!, configs)
    }


    public func getDecisionInfo() -> [DecisionInfo] {
        return decisions
    }
}
