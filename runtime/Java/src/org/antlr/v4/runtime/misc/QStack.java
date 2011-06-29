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

import java.util.EmptyStackException;

/** A quicker stack than Stack */
public class QStack<T> {
	T[] elements;
	public int sp = -1;

	public QStack() {
		elements = (T[])new Object[10];
	}

	public QStack(QStack s) {
		elements = (T[])new Object[s.elements.length];
		System.arraycopy(s.elements, 0, elements, 0, s.elements.length);
		this.sp = s.sp;
	}

	public void push(T fset) {
		if ( (sp+1)>=elements.length ) {
			T[] f = (T[])new Object[elements.length*2];
			System.arraycopy(elements, 0, f, 0, elements.length);
			elements = f;
		}
		elements[++sp] = fset;
	}

	public T peek() {
		if ( sp<0 ) throw new EmptyStackException();
		return elements[sp];
	}

	public T get(int i) {
		if ( i<0 ) throw new IllegalArgumentException("i<0");
		if ( i>sp ) throw new IllegalArgumentException("i>"+sp);
		return elements[sp];
	}

	public T pop() {
		if ( sp<0 ) throw new EmptyStackException();
		T o = elements[sp];
		elements[sp] = null; // let gc reclaim that element
		sp--;
		return o;
	}

	public void clear() { sp = -1; }
}
