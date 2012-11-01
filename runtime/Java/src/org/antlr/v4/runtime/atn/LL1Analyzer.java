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

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Args;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

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

	/** From an ATN state, s, find the set of all labels reachable from s at
	 *  depth k.  Only for DecisionStates.
	 */
	@Nullable
	public IntervalSet[] getDecisionLookahead(@Nullable ATNState s) {
//		System.out.println("LOOK("+s.stateNumber+")");
		if ( s==null ) return null;
		IntervalSet[] look = new IntervalSet[s.getNumberOfTransitions()+1];
		for (int alt=1; alt<=s.getNumberOfTransitions(); alt++) {
			look[alt] = new IntervalSet();
			Set<ATNConfig> lookBusy = new HashSet<ATNConfig>();
			boolean seeThruPreds = false; // fail to get lookahead upon pred
			_LOOK(s.transition(alt - 1).target,
				  PredictionContext.EMPTY_FULL,
				  look[alt], lookBusy, seeThruPreds);
			// Wipe out lookahead for this alternative if we found nothing
			// or we had a predicate when we !seeThruPreds
			if ( look[alt].size()==0 || look[alt].contains(HIT_PRED) ) {
				look[alt] = null;
			}
		}
		return look;
	}

    /** Get lookahead, using {@code ctx} if we reach end of rule. If {@code ctx}
	 *  is {@link PredictionContext#EMPTY_FULL}, don't chase FOLLOW. If {@code ctx}
	 *  is {@link PredictionContext#EMPTY_LOCAL}, EPSILON is in set if we can reach
	 * end of rule.
     */
    @NotNull
   	public IntervalSet LOOK(@NotNull ATNState s, @NotNull PredictionContext ctx) {
		Args.notNull("ctx", ctx);
   		IntervalSet r = new IntervalSet();
		boolean seeThruPreds = true; // ignore preds; get all lookahead
   		_LOOK(s, ctx, r, new HashSet<ATNConfig>(), seeThruPreds);
   		return r;
   	}

    /** Compute set of tokens that can come next. If the context is {@link PredictionContext#EMPTY_FULL},
     *  then we don't go anywhere when we hit the end of the rule. We have
     *  the correct set.  If the context is {@link PredictionContext#EMPTY_LOCAL},
	 *  that means that we did not want any tokens following this rule--just the
	 *  tokens that could be found within this rule. Add EPSILON to the set
	 *  indicating we reached the end of the ruled out having to match a token.
     */
    protected void _LOOK(@NotNull ATNState s, @NotNull PredictionContext ctx,
						 @NotNull IntervalSet look,
                         @NotNull Set<ATNConfig> lookBusy,
						 boolean seeThruPreds)
	{
//		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
        ATNConfig c = ATNConfig.create(s, 0, ctx);
        if ( !lookBusy.add(c) ) return;

        if ( s instanceof RuleStopState ) {
            if ( PredictionContext.isEmptyLocal(ctx) ) {
                look.add(Token.EPSILON);
                return;
            }
			for (int i = 0; i < ctx.size(); i++) {
				if ( ctx.getReturnState(i)!=PredictionContext.EMPTY_FULL_STATE_KEY ) {
					ATNState returnState = atn.states.get(ctx.getReturnState(i));
//			System.out.println("popping back to "+retState);
					for (int j = 0; j < ctx.size(); j++) {
						_LOOK(returnState, ctx.getParent(j), look, lookBusy, seeThruPreds);
					}
					return;
				}
			}
        }

        int n = s.getNumberOfTransitions();
        for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t.getClass() == RuleTransition.class ) {
				PredictionContext newContext = ctx.getChild(((RuleTransition)t).followState.stateNumber);
				_LOOK(t.target, newContext, look, lookBusy, seeThruPreds);
			}
			else if ( t instanceof PredicateTransition ) {
				if ( seeThruPreds ) {
					_LOOK(t.target, ctx, look, lookBusy, seeThruPreds);
				}
				else {
					look.add(HIT_PRED);
				}
			}
			else if ( t.isEpsilon() ) {
				_LOOK(t.target, ctx, look, lookBusy, seeThruPreds);
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
