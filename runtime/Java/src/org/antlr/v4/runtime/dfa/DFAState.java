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

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A DFA state represents a set of possible ATN configurations.
 *  As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
 *  to keep track of all possible states the ATN can be in after
 *  reading each input symbol.  That is to say, after reading
 *  input a1a2..an, the DFA is in a state that represents the
 *  subset T of the states of the ATN that are reachable from the
 *  ATN's start state along some path labeled a1a2..an."
 *  In conventional NFA->DFA conversion, therefore, the subset T
 *  would be a bitset representing the set of states the
 *  ATN could be in.  We need to track the alt predicted by each
 *  state as well, however.  More importantly, we need to maintain
 *  a stack of states, tracking the closure operations as they
 *  jump from rule to rule, emulating rule invocations (method calls).
 *  I have to add a stack to simulate the proper lookahead sequences for
 *  the underlying LL grammar from which the ATN was derived.
 *
 *  I use a set of ATNConfig objects not simple states.  An ATNConfig
 *  is both a state (ala normal conversion) and a RuleContext describing
 *  the chain of rules (if any) followed to arrive at that state.
 *
 *  A DFA state may have multiple references to a particular state,
 *  but with different ATN contexts (with same or different alts)
 *  meaning that state was reached via a different set of rule invocations.
 */
public class DFAState {
	public int stateNumber = -1;

	/** The set of ATN configurations (state,alt,context) for this DFA state */
	@Nullable
	public ATNConfigSet configset;

	/** edges[symbol] points to target of symbol */
	@Nullable
	private EdgeMap<DFAState> edges;
	private final int minSymbol;
	private final int maxSymbol;

	public boolean isAcceptState = false;

	public int prediction; // if accept state, what ttype do we match? is "else" clause if predicated

	public int lexerRuleIndex = -1;		// if accept, exec action in what rule?
	public int lexerActionIndex = -1;	// if accept, exec what action?

	// todo: rename as unique?
//	public boolean complete; // all alts predict "prediction"
	public boolean isCtxSensitive;

	/** These keys for these edges are the top level element of the global context. */
	@Nullable
	private EdgeMap<DFAState> contextEdges;

	/** Symbols in this set require a global context transition before matching an input symbol. */
	@Nullable
	public Set<Integer> contextSymbols;

	/** DFA accept states use predicates in two situations:
	 *  disambiguating and validating predicates. If an accept state
	 *  predicts more than one alternative, It's ambiguous and we
	 *  try to resolve with predicates.  Disambiguating predicates
	 *  are evaluated when there is a unique prediction for this accept state.
	 *  This array tracks the list of predicates to test in either case;
	 *  there will only be one in the case of a disambiguating predicate.
	 *
	 *  Because there could be 20 alternatives for a decision,
	 *  we don't want to map alt to predicates; we might have to walk
	 *  all of the early alternatives just to get to the predicates.
	 *
	 *  If this is null then there are no predicates involved in
	 *  decision-making for this state.
	 *
	 *  As an example, we might have:
	 *
	 *  predicates = [(p,3), (q,4), (null, 2)]
	 *
	 *  This means that there are 2 predicates for 3 ambiguous alternatives.
	 *  If the first 2 predicates fail, then we default to the last
	 *  PredPrediction pair, which predicts alt 2. This comes from:
	 *
	 *  r : B
     *    |      A
	 *    | {p}? A
	 *    | {q}? A
	 *    ;
	 *
	 *  This is used only when isCtxSensitive = false;
	 */
	@Nullable
	public List<PredPrediction> predicates;

	/** Map a predicate to a predicted alternative */
	public static class PredPrediction {
		public SemanticContext pred;
		public int alt;
		public PredPrediction(SemanticContext pred, int alt) {
			this.alt = alt;
			this.pred = pred;
		}
		@Override
		public String toString() {
			return "("+pred+", "+alt+ ")";
		}
	}

	public DFAState(ATNConfigSet configs, int minSymbol, int maxSymbol) {
		this.configset = configs;
		this.minSymbol = minSymbol;
		this.maxSymbol = maxSymbol;
	}

	public void setContextSensitive(ATN atn) {
		assert !configset.isOutermostConfigSet();

		if (!isCtxSensitive) {
			isCtxSensitive = true;
			contextEdges = new SingletonEdgeMap<DFAState>(-1, atn.states.size() - 1);
			contextSymbols = new HashSet<Integer>();
			if (edges != null) {
				edges = edges.clear();
			}
		}
	}

	public DFAState getTarget(int symbol) {
		if (edges == null) {
			return null;
		}

		return edges.get(symbol);
	}

	public void setTarget(int symbol, DFAState target) {
		if (edges == null) {
			edges = new SingletonEdgeMap<DFAState>(minSymbol, maxSymbol);
		}

		edges = edges.put(symbol, target);
	}

	public Map<Integer, DFAState> getEdgeMap() {
		if (edges == null) {
			return Collections.emptyMap();
		}

		return edges.toMap();
	}

	public DFAState getContextTarget(int invokingState) {
		if (contextEdges == null) {
			return null;
		}

		if (invokingState == PredictionContext.EMPTY_STATE_KEY) {
			invokingState = -1;
		}

		return contextEdges.get(invokingState);
	}

	public void setContextTarget(int invokingState, DFAState target) {
		if (contextEdges == null) {
			throw new IllegalStateException("The state is not context sensitive.");
		}

		if (invokingState == PredictionContext.EMPTY_STATE_KEY) {
			invokingState = -1;
		}

		contextEdges = contextEdges.put(invokingState, target);
	}

	public Map<Integer, DFAState> getContextEdgeMap() {
		if (contextEdges == null) {
			return Collections.emptyMap();
		}

		Map<Integer, DFAState> map = contextEdges.toMap();
		if (map.containsKey(-1)) {
			if (map.size() == 1) {
				return Collections.singletonMap(PredictionContext.EMPTY_STATE_KEY, map.get(-1));
			}
			else {
				try {
					map.put(PredictionContext.EMPTY_STATE_KEY, map.remove(-1));
				} catch (UnsupportedOperationException ex) {
					// handles read only, non-singleton maps
					map = new LinkedHashMap<Integer, DFAState>(map);
					map.put(PredictionContext.EMPTY_STATE_KEY, map.remove(-1));
				}
			}
		}

		return map;
	}

	/** Get the set of all alts mentioned by all ATN configurations in this
	 *  DFA state.
	 */
	public Set<Integer> getAltSet() {
		// TODO (sam): what to do when configs==null?
		Set<Integer> alts = new HashSet<Integer>();
		for (ATNConfig c : configset) {
			alts.add(c.alt);
		}
		if ( alts.isEmpty() ) return null;
		return alts;
	}

	/*
	public void setContextSensitivePrediction(RuleContext ctx, int predictedAlt) {
		isCtxSensitive = true;
		if ( ctxToPrediction==null ) {
			ctxToPrediction = new LinkedHashMap<RuleContext, Integer>();
		}
		ctxToPrediction.put(ctx, predictedAlt);
	}
	*/

	/** A decent hash for a DFA state is the sum of the ATN state/alt pairs. */
	@Override
	public int hashCode() {
		if (configset == null) {
			return 1;
		}

		return configset.hashCode();
	}

	/** Two DFAStates are equal if their ATN configuration sets are the
	 *  same. This method is used to see if a DFA state already exists.
	 *
	 *  Because the number of alternatives and number of ATN configurations are
	 *  finite, there is a finite number of DFA states that can be processed.
	 *  This is necessary to show that the algorithm terminates.
	 *
	 *  Cannot test the DFA state numbers here because in DFA.addState we need
	 *  to know if any other state exists that has this exact set of ATN
	 *  configurations.  The DFAState state number is irrelevant.
	 */
	@Override
	public boolean equals(Object o) {
		// compare set of ATN configurations in this set with other
		if ( this==o ) return true;
		DFAState other = (DFAState)o;
		// TODO (sam): what to do when configs==null?
		boolean sameSet = this.configset.equals(other.configset);
//		System.out.println("DFAState.equals: "+configs+(sameSet?"==":"!=")+other.configs);
		return sameSet;
	}

	@Override
	public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(stateNumber + ":" + configset);
        if ( isAcceptState ) {
            buf.append("=>");
            if ( predicates!=null ) {
                buf.append(predicates);
            }
            else {
                buf.append(prediction);
            }
        }
		return buf.toString();
	}
}
