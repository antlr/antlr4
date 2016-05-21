package antlr

import "sort"

type DFA struct {
	atnStartState DecisionState
	decision      int
	_states       map[string]*DFAState
	s0            *DFAState
	precedenceDfa bool
}

func NewDFA(atnStartState DecisionState, decision int) *DFA {

	this := new(DFA)

	// From which ATN state did we create this DFA?
	this.atnStartState = atnStartState
	this.decision = decision
	// A set of all DFA states. Use {@link Map} so we can get old state back
	// ({@link Set} only allows you to see if it's there).
	this._states = make(map[string]*DFAState)
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
	if !(this.precedenceDfa) {
		panic("Only precedence DFAs may contain a precedence start state.")
	}
	// s0.edges is never nil for a precedence DFA
	if precedence < 0 || precedence >= len(this.s0.edges) {
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
func (this *DFA) setPrecedenceStartState(precedence int, startState *DFAState) {
	if !(this.precedenceDfa) {
		panic("Only precedence DFAs may contain a precedence start state.")
	}
	if precedence < 0 {
		return
	}

	// Synchronization on s0 here is ok. when the DFA is turned into a
	// precedence DFA, s0 will be initialized once and not updated again
	// s0.edges is never nil for a precedence DFA

	// s0.edges is never null for a precedence DFA
	if precedence >= len(this.s0.edges) {
		// enlarge the slice
		this.s0.edges = append(this.s0.edges, make([]*DFAState, precedence+1-len(this.s0.edges))...)
	}

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
	if this.precedenceDfa != precedenceDfa {
		this._states = make(map[string]*DFAState)
		if precedenceDfa {
			var precedenceState = NewDFAState(-1, NewBaseATNConfigSet(false))
			precedenceState.edges = make([]*DFAState, 0)
			precedenceState.isAcceptState = false
			precedenceState.requiresFullContext = false
			this.s0 = precedenceState
		} else {
			this.s0 = nil
		}
		this.precedenceDfa = precedenceDfa
	}
}

func (this *DFA) GetStates() map[string]*DFAState {
	return this._states
}

type DFAStateList []*DFAState

func (a DFAStateList) Len() int           { return len(a) }
func (a DFAStateList) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a DFAStateList) Less(i, j int) bool { return a[i].stateNumber < a[j].stateNumber }

// Return a list of all states in this DFA, ordered by state number.
func (this *DFA) sortedStates() []*DFAState {

	// extract the values
	vs := make([]*DFAState, len(this._states))
	i := 0
	for _, v := range this._states {
		vs[i] = v
		i++
	}

	sort.Sort(DFAStateList(vs))
	return vs
}

func (this *DFA) String(literalNames []string, symbolicNames []string) string {
	if this.s0 == nil {
		return ""
	}
	var serializer = NewDFASerializer(this, literalNames, symbolicNames)
	return serializer.String()
}

func (this *DFA) ToLexerString() string {
	if this.s0 == nil {
		return ""
	}
	var serializer = NewLexerDFASerializer(this)
	return serializer.String()
}
