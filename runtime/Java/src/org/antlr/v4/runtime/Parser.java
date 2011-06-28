/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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


import org.antlr.v4.runtime.tree.TreeAdaptor;

/** A parser for TokenStreams.  "parser grammars" result in a subclass
 *  of this.
 */
public class Parser extends BaseRecognizer {

	public TreeAdaptor _adaptor;

	public Parser(TokenStream input) {
		super(input);
    }

	public void reset() {
		super.reset(); // reset all recognizer state variables
		if ( input!=null ) {
			input.seek(0); // rewind the input
		}
	}

	protected Object getCurrentInputSymbol() {
		return input.LT(1);
	}

	protected Object getMissingSymbol(RecognitionException e,
									  int expectedTokenType)
	{
		String tokenText = null;
		if ( expectedTokenType== Token.EOF ) tokenText = "<missing EOF>";
		else tokenText = "<missing "+getTokenNames()[expectedTokenType]+">";
		CommonToken t = new CommonToken(expectedTokenType, tokenText);
		Token current = input.LT(1);
		if ( current.getType() == Token.EOF ) {
			current = input.LT(-1);
		}
		t.line = current.getLine();
		t.charPositionInLine = current.getCharPositionInLine();
		t.channel = Token.DEFAULT_CHANNEL;
		t.input = current.getInputStream();
		return t;
	}

	/** Set the token stream and reset the parser */
	public void setTokenStream(TokenStream input) {
		this.input = null;
		reset();
		this.input = input;
	}

    public TokenStream getTokenStream() {
		return (TokenStream)input;
	}

	public String getSourceName() {
		return input.getSourceName();
	}

	public void traceIn(String ruleName, int ruleIndex)  {
		super.traceIn(ruleName, ruleIndex, input.LT(1));
	}

	public void traceOut(String ruleName, int ruleIndex)  {
		super.traceOut(ruleName, ruleIndex, input.LT(1));
	}
}
