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
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// This class provides access to specific and aggregate statistics gathered
    /// during profiling of a parser.
    /// </summary>
    /// <remarks>
    /// This class provides access to specific and aggregate statistics gathered
    /// during profiling of a parser.
    /// </remarks>
    /// <since>4.3</since>
    public class ParseInfo
    {
        protected internal readonly ProfilingATNSimulator atnSimulator;

        public ParseInfo(ProfilingATNSimulator atnSimulator)
        {
            this.atnSimulator = atnSimulator;
        }

        /// <summary>
        /// Gets an array of
        /// <see cref="DecisionInfo"/>
        /// instances containing the profiling
        /// information gathered for each decision in the ATN.
        /// </summary>
        /// <returns>
        /// An array of
        /// <see cref="DecisionInfo"/>
        /// instances, indexed by decision
        /// number.
        /// </returns>
        [NotNull]
        public virtual Antlr4.Runtime.Atn.DecisionInfo[] DecisionInfo
        {
            get
            {
                return atnSimulator.DecisionInfo;
            }
        }

        /// <summary>
        /// Gets the decision numbers for decisions that required one or more
        /// full-context predictions during parsing.
        /// </summary>
        /// <remarks>
        /// Gets the decision numbers for decisions that required one or more
        /// full-context predictions during parsing. These are decisions for which
        /// <see cref="Antlr4.Runtime.Atn.DecisionInfo.LL_Fallback"/>
        /// is non-zero.
        /// </remarks>
        /// <returns>
        /// A list of decision numbers which required one or more
        /// full-context predictions during parsing.
        /// </returns>
        [return: NotNull]
        public virtual IList<int> GetLLDecisions()
        {
            Antlr4.Runtime.Atn.DecisionInfo[] decisions = atnSimulator.DecisionInfo;
            IList<int> Ll = new List<int>();
            for (int i = 0; i < decisions.Length; i++)
            {
                long fallBack = decisions[i].LL_Fallback;
                if (fallBack > 0)
                {
                    Ll.Add(i);
                }
            }
            return Ll;
        }

        /// <summary>
        /// Gets the total number of SLL lookahead operations across all decisions
        /// made during parsing.
        /// </summary>
        /// <remarks>
        /// Gets the total number of SLL lookahead operations across all decisions
        /// made during parsing. This value is the sum of
        /// <see cref="Antlr4.Runtime.Atn.DecisionInfo.SLL_TotalLook"/>
        /// for all decisions.
        /// </remarks>
        public virtual long GetTotalSLLLookaheadOps()
        {
            Antlr4.Runtime.Atn.DecisionInfo[] decisions = atnSimulator.DecisionInfo;
            long k = 0;
            for (int i = 0; i < decisions.Length; i++)
            {
                k += decisions[i].SLL_TotalLook;
            }
            return k;
        }

        /// <summary>
        /// Gets the total number of LL lookahead operations across all decisions
        /// made during parsing.
        /// </summary>
        /// <remarks>
        /// Gets the total number of LL lookahead operations across all decisions
        /// made during parsing. This value is the sum of
        /// <see cref="Antlr4.Runtime.Atn.DecisionInfo.LL_TotalLook"/>
        /// for all decisions.
        /// </remarks>
        public virtual long GetTotalLLLookaheadOps()
        {
            Antlr4.Runtime.Atn.DecisionInfo[] decisions = atnSimulator.DecisionInfo;
            long k = 0;
            for (int i = 0; i < decisions.Length; i++)
            {
                k += decisions[i].LL_TotalLook;
            }
            return k;
        }

        /// <summary>
        /// Gets the total number of ATN lookahead operations for SLL prediction
        /// across all decisions made during parsing.
        /// </summary>
        /// <remarks>
        /// Gets the total number of ATN lookahead operations for SLL prediction
        /// across all decisions made during parsing.
        /// </remarks>
        public virtual long GetTotalSLLATNLookaheadOps()
        {
            Antlr4.Runtime.Atn.DecisionInfo[] decisions = atnSimulator.DecisionInfo;
            long k = 0;
            for (int i = 0; i < decisions.Length; i++)
            {
                k += decisions[i].SLL_ATNTransitions;
            }
            return k;
        }

        /// <summary>
        /// Gets the total number of ATN lookahead operations for LL prediction
        /// across all decisions made during parsing.
        /// </summary>
        /// <remarks>
        /// Gets the total number of ATN lookahead operations for LL prediction
        /// across all decisions made during parsing.
        /// </remarks>
        public virtual long GetTotalLLATNLookaheadOps()
        {
            Antlr4.Runtime.Atn.DecisionInfo[] decisions = atnSimulator.DecisionInfo;
            long k = 0;
            for (int i = 0; i < decisions.Length; i++)
            {
                k += decisions[i].LL_ATNTransitions;
            }
            return k;
        }

        /// <summary>
        /// Gets the total number of ATN lookahead operations for SLL and LL
        /// prediction across all decisions made during parsing.
        /// </summary>
        /// <remarks>
        /// Gets the total number of ATN lookahead operations for SLL and LL
        /// prediction across all decisions made during parsing.
        /// <p>
        /// This value is the sum of
        /// <see cref="GetTotalSLLATNLookaheadOps()"/>
        /// and
        /// <see cref="GetTotalLLATNLookaheadOps()"/>
        /// .</p>
        /// </remarks>
        public virtual long GetTotalATNLookaheadOps()
        {
            Antlr4.Runtime.Atn.DecisionInfo[] decisions = atnSimulator.DecisionInfo;
            long k = 0;
            for (int i = 0; i < decisions.Length; i++)
            {
                k += decisions[i].SLL_ATNTransitions;
                k += decisions[i].LL_ATNTransitions;
            }
            return k;
        }

        /// <summary>
        /// Gets the total number of DFA states stored in the DFA cache for all
        /// decisions in the ATN.
        /// </summary>
        /// <remarks>
        /// Gets the total number of DFA states stored in the DFA cache for all
        /// decisions in the ATN.
        /// </remarks>
        public virtual int GetDFASize()
        {
            int n = 0;
            DFA[] decisionToDFA = atnSimulator.atn.decisionToDFA;
            for (int i = 0; i < decisionToDFA.Length; i++)
            {
                n += GetDFASize(i);
            }
            return n;
        }

        /// <summary>
        /// Gets the total number of DFA states stored in the DFA cache for a
        /// particular decision.
        /// </summary>
        /// <remarks>
        /// Gets the total number of DFA states stored in the DFA cache for a
        /// particular decision.
        /// </remarks>
        public virtual int GetDFASize(int decision)
        {
            DFA decisionToDFA = atnSimulator.atn.decisionToDFA[decision];
            return decisionToDFA.states.Count;
        }
    }
}
