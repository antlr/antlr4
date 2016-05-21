package antlr4

import (
	"encoding/hex"
	"fmt"
	"strconv"
	"strings"
	"unicode/utf16"
)

// This is the earliest supported serialized UUID.
// stick to serialized version for now, we don't need a UUID instance
var BaseSerializedUUID = "AADB8D7E-AEEF-4415-AD2B-8204D6CF042E"

// This list contains all of the currently supported UUIDs, ordered by when
// the feature first appeared in this branch.
var SupportedUUIDs = []string{BaseSerializedUUID}

var SerializedVersion = 3

// This is the current serialized UUID.
var SerializedUUID = BaseSerializedUUID

type LoopEndStateIntPair struct {
	item0 *LoopEndState
	item1 int
}

type BlockStartStateIntPair struct {
	item0 BlockStartState
	item1 int
}

type ATNDeserializer struct {
	deserializationOptions *ATNDeserializationOptions
	data                   []rune
	pos                    int
	uuid                   string
}

func NewATNDeserializer(options *ATNDeserializationOptions) *ATNDeserializer {

	if options == nil {
		options = ATNDeserializationOptionsdefaultOptions
	}

	this := new(ATNDeserializer)

	this.deserializationOptions = options

	return this
}

func stringInSlice(a string, list []string) int {
	for i, b := range list {
		if b == a {
			return i
		}
	}
	return -1
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

func (this *ATNDeserializer) isFeatureSupported(feature, actualUuid string) bool {
	var idx1 = stringInSlice(feature, SupportedUUIDs)
	if idx1 < 0 {
		return false
	}
	var idx2 = stringInSlice(actualUuid, SupportedUUIDs)
	return idx2 >= idx1
}

func (this *ATNDeserializer) DeserializeFromUInt16(data []uint16) *ATN {

	this.reset(utf16.Decode(data))
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
	if this.deserializationOptions.generateRuleBypassTransitions && atn.grammarType == ATNTypeParser {
		this.generateRuleBypassTransitions(atn)
		// re-verify after modification
		this.verifyATN(atn)
	}
	return atn

}

func (this *ATNDeserializer) reset(data []rune) {

	temp := make([]rune, len(data))

	for i, c := range data {
		// don't adjust the first value since that's the version number
		if i == 0 {
			temp[i] = c
		} else {
			temp[i] = c - 2
		}
	}

	this.data = temp
	this.pos = 0
}

func (this *ATNDeserializer) checkVersion() {
	var version = this.readInt()
	if version != SerializedVersion {
		panic("Could not deserialize ATN with version " + strconv.Itoa(version) + " (expected " + strconv.Itoa(SerializedVersion) + ").")
	}
}

func (this *ATNDeserializer) checkUUID() {
	var uuid = this.readUUID()
	if stringInSlice(uuid, SupportedUUIDs) < 0 {
		panic("Could not deserialize ATN with UUID: " + uuid + " (expected " + SerializedUUID + " or a legacy UUID).")
	}
	this.uuid = uuid
}

func (this *ATNDeserializer) readATN() *ATN {
	var grammarType = this.readInt()
	var maxTokenType = this.readInt()
	return NewATN(grammarType, maxTokenType)
}

func (this *ATNDeserializer) readStates(atn *ATN) {

	var loopBackStateNumbers = make([]LoopEndStateIntPair, 0)
	var endStateNumbers = make([]BlockStartStateIntPair, 0)

	var nstates = this.readInt()

	for i := 0; i < nstates; i++ {
		var stype = this.readInt()
		// ignore bad type of states
		if stype == ATNStateInvalidType {
			atn.addState(nil)
			continue
		}
		var ruleIndex = this.readInt()
		if ruleIndex == 0xFFFF {
			ruleIndex = -1
		}
		var s = this.stateFactory(stype, ruleIndex)
		if stype == ATNStateLoopEnd {
			var loopBackStateNumber = this.readInt()
			loopBackStateNumbers = append(loopBackStateNumbers, LoopEndStateIntPair{s.(*LoopEndState), loopBackStateNumber})
		} else if s2, ok := s.(BlockStartState); ok {
			var endStateNumber = this.readInt()
			endStateNumbers = append(endStateNumbers, BlockStartStateIntPair{s2, endStateNumber})
		}
		atn.addState(s)
	}
	// delay the assignment of loop back and end states until we know all the
	// state instances have been initialized
	for j := 0; j < len(loopBackStateNumbers); j++ {
		pair := loopBackStateNumbers[j]
		pair.item0.loopBackState = atn.states[pair.item1]
	}

	for j := 0; j < len(endStateNumbers); j++ {
		pair := endStateNumbers[j]
		pair.item0.setEndState(atn.states[pair.item1].(*BlockEndState))
	}

	var numNonGreedyStates = this.readInt()
	for j := 0; j < numNonGreedyStates; j++ {
		stateNumber := this.readInt()
		atn.states[stateNumber].(DecisionState).setNonGreedy(true)
	}

	var numPrecedenceStates = this.readInt()
	for j := 0; j < numPrecedenceStates; j++ {
		stateNumber := this.readInt()
		atn.states[stateNumber].(*RuleStartState).isPrecedenceRule = true
	}
}

func (this *ATNDeserializer) readRules(atn *ATN) {

	var nrules = this.readInt()
	if atn.grammarType == ATNTypeLexer {
		atn.ruleToTokenType = make([]int, nrules) // initIntArray(nrules, 0)
	}
	atn.ruleToStartState = make([]*RuleStartState, nrules) // initIntArray(nrules, 0)
	for i := 0; i < nrules; i++ {
		var s = this.readInt()
		var startState = atn.states[s].(*RuleStartState)
		atn.ruleToStartState[i] = startState
		if atn.grammarType == ATNTypeLexer {
			var tokenType = this.readInt()
			if tokenType == 0xFFFF {
				tokenType = TokenEOF
			}
			atn.ruleToTokenType[i] = tokenType
		}
	}
	atn.ruleToStopState = make([]*RuleStopState, nrules) //initIntArray(nrules, 0)
	for i := 0; i < len(atn.states); i++ {
		var state = atn.states[i]
		if s2, ok := state.(*RuleStopState); ok {
			atn.ruleToStopState[s2.ruleIndex] = s2
			atn.ruleToStartState[s2.ruleIndex].stopState = s2
		}
	}
}

func (this *ATNDeserializer) readModes(atn *ATN) {
	var nmodes = this.readInt()
	for i := 0; i < nmodes; i++ {
		var s = this.readInt()
		atn.modeToStartState = append(atn.modeToStartState, atn.states[s].(*TokensStartState))
	}
}

func (this *ATNDeserializer) readSets(atn *ATN) []*IntervalSet {
	var sets = make([]*IntervalSet, 0)
	var m = this.readInt()
	for i := 0; i < m; i++ {
		var iset = NewIntervalSet()
		sets = append(sets, iset)
		var n = this.readInt()
		var containsEof = this.readInt()
		if containsEof != 0 {
			iset.addOne(-1)
		}
		for j := 0; j < n; j++ {
			var i1 = this.readInt()
			var i2 = this.readInt()
			iset.addRange(i1, i2)
		}
	}
	return sets
}

func (this *ATNDeserializer) readEdges(atn *ATN, sets []*IntervalSet) {

	var nedges = this.readInt()
	for i := 0; i < nedges; i++ {
		var src = this.readInt()
		var trg = this.readInt()
		var ttype = this.readInt()
		var arg1 = this.readInt()
		var arg2 = this.readInt()
		var arg3 = this.readInt()
		trans := this.edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets)
		var srcState = atn.states[src]
		srcState.AddTransition(trans, -1)
	}
	// edges for rule stop states can be derived, so they aren't serialized
	for i := 0; i < len(atn.states); i++ {
		state := atn.states[i]
		for j := 0; j < len(state.GetTransitions()); j++ {
			var t, ok = state.GetTransitions()[j].(*RuleTransition)
			if !ok {
				continue
			}
			var outermostPrecedenceReturn = -1
			if atn.ruleToStartState[t.getTarget().GetRuleIndex()].isPrecedenceRule {
				if t.precedence == 0 {
					outermostPrecedenceReturn = t.getTarget().GetRuleIndex()
				}
			}

			trans := NewEpsilonTransition(t.followState, outermostPrecedenceReturn)
			atn.ruleToStopState[t.getTarget().GetRuleIndex()].AddTransition(trans, -1)
		}
	}

	for i := 0; i < len(atn.states); i++ {
		state := atn.states[i]
		if s2, ok := state.(*BaseBlockStartState); ok {
			// we need to know the end state to set its start state
			if s2.endState == nil {
				panic("IllegalState")
			}
			// block end states can only be associated to a single block start
			// state
			if s2.endState.startState != nil {
				panic("IllegalState")
			}
			s2.endState.startState = state
		}
		if s2, ok := state.(*PlusLoopbackState); ok {
			for j := 0; j < len(s2.GetTransitions()); j++ {
				target := s2.GetTransitions()[j].getTarget()
				if t2, ok := target.(*PlusBlockStartState); ok {
					t2.loopBackState = state
				}
			}
		} else if s2, ok := state.(*StarLoopbackState); ok {
			for j := 0; j < len(s2.GetTransitions()); j++ {
				target := s2.GetTransitions()[j].getTarget()
				if t2, ok := target.(*StarLoopEntryState); ok {
					t2.loopBackState = state
				}
			}
		}
	}
}

func (this *ATNDeserializer) readDecisions(atn *ATN) {
	var ndecisions = this.readInt()
	for i := 0; i < ndecisions; i++ {
		var s = this.readInt()
		var decState = atn.states[s].(DecisionState)
		atn.DecisionToState = append(atn.DecisionToState, decState)
		decState.setDecision(i)
	}
}

func (this *ATNDeserializer) readLexerActions(atn *ATN) {
	if atn.grammarType == ATNTypeLexer {
		var count = this.readInt()
		atn.lexerActions = make([]LexerAction, count) // initIntArray(count, nil)
		for i := 0; i < count; i++ {
			var actionType = this.readInt()
			var data1 = this.readInt()
			if data1 == 0xFFFF {
				data1 = -1
			}
			var data2 = this.readInt()
			if data2 == 0xFFFF {
				data2 = -1
			}
			var lexerAction = this.lexerActionFactory(actionType, data1, data2)
			atn.lexerActions[i] = lexerAction
		}
	}
}

func (this *ATNDeserializer) generateRuleBypassTransitions(atn *ATN) {
	var count = len(atn.ruleToStartState)
	for i := 0; i < count; i++ {
		atn.ruleToTokenType[i] = atn.maxTokenType + i + 1
	}
	for i := 0; i < count; i++ {
		this.generateRuleBypassTransition(atn, i)
	}
}

func (this *ATNDeserializer) generateRuleBypassTransition(atn *ATN, idx int) {

	var bypassStart = NewBasicBlockStartState()
	bypassStart.ruleIndex = idx
	atn.addState(bypassStart)

	var bypassStop = NewBlockEndState()
	bypassStop.ruleIndex = idx
	atn.addState(bypassStop)

	bypassStart.endState = bypassStop

	atn.defineDecisionState(bypassStart.BaseDecisionState)

	bypassStop.startState = bypassStart

	var excludeTransition Transition = nil
	var endState ATNState = nil

	if atn.ruleToStartState[idx].isPrecedenceRule {
		// wrap from the beginning of the rule to the StarLoopEntryState
		endState = nil
		for i := 0; i < len(atn.states); i++ {
			state := atn.states[i]
			if this.stateIsEndStateFor(state, idx) != nil {
				endState = state
				excludeTransition = state.(*StarLoopEntryState).loopBackState.GetTransitions()[0]
				break
			}
		}
		if excludeTransition == nil {
			panic("Couldn't identify final state of the precedence rule prefix section.")
		}
	} else {
		endState = atn.ruleToStopState[idx]
	}

	// all non-excluded transitions that currently target end state need to
	// target blockEnd instead
	for i := 0; i < len(atn.states); i++ {
		state := atn.states[i]
		for j := 0; j < len(state.GetTransitions()); j++ {
			var transition = state.GetTransitions()[j]
			if transition == excludeTransition {
				continue
			}
			if transition.getTarget() == endState {
				transition.setTarget(bypassStop)
			}
		}
	}

	// all transitions leaving the rule start state need to leave blockStart
	// instead
	var ruleToStartState = atn.ruleToStartState[idx]
	var count = len(ruleToStartState.GetTransitions())
	for count > 0 {
		bypassStart.AddTransition(ruleToStartState.GetTransitions()[count-1], -1)
		ruleToStartState.SetTransitions([]Transition{ruleToStartState.GetTransitions()[len(ruleToStartState.GetTransitions())-1]})
	}
	// link the new states
	atn.ruleToStartState[idx].AddTransition(NewEpsilonTransition(bypassStart, -1), -1)
	bypassStop.AddTransition(NewEpsilonTransition(endState, -1), -1)

	var MatchState = NewBasicState()
	atn.addState(MatchState)
	MatchState.AddTransition(NewAtomTransition(bypassStop, atn.ruleToTokenType[idx]), -1)
	bypassStart.AddTransition(NewEpsilonTransition(MatchState, -1), -1)
}

func (this *ATNDeserializer) stateIsEndStateFor(state ATNState, idx int) ATNState {
	if state.GetRuleIndex() != idx {
		return nil
	}
	if _, ok := state.(*StarLoopEntryState); !ok {
		return nil
	}
	var maybeLoopEndState = state.GetTransitions()[len(state.GetTransitions())-1].getTarget()
	if _, ok := maybeLoopEndState.(*LoopEndState); !ok {
		return nil
	}

	_, ok := maybeLoopEndState.GetTransitions()[0].getTarget().(*RuleStopState)

	if maybeLoopEndState.(*LoopEndState).epsilonOnlyTransitions && ok {
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
func (this *ATNDeserializer) markPrecedenceDecisions(atn *ATN) {
	for _, state := range atn.states {
		if _, ok := state.(*StarLoopEntryState); !ok {
			continue
		}
		// We analyze the ATN to determine if this ATN decision state is the
		// decision for the closure block that determines whether a
		// precedence rule should continue or complete.
		//
		if atn.ruleToStartState[state.GetRuleIndex()].isPrecedenceRule {

			var maybeLoopEndState = state.GetTransitions()[len(state.GetTransitions())-1].getTarget()

			if s3, ok := maybeLoopEndState.(*LoopEndState); ok {

				_, ok2 := maybeLoopEndState.GetTransitions()[0].getTarget().(*RuleStopState)

				if s3.epsilonOnlyTransitions && ok2 {
					state.(*StarLoopEntryState).precedenceRuleDecision = true
				}
			}
		}
	}
}

func (this *ATNDeserializer) verifyATN(atn *ATN) {
	if !this.deserializationOptions.verifyATN {
		return
	}
	// verify assumptions
	for i := 0; i < len(atn.states); i++ {

		var state = atn.states[i]
		if state == nil {
			continue
		}
		this.checkCondition(state.GetEpsilonOnlyTransitions() || len(state.GetTransitions()) <= 1, "")

		switch s2 := state.(type) {

		case *PlusBlockStartState:
			this.checkCondition(s2.loopBackState != nil, "")
		case *StarLoopEntryState:

			this.checkCondition(s2.loopBackState != nil, "")
			this.checkCondition(len(s2.GetTransitions()) == 2, "")

			switch s2 := state.(type) {
			case *StarBlockStartState:
				_, ok2 := s2.GetTransitions()[1].getTarget().(*LoopEndState)
				this.checkCondition(ok2, "")
				this.checkCondition(!s2.nonGreedy, "")
			case *LoopEndState:
				s3, ok2 := s2.GetTransitions()[1].getTarget().(*StarBlockStartState)
				this.checkCondition(ok2, "")
				this.checkCondition(s3.nonGreedy, "")
			default:
				panic("IllegalState")
			}

		case *StarLoopbackState:
			this.checkCondition(len(state.GetTransitions()) == 1, "")
			_, ok2 := state.GetTransitions()[0].getTarget().(*StarLoopEntryState)
			this.checkCondition(ok2, "")
		case *LoopEndState:
			this.checkCondition(s2.loopBackState != nil, "")
		case *RuleStartState:
			this.checkCondition(s2.stopState != nil, "")
		case *BaseBlockStartState:
			this.checkCondition(s2.endState != nil, "")
		case *BlockEndState:
			this.checkCondition(s2.startState != nil, "")
		case DecisionState:
			this.checkCondition(len(s2.GetTransitions()) <= 1 || s2.getDecision() >= 0, "")
		default:
			_, ok := s2.(*RuleStopState)
			this.checkCondition(len(s2.GetTransitions()) <= 1 || ok, "")
		}
	}
}

func (this *ATNDeserializer) checkCondition(condition bool, message string) {
	if !condition {
		if message == "" {
			message = "IllegalState"
		}
		panic(message)
	}
}

func (this *ATNDeserializer) readInt() int {
	v := this.data[this.pos]
	this.pos += 1
	return int(v)
}

//func (this *ATNDeserializer) readLong() int64 {
//    panic("Not implemented")
//    var low = this.readInt32()
//    var high = this.readInt32()
//    return (low & 0x00000000FFFFFFFF) | (high << int32)
//}

func createByteToHex() []string {
	var bth = make([]string, 256)
	for i := 0; i < 256; i++ {
		bth[i] = strings.ToUpper(hex.EncodeToString([]byte{byte(i)}))
	}
	return bth
}

var byteToHex = createByteToHex()

func (this *ATNDeserializer) readUUID() string {
	var bb = make([]int, 16)
	for i := 7; i >= 0; i-- {
		var integer = this.readInt()
		bb[(2*i)+1] = integer & 0xFF
		bb[2*i] = (integer >> 8) & 0xFF
	}
	return byteToHex[bb[0]] + byteToHex[bb[1]] +
		byteToHex[bb[2]] + byteToHex[bb[3]] + "-" +
		byteToHex[bb[4]] + byteToHex[bb[5]] + "-" +
		byteToHex[bb[6]] + byteToHex[bb[7]] + "-" +
		byteToHex[bb[8]] + byteToHex[bb[9]] + "-" +
		byteToHex[bb[10]] + byteToHex[bb[11]] +
		byteToHex[bb[12]] + byteToHex[bb[13]] +
		byteToHex[bb[14]] + byteToHex[bb[15]]
}

func (this *ATNDeserializer) edgeFactory(atn *ATN, typeIndex, src, trg, arg1, arg2, arg3 int, sets []*IntervalSet) Transition {

	var target = atn.states[trg]

	switch typeIndex {
	case TransitionEPSILON:
		return NewEpsilonTransition(target, -1)
	case TransitionRANGE:
		if arg3 != 0 {
			return NewRangeTransition(target, TokenEOF, arg2)
		} else {
			return NewRangeTransition(target, arg1, arg2)
		}
	case TransitionRULE:
		return NewRuleTransition(atn.states[arg1], arg2, arg3, target)
	case TransitionPREDICATE:
		return NewPredicateTransition(target, arg1, arg2, arg3 != 0)
	case TransitionPRECEDENCE:
		return NewPrecedencePredicateTransition(target, arg1)
	case TransitionATOM:
		if arg3 != 0 {
			return NewAtomTransition(target, TokenEOF)
		} else {
			return NewAtomTransition(target, arg1)
		}
	case TransitionACTION:
		return NewActionTransition(target, arg1, arg2, arg3 != 0)
	case TransitionSET:
		return NewSetTransition(target, sets[arg1])
	case TransitionNOT_SET:
		return NewNotSetTransition(target, sets[arg1])
	case TransitionWILDCARD:
		return NewWildcardTransition(target)
	}

	panic("The specified transition type is not valid.")
}

func (this *ATNDeserializer) stateFactory(typeIndex, ruleIndex int) ATNState {

	var s ATNState
	switch typeIndex {
	case ATNStateInvalidType:
		return nil
	case ATNStateBasic:
		s = NewBasicState()
	case ATNStateRuleStart:
		s = NewRuleStartState()
	case ATNStateBlockStart:
		s = NewBasicBlockStartState()
	case ATNStatePlusBlockStart:
		s = NewPlusBlockStartState()
	case ATNStateStarBlockStart:
		s = NewStarBlockStartState()
	case ATNStateTokenStart:
		s = NewTokensStartState()
	case ATNStateRuleStop:
		s = NewRuleStopState()
	case ATNStateBlockEnd:
		s = NewBlockEndState()
	case ATNStateStarLoopBack:
		s = NewStarLoopbackState()
	case ATNStateStarLoopEntry:
		s = NewStarLoopEntryState()
	case ATNStatePlusLoopBack:
		s = NewPlusLoopbackState()
	case ATNStateLoopEnd:
		s = NewLoopEndState()
	default:
		message := fmt.Sprintf("The specified state type %d is not valid.", typeIndex)
		panic(message)
	}

	s.SetRuleIndex(ruleIndex)
	return s
}

func (this *ATNDeserializer) lexerActionFactory(typeIndex, data1, data2 int) LexerAction {
	switch typeIndex {
	case LexerActionTypeChannel:
		return NewLexerChannelAction(data1)
	case LexerActionTypeCustom:
		return NewLexerCustomAction(data1, data2)
	case LexerActionTypeMode:
		return NewLexerModeAction(data1)
	case LexerActionTypeMore:
		return LexerMoreActionINSTANCE
	case LexerActionTypePopMode:
		return LexerPopModeActionINSTANCE
	case LexerActionTypePushMode:
		return NewLexerPushModeAction(data1)
	case LexerActionTypeSkip:
		return LexerSkipActionINSTANCE
	case LexerActionTypeType:
		return NewLexerTypeAction(data1)
	default:
		message := fmt.Sprintf("The specified lexer action typeIndex%d is not valid.", typeIndex)
		panic(message)
	}
	return nil
}
