package org.antlr.v4.parse;

import org.antlr.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class v3TreeGrammarException extends ParseCancellationException {
	public Token location;

	public v3TreeGrammarException(Token location) {
		this.location = location;
	}
}
