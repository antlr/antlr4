package org.antlr.v4.runtime.misc;

import org.antlr.v4.runtime.Lexer;

import java.util.HashMap;
import java.util.Map;

public class CharSupport {
	public final static Map<Character, Character> EscapedCharValue = new HashMap<>();

	public final static Map<Character, String> CharValueEscape = new HashMap<>();

	static {
		EscapedCharValue.put('n', '\n');
		EscapedCharValue.put('r', '\r');
		EscapedCharValue.put('t', '\t');
		EscapedCharValue.put('b', '\b');
		EscapedCharValue.put('f', '\f');
		EscapedCharValue.put('\\', '\\');
		CharValueEscape.put('\n', "\\n");
		CharValueEscape.put('\r', "\\r");
		CharValueEscape.put('\t', "\\t");
		CharValueEscape.put('\b', "\\b");
		CharValueEscape.put('\f', "\\f");
		CharValueEscape.put('\\', "\\\\");
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
			String charValueEscape = CharValueEscape.get((char) c);
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
