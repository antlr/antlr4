/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

/**
 * Vacuum all input from a {@link Reader}/{@link InputStream} and then treat it
 * like a {@code char[]} buffer. Can also pass in a {@link String} or
 * {@code char[]} to use.
 *
 * <p>If you need encoding, pass in stream/reader with correct encoding.</p>
 *
 * @deprecated as of 4.7 Please use {@link CharStreams} interface.
 */
public class ANTLRInputStream implements CharStream {
    public static final int READ_BUFFER_SIZE = 1024;
   	public static final int INITIAL_BUFFER_SIZE = 1024;

	/** The data being scanned */
	protected char[] data;

	/** How many characters are actually in the buffer */
	protected int n;

	/** 0..n-1 index into string of next char */
	protected int p=0;

	/** What is name or source of this char stream? */
	public String name;

    public ANTLRInputStream() { }

	/** Copy data in string to a local char array */
	public ANTLRInputStream(String input) {
		this.data = input.toCharArray();
		this.n = input.length();
	}

	/** This is the preferred constructor for strings as no data is copied */
	public ANTLRInputStream(char[] data, int numberOfActualCharsInArray) {
		this.data = data;
		this.n = numberOfActualCharsInArray;
	}

    public ANTLRInputStream(Reader r) throws IOException {
        this(r, INITIAL_BUFFER_SIZE, READ_BUFFER_SIZE);
    }

    public ANTLRInputStream(Reader r, int initialSize) throws IOException {
        this(r, initialSize, READ_BUFFER_SIZE);
    }

    public ANTLRInputStream(Reader r, int initialSize, int readChunkSize) throws IOException {
        load(r, initialSize, readChunkSize);
    }

	public ANTLRInputStream(InputStream input) throws IOException {
		this(new InputStreamReader(input), INITIAL_BUFFER_SIZE);
	}

	public ANTLRInputStream(InputStream input, int initialSize) throws IOException {
		this(new InputStreamReader(input), initialSize);
	}

	public ANTLRInputStream(InputStream input, int initialSize, int readChunkSize) throws IOException {
		this(new InputStreamReader(input), initialSize, readChunkSize);
	}

	public void load(Reader r, int size, int readChunkSize)
		throws IOException
	{
		if ( r==null ) {
			return;
		}
		if ( size<=0 ) {
			size = INITIAL_BUFFER_SIZE;
		}
		if ( readChunkSize<=0 ) {
			readChunkSize = READ_BUFFER_SIZE;
   		}
   		// System.out.println("load "+size+" in chunks of "+readChunkSize);
   		try {
   			// alloc initial buffer size.
   			data = new char[size];
   			// read all the data in chunks of readChunkSize
   			int numRead=0;
   			int p = 0;
   			do {
   				if ( p+readChunkSize > data.length ) { // overflow?
   					// System.out.println("### overflow p="+p+", data.length="+data.length);
   					data = Arrays.copyOf(data, data.length * 2);
   				}
   				numRead = r.read(data, p, readChunkSize);
   				// System.out.println("read "+numRead+" chars; p was "+p+" is now "+(p+numRead));
   				p += numRead;
   			} while (numRead!=-1); // while not EOF
   			// set the actual size of the data available;
   			// EOF subtracted one above in p+=numRead; add one back
   			n = p+1;
   			//System.out.println("n="+n);
   		}
   		finally {
   			r.close();
   		}
   	}

	/** Reset the stream so that it's in the same state it was
	 *  when the object was created *except* the data array is not
	 *  touched.
	 */
	public void reset() {
		p = 0;
	}

    @Override
    public void consume() {
		if (p >= n) {
			assert LA(1) == IntStream.EOF;
			throw new IllegalStateException("cannot consume EOF");
		}

		//System.out.println("prev p="+p+", c="+(char)data[p]);
        if ( p < n ) {
            p++;
			//System.out.println("p moves to "+p+" (c='"+(char)data[p]+"')");
        }
    }

    @Override
    public int LA(int i) {
		if ( i==0 ) {
			return 0; // undefined
		}
		if ( i<0 ) {
			i++; // e.g., translate LA(-1) to use offset i=0; then data[p+0-1]
			if ( (p+i-1) < 0 ) {
				return IntStream.EOF; // invalid; no char before first char
			}
		}

		if ( (p+i-1) >= n ) {
            //System.out.println("char LA("+i+")=EOF; p="+p);
            return IntStream.EOF;
        }
        //System.out.println("char LA("+i+")="+(char)data[p+i-1]+"; p="+p);
		//System.out.println("LA("+i+"); p="+p+" n="+n+" data.length="+data.length);
		return data[p+i-1];
    }

	public int LT(int i) {
		return LA(i);
	}

	/** Return the current input symbol index 0..n where n indicates the
     *  last symbol has been read.  The index is the index of char to
	 *  be returned from LA(1).
     */
    @Override
    public int index() {
        return p;
    }

	@Override
	public int size() {
		return n;
	}

    /** mark/release do nothing; we have entire buffer */
	@Override
	public int mark() {
		return -1;
    }

	@Override
	public void release(int marker) {
	}

	/** consume() ahead until p==index; can't just set p=index as we must
	 *  update line and charPositionInLine. If we seek backwards, just set p
	 */
	@Override
	public void seek(int index) {
		if ( index<=p ) {
			p = index; // just jump; don't update stream state (line, ...)
			return;
		}
		// seek forward, consume until p hits index or n (whichever comes first)
		index = Math.min(index, n);
		while ( p<index ) {
			consume();
		}
	}

	@Override
	public String getText(Interval interval) {
		int start = interval.a;
		int stop = interval.b;
		if ( stop >= n ) stop = n-1;
		int count = stop - start + 1;
		if ( start >= n ) return "";
//		System.err.println("data: "+Arrays.toString(data)+", n="+n+
//						   ", start="+start+
//						   ", stop="+stop);
		return new String(data, start, count);
	}

	@Override
	public String getSourceName() {
		if (name == null || name.isEmpty()) {
			return UNKNOWN_SOURCE_NAME;
		}

		return name;
	}

    @Override
    public String toString() { return new String(data); }
}
