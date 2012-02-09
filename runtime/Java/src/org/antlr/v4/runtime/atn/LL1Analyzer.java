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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.HashSet;
import java.util.Set;

public class LL1Analyzer {
	/** Used during LOOK to detect computation cycles. E.g., ()* causes
	 *  infinite loop without it.  If we get to same state would be infinite
	 *  loop.
	 */

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
				  PredictionContext.EMPTY,
				  false,
				  look[alt], lookBusy, seeThruPreds);
			if ( look[alt].size()==0 ) look[alt] = null;
		}
		return look;
	}

    /** Get lookahead, using ctx if we reach end of rule. If ctx is EMPTY, don't chase FOLLOW.
     *  If ctx is null, EPSILON is in set if we can reach end of rule.
     */
    @NotNull
   	public IntervalSet LOOK(@NotNull ATNState s, @Nullable PredictionContext ctx) {
   		IntervalSet r = new IntervalSet();
		boolean seeThruPreds = true; // ignore preds; get all lookahead
   		_LOOK(s, ctx != null ? ctx : PredictionContext.EMPTY, ctx == null, r, new HashSet<ATNConfig>(), seeThruPreds);
   		return r;
   	}

    /** Computer set of tokens that can come next. If the context is EMPTY,
     *  then we don't go anywhere when we hit the end of the rule. We have
     *  the correct set.  If the context is null, that means that we did not want
     *  any tokens following this rule--just the tokens that could be found within this
     *  rule. Add EPSILON to the set indicating we reached the end of the ruled out having
     *  to match a token.
     */
    protected void _LOOK(@NotNull ATNState s, @Nullable PredictionContext ctx,
						 boolean epsilonStopState,
						 @NotNull IntervalSet look,
                         @NotNull Set<ATNConfig> lookBusy,
						 boolean seeThruPreds)
	{
//		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
        ATNConfig c = new ATNConfig(s, 0, ctx);
        if ( !lookBusy.add(c) ) return;

        if ( s instanceof RuleStopState ) {
            if ( ctx.isEmpty() && epsilonStopState ) {
                look.add(Token.EPSILON);
                return;
            }
            if ( !ctx.isEmpty() ) {
                ATNState invokingState = atn.states.get(ctx.invokingState);
                RuleTransition rt = (RuleTransition)invokingState.transition(0);
                ATNState retState = rt.followState;
//			System.out.println("popping back to "+retState);
                _LOOK(retState, ctx.parent, epsilonStopState, look, lookBusy, seeThruPreds);
                return;
            }
        }

        int n = s.getNumberOfTransitions();
        for (int i=0; i<n; i++) {
            Transition t = s.transition(i);
            if ( t.getClass() == RuleTransition.class ) {
                PredictionContext newContext = ctx.getChild(s.stateNumber);
                _LOOK(t.target, newContext, epsilonStopState, look, lookBusy, seeThruPreds);
            }
            else if ( t.isEpsilon() && seeThruPreds ) {
                _LOOK(t.target, ctx, epsilonStopState, look, lookBusy, seeThruPreds);
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
