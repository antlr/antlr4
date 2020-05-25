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
    public abstract class BaseInputCharStream : ICharStream
    {
        public const int ReadBufferSize = 1024;

        public const int InitialBufferSize = 1024;

        /// <summary>How many characters are actually in the buffer</summary>
        protected internal int n;

        /// <summary>0..n-1 index into string of next char</summary>
        protected internal int p = 0;

        /// <summary>What is name or source of this char stream?</summary>
        public string name;

        /// <summary>
        /// Reset the stream so that it's in the same state it was
        /// when the object was created *except* the data array is not
        /// touched.
        /// </summary>
        /// <remarks>
        /// Reset the stream so that it's in the same state it was
        /// when the object was created *except* the data array is not
        /// touched.
        /// </remarks>
        public virtual void Reset()
        {
            p = 0;
        }

        public virtual void Consume()
        {
            if (p >= n)
            {
                System.Diagnostics.Debug.Assert(LA(1) == IntStreamConstants.EOF);
                throw new InvalidOperationException("cannot consume EOF");
            }
            else
            {
                p++;
            }
        }

        //System.out.println("p moves to "+p+" (c='"+(char)data[p]+"')");
        public virtual int LA(int i)
        {
            if (i == 0)
            {
                return 0;
            }
            // undefined
            if (i < 0)
            {
                i++;
                // e.g., translate LA(-1) to use offset i=0; then data[p+0-1]
                if ((p + i - 1) < 0)
                {
                    return IntStreamConstants.EOF;
                }
            }
            // invalid; no char before first char
            if ((p + i - 1) >= n)
            {
                //System.out.println("char LA("+i+")=EOF; p="+p);
                return IntStreamConstants.EOF;
            }
            //System.out.println("char LA("+i+")="+(char)data[p+i-1]+"; p="+p);
            //System.out.println("LA("+i+"); p="+p+" n="+n+" data.length="+data.length);
            return ValueAt(p + i - 1);
        }

        public virtual int Lt(int i)
        {
            return LA(i);
        }

        /// <summary>
        /// Return the current input symbol index 0..n where n indicates the
        /// last symbol has been read.
        /// </summary>
        /// <remarks>
        /// Return the current input symbol index 0..n where n indicates the
        /// last symbol has been read.  The index is the index of char to
        /// be returned from LA(1).
        /// </remarks>
        public virtual int Index
        {
            get
            {
                return p;
            }
        }

        public virtual int Size
        {
            get
            {
                return n;
            }
        }

        /// <summary>mark/release do nothing; we have entire buffer</summary>
        public virtual int Mark()
        {
            return -1;
        }

        public virtual void Release(int marker)
        {
        }

        /// <summary>
        /// consume() ahead until p==index; can't just set p=index as we must
        /// update line and charPositionInLine.
        /// </summary>
        /// <remarks>
        /// consume() ahead until p==index; can't just set p=index as we must
        /// update line and charPositionInLine. If we seek backwards, just set p
        /// </remarks>
        public virtual void Seek(int index)
        {
            if (index <= p)
            {
                p = index;
                // just jump; don't update stream state (line, ...)
                return;
            }
            // seek forward, consume until p hits index or n (whichever comes first)
            index = Math.Min(index, n);
            while (p < index)
            {
                Consume();
            }
        }

        public virtual string GetText(Interval interval)
        {
            int start = interval.a;
            int stop = interval.b;
            if (stop >= n)
            {
                stop = n - 1;
            }
            int count = stop - start + 1;
            if (start >= n)
            {
                return string.Empty;
            }
            return ConvertDataToString(start, count);
        }

        protected abstract int ValueAt(int i);

        protected abstract string ConvertDataToString(int start, int count);

        public override sealed string ToString()
        {
            return ConvertDataToString(0, n);
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
    }

    /// <summary>
    /// Vacuum all input from a
    /// <see cref="System.IO.TextReader"/>
    /// /
    /// <see cref="System.IO.Stream"/>
    /// and then treat it
    /// like a
    /// <c>char[]</c>
    /// buffer. Can also pass in a
    /// <see cref="string"/>
    /// or
    /// <c>char[]</c>
    /// to use.
    /// <p>If you need encoding, pass in stream/reader with correct encoding.</p>
    /// </summary>
    public class AntlrInputStream : BaseInputCharStream
    {
        /// <summary>The data being scanned</summary>
        protected internal char[] data;

        public AntlrInputStream()
        {
        }

        /// <summary>Copy data in string to a local char array</summary>
        public AntlrInputStream(string input)
        {
            this.data = input.ToCharArray();
            this.n = input.Length;
        }

        /// <summary>This is the preferred constructor for strings as no data is copied</summary>
        public AntlrInputStream(char[] data, int numberOfActualCharsInArray)
        {
            this.data = data;
            this.n = numberOfActualCharsInArray;
        }

        /// <exception cref="System.IO.IOException"/>
        public AntlrInputStream(TextReader r)
            : this(r, InitialBufferSize, ReadBufferSize)
        {
        }

        /// <exception cref="System.IO.IOException"/>
        public AntlrInputStream(TextReader r, int initialSize)
            : this(r, initialSize, ReadBufferSize)
        {
        }

        /// <exception cref="System.IO.IOException"/>
        public AntlrInputStream(TextReader r, int initialSize, int readChunkSize)
        {
            Load(r, initialSize, readChunkSize);
        }

        /// <exception cref="System.IO.IOException"/>
        public AntlrInputStream(Stream input)
            : this(new StreamReader(input), InitialBufferSize)
        {
        }

        /// <exception cref="System.IO.IOException"/>
        public AntlrInputStream(Stream input, int initialSize)
            : this(new StreamReader(input), initialSize)
        {
        }

        /// <exception cref="System.IO.IOException"/>
        public AntlrInputStream(Stream input, int initialSize, int readChunkSize)
            : this(new StreamReader(input), initialSize, readChunkSize)
        {
        }

        /// <exception cref="System.IO.IOException"/>
        public virtual void Load(TextReader r, int size, int readChunkSize)
        {
            if (r == null)
            {
                return;
            }

            data = r.ReadToEnd().ToCharArray();
            n = data.Length;
        }

        protected override int ValueAt(int i)
        {
            return data[i];
        }

        protected override string ConvertDataToString(int start, int count)
        {
            //		System.err.println("data: "+Arrays.toString(data)+", n="+n+
            //                                             ", start="+start+
            //                                             ", stop="+stop);
            return new string(data, start, count);
        }
    }

    /// <summary>
    /// Alternative to
    /// <see cref="AntlrInputStream"/>
    /// which treats the input as a series of Unicode code points,
    /// instead of a series of UTF-16 code units.
    ///
    /// Use this if you need to parse input which potentially contains
    /// Unicode values > U+FFFF.
    /// </summary>
    public class CodePointCharStream : BaseInputCharStream
    {
        private int[] data;

        public CodePointCharStream(string input)
        {
            this.data = new int[input.Length];
            int dataIdx = 0;
            for (int i = 0; i < input.Length; ) {
                var codePoint = Char.ConvertToUtf32(input, i);
                data[dataIdx++] = codePoint;
                if (dataIdx > data.Length) {
                    Array.Resize(ref data, data.Length * 2);
                }
                i += codePoint <= 0xFFFF ? 1 : 2;
            }
            this.n = dataIdx;
        }

        protected override int ValueAt(int i)
        {
            return data[i];
        }

        protected override string ConvertDataToString(int start, int count)
        {
            var sb = new StringBuilder(count);
            for (int i = start; i < start + count; i++) {
                sb.Append(Char.ConvertFromUtf32(data[i]));
            }
            return sb.ToString();
        }
    }
}
