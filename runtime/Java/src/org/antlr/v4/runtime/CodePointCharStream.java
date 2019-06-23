/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.nio.charset.StandardCharsets;

/**
 * Alternative to {@link ANTLRInputStream} which treats the input
 * as a series of Unicode code points, instead of a series of UTF-16
 * code units.
 *
 * Use this if you need to parse input which potentially contains
 * Unicode values > U+FFFF.
 */
public abstract class CodePointCharStream implements CharStream {
	protected final int size;
	protected final String name;

	// To avoid lots of virtual method calls, we directly access
	// the state of the underlying code points in the
	// CodePointBuffer.
	protected int position;

	// Use the factory method {@link #fromBuffer(CodePointBuffer)} to
	// construct instances of this type.
	private CodePointCharStream(int position, int remaining, String name) {
		// TODO
		assert position == 0;
		this.size = remaining;
		this.name = name;
		this.position = 0;
	}

	// Visible for testing.
	abstract Object getInternalStorage();

	/**
	 * Constructs a {@link CodePointCharStream} which provides access
	 * to the Unicode code points stored in {@code codePointBuffer}.
	 */
	public static CodePointCharStream fromBuffer(CodePointBuffer codePointBuffer) {
		return fromBuffer(codePointBuffer, UNKNOWN_SOURCE_NAME);
	}

	/**
	 * Constructs a named {@link CodePointCharStream} which provides access
	 * to the Unicode code points stored in {@code codePointBuffer}.
	 */
	public static CodePointCharStream fromBuffer(CodePointBuffer codePointBuffer, String name) {
		// Java lacks generics on primitive types.
		//
		// To avoid lots of calls to virtual methods in the
		// very hot codepath of LA() below, we construct one
		// of three concrete subclasses.
		//
		// The concrete subclasses directly access the code
		// points stored in the underlying array (byte[],
		// char[], or int[]), so we can avoid lots of virtual
		// method calls to ByteBuffer.get(offset).
		switch (codePointBuffer.getType()) {
			case BYTE:
				return new CodePoint8BitCharStream(
						codePointBuffer.position(),
						codePointBuffer.remaining(),
						name,
						codePointBuffer.byteArray(),
						codePointBuffer.arrayOffset());
			case CHAR:
				return new CodePoint16BitCharStream(
						codePointBuffer.position(),
						codePointBuffer.remaining(),
						name,
						codePointBuffer.charArray(),
						codePointBuffer.arrayOffset());
			case INT:
				return new CodePoint32BitCharStream(
						codePointBuffer.position(),
						codePointBuffer.remaining(),
						name,
						codePointBuffer.intArray(),
						codePointBuffer.arrayOffset());
		}
		throw new UnsupportedOperationException("Not reached");
	}

	@Override
	public final void consume() {
		if (size - position == 0) {
			assert LA(1) == IntStream.EOF;
			throw new IllegalStateException("cannot consume EOF");
		}
		position = position + 1;
	}

	@Override
	public final int index() {
		return position;
	}

	@Override
	public final int size() {
		return size;
	}

	/** mark/release do nothing; we have entire buffer */
	@Override
	public final int mark() {
		return -1;
	}

	@Override
	public final void release(int marker) {
	}

	@Override
	public final void seek(int index) {
		position = index;
	}

	@Override
	public final String getSourceName() {
		if (name == null || name.isEmpty()) {
			return UNKNOWN_SOURCE_NAME;
		}

		return name;
	}

	@Override
	public final String toString() {
		return getText(Interval.of(0, size - 1));
	}

	// 8-bit storage for code points <= U+00FF.
	private static final class CodePoint8BitCharStream extends CodePointCharStream {
		private final byte[] byteArray;

		private CodePoint8BitCharStream(int position, int remaining, String name, byte[] byteArray, int arrayOffset) {
			super(position, remaining, name);
			// TODO
			assert arrayOffset == 0;
			this.byteArray = byteArray;
		}

		/** Return the UTF-16 encoded string for the given interval */
		@Override
		public String getText(Interval interval) {
			int startIdx = Math.min(interval.a, size);
			int len = Math.min(interval.b - interval.a + 1, size - startIdx);

			// We know the maximum code point in byteArray is U+00FF,
			// so we can treat this as if it were ISO-8859-1, aka Latin-1,
			// which shares the same code points up to 0xFF.
			return new String(byteArray, startIdx, len, StandardCharsets.ISO_8859_1);
		}

		@Override
		public int LA(int i) {
			int offset;
			switch (Integer.signum(i)) {
				case -1:
					offset = position + i;
					if (offset < 0) {
						return IntStream.EOF;
					}
					return byteArray[offset] & 0xFF;
				case 0:
					// Undefined
					return 0;
				case 1:
					offset = position + i - 1;
					if (offset >= size) {
						return IntStream.EOF;
					}
					return byteArray[offset] & 0xFF;
			}
			throw new UnsupportedOperationException("Not reached");
		}

		@Override
		Object getInternalStorage() {
			return byteArray;
		}
	}

	// 16-bit internal storage for code points between U+0100 and U+FFFF.
	private static final class CodePoint16BitCharStream extends CodePointCharStream {
		private final char[] charArray;

		private CodePoint16BitCharStream(int position, int remaining, String name, char[] charArray, int arrayOffset) {
			super(position, remaining, name);
			this.charArray = charArray;
			// TODO
			assert arrayOffset == 0;
		}

		/** Return the UTF-16 encoded string for the given interval */
		@Override
		public String getText(Interval interval) {
			int startIdx = Math.min(interval.a, size);
			int len = Math.min(interval.b - interval.a + 1, size - startIdx);

			// We know there are no surrogates in this
			// array, since otherwise we would be given a
			// 32-bit int[] array.
			//
			// So, it's safe to treat this as if it were
			// UTF-16.
			return new String(charArray, startIdx, len);
		}

		@Override
		public int LA(int i) {
			int offset;
			switch (Integer.signum(i)) {
				case -1:
					offset = position + i;
					if (offset < 0) {
						return IntStream.EOF;
					}
					return charArray[offset] & 0xFFFF;
				case 0:
					// Undefined
					return 0;
				case 1:
					offset = position + i - 1;
					if (offset >= size) {
						return IntStream.EOF;
					}
					return charArray[offset] & 0xFFFF;
			}
			throw new UnsupportedOperationException("Not reached");
		}

		@Override
		Object getInternalStorage() {
			return charArray;
		}
	}

	// 32-bit internal storage for code points between U+10000 and U+10FFFF.
	private static final class CodePoint32BitCharStream extends CodePointCharStream {
		private final int[] intArray;

		private CodePoint32BitCharStream(int position, int remaining, String name, int[] intArray, int arrayOffset) {
			super(position, remaining, name);
			this.intArray = intArray;
			// TODO
			assert arrayOffset == 0;
		}

		/** Return the UTF-16 encoded string for the given interval */
		@Override
		public String getText(Interval interval) {
			int startIdx = Math.min(interval.a, size);
			int len = Math.min(interval.b - interval.a + 1, size - startIdx);

			// Note that we pass the int[] code points to the String constructor --
			// this is supported, and the constructor will convert to UTF-16 internally.
			return new String(intArray, startIdx, len);
		}

		@Override
		public int LA(int i) {
			int offset;
			switch (Integer.signum(i)) {
				case -1:
					offset = position + i;
					if (offset < 0) {
						return IntStream.EOF;
					}
					return intArray[offset];
				case 0:
					// Undefined
					return 0;
				case 1:
					offset = position + i - 1;
					if (offset >= size) {
						return IntStream.EOF;
					}
					return intArray[offset];
			}
			throw new UnsupportedOperationException("Not reached");
		}

		@Override
		Object getInternalStorage() {
			return intArray;
		}
	}
}
