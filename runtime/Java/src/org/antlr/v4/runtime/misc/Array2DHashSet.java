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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/** {@link Set} implementation with closed hashing (open addressing). */
public class Array2DHashSet<T> implements Set<T> {
	public static final int INITAL_CAPACITY = 16; // must be power of 2
	public static final int INITAL_BUCKET_CAPACITY = 8;
	public static final double LOAD_FACTOR = 0.75;


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

	public Array2DHashSet(AbstractEqualityComparator<? super T> comparator) {
		this(comparator, INITAL_CAPACITY, INITAL_BUCKET_CAPACITY);
	}

	public Array2DHashSet(AbstractEqualityComparator<? super T> comparator, int initialCapacity, int initialBucketCapacity) {
		if (comparator == null) {
			comparator = ObjectEqualityComparator.INSTANCE;
		}

		this.comparator = comparator;
		this.buckets = createBuckets(initialCapacity);
		this.initialBucketCapacity = initialBucketCapacity;
	}

	/**
	 * Add {@code o} to set if not there; return existing value if already
	 * there. This method performs the same operation as {@link #add} aside from
	 * the return value.
	 */
	public final T getOrAdd(T o) {
		if ( n > threshold ) expand();
		return getOrAddImpl(o);
	}

	protected T getOrAddImpl(T o) {
		int b = getBucket(o);
		T[] bucket = buckets[b];

		// NEW BUCKET
		if ( bucket==null ) {
			bucket = createBucket(initialBucketCapacity);
			bucket[0] = o;
			buckets[b] = bucket;
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
		int oldLength = bucket.length;
		bucket = Arrays.copyOf(bucket, bucket.length * 2);
		buckets[b] = bucket;
		bucket[oldLength] = o; // add to end
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

	protected final int getBucket(T o) {
		int hash = comparator.hashCode(o);
		int b = hash & (buckets.length-1); // assumes len is power of 2
		return b;
	}

	@Override
	public int hashCode() {
		int hash = MurmurHash.initialize();
		for (T[] bucket : buckets) {
			if ( bucket==null ) continue;
			for (T o : bucket) {
				if ( o==null ) break;
				hash = MurmurHash.update(hash, comparator.hashCode(o));
			}
		}

		hash = MurmurHash.finish(hash, size());
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if ( !(o instanceof Array2DHashSet) ) return false;
		Array2DHashSet<?> other = (Array2DHashSet<?>)o;
		if ( other.size() != size() ) return false;
		boolean same = this.containsAll(other);
		return same;
	}

	protected void expand() {
		T[][] old = buckets;
		currentPrime += 4;
		int newCapacity = buckets.length * 2;
		T[][] newTable = createBuckets(newCapacity);
		int[] newBucketLengths = new int[newTable.length];
		buckets = newTable;
		threshold = (int)(newCapacity * LOAD_FACTOR);
//		System.out.println("new size="+newCapacity+", thres="+threshold);
		// rehash all existing entries
		int oldSize = size();
		for (T[] bucket : old) {
			if ( bucket==null ) {
				continue;
			}

			for (T o : bucket) {
				if ( o==null ) {
					break;
				}

				int b = getBucket(o);
				int bucketLength = newBucketLengths[b];
				T[] newBucket;
				if (bucketLength == 0) {
					// new bucket
					newBucket = createBucket(initialBucketCapacity);
					newTable[b] = newBucket;
				}
				else {
					newBucket = newTable[b];
					if (bucketLength == newBucket.length) {
						// expand
						newBucket = Arrays.copyOf(newBucket, newBucket.length * 2);
						newTable[b] = newBucket;
					}
				}

				newBucket[bucketLength] = o;
				newBucketLengths[b]++;
			}
		}

		assert n == oldSize;
	}

	@Override
	public final boolean add(T t) {
		T existing = getOrAdd(t);
		return existing==t;
	}

	@Override
	public final int size() {
		return n;
	}

	@Override
	public final boolean isEmpty() {
		return n==0;
	}

	@Override
	public final boolean contains(Object o) {
		return containsFast(asElementType(o));
	}

	public boolean containsFast(T obj) {
		if (obj == null) {
			return false;
		}

		return get(obj) != null;
	}

	@Override
	public Iterator<T> iterator() {
		return new SetIterator(toArray());
	}

	@Override
	public T[] toArray() {
		T[] a = createBucket(size());
		int i = 0;
		for (T[] bucket : buckets) {
			if ( bucket==null ) {
				continue;
			}

			for (T o : bucket) {
				if ( o==null ) {
					break;
				}

				a[i++] = o;
			}
		}

		return a;
	}

	@Override
	public <U> U[] toArray(U[] a) {
		if (a.length < size()) {
			a = Arrays.copyOf(a, size());
		}

		int i = 0;
		for (T[] bucket : buckets) {
			if ( bucket==null ) {
				continue;
			}

			for (T o : bucket) {
				if ( o==null ) {
					break;
				}

				@SuppressWarnings("unchecked") // array store will check this
				U targetElement = (U)o;
				a[i++] = targetElement;
			}
		}
		return a;
	}

	@Override
	public final boolean remove(Object o) {
		return removeFast(asElementType(o));
	}

	public boolean removeFast(T obj) {
		if (obj == null) {
			return false;
		}

		int b = getBucket(obj);
		T[] bucket = buckets[b];
		if ( bucket==null ) {
			// no bucket
			return false;
		}

		for (int i=0; i<bucket.length; i++) {
			T e = bucket[i];
			if ( e==null ) {
				// empty slot; not there
				return false;
			}

			if ( comparator.equals(e, obj) ) {          // found it
				// shift all elements to the right down one
				System.arraycopy(bucket, i+1, bucket, i, bucket.length-i-1);
				bucket[bucket.length - 1] = null;
				n--;
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		if ( collection instanceof Array2DHashSet ) {
			Array2DHashSet<?> s = (Array2DHashSet<?>)collection;
			for (Object[] bucket : s.buckets) {
				if ( bucket==null ) continue;
				for (Object o : bucket) {
					if ( o==null ) break;
					if ( !this.containsFast(asElementType(o)) ) return false;
				}
			}
		}
		else {
			for (Object o : collection) {
				if ( !this.containsFast(asElementType(o)) ) return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T o : c) {
			T existing = getOrAdd(o);
			if ( existing!=o ) changed=true;
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		int newsize = 0;
		for (T[] bucket : buckets) {
			if (bucket == null) {
				continue;
			}

			int i;
			int j;
			for (i = 0, j = 0; i < bucket.length; i++) {
				if (bucket[i] == null) {
					break;
				}

				if (!c.contains(bucket[i])) {
					// removed
					continue;
				}

				// keep
				if (i != j) {
					bucket[j] = bucket[i];
				}

				j++;
				newsize++;
			}

			newsize += j;

			while (j < i) {
				bucket[j] = null;
				j++;
			}
		}

		boolean changed = newsize != n;
		n = newsize;
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			changed |= removeFast(asElementType(o));
		}

		return changed;
	}

	@Override
	public void clear() {
		buckets = createBuckets(INITAL_CAPACITY);
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

	/**
	 * Return {@code o} as an instance of the element type {@code T}. If
	 * {@code o} is non-null but known to not be an instance of {@code T}, this
	 * method returns {@code null}. The base implementation does not perform any
	 * type checks; override this method to provide strong type checks for the
	 * {@link #contains} and {@link #remove} methods to ensure the arguments to
	 * the {@link EqualityComparator} for the set always have the expected
	 * types.
	 *
	 * @param o the object to try and cast to the element type of the set
	 * @return {@code o} if it could be an instance of {@code T}, otherwise
	 * {@code null}.
	 */
	@SuppressWarnings("unchecked")
	protected T asElementType(Object o) {
		return (T)o;
	}

	/**
	 * Return an array of {@code T[]} with length {@code capacity}.
	 *
	 * @param capacity the length of the array to return
	 * @return the newly constructed array
	 */
	@SuppressWarnings("unchecked")
	protected T[][] createBuckets(int capacity) {
		return (T[][])new Object[capacity][];
	}

	/**
	 * Return an array of {@code T} with length {@code capacity}.
	 *
	 * @param capacity the length of the array to return
	 * @return the newly constructed array
	 */
	@SuppressWarnings("unchecked")
	protected T[] createBucket(int capacity) {
		return (T[])new Object[capacity];
	}

	protected class SetIterator implements Iterator<T> {
		final T[] data;
		int nextIndex = 0;
		boolean removed = true;

		public SetIterator(T[] data) {
			this.data = data;
		}

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
			return data[nextIndex++];
		}

		@Override
		public void remove() {
			if (removed) {
				throw new IllegalStateException();
			}

			Array2DHashSet.this.remove(data[nextIndex - 1]);
			removed = true;
		}
	}
}
