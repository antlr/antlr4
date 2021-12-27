/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.codegen.target.*;

/**
 * Utility class to escape Unicode code points using various
 * languages' syntax.
 */
public class UnicodeEscapes {
	public static String escapeCodePoint(int codePoint, String language) {
		StringBuilder result = new StringBuilder();
		appendEscapedCodePoint(result, codePoint, language);
		return result.toString();
	}

	public static void appendEscapedCodePoint(StringBuilder sb, int codePoint, String language) {
		switch (language) {
			case JavaTarget.key:
			case JavaScriptTarget.key:
			case DartTarget.key:
				if (Character.isSupplementaryCodePoint(codePoint)) {
					// char is not an 'integral' type, so we have to explicitly convert
					// to int before passing to the %X formatter or else it throws.
					sb.append(String.format("\\u%04X", (int)Character.highSurrogate(codePoint)));
					sb.append(String.format("\\u%04X", (int)Character.lowSurrogate(codePoint)));
				}
				else {
					sb.append(String.format("\\u%04X", codePoint));
				}
				break;
			case CSharpTarget.key:
			case Python2Target.key:
			case Python3Target.key:
			case CppTarget.key:
			case GoTarget.key:
			case PHPTarget.key:
				String format = Character.isSupplementaryCodePoint(codePoint) ? "\\U%08X" : "\\u%04X";
				sb.append(String.format(format, codePoint));
				break;
			case SwiftTarget.key:
				sb.append(String.format("\\u{%04X}", codePoint));
				break;
		}
	}
}
