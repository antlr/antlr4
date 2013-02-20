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
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Sharpen;
using Interlocked = System.Threading.Interlocked;

namespace Antlr4.Runtime.Dfa
{
    public class DFA
    {
        /// <summary>A set of all DFA states.</summary>
        /// <remarks>
        /// A set of all DFA states. Use
        /// <see cref="System.Collections.IDictionary{K, V}">System.Collections.IDictionary&lt;K, V&gt;
        ///     </see>
        /// so we can get old state back
        /// (
        /// <see cref="Sharpen.ISet{E}">Sharpen.ISet&lt;E&gt;</see>
        /// only allows you to see if it's there).
        /// </remarks>
        [NotNull]
        public readonly ConcurrentDictionary<DFAState, DFAState> states = new ConcurrentDictionary
            <DFAState, DFAState>();

        [Nullable]
        public readonly AtomicReference<DFAState> s0 = new AtomicReference<DFAState>();

        [Nullable]
        public readonly AtomicReference<DFAState> s0full = new AtomicReference<DFAState>(
            );

        public readonly int decision;

        /// <summary>From which ATN state did we create this DFA?</summary>
        [NotNull]
        public readonly ATNState atnStartState;

        private int nextStateNumber;

        /// <summary>
        /// Set of configs for a DFA state with at least one conflict? Mainly used as "return value"
        /// from
        /// <see cref="Antlr4.Runtime.Atn.ParserATNSimulator.PredictATN(DFA, Antlr4.Runtime.ITokenStream, Antlr4.Runtime.ParserRuleContext, bool)
        ///     ">Antlr4.Runtime.Atn.ParserATNSimulator.PredictATN(DFA, Antlr4.Runtime.ITokenStream, Antlr4.Runtime.ParserRuleContext, bool)
        ///     </see>
        /// for retry.
        /// </summary>
        public DFA(ATNState atnStartState) : this(atnStartState, 0)
        {
        }

        public DFA(ATNState atnStartState, int decision)
        {
            //	public OrderedHashSet<ATNConfig> conflictSet;
            this.atnStartState = atnStartState;
            this.decision = decision;
        }

        public virtual bool IsEmpty()
        {
            return s0.Get() == null && s0full.Get() == null;
        }

        public virtual bool IsContextSensitive()
        {
            return s0full.Get() != null;
        }

        public virtual DFAState AddState(DFAState state)
        {
            state.stateNumber = Interlocked.Increment(ref nextStateNumber);
            return states.GetOrAdd(state, state);
        }

        public override string ToString()
        {
            return ToString(null);
        }

        public virtual string ToString(string[] tokenNames)
        {
            if (s0.Get() == null)
            {
                return string.Empty;
            }
            DFASerializer serializer = new DFASerializer(this, tokenNames);
            return serializer.ToString();
        }

        public virtual string ToString(string[] tokenNames, string[] ruleNames)
        {
            if (s0.Get() == null)
            {
                return string.Empty;
            }
            DFASerializer serializer = new DFASerializer(this, tokenNames, ruleNames, atnStartState
                .atn);
            return serializer.ToString();
        }

        public virtual string ToLexerString()
        {
            if (s0.Get() == null)
            {
                return string.Empty;
            }
            DFASerializer serializer = new LexerDFASerializer(this);
            return serializer.ToString();
        }
    }
}
