package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

/** Convert a list of token objects to a source of tokens for a TokenStream. */
public class ListTokenSource implements TokenSource {
	protected List<? extends Token> tokens;
	protected int i = 0;
	protected TokenFactory<?> _factory = CommonTokenFactory.DEFAULT;

	public ListTokenSource(List<? extends Token> tokens) {
		assert tokens!=null;
		this.tokens = tokens;
	}

	@Override
	public int getCharPositionInLine() {
		return -1;
	}

	@Override
	public Token nextToken() {
		if ( i>=tokens.size() ) {
			Token eof = new CommonToken(Token.EOF); // ignore factory
			return eof;
		}
		Token t = tokens.get(i);
		i++;
		return t;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public CharStream getInputStream() {
		return null;
	}

	@Override
	public String getSourceName() {
		return "<List>";
	}

	@Override
	public void setTokenFactory(@NotNull TokenFactory<?> factory) {
		this._factory = factory;
	}

	@Override
	public TokenFactory<?> getTokenFactory() {
		return _factory;
	}
}
