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
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>An ATN transition between any two ATN states.</summary>
    /// <remarks>
    /// An ATN transition between any two ATN states.  Subclasses define
    /// atom, set, epsilon, action, predicate, rule transitions.
    /// <p>This is a one way link.  It emanates from a state (usually via a list of
    /// transitions) and has a target state.</p>
    /// <p>Since we never have to change the ATN transitions once we construct it,
    /// we can fix these transitions as specific classes. The DFA transitions
    /// on the other hand need to update the labels as it adds transitions to
    /// the states. We'll use the term Edge for the DFA to distinguish them from
    /// ATN transitions.</p>
    /// </remarks>
    public abstract class Transition
    {
        public static readonly ReadOnlyCollection<string> serializationNames = new ReadOnlyCollection<string>(Arrays.AsList("INVALID", "EPSILON", "RANGE", "RULE", "PREDICATE", "ATOM", "ACTION", "SET", "NOT_SET", "WILDCARD", "PRECEDENCE"));

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

        public abstract TransitionType TransitionType
        {
            get;
        }

        /// <summary>Determines if the transition is an "epsilon" transition.</summary>
        /// <remarks>
        /// Determines if the transition is an "epsilon" transition.
        /// <p>The default implementation returns
        /// <see langword="false"/>
        /// .</p>
        /// </remarks>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if traversing this transition in the ATN does not
        /// consume an input symbol; otherwise,
        /// <see langword="false"/>
        /// if traversing this
        /// transition consumes (matches) an input symbol.
        /// </returns>
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
