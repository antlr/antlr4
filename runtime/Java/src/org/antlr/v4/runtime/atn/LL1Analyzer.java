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

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Args;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class LL1Analyzer {
	/** Special value added to the lookahead sets to indicate that we hit
	 *  a predicate during analysis if {@code seeThruPreds==false}.
	 */
	public static final int HIT_PRED = Token.INVALID_TYPE;

	@NotNull
	public final ATN atn;

	public LL1Analyzer(@NotNull ATN atn) { this.atn = atn; }

	/**
	 * Calculates the SLL(1) expected lookahead set for each outgoing transition
	 * of an {@link ATNState}. The returned array has one element for each
	 * outgoing transition in {@code s}. If the closure from transition
	 * <em>i</em> leads to a semantic predicate before matching a symbol, the
	 * element at index <em>i</em> of the result will be {@code null}.
	 *
	 * @param s the ATN state
	 * @return the expected symbols for each outgoing transition of {@code s}.
	 */
	@Nullable
	public IntervalSet[] getDecisionLookahead(@Nullable ATNState s) {
//		System.out.println("LOOK("+s.stateNumber+")");
		if ( s==null ) {
			return null;
		}

		IntervalSet[] look = new IntervalSet[s.getNumberOfTransitions()];
		for (int alt = 0; alt < s.getNumberOfTransitions(); alt++) {
			look[alt] = new IntervalSet();
			Set<ATNConfig> lookBusy = new HashSet<ATNConfig>();
			boolean seeThruPreds = false; // fail to get lookahead upon pred
			_LOOK(s.transition(alt).target, null, PredictionContext.EMPTY_LOCAL,
				  look[alt], lookBusy, new BitSet(), seeThruPreds, false);
			// Wipe out lookahead for this alternative if we found nothing
			// or we had a predicate when we !seeThruPreds
			if ( look[alt].size()==0 || look[alt].contains(HIT_PRED) ) {
				look[alt] = null;
			}
		}
		return look;
	}

	/**
	 * Compute set of tokens that can follow {@code s} in the ATN in the
	 * specified {@code ctx}.
	 *
	 * <p>If {@code ctx} is {@code null} and the end of the rule containing
	 * {@code s} is reached, {@link Token#EPSILON} is added to the result set.
	 * If {@code ctx} is not {@code null} and the end of the outermost rule is
	 * reached, {@link Token#EOF} is added to the result set.</p>
	 *
	 * @param s the ATN state
	 * @param ctx the complete parser context, or {@code null} if the context
	 * should be ignored
	 *
	 * @return The set of tokens that can follow {@code s} in the ATN in the
	 * specified {@code ctx}.
	 */
    @NotNull
   	public IntervalSet LOOK(@NotNull ATNState s, @NotNull PredictionContext ctx) {
		return LOOK(s, s.atn.ruleToStopState[s.ruleIndex], ctx);
   	}

	/**
	 * Compute set of tokens that can follow {@code s} in the ATN in the
	 * specified {@code ctx}.
	 *
	 * <p>If {@code ctx} is {@code null} and the end of the rule containing
	 * {@code s} is reached, {@link Token#EPSILON} is added to the result set.
	 * If {@code ctx} is not {@code PredictionContext#EMPTY_LOCAL} and the end of the outermost rule is
	 * reached, {@link Token#EOF} is added to the result set.</p>
	 *
	 * @param s the ATN state
	 * @param stopState the ATN state to stop at. This can be a
	 * {@link BlockEndState} to detect epsilon paths through a closure.
	 * @param ctx the complete parser context, or {@code null} if the context
	 * should be ignored
	 *
	 * @return The set of tokens that can follow {@code s} in the ATN in the
	 * specified {@code ctx}.
	 */
    @NotNull
   	public IntervalSet LOOK(@NotNull ATNState s, @Nullable ATNState stopState, @NotNull PredictionContext ctx) {
   		IntervalSet r = new IntervalSet();
		final boolean seeThruPreds = true; // ignore preds; get all lookahead
		final boolean addEOF = true;
   		_LOOK(s, stopState, ctx, r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, addEOF);
   		return r;
   	}

	/**
	 * Compute set of tokens that can follow {@code s} in the ATN in the
	 * specified {@code ctx}.
	 * <p/>
	 * If {@code ctx} is {@link PredictionContext#EMPTY_LOCAL} and
	 * {@code stopState} or the end of the rule containing {@code s} is reached,
	 * {@link Token#EPSILON} is added to the result set. If {@code ctx} is not
	 * {@link PredictionContext#EMPTY_LOCAL} and {@code addEOF} is {@code true}
	 * and {@code stopState} or the end of the outermost rule is reached,
	 * {@link Token#EOF} is added to the result set.
	 *
	 * @param s the ATN state.
	 * @param stopState the ATN state to stop at. This can be a
	 * {@link BlockEndState} to detect epsilon paths through a closure.
	 * @param ctx The outer context, or {@link PredictionContext#EMPTY_LOCAL} if
	 * the outer context should not be used.
	 * @param look The result lookahead set.
	 * @param lookBusy A set used for preventing epsilon closures in the ATN
	 * from causing a stack overflow. Outside code should pass
	 * {@code new HashSet<ATNConfig>} for this argument.
	 * @param calledRuleStack A set used for preventing left recursion in the
	 * ATN from causing a stack overflow. Outside code should pass
	 * {@code new BitSet()} for this argument.
	 * @param seeThruPreds {@code true} to true semantic predicates as
	 * implicitly {@code true} and "see through them", otherwise {@code false}
	 * to treat semantic predicates as opaque and add {@link #HIT_PRED} to the
	 * result if one is encountered.
	 * @param addEOF Add {@link Token#EOF} to the result if the end of the
	 * outermost context is reached. This parameter has no effect if {@code ctx}
	 * is {@link PredictionContext#EMPTY_LOCAL}.
	 */
    protected void _LOOK(@NotNull ATNState s,
						 @Nullable ATNState stopState,
						 @NotNull PredictionContext ctx,
						 @NotNull IntervalSet look,
                         @NotNull Set<ATNConfig> lookBusy,
						 @NotNull BitSet calledRuleStack,
						 boolean seeThruPreds, boolean addEOF)
	{
//		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
        ATNConfig c = ATNConfig.create(s, 0, ctx);
        if ( !lookBusy.add(c) ) return;

		if (s == stopState) {
			if (PredictionContext.isEmptyLocal(ctx)) {
				look.add(Token.EPSILON);
				return;
			} else if (ctx.isEmpty()) {
				if (addEOF) {
					look.add(Token.EOF);
				}

				return;
			}
		}

        if ( s instanceof RuleStopState ) {
            if (ctx.isEmpty() && !PredictionContext.isEmptyLocal(ctx)) {
				if (addEOF) {
					look.add(Token.EOF);
				}

				return;
			}

			boolean removed = calledRuleStack.get(s.ruleIndex);
			try {
				calledRuleStack.clear(s.ruleIndex);
				for (int i = 0; i < ctx.size(); i++) {
					if (ctx.getReturnState(i) == PredictionContext.EMPTY_FULL_STATE_KEY) {
						continue;
					}

					ATNState returnState = atn.states.get(ctx.getReturnState(i));
//					System.out.println("popping back to "+retState);
					_LOOK(returnState, stopState, ctx.getParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
				}
			}
			finally {
				if (removed) {
					calledRuleStack.set(s.ruleIndex);
				}
			}
        }

        int n = s.getNumberOfTransitions();
        for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition ) {
				RuleTransition ruleTransition = (RuleTransition)t;
				if (calledRuleStack.get(ruleTransition.ruleIndex)) {
					continue;
				}

				PredictionContext newContext = ctx.getChild(ruleTransition.followState.stateNumber);

				try {
					calledRuleStack.set(ruleTransition.ruleIndex);
					_LOOK(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
				}
				finally {
					calledRuleStack.clear(ruleTransition.ruleIndex);
				}
			}
			else if ( t instanceof AbstractPredicateTransition ) {
				if ( seeThruPreds ) {
					_LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
				}
				else {
					look.add(HIT_PRED);
				}
			}
			else if ( t.isEpsilon() ) {
				_LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
			}
			else if ( t.getClass() == WildcardTransition.class ) {
				look.addAll( IntervalSet.of(Token.MIN_USER_TOKEN_TYPE, atn.maxTokenType) );
			}
			else {
//				System.out.println("adding "+ t);
				IntervalSet set = t.label();
				if (set != null) {
					if (t instanceof NotSetTransition) {
						set = set.complement(IntervalSet.of(Token.MIN_USER_TOKEN_TYPE, atn.maxTokenType));
					}
					look.addAll(set);
				}
			}
		}
	}
}
