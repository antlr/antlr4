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
using System.IO;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
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
    public class AntlrInputStream : ICharStream
    {
        public const int ReadBufferSize = 1024;

        public const int InitialBufferSize = 1024;

        /// <summary>The data being scanned</summary>
        protected internal char[] data;

        /// <summary>How many characters are actually in the buffer</summary>
        protected internal int n;

        /// <summary>0..n-1 index into string of next char</summary>
        protected internal int p = 0;

        /// <summary>What is name or source of this char stream?</summary>
        public string name;

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
                System.Diagnostics.Debug.Assert(La(1) == IntStreamConstants.Eof);
                throw new InvalidOperationException("cannot consume EOF");
            }
            //System.out.println("prev p="+p+", c="+(char)data[p]);
            if (p < n)
            {
                p++;
            }
        }

        //System.out.println("p moves to "+p+" (c='"+(char)data[p]+"')");
        public virtual int La(int i)
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
                    return IntStreamConstants.Eof;
                }
            }
            // invalid; no char before first char
            if ((p + i - 1) >= n)
            {
                //System.out.println("char LA("+i+")=EOF; p="+p);
                return IntStreamConstants.Eof;
            }
            //System.out.println("char LA("+i+")="+(char)data[p+i-1]+"; p="+p);
            //System.out.println("LA("+i+"); p="+p+" n="+n+" data.length="+data.length);
            return data[p + i - 1];
        }

        public virtual int Lt(int i)
        {
            return La(i);
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
            //		System.err.println("data: "+Arrays.toString(data)+", n="+n+
            //						   ", start="+start+
            //						   ", stop="+stop);
            return new string(data, start, count);
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

        public override string ToString()
        {
            return new string(data);
        }
    }
}
