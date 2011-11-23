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
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.tool.LexerGrammar;

public class LexerInterpreter implements TokenSource {
	protected LexerGrammar g;
	protected LexerATNSimulator interp;
	protected LexerATNSimulator.State state;

	public LexerInterpreter(LexerGrammar g, String inputString) {
		this(g);
		setInput(inputString);
	}

	public LexerInterpreter(LexerGrammar g) {
		Tool antlr = new Tool();
		antlr.process(g,false);
		interp = new LexerATNSimulator(g.atn);
	}

	public void setInput(String inputString) {
		setInput(new ANTLRInputStream(inputString));
	}

	public void setInput(CharStream input) {
		this.state = new LexerATNSimulator.State(input);
	}

	public String getSourceName() {	return g.name; }

	public int getCharPositionInLine() {
		return 0;
	}

	public int getLine() {
		return 0;
	}

	public CharStream getInputStream() {
		return state.getInput();
	}

	public Token nextToken() {
		// TODO: Deal with off channel tokens
		int start = state.getInput().index();
		int tokenStartCharPositionInLine = interp.getCharPositionInLine(state);
		int tokenStartLine = interp.getLine(state);
		int ttype = interp.match(state, Lexer.DEFAULT_MODE);
		int stop = state.getInput().index()-1;
		WritableToken t = new CommonToken(this, ttype, Token.DEFAULT_CHANNEL, start, stop);
		t.setLine(tokenStartLine);
		t.setCharPositionInLine(tokenStartCharPositionInLine);
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
