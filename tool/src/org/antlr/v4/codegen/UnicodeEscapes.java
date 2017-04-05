/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;

/**
 * Utility class to escape Unicode code points using various
 * languages' syntaxes.
 */
public abstract class UnicodeEscapes {
	static public void appendJavaStyleEscapedCodePoint(int codePoint, StringBuilder sb) {
		if (Character.isSupplementaryCodePoint(codePoint)) {
			// char is not an 'integral' type, so we have to explicitly convert
			// to int before passing to the %X formatter or else it throws.
			sb.append(String.format("\\u%04X", (int)Character.highSurrogate(codePoint)));
			sb.append(String.format("\\u%04X", (int)Character.lowSurrogate(codePoint)));
		}
		else {
			sb.append(String.format("\\u%04X", codePoint));
		}
	}

	static public void appendPythonStyleEscapedCodePoint(int codePoint, StringBuilder sb) {
		if (Character.isSupplementaryCodePoint(codePoint)) {
			sb.append(String.format("\\U%08X", codePoint));
		}
		else {
			sb.append(String.format("\\u%04X", codePoint));
		}
	}

	static public void appendSwiftStyleEscapedCodePoint(int codePoint, StringBuilder sb) {
		sb.append(String.format("\\u{%04X}", codePoint));
	}
}
