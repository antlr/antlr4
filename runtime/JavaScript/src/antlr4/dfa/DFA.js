/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

const {Set} = require("../Utils");
const {DFAState} = require('./DFAState');
const {StarLoopEntryState} = require('../atn/ATNState');
const {ATNConfigSet} = require('./../atn/ATNConfigSet');
const {DFASerializer} = require('./DFASerializer');
const {LexerDFASerializer} = require('./DFASerializer');

class DFA {
	constructor(atnStartState, decision) {
		if (decision === undefined) {
			decision = 0;
		}
		/**
		 * From which ATN state did we create this DFA?
		 */
		this.atnStartState = atnStartState;
		this.decision = decision;
		/**
		 * A set of all DFA states. Use {@link Map} so we can get old state back
		 * ({@link Set} only allows you to see if it's there).
		 */
		this._states = new Set();
		this.s0 = null;
		/**
		 * {@code true} if this DFA is for a precedence decision; otherwise,
		 * {@code false}. This is the backing field for {@link //isPrecedenceDfa},
		 * {@link //setPrecedenceDfa}
		 */
		this.precedenceDfa = false;
		if (atnStartState instanceof StarLoopEntryState)
		{
			if (atnStartState.isPrecedenceDecision) {
				this.precedenceDfa = true;
				const precedenceState = new DFAState(null, new ATNConfigSet());
				precedenceState.edges = [];
				precedenceState.isAcceptState = false;
				precedenceState.requiresFullContext = false;
				this.s0 = precedenceState;
			}
		}
	}

	/**
	 * Get the start state for a specific precedence value.
	 *
	 * @param precedence The current precedence.
	 * @return The start state corresponding to the specified precedence, or
	 * {@code null} if no start state exists for the specified precedence.
	 *
	 * @throws IllegalStateException if this is not a precedence DFA.
	 * @see //isPrecedenceDfa()
	 */
	getPrecedenceStartState(precedence) {
		if (!(this.precedenceDfa)) {
			throw ("Only precedence DFAs may contain a precedence start state.");
		}
		// s0.edges is never null for a precedence DFA
		if (precedence < 0 || precedence >= this.s0.edges.length) {
			return null;
		}
		return this.s0.edges[precedence] || null;
	}

	/**
	 * Set the start state for a specific precedence value.
	 *
	 * @param precedence The current precedence.
	 * @param startState The start state corresponding to the specified
	 * precedence.
	 *
	 * @throws IllegalStateException if this is not a precedence DFA.
	 * @see //isPrecedenceDfa()
	 */
	setPrecedenceStartState(precedence, startState) {
		if (!(this.precedenceDfa)) {
			throw ("Only precedence DFAs may contain a precedence start state.");
		}
		if (precedence < 0) {
			return;
		}

		/**
		 * synchronization on s0 here is ok. when the DFA is turned into a
		 * precedence DFA, s0 will be initialized once and not updated again
		 * s0.edges is never null for a precedence DFA
		 */
		this.s0.edges[precedence] = startState;
	}

	/**
	 * Sets whether this is a precedence DFA. If the specified value differs
	 * from the current DFA configuration, the following actions are taken;
	 * otherwise no changes are made to the current DFA.
	 *
	 * <ul>
	 * <li>The {@link //states} map is cleared</li>
	 * <li>If {@code precedenceDfa} is {@code false}, the initial state
	 * {@link //s0} is set to {@code null}; otherwise, it is initialized to a new
	 * {@link DFAState} with an empty outgoing {@link DFAState//edges} array to
	 * store the start states for individual precedence values.</li>
	 * <li>The {@link //precedenceDfa} field is updated</li>
	 * </ul>
	 *
	 * @param precedenceDfa {@code true} if this is a precedence DFA; otherwise,
	 * {@code false}
	 */
	setPrecedenceDfa(precedenceDfa) {
		if (this.precedenceDfa!==precedenceDfa) {
			this._states = new Set();
			if (precedenceDfa) {
				const precedenceState = new DFAState(null, new ATNConfigSet());
				precedenceState.edges = [];
				precedenceState.isAcceptState = false;
				precedenceState.requiresFullContext = false;
				this.s0 = precedenceState;
			} else {
				this.s0 = null;
			}
			this.precedenceDfa = precedenceDfa;
		}
	}

	/**
	 * Return a list of all states in this DFA, ordered by state number.
	 */
	sortedStates() {
		const list = this._states.values();
		return list.sort(function(a, b) {
			return a.stateNumber - b.stateNumber;
		});
	}

	toString(literalNames, symbolicNames) {
		literalNames = literalNames || null;
		symbolicNames = symbolicNames || null;
		if (this.s0 === null) {
			return "";
		}
		const serializer = new DFASerializer(this, literalNames, symbolicNames);
		return serializer.toString();
	}

	toLexerString() {
		if (this.s0 === null) {
			return "";
		}
		const serializer = new LexerDFASerializer(this);
		return serializer.toString();
	}

	get states(){
		return this._states;
	}
}


module.exports = DFA;
