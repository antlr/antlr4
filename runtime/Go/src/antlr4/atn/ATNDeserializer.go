package atn

//var Token = require('./../Token').Token
//var ATN = require('./ATN').ATN
//var ATNType = require('./ATNType').ATNType
//var ATNStates = require('./ATNState')
var ATNState = ATNStates.ATNState
var BasicState = ATNStates.BasicState
var DecisionState = ATNStates.DecisionState
var BlockStartState = ATNStates.BlockStartState
var BlockEndState = ATNStates.BlockEndState
var LoopEndState = ATNStates.LoopEndState
var RuleStartState = ATNStates.RuleStartState
var RuleStopState = ATNStates.RuleStopState
var TokensStartState = ATNStates.TokensStartState
var PlusLoopbackState = ATNStates.PlusLoopbackState
var StarLoopbackState = ATNStates.StarLoopbackState
var StarLoopEntryState = ATNStates.StarLoopEntryState
var PlusBlockStartState = ATNStates.PlusBlockStartState
var StarBlockStartState = ATNStates.StarBlockStartState
var BasicBlockStartState = ATNStates.BasicBlockStartState
//var Transitions = require('./Transition')
var Transition = Transitions.Transition
var AtomTransition = Transitions.AtomTransition
var SetTransition = Transitions.SetTransition
var NotSetTransition = Transitions.NotSetTransition
var RuleTransition = Transitions.RuleTransition
var RangeTransition = Transitions.RangeTransition
var ActionTransition = Transitions.ActionTransition
var EpsilonTransition = Transitions.EpsilonTransition
var WildcardTransition = Transitions.WildcardTransition
var PredicateTransition = Transitions.PredicateTransition
var PrecedencePredicateTransition = Transitions.PrecedencePredicateTransition
//var IntervalSet = require('./../IntervalSet').IntervalSet
//var Interval = require('./../IntervalSet').Interval
//var ATNDeserializationOptions = require('./ATNDeserializationOptions').ATNDeserializationOptions
//var LexerActions = require('./LexerAction')
var LexerActionType = LexerActions.LexerActionType
var LexerSkipAction = LexerActions.LexerSkipAction
var LexerChannelAction = LexerActions.LexerChannelAction
var LexerCustomAction = LexerActions.LexerCustomAction
var LexerMoreAction = LexerActions.LexerMoreAction
var LexerTypeAction = LexerActions.LexerTypeAction
var LexerPushModeAction = LexerActions.LexerPushModeAction
var LexerPopModeAction = LexerActions.LexerPopModeAction
var LexerModeAction = LexerActions.LexerModeAction
// This is the earliest supported serialized UUID.
// stick to serialized version for now, we don't need a UUID instance
var BASE_SERIALIZED_UUID = "AADB8D7E-AEEF-4415-AD2B-8204D6CF042E"

// This list contains all of the currently supported UUIDs, ordered by when
// the feature first appeared in this branch.
var SUPPORTED_UUIDS = [ BASE_SERIALIZED_UUID ]

var SERIALIZED_VERSION = 3

// This is the current serialized UUID.
var SERIALIZED_UUID = BASE_SERIALIZED_UUID

func initArray( length, value) {
	var tmp = []
	tmp[length-1] = value
	return tmp.map(function(i) {return value})
}

func ATNDeserializer (options) {
	
    if ( options== undefined || options == nil ) {
        options = ATNDeserializationOptions.defaultOptions
    }
    this.deserializationOptions = options
    this.stateFactories = nil
    this.actionFactories = nil
    
    return this
}

// Determines if a particular serialized representation of an ATN supports
// a particular feature, identified by the {@link UUID} used for serializing
// the ATN at the time the feature was first introduced.
//
// @param feature The {@link UUID} marking the first time the feature was
// supported in the serialized ATN.
// @param actualUuid The {@link UUID} of the actual serialized ATN which is
// currently being deserialized.
// @return {@code true} if the {@code actualUuid} value represents a
// serialized ATN at or after the feature identified by {@code feature} was
// introduced otherwise, {@code false}.

func (this *ATNDeserializer) isFeatureSupported(feature, actualUuid) {
    var idx1 = SUPPORTED_UUIDS.index(feature)
    if (idx1<0) {
        return false
    }
    var idx2 = SUPPORTED_UUIDS.index(actualUuid)
    return idx2 >= idx1
}

func (this *ATNDeserializer) deserialize(data) {
    this.reset(data)
    this.checkVersion()
    this.checkUUID()
    var atn = this.readATN()
    this.readStates(atn)
    this.readRules(atn)
    this.readModes(atn)
    var sets = this.readSets(atn)
    this.readEdges(atn, sets)
    this.readDecisions(atn)
    this.readLexerActions(atn)
    this.markPrecedenceDecisions(atn)
    this.verifyATN(atn)
    if (this.deserializationOptions.generateRuleBypassTransitions && atn.grammarType == ATNTypeParser ) {
        this.generateRuleBypassTransitions(atn)
        // re-verify after modification
        this.verifyATN(atn)
    }
    return atn
}

func (this *ATNDeserializer) reset(data) {
	var adjust = function(c) {
        var v = c.charCodeAt(0)
        return v>1  ? v-2 : -1
	}
    var temp = data.split("").map(adjust)
    // don't adjust the first value since that's the version number
    temp[0] = data.charCodeAt(0)
    this.data = temp
    this.pos = 0
}

func (this *ATNDeserializer) checkVersion() {
    var version = this.readInt()
    if ( version != SERIALIZED_VERSION ) {
        panic ("Could not deserialize ATN with version " + version + " (expected " + SERIALIZED_VERSION + ").")
    }
}

func (this *ATNDeserializer) checkUUID() {
    var uuid = this.readUUID()
    if (SUPPORTED_UUIDS.indexOf(uuid)<0) {
        panic ("Could not deserialize ATN with UUID: " + uuid +
                        " (expected " + SERIALIZED_UUID + " or a legacy UUID).", uuid, SERIALIZED_UUID)
    }
    this.uuid = uuid
}

func (this *ATNDeserializer) readATN() {
    var grammarType = this.readInt()
    var maxTokenType = this.readInt()
    return NewATN(grammarType, maxTokenType)
}

func (this *ATNDeserializer) readStates(atn) {
	var j, pair, stateNumber
    var loopBackStateNumbers = []
    var endStateNumbers = []
    var nstates = this.readInt()
    for(var i=0 i<nstates i++) {
        var stype = this.readInt()
        // ignore bad type of states
        if (stype==ATNStateInvalidType) {
            atn.addState(nil)
            continue
        }
        var ruleIndex = this.readInt()
        if (ruleIndex == 0xFFFF) {
            ruleIndex = -1
        }
        var s = this.stateFactory(stype, ruleIndex)
        if (stype == ATNStateLOOP_END) { // special case
            var loopBackStateNumber = this.readInt()
            loopBackStateNumbers.push([s, loopBackStateNumber])
        } else if_, ok := s.(BlockStartState); ok {
            var endStateNumber = this.readInt()
            endStateNumbers.push([s, endStateNumber])
        }
        atn.addState(s)
    }
    // delay the assignment of loop back and end states until we know all the
	// state instances have been initialized
    for (j=0 j<loopBackStateNumbers.length j++) {
        pair = loopBackStateNumbers[j]
        pair[0].loopBackState = atn.states[pair[1]]
    }

    for (j=0 j<endStateNumbers.length j++) {
        pair = endStateNumbers[j]
        pair[0].endState = atn.states[pair[1]]
    }
    
    var numNonGreedyStates = this.readInt()
    for (j=0 j<numNonGreedyStates j++) {
        stateNumber = this.readInt()
        atn.states[stateNumber].nonGreedy = true
    }

    var numPrecedenceStates = this.readInt()
    for (j=0 j<numPrecedenceStates j++) {
        stateNumber = this.readInt()
        atn.states[stateNumber].isPrecedenceRule = true
    }
}

func (this *ATNDeserializer) readRules(atn) {
    var i
    var nrules = this.readInt()
    if (atn.grammarType == ATNTypeLexer ) {
        atn.ruleToTokenType = initArray(nrules, 0)
    }
    atn.ruleToStartState = initArray(nrules, 0)
    for (i=0 i<nrules i++) {
        var s = this.readInt()
        var startState = atn.states[s]
        atn.ruleToStartState[i] = startState
        if ( atn.grammarType == ATNTypeLexer ) {
            var tokenType = this.readInt()
            if (tokenType == 0xFFFF) {
                tokenType = TokenEOF
            }
            atn.ruleToTokenType[i] = tokenType
        }
    }
    atn.ruleToStopState = initArray(nrules, 0)
    for (i=0 i<atn.states.length i++) {
        var state = atn.states[i]
        if (!_, ok := state.(RuleStopState); ok) {
            continue
        }
        atn.ruleToStopState[state.ruleIndex] = state
        atn.ruleToStartState[state.ruleIndex].stopState = state
    }
}

func (this *ATNDeserializer) readModes(atn) {
    var nmodes = this.readInt()
    for (var i=0 i<nmodes i++) {
        var s = this.readInt()
        atn.modeToStartState.push(atn.states[s])
    }
}

func (this *ATNDeserializer) readSets(atn) {
    var sets = []
    var m = this.readInt()
    for (var i=0 i<m i++) {
        var iset = NewIntervalSet()
        sets.push(iset)
        var n = this.readInt()
        var containsEof = this.readInt()
        if (containsEof!=0) {
            iset.addOne(-1)
        }
        for (var j=0 j<n j++) {
            var i1 = this.readInt()
            var i2 = this.readInt()
            iset.addRange(i1, i2)
        }
    }
    return sets
}

func (this *ATNDeserializer) readEdges(atn, sets) {
	var i, j, state, trans, target
    var nedges = this.readInt()
    for (i=0 i<nedges i++) {
        var src = this.readInt()
        var trg = this.readInt()
        var ttype = this.readInt()
        var arg1 = this.readInt()
        var arg2 = this.readInt()
        var arg3 = this.readInt()
        trans = this.edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets)
        var srcState = atn.states[src]
        srcState.addTransition(trans)
    }
    // edges for rule stop states can be derived, so they aren't serialized
    for (i=0 i<atn.states.length i++) {
        state = atn.states[i]
        for (j=0 j<state.transitions.length j++) {
            var t = state.transitions[j]
            if (!_, ok := t.(RuleTransition); ok) {
                continue
            }
			var outermostPrecedenceReturn = -1
			if (atn.ruleToStartState[t.target.ruleIndex].isPrecedenceRule) {
				if (t.precedence == 0) {
					outermostPrecedenceReturn = t.target.ruleIndex
				}
			}

			trans = NewEpsilonTransition(t.followState, outermostPrecedenceReturn)
            atn.ruleToStopState[t.target.ruleIndex].addTransition(trans)
        }
    }

    for (i=0 i<atn.states.length i++) {
        state = atn.states[i]
        if _, ok := state.(BlockStartState); ok {
            // we need to know the end state to set its start state
            if (state.endState == nil) {
                panic ("IllegalState")
            }
            // block end states can only be associated to a single block start
			// state
            if ( state.endState.startState != nil) {
                panic ("IllegalState")
            }
            state.endState.startState = state
        }
        if _, ok := state.(PlusLoopbackState); ok {
            for (j=0 j<state.transitions.length j++) {
                target = state.transitions[j].target
                if _, ok := target.(PlusBlockStartState); ok {
                    target.loopBackState = state
                }
            }
        } else if _, ok := state.(StarLoopbackState); ok {
            for (j=0 j<state.transitions.length j++) {
                target = state.transitions[j].target
                if _, ok := target.(StarLoopEntryState); ok {
                    target.loopBackState = state
                }
            }
        }
    }
}

func (this *ATNDeserializer) readDecisions(atn) {
    var ndecisions = this.readInt()
    for (var i=0 i<ndecisions i++) {
        var s = this.readInt()
        var decState = atn.states[s]
        atn.decisionToState.push(decState)
        decState.decision = i
    }
}

func (this *ATNDeserializer) readLexerActions(atn) {
    if (atn.grammarType == ATNTypeLexer) {
        var count = this.readInt()
        atn.lexerActions = initArray(count, nil)
        for (var i=0 i<count i++) {
            var actionType = this.readInt()
            var data1 = this.readInt()
            if (data1 == 0xFFFF) {
                data1 = -1
            }
            var data2 = this.readInt()
            if (data2 == 0xFFFF) {
                data2 = -1
            }
            var lexerAction = this.lexerActionFactory(actionType, data1, data2)
            atn.lexerActions[i] = lexerAction
        }
    }
}

func (this *ATNDeserializer) generateRuleBypassTransitions(atn) {
	var i
    var count = atn.ruleToStartState.length
    for(i=0 i<count i++) {
        atn.ruleToTokenType[i] = atn.maxTokenType + i + 1
    }
    for(i=0 i<count i++) {
        this.generateRuleBypassTransition(atn, i)
    }
}

func (this *ATNDeserializer) generateRuleBypassTransition(atn, idx) {
	var i, state
    var bypassStart = NewBasicBlockStartState()
    bypassStart.ruleIndex = idx
    atn.addState(bypassStart)

    var bypassStop = NewBlockEndState()
    bypassStop.ruleIndex = idx
    atn.addState(bypassStop)

    bypassStart.endState = bypassStop
    atn.defineDecisionState(bypassStart)

    bypassStop.startState = bypassStart

    var excludeTransition = nil
    var endState = nil
    
    if (atn.ruleToStartState[idx].isPrecedenceRule) {
        // wrap from the beginning of the rule to the StarLoopEntryState
        endState = nil
        for(i=0 i<atn.states.length i++) {
            state = atn.states[i]
            if (this.stateIsEndStateFor(state, idx)) {
                endState = state
                excludeTransition = state.loopBackState.transitions[0]
                break
            }
        }
        if (excludeTransition == nil) {
            panic ("Couldn't identify final state of the precedence rule prefix section.")
        }
    } else {
        endState = atn.ruleToStopState[idx]
    }
    
    // all non-excluded transitions that currently target end state need to
	// target blockEnd instead
    for(i=0 i<atn.states.length i++) {
        state = atn.states[i]
        for(var j=0 j<state.transitions.length j++) {
            var transition = state.transitions[j]
            if (transition == excludeTransition) {
                continue
            }
            if (transition.target == endState) {
                transition.target = bypassStop
            }
        }
    }

    // all transitions leaving the rule start state need to leave blockStart
	// instead
    var ruleToStartState = atn.ruleToStartState[idx]
    var count = ruleToStartState.transitions.length
    for ( count > 0) {
        bypassStart.addTransition(ruleToStartState.transitions[count-1])
        ruleToStartState.transitions = ruleToStartState.transitions.slice(-1)
    }
    // link the Newstates
    atn.ruleToStartState[idx].addTransition(NewEpsilonTransition(bypassStart))
    bypassStop.addTransition(NewEpsilonTransition(endState))

    var matchState = NewBasicState()
    atn.addState(matchState)
    matchState.addTransition(NewAtomTransition(bypassStop, atn.ruleToTokenType[idx]))
    bypassStart.addTransition(NewEpsilonTransition(matchState))
}

func (this *ATNDeserializer) stateIsEndStateFor(state, idx) {
    if ( state.ruleIndex != idx) {
        return nil
    }
    if (!( state instanceof StarLoopEntryState)) {
        return nil
    }
    var maybeLoopEndState = state.transitions[state.transitions.length - 1].target
    if (!( maybeLoopEndState instanceof LoopEndState)) {
        return nil
    }
    if (maybeLoopEndState.epsilonOnlyTransitions &&
        (maybeLoopEndState.transitions[0].target instanceof RuleStopState)) {
        return state
    } else {
        return nil
    }
}

//
// Analyze the {@link StarLoopEntryState} states in the specified ATN to set
// the {@link StarLoopEntryState//precedenceRuleDecision} field to the
// correct value.
//
// @param atn The ATN.
//
func (this *ATNDeserializer) markPrecedenceDecisions(atn) {
	for(var i=0 i<atn.states.length i++) {
		var state = atn.states[i]
		if (!( state instanceof StarLoopEntryState)) {
            continue
        }
        // We analyze the ATN to determine if this ATN decision state is the
        // decision for the closure block that determines whether a
        // precedence rule should continue or complete.
        //
        if ( atn.ruleToStartState[state.ruleIndex].isPrecedenceRule) {
            var maybeLoopEndState = state.transitions[state.transitions.length - 1].target
            if _, ok := maybeLoopEndState.(LoopEndState); ok {
                if ( maybeLoopEndState.epsilonOnlyTransitions &&
                        (maybeLoopEndState.transitions[0].target instanceof RuleStopState)) {
                    state.precedenceRuleDecision = true
                }
            }
        }
	}
}

func (this *ATNDeserializer) verifyATN(atn) {
    if (!this.deserializationOptions.verifyATN) {
        return
    }
    // verify assumptions
	for(var i=0 i<atn.states.length i++) {
        var state = atn.states[i]
        if (state == nil) {
            continue
        }
        this.checkCondition(state.epsilonOnlyTransitions || state.transitions.length <= 1)
        if _, ok := state.(PlusBlockStartState); ok {
            this.checkCondition(state.loopBackState != nil)
        } else  if _, ok := state.(StarLoopEntryState); ok {
            this.checkCondition(state.loopBackState != nil)
            this.checkCondition(state.transitions.length == 2)
            if (state.transitions[0].target instanceof StarBlockStartState) {
                this.checkCondition(state.transitions[1].target instanceof LoopEndState)
                this.checkCondition(!state.nonGreedy)
            } else if (state.transitions[0].target instanceof LoopEndState) {
                this.checkCondition(state.transitions[1].target instanceof StarBlockStartState)
                this.checkCondition(state.nonGreedy)
            } else {
                panic("IllegalState")
            }
        } else if _, ok := state.(StarLoopbackState); ok {
            this.checkCondition(state.transitions.length == 1)
            this.checkCondition(state.transitions[0].target instanceof StarLoopEntryState)
        } else if _, ok := state.(LoopEndState); ok {
            this.checkCondition(state.loopBackState != nil)
        } else if _, ok := state.(RuleStartState); ok {
            this.checkCondition(state.stopState != nil)
        } else if _, ok := state.(BlockStartState); ok {
            this.checkCondition(state.endState != nil)
        } else if _, ok := state.(BlockEndState); ok {
            this.checkCondition(state.startState != nil)
        } else if _, ok := state.(DecisionState); ok {
            this.checkCondition(state.transitions.length <= 1 || state.decision >= 0)
        } else {
            this.checkCondition(state.transitions.length <= 1 || _, ok := state.(RuleStopState); ok)
        }
	}
}

func (this *ATNDeserializer) checkCondition(condition, message) {
    if (!condition) {
        if (message == undefined || message==nil) {
            message = "IllegalState"
        }
        panic (message)
    }
}

func (this *ATNDeserializer) readInt() {
    return this.data[this.pos++]
}

ATNDeserializer.prototype.readInt32 = function() {
    var low = this.readInt()
    var high = this.readInt()
    return low | (high << 16)
}

func (this *ATNDeserializer) readLong() {
    var low = this.readInt32()
    var high = this.readInt32()
    return (low & 0x00000000FFFFFFFF) | (high << 32)
}

type createByteToHex struct {
	var bth = []
	for i := 0 i < 256 i++) {
		bth[i] = (i + 0x100).toString(16).substr(1).toUpperCase()
	}
	return bth
}

var byteToHex = createByteToHex()
	
func (this *ATNDeserializer) readUUID() {
	var bb = []
	for(var i=7i>=0i--) {
		var int = this.readInt()
		/* jshint bitwise: false */
		bb[(2*i)+1] = int & 0xFF
		bb[2*i] = (int >> 8) & 0xFF
	}
    return byteToHex[bb[0]] + byteToHex[bb[1]] +
    byteToHex[bb[2]] + byteToHex[bb[3]] + '-' +
    byteToHex[bb[4]] + byteToHex[bb[5]] + '-' +
    byteToHex[bb[6]] + byteToHex[bb[7]] + '-' +
    byteToHex[bb[8]] + byteToHex[bb[9]] + '-' +
    byteToHex[bb[10]] + byteToHex[bb[11]] +
    byteToHex[bb[12]] + byteToHex[bb[13]] +
    byteToHex[bb[14]] + byteToHex[bb[15]]
}

ATNDeserializer.prototype.edgeFactory = function(atn, type, src, trg, arg1, arg2, arg3, sets) {
    var target = atn.states[trg]
    switch(type) {
    case Transition.EPSILON:
        return NewEpsilonTransition(target)
    case Transition.RANGE:
        return arg3 != 0 ? NewRangeTransition(target, TokenEOF, arg2) : NewRangeTransition(target, arg1, arg2)
    case Transition.RULE:
        return NewRuleTransition(atn.states[arg1], arg2, arg3, target)
    case Transition.PREDICATE:
        return NewPredicateTransition(target, arg1, arg2, arg3 != 0)
    case Transition.PRECEDENCE:
        return NewPrecedencePredicateTransition(target, arg1)
    case Transition.ATOM:
        return arg3 != 0 ? NewAtomTransition(target, TokenEOF) : NewAtomTransition(target, arg1)
    case Transition.ACTION:
        return NewActionTransition(target, arg1, arg2, arg3 != 0)
    case Transition.SET:
        return NewSetTransition(target, sets[arg1])
    case Transition.NOT_SET:
        return NewNotSetTransition(target, sets[arg1])
    case Transition.WILDCARD:
        return NewWildcardTransition(target)
    default:
        panic "The specified transition type: " + type + " is not valid."
    }
}

func (this *ATNDeserializer) stateFactory(type, ruleIndex) {
    if (this.stateFactories == nil) {
        var sf = []
        sf[ATNStateInvalidType] = nil
        sf[ATNStateBASIC] = function() { return NewBasicState() }
        sf[ATNStateRULE_START] = function() { return NewRuleStartState() }
        sf[ATNStateBLOCK_START] = function() { return NewBasicBlockStartState() }
        sf[ATNStatePLUS_BLOCK_START] = function() { return NewPlusBlockStartState() }
        sf[ATNStateSTAR_BLOCK_START] = function() { return NewStarBlockStartState() }
        sf[ATNStateTOKEN_START] = function() { return NewTokensStartState() }
        sf[ATNStateRULE_STOP] = function() { return NewRuleStopState() }
        sf[ATNStateBLOCK_END] = function() { return NewBlockEndState() }
        sf[ATNStateSTAR_LOOP_BACK] = function() { return NewStarLoopbackState() }
        sf[ATNStateSTAR_LOOP_ENTRY] = function() { return NewStarLoopEntryState() }
        sf[ATNStatePLUS_LOOP_BACK] = function() { return NewPlusLoopbackState() }
        sf[ATNStateLOOP_END] = function() { return NewLoopEndState() }
        this.stateFactories = sf
    }
    if (type>this.stateFactories.length || this.stateFactories[type] == nil) {
        panic("The specified state type " + type + " is not valid.")
    } else {
        var s = this.stateFactories[type]()
        if (s!=nil) {
            s.ruleIndex = ruleIndex
            return s
        }
    }
}

ATNDeserializer.prototype.lexerActionFactory = function(type, data1, data2) {
    if (this.actionFactories == nil) {
        var af = []
        af[LexerActionTypeCHANNEL] = function(data1, data2) { return NewLexerChannelAction(data1) }
        af[LexerActionTypeCUSTOM] = function(data1, data2) { return NewLexerCustomAction(data1, data2) }
        af[LexerActionTypeMODE] = function(data1, data2) { return NewLexerModeAction(data1) }
        af[LexerActionTypeMORE] = function(data1, data2) { return LexerMoreAction.INSTANCE }
        af[LexerActionTypePOP_MODE] = function(data1, data2) { return LexerPopModeAction.INSTANCE }
        af[LexerActionTypePUSH_MODE] = function(data1, data2) { return NewLexerPushModeAction(data1) }
        af[LexerActionTypeSKIP] = function(data1, data2) { return LexerSkipAction.INSTANCE }
        af[LexerActionTypeTYPE] = function(data1, data2) { return NewLexerTypeAction(data1) }
        this.actionFactories = af
    }
    if (type>this.actionFactories.length || this.actionFactories[type] == nil) {
        panic("The specified lexer action type " + type + " is not valid.")
    } else {
        return this.actionFactories[type](data1, data2)
    }
}
   

