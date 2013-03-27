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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** */
public class ATN {
	public static final int INVALID_ALT_NUMBER = 0;

	@NotNull
	public final List<ATNState> states = new ArrayList<ATNState>();

	/** Each subrule/rule is a decision point and we must track them so we
	 *  can go back later and build DFA predictors for them.  This includes
	 *  all the rules, subrules, optional blocks, ()+, ()* etc...
	 */
	@NotNull
	public final List<DecisionState> decisionToState = new ArrayList<DecisionState>();

	/**
	 * Maps from rule index to starting state number.
	 */
	public RuleStartState[] ruleToStartState;

	/**
	 * Maps from rule index to stop state number.
	 */
	public RuleStopState[] ruleToStopState;

	@NotNull
	public final Map<String, TokensStartState> modeNameToStartState =
		new LinkedHashMap<String, TokensStartState>();

	/**
	 * The type of the ATN.
	 */
	public final ATNType grammarType;

	/**
	 * The maximum value for any symbol recognized by a transition in the ATN.
	 */
	public final int maxTokenType;

	/**
	 * For lexer ATNs, this maps the rule index to the resulting token type.
	 * <p/>
	 * This is {@code null} for parser ATNs.
	 */
	public int[] ruleToTokenType;

	/**
	 * For lexer ATNs, this maps the rule index to the action which should be
	 * executed following a match.
	 * <p/>
	 * This is {@code null} for parser ATNs.
	 */
	public int[] ruleToActionIndex;

	@NotNull
	public final List<TokensStartState> modeToStartState = new ArrayList<TokensStartState>();

	/** Used for runtime deserialization of ATNs from strings */
	public ATN(@NotNull ATNType grammarType, int maxTokenType) {
		this.grammarType = grammarType;
		this.maxTokenType = maxTokenType;
	}

	/** Compute the set of valid tokens that can occur starting in state {@code s}.
	 *  If {@code ctx} is null, the set of tokens will not include what can follow
	 *  the rule surrounding {@code s}. In other words, the set will be
	 *  restricted to tokens reachable staying within {@code s}'s rule.
	 */
	@NotNull
	public IntervalSet nextTokens(ATNState s, RuleContext ctx) {
		LL1Analyzer anal = new LL1Analyzer(this);
		IntervalSet next = anal.LOOK(s, ctx);
		return next;
	}

    /**
	 * Compute the set of valid tokens that can occur starting in {@code s} and
	 * staying in same rule. {@link Token#EPSILON} is in set if we reach end of
	 * rule.
     */
	@NotNull
    public IntervalSet nextTokens(@NotNull ATNState s) {
        if ( s.nextTokenWithinRule != null ) return s.nextTokenWithinRule;
        s.nextTokenWithinRule = nextTokens(s, null);
        s.nextTokenWithinRule.setReadonly(true);
        return s.nextTokenWithinRule;
    }

	public void addState(@Nullable ATNState state) {
		if (state != null) {
			state.atn = this;
			state.stateNumber = states.size();
		}

		states.add(state);
	}

	public void removeState(@NotNull ATNState state) {
		states.set(state.stateNumber, null); // just free mem, don't shift states in list
	}

	public int defineDecisionState(@NotNull DecisionState s) {
		decisionToState.add(s);
		s.decision = decisionToState.size()-1;
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

	/**
	 * Computes the set of input symbols which could follow ATN state number
	 * {@code stateNumber} in the specified full {@code context}. This method
	 * considers the complete parser context, but does not evaluate semantic
	 * predicates (i.e. all predicates encountered during the calculation are
	 * assumed true). If a path in the ATN exists from the starting state to the
	 * {@link RuleStopState} of the outermost context without matching any
	 * symbols, {@link Token#EOF} is added to the returned set.
	 * <p/>
	 * If {@code context} is {@code null}, it is treated as
	 * {@link ParserRuleContext#EMPTY}.
	 *
	 * @param stateNumber the ATN state number
	 * @param context the full parse context
	 * @return The set of potentially valid input symbols which could follow the
	 * specified state in the specified context.
	 * @throws IllegalArgumentException if the ATN does not contain a state with
	 * number {@code stateNumber}
	 */
	@NotNull
	public IntervalSet getExpectedTokens(int stateNumber, @Nullable RuleContext context) {
		if (stateNumber < 0 || stateNumber >= states.size()) {
			throw new IllegalArgumentException("Invalid state number.");
		}

		RuleContext ctx = context;
		ATNState s = states.get(stateNumber);
		IntervalSet following = nextTokens(s);
		if (!following.contains(Token.EPSILON)) {
			return following;
		}

		IntervalSet expected = new IntervalSet();
		expected.addAll(following);
		expected.remove(Token.EPSILON);
		while (ctx != null && ctx.invokingState >= 0 && following.contains(Token.EPSILON)) {
			ATNState invokingState = states.get(ctx.invokingState);
			RuleTransition rt = (RuleTransition)invokingState.transition(0);
			following = nextTokens(rt.followState);
			expected.addAll(following);
			expected.remove(Token.EPSILON);
			ctx = ctx.parent;
		}

		if (following.contains(Token.EPSILON)) {
			expected.add(Token.EOF);
		}

		return expected;
	}
}
