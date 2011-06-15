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
		return elements[sp--];
	}

	public void clear() { sp = -1; }
}
