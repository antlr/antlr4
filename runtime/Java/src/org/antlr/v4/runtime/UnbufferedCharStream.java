/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

/** Do not buffer up the entire char stream. It does keep a small buffer
 *  for efficiency and also buffers while a mark exists (set by the
 *  lookahead prediction in parser). "Unbuffered" here refers to fact
 *  that it doesn't buffer all data, not that's it's on demand loading of char.
 */
public class UnbufferedCharStream implements CharStream {
	/**
	 * A moving window buffer of the data being scanned. While there's a marker,
	 * we keep adding to buffer. Otherwise, {@link #consume consume()} resets so
	 * we start filling at index 0 again.
	 */
   	protected char[] data;

	/**
	 * The number of characters currently in {@link #data data}.
	 *
	 * <p>This is not the buffer capacity, that's {@code data.length}.</p>
	 */
   	protected int n;

	/**
	 * 0..n-1 index into {@link #data data} of next character.
	 *
	 * <p>The {@code LA(1)} character is {@code data[p]}. If {@code p == n}, we are
	 * out of buffered characters.</p>
	 */
   	protected int p=0;

	/**
	 * Count up with {@link #mark mark()} and down with
	 * {@link #release release()}. When we {@code release()} the last mark,
	 * {@code numMarkers} reaches 0 and we reset the buffer. Copy
	 * {@code data[p]..data[n-1]} to {@code data[0]..data[(n-1)-p]}.
	 */
	protected int numMarkers = 0;

	/**
	 * This is the {@code LA(-1)} character for the current position.
	 */
	protected int lastChar = -1;

	/**
	 * When {@code numMarkers > 0}, this is the {@code LA(-1)} character for the
	 * first character in {@link #data data}. Otherwise, this is unspecified.
	 */
	protected int lastCharBufferStart;

	/**
	 * Absolute character index. It's the index of the character about to be
	 * read via {@code LA(1)}. Goes from 0 to the number of characters in the
	 * entire stream, although the stream size is unknown before the end is
	 * reached.
	 */
    protected int currentCharIndex = 0;

    protected Reader input;

	/** The name or source of this char stream. */
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
		if (LA(1) == IntStream.EOF) {
			throw new IllegalStateException("cannot consume EOF");
		}

		// buf always has at least data[p==0] in this method due to ctor
		lastChar = data[p];   // track last char for LA(-1)

		if (p == n-1 && numMarkers==0) {
			n = 0;
			p = -1; // p++ will leave this at 0
			lastCharBufferStart = lastChar;
		}

		p++;
		currentCharIndex++;
		sync(1);
	}

	/**
	 * Make sure we have 'need' elements from current position {@link #p p}.
	 * Last valid {@code p} index is {@code data.length-1}. {@code p+need-1} is
	 * the char index 'need' elements ahead. If we need 1 element,
	 * {@code (p+1-1)==p} must be less than {@code data.length}.
	 */
	protected void sync(int want) {
		int need = (p+want-1) - n + 1; // how many more elements we need?
		if ( need > 0 ) {
			fill(need);
		}
	}

	/**
	 * Add {@code n} characters to the buffer. Returns the number of characters
	 * actually added to the buffer. If the return value is less than {@code n},
	 * then EOF was reached before {@code n} characters could be added.
	 */
	protected int fill(int n) {
		for (int i=0; i<n; i++) {
			if (this.n > 0 && data[this.n - 1] == (char)IntStream.EOF) {
				return i;
			}

			try {
				int c = nextChar();
				add(c);
			}
			catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}

		return n;
	}

	/**
	 * Override to provide different source of characters than
	 * {@link #input input}.
	 */
	protected int nextChar() throws IOException {
		return input.read();
	}

	protected void add(int c) {
		if ( n>=data.length ) {
			data = Arrays.copyOf(data, data.length * 2);
        }
        data[n++] = (char)c;
    }

    @Override
    public int LA(int i) {
		if ( i==-1 ) return lastChar; // special case
        sync(i);
        int index = p + i - 1;
        if ( index < 0 ) throw new IndexOutOfBoundsException();
		if ( index >= n ) return IntStream.EOF;
        char c = data[index];
        if ( c==(char)IntStream.EOF ) return IntStream.EOF;
        return c;
    }

	/**
	 * Return a marker that we can release later.
	 *
	 * <p>The specific marker value used for this class allows for some level of
	 * protection against misuse where {@code seek()} is called on a mark or
	 * {@code release()} is called in the wrong order.</p>
	 */
    @Override
    public int mark() {
		if (numMarkers == 0) {
			lastCharBufferStart = lastChar;
		}

		int mark = -numMarkers - 1;
		numMarkers++;
		return mark;
    }

	/** Decrement number of markers, resetting buffer if we hit 0.
	 * @param marker
	 */
    @Override
    public void release(int marker) {
		int expectedMark = -numMarkers;
		if ( marker!=expectedMark ) {
			throw new IllegalStateException("release() called with an invalid marker.");
		}

		numMarkers--;
		if ( numMarkers==0 && p > 0 ) { // release buffer when we can, but don't do unnecessary work
			// Copy data[p]..data[n-1] to data[0]..data[(n-1)-p], reset ptrs
			// p is last valid char; move nothing if p==n as we have no valid char
			System.arraycopy(data, p, data, 0, n - p); // shift n-p char from p to 0
			n = n - p;
			p = 0;
			lastCharBufferStart = lastChar;
		}
    }

    @Override
    public int index() {
		return currentCharIndex;
    }

	/** Seek to absolute character index, which might not be in the current
	 *  sliding window.  Move {@code p} to {@code index-bufferStartIndex}.
	 */
    @Override
    public void seek(int index) {
		if (index == currentCharIndex) {
			return;
		}

		if (index > currentCharIndex) {
			sync(index - currentCharIndex);
			index = Math.min(index, getBufferStartIndex() + n - 1);
		}

        // index == to bufferStartIndex should set p to 0
        int i = index - getBufferStartIndex();
        if ( i < 0 ) {
			throw new IllegalArgumentException("cannot seek to negative index " + index);
		}
		else if (i >= n) {
            throw new UnsupportedOperationException("seek to index outside buffer: "+
                    index+" not in "+getBufferStartIndex()+".."+(getBufferStartIndex()+n));
        }

		p = i;
		currentCharIndex = index;
		if (p == 0) {
			lastChar = lastCharBufferStart;
		}
		else {
			lastChar = data[p-1];
		}
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
    }

    @Override
    public String getSourceName() {
		if (name == null || name.isEmpty()) {
			return UNKNOWN_SOURCE_NAME;
		}

		return name;
	}

	@Override
	public String getText(Interval interval) {
		if (interval.a < 0 || interval.b < interval.a - 1) {
			throw new IllegalArgumentException("invalid interval");
		}

		int bufferStartIndex = getBufferStartIndex();
		if (n > 0 && data[n - 1] == Character.MAX_VALUE) {
			if (interval.a + interval.length() > bufferStartIndex + n) {
				throw new IllegalArgumentException("the interval extends past the end of the stream");
			}
		}

		if (interval.a < bufferStartIndex || interval.b >= bufferStartIndex + n) {
			throw new UnsupportedOperationException("interval "+interval+" outside buffer: "+
			                    bufferStartIndex+".."+(bufferStartIndex+n-1));
		}
		// convert from absolute to local index
		int i = interval.a - bufferStartIndex;
		return new String(data, i, interval.length());
	}

	protected final int getBufferStartIndex() {
		return currentCharIndex - p;
	}
}
