package org.antlr.v4.misc;

import java.util.*;

/** I need the get-element-i functionality so I'm subclassing
 *  LinkedHashMap.
 */
public class OrderedHashMap<K,V> extends LinkedHashMap<K,V> {
	/** Track the elements as they are added to the set */
	protected List<K> elements = new ArrayList<K>();

	public K getKey(int i) { return elements.get(i); }

	@Override
	public V put(K key, V value) {
		elements.add(key);
		return super.put(key, value);
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		elements.clear();
		super.clear();
	}
}
