/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
		switch ( literal.length() ) {
			case 3 :
				// 'x'
				return literal.charAt(1); // no escape char
			case 4 :
				if ( literal.charAt(1)!='\\' ) return -1;
				// '\x'  (antlr lexer will catch invalid char)
				if ( Character.isDigit(literal.charAt(2)) ) {
//					ErrorManager.error(ErrorManager.MSG_SYNTAX_ERROR,
//									   "invalid char literal: "+literal);
					return -1;
				}
				int escChar = literal.charAt(2);
				int charVal = ANTLRLiteralEscapedCharValue[escChar];
				if ( charVal==0 ) {
					// Unnecessary escapes like '\{' should just yield {
					return escChar;
				}
				return charVal;
			case 8 :
				// '\u1234'
				String unicodeChars = literal.substring(3,literal.length()-1);
				return Integer.parseInt(unicodeChars, 16);
			default :
//				ErrorManager.error(ErrorManager.MSG_SYNTAX_ERROR,
//								   "invalid char literal: "+literal);
				return -1;
		}
	}

	public static String getStringFromGrammarStringLiteral(String literal) {
		StringBuilder buf = new StringBuilder();
		int n = literal.length();
		int i = 1; // skip first quote
		while ( i < (n-1) ) { // scan all but last quote
			switch ( literal.charAt(i) ) {
				case '\\' :
					i++;
					if ( literal.charAt(i)=='u' ) { // '\u1234'
						i++;
						String unicodeChars = literal.substring(i,i+4);
						int h = Integer.parseInt(unicodeChars, 16);
						buf.append((char)h);
						i += 4;
					}
					else {
						char escChar = literal.charAt(i);
						int charVal = ANTLRLiteralEscapedCharValue[escChar];
						if ( charVal==0 ) buf.append(escChar); // Unnecessary escapes like '\{' should just yield {
						else buf.append((char)charVal);
						i++;
					}
					break;
				default :
					buf.append(literal.charAt(i));
					i++;
					break;
			}
		}
		return buf.toString();
	}

	public static final String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}
