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
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public class LL1Analyzer
    {
        /// <summary>
        /// Special value added to the lookahead sets to indicate that we hit
        /// a predicate during analysis if
        /// <c>seeThruPreds==false</c>
        /// .
        /// </summary>
        public const int HitPred = TokenConstants.InvalidType;

        [NotNull]
        public readonly ATN atn;

        public LL1Analyzer(ATN atn)
        {
            this.atn = atn;
        }

        /// <summary>
        /// Calculates the SLL(1) expected lookahead set for each outgoing transition
        /// of an
        /// <see cref="ATNState"/>
        /// . The returned array has one element for each
        /// outgoing transition in
        /// <paramref name="s"/>
        /// . If the closure from transition
        /// <em>i</em> leads to a semantic predicate before matching a symbol, the
        /// element at index <em>i</em> of the result will be
        /// <see langword="null"/>
        /// .
        /// </summary>
        /// <param name="s">the ATN state</param>
        /// <returns>
        /// the expected symbols for each outgoing transition of
        /// <paramref name="s"/>
        /// .
        /// </returns>
        [return: Nullable]
        public virtual IntervalSet[] GetDecisionLookahead(ATNState s)
        {
            //		System.out.println("LOOK("+s.stateNumber+")");
            if (s == null)
            {
                return null;
            }
            IntervalSet[] look = new IntervalSet[s.NumberOfTransitions];
            for (int alt = 0; alt < s.NumberOfTransitions; alt++)
            {
                look[alt] = new IntervalSet();
                HashSet<ATNConfig> lookBusy = new HashSet<ATNConfig>();
                bool seeThruPreds = false;
                // fail to get lookahead upon pred
                Look(s.Transition(alt).target, null, PredictionContext.EmptyLocal, look[alt], lookBusy, new BitSet(), seeThruPreds, false);
                // Wipe out lookahead for this alternative if we found nothing
                // or we had a predicate when we !seeThruPreds
                if (look[alt].Count == 0 || look[alt].Contains(HitPred))
                {
                    look[alt] = null;
                }
            }
            return look;
        }

        /// <summary>
        /// Compute set of tokens that can follow
        /// <paramref name="s"/>
        /// in the ATN in the
        /// specified
        /// <paramref name="ctx"/>
        /// .
        /// <p>If
        /// <paramref name="ctx"/>
        /// is
        /// <see langword="null"/>
        /// and the end of the rule containing
        /// <paramref name="s"/>
        /// is reached,
        /// <see cref="TokenConstants.Epsilon"/>
        /// is added to the result set.
        /// If
        /// <paramref name="ctx"/>
        /// is not
        /// <see langword="null"/>
        /// and the end of the outermost rule is
        /// reached,
        /// <see cref="TokenConstants.Eof"/>
        /// is added to the result set.</p>
        /// </summary>
        /// <param name="s">the ATN state</param>
        /// <param name="ctx">
        /// the complete parser context, or
        /// <see langword="null"/>
        /// if the context
        /// should be ignored
        /// </param>
        /// <returns>
        /// The set of tokens that can follow
        /// <paramref name="s"/>
        /// in the ATN in the
        /// specified
        /// <paramref name="ctx"/>
        /// .
        /// </returns>
        [return: NotNull]
        public virtual IntervalSet Look(ATNState s, PredictionContext ctx)
        {
            return Look(s, s.atn.ruleToStopState[s.ruleIndex], ctx);
        }

        /// <summary>
        /// Compute set of tokens that can follow
        /// <paramref name="s"/>
        /// in the ATN in the
        /// specified
        /// <paramref name="ctx"/>
        /// .
        /// <p>If
        /// <paramref name="ctx"/>
        /// is
        /// <see langword="null"/>
        /// and the end of the rule containing
        /// <paramref name="s"/>
        /// is reached,
        /// <see cref="TokenConstants.Epsilon"/>
        /// is added to the result set.
        /// If
        /// <paramref name="ctx"/>
        /// is not
        /// <c>PredictionContext#EMPTY_LOCAL</c>
        /// and the end of the outermost rule is
        /// reached,
        /// <see cref="TokenConstants.Eof"/>
        /// is added to the result set.</p>
        /// </summary>
        /// <param name="s">the ATN state</param>
        /// <param name="stopState">
        /// the ATN state to stop at. This can be a
        /// <see cref="BlockEndState"/>
        /// to detect epsilon paths through a closure.
        /// </param>
        /// <param name="ctx">
        /// the complete parser context, or
        /// <see langword="null"/>
        /// if the context
        /// should be ignored
        /// </param>
        /// <returns>
        /// The set of tokens that can follow
        /// <paramref name="s"/>
        /// in the ATN in the
        /// specified
        /// <paramref name="ctx"/>
        /// .
        /// </returns>
        [return: NotNull]
        public virtual IntervalSet Look(ATNState s, ATNState stopState, PredictionContext ctx)
        {
            IntervalSet r = new IntervalSet();
            bool seeThruPreds = true;
            // ignore preds; get all lookahead
            bool addEOF = true;
            Look(s, stopState, ctx, r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, addEOF);
            return r;
        }

        /// <summary>
        /// Compute set of tokens that can follow
        /// <paramref name="s"/>
        /// in the ATN in the
        /// specified
        /// <paramref name="ctx"/>
        /// .
        /// <p/>
        /// If
        /// <paramref name="ctx"/>
        /// is
        /// <see cref="PredictionContext.EmptyLocal"/>
        /// and
        /// <paramref name="stopState"/>
        /// or the end of the rule containing
        /// <paramref name="s"/>
        /// is reached,
        /// <see cref="TokenConstants.Epsilon"/>
        /// is added to the result set. If
        /// <paramref name="ctx"/>
        /// is not
        /// <see cref="PredictionContext.EmptyLocal"/>
        /// and
        /// <paramref name="addEOF"/>
        /// is
        /// <see langword="true"/>
        /// and
        /// <paramref name="stopState"/>
        /// or the end of the outermost rule is reached,
        /// <see cref="TokenConstants.Eof"/>
        /// is added to the result set.
        /// </summary>
        /// <param name="s">the ATN state.</param>
        /// <param name="stopState">
        /// the ATN state to stop at. This can be a
        /// <see cref="BlockEndState"/>
        /// to detect epsilon paths through a closure.
        /// </param>
        /// <param name="ctx">
        /// The outer context, or
        /// <see cref="PredictionContext.EmptyLocal"/>
        /// if
        /// the outer context should not be used.
        /// </param>
        /// <param name="look">The result lookahead set.</param>
        /// <param name="lookBusy">
        /// A set used for preventing epsilon closures in the ATN
        /// from causing a stack overflow. Outside code should pass
        /// <c>new HashSet&lt;ATNConfig&gt;</c>
        /// for this argument.
        /// </param>
        /// <param name="calledRuleStack">
        /// A set used for preventing left recursion in the
        /// ATN from causing a stack overflow. Outside code should pass
        /// <c>new BitSet()</c>
        /// for this argument.
        /// </param>
        /// <param name="seeThruPreds">
        /// 
        /// <see langword="true"/>
        /// to true semantic predicates as
        /// implicitly
        /// <see langword="true"/>
        /// and "see through them", otherwise
        /// <see langword="false"/>
        /// to treat semantic predicates as opaque and add
        /// <see cref="HitPred"/>
        /// to the
        /// result if one is encountered.
        /// </param>
        /// <param name="addEOF">
        /// Add
        /// <see cref="TokenConstants.Eof"/>
        /// to the result if the end of the
        /// outermost context is reached. This parameter has no effect if
        /// <paramref name="ctx"/>
        /// is
        /// <see cref="PredictionContext.EmptyLocal"/>
        /// .
        /// </param>
        protected internal virtual void Look(ATNState s, ATNState stopState, PredictionContext ctx, IntervalSet look, HashSet<ATNConfig> lookBusy, BitSet calledRuleStack, bool seeThruPreds, bool addEOF)
        {
            //		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
            ATNConfig c = ATNConfig.Create(s, 0, ctx);
            if (!lookBusy.Add(c))
            {
                return;
            }
            if (s == stopState)
            {
                if (PredictionContext.IsEmptyLocal(ctx))
                {
                    look.Add(TokenConstants.Epsilon);
                    return;
                }
                else
                {
                    if (ctx.IsEmpty)
                    {
                        if (addEOF)
                        {
                            look.Add(TokenConstants.Eof);
                        }
                        return;
                    }
                }
            }
            if (s is RuleStopState)
            {
                if (ctx.IsEmpty && !PredictionContext.IsEmptyLocal(ctx))
                {
                    if (addEOF)
                    {
                        look.Add(TokenConstants.Eof);
                    }
                    return;
                }
                bool removed = calledRuleStack.Get(s.ruleIndex);
                try
                {
                    calledRuleStack.Clear(s.ruleIndex);
                    for (int i = 0; i < ctx.Size; i++)
                    {
                        if (ctx.GetReturnState(i) == PredictionContext.EmptyFullStateKey)
                        {
                            continue;
                        }
                        ATNState returnState = atn.states[ctx.GetReturnState(i)];
                        //					System.out.println("popping back to "+retState);
                        Look(returnState, stopState, ctx.GetParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    }
                }
                finally
                {
                    if (removed)
                    {
                        calledRuleStack.Set(s.ruleIndex);
                    }
                }
            }
            int n = s.NumberOfTransitions;
            for (int i_1 = 0; i_1 < n; i_1++)
            {
                Transition t = s.Transition(i_1);
                if (t is RuleTransition)
                {
                    RuleTransition ruleTransition = (RuleTransition)t;
                    if (calledRuleStack.Get(ruleTransition.ruleIndex))
                    {
                        continue;
                    }
                    PredictionContext newContext = ctx.GetChild(ruleTransition.followState.stateNumber);
                    try
                    {
                        calledRuleStack.Set(ruleTransition.ruleIndex);
                        Look(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    }
                    finally
                    {
                        calledRuleStack.Clear(ruleTransition.ruleIndex);
                    }
                }
                else
                {
                    if (t is AbstractPredicateTransition)
                    {
                        if (seeThruPreds)
                        {
                            Look(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
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
                            Look(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
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
                                        set = set.Complement(IntervalSet.Of(TokenConstants.MinUserTokenType, atn.maxTokenType));
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
