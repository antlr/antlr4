package atn

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
    this.atn = nil
    this.stateNumber = ATNState.INVALID_STATE_NUMBER
    this.stateType = nil
    this.ruleIndex = 0 // at runtime, we don't have Rule objects
    this.epsilonOnlyTransitions = false
    // Track the transitions emanating from this ATN state.
    this.transitions = []
    // Used to cache lookahead during parsing, not used during construction
    this.nextTokenWithinRule = nil
    return this
}

// constants for serialization
ATNState.INVALID_TYPE = 0
ATNState.BASIC = 1
ATNState.RULE_START = 2
ATNState.BLOCK_START = 3
ATNState.PLUS_BLOCK_START = 4
ATNState.STAR_BLOCK_START = 5
ATNState.TOKEN_START = 6
ATNState.RULE_STOP = 7
ATNState.BLOCK_END = 8
ATNState.STAR_LOOP_BACK = 9
ATNState.STAR_LOOP_ENTRY = 10
ATNState.PLUS_LOOP_BACK = 11
ATNState.LOOP_END = 12

ATNState.serializationNames = [
            "INVALID",
            "BASIC",
            "RULE_START",
            "BLOCK_START",
            "PLUS_BLOCK_START",
            "STAR_BLOCK_START",
            "TOKEN_START",
            "RULE_STOP",
            "BLOCK_END",
            "STAR_LOOP_BACK",
            "STAR_LOOP_ENTRY",
            "PLUS_LOOP_BACK",
            "LOOP_END" ]

ATNState.INVALID_STATE_NUMBER = -1

func (this *ATNState) toString() {
	return this.stateNumber
}

func (this *ATNState) equals(other) {
    if (other instanceof ATNState) {
        return this.stateNumber==other.stateNumber
    } else {
        return false
    }
}

func (this *ATNState) isNonGreedyExitState() {
    return false
}


func (this *ATNState) addTransition(trans, index) {
	if(index==undefined) {
		index = -1
	}
    if (this.transitions.length==0) {
        this.epsilonOnlyTransitions = trans.isEpsilon
    } else if(this.epsilonOnlyTransitions !== trans.isEpsilon) {
        this.epsilonOnlyTransitions = false
    }
    if (index==-1) {
        this.transitions.push(trans)
    } else {
        this.transitions.splice(index, 1, trans)
    }
}

type BasicState struct {
	ATNState.call(this)
    this.stateType = ATNState.BASIC
    return this
}

BasicState.prototype = Object.create(ATNState.prototype)
BasicState.prototype.constructor = BasicState


type DecisionState struct {
	ATNState.call(this)
    this.decision = -1
    this.nonGreedy = false
    return this
}

DecisionState.prototype = Object.create(ATNState.prototype)
DecisionState.prototype.constructor = DecisionState


//  The start of a regular {@code (...)} block.
type BlockStartState struct {
	DecisionState.call(this)
	this.endState = nil
	return this
}

BlockStartState.prototype = Object.create(DecisionState.prototype)
BlockStartState.prototype.constructor = BlockStartState


type BasicBlockStartState struct {
	BlockStartState.call(this)
	this.stateType = ATNState.BLOCK_START
	return this
}

BasicBlockStartState.prototype = Object.create(BlockStartState.prototype)
BasicBlockStartState.prototype.constructor = BasicBlockStartState


// Terminal node of a simple {@code (a|b|c)} block.
type BlockEndState struct {
	ATNState.call(this)
	this.stateType = ATNState.BLOCK_END
    this.startState = nil
    return this
}

BlockEndState.prototype = Object.create(ATNState.prototype)
BlockEndState.prototype.constructor = BlockEndState


// The last node in the ATN for a rule, unless that rule is the start symbol.
//  In that case, there is one transition to EOF. Later, we might encode
//  references to all calls to this rule to compute FOLLOW sets for
//  error handling.
//
type RuleStopState struct {
	ATNState.call(this)
    this.stateType = ATNState.RULE_STOP
    return this
}

RuleStopState.prototype = Object.create(ATNState.prototype)
RuleStopState.prototype.constructor = RuleStopState

type RuleStartState struct {
	ATNState.call(this)
	this.stateType = ATNState.RULE_START
	this.stopState = nil
	this.isPrecedenceRule = false
	return this
}

RuleStartState.prototype = Object.create(ATNState.prototype)
RuleStartState.prototype.constructor = RuleStartState

// Decision state for {@code A+} and {@code (A|B)+}.  It has two transitions:
//  one to the loop back to start of the block and one to exit.
//
type PlusLoopbackState struct {
	DecisionState.call(this)
	this.stateType = ATNState.PLUS_LOOP_BACK
	return this
}

PlusLoopbackState.prototype = Object.create(DecisionState.prototype)
PlusLoopbackState.prototype.constructor = PlusLoopbackState
        

// Start of {@code (A|B|...)+} loop. Technically a decision state, but
//  we don't use for code generation somebody might need it, so I'm defining
//  it for completeness. In reality, the {@link PlusLoopbackState} node is the
//  real decision-making note for {@code A+}.
//
type PlusBlockStartState struct {
	BlockStartState.call(this)
	this.stateType = ATNState.PLUS_BLOCK_START
    this.loopBackState = nil
    return this
}

PlusBlockStartState.prototype = Object.create(BlockStartState.prototype)
PlusBlockStartState.prototype.constructor = PlusBlockStartState

// The block that begins a closure loop.
type StarBlockStartState struct {
	BlockStartState.call(this)
	this.stateType = ATNState.STAR_BLOCK_START
	return this
}

StarBlockStartState.prototype = Object.create(BlockStartState.prototype)
StarBlockStartState.prototype.constructor = StarBlockStartState


type StarLoopbackState struct {
	ATNState.call(this)
	this.stateType = ATNState.STAR_LOOP_BACK
	return this
}

StarLoopbackState.prototype = Object.create(ATNState.prototype)
StarLoopbackState.prototype.constructor = StarLoopbackState


type StarLoopEntryState struct {
	DecisionState.call(this)
	this.stateType = ATNState.STAR_LOOP_ENTRY
    this.loopBackState = nil
    // Indicates whether this state can benefit from a precedence DFA during SLL decision making.
    this.precedenceRuleDecision = nil
    return this
}

StarLoopEntryState.prototype = Object.create(DecisionState.prototype)
StarLoopEntryState.prototype.constructor = StarLoopEntryState


// Mark the end of a * or + loop.
type LoopEndState struct {
	ATNState.call(this)
	this.stateType = ATNState.LOOP_END
	this.loopBackState = nil
	return this
}

LoopEndState.prototype = Object.create(ATNState.prototype)
LoopEndState.prototype.constructor = LoopEndState


// The Tokens rule start state linking to each lexer rule start state */
type TokensStartState struct {
	DecisionState.call(this)
	this.stateType = ATNState.TOKEN_START
	return this
}

TokensStartState.prototype = Object.create(DecisionState.prototype)
TokensStartState.prototype.constructor = TokensStartState
















