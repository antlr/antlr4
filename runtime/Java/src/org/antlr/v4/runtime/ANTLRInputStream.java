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

/** Vacuum all input from a Reader/InputStream and then treat it like a char[] buffer.
 *  Can also pass in a string or char[] to use.
 *
 *  If you need encoding, pass in stream/reader with correct encoding.
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

    public ANTLRInputStream(Reader r, int size) throws IOException {
        this(r, size, READ_BUFFER_SIZE);
    }

    public ANTLRInputStream(Reader r, int size, int readChunkSize) throws IOException {
        load(r, size, readChunkSize);
    }

    public ANTLRInputStream(InputStream input) throws IOException {
        this(new InputStreamReader(input), INITIAL_BUFFER_SIZE);
    }

    public ANTLRInputStream(InputStream input, int size) throws IOException {
        this(new InputStreamReader(input), size);
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
   					char[] newdata = new char[data.length*2]; // resize
   					System.arraycopy(data, 0, newdata, 0, data.length);
   					data = newdata;
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
				return CharStream.EOF; // invalid; no char before first char
			}
		}

		if ( (p+i-1) >= n ) {
            //System.out.println("char LA("+i+")=EOF; p="+p);
            return CharStream.EOF;
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
		// seek forward, consume until p hits index
		while ( p<index && index<n ) {
			consume();
		}
	}

	@Override
	public String substring(int start, int stop) {
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
		return name;
	}

    public String toString() { return new String(data); }
}
