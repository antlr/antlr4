/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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
package org.antlr.v4.runtime.misc;

import java.util.NoSuchElementException;

/** A lookahead queue that knows how to mark/release locations
 *  in the buffer for backtracking purposes. Any markers force the FastQueue
 *  superclass to keep all tokens until no more markers; then can reset
 *  to avoid growing a huge buffer.
 */
public abstract class LookaheadStream<T> extends FastQueue<T> {
    public static final int UNINITIALIZED_EOF_ELEMENT_INDEX = Integer.MAX_VALUE;

    /** Absolute token index. It's the index of the symbol about to be
	 *  read via LT(1). Goes from 0 to numtokens.
     */
    protected int currentElementIndex = 0;

    protected T prevElement;

    /** Track object returned by nextElement upon end of stream;
     *  Return it later when they ask for LT passed end of input.
     */
    public T eof = null;

    /** Track the last mark() call result value for use in rewind(). */
    protected int lastMarker;

    /** tracks how deep mark() calls are nested */
    protected int markDepth = 0;

    public void reset() {
        super.reset();
        currentElementIndex = 0;
        p = 0;
        prevElement=null;
    }

    /** Implement nextElement to supply a stream of elements to this
     *  lookahead buffer.  Return eof upon end of the stream we're pulling from.
     */
    public abstract T nextElement();

    public abstract boolean isEOF(T o);

    /** Get and remove first element in queue; override FastQueue.remove();
     *  it's the same, just checks for backtracking.
     */
    public T remove() {
        T o = elementAt(0);
        p++;
        // have we hit end of buffer and not backtracking?
        if ( p == data.size() && markDepth==0 ) {
            // if so, it's an opportunity to start filling at index 0 again
            clear(); // size goes to 0, but retains memory
        }
        return o;
    }

    /** Make sure we have at least one element to remove, even if EOF */
    public void consume() {
        syncAhead(1);
        prevElement = remove();
        currentElementIndex++;
    }

    /** Make sure we have 'need' elements from current position p. Last valid
     *  p index is data.size()-1.  p+need-1 is the data index 'need' elements
     *  ahead.  If we need 1 element, (p+1-1)==p must be < data.size().
     */
    protected void syncAhead(int need) {
        int n = (p+need-1) - data.size() + 1; // how many more elements we need?
        if ( n > 0 ) fill(n);                 // out of elements?
    }

    /** add n elements to buffer */
    public void fill(int n) {
        for (int i=1; i<=n; i++) {
            T o = nextElement();
            if ( isEOF(o) ) eof = o;
            data.add(o);
        }
    }

    /** Size of entire stream is unknown; we only know buffer size from FastQueue */
    public int size() { throw new UnsupportedOperationException("streams are of unknown size"); }

    public T LT(int k) {
		if ( k==0 ) {
			return null;
		}
		if ( k<0 ) return LB(-k);
		//System.out.print("LT(p="+p+","+k+")=");
        syncAhead(k);
        if ( (p+k-1) > data.size() ) return eof;
        return elementAt(k-1);
	}

    public int index() { return currentElementIndex; }

	public int mark() {
        markDepth++;
        lastMarker = p; // track where we are in buffer not absolute token index
        return lastMarker;
	}

	public void release(int marker) {
		// no resources to release
	}

	public void rewind(int marker) {
        markDepth--;
        seek(marker); // assume marker is top
        // release(marker); // waste of call; it does nothing in this class
    }

	public void rewind() {
        seek(lastMarker); // rewind but do not release marker
    }

    /** Seek to a 0-indexed position within data buffer.  Can't handle
     *  case where you seek beyond end of existing buffer.  Normally used
     *  to seek backwards in the buffer. Does not force loading of nodes.
     *  Doesn't see to absolute position in input stream since this stream
     *  is unbuffered. Seeks only into our moving window of elements.
     */
    public void seek(int index) { p = index; }

    protected T LB(int k) {
        if ( k==1 ) return prevElement;
        throw new NoSuchElementException("can't look backwards more than one token in this stream");
    }
}
