package org.antlr.v4.runtime;

public class UnwantedTokenException extends RecognitionException {
	public final Token token;

	public UnwantedTokenException(Token token, Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
		super(recognizer, input, ctx);
		this.token = token;
	}
}
