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
package org.antlr.v4.runtime.tree;

public class TreePatternLexer {
	public static final int EOF = -1;
	public static final int BEGIN = 1;
	public static final int END = 2;
	public static final int ID = 3;
	public static final int ARG = 4;
	public static final int PERCENT = 5;
	public static final int COLON = 6;
	public static final int DOT = 7;

	/** The tree pattern to lex like "(A B C)" */
	protected String pattern;

	/** Index into input string */
	protected int p = -1;

	/** Current char */
	protected int c;

	/** How long is the pattern in char? */
	protected int n;

	/** Set when token type is ID or ARG (name mimics Java's StreamTokenizer) */
	public StringBuffer sval = new StringBuffer();

	public boolean error = false;

	public TreePatternLexer(String pattern) {
		this.pattern = pattern;
		this.n = pattern.length();
		consume();
	}

	public int nextToken() {
		sval.setLength(0); // reset, but reuse buffer
		while ( c != EOF ) {
			if ( c==' ' || c=='\n' || c=='\r' || c=='\t' ) {
				consume();
				continue;
			}
			if ( (c>='a' && c<='z') || (c>='A' && c<='Z') || c=='_' ) {
				sval.append((char)c);
				consume();
				while ( (c>='a' && c<='z') || (c>='A' && c<='Z') ||
						(c>='0' && c<='9') || c=='_' )
				{
					sval.append((char)c);
					consume();
				}
				return ID;
			}
			if ( c=='(' ) {
				consume();
				return BEGIN;
			}
			if ( c==')' ) {
				consume();
				return END;
			}
			if ( c=='%' ) {
				consume();
				return PERCENT;
			}
			if ( c==':' ) {
				consume();
				return COLON;
			}
			if ( c=='.' ) {
				consume();
				return DOT;
			}
			if ( c=='[' ) { // grab [x] as a string, returning x
				consume();
				while ( c!=']' ) {
					if ( c=='\\' ) {
						consume();
						if ( c!=']' ) {
							sval.append('\\');
						}
						sval.append((char)c);
					}
					else {
						sval.append((char)c);
					}
					consume();
				}
				consume();
				return ARG;
			}
			consume();
			error = true;
			return EOF;
		}
		return EOF;
	}

	protected void consume() {
		p++;
		if ( p>=n ) {
			c = EOF;
		}
		else {
			c = pattern.charAt(p);
		}
	}
}
