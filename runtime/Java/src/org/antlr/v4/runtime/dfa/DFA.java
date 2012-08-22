/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DFA {
	/** A set of all DFA states. Use Map so we can get old state back
	 *  (Set only allows you to see if it's there).
     */
    @NotNull
	public final ConcurrentMap<DFAState, DFAState> states = new ConcurrentHashMap<DFAState, DFAState>();

	@Nullable
	public final AtomicReference<DFAState> s0 = new AtomicReference<DFAState>();

	@Nullable
	public final AtomicReference<DFAState> s0full = new AtomicReference<DFAState>();

	public final int decision;

	/** From which ATN state did we create this DFA? */
	@NotNull
	public final ATNState atnStartState;

	private final AtomicInteger nextStateNumber = new AtomicInteger();

	/** Set of configs for a DFA state with at least one conflict? Mainly used as "return value"
	 *  from predictATN() for retry.
	 */
//	public OrderedHashSet<ATNConfig> conflictSet;

	public DFA(@NotNull ATNState atnStartState) {
		this(atnStartState, 0);
	}

	public DFA(@NotNull ATNState atnStartState, int decision) {
		this.atnStartState = atnStartState;
		this.decision = decision;
	}

	public boolean isEmpty() {
		return s0.get() == null && s0full.get() == null;
	}

	public boolean isContextSensitive() {
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
