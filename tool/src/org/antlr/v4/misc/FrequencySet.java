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

package org.antlr.v4.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Object that counts the minimum and maximum number of occurrences for each key.
 * @param <T>
 */
public class FrequencySet<T> extends HashMap<T, FrequencyRange> {
	private static final FrequencyRange ONE = new FrequencyRange(Frequency.ONE, Frequency.ONE);

	public FrequencySet() {}

	public FrequencySet(FrequencySet<T> that) {
		for (Map.Entry<T, FrequencyRange> entry : that.entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
	}

	public void union(FrequencySet<T> that) {
		for (Map.Entry<T, FrequencyRange> thatEntry : that.entrySet()) {
			final T key = thatEntry.getKey();
			final FrequencyRange thatValue = thatEntry.getValue();
			final FrequencyRange thisValue = get(key);
			if (thisValue == null) {
				// Key present only in that set.
				put(key, new FrequencyRange(Frequency.NONE, thatValue.max));
			} else {
				// Key present in both sets.
				thisValue.union(thatValue);
			}
		}
		for (Map.Entry<T, FrequencyRange> thisEntry : this.entrySet()) {
			final T key = thisEntry.getKey();
			final FrequencyRange thisValue = thisEntry.getValue();
			if (!that.containsKey(key)) {
				// Key present only in this set.
				thisValue.min = Frequency.NONE;
			}
		}
	}

	public void addAll(FrequencySet<T> that) {
		for (Map.Entry<T, FrequencyRange> entry : that.entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
	}

	public void add(T key) {
		add(key, ONE);
	}

	public void add(T key, FrequencyRange range) {
		FrequencyRange value = get(key);
		if (value == null) {
			// Create a copy so 'range' doesn't get modified by subsequent
			// operations on this FrequencySet.
			put(key, new FrequencyRange(range));
		}
		else {
			value.add(range);
		}
	}
}
