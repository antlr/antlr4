/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
    /// <see cref="BufferedTokenStream.LA(int)"/>
    /// ,
    /// <see cref="LT(int)"/>
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
        /// <see cref="TokenConstants.EOF"/>
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

        public override IToken LT(int k)
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
                if (t.Type == TokenConstants.EOF)
                {
                    break;
                }
            }
            return n;
        }
    }
}
