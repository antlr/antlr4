package antlr4

type DFAStatesSet struct {
	states map[string]DFAState
}

func NewDFAStatesSet() *DFAStatesSet {
	n := new(DFAStatesSet)
	n.states = make(map[string]DFAState)
	return n
}

func (this *DFAStatesSet) length() int {
	return len(this.states)
}

type DFA struct {
	atnStartState *DecisionState
	decision int
	_states *DFAStatesSet
	s0 DFAState
	precedenceDfa bool
}

func NewDFA(atnStartState *DecisionState, decision int) *DFA {

	if (decision == nil) {
		decision = 0
	}

	this := new(DFA)

	// From which ATN state did we create this DFA?
	this.atnStartState = atnStartState
	this.decision = decision
	// A set of all DFA states. Use {@link Map} so we can get old state back
	// ({@link Set} only allows you to see if it's there).
	this._states = NewDFAStatesSet()
	this.s0 = nil
	// {@code true} if this DFA is for a precedence decision otherwise,
	// {@code false}. This is the backing field for {@link //isPrecedenceDfa},
	// {@link //setPrecedenceDfa}.
	this.precedenceDfa = false

	return this
}

// Get the start state for a specific precedence value.
//
// @param precedence The current precedence.
// @return The start state corresponding to the specified precedence, or
// {@code nil} if no start state exists for the specified precedence.
//
// @panics IllegalStateException if this is not a precedence DFA.
// @see //isPrecedenceDfa()

func (this *DFA) getPrecedenceStartState(precedence int) *DFAState {
	if (!(this.precedenceDfa)) {
		panic("Only precedence DFAs may contain a precedence start state.")
	}
	// s0.edges is never nil for a precedence DFA
	if (precedence < 0 || precedence >= this.s0.edges.length) {
		return nil
	}
	return this.s0.edges[precedence]
}

// Set the start state for a specific precedence value.
//
// @param precedence The current precedence.
// @param startState The start state corresponding to the specified
// precedence.
//
// @panics IllegalStateException if this is not a precedence DFA.
// @see //isPrecedenceDfa()
//
func (this *DFA) setPrecedenceStartState(precedence int, startState *DFAState)  {
	if (!(this.precedenceDfa)) {
		panic ("Only precedence DFAs may contain a precedence start state.")
	}
	if (precedence < 0) {
		return
	}

	// synchronization on s0 here is ok. when the DFA is turned into a
	// precedence DFA, s0 will be initialized once and not updated again
	// s0.edges is never nil for a precedence DFA
	this.s0.edges[precedence] = startState
}

//
// Sets whether this is a precedence DFA. If the specified value differs
// from the current DFA configuration, the following actions are taken
// otherwise no changes are made to the current DFA.
//
// <ul>
// <li>The {@link //states} map is cleared</li>
// <li>If {@code precedenceDfa} is {@code false}, the initial state
// {@link //s0} is set to {@code nil} otherwise, it is initialized to a new
// {@link DFAState} with an empty outgoing {@link DFAState//edges} array to
// store the start states for individual precedence values.</li>
// <li>The {@link //precedenceDfa} field is updated</li>
// </ul>
//
// @param precedenceDfa {@code true} if this is a precedence DFA otherwise,
// {@code false}

func (this *DFA) setPrecedenceDfa(precedenceDfa bool) {
	if (this.precedenceDfa!=precedenceDfa) {
		this._states = NewDFAStatesSet()
		if (precedenceDfa) {
			var precedenceState = NewDFAState(NewATNConfigSet(false))
			precedenceState.edges = make([]*DFAState)
			precedenceState.isAcceptState = false
			precedenceState.requiresFullContext = false
			this.s0 = precedenceState
		} else {
			this.s0 = nil
		}
		this.precedenceDfa = precedenceDfa
	}
}

func (this *DFA) states(precedenceDfa bool) *DFAStatesSet {
	return this._states
}

// Return a list of all states in this DFA, ordered by state number.
func (this *DFA) sortedStates() []*DFAState {

	panic("Not implemented")

	return nil
	// states_ is a map of state/state, where key=value
//	var keys = Object.keys(this._states)
//	var list = []
//	for i:=0; i<keys.length; i++ {
//		list.push(this._states[keys[i]])
//	}
//	return list.sort(function(a, b) {
//		return a.stateNumber - b.stateNumber
//	})
}

func (this *DFA) toString(literalNames []string, symbolicNames []string) string {
	if (this.s0 == nil) {
		return ""
	}
	var serializer = NewDFASerializer(this, literalNames, symbolicNames)
	return serializer.toString()
}

func (this *DFA) toLexerString() {
	if (this.s0 == nil) {
		return ""
	}
	var serializer = NewLexerDFASerializer(this)
	return serializer.toString()
}


