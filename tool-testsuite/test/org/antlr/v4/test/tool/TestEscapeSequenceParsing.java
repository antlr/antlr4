/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.misc.EscapeSequenceParsing;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.junit.jupiter.api.Test;

import static org.antlr.v4.misc.EscapeSequenceParsing.Result;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEscapeSequenceParsing {
	@Test
	public void testParseEmpty() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("", 0).type);
	}

	@Test
	public void testParseJustBackslash() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\", 0).type);
	}

	@Test
	public void testParseInvalidEscape() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\z", 0).type);
	}

	@Test
	public void testParseNewline() {
		assertEquals(
				new Result(Result.Type.CODE_POINT, '\n', IntervalSet.EMPTY_SET, 0,2),
				EscapeSequenceParsing.parseEscape("\\n", 0));
	}

	@Test
	public void testParseTab() {
		assertEquals(
				new Result(Result.Type.CODE_POINT, '\t', IntervalSet.EMPTY_SET, 0,2),
				EscapeSequenceParsing.parseEscape("\\t", 0));
	}

	@Test
	public void testParseUnicodeTooShort() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\uABC", 0).type);
	}

	@Test
	public void testParseUnicodeBMP() {
		assertEquals(
				new Result(Result.Type.CODE_POINT, 0xABCD, IntervalSet.EMPTY_SET, 0,6),
				EscapeSequenceParsing.parseEscape("\\uABCD", 0));
	}

	@Test
	public void testParseUnicodeSMPTooShort() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\u{}", 0).type);
	}

	@Test
	public void testParseUnicodeSMPMissingCloseBrace() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\u{12345", 0).type);
	}

	@Test
	public void testParseUnicodeTooBig() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\u{110000}", 0).type);
	}

	@Test
	public void testParseUnicodeSMP() {
		assertEquals(
				new Result(Result.Type.CODE_POINT, 0x10ABCD, IntervalSet.EMPTY_SET, 0,10),
				EscapeSequenceParsing.parseEscape("\\u{10ABCD}", 0));
	}

	@Test
	public void testParseUnicodePropertyTooShort() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\p{}", 0).type);
	}

	@Test
	public void testParseUnicodePropertyMissingCloseBrace() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\p{1234", 0).type);
	}

	@Test
	public void testParseUnicodeProperty() {
		assertEquals(
				new Result(Result.Type.PROPERTY, -1, IntervalSet.of(66560, 66639), 0,11),
				EscapeSequenceParsing.parseEscape("\\p{Deseret}", 0));
	}

	@Test
	public void testParseUnicodePropertyInvertedTooShort() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\P{}", 0).type);
	}

	@Test
	public void testParseUnicodePropertyInvertedMissingCloseBrace() {
		assertEquals(
				EscapeSequenceParsing.Result.Type.INVALID,
				EscapeSequenceParsing.parseEscape("\\P{Deseret", 0).type);
	}

	@Test
	public void testParseUnicodePropertyInverted() {
		IntervalSet expected = IntervalSet.of(0, 66559);
		expected.add(66640, Character.MAX_CODE_POINT);
		assertEquals(
				new Result(Result.Type.PROPERTY, -1, expected, 0, 11),
				EscapeSequenceParsing.parseEscape("\\P{Deseret}", 0));
	}
}
