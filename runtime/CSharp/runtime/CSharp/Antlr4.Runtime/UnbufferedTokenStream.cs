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
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    public class UnbufferedTokenStream : ITokenStream
    {
        private ITokenSource _tokenSource;

        /// <summary>A moving window buffer of the data being scanned.</summary>
        /// <remarks>
        /// A moving window buffer of the data being scanned. While there's a marker,
        /// we keep adding to buffer. Otherwise,
        /// <see cref="Consume()">consume()</see>
        /// resets so
        /// we start filling at index 0 again.
        /// </remarks>
        protected internal IToken[] tokens;

        /// <summary>
        /// The number of tokens currently in
        /// <see cref="tokens">tokens</see>
        /// .
        /// <p>This is not the buffer capacity, that's
        /// <c>tokens.length</c>
        /// .</p>
        /// </summary>
        protected internal int n;

        /// <summary>
        /// 0..n-1 index into
        /// <see cref="tokens">tokens</see>
        /// of next token.
        /// <p>The
        /// <c>LT(1)</c>
        /// token is
        /// <c>tokens[p]</c>
        /// . If
        /// <c>p == n</c>
        /// , we are
        /// out of buffered tokens.</p>
        /// </summary>
        protected internal int p = 0;

        /// <summary>
        /// Count up with
        /// <see cref="Mark()">mark()</see>
        /// and down with
        /// <see cref="Release(int)">release()</see>
        /// . When we
        /// <c>release()</c>
        /// the last mark,
        /// <c>numMarkers</c>
        /// reaches 0 and we reset the buffer. Copy
        /// <c>tokens[p]..tokens[n-1]</c>
        /// to
        /// <c>tokens[0]..tokens[(n-1)-p]</c>
        /// .
        /// </summary>
        protected internal int numMarkers = 0;

        /// <summary>
        /// This is the
        /// <c>LT(-1)</c>
        /// token for the current position.
        /// </summary>
        protected internal IToken lastToken;

        /// <summary>
        /// When
        /// <c>numMarkers &gt; 0</c>
        /// , this is the
        /// <c>LT(-1)</c>
        /// token for the
        /// first token in
        /// <see cref="tokens"/>
        /// . Otherwise, this is
        /// <see langword="null"/>
        /// .
        /// </summary>
        protected internal IToken lastTokenBufferStart;

        /// <summary>Absolute token index.</summary>
        /// <remarks>
        /// Absolute token index. It's the index of the token about to be read via
        /// <c>LT(1)</c>
        /// . Goes from 0 to the number of tokens in the entire stream,
        /// although the stream size is unknown before the end is reached.
        /// <p>This value is used to set the token indexes if the stream provides tokens
        /// that implement
        /// <see cref="IWritableToken"/>
        /// .</p>
        /// </remarks>
        protected internal int currentTokenIndex = 0;

        public UnbufferedTokenStream(ITokenSource tokenSource)
            : this(tokenSource, 256)
        {
        }

        public UnbufferedTokenStream(ITokenSource tokenSource, int bufferSize)
        {
            this.TokenSource = tokenSource;
            this.tokens = new IToken[bufferSize];
            n = 0;
            Fill(1);
        }

        // prime the pump
        public virtual IToken Get(int i)
        {
            int bufferStartIndex = GetBufferStartIndex();
            if (i < bufferStartIndex || i >= bufferStartIndex + n)
            {
                throw new ArgumentOutOfRangeException("get(" + i + ") outside buffer: " + bufferStartIndex + ".." + (bufferStartIndex + n));
            }
            return tokens[i - bufferStartIndex];
        }

        public virtual IToken Lt(int i)
        {
            if (i == -1)
            {
                return lastToken;
            }
            Sync(i);
            int index = p + i - 1;
            if (index < 0)
            {
                throw new ArgumentOutOfRangeException("LT(" + i + ") gives negative index");
            }
            if (index >= n)
            {
                System.Diagnostics.Debug.Assert(n > 0 && tokens[n - 1].Type == TokenConstants.Eof);
                return tokens[n - 1];
            }
            return tokens[index];
        }

        public virtual int La(int i)
        {
            return Lt(i).Type;
        }

        public virtual ITokenSource TokenSource
        {
            get
            {
                return _tokenSource;
            }
			set 
			{
				_tokenSource = value;
			}
        }

        [return: NotNull]
        public virtual string GetText()
        {
            return string.Empty;
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
            throw new NotSupportedException("The specified start and stop symbols are not supported.");
        }

        public virtual void Consume()
        {
            if (La(1) == TokenConstants.Eof)
            {
                throw new InvalidOperationException("cannot consume EOF");
            }
            // buf always has at least tokens[p==0] in this method due to ctor
            lastToken = tokens[p];
            // track last token for LT(-1)
            // if we're at last token and no markers, opportunity to flush buffer
            if (p == n - 1 && numMarkers == 0)
            {
                n = 0;
                p = -1;
                // p++ will leave this at 0
                lastTokenBufferStart = lastToken;
            }
            p++;
            currentTokenIndex++;
            Sync(1);
        }

        /// <summary>
        /// Make sure we have 'need' elements from current position
        /// <see cref="p">p</see>
        /// . Last valid
        /// <c>p</c>
        /// index is
        /// <c>tokens.length-1</c>
        /// .
        /// <c>p+need-1</c>
        /// is the tokens index 'need' elements
        /// ahead.  If we need 1 element,
        /// <c>(p+1-1)==p</c>
        /// must be less than
        /// <c>tokens.length</c>
        /// .
        /// </summary>
        protected internal virtual void Sync(int want)
        {
            int need = (p + want - 1) - n + 1;
            // how many more elements we need?
            if (need > 0)
            {
                Fill(need);
            }
        }

        /// <summary>
        /// Add
        /// <paramref name="n"/>
        /// elements to the buffer. Returns the number of tokens
        /// actually added to the buffer. If the return value is less than
        /// <paramref name="n"/>
        /// ,
        /// then EOF was reached before
        /// <paramref name="n"/>
        /// tokens could be added.
        /// </summary>
        protected internal virtual int Fill(int n)
        {
            for (int i = 0; i < n; i++)
            {
                if (this.n > 0 && tokens[this.n - 1].Type == TokenConstants.Eof)
                {
                    return i;
                }
                IToken t = TokenSource.NextToken();
                Add(t);
            }
            return n;
        }

        protected internal virtual void Add(IToken t)
        {
            if (n >= tokens.Length)
            {
                tokens = Arrays.CopyOf(tokens, tokens.Length * 2);
            }
            if (t is IWritableToken)
            {
                ((IWritableToken)t).TokenIndex = GetBufferStartIndex() + n;
            }
            tokens[n++] = t;
        }

        /// <summary>Return a marker that we can release later.</summary>
        /// <remarks>
        /// Return a marker that we can release later.
        /// <p>The specific marker value used for this class allows for some level of
        /// protection against misuse where
        /// <c>seek()</c>
        /// is called on a mark or
        /// <c>release()</c>
        /// is called in the wrong order.</p>
        /// </remarks>
        public virtual int Mark()
        {
            if (numMarkers == 0)
            {
                lastTokenBufferStart = lastToken;
            }
            int mark = -numMarkers - 1;
            numMarkers++;
            return mark;
        }

        public virtual void Release(int marker)
        {
            int expectedMark = -numMarkers;
            if (marker != expectedMark)
            {
                throw new InvalidOperationException("release() called with an invalid marker.");
            }
            numMarkers--;
            if (numMarkers == 0)
            {
                // can we release buffer?
                if (p > 0)
                {
                    // Copy tokens[p]..tokens[n-1] to tokens[0]..tokens[(n-1)-p], reset ptrs
                    // p is last valid token; move nothing if p==n as we have no valid char
                    System.Array.Copy(tokens, p, tokens, 0, n - p);
                    // shift n-p tokens from p to 0
                    n = n - p;
                    p = 0;
                }
                lastTokenBufferStart = lastToken;
            }
        }

        public virtual int Index
        {
            get
            {
                return currentTokenIndex;
            }
        }

        public virtual void Seek(int index)
        {
            // seek to absolute index
            if (index == currentTokenIndex)
            {
                return;
            }
            if (index > currentTokenIndex)
            {
                Sync(index - currentTokenIndex);
                index = Math.Min(index, GetBufferStartIndex() + n - 1);
            }
            int bufferStartIndex = GetBufferStartIndex();
            int i = index - bufferStartIndex;
            if (i < 0)
            {
                throw new ArgumentException("cannot seek to negative index " + index);
            }
            else
            {
                if (i >= n)
                {
                    throw new NotSupportedException("seek to index outside buffer: " + index + " not in " + bufferStartIndex + ".." + (bufferStartIndex + n));
                }
            }
            p = i;
            currentTokenIndex = index;
            if (p == 0)
            {
                lastToken = lastTokenBufferStart;
            }
            else
            {
                lastToken = tokens[p - 1];
            }
        }

        public virtual int Size
        {
            get
            {
                throw new NotSupportedException("Unbuffered stream cannot know its size");
            }
        }

        public virtual string SourceName
        {
            get
            {
                return TokenSource.SourceName;
            }
        }

        [return: NotNull]
        public virtual string GetText(Interval interval)
        {
            int bufferStartIndex = GetBufferStartIndex();
            int bufferStopIndex = bufferStartIndex + tokens.Length - 1;
            int start = interval.a;
            int stop = interval.b;
            if (start < bufferStartIndex || stop > bufferStopIndex)
            {
                throw new NotSupportedException("interval " + interval + " not in token buffer window: " + bufferStartIndex + ".." + bufferStopIndex);
            }
            int a = start - bufferStartIndex;
            int b = stop - bufferStartIndex;
            StringBuilder buf = new StringBuilder();
            for (int i = a; i <= b; i++)
            {
                IToken t = tokens[i];
                buf.Append(t.Text);
            }
            return buf.ToString();
        }

        protected internal int GetBufferStartIndex()
        {
            return currentTokenIndex - p;
        }
    }
}
