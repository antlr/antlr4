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

/** A source of tokens must provide a sequence of tokens via nextToken()
 *  and also must reveal it's source of characters; CommonToken's text is
 *  computed from a CharStream; it only store indices into the char stream.
 *
 *  Errors from the lexer are never passed to the parser.  Either you want
 *  to keep going or you do not upon token recognition error.  If you do not
 *  want to continue lexing then you do not want to continue parsing.  Just
 *  throw an exception not under RecognitionException and Java will naturally
 *  toss you all the way out of the recognizers.  If you want to continue
 *  lexing then you should not throw an exception to the parser--it has already
 *  requested a token.  Keep lexing until you get a valid one.  Just report
 *  errors and keep going, looking for a valid token.
 */
public interface TokenSource {
	/** Return a Token object from your input stream (usually a CharStream).
	 *  Do not fail/return upon lexing error; keep chewing on the characters
	 *  until you get a good one; errors are not passed through to the parser.
	 */
	public Token nextToken();

	public int getLine();

	public int getCharPositionInLine();

	/** From what character stream was this token created?  You don't have to
	 *  implement but it's nice to know where a Token comes from if you have
	 *  include files etc... on the input.
	 */
	public CharStream getInputStream();

	/** Where are you getting tokens from? normally the implication will simply
	 *  ask lexers input stream.
	 */
	public String getSourceName();

	/** Optional method that lets users set factory in lexer or other source */
	public void setTokenFactory(TokenFactory<?> factory);
}
