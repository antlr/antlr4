/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.IO;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>Do not buffer up the entire char stream.</summary>
    /// <remarks>
    /// Do not buffer up the entire char stream. It does keep a small buffer
    /// for efficiency and also buffers while a mark exists (set by the
    /// lookahead prediction in parser). "Unbuffered" here refers to fact
    /// that it doesn't buffer all data, not that's it's on demand loading of char.
    /// </remarks>
    public class UnbufferedCharStream : ICharStream
    {
        /// <summary>A moving window buffer of the data being scanned.</summary>
        /// <remarks>
        /// A moving window buffer of the data being scanned. While there's a marker,
        /// we keep adding to buffer. Otherwise,
        /// <see cref="Consume()">consume()</see>
        /// resets so
        /// we start filling at index 0 again.
        /// </remarks>
        protected internal int[] data;

        /// <summary>
        /// The number of characters currently in
        /// <see cref="data">data</see>
        /// .
        /// <p>This is not the buffer capacity, that's
        /// <c>data.length</c>
        /// .</p>
        /// </summary>
        protected internal int n;

        /// <summary>
        /// 0..n-1 index into
        /// <see cref="data">data</see>
        /// of next character.
        /// <p>The
        /// <c>LA(1)</c>
        /// character is
        /// <c>data[p]</c>
        /// . If
        /// <c>p == n</c>
        /// , we are
        /// out of buffered characters.</p>
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
        /// <c>data[p]..data[n-1]</c>
        /// to
        /// <c>data[0]..data[(n-1)-p]</c>
        /// .
        /// </summary>
        protected internal int numMarkers = 0;

        /// <summary>
        /// This is the
        /// <c>LA(-1)</c>
        /// character for the current position.
        /// </summary>
        protected internal int lastChar = -1;

        /// <summary>
        /// When
        /// <c>numMarkers &gt; 0</c>
        /// , this is the
        /// <c>LA(-1)</c>
        /// character for the
        /// first character in
        /// <see cref="data">data</see>
        /// . Otherwise, this is unspecified.
        /// </summary>
        protected internal int lastCharBufferStart;

        /// <summary>Absolute character index.</summary>
        /// <remarks>
        /// Absolute character index. It's the index of the character about to be
        /// read via
        /// <c>LA(1)</c>
        /// . Goes from 0 to the number of characters in the
        /// entire stream, although the stream size is unknown before the end is
        /// reached.
        /// </remarks>
        protected internal int currentCharIndex = 0;

        protected internal TextReader input;

        /// <summary>The name or source of this char stream.</summary>
        /// <remarks>The name or source of this char stream.</remarks>
        public string name;

        /// <summary>Useful for subclasses that pull char from other than this.input.</summary>
        /// <remarks>Useful for subclasses that pull char from other than this.input.</remarks>
        public UnbufferedCharStream()
            : this(256)
        {
        }

        /// <summary>Useful for subclasses that pull char from other than this.input.</summary>
        /// <remarks>Useful for subclasses that pull char from other than this.input.</remarks>
        public UnbufferedCharStream(int bufferSize)
        {
            n = 0;
            data = new int[bufferSize];
        }

        public UnbufferedCharStream(Stream input)
            : this(input, 256)
        {
        }

        public UnbufferedCharStream(TextReader input)
            : this(input, 256)
        {
        }

        public UnbufferedCharStream(Stream input, int bufferSize)
            : this(bufferSize)
        {
            this.input = new StreamReader(input);
            Fill(1);
        }

        public UnbufferedCharStream(TextReader input, int bufferSize)
            : this(bufferSize)
        {
            // prime
            this.input = input;
            Fill(1);
        }

        // prime
        public virtual void Consume()
        {
            if (LA(1) == IntStreamConstants.EOF)
            {
                throw new InvalidOperationException("cannot consume EOF");
            }
            // buf always has at least data[p==0] in this method due to ctor
            lastChar = data[p];
            // track last char for LA(-1)
            if (p == n - 1 && numMarkers == 0)
            {
                n = 0;
                p = -1;
                // p++ will leave this at 0
                lastCharBufferStart = lastChar;
            }
            p++;
            currentCharIndex++;
            Sync(1);
        }

        /// <summary>
        /// Make sure we have 'need' elements from current position
        /// <see cref="p">p</see>
        /// .
        /// Last valid
        /// <c>p</c>
        /// index is
        /// <c>data.length-1</c>
        /// .
        /// <c>p+need-1</c>
        /// is
        /// the char index 'need' elements ahead. If we need 1 element,
        /// <c>(p+1-1)==p</c>
        /// must be less than
        /// <c>data.length</c>
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
        /// characters to the buffer. Returns the number of characters
        /// actually added to the buffer. If the return value is less than
        /// <paramref name="n"/>
        /// ,
        /// then EOF was reached before
        /// <paramref name="n"/>
        /// characters could be added.
        /// </summary>
        protected internal virtual int Fill(int n)
        {
            for (int i = 0; i < n; i++)
            {
                if (this.n > 0 && data[this.n - 1] == IntStreamConstants.EOF)
                {
                    return i;
                }

                int c = NextChar();
                if (c > char.MaxValue || c == IntStreamConstants.EOF)
                {
                    Add(c);
                }
                else
                {
                    char ch = unchecked((char)c);
                    if (Char.IsLowSurrogate(ch))
                    {
                        throw new ArgumentException("Invalid UTF-16 (low surrogate with no preceding high surrogate)");
                    }
                    else if (Char.IsHighSurrogate(ch))
                    {
                        int lowSurrogate = NextChar();
                        if (lowSurrogate > char.MaxValue)
                        {
                            throw new ArgumentException("Invalid UTF-16 (high surrogate followed by code point > U+FFFF");
                        }
                        else if (lowSurrogate == IntStreamConstants.EOF)
                        {
                            throw new ArgumentException("Invalid UTF-16 (low surrogate with no preceding high surrogate)");
                        }
                        else
                        {
                            char lowSurrogateChar = unchecked((char)lowSurrogate);
                            if (Char.IsLowSurrogate(lowSurrogateChar))
                            {
                                Add(Char.ConvertToUtf32(ch, lowSurrogateChar));
                            }
                            else
                            {
                                throw new ArgumentException("Invalid UTF-16 (low surrogate with no preceding high surrogate)");
                            }
                        }
                    }
                    else
                    {
                        Add(c);
                    }
                }
            }
            return n;
        }

        /// <summary>
        /// Override to provide different source of characters than
        /// <see cref="input">input</see>
        /// .
        /// </summary>
        /// <exception cref="System.IO.IOException"/>
        protected internal virtual int NextChar()
        {
            return input.Read();
        }

        protected internal virtual void Add(int c)
        {
            if (n >= data.Length)
            {
                data = Arrays.CopyOf(data, data.Length * 2);
            }
            data[n++] = c;
        }

        public virtual int LA(int i)
        {
            if (i == -1)
            {
                return lastChar;
            }
            // special case
            Sync(i);
            int index = p + i - 1;
            if (index < 0)
            {
                throw new ArgumentOutOfRangeException();
            }
            if (index >= n)
            {
                return IntStreamConstants.EOF;
            }
            return data[index];
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
                lastCharBufferStart = lastChar;
            }
            int mark = -numMarkers - 1;
            numMarkers++;
            return mark;
        }

        /// <summary>Decrement number of markers, resetting buffer if we hit 0.</summary>
        /// <remarks>Decrement number of markers, resetting buffer if we hit 0.</remarks>
        /// <param name="marker"/>
        public virtual void Release(int marker)
        {
            int expectedMark = -numMarkers;
            if (marker != expectedMark)
            {
                throw new InvalidOperationException("release() called with an invalid marker.");
            }
            numMarkers--;
            if (numMarkers == 0 && p > 0)
            {
                // release buffer when we can, but don't do unnecessary work
                // Copy data[p]..data[n-1] to data[0]..data[(n-1)-p], reset ptrs
                // p is last valid char; move nothing if p==n as we have no valid char
                System.Array.Copy(data, p, data, 0, n - p);
                // shift n-p char from p to 0
                n = n - p;
                p = 0;
                lastCharBufferStart = lastChar;
            }
        }

        public virtual int Index
        {
            get
            {
                return currentCharIndex;
            }
        }

        /// <summary>
        /// Seek to absolute character index, which might not be in the current
        /// sliding window.
        /// </summary>
        /// <remarks>
        /// Seek to absolute character index, which might not be in the current
        /// sliding window.  Move
        /// <c>p</c>
        /// to
        /// <c>index-bufferStartIndex</c>
        /// .
        /// </remarks>
        public virtual void Seek(int index)
        {
            if (index == currentCharIndex)
            {
                return;
            }
            if (index > currentCharIndex)
            {
                Sync(index - currentCharIndex);
                index = Math.Min(index, BufferStartIndex + n - 1);
            }
            // index == to bufferStartIndex should set p to 0
            int i = index - BufferStartIndex;
            if (i < 0)
            {
                throw new ArgumentException("cannot seek to negative index " + index);
            }
            else
            {
                if (i >= n)
                {
                    throw new NotSupportedException("seek to index outside buffer: " + index + " not in " + BufferStartIndex + ".." + (BufferStartIndex + n));
                }
            }
            p = i;
            currentCharIndex = index;
            if (p == 0)
            {
                lastChar = lastCharBufferStart;
            }
            else
            {
                lastChar = data[p - 1];
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
                if (string.IsNullOrEmpty(name))
                {
                    return IntStreamConstants.UnknownSourceName;
                }
                return name;
            }
        }

        public virtual string GetText(Interval interval)
        {
            if (interval.a < 0 || interval.b < interval.a - 1)
            {
                throw new ArgumentException("invalid interval");
            }
            int bufferStartIndex = BufferStartIndex;
            if (n > 0 && data[n - 1] == IntStreamConstants.EOF)
            {
                if (interval.a + interval.Length > bufferStartIndex + n)
                {
                    throw new ArgumentException("the interval extends past the end of the stream");
                }
            }
            if (interval.a < bufferStartIndex || interval.b >= bufferStartIndex + n)
            {
                throw new NotSupportedException("interval " + interval + " outside buffer: " + bufferStartIndex + ".." + (bufferStartIndex + n - 1));
            }
            // convert from absolute to local index
            int i = interval.a - bufferStartIndex;
            // build a UTF-16 string from the Unicode code points in data
            var sb = new StringBuilder(interval.Length);
            for (int offset = 0; offset < interval.Length; offset++) {
                sb.Append(Char.ConvertFromUtf32(data[i + offset]));
            }
            return sb.ToString();
        }

        protected internal int BufferStartIndex
        {
            get
            {
                return currentCharIndex - p;
            }
        }
    }
}
