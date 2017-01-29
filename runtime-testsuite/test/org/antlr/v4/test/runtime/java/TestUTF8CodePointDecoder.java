/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.UTF8CodePointDecoder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestUTF8CodePointDecoder {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void decodeEmptyByteBufferWritesNothing() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPLACE);
		ByteBuffer utf8BytesIn = ByteBuffer.allocate(0);
		IntBuffer codePointsOut = IntBuffer.allocate(0);
		IntBuffer result = decoder.decodeCodePointsFromBuffer(
				utf8BytesIn,
				codePointsOut,
				true);
		result.flip();
		assertEquals(0, result.remaining());
	}

	@Test
	public void decodeLatinByteBufferWritesCodePoint() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPLACE);
		ByteBuffer utf8BytesIn = StandardCharsets.UTF_8.encode("X");
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		IntBuffer result = decoder.decodeCodePointsFromBuffer(
				utf8BytesIn,
				codePointsOut,
				true);
		result.flip();
		assertEquals(1, result.remaining());
		assertEquals('X', result.get(0));
	}

	@Test
	public void decodeCyrillicByteBufferWritesCodePoint() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPLACE);
		ByteBuffer utf8BytesIn = StandardCharsets.UTF_8.encode("\u042F");
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		IntBuffer result = decoder.decodeCodePointsFromBuffer(
				utf8BytesIn,
				codePointsOut,
				true);
		result.flip();
		assertEquals(1, result.remaining());
		assertEquals(0x042F, result.get(0));
	}

	@Test
	public void decodeCJKByteBufferWritesCodePoint() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPLACE);
		ByteBuffer utf8BytesIn = StandardCharsets.UTF_8.encode("\u611B");
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		IntBuffer result = decoder.decodeCodePointsFromBuffer(
				utf8BytesIn,
				codePointsOut,
				true);
		result.flip();
		assertEquals(1, result.remaining());
		assertEquals(0x611B, result.get(0));
	}

	@Test
	public void decodeEmojiByteBufferWritesCodePoint() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPLACE);
		ByteBuffer utf8BytesIn = StandardCharsets.UTF_8.encode(
				new StringBuilder().appendCodePoint(0x1F4A9).toString()
		);
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		IntBuffer result = decoder.decodeCodePointsFromBuffer(
				utf8BytesIn,
				codePointsOut,
				true);
		result.flip();
		assertEquals(1, result.remaining());
		assertEquals(0x1F4A9, result.get(0));
	}

	@Test
	public void decodingInvalidLeadInReplaceModeWritesSubstitutionCharacter() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPLACE);
		ByteBuffer utf8BytesIn = ByteBuffer.wrap(new byte[] { (byte)0xF8 });
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		IntBuffer result = decoder.decodeCodePointsFromBuffer(utf8BytesIn, codePointsOut, true);
		result.flip();
		assertEquals(1, result.remaining());
		assertEquals(0xFFFD, result.get(0));
	}

	@Test
	public void decodingInvalidLeadInReportModeThrows() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPORT);
		ByteBuffer utf8BytesIn = ByteBuffer.wrap(new byte[] { (byte)0xF8 });
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		thrown.expect(CharacterCodingException.class);
		thrown.expectMessage("Invalid UTF-8 leading byte 0xF8");
		decoder.decodeCodePointsFromBuffer(utf8BytesIn, codePointsOut, true);
	}

	@Test
	public void decodingInvalidTrailInReplaceModeWritesSubstitutionCharacter() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPLACE);
		ByteBuffer utf8BytesIn = ByteBuffer.wrap(new byte[] { (byte)0xC0, (byte)0xC0 });
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		IntBuffer result = decoder.decodeCodePointsFromBuffer(utf8BytesIn, codePointsOut, true);
		result.flip();
		assertEquals(1, result.remaining());
		assertEquals(0xFFFD, result.get(0));
	}

	@Test
	public void decodingInvalidTrailInReportModeThrows() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPORT);
		ByteBuffer utf8BytesIn = ByteBuffer.wrap(new byte[] { (byte)0xC0, (byte)0xC0 });
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		thrown.expect(CharacterCodingException.class);
		thrown.expectMessage("Invalid UTF-8 trailing byte 0xC0");
		decoder.decodeCodePointsFromBuffer(utf8BytesIn, codePointsOut, true);
	}

	@Test
	public void decodingNonShortestFormInReplaceModeWritesSubstitutionCharacter() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPLACE);
		// 0xC1 0x9C would decode to \ (U+005C) if we didn't have this check
		ByteBuffer utf8BytesIn = ByteBuffer.wrap(new byte[] { (byte)0xC1, (byte)0x9C });
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		IntBuffer result = decoder.decodeCodePointsFromBuffer(utf8BytesIn, codePointsOut, true);
		result.flip();
		assertEquals(1, result.remaining());
		assertEquals(0xFFFD, result.get(0));
	}

	@Test
	public void decodingNonShortestFormInReportModeThrows() throws Exception {
		UTF8CodePointDecoder decoder = new UTF8CodePointDecoder(CodingErrorAction.REPORT);
		// 0xC1 0x9C would decode to \ (U+005C) if we didn't have this check
		ByteBuffer utf8BytesIn = ByteBuffer.wrap(new byte[] { (byte)0xC1, (byte)0x9C });
		IntBuffer codePointsOut = IntBuffer.allocate(1);
		thrown.expect(CharacterCodingException.class);
		thrown.expectMessage("Code point 92 is out of expected range 128..2047");
		decoder.decodeCodePointsFromBuffer(utf8BytesIn, codePointsOut, true);
	}
}
