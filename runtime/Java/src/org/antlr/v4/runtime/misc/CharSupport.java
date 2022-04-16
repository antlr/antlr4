package org.antlr.v4.runtime.misc;

import org.antlr.v4.runtime.Lexer;

public class CharSupport {
	public final static int[] EscapedCharValue = new int[255];

	public final static String[] CharValueEscape = new String[255];

	static {
		EscapedCharValue['n'] = '\n';
		EscapedCharValue['r'] = '\r';
		EscapedCharValue['t'] = '\t';
		EscapedCharValue['b'] = '\b';
		EscapedCharValue['f'] = '\f';
		EscapedCharValue['\\'] = '\\';
		CharValueEscape['\n'] = "\\n";
		CharValueEscape['\r'] = "\\r";
		CharValueEscape['\t'] = "\\t";
		CharValueEscape['\b'] = "\\b";
		CharValueEscape['\f'] = "\\f";
		CharValueEscape['\\'] = "\\\\";
	}

	public static String getPrintable(int c) {
		return getPrintable(c, true);
	}

	/** Return a string representing the escaped char for code c.  E.g., If c
	 *  has value 0x100, you will get "\\u0100".  ASCII gets the usual
	 *  char (non-hex) representation.  Non-ASCII characters are spit out
	 *  as \\uXXXX or \\u{XXXXXX} escapes.
	 */
	public static String getPrintable(int c, boolean appendQuotes) {
		String result;
		if ( c < Lexer.MIN_CHAR_VALUE ) {
			result = "<INVALID>";
		}
		else {
			String charValueEscape = c < CharValueEscape.length ? CharValueEscape[c] : null;
			if (charValueEscape != null) {
				result = charValueEscape;
			}
			else if (Character.UnicodeBlock.of((char) c) == Character.UnicodeBlock.BASIC_LATIN &&
					!Character.isISOControl((char) c)) {
				if (c == '\\') {
					result = "\\\\";
				}
				else if (c == '\'') {
					result = "\\'";
				}
				else {
					result = Character.toString((char) c);
				}
			}
			else if (c <= 0xFFFF) {
				result = String.format("\\u%04X", c);
			} else {
				result = String.format("\\u{%06X}", c);
			}
		}
		if (appendQuotes) {
			return '\'' + result + '\'';
		}
		return result;
	}
}
