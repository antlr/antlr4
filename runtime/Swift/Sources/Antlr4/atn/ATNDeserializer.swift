/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// 
/// -  Sam Harwell

import Foundation

public class ATNDeserializer {
    public static let SERIALIZED_VERSION: Int = {
        //SERIALIZED_VERSION = 3;
        return 3
    }()


    /// This is the earliest supported serialized UUID.
    private static let BASE_SERIALIZED_UUID: UUID = UUID(uuidString: "33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3")!

    /// This UUID indicates an extension of {@link BASE_SERIALIZED_UUID} for the
    /// addition of precedence predicates.
    private static let ADDED_PRECEDENCE_TRANSITIONS: UUID = UUID(uuidString: "1DA0C57D-6C06-438A-9B27-10BCB3CE0F61")!
    /// This UUID indicates an extension of {@link #ADDED_PRECEDENCE_TRANSITIONS}
    /// for the addition of lexer actions encoded as a sequence of
    /// {@link org.antlr.v4.runtime.atn.LexerAction} instances.
    private static let ADDED_LEXER_ACTIONS: UUID = UUID(uuidString: "AADB8D7E-AEEF-4415-AD2B-8204D6CF042E")!
    /// This list contains all of the currently supported UUIDs, ordered by when
    /// the feature first appeared in this branch.
    private static let SUPPORTED_UUIDS: Array<UUID> = {
        var suuid = Array<UUID>()
        suuid.append(ATNDeserializer.BASE_SERIALIZED_UUID)
        suuid.append(ATNDeserializer.ADDED_PRECEDENCE_TRANSITIONS)
        suuid.append(ATNDeserializer.ADDED_LEXER_ACTIONS)
        return suuid

    }()

    /// This is the current serialized UUID.
    public static let SERIALIZED_UUID: UUID = {
        // SERIALIZED_UUID = ADDED_LEXER_ACTIONS;
        return UUID(uuidString: "AADB8D7E-AEEF-4415-AD2B-8204D6CF042E")!
    }()


    private let deserializationOptions: ATNDeserializationOptions

    public convenience init() {
        self.init(ATNDeserializationOptions.getDefaultOptions())
    }
    // private var once = dispatch_once_t()
    public init(_ deserializationOptions: ATNDeserializationOptions?) {
        var deserializationOptions = deserializationOptions
        if deserializationOptions == nil {
            deserializationOptions = ATNDeserializationOptions.getDefaultOptions()
        }

        self.deserializationOptions = deserializationOptions!


    }

    /// Determines if a particular serialized representation of an ATN supports
    /// a particular feature, identified by the {@link java.util.UUID} used for serializing
    /// the ATN at the time the feature was first introduced.
    /// 
    /// - parameter feature: The {@link java.util.UUID} marking the first time the feature was
    /// supported in the serialized ATN.
    /// - parameter actualUuid: The {@link java.util.UUID} of the actual serialized ATN which is
    /// currently being deserialized.
    /// - returns: {@code true} if the {@code actualUuid} value represents a
    /// serialized ATN at or after the feature identified by {@code feature} was
    /// introduced; otherwise, {@code false}.
    internal func isFeatureSupported(_ feature: UUID, _ actualUuid: UUID) -> Bool {
        let featureIndex: Int = ATNDeserializer.SUPPORTED_UUIDS.index(of: feature)!
        if featureIndex < 0 {
            return false
        }

        return ATNDeserializer.SUPPORTED_UUIDS.index(of: actualUuid)! >= featureIndex
    }


    public func deserialize(_ inData: [Character]) throws -> ATN {
        //TODO:data = data.clone();
        var data = inData
        // don't adjust the first value since that's the version number
        let length = data.count
        for i in 1..<length {
            data[i] = Character(integerLiteral: data[i].unicodeValue - 2)
        }

        var p: Int = 0
        let version: Int = data[p].unicodeValue    //toInt(data[p++]);
        p += 1
        if version != ATNDeserializer.SERIALIZED_VERSION {

            let reason: String = "Could not deserialize ATN with version \(version) (expected \(ATNDeserializer.SERIALIZED_VERSION))."

            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let uuid: UUID = toUUID(data, p)
        p += 8
        if !ATNDeserializer.SUPPORTED_UUIDS.contains(uuid) {
            let reason: String = "Could not deserialize ATN with UUID \(uuid) (expected \(ATNDeserializer.SERIALIZED_UUID) or a legacy UUID)."
            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let supportsPrecedencePredicates: Bool = isFeatureSupported(ATNDeserializer.ADDED_PRECEDENCE_TRANSITIONS, uuid)
        let supportsLexerActions: Bool = isFeatureSupported(ATNDeserializer.ADDED_LEXER_ACTIONS, uuid)

        let grammarType: ATNType = ATNType(rawValue: toInt(data[p]))!
        p += 1
        let maxTokenType: Int = toInt(data[p])
        p += 1
        let atn: ATN = ATN(grammarType, maxTokenType)

        //
        // STATES
        //
        var loopBackStateNumbers: Array<(LoopEndState, Int)> = Array<(LoopEndState, Int)>()
        var endStateNumbers: Array<(BlockStartState, Int)> = Array<(BlockStartState, Int)>()
        let nstates: Int = toInt(data[p])
        p += 1
        for _ in 0..<nstates {
            let stype: Int = toInt(data[p])
            p += 1
            // ignore bad type of states
            if stype == ATNState.INVALID_TYPE {
                atn.addState(nil)
                continue
            }

            var ruleIndex: Int = toInt(data[p])
            p += 1
            if ruleIndex == Int.max {
                // Character.MAX_VALUE
                ruleIndex = -1
            }

            let s: ATNState = try stateFactory(stype, ruleIndex)!
            if stype == ATNState.LOOP_END {
                // special case
                let loopBackStateNumber: Int = toInt(data[p])
                p += 1
                loopBackStateNumbers.append((s as! LoopEndState, loopBackStateNumber))
            } else {
                if let s = s as? BlockStartState {
                    let endStateNumber: Int = toInt(data[p])
                    p += 1
                    endStateNumbers.append(s, endStateNumber)
                }
            }
            atn.addState(s)
        }

        // delay the assignment of loop back and end states until we know all the state instances have been initialized
        for pair: (LoopEndState, Int) in loopBackStateNumbers {
            pair.0.loopBackState = atn.states[pair.1]
        }

        for pair: (BlockStartState, Int) in endStateNumbers {
            pair.0.endState = atn.states[pair.1] as? BlockEndState
        }

        let numNonGreedyStates: Int = toInt(data[p])
        p += 1
        for _ in 0..<numNonGreedyStates{
            let stateNumber: Int = toInt(data[p])
            p += 1
            (atn.states[stateNumber] as! DecisionState).nonGreedy = true
        }

        if supportsPrecedencePredicates {
            let numPrecedenceStates: Int = toInt(data[p])
            p += 1
            for _ in 0..<numPrecedenceStates {
                let stateNumber: Int = toInt(data[p])
                p += 1
                (atn.states[stateNumber] as! RuleStartState).isPrecedenceRule = true
            }
        }

        //
        // RULES
        //
        let nrules: Int = toInt(data[p])
        p += 1
        if atn.grammarType == ATNType.lexer {
            atn.ruleToTokenType = [nrules]
        }

        atn.ruleToStartState = [RuleStartState]() // [nrules];
        for i in 0..<nrules {
            let s: Int = toInt(data[p])
            p += 1
            let startState: RuleStartState = atn.states[s] as! RuleStartState
            atn.ruleToStartState[i] = startState
            if atn.grammarType == ATNType.lexer {
                var tokenType: Int = toInt(data[p])
                p += 1
                if tokenType == 0xFFFF {
                    tokenType = CommonToken.EOF
                }

                atn.ruleToTokenType[i] = tokenType

                if !isFeatureSupported(ATNDeserializer.ADDED_LEXER_ACTIONS, uuid) {
                    // this piece of unused metadata was serialized prior to the
                    // addition of LexerAction
                    var actionIndexIgnored: Int = toInt(data[p])
                    p += 1
                    if actionIndexIgnored == 0xFFFF {
                        actionIndexIgnored = -1
                    }
                }
            }
        }

        atn.ruleToStopState = [RuleStopState]()//new RuleStopState[nrules];
        for state: ATNState? in atn.states {
            if let stopState = state as? RuleStopState {
                if let index = stopState.ruleIndex {
                    atn.ruleToStopState[index] = stopState
                    atn.ruleToStartState[index].stopState = stopState
                }
            }
        }

        //
        // MODES
        //
        let nmodes: Int = toInt(data[p])
        p += 1
        for _ in 0..<nmodes {
            let s: Int = toInt(data[p])
            p += 1
            atn.appendModeToStartState(atn.states[s] as! TokensStartState)
            //atn.modeToStartState.append(atn.states[s] as! TokensStartState)
        }

        //
        // SETS
        //
        var sets: Array<IntervalSet> = Array<IntervalSet>()
        let nsets: Int = toInt(data[p])
        p += 1
        for _ in 0..<nsets {
            let nintervals: Int = toInt(data[p])
            p += 1
            let set: IntervalSet = try IntervalSet()
            sets.append(set)

            let containsEof: Bool = toInt(data[p]) != 0
            p += 1
            if containsEof {
                try set.add(-1)
            }

            for _ in 0..<nintervals {
                try set.add(toInt(data[p]), toInt(data[p + 1]))
                p += 2
            }
        }

        //
        // EDGES
        //
        let nedges: Int = toInt(data[p])
        p += 1
        for _ in 0..<nedges {
            let src: Int = toInt(data[p])
            let trg: Int = toInt(data[p + 1])
            let ttype: Int = toInt(data[p + 2])
            let arg1: Int = toInt(data[p + 3])
            let arg2: Int = toInt(data[p + 4])
            let arg3: Int = toInt(data[p + 5])
            let trans: Transition = try edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets)

            let srcState: ATNState = atn.states[src]!
            srcState.addTransition(trans)
            p += 6
        }

        // edges for rule stop states can be derived, so they aren't serialized
        for state: ATNState? in atn.states {
            if let state = state {
                let length = state.getNumberOfTransitions()
                for i in 0..<length {
                    let t: Transition = state.transition(i)
                    if !(t is RuleTransition) {
                        continue
                    }

                    let ruleTransition: RuleTransition = t as! RuleTransition
                    var outermostPrecedenceReturn: Int = -1
                    if atn.ruleToStartState[ruleTransition.target.ruleIndex!].isPrecedenceRule {
                        if ruleTransition.precedence == 0 {
                            outermostPrecedenceReturn = ruleTransition.target.ruleIndex!
                        }
                    }

                    let returnTransition: EpsilonTransition = EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn)
                    atn.ruleToStopState[ruleTransition.target.ruleIndex!].addTransition(returnTransition)
                }
            }
        }

        for state: ATNState? in atn.states {

            if let state = state as? BlockStartState {
                // we need to know the end state to set its start state
                if state.endState == nil {
                    throw ANTLRError.illegalState(msg: "state.endState == nil")

                }

                // block end states can only be associated to a single block start state
                if let endState = state.endState {
                    if endState.startState != nil {
                        throw ANTLRError.illegalState(msg: "state.endState.startState != nil")

                    }

                    endState.startState = state
                }
            }


            if let loopbackState = state as? PlusLoopbackState {
                let length = loopbackState.getNumberOfTransitions()
                for i in 0..<length {
                    let target: ATNState = loopbackState.transition(i).target
                    if target is PlusBlockStartState {
                        (target as! PlusBlockStartState).loopBackState = loopbackState
                    }
                }
            } else {
                if let loopbackState = state as? StarLoopbackState {
                    let length = loopbackState.getNumberOfTransitions()
                    for i in 0..<length {
                        let target: ATNState = loopbackState.transition(i).target
                        if let target = target as? StarLoopEntryState {
                            target.loopBackState = loopbackState
                        }
                    }
                }
            }
        }

        //
        // DECISIONS
        //
        let ndecisions: Int = toInt(data[p])
        p += 1
        for i in 1...ndecisions {
            let s: Int = toInt(data[p])
            p += 1
            let decState: DecisionState = atn.states[s] as! DecisionState
            atn.appendDecisionToState(decState)
            //atn.decisionToState.append(decState)
            decState.decision = i - 1
        }

        //
        // LEXER ACTIONS
        //
        if atn.grammarType == ATNType.lexer {
            if supportsLexerActions {
                atn.lexerActions = [LexerAction]()   //[toInt(data[p++])];
                let length = atn.lexerActions.count
                for i in 0..<length {
                    let actionType: LexerActionType = LexerActionType(rawValue: toInt(data[p]))! //LexerActionType.values()[toInt(data[p++])];
                    p += 1
                    var data1: Int = toInt(data[p])
                    p += 1
                    if data1 == 0xFFFF {
                        data1 = -1
                    }

                    var data2: Int = toInt(data[p])
                    p += 1
                    if data2 == 0xFFFF {
                        data2 = -1
                    }

                    let lexerAction: LexerAction = lexerActionFactory(actionType, data1, data2)

                    atn.lexerActions[i] = lexerAction
                }
            } else {
                // for compatibility with older serialized ATNs, convert the old
                // serialized action index for action transitions to the new
                // form, which is the index of a LexerCustomAction
                var legacyLexerActions: Array<LexerAction> = Array<LexerAction>()
                for state: ATNState? in atn.states {
                    if let state = state {
                        let length = state.getNumberOfTransitions()
                        for i in 0..<length {
                            let transition: Transition = state.transition(i)
                            if !(transition is ActionTransition) {
                                continue
                            }

                            let ruleIndex: Int = (transition as! ActionTransition).ruleIndex
                            let actionIndex: Int = (transition as! ActionTransition).actionIndex
                            let lexerAction: LexerCustomAction = LexerCustomAction(ruleIndex, actionIndex)
                            state.setTransition(i, ActionTransition(transition.target, ruleIndex, legacyLexerActions.count, false))
                            legacyLexerActions.append(lexerAction)
                        }
                    }
                }

                atn.lexerActions = legacyLexerActions //.toArray(new, LexerAction[legacyLexerActions.size()]);
            }
        }

        markPrecedenceDecisions(atn)

        if deserializationOptions.isVerifyATN() {
            try verifyATN(atn)
        }

        if deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType.parser {
            atn.ruleToTokenType = [Int]()  //new int[atn.ruleToStartState.length];
            let length = atn.ruleToStartState.count
            for i in 0..<length {
                atn.ruleToTokenType[i] = atn.maxTokenType + i + 1
            }

            for i in 0..<length {
                let bypassStart: BasicBlockStartState = BasicBlockStartState()
                bypassStart.ruleIndex = i
                atn.addState(bypassStart)

                let bypassStop: BlockEndState = BlockEndState()
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
                    for state: ATNState? in atn.states {
                        if let state = state {
                            if state.ruleIndex != i {
                                continue
                            }

                            if !(state is StarLoopEntryState) {
                                continue
                            }

                            let maybeLoopEndState: ATNState = state.transition(state.getNumberOfTransitions() - 1).target
                            if !(maybeLoopEndState is LoopEndState) {
                                continue
                            }

                            if maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target is RuleStopState {
                                endState = state
                                break
                            }
                        }
                    }

                    if endState == nil {
                        throw ANTLRError.unsupportedOperation(msg: "Couldn't identify final state of the precedence rule prefix section.")

                    }

                    excludeTransition = (endState as? StarLoopEntryState)?.loopBackState?.transition(0)
                } else {
                    endState = atn.ruleToStopState[i]
                }

                // all non-excluded transitions that currently target end state need to target blockEnd instead
                for state: ATNState? in atn.states {
                    if let state = state {
                        for transition: Transition in state.transitions {
                            if transition === excludeTransition! {
                                continue
                            }

                            if transition.target == endState {
                                transition.target = bypassStop
                            }
                        }
                    }
                }

                // all transitions leaving the rule start state need to leave blockStart instead
                while atn.ruleToStartState[i].getNumberOfTransitions() > 0 {
                    let transition: Transition = atn.ruleToStartState[i].removeTransition(atn.ruleToStartState[i].getNumberOfTransitions() - 1)
                    bypassStart.addTransition(transition)
                }

                // link the new states
                atn.ruleToStartState[i].addTransition(EpsilonTransition(bypassStart))
                bypassStop.addTransition(EpsilonTransition(endState!))

                let matchState: ATNState = BasicState()
                atn.addState(matchState)
                matchState.addTransition(AtomTransition(bypassStop, atn.ruleToTokenType[i]))
                bypassStart.addTransition(EpsilonTransition(matchState))
            }

            if deserializationOptions.isVerifyATN() {
                // reverify after modification
                try verifyATN(atn)
            }
        }

        return atn
    }

    public func deserializeFromJson(_ jsonStr: String) -> ATN {
        // let jsonStr = Utils.readFile2String(jsonFileName)
        guard !jsonStr.isEmpty else {
            fatalError("ATN Serialization is empty,Please include *LexerATN.json and  *ParserATN.json in TARGETS-Build Phases-Copy Bundle Resources")
        }
        if let JSONData = jsonStr.data(using: String.Encoding.utf8) {
            do {
                let JSON = try JSONSerialization.jsonObject(with: JSONData, options: JSONSerialization.ReadingOptions(rawValue: 0))
                guard let JSONDictionary: NSDictionary = JSON as? NSDictionary  else {
                    print("Not a Dictionary")
                    // put in function
                    fatalError("deserializeFromJson Not a Dictionary")
                }

                return try dictToJson(JSONDictionary)

            } catch let JSONError as NSError {
                print("\(JSONError)")
            }
        }

        fatalError("Could not deserialize ATN ")
    }

    public func dictToJson(_ dict: NSDictionary) throws -> ATN {


        let version: Int = dict.object(forKey: "version") as! Int
        if version != ATNDeserializer.SERIALIZED_VERSION {

            let reason: String = "Could not deserialize ATN with version \(version) (expected \(ATNDeserializer.SERIALIZED_VERSION))."

            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let uuid: UUID = UUID(uuidString: dict.object(forKey: "uuid") as! String)!

        if !ATNDeserializer.SUPPORTED_UUIDS.contains(uuid) {
            let reason: String = "Could not deserialize ATN with UUID \(uuid) (expected \(ATNDeserializer.SERIALIZED_UUID) or a legacy UUID)."

            throw ANTLRError.unsupportedOperation(msg: reason)
        }

        let supportsPrecedencePredicates: Bool = isFeatureSupported(ATNDeserializer.ADDED_PRECEDENCE_TRANSITIONS, uuid)
        let supportsLexerActions: Bool = isFeatureSupported(ATNDeserializer.ADDED_LEXER_ACTIONS, uuid)

        let grammarType: ATNType = ATNType(rawValue: dict.object(forKey: "grammarType") as! Int)!
        let maxTokenType: Int = dict.object(forKey: "maxTokenType") as! Int
        let atn: ATN = ATN(grammarType, maxTokenType)

        //
        // STATES
        //
        var loopBackStateNumbers: Array<(LoopEndState, Int)> = Array<(LoopEndState, Int)>()
        var endStateNumbers: Array<(BlockStartState, Int)> = Array<(BlockStartState, Int)>()

        let states = dict.object(forKey: "states") as! [NSDictionary]

        for state in states {


            let ruleIndex: Int = state.object(forKey: "ruleIndex") as! Int

            let stype: Int = state.object(forKey: "stateType") as! Int
            let s: ATNState = try stateFactory(stype, ruleIndex)!
            if stype == ATNState.LOOP_END {
                // special case
                let loopBackStateNumber: Int = state.object(forKey: "detailStateNumber") as! Int
                loopBackStateNumbers.append((s as! LoopEndState, loopBackStateNumber))
            } else {
                if s is BlockStartState {
                    let endStateNumber: Int = state.object(forKey: "detailStateNumber") as! Int
                    endStateNumbers.append((s as! BlockStartState, endStateNumber))
                }
            }
            atn.addState(s)
        }



        // delay the assignment of loop back and end states until we know all the state instances have been initialized
        for pair: (LoopEndState, Int) in loopBackStateNumbers {
            pair.0.loopBackState = atn.states[pair.1]
        }

        for pair: (BlockStartState, Int) in endStateNumbers {
            pair.0.endState = atn.states[pair.1] as? BlockEndState
        }

        let numNonGreedyStates = dict.object(forKey: "nonGreedyStates") as! [Int]
        for numNonGreedyState in numNonGreedyStates {
            (atn.states[numNonGreedyState] as! DecisionState).nonGreedy = true
        }

        if supportsPrecedencePredicates {
            let numPrecedenceStates = dict.object(forKey: "precedenceStates") as! [Int]
            for numPrecedenceState in numPrecedenceStates {
                (atn.states[numPrecedenceState] as! RuleStartState).isPrecedenceRule = true
            }
        }


        //
        // RULES
        //
        let ruleToStartState = dict.object(forKey: "ruleToStartState") as! [NSDictionary]

        let nrules: Int = ruleToStartState.count
        if atn.grammarType == ATNType.lexer {
            atn.ruleToTokenType = [Int](repeating: 0, count: nrules)
        }

        atn.ruleToStartState = [RuleStartState](repeating: RuleStartState(), count: nrules) // [nrules];
        for i in 0..<nrules {
            let currentRuleToStartState = ruleToStartState[i]
            let s: Int = currentRuleToStartState.object(forKey: "stateNumber") as! Int
            let startState: RuleStartState = atn.states[s] as! RuleStartState
            atn.ruleToStartState[i] = startState
            if atn.grammarType == ATNType.lexer {
                var tokenType: Int = currentRuleToStartState.object(forKey: "ruleToTokenType") as! Int
                if tokenType == -1 {
                    tokenType = CommonToken.EOF
                }

                atn.ruleToTokenType[i] = tokenType

            }
        }

        atn.ruleToStopState = [RuleStopState](repeating: RuleStopState(), count: nrules)

        for state: ATNState? in atn.states {

            if let stopState = state as? RuleStopState {
                if let index = stopState.ruleIndex {
                    atn.ruleToStopState[index] = stopState
                    atn.ruleToStartState[index].stopState = stopState
                }
            }

        }


        //
        // MODES
        //
        let modeToStartState = dict.object(forKey: "modeToStartState") as! [Int]
        //let nmodes : Int = toInt(data[p++]);
        //for  var i : Int=0; i<nmodes; i++ {
        for stateNumber in modeToStartState {
            let s: Int = stateNumber
            atn.appendModeToStartState(atn.states[s] as! TokensStartState)
            //atn.modeToStartState.append(atn.states[s] as! TokensStartState)
        }




        //
        // SETS
        //
        var sets: Array<IntervalSet> = Array<IntervalSet>()
        let nsets: Int = dict.object(forKey: "nsets") as! Int
        let intervalSet = dict.object(forKey: "IntervalSet") as! [NSDictionary]

        for i in 0..<nsets {
            let setBuilder = intervalSet[i]
            let nintervals: Int = setBuilder.object(forKey: "size") as! Int

            let set: IntervalSet = try IntervalSet()
            sets.append(set)

            let containsEof: Bool = (setBuilder.object(forKey: "containsEof") as! Int) != 0
            if containsEof {
                try set.add(-1)
            }
            let intervalsBuilder = setBuilder.object(forKey: "Intervals") as! [NSDictionary]


            for j in 0..<nintervals {
                let vals = intervalsBuilder[j]
                try set.add((vals.object(forKey: "a") as! Int), (vals.object(forKey: "b") as! Int))

            }
        }


        //
        // EDGES
        //
        //   let nedges : Int = dict.objectForKey("nedges") as!  Int
        let allTransitions = dict.object(forKey: "allTransitionsBuilder") as! [[NSDictionary]]

        for transitionsBuilder in allTransitions {

            for transition in transitionsBuilder {
                let src: Int = transition.object(forKey: "src") as! Int
                let trg: Int = transition.object(forKey: "trg") as! Int
                let ttype: Int = transition.object(forKey: "edgeType") as! Int
                let arg1: Int = transition.object(forKey: "arg1") as! Int
                let arg2: Int = transition.object(forKey: "arg2") as! Int
                let arg3: Int = transition.object(forKey: "arg3") as! Int
                let trans: Transition = try edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets)

                let srcState: ATNState = atn.states[src]!
                srcState.addTransition(trans)
            }

        }


        // edges for rule stop states can be derived, so they aren't serialized
        for state: ATNState? in atn.states {
            if let state =  state {
                let length = state.getNumberOfTransitions()
                for i in 0..<length {
                    let t: Transition = state.transition(i)
                    if let ruleTransition = t as? RuleTransition {
                        var outermostPrecedenceReturn: Int = -1
                        if let targetRuleIndex = ruleTransition.target.ruleIndex {
                            if atn.ruleToStartState[targetRuleIndex].isPrecedenceRule {
                                if ruleTransition.precedence == 0 {
                                    outermostPrecedenceReturn = targetRuleIndex
                                }
                            }

                            let returnTransition: EpsilonTransition = EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn)
                            atn.ruleToStopState[targetRuleIndex].addTransition(returnTransition)
                        }
                    }
                }
            }
        }

        for state: ATNState? in atn.states {
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

            if let loopbackState = state as? PlusLoopbackState {
                let length = loopbackState.getNumberOfTransitions()
                for i in 0..<length {
                    let target: ATNState = loopbackState.transition(i).target
                    if target is PlusBlockStartState {
                        (target as! PlusBlockStartState).loopBackState = loopbackState
                    }
                }
            } else {
                if let loopbackState = state as? StarLoopbackState {
                    let length = loopbackState.getNumberOfTransitions()
                    for i in 0..<length {
                        let target: ATNState = loopbackState.transition(i).target
                        if target is StarLoopEntryState {
                            (target as! StarLoopEntryState).loopBackState = loopbackState
                        }
                    }
                }
            }
        }


        //
        // DECISIONS
        //
        let ndecisions: [Int] = dict.object(forKey: "decisionToState") as! [Int]
        let length = ndecisions.count
        for i in 0..<length {
            let s: Int = ndecisions[i]
            let decState: DecisionState = atn.states[s] as! DecisionState
            atn.appendDecisionToState(decState)
            //atn.decisionToState.append(decState)
            decState.decision = i
        }

        //
        // LEXER ACTIONS
        //
        if atn.grammarType == ATNType.lexer {
            let lexerActionsBuilder = dict.object(forKey: "lexerActions") as! [NSDictionary]
            if supportsLexerActions {
                atn.lexerActions = [LexerAction](repeating: LexerAction(), count: lexerActionsBuilder.count)   //[toInt(data[p++])];
                let length = atn.lexerActions.count
                for i in 0..<length {
                    let actionTypeValue = lexerActionsBuilder[i].object(forKey: "actionType") as! Int
                    let actionType: LexerActionType = LexerActionType(rawValue: actionTypeValue)! //LexerActionType.values()[toInt(data[p++])];
                    let data1: Int = lexerActionsBuilder[i].object(forKey: "a") as! Int


                    let data2: Int = lexerActionsBuilder[i].object(forKey: "b") as! Int


                    let lexerAction: LexerAction = lexerActionFactory(actionType, data1, data2)

                    atn.lexerActions[i] = lexerAction
                }
            } else {
                // for compatibility with older serialized ATNs, convert the old
                // serialized action index for action transitions to the new
                // form, which is the index of a LexerCustomAction
                var legacyLexerActions: Array<LexerAction> = Array<LexerAction>()
                for state: ATNState? in atn.states {
                    if let state = state {
                        let length = state.getNumberOfTransitions()
                        for i in 0..<length {
                            let transition: Transition = state.transition(i)
                            if !(transition is ActionTransition) {
                                continue
                            }

                            let ruleIndex: Int = (transition as! ActionTransition).ruleIndex
                            let actionIndex: Int = (transition as! ActionTransition).actionIndex
                            let lexerAction: LexerCustomAction = LexerCustomAction(ruleIndex, actionIndex)
                            state.setTransition(i, ActionTransition(transition.target, ruleIndex, legacyLexerActions.count, false))
                            legacyLexerActions.append(lexerAction)
                        }
                    }
                }

                atn.lexerActions = legacyLexerActions
            }
        }

        markPrecedenceDecisions(atn)

        if deserializationOptions.isVerifyATN() {
            try verifyATN(atn)
        }

        if deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType.parser {
            atn.ruleToTokenType = [Int]()
            let length = atn.ruleToStartState.count
            for i in 0..<length {
                atn.ruleToTokenType[i] = atn.maxTokenType + i + 1
            }

            for i in 0..<length {
                let bypassStart: BasicBlockStartState = BasicBlockStartState()
                bypassStart.ruleIndex = i
                atn.addState(bypassStart)

                let bypassStop: BlockEndState = BlockEndState()
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
                    for state: ATNState? in atn.states {
                        if let state = state {
                            if state.ruleIndex != i {
                                continue
                            }

                            if !(state is StarLoopEntryState) {
                                continue
                            }

                            let maybeLoopEndState: ATNState = state.transition(state.getNumberOfTransitions() - 1).target
                            if !(maybeLoopEndState is LoopEndState) {
                                continue
                            }

                            if maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target is RuleStopState {
                                endState = state
                                break
                            }
                        }
                    }

                    if endState == nil {
                        throw ANTLRError.unsupportedOperation(msg: "Couldn't identify final state of the precedence rule prefix section.")

                    }

                    excludeTransition = (endState as? StarLoopEntryState)?.loopBackState?.transition(0)
                } else {
                    endState = atn.ruleToStopState[i]
                }

                // all non-excluded transitions that currently target end state need to target blockEnd instead
                for state: ATNState? in atn.states {
                    if let state = state {
                        for transition: Transition in state.transitions {
                            if transition === excludeTransition! {
                                continue
                            }

                            if transition.target == endState {
                                transition.target = bypassStop
                            }
                        }
                    }
                }

                // all transitions leaving the rule start state need to leave blockStart instead
                while atn.ruleToStartState[i].getNumberOfTransitions() > 0 {
                    let transition: Transition = atn.ruleToStartState[i].removeTransition(atn.ruleToStartState[i].getNumberOfTransitions() - 1)
                    bypassStart.addTransition(transition)
                }

                // link the new states
                atn.ruleToStartState[i].addTransition(EpsilonTransition(bypassStart))
                bypassStop.addTransition(EpsilonTransition(endState!))

                let matchState: ATNState = BasicState()
                atn.addState(matchState)
                matchState.addTransition(AtomTransition(bypassStop, atn.ruleToTokenType[i]))
                bypassStart.addTransition(EpsilonTransition(matchState))
            }

            if deserializationOptions.isVerifyATN() {
                // reverify after modification
                try verifyATN(atn)
            }
        }

        return atn
    }


    /// Analyze the {@link org.antlr.v4.runtime.atn.StarLoopEntryState} states in the specified ATN to set
    /// the {@link org.antlr.v4.runtime.atn.StarLoopEntryState#precedenceRuleDecision} field to the
    /// correct value.
    /// 
    /// - parameter atn: The ATN.
    internal func markPrecedenceDecisions(_ atn: ATN) {
        for state: ATNState? in atn.states {
            if let state = state as? StarLoopEntryState {

                /// We analyze the ATN to determine if this ATN decision state is the
                /// decision for the closure block that determines whether a
                /// precedence rule should continue or complete.
                if let stateRuleIndex = state.ruleIndex {
                    if  atn.ruleToStartState[stateRuleIndex].isPrecedenceRule {
                        let maybeLoopEndState: ATNState = state.transition(state.getNumberOfTransitions() - 1).target
                        if maybeLoopEndState is LoopEndState {
                            if maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target is RuleStopState {
                                state.precedenceRuleDecision = true
                            }
                        }
                    }
                }
            }
        }
    }

    internal func verifyATN(_ atn: ATN) throws {
        // verify assumptions
        for state: ATNState? in atn.states {
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

            if state is DecisionState {
                let decisionState: DecisionState = state as! DecisionState
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
                              _ sets: Array<IntervalSet>) throws -> Transition {
        let target: ATNState = atn.states[trg]!
        switch type {
        case Transition.EPSILON: return EpsilonTransition(target)
        case Transition.RANGE:
            if arg3 != 0 {
                return RangeTransition(target, CommonToken.EOF, arg2)
            } else {
                return RangeTransition(target, arg1, arg2)
            }
        case Transition.RULE:
            let rt: RuleTransition = RuleTransition(atn.states[arg1] as! RuleStartState, arg2, arg3, target)
            return rt
        case Transition.PREDICATE:
            let pt: PredicateTransition = PredicateTransition(target, arg1, arg2, arg3 != 0)
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
            let a: ActionTransition = ActionTransition(target, arg1, arg2, arg3 != 0)
            return a
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

                //default:

        }
        //  let message : String = "The specified lexer action type \(type) is not valid."
        // RuntimeException(message)

    }
}
