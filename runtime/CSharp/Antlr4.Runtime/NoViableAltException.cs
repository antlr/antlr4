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
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// Indicates that the parser could not decide which of two or more paths
    /// to take based upon the remaining input.
    /// </summary>
    /// <remarks>
    /// Indicates that the parser could not decide which of two or more paths
    /// to take based upon the remaining input. It tracks the starting token
    /// of the offending input and also knows where the parser was
    /// in the various paths when the error. Reported by reportNoViableAlternative()
    /// </remarks>
    [System.Serializable]
    public class NoViableAltException : RecognitionException
    {
        private const long serialVersionUID = 5096000008992867052L;

        /// <summary>Which configurations did we try at input.index() that couldn't match input.LT(1)?</summary>
        [Nullable]
        private readonly ATNConfigSet deadEndConfigs;

        /// <summary>
        /// The token object at the start index; the input stream might
        /// not be buffering tokens so get a reference to it.
        /// </summary>
        /// <remarks>
        /// The token object at the start index; the input stream might
        /// not be buffering tokens so get a reference to it. (At the
        /// time the error occurred, of course the stream needs to keep a
        /// buffer all of the tokens but later we might not have access to those.)
        /// </remarks>
        [NotNull]
        private readonly IToken startToken;

        public NoViableAltException(Parser recognizer)
			: this(recognizer, ((ITokenStream)recognizer.InputStream), recognizer.CurrentToken, recognizer.CurrentToken, null, recognizer.RuleContext)
        {
        }

        public NoViableAltException(IRecognizer recognizer, ITokenStream input, IToken startToken, IToken offendingToken, ATNConfigSet deadEndConfigs, ParserRuleContext ctx)
            : base(recognizer, input, ctx)
        {
            // LL(1) error
            this.deadEndConfigs = deadEndConfigs;
            this.startToken = startToken;
            this.OffendingToken = offendingToken;
        }

        public virtual IToken StartToken
        {
            get
            {
                return startToken;
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
    }
}
