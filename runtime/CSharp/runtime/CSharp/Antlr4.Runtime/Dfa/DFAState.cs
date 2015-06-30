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
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Dfa
{
    /// <summary>A DFA state represents a set of possible ATN configurations.</summary>
    /// <remarks>
    /// A DFA state represents a set of possible ATN configurations.
    /// As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
    /// to keep track of all possible states the ATN can be in after
    /// reading each input symbol.  That is to say, after reading
    /// input a1a2..an, the DFA is in a state that represents the
    /// subset T of the states of the ATN that are reachable from the
    /// ATN's start state along some path labeled a1a2..an."
    /// In conventional NFA&#x2192;DFA conversion, therefore, the subset T
    /// would be a bitset representing the set of states the
    /// ATN could be in.  We need to track the alt predicted by each
    /// state as well, however.  More importantly, we need to maintain
    /// a stack of states, tracking the closure operations as they
    /// jump from rule to rule, emulating rule invocations (method calls).
    /// I have to add a stack to simulate the proper lookahead sequences for
    /// the underlying LL grammar from which the ATN was derived.
    /// <p>I use a set of ATNConfig objects not simple states.  An ATNConfig
    /// is both a state (ala normal conversion) and a RuleContext describing
    /// the chain of rules (if any) followed to arrive at that state.</p>
    /// <p>A DFA state may have multiple references to a particular state,
    /// but with different ATN contexts (with same or different alts)
    /// meaning that state was reached via a different set of rule invocations.</p>
    /// </remarks>
    public class DFAState
    {
        public int stateNumber = -1;

        [NotNull]
        public readonly ATNConfigSet configs;

        /// <summary>
        /// <c>edges.get(symbol)</c>
        /// points to target of symbol.
        /// </summary>
        [NotNull]
        private volatile AbstractEdgeMap<Antlr4.Runtime.Dfa.DFAState> edges;

        private Antlr4.Runtime.Dfa.AcceptStateInfo acceptStateInfo;

        /// <summary>These keys for these edges are the top level element of the global context.</summary>
        /// <remarks>These keys for these edges are the top level element of the global context.</remarks>
        [NotNull]
        private volatile AbstractEdgeMap<Antlr4.Runtime.Dfa.DFAState> contextEdges;

        /// <summary>Symbols in this set require a global context transition before matching an input symbol.</summary>
        /// <remarks>Symbols in this set require a global context transition before matching an input symbol.</remarks>
        [Nullable]
        private BitSet contextSymbols;

		/// <summary>
        /// This list is computed by
        /// <see cref="Antlr4.Runtime.Atn.ParserATNSimulator.PredicateDFAState(DFAState, Antlr4.Runtime.Atn.ATNConfigSet, int)"/>
        /// .
        /// </summary>
        [Nullable]
        public DFAState.PredPrediction[] predicates;

        /// <summary>Map a predicate to a predicted alternative.</summary>
        /// <remarks>Map a predicate to a predicted alternative.</remarks>
        public class PredPrediction
        {
            [NotNull]
            public SemanticContext pred;

            public int alt;

            public PredPrediction(SemanticContext pred, int alt)
            {
                // never null; at least SemanticContext.NONE
                this.alt = alt;
                this.pred = pred;
            }

            public override string ToString()
            {
                return "(" + pred + ", " + alt + ")";
            }
        }

        public DFAState(DFA dfa, ATNConfigSet configs)
            : this(dfa.EmptyEdgeMap, dfa.EmptyContextEdgeMap, configs)
        {
        }

        public DFAState(EmptyEdgeMap<DFAState> emptyEdges, EmptyEdgeMap<DFAState> emptyContextEdges, ATNConfigSet configs)
        {
            this.configs = configs;
            this.edges = emptyEdges;
            this.contextEdges = emptyContextEdges;
        }

        public bool IsContextSensitive
        {
            get
            {
                return contextSymbols != null;
            }
        }

        public bool IsContextSymbol(int symbol)
        {
            if (!IsContextSensitive || symbol < edges.minIndex)
            {
                return false;
            }
            return contextSymbols.Get(symbol - edges.minIndex);
        }

        public void SetContextSymbol(int symbol)
        {
            System.Diagnostics.Debug.Assert(IsContextSensitive);
            if (symbol < edges.minIndex)
            {
                return;
            }
            contextSymbols.Set(symbol - edges.minIndex);
        }

        public virtual void SetContextSensitive(ATN atn)
        {
            System.Diagnostics.Debug.Assert(!configs.IsOutermostConfigSet);
            if (IsContextSensitive)
            {
                return;
            }
            lock (this)
            {
                if (contextSymbols == null)
                {
                    contextSymbols = new BitSet();
                }
            }
        }

        public AcceptStateInfo AcceptStateInfo
        {
            get
            {
                return acceptStateInfo;
            }
            set
            {
                AcceptStateInfo acceptStateInfo = value;
                this.acceptStateInfo = acceptStateInfo;
            }
        }

        public bool IsAcceptState
        {
            get
            {
                return acceptStateInfo != null;
            }
        }

        public int Prediction
        {
            get
            {
                if (acceptStateInfo == null)
                {
                    return ATN.InvalidAltNumber;
                }
                return acceptStateInfo.Prediction;
            }
        }

        public LexerActionExecutor LexerActionExecutor
        {
            get
            {
                if (acceptStateInfo == null)
                {
                    return null;
                }
                return acceptStateInfo.LexerActionExecutor;
            }
        }

        public virtual DFAState GetTarget(int symbol)
        {
            return edges[symbol];
        }

        public virtual void SetTarget(int symbol, DFAState target)
        {
            edges = edges.Put(symbol, target);
        }

#if NET45PLUS
        public virtual IReadOnlyDictionary<int, DFAState> EdgeMap
#else
        public virtual IDictionary<int, DFAState> EdgeMap
#endif
        {
            get
            {
                return edges.ToMap();
            }
        }

        public virtual DFAState GetContextTarget(int invokingState)
        {
            lock (this)
            {
                if (invokingState == PredictionContext.EmptyFullStateKey)
                {
                    invokingState = -1;
                }
                return contextEdges[invokingState];
            }
        }

        public virtual void SetContextTarget(int invokingState, DFAState target)
        {
            lock (this)
            {
                if (!IsContextSensitive)
                {
                    throw new InvalidOperationException("The state is not context sensitive.");
                }
                if (invokingState == PredictionContext.EmptyFullStateKey)
                {
                    invokingState = -1;
                }
                contextEdges = contextEdges.Put(invokingState, target);
            }
        }

#if NET45PLUS
        public virtual IReadOnlyDictionary<int, DFAState> ContextEdgeMap
#else
        public virtual IDictionary<int, DFAState> ContextEdgeMap
#endif
        {
            get
            {
                var map = contextEdges.ToMap();
                if (map.ContainsKey(-1))
                {
                    if (map.Count == 1)
                    {
                        return Sharpen.Collections.SingletonMap(PredictionContext.EmptyFullStateKey, map[-1]);
                    }
                    else
                    {
#if NET45PLUS
                        Dictionary<int, DFAState> result = map.ToDictionary(i => i.Key, i => i.Value);
#else
                        Dictionary<int, DFAState> result = new Dictionary<int, DFAState>(map);
#endif
                        result.Add(PredictionContext.EmptyFullStateKey, result[-1]);
                        result.Remove(-1);
#if NET45PLUS
                        map = new ReadOnlyDictionary<int, DFAState>(new SortedDictionary<int, DFAState>(result));
#elif COMPACT
                        map = new SortedList<int, DFAState>(result);
#elif PORTABLE && !NET45PLUS
                        map = new Dictionary<int, DFAState>(result);
#else
                        map = new SortedDictionary<int, DFAState>(result);
#endif
                    }
                }
                return map;
            }
        }

        public override int GetHashCode()
        {
            int hash = MurmurHash.Initialize(7);
            hash = MurmurHash.Update(hash, configs.GetHashCode());
            hash = MurmurHash.Finish(hash, 1);
            return hash;
        }

        /// <summary>
        /// Two
        /// <see cref="DFAState"/>
        /// instances are equal if their ATN configuration sets
        /// are the same. This method is used to see if a state already exists.
        /// <p>Because the number of alternatives and number of ATN configurations are
        /// finite, there is a finite number of DFA states that can be processed.
        /// This is necessary to show that the algorithm terminates.</p>
        /// <p>Cannot test the DFA state numbers here because in
        /// <see cref="Antlr4.Runtime.Atn.ParserATNSimulator.AddDFAState(DFA, Antlr4.Runtime.Atn.ATNConfigSet, Antlr4.Runtime.Atn.PredictionContextCache)"/>
        /// we need to know if any other state
        /// exists that has this exact set of ATN configurations. The
        /// <see cref="stateNumber"/>
        /// is irrelevant.</p>
        /// </summary>
        public override bool Equals(object o)
        {
            // compare set of ATN configurations in this set with other
            if (this == o)
            {
                return true;
            }
            if (!(o is DFAState))
            {
                return false;
            }
            DFAState other = (DFAState)o;
            bool sameSet = this.configs.Equals(other.configs);
            //		System.out.println("DFAState.equals: "+configs+(sameSet?"==":"!=")+other.configs);
            return sameSet;
        }

        public override string ToString()
        {
            StringBuilder buf = new StringBuilder();
            buf.Append(stateNumber).Append(":").Append(configs);
            if (IsAcceptState)
            {
                buf.Append("=>");
                if (predicates != null)
                {
                    buf.Append(Arrays.ToString(predicates));
                }
                else
                {
                    buf.Append(Prediction);
                }
            }
            return buf.ToString();
        }
    }
}
