package org.antlr.v4.runtime.misc;

import java.util.EmptyStackException;

/** A quicker stack than Stack */
public class QStack<T> {
	Object[] elements;
	int sp = -1;

	public void push(T fset) {
		if ( (sp+1)>=elements.length ) {
			Object[] f = new Object[elements.length*2];
			System.arraycopy(elements, 0, f, 0, elements.length);
			elements = f;
		}
		elements[++sp] = fset;
	}

	public T peek() {
		if ( sp<0 ) throw new EmptyStackException();
		return (T)elements[sp];
	}

	public T pop() {
		if ( sp<0 ) throw new EmptyStackException();
		return (T)elements[sp--];
	}
}
