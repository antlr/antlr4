/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <author>Sam Harwell</author>
    public sealed class PrecedencePredicateTransition : AbstractPredicateTransition
    {
        public readonly int precedence;

        public PrecedencePredicateTransition(ATNState target, int precedence)
            : base(target)
        {
            this.precedence = precedence;
        }

        public override Antlr4.Runtime.Atn.TransitionType TransitionType
        {
            get
            {
                return Antlr4.Runtime.Atn.TransitionType.PRECEDENCE;
            }
        }

        public override bool IsEpsilon
        {
            get
            {
                return true;
            }
        }

        public override bool Matches(int symbol, int minVocabSymbol, int maxVocabSymbol)
        {
            return false;
        }

        public SemanticContext.PrecedencePredicate Predicate
        {
            get
            {
                return new SemanticContext.PrecedencePredicate(precedence);
            }
        }

        public override string ToString()
        {
            return precedence + " >= _p";
        }
    }
}
