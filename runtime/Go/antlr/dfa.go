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

	d := new(DFA)

	// From which ATN state did we create d DFA?
	d.atnStartState = atnStartState
	d.decision = decision
	// A set of all DFA states. Use {@link Map} so we can get old state back
	// ({@link Set} only allows you to see if it's there).
	d._states = make(map[string]*DFAState)
	d.s0 = nil
	// {@code true} if d DFA is for a precedence decision otherwise,
	// {@code false}. This is the backing field for {@link //isPrecedenceDfa},
	// {@link //setPrecedenceDfa}.
	d.precedenceDfa = false

	return d
}

// Get the start state for a specific precedence value.
//
// @param precedence The current precedence.
// @return The start state corresponding to the specified precedence, or
// {@code nil} if no start state exists for the specified precedence.
//
// @panics IllegalStateException if d is not a precedence DFA.
// @see //isPrecedenceDfa()

func (d *DFA) getPrecedenceStartState(precedence int) *DFAState {
	if !(d.precedenceDfa) {
		panic("Only precedence DFAs may contain a precedence start state.")
	}
	// s0.edges is never nil for a precedence DFA
	if precedence < 0 || precedence >= len(d.s0.edges) {
		return nil
	}
	return d.s0.edges[precedence]
}

// Set the start state for a specific precedence value.
//
// @param precedence The current precedence.
// @param startState The start state corresponding to the specified
// precedence.
//
// @panics IllegalStateException if d is not a precedence DFA.
// @see //isPrecedenceDfa()
//
func (d *DFA) setPrecedenceStartState(precedence int, startState *DFAState) {
	if !(d.precedenceDfa) {
		panic("Only precedence DFAs may contain a precedence start state.")
	}
	if precedence < 0 {
		return
	}

	// Synchronization on s0 here is ok. when the DFA is turned into a
	// precedence DFA, s0 will be initialized once and not updated again
	// s0.edges is never nil for a precedence DFA

	// s0.edges is never null for a precedence DFA
	if precedence >= len(d.s0.edges) {
		// enlarge the slice
		d.s0.edges = append(d.s0.edges, make([]*DFAState, precedence+1-len(d.s0.edges))...)
	}

	d.s0.edges[precedence] = startState
}

//
// Sets whether d is a precedence DFA. If the specified value differs
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
// @param precedenceDfa {@code true} if d is a precedence DFA otherwise,
// {@code false}

func (d *DFA) setPrecedenceDfa(precedenceDfa bool) {
	if d.precedenceDfa != precedenceDfa {
		d._states = make(map[string]*DFAState)
		if precedenceDfa {
			var precedenceState = NewDFAState(-1, NewBaseATNConfigSet(false))
			precedenceState.edges = make([]*DFAState, 0)
			precedenceState.isAcceptState = false
			precedenceState.requiresFullContext = false
			d.s0 = precedenceState
		} else {
			d.s0 = nil
		}
		d.precedenceDfa = precedenceDfa
	}
}

func (d *DFA) GetStates() map[string]*DFAState {
	return d._states
}

type DFAStateList []*DFAState

func (a DFAStateList) Len() int           { return len(a) }
func (a DFAStateList) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a DFAStateList) Less(i, j int) bool { return a[i].stateNumber < a[j].stateNumber }

// Return a list of all states in d DFA, ordered by state number.
func (d *DFA) sortedStates() []*DFAState {

	// extract the values
	vs := make([]*DFAState, len(d._states))
	i := 0
	for _, v := range d._states {
		vs[i] = v
		i++
	}

	sort.Sort(DFAStateList(vs))
	return vs
}

func (d *DFA) String(literalNames []string, symbolicNames []string) string {
	if d.s0 == nil {
		return ""
	}
	var serializer = NewDFASerializer(d, literalNames, symbolicNames)
	return serializer.String()
}

func (d *DFA) ToLexerString() string {
	if d.s0 == nil {
		return ""
	}
	var serializer = NewLexerDFASerializer(d)
	return serializer.String()
}
