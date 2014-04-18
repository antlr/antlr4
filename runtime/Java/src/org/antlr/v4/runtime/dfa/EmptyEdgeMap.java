/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Sam Harwell
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
import java.util.Set;

/**
 * This implementation of {@link AbstractEdgeMap} represents an empty edge map.
 *
 * @author Sam Harwell
 */
public final class EmptyEdgeMap<T> extends AbstractEdgeMap<T> {

	public EmptyEdgeMap(int minIndex, int maxIndex) {
		super(minIndex, maxIndex);
	}

	@Override
	public AbstractEdgeMap<T> put(int key, T value) {
		if (value == null || key < minIndex || key > maxIndex) {
			// remains empty
			return this;
		}

		return new SingletonEdgeMap<T>(minIndex, maxIndex, key, value);
	}

	@Override
	public AbstractEdgeMap<T> clear() {
		return this;
	}

	@Override
	public AbstractEdgeMap<T> remove(int key) {
		return this;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean containsKey(int key) {
		return false;
	}

	@Override
	public T get(int key) {
		return null;
	}

	@Override
	public Map<Integer, T> toMap() {
		return Collections.emptyMap();
	}

	@Override
	public Set<Map.Entry<Integer, T>> entrySet() {
		return Collections.<Integer, T>emptyMap().entrySet();
	}
}
