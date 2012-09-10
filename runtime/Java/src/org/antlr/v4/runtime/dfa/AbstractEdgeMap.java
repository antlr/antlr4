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

import java.util.AbstractSet;
import java.util.Map;

/**
 *
 * @author Sam Harwell
 */
public abstract class AbstractEdgeMap<T> implements EdgeMap<T> {

	protected final int minIndex;
	protected final int maxIndex;

	public AbstractEdgeMap(int minIndex, int maxIndex) {
		// the allowed range (with minIndex and maxIndex inclusive) should be less than 2^32
		assert maxIndex - minIndex + 1 >= 0;
		this.minIndex = minIndex;
		this.maxIndex = maxIndex;
	}

	@Override
	public abstract AbstractEdgeMap<T> put(int key, T value);

	@Override
	public AbstractEdgeMap<T> putAll(EdgeMap<? extends T> m) {
		AbstractEdgeMap<T> result = this;
		for (Map.Entry<Integer, ? extends T> entry : m.entrySet()) {
			result = result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	@Override
	public abstract AbstractEdgeMap<T> clear();

	@Override
	public abstract AbstractEdgeMap<T> remove(int key);

	protected abstract class AbstractEntrySet extends AbstractSet<Map.Entry<Integer, T>> {
		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}

			Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
			if (entry.getKey() instanceof Integer) {
				int key = (Integer)entry.getKey();
				Object value = entry.getValue();
				T existing = get(key);
				return value == existing || (existing != null && existing.equals(value));
			}

			return false;
		}

		@Override
		public int size() {
			return AbstractEdgeMap.this.size();
		}
	}

}
