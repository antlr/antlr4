/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.misc.GrammarLiteralParser;
import org.antlr.v4.misc.CharParseResult;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestGrammarLiteralParser {
	@Test
	public void testParseCharValueFromGrammarStringLiteral() {
		assertEquals(-1, GrammarLiteralParser.parseCharFromStringLiteral(null).codePoint);
		assertEquals(-1, GrammarLiteralParser.parseCharFromStringLiteral("").codePoint);
		assertEquals(-1, GrammarLiteralParser.parseCharFromStringLiteral("b").codePoint);
		assertEquals(111, GrammarLiteralParser.parseCharFromStringLiteral("'o'").codePoint);
	}

	@Test
	public void testParseStringFromGrammarStringLiteral() {
		assertNull(GrammarLiteralParser.parseStringFromStringLiteral("foo\\u{bbb"));
		assertNull(GrammarLiteralParser.parseStringFromStringLiteral("foo\\u{[]bb"));
		assertNull(GrammarLiteralParser.parseStringFromStringLiteral("foo\\u[]bb"));
		assertNull(GrammarLiteralParser.parseStringFromStringLiteral("foo\\ubb"));

		assertEquals("oo»b", GrammarLiteralParser.parseStringFromStringLiteral("foo\\u{bb}bb"));
	}

	@Test
	public void testParseCharFromGrammarStringLiteral() {
		assertEquals(102, parseUnquotedChar("f", true));

		assertEquals(-1, parseUnquotedChar("' ", true));
		assertEquals(-1, parseUnquotedChar("\\ ", true));
		assertEquals(39, parseUnquotedChar("\\'", true));
		assertEquals(10, parseUnquotedChar("\\n", true));
		assertEquals(-1, parseUnquotedChar("\\]", true));
		assertEquals(-1, parseUnquotedChar("\\-", true));

		assertEquals(-1, parseUnquotedChar("foobar", true));
		assertEquals(4660, parseUnquotedChar("\\u1234", true));
		assertEquals(18, parseUnquotedChar("\\u{12}", true));

		assertEquals(-1, parseUnquotedChar("\\u{", true));
		assertEquals(-1, parseUnquotedChar("foo", true));
	}

	@Test
	public void testParseCharFromGrammarSetLiteral() {
		assertEquals(93, parseUnquotedChar("\\]", false));
		assertEquals(45, parseUnquotedChar("\\-", false));
		assertEquals(-1, parseUnquotedChar("\\'", false));
	}

	@Test
	public void testParseHexValue() {
		assertEquals(-1, GrammarLiteralParser.parseHexValue("foobar", -1, 3));
		assertEquals(-1, GrammarLiteralParser.parseHexValue("foobar", 1, -1));
		assertEquals(-1, GrammarLiteralParser.parseHexValue("foobar", 1, 3));
		assertEquals(35, GrammarLiteralParser.parseHexValue("123456", 1, 3));
	}

	@Test
	public void testParseEmpty() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("").type);
	}

	@Test
	public void testParseJustBackslash() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\").type);
	}

	@Test
	public void testParseInvalidEscape() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\z").type);
	}

	@Test
	public void testParseNewline() {
		assertEquals(CharParseResult.createCodePoint( '\n', 0,2), parseCharInSetLiteral("\\n"));
	}

	@Test
	public void testParseTab() {
		assertEquals(CharParseResult.createCodePoint('\t', 0,2), parseCharInSetLiteral("\\t"));
	}

	@Test
	public void testParseUnicodeTooShort() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\uABC").type);
	}

	@Test
	public void testParseUnicodeBMP() {
		assertEquals(CharParseResult.createCodePoint(0xABCD, 0,6), parseCharInSetLiteral("\\uABCD"));
	}

	@Test
	public void testParseUnicodeSMPTooShort() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\u{}").type);
	}

	@Test
	public void testParseUnicodeSMPMissingCloseBrace() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\u{12345").type);
	}

	@Test
	public void testParseUnicodeTooBig() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\u{110000}").type);
	}

	@Test
	public void testParseUnicodeSMP() {
		assertEquals(CharParseResult.createCodePoint(0x10ABCD, 0, 10), parseCharInSetLiteral("\\u{10ABCD}"));
	}

	@Test
	public void testParseUnicodePropertyTooShort() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\p{}").type);
	}

	@Test
	public void testParseUnicodePropertyMissingCloseBrace() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\p{1234").type);
	}

	@Test
	public void testParseUnicodeProperty() {
		assertEquals(
				CharParseResult.createProperty(IntervalSet.of(66560, 66639), 0, 11),
				parseCharInSetLiteral("\\p{Deseret}"));
	}

	@Test
	public void testParseUnicodePropertyInvertedTooShort() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\P{}").type);
	}

	@Test
	public void testParseUnicodePropertyInvertedMissingCloseBrace() {
		assertEquals(CharParseResult.Type.INVALID, parseCharInSetLiteral("\\P{Deseret").type);
	}

	@Test
	public void testParseUnicodePropertyInverted() {
		IntervalSet expected = IntervalSet.of(0, 66559);
		expected.add(66640, Character.MAX_CODE_POINT);
		assertEquals(CharParseResult.createProperty(expected, 0, 11), parseCharInSetLiteral("\\P{Deseret}"));
	}

	private int parseUnquotedChar(String s, boolean isStringLiteral) {
		return GrammarLiteralParser.parseChar(s, isStringLiteral, false).codePoint;
	}

	private CharParseResult parseCharInSetLiteral(String s) {
		return GrammarLiteralParser.parseNextChar(s, 0, s.length(), false);
	}
}
