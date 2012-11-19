package org.antlr.v4.parse;

import org.antlr.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class v3TreeGrammarException extends ParseCancellationException {
	private static final long serialVersionUID = -8383611621498312969L;

	public Token location;

	public v3TreeGrammarException(Token location) {
		this.location = location;
	}
}
