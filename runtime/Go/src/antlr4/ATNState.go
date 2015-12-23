package antlr4

import "strconv"

const (
	// constants for serialization
	ATNStateInvalidType      = 0
	ATNStateBASIC            = 1
	ATNStateRULE_START       = 2
	ATNStateBLOCK_START      = 3
	ATNStatePLUS_BLOCK_START = 4
	ATNStateSTAR_BLOCK_START = 5
	ATNStateTOKEN_START      = 6
	ATNStateRULE_STOP        = 7
	ATNStateBLOCK_END        = 8
	ATNStateSTAR_LOOP_BACK   = 9
	ATNStateSTAR_LOOP_ENTRY  = 10
	ATNStatePLUS_LOOP_BACK   = 11
	ATNStateLOOP_END         = 12

	ATNStateINVALID_STATE_NUMBER = -1
)

//var ATNState.serializationNames = [
//            "INVALID",
//            "BASIC",
//            "RULE_START",
//            "BLOCK_START",
//            "PLUS_BLOCK_START",
//            "STAR_BLOCK_START",
//            "TOKEN_START",
//            "RULE_STOP",
//            "BLOCK_END",
//            "STAR_LOOP_BACK",
//            "STAR_LOOP_ENTRY",
//            "PLUS_LOOP_BACK",
//            "LOOP_END" ]

var INITIAL_NUM_TRANSITIONS = 4

type IATNState interface {
	getEpsilonOnlyTransitions() bool

	getRuleIndex() int
	setRuleIndex(int)

	getNextTokenWithinRule() *IntervalSet
	setNextTokenWithinRule(*IntervalSet)

	getATN() *ATN
	setATN(*ATN)

	GetStateType() int

	GetStateNumber() int
	SetStateNumber(int)

	getTransitions() []ITransition
	setTransitions([]ITransition)
	addTransition(ITransition, int)

	toString() string
}

type ATNState struct {
	// Which ATN are we in?
	atn                    *ATN
	stateNumber            int
	stateType              int
	ruleIndex              int
	epsilonOnlyTransitions bool
	// Track the transitions emanating from this ATN state.
	transitions []ITransition
	// Used to cache lookahead during parsing, not used during construction
	nextTokenWithinRule *IntervalSet
}

func NewATNState() *ATNState {

	as := new(ATNState)
	as.InitATNState()

	return as
}

func (as *ATNState) InitATNState() {

	// Which ATN are we in?
	as.atn = nil
	as.stateNumber = ATNStateINVALID_STATE_NUMBER
	as.stateType = ATNStateInvalidType
	as.ruleIndex = 0 // at runtime, we don't have Rule objects
	as.epsilonOnlyTransitions = false
	// Track the transitions emanating from this ATN state.
	as.transitions = make([]ITransition, 0)
	// Used to cache lookahead during parsing, not used during construction
	as.nextTokenWithinRule = nil

}

func (as *ATNState) getRuleIndex() int {
	return as.ruleIndex
}

func (as *ATNState) setRuleIndex(v int) {
	as.ruleIndex = v
}
func (as *ATNState) getEpsilonOnlyTransitions() bool {
	return as.epsilonOnlyTransitions
}

func (as *ATNState) getATN() *ATN {
	return as.atn
}

func (as *ATNState) setATN(atn *ATN) {
	as.atn = atn
}

func (as *ATNState) getTransitions() []ITransition {
	return as.transitions
}

func (as *ATNState) setTransitions(t []ITransition) {
	as.transitions = t
}

func (as *ATNState) GetStateType() int {
	return as.stateType
}

func (as *ATNState) GetStateNumber() int {
	return as.stateNumber
}

func (as *ATNState) SetStateNumber(stateNumber int) {
	as.stateNumber = stateNumber
}

func (as *ATNState) getNextTokenWithinRule() *IntervalSet {
	return as.nextTokenWithinRule
}

func (as *ATNState) setNextTokenWithinRule(v *IntervalSet) {
	as.nextTokenWithinRule = v
}

func (this *ATNState) toString() string {
	return strconv.Itoa(this.stateNumber)
}

func (this *ATNState) equals(other interface{}) bool {
	if ot, ok := other.(IATNState); ok {
		return this.stateNumber == ot.GetStateNumber()
	} else {
		return false
	}
}

func (this *ATNState) isNonGreedyExitState() bool {
	return false
}

func (this *ATNState) addTransition(trans ITransition, index int) {
	if len(this.transitions) == 0 {
		this.epsilonOnlyTransitions = trans.getIsEpsilon()
	} else if this.epsilonOnlyTransitions != trans.getIsEpsilon() {
		this.epsilonOnlyTransitions = false
	}
	if index == -1 {
		this.transitions = append(this.transitions, trans)
	} else {
		this.transitions = append(this.transitions[:index], append([]ITransition{trans}, this.transitions[index:]...)...)
		//        this.transitions.splice(index, 1, trans)
	}
}

type BasicState struct {
	*ATNState
}

func NewBasicState() *BasicState {
	this := new(BasicState)
	this.InitATNState()

	this.stateType = ATNStateBASIC
	return this
}

type DecisionState struct {
	*ATNState

	decision  int
	nonGreedy bool
}

func NewDecisionState() *DecisionState {

	this := new(DecisionState)

	this.InitATNState()
	this.InitDecisionState()

	return this
}

func (this *DecisionState) InitDecisionState() {

	this.decision = -1
	this.nonGreedy = false

}

//  The start of a regular {@code (...)} block.
type BlockStartState struct {
	*DecisionState

	endState *BlockEndState
}

func NewBlockStartState() *BlockStartState {

	this := new(BlockStartState)

	this.InitATNState()
	this.InitDecisionState()

	return this
}

func (this *BlockStartState) InitBlockStartState() {

	this.endState = nil

}

type BasicBlockStartState struct {
	*BlockStartState
}

func NewBasicBlockStartState() *BasicBlockStartState {

	this := new(BasicBlockStartState)

	this.InitATNState()
	this.InitDecisionState()
	this.InitBlockStartState()

	this.stateType = ATNStateBLOCK_START
	return this
}

// Terminal node of a simple {@code (a|b|c)} block.
type BlockEndState struct {
	ATNState

	startState IATNState
}

func NewBlockEndState() *BlockEndState {

	this := new(BlockEndState)

	this.InitATNState()
	this.stateType = ATNStateBLOCK_END
	this.startState = nil

	return this
}

// The last node in the ATN for a rule, unless that rule is the start symbol.
//  In that case, there is one transition to EOF. Later, we might encode
//  references to all calls to this rule to compute FOLLOW sets for
//  error handling.
//
type RuleStopState struct {
	ATNState
}

func NewRuleStopState() *RuleStopState {
	this := new(RuleStopState)

	this.InitATNState()
	this.stateType = ATNStateRULE_STOP
	return this
}

type RuleStartState struct {
	ATNState

	stopState        IATNState
	isPrecedenceRule bool
}

func NewRuleStartState() *RuleStartState {

	this := new(RuleStartState)

	this.InitATNState()
	this.stateType = ATNStateRULE_START
	this.stopState = nil
	this.isPrecedenceRule = false

	return this
}

// Decision state for {@code A+} and {@code (A|B)+}.  It has two transitions:
//  one to the loop back to start of the block and one to exit.
//
type PlusLoopbackState struct {
	*BlockStartState
}

func NewPlusLoopbackState() *PlusLoopbackState {

	this := new(PlusLoopbackState)

	this.InitATNState()
	this.InitDecisionState()
	this.InitBlockStartState()

	this.stateType = ATNStatePLUS_LOOP_BACK
	return this
}

// Start of {@code (A|B|...)+} loop. Technically a decision state, but
//  we don't use for code generation somebody might need it, so I'm defining
//  it for completeness. In reality, the {@link PlusLoopbackState} node is the
//  real decision-making note for {@code A+}.
//
type PlusBlockStartState struct {
	*BlockStartState

	loopBackState IATNState
}

func NewPlusBlockStartState() *PlusBlockStartState {

	this := new(PlusBlockStartState)

	this.InitATNState()
	this.InitDecisionState()
	this.InitBlockStartState()

	this.stateType = ATNStatePLUS_BLOCK_START
	this.loopBackState = nil

	return this
}

// The block that begins a closure loop.
type StarBlockStartState struct {
	*BlockStartState
}

func NewStarBlockStartState() *StarBlockStartState {

	this := new(StarBlockStartState)

	this.InitATNState()
	this.InitDecisionState()
	this.InitBlockStartState()

	this.stateType = ATNStateSTAR_BLOCK_START

	return this
}

type StarLoopbackState struct {
	*ATNState
}

func NewStarLoopbackState() *StarLoopbackState {

	this := new(StarLoopbackState)

	this.InitATNState()

	this.stateType = ATNStateSTAR_LOOP_BACK
	return this
}

type StarLoopEntryState struct {
	*DecisionState

	loopBackState          IATNState
	precedenceRuleDecision bool
}

func NewStarLoopEntryState() *StarLoopEntryState {

	this := new(StarLoopEntryState)

	this.InitATNState()
	this.InitDecisionState()

	this.stateType = ATNStateSTAR_LOOP_ENTRY
	this.loopBackState = nil

	// Indicates whether this state can benefit from a precedence DFA during SLL decision making.
	this.precedenceRuleDecision = false

	return this
}

// Mark the end of a * or + loop.
type LoopEndState struct {
	*ATNState
	loopBackState IATNState
}

func NewLoopEndState() *LoopEndState {

	this := new(LoopEndState)

	this.InitATNState()

	this.stateType = ATNStateLOOP_END
	this.loopBackState = nil

	return this
}

// The Tokens rule start state linking to each lexer rule start state */
type TokensStartState struct {
	*DecisionState
}

func NewTokensStartState() *TokensStartState {

	this := new(TokensStartState)

	this.InitATNState()
	this.InitDecisionState()

	this.stateType = ATNStateTOKEN_START
	return this
}
