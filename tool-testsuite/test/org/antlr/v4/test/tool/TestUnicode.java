/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import java.util.Map;

import org.antlr.v4.codegen.Unicode;
import org.antlr.v4.runtime.misc.IntervalSet;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUnicode {
	@Test
	public void testUnicodeCategoryCodes() {
		Map<String, IntervalSet> unicodeCategoryCodesToCodePointRanges = Unicode.getUnicodeCategoryCodesToCodePointRanges();
		assertTrue(unicodeCategoryCodesToCodePointRanges.get("Lu").contains('X'));
		assertFalse(unicodeCategoryCodesToCodePointRanges.get("Lu").contains('x'));
		assertTrue(unicodeCategoryCodesToCodePointRanges.get("Ll").contains('x'));
		assertFalse(unicodeCategoryCodesToCodePointRanges.get("Ll").contains('X'));
		assertTrue(unicodeCategoryCodesToCodePointRanges.get("L").contains('X'));
		assertTrue(unicodeCategoryCodesToCodePointRanges.get("L").contains('x'));
		assertTrue(unicodeCategoryCodesToCodePointRanges.get("N").contains('0'));
		assertTrue(unicodeCategoryCodesToCodePointRanges.get("Z").contains(' '));
	}

	@Test
	public void testUnicodeCategoryCodesToNames() {
		Map<String, String> unicodeCategoryCodesToNames = Unicode.getUnicodeCategoryCodesToNames();
		assertEquals("Lowercase_Letter", unicodeCategoryCodesToNames.get("Ll"));
		assertEquals("Letter", unicodeCategoryCodesToNames.get("L"));
		assertEquals("Enclosing_Mark", unicodeCategoryCodesToNames.get("Me"));
		assertEquals("Mark", unicodeCategoryCodesToNames.get("M"));
	}

	@Test
	public void testUnicodeBinaryPropertyCodesToCodePointRanges() {
		Map<String, IntervalSet> unicodeBinaryPropertyCodesToCodePointRanges = Unicode.getUnicodeBinaryPropertyCodesToCodePointRanges();
		assertTrue(unicodeBinaryPropertyCodesToCodePointRanges.get("Emoji").contains(0x1F4A9));
		assertFalse(unicodeBinaryPropertyCodesToCodePointRanges.get("Emoji").contains('X'));
		assertTrue(unicodeBinaryPropertyCodesToCodePointRanges.get("alnum").contains('9'));
		assertFalse(unicodeBinaryPropertyCodesToCodePointRanges.get("alnum").contains(0x1F4A9));
		assertTrue(unicodeBinaryPropertyCodesToCodePointRanges.get("Dash").contains('-'));
		assertTrue(unicodeBinaryPropertyCodesToCodePointRanges.get("Hex").contains('D'));
		assertFalse(unicodeBinaryPropertyCodesToCodePointRanges.get("Hex").contains('Q'));
	}

	@Test
	public void testUnicodeBinaryPropertyCodesToNames() {
		Map<String, String> unicodeBinaryPropertyCodesToNames = Unicode.getUnicodeBinaryPropertyCodesToNames();
		assertEquals("Ideographic", unicodeBinaryPropertyCodesToNames.get("Ideo"));
		assertEquals("Soft_Dotted", unicodeBinaryPropertyCodesToNames.get("SD"));
		assertEquals("Noncharacter_Code_Point", unicodeBinaryPropertyCodesToNames.get("NChar"));
	}

	@Test
	public void testUnicodeScriptCodesToCodePointRanges() {
		Map<String, IntervalSet> unicodeScriptCodesToCodePointRanges = Unicode.getUnicodeScriptCodesToCodePointRanges();
		assertTrue(unicodeScriptCodesToCodePointRanges.get("Zyyy").contains('0'));
		assertTrue(unicodeScriptCodesToCodePointRanges.get("Latn").contains('X'));
		assertTrue(unicodeScriptCodesToCodePointRanges.get("Hani").contains(0x4E04));
		assertTrue(unicodeScriptCodesToCodePointRanges.get("Cyrl").contains(0x0404));
	}

	@Test
	public void testUnicodeScriptCodesToNames() {
		Map<String, String> unicodeScriptCodesToNames = Unicode.getUnicodeScriptCodesToNames();
		assertEquals("Common", unicodeScriptCodesToNames.get("Zyyy"));
		assertEquals("Latin", unicodeScriptCodesToNames.get("Latn"));
		assertEquals("Han", unicodeScriptCodesToNames.get("Hani"));
		assertEquals("Cyrillic", unicodeScriptCodesToNames.get("Cyrl"));
	}
}
