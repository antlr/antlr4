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

import org.antlr.v4.runtime.atn.LexerATNSimulator;

import java.util.ArrayDeque;
import java.util.EmptyStackException;

/** A lexer is recognizer that draws input symbols from a character stream.
 *  lexer grammars result in a subclass of this object. A Lexer object
 *  uses simplified match() and error recovery mechanisms in the interest
 *  of speed.
 */
public abstract class Lexer extends Recognizer<Integer, LexerATNSimulator>
	implements TokenSource
{
	public static final int DEFAULT_MODE = 0;
	public static final int MORE = -2;
	public static final int SKIP = -3;

	public static final int DEFAULT_TOKEN_CHANNEL = Token.DEFAULT_CHANNEL;
	public static final int HIDDEN = Token.HIDDEN_CHANNEL;
	public static final int MIN_CHAR_VALUE = '\u0000';
	public static final int MAX_CHAR_VALUE = '\uFFFE';

	public CharStream input;

	/** The goal of all lexer rules/methods is to create a token object.
	 *  This is an instance variable as multiple rules may collaborate to
	 *  create a single token.  nextToken will return this object after
	 *  matching lexer rule(s).  If you subclass to allow multiple token
	 *  emissions, then set this to the last token to be matched or
	 *  something nonnull so that the auto token emit mechanism will not
	 *  emit another token.
	 */
	public Token token;

	/** What character index in the stream did the current token start at?
	 *  Needed, for example, to get the text for current token.  Set at
	 *  the start of nextToken.
	 */
	public int tokenStartCharIndex = -1;

	/** The line on which the first character of the token resides */
	public int tokenStartLine;

	/** The character position of first character within the line */
	public int tokenStartCharPositionInLine;

	/** Once we see EOF on char stream, next token will be EOF.
	 *  If you have DONE : EOF ; then you see DONE EOF.
	 */
	public boolean hitEOF;

	/** The channel number for the current token */
	public int channel;

	/** The token type for the current token */
	public int type;

	public ArrayDeque<Integer> modeStack;
	public int mode = Lexer.DEFAULT_MODE;

	/** You can set the text for the current token to override what is in
	 *  the input char buffer.  Use setText() or can set this instance var.
	 */
	public String text;

	public Lexer(CharStream input) {
		this.input = input;
	}

	public void reset() {
		// wack Lexer state variables
		if ( input!=null ) {
			input.seek(0); // rewind the input
		}
		token = null;
		type = Token.INVALID_TYPE;
		channel = Token.DEFAULT_CHANNEL;
		tokenStartCharIndex = -1;
		tokenStartCharPositionInLine = -1;
		tokenStartLine = -1;
		text = null;

		hitEOF = false;
		mode = Lexer.DEFAULT_MODE;
		if (modeStack != null) {
			modeStack.clear();
		}

		getInterpreter().reset();
	}

	/** Return a token from this source; i.e., match a token on the char
	 *  stream.
	 */
	@Override
	public Token nextToken() {
		if ( hitEOF ) return emitEOF();

		outer:
		while (true) {
			token = null;
			channel = Token.DEFAULT_CHANNEL;
			tokenStartCharIndex = input.index();
			tokenStartCharPositionInLine = getInterpreter().getCharPositionInLine();
			tokenStartLine = getInterpreter().getLine();
			text = null;
			do {
				type = Token.INVALID_TYPE;
//				System.out.println("nextToken line "+tokenStartLine+" at "+((char)input.LA(1))+
//								   " in mode "+mode+
//								   " at index "+input.index());
				int ttype;
				try {
					ttype = getInterpreter().match(input, mode);
				}
				catch (LexerNoViableAltException e) {
					notifyListeners(e);		// report error
					recover(e);
					ttype = SKIP;
				}
				if ( input.LA(1)==CharStream.EOF ) {
					hitEOF = true;
				}
				if ( type == Token.INVALID_TYPE ) type = ttype;
				if ( type==SKIP ) {
					continue outer;
				}
			} while ( type==MORE );
			if ( token==null ) emit();
			return token;
		}
	}

	/** Instruct the lexer to skip creating a token for current lexer rule
	 *  and look for another token.  nextToken() knows to keep looking when
	 *  a lexer rule finishes with token set to SKIP_TOKEN.  Recall that
	 *  if token==null at end of any token rule, it creates one for you
	 *  and emits it.
	 */
	public void skip() {
		type = SKIP;
	}

	public void more() {
		type = MORE;
	}

	public void mode(int m) {
		mode = m;
	}

	public void pushMode(int m) {
		if ( LexerATNSimulator.debug ) System.out.println("pushMode "+m);
		if ( modeStack==null ) modeStack = new ArrayDeque<Integer>();
		getInterpreter().tracePushMode(m);
		modeStack.push(mode);
		mode(m);
	}

	public int popMode() {
		if ( modeStack==null ) throw new EmptyStackException();
		if ( LexerATNSimulator.debug ) System.out.println("popMode back to "+modeStack.peek());
		getInterpreter().tracePopMode();
		mode( modeStack.pop() );
		return mode;
	}

	/** Set the char stream and reset the lexer */
	@Override
	public void setInputStream(IntStream input) {
		this.input = null;
		reset();
		this.input = (CharStream)input;
	}

	@Override
	public String getSourceName() {
		return input.getSourceName();
	}

	@Override
	public CharStream getInputStream() {
		return input;
	}

	/** Currently does not support multiple emits per nextToken invocation
	 *  for efficiency reasons.  Subclass and override this method and
	 *  nextToken (to push tokens into a list and pull from that list rather
	 *  than a single variable as this implementation does).
	 */
	public void emit(Token token) {
		getInterpreter().traceEmit(token);
		//System.err.println("emit "+token);
		this.token = token;
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
		WritableToken t = new CommonToken(this, type,
										  channel, tokenStartCharIndex,
										  getCharIndex()-1);
		t.setLine(tokenStartLine);
		if ( text!=null ) t.setText(text);
		t.setCharPositionInLine(tokenStartCharPositionInLine);
		emit(t);
		return t;
	}

	public Token emitEOF() {
		WritableToken eof = new CommonToken(this,Token.EOF,
											Token.DEFAULT_CHANNEL,
											input.index(),input.index()-1);
		eof.setLine(getLine());
		// The character position for EOF is one beyond the position of
		// the previous token's last character
		int cpos = getCharPositionInLine();
		if ( token!=null ) {
			int n = token.getStopIndex() - token.getStartIndex() + 1;
			cpos = token.getCharPositionInLine()+n;
		}
		eof.setCharPositionInLine(cpos);
		return eof;
	}

	@Override
	public int getLine() {
		return getInterpreter().getLine();
	}

	@Override
	public int getCharPositionInLine() {
		return getInterpreter().getCharPositionInLine();
	}

	/** What is the index of the current character of lookahead? */
	public int getCharIndex() {
		return input.index();
	}

	/** Return the text matched so far for the current token or any
	 *  text override.
	 */
	public String getText() {
		if ( text!=null ) {
			return text;
		}
		return getInterpreter().getText(input);
//		return ((CharStream)input).substring(tokenStartCharIndex,getCharIndex()-1);
	}

	/** Set the complete text of this token; it wipes any previous
	 *  changes to the text.
	 */
	public void setText(String text) {
		this.text = text;
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
	@Override
	public String[] getTokenNames() {
		return null;
	}

	public void recover(LexerNoViableAltException e) {
		getInterpreter().consume(input); // skip a char and try again
	}

	public void notifyListeners(LexerNoViableAltException e) {
		String msg = "token recognition error at: '"+
			input.substring(tokenStartCharIndex,input.index())+"'";
		ANTLRErrorListener<Integer>[] listeners = getListeners();
		if ( listeners.length == 0 ) {
			System.err.println("line "+tokenStartLine+":"+
							   tokenStartCharPositionInLine+" "+
							   msg);
			return;
		}
		for (ANTLRErrorListener<Integer> pl : listeners) {
			pl.error(this, null, tokenStartLine, tokenStartCharPositionInLine, msg, e);
		}
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
		// TODO: Do we lose character or line position information?
		input.consume();
	}
}
