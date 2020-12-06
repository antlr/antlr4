/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// 
/// -  Sam Harwell
/// 

import Foundation

public class ATNDeserializer {
    public static let SERIALIZED_VERSION = 3

    ///
    /// This is the earliest supported serialized UUID.
    /// 
    private static let BASE_SERIALIZED_UUID = UUID(uuidString: "33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3")!

    /// 
    /// This UUID indicates an extension of _BASE_SERIALIZED_UUID_ for the
    /// addition of precedence predicates.
    /// 
    private static let ADDED_PRECEDENCE_TRANSITIONS = UUID(uuidString: "1DA0C57D-6C06-438A-9B27-10BCB3CE0F61")!
    /// 
    /// This UUID indicates an extension of _#ADDED_PRECEDENCE_TRANSITIONS_
    /// for the addition of lexer actions encoded as a sequence of
    /// _org.antlr.v4.runtime.atn.LexerAction_ instances.
    /// 
    private static let ADDED_LEXER_ACTIONS = UUID(uuidString: "AADB8D7E-AEEF-4415-AD2B-8204D6CF042E")!

    /// 
    /// This UUID indicates the serialized ATN contains two sets of
    /// IntervalSets, where the second set's values are encoded as
    /// 32-bit integers to support the full Unicode SMP range up to U+10FFFF.
    /// 
    private static let ADDED_UNICODE_SMP = UUID(uuidString: "59627784-3BE5-417A-B9EB-8131A7286089")!

    /// 
    /// This list contains all of the currently supported UUIDs, ordered by when
    /// the feature first appeared in this branch.
    /// 
    private static let SUPPORTED_UUIDS = [
        ATNDeserializer.BASE_SERIALIZED_UUID,
        ATNDeserializer.ADDED_PRECEDENCE_TRANSITIONS,
        ATNDeserializer.ADDED_LEXER_ACTIONS,
        ATNDeserializer.ADDED_UNICODE_SMP,
        ]

    /// 
    /// This is the current serialized UUID.
    /// 
    public static let SERIALIZED_UUID = ADDED_UNICODE_SMP


    private let deserializationOptions: ATNDeserializationOptions

    public init(_ deserializationOptions: ATNDeserializationOptions? = nil) {
        self.deserializationOptions = deserializationOptions ?? ATNDeserializationOptions()
    }

    /// 
    /// Determines if a particular serialized representation of an ATN supports
    /// a particular feature, identified by the _java.util.UUID_ used for serializing
    /// the ATN at the time the feature was first introduced.
    /// 
    /// - parameter feature: The _java.util.UUID_ marking the first time the feature was
    /// supported in the serialized ATN.
    /// - parameter actualUuid: The _java.util.UUID_ of the actual serialized ATN which is
    /// currently being deserialized.
    /// - returns: `true` if the `actualUuid` value represents a
    /// serialized ATN at or after the feature identified by `feature` was
    /// introduced; otherwise, `false`.
    /// 
    internal func isFeatureSupported(_ feature: UUID, _ actualUuid: UUID) -> Bool {
        let supported = ATNDeserializer.SUPPORTED_UUIDS
        guard let featureIndex = supported.firstIndex(of: feature),
            let actualIndex = supported.firstIndex(of: actualUuid) else {
                return false
        }
        return actualIndex >= featureIndex
    }


    public func deserialize(_ inData: [Character]) throws -> ATN {
        // don't adjust the first value since that's the version number
        let data = [inData[0]] + inData[1...].map { Character(integerLiteral: $0.unicodeValue - 2) }

        var p = 0
        let version = data[p].unicodeValue
        p += 1
        if version != ATNDeserializer.SERIALIZED_VERSION {
            let reason = "Could not deserialize ATN with version \(version) (expected \(ATNDeserializer.SERIALIZED_VERSION))."
            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let uuid = toUUID(data, p)
        p += 8
        if !ATNDeserializer.SUPPORTED_UUIDS.contains(uuid) {
            let reason = "Could not deserialize ATN with UUID \(uuid) (expected \(ATNDeserializer.SERIALIZED_UUID) or a legacy UUID)."
            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let supportsPrecedencePredicates = isFeatureSupported(ATNDeserializer.ADDED_PRECEDENCE_TRANSITIONS, uuid)
        let supportsLexerActions = isFeatureSupported(ATNDeserializer.ADDED_LEXER_ACTIONS, uuid)

        let grammarType = ATNType(rawValue: toInt(data[p]))!
        p += 1
        let maxTokenType = toInt(data[p])
        p += 1
        let atn = ATN(grammarType, maxTokenType)

        //
        // STATES
        //
        var loopBackStateNumbers = [(LoopEndState, Int)]()
        var endStateNumbers = [(BlockStartState, Int)]()
        let nstates = toInt(data[p])
        p += 1
        for _ in 0..<nstates {
            let stype = toInt(data[p])
            p += 1
            // ignore bad type of states
            if stype == ATNState.INVALID_TYPE {
                atn.addState(nil)
                continue
            }

            var ruleIndex = toInt(data[p])
            p += 1
            if ruleIndex == Int.max {
                // Character.MAX_VALUE
                ruleIndex = -1
            }

            let s = try stateFactory(stype, ruleIndex)!
            if stype == ATNState.LOOP_END {
                // special case
                let loopBackStateNumber = toInt(data[p])
                p += 1
                loopBackStateNumbers.append((s as! LoopEndState, loopBackStateNumber))
            } else if let s = s as? BlockStartState {
                let endStateNumber = toInt(data[p])
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

        let numNonGreedyStates = toInt(data[p])
        p += 1
        for _ in 0..<numNonGreedyStates {
            let stateNumber = toInt(data[p])
            p += 1
            (atn.states[stateNumber] as! DecisionState).nonGreedy = true
        }

        if supportsPrecedencePredicates {
            let numPrecedenceStates = toInt(data[p])
            p += 1
            for _ in 0..<numPrecedenceStates {
                let stateNumber = toInt(data[p])
                p += 1
                (atn.states[stateNumber] as! RuleStartState).isPrecedenceRule = true
            }
        }

        //
        // RULES
        //
        let nrules = toInt(data[p])
        p += 1
        var ruleToTokenType = [Int]()
        var ruleToStartState = [RuleStartState]()
        for _ in 0..<nrules {
            let s = toInt(data[p])
            p += 1
            let startState = atn.states[s] as! RuleStartState
            ruleToStartState.append(startState)

            if atn.grammarType == ATNType.lexer {
                var tokenType = toInt(data[p])
                p += 1
                if tokenType == 0xFFFF {
                    tokenType = CommonToken.EOF
                }

                ruleToTokenType.append(tokenType)

                if !isFeatureSupported(ATNDeserializer.ADDED_LEXER_ACTIONS, uuid) {
                    // this piece of unused metadata was serialized prior to the
                    // addition of LexerAction
                    var actionIndexIgnored = toInt(data[p])
                    p += 1
                    if actionIndexIgnored == 0xFFFF {
                        actionIndexIgnored = -1
                    }
                }
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
        let nmodes = toInt(data[p])
        p += 1
        for _ in 0..<nmodes {
            let s = toInt(data[p])
            p += 1
            atn.appendModeToStartState(atn.states[s] as! TokensStartState)
        }

        //
        // SETS
        //
        var sets = [IntervalSet]()

        // First, deserialize sets with 16-bit arguments <= U+FFFF.
        readSets(data, &p, &sets, readUnicodeInt)

        // Next, if the ATN was serialized with the Unicode SMP feature,
        // deserialize sets with 32-bit arguments <= U+10FFFF.
        if isFeatureSupported(ATNDeserializer.ADDED_UNICODE_SMP, uuid) {
            readSets(data, &p, &sets, readUnicodeInt32)
        }

        //
        // EDGES
        //
        let nedges = toInt(data[p])
        p += 1
        for _ in 0..<nedges {
            let src = toInt(data[p])
            let trg = toInt(data[p + 1])
            let ttype = toInt(data[p + 2])
            let arg1 = toInt(data[p + 3])
            let arg2 = toInt(data[p + 4])
            let arg3 = toInt(data[p + 5])
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
        let ndecisions = toInt(data[p])
        p += 1
        for i in 1...ndecisions {
            let s = toInt(data[p])
            p += 1
            let decState = atn.states[s] as! DecisionState
            atn.appendDecisionToState(decState)
            decState.decision = i - 1
        }

        //
        // LEXER ACTIONS
        //
        if atn.grammarType == ATNType.lexer {
            if supportsLexerActions {
                let length = toInt(data[p])
                p += 1
                var lexerActions = [LexerAction]()
                for _ in 0..<length {
                    let actionType = LexerActionType(rawValue: toInt(data[p]))!
                    p += 1
                    var data1 = toInt(data[p])
                    p += 1
                    if data1 == 0xFFFF {
                        data1 = -1
                    }

                    var data2 = toInt(data[p])
                    p += 1
                    if data2 == 0xFFFF {
                        data2 = -1
                    }

                    let lexerAction = lexerActionFactory(actionType, data1, data2)
                    lexerActions.append(lexerAction)
                }
                atn.lexerActions = lexerActions

            }
            else {
                convertOldActionTransitions(atn)
            }
        }

        try finalizeATN(atn)
        return atn
    }


    private func readUnicodeInt(_ data: [Character], _ p: inout Int) -> Int {
        let result = toInt(data[p])
        p += 1
        return result
    }

    private func readUnicodeInt32(_ data: [Character], _ p: inout Int) -> Int {
        let result = toInt32(data, p)
        p += 2
        return result
    }

    private func readSets(_ data: [Character], _ p: inout Int, _ sets: inout [IntervalSet], _ readUnicode: ([Character], inout Int) -> Int) {
        let nsets = toInt(data[p])
        p += 1
        for _ in 0..<nsets {
            let nintervals = toInt(data[p])
            p += 1
            let set = IntervalSet()
            sets.append(set)

            let containsEof = (toInt(data[p]) != 0)
            p += 1
            if containsEof {
                try! set.add(-1)
            }

            for _ in 0..<nintervals {
                try! set.add(readUnicode(data, &p), readUnicode(data, &p))
            }
        }
    }

    public func deserializeFromJson(_ jsonStr: String) -> ATN {
        guard !jsonStr.isEmpty else {
            fatalError("ATN Serialization is empty,Please include *LexerATN.json and  *ParserATN.json in TARGETS-Build Phases-Copy Bundle Resources")
        }
        if let JSONData = jsonStr.data(using: .utf8) {
            do {
                let JSON = try JSONSerialization.jsonObject(with: JSONData, options: JSONSerialization.ReadingOptions(rawValue: 0))
                guard let JSONDictionary = JSON as? [String: Any] else {
                    fatalError("deserializeFromJson Not a Dictionary")
                }

                return try dictToJson(JSONDictionary)

            } catch let JSONError {
                print("\(JSONError)")
            }
        }

        fatalError("Could not deserialize ATN ")
    }

    public func dictToJson(_ dict: [String: Any]) throws -> ATN {
        let version = dict["version"] as! Int
        if version != ATNDeserializer.SERIALIZED_VERSION {
            let reason = "Could not deserialize ATN with version \(version) (expected \(ATNDeserializer.SERIALIZED_VERSION))."
            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let uuid = UUID(uuidString: dict["uuid"] as! String)!

        if !ATNDeserializer.SUPPORTED_UUIDS.contains(uuid) {
            let reason = "Could not deserialize ATN with UUID \(uuid) (expected \(ATNDeserializer.SERIALIZED_UUID) or a legacy UUID)."

            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let supportsPrecedencePredicates = isFeatureSupported(ATNDeserializer.ADDED_PRECEDENCE_TRANSITIONS, uuid)
        let supportsLexerActions = isFeatureSupported(ATNDeserializer.ADDED_LEXER_ACTIONS, uuid)

        let grammarType = ATNType(rawValue: dict["grammarType"] as! Int)!
        let maxTokenType = dict["maxTokenType"] as! Int
        let atn = ATN(grammarType, maxTokenType)

        //
        // STATES
        //
        var loopBackStateNumbers = [(LoopEndState, Int)]()
        var endStateNumbers = [(BlockStartState, Int)]()

        let states = dict["states"] as! [[String: Any]]
        
        for state in states {
            let ruleIndex = state["ruleIndex"] as! Int
            let stype = state["stateType"] as! Int
            let s = try stateFactory(stype, ruleIndex)!
            if stype == ATNState.LOOP_END {
                // special case
                let loopBackStateNumber = state["detailStateNumber"] as! Int
                loopBackStateNumbers.append((s as! LoopEndState, loopBackStateNumber))
            }
            else if let bsState = s as? BlockStartState {
                let endStateNumber = state["detailStateNumber"] as! Int
                endStateNumbers.append((bsState, endStateNumber))
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

        let numNonGreedyStates = dict["nonGreedyStates"] as! [Int]
        for numNonGreedyState in numNonGreedyStates {
            (atn.states[numNonGreedyState] as! DecisionState).nonGreedy = true
        }

        if supportsPrecedencePredicates {
            let numPrecedenceStates = dict["precedenceStates"] as! [Int]
            for numPrecedenceState in numPrecedenceStates {
                (atn.states[numPrecedenceState] as! RuleStartState).isPrecedenceRule = true
            }
        }

        //
        // RULES
        //
        let ruleToStartState = dict["ruleToStartState"] as! [[String: Any]]
        let nrules = ruleToStartState.count
        var ruleToTokenType = [Int]()
        var ruleToStartStateParsed = [RuleStartState]()
        for i in 0..<nrules {
            let currentRuleToStartState = ruleToStartState[i]
            let s = currentRuleToStartState["stateNumber"] as! Int
            let startState = atn.states[s] as! RuleStartState
            ruleToStartStateParsed.append(startState)

            if atn.grammarType == ATNType.lexer {
                var tokenType = currentRuleToStartState["ruleToTokenType"] as! Int
                if tokenType == -1 {
                    tokenType = CommonToken.EOF
                }
                ruleToTokenType.append(tokenType)
            }
        }
        atn.ruleToStartState = ruleToStartStateParsed
        if atn.grammarType == ATNType.lexer {
            atn.ruleToTokenType = ruleToTokenType
        }

        fillRuleToStopState(atn)

        //
        // MODES
        //
        let modeToStartState = dict["modeToStartState"] as! [Int]
        for stateNumber in modeToStartState {
            atn.appendModeToStartState(atn.states[stateNumber] as! TokensStartState)
        }

        //
        // SETS
        //
        var sets = [IntervalSet]()
        let nsets = dict["nsets"] as! Int
        let intervalSet = dict["IntervalSet"] as! [[String: Any]]

        for i in 0..<nsets {
            let setBuilder = intervalSet[i]
            let nintervals = setBuilder["size"] as! Int

            let set = IntervalSet()
            sets.append(set)

            let containsEof = (setBuilder["containsEof"] as! Int) != 0
            if containsEof {
                try! set.add(-1)
            }
            let intervalsBuilder = setBuilder["Intervals"] as! [[String: Any]]

            for j in 0..<nintervals {
                let vals = intervalsBuilder[j]
                try! set.add((vals["a"] as! Int), (vals["b"] as! Int))
            }
        }


        //
        // EDGES
        //
        let allTransitions = dict["allTransitionsBuilder"] as! [[[String: Any]]]

        for transitionsBuilder in allTransitions {
            for transition in transitionsBuilder {
                let src = transition["src"] as! Int
                let trg = transition["trg"] as! Int
                let ttype = transition["edgeType"] as! Int
                let arg1 = transition["arg1"] as! Int
                let arg2 = transition["arg2"] as! Int
                let arg3 = transition["arg3"] as! Int
                let trans = try edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets)

                let srcState = atn.states[src]!
                srcState.addTransition(trans)
            }
        }

        deriveEdgesForRuleStopStates(atn)
        try validateStates(atn)

        //
        // DECISIONS
        //
        let ndecisions = dict["decisionToState"] as! [Int]
        let length = ndecisions.count
        for i in 0..<length {
            let s = ndecisions[i]
            let decState = atn.states[s] as! DecisionState
            atn.appendDecisionToState(decState)
            decState.decision = i
        }

        //
        // LEXER ACTIONS
        //
        if atn.grammarType == ATNType.lexer {
            let lexerActionsBuilder = dict["lexerActions"] as! [[String: Any]]
            if supportsLexerActions {
                var lexerActions = [LexerAction]()
                for lexerActionDict in lexerActionsBuilder {
                    let actionTypeValue = lexerActionDict["actionType"] as! Int
                    let actionType = LexerActionType(rawValue: actionTypeValue)!
                    let data1 = lexerActionDict["a"] as! Int
                    let data2 = lexerActionDict["b"] as! Int
                    let lexerAction = lexerActionFactory(actionType, data1, data2)
                    lexerActions.append(lexerAction)
                }
                atn.lexerActions = lexerActions
            }
            else {
                convertOldActionTransitions(atn)
            }
        }

        try finalizeATN(atn)
        return atn
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


    /// for compatibility with older serialized ATNs, convert the old
    /// serialized action index for action transitions to the new
    /// form, which is the index of a LexerCustomAction
    private func convertOldActionTransitions(_ atn: ATN) {
        var legacyLexerActions = [LexerAction]()
        for state in atn.states {
            guard let state = state else {
                continue
            }
            let length = state.getNumberOfTransitions()
            for i in 0..<length {
                guard let transition = state.transition(i) as? ActionTransition else {
                    continue
                }

                let ruleIndex = transition.ruleIndex
                let actionIndex = transition.actionIndex
                let lexerAction = LexerCustomAction(ruleIndex, actionIndex)
                state.setTransition(i, ActionTransition(transition.target, ruleIndex, legacyLexerActions.count, false))
                legacyLexerActions.append(lexerAction)
            }
        }
        atn.lexerActions = legacyLexerActions
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
