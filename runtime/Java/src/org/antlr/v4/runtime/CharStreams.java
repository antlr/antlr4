/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class to create {@link CharStream}s from various sources of
 * string data.
 *
 * Main entry points are the factory methods {@code CharStreams.fromPath()},
 * {@code CharStreams.fromString()}, etc.
 */
public final class CharStreams {
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	// Utility class; do not construct.
	private CharStreams() { }

	/**
	 * Creates a {@link CharStream} given a path to a UTF-8
	 * encoded file on disk.
	 *
	 * Reads the entire contents of the file into the result before returning.
	 */
	public static CharStream fromPath(Path path) throws IOException {
		return fromPath(path, StandardCharsets.UTF_8);
	}

	/**
	 * Creates a {@link CharStream} given a path to a file on disk and the
	 * charset of the bytes contained in the file.
	 *
	 * Reads the entire contents of the file into the result before returning.
	 *
	 * For sources encoded in UTF-8, supports the full Unicode code point
	 * range.
	 *
	 * For other sources, only supports Unicode code points up to U+FFFF.
	 */
	public static CharStream fromPath(Path path, Charset charset) throws IOException {
		if (charset.equals(StandardCharsets.UTF_8)) {
			try (ReadableByteChannel channel = Files.newByteChannel(path)) {
				return fromChannel(
					channel,
					DEFAULT_BUFFER_SIZE,
					CodingErrorAction.REPLACE,
					path.toString());
			}
		} else {
			return new ANTLRFileStream(path.toString(), charset.toString());
		}
	}

	/**
	 * Creates a {@link CharStream} given a string containing a
	 * path to a UTF-8 file on disk.
	 *
	 * Reads the entire contents of the file into the result before returning.
	 */
	public static CharStream fromFileName(String fileName) throws IOException {
		return fromPath(Paths.get(fileName), StandardCharsets.UTF_8);
	}

	/**
	 * Creates a {@link CharStream} given a string containing a
	 * path to a file on disk and the charset of the bytes
	 * contained in the file.
	 *
	 * Reads the entire contents of the file into the result before returning.
	 *
	 * For sources encoded in UTF-8, supports the full Unicode code point
	 * range.
	 *
	 * For other sources, only supports Unicode code points up to U+FFFF.
	 */
	public static CharStream fromFileName(String fileName, Charset charset) throws IOException {
		return fromPath(Paths.get(fileName), charset);
	}


	/**
	 * Creates a {@link CharStream} given an opened {@link InputStream}
         * containing UTF-8 bytes.
	 *
	 * Reads the entire contents of the {@code InputStream} into
	 * the result before returning, then closes the {@code InputStream}.
	 */
        public static CharStream fromStream(InputStream is) throws IOException {
                return fromStream(is, StandardCharsets.UTF_8);
        }

/**
	 * Creates a {@link CharStream} given an opened {@link InputStream} and the
	 * charset of the bytes contained in the stream.
	 *
	 * Reads the entire contents of the {@code InputStream} into
	 * the result before returning, then closes the {@code InputStream}.
	 *
	 * For sources encoded in UTF-8, supports the full Unicode code point
	 * range.
	 *
	 * For other sources, only supports Unicode code points up to U+FFFF.
	 */
	public static CharStream fromStream(InputStream is, Charset charset) throws IOException {
		if (charset.equals(StandardCharsets.UTF_8)) {
			try (ReadableByteChannel channel = Channels.newChannel(is)) {
				return fromChannel(
						channel,
						DEFAULT_BUFFER_SIZE,
						CodingErrorAction.REPLACE,
						IntStream.UNKNOWN_SOURCE_NAME);
			}
		} else {
			try (InputStreamReader isr = new InputStreamReader(is, charset)) {
				return new ANTLRInputStream(isr);
			}
		}
	}

	/**
	 * Creates a {@link CharStream} given an opened {@link ReadableByteChannel}
	 * containing UTF-8 bytes.
	 *
	 * Reads the entire contents of the {@code channel} into
	 * the result before returning, then closes the {@code channel}.
	 */
	public static CharStream fromChannel(ReadableByteChannel channel) throws IOException {
		return fromChannel(channel, StandardCharsets.UTF_8);
	}

	/**
	 * Creates a {@link CharStream} given an opened {@link ReadableByteChannel} and the
	 * charset of the bytes contained in the channel.
	 *
	 * Reads the entire contents of the {@code channel} into
	 * the result before returning, then closes the {@code channel}.
	 *
	 * For sources encoded in UTF-8, supports the full Unicode code point
	 * range.
	 *
	 * For other sources, only supports Unicode code points up to U+FFFF.
	 */
	public static CharStream fromChannel(ReadableByteChannel channel, Charset charset) throws IOException {
		if (charset.equals(StandardCharsets.UTF_8)) {
			return fromChannel(
					channel,
					DEFAULT_BUFFER_SIZE,
					CodingErrorAction.REPLACE,
					IntStream.UNKNOWN_SOURCE_NAME);
		} else {
			try (InputStream is = Channels.newInputStream(channel);
			     InputStreamReader isr = new InputStreamReader(Channels.newInputStream(channel), charset)) {
				return new ANTLRInputStream(isr);
			}
		}
	}

	/**
	 * Creates a {@link CharStream} given a {@link Reader}. Closes
	 * the reader before returning.
	 */
	public static CodePointCharStream fromReader(Reader r) throws IOException {
		return fromReader(r, IntStream.UNKNOWN_SOURCE_NAME);
	}

	/**
	 * Creates a {@link CharStream} given a {@link Reader} and its
	 * source name. Closes the reader before returning.
	 */
	public static CodePointCharStream fromReader(Reader r, String sourceName) throws IOException {
		IntBuffer codePointBuffer = IntBuffer.allocate(DEFAULT_BUFFER_SIZE);
		int highSurrogate = -1;
		int curCodeUnit;
		try {
			while ((curCodeUnit = r.read()) != -1) {
				if (!codePointBuffer.hasRemaining()) {
					// Grow the code point buffer size by 2.
					IntBuffer newBuffer = IntBuffer.allocate(codePointBuffer.capacity() * 2);
					codePointBuffer.flip();
					newBuffer.put(codePointBuffer);
					codePointBuffer = newBuffer;
				}
				if (Character.isHighSurrogate((char) curCodeUnit)) {
					if (highSurrogate != -1) {
						// Dangling high surrogate followed by another high surrogate.
						codePointBuffer.put(highSurrogate);
					}
					highSurrogate = curCodeUnit;
				} else if (Character.isLowSurrogate((char) curCodeUnit)) {
					if (highSurrogate == -1) {
						// Low surrogate not preceded by high surrogate.
						codePointBuffer.put(curCodeUnit);
					} else {
						codePointBuffer.put(Character.toCodePoint((char) highSurrogate, (char) curCodeUnit));
						highSurrogate = -1;
					}
				} else {
					if (highSurrogate != -1) {
						// Dangling high surrogate followed by a non-surrogate.
						codePointBuffer.put(highSurrogate);
						highSurrogate = -1;
					}
					codePointBuffer.put(curCodeUnit);
				}
			}
			if (highSurrogate != -1) {
				// Dangling high surrogate at end of file.
				codePointBuffer.put(highSurrogate);
			}
			codePointBuffer.flip();
			return new CodePointCharStream(codePointBuffer, sourceName);
		} finally {
			r.close();
		}
	}

	/**
	 * Creates a {@link CharStream} given a {@link String}.
	 */
	public static CodePointCharStream fromString(String s) {
		return fromString(s, IntStream.UNKNOWN_SOURCE_NAME);
	}

	/**
	 * Creates a {@link CharStream} given a {@link String} and the {@code sourceName}
	 * from which it came.
	 */
	public static CodePointCharStream fromString(String s, String sourceName) {
		// Initial guess assumes no code points > U+FFFF: one code
		// point for each code unit in the string
		IntBuffer codePointBuffer = IntBuffer.allocate(s.length());
		int stringIdx = 0;
		while (stringIdx < s.length()) {
			if (!codePointBuffer.hasRemaining()) {
				// Grow the code point buffer size by 2.
				IntBuffer newBuffer = IntBuffer.allocate(codePointBuffer.capacity() * 2);
				codePointBuffer.flip();
				newBuffer.put(codePointBuffer);
				codePointBuffer = newBuffer;
			}
			int codePoint = Character.codePointAt(s, stringIdx);
			codePointBuffer.put(codePoint);
			stringIdx += Character.charCount(codePoint);
		}
		codePointBuffer.flip();
		return new CodePointCharStream(codePointBuffer, sourceName);
	}

	/**
	 * Creates a {@link CharStream} given an opened {@link ReadableByteChannel}
	 * containing UTF-8 bytes.
	 *
	 * Reads the entire contents of the {@code channel} into
	 * the result before returning, then closes the {@code channel}.
	 */
	public static CodePointCharStream fromChannel(
			ReadableByteChannel channel,
			int bufferSize,
			CodingErrorAction decodingErrorAction,
			String sourceName
	) throws IOException {
		try {
			ByteBuffer utf8BytesIn = ByteBuffer.allocateDirect(bufferSize);
			IntBuffer codePointsOut = IntBuffer.allocate(bufferSize);
			boolean endOfInput = false;
			UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(decodingErrorAction);
			while (!endOfInput) {
				int bytesRead = channel.read(utf8BytesIn);
				endOfInput = (bytesRead == -1);
				utf8BytesIn.flip();
				codePointsOut = decoder.decodeCodePointsFromBuffer(
						utf8BytesIn,
						codePointsOut,
						endOfInput);
				utf8BytesIn.compact();
			}
			codePointsOut.limit(codePointsOut.position());
			codePointsOut.flip();
			return new CodePointCharStream(codePointsOut, sourceName);
		} finally {
			channel.close();
		}
	}
}
