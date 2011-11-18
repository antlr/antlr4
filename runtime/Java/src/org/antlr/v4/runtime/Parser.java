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


/** A parser for TokenStreams.  "parser grammars" result in a subclass
 *  of this.
 */
public class Parser extends BaseRecognizer<Token> {
	protected TokenStream _input;

	public Parser(TokenStream input) {
		super(input);
    }

	@Override
	public void reset() {
		super.reset(); // reset all recognizer state variables
		if ( _input !=null ) {
			_input.seek(0); // rewind the input
		}
	}

	/** Always called by generated parsers upon entry to a rule.
	 *  This occurs after the new context has been pushed. Access field
	 *  _ctx get the current context.
	 *
	 *  This is flexible because users do not have to regenerate parsers
	 *  to get trace facilities.
	 */
	@Override
	public void enterRule(ParserRuleContext<Token> localctx, int ruleIndex) {
		_ctx = localctx;
		_ctx.start = _input.LT(1);
		_ctx.ruleIndex = ruleIndex;
		if ( buildParseTrees ) addContextToParseTree();
	}

	@Override
	public Token match(int ttype) throws RecognitionException {
		return super.match(ttype);
	}

	@Override
	public Token getCurrentInputSymbol() {
		return _input.LT(1);
	}

	@Override
	public TokenStream getInputStream() { return _input; }

	@Override
	public void setInputStream(IntStream input) { _input = (TokenStream)input; }

	/** Set the token stream and reset the parser */
	public void setTokenStream(TokenStream input) {
		this._input = null;
		reset();
		this._input = input;
	}

    public TokenStream getTokenStream() {
		return _input;
	}

	@Override
	public String getSourceName() {
		return _input.getSourceName();
	}
}
