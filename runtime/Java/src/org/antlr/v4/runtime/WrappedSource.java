package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Pair;

/**
 * An abstract base class for {@link TokenSource} wrappers.  It provides
 * common implementation and utility methods for deriving wrappers.
 * Tokens subclasses will generally want to use the cloneToken method
 * to avoid inadvertent changes to source tokens.
 */
public abstract class WrappedSource implements TokenSource {
	protected TokenFactory<?> factory;

	/** Clone an input Token.  Our stream view clones tokens so that the
	 * original tokens are not modified.  This is particularly important
	 * to preserve the source Token's index.
	 * */
	protected Token cloneToken(Token t) {
		if (factory != null) {
			return factory.create(new Pair<TokenSource, CharStream>(this, null), t.getType(), t.getText(), t.getChannel(),
					-1, -1, getLine(), getCharPositionInLine());
		} else {
			return new CommonToken(t);
		}
	}

	protected Token createEOF() {
		// All tokens exhausted.  Return EOF
		if (factory != null) {
			return factory.create(new Pair<TokenSource, CharStream>(this, null), Token.EOF, "",
					Token.DEFAULT_CHANNEL, -1, -1, getLine(), getCharPositionInLine());
		} else {
			return new CommonToken(new Pair<TokenSource, CharStream>(this, null), Token.EOF, Token.DEFAULT_CHANNEL,
					-1, -1);
		}
	}

	@Override
	public int getLine() {
		// Possible TODO: boolean switch to enable line tracking
		return -1;
	}

	@Override
	public int getCharPositionInLine() {
		// Possible TODO: boolean switch to enable column tracking
		return -1;
	}

	@Override
	public CharStream getInputStream() {
		return null;
	}

	@Override
	public void setTokenFactory(@NotNull TokenFactory<?> factory) {
		this.factory = factory;
	}

	@Override
	public TokenFactory<?> getTokenFactory() {
		return factory;
	}
}
