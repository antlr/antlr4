/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Pair;

import java.util.List;

/**
 * Provides an implementation of {@link TokenSource} as a wrapper around a list
 * of {@link Token} objects.
 *
 * <p>If the final token in the list is an {@link Token#EOF} token, it will be used
 * as the EOF token for every call to {@link #nextToken} after the end of the
 * list is reached. Otherwise, an EOF token will be created.</p>
 */
public class ListTokenSource implements TokenSource {
	/**
	 * The wrapped collection of {@link Token} objects to return.
	 */
	protected final List<? extends Token> tokens;

	/**
	 * The name of the input source. If this value is {@code null}, a call to
	 * {@link #getSourceName} should return the source name used to create the
	 * the next token in {@link #tokens} (or the previous token if the end of
	 * the input has been reached).
	 */
	private final String sourceName;

	/**
	 * The index into {@link #tokens} of token to return by the next call to
	 * {@link #nextToken}. The end of the input is indicated by this value
	 * being greater than or equal to the number of items in {@link #tokens}.
	 */
	protected int i;

	/**
	 * This field caches the EOF token for the token source.
	 */
	protected Token eofToken;

	/**
	 * This is the backing field for {@link #getTokenFactory} and
	 * {@link setTokenFactory}.
	 */
	private TokenFactory<?> _factory = CommonTokenFactory.DEFAULT;

	/**
	 * Constructs a new {@link ListTokenSource} instance from the specified
	 * collection of {@link Token} objects.
	 *
	 * @param tokens The collection of {@link Token} objects to provide as a
	 * {@link TokenSource}.
	 * @exception NullPointerException if {@code tokens} is {@code null}
	 */
	public ListTokenSource(List<? extends Token> tokens) {
		this(tokens, null);
	}

	/**
	 * Constructs a new {@link ListTokenSource} instance from the specified
	 * collection of {@link Token} objects and source name.
	 *
	 * @param tokens The collection of {@link Token} objects to provide as a
	 * {@link TokenSource}.
	 * @param sourceName The name of the {@link TokenSource}. If this value is
	 * {@code null}, {@link #getSourceName} will attempt to infer the name from
	 * the next {@link Token} (or the previous token if the end of the input has
	 * been reached).
	 *
	 * @exception NullPointerException if {@code tokens} is {@code null}
	 */
	public ListTokenSource(List<? extends Token> tokens, String sourceName) {
		if (tokens == null) {
			throw new NullPointerException("tokens cannot be null");
		}

		this.tokens = tokens;
		this.sourceName = sourceName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCharPositionInLine() {
		if (i < tokens.size()) {
			return tokens.get(i).getCharPositionInLine();
		}
		else if (eofToken != null) {
			return eofToken.getCharPositionInLine();
		}
		else if (tokens.size() > 0) {
			// have to calculate the result from the line/column of the previous
			// token, along with the text of the token.
			Token lastToken = tokens.get(tokens.size() - 1);
			String tokenText = lastToken.getText();
			if (tokenText != null) {
				int lastNewLine = tokenText.lastIndexOf('\n');
				if (lastNewLine >= 0) {
					return tokenText.length() - lastNewLine - 1;
				}
			}

			return lastToken.getCharPositionInLine() + lastToken.getStopIndex() - lastToken.getStartIndex() + 1;
		}

		// only reach this if tokens is empty, meaning EOF occurs at the first
		// position in the input
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Token nextToken() {
		if (i >= tokens.size()) {
			if (eofToken == null) {
				int start = -1;
				if (tokens.size() > 0) {
					int previousStop = tokens.get(tokens.size() - 1).getStopIndex();
					if (previousStop != -1) {
						start = previousStop + 1;
					}
				}

				int stop = Math.max(-1, start - 1);
				eofToken = _factory.create(new Pair<TokenSource, CharStream>(this, getInputStream()), Token.EOF, "EOF", Token.DEFAULT_CHANNEL, start, stop, getLine(), getCharPositionInLine());
			}

			return eofToken;
		}

		Token t = tokens.get(i);
		if (i == tokens.size() - 1 && t.getType() == Token.EOF) {
			eofToken = t;
		}

		i++;
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLine() {
		if (i < tokens.size()) {
			return tokens.get(i).getLine();
		}
		else if (eofToken != null) {
			return eofToken.getLine();
		}
		else if (tokens.size() > 0) {
			// have to calculate the result from the line/column of the previous
			// token, along with the text of the token.
			Token lastToken = tokens.get(tokens.size() - 1);
			int line = lastToken.getLine();

			String tokenText = lastToken.getText();
			if (tokenText != null) {
				for (int i = 0; i < tokenText.length(); i++) {
					if (tokenText.charAt(i) == '\n') {
						line++;
					}
				}
			}

			// if no text is available, assume the token did not contain any newline characters.
			return line;
		}

		// only reach this if tokens is empty, meaning EOF occurs at the first
		// position in the input
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharStream getInputStream() {
		if (i < tokens.size()) {
			return tokens.get(i).getInputStream();
		}
		else if (eofToken != null) {
			return eofToken.getInputStream();
		}
		else if (tokens.size() > 0) {
			return tokens.get(tokens.size() - 1).getInputStream();
		}

		// no input stream information is available
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSourceName() {
		if (sourceName != null) {
			return sourceName;
		}

		CharStream inputStream = getInputStream();
		if (inputStream != null) {
			return inputStream.getSourceName();
		}

		return "List";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTokenFactory(TokenFactory<?> factory) {
		this._factory = factory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TokenFactory<?> getTokenFactory() {
		return _factory;
	}
}
