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
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// This implementation of
    /// <see cref="ITokenStream"/>
    /// loads tokens from a
    /// <see cref="ITokenSource"/>
    /// on-demand, and places the tokens in a buffer to provide
    /// access to any previous token by index.
    /// <p>
    /// This token stream ignores the value of
    /// <see cref="IToken.Channel()"/>
    /// . If your
    /// parser requires the token stream filter tokens to only those on a particular
    /// channel, such as
    /// <see cref="TokenConstants.DefaultChannel"/>
    /// or
    /// <see cref="TokenConstants.HiddenChannel"/>
    /// , use a filtering token stream such a
    /// <see cref="CommonTokenStream"/>
    /// .</p>
    /// </summary>
    public class BufferedTokenStream : ITokenStream
    {
        /// <summary>
        /// The
        /// <see cref="ITokenSource"/>
        /// from which tokens for this stream are fetched.
        /// </summary>
        [NotNull]
        private ITokenSource _tokenSource;

        /// <summary>A collection of all tokens fetched from the token source.</summary>
        /// <remarks>
        /// A collection of all tokens fetched from the token source. The list is
        /// considered a complete view of the input once
        /// <see cref="fetchedEOF"/>
        /// is set
        /// to
        /// <see langword="true"/>
        /// .
        /// </remarks>
        protected internal IList<IToken> tokens = new List<IToken>(100);

        /// <summary>
        /// The index into
        /// <see cref="tokens"/>
        /// of the current token (next token to
        /// <see cref="Consume()"/>
        /// ).
        /// <see cref="tokens"/>
        /// <c>[</c>
        /// <see cref="p"/>
        /// <c>]</c>
        /// should be
        /// <see cref="Lt(int)">LT(1)</see>
        /// .
        /// <p>This field is set to -1 when the stream is first constructed or when
        /// <see cref="SetTokenSource(ITokenSource)"/>
        /// is called, indicating that the first token has
        /// not yet been fetched from the token source. For additional information,
        /// see the documentation of
        /// <see cref="IIntStream"/>
        /// for a description of
        /// Initializing Methods.</p>
        /// </summary>
        protected internal int p = -1;

        /// <summary>
        /// Indicates whether the
        /// <see cref="TokenConstants.Eof"/>
        /// token has been fetched from
        /// <see cref="_tokenSource"/>
        /// and added to
        /// <see cref="tokens"/>
        /// . This field improves
        /// performance for the following cases:
        /// <ul>
        /// <li>
        /// <see cref="Consume()"/>
        /// : The lookahead check in
        /// <see cref="Consume()"/>
        /// to prevent
        /// consuming the EOF symbol is optimized by checking the values of
        /// <see cref="fetchedEOF"/>
        /// and
        /// <see cref="p"/>
        /// instead of calling
        /// <see cref="La(int)"/>
        /// .</li>
        /// <li>
        /// <see cref="Fetch(int)"/>
        /// : The check to prevent adding multiple EOF symbols into
        /// <see cref="tokens"/>
        /// is trivial with this field.</li>
        /// </ul>
        /// </summary>
        protected internal bool fetchedEOF;

        public BufferedTokenStream(ITokenSource tokenSource)
        {
            if (tokenSource == null)
            {
                throw new ArgumentNullException("tokenSource cannot be null");
            }
            this._tokenSource = tokenSource;
        }

        public virtual ITokenSource TokenSource
        {
            get
            {
                return _tokenSource;
            }
        }

        public virtual int Index
        {
            get
            {
                return p;
            }
        }

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
            bool skipEofCheck;
            if (p >= 0)
            {
                if (fetchedEOF)
                {
                    // the last token in tokens is EOF. skip check if p indexes any
                    // fetched token except the last.
                    skipEofCheck = p < tokens.Count - 1;
                }
                else
                {
                    // no EOF token in tokens. skip check if p indexes a fetched token.
                    skipEofCheck = p < tokens.Count;
                }
            }
            else
            {
                // not yet initialized
                skipEofCheck = false;
            }
            if (!skipEofCheck && La(1) == IntStreamConstants.Eof)
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
        /// <paramref name="i"/>
        /// in tokens has a token.
        /// </summary>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if a token is located at index
        /// <paramref name="i"/>
        /// , otherwise
        /// <see langword="false"/>
        /// .
        /// </returns>
        /// <seealso cref="Get(int)"/>
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
        /// <paramref name="n"/>
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
                IToken t = _tokenSource.NextToken();
                if (t is IWritableToken)
                {
                    ((IWritableToken)t).TokenIndex = tokens.Count;
                }
                tokens.Add(t);
                if (t.Type == TokenConstants.Eof)
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
                throw new ArgumentOutOfRangeException("token index " + i + " out of range 0.." + (tokens.Count - 1));
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
                if (t.Type == TokenConstants.Eof)
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

        [return: NotNull]
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
        /// <paramref name="i"/>
        /// . If an
        /// exception is thrown in this method, the current stream index should not be
        /// changed.
        /// <p>For example,
        /// <see cref="CommonTokenStream"/>
        /// overrides this method to ensure that
        /// the seek target is always an on-channel token.</p>
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
            this._tokenSource = tokenSource;
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
        /// <c>List</c>
        /// of all tokens in
        /// the token type
        /// <c>BitSet</c>
        /// .  Return
        /// <see langword="null"/>
        /// if no tokens were found.  This
        /// method looks at both on and off channel tokens.
        /// </summary>
        public virtual IList<IToken> GetTokens(int start, int stop, BitSet types)
        {
            LazyInit();
            if (start < 0 || stop >= tokens.Count || stop < 0 || start >= tokens.Count)
            {
                throw new ArgumentOutOfRangeException("start " + start + " or stop " + stop + " not in 0.." + (tokens.Count - 1));
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
            if (filteredTokens.Count == 0)
            {
                filteredTokens = null;
            }
            return filteredTokens;
        }

        public virtual IList<IToken> GetTokens(int start, int stop, int ttype)
        {
            BitSet s = new BitSet(ttype);
            s.Set(ttype);
            return GetTokens(start, stop, s);
        }

        /// <summary>Given a starting index, return the index of the next token on channel.</summary>
        /// <remarks>
        /// Given a starting index, return the index of the next token on channel.
        /// Return
        /// <paramref name="i"/>
        /// if
        /// <c>tokens[i]</c>
        /// is on channel. Return the index of
        /// the EOF token if there are no tokens on channel between
        /// <paramref name="i"/>
        /// and
        /// EOF.
        /// </remarks>
        protected internal virtual int NextTokenOnChannel(int i, int channel)
        {
            Sync(i);
            if (i >= Size)
            {
                return Size - 1;
            }
            IToken token = tokens[i];
            while (token.Channel != channel)
            {
                if (token.Type == TokenConstants.Eof)
                {
                    return i;
                }
                i++;
                Sync(i);
                token = tokens[i];
            }
            return i;
        }

        /// <summary>
        /// Given a starting index, return the index of the previous token on
        /// channel.
        /// </summary>
        /// <remarks>
        /// Given a starting index, return the index of the previous token on
        /// channel. Return
        /// <paramref name="i"/>
        /// if
        /// <c>tokens[i]</c>
        /// is on channel. Return -1
        /// if there are no tokens on channel between
        /// <paramref name="i"/>
        /// and 0.
        /// <p>
        /// If
        /// <paramref name="i"/>
        /// specifies an index at or after the EOF token, the EOF token
        /// index is returned. This is due to the fact that the EOF token is treated
        /// as though it were on every channel.</p>
        /// </remarks>
        protected internal virtual int PreviousTokenOnChannel(int i, int channel)
        {
            Sync(i);
            if (i >= Size)
            {
                // the EOF token is on every channel
                return Size - 1;
            }
            while (i >= 0)
            {
                IToken token = tokens[i];
                if (token.Type == TokenConstants.Eof || token.Channel == channel)
                {
                    return i;
                }
                i--;
            }
            return i;
        }

        /// <summary>
        /// Collect all tokens on specified channel to the right of
        /// the current token up until we see a token on
        /// <see cref="Lexer.DefaultTokenChannel"/>
        /// or
        /// EOF. If
        /// <paramref name="channel"/>
        /// is
        /// <c>-1</c>
        /// , find any non default channel token.
        /// </summary>
        public virtual IList<IToken> GetHiddenTokensToRight(int tokenIndex, int channel)
        {
            LazyInit();
            if (tokenIndex < 0 || tokenIndex >= tokens.Count)
            {
                throw new ArgumentOutOfRangeException(tokenIndex + " not in 0.." + (tokens.Count - 1));
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
        /// <see cref="Lexer.DefaultTokenChannel"/>
        /// or EOF.
        /// </summary>
        public virtual IList<IToken> GetHiddenTokensToRight(int tokenIndex)
        {
            return GetHiddenTokensToRight(tokenIndex, -1);
        }

        /// <summary>
        /// Collect all tokens on specified channel to the left of
        /// the current token up until we see a token on
        /// <see cref="Lexer.DefaultTokenChannel"/>
        /// .
        /// If
        /// <paramref name="channel"/>
        /// is
        /// <c>-1</c>
        /// , find any non default channel token.
        /// </summary>
        public virtual IList<IToken> GetHiddenTokensToLeft(int tokenIndex, int channel)
        {
            LazyInit();
            if (tokenIndex < 0 || tokenIndex >= tokens.Count)
            {
                throw new ArgumentOutOfRangeException(tokenIndex + " not in 0.." + (tokens.Count - 1));
            }
            if (tokenIndex == 0)
            {
                // obviously no tokens can appear before the first token
                return null;
            }
            int prevOnChannel = PreviousTokenOnChannel(tokenIndex - 1, Lexer.DefaultTokenChannel);
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
        /// <see cref="Lexer.DefaultTokenChannel"/>
        /// .
        /// </summary>
        public virtual IList<IToken> GetHiddenTokensToLeft(int tokenIndex)
        {
            return GetHiddenTokensToLeft(tokenIndex, -1);
        }

        protected internal virtual IList<IToken> FilterForChannel(int from, int to, int channel)
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
            if (hidden.Count == 0)
            {
                return null;
            }
            return hidden;
        }

        public virtual string SourceName
        {
            get
            {
                return _tokenSource.SourceName;
            }
        }

        /// <summary>Get the text of all tokens in this buffer.</summary>
        /// <remarks>Get the text of all tokens in this buffer.</remarks>
        [return: NotNull]
        public virtual string GetText()
        {
            Fill();
            return GetText(Interval.Of(0, Size - 1));
        }

        [return: NotNull]
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
                if (t.Type == TokenConstants.Eof)
                {
                    break;
                }
                buf.Append(t.Text);
            }
            return buf.ToString();
        }

        [return: NotNull]
        public virtual string GetText(RuleContext ctx)
        {
            return GetText(ctx.SourceInterval);
        }

        [return: NotNull]
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
