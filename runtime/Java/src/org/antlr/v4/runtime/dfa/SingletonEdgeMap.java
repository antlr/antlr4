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

import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Sam Harwell
 */
public class SingletonEdgeMap<T> extends AbstractEdgeMap<T> {

	private int key;
	private T value;

	public SingletonEdgeMap(int minIndex, int maxIndex) {
		super(minIndex, maxIndex);
	}

	public SingletonEdgeMap(int minIndex, int maxIndex, int key, T value) {
		super(minIndex, maxIndex);
		if (key >= minIndex && key <= maxIndex) {
			this.key = key;
			this.value = value;
		}
	}

	public int getKey() {
		return key;
	}

	public T getValue() {
		return value;
	}

	@Override
	public int size() {
		return value != null ? 1 : 0;
	}

	@Override
	public boolean isEmpty() {
		return value == null;
	}

	@Override
	public boolean containsKey(int key) {
		return key == this.key && value != null;
	}

	@Override
	public T get(int key) {
		if (key == this.key) {
			return value;
		}

		return null;
	}

	@Override
	public EdgeMap<T> put(int key, T value) {
		if (key < minIndex || key > maxIndex) {
			return this;
		}

		if (key == this.key || this.value == null) {
			this.key = key;
			this.value = value;
			return this;
		} else if (value != null) {
			EdgeMap<T> result = new SparseEdgeMap<T>(minIndex, maxIndex);
			result = result.put(this.key, this.value);
			result = result.put(key, value);
			return result;
		} else {
			return this;
		}
	}

	@Override
	public SingletonEdgeMap<T> remove(int key) {
		if (key == this.key) {
			this.value = null;
		}

		return this;
	}

	@Override
	public EdgeMap<T> putAll(EdgeMap<? extends T> m) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public SingletonEdgeMap<T> clear() {
		this.value = null;
		return this;
	}

	@Override
	public Map<Integer, T> toMap() {
		if (isEmpty()) {
			return Collections.emptyMap();
		}

		return Collections.singletonMap(key, value);
	}

}
