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

/** Buffer up all char from stream, but pull chars from input on-demand,
 *  in a lazy fashion.  This allows us build interactive programs using
 *  ANTLR like shells and REPLs.  This is a kind of ANTLRInputStream
 *  that does not load() everything up front.
 */
public class LazyCharStream implements CharStream {
	/** A buffer of the data being scanned */
	protected char[] data;

	/** This is where we will read next char. It's also how many characters
	 *  we've actually read into the buffer. It's less than or equal
	 *  to data.length.
	 */
	protected int next;

	/** 0..n-1 index into string of next lookahead char. LA(1) is data[p] */
	protected int p=0;

	protected Reader input;

	/** What is name or source of this char stream? */
	public String name;

	public LazyCharStream(InputStream input) {
		this(input, 256);
	}

	public LazyCharStream(Reader input) {
		this(input, 256);
	}

	public LazyCharStream(InputStream input, int initialSize) {
		this.input = new InputStreamReader(input);
		data = new char[initialSize];
	}

	public LazyCharStream(Reader input, int initialSize) {
		this.input = input;
		data = new char[initialSize];
	}

	public void reset() {
		p = 0;
		next = 0;
	}

	@Override
	public void consume() {
		sync(1); // make sure we have read current char
		p++;
	}

	@Override
	public int LA(int i) {
		sync(i);
		int index = p + i - 1;
		if ( index < 0 ) throw new IndexOutOfBoundsException();
		if ( index > next) return CharStream.EOF;
		int c = data[index];
		if ( c==(char)CharStream.EOF ) return CharStream.EOF;
		return c;
	}

	/** Return a marker that we can release later.  Marker happens to be
	 *  index into buffer (not index()).
	 */
	@Override
	public int mark() {
		return -1;
	}

	@Override
	public void release(int marker) { }

	@Override
	public int index() {
		return p;
	}

	@Override
	public void seek(int index) {
		if ( index<=p ) {
			p = index; // just jump; don't update stream state (line, ...)
			return;
		}
		// seek forward, consume until p hits index
		while ( p<index && index< next) {
			consume();
		}
	}

	/** Return num char we've read so far */
	@Override
	public int size() {
		return next;
	}

	@Override
	public String getSourceName() {
		return name;
	}

	@Override
	public String getText(Interval interval) {
		int start = interval.a;
		int stop = interval.b;
		if ( stop < start ) {
			throw new IllegalArgumentException("inverted interval");
		}
		if ( stop >= next) {
			int need = stop - next + 1;
			fill(need);
		}
		int count = stop - start + 1;
		return new String(data, start, count);
	}

	/** Make sure we have 'want' elements from current position p. Last valid
	 *  p index is data.size()-1.  p+want-1 is the data index 'want' elements
	 *  ahead.  If we want 1 element, (p+1-1)==p must be < n.
	 */
	protected void sync(int want) {
		// E.g., sync(1) means current p must have a char. If p>=next, we
		// need a char
		int need = (p+want-1) - next + 1; // how many more elements we need?
		if ( need > 0 ) fill(need);       // out of elements?
	}

	/** add n elements to buffer */
	public void fill(int n) {
		for (int i=1; i<=n; i++) {
			try {
				int c = input.read();
				add(c);
				if ( c==EOF ) break;
			}
			catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	}

	protected void add(int c) {
		if ( next >=data.length ) {
			char[] newdata = new char[data.length*2]; // resize
			System.arraycopy(data, 0, newdata, 0, data.length);
			data = newdata;
		}
		data[next++] = (char)c;
	}
}
