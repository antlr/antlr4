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

import java.lang.IllegalArgumentException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Sam Harwell
 */
public class IntegerList {

	private static int[] EMPTY_DATA = new int[0];

	private static final int INITIAL_SIZE = 4;
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;


	private int[] _data;

	private int _size;

	public IntegerList() {
		_data = EMPTY_DATA;
	}

	public IntegerList(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException();
		}

		if (capacity == 0) {
			_data = EMPTY_DATA;
		} else {
			_data = new int[capacity];
		}
	}

	public IntegerList(IntegerList list) {
		_data = list._data.clone();
		_size = list._size;
	}

	public IntegerList(Collection<Integer> list) {
		this(list.size());
		for (Integer value : list) {
			add(value);
		}
	}

	public final void add(int value) {
		if (_data.length == _size) {
			ensureCapacity(_size + 1);
		}

		_data[_size] = value;
		_size++;
	}

	public final void addAll(int[] array) {
		ensureCapacity(_size + array.length);
		System.arraycopy(array, 0, _data, _size, array.length);
		_size += array.length;
	}

	public final void addAll(IntegerList list) {
		ensureCapacity(_size + list._size);
		System.arraycopy(list._data, 0, _data, _size, list._size);
		_size += list._size;
	}

	public final void addAll(Collection<Integer> list) {
		ensureCapacity(_size + list.size());
		int current = 0;
    		for (int x : list) {
      			_data[_size + current] = x;
      			current++;
    		}
    		_size += list.size();
	}

	public final int get(int index) {
		if (index < 0 || index >= _size) {
			throw new IndexOutOfBoundsException();
		}

		return _data[index];
	}

	public final boolean contains(int value) {
		for (int i = 0; i < _size; i++) {
			if (_data[i] == value) {
				return true;
			}
		}

		return false;
	}

	public final int set(int index, int value) {
		if (index < 0 || index >= _size) {
			throw new IndexOutOfBoundsException();
		}

		int previous = _data[index];
		_data[index] = value;
		return previous;
	}

	public final int removeAt(int index) {
		int value = get(index);
		System.arraycopy(_data, index + 1, _data, index, _size - index - 1);
		_data[_size - 1] = 0;
		_size--;
		return value;
	}

	public final void removeRange(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex < 0 || fromIndex > _size || toIndex > _size) {
			throw new IndexOutOfBoundsException();
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException();
		}

		System.arraycopy(_data, toIndex, _data, fromIndex, _size - toIndex);
		Arrays.fill(_data, _size - (toIndex - fromIndex), _size, 0);
		_size -= (toIndex - fromIndex);
	}

	public final boolean isEmpty() {
		return _size == 0;
	}

	public final int size() {
		return _size;
	}

	public final void trimToSize() {
		if (_data.length == _size) {
			return;
		}

		_data = Arrays.copyOf(_data, _size);
	}

	public final void clear() {
		Arrays.fill(_data, 0, _size, 0);
		_size = 0;
	}

	public final int[] toArray() {
		if (_size == 0) {
			return EMPTY_DATA;
		}

		return Arrays.copyOf(_data, _size);
	}

	public final void sort() {
		Arrays.sort(_data, 0, _size);
	}

	/**
	 * Compares the specified object with this list for equality.  Returns
	 * {@code true} if and only if the specified object is also an {@link IntegerList},
	 * both lists have the same size, and all corresponding pairs of elements in
	 * the two lists are equal.  In other words, two lists are defined to be
	 * equal if they contain the same elements in the same order.
	 * <p>
	 * This implementation first checks if the specified object is this
	 * list. If so, it returns {@code true}; if not, it checks if the
	 * specified object is an {@link IntegerList}. If not, it returns {@code false};
	 * if so, it checks the size of both lists. If the lists are not the same size,
	 * it returns {@code false}; otherwise it iterates over both lists, comparing
	 * corresponding pairs of elements.  If any comparison returns {@code false},
	 * this method returns {@code false}.
	 *
	 * @param o the object to be compared for equality with this list
	 * @return {@code true} if the specified object is equal to this list
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof IntegerList)) {
			return false;
		}

		IntegerList other = (IntegerList)o;
		if (_size != other._size) {
			return false;
		}

		for (int i = 0; i < _size; i++) {
			if (_data[i] != other._data[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the hash code value for this list.
	 *
	 * <p>This implementation uses exactly the code that is used to define the
	 * list hash function in the documentation for the {@link List#hashCode}
	 * method.</p>
	 *
	 * @return the hash code value for this list
	 */
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < _size; i++) {
			hashCode = 31*hashCode + _data[i];
		}

		return hashCode;
	}

	/**
	 * Returns a string representation of this list.
	 */
	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}

	public final int binarySearch(int key) {
		return Arrays.binarySearch(_data, 0, _size, key);
	}

	public final int binarySearch(int fromIndex, int toIndex, int key) {
		if (fromIndex < 0 || toIndex < 0 || fromIndex > _size || toIndex > _size) {
			throw new IndexOutOfBoundsException();
		}
		if (fromIndex > toIndex) {
        		throw new IllegalArgumentException();
		}

		return Arrays.binarySearch(_data, fromIndex, toIndex, key);
	}

	private void ensureCapacity(int capacity) {
		if (capacity < 0 || capacity > MAX_ARRAY_SIZE) {
			throw new OutOfMemoryError();
		}

		int newLength;
		if (_data.length == 0) {
			newLength = INITIAL_SIZE;
		} else {
			newLength = _data.length;
		}

		while (newLength < capacity) {
			newLength = newLength * 2;
			if (newLength < 0 || newLength > MAX_ARRAY_SIZE) {
				newLength = MAX_ARRAY_SIZE;
			}
		}

		_data = Arrays.copyOf(_data, newLength);
	}

}
