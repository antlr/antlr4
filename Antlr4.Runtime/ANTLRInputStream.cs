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
using Sharpen;

namespace Antlr4.Runtime
{
	/// <summary>Vacuum all input from a Reader/InputStream and then treat it like a char[] buffer.
	/// 	</summary>
	/// <remarks>
	/// Vacuum all input from a Reader/InputStream and then treat it like a char[] buffer.
	/// Can also pass in a string or char[] to use.
	/// If you need encoding, pass in stream/reader with correct encoding.
	/// </remarks>
	public class ANTLRInputStream : CharStream
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

		public ANTLRInputStream()
		{
		}

		/// <summary>Copy data in string to a local char array</summary>
		public ANTLRInputStream(string input)
		{
			this.data = input.ToCharArray();
			this.n = input.Length;
		}

		/// <summary>This is the preferred constructor for strings as no data is copied</summary>
		public ANTLRInputStream(char[] data, int numberOfActualCharsInArray)
		{
			this.data = data;
			this.n = numberOfActualCharsInArray;
		}

		/// <exception cref="System.IO.IOException"></exception>
		public ANTLRInputStream(StreamReader r) : this(r, InitialBufferSize, ReadBufferSize
			)
		{
		}

		/// <exception cref="System.IO.IOException"></exception>
		public ANTLRInputStream(StreamReader r, int initialSize) : this(r, initialSize, ReadBufferSize
			)
		{
		}

		/// <exception cref="System.IO.IOException"></exception>
		public ANTLRInputStream(StreamReader r, int initialSize, int readChunkSize)
		{
			Load(r, initialSize, readChunkSize);
		}

		/// <exception cref="System.IO.IOException"></exception>
		public ANTLRInputStream(InputStream input) : this(new InputStreamReader(input), InitialBufferSize
			)
		{
		}

		/// <exception cref="System.IO.IOException"></exception>
		public ANTLRInputStream(InputStream input, int initialSize) : this(new InputStreamReader
			(input), initialSize)
		{
		}

		/// <exception cref="System.IO.IOException"></exception>
		public ANTLRInputStream(InputStream input, int initialSize, int readChunkSize) : 
			this(new InputStreamReader(input), initialSize, readChunkSize)
		{
		}

		/// <exception cref="System.IO.IOException"></exception>
		public virtual void Load(StreamReader r, int size, int readChunkSize)
		{
			if (r == null)
			{
				return;
			}
			if (size <= 0)
			{
				size = InitialBufferSize;
			}
			if (readChunkSize <= 0)
			{
				readChunkSize = ReadBufferSize;
			}
			// System.out.println("load "+size+" in chunks of "+readChunkSize);
			try
			{
				// alloc initial buffer size.
				data = new char[size];
				// read all the data in chunks of readChunkSize
				int numRead = 0;
				int p = 0;
				do
				{
					if (p + readChunkSize > data.Length)
					{
						// overflow?
						// System.out.println("### overflow p="+p+", data.length="+data.length);
						data = Arrays.CopyOf(data, data.Length * 2);
					}
					numRead = r.Read(data, p, readChunkSize);
					// System.out.println("read "+numRead+" chars; p was "+p+" is now "+(p+numRead));
					p += numRead;
				}
				while (numRead != -1);
				// while not EOF
				// set the actual size of the data available;
				// EOF subtracted one above in p+=numRead; add one back
				n = p + 1;
			}
			finally
			{
				//System.out.println("n="+n);
				r.Close();
			}
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
				System.Diagnostics.Debug.Assert(La(1) == IntStream.Eof);
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
					return IntStream.Eof;
				}
			}
			// invalid; no char before first char
			if ((p + i - 1) >= n)
			{
				//System.out.println("char LA("+i+")=EOF; p="+p);
				return IntStream.Eof;
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
		public virtual int Index()
		{
			return p;
		}

		public virtual int Size()
		{
			return n;
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
			// seek forward, consume until p hits index
			while (p < index && index < n)
			{
				Consume();
			}
		}

		public override string GetText(Interval interval)
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

		public virtual string GetSourceName()
		{
			return name;
		}

		public override string ToString()
		{
			return new string(data);
		}
	}
}
