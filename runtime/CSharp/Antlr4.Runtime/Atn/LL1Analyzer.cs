/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Atn
{
    public class LL1Analyzer
    {
        /// <summary>
        /// Special value added to the lookahead sets to indicate that we hit
        /// a predicate during analysis if seeThruPreds==false.
        /// </summary>
        /// <remarks>
        /// Special value added to the lookahead sets to indicate that we hit
        /// a predicate during analysis if seeThruPreds==false.
        /// </remarks>
        public const int HitPred = TokenConstants.InvalidType;

        [NotNull]
        public readonly ATN atn;

        public LL1Analyzer(ATN atn)
        {
            this.atn = atn;
        }

        /// <summary>
        /// From an ATN state,
        /// <code>s</code>
        /// , find the set of all labels reachable from
        /// <code>s</code>
        /// at depth k. Only for DecisionStates.
        /// </summary>
        [return: Nullable]
        public virtual IntervalSet[] GetDecisionLookahead(ATNState s)
        {
            //		System.out.println("LOOK("+s.stateNumber+")");
            if (s == null)
            {
                return null;
            }
            IntervalSet[] look = new IntervalSet[s.NumberOfTransitions + 1];
            for (int alt = 1; alt <= s.NumberOfTransitions; alt++)
            {
                look[alt] = new IntervalSet();
                HashSet<ATNConfig> lookBusy = new HashSet<ATNConfig>();
                bool seeThruPreds = false;
                // fail to get lookahead upon pred
                Look(s.Transition(alt - 1).target, PredictionContext.EmptyFull, look[alt], lookBusy
                    , seeThruPreds, false);
                // Wipe out lookahead for this alternative if we found nothing
                // or we had a predicate when we !seeThruPreds
                if (look[alt].Size() == 0 || look[alt].Contains(HitPred))
                {
                    look[alt] = null;
                }
            }
            return look;
        }

        /// <summary>
        /// Get lookahead, using
        /// <code>ctx</code>
        /// if we reach end of rule. If
        /// <code>ctx</code>
        /// is
        /// <code>PredictionContext#EMPTY_LOCAL</code>
        /// or
        /// <see cref="PredictionContext.EmptyFull">PredictionContext.EmptyFull</see>
        /// , don't chase FOLLOW. If
        /// <code>ctx</code>
        /// is
        /// <code>PredictionContext#EMPTY_LOCAL</code>
        /// ,
        /// <see cref="Antlr4.Runtime.IToken.Epsilon">EPSILON</see>
        /// is in set if we can reach end of rule. If
        /// <code>ctx</code>
        /// is
        /// <see cref="PredictionContext.EmptyFull">PredictionContext.EmptyFull</see>
        /// ,
        /// <see cref="Antlr4.Runtime.IIntStream.Eof">EOF</see>
        /// is in set
        /// if we can reach end of rule.
        /// </summary>
        [return: NotNull]
        public virtual IntervalSet Look(ATNState s, PredictionContext ctx)
        {
            Args.NotNull("ctx", ctx);
            IntervalSet r = new IntervalSet();
            bool seeThruPreds = true;
            // ignore preds; get all lookahead
            Look(s, ctx, r, new HashSet<ATNConfig>(), seeThruPreds, true);
            return r;
        }

        /// <summary>Compute set of tokens that can come next.</summary>
        /// <remarks>
        /// Compute set of tokens that can come next. If the context is
        /// <see cref="PredictionContext.EmptyFull">PredictionContext.EmptyFull</see>
        /// ,
        /// then we don't go anywhere when we hit the end of the rule. We have
        /// the correct set.  If the context is
        /// <see cref="PredictionContext.EmptyLocal">PredictionContext.EmptyLocal</see>
        /// ,
        /// that means that we did not want any tokens following this rule--just the
        /// tokens that could be found within this rule. Add EPSILON to the set
        /// indicating we reached the end of the ruled out having to match a token.
        /// </remarks>
        protected internal virtual void Look(ATNState s, PredictionContext ctx, IntervalSet
             look, HashSet<ATNConfig> lookBusy, bool seeThruPreds, bool addEOF)
        {
            //		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
            ATNConfig c = ATNConfig.Create(s, 0, ctx);
            if (!lookBusy.Add(c))
            {
                return;
            }
            if (s is RuleStopState)
            {
                if (PredictionContext.IsEmptyLocal(ctx))
                {
                    look.Add(TokenConstants.Epsilon);
                    return;
                }
                else
                {
                    if (ctx.IsEmpty && addEOF)
                    {
                        look.Add(TokenConstants.Eof);
                        return;
                    }
                }
                for (int i = 0; i < ctx.Size; i++)
                {
                    if (ctx.GetReturnState(i) != PredictionContext.EmptyFullStateKey)
                    {
                        ATNState returnState = atn.states[ctx.GetReturnState(i)];
                        //			System.out.println("popping back to "+retState);
                        for (int j = 0; j < ctx.Size; j++)
                        {
                            Look(returnState, ctx.GetParent(j), look, lookBusy, seeThruPreds, addEOF);
                        }
                        return;
                    }
                }
            }
            int n = s.NumberOfTransitions;
            for (int i_1 = 0; i_1 < n; i_1++)
            {
                Transition t = s.Transition(i_1);
                if (t.GetType() == typeof(RuleTransition))
                {
                    PredictionContext newContext = ctx.GetChild(((RuleTransition)t).followState.stateNumber
                        );
                    Look(t.target, newContext, look, lookBusy, seeThruPreds, addEOF);
                }
                else
                {
                    if (t is AbstractPredicateTransition)
                    {
                        if (seeThruPreds)
                        {
                            Look(t.target, ctx, look, lookBusy, seeThruPreds, addEOF);
                        }
                        else
                        {
                            look.Add(HitPred);
                        }
                    }
                    else
                    {
                        if (t.IsEpsilon)
                        {
                            Look(t.target, ctx, look, lookBusy, seeThruPreds, addEOF);
                        }
                        else
                        {
                            if (t.GetType() == typeof(WildcardTransition))
                            {
                                look.AddAll(IntervalSet.Of(TokenConstants.MinUserTokenType, atn.maxTokenType));
                            }
                            else
                            {
                                //				System.out.println("adding "+ t);
                                IntervalSet set = t.Label;
                                if (set != null)
                                {
                                    if (t is NotSetTransition)
                                    {
                                        set = set.Complement(IntervalSet.Of(TokenConstants.MinUserTokenType, atn.maxTokenType
                                            ));
                                    }
                                    look.AddAll(set);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
