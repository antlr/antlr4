/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.misc;

import org.antlr.v4.runtime.misc.CharSupport;

/** */
public class AntlrCharSupport {
	/** Given a literal like (the 3 char sequence with single quotes) 'a',
	 *  return the int value of 'a'. Convert escape sequences here also.
	 *  Return -1 if not single char.
	 */
	public static int getCharValueFromGrammarCharLiteral(String literal) {
		if ( literal==null || literal.length()<3 ) return -1;
		return getCharValueFromCharInGrammarLiteral(literal.substring(1,literal.length()-1));
	}

	public static String getStringFromGrammarStringLiteral(String literal) {
		StringBuilder buf = new StringBuilder();
		int i = 1; // skip first quote
		int n = literal.length()-1; // skip last quote
		while ( i < n ) { // scan all but last quote
			int end = i+1;
			if ( literal.charAt(i) == '\\' ) {
				end = i+2;
				if ( i+1 < n && literal.charAt(i+1) == 'u' ) {
					if ( i+2 < n && literal.charAt(i+2) == '{' ) { // extended escape sequence
						end = i + 3;
						while (true) {
							if ( end + 1 > n ) return null; // invalid escape sequence.
							char charAt = literal.charAt(end++);
							if (charAt == '}') {
								break;
							}
							if (!Character.isDigit(charAt) && !(charAt >= 'a' && charAt <= 'f') && !(charAt >= 'A' && charAt <= 'F')) {
								return null; // invalid escape sequence.
							}
						}
					}
					else {
						for (end = i + 2; end < i + 6; end++) {
							if ( end>n ) return null; // invalid escape sequence.
							char charAt = literal.charAt(end);
							if (!Character.isDigit(charAt) && !(charAt >= 'a' && charAt <= 'f') && !(charAt >= 'A' && charAt <= 'F')) {
								return null; // invalid escape sequence.
							}
						}
					}
				}
			}
			if ( end>n ) return null; // invalid escape sequence.
			String esc = literal.substring(i, end);
			int c = getCharValueFromCharInGrammarLiteral(esc);
			if ( c==-1 ) {
				return null; // invalid escape sequence.
			}
			else buf.appendCodePoint(c);
			i = end;
		}
		return buf.toString();
	}

	/** Given char x or \\t or \\u1234 return the char value;
	 *  Unnecessary escapes like '\{' yield -1.
	 */
	public static int getCharValueFromCharInGrammarLiteral(String cstr) {
		switch ( cstr.length() ) {
			case 1:
				// 'x'
				return cstr.charAt(0); // no escape char
			case 2:
				if ( cstr.charAt(0)!='\\' ) return -1;
				// '\x'  (antlr lexer will catch invalid char)
				char escChar = cstr.charAt(1);
				if (escChar == '\'') return escChar; // escape quote only in string literals.
				Character escapedCharValue = CharSupport.EscapedCharValue.get(escChar);
				if (escapedCharValue == null) return -1;
				return escapedCharValue;
			case 6:
				// '\\u1234' or '\\u{12}'
				if ( !cstr.startsWith("\\u") ) return -1;
				int startOff;
				int endOff;
				if ( cstr.charAt(2) == '{' ) {
					startOff = 3;
					endOff = cstr.indexOf('}');
				}
				else {
					startOff = 2;
					endOff = cstr.length();
				}
				return parseHexValue(cstr, startOff, endOff);
			default:
				if ( cstr.startsWith("\\u{") ) {
					return parseHexValue(cstr, 3, cstr.indexOf('}'));
				}
				return -1;
		}
	}

	public static int parseHexValue(String cstr, int startOff, int endOff) {
		if (startOff < 0 || endOff < 0) {
			return -1;
		}
		String unicodeChars = cstr.substring(startOff, endOff);
		int result = -1;
		try {
			result = Integer.parseInt(unicodeChars, 16);
		}
		catch (NumberFormatException e) {
		}
		return result;
	}

	public static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}
