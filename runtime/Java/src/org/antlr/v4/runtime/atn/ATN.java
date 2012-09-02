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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Args;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** */
public class ATN {
	public static final int INVALID_ALT_NUMBER = 0;

	public static final int PARSER = 1;
	public static final int LEXER = 2;

	@NotNull
	public final List<ATNState> states = new ArrayList<ATNState>();

	/** Each subrule/rule is a decision point and we must track them so we
	 *  can go back later and build DFA predictors for them.  This includes
	 *  all the rules, subrules, optional blocks, ()+, ()* etc...
	 */
	@NotNull
	public final List<DecisionState> decisionToState = new ArrayList<DecisionState>();

	public RuleStartState[] ruleToStartState;
	public RuleStopState[] ruleToStopState;

	@NotNull
	public final Map<String, TokensStartState> modeNameToStartState =
		new LinkedHashMap<String, TokensStartState>();

	// runtime for parsers, lexers
	public int grammarType; // ATN.LEXER, ...
	public int maxTokenType;

	// runtime for lexer only
	public int[] ruleToTokenType;
	public int[] ruleToActionIndex;
	@NotNull
	public final List<TokensStartState> modeToStartState = new ArrayList<TokensStartState>();

	/** used during construction from grammar AST */
	int stateNumber = 0;

	private final ConcurrentMap<PredictionContext, PredictionContext> contextCache =
		new ConcurrentHashMap<PredictionContext, PredictionContext>();

	@NotNull
	public DFA[] decisionToDFA = new DFA[0];
	@NotNull
	public DFA[] modeToDFA = new DFA[0];

	protected final ConcurrentMap<Integer, Integer> LL1Table = new ConcurrentHashMap<Integer, Integer>();

	/** Used for runtime deserialization of ATNs from strings */
	public ATN() { }

	public final void clearDFA() {
		decisionToDFA = new DFA[decisionToState.size()];
		for (int i = 0; i < decisionToDFA.length; i++) {
			decisionToDFA[i] = new DFA(decisionToState.get(i), i);
		}

		modeToDFA = new DFA[modeToStartState.size()];
		for (int i = 0; i < modeToDFA.length; i++) {
			modeToDFA[i] = new DFA(modeToStartState.get(i));
		}

		contextCache.clear();
		LL1Table.clear();
	}

	public PredictionContext getCachedContext(PredictionContext context) {
		return PredictionContext.getCachedContext(context, contextCache, new IdentityHashMap<PredictionContext, PredictionContext>());
	}

	public final DFA[] getDecisionToDFA() {
		assert decisionToDFA != null && decisionToDFA.length == decisionToState.size();
		return decisionToDFA;
	}

	/** Compute the set of valid tokens that can occur starting in s.
	 *  If ctx is {@link PredictionContext#EMPTY_LOCAL}, the set of tokens will not include what can follow
	 *  the rule surrounding s. In other words, the set will be
	 *  restricted to tokens reachable staying within s's rule.
	 */
	public IntervalSet nextTokens(ATNState s, @NotNull PredictionContext ctx) {
		Args.notNull("ctx", ctx);
		LL1Analyzer anal = new LL1Analyzer(this);
		IntervalSet next = anal.LOOK(s, ctx);
		return next;
	}

    /** Compute the set of valid tokens that can occur starting in s and staying in same rule.
     *  EPSILON is in set if we reach end of rule.
     */
    public IntervalSet nextTokens(ATNState s) {
        if ( s.nextTokenWithinRule != null ) return s.nextTokenWithinRule;
        s.nextTokenWithinRule = nextTokens(s, PredictionContext.EMPTY_LOCAL);
        s.nextTokenWithinRule.setReadonly(true);
        return s.nextTokenWithinRule;
    }

	public void addState(@Nullable ATNState state) {
		if ( state==null ) { states.add(null); stateNumber++; return; }
		state.atn = this;
		states.add(state);
		state.stateNumber = stateNumber++;
	}

	public void removeState(@NotNull ATNState state) {
		states.set(state.stateNumber, null); // just free mem, don't shift states in list
	}

	public void defineMode(@NotNull String name, @NotNull TokensStartState s) {
		modeNameToStartState.put(name, s);
		modeToStartState.add(s);
		modeToDFA = Arrays.copyOf(modeToDFA, modeToStartState.size());
		modeToDFA[modeToDFA.length - 1] = new DFA(s);
		defineDecisionState(s);
	}

	public int defineDecisionState(@NotNull DecisionState s) {
		decisionToState.add(s);
		s.decision = decisionToState.size()-1;
		decisionToDFA = Arrays.copyOf(decisionToDFA, decisionToState.size());
		decisionToDFA[decisionToDFA.length - 1] = new DFA(s, s.decision);
		return s.decision;
	}

    public DecisionState getDecisionState(int decision) {
        if ( !decisionToState.isEmpty() ) {
            return decisionToState.get(decision);
        }
        return null;
    }

	public int getNumberOfDecisions() {
		return decisionToState.size();
	}
}
