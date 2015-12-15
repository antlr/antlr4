package dfa

var DFAState = require('./DFAState').DFAState
var ATNConfigSet = require('./../atn/ATNConfigSet').ATNConfigSet
var DFASerializer = require('./DFASerializer').DFASerializer
var LexerDFASerializer = require('./DFASerializer').LexerDFASerializer

type DFAStatesSet struct {
	return this
}

Object.defineProperty(DFAStatesSet.prototype, "length", {
	get : function() {
		return Object.keys(this).length
	}
})

func DFA(atnStartState, decision) {
	if (decision == undefined) {
		decision = 0
	}
	// From which ATN state did we create this DFA?
	this.atnStartState = atnStartState
	this.decision = decision
	// A set of all DFA states. Use {@link Map} so we can get old state back
	// ({@link Set} only allows you to see if it's there).
	this._states = new DFAStatesSet()
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
// @throws IllegalStateException if this is not a precedence DFA.
// @see //isPrecedenceDfa()

func (this *DFA) getPrecedenceStartState(precedence) {
	if (!(this.precedenceDfa)) {
		throw ("Only precedence DFAs may contain a precedence start state.")
	}
	// s0.edges is never nil for a precedence DFA
	if (precedence < 0 || precedence >= this.s0.edges.length) {
		return nil
	}
	return this.s0.edges[precedence] || nil
}

// Set the start state for a specific precedence value.
//
// @param precedence The current precedence.
// @param startState The start state corresponding to the specified
// precedence.
//
// @throws IllegalStateException if this is not a precedence DFA.
// @see //isPrecedenceDfa()
//
func (this *DFA) setPrecedenceStartState(precedence, startState) {
	if (!(this.precedenceDfa)) {
		throw ("Only precedence DFAs may contain a precedence start state.")
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

func (this *DFA) setPrecedenceDfa(precedenceDfa) {
	if (this.precedenceDfa!=precedenceDfa) {
		this._states = new DFAStatesSet()
		if (precedenceDfa) {
			var precedenceState = new DFAState(new ATNConfigSet())
			precedenceState.edges = []
			precedenceState.isAcceptState = false
			precedenceState.requiresFullContext = false
			this.s0 = precedenceState
		} else {
			this.s0 = nil
		}
		this.precedenceDfa = precedenceDfa
	}
}

Object.defineProperty(DFA.prototype, "states", {
	get : function() {
		return this._states
	}
})

// Return a list of all states in this DFA, ordered by state number.
func (this *DFA) sortedStates() {
	// states_ is a map of state/state, where key=value
	var keys = Object.keys(this._states)
	var list = []
	for(var i=0i<keys.lengthi++) {
		list.push(this._states[keys[i]])
	}
	return list.sort(function(a, b) {
		return a.stateNumber - b.stateNumber
	})
}

func (this *DFA) toString(literalNames, symbolicNames) {
	literalNames = literalNames || nil
	symbolicNames = symbolicNames || nil
	if (this.s0 == nil) {
		return ""
	}
	var serializer = new DFASerializer(this, literalNames, symbolicNames)
	return serializer.toString()
}

func (this *DFA) toLexerString() {
	if (this.s0 == nil) {
		return ""
	}
	var serializer = new LexerDFASerializer(this)
	return serializer.toString()
}


