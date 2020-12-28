/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>TODO: make all transitions sets? no, should remove set edges</summary>
    public sealed class AtomTransition : Transition
    {
        /// <summary>The token type or character value; or, signifies special label.</summary>
        /// <remarks>The token type or character value; or, signifies special label.</remarks>
        public readonly int token;

		public AtomTransition(ATNState target, int token)
            : base(target)
        {
			this.token = token;
        }

        public override Antlr4.Runtime.Atn.TransitionType TransitionType
        {
            get
            {
                return Antlr4.Runtime.Atn.TransitionType.ATOM;
            }
        }

        public override IntervalSet Label
        {
            get
            {
				return IntervalSet.Of(token);
            }
        }

        public override bool Matches(int symbol, int minVocabSymbol, int maxVocabSymbol)
        {
			return token == symbol;
        }

        [return: NotNull]
        public override string ToString()
        {
			return token.ToString();
        }
    }
}
