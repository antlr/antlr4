/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.junit.Assert;
import org.junit.Test;

public class TestCharSupport {
	@Test
	public void testGetANTLRCharLiteralForChar() {
		Assert.assertEquals("'<INVALID>'",
			CharSupport.getANTLRCharLiteralForChar(-1));
		Assert.assertEquals("'\\n'",
			CharSupport.getANTLRCharLiteralForChar('\n'));
		Assert.assertEquals("'\\\\'",
			CharSupport.getANTLRCharLiteralForChar('\\'));
		Assert.assertEquals("'\\''",
			CharSupport.getANTLRCharLiteralForChar('\''));
		Assert.assertEquals("'b'",
			CharSupport.getANTLRCharLiteralForChar('b'));
		Assert.assertEquals("'\\uFFFF'",
			CharSupport.getANTLRCharLiteralForChar(0xFFFF));
		Assert.assertEquals("'\\u{10FFFF}'",
			CharSupport.getANTLRCharLiteralForChar(0x10FFFF));
	}

	@Test
	public void testGetCharValueFromGrammarCharLiteral() {
		Assert.assertEquals(-1,
			CharSupport.getCharValueFromGrammarCharLiteral(null));
		Assert.assertEquals(-1,
			CharSupport.getCharValueFromGrammarCharLiteral(""));
		Assert.assertEquals(-1,
			CharSupport.getCharValueFromGrammarCharLiteral("b"));
		Assert.assertEquals(111,
			CharSupport.getCharValueFromGrammarCharLiteral("foo"));
	}

	@Test
	public void testGetStringFromGrammarStringLiteral() {
		Assert.assertNull(CharSupport
			.getStringFromGrammarStringLiteral("foo\\u{bbb"));
		Assert.assertNull(CharSupport
			.getStringFromGrammarStringLiteral("foo\\u{[]bb"));
		Assert.assertNull(CharSupport
			.getStringFromGrammarStringLiteral("foo\\u[]bb"));
		Assert.assertNull(CharSupport
			.getStringFromGrammarStringLiteral("foo\\ubb"));

		Assert.assertEquals("oo»b", CharSupport
			.getStringFromGrammarStringLiteral("foo\\u{bb}bb"));
	}

	@Test
	public void testGetCharValueFromCharInGrammarLiteral() {
		Assert.assertEquals(102,
			CharSupport.getCharValueFromCharInGrammarLiteral("f"));

		Assert.assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("\' "));
		Assert.assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\ "));
		Assert.assertEquals(39,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\\'"));
		Assert.assertEquals(10,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\n"));

		Assert.assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("foobar"));
		Assert.assertEquals(4660,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\u1234"));
		Assert.assertEquals(18,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\u{12}"));

		Assert.assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("\\u{"));
		Assert.assertEquals(-1,
			CharSupport.getCharValueFromCharInGrammarLiteral("foo"));
	}

	@Test
	public void testParseHexValue() {
		Assert.assertEquals(-1, CharSupport.parseHexValue("foobar", -1, 3));
		Assert.assertEquals(-1, CharSupport.parseHexValue("foobar", 1, -1));
		Assert.assertEquals(-1, CharSupport.parseHexValue("foobar", 1, 3));
		Assert.assertEquals(35, CharSupport.parseHexValue("123456", 1, 3));
	}

	@Test
	public void testCapitalize() {
		Assert.assertEquals("Foo", CharSupport.capitalize("foo"));
	}

	@Test
	public void testGetIntervalSetEscapedString() {
		Assert.assertEquals("",
			CharSupport.getIntervalSetEscapedString(new IntervalSet()));
		Assert.assertEquals("'\\u0000'",
			CharSupport.getIntervalSetEscapedString(new IntervalSet(0)));
		Assert.assertEquals("'\\u0001'..'\\u0003'",
			CharSupport.getIntervalSetEscapedString(new IntervalSet(3, 1, 2)));
	}

	@Test
	public void testGetRangeEscapedString() {
		Assert.assertEquals("'\\u0002'..'\\u0004'",
			CharSupport.getRangeEscapedString(2, 4));
		Assert.assertEquals("'\\u0002'",
			CharSupport.getRangeEscapedString(2, 2));
	}
}
