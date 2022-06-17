///
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
///

import Foundation

public class ATNDeserializer {
    public static let SERIALIZED_VERSION = 4

    private let deserializationOptions: ATNDeserializationOptions

    public init(_ deserializationOptions: ATNDeserializationOptions? = nil) {
        self.deserializationOptions = deserializationOptions ?? ATNDeserializationOptions()
    }

    public func deserialize(_ data: [Int]) throws -> ATN {
        var p = 0

        let version = data[p]
        p += 1
        if version != ATNDeserializer.SERIALIZED_VERSION {
            let reason = "Could not deserialize ATN with version \(version) (expected \(ATNDeserializer.SERIALIZED_VERSION))."
            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let grammarType = ATNType(rawValue: data[p])!
        p += 1
        let maxTokenType = data[p]
        p += 1
        let atn = ATN(grammarType, maxTokenType)

        //
        // STATES
        //
        var loopBackStateNumbers = [(LoopEndState, Int)]()
        var endStateNumbers = [(BlockStartState, Int)]()
        let nstates = data[p]
        p += 1
        for _ in 0..<nstates {
            let stype = data[p]
            p += 1
            // ignore bad type of states
            if stype == ATNState.INVALID_TYPE {
                atn.addState(nil)
                continue
            }

            let ruleIndex = data[p]
            p += 1
            let s = try stateFactory(stype, ruleIndex)!
            if stype == ATNState.LOOP_END {
                // special case
                let loopBackStateNumber = data[p]
                p += 1
                loopBackStateNumbers.append((s as! LoopEndState, loopBackStateNumber))
            } else if let s = s as? BlockStartState {
                let endStateNumber = data[p]
                p += 1
                endStateNumbers.append((s, endStateNumber))
            }
            atn.addState(s)
        }

        // delay the assignment of loop back and end states until we know all the state instances have been initialized
        for pair in loopBackStateNumbers {
            pair.0.loopBackState = atn.states[pair.1]
        }

        for pair in endStateNumbers {
            pair.0.endState = atn.states[pair.1] as? BlockEndState
        }

        let numNonGreedyStates = data[p]
        p += 1
        for _ in 0..<numNonGreedyStates {
            let stateNumber = data[p]
            p += 1
            (atn.states[stateNumber] as! DecisionState).nonGreedy = true
        }

        let numPrecedenceStates = data[p]
        p += 1
        for _ in 0..<numPrecedenceStates {
            let stateNumber = data[p]
            p += 1
            (atn.states[stateNumber] as! RuleStartState).isPrecedenceRule = true
        }

        //
        // RULES
        //
        let nrules = data[p]
        p += 1
        var ruleToTokenType = [Int]()
        var ruleToStartState = [RuleStartState]()
        for _ in 0..<nrules {
            let s = data[p]
            p += 1
            let startState = atn.states[s] as! RuleStartState
            ruleToStartState.append(startState)

            if atn.grammarType == ATNType.lexer {
                let tokenType = data[p]
                p += 1
                ruleToTokenType.append(tokenType)
            }
        }
        atn.ruleToStartState = ruleToStartState
        if atn.grammarType == ATNType.lexer {
            atn.ruleToTokenType = ruleToTokenType
        }

        fillRuleToStopState(atn)

        //
        // MODES
        //
        let nmodes = data[p]
        p += 1
        for _ in 0..<nmodes {
            let s = data[p]
            p += 1
            atn.appendModeToStartState(atn.states[s] as! TokensStartState)
        }

        //
        // SETS
        //
        var sets = [IntervalSet]()

        readSets(data, &p, &sets, readInt)

        //
        // EDGES
        //
        let nedges = data[p]
        p += 1
        for _ in 0..<nedges {
            let src = data[p]
            let trg = data[p + 1]
            let ttype = data[p + 2]
            let arg1 = data[p + 3]
            let arg2 = data[p + 4]
            let arg3 = data[p + 5]
            let trans = try edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets)

            let srcState = atn.states[src]!
            srcState.addTransition(trans)
            p += 6
        }

        deriveEdgesForRuleStopStates(atn)
        try validateStates(atn)

        //
        // DECISIONS
        //
        let ndecisions = data[p]
        p += 1
        if (ndecisions >= 1) {
            for i in 1...ndecisions {
                let s = data[p]
                p += 1
                let decState = atn.states[s] as! DecisionState
                atn.appendDecisionToState(decState)
                decState.decision = i - 1
            }
        }

        //
        // LEXER ACTIONS
        //
        if atn.grammarType == ATNType.lexer {
            let length = data[p]
            p += 1
            var lexerActions = [LexerAction]()
            for _ in 0..<length {
                let actionType = LexerActionType(rawValue: data[p])!
                p += 1
                let data1 = data[p]
                p += 1
                let data2 = data[p]
                p += 1
                let lexerAction = lexerActionFactory(actionType, data1, data2)
                lexerActions.append(lexerAction)
            }
            atn.lexerActions = lexerActions
        }

        try finalizeATN(atn)
        return atn
    }

    private func readInt(_ data: [Int], _ p: inout Int) -> Int {
        let result = data[p]
        p += 1
        return result
    }

    private func readSets(_ data: [Int], _ p: inout Int, _ sets: inout [IntervalSet], _ readUnicode: ([Int], inout Int) -> Int) {
        let nsets = data[p]
        p += 1
        for _ in 0..<nsets {
            let nintervals = data[p]
            p += 1
            let set = IntervalSet()
            sets.append(set)

            let containsEof = (data[p] != 0)
            p += 1
            if containsEof {
                try! set.add(-1)
            }

            for _ in 0..<nintervals {
                try! set.add(readUnicode(data, &p), readUnicode(data, &p))
            }
        }
    }

    private func fillRuleToStopState(_ atn: ATN) {
        let nrules = atn.ruleToStartState.count
        atn.ruleToStopState = [RuleStopState](repeating: RuleStopState(), count: nrules)

        for state in atn.states {
            if let stopState = state as? RuleStopState, let index = stopState.ruleIndex {
                atn.ruleToStopState[index] = stopState
                atn.ruleToStartState[index].stopState = stopState
            }
        }
    }

    /// edges for rule stop states can be derived, so they aren't serialized
    private func deriveEdgesForRuleStopStates(_ atn: ATN) {
        for state in atn.states {
            guard let state = state else {
                continue
            }
            let length = state.getNumberOfTransitions()
            for i in 0..<length {
                let t = state.transition(i)
                guard let ruleTransition = t as? RuleTransition else {
                    continue
                }
                var outermostPrecedenceReturn = -1
                if let targetRuleIndex = ruleTransition.target.ruleIndex {
                    if atn.ruleToStartState[targetRuleIndex].isPrecedenceRule {
                        if ruleTransition.precedence == 0 {
                            outermostPrecedenceReturn = targetRuleIndex
                        }
                    }

                    let returnTransition = EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn)
                    atn.ruleToStopState[targetRuleIndex].addTransition(returnTransition)
                }
            }
        }
    }

    private func validateStates(_ atn: ATN) throws {
        for state in atn.states {
            if let state = state as? BlockStartState {
                // we need to know the end state to set its start state
                if let stateEndState = state.endState {
                    // block end states can only be associated to a single block start state
                    if stateEndState.startState != nil {
                        throw ANTLRError.illegalState(msg: "state.endState.startState != nil")
                    }
                    stateEndState.startState = state
                }
                else {
                    throw ANTLRError.illegalState(msg: "state.endState == nil")
                }
            }
            else if let loopbackState = state as? PlusLoopbackState {
                let length = loopbackState.getNumberOfTransitions()
                for i in 0..<length {
                    let target = loopbackState.transition(i).target
                    if let startState = target as? PlusBlockStartState {
                        startState.loopBackState = loopbackState
                    }
                }
            }
            else if let loopbackState = state as? StarLoopbackState {
                let length = loopbackState.getNumberOfTransitions()
                for i in 0..<length {
                    let target = loopbackState.transition(i).target
                    if let entryState = target as? StarLoopEntryState {
                        entryState.loopBackState = loopbackState
                    }
                }
            }
        }
    }


    private func finalizeATN(_ atn: ATN) throws {
        markPrecedenceDecisions(atn)
        if deserializationOptions.verifyATN {
            try verifyATN(atn)
        }
        if deserializationOptions.generateRuleBypassTransitions && atn.grammarType == ATNType.parser {
            try generateRuleBypassTransitions(atn)

            if deserializationOptions.verifyATN {
                // reverify after modification
                try verifyATN(atn)
            }
        }
    }


    ///
    /// Analyze the _org.antlr.v4.runtime.atn.StarLoopEntryState_ states in the specified ATN to set
    /// the _org.antlr.v4.runtime.atn.StarLoopEntryState#precedenceRuleDecision_ field to the
    /// correct value.
    ///
    /// - parameter atn: The ATN.
    ///
    internal func markPrecedenceDecisions(_ atn: ATN) {
        for state in atn.states {
            ///
            /// We analyze the ATN to determine if this ATN decision state is the
            /// decision for the closure block that determines whether a
            /// precedence rule should continue or complete.
            ///
            guard let state = state as? StarLoopEntryState, let stateRuleIndex = state.ruleIndex, atn.ruleToStartState[stateRuleIndex].isPrecedenceRule else {
                continue
            }
            let maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target
            if maybeLoopEndState is LoopEndState && maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target is RuleStopState {
                state.precedenceRuleDecision = true
            }
        }
    }


    private func generateRuleBypassTransitions(_ atn: ATN) throws {
        let length = atn.ruleToStartState.count
        atn.ruleToTokenType = (0..<length).map { atn.maxTokenType + $0 + 1 }

        for i in 0..<length {
            let bypassStart = BasicBlockStartState()
            bypassStart.ruleIndex = i
            atn.addState(bypassStart)

            let bypassStop = BlockEndState()
            bypassStop.ruleIndex = i
            atn.addState(bypassStop)

            bypassStart.endState = bypassStop
            atn.defineDecisionState(bypassStart)

            bypassStop.startState = bypassStart

            var endState: ATNState?
            var excludeTransition: Transition? = nil
            if atn.ruleToStartState[i].isPrecedenceRule {
                // wrap from the beginning of the rule to the StarLoopEntryState
                endState = nil
                for state in atn.states {
                    guard let state = state, state.ruleIndex == i, state is StarLoopEntryState else {
                        continue
                    }

                    let maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target
                    if !(maybeLoopEndState is LoopEndState) {
                        continue
                    }

                    if maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target is RuleStopState {
                        endState = state
                        break
                    }
                }

                if endState == nil {
                    throw ANTLRError.unsupportedOperation(msg: "Couldn't identify final state of the precedence rule prefix section.")
                }

                excludeTransition = (endState as? StarLoopEntryState)?.loopBackState?.transition(0)
            }
            else {
                endState = atn.ruleToStopState[i]
            }

            // all non-excluded transitions that currently target end state need to target blockEnd instead
            for state in atn.states {
                guard let state = state else {
                    continue
                }
                for transition in state.transitions {
                    if transition === excludeTransition! {
                        continue
                    }

                    if transition.target == endState {
                        transition.target = bypassStop
                    }
                }
            }

            // all transitions leaving the rule start state need to leave blockStart instead
            while atn.ruleToStartState[i].getNumberOfTransitions() > 0 {
                let transition = atn.ruleToStartState[i].removeTransition(atn.ruleToStartState[i].getNumberOfTransitions() - 1)
                bypassStart.addTransition(transition)
            }

            // link the new states
            atn.ruleToStartState[i].addTransition(EpsilonTransition(bypassStart))
            bypassStop.addTransition(EpsilonTransition(endState!))

            let matchState = BasicState()
            atn.addState(matchState)
            matchState.addTransition(AtomTransition(bypassStop, atn.ruleToTokenType[i]))
            bypassStart.addTransition(EpsilonTransition(matchState))
        }
    }


    internal func verifyATN(_ atn: ATN) throws {
        // verify assumptions
        for state in atn.states {
            guard let state = state else {
                continue
            }

            try checkCondition(state.onlyHasEpsilonTransitions() || state.getNumberOfTransitions() <= 1)

            if let state = state as? PlusBlockStartState {
                try checkCondition(state.loopBackState != nil)
            }

            if let starLoopEntryState = state as? StarLoopEntryState {
                try checkCondition(starLoopEntryState.loopBackState != nil)
                try checkCondition(starLoopEntryState.getNumberOfTransitions() == 2)

                if starLoopEntryState.transition(0).target is StarBlockStartState {
                    try checkCondition(starLoopEntryState.transition(1).target is LoopEndState)
                    try checkCondition(!starLoopEntryState.nonGreedy)
                } else {
                    if starLoopEntryState.transition(0).target is LoopEndState {
                        try checkCondition(starLoopEntryState.transition(1).target is StarBlockStartState)
                        try checkCondition(starLoopEntryState.nonGreedy)
                    } else {
                        throw ANTLRError.illegalState(msg: "IllegalStateException")
                    }
                }
            }

            if let state = state as? StarLoopbackState {
                try checkCondition(state.getNumberOfTransitions() == 1)
                try checkCondition(state.transition(0).target is StarLoopEntryState)
            }

            if state is LoopEndState {
                try checkCondition((state as! LoopEndState).loopBackState != nil)
            }

            if state is RuleStartState {
                try checkCondition((state as! RuleStartState).stopState != nil)
            }

            if state is BlockStartState {
                try checkCondition((state as! BlockStartState).endState != nil)
            }

            if state is BlockEndState {
                try checkCondition((state as! BlockEndState).startState != nil)
            }

            if let decisionState = state as? DecisionState {
                try checkCondition(decisionState.getNumberOfTransitions() <= 1 || decisionState.decision >= 0)
            } else {
                try checkCondition(state.getNumberOfTransitions() <= 1 || state is RuleStopState)
            }
        }
    }

    internal func checkCondition(_ condition: Bool) throws {
        try checkCondition(condition, nil)
    }

    internal func checkCondition(_ condition: Bool, _ message: String?) throws {
        if !condition {
            throw ANTLRError.illegalState(msg: message ?? "")

        }
    }


    internal func edgeFactory(_ atn: ATN,
                              _ type: Int, _ src: Int, _ trg: Int,
                              _ arg1: Int, _ arg2: Int, _ arg3: Int,
                              _ sets: [IntervalSet]) throws -> Transition {
        let target = atn.states[trg]!
        switch type {
        case Transition.EPSILON: return EpsilonTransition(target)
        case Transition.RANGE:
            if arg3 != 0 {
                return RangeTransition(target, CommonToken.EOF, arg2)
            } else {
                return RangeTransition(target, arg1, arg2)
            }
        case Transition.RULE:
            let rt = RuleTransition(atn.states[arg1] as! RuleStartState, arg2, arg3, target)
            return rt
        case Transition.PREDICATE:
            let pt = PredicateTransition(target, arg1, arg2, arg3 != 0)
            return pt
        case Transition.PRECEDENCE:
            return PrecedencePredicateTransition(target, arg1)
        case Transition.ATOM:
            if arg3 != 0 {
                return AtomTransition(target, CommonToken.EOF)
            } else {
                return AtomTransition(target, arg1)
            }
        case Transition.ACTION:
            return ActionTransition(target, arg1, arg2, arg3 != 0)

        case Transition.SET: return SetTransition(target, sets[arg1])
        case Transition.NOT_SET: return NotSetTransition(target, sets[arg1])
        case Transition.WILDCARD: return WildcardTransition(target)
        default:
            throw ANTLRError.illegalState(msg: "The specified transition type is not valid.")
        }
    }

    internal func stateFactory(_ type: Int, _ ruleIndex: Int) throws -> ATNState? {
        var s: ATNState
        switch type {
        case ATNState.INVALID_TYPE: return nil
        case ATNState.BASIC: s = BasicState()
        case ATNState.RULE_START: s = RuleStartState()
        case ATNState.BLOCK_START: s = BasicBlockStartState()
        case ATNState.PLUS_BLOCK_START: s = PlusBlockStartState()
        case ATNState.STAR_BLOCK_START: s = StarBlockStartState()
        case ATNState.TOKEN_START: s = TokensStartState()
        case ATNState.RULE_STOP: s = RuleStopState()
        case ATNState.BLOCK_END: s = BlockEndState()
        case ATNState.STAR_LOOP_BACK: s = StarLoopbackState()
        case ATNState.STAR_LOOP_ENTRY: s = StarLoopEntryState()
        case ATNState.PLUS_LOOP_BACK: s = PlusLoopbackState()
        case ATNState.LOOP_END: s = LoopEndState()
        default:
            let message: String = "The specified state type \(type) is not valid."

            throw ANTLRError.illegalArgument(msg: message)
        }

        s.ruleIndex = ruleIndex
        return s
    }

    internal func lexerActionFactory(_ type: LexerActionType, _ data1: Int, _ data2: Int) -> LexerAction {
        switch type {
        case .channel:
            return LexerChannelAction(data1)

        case .custom:
            return LexerCustomAction(data1, data2)

        case .mode:
            return LexerModeAction(data1)

        case .more:
            return LexerMoreAction.INSTANCE

        case .popMode:
            return LexerPopModeAction.INSTANCE

        case .pushMode:
            return LexerPushModeAction(data1)

        case .skip:
            return LexerSkipAction.INSTANCE

        case .type:
            return LexerTypeAction(data1)
        }
    }
}
