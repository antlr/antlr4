package org.antlr.v4.runtime;

public abstract class LexerException extends RecognitionException {
	public final int startIndex;
	public final int length;

	protected LexerException(Lexer lexer, IntStream input, int startIndex, int length) {
		super(lexer, input, null);
		this.startIndex = startIndex;
		this.length = length;
	}

	public abstract String getErrorMessage(String input);
}
