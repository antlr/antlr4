/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.misc;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Sometimes we need to map a key to a value but key is two pieces of data.
 *  This nested hash table saves creating a single key each time we access
 *  map; avoids mem creation.
 */
public class DoubleKeyMap<Key1, Key2, Value> {
	Map<Key1, Map<Key2, Value>> data = new LinkedHashMap<Key1, Map<Key2, Value>>();

	public Value put(Key1 k1, Key2 k2, Value v) {
		Map<Key2, Value> data2 = data.get(k1);
		Value prev = null;
		if ( data2==null ) {
			data2 = new LinkedHashMap<Key2, Value>();
			data.put(k1, data2);
		}
		else {
			prev = data2.get(k2);
		}
		data2.put(k2, v);
		return prev;
	}

	public Value get(Key1 k1, Key2 k2) {
		Map<Key2, Value> data2 = data.get(k1);
		if ( data2==null ) return null;
		return data2.get(k2);
	}

	public Map<Key2, Value> get(Key1 k1) { return data.get(k1); }

	/** Get all values associated with primary key */
	public Collection<Value> values(Key1 k1) {
		Map<Key2, Value> data2 = data.get(k1);
		if ( data2==null ) return null;
		return data2.values();
	}

	/** get all primary keys */
	public Set<Key1> keySet() {
		return data.keySet();
	}

	/** get all secondary keys associated with a primary key */
	public Set<Key2> keySet(Key1 k1) {
		Map<Key2, Value> data2 = data.get(k1);
		if ( data2==null ) return null;
		return data2.keySet();
	}
}
