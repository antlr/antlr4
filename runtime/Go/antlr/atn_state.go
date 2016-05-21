package antlr

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
	NextTokenWithinRule *IntervalSet
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
	as.NextTokenWithinRule = nil

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
	return as.NextTokenWithinRule
}

func (as *BaseATNState) SetNextTokenWithinRule(v *IntervalSet) {
	as.NextTokenWithinRule = v
}

func (as *BaseATNState) String() string {
	return strconv.Itoa(as.stateNumber)
}

func (as *BaseATNState) equals(other interface{}) bool {
	if ot, ok := other.(ATNState); ok {
		return as.stateNumber == ot.GetStateNumber()
	} else {
		return false
	}
}

func (as *BaseATNState) isNonGreedyExitState() bool {
	return false
}

func (as *BaseATNState) AddTransition(trans Transition, index int) {
	if len(as.transitions) == 0 {
		as.epsilonOnlyTransitions = trans.getIsEpsilon()
	} else if as.epsilonOnlyTransitions != trans.getIsEpsilon() {
		as.epsilonOnlyTransitions = false
	}
	if index == -1 {
		as.transitions = append(as.transitions, trans)
	} else {
		as.transitions = append(as.transitions[:index], append([]Transition{trans}, as.transitions[index:]...)...)
		//        as.transitions.splice(index, 1, trans)
	}
}

type BasicState struct {
	*BaseATNState
}

func NewBasicState() *BasicState {
	b := new(BasicState)
	b.BaseATNState = NewBaseATNState()

	b.stateType = ATNStateBasic
	return b
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

	b := new(BaseDecisionState)

	b.BaseATNState = NewBaseATNState()

	b.decision = -1
	b.nonGreedy = false

	return b
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

type BlockStartState interface {
	DecisionState

	getEndState() *BlockEndState
	setEndState(*BlockEndState)
}

//  The start of a regular {@code (...)} block.
type BaseBlockStartState struct {
	*BaseDecisionState

	endState *BlockEndState
}

func NewBlockStartState() *BaseBlockStartState {

	b := new(BaseBlockStartState)

	b.BaseDecisionState = NewBaseDecisionState()
	b.endState = nil

	return b
}

func (s *BaseBlockStartState) getEndState() *BlockEndState {
	return s.endState
}

func (s *BaseBlockStartState) setEndState(b *BlockEndState) {
	s.endState = b
}

type BasicBlockStartState struct {
	*BaseBlockStartState
}

func NewBasicBlockStartState() *BasicBlockStartState {

	b := new(BasicBlockStartState)

	b.BaseBlockStartState = NewBlockStartState()

	b.stateType = ATNStateBlockStart
	return b
}

// Terminal node of a simple {@code (a|b|c)} block.
type BlockEndState struct {
	*BaseATNState

	startState ATNState
}

func NewBlockEndState() *BlockEndState {

	b := new(BlockEndState)

	b.BaseATNState = NewBaseATNState()
	b.stateType = ATNStateBlockEnd
	b.startState = nil

	return b
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
	r := new(RuleStopState)

	r.BaseATNState = NewBaseATNState()
	r.stateType = ATNStateRuleStop
	return r
}

type RuleStartState struct {
	*BaseATNState

	stopState        ATNState
	isPrecedenceRule bool
}

func NewRuleStartState() *RuleStartState {

	r := new(RuleStartState)

	r.BaseATNState = NewBaseATNState()
	r.stateType = ATNStateRuleStart
	r.stopState = nil
	r.isPrecedenceRule = false

	return r
}

// Decision state for {@code A+} and {@code (A|B)+}.  It has two transitions:
//  one to the loop back to start of the block and one to exit.
//
type PlusLoopbackState struct {
	*BaseDecisionState
}

func NewPlusLoopbackState() *PlusLoopbackState {

	p := new(PlusLoopbackState)

	p.BaseDecisionState = NewBaseDecisionState()

	p.stateType = ATNStatePlusLoopBack
	return p
}

// Start of {@code (A|B|...)+} loop. Technically a decision state, but
//  we don't use for code generation somebody might need it, so I'm defining
//  it for completeness. In reality, the {@link PlusLoopbackState} node is the
//  real decision-making note for {@code A+}.
//
type PlusBlockStartState struct {
	*BaseBlockStartState

	loopBackState ATNState
}

func NewPlusBlockStartState() *PlusBlockStartState {

	p := new(PlusBlockStartState)

	p.BaseBlockStartState = NewBlockStartState()

	p.stateType = ATNStatePlusBlockStart
	p.loopBackState = nil

	return p
}

// The block that begins a closure loop.
type StarBlockStartState struct {
	*BaseBlockStartState
}

func NewStarBlockStartState() *StarBlockStartState {

	s := new(StarBlockStartState)

	s.BaseBlockStartState = NewBlockStartState()

	s.stateType = ATNStateStarBlockStart

	return s
}

type StarLoopbackState struct {
	*BaseATNState
}

func NewStarLoopbackState() *StarLoopbackState {

	s := new(StarLoopbackState)

	s.BaseATNState = NewBaseATNState()

	s.stateType = ATNStateStarLoopBack
	return s
}

type StarLoopEntryState struct {
	*BaseDecisionState

	loopBackState          ATNState
	precedenceRuleDecision bool
}

func NewStarLoopEntryState() *StarLoopEntryState {

	s := new(StarLoopEntryState)

	s.BaseDecisionState = NewBaseDecisionState()

	s.stateType = ATNStateStarLoopEntry
	s.loopBackState = nil

	// Indicates whether s state can benefit from a precedence DFA during SLL decision making.
	s.precedenceRuleDecision = false

	return s
}

// Mark the end of a * or + loop.
type LoopEndState struct {
	*BaseATNState

	loopBackState ATNState
}

func NewLoopEndState() *LoopEndState {

	l := new(LoopEndState)

	l.BaseATNState = NewBaseATNState()

	l.stateType = ATNStateLoopEnd
	l.loopBackState = nil

	return l
}

// The Tokens rule start state linking to each lexer rule start state */
type TokensStartState struct {
	*BaseDecisionState
}

func NewTokensStartState() *TokensStartState {

	t := new(TokensStartState)

	t.BaseDecisionState = NewBaseDecisionState()

	t.stateType = ATNStateTokenStart
	return t
}
