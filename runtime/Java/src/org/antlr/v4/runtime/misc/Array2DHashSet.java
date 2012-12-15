/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/** Set impl with closed hashing (open addressing). */
public class Array2DHashSet<T> implements Set<T> {
	public static final int INITAL_CAPACITY = 16; // must be power of 2
	public static final int INITAL_BUCKET_CAPACITY = 8;
	public static final double LOAD_FACTOR = 0.75;

	@NotNull
	protected final AbstractEqualityComparator<? super T> comparator;

	protected T[][] buckets;

	/** How many elements in set */
	protected int n = 0;

	protected int threshold = (int)(INITAL_CAPACITY * LOAD_FACTOR); // when to expand

	protected int currentPrime = 1; // jump by 4 primes each expand or whatever
	protected int initialBucketCapacity = INITAL_BUCKET_CAPACITY;

	public Array2DHashSet() {
		this(null, INITAL_CAPACITY, INITAL_BUCKET_CAPACITY);
	}

	public Array2DHashSet(@Nullable AbstractEqualityComparator<? super T> comparator) {
		this(comparator, INITAL_CAPACITY, INITAL_BUCKET_CAPACITY);
	}

	public Array2DHashSet(@Nullable AbstractEqualityComparator<? super T> comparator, int initialCapacity, int initialBucketCapacity) {
		if (comparator == null) {
			comparator = ObjectEqualityComparator.INSTANCE;
		}

		this.comparator = comparator;
		this.buckets = (T[][])new Object[initialCapacity][];
		this.initialBucketCapacity = initialBucketCapacity;
	}

	/** Add o to set if not there; return existing value if already there.
	 *  Absorb is used as synonym for add.
	 */
	public T absorb(T o) {
		if ( n > threshold ) expand();
		return absorb_(o);
	}

	protected T absorb_(T o) {
		int b = getBucket(o);
		T[] bucket = buckets[b];
		// NEW BUCKET
		if ( bucket==null ) {
			buckets[b] = (T[])new Object[initialBucketCapacity];
			buckets[b][0] = o;
			n++;
			return o;
		}
		// LOOK FOR IT IN BUCKET
		for (int i=0; i<bucket.length; i++) {
			T existing = bucket[i];
			if ( existing==null ) { // empty slot; not there, add.
				bucket[i] = o;
				n++;
				return o;
			}
			if ( comparator.equals(existing, o) ) return existing; // found existing, quit
		}
		// FULL BUCKET, expand and add to end
		T[] old = bucket;
		bucket = (T[])new Object[old.length * 2];
		buckets[b] = bucket;
		System.arraycopy(old, 0, bucket, 0, old.length);
		bucket[old.length] = o; // add to end
		n++;
		return o;
	}

	public T get(T o) {
		if ( o==null ) return o;
		int b = getBucket(o);
		T[] bucket = buckets[b];
		if ( bucket==null ) return null; // no bucket
		for (T e : bucket) {
			if ( e==null ) return null; // empty slot; not there
			if ( comparator.equals(e, o) ) return e;
		}
		return null;
	}

	protected int getBucket(T o) {
		int hash = comparator.hashCode(o);
		int b = hash & (buckets.length-1); // assumes len is power of 2
		return b;
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (T[] bucket : buckets) {
			if ( bucket==null ) continue;
			for (T o : bucket) {
				if ( o==null ) break;
				h += comparator.hashCode(o);
			}
		}
		return h;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if ( !(o instanceof Array2DHashSet) || o==null ) return false;
		Array2DHashSet<T> other = (Array2DHashSet<T>)o;
		if ( other.size() != size() ) return false;
		boolean same = this.containsAll(other);
		return same;
	}

	protected void expand() {
		T[][] old = buckets;
		currentPrime += 4;
		int newCapacity = buckets.length * 2;
		T[][] newTable = (T[][])new Object[newCapacity][];
		buckets = newTable;
		threshold = (int)(newCapacity * LOAD_FACTOR);
//		System.out.println("new size="+newCapacity+", thres="+threshold);
		// rehash all existing entries
		int oldSize = size();
		for (T[] bucket : old) {
			if ( bucket==null ) continue;
			for (T o : bucket) {
				if ( o==null ) break;
				absorb_(o);
			}
		}
		n = oldSize;
	}

	@Override
	public boolean add(T t) {
		T existing = absorb(t);
		return existing==t;
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
		return get((T)o) != null;
	}

	@Override
	public Iterator<T> iterator() {
		final Object[] data = toArray();
		return new Iterator<T>() {
			int nextIndex = 0;
			boolean removed = true;

			@Override
			public boolean hasNext() {
				return nextIndex < data.length;
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				removed = false;
				return (T)data[nextIndex++];
			}

			@Override
			public void remove() {
				if (removed) {
					throw new IllegalStateException();
				}

				Array2DHashSet.this.remove(data[nextIndex - 1]);
				removed = true;
			}

		};
	}

	@Override
	public Object[] toArray() {
		Object[] a = new Object[size()];
		int i = 0;
		for (T[] bucket : buckets) {
			if ( bucket==null ) continue;
			for (T o : bucket) {
				if ( o==null ) break;
				a[i++] = o;
			}
		}
		return a;
	}

	@Override
	public <U> U[] toArray(U[] a) {
		int i = 0;
		for (T[] bucket : buckets) {
			if ( bucket==null ) continue;
			for (T o : bucket) {
				if ( o==null ) break;
				a[i++] = (U)o;
			}
		}
		return a;
	}

	@Override
	public boolean remove(Object o) {
		if ( o==null ) return false;
		int b = getBucket((T)o);
		T[] bucket = buckets[b];
		if ( bucket==null ) return false; // no bucket
		for (int i=0; i<bucket.length; i++) {
			T e = bucket[i];
			if ( e==null ) return false;  // empty slot; not there
			if ( comparator.equals(e, (T) o) ) {          // found it
				// shift all elements to the right down one
//				for (int j=i; j<bucket.length-1; j++) bucket[j] = bucket[j+1];
				System.arraycopy(bucket, i+1, bucket, i, bucket.length-i-1);
				n--;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		if ( collection instanceof Array2DHashSet ) {
			Array2DHashSet<T> s = (Array2DHashSet<T>)collection;
			for (T[] bucket : s.buckets) {
				if ( bucket==null ) continue;
				for (T o : bucket) {
					if ( o==null ) break;
					if ( !this.contains(o) ) return false;
				}
			}
		}
		else {
			for (Object o : collection) {
				if ( !this.contains(o) ) return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T o : c) {
			T existing = absorb(o);
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
		buckets = (T[][])new Object[INITAL_CAPACITY][];
		n = 0;
	}

	@Override
	public String toString() {
		if ( size()==0 ) return "{}";

		StringBuilder buf = new StringBuilder();
		buf.append('{');
		boolean first = true;
		for (T[] bucket : buckets) {
			if ( bucket==null ) continue;
			for (T o : bucket) {
				if ( o==null ) break;
				if ( first ) first=false;
				else buf.append(", ");
				buf.append(o.toString());
			}
		}
		buf.append('}');
		return buf.toString();
	}

	public String toTableString() {
		StringBuilder buf = new StringBuilder();
		for (T[] bucket : buckets) {
			if ( bucket==null ) {
				buf.append("null\n");
				continue;
			}
			buf.append('[');
			boolean first = true;
			for (T o : bucket) {
				if ( first ) first=false;
				else buf.append(" ");
				if ( o==null ) buf.append("_");
				else buf.append(o.toString());
			}
			buf.append("]\n");
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		Array2DHashSet<String> clset = new Array2DHashSet<String>();
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
