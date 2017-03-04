/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import java.util.Map;

import org.antlr.v4.unicode.UnicodeData;
import org.antlr.v4.runtime.misc.IntervalSet;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUnicodeData {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testUnicodeGeneralCategoriesLatin() {
		assertTrue(UnicodeData.getPropertyCodePoints("Lu").contains('X'));
		assertFalse(UnicodeData.getPropertyCodePoints("Lu").contains('x'));
		assertTrue(UnicodeData.getPropertyCodePoints("Ll").contains('x'));
		assertFalse(UnicodeData.getPropertyCodePoints("Ll").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains('x'));
		assertTrue(UnicodeData.getPropertyCodePoints("N").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Z").contains(' '));
	}

	@Test
	public void testUnicodeGeneralCategoriesBMP() {
		assertTrue(UnicodeData.getPropertyCodePoints("Lu").contains('\u1E3A'));
		assertFalse(UnicodeData.getPropertyCodePoints("Lu").contains('\u1E3B'));
		assertTrue(UnicodeData.getPropertyCodePoints("Ll").contains('\u1E3B'));
		assertFalse(UnicodeData.getPropertyCodePoints("Ll").contains('\u1E3A'));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains('\u1E3A'));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains('\u1E3B'));
		assertTrue(UnicodeData.getPropertyCodePoints("N").contains('\u1BB0'));
		assertFalse(UnicodeData.getPropertyCodePoints("N").contains('\u1E3A'));
		assertTrue(UnicodeData.getPropertyCodePoints("Z").contains('\u2028'));
		assertFalse(UnicodeData.getPropertyCodePoints("Z").contains('\u1E3A'));
	}

	@Test
	public void testUnicodeGeneralCategoriesSMP() {
		assertTrue(UnicodeData.getPropertyCodePoints("Lu").contains(0x1D5D4));
		assertFalse(UnicodeData.getPropertyCodePoints("Lu").contains(0x1D770));
		assertTrue(UnicodeData.getPropertyCodePoints("Ll").contains(0x1D770));
		assertFalse(UnicodeData.getPropertyCodePoints("Ll").contains(0x1D5D4));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains(0x1D5D4));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains(0x1D770));
		assertTrue(UnicodeData.getPropertyCodePoints("N").contains(0x11C50));
		assertFalse(UnicodeData.getPropertyCodePoints("N").contains(0x1D5D4));
	}

	@Test
	public void testUnicodeCategoryAliases() {
		assertTrue(UnicodeData.getPropertyCodePoints("Lowercase_Letter").contains('x'));
		assertFalse(UnicodeData.getPropertyCodePoints("Lowercase_Letter").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Letter").contains('x'));
		assertFalse(UnicodeData.getPropertyCodePoints("Letter").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Enclosing_Mark").contains(0x20E2));
		assertFalse(UnicodeData.getPropertyCodePoints("Enclosing_Mark").contains('x'));
	}

	@Test
	public void testUnicodeBinaryProperties() {
		assertTrue(UnicodeData.getPropertyCodePoints("Emoji").contains(0x1F4A9));
		assertFalse(UnicodeData.getPropertyCodePoints("Emoji").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("alnum").contains('9'));
		assertFalse(UnicodeData.getPropertyCodePoints("alnum").contains(0x1F4A9));
		assertTrue(UnicodeData.getPropertyCodePoints("Dash").contains('-'));
		assertTrue(UnicodeData.getPropertyCodePoints("Hex").contains('D'));
		assertFalse(UnicodeData.getPropertyCodePoints("Hex").contains('Q'));
	}

	@Test
	public void testUnicodeBinaryPropertyAliases() {
		assertTrue(UnicodeData.getPropertyCodePoints("Ideo").contains('\u611B'));
		assertFalse(UnicodeData.getPropertyCodePoints("Ideo").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Soft_Dotted").contains('\u0456'));
		assertFalse(UnicodeData.getPropertyCodePoints("Soft_Dotted").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Noncharacter_Code_Point").contains('\uFFFF'));
		assertFalse(UnicodeData.getPropertyCodePoints("Noncharacter_Code_Point").contains('X'));
	}

	@Test
	public void testUnicodeScripts() {
		assertTrue(UnicodeData.getPropertyCodePoints("Zyyy").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Latn").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Hani").contains(0x4E04));
		assertTrue(UnicodeData.getPropertyCodePoints("Cyrl").contains(0x0404));
	}

	@Test
	public void testUnicodeScriptAliases() {
		assertTrue(UnicodeData.getPropertyCodePoints("Common").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Latin").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Han").contains(0x4E04));
		assertTrue(UnicodeData.getPropertyCodePoints("Cyrillic").contains(0x0404));
	}

	@Test
	public void testUnicodeBlocks() {
		assertTrue(UnicodeData.getPropertyCodePoints("InASCII").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("InCJK").contains(0x4E04));
		assertTrue(UnicodeData.getPropertyCodePoints("InCyrillic").contains(0x0404));
		assertTrue(UnicodeData.getPropertyCodePoints("InMisc_Pictographs").contains(0x1F4A9));
	}

	@Test
	public void testUnicodeBlockAliases() {
		assertTrue(UnicodeData.getPropertyCodePoints("InBasic_Latin").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("InMiscellaneous_Mathematical_Symbols_B").contains(0x29BE));
	}

	@Test
	public void testPropertyCaseInsensitivity() {
		assertTrue(UnicodeData.getPropertyCodePoints("l").contains('x'));
		assertFalse(UnicodeData.getPropertyCodePoints("l").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("common").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Alnum").contains('0'));
	}

	@Test
	public void testPropertyDashSameAsUnderscore() {
		assertTrue(UnicodeData.getPropertyCodePoints("InLatin-1").contains('\u00F0'));
	}

	@Test
	public void modifyingUnicodeDataShouldThrow() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("can't alter readonly IntervalSet");
		UnicodeData.getPropertyCodePoints("L").add(0x12345);
	}
}
