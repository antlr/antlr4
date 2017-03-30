/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

/** A source of characters for an ANTLR lexer. */
public interface CharStream extends IntStream {
	/**
	 * This method returns the text for a range of characters within this input
	 * stream. This method is guaranteed to not throw an exception if the
	 * specified {@code interval} lies entirely within a marked range. For more
	 * information about marked ranges, see {@link IntStream#mark}.
	 *
	 * @param interval an interval within the stream
	 * @return the text of the specified interval
	 *
	 * @throws NullPointerException if {@code interval} is {@code null}
	 * @throws IllegalArgumentException if {@code interval.a < 0}, or if
	 * {@code interval.b < interval.a - 1}, or if {@code interval.b} lies at or
	 * past the end of the stream
	 * @throws UnsupportedOperationException if the stream does not support
	 * getting the text of the specified interval
	 */
	public String getText(Interval interval);
}
