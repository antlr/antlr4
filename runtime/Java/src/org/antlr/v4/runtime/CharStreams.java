/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class to create {@link CodePointCharStream}s from
 * various sources of Unicode data.
 */
public final class CharStreams {
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	// Utility class; do not construct.
	private CharStreams() { }

	/**
	 * Convenience method to create a {@link CodePointCharStream}
	 * for the Unicode code points in a Java {@link String}.
	 */
	public static CodePointCharStream createWithString(String s) {
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
		return new CodePointCharStream(codePointBuffer, IntStream.UNKNOWN_SOURCE_NAME);
	}

	public static CodePointCharStream createWithUTF8(Path path) throws IOException {
		try (ReadableByteChannel channel = Files.newByteChannel(path)) {
			return createWithUTF8Channel(
					channel,
					DEFAULT_BUFFER_SIZE,
					CodingErrorAction.REPLACE,
					path.toString());
		}
	}

	public static CodePointCharStream createWithUTF8Stream(InputStream is) throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(is)) {
			return createWithUTF8Channel(
					channel,
					DEFAULT_BUFFER_SIZE,
					CodingErrorAction.REPLACE,
					IntStream.UNKNOWN_SOURCE_NAME);
		}
	}

	public static CodePointCharStream createWithUTF8Channel(
			ReadableByteChannel channel,
			int bufferSize,
			CodingErrorAction decodingErrorAction,
			String sourceName
	) throws IOException {
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
	}
}
