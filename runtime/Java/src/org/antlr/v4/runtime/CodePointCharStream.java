/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.nio.IntBuffer;

/**
 * Alternative to {@link ANTLRInputStream} which treats the input
 * as a series of Unicode code points, instead of a series of UTF-16
 * code units.
 *
 * Use this if you need to parse input which potentially contains
 * Unicode values > U+FFFF.
 */
public final class CodePointCharStream implements CharStream {
	private final IntBuffer codePointBuffer;
	private final int initialPosition;
	private final int size;
	private final String name;

	/**
	 * Constructs a {@link CodePointCharStream} which provides access
	 * to the Unicode code points stored in {@code codePointBuffer}.
	 *
	 * {@code codePointBuffer}'s {@link IntBuffer#position position}
	 * reflects the first code point of the stream, and its
	 * {@link IntBuffer#limit limit} is just after the last code point
	 * of the stream.
	 */
	public CodePointCharStream(IntBuffer codePointBuffer) {
		this(codePointBuffer, UNKNOWN_SOURCE_NAME);
	}

	/**
	 * Constructs a named {@link CodePointCharStream} which provides access
	 * to the Unicode code points stored in {@code codePointBuffer}.
	 *
	 * {@code codePointBuffer}'s {@link IntBuffer#position position}
	 * reflects the first code point of the stream, and its
	 * {@link IntBuffer#limit limit} is just after the last code point
	 * of the stream.
	 */
	public CodePointCharStream(IntBuffer codePointBuffer, String name) {
		this.codePointBuffer = codePointBuffer;
		this.initialPosition = codePointBuffer.position();
		this.size = codePointBuffer.remaining();
		this.name = name;
	}

	private int relativeBufferPosition(int i) {
		return initialPosition + codePointBuffer.position() + i;
	}

	@Override
	public void consume() {
		if (!codePointBuffer.hasRemaining()) {
			assert LA(1) == IntStream.EOF;
			throw new IllegalStateException("cannot consume EOF");
		}
		codePointBuffer.position(codePointBuffer.position() + 1);
	}

	@Override
	public int LA(int i) {
		if (i == 0) {
			// Undefined
			return 0;
		} else if (i < 0) {
			if (codePointBuffer.position() + i < initialPosition) {
				return IntStream.EOF;
			}
			return codePointBuffer.get(relativeBufferPosition(i));
		} else if (i > codePointBuffer.remaining()) {
			return IntStream.EOF;
		} else {
			return codePointBuffer.get(relativeBufferPosition(i - 1));
		}
	}

	@Override
	public int index() {
		return codePointBuffer.position() - initialPosition;
	}

	@Override
	public int size() {
		return size;
	}

	/** mark/release do nothing; we have entire buffer */
	@Override
	public int mark() {
		return -1;
	}

	@Override
	public void release(int marker) {
	}

	@Override
	public void seek(int index) {
		codePointBuffer.position(initialPosition + index);
	}

	/** Return the UTF-16 encoded string for the given interval */
	@Override
	public String getText(Interval interval) {
		final int startIdx = initialPosition + Math.min(interval.a, size - 1);
		final int stopIdx = initialPosition + Math.min(interval.b, size - 1);
		// interval.length() will be too small if we contain any code points > U+FFFF,
		// but it's just a hint for initial capacity; StringBuilder will grow anyway.
		StringBuilder sb = new StringBuilder(interval.length());
		for (int codePointIdx = startIdx; codePointIdx <= stopIdx; codePointIdx++) {
			sb.appendCodePoint(codePointBuffer.get(codePointIdx));
		}
		return sb.toString();
	}

	@Override
	public String getSourceName() {
		if (name == null || name.isEmpty()) {
			return UNKNOWN_SOURCE_NAME;
		}

		return name;
	}

	@Override
	public String toString() {
		return getText(Interval.of(0, size - 1));
	}
}
