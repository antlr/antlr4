/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/**
 * Utility class to create {@link AtomTransition}, {@link RangeTransition},
 * and {@link SetTransition} appropriately based on the range of the input.
 *
 * Previously, we distinguished between atom and range transitions for
 * Unicode code points <= U+FFFF and those above. We used a set
 * transition for a Unicode code point > U+FFFF. Now that we can serialize
 * 32-bit int/chars in the ATN serialization, this is no longer necessary.
 */
public abstract class CodePointTransitions {
	/** Return new {@link AtomTransition} */
	public static Transition createWithCodePoint(ATNState target, int codePoint) {
		return createWithCodePointRange(target, codePoint, codePoint);
	}

	/** Return new {@link AtomTransition} if range represents one atom else {@link SetTransition}. */
	public static Transition createWithCodePointRange(ATNState target, int codePointFrom, int codePointTo) {
		return codePointFrom == codePointTo
				? new AtomTransition(target, codePointFrom)
				: new RangeTransition(target, codePointFrom, codePointTo);
	}
}
