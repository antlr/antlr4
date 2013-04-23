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

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DFA {
	/** A set of all DFA states. Use {@link Map} so we can get old state back
	 *  ({@link Set} only allows you to see if it's there).
     */
    @NotNull
	public final Map<DFAState, DFAState> states = new HashMap<DFAState, DFAState>();
	@Nullable
	public DFAState s0;

	public final int decision;

	/** From which ATN state did we create this DFA? */
	@NotNull
	public final DecisionState atnStartState;

	/** Set of configs for a DFA state with at least one conflict? Mainly used as "return value"
	 *  from {@link ParserATNSimulator#predictATN} for retry.
	 */
//	public OrderedHashSet<ATNConfig> conflictSet;

	public DFA(@NotNull DecisionState atnStartState) {
		this(atnStartState, 0);
	}

	public DFA(@NotNull DecisionState atnStartState, int decision) {
		this.atnStartState = atnStartState;
		this.decision = decision;
	}

	/**
	 * Return a list of all states in this DFA, ordered by state number.
	 */
	@NotNull
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

	public List<Set<ATNState>> getATNStatesAlongPath(ParserATNSimulator atn,
													 List<DFAState> dfaStates,
													 TokenStream input, int start, int stop)
	{
		List<Set<ATNState>> atnStates = new ArrayList<Set<ATNState>>();
		int i = start;
		for (DFAState D : dfaStates) {
			Set<ATNState> fullSet = D.configs.getStates();
			Set<ATNState> statesInvolved = new HashSet<ATNState>();
			for (ATNState astate : fullSet) {
				Transition t = astate.transition(0);
				ATNState target = atn.getReachableTarget(t, input.get(i).getType());
				if ( target!=null ) {
					statesInvolved.add(astate);
				}
			}
			System.out.println("statesInvolved upon "+input.get(i).getText()+"="+statesInvolved);
			i++;
			atnStates.add(statesInvolved);
		}
		return atnStates;
	}

	@Override
	public String toString() { return toString(null); }

	public String toString(@Nullable String[] tokenNames) {
		if ( s0==null ) return "";
		DFASerializer serializer = new DFASerializer(this,tokenNames);
		return serializer.toString();
	}

	public String toLexerString() {
		if ( s0==null ) return "";
		DFASerializer serializer = new LexerDFASerializer(this);
		return serializer.toString();
	}

}
