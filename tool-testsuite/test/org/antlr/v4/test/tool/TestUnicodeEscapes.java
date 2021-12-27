/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.codegen.UnicodeEscapes;
import org.antlr.v4.codegen.target.JavaTarget;
import org.antlr.v4.codegen.target.Python2Target;
import org.antlr.v4.codegen.target.Python3Target;
import org.antlr.v4.codegen.target.SwiftTarget;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestUnicodeEscapes {
	@Test
	public void latinJavaEscape() {
		checkUnicodeEscape("\\u0061", 0x0061, JavaTarget.key);
	}

	@Test
	public void latinPythonEscape() {
		checkUnicodeEscape("\\u0061", 0x0061, Python2Target.key);
		checkUnicodeEscape("\\u0061", 0x0061, Python3Target.key);
	}

	@Test
	public void latinSwiftEscape() {
		checkUnicodeEscape("\\u{0061}", 0x0061, SwiftTarget.key);
	}

	@Test
	public void bmpJavaEscape() {
		checkUnicodeEscape("\\uABCD", 0xABCD, JavaTarget.key);
	}

	@Test
	public void bmpPythonEscape() {
		checkUnicodeEscape("\\uABCD", 0xABCD, Python2Target.key);
		checkUnicodeEscape("\\uABCD", 0xABCD, Python3Target.key);
	}

	@Test
	public void bmpSwiftEscape() {
		checkUnicodeEscape("\\u{ABCD}", 0xABCD, SwiftTarget.key);
	}

	@Test
	public void smpJavaEscape() {
		checkUnicodeEscape("\\uD83D\\uDCA9", 0x1F4A9, JavaTarget.key);
	}

	@Test
	public void smpPythonEscape() {
		checkUnicodeEscape("\\U0001F4A9", 0x1F4A9, Python2Target.key);
		checkUnicodeEscape("\\U0001F4A9", 0x1F4A9, Python3Target.key);
	}

	@Test
	public void smpSwiftEscape() {
		checkUnicodeEscape("\\u{1F4A9}", 0x1F4A9, SwiftTarget.key);
	}

	private void checkUnicodeEscape(String expected, int input, String language) {
		assertEquals(expected, UnicodeEscapes.escapeCodePoint(input, language));
	}
}
