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
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
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
	 */
	public static CharStream fromPath(Path path, Charset charset) throws IOException {
		long size = Files.size(path);
		try (ReadableByteChannel channel = Files.newByteChannel(path)) {
			return fromChannel(
				channel,
				charset,
				DEFAULT_BUFFER_SIZE,
				CodingErrorAction.REPLACE,
				path.toString(),
				size);
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
	 */
	public static CharStream fromStream(InputStream is, Charset charset) throws IOException {
		return fromStream(is, charset, -1);
	}

	public static CharStream fromStream(InputStream is, Charset charset, long inputSize) throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(is)) {
			return fromChannel(
				channel,
				charset,
				DEFAULT_BUFFER_SIZE,
				CodingErrorAction.REPLACE,
				IntStream.UNKNOWN_SOURCE_NAME,
				inputSize);
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
	 */
	public static CharStream fromChannel(ReadableByteChannel channel, Charset charset) throws IOException {
		return fromChannel(
			channel,
			DEFAULT_BUFFER_SIZE,
			CodingErrorAction.REPLACE,
			IntStream.UNKNOWN_SOURCE_NAME);
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
		try {
			CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder(DEFAULT_BUFFER_SIZE);
			CharBuffer charBuffer = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);
			while ((r.read(charBuffer)) != -1) {
				charBuffer.flip();
				codePointBufferBuilder.append(charBuffer);
				charBuffer.compact();
			}
			return CodePointCharStream.fromBuffer(codePointBufferBuilder.build(), sourceName);
		}
		finally {
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
		CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder(s.length());
		// TODO: CharBuffer.wrap(String) rightfully returns a read-only buffer
		// which doesn't expose its array, so we make a copy.
		CharBuffer cb = CharBuffer.allocate(s.length());
		cb.put(s);
		cb.flip();
		codePointBufferBuilder.append(cb);
		return CodePointCharStream.fromBuffer(codePointBufferBuilder.build(), sourceName);
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
		String sourceName)
		throws IOException
	{
		return fromChannel(channel, StandardCharsets.UTF_8, bufferSize, decodingErrorAction, sourceName, -1);
	}

	public static CodePointCharStream fromChannel(
		ReadableByteChannel channel,
		Charset charset,
		int bufferSize,
		CodingErrorAction decodingErrorAction,
		String sourceName,
		long inputSize)
		throws IOException
	{
		try {
			ByteBuffer utf8BytesIn = ByteBuffer.allocate(bufferSize);
			CharBuffer utf16CodeUnitsOut = CharBuffer.allocate(bufferSize);
			if (inputSize == -1) {
				inputSize = bufferSize;
			} else if (inputSize > Integer.MAX_VALUE) {
				// ByteBuffer et al don't support long sizes
				throw new IOException(String.format("inputSize %d larger than max %d", inputSize, Integer.MAX_VALUE));
			}
			CodePointBuffer.Builder codePointBufferBuilder = CodePointBuffer.builder((int) inputSize);
			CharsetDecoder decoder = charset
					.newDecoder()
					.onMalformedInput(decodingErrorAction)
					.onUnmappableCharacter(decodingErrorAction);

			boolean endOfInput = false;
			while (!endOfInput) {
				int bytesRead = channel.read(utf8BytesIn);
				endOfInput = (bytesRead == -1);
				utf8BytesIn.flip();
				CoderResult result = decoder.decode(
					utf8BytesIn,
					utf16CodeUnitsOut,
					endOfInput);
				if (result.isError() && decodingErrorAction.equals(CodingErrorAction.REPORT)) {
					result.throwException();
				}
				utf16CodeUnitsOut.flip();
				codePointBufferBuilder.append(utf16CodeUnitsOut);
				utf8BytesIn.compact();
				utf16CodeUnitsOut.compact();
			}
			// Handle any bytes at the end of the file which need to
			// be represented as errors or substitution characters.
			CoderResult flushResult = decoder.flush(utf16CodeUnitsOut);
			if (flushResult.isError() && decodingErrorAction.equals(CodingErrorAction.REPORT)) {
				flushResult.throwException();
			}
			utf16CodeUnitsOut.flip();
			codePointBufferBuilder.append(utf16CodeUnitsOut);

			CodePointBuffer codePointBuffer = codePointBufferBuilder.build();
			return CodePointCharStream.fromBuffer(codePointBuffer, sourceName);
		}
		finally {
			channel.close();
		}
	}
}
