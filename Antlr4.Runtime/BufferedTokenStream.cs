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
using System.Collections;
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// Buffer all input tokens but do on-demand fetching of new tokens from
    /// lexer.
    /// </summary>
    /// <remarks>
    /// Buffer all input tokens but do on-demand fetching of new tokens from
    /// lexer. Useful when the parser or lexer has to set context/mode info before
    /// proper lexing of future tokens. The ST template parser needs this,
    /// for example, because it has to constantly flip back and forth between
    /// inside/output templates. E.g.,
    /// <code></code>
    /// &lt;names:
    /// hi, <it>}&gt;} has to parse names
    /// as part of an expression but
    /// <code>"hi, <it>"</code>
    /// as a nested template.
    /// You can't use this stream if you pass whitespace or other off-channel
    /// tokens to the parser. The stream can't ignore off-channel tokens.
    /// (
    /// <see cref="UnbufferedTokenStream">UnbufferedTokenStream</see>
    /// is the same way.)  Use
    /// <see cref="CommonTokenStream">CommonTokenStream</see>
    /// .
    /// This is not a subclass of
    /// <code>UnbufferedTokenStream</code>
    /// because I don't want
    /// to confuse small moving window of tokens it uses for the full buffer.
    /// </remarks>
    public class BufferedTokenStream : ITokenStream
    {
        [NotNull]
        protected internal ITokenSource tokenSource;

        /// <summary>
        /// Record every single token pulled from the source so we can reproduce
        /// chunks of it later.
        /// </summary>
        /// <remarks>
        /// Record every single token pulled from the source so we can reproduce
        /// chunks of it later. This list captures everything so we can access
        /// complete input text.
        /// </remarks>
        protected internal IList<IToken> tokens = new List<IToken>(100);

        /// <summary>
        /// The index into the tokens list of the current token (next token
        /// to consume).
        /// </summary>
        /// <remarks>
        /// The index into the tokens list of the current token (next token
        /// to consume).
        /// <code>tokens[p]</code>
        /// should be
        /// <code>LT(1)</code>
        /// .
        /// <code>p==-1</code>
        /// indicates need
        /// to initialize with first token.  The ctor doesn't get a token.
        /// First call to
        /// <code>LT(1)</code>
        /// or whatever gets the first token and sets
        /// <code>p=0;</code>
        /// .
        /// </remarks>
        protected internal int p = -1;

        /// <summary>
        /// Set to
        /// <code>true</code>
        /// when the EOF token is fetched. Do not continue fetching
        /// tokens after that point, or multiple EOF tokens could end up in the
        /// <see cref="tokens">tokens</see>
        /// array.
        /// </summary>
        /// <seealso cref="Fetch(int)">Fetch(int)</seealso>
        protected internal bool fetchedEOF;

        public BufferedTokenStream(ITokenSource tokenSource)
        {
            if (tokenSource == null)
            {
                throw new ArgumentNullException("tokenSource cannot be null");
            }
            this.tokenSource = tokenSource;
        }

        public virtual ITokenSource TokenSource
        {
            get
            {
                return tokenSource;
            }
        }

        public virtual int Index
        {
            get
            {
                return p;
            }
        }

        //	public int range() { return range; }
        public virtual int Mark()
        {
            return 0;
        }

        public virtual void Release(int marker)
        {
        }

        // no resources to release
        public virtual void Reset()
        {
            Seek(0);
        }

        public virtual void Seek(int index)
        {
            LazyInit();
            p = AdjustSeekIndex(index);
        }

        public virtual int Size
        {
            get
            {
                return tokens.Count;
            }
        }

        public virtual void Consume()
        {
            if (La(1) == Eof)
            {
                throw new InvalidOperationException("cannot consume EOF");
            }
            if (Sync(p + 1))
            {
                p = AdjustSeekIndex(p + 1);
            }
        }

        /// <summary>
        /// Make sure index
        /// <code>i</code>
        /// in tokens has a token.
        /// </summary>
        /// <returns>
        /// 
        /// <code>true</code>
        /// if a token is located at index
        /// <code>i</code>
        /// , otherwise
        /// <code>false</code>
        /// .
        /// </returns>
        /// <seealso cref="Get(int)">Get(int)</seealso>
        protected internal virtual bool Sync(int i)
        {
            System.Diagnostics.Debug.Assert(i >= 0);
            int n = i - tokens.Count + 1;
            // how many more elements we need?
            //System.out.println("sync("+i+") needs "+n);
            if (n > 0)
            {
                int fetched = Fetch(n);
                return fetched >= n;
            }
            return true;
        }

        /// <summary>
        /// Add
        /// <code>n</code>
        /// elements to buffer.
        /// </summary>
        /// <returns>The actual number of elements added to the buffer.</returns>
        protected internal virtual int Fetch(int n)
        {
            if (fetchedEOF)
            {
                return 0;
            }
            for (int i = 0; i < n; i++)
            {
                IToken t = tokenSource.NextToken();
                if (t is IWritableToken)
                {
                    ((IWritableToken)t).TokenIndex = tokens.Count;
                }
                tokens.Add(t);
                if (t.Type == IToken.Eof)
                {
                    fetchedEOF = true;
                    return i + 1;
                }
            }
            return n;
        }

        public virtual IToken Get(int i)
        {
            if (i < 0 || i >= tokens.Count)
            {
                throw new ArgumentOutOfRangeException("token index " + i + " out of range 0.." + 
                    (tokens.Count - 1));
            }
            return tokens[i];
        }

        /// <summary>Get all tokens from start..stop inclusively.</summary>
        /// <remarks>Get all tokens from start..stop inclusively.</remarks>
        public virtual IList<IToken> Get(int start, int stop)
        {
            if (start < 0 || stop < 0)
            {
                return null;
            }
            LazyInit();
            IList<IToken> subset = new List<IToken>();
            if (stop >= tokens.Count)
            {
                stop = tokens.Count - 1;
            }
            for (int i = start; i <= stop; i++)
            {
                IToken t = tokens[i];
                if (t.Type == IToken.Eof)
                {
                    break;
                }
                subset.Add(t);
            }
            return subset;
        }

        public virtual int La(int i)
        {
            return Lt(i).Type;
        }

        protected internal virtual IToken Lb(int k)
        {
            if ((p - k) < 0)
            {
                return null;
            }
            return tokens[p - k];
        }

        public virtual IToken Lt(int k)
        {
            LazyInit();
            if (k == 0)
            {
                return null;
            }
            if (k < 0)
            {
                return Lb(-k);
            }
            int i = p + k - 1;
            Sync(i);
            if (i >= tokens.Count)
            {
                // return EOF token
                // EOF must be last token
                return tokens[tokens.Count - 1];
            }
            //		if ( i>range ) range = i;
            return tokens[i];
        }

        /// <summary>
        /// Allowed derived classes to modify the behavior of operations which change
        /// the current stream position by adjusting the target token index of a seek
        /// operation.
        /// </summary>
        /// <remarks>
        /// Allowed derived classes to modify the behavior of operations which change
        /// the current stream position by adjusting the target token index of a seek
        /// operation. The default implementation simply returns
        /// <code>i</code>
        /// . If an
        /// exception is thrown in this method, the current stream index should not be
        /// changed.
        /// <p/>
        /// For example,
        /// <see cref="CommonTokenStream">CommonTokenStream</see>
        /// overrides this method to ensure that
        /// the seek target is always an on-channel token.
        /// </remarks>
        /// <param name="i">The target token index.</param>
        /// <returns>The adjusted target token index.</returns>
        protected internal virtual int AdjustSeekIndex(int i)
        {
            return i;
        }

        protected internal void LazyInit()
        {
            if (p == -1)
            {
                Setup();
            }
        }

        protected internal virtual void Setup()
        {
            Sync(0);
            p = AdjustSeekIndex(0);
        }

        /// <summary>Reset this token stream by setting its token source.</summary>
        /// <remarks>Reset this token stream by setting its token source.</remarks>
        public virtual void SetTokenSource(ITokenSource tokenSource)
        {
            this.tokenSource = tokenSource;
            tokens.Clear();
            p = -1;
        }

        public virtual IList<IToken> GetTokens()
        {
            return tokens;
        }

        public virtual IList<IToken> GetTokens(int start, int stop)
        {
            return GetTokens(start, stop, null);
        }

        /// <summary>
        /// Given a start and stop index, return a
        /// <code>List</code>
        /// of all tokens in
        /// the token type
        /// <code>BitSet</code>
        /// .  Return
        /// <code>null</code>
        /// if no tokens were found.  This
        /// method looks at both on and off channel tokens.
        /// </summary>
        public virtual IList<IToken> GetTokens(int start, int stop, BitArray types)
        {
            LazyInit();
            if (start < 0 || stop >= tokens.Count || stop < 0 || start >= tokens.Count)
            {
                throw new ArgumentOutOfRangeException("start " + start + " or stop " + stop + " not in 0.."
                     + (tokens.Count - 1));
            }
            if (start > stop)
            {
                return null;
            }
            // list = tokens[start:stop]:{T t, t.getType() in types}
            IList<IToken> filteredTokens = new List<IToken>();
            for (int i = start; i <= stop; i++)
            {
                IToken t = tokens[i];
                if (types == null || types.Get(t.Type))
                {
                    filteredTokens.Add(t);
                }
            }
            if (filteredTokens.IsEmpty())
            {
                filteredTokens = null;
            }
            return filteredTokens;
        }

        public virtual IList<IToken> GetTokens(int start, int stop, int ttype)
        {
            BitArray s = new BitArray(ttype);
            s.Set(ttype);
            return GetTokens(start, stop, s);
        }

        /// <summary>Given a starting index, return the index of the next token on channel.</summary>
        /// <remarks>
        /// Given a starting index, return the index of the next token on channel.
        /// Return
        /// <code>i</code>
        /// if
        /// <code>tokens[i]</code>
        /// is on channel.  Return
        /// <code>-1</code>
        /// if there are no tokens
        /// on channel between
        /// <code>i</code>
        /// and EOF.
        /// </remarks>
        protected internal virtual int NextTokenOnChannel(int i, int channel)
        {
            Sync(i);
            IToken token = tokens[i];
            if (i >= Size)
            {
                return -1;
            }
            while (token.Channel != channel)
            {
                if (token.Type == IToken.Eof)
                {
                    return -1;
                }
                i++;
                Sync(i);
                token = tokens[i];
            }
            return i;
        }

        /// <summary>Given a starting index, return the index of the previous token on channel.
        ///     </summary>
        /// <remarks>
        /// Given a starting index, return the index of the previous token on channel.
        /// Return
        /// <code>i</code>
        /// if
        /// <code>tokens[i]</code>
        /// is on channel. Return
        /// <code>-1</code>
        /// if there are no tokens
        /// on channel between
        /// <code>i</code>
        /// and
        /// <code>0</code>
        /// .
        /// </remarks>
        protected internal virtual int PreviousTokenOnChannel(int i, int channel)
        {
            while (i >= 0 && tokens[i].Channel != channel)
            {
                i--;
            }
            return i;
        }

        /// <summary>
        /// Collect all tokens on specified channel to the right of
        /// the current token up until we see a token on
        /// <see cref="Lexer.DefaultTokenChannel">Lexer.DefaultTokenChannel</see>
        /// or
        /// EOF. If
        /// <code>channel</code>
        /// is
        /// <code>-1</code>
        /// , find any non default channel token.
        /// </summary>
        public virtual IList<IToken> GetHiddenTokensToRight(int tokenIndex, int channel)
        {
            LazyInit();
            if (tokenIndex < 0 || tokenIndex >= tokens.Count)
            {
                throw new ArgumentOutOfRangeException(tokenIndex + " not in 0.." + (tokens.Count 
                    - 1));
            }
            int nextOnChannel = NextTokenOnChannel(tokenIndex + 1, Lexer.DefaultTokenChannel);
            int to;
            int from = tokenIndex + 1;
            // if none onchannel to right, nextOnChannel=-1 so set to = last token
            if (nextOnChannel == -1)
            {
                to = Size - 1;
            }
            else
            {
                to = nextOnChannel;
            }
            return FilterForChannel(from, to, channel);
        }

        /// <summary>
        /// Collect all hidden tokens (any off-default channel) to the right of
        /// the current token up until we see a token on
        /// <see cref="Lexer.DefaultTokenChannel">Lexer.DefaultTokenChannel</see>
        /// or EOF.
        /// </summary>
        public virtual IList<IToken> GetHiddenTokensToRight(int tokenIndex)
        {
            return GetHiddenTokensToRight(tokenIndex, -1);
        }

        /// <summary>
        /// Collect all tokens on specified channel to the left of
        /// the current token up until we see a token on
        /// <see cref="Lexer.DefaultTokenChannel">Lexer.DefaultTokenChannel</see>
        /// .
        /// If
        /// <code>channel</code>
        /// is
        /// <code>-1</code>
        /// , find any non default channel token.
        /// </summary>
        public virtual IList<IToken> GetHiddenTokensToLeft(int tokenIndex, int channel)
        {
            LazyInit();
            if (tokenIndex < 0 || tokenIndex >= tokens.Count)
            {
                throw new ArgumentOutOfRangeException(tokenIndex + " not in 0.." + (tokens.Count 
                    - 1));
            }
            int prevOnChannel = PreviousTokenOnChannel(tokenIndex - 1, Lexer.DefaultTokenChannel
                );
            if (prevOnChannel == tokenIndex - 1)
            {
                return null;
            }
            // if none onchannel to left, prevOnChannel=-1 then from=0
            int from = prevOnChannel + 1;
            int to = tokenIndex - 1;
            return FilterForChannel(from, to, channel);
        }

        /// <summary>
        /// Collect all hidden tokens (any off-default channel) to the left of
        /// the current token up until we see a token on
        /// <see cref="Lexer.DefaultTokenChannel">Lexer.DefaultTokenChannel</see>
        /// .
        /// </summary>
        public virtual IList<IToken> GetHiddenTokensToLeft(int tokenIndex)
        {
            return GetHiddenTokensToLeft(tokenIndex, -1);
        }

        protected internal virtual IList<IToken> FilterForChannel(int from, int to, int channel
            )
        {
            IList<IToken> hidden = new List<IToken>();
            for (int i = from; i <= to; i++)
            {
                IToken t = tokens[i];
                if (channel == -1)
                {
                    if (t.Channel != Lexer.DefaultTokenChannel)
                    {
                        hidden.Add(t);
                    }
                }
                else
                {
                    if (t.Channel == channel)
                    {
                        hidden.Add(t);
                    }
                }
            }
            if (hidden.IsEmpty())
            {
                return null;
            }
            return hidden;
        }

        public virtual string SourceName
        {
            get
            {
                return tokenSource.SourceName;
            }
        }

        /// <summary>Get the text of all tokens in this buffer.</summary>
        /// <remarks>Get the text of all tokens in this buffer.</remarks>
        [NotNull]
        public virtual string GetText()
        {
            Fill();
            return GetText(Interval.Of(0, Size - 1));
        }

        [NotNull]
        public virtual string GetText(Interval interval)
        {
            int start = interval.a;
            int stop = interval.b;
            if (start < 0 || stop < 0)
            {
                return string.Empty;
            }
            LazyInit();
            if (stop >= tokens.Count)
            {
                stop = tokens.Count - 1;
            }
            StringBuilder buf = new StringBuilder();
            for (int i = start; i <= stop; i++)
            {
                IToken t = tokens[i];
                if (t.Type == IToken.Eof)
                {
                    break;
                }
                buf.Append(t.Text);
            }
            return buf.ToString();
        }

        [NotNull]
        public virtual string GetText(RuleContext ctx)
        {
            return GetText(ctx.SourceInterval);
        }

        [NotNull]
        public virtual string GetText(IToken start, IToken stop)
        {
            if (start != null && stop != null)
            {
                return GetText(Interval.Of(start.TokenIndex, stop.TokenIndex));
            }
            return string.Empty;
        }

        /// <summary>Get all tokens from lexer until EOF.</summary>
        /// <remarks>Get all tokens from lexer until EOF.</remarks>
        public virtual void Fill()
        {
            LazyInit();
            int blockSize = 1000;
            while (true)
            {
                int fetched = Fetch(blockSize);
                if (fetched < blockSize)
                {
                    return;
                }
            }
        }
    }
}
