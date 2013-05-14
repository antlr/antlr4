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

import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class LL1Analyzer {
	/** Special value added to the lookahead sets to indicate that we hit
	 *  a predicate during analysis if seeThruPreds==false.
	 */
	public static final int HIT_PRED = Token.INVALID_TYPE;

	@NotNull
	public final ATN atn;

	public LL1Analyzer(@NotNull ATN atn) { this.atn = atn; }

	/**
	 * From an ATN state, {@code s}, find the set of all labels reachable from
	 * {@code s} at depth k. Only for DecisionStates.
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
			_LOOK(s.transition(alt).target,
				  PredictionContext.EMPTY,
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
	 * Get lookahead, using {@code ctx} if we reach end of rule. If {@code ctx}
	 * is {@code null} or {@link RuleContext#EMPTY EMPTY}, don't chase FOLLOW.
	 * If {@code ctx} is {@code null}, {@link Token#EPSILON EPSILON} is in set
	 * if we can reach end of rule. If {@code ctx} is
	 * {@link RuleContext#EMPTY EMPTY}, {@link IntStream#EOF EOF} is in set if
	 * we can reach end of rule.
	 */
    @NotNull
   	public IntervalSet LOOK(@NotNull ATNState s, @Nullable RuleContext ctx) {
   		IntervalSet r = new IntervalSet();
		boolean seeThruPreds = true; // ignore preds; get all lookahead
		PredictionContext lookContext = ctx != null ? PredictionContext.fromRuleContext(s.atn, ctx) : null;
   		_LOOK(s, lookContext,
			  r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, true);
   		return r;
   	}

    /** Compute set of tokens that can come next. If the {@code ctx} is {@link PredictionContext#EMPTY},
     *  then we don't go anywhere when we hit the end of the rule. We have
     *  the correct set.  If {@code ctx} is null, that means that we did not want
     *  any tokens following this rule--just the tokens that could be found within this
     *  rule. Add {@link Token#EPSILON} to the set indicating we reached the end of the ruled out having
     *  to match a token.
     */
    protected void _LOOK(@NotNull ATNState s, @Nullable PredictionContext ctx,
						 @NotNull IntervalSet look,
                         @NotNull Set<ATNConfig> lookBusy,
						 @NotNull BitSet calledRuleStack,
						 boolean seeThruPreds, boolean addEOF)
	{
//		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
        ATNConfig c = new ATNConfig(s, 0, ctx);
        if ( !lookBusy.add(c) ) return;

        if ( s instanceof RuleStopState ) {
            if ( ctx==null ) {
                look.add(Token.EPSILON);
                return;
            } else if (ctx.isEmpty() && addEOF) {
				look.add(Token.EOF);
				return;
			}

			if ( ctx != PredictionContext.EMPTY ) {
				// run thru all possible stack tops in ctx
				for (SingletonPredictionContext p : ctx) {
					ATNState returnState = atn.states.get(p.returnState);
//					System.out.println("popping back to "+retState);

					boolean removed = calledRuleStack.get(returnState.ruleIndex);
					try {
						calledRuleStack.clear(returnState.ruleIndex);
						_LOOK(returnState, p.parent, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
					}
					finally {
						if (removed) {
							calledRuleStack.set(returnState.ruleIndex);
						}
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
					_LOOK(t.target, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
				}
				finally {
					calledRuleStack.clear(((RuleTransition)t).target.ruleIndex);
				}
			}
			else if ( t instanceof PredicateTransition ) {
				if ( seeThruPreds ) {
					_LOOK(t.target, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
				}
				else {
					look.add(HIT_PRED);
				}
			}
			else if ( t.isEpsilon() ) {
				_LOOK(t.target, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
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
