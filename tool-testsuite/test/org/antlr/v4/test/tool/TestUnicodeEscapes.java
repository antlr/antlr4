/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.codegen.Language;
import org.antlr.v4.codegen.UnicodeEscapes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestUnicodeEscapes {
	@Test
	public void latinJavaEscape() {
		checkUnicodeEscape("\\u0061", 0x0061, Language.Java);
	}

	@Test
	public void latinPythonEscape() {
		checkUnicodeEscape("\\u0061", 0x0061, Language.Python2);
		checkUnicodeEscape("\\u0061", 0x0061, Language.Python3);
	}

	@Test
	public void latinSwiftEscape() {
		checkUnicodeEscape("\\u{0061}", 0x0061, Language.Swift);
	}

	@Test
	public void bmpJavaEscape() {
		checkUnicodeEscape("\\uABCD", 0xABCD, Language.Java);
	}

	@Test
	public void bmpPythonEscape() {
		checkUnicodeEscape("\\uABCD", 0xABCD, Language.Python2);
		checkUnicodeEscape("\\uABCD", 0xABCD, Language.Python3);
	}

	@Test
	public void bmpSwiftEscape() {
		checkUnicodeEscape("\\u{ABCD}", 0xABCD, Language.Swift);
	}

	@Test
	public void smpJavaEscape() {
		checkUnicodeEscape("\\uD83D\\uDCA9", 0x1F4A9, Language.Java);
	}

	@Test
	public void smpPythonEscape() {
		checkUnicodeEscape("\\U0001F4A9", 0x1F4A9, Language.Python2);
		checkUnicodeEscape("\\U0001F4A9", 0x1F4A9, Language.Python3);
	}

	@Test
	public void smpSwiftEscape() {
		checkUnicodeEscape("\\u{1F4A9}", 0x1F4A9, Language.Swift);
	}

	private void checkUnicodeEscape(String expected, int input, Language language) {
		assertEquals(expected, UnicodeEscapes.escapeCodePoint(input, language));
	}
}
