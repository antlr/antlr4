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
	GetEpsilonOnlyTransitions() bool

	GetRuleIndex() int
	SetRuleIndex(int)

	GetNextTokenWithinRule() *IntervalSet
	SetNextTokenWithinRule(*IntervalSet)

	GetATN() *ATN
	SetATN(*ATN)

	GetStateType() int

	GetStateNumber() int
	SetStateNumber(int)

	GetTransitions() []ITransition
	SetTransitions([]ITransition)
	AddTransition(ITransition, int)

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

	return as
}

func (as *ATNState) GetRuleIndex() int {
	return as.ruleIndex
}

func (as *ATNState) SetRuleIndex(v int) {
	as.ruleIndex = v
}
func (as *ATNState) GetEpsilonOnlyTransitions() bool {
	return as.epsilonOnlyTransitions
}

func (as *ATNState) GetATN() *ATN {
	return as.atn
}

func (as *ATNState) SetATN(atn *ATN) {
	as.atn = atn
}

func (as *ATNState) GetTransitions() []ITransition {
	return as.transitions
}

func (as *ATNState) SetTransitions(t []ITransition) {
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

func (as *ATNState) GetNextTokenWithinRule() *IntervalSet {
	return as.nextTokenWithinRule
}

func (as *ATNState) SetNextTokenWithinRule(v *IntervalSet) {
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

func (this *ATNState) AddTransition(trans ITransition, index int) {
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
	this.ATNState = NewATNState()

	this.stateType = ATNStateBASIC
	return this
}

type IDecisionState interface {

	IATNState

	getDecision() int
	setDecision(int)

	getNonGreedy() bool
	setNonGreedy(bool)

}

type DecisionState struct {
	*ATNState

	decision  int
	nonGreedy bool
}

func NewDecisionState() *DecisionState {

	this := new(DecisionState)

	this.ATNState = NewATNState()

	this.decision = -1
	this.nonGreedy = false

	return this
}

func (s *DecisionState) getDecision() int {
	return s.decision
}

func (s *DecisionState) setDecision(b int) {
	s.decision = b
}

func (s *DecisionState) getNonGreedy() bool {
	return s.nonGreedy
}

func (s *DecisionState) setNonGreedy(b bool) {
	s.nonGreedy = b
}

type IBlockStartState interface {

	IDecisionState

	getEndState() *BlockEndState
	setEndState(*BlockEndState)

}

//  The start of a regular {@code (...)} block.
type BlockStartState struct {
	*DecisionState

	endState *BlockEndState
}

func NewBlockStartState() *BlockStartState {

	this := new(BlockStartState)

	this.DecisionState = NewDecisionState()
	this.endState = nil

	return this
}

func (s *BlockStartState) getEndState() *BlockEndState {
	return s.endState
}

func (s *BlockStartState) setEndState(b *BlockEndState) {
	s.endState = b
}

type BasicBlockStartState struct {
	*BlockStartState
}

func NewBasicBlockStartState() *BasicBlockStartState {

	this := new(BasicBlockStartState)

	this.BlockStartState = NewBlockStartState()

	this.stateType = ATNStateBLOCK_START
	return this
}


// Terminal node of a simple {@code (a|b|c)} block.
type BlockEndState struct {
	*ATNState

	startState IATNState
}

func NewBlockEndState() *BlockEndState {

	this := new(BlockEndState)

	this.ATNState = NewATNState()
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
	*ATNState
}

func NewRuleStopState() *RuleStopState {
	this := new(RuleStopState)

	this.ATNState = NewATNState()
	this.stateType = ATNStateRULE_STOP
	return this
}

type RuleStartState struct {
	*ATNState

	stopState        IATNState
	isPrecedenceRule bool
}

func NewRuleStartState() *RuleStartState {

	this := new(RuleStartState)

	this.ATNState = NewATNState()
	this.stateType = ATNStateRULE_START
	this.stopState = nil
	this.isPrecedenceRule = false

	return this
}

// Decision state for {@code A+} and {@code (A|B)+}.  It has two transitions:
//  one to the loop back to start of the block and one to exit.
//
type PlusLoopbackState struct {
	*DecisionState
}

func NewPlusLoopbackState() *PlusLoopbackState {

	this := new(PlusLoopbackState)

	this.DecisionState = NewDecisionState()

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

	this.BlockStartState = NewBlockStartState()

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

	this.BlockStartState = NewBlockStartState()

	this.stateType = ATNStateSTAR_BLOCK_START

	return this
}

type StarLoopbackState struct {
	*ATNState
}

func NewStarLoopbackState() *StarLoopbackState {

	this := new(StarLoopbackState)

	this.ATNState = NewATNState()

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

	this.DecisionState = NewDecisionState()

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

	this.ATNState = NewATNState()

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

	this.DecisionState = NewDecisionState()

	this.stateType = ATNStateTOKEN_START
	return this
}
