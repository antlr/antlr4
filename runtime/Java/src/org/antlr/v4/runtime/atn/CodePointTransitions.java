/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

/**
 * Utility class to create {@link AtomTransition}, {@link RangeTransition},
 * and {@link SetTransition} appropriately based on the range of the input.
 *
 * To keep the serialized ATN size small, we only inline atom and
 * range transitions for Unicode code points <= U+FFFF.
 *
 * Whenever we encounter a Unicode code point > U+FFFF, we represent that
 * as a set transition (even if it is logically an atom or a range).
 */
public abstract class CodePointTransitions {
	/**
	 * If {@code codePoint} is <= U+FFFF, returns a new {@link AtomTransition}.
	 * Otherwise, returns a new {@link SetTransition}.
	 */
	public static Transition createWithCodePoint(ATNState target, int codePoint, boolean caseInsensitive) {
		return createWithCodePointRange(target, codePoint, codePoint, caseInsensitive);
	}

	/**
	 * If {@code codePointFrom} and {@code codePointTo} are both
	 * <= U+FFFF, returns a new {@link RangeTransition}.
	 * Otherwise, returns a new {@link SetTransition}.
	 */
	public static Transition createWithCodePointRange(
			ATNState target,
			int codePointFrom,
			int codePointTo,
			boolean caseInsensitive) {
		if (caseInsensitive) {
			int lowerCodePointFrom = Character.toLowerCase(codePointFrom);
			int upperCodePointFrom = Character.toUpperCase(codePointFrom);
			int lowerCodePointTo = Character.toLowerCase(codePointTo);
			int upperCodePointTo = Character.toUpperCase(codePointTo);
			if (lowerCodePointFrom == upperCodePointFrom && lowerCodePointTo == upperCodePointTo) {
				return createWithCodePointRange(target, lowerCodePointFrom, lowerCodePointTo, false);
			} else  {
				IntervalSet intervalSet = new IntervalSet();
				intervalSet.add(lowerCodePointFrom, lowerCodePointTo);
				intervalSet.add(upperCodePointFrom, upperCodePointTo);
				return new SetTransition(target, intervalSet);
			}
		} else {
			if (Character.isSupplementaryCodePoint(codePointFrom) ||
					Character.isSupplementaryCodePoint(codePointTo)) {
				return new SetTransition(target, IntervalSet.of(codePointFrom, codePointTo));
			} else {
				return codePointFrom == codePointTo
						? new AtomTransition(target, codePointFrom)
						: new RangeTransition(target, codePointFrom, codePointTo);
			}
		}
	}
}
