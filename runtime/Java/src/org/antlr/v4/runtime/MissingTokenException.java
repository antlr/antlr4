package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.IntervalSet;

public class MissingTokenException extends RecognitionException {
	public final IntervalSet expecting;

	public MissingTokenException(IntervalSet expecting, Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
		super(recognizer, input, ctx);
		this.expecting = expecting;
	}
}
