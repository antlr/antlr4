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
    /** A buffer of the data being scanned */
   	protected char[] data;

   	/** How many characters are actually in the buffer */
   	protected int n;

    /** 0..n-1 index into string of next char */
   	protected int p=0;

    protected int earliestMarker = -1;

	/** Absolute char index. It's the index of the char about to be
	 *  read via LA(1). Goes from 0 to numchar-1.
	 */
    protected int currentCharIndex = 0;

    /** Buf is window into stream. This is absolute index of data[0] */
    protected int bufferStartIndex = 0;

    protected Reader input;

	/** What is name or source of this char stream? */
	public String name;

    public UnbufferedCharStream(InputStream input) {
   		this(input, 256);
   	}

   	public UnbufferedCharStream(Reader input) {
        this(input, 256);
   	}

    public UnbufferedCharStream(InputStream input, int bufferSize) {
   		this.input = new InputStreamReader(input);
        data = new char[bufferSize];
   	}

   	public UnbufferedCharStream(Reader input, int bufferSize) {
   		this.input = input;
        data = new char[bufferSize];
   	}

	public void reset() {
		p = 0;
		earliestMarker = -1;
		currentCharIndex = 0;
        bufferStartIndex = 0;
		n = 0;
	}

	@Override
	public void consume() {
		p++;
        currentCharIndex++;
		// have we hit end of buffer when no markers?
		if ( p==n && earliestMarker < 0 ) {
			// if so, it's an opportunity to start filling at index 0 again
//            System.out.println("p=="+n+", no marker; reset buf start index="+currentCharIndex);
            p = 0;
			n = 0;
            bufferStartIndex = currentCharIndex;
        }
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
                int c = input.read();
                add(c);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
		}
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
        if ( p < earliestMarker) {
            // they must have done seek to before min marker
            throw new IllegalArgumentException("can't set marker earlier than previous existing marker: "+p+"<"+ earliestMarker);
        }
        if ( earliestMarker < 0 ) earliestMarker = m; // set first marker
        return m;
    }

    @Override
    public void release(int marker) {
        // release is noop unless we remove earliest. then we don't need to
        // keep anything in buffer. We only care about earliest. Releasing
        // marker other than earliest does nothing as we can just keep in
        // buffer.
        if ( marker < earliestMarker || marker >= n ) {
            throw new IllegalArgumentException("invalid marker: "+
                    marker+" not in "+0+".."+n);
        }
        if ( marker == earliestMarker) earliestMarker = -1;
    }

    @Override
    public int index() {
        return p + bufferStartIndex;
    }

    @Override
    public void seek(int index) {
        // index == to bufferStartIndex should set p to 0
        int i = index - bufferStartIndex;
        if ( i < 0 || i >= n ) {
            throw new UnsupportedOperationException("seek to index outside buffer: "+
                    index+" not in "+bufferStartIndex+".."+(bufferStartIndex+n));
        }
        p = i;
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
		if (interval.a < bufferStartIndex || interval.b >= bufferStartIndex + n) {
			throw new UnsupportedOperationException();
		}

		return new String(data, interval.a, interval.length());
    }
}
