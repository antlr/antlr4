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

import org.antlr.v4.runtime.atn.LexerInterpreter;
import org.antlr.v4.runtime.misc.QStack;

import java.util.EmptyStackException;

/** A lexer is recognizer that draws input symbols from a character stream.
 *  lexer grammars result in a subclass of this object. A Lexer object
 *  uses simplified match() and error recovery mechanisms in the interest
 *  of speed.
 */
public abstract class Lexer extends Recognizer<LexerSharedState, LexerInterpreter>
	implements TokenSource
{
	public static final int DEFAULT_MODE = 0;
	public static final int MORE = -2;
	public static final int SKIP = -3;

	public static final int DEFAULT_TOKEN_CHANNEL = Token.DEFAULT_CHANNEL;
	public static final int HIDDEN = Token.HIDDEN_CHANNEL;

	public LexerSharedState state;

	public Lexer(CharStream input) {
		this(input, new LexerSharedState());
	}

	public Lexer(CharStream input, LexerSharedState state) {
		if ( state==null ) {
			state = new LexerSharedState();
		}
		this.state = state;
		state.input = input;
	}

	public void reset() {
		// wack Lexer state variables
		if ( state.input!=null ) {
			state.input.seek(0); // rewind the input
		}
		if ( state==null ) {
			return; // no shared state work to do
		}
		state.token = null;
		state.type = Token.INVALID_TYPE;
		state.channel = Token.DEFAULT_CHANNEL;
		state.tokenStartCharIndex = -1;
		state.tokenStartCharPositionInLine = -1;
		state.tokenStartLine = -1;
		state.text = null;
	}

	/** Return a token from this source; i.e., match a token on the char
	 *  stream.
	 */
	public Token nextToken() {
		outer:
		while (true) {
			state.token = null;
			state.channel = Token.DEFAULT_CHANNEL;
			state.tokenStartCharIndex = state.input.index();
			state.tokenStartCharPositionInLine = state.input.getCharPositionInLine();
			state.tokenStartLine = state.input.getLine();
			state.text = null;
			do {
				state.type = Token.INVALID_TYPE;
				if ( state.input.LA(1)==CharStream.EOF ) {
					Token eof = new CommonToken(state.input,Token.EOF,
												Token.DEFAULT_CHANNEL,
												state.input.index(),state.input.index());
					eof.setLine(getLine());
					eof.setCharPositionInLine(getCharPositionInLine());
					return eof;
				}
//				System.out.println("nextToken at "+((char)state.input.LA(1))+
//								   " in mode "+state.mode+
//								   " at index "+state.input.index());
				int ttype = _interp.match(state.input, state.mode);
//				System.out.println("accepted ttype "+ttype);
				if ( state.type == Token.INVALID_TYPE) state.type = ttype;
				if ( state.type==SKIP ) {
					continue outer;
				}
			} while ( state.type==MORE );
			if ( state.token==null ) emit();
			return state.token;
		}
	}

	/** Instruct the lexer to skip creating a token for current lexer rule
	 *  and look for another token.  nextToken() knows to keep looking when
	 *  a lexer rule finishes with token set to SKIP_TOKEN.  Recall that
	 *  if token==null at end of any token rule, it creates one for you
	 *  and emits it.
	 */
	public void skip() {
		state.type = SKIP;
	}

	public void more() {
		state.type = MORE;
	}

	public void mode(int m) {
		state.mode = m;
	}

	public void pushMode(int m) {
//		System.out.println("pushMode "+m);
		if ( state.modeStack==null ) state.modeStack = new QStack<Integer>();
		state.modeStack.push(state.mode);
		mode(m);
	}

	public int popMode() {
		if ( state.modeStack==null ) throw new EmptyStackException();
//		System.out.println("popMode back to "+state.modeStack.peek());
		mode( state.modeStack.pop() );
		return state.mode;
	}

	/** Set the char stream and reset the lexer */
	public void setCharStream(CharStream input) {
		this.state.input = null;
		reset();
		this.state.input = input;
	}

	public CharStream getCharStream() {
		return ((CharStream)state.input);
	}

	public String getSourceName() {
		return state.input.getSourceName();
	}

	/** Currently does not support multiple emits per nextToken invocation
	 *  for efficiency reasons.  Subclass and override this method and
	 *  nextToken (to push tokens into a list and pull from that list rather
	 *  than a single variable as this implementation does).
	 */
	public void emit(Token token) {
		//System.err.println("emit "+token);
		state.token = token;
	}

	/** The standard method called to automatically emit a token at the
	 *  outermost lexical rule.  The token object should point into the
	 *  char buffer start..stop.  If there is a text override in 'text',
	 *  use that to set the token's text.  Override this method to emit
	 *  custom Token objects.
	 *
	 *  If you are building trees, then you should also override
	 *  Parser or TreeParser.getMissingSymbol().
	 */
	public Token emit() {
		Token t = new CommonToken(((CharStream)state.input), state.type,
								  state.channel, state.tokenStartCharIndex,
								  getCharIndex()-1);
		t.setLine(state.tokenStartLine);
		t.setText(state.text);
		t.setCharPositionInLine(state.tokenStartCharPositionInLine);
		emit(t);
		return t;
	}

	public int getLine() {
		return ((CharStream)state.input).getLine();
	}

	public int getCharPositionInLine() {
		return ((CharStream)state.input).getCharPositionInLine();
	}

	/** What is the index of the current character of lookahead? */
	public int getCharIndex() {
		return state.input.index();
	}

	/** Return the text matched so far for the current token or any
	 *  text override.
	 */
	public String getText() {
		if ( state.text!=null ) {
			return state.text;
		}
		return ((CharStream)state.input).substring(state.tokenStartCharIndex,getCharIndex()-1);
	}

	/** Set the complete text of this token; it wipes any previous
	 *  changes to the text.
	 */
	public void setText(String text) {
		state.text = text;
	}

	public void reportError(RecognitionException e) {
		/** TODO: not thought about recovery in lexer yet.
		 *
		// if we've already reported an error and have not matched a token
		// yet successfully, don't report any errors.
		if ( errorRecovery ) {
			//System.err.print("[SPURIOUS] ");
			return;
		}
		errorRecovery = true;
		 */

		//displayRecognitionError(this.getTokenNames(), e);
	}

	/** Used to print out token names like ID during debugging and
	 *  error reporting.  The generated parsers implement a method
	 *  that overrides this to point to their String[] tokenNames.
	 */
	public String[] getTokenNames() {
		return null;
	}

	public String getErrorMessage(RecognitionException e) {
		String msg = null;
		if ( e instanceof MismatchedTokenException ) {
			MismatchedTokenException mte = (MismatchedTokenException)e;
			msg = "mismatched character "+getCharErrorDisplay(e.c)+" expecting "+
				  getCharErrorDisplay(mte.expecting.getSingleElement());
		}
		else if ( e instanceof NoViableAltException ) {
			NoViableAltException nvae = (NoViableAltException)e;
			// for development, can add "decision=<<"+nvae.grammarDecisionDescription+">>"
			// and "(decision="+nvae.decisionNumber+") and
			// "state "+nvae.stateNumber
			msg = "no viable alternative at character "+getCharErrorDisplay(e.c);
		}
		else if ( e instanceof EarlyExitException ) {
			EarlyExitException eee = (EarlyExitException)e;
			// for development, can add "(decision="+eee.decisionNumber+")"
			msg = "required (...)+ loop did not match anything at character "+getCharErrorDisplay(e.c);
		}
		else if ( e instanceof MismatchedNotSetException ) {
			MismatchedNotSetException mse = (MismatchedNotSetException)e;
			msg = "mismatched character "+getCharErrorDisplay(e.c)+" expecting set "+mse.expecting;
		}
		else if ( e instanceof MismatchedSetException ) {
			MismatchedSetException mse = (MismatchedSetException)e;
			msg = "mismatched character "+getCharErrorDisplay(e.c)+" expecting set "+mse.expecting;
		}
		else if ( e instanceof MismatchedRangeException ) {
			MismatchedRangeException mre = (MismatchedRangeException)e;
			msg = "mismatched character "+getCharErrorDisplay(e.c)+" expecting set "+
				  getCharErrorDisplay(mre.a)+".."+getCharErrorDisplay(mre.b);
		}
		else {
			//msg = super.getErrorMessage(e, tokenNames);
		}
		return msg;
	}

	public String getCharErrorDisplay(int c) {
		String s = String.valueOf((char)c);
		switch ( c ) {
			case Token.EOF :
				s = "<EOF>";
				break;
			case '\n' :
				s = "\\n";
				break;
			case '\t' :
				s = "\\t";
				break;
			case '\r' :
				s = "\\r";
				break;
		}
		return "'"+s+"'";
	}

	/** Lexers can normally match any char in it's vocabulary after matching
	 *  a token, so do the easy thing and just kill a character and hope
	 *  it all works out.  You can instead use the rule invocation stack
	 *  to do sophisticated error recovery if you are in a fragment rule.
	 */
	public void recover(RecognitionException re) {
		//System.out.println("consuming char "+(char)state.input.LA(1)+" during recovery");
		//re.printStackTrace();
		state.input.consume();
	}
}
