// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import "sort"

type DFA struct {
	// atnStartState is the ATN state in which this was created
	atnStartState DecisionState

	decision int

	// states is all the DFA states. Use Map to get the old state back; Set can only
	// indicate whether it is there.
	states map[int]*DFAState

	s0 *DFAState

	// precedenceDfa is the backing field for isPrecedenceDfa and setPrecedenceDfa.
	// True if the DFA is for a precedence decision and false otherwise.
	precedenceDfa bool
}

func NewDFA(atnStartState DecisionState, decision int) *DFA {
	return &DFA{
		atnStartState: atnStartState,
		decision:      decision,
		states:        make(map[int]*DFAState),
	}
}

// getPrecedenceStartState gets the start state for the current precedence and
// returns the start state corresponding to the specified precedence if a start
// state exists for the specified precedence and nil otherwise. d must be a
// precedence DFA. See also isPrecedenceDfa.
func (d *DFA) getPrecedenceStartState(precedence int) *DFAState {
	if !d.precedenceDfa {
		panic("only precedence DFAs may contain a precedence start state")
	}

	// s0.edges is never nil for a precedence DFA
	if precedence < 0 || precedence >= len(d.s0.edges) {
		return nil
	}

	return d.s0.edges[precedence]
}

// setPrecedenceStartState sets the start state for the current precedence. d
// must be a precedence DFA. See also isPrecedenceDfa.
func (d *DFA) setPrecedenceStartState(precedence int, startState *DFAState) {
	if !d.precedenceDfa {
		panic("only precedence DFAs may contain a precedence start state")
	}

	if precedence < 0 {
		return
	}

	// Synchronization on s0 here is ok. When the DFA is turned into a
	// precedence DFA, s0 will be initialized once and not updated again. s0.edges
	// is never nil for a precedence DFA.
	if precedence >= len(d.s0.edges) {
		d.s0.edges = append(d.s0.edges, make([]*DFAState, precedence+1-len(d.s0.edges))...)
	}

	d.s0.edges[precedence] = startState
}

// setPrecedenceDfa sets whether d is a precedence DFA. If precedenceDfa differs
// from the current DFA configuration, then d.states is cleared, the initial
// state s0 is set to a new DFAState with an empty outgoing DFAState.edges to
// store the start states for individual precedence values if precedenceDfa is
// true or nil otherwise, and d.precedenceDfa is updated.
func (d *DFA) setPrecedenceDfa(precedenceDfa bool) {
	if d.precedenceDfa != precedenceDfa {
		d.states = make(map[int]*DFAState)

		if precedenceDfa {
			precedenceState := NewDFAState(-1, NewBaseATNConfigSet(false))

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

func (d *DFA) GetStates() map[int]*DFAState {
	return d.states
}

type DFAStateList []*DFAState

func (d DFAStateList) Len() int           { return len(d) }
func (d DFAStateList) Less(i, j int) bool { return d[i].stateNumber < d[j].stateNumber }
func (d DFAStateList) Swap(i, j int)      { d[i], d[j] = d[j], d[i] }

// sortedStates returns the states in d sorted by their state number.
func (d *DFA) sortedStates() []*DFAState {
	vs := make([]*DFAState, 0, len(d.states))

	for _, v := range d.states {
		vs = append(vs, v)
	}

	sort.Sort(DFAStateList(vs))

	return vs
}

func (d *DFA) String(literalNames []string, symbolicNames []string) string {
	if d.s0 == nil {
		return ""
	}

	return NewDFASerializer(d, literalNames, symbolicNames).String()
}

func (d *DFA) ToLexerString() string {
	if d.s0 == nil {
		return ""
	}

	return NewLexerDFASerializer(d).String()
}
