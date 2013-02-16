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
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>An ATN transition between any two ATN states.</summary>
    /// <remarks>
    /// An ATN transition between any two ATN states.  Subclasses define
    /// atom, set, epsilon, action, predicate, rule transitions.
    /// <p/>
    /// This is a one way link.  It emanates from a state (usually via a list of
    /// transitions) and has a target state.
    /// <p/>
    /// Since we never have to change the ATN transitions once we construct it,
    /// we can fix these transitions as specific classes. The DFA transitions
    /// on the other hand need to update the labels as it adds transitions to
    /// the states. We'll use the term Edge for the DFA to distinguish them from
    /// ATN transitions.
    /// </remarks>
    public abstract class Transition
    {
        public const int Epsilon = 1;

        public const int Range = 2;

        public const int Rule = 3;

        public const int Predicate = 4;

        public const int Atom = 5;

        public const int Action = 6;

        public const int Set = 7;

        public const int NotSet = 8;

        public const int Wildcard = 9;

        public const int Precedence = 10;

        public static readonly IList<string> serializationNames = Sharpen.Collections.UnmodifiableList
            (Arrays.AsList("INVALID", "EPSILON", "RANGE", "RULE", "PREDICATE", "ATOM", "ACTION"
            , "SET", "NOT_SET", "WILDCARD", "PRECEDENCE"));

        private sealed class _Dictionary_86 : Dictionary<Type, int>
        {
            public _Dictionary_86()
            {
                {
                    // constants for serialization
                    // e.g., {isType(input.LT(1))}?
                    // ~(A|B) or ~atom, wildcard, which convert to next 2
                    this.Put(typeof(EpsilonTransition), Antlr4.Runtime.Atn.Transition.Epsilon);
                    this.Put(typeof(RangeTransition), Antlr4.Runtime.Atn.Transition.Range);
                    this.Put(typeof(RuleTransition), Antlr4.Runtime.Atn.Transition.Rule);
                    this.Put(typeof(PredicateTransition), Antlr4.Runtime.Atn.Transition.Predicate);
                    this.Put(typeof(AtomTransition), Antlr4.Runtime.Atn.Transition.Atom);
                    this.Put(typeof(ActionTransition), Antlr4.Runtime.Atn.Transition.Action);
                    this.Put(typeof(SetTransition), Antlr4.Runtime.Atn.Transition.Set);
                    this.Put(typeof(NotSetTransition), Antlr4.Runtime.Atn.Transition.NotSet);
                    this.Put(typeof(WildcardTransition), Antlr4.Runtime.Atn.Transition.Wildcard);
                    this.Put(typeof(PrecedencePredicateTransition), Antlr4.Runtime.Atn.Transition.Precedence
                        );
                }
            }
        }

        public static readonly IDictionary<Type, int> serializationTypes = Sharpen.Collections
            .UnmodifiableMap(new _Dictionary_86());

        /// <summary>The target of this transition.</summary>
        /// <remarks>The target of this transition.</remarks>
        [NotNull]
        public ATNState target;

        protected internal Transition(ATNState target)
        {
            if (target == null)
            {
                throw new ArgumentNullException("target cannot be null.");
            }
            this.target = target;
        }

        public abstract int SerializationType
        {
            get;
        }

        /// <summary>Are we epsilon, action, sempred?</summary>
        public virtual bool IsEpsilon
        {
            get
            {
                return false;
            }
        }

        public virtual IntervalSet Label
        {
            get
            {
                return null;
            }
        }

        public abstract bool Matches(int symbol, int minVocabSymbol, int maxVocabSymbol);
    }
}
