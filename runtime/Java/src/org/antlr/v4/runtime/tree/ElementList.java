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

package org.antlr.v4.runtime.tree;

import java.util.*;

/** This list tracks elements to left of -> for use on right of -> */
public class ElementList<E> extends ArrayList<E> {
	protected ASTAdaptor<E> adaptor;

	/** Once a node / subtree has been used in a stream, it must be dup'd
	 *  from then on.
	 */
	protected HashSet<Integer> used = new HashSet<Integer>();

	public class ElementListIterator implements Iterator<E> {
		int cursor = 0;

		/** If just 1 element, we still track cursor; next() will dup if
		 *  cursor beyond 1 element.
		 */
		@Override
		public boolean hasNext() {
			int n = size();
			return (n==1 && cursor<1) || (n>1 && cursor<n);
		}

		@Override
		public E next() {
			int n = size();
			if ( n == 0 ) throw new RewriteEmptyStreamException("n/a");
			if ( cursor >= n) { // out of elements?
				if ( n == 1 ) { // if size is 1, it's ok; return and we'll dup
					return adaptor.dupTree( get(0) );
				}
				// out of elements and size was not 1, so we can't dup
				throw new RewriteCardinalityException("size=="+n+" and out of elements");
			}

			// we have elements
			if ( n == 1 ) {
				cursor++; // move cursor even for single element list
				return adaptor.dupTree( get(0) );
			}
			// must have more than one in list, pull from elements
			E e = get(cursor);
			cursor++;
			return e;
		}

		@Override
		public void remove() { throw new UnsupportedOperationException(); }
	}

	public ElementList(ASTAdaptor<E> adaptor) {
		this.adaptor = adaptor;
	}

	@Override
	public E get(int index) {
		E o = super.get(index);
		if ( used.contains(index) ) {
			return adaptor.dupTree( o );
		}
		used.add(index); // any subsequent ref must be dup'd
		return o;
	}

	@Override
	public Iterator<E> iterator() {
		return new ElementListIterator();
	}
}
