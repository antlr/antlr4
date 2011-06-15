package org.antlr.v4.runtime;

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
		this.line = input.getLine();
		this.charPositionInLine = input.getCharPositionInLine();
	}

}
