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
using Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// The most common stream of tokens where every token is buffered up
    /// and tokens are filtered for a certain channel (the parser will only
    /// see these tokens).
    /// </summary>
    /// <remarks>
    /// The most common stream of tokens where every token is buffered up
    /// and tokens are filtered for a certain channel (the parser will only
    /// see these tokens).
    /// Even though it buffers all of the tokens, this token stream pulls tokens
    /// from the tokens source on demand. In other words, until you ask for a
    /// token using consume(), LT(), etc. the stream does not pull from the lexer.
    /// The only difference between this stream and
    /// <see cref="BufferedTokenStream">BufferedTokenStream</see>
    /// superclass
    /// is that this stream knows how to ignore off channel tokens. There may be
    /// a performance advantage to using the superclass if you don't pass
    /// whitespace and comments etc. to the parser on a hidden channel (i.e.,
    /// you set
    /// <code>$channel</code>
    /// instead of calling
    /// <code>skip()</code>
    /// in lexer rules.)
    /// </remarks>
    /// <seealso cref="UnbufferedTokenStream">UnbufferedTokenStream</seealso>
    /// <seealso cref="BufferedTokenStream">BufferedTokenStream</seealso>
    public class CommonTokenStream : BufferedTokenStream
    {
        /// <summary>Skip tokens on any channel but this one; this is how we skip whitespace...</summary>
        /// <remarks>Skip tokens on any channel but this one; this is how we skip whitespace...</remarks>
        protected internal int channel = TokenConstants.DefaultChannel;

        public CommonTokenStream(ITokenSource tokenSource)
            : base(tokenSource)
        {
        }

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
