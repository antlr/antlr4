/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.Parser;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DFA {
	/** A set of all DFA states. Use {@link Map} so we can get old state back
	 *  ({@link Set} only allows you to see if it's there).
     */
    @NotNull
	public final ConcurrentMap<DFAState, DFAState> states = new ConcurrentHashMap<DFAState, DFAState>();

	@NotNull
	public final AtomicReference<DFAState> s0 = new AtomicReference<DFAState>();

	@NotNull
	public final AtomicReference<DFAState> s0full = new AtomicReference<DFAState>();

	public final int decision;

	/** From which ATN state did we create this DFA? */
	@NotNull
	public final ATNState atnStartState;

	private final AtomicInteger nextStateNumber = new AtomicInteger();

	/**
	 * {@code true} if this DFA is for a precedence decision; otherwise,
	 * {@code false}. This is the backing field for {@link #isPrecedenceDfa},
	 * {@link #setPrecedenceDfa}.
	 */
	private volatile boolean precedenceDfa;

	public DFA(@NotNull ATNState atnStartState) {
		this(atnStartState, 0);
	}

	public DFA(@NotNull ATNState atnStartState, int decision) {
		this.atnStartState = atnStartState;
		this.decision = decision;
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
	public final DFAState getPrecedenceStartState(int precedence, boolean fullContext) {
		if (!isPrecedenceDfa()) {
			throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
		}

		// s0.get() and s0full.get() are never null for a precedence DFA
		if (fullContext) {
			return s0full.get().getTarget(precedence);
		}
		else {
			return s0.get().getTarget(precedence);
		}
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
	public final void setPrecedenceStartState(int precedence, boolean fullContext, DFAState startState) {
		if (!isPrecedenceDfa()) {
			throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
		}

		if (precedence < 0) {
			return;
		}

		if (fullContext) {
			synchronized (s0full) {
				// s0full.get() is never null for a precedence DFA
				s0full.get().setTarget(precedence, startState);
			}
		}
		else {
			synchronized (s0) {
				// s0.get() is never null for a precedence DFA
				s0.get().setTarget(precedence, startState);
			}
		}
	}

	/**
	 * Sets whether this is a precedence DFA. If the specified value differs
	 * from the current DFA configuration, the following actions are taken;
	 * otherwise no changes are made to the current DFA.
	 *
	 * <ul>
	 * <li>The {@link #states} map is cleared</li>
	 * <li>If {@code precedenceDfa} is {@code false}, the initial state
	 * {@link #s0} is set to {@code null}; otherwise, it is initialized to a new
	 * {@link DFAState} with an empty outgoing {@link DFAState#edges} array to
	 * store the start states for individual precedence values.</li>
	 * <li>The {@link #precedenceDfa} field is updated</li>
	 * </ul>
	 *
	 * @param precedenceDfa {@code true} if this is a precedence DFA; otherwise,
	 * {@code false}
	 */
	public final synchronized void setPrecedenceDfa(boolean precedenceDfa) {
		if (this.precedenceDfa != precedenceDfa) {
			this.states.clear();
			if (precedenceDfa) {
				DFAState precedenceState = new DFAState(new ATNConfigSet(), 0, 200);
				precedenceState.isAcceptState = false;
				this.s0.set(precedenceState);

				DFAState fullContextPrecedenceState = new DFAState(new ATNConfigSet(), 0, 200);
				fullContextPrecedenceState.isAcceptState = false;
				this.s0full.set(fullContextPrecedenceState);
			}
			else {
				this.s0.set(null);
				this.s0full.set(null);
			}

			this.precedenceDfa = precedenceDfa;
		}
	}

	public boolean isEmpty() {
		if (isPrecedenceDfa()) {
			return s0.get().getEdgeMap().isEmpty() && s0full.get().getEdgeMap().isEmpty();
		}

		return s0.get() == null && s0full.get() == null;
	}

	public boolean isContextSensitive() {
		if (isPrecedenceDfa()) {
			return s0full.get().getEdgeMap().isEmpty();
		}

		return s0full.get() != null;
	}

	public DFAState addState(DFAState state) {
		state.stateNumber = nextStateNumber.getAndIncrement();
		DFAState existing = states.putIfAbsent(state, state);
		if (existing != null) {
			return existing;
		}

		return state;
	}

	@Override
	public String toString() { return toString(null); }

	public String toString(@Nullable String[] tokenNames) {
		if ( s0.get()==null ) return "";
		DFASerializer serializer = new DFASerializer(this,tokenNames);
		return serializer.toString();
	}

	public String toString(@Nullable String[] tokenNames, @Nullable String[] ruleNames) {
		if ( s0.get()==null ) return "";
		DFASerializer serializer = new DFASerializer(this,tokenNames,ruleNames,atnStartState.atn);
		return serializer.toString();
	}

	public String toLexerString() {
		if ( s0.get()==null ) return "";
		DFASerializer serializer = new LexerDFASerializer(this);
		return serializer.toString();
	}

}
