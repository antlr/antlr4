/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public class LL1Analyzer
    {
        /** Special value added to the lookahead sets to indicate that we hit
         *  a predicate during analysis if {@code seeThruPreds==false}.
         */
        public const int HitPred = TokenConstants.InvalidType;

        [NotNull]
        public readonly ATN atn;

        public LL1Analyzer(ATN atn)
        {
            this.atn = atn;
        }

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
                Look_(s.Transition(alt).target, null, EmptyPredictionContext.Instance, look[alt], lookBusy, new BitSet(), seeThruPreds, false);
                // Wipe out lookahead for this alternative if we found nothing
                // or we had a predicate when we !seeThruPreds
                if (look[alt].Count == 0 || look[alt].Contains(HitPred))
                {
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
        [return: NotNull]
        public virtual IntervalSet Look(ATNState s, RuleContext ctx)
        {
            return Look(s, null, ctx);
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
        [return: NotNull]
        public virtual IntervalSet Look(ATNState s, ATNState stopState, RuleContext ctx)
        {
            IntervalSet r = new IntervalSet();
            bool seeThruPreds = true;
            PredictionContext lookContext = ctx != null ? PredictionContext.FromRuleContext(s.atn, ctx) : null;
            Look_(s, stopState, lookContext, r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, true);
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
        protected internal virtual void Look_(ATNState s, ATNState stopState, PredictionContext ctx, IntervalSet look, HashSet<ATNConfig> lookBusy, BitSet calledRuleStack, bool seeThruPreds, bool addEOF)
        {
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
                else if (ctx.IsEmpty && addEOF)
                {
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
                if (ctx != EmptyPredictionContext.Instance)
                {
                    bool removed = calledRuleStack.Get(s.ruleIndex);
                    try
                    {
                        calledRuleStack.Clear(s.ruleIndex);
                        for (int i = 0; i < ctx.Size; i++)
                        {
                            ATNState returnState = atn.states[ctx.GetReturnState(i)];
                            Look_(returnState, stopState, ctx.GetParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                        }
                    }
                    finally
                    {
                        if (removed)
                        {
                            calledRuleStack.Set(s.ruleIndex);
                        }
                    }
                    return;
                }
            }
            int n = s.NumberOfTransitions;
            for (int i_1 = 0; i_1 < n; i_1++)
            {
                Transition t = s.Transition(i_1);
                if (t.GetType() == typeof(RuleTransition))
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
                        Look_(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    }
                    finally
                    {
                        calledRuleStack.Clear(ruleTransition.target.ruleIndex);
                    }
                }
                else if (t is AbstractPredicateTransition)
                {
                    if (seeThruPreds)
                    {
                        Look_(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    }
                    else
                    {
                        look.Add(HitPred);
                    }
                }
                else if (t.IsEpsilon)
                {
                    Look_(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                }
                else if (t.GetType() == typeof(WildcardTransition))
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
