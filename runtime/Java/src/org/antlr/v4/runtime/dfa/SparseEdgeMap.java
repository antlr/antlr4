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

import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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

	private SparseEdgeMap(@NotNull SparseEdgeMap<T> map, int maxSparseSize) {
		super(map.minIndex, map.maxIndex);
		if (maxSparseSize < map.values.size()) {
			throw new IllegalArgumentException();
		}

		keys = Arrays.copyOf(map.keys, maxSparseSize);
		values = new ArrayList<T>(maxSparseSize);
		values.addAll(map.values);
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
	public synchronized T get(int key) {
		int index = Arrays.binarySearch(keys, 0, size(), key);
		if (index < 0) {
			return null;
		}

		return values.get(index);
	}

	@Override
	public synchronized AbstractEdgeMap<T> put(int key, T value) {
		if (key < minIndex || key > maxIndex) {
			return this;
		}

		if (value == null) {
			return remove(key);
		}

		synchronized (values) {
			int index = Arrays.binarySearch(keys, 0, size(), key);
			if (index >= 0) {
				// replace existing entry
				values.set(index, value);
				return this;
			}

			assert index < 0 && value != null;
			int insertIndex = -index - 1;
			if (size() < getMaxSparseSize() && insertIndex == size()) {
				// stay sparse and add new entry
				keys[insertIndex] = key;
				values.add(value);
				return this;
			}

			int desiredSize = size() >= getMaxSparseSize() ? getMaxSparseSize() * 2 : getMaxSparseSize();
			int space = maxIndex - minIndex + 1;
			// SparseEdgeMap only uses less memory than ArrayEdgeMap up to half the size of the symbol space
			if (desiredSize >= space / 2) {
				ArrayEdgeMap<T> arrayMap = new ArrayEdgeMap<T>(minIndex, maxIndex);
				arrayMap = arrayMap.putAll(this);
				arrayMap.put(key, value);
				return arrayMap;
			}
			else {
				SparseEdgeMap<T> resized = new SparseEdgeMap<T>(this, desiredSize);
				System.arraycopy(resized.keys, insertIndex, resized.keys, insertIndex + 1, resized.keys.length - insertIndex - 1);
				resized.keys[insertIndex] = key;
				resized.values.add(insertIndex, value);
				return resized;
			}
		}
	}

	@Override
	public SparseEdgeMap<T> remove(int key) {
		int index = Arrays.binarySearch(keys, 0, size(), key);
		if (index < 0) {
			return this;
		}

		if (index == values.size() - 1) {
			values.remove(index);
			return this;
		}

		SparseEdgeMap<T> result = new SparseEdgeMap<T>(this, getMaxSparseSize());
		System.arraycopy(result.keys, index + 1, result.keys, index, size() - index - 1);
		result.values.remove(index);
		return result;
	}

	@Override
	public SparseEdgeMap<T> clear() {
		if (isEmpty()) {
			return this;
		}

		SparseEdgeMap<T> result = new SparseEdgeMap<T>(this, getMaxSparseSize());
		result.values.clear();
		return result;
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

	@Override
	public Set<Map.Entry<Integer, T>> entrySet() {
		return new EntrySet();
	}

	private class EntrySet extends AbstractEntrySet {
		@Override
		public Iterator<Map.Entry<Integer, T>> iterator() {
			return new EntryIterator();
		}
	}

	private class EntryIterator implements Iterator<Map.Entry<Integer, T>> {
		private int current;

		@Override
		public boolean hasNext() {
			return current < size();
		}

		@Override
		public Map.Entry<Integer, T> next() {
			if (current >= size()) {
				throw new NoSuchElementException();
			}

			current++;
			return new Map.Entry<Integer, T>() {
				private final int key = keys[current - 1];
				private final T value = values.get(current - 1);

				@Override
				public Integer getKey() {
					return key;
				}

				@Override
				public T getValue() {
					return value;
				}

				@Override
				public T setValue(T value) {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			};
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
