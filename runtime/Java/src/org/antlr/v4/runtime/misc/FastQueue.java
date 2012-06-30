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
package org.antlr.v4.runtime.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/** A queue that can dequeue and get(i) in O(1) and grow arbitrarily large.
 *  A linked list is fast at dequeue but slow at get(i).  An array is
 *  the reverse.  This is O(1) for both operations.
 *
 *  List grows until you dequeue last element at end of buffer. Then
 *  it resets to start filling at 0 again.  If adds/removes are balanced, the
 *  buffer will not grow too large.
 *
 *  No iterator stuff as that's not how we'll use it.
 */
public class FastQueue<T> {
    /** dynamically-sized buffer of elements */
    protected List<T> data = new ArrayList<T>();
    /** index of next element to fill */
    protected int p = 0;

    public void reset() { clear(); }
    public void clear() { p = 0; data.clear(); }

    /** Get and remove first element in queue */
    public T remove() {
        T o = elementAt(0);
        p++;
        // have we hit end of buffer?
        if ( p == data.size() ) {
            // if so, it's an opportunity to start filling at index 0 again
            clear(); // size goes to 0, but retains memory
        }
        return o;
    }

    public void add(T o) { data.add(o); }

    public int size() { return data.size() - p; }

    public T head() { return elementAt(0); }

    /** Return element i elements ahead of current element.  i==0 gets
     *  current element.  This is not an absolute index into the data list
     *  since p defines the start of the real list.
     */
    public T elementAt(int i) {
		int absIndex = p + i;
		if ( absIndex >= data.size() ) {
            throw new NoSuchElementException("queue index "+ absIndex +" > last index "+(data.size()-1));
        }
        if ( absIndex < 0 ) {
            throw new NoSuchElementException("queue index "+ absIndex +" < 0");
        }
        return data.get(absIndex);
    }

    /** Return string of current buffer contents; non-destructive */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        int n = size();
        for (int i=0; i<n; i++) {
            buf.append(elementAt(i));
            if ( (i+1)<n ) buf.append(" ");
        }
        return buf.toString();
    }
}
