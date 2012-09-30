/*
 [The "BSD license"]
  Copyright (c) 2011 Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/** Do not buffer up the entire char stream. It does keep a small buffer
 *  for efficiency and also buffers while a mark exists (set by the
 *  lookahead prediction in parser). "Unbuffered" here refers to fact
 *  that it doesn't buffer all data, not that's it's on demand loading of char.
 */
public class UnbufferedCharStream implements CharStream {
    /** A moving window buffer of the data being scanned. While there's a
	 *  marker, we keep adding to buffer.  Otherwise, consume() resets
	 *  so we start filling at index 0 again.
	 */
   	protected char[] data;

   	/** How many characters are actually in the buffer; this is not
		the buffer size, that's data.length.
 	 */
   	protected int n;

    /** 0..n-1 index into data of next char; data[p] is LA(1).
	 *  If p == n, we are out of buffered char.
	 */
   	protected int p=0;

	/** Count up with mark() and down with release(). When we release()
	 *  and hit zero, reset buffer to beginning. Copy data[p]..data[n-1]
	 *  to data[0]..data[(n-1)-p].
	 */
	protected int numMarkers = 0;

	protected int lastChar = -1;

	/** Absolute char index. It's the index of the char about to be
	 *  read via LA(1). Goes from 0 to numchar-1 in entire stream.
	 */
    protected int currentCharIndex = 0;

    protected Reader input;

	/** What is name or source of this char stream? */
	public String name;

	/** Useful for subclasses that pull char from other than this.input. */
	public UnbufferedCharStream() {
		this(256);
	}

	/** Useful for subclasses that pull char from other than this.input. */
	public UnbufferedCharStream(int bufferSize) {
		n = 0;
		data = new char[bufferSize];
	}

	public UnbufferedCharStream(InputStream input) {
		this(input, 256);
	}

	public UnbufferedCharStream(Reader input) {
		this(input, 256);
	}

	public UnbufferedCharStream(InputStream input, int bufferSize) {
		this(bufferSize);
		this.input = new InputStreamReader(input);
		fill(1); // prime
	}

	public UnbufferedCharStream(Reader input, int bufferSize) {
		this(bufferSize);
		this.input = input;
		fill(1); // prime
	}

	@Override
	public void consume() {
		// buf always has at least data[p==0] in this method due to ctor
		if ( p==0 ) lastChar = -1; // we're at first char; no LA(-1)
		else lastChar = data[p];   // track last char for LA(-1)
		p++;
		currentCharIndex++;
//		System.out.println("consume p="+p+", numMarkers="+numMarkers+
//						   ", currentCharIndex="+currentCharIndex+", n="+n);
		sync(1);
	}

	/** Make sure we have 'need' elements from current position p. Last valid
	 *  p index is data.size()-1.  p+need-1 is the data index 'need' elements
	 *  ahead.  If we need 1 element, (p+1-1)==p must be < data.size().
	 */
	protected void sync(int want) {
		int need = (p+want-1) - n + 1; // how many more elements we need?
		if ( need > 0 ) fill(need);    // out of elements?
	}

	/** add n elements to buffer */
	public void fill(int n) {
		for (int i=1; i<=n; i++) {
            try {
                int c = nextChar();
                add(c);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
		}
	}

	/** Override to provide different source of characters than this.input */
	protected int nextChar() throws IOException {
		return input.read();
	}

	protected void add(int c) {
		if ( n>=data.length ) {
			char[] newdata = new char[data.length*2]; // resize
            System.arraycopy(data, 0, newdata, 0, data.length);
            data = newdata;
        }
        data[n++] = (char)c;
    }

    @Override
    public int LA(int i) {
		if ( i==-1 ) return lastChar; // special case
        sync(i);
        int index = p + i - 1;
        if ( index < 0 ) throw new IndexOutOfBoundsException();
		if ( index > n ) return CharStream.EOF;
        int c = data[index];
        if ( c==(char)CharStream.EOF ) return CharStream.EOF;
        return c;
    }

    /** Return a marker that we can release later.  Marker happens to be
     *  index into buffer (not index()).
     */
    @Override
    public int mark() {
        int m = p;
		numMarkers++;
//		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//		System.out.println(stackTrace[2].getMethodName()+": mark " + m);
        return m;
    }

	/** Decrement number of markers, resetting buffer if we hit 0.
	 * @param marker
	 */
    @Override
    public void release(int marker) {
		if ( numMarkers==0 ) {
			throw new IllegalStateException("release() called w/o prior matching mark()");
		}
//		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//		System.out.println(stackTrace[2].getMethodName()+": release " + marker);
		numMarkers--;
		if ( numMarkers==0 ) { // can we release buffer?
//			System.out.println("release: shift "+p+".."+(n-1)+" to 0: '"+ new String(data,p,n)+"'");
			// Copy data[p]..data[n-1] to data[0]..data[(n-1)-p], reset ptrs
			// p is last valid char; move nothing if p==n as we have no valid char
			System.arraycopy(data, p, data, 0, n - p); // shift n-p char from p to 0
			n = n - p;
			p = 0;
		}
    }

    @Override
    public int index() {
		return currentCharIndex;
    }

	/** Seek to absolute character index, which might not be in the current
	 *  sliding window.  Move p to index-bufferStartIndex.
	 */
    @Override
    public void seek(int index) {
//		System.out.println("seek "+index);
        // index == to bufferStartIndex should set p to 0
        int i = index - getBufferStartIndex();
        if ( i < 0 || i >= n ) {
            throw new UnsupportedOperationException("seek to index outside buffer: "+
                    index+" not in "+getBufferStartIndex()+".."+(getBufferStartIndex()+n));
        }
        p = i;
		currentCharIndex = index;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
    }

    @Override
    public String getSourceName() {
		return name;
	}

	@Override
	public String getText(Interval interval) {
		int bufferStartIndex = getBufferStartIndex();
		if (interval.a < bufferStartIndex || interval.b >= bufferStartIndex + n) {
			throw new IndexOutOfBoundsException("interval "+interval+" outside buffer: "+
			                    bufferStartIndex+".."+(bufferStartIndex+n));
		}
		// convert from absolute to local index
		int i = interval.a - bufferStartIndex;
		return new String(data, i, interval.length());
	}

	/** For testing.  What's in moving window into data stream from
	 *  current index, LA(1) or data[p], to end of buffer?
	 */
	public String getRemainingBuffer() {
		if ( n==0 ) return null;
		return new String(data,p,n-p);
	}

	/** For testing.  What's in moving window buffer into data stream.
	 *  From 0..p-1 have been consume.
	 */
	public String getBuffer() {
		if ( n==0 ) return null;
		return new String(data,0,n);
	}

	public int getBufferStartIndex() {
		return currentCharIndex - p;
	}
}
