/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

using System;
using System.Globalization;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    [Serializable]
    public class LexerNoViableAltException : LexerException
    {
        /// <summary>Which configurations did we try at input.index() that couldn't match input.LA(1)?</summary>
        [Nullable]
        private readonly ATNConfigSet deadEndConfigs;

        [Obsolete]
        public LexerNoViableAltException(Lexer lexer, ICharStream input, int startIndex, ATNConfigSet deadEndConfigs)
            : base(lexer, input, startIndex, 1)
        {
            this.deadEndConfigs = deadEndConfigs;
        }

        public LexerNoViableAltException(Lexer lexer, ICharStream input, int startIndex, int length, ATNConfigSet deadEndConfigs)
            : base(lexer, input, startIndex, length)
        {
            this.deadEndConfigs = deadEndConfigs;
        }

        [Nullable]
        public virtual ATNConfigSet DeadEndConfigs => deadEndConfigs;

        public override IIntStream InputStream => (ICharStream)base.InputStream;

        public override string GetErrorMessage(string input) => "token recognition error at: '" + input + "'";

        public override string ToString()
        {
            string symbol = string.Empty;
            if (StartIndex >= 0 && StartIndex < ((ICharStream)InputStream).Size)
            {
                symbol = ((ICharStream)InputStream).GetText(Interval.Of(StartIndex, StartIndex));
                symbol = Utils.EscapeWhitespace(symbol, false);
            }
            return string.Format(CultureInfo.CurrentCulture, "{0}('{1}')", typeof(Antlr4.Runtime.LexerNoViableAltException).Name, symbol);
        }
    }
}
