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

import org.antlr.runtime.*;

/** A lexer is recognizer that draws input symbols from a character stream.
 *  lexer grammars result in a subclass of this object. A Lexer object
 *  uses simplified match() and error recovery mechanisms in the interest
 *  of speed.
 */
public abstract class Lexer extends BaseRecognizer implements TokenSource {
	/** Where is the lexer drawing characters from? */
	protected CharStream input;

	public Lexer() {
	}

	public Lexer(CharStream input) {
		this.input = input;
	}

	public Lexer(CharStream input, RecognizerSharedState state) {
		super(state);
		this.input = input;
	}

	public void reset() {
		super.reset(); // reset all recognizer state variables
		// wack Lexer state variables
		if ( input!=null ) {
			input.seek(0); // rewind the input
		}
		if ( state==null ) {
			return; // no shared state work to do
		}
		state.token = null;
		state.type = Token.INVALID_TOKEN_TYPE;
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
		while (true) {
			state.token = null;
			state.channel = Token.DEFAULT_CHANNEL;
			state.tokenStartCharIndex = input.index();
			state.tokenStartCharPositionInLine = input.getCharPositionInLine();
			state.tokenStartLine = input.getLine();
			state.text = null;
			if ( input.LA(1)==CharStream.EOF ) {
                Token eof = new CommonToken((CharStream)input,Token.EOF,
                                            Token.DEFAULT_CHANNEL,
                                            input.index(),input.index());
                eof.setLine(getLine());
                eof.setCharPositionInLine(getCharPositionInLine());
                return eof;
			}
			try {
				mTokens();
				if ( state.token==null ) {
					emit();
				}
				else if ( state.token==Token.SKIP_TOKEN ) {
					continue;
				}
				return state.token;
			}
			catch (NoViableAltException nva) {
				reportError(nva);
				recover(nva); // throw out current char and try again
			}
			catch (RecognitionException re) {
				reportError(re);
				// match() routine has already called recover()
			}
		}
	}

	/** Instruct the lexer to skip creating a token for current lexer rule
	 *  and look for another token.  nextToken() knows to keep looking when
	 *  a lexer rule finishes with token set to SKIP_TOKEN.  Recall that
	 *  if token==null at end of any token rule, it creates one for you
	 *  and emits it.
	 */
	public void skip() {
		state.token = Token.SKIP_TOKEN;
	}

	/** This is the lexer entry point that sets instance var 'token' */
	public abstract void mTokens() throws RecognitionException;

	/** Set the char stream and reset the lexer */
	public void setCharStream(CharStream input) {
		this.input = null;
		reset();
		this.input = input;
	}

	public CharStream getCharStream() {
		return this.input;
	}

	public String getSourceName() {
		return input.getSourceName();
	}

	/** Currently does not support multiple emits per nextToken invocation
	 *  for efficiency reasons.  Subclass and override this method and
	 *  nextToken (to push tokens into a list and pull from that list rather
	 *  than a single variable as this implementation does).
	 */
	public void emit(Token token) {
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
		Token t = new CommonToken(input, state.type, state.channel, state.tokenStartCharIndex, getCharIndex()-1);
		t.setLine(state.tokenStartLine);
		t.setText(state.text);
		t.setCharPositionInLine(state.tokenStartCharPositionInLine);
		emit(t);
		return t;
	}

	public void match(String s) throws MismatchedTokenException {
		int i = 0;
		while ( i<s.length() ) {
			if ( input.LA(1)!=s.charAt(i) ) {
				if ( state.backtracking>0 ) {
					state.failed = true;
					return;
				}
				MismatchedTokenException mte =
					new MismatchedTokenException(s.charAt(i), input);
				recover(mte);
				throw mte;
			}
			i++;
			input.consume();
			state.failed = false;
		}
	}

	public void matchAny() {
		input.consume();
	}

	public void match(int c) throws MismatchedTokenException {
		if ( input.LA(1)!=c ) {
			if ( state.backtracking>0 ) {
				state.failed = true;
				return;
			}
			MismatchedTokenException mte =
				new MismatchedTokenException(c, input);
			recover(mte);  // don't really recover; just consume in lexer
			throw mte;
		}
		input.consume();
		state.failed = false;
	}

	public void matchRange(int a, int b)
		throws MismatchedRangeException
	{
		if ( input.LA(1)<a || input.LA(1)>b ) {
			if ( state.backtracking>0 ) {
				state.failed = true;
				return;
			}
			MismatchedRangeException mre =
				new MismatchedRangeException(a,b,input);
			recover(mre);
			throw mre;
		}
		input.consume();
		state.failed = false;
	}

	public int getLine() {
		return input.getLine();
	}

	public int getCharPositionInLine() {
		return input.getCharPositionInLine();
	}

	/** What is the index of the current character of lookahead? */
	public int getCharIndex() {
		return input.index();
	}

	/** Return the text matched so far for the current token or any
	 *  text override.
	 */
	public String getText() {
		if ( state.text!=null ) {
			return state.text;
		}
		return input.substring(state.tokenStartCharIndex,getCharIndex()-1);
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

		displayRecognitionError(this.getTokenNames(), e);
	}

	public String getErrorMessage(RecognitionException e, String[] tokenNames) {
		String msg = null;
		if ( e instanceof MismatchedTokenException ) {
			MismatchedTokenException mte = (MismatchedTokenException)e;
			msg = "mismatched character "+getCharErrorDisplay(e.c)+" expecting "+getCharErrorDisplay(mte.expecting);
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
			msg = super.getErrorMessage(e, tokenNames);
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
		//System.out.println("consuming char "+(char)input.LA(1)+" during recovery");
		//re.printStackTrace();
		input.consume();
	}

	public void traceIn(String ruleName, int ruleIndex)  {
		String inputSymbol = ((char)input.LT(1))+" line="+getLine()+":"+getCharPositionInLine();
		super.traceIn(ruleName, ruleIndex, inputSymbol);
	}

	public void traceOut(String ruleName, int ruleIndex)  {
		String inputSymbol = ((char)input.LT(1))+" line="+getLine()+":"+getCharPositionInLine();
		super.traceOut(ruleName, ruleIndex, inputSymbol);
	}
}
