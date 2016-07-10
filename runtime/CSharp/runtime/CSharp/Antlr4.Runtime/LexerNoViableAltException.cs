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
using System.Globalization;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    [System.Serializable]
    public class LexerNoViableAltException : RecognitionException
    {
        private const long serialVersionUID = -730999203913001726L;

        /// <summary>Matching attempted at what input index?</summary>
        private readonly int startIndex;

        /// <summary>Which configurations did we try at input.index() that couldn't match input.LA(1)?</summary>
        [Nullable]
        private readonly ATNConfigSet deadEndConfigs;

        public LexerNoViableAltException(Lexer lexer, ICharStream input, int startIndex, ATNConfigSet deadEndConfigs)
            : base(lexer, input)
        {
            this.startIndex = startIndex;
            this.deadEndConfigs = deadEndConfigs;
        }

        public virtual int StartIndex
        {
            get
            {
                return startIndex;
            }
        }

        [Nullable]
        public virtual ATNConfigSet DeadEndConfigs
        {
            get
            {
                return deadEndConfigs;
            }
        }

        public override IIntStream InputStream
        {
            get
            {
                return (ICharStream)base.InputStream;
            }
        }

        public override string ToString()
        {
            string symbol = string.Empty;
            if (startIndex >= 0 && startIndex < ((ICharStream)InputStream).Size)
            {
                symbol = ((ICharStream)InputStream).GetText(Interval.Of(startIndex, startIndex));
                symbol = Utils.EscapeWhitespace(symbol, false);
            }
            return string.Format(CultureInfo.CurrentCulture, "{0}('{1}')", typeof(Antlr4.Runtime.LexerNoViableAltException).Name, symbol);
        }
    }
}
