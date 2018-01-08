/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class LL1Analyzer {
	/** Special value added to the lookahead sets to indicate that we hit
	 *  a predicate during analysis if {@code seeThruPreds==false}.
	 */
	public static final int HIT_PRED = Token.INVALID_TYPE;

	public final ATN atn;

	public LL1Analyzer(ATN atn) { this.atn = atn; }

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
	public IntervalSet[] getDecisionLookahead(ATNState s) {
//		System.out.println("LOOK("+s.stateNumber+")");
		if ( s==null ) {
			return null;
		}

		IntervalSet[] look = new IntervalSet[s.getNumberOfTransitions()];
		for (int alt = 0; alt < s.getNumberOfTransitions(); alt++) {
			look[alt] = new IntervalSet();
			Set<ATNConfig> lookBusy = new HashSet<ATNConfig>();
			boolean seeThruPreds = false; // fail to get lookahead upon pred
			_LOOK(s.transition(alt).target, null, PredictionContext.EMPTY,
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
   	public IntervalSet LOOK(ATNState s, RuleContext ctx) {
		return LOOK(s, null, ctx);
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
	 * @param stopState the ATN state to stop at. This can be a
	 * {@link BlockEndState} to detect epsilon paths through a closure.
	 * @param ctx the complete parser context, or {@code null} if the context
	 * should be ignored
	 *
	 * @return The set of tokens that can follow {@code s} in the ATN in the
	 * specified {@code ctx}.
	 */

   	public IntervalSet LOOK(ATNState s, ATNState stopState, RuleContext ctx) {
   		IntervalSet r = new IntervalSet();
		boolean seeThruPreds = true; // ignore preds; get all lookahead
		PredictionContext lookContext = ctx != null ? PredictionContext.fromRuleContext(s.atn, ctx) : null;
   		_LOOK(s, stopState, lookContext,
			  r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, true);
   		return r;
   	}

	/**
	 * Compute set of tokens that can follow {@code s} in the ATN in the
	 * specified {@code ctx}.
	 *
	 * <p>If {@code ctx} is {@code null} and {@code stopState} or the end of the
	 * rule containing {@code s} is reached, {@link Token#EPSILON} is added to
	 * the result set. If {@code ctx} is not {@code null} and {@code addEOF} is
	 * {@code true} and {@code stopState} or the end of the outermost rule is
	 * reached, {@link Token#EOF} is added to the result set.</p>
	 *
	 * @param s the ATN state.
	 * @param stopState the ATN state to stop at. This can be a
	 * {@link BlockEndState} to detect epsilon paths through a closure.
	 * @param ctx The outer context, or {@code null} if the outer context should
	 * not be used.
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
	 * is {@code null}.
	 */
    protected void _LOOK(ATNState s,
						 ATNState stopState,
						 PredictionContext ctx,
						 IntervalSet look,
                         Set<ATNConfig> lookBusy,
						 BitSet calledRuleStack,
						 boolean seeThruPreds, boolean addEOF)
	{
//		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
        ATNConfig c = new ATNConfig(s, 0, ctx);
        if ( !lookBusy.add(c) ) return;

		if (s == stopState) {
			if (ctx == null) {
				look.add(Token.EPSILON);
				return;
			}
			else if (ctx.isEmpty() && addEOF) {
				look.add(Token.EOF);
				return;
			}
		}

        if ( s instanceof RuleStopState ) {
            if ( ctx==null ) {
                look.add(Token.EPSILON);
                return;
            }
            else if (ctx.isEmpty() && addEOF) {
				look.add(Token.EOF);
				return;
			}

			if ( ctx != PredictionContext.EMPTY ) {
				// run thru all possible stack tops in ctx
				boolean removed = calledRuleStack.get(s.ruleIndex);
				try {
					calledRuleStack.clear(s.ruleIndex);
					for (int i = 0; i < ctx.size(); i++) {
						ATNState returnState = atn.states.get(ctx.getReturnState(i));
//					    System.out.println("popping back to "+retState);
						_LOOK(returnState, stopState, ctx.getParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
					}
				}
				finally {
					if (removed) {
						calledRuleStack.set(s.ruleIndex);
					}
				}
				return;
			}
        }

        int n = s.getNumberOfTransitions();
        for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t.getClass() == RuleTransition.class ) {
				if (calledRuleStack.get(((RuleTransition)t).target.ruleIndex)) {
					continue;
				}

				PredictionContext newContext =
					SingletonPredictionContext.create(ctx, ((RuleTransition)t).followState.stateNumber);

				try {
					calledRuleStack.set(((RuleTransition)t).target.ruleIndex);
					_LOOK(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
				}
				finally {
					calledRuleStack.clear(((RuleTransition)t).target.ruleIndex);
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
