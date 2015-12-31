package antlr4

import "strconv"

const (
	// constants for serialization
	ATNStateInvalidType    = 0
	ATNStateBasic          = 1
	ATNStateRuleStart      = 2
	ATNStateBlockStart     = 3
	ATNStatePlusBlockStart = 4
	ATNStateStarBlockStart = 5
	ATNStateTokenStart     = 6
	ATNStateRuleStop       = 7
	ATNStateBlockEnd       = 8
	ATNStateStarLoopBack   = 9
	ATNStateStarLoopEntry  = 10
	ATNStatePlusLoopBack   = 11
	ATNStateLoopEnd        = 12

	ATNStateInvalidStateNumber = -1
)

var ATNStateInitialNumTransitions = 4

type ATNState interface {
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

	GetTransitions() []Transition
	SetTransitions([]Transition)
	AddTransition(Transition, int)

	String() string
}

type BaseATNState struct {
	// Which ATN are we in?
	atn                    *ATN
	stateNumber            int
	stateType              int
	ruleIndex              int
	epsilonOnlyTransitions bool
	// Track the transitions emanating from this ATN state.
	transitions []Transition
	// Used to cache lookahead during parsing, not used during construction
	nextTokenWithinRule *IntervalSet
}

func NewBaseATNState() *BaseATNState {

	as := new(BaseATNState)

	// Which ATN are we in?
	as.atn = nil
	as.stateNumber = ATNStateInvalidStateNumber
	as.stateType = ATNStateInvalidType
	as.ruleIndex = 0 // at runtime, we don't have Rule objects
	as.epsilonOnlyTransitions = false
	// Track the transitions emanating from this ATN state.
	as.transitions = make([]Transition, 0)
	// Used to cache lookahead during parsing, not used during construction
	as.nextTokenWithinRule = nil

	return as
}

func (as *BaseATNState) GetRuleIndex() int {
	return as.ruleIndex
}

func (as *BaseATNState) SetRuleIndex(v int) {
	as.ruleIndex = v
}
func (as *BaseATNState) GetEpsilonOnlyTransitions() bool {
	return as.epsilonOnlyTransitions
}

func (as *BaseATNState) GetATN() *ATN {
	return as.atn
}

func (as *BaseATNState) SetATN(atn *ATN) {
	as.atn = atn
}

func (as *BaseATNState) GetTransitions() []Transition {
	return as.transitions
}

func (as *BaseATNState) SetTransitions(t []Transition) {
	as.transitions = t
}

func (as *BaseATNState) GetStateType() int {
	return as.stateType
}

func (as *BaseATNState) GetStateNumber() int {
	return as.stateNumber
}

func (as *BaseATNState) SetStateNumber(stateNumber int) {
	as.stateNumber = stateNumber
}

func (as *BaseATNState) GetNextTokenWithinRule() *IntervalSet {
	return as.nextTokenWithinRule
}

func (as *BaseATNState) SetNextTokenWithinRule(v *IntervalSet) {
	as.nextTokenWithinRule = v
}

func (this *BaseATNState) String() string {
	return strconv.Itoa(this.stateNumber)
}

func (this *BaseATNState) equals(other interface{}) bool {
	if ot, ok := other.(ATNState); ok {
		return this.stateNumber == ot.GetStateNumber()
	} else {
		return false
	}
}

func (this *BaseATNState) isNonGreedyExitState() bool {
	return false
}

func (this *BaseATNState) AddTransition(trans Transition, index int) {
	if len(this.transitions) == 0 {
		this.epsilonOnlyTransitions = trans.getIsEpsilon()
	} else if this.epsilonOnlyTransitions != trans.getIsEpsilon() {
		this.epsilonOnlyTransitions = false
	}
	if index == -1 {
		this.transitions = append(this.transitions, trans)
	} else {
		this.transitions = append(this.transitions[:index], append([]Transition{trans}, this.transitions[index:]...)...)
		//        this.transitions.splice(index, 1, trans)
	}
}

type BasicState struct {
	*BaseATNState
}

func NewBasicState() *BasicState {
	this := new(BasicState)
	this.BaseATNState = NewBaseATNState()

	this.stateType = ATNStateBasic
	return this
}

type DecisionState interface {
	ATNState

	getDecision() int
	setDecision(int)

	getNonGreedy() bool
	setNonGreedy(bool)
}

type BaseDecisionState struct {
	*BaseATNState

	decision  int
	nonGreedy bool
}

func NewBaseDecisionState() *BaseDecisionState {

	this := new(BaseDecisionState)

	this.BaseATNState = NewBaseATNState()

	this.decision = -1
	this.nonGreedy = false

	return this
}

func (s *BaseDecisionState) getDecision() int {
	return s.decision
}

func (s *BaseDecisionState) setDecision(b int) {
	s.decision = b
}

func (s *BaseDecisionState) getNonGreedy() bool {
	return s.nonGreedy
}

func (s *BaseDecisionState) setNonGreedy(b bool) {
	s.nonGreedy = b
}

type IBlockStartState interface {
	DecisionState

	getEndState() *BlockEndState
	setEndState(*BlockEndState)
}

//  The start of a regular {@code (...)} block.
type BlockStartState struct {
	*BaseDecisionState

	endState *BlockEndState
}

func NewBlockStartState() *BlockStartState {

	this := new(BlockStartState)

	this.BaseDecisionState = NewBaseDecisionState()
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

	this.stateType = ATNStateBlockStart
	return this
}

// Terminal node of a simple {@code (a|b|c)} block.
type BlockEndState struct {
	*BaseATNState

	startState ATNState
}

func NewBlockEndState() *BlockEndState {

	this := new(BlockEndState)

	this.BaseATNState = NewBaseATNState()
	this.stateType = ATNStateBlockEnd
	this.startState = nil

	return this
}

// The last node in the ATN for a rule, unless that rule is the start symbol.
//  In that case, there is one transition to EOF. Later, we might encode
//  references to all calls to this rule to compute FOLLOW sets for
//  error handling.
//
type RuleStopState struct {
	*BaseATNState
}

func NewRuleStopState() *RuleStopState {
	this := new(RuleStopState)

	this.BaseATNState = NewBaseATNState()
	this.stateType = ATNStateRuleStop
	return this
}

type RuleStartState struct {
	*BaseATNState

	stopState        ATNState
	isPrecedenceRule bool
}

func NewRuleStartState() *RuleStartState {

	this := new(RuleStartState)

	this.BaseATNState = NewBaseATNState()
	this.stateType = ATNStateRuleStart
	this.stopState = nil
	this.isPrecedenceRule = false

	return this
}

// Decision state for {@code A+} and {@code (A|B)+}.  It has two transitions:
//  one to the loop back to start of the block and one to exit.
//
type PlusLoopbackState struct {
	*BaseDecisionState
}

func NewPlusLoopbackState() *PlusLoopbackState {

	this := new(PlusLoopbackState)

	this.BaseDecisionState = NewBaseDecisionState()

	this.stateType = ATNStatePlusLoopBack
	return this
}

// Start of {@code (A|B|...)+} loop. Technically a decision state, but
//  we don't use for code generation somebody might need it, so I'm defining
//  it for completeness. In reality, the {@link PlusLoopbackState} node is the
//  real decision-making note for {@code A+}.
//
type PlusBlockStartState struct {
	*BlockStartState

	loopBackState ATNState
}

func NewPlusBlockStartState() *PlusBlockStartState {

	this := new(PlusBlockStartState)

	this.BlockStartState = NewBlockStartState()

	this.stateType = ATNStatePlusBlockStart
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

	this.stateType = ATNStateStarBlockStart

	return this
}

type StarLoopbackState struct {
	*BaseATNState
}

func NewStarLoopbackState() *StarLoopbackState {

	this := new(StarLoopbackState)

	this.BaseATNState = NewBaseATNState()

	this.stateType = ATNStateStarLoopBack
	return this
}

type StarLoopEntryState struct {
	*BaseDecisionState

	loopBackState          ATNState
	precedenceRuleDecision bool
}

func NewStarLoopEntryState() *StarLoopEntryState {

	this := new(StarLoopEntryState)

	this.BaseDecisionState = NewBaseDecisionState()

	this.stateType = ATNStateStarLoopEntry
	this.loopBackState = nil

	// Indicates whether this state can benefit from a precedence DFA during SLL decision making.
	this.precedenceRuleDecision = false

	return this
}

// Mark the end of a * or + loop.
type LoopEndState struct {
	*BaseATNState

	loopBackState ATNState
}

func NewLoopEndState() *LoopEndState {

	this := new(LoopEndState)

	this.BaseATNState = NewBaseATNState()

	this.stateType = ATNStateLoopEnd
	this.loopBackState = nil

	return this
}

// The Tokens rule start state linking to each lexer rule start state */
type TokensStartState struct {
	*BaseDecisionState
}

func NewTokensStartState() *TokensStartState {

	this := new(TokensStartState)

	this.BaseDecisionState = NewBaseDecisionState()

	this.stateType = ATNStateTokenStart
	return this
}
