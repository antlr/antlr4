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

package org.antlr.v4.runtime;

// TODO: del or rename LexerNoViableAlt?
public class LexerRecognitionExeption extends RuntimeException {
	/** Who threw the exception? */
	public Lexer lexer;

	/** What is index of token/char were we looking at when the error occurred? */
	public int index;

	/** The current char when an error occurred. For lexers. */
	public int c;

	/** Track the line at which the error occurred in case this is
	 *  generated from a lexer.  We need to track this since the
	 *  unexpected char doesn't carry the line info.
	 */
	public int line;

	public int charPositionInLine;

	/** Used for remote debugger deserialization */
	public LexerRecognitionExeption() {
	}

	public LexerRecognitionExeption(Lexer lexer, CharStream input) {
		this.lexer = lexer;
		this.index = input.index();
		this.c = input.LA(1);
		if ( lexer!=null ) {
			this.line = lexer.getLine();
			this.charPositionInLine = lexer.getCharPositionInLine();
		}
	}

}
