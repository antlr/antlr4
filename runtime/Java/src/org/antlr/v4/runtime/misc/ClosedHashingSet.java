package org.antlr.v4.runtime.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/** Set impl with closed hashing (open addressing). */
public class ClosedHashingSet<T> implements Set<T> {
	public static final int INITAL_CAPACITY = 4;
	public static final double LOAD_FACTOR = 0.5;

	protected T[] table;

	/** How many elements in set */
	protected int n;

	protected int threshold;

	public ClosedHashingSet() {
		table = (T[])new Object[INITAL_CAPACITY];
		threshold = (int)(INITAL_CAPACITY * LOAD_FACTOR);
	}

	/** Add t to set if not there; return existing value if already there. */
	public T put(T o) {
		int i = findInsertionSlot(o);
		if ( i == -1 ) {
			expand();
			i = findInsertionSlot(o);
		}
		if ( table[i]==null ) {
			table[i] = o;
			n++;
			if ( n > threshold ) expand();
		}
		else {
			o = table[i]; // found, return old one
		}
		return o;
	}

	public T get(Object o) {
		int i = findSlot((T)o);
		if ( i == -1 ) return null;
		return table[i];
	}

	/** Where does o go in table? Return -1 if no empty space.
	 *  Uses double-hashing to find non-home slot as explained here:
	 *  http://www.cs.wcupa.edu/~rkline/ds/closed-hashing.html
	 */
	protected int findInsertionSlot(T o) {
		// look for an empty slot.
		int hash = hashCode(o);
		int hash2 = table.length-2 - Math.abs(hash % table.length-2);
		int i = hash & (table.length-1); // assumes len is power of 2
		int home = i;
		if ( n>=table.length ) return -1; // full
		while ( table[i]!=null && !equals(table[i],o) ) {
//			System.out.println("table["+i+"]="+table[i]+", o="+o);
			i = (i+hash2) & (table.length-1);
		}
		if ( i!=home ) System.out.println("rehash "+i+" from "+home);
		return i;
	}

	/** Where is o in table? Return -1 if not found.
	 *  Uses double-hashing to find non-home slot as explained here:
	 *  http://www.cs.wcupa.edu/~rkline/ds/closed-hashing.html
	 */
	protected int findSlot(T o) {
		// look for the key or find an empty slot.
		int hash = hashCode(o);
		int hash2 = table.length-2 - Math.abs(hash % table.length-2);
		int i = hash & (table.length-1); // assumes len is power of 2
		int home = i;
		while ( table[i]!=null && !equals(table[i],o) ) {
//			System.out.println("table["+i+"]="+table[i]+", o="+o);
			i = (i+hash2) & (table.length-1);
		}
		if ( i!=home ) System.out.println("rehash "+i+" from "+home);
		if ( table[i]==null ) return -1; // not found
		return i;
	}

	protected void expand() {
		T[] old = table;
		int newCapacity = old.length * 2;
		T[] newTable = (T[])new Object[newCapacity];
		table = newTable;
		threshold = (int)(newCapacity * LOAD_FACTOR);
		System.out.println("new size="+newCapacity+", thres="+threshold);
		// rehash all existing entries
		for (T o : old) {
			if ( o!=null ) {
				int i = findInsertionSlot(o);
				table[i] = o;
			}
		}
	}

	public int hashCode(T t) { return t.hashCode(); }

//	// return i+1 for linear probing. doesn't terminate for other schemes
//	public int rehash(int hash, int i) { return table.length-2 - Math.abs(hash % table.length); }

	public boolean equals(T a, T b) {
		if ( a==null && b==null ) return true;
		if ( a==null || b==null ) return false;
		if ( a==b ) return true;
		return a.equals(b);
	}

	@Override
	public boolean add(T t) {
		T existing = put(t);
		return existing!=t;
	}

	@Override
	public int size() {
		return n;
	}

	@Override
	public boolean isEmpty() {
		return n==0;
	}

	@Override
	public boolean contains(Object o) {
		int i = findSlot((T)o);
		return i == -1 || table[i]!=null;
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		Object[] a = new Object[size()];
		int i = 0;
		for (T o : table) a[i++] = o;
		return a;
	}

	@Override
	public <U> U[] toArray(U[] a) {
		int i = 0;
		for (T o : table) a[i++] = (U)o;
		return a;
	}

	@Override
	public boolean remove(Object o) {
		int i = findSlot((T)o);
		if ( i == -1 ) return false;
		T existing = table[i];
		table[i]=null;
		if ( existing!=null ) n--; // we wacked something
		return existing!=null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T o : c) {
			T existing = put(o);
			if ( existing!=o ) changed=true;
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		table = (T[])new Object[table.length];
		n = 0;
	}

	public String toString() {
		if ( size()==0 ) return "{}";

		StringBuilder buf = new StringBuilder();
		buf.append('{');
		boolean first = true;
		for (T o : table) {
			if ( o==null ) continue;
			if ( first ) first=false;
			else buf.append(", ");
			buf.append(o.toString());
		}
		buf.append('}');
		return buf.toString();
	}

	public String toTableString() {
		StringBuilder buf = new StringBuilder();
		buf.append('[');
		boolean first = true;
		for (T o : table) {
			if ( first ) first=false;
			else buf.append(" ");
			if ( o==null ) buf.append("_");
			else buf.append(o.toString());
		}
		buf.append(']');
		return buf.toString();
	}

	public static void main(String[] args) {
		ClosedHashingSet<String> clset = new ClosedHashingSet<String>();
		Set<String> set = clset;
		set.add("hi");
		set.add("mom");
		set.add("foo");
		set.add("ach");
		set.add("cbba");
		set.add("d");
		set.add("edf");
		set.add("f");
		set.add("gab");
		set.remove("ach");
		System.out.println(set);
		System.out.println(clset.toTableString());
	}
}
