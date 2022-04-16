/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.misc.AntlrCharSupport;
import org.antlr.v4.runtime.misc.CharSupport;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestCharSupport {
	@Test
	public void testGetPrintable() {
		assertEquals("'<INVALID>'", CharSupport.getPrintable(-1));
		assertEquals("'\\n'", CharSupport.getPrintable('\n'));
		assertEquals("'\\\\'", CharSupport.getPrintable('\\'));
		assertEquals("'\\''", CharSupport.getPrintable('\''));
		assertEquals("'b'", CharSupport.getPrintable('b'));
		assertEquals("'\\uFFFF'", CharSupport.getPrintable(0xFFFF));
		assertEquals("'\\u{10FFFF}'", CharSupport.getPrintable(0x10FFFF));
	}

	@Test
	public void testGetCharValueFromGrammarCharLiteral() {
		assertEquals(-1, AntlrCharSupport.getCharValueFromGrammarCharLiteral(null));
		assertEquals(-1, AntlrCharSupport.getCharValueFromGrammarCharLiteral(""));
		assertEquals(-1, AntlrCharSupport.getCharValueFromGrammarCharLiteral("b"));
		assertEquals(111, AntlrCharSupport.getCharValueFromGrammarCharLiteral("foo"));
	}

	@Test
	public void testGetStringFromGrammarStringLiteral() {
		assertNull(AntlrCharSupport.getStringFromGrammarStringLiteral("foo\\u{bbb"));
		assertNull(AntlrCharSupport.getStringFromGrammarStringLiteral("foo\\u{[]bb"));
		assertNull(AntlrCharSupport.getStringFromGrammarStringLiteral("foo\\u[]bb"));
		assertNull(AntlrCharSupport.getStringFromGrammarStringLiteral("foo\\ubb"));

		assertEquals("oo»b", AntlrCharSupport.getStringFromGrammarStringLiteral("foo\\u{bb}bb"));
	}

	@Test
	public void testGetCharValueFromCharInGrammarLiteral() {
		assertEquals(102, AntlrCharSupport.getCharValueFromCharInGrammarLiteral("f"));

		assertEquals(-1,	AntlrCharSupport.getCharValueFromCharInGrammarLiteral("\' "));
		assertEquals(-1, AntlrCharSupport.getCharValueFromCharInGrammarLiteral("\\ "));
		assertEquals(39,	AntlrCharSupport.getCharValueFromCharInGrammarLiteral("\\\'"));
		assertEquals(10,	AntlrCharSupport.getCharValueFromCharInGrammarLiteral("\\n"));

		assertEquals(-1,	AntlrCharSupport.getCharValueFromCharInGrammarLiteral("foobar"));
		assertEquals(4660, AntlrCharSupport.getCharValueFromCharInGrammarLiteral("\\u1234"));
		assertEquals(18, AntlrCharSupport.getCharValueFromCharInGrammarLiteral("\\u{12}"));

		assertEquals(-1, AntlrCharSupport.getCharValueFromCharInGrammarLiteral("\\u{"));
		assertEquals(-1, AntlrCharSupport.getCharValueFromCharInGrammarLiteral("foo"));
	}

	@Test
	public void testParseHexValue() {
		assertEquals(-1, AntlrCharSupport.parseHexValue("foobar", -1, 3));
		assertEquals(-1, AntlrCharSupport.parseHexValue("foobar", 1, -1));
		assertEquals(-1, AntlrCharSupport.parseHexValue("foobar", 1, 3));
		assertEquals(35, AntlrCharSupport.parseHexValue("123456", 1, 3));
	}

	@Test
	public void testCapitalize() {
		assertEquals("Foo", AntlrCharSupport.capitalize("foo"));
	}

	@Test
	public void testGetIntervalSetEscapedString() {
		assertEquals("{}", new IntervalSet().toString(true));
		assertEquals("'\\u0000'", new IntervalSet(0).toString(true));
		assertEquals("{'\\u0001'..'\\u0003'}", new IntervalSet(3, 1, 2).toString(true));
	}
}
