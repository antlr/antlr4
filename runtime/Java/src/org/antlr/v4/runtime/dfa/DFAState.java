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

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.LexerActionExecutor;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** A DFA state represents a set of possible ATN configurations.
 *  As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
 *  to keep track of all possible states the ATN can be in after
 *  reading each input symbol.  That is to say, after reading
 *  input a1a2..an, the DFA is in a state that represents the
 *  subset T of the states of the ATN that are reachable from the
 *  ATN's start state along some path labeled a1a2..an."
 *  In conventional NFA&rarr;DFA conversion, therefore, the subset T
 *  would be a bitset representing the set of states the
 *  ATN could be in.  We need to track the alt predicted by each
 *  state as well, however.  More importantly, we need to maintain
 *  a stack of states, tracking the closure operations as they
 *  jump from rule to rule, emulating rule invocations (method calls).
 *  I have to add a stack to simulate the proper lookahead sequences for
 *  the underlying LL grammar from which the ATN was derived.
 *
 *  <p>I use a set of ATNConfig objects not simple states.  An ATNConfig
 *  is both a state (ala normal conversion) and a RuleContext describing
 *  the chain of rules (if any) followed to arrive at that state.</p>
 *
 *  <p>A DFA state may have multiple references to a particular state,
 *  but with different ATN contexts (with same or different alts)
 *  meaning that state was reached via a different set of rule invocations.</p>
 */
public class DFAState {
	public int stateNumber = -1;

	@NotNull
	public final ATNConfigSet configs;

	/** {@code edges.get(symbol)} points to target of symbol.
	 */
	@NotNull
	private AbstractEdgeMap<DFAState> edges;

	public boolean isAcceptState = false;

	/** if accept state, what ttype do we match or alt do we predict?
	 *  This is set to {@link ATN#INVALID_ALT_NUMBER} when {@link #predicates}{@code !=null}.
	 */
	public int prediction;

	public LexerActionExecutor lexerActionExecutor;

	/** These keys for these edges are the top level element of the global context. */
	@NotNull
	private AbstractEdgeMap<DFAState> contextEdges;

	/** Symbols in this set require a global context transition before matching an input symbol. */
	@Nullable
	private BitSet contextSymbols;

	/**
	 * This list is computed by {@link ParserATNSimulator#predicateDFAState}.
	 */
	@Nullable
	public PredPrediction[] predicates;

	/** Map a predicate to a predicted alternative. */
	public static class PredPrediction {
		@NotNull
		public SemanticContext pred; // never null; at least SemanticContext.NONE
		public int alt;
		public PredPrediction(@NotNull SemanticContext pred, int alt) {
			this.alt = alt;
			this.pred = pred;
		}
		@Override
		public String toString() {
			return "("+pred+", "+alt+ ")";
		}
	}

	public DFAState(@NotNull DFA dfa, @NotNull ATNConfigSet configs) {
		this(dfa.getEmptyEdgeMap(), dfa.getEmptyContextEdgeMap(), configs);
	}

	public DFAState(@NotNull EmptyEdgeMap<DFAState> emptyEdges, @NotNull EmptyEdgeMap<DFAState> emptyContextEdges, @NotNull ATNConfigSet configs) {
		this.configs = configs;
		this.edges = emptyEdges;
		this.contextEdges = emptyContextEdges;
	}

	public final boolean isContextSensitive() {
		return contextSymbols != null;
	}

	public final boolean isContextSymbol(int symbol) {
		if (!isContextSensitive() || symbol < edges.minIndex) {
			return false;
		}

		return contextSymbols.get(symbol - edges.minIndex);
	}

	public final void setContextSymbol(int symbol) {
		assert isContextSensitive();
		if (symbol < edges.minIndex) {
			return;
		}

		contextSymbols.set(symbol - edges.minIndex);
	}

	public synchronized void setContextSensitive(ATN atn) {
		assert !configs.isOutermostConfigSet();
		if (isContextSensitive()) {
			return;
		}

		contextSymbols = new BitSet();
	}

	public synchronized DFAState getTarget(int symbol) {
		return edges.get(symbol);
	}

	public synchronized void setTarget(int symbol, DFAState target) {
		edges = edges.put(symbol, target);
	}

	public Map<Integer, DFAState> getEdgeMap() {
		return edges.toMap();
	}

	public synchronized DFAState getContextTarget(int invokingState) {
		if (invokingState == PredictionContext.EMPTY_FULL_STATE_KEY) {
			invokingState = -1;
		}

		return contextEdges.get(invokingState);
	}

	public synchronized void setContextTarget(int invokingState, DFAState target) {
		if (!isContextSensitive()) {
			throw new IllegalStateException("The state is not context sensitive.");
		}

		if (invokingState == PredictionContext.EMPTY_FULL_STATE_KEY) {
			invokingState = -1;
		}

		contextEdges = contextEdges.put(invokingState, target);
	}

	public Map<Integer, DFAState> getContextEdgeMap() {
		Map<Integer, DFAState> map = contextEdges.toMap();
		if (map.containsKey(-1)) {
			if (map.size() == 1) {
				return Collections.singletonMap(PredictionContext.EMPTY_FULL_STATE_KEY, map.get(-1));
			}
			else {
				try {
					map.put(PredictionContext.EMPTY_FULL_STATE_KEY, map.remove(-1));
				} catch (UnsupportedOperationException ex) {
					// handles read only, non-singleton maps
					map = new LinkedHashMap<Integer, DFAState>(map);
					map.put(PredictionContext.EMPTY_FULL_STATE_KEY, map.remove(-1));
				}
			}
		}

		return map;
	}

	@Override
	public int hashCode() {
		int hash = MurmurHash.initialize(7);
		hash = MurmurHash.update(hash, configs.hashCode());
		hash = MurmurHash.finish(hash, 1);
		return hash;
	}

	/**
	 * Two {@link DFAState} instances are equal if their ATN configuration sets
	 * are the same. This method is used to see if a state already exists.
	 *
	 * <p>Because the number of alternatives and number of ATN configurations are
	 * finite, there is a finite number of DFA states that can be processed.
	 * This is necessary to show that the algorithm terminates.</p>
	 *
	 * <p>Cannot test the DFA state numbers here because in
	 * {@link ParserATNSimulator#addDFAState} we need to know if any other state
	 * exists that has this exact set of ATN configurations. The
	 * {@link #stateNumber} is irrelevant.</p>
	 */
	@Override
	public boolean equals(Object o) {
		// compare set of ATN configurations in this set with other
		if ( this==o ) return true;

		if (!(o instanceof DFAState)) {
			return false;
		}

		DFAState other = (DFAState)o;
		boolean sameSet = this.configs.equals(other.configs);
//		System.out.println("DFAState.equals: "+configs+(sameSet?"==":"!=")+other.configs);
		return sameSet;
	}

	@Override
	public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(stateNumber).append(":").append(configs);
        if ( isAcceptState ) {
            buf.append("=>");
            if ( predicates!=null ) {
                buf.append(Arrays.toString(predicates));
            }
            else {
                buf.append(prediction);
            }
        }
		return buf.toString();
	}
}
