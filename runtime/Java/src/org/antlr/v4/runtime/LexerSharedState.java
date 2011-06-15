package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.QStack;

public class LexerSharedState extends RecognizerSharedState<CharStream> {
	//public CharStream input;

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

	/** The channel number for the current token */
	public int channel;

	/** The token type for the current token */
	public int type;

	public QStack<Integer> modeStack;
	public int mode = Lexer.DEFAULT_MODE;

	/** You can set the text for the current token to override what is in
	 *  the input char buffer.  Use setText() or can set this instance var.
	 */
	public String text;

	public LexerSharedState() {
	}

	public LexerSharedState(LexerSharedState state) {
		this.token = state.token;
		this.tokenStartCharIndex = state.tokenStartCharIndex;
		this.tokenStartLine = state.tokenStartLine;
		this.tokenStartCharPositionInLine = state.tokenStartCharPositionInLine;
		this.channel = state.channel;
		this.type = state.type;
		this.text = state.text;
	}
}
