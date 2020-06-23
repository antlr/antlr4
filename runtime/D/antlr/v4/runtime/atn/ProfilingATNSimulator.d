/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.ProfilingATNSimulator;

import std.conv;
import std.datetime;
import std.algorithm;
import antlr.v4.runtime.atn.ATNConfigSet;
import antlr.v4.runtime.atn.ParserATNSimulator;
import antlr.v4.runtime.atn.DecisionInfo;
import antlr.v4.runtime.atn.ErrorInfo;
import antlr.v4.runtime.atn.PredicateEvalInfo;
import antlr.v4.runtime.atn.LookaheadEventInfo;
import antlr.v4.runtime.dfa.DFAState;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.Parser;
import antlr.v4.runtime.atn.SemanticContext;
import antlr.v4.runtime.TokenStream;
import antlr.v4.runtime.ParserRuleContext;

/**
 * TODO add class description
 */
class ProfilingATNSimulator : ParserATNSimulator
{

    protected DecisionInfo[] decisions;

    protected int currentDecision;

    protected int numDecisions;

    protected size_t _sllStopIndex;

    protected size_t _llStopIndex;

    protected DFAState currentState;

    /**
     *  we can determine whether or not a decision / input pair is context-sensitive.
     *  If LL gives a different result than SLL's predicted alternative, we have a
     *  context sensitivity for sure. The converse is not necessarily true, however.
     *  It's possible that after conflict resolution chooses minimum alternatives,
     *  SLL could get the same answer as LL. Regardless of whether or not the result indicates
     *  an ambiguity, it is not treated as a context sensitivity because LL prediction
     *  was not required in order to produce a correct prediction for this decision and input sequence.
     *  It may in fact still be a context sensitivity but we don't know by looking at the
     *  minimum alternatives for the current input.
     */
    public int conflictingAltResolvedBySLL;

    public this(Parser parser)
    {
    super(parser,
              parser.getInterpreter().atn,
              parser.getInterpreter().decisionToDFA,
              parser.getInterpreter().sharedContextCache);
        numDecisions = to!int(atn.decisionToState.length);
        decisions = new DecisionInfo[numDecisions];
        for (int i=0; i<numDecisions; i++) {
            decisions[i] = new DecisionInfo(i);
        }
    }

    /**
     * @uml
     * @override
     */
    public override int adaptivePredict(TokenStream input, int decision, ParserRuleContext outerContext)
    {
    try {
            this._sllStopIndex = -1;
            this._llStopIndex = -1;
            this.currentDecision = decision;
            auto start = MonoTime.currTime; // expensive but useful info
            int alt = super.adaptivePredict(input, decision, outerContext);
            auto stop = MonoTime.currTime;
            decisions[decision].timeInPrediction += ticksToNSecs(stop.ticks - start.ticks);
            decisions[decision].invocations++;

            auto SLL_k = _sllStopIndex - _startIndex + 1;
            decisions[decision].SLL_TotalLook += SLL_k;
            decisions[decision].SLL_MinLook = decisions[decision].SLL_MinLook==0 ? SLL_k : min(decisions[decision].SLL_MinLook, SLL_k);
            if ( SLL_k > decisions[decision].SLL_MaxLook ) {
                decisions[decision].SLL_MaxLook = SLL_k;
                decisions[decision].SLL_MaxLookEvent =
                    new LookaheadEventInfo(decision, null, alt, input, _startIndex, _sllStopIndex, false);
            }

            if (_llStopIndex >= 0) {
                int LL_k = to!int(_llStopIndex - _startIndex) + 1;
                decisions[decision].LL_TotalLook += LL_k;
                decisions[decision].LL_MinLook = decisions[decision].LL_MinLook==0 ? LL_k : min(decisions[decision].LL_MinLook, LL_k);
                if ( LL_k > decisions[decision].LL_MaxLook ) {
                    decisions[decision].LL_MaxLook = LL_k;
                    decisions[decision].LL_MaxLookEvent =
                        new LookaheadEventInfo(decision, null, alt, input, _startIndex, _llStopIndex, true);
                }
            }

            return alt;
        }
        finally {
            this.currentDecision = -1;
        }

    }

    /**
     * @uml
     * @override
     */
    public override DFAState getExistingTargetState(DFAState previousD, int t)
    {
    // this method is called after each time the input position advances
        // during SLL prediction
        _sllStopIndex = _input.index;

        DFAState existingTargetState = super.getExistingTargetState(previousD, t);
        if (existingTargetState !is null) {
            decisions[currentDecision].SLL_DFATransitions++; // count only if we transition over a DFA state
            if ( existingTargetState==ERROR ) {
                decisions[currentDecision].errors
                    ~= new ErrorInfo(currentDecision, previousD.configs, _input, _startIndex, _sllStopIndex, false);
            }
        }

        currentState = existingTargetState;
        return existingTargetState;

    }

    /**
     * @uml
     * @override
     */
    protected override DFAState computeTargetState(DFA dfa, DFAState previousD, int t)
    {
    DFAState state = super.computeTargetState(dfa, previousD, t);
        currentState = state;
        return state;
    }

    /**
     * @uml
     * @override
     */
    protected override ATNConfigSet computeReachSet(ATNConfigSet closure, int t, bool fullCtx)
    {
    if (fullCtx) {
            // this method is called after each time the input position advances
            // during full context prediction
            _llStopIndex = _input.index();
        }

        ATNConfigSet reachConfigs = super.computeReachSet(closure, t, fullCtx);
        if (fullCtx) {
            decisions[currentDecision].LL_ATNTransitions++; // count computation even if error
            if (reachConfigs !is null) {
            }
            else { // no reach on current lookahead symbol. ERROR.
                // TODO: does not handle delayed errors per getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule()
                decisions[currentDecision].errors
                    ~= new ErrorInfo(currentDecision, closure, _input, _startIndex, _llStopIndex, true);
            }
        }
        else {
            decisions[currentDecision].SLL_ATNTransitions++;
            if (reachConfigs !is null) {
            }
            else { // no reach on current lookahead symbol. ERROR.
                decisions[currentDecision].errors
                    ~= new ErrorInfo(currentDecision, closure, _input, _startIndex, _sllStopIndex, false);
            }
        }
        return reachConfigs;

    }

    /**
     * @uml
     * @override
     */
    protected override bool evalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack,
        int alt, bool fullCtx)
    {
    bool result = super.evalSemanticContext(pred, parserCallStack, alt, fullCtx);
        if (pred.classinfo != SemanticContext.PrecedencePredicate.classinfo) {
            bool fullContext = _llStopIndex >= 0;
            auto stopIndex = fullContext ? _llStopIndex : _sllStopIndex;
            decisions[currentDecision].predicateEvals
                ~= new PredicateEvalInfo(currentDecision, _input, _startIndex, stopIndex, pred, result, alt, fullCtx);
        }
        return result;
    }

    public DecisionInfo[] getDecisionInfo()
    {
        return decisions;
    }

}
