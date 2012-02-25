package org.antlr.v4.runtime.tree;

import java.io.Serializable;
import java.util.*;

/**It is often the case that listeners would like to compute values and
 * associate them with particular tree nodes. This is analogous to the
 * function return types in a call tree. The problem is that the parse
 * trees returned from ANTLR are actually the rule context objects used
 * during analysis/parsing. The equals() method is defined to be something
 * special that makes it invalid to use Map<ParseTree, SomeType> to associate
 * values to nodes. Moreover, what we really need is identity comparison
 * rather than equals(). We only want to store values with a very specific
 * node not two nodes that are equal(). This class is a general Map
 * useful outside of ANTLR applications. Does not use key.equals() and
 * uses System.identityHashCode() to get hash codes instead of key.hashCode().
 * ANTLR users can directly use this class or use the specialized
 * ParseTreeProperty class, which defines everything properly for you.
 *
 * Object insertion order is not tracked and is nondeterministic from
 * things like keySet and toString.
 */
public class IdentityMap<K,V> implements Map<K,V>, Cloneable, Serializable {
	public static final int INITIAL_CAPACITY = 16; // power of 2 so mod is (x & size-1)
	public static final double LOAD_FACTOR = 0.75;


	protected static class Entry<K,V> implements Map.Entry<K,V> {
		protected K key;
		protected V value;
		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		@Override
		public K getKey() { return key; }
		@Override
		public V getValue() { return value; }
		@Override
		public V setValue(V value) { this.value = value; return value; }

		@Override
		public String toString() {
			return key+":"+value;
		}
	}

	protected LinkedList<Entry<K,V>>[] buckets;

	/** number of pairs */
	protected int size= 0;

	protected int sizeThreshold;

	public IdentityMap(int numBuckets) { init(numBuckets); }

	public IdentityMap() { this(INITIAL_CAPACITY); }

	@Override
	public void clear() {
		init(buckets.length);
	}

	@SuppressWarnings("unchecked")
	protected void init(int numBuckets) {
		// num buckets must be power of 2; stop when n first power of 2 >= numBuckets
		int n = 1;
		while (n < numBuckets) n <<= 1;
		numBuckets = n;

		buckets = new LinkedList[numBuckets];
		for (int i = 0; i<numBuckets; i++) {
			buckets[i] = new LinkedList<Entry<K,V>>();
		}
		size = 0;
		sizeThreshold = (int)(numBuckets * LOAD_FACTOR);
	}

	@Override
	public int size() { return size; }

	@Override
	public boolean isEmpty() { return size==0; }

	@Override
	public boolean containsKey(Object key) {
		Entry<K,V> pair = findPair(key);
		return pair!=null;
	}

	@Override
	public boolean containsValue(Object value) {
		for (LinkedList<Entry<K,V>> bucket : buckets) {
			for (Entry<K,V> pair : bucket) {
				if ( pair.value == value || pair.value.equals(value) ) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public V get(Object key) {
		if ( key==null ) return null;
		Entry<K,V> pair = findPair(key);
		if ( pair!=null ) return pair.value;
		return null;
	}

	@Override
	public V put(K key, V value) {
		if ( key==null ) return null;
		Entry<K,V> pair = findPair(key);
		if ( pair!=null ) {
			pair.value = value;
			return value;
		}

		size++;

		if ( size > sizeThreshold) rehash();

		int hash = hash(key);
		int bucket = bucket(buckets, hash);
		buckets[bucket].add( new Entry<K,V>(key, value) );
		return null;
	}

	@Override
	public V remove(Object key) {
		int hash = hash(key);
		int b = bucket(buckets, hash);
		int i = 0;
		LinkedList<Entry<K, V>> bucket = buckets[b];
		if ( bucket==null ) return null;
		for (Entry<K,V> pair : bucket) {
			int h = hash(pair.key);
			if ( h == hash && key==pair.key ) { // must be identity not equals
				buckets[b].remove(i);
				return pair.value;
			}
			i++;
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K t : m.keySet()) {
			put(t, m.get(t));
		}
	}

	// these are inefficient (copy elements into new list)

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		if ( size()==0 ) return null;
		Set<Map.Entry<K, V>> pairs = new HashSet<Map.Entry<K, V>>();
		for (LinkedList<Entry<K,V>> bucket : buckets) {
			if ( bucket==null ) continue;
			for (Entry<K,V> pair : bucket) {
				pairs.add(pair);
			}
		}
		return pairs;
	}

	@Override
	public Set<K> keySet() {
		if ( size()==0 ) return null;
		Set<K> keys = new HashSet<K>();
		for (LinkedList<Entry<K,V>> bucket : buckets) {
			if ( bucket==null ) continue;
			for (Entry<K,V> pair : bucket) {
				keys.add(pair.key);
			}
		}
		return keys;
	}

	@Override
	public Collection<V> values() {
		if ( size()==0 ) return null;
		List<V> values = new ArrayList<V>();
		for (LinkedList<Entry<K,V>> bucket : buckets) {
			if ( bucket==null ) continue;
			for (Entry<K,V> pair : bucket) {
				values.add(pair.value);
			}
		}
		return values;
	}

	public Collection<V> values(Collection<K> keys) {
		if ( keys==null ) return null;
		List<V> values = new ArrayList<V>();
		for (K key : keys) {
			values.add( get(key) );
		}
		if ( values.size()==0 ) return null;
		return values;
	}

	protected Entry<K,V> findPair(Object key) {
		int hash = hash(key);
		int b = bucket(buckets, hash);
		LinkedList<Entry<K, V>> bucket = buckets[b];
		if ( bucket==null ) return null;
		for (Entry<K,V> pair : bucket) {
			if ( pair==null ) continue;
			int h = hash(pair.key);
			if ( h == hash && key==pair.key ) return pair; // must be identity not equals
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected void rehash() {
//		System.out.println("old:\n"+getStructureDisplay());
		int newNumBuckets = buckets.length * 2;
		LinkedList<Entry<K,V>>[] oldBuckets = buckets;
		LinkedList<Entry<K,V>>[] newBuckets = new LinkedList[newNumBuckets];
		for (int i = 0; i<newNumBuckets; i++) {
			newBuckets[i] = new LinkedList<Entry<K,V>>();
		}

		for (LinkedList<Entry<K,V>> oldBucket : oldBuckets) {
			if ( oldBucket==null ) continue;
			for (Entry<K,V> pair : oldBucket) {
				int b = bucket(newBuckets, hash(pair.key));
				newBuckets[b].add(pair);
			}
		}

		buckets = newBuckets;
		sizeThreshold = (int)(newNumBuckets * LOAD_FACTOR);
//		System.out.println("sizeThreshold="+sizeThreshold);
//		System.out.println("new:\n"+getStructureDisplay());
	}

	public int getNumberOfBuckets() { return buckets.length; }

	protected int bucket(LinkedList<Entry<K,V>>[] buckets, int hash) {
		return hash & (buckets.length-1); // hash % buckets.length
	}

	// use system hash since we want identity
	public int hash(Object node) {
		return System.identityHashCode(node);
	}

	public String getStructureDisplay() {
		boolean firstOne = true;
		StringBuilder buf = new StringBuilder();
		int b = 0;
		for (LinkedList<Entry<K,V>> bucket : buckets) {
			buf.append("["+b+"]");
			buf.append("=");
			buf.append(bucket.toString());
			buf.append('\n');
			b++;
		}
		return buf.toString();
	}

	public String toString() {
		if ( size()==0 ) return "{}";

		boolean firstOne = true;
		StringBuilder buf = new StringBuilder();
		buf.append('{');
		for (LinkedList<Entry<K,V>> bucket : buckets) {
			if ( bucket==null ) continue;
			for (Entry<K,V> pair : bucket) {
				if ( !firstOne ) buf.append(", ");
				else firstOne = false;
				buf.append(pair.key);
				buf.append(':');
				buf.append(pair.value);
			}
		}
		buf.append('}');
		return buf.toString();
	}
}
