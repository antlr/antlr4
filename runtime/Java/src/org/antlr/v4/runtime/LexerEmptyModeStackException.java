package org.antlr.v4.runtime;

public class LexerEmptyModeStackException extends LexerException {
	public LexerEmptyModeStackException(Lexer lexer, IntStream input, int startIndex, int length) {
		super(lexer, input, startIndex, length);
	}

	@Override
	public String getErrorMessage(String input) {
		return "Unable to pop mode because mode stack is empty at: '" + input + "'";
	}
}
