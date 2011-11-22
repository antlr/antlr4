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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ANTLRUnbufferedInputStream implements CharStream {
    /** A buffer of the data being scanned */
   	protected int[] data = new int[256];

   	/** How many characters are actually in the buffer */
   	protected int n;

    /** 0..n-1 index into string of next char */
   	protected int p=0;

    protected int minMarker = -1;

	/** Absolute char index. It's the index of the char about to be
	 *  read via LA(1). Goes from 0 to numchar-1.
	 */
	protected int currentElementIndex = 0;

    protected Reader input;

	/** What is name or source of this char stream? */
	public String name;

	public ANTLRUnbufferedInputStream(InputStream input) {
		this.input = new InputStreamReader(input);
	}

	public ANTLRUnbufferedInputStream(Reader input) {
		this.input = input;
	}

	public void reset() {
		p = 0;
		minMarker = -1;
		currentElementIndex = 0;
		n = 0;
	}

	@Override
	public void consume() {
		sync(1);
		p++;
		// have we hit end of buffer when no markers?
		if ( p==n && minMarker<0 ) {
			// if so, it's an opportunity to start filling at index 0 again
            p = 0;
			n = 0;
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
        if ( p>=data.length ) {
			int[] newdata = new int[data.length*2]; // resize
            System.arraycopy(data, 0, newdata, 0, data.length);
            data = newdata;
        }
        data[n++] = c;
    }

    @Override
    public int LA(int i) {
        sync(i);
        int index = p + i - 1;
        if ( index < 0 ) throw new IndexOutOfBoundsException();
		if ( index > n ) return CharStream.EOF;
        return data[index];
    }

    @Override
    public int mark() {
        int m = p;
        if ( p < minMarker ) {
            throw new IllegalArgumentException("can't set marker earlier than previous existing marker: "+p+"<"+minMarker);
        }
        if ( minMarker<0 ) minMarker = m; // set first marker
        return m;
    }

    @Override
    public void release(int marker) {
        if ( marker == minMarker ) minMarker = -1;
    }

    @Override
    public int index() {
        return 0;
    }

    @Override
    public void seek(int index) {
        p = index;
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
    public String substring(int start, int stop) {
        return null; // map to buffer indexes
    }
}
