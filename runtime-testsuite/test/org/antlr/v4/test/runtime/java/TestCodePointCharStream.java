/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.IntBuffer;

import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.IntStream;

import org.antlr.v4.runtime.misc.Interval;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestCodePointCharStream {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void emptyBytesHasSize0() {
		CodePointCharStream s = CodePointCharStream.createWithString("");
		assertEquals(0, s.size());
		assertEquals(0, s.index());
	}

	@Test
	public void emptyBytesLookAheadReturnsEOF() {
		CodePointCharStream s = CodePointCharStream.createWithString("");
		assertEquals(IntStream.EOF, s.LA(1));
		assertEquals(0, s.index());
	}

	@Test
	public void consumingEmptyStreamShouldThrow() {
		CodePointCharStream s = CodePointCharStream.createWithString("");
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("cannot consume EOF");
		s.consume();
	}

	@Test
	public void singleLatinCodePointHasSize1() {
		CodePointCharStream s = CodePointCharStream.createWithString("X");
		assertEquals(1, s.size());
	}

	@Test
	public void consumingSingleLatinCodePointShouldMoveIndex() {
		CodePointCharStream s = CodePointCharStream.createWithString("X");
		assertEquals(0, s.index());
		s.consume();
		assertEquals(1, s.index());
	}

	@Test
	public void consumingPastSingleLatinCodePointShouldThrow() {
		CodePointCharStream s = CodePointCharStream.createWithString("X");
		s.consume();
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("cannot consume EOF");
		s.consume();
	}

	@Test
	public void singleLatinCodePointLookAheadShouldReturnCodePoint() {
		CodePointCharStream s = CodePointCharStream.createWithString("X");
		assertEquals('X', s.LA(1));
		assertEquals(0, s.index());
	}

	@Test
	public void multipleLatinCodePointsLookAheadShouldReturnCodePoints() {
		CodePointCharStream s = CodePointCharStream.createWithString("XYZ");
		assertEquals('X', s.LA(1));
		assertEquals(0, s.index());
		assertEquals('Y', s.LA(2));
		assertEquals(0, s.index());
		assertEquals('Z', s.LA(3));
		assertEquals(0, s.index());
	}

	@Test
	public void singleLatinCodePointLookAheadPastEndShouldReturnEOF() {
		CodePointCharStream s = CodePointCharStream.createWithString("X");
		assertEquals(IntStream.EOF, s.LA(2));
	}

	@Test
	public void singleCJKCodePointHasSize1() {
		CodePointCharStream s = CodePointCharStream.createWithString("\u611B");
		assertEquals(1, s.size());
		assertEquals(0, s.index());
	}

	@Test
	public void consumingSingleCJKCodePointShouldMoveIndex() {
		CodePointCharStream s = CodePointCharStream.createWithString("\u611B");
		assertEquals(0, s.index());
		s.consume();
		assertEquals(1, s.index());
	}

	@Test
	public void consumingPastSingleCJKCodePointShouldThrow() {
		CodePointCharStream s = CodePointCharStream.createWithString("\u611B");
		s.consume();
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("cannot consume EOF");
		s.consume();
	}

	@Test
	public void singleCJKCodePointLookAheadShouldReturnCodePoint() {
		CodePointCharStream s = CodePointCharStream.createWithString("\u611B");
		assertEquals(0x611B, s.LA(1));
		assertEquals(0, s.index());
	}

	@Test
	public void singleCJKCodePointLookAheadPastEndShouldReturnEOF() {
		CodePointCharStream s = CodePointCharStream.createWithString("\u611B");
		assertEquals(IntStream.EOF, s.LA(2));
		assertEquals(0, s.index());
	}

	@Test
	public void singleEmojiCodePointHasSize1() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		assertEquals(1, s.size());
		assertEquals(0, s.index());
	}

	@Test
	public void consumingSingleEmojiCodePointShouldMoveIndex() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		assertEquals(0, s.index());
		s.consume();
		assertEquals(1, s.index());
	}

	@Test
	public void consumingPastEndOfEmojiCodePointWithShouldThrow() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		assertEquals(0, s.index());
		s.consume();
		assertEquals(1, s.index());
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("cannot consume EOF");
		s.consume();
	}

	@Test
	public void singleEmojiCodePointLookAheadShouldReturnCodePoint() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		assertEquals(0x1F4A9, s.LA(1));
		assertEquals(0, s.index());
	}

	@Test
	public void singleEmojiCodePointLookAheadPastEndShouldReturnEOF() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		assertEquals(IntStream.EOF, s.LA(2));
		assertEquals(0, s.index());
	}

	@Test
	public void getTextWithLatin() {
		CodePointCharStream s = CodePointCharStream.createWithString("0123456789");
		assertEquals("34567", s.getText(Interval.of(3, 7)));
	}

	@Test
	public void getTextWithCJK() {
		CodePointCharStream s = CodePointCharStream.createWithString("01234\u40946789");
		assertEquals("34\u409467", s.getText(Interval.of(3, 7)));
	}

	@Test
	public void getTextWithEmoji() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		assertEquals("34\uD83D\uDD2267", s.getText(Interval.of(3, 7)));
	}

	@Test
	public void toStringWithLatin() {
		CodePointCharStream s = CodePointCharStream.createWithString("0123456789");
		assertEquals("0123456789", s.toString());
	}

	@Test
	public void toStringWithCJK() {
		CodePointCharStream s = CodePointCharStream.createWithString("01234\u40946789");
		assertEquals("01234\u40946789", s.toString());
	}

	@Test
	public void toStringWithEmoji() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		assertEquals("01234\uD83D\uDD226789", s.toString());
	}

	@Test
	public void lookAheadWithLatin() {
		CodePointCharStream s = CodePointCharStream.createWithString("0123456789");
		assertEquals('5', s.LA(6));
	}

	@Test
	public void lookAheadWithCJK() {
		CodePointCharStream s = CodePointCharStream.createWithString("01234\u40946789");
		assertEquals(0x4094, s.LA(6));
	}

	@Test
	public void lookAheadWithEmoji() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		assertEquals(0x1F522, s.LA(6));
	}

	@Test
	public void seekWithLatin() {
		CodePointCharStream s = CodePointCharStream.createWithString("0123456789");
		s.seek(5);
		assertEquals('5', s.LA(1));
	}

	@Test
	public void seekWithCJK() {
		CodePointCharStream s = CodePointCharStream.createWithString("01234\u40946789");
		s.seek(5);
		assertEquals(0x4094, s.LA(1));
	}

	@Test
	public void seekWithEmoji() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		s.seek(5);
		assertEquals(0x1F522, s.LA(1));
	}

	@Test
	public void lookBehindWithLatin() {
		CodePointCharStream s = CodePointCharStream.createWithString("0123456789");
		s.seek(6);
		assertEquals('5', s.LA(-1));
	}

	@Test
	public void lookBehindWithCJK() {
		CodePointCharStream s = CodePointCharStream.createWithString("01234\u40946789");
		s.seek(6);
		assertEquals(0x4094, s.LA(-1));
	}

	@Test
	public void lookBehindWithEmoji() {
		CodePointCharStream s = CodePointCharStream.createWithString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		s.seek(6);
		assertEquals(0x1F522, s.LA(-1));
	}
}
