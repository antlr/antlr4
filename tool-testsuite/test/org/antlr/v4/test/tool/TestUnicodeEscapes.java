/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.codegen.UnicodeEscapes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestUnicodeEscapes {
	@Test
	public void latinJavaEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendJavaStyleEscapedCodePoint(0x0061, sb);
		assertEquals("\\u0061", sb.toString());
	}

	@Test
	public void latinPythonEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendPythonStyleEscapedCodePoint(0x0061, sb);
		assertEquals("\\u0061", sb.toString());
	}

	@Test
	public void latinSwiftEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendSwiftStyleEscapedCodePoint(0x0061, sb);
		assertEquals("\\u{0061}", sb.toString());
	}

	@Test
	public void bmpJavaEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendJavaStyleEscapedCodePoint(0xABCD, sb);
		assertEquals("\\uABCD", sb.toString());
	}

	@Test
	public void bmpPythonEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendPythonStyleEscapedCodePoint(0xABCD, sb);
		assertEquals("\\uABCD", sb.toString());
	}

	@Test
	public void bmpSwiftEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendSwiftStyleEscapedCodePoint(0xABCD, sb);
		assertEquals("\\u{ABCD}", sb.toString());
	}

	@Test
	public void smpJavaEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendJavaStyleEscapedCodePoint(0x1F4A9, sb);
		assertEquals("\\uD83D\\uDCA9", sb.toString());
	}

	@Test
	public void smpPythonEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendPythonStyleEscapedCodePoint(0x1F4A9, sb);
		assertEquals("\\U0001F4A9", sb.toString());
	}

	@Test
	public void smpSwiftEscape() {
		StringBuilder sb = new StringBuilder();
		UnicodeEscapes.appendSwiftStyleEscapedCodePoint(0x1F4A9, sb);
		assertEquals("\\u{1F4A9}", sb.toString());
	}
}
