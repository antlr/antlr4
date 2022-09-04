/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestCharSupport {
	@Test
	public void testGetANTLRCharLiteralForChar() {
		assertEquals("'<INVALID>'",
			CharSupport.getANTLRCharLiteralForChar(-1));
		assertEquals("'\\n'",
			CharSupport.getANTLRCharLiteralForChar('\n'));
		assertEquals("'\\\\'",
			CharSupport.getANTLRCharLiteralForChar('\\'));
		assertEquals("'\\''",
			CharSupport.getANTLRCharLiteralForChar('\''));
		assertEquals("'b'",
			CharSupport.getANTLRCharLiteralForChar('b'));
		assertEquals("'\\uFFFF'",
			CharSupport.getANTLRCharLiteralForChar(0xFFFF));
		assertEquals("'\\u{10FFFF}'",
			CharSupport.getANTLRCharLiteralForChar(0x10FFFF));
	}

	@Test
	public void testGetCharValueFromGrammarCharLiteral() {
		assertEquals(-1,
			CharSupport.getCharValueFromGrammarCharLiteral(null));
		assertEquals(-1,
			CharSupport.getCharValueFromGrammarCharLiteral(""));
		assertEquals(-1,
			CharSupport.getCharValueFromGrammarCharLiteral("b"));
		assertEquals(111,
			CharSupport.getCharValueFromGrammarCharLiteral("foo"));
	}

	@Test
	public void testGetStringFromGrammarStringLiteral() {
		assertNull(CharSupport
			.getStringFromGrammarStringLiteral("foo\\u{bbb"));
		assertNull(CharSupport
			.getStringFromGrammarStringLiteral("foo\\u{[]bb"));
		assertNull(CharSupport
			.getStringFromGrammarStringLiteral("foo\\u[]bb"));
		assertNull(CharSupport
			.getStringFromGrammarStringLiteral("foo\\ubb"));

		assertEquals("oo»b", CharSupport
			.getStringFromGrammarStringLiteral("foo\\u{bb}bb"));
	}

	@Test
	public void testGetCharValueFromCharInGrammarLiteral() {
		assertEquals(102,
			CharSupport.getCharValueFromCharInGrammarLiteral("f"));

		assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("\' "));
		assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\ "));
		assertEquals(39,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\\'"));
		assertEquals(10,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\n"));

		assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("foobar"));
		assertEquals(4660,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\u1234"));
		assertEquals(18,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\u{12}"));

		assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\u{"));
		assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("foo"));
	}

	@Test
	public void testParseHexValue() {
		assertEquals(-1, CharSupport.parseHexValue("foobar", -1, 3));
		assertEquals(-1, CharSupport.parseHexValue("foobar", 1, -1));
		assertEquals(-1, CharSupport.parseHexValue("foobar", 1, 3));
		assertEquals(35, CharSupport.parseHexValue("123456", 1, 3));
	}

	@Test
	public void testCapitalize() {
		assertEquals("Foo", CharSupport.capitalize("foo"));
	}

	@Test
	public void testGetIntervalSetEscapedString() {
		assertEquals("",
			CharSupport.getIntervalSetEscapedString(new IntervalSet()));
		assertEquals("'\\u0000'",
			CharSupport.getIntervalSetEscapedString(new IntervalSet(0)));
		assertEquals("'\\u0001'..'\\u0003'",
			CharSupport.getIntervalSetEscapedString(new IntervalSet(3, 1, 2)));
	}

	@Test
	public void testGetRangeEscapedString() {
		assertEquals("'\\u0002'..'\\u0004'",
			CharSupport.getRangeEscapedString(2, 4));
		assertEquals("'\\u0002'",
			CharSupport.getRangeEscapedString(2, 2));
	}
}
