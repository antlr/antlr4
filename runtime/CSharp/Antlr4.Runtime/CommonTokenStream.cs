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
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// This class extends
    /// <see cref="BufferedTokenStream"/>
    /// with functionality to filter
    /// token streams to tokens on a particular channel (tokens where
    /// <see cref="IToken.Channel()"/>
    /// returns a particular value).
    /// <p>
    /// This token stream provides access to all tokens by index or when calling
    /// methods like
    /// <see cref="BufferedTokenStream.GetText()"/>
    /// . The channel filtering is only used for code
    /// accessing tokens via the lookahead methods
    /// <see cref="BufferedTokenStream.La(int)"/>
    /// ,
    /// <see cref="Lt(int)"/>
    /// , and
    /// <see cref="Lb(int)"/>
    /// .</p>
    /// <p>
    /// By default, tokens are placed on the default channel
    /// (
    /// <see cref="TokenConstants.DefaultChannel"/>
    /// ), but may be reassigned by using the
    /// <c>-&gt;channel(HIDDEN)</c>
    /// lexer command, or by using an embedded action to
    /// call
    /// <see cref="Lexer.Channel"/>
    /// .
    /// </p>
    /// <p>
    /// Note: lexer rules which use the
    /// <c>-&gt;skip</c>
    /// lexer command or call
    /// <see cref="Lexer.Skip()"/>
    /// do not produce tokens at all, so input text matched by
    /// such a rule will not be available as part of the token stream, regardless of
    /// channel.</p>
    /// </summary>
    public class CommonTokenStream : BufferedTokenStream
    {
        /// <summary>Specifies the channel to use for filtering tokens.</summary>
        /// <remarks>
        /// Specifies the channel to use for filtering tokens.
        /// <p>
        /// The default value is
        /// <see cref="TokenConstants.DefaultChannel"/>
        /// , which matches the
        /// default channel assigned to tokens created by the lexer.</p>
        /// </remarks>
        protected internal int channel = TokenConstants.DefaultChannel;

        /// <summary>
        /// Constructs a new
        /// <see cref="CommonTokenStream"/>
        /// using the specified token
        /// source and the default token channel (
        /// <see cref="TokenConstants.DefaultChannel"/>
        /// ).
        /// </summary>
        /// <param name="tokenSource">The token source.</param>
        public CommonTokenStream(ITokenSource tokenSource)
            : base(tokenSource)
        {
        }

        /// <summary>
        /// Constructs a new
        /// <see cref="CommonTokenStream"/>
        /// using the specified token
        /// source and filtering tokens to the specified channel. Only tokens whose
        /// <see cref="IToken.Channel()"/>
        /// matches
        /// <paramref name="channel"/>
        /// or have the
        /// <see cref="IToken.Type()"/>
        /// equal to
        /// <see cref="TokenConstants.Eof"/>
        /// will be returned by the
        /// token stream lookahead methods.
        /// </summary>
        /// <param name="tokenSource">The token source.</param>
        /// <param name="channel">The channel to use for filtering tokens.</param>
        public CommonTokenStream(ITokenSource tokenSource, int channel)
            : this(tokenSource)
        {
            this.channel = channel;
        }

        protected internal override int AdjustSeekIndex(int i)
        {
            return NextTokenOnChannel(i, channel);
        }

        protected internal override IToken Lb(int k)
        {
            if (k == 0 || (p - k) < 0)
            {
                return null;
            }
            int i = p;
            int n = 1;
            // find k good tokens looking backwards
            while (n <= k)
            {
                // skip off-channel tokens
                i = PreviousTokenOnChannel(i - 1, channel);
                n++;
            }
            if (i < 0)
            {
                return null;
            }
            return tokens[i];
        }

        public override IToken Lt(int k)
        {
            //System.out.println("enter LT("+k+")");
            LazyInit();
            if (k == 0)
            {
                return null;
            }
            if (k < 0)
            {
                return Lb(-k);
            }
            int i = p;
            int n = 1;
            // we know tokens[p] is a good one
            // find k good tokens
            while (n < k)
            {
                // skip off-channel tokens, but make sure to not look past EOF
                if (Sync(i + 1))
                {
                    i = NextTokenOnChannel(i + 1, channel);
                }
                n++;
            }
            //		if ( i>range ) range = i;
            return tokens[i];
        }

        /// <summary>Count EOF just once.</summary>
        /// <remarks>Count EOF just once.</remarks>
        public virtual int GetNumberOfOnChannelTokens()
        {
            int n = 0;
            Fill();
            for (int i = 0; i < tokens.Count; i++)
            {
                IToken t = tokens[i];
                if (t.Channel == channel)
                {
                    n++;
                }
                if (t.Type == TokenConstants.Eof)
                {
                    break;
                }
            }
            return n;
        }
    }
}
