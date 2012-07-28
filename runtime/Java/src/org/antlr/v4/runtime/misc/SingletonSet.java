package org.antlr.v4.runtime.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** A set with a single element. */
public class SingletonSet<T> implements EquivalenceSet<T> {
	protected T element = null;

	public SingletonSet(T o) {
		element = o;
	}

	@Override
	public T absorb(T o) {
		if ( o==null ) {
			element = o;
			return o;
		}
		if ( element.equals(o) ) return element;
		throw new IllegalStateException("Can't add more than one to a singleton set");
	}

	@Override
	public int hashCode(T o) {
		return o!=null ? o.hashCode() : 0;
	}

	@Override
	public boolean equals(T a, T b) {
		return a==b || a.equals(b);
	}

	@Override
	public boolean add(T o) {
		T a = absorb(o);
		return a!=o;
	}

	@Override
	public int size() {
		return element!=null ? 1 : 0;
	}

	@Override
	public boolean isEmpty() {
		return element==null;
	}

	@Override
	public boolean contains(Object o) {
		return element!=null &&
			   (o==element || element.equals(o));
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			boolean returned = false;
			@Override
			public boolean hasNext() { return !returned; }

			@Override
			public T next() {
				if ( hasNext() ) return element;
				throw new NoSuchElementException();
			}

			@Override
			public void remove() { element = null; }
		};
	}

	@Override
	public Object[] toArray() {
		Object[] a = { element };
		if ( !isEmpty() ) return a;
		return new Object[0];
	}

	@Override
	public <U> U[] toArray(U[] a) {
		if ( !isEmpty() ) { a[0] = (U)element; return a; }
		return a;
	}

	@Override
	public boolean remove(Object o) {
		if ( isEmpty() ) return false;
		if ( element.equals(o) ) { element = null; return true; }
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return size() == c.size() && this.contains(c.toArray()[0]);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T o : c) {
			changed |= add(o);
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() { element = null; }
}
