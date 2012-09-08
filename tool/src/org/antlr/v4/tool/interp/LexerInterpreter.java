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

package org.antlr.v4.tool.interp;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.tool.LexerGrammar;

public class LexerInterpreter implements TokenSource {
	protected LexerGrammar g;
	protected LexerATNSimulator interp;
	protected CharStream input;

	/** How to create token objects */
	protected TokenFactory<?> _factory = CommonTokenFactory.DEFAULT;

	protected static final DFA[] _decisionToDFA = new DFA[1];
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();

	public LexerInterpreter(LexerGrammar g, String inputString) {
		this(g);
		setInput(inputString);
	}

	public LexerInterpreter(LexerGrammar g) {
		Tool antlr = new Tool();
		antlr.process(g,false);
		interp = new LexerATNSimulator(g.atn,_decisionToDFA,_sharedContextCache);
	}

	public void setInput(String inputString) {
		input = new ANTLRInputStream(inputString);
	}

	public void setInput(CharStream input) {
		this.input = input;
	}

	@Override
	public String getSourceName() {	return g.name; }

	@Override
	public void setTokenFactory(TokenFactory<?> factory) {
		this._factory = factory;
	}

	@Override
	public int getCharPositionInLine() {
		return 0;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public CharStream getInputStream() {
		return input;
	}

	@Override
	public Token nextToken() {
		// TODO: Deal with off channel tokens
		int start = input.index();
		int tokenStartCharPositionInLine = interp.getCharPositionInLine();
		int tokenStartLine = interp.getLine();
		int ttype = interp.match(input, Lexer.DEFAULT_MODE);
		int stop = input.index()-1;

		Token t =
			_factory.create(this, ttype, null, Token.DEFAULT_CHANNEL, start, stop,
							tokenStartLine, tokenStartCharPositionInLine);
		return t;

		/*
		outer:
		while (true) {
			token = null;
			channel = Token.DEFAULT_CHANNEL;
			tokenStartCharIndex = input.index();
			tokenStartCharPositionInLine = input.getCharPositionInLine();
			tokenStartLine = input.getLine();
			text = null;
			do {
				type = Token.INVALID_TYPE;
				if ( input.LA(1)==CharStream.EOF ) {
					Token eof = new CommonToken(input,Token.EOF,
												Token.DEFAULT_CHANNEL,
												input.index(),input.index());
					eof.setLine(getLine());
					eof.setCharPositionInLine(getCharPositionInLine());
					return eof;
				}
//				System.out.println("nextToken at "+((char)input.LA(1))+
//								   " in mode "+mode+
//								   " at index "+input.index());
				int ttype = _interp.match(input, mode);
//				System.out.println("accepted ttype "+ttype);
				if ( type == Token.INVALID_TYPE) type = ttype;
				if ( type==SKIP ) {
					continue outer;
				}
			} while ( type==MORE );
			if ( token==null ) emit();
			return token;
		}
*/
	}
}
