package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.misc.OrderedHashSet;

public class LexerNoViableAltException extends LexerRecognitionExeption {
	/** Prediction began at what input index? */
	public int startIndex;

	/** Which configurations did we try at input.index() that couldn't match input.LT(1)? */
	public OrderedHashSet<ATNConfig> deadEndConfigs;

	/** Used for remote debugger deserialization */
	public LexerNoViableAltException() {;}

	public LexerNoViableAltException(Lexer lexer,
									 CharStream input,
									 OrderedHashSet<ATNConfig> deadEndConfigs) {
		super(lexer, input);
		this.deadEndConfigs = deadEndConfigs;
	}

	public String toString() {
		return "NoViableAltException('"+(char)c+"'";
	}
}
