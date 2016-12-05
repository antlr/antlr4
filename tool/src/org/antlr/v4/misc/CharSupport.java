/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.misc;

import org.antlr.v4.runtime.Lexer;

/** */
public class CharSupport {
	/** When converting ANTLR char and string literals, here is the
	 *  value set of escape chars.
	 */
	public static int ANTLRLiteralEscapedCharValue[] = new int[255];

	/** Given a char, we need to be able to show as an ANTLR literal.
	 */
	public static String ANTLRLiteralCharValueEscape[] = new String[255];

	static {
		ANTLRLiteralEscapedCharValue['n'] = '\n';
		ANTLRLiteralEscapedCharValue['r'] = '\r';
		ANTLRLiteralEscapedCharValue['t'] = '\t';
		ANTLRLiteralEscapedCharValue['b'] = '\b';
		ANTLRLiteralEscapedCharValue['f'] = '\f';
		ANTLRLiteralEscapedCharValue['\\'] = '\\';
		ANTLRLiteralEscapedCharValue['\''] = '\'';
		ANTLRLiteralEscapedCharValue['"'] = '"';
		ANTLRLiteralCharValueEscape['\n'] = "\\n";
		ANTLRLiteralCharValueEscape['\r'] = "\\r";
		ANTLRLiteralCharValueEscape['\t'] = "\\t";
		ANTLRLiteralCharValueEscape['\b'] = "\\b";
		ANTLRLiteralCharValueEscape['\f'] = "\\f";
		ANTLRLiteralCharValueEscape['\\'] = "\\\\";
		ANTLRLiteralCharValueEscape['\''] = "\\'";
	}

	/** Return a string representing the escaped char for code c.  E.g., If c
	 *  has value 0x100, you will get "\u0100".  ASCII gets the usual
	 *  char (non-hex) representation.  Control characters are spit out
	 *  as unicode.  While this is specially set up for returning Java strings,
	 *  it can be used by any language target that has the same syntax. :)
	 */
	public static String getANTLRCharLiteralForChar(int c) {
		if ( c< Lexer.MIN_CHAR_VALUE ) {
			return "'<INVALID>'";
		}
		if ( c<ANTLRLiteralCharValueEscape.length && ANTLRLiteralCharValueEscape[c]!=null ) {
			return '\''+ANTLRLiteralCharValueEscape[c]+'\'';
		}
		if ( Character.UnicodeBlock.of((char)c)==Character.UnicodeBlock.BASIC_LATIN &&
			 !Character.isISOControl((char)c) ) {
			if ( c=='\\' ) {
				return "'\\\\'";
			}
			if ( c=='\'') {
				return "'\\''";
			}
			return '\''+Character.toString((char)c)+'\'';
		}
		// turn on the bit above max "\uFFFF" value so that we pad with zeros
		// then only take last 4 digits
		String hex = Integer.toHexString(c|0x10000).toUpperCase().substring(1,5);
		String unicodeStr = "'\\u"+hex+"'";
		return unicodeStr;
	}

	/** Given a literal like (the 3 char sequence with single quotes) 'a',
	 *  return the int value of 'a'. Convert escape sequences here also.
	 *  Return -1 if not single char.
	 */
	public static int getCharValueFromGrammarCharLiteral(String literal) {
		if ( literal==null || literal.length()<3 ) return -1;
		return getCharValueFromCharInGrammarLiteral(literal.substring(1,literal.length()-1));
	}

	/** Given char x or \t or \u1234 return the char value;
	 *  Unnecessary escapes like '\{' yield -1.
	 */
	public static int getCharValueFromCharInGrammarLiteral(String cstr) {
		switch ( cstr.length() ) {
			case 1 :
				// 'x'
				return cstr.charAt(0); // no escape char
			case 2 :
				if ( cstr.charAt(0)!='\\' ) return -1;
				// '\x'  (antlr lexer will catch invalid char)
				if ( Character.isDigit(cstr.charAt(1)) ) return -1;
				int escChar = cstr.charAt(1);
				int charVal = ANTLRLiteralEscapedCharValue[escChar];
				if ( charVal==0 ) return -1;
				return charVal;
			case 6 :
				// '\u1234'
				if ( !cstr.startsWith("\\u") ) return -1;
				String unicodeChars = cstr.substring(2, cstr.length());
				return Integer.parseInt(unicodeChars, 16);
			default :
				return -1;
		}
	}

	public static String getStringFromGrammarStringLiteral(String literal) {
		StringBuilder buf = new StringBuilder();
		int i = 1; // skip first quote
		int n = literal.length()-1; // skip last quote
		while ( i < n ) { // scan all but last quote
			int end = i+1;
			if ( literal.charAt(i) == '\\' ) {
				end = i+2;
				if ( (i+1)>=n ) break; // ignore spurious \ on end
				if ( literal.charAt(i+1) == 'u' ) end = i+6;
			}
			if ( end>n ) break;
			String esc = literal.substring(i, end);
			int c = getCharValueFromCharInGrammarLiteral(esc);
			if ( c==-1 ) { buf.append(esc); }
			else buf.append((char)c);
			i = end;
		}
		return buf.toString();
	}

	public static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}
