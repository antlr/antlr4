package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

public class WildcardTagToken implements Token {
	@Override
	public int getChannel() {
		return 0;
	}

	@Override
	public String getText() {
		return "<.>";
	}

	@Override
	public int getType() {
		return Token.INVALID_TYPE;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public int getCharPositionInLine() {
		return 0;
	}

	@Override
	public int getTokenIndex() {
		return 0;
	}

	@Override
	public int getStartIndex() {
		return 0;
	}

	@Override
	public int getStopIndex() {
		return 0;
	}

	@Override
	public TokenSource getTokenSource() {
		return null;
	}

	@Override
	public CharStream getInputStream() {
		return null;
	}

	@Override
	public String toString() {
		return "<.>";
	}
}
