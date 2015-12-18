package atn
import "antlr4"

// The following images show the relation of states and
// {@link ATNState//transitions} for various grammar constructs.
//
// <ul>
//
// <li>Solid edges marked with an &//0949 indicate a required
// {@link EpsilonTransition}.</li>
//
// <li>Dashed edges indicate locations where any transition derived from
// {@link Transition} might appear.</li>
//
// <li>Dashed nodes are place holders for either a sequence of linked
// {@link BasicState} states or the inclusion of a block representing a nested
// construct in one of the forms below.</li>
//
// <li>Nodes showing multiple outgoing alternatives with a {@code ...} support
// any number of alternatives (one or more). Nodes without the {@code ...} only
// support the exact number of alternatives shown in the diagram.</li>
//
// </ul>
//
// <h2>Basic Blocks</h2>
//
// <h3>Rule</h3>
//
// <embed src="images/Rule.svg" type="image/svg+xml"/>
//
// <h3>Block of 1 or more alternatives</h3>
//
// <embed src="images/Block.svg" type="image/svg+xml"/>
//
// <h2>Greedy Loops</h2>
//
// <h3>Greedy Closure: {@code (...)*}</h3>
//
// <embed src="images/ClosureGreedy.svg" type="image/svg+xml"/>
//
// <h3>Greedy Positive Closure: {@code (...)+}</h3>
//
// <embed src="images/PositiveClosureGreedy.svg" type="image/svg+xml"/>
//
// <h3>Greedy Optional: {@code (...)?}</h3>
//
// <embed src="images/OptionalGreedy.svg" type="image/svg+xml"/>
//
// <h2>Non-Greedy Loops</h2>
//
// <h3>Non-Greedy Closure: {@code (...)*?}</h3>
//
// <embed src="images/ClosureNonGreedy.svg" type="image/svg+xml"/>
//
// <h3>Non-Greedy Positive Closure: {@code (...)+?}</h3>
//
// <embed src="images/PositiveClosureNonGreedy.svg" type="image/svg+xml"/>
//
// <h3>Non-Greedy Optional: {@code (...)??}</h3>
//
// <embed src="images/OptionalNonGreedy.svg" type="image/svg+xml"/>
//

var INITIAL_NUM_TRANSITIONS = 4

type ATNState struct {
	// Which ATN are we in?
	atn *ATN
	stateNumber int
	stateType int
	ruleIndex int
	epsilonOnlyTransitions bool
	// Track the transitions emanating from this ATN state.
	transitions []*Transition
	// Used to cache lookahead during parsing, not used during construction
	nextTokenWithinRule *antlr4.Token
}

func NewATNState() *ATNState {

	as := new(ATNState)
	as.initATNState()

    return as
}

func (as *ATNState) initATNState(){

	// Which ATN are we in?
	as.atn = nil
	as.stateNumber = ATNStateINVALID_STATE_NUMBER
	as.stateType = nil
	as.ruleIndex = 0 // at runtime, we don't have Rule objects
	as.epsilonOnlyTransitions = false
	// Track the transitions emanating from this ATN state.
	as.transitions = make([]Transition, 0)
	// Used to cache lookahead during parsing, not used during construction
	as.nextTokenWithinRule = nil

}

const (
	// constants for serialization
	ATNStateInvalidType = 0
	ATNStateBASIC = 1
	ATNStateRULE_START = 2
	ATNStateBLOCK_START = 3
	ATNStatePLUS_BLOCK_START = 4
	ATNStateSTAR_BLOCK_START = 5
	ATNStateTOKEN_START = 6
	ATNStateRULE_STOP = 7
	ATNStateBLOCK_END = 8
	ATNStateSTAR_LOOP_BACK = 9
	ATNStateSTAR_LOOP_ENTRY = 10
	ATNStatePLUS_LOOP_BACK = 11
	ATNStateLOOP_END = 12

	ATNStateINVALID_STATE_NUMBER = -1
)
//
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

func (this *ATNState) toString() string {
	return this.stateNumber
}

func (this *ATNState) equals(other *ATNState) bool {
    if ok := other.(ATNState); ok {
        return this.stateNumber == other.stateNumber
    } else {
        return false
    }
}

func (this *ATNState) isNonGreedyExitState() {
    return false
}

func (this *ATNState) addTransition(trans *Transition, index int) {
    if ( len(this.transitions) == 0 ) {
        this.epsilonOnlyTransitions = trans.isEpsilon
    } else if(this.epsilonOnlyTransitions != trans.isEpsilon) {
        this.epsilonOnlyTransitions = false
    }
    if (index==-1) {
		this.transitions = append(this.transitions, trans)
    } else {
        this.transitions.splice(index, 1, trans)
    }
}

type BasicState struct {
	ATNState
}

func NewBasicState() *BasicState {
	this := new(BasicState)
	this.initATNState()

    this.stateType = ATNStateBASIC
    return this
}

type DecisionState struct {
	ATNState

	decision int
	nonGreedy bool
}

func NewDecisionState() *DecisionState {

	this := new(DecisionState)

	this.initATNState()
	this.initDecisionState()

    return this
}

func (this *DecisionState) initDecisionState() {

	this.decision = -1
	this.nonGreedy = false

}

//  The start of a regular {@code (...)} block.
type BlockStartState struct {
	DecisionState

	endState *ATNState
}

func NewBlockStartState() *BlockStartState {

	this := new(BlockStartState)

	this.initATNState()
	this.initDecisionState()

	return this
}

func (this *BlockStartState) initBlockStartState() {

	this.endState = nil

}

type BasicBlockStartState struct {
	BlockStartState
}

func NewBasicBlockStartState() *BasicBlockStartState {

	this := new(BasicBlockStartState)

	this.initATNState()
	this.initDecisionState()
	this.initBlockStartState()

	this.stateType = ATNStateBLOCK_START
	return this
}

// Terminal node of a simple {@code (a|b|c)} block.
type BlockEndState struct {
	ATNState

	startState *ATNState
}

func NewBlockEndState() *BlockEndState {

	this := new(BlockEndState)

	this.initATNState()
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

	this.initATNState()
    this.stateType = ATNStateRULE_STOP
    return this
}

type RuleStartState struct {
	ATNState

	stopState *ATNState
	isPrecedenceRule bool
}

func NewRuleStartState() *RuleStartState {

	this := new(RuleStartState)

	this.initATNState()
	this.stateType = ATNStateRULE_START
	this.stopState = nil
	this.isPrecedenceRule = false

	return this
}

// Decision state for {@code A+} and {@code (A|B)+}.  It has two transitions:
//  one to the loop back to start of the block and one to exit.
//
type PlusLoopbackState struct {
	BlockStartState
}

func NewPlusLoopbackState() *PlusLoopbackState {

	this := new(PlusLoopbackState)

	this.initATNState()
	this.initDecisionState()
	this.initBlockStartState()

	this.stateType = ATNStatePLUS_LOOP_BACK
	return this
}

// Start of {@code (A|B|...)+} loop. Technically a decision state, but
//  we don't use for code generation somebody might need it, so I'm defining
//  it for completeness. In reality, the {@link PlusLoopbackState} node is the
//  real decision-making note for {@code A+}.
//
type PlusBlockStartState struct {
	BlockStartState

	loopBackState *ATNState
}

func NewPlusBlockStartState() *PlusBlockStartState {

	this := new(PlusBlockStartState)

	this.initATNState()
	this.initDecisionState()
	this.initBlockStartState()

	this.stateType = ATNStatePLUS_BLOCK_START
    this.loopBackState = nil

    return this
}

// The block that begins a closure loop.
type StarBlockStartState struct {
	BlockStartState
}

func NewStarBlockStartState() *StarBlockStartState {

	this := new(StarBlockStartState)

	this.initATNState()
	this.initDecisionState()
	this.initBlockStartState()

	this.stateType = ATNStateSTAR_BLOCK_START

	return this
}


type StarLoopbackState struct {
	ATNState
}

func NewStarLoopbackState() *StarLoopbackState {

	this := new(StarLoopbackState)

	this.initATNState()

	this.stateType = ATNStateSTAR_LOOP_BACK
	return this
}


type StarLoopEntryState struct {
	DecisionState

	loopBackState *ATNState
	precedenceRuleDecision bool
}

func NewStarLoopEntryState() *StarLoopEntryState {

	this := new(StarLoopEntryState)

	this.initATNState()
	this.initDecisionState()

	this.stateType = ATNStateSTAR_LOOP_ENTRY
    this.loopBackState = nil

    // Indicates whether this state can benefit from a precedence DFA during SLL decision making.
    this.precedenceRuleDecision = false

    return this
}


// Mark the end of a * or + loop.
type LoopEndState struct {
	ATNState
	loopBackState *ATNState
}

func NewLoopEndState() *LoopEndState {

	this := new(LoopEndState)

	this.initATNState()

	this.stateType = ATNStateLOOP_END
	this.loopBackState = nil

	return this
}

// The Tokens rule start state linking to each lexer rule start state */
type TokensStartState struct {
	DecisionState
}

func NewTokensStartState() *TokensStartState {

	this := new(TokensStartState)

	this.initATNState()
	this.initDecisionState()

	this.stateType = ATNStateTOKEN_START
	return this
}















