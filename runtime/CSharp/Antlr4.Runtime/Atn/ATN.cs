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
using System.Collections.Concurrent;
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Atn
{
    public class ATN
    {
        public const int InvalidAltNumber = 0;

        public const int Parser = 1;

        public const int Lexer = 2;

        [NotNull]
        public readonly IList<ATNState> states = new List<ATNState>();

        /// <summary>
        /// Each subrule/rule is a decision point and we must track them so we
        /// can go back later and build DFA predictors for them.
        /// </summary>
        /// <remarks>
        /// Each subrule/rule is a decision point and we must track them so we
        /// can go back later and build DFA predictors for them.  This includes
        /// all the rules, subrules, optional blocks, ()+, ()* etc...
        /// </remarks>
        [NotNull]
        public readonly IList<DecisionState> decisionToState = new List<DecisionState>();

        public RuleStartState[] ruleToStartState;

        public RuleStopState[] ruleToStopState;

        [NotNull]
        public readonly IDictionary<string, TokensStartState> modeNameToStartState = new 
            LinkedHashMap<string, TokensStartState>();

        public int grammarType;

        public int maxTokenType;

        public int[] ruleToTokenType;

        public int[] ruleToActionIndex;

        [NotNull]
        public readonly IList<TokensStartState> modeToStartState = new List<TokensStartState
            >();

        /// <summary>used during construction from grammar AST</summary>
        internal int stateNumber = 0;

        private readonly ConcurrentDictionary<PredictionContext, PredictionContext> contextCache
             = new ConcurrentDictionary<PredictionContext, PredictionContext>();

        [NotNull]
        public DFA[] decisionToDFA = new DFA[0];

        [NotNull]
        public DFA[] modeToDFA = new DFA[0];

        protected internal readonly ConcurrentDictionary<int, int> LL1Table = new ConcurrentDictionary
            <int, int>();

        /// <summary>Used for runtime deserialization of ATNs from strings</summary>
        public ATN()
        {
        }

        // runtime for parsers, lexers
        // ATN.LEXER, ...
        // runtime for lexer only
        public void ClearDFA()
        {
            decisionToDFA = new DFA[decisionToState.Count];
            for (int i = 0; i < decisionToDFA.Length; i++)
            {
                decisionToDFA[i] = new DFA(decisionToState[i], i);
            }
            modeToDFA = new DFA[modeToStartState.Count];
            for (int i_1 = 0; i_1 < modeToDFA.Length; i_1++)
            {
                modeToDFA[i_1] = new DFA(modeToStartState[i_1]);
            }
            contextCache.Clear();
            LL1Table.Clear();
        }

        public virtual int GetContextCacheSize()
        {
            return contextCache.Count;
        }

        public virtual PredictionContext GetCachedContext(PredictionContext context)
        {
            return PredictionContext.GetCachedContext(context, contextCache, new PredictionContext.IdentityHashMap
                ());
        }

        public DFA[] GetDecisionToDFA()
        {
            System.Diagnostics.Debug.Assert(decisionToDFA != null && decisionToDFA.Length == 
                decisionToState.Count);
            return decisionToDFA;
        }

        /// <summary>Compute the set of valid tokens that can occur starting in s.</summary>
        /// <remarks>
        /// Compute the set of valid tokens that can occur starting in s.
        /// If ctx is
        /// <see cref="PredictionContext.EmptyLocal">PredictionContext.EmptyLocal</see>
        /// , the set of tokens will not include what can follow
        /// the rule surrounding s. In other words, the set will be
        /// restricted to tokens reachable staying within s's rule.
        /// </remarks>
        public virtual IntervalSet NextTokens(ATNState s, PredictionContext ctx)
        {
            Args.NotNull("ctx", ctx);
            LL1Analyzer anal = new LL1Analyzer(this);
            IntervalSet next = anal.Look(s, ctx);
            return next;
        }

        /// <summary>Compute the set of valid tokens that can occur starting in s and staying in same rule.
        ///     </summary>
        /// <remarks>
        /// Compute the set of valid tokens that can occur starting in s and staying in same rule.
        /// EPSILON is in set if we reach end of rule.
        /// </remarks>
        public virtual IntervalSet NextTokens(ATNState s)
        {
            if (s.nextTokenWithinRule != null)
            {
                return s.nextTokenWithinRule;
            }
            s.nextTokenWithinRule = NextTokens(s, PredictionContext.EmptyLocal);
            s.nextTokenWithinRule.SetReadonly(true);
            return s.nextTokenWithinRule;
        }

        public virtual void AddState(ATNState state)
        {
            if (state == null)
            {
                states.AddItem(null);
                stateNumber++;
                return;
            }
            state.atn = this;
            states.AddItem(state);
            state.stateNumber = stateNumber++;
        }

        public virtual void RemoveState(ATNState state)
        {
            states.Set(state.stateNumber, null);
        }

        // just free mem, don't shift states in list
        public virtual void DefineMode(string name, TokensStartState s)
        {
            modeNameToStartState.Put(name, s);
            modeToStartState.AddItem(s);
            modeToDFA = Arrays.CopyOf(modeToDFA, modeToStartState.Count);
            modeToDFA[modeToDFA.Length - 1] = new DFA(s);
            DefineDecisionState(s);
        }

        public virtual int DefineDecisionState(DecisionState s)
        {
            decisionToState.AddItem(s);
            s.decision = decisionToState.Count - 1;
            decisionToDFA = Arrays.CopyOf(decisionToDFA, decisionToState.Count);
            decisionToDFA[decisionToDFA.Length - 1] = new DFA(s, s.decision);
            return s.decision;
        }

        public virtual DecisionState GetDecisionState(int decision)
        {
            if (!decisionToState.IsEmpty())
            {
                return decisionToState[decision];
            }
            return null;
        }

        public virtual int GetNumberOfDecisions()
        {
            return decisionToState.Count;
        }
    }
}
