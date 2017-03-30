/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.StarLoopEntryState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DFA {
	/** A set of all DFA states. Use {@link Map} so we can get old state back
	 *  ({@link Set} only allows you to see if it's there).
     */

	public final Map<DFAState, DFAState> states = new HashMap<DFAState, DFAState>();

	public volatile DFAState s0;

	public final int decision;

	/** From which ATN state did we create this DFA? */

	public final DecisionState atnStartState;

	/**
	 * {@code true} if this DFA is for a precedence decision; otherwise,
	 * {@code false}. This is the backing field for {@link #isPrecedenceDfa}.
	 */
	private final boolean precedenceDfa;

	public DFA(DecisionState atnStartState) {
		this(atnStartState, 0);
	}

	public DFA(DecisionState atnStartState, int decision) {
		this.atnStartState = atnStartState;
		this.decision = decision;

		boolean precedenceDfa = false;
		if (atnStartState instanceof StarLoopEntryState) {
			if (((StarLoopEntryState)atnStartState).isPrecedenceDecision) {
				precedenceDfa = true;
				DFAState precedenceState = new DFAState(new ATNConfigSet());
				precedenceState.edges = new DFAState[0];
				precedenceState.isAcceptState = false;
				precedenceState.requiresFullContext = false;
				this.s0 = precedenceState;
			}
		}

		this.precedenceDfa = precedenceDfa;
	}

	/**
	 * Gets whether this DFA is a precedence DFA. Precedence DFAs use a special
	 * start state {@link #s0} which is not stored in {@link #states}. The
	 * {@link DFAState#edges} array for this start state contains outgoing edges
	 * supplying individual start states corresponding to specific precedence
	 * values.
	 *
	 * @return {@code true} if this is a precedence DFA; otherwise,
	 * {@code false}.
	 * @see Parser#getPrecedence()
	 */
	public final boolean isPrecedenceDfa() {
		return precedenceDfa;
	}

	/**
	 * Get the start state for a specific precedence value.
	 *
	 * @param precedence The current precedence.
	 * @return The start state corresponding to the specified precedence, or
	 * {@code null} if no start state exists for the specified precedence.
	 *
	 * @throws IllegalStateException if this is not a precedence DFA.
	 * @see #isPrecedenceDfa()
	 */
	@SuppressWarnings("null")
	public final DFAState getPrecedenceStartState(int precedence) {
		if (!isPrecedenceDfa()) {
			throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
		}

		// s0.edges is never null for a precedence DFA
		if (precedence < 0 || precedence >= s0.edges.length) {
			return null;
		}

		return s0.edges[precedence];
	}

	/**
	 * Set the start state for a specific precedence value.
	 *
	 * @param precedence The current precedence.
	 * @param startState The start state corresponding to the specified
	 * precedence.
	 *
	 * @throws IllegalStateException if this is not a precedence DFA.
	 * @see #isPrecedenceDfa()
	 */
	@SuppressWarnings({"SynchronizeOnNonFinalField", "null"})
	public final void setPrecedenceStartState(int precedence, DFAState startState) {
		if (!isPrecedenceDfa()) {
			throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
		}

		if (precedence < 0) {
			return;
		}

		// synchronization on s0 here is ok. when the DFA is turned into a
		// precedence DFA, s0 will be initialized once and not updated again
		synchronized (s0) {
			// s0.edges is never null for a precedence DFA
			if (precedence >= s0.edges.length) {
				s0.edges = Arrays.copyOf(s0.edges, precedence + 1);
			}

			s0.edges[precedence] = startState;
		}
	}

	/**
	 * Sets whether this is a precedence DFA.
	 *
	 * @param precedenceDfa {@code true} if this is a precedence DFA; otherwise,
	 * {@code false}
	 *
	 * @throws UnsupportedOperationException if {@code precedenceDfa} does not
	 * match the value of {@link #isPrecedenceDfa} for the current DFA.
	 *
	 * @deprecated This method no longer performs any action.
	 */
	@Deprecated
	public final void setPrecedenceDfa(boolean precedenceDfa) {
		if (precedenceDfa != isPrecedenceDfa()) {
			throw new UnsupportedOperationException("The precedenceDfa field cannot change after a DFA is constructed.");
		}
	}

	/**
	 * Return a list of all states in this DFA, ordered by state number.
	 */

	public List<DFAState> getStates() {
		List<DFAState> result = new ArrayList<DFAState>(states.keySet());
		Collections.sort(result, new Comparator<DFAState>() {
			@Override
			public int compare(DFAState o1, DFAState o2) {
				return o1.stateNumber - o2.stateNumber;
			}
		});

		return result;
	}

	@Override
	public String toString() { return toString(VocabularyImpl.EMPTY_VOCABULARY); }

	/**
	 * @deprecated Use {@link #toString(Vocabulary)} instead.
	 */
	@Deprecated
	public String toString(String[] tokenNames) {
		if ( s0==null ) return "";
		DFASerializer serializer = new DFASerializer(this,tokenNames);
		return serializer.toString();
	}

	public String toString(Vocabulary vocabulary) {
		if (s0 == null) {
			return "";
		}

		DFASerializer serializer = new DFASerializer(this, vocabulary);
		return serializer.toString();
	}

	public String toLexerString() {
		if ( s0==null ) return "";
		DFASerializer serializer = new LexerDFASerializer(this);
		return serializer.toString();
	}

}
