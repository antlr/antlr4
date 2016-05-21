package antlr

import (
	"fmt"
	"strconv"
)

// Map a predicate to a predicted alternative.///

type PredPrediction struct {
	alt  int
	pred SemanticContext
}

func NewPredPrediction(pred SemanticContext, alt int) *PredPrediction {
	p := new(PredPrediction)

	p.alt = alt
	p.pred = pred

	return p
}

func (p *PredPrediction) String() string {
	return "(" + fmt.Sprint(p.pred) + ", " + fmt.Sprint(p.alt) + ")"
}

// A DFA state represents a set of possible ATN configurations.
// As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
// to keep track of all possible states the ATN can be in after
// reading each input symbol. That is to say, after reading
// input a1a2..an, the DFA is in a state that represents the
// subset T of the states of the ATN that are reachable from the
// ATN's start state along some path labeled a1a2..an."
// In conventional NFA&rarrDFA conversion, therefore, the subset T
// would be a bitset representing the set of states the
// ATN could be in. We need to track the alt predicted by each
// state as well, however. More importantly, we need to maintain
// a stack of states, tracking the closure operations as they
// jump from rule to rule, emulating rule invocations (method calls).
// I have to add a stack to simulate the proper lookahead sequences for
// the underlying LL grammar from which the ATN was derived.
//
// <p>I use a set of ATNConfig objects not simple states. An ATNConfig
// is both a state (ala normal conversion) and a RuleContext describing
// the chain of rules (if any) followed to arrive at that state.</p>
//
// <p>A DFA state may have multiple references to a particular state,
// but with different ATN contexts (with same or different alts)
// meaning that state was reached via a different set of rule invocations.</p>
// /

type DFAState struct {
	stateNumber         int
	configs             ATNConfigSet
	edges               []*DFAState
	isAcceptState       bool
	prediction          int
	lexerActionExecutor *LexerActionExecutor
	requiresFullContext bool
	predicates          []*PredPrediction
}

func NewDFAState(stateNumber int, configs ATNConfigSet) *DFAState {

	if configs == nil {
		configs = NewBaseATNConfigSet(false)
	}

	d := new(DFAState)

	d.stateNumber = stateNumber
	d.configs = configs
	// {@code edges[symbol]} points to target of symbol. Shift up by 1 so (-1)
	// {@link Token//EOF} maps to {@code edges[0]}.
	d.edges = nil
	d.isAcceptState = false
	// if accept state, what ttype do we Match or alt do we predict?
	// This is set to {@link ATN//INVALID_ALT_NUMBER} when {@link
	// //predicates}{@code !=nil} or
	// {@link //requiresFullContext}.
	d.prediction = 0
	d.lexerActionExecutor = nil
	// Indicates that d state was created during SLL prediction that
	// discovered a conflict between the configurations in the state. Future
	// {@link ParserATNSimulator//execATN} invocations immediately jumped doing
	// full context prediction if d field is true.
	d.requiresFullContext = false
	// During SLL parsing, d is a list of predicates associated with the
	// ATN configurations of the DFA state. When we have predicates,
	// {@link //requiresFullContext} is {@code false} since full context
	// prediction evaluates predicates
	// on-the-fly. If d is not nil, then {@link //prediction} is
	// {@link ATN//INVALID_ALT_NUMBER}.
	//
	// <p>We only use these for non-{@link //requiresFullContext} but
	// conflicting states. That
	// means we know from the context (it's $ or we don't dip into outer
	// context) that it's an ambiguity not a conflict.</p>
	//
	// <p>This list is computed by {@link
	// ParserATNSimulator//predicateDFAState}.</p>
	d.predicates = nil
	return d
}

// Get the set of all alts mentioned by all ATN configurations in d
// DFA state.
func (d *DFAState) GetAltSet() *Set {
	var alts = NewSet(nil, nil)
	if d.configs != nil {
		for _, c := range d.configs.GetItems() {
			alts.add(c.GetAlt())
		}
	}
	if alts.length() == 0 {
		return nil
	}

	return alts
}

func (d *DFAState) setPrediction(v int) {
	d.prediction = v
}

// Two {@link DFAState} instances are equal if their ATN configuration sets
// are the same. This method is used to see if a state already exists.
//
// <p>Because the number of alternatives and number of ATN configurations are
// finite, there is a finite number of DFA states that can be processed.
// This is necessary to show that the algorithm terminates.</p>
//
// <p>Cannot test the DFA state numbers here because in
// {@link ParserATNSimulator//addDFAState} we need to know if any other state
// exists that has d exact set of ATN configurations. The
// {@link //stateNumber} is irrelevant.</p>

func (d *DFAState) equals(other interface{}) bool {

	if d == other {
		return true
	} else if _, ok := other.(*DFAState); !ok {
		return false
	}

	return d.configs.Equals(other.(*DFAState).configs)
}

func (d *DFAState) String() string {
	return strconv.Itoa(d.stateNumber) + ":" + d.Hash()
}

func (d *DFAState) Hash() string {

	var s string
	if d.isAcceptState {
		if d.predicates != nil {
			s = "=>" + fmt.Sprint(d.predicates)
		} else {
			s = "=>" + fmt.Sprint(d.prediction)
		}
	}

	return fmt.Sprint(d.configs) + s
}
