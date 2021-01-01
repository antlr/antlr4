/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public sealed class RangeTransition : Transition
    {
        public readonly int from;

        public readonly int to;

        public RangeTransition(ATNState target, int from, int to)
            : base(target)
        {
            this.from = from;
            this.to = to;
        }

        public override Antlr4.Runtime.Atn.TransitionType TransitionType
        {
            get
            {
                return Antlr4.Runtime.Atn.TransitionType.RANGE;
            }
        }

        public override IntervalSet Label
        {
            get
            {
                return IntervalSet.Of(from, to);
            }
        }

        public override bool Matches(int symbol, int minVocabSymbol, int maxVocabSymbol)
        {
            return symbol >= from && symbol <= to;
        }

        [return: NotNull]
        public override string ToString()
        {
            return "'" + (char)from + "'..'" + (char)to + "'";
        }
    }
}
