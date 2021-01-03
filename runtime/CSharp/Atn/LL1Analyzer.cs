/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
                Look(s.Transition(alt).target, null, PredictionContext.EMPTY, look[alt], lookBusy, new BitSet(), seeThruPreds, false);
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
        /// <see cref="TokenConstants.EPSILON"/>
        /// is added to the result set.
        /// If
        /// <paramref name="ctx"/>
        /// is not
        /// <see langword="null"/>
        /// and the end of the outermost rule is
        /// reached,
        /// <see cref="TokenConstants.EOF"/>
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
        public virtual IntervalSet Look(ATNState s, RuleContext ctx)
        {
            return Look(s, null, ctx);
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
        /// <see cref="TokenConstants.EPSILON"/>
        /// is added to the result set.
        /// If
        /// <paramref name="ctx"/>
        /// is not
        /// <c>PredictionContext#EMPTY_LOCAL</c>
        /// and the end of the outermost rule is
        /// reached,
        /// <see cref="TokenConstants.EOF"/>
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
        public virtual IntervalSet Look(ATNState s, ATNState stopState, RuleContext ctx)
        {
            IntervalSet r = new IntervalSet();
            bool seeThruPreds = true;
			PredictionContext lookContext = ctx != null ? PredictionContext.FromRuleContext(s.atn, ctx) : null;
            Look(s, stopState, lookContext, r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, true);
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
        /// <see cref="PredictionContext.EMPTY"/>
        /// and
        /// <paramref name="stopState"/>
        /// or the end of the rule containing
        /// <paramref name="s"/>
        /// is reached,
        /// <see cref="TokenConstants.EPSILON"/>
        /// is added to the result set. If
        /// <paramref name="ctx"/>
        /// is not
        /// <see cref="PredictionContext.EMPTY"/>
        /// and
        /// <paramref name="addEOF"/>
        /// is
        /// <see langword="true"/>
        /// and
        /// <paramref name="stopState"/>
        /// or the end of the outermost rule is reached,
        /// <see cref="TokenConstants.EOF"/>
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
        /// <see cref="PredictionContext.EMPTY"/>
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
        /// <see cref="TokenConstants.EOF"/>
        /// to the result if the end of the
        /// outermost context is reached. This parameter has no effect if
        /// <paramref name="ctx"/>
        /// is
        /// <see cref="PredictionContext.EMPTY"/>
        /// .
        /// </param>
        protected internal virtual void Look(ATNState s, ATNState stopState, PredictionContext ctx, IntervalSet look, HashSet<ATNConfig> lookBusy, BitSet calledRuleStack, bool seeThruPreds, bool addEOF)
        {
            //		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
            ATNConfig c = new ATNConfig(s, 0, ctx);
            if (!lookBusy.Add(c))
            {
                return;
            }
            if (s == stopState)
            {
                if (ctx == null)
                {
                    look.Add(TokenConstants.EPSILON);
                    return;
                }
                else if (ctx.IsEmpty && addEOF) {
                    look.Add(TokenConstants.EOF);
                   return;
                }
            }
            if (s is RuleStopState)
            {
				if (ctx == null)
				{
					look.Add(TokenConstants.EPSILON);
					return;
				}
                else if (ctx.IsEmpty && addEOF)
                {
                    look.Add(TokenConstants.EOF);
                    return;
                }
				if (ctx != PredictionContext.EMPTY)
				{
					for (int i = 0; i < ctx.Size; i++)
					{
						ATNState returnState = atn.states[ctx.GetReturnState(i)];
						bool removed = calledRuleStack.Get(returnState.ruleIndex);
						try
						{
							calledRuleStack.Clear(returnState.ruleIndex);
							Look(returnState, stopState, ctx.GetParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
						}
						finally
						{
							if (removed)
							{
								calledRuleStack.Set(returnState.ruleIndex);
							}
						}
					}
					return;
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
                    PredictionContext newContext = SingletonPredictionContext.Create(ctx, ruleTransition.followState.stateNumber);
                    try
                    {
                        calledRuleStack.Set(ruleTransition.target.ruleIndex);
                        Look(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    }
                    finally
                    {
                        calledRuleStack.Clear(ruleTransition.target.ruleIndex);
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
                            if (t is WildcardTransition)
                            {
                                look.AddAll(IntervalSet.Of(TokenConstants.MinUserTokenType, atn.maxTokenType));
                            }
                            else
                            {
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
