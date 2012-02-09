/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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
package org.antlr.v4.runtime.dfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sam Harwell
 */
public class SparseEdgeMap<T> extends AbstractEdgeMap<T> {
	private static final int DEFAULT_MAX_SIZE = 5;

	private final int[] keys;
	private final List<T> values;

	public SparseEdgeMap(int minIndex, int maxIndex) {
		this(minIndex, maxIndex, DEFAULT_MAX_SIZE);
	}

	public SparseEdgeMap(int minIndex, int maxIndex, int maxSparseSize) {
		super(minIndex, maxIndex);
		this.keys = new int[maxSparseSize];
		this.values = new ArrayList<T>(maxSparseSize);
	}

	public int[] getKeys() {
		return keys;
	}

	public List<T> getValues() {
		return values;
	}

	public int getMaxSparseSize() {
		return keys.length;
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public boolean containsKey(int key) {
		return get(key) != null;
	}

	@Override
	public T get(int key) {
		int index = Arrays.binarySearch(keys, 0, size(), key);
		if (index < 0) {
			return null;
		}

		return values.get(index);
	}

	@Override
	public EdgeMap<T> put(int key, T value) {
		if (key < minIndex || key > maxIndex) {
			return this;
		}

		if (value == null) {
			return remove(key);
		}

		int index = Arrays.binarySearch(keys, 0, size(), key);
		if (index >= 0) {
			// replace existing entry
			values.set(index, value);
			return this;
		}

		assert index < 0 && value != null;
		if (size() < getMaxSparseSize()) {
			// stay sparse and add new entry
			int insertIndex = -index - 1;
			System.arraycopy(keys, insertIndex, keys, insertIndex + 1, keys.length - insertIndex - 1);
			keys[insertIndex] = key;
			values.add(insertIndex, value);
			return this;
		}

		assert size() == getMaxSparseSize();
		ArrayEdgeMap<T> arrayMap = new ArrayEdgeMap<T>(minIndex, maxIndex);
		arrayMap = arrayMap.putAll(this);
		arrayMap.put(key, value);
		return arrayMap;
	}

	@Override
	public SparseEdgeMap<T> remove(int key) {
		int index = Arrays.binarySearch(keys, 0, size(), key);
		if (index >= 0) {
			System.arraycopy(keys, index + 1, keys, index, size() - index - 1);
			values.remove(index);
		}

		return this;
	}

	@Override
	public EdgeMap<T> putAll(EdgeMap<? extends T> m) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public SparseEdgeMap<T> clear() {
		values.clear();
		return this;
	}

	@Override
	public Map<Integer, T> toMap() {
		if (isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Integer, T> result = new LinkedHashMap<Integer, T>();
		for (int i = 0; i < size(); i++) {
			result.put(keys[i], values.get(i));
		}

		return result;
	}

}
