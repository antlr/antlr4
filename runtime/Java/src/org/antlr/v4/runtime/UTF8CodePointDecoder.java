/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;

/**
 * Decodes UTF-8 bytes directly to Unicode code points, stored in an
 * {@link IntBuffer}.
 *
 * Unlike {@link CharsetDecoder}, this does not use UTF-16 as an
 * intermediate representation, so this optimizes the common case of
 * decoding a UTF-8 file for parsing as Unicode code points.
 */
public class UTF8CodePointDecoder {
	private static final int SUBSTITUTION_CHARACTER = 0xFFFD;
	private static final byte NVAL = (byte) 0xFF;

	// Table mapping UTF-8 leading byte to the length of the trailing
	// sequence.
	protected static final byte[] UTF8_LEADING_BYTE_LENGTHS = new byte[] {
		// [0x00, 0x7F] -> 0 trailing bytes
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

		// [0x80, 0xBF] -> invalid leading byte
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL,
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL,
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL,
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL,
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL,
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL,
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL,
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL,

		// [0xC0, 0xDF] -> one trailing byte
		0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
		0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
		0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
		0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,

		// [0xE0, 0xEF] -> two trailing bytes
		0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02,
		0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02,

		// [0xF0, 0xF7] -> three trailing bytes
		0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03,

		// [0xF8, 0xFF] -> invalid leading sequence
		NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL, NVAL
	};

	// Table mapping UTF-8 sequence length to valid Unicode code point
	// ranges for that sequence length.
	protected static final Interval[] UTF8_VALID_INTERVALS = new Interval[] {
		Interval.of(0x00, 0x7F),
		Interval.of(0x80, 0x7FF),
		Interval.of(0x800, 0xFFFF),
		Interval.of(0x10000, 0x10FFFF)
	};

	protected final CodingErrorAction decodingErrorAction;
	protected int decodingTrailBytesNeeded;
	protected int decodingCurrentCodePoint;
	protected Interval validDecodedCodePointRange;

	/**
	 * Constructs a new {@link UTF8CodePointDecoder} with a specified
	 * {@link CodingErrorAction} to handle invalid UTF-8 sequences.
	 */
	public UTF8CodePointDecoder(CodingErrorAction decodingErrorAction) {
		this.decodingErrorAction = decodingErrorAction;
		reset();
	}

	/**
	 * Resets the state in this {@link UTF8CodePointDecoder}, preparing it
	 * for use with a new input buffer.
	 */
	public void reset() {
		this.decodingTrailBytesNeeded = -1;
		this.decodingCurrentCodePoint = -1;
		this.validDecodedCodePointRange = Interval.INVALID;
	}

	/**
	 * Decodes as many UTF-8 bytes as possible from {@code utf8BytesIn},
	 * writing the result to {@code codePointsOut}.
	 *
	 * If you have more bytes to decode, set {@code endOfInput} to
	 * {@code false} and call this method again once more bytes
	 * are available.
	 *
	 * If there are no more bytes available, make sure to call this
	 * setting {@code endOfInput} to {@code true} so that any invalid
	 * UTF-8 sequence at the end of the input is handled.
	 *
	 * If {@code codePointsOut} is not large enough to store the result,
	 * a new buffer is allocated and returned. Otherwise, returns
	 * {@code codePointsOut}.
	 *
	 * After returning, the {@link ByteBuffer#position position} of
	 * {@code utf8BytesIn} is moved forward to reflect the bytes consumed,
	 * and the {@link IntBuffer#position position} of the result
	 * is moved forward to reflect the code points written.
	 *
	 * The {@link IntBuffer#limit limit} of the result is not changed,
	 * so if this is the end of the input, you will want to set the
	 * limit to the {@link IntBuffer#position position}, then
	 * {@link IntBuffer#flip flip} the result to prepare for reading.
	 */
	public IntBuffer decodeCodePointsFromBuffer(
			ByteBuffer utf8BytesIn,
			IntBuffer codePointsOut,
			boolean endOfInput
	) throws CharacterCodingException {
		while (utf8BytesIn.hasRemaining()) {
			if (decodingTrailBytesNeeded == -1) {
				// Start a new UTF-8 sequence by checking the leading byte.
				byte leadingByte = utf8BytesIn.get();
				if (!decodeLeadingByte(leadingByte)) {
					codePointsOut = handleDecodeError(
						String.format("Invalid UTF-8 leading byte 0x%02X", leadingByte),
						codePointsOut);
					reset();
					continue;
				}
			}
			assert decodingTrailBytesNeeded != -1;
			if (utf8BytesIn.remaining() < decodingTrailBytesNeeded) {
				// The caller will have to call us back with more bytes.
				break;
			}
			// Now we know the input buffer has enough bytes to decode
			// the entire sequence.
			while (decodingTrailBytesNeeded > 0) {
				// Continue a multi-byte UTF-8 sequence by checking the next trailing byte.
				byte trailingByte = utf8BytesIn.get();
				decodingTrailBytesNeeded--;
				if (!decodeTrailingByte(trailingByte)) {
					codePointsOut = handleDecodeError(
							String.format("Invalid UTF-8 trailing byte 0x%02X", trailingByte),
							codePointsOut);
					// Skip past any remaining trailing bytes in the sequence.
					utf8BytesIn.position(utf8BytesIn.position() + decodingTrailBytesNeeded);
					reset();
					continue;
				}
			}
			if (decodingTrailBytesNeeded == 0) {
				codePointsOut = appendCodePointFromInterval(
						decodingCurrentCodePoint,
						validDecodedCodePointRange,
						codePointsOut);
				reset();
				continue;
			}
		}
		if (endOfInput) {
			if (decodingTrailBytesNeeded != -1) {
				codePointsOut = handleDecodeError(
						"Unterminated UTF-8 sequence at end of bytes",
						codePointsOut);
			}
		}
		return codePointsOut;
	}

	private boolean decodeLeadingByte(byte leadingByte) {
		// Be careful about Java silently widening (unsigned)
		// byte to (signed) int and sign-extending here.
		//
		// We use binary AND liberally below to prevent widening.
		int leadingByteIdx = leadingByte & 0xFF;
		decodingTrailBytesNeeded = UTF8_LEADING_BYTE_LENGTHS[leadingByteIdx];
		switch (decodingTrailBytesNeeded) {
			case 0:
				decodingCurrentCodePoint = leadingByte;
				break;
			case 1:
			case 2:
			case 3:
				int mask = (0b00111111 >> decodingTrailBytesNeeded);
				decodingCurrentCodePoint = leadingByte & mask;
				break;
			default:
				return false;
		}
		validDecodedCodePointRange = UTF8_VALID_INTERVALS[decodingTrailBytesNeeded];
		return true;
	}

	private boolean decodeTrailingByte(byte trailingByte) {
		int trailingValue = (trailingByte & 0xFF) - 0x80;
		if (trailingValue < 0x00 || trailingValue > 0x3F) {
			return false;
		} else {
			decodingCurrentCodePoint = (decodingCurrentCodePoint << 6) | trailingValue;
			return true;
		}
	}

	private IntBuffer appendCodePointFromInterval(
			int codePoint,
			Interval validCodePointRange,
			IntBuffer codePointsOut
	) throws CharacterCodingException {
		assert validCodePointRange != Interval.INVALID;

		// Security check: UTF-8 must represent code points using their
		// shortest encoded form.
		if (codePoint < validCodePointRange.a ||
			codePoint > validCodePointRange.b) {
			return handleDecodeError(
					String.format(
							"Code point %d is out of expected range %s",
							codePoint,
							validCodePointRange),
					codePointsOut);
		} else {
			return appendCodePoint(codePoint, codePointsOut);
		}
	}

	private IntBuffer appendCodePoint(int codePoint, IntBuffer codePointsOut) {
		if (!codePointsOut.hasRemaining()) {
			// Grow the code point buffer size by 2.
			IntBuffer newBuffer = IntBuffer.allocate(codePointsOut.capacity() * 2);
			codePointsOut.flip();
			newBuffer.put(codePointsOut);
			codePointsOut = newBuffer;
		}
		codePointsOut.put(codePoint);
		return codePointsOut;
	}

	private IntBuffer handleDecodeError(
			final String error,
			IntBuffer codePointsOut
	) throws CharacterCodingException {
		if (decodingErrorAction == CodingErrorAction.REPLACE) {
			codePointsOut = appendCodePoint(SUBSTITUTION_CHARACTER, codePointsOut);
		} else if (decodingErrorAction == CodingErrorAction.REPORT) {
			throw new CharacterCodingException() {
				@Override
				public String getMessage() {
					return error;
				}
			};
		}
		return codePointsOut;
	}
}
