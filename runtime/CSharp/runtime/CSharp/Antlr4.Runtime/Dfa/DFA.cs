/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Interlocked = System.Threading.Interlocked;

namespace Antlr4.Runtime.Dfa
{
    public class DFA
    {
        /// <summary>A set of all DFA states.</summary>
        /// <remarks>
        /// A set of all DFA states. Use
        /// <see cref="System.Collections.Generic.IDictionary{K, V}"/>
        /// so we can get old state back
        /// (
        /// <see cref="HashSet{T}"/>
        /// only allows you to see if it's there).
        /// </remarks>
        [NotNull]
        public readonly ConcurrentDictionary<DFAState, DFAState> states = new ConcurrentDictionary<DFAState, DFAState>();

        [NotNull]
        public readonly AtomicReference<DFAState> s0 = new AtomicReference<DFAState>();

        [NotNull]
        public readonly AtomicReference<DFAState> s0full = new AtomicReference<DFAState>();

        public readonly int decision;

        /// <summary>From which ATN state did we create this DFA?</summary>
        [NotNull]
        public readonly ATNState atnStartState;

        private int nextStateNumber;

        private readonly int minDfaEdge;

        private readonly int maxDfaEdge;

        [NotNull]
        private static readonly Antlr4.Runtime.Dfa.EmptyEdgeMap<DFAState> emptyPrecedenceEdges = new Antlr4.Runtime.Dfa.EmptyEdgeMap<DFAState>(0, 200);

        [NotNull]
        private readonly Antlr4.Runtime.Dfa.EmptyEdgeMap<DFAState> emptyEdgeMap;

        [NotNull]
        private readonly Antlr4.Runtime.Dfa.EmptyEdgeMap<DFAState> emptyContextEdgeMap;

        /// <summary>
        /// <see langword="true"/>
        /// if this DFA is for a precedence decision; otherwise,
        /// <see langword="false"/>
        /// . This is the backing field for <see cref="IsPrecedenceDfa"/>.
        /// </summary>
        private volatile bool precedenceDfa;

        public DFA(ATNState atnStartState)
            : this(atnStartState, 0)
        {
        }

        public DFA(ATNState atnStartState, int decision)
        {
            this.atnStartState = atnStartState;
            this.decision = decision;
            if (this.atnStartState.atn.grammarType == ATNType.Lexer)
            {
                minDfaEdge = LexerATNSimulator.MinDfaEdge;
                maxDfaEdge = LexerATNSimulator.MaxDfaEdge;
            }
            else
            {
                minDfaEdge = TokenConstants.Eof;
                maxDfaEdge = atnStartState.atn.maxTokenType;
            }
            this.emptyEdgeMap = new Antlr4.Runtime.Dfa.EmptyEdgeMap<DFAState>(minDfaEdge, maxDfaEdge);
            this.emptyContextEdgeMap = new Antlr4.Runtime.Dfa.EmptyEdgeMap<DFAState>(-1, atnStartState.atn.states.Count - 1);
        }

        public int MinDfaEdge
        {
            get
            {
                return minDfaEdge;
            }
        }

        public int MaxDfaEdge
        {
            get
            {
                return maxDfaEdge;
            }
        }

        public virtual Antlr4.Runtime.Dfa.EmptyEdgeMap<DFAState> EmptyEdgeMap
        {
            get
            {
                return emptyEdgeMap;
            }
        }

        public virtual Antlr4.Runtime.Dfa.EmptyEdgeMap<DFAState> EmptyContextEdgeMap
        {
            get
            {
                return emptyContextEdgeMap;
            }
        }

        /// <summary>Gets whether this DFA is a precedence DFA.</summary>
        /// <remarks>
        /// Gets whether this DFA is a precedence DFA. Precedence DFAs use a special
        /// start state
        /// <see cref="s0"/>
        /// which is not stored in
        /// <see cref="states"/>
        /// . The
        /// <see cref="DFAState.edges"/>
        /// array for this start state contains outgoing edges
        /// supplying individual start states corresponding to specific precedence
        /// values.
        /// </remarks>
        /// <returns>
        ///
        /// <see langword="true"/>
        /// if this is a precedence DFA; otherwise,
        /// <see langword="false"/>
        /// .
        /// </returns>
        /// <seealso cref="Antlr4.Runtime.Parser.Precedence()"/>
        /// <summary>Sets whether this is a precedence DFA.</summary>
        /// <remarks>
        /// Sets whether this is a precedence DFA. If the specified value differs
        /// from the current DFA configuration, the following actions are taken;
        /// otherwise no changes are made to the current DFA.
        /// <ul>
        /// <li>The
        /// <see cref="states"/>
        /// map is cleared</li>
        /// <li>If
        /// <c>precedenceDfa</c>
        /// is
        /// <see langword="false"/>
        /// , the initial state
        /// <see cref="s0"/>
        /// is set to
        /// <see langword="null"/>
        /// ; otherwise, it is initialized to a new
        /// <see cref="DFAState"/>
        /// with an empty outgoing
        /// <see cref="DFAState.edges"/>
        /// array to
        /// store the start states for individual precedence values.</li>
        /// <li>The
        /// <see cref="precedenceDfa"/>
        /// field is updated</li>
        /// </ul>
        /// </remarks>
        /// <value>
        ///
        /// <see langword="true"/>
        /// if this is a precedence DFA; otherwise,
        /// <see langword="false"/>
        /// </value>
        public bool IsPrecedenceDfa
        {
            get
            {
                return precedenceDfa;
            }
            set
            {
                bool precedenceDfa = value;
                // s0.get() and s0full.get() are never null for a precedence DFA
                // s0full.get() is never null for a precedence DFA
                // s0.get() is never null for a precedence DFA
                lock (this)
                {
                    if (this.precedenceDfa != precedenceDfa)
                    {
                        this.states.Clear();
                        if (precedenceDfa)
                        {
                            this.s0.Set(new DFAState(emptyPrecedenceEdges, EmptyContextEdgeMap, new ATNConfigSet()));
                            this.s0full.Set(new DFAState(emptyPrecedenceEdges, EmptyContextEdgeMap, new ATNConfigSet()));
                        }
                        else
                        {
                            this.s0.Set(null);
                            this.s0full.Set(null);
                        }
                        this.precedenceDfa = precedenceDfa;
                    }
                }
            }
        }

        /// <summary>Get the start state for a specific precedence value.</summary>
        /// <remarks>Get the start state for a specific precedence value.</remarks>
        /// <param name="precedence">The current precedence.</param>
        /// <param name="fullContext">Whether to get from local of full context.</param>
        /// <returns>
        /// The start state corresponding to the specified precedence, or
        /// <see langword="null"/>
        /// if no start state exists for the specified precedence.
        /// </returns>
        /// <exception cref="System.InvalidOperationException">if this is not a precedence DFA.</exception>
        /// <seealso cref="IsPrecedenceDfa()"/>
        public DFAState GetPrecedenceStartState(int precedence, bool fullContext)
        {
            if (!IsPrecedenceDfa)
            {
                throw new InvalidOperationException("Only precedence DFAs may contain a precedence start state.");
            }
            if (fullContext)
            {
                return s0full.Get().GetTarget(precedence);
            }
            else
            {
                return s0.Get().GetTarget(precedence);
            }
        }

        /// <summary>Set the start state for a specific precedence value.</summary>
        /// <remarks>Set the start state for a specific precedence value.</remarks>
        /// <param name="precedence">The current precedence.</param>
        /// <param name="fullContext">Whether to set local of full context.</param>
        /// <param name="startState">
        /// The start state corresponding to the specified
        /// precedence.
        /// </param>
        /// <exception cref="System.InvalidOperationException">if this is not a precedence DFA.</exception>
        /// <seealso cref="IsPrecedenceDfa()"/>
        public void SetPrecedenceStartState(int precedence, bool fullContext, DFAState startState)
        {
            if (!IsPrecedenceDfa)
            {
                throw new InvalidOperationException("Only precedence DFAs may contain a precedence start state.");
            }
            if (precedence < 0)
            {
                return;
            }
            if (fullContext)
            {
                lock (s0full)
                {
                    s0full.Get().SetTarget(precedence, startState);
                }
            }
            else
            {
                lock (s0)
                {
                    s0.Get().SetTarget(precedence, startState);
                }
            }
        }

        public virtual bool IsEmpty
        {
            get
            {
                if (IsPrecedenceDfa)
                {
                    return s0.Get().EdgeMap.Count == 0 && s0full.Get().EdgeMap.Count == 0;
                }
                return s0.Get() == null && s0full.Get() == null;
            }
        }

        public virtual bool IsContextSensitive
        {
            get
            {
                if (IsPrecedenceDfa)
                {
                    return s0full.Get().EdgeMap.Count != 0;
                }
                return s0full.Get() != null;
            }
        }

        public virtual DFAState AddState(DFAState state)
        {
            state.stateNumber = Interlocked.Increment(ref nextStateNumber) - 1;
            return states.GetOrAdd(state, state);
        }

        public override string ToString()
        {
            return ToString(Vocabulary.EmptyVocabulary);
        }

        public virtual string ToString(IVocabulary vocabulary)
        {
            if (s0.Get() == null)
            {
                return string.Empty;
            }
            DFASerializer serializer = new DFASerializer(this, vocabulary);
            return serializer.ToString();
        }

        public virtual string ToString(IVocabulary vocabulary, string[] ruleNames)
        {
            if (s0.Get() == null)
            {
                return string.Empty;
            }
            DFASerializer serializer = new DFASerializer(this, vocabulary, ruleNames, atnStartState.atn);
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
