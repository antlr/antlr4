/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.misc;

import java.util.HashMap;

/** Count how many of each key we have; not thread safe */
public class FrequencySet<T> extends HashMap<T, MutableInt> {
	public int count(T key) {
		MutableInt value = get(key);
		if (value == null) return 0;
		return value.v;
	}
	public void add(T key) {
		MutableInt value = get(key);
		if (value == null) {
			value = new MutableInt(1);
			put(key, value);
		}
		else {
			value.v++;
		}
	}
}
