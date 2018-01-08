/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

/**
 * A source of tokens must provide a sequence of tokens via {@link #nextToken()}
 * and also must reveal it's source of characters; {@link CommonToken}'s text is
 * computed from a {@link CharStream}; it only store indices into the char
 * stream.
 *
 * <p>Errors from the lexer are never passed to the parser. Either you want to keep
 * going or you do not upon token recognition error. If you do not want to
 * continue lexing then you do not want to continue parsing. Just throw an
 * exception not under {@link RecognitionException} and Java will naturally toss
 * you all the way out of the recognizers. If you want to continue lexing then
 * you should not throw an exception to the parser--it has already requested a
 * token. Keep lexing until you get a valid one. Just report errors and keep
 * going, looking for a valid token.</p>
 */
public interface TokenSource {
	/**
	 * Return a {@link Token} object from your input stream (usually a
	 * {@link CharStream}). Do not fail/return upon lexing error; keep chewing
	 * on the characters until you get a good one; errors are not passed through
	 * to the parser.
	 */
	public Token nextToken();

	/**
	 * Get the line number for the current position in the input stream. The
	 * first line in the input is line 1.
	 *
	 * @return The line number for the current position in the input stream, or
	 * 0 if the current token source does not track line numbers.
	 */
	public int getLine();

	/**
	 * Get the index into the current line for the current position in the input
	 * stream. The first character on a line has position 0.
	 *
	 * @return The line number for the current position in the input stream, or
	 * -1 if the current token source does not track character positions.
	 */
	public int getCharPositionInLine();

	/**
	 * Get the {@link CharStream} from which this token source is currently
	 * providing tokens.
	 *
	 * @return The {@link CharStream} associated with the current position in
	 * the input, or {@code null} if no input stream is available for the token
	 * source.
	 */
	public CharStream getInputStream();

	/**
	 * Gets the name of the underlying input source. This method returns a
	 * non-null, non-empty string. If such a name is not known, this method
	 * returns {@link IntStream#UNKNOWN_SOURCE_NAME}.
	 */
	public String getSourceName();

	/**
	 * Set the {@link TokenFactory} this token source should use for creating
	 * {@link Token} objects from the input.
	 *
	 * @param factory The {@link TokenFactory} to use for creating tokens.
	 */
	public void setTokenFactory(TokenFactory<?> factory);

	/**
	 * Gets the {@link TokenFactory} this token source is currently using for
	 * creating {@link Token} objects from the input.
	 *
	 * @return The {@link TokenFactory} currently used by this token source.
	 */
	public TokenFactory<?> getTokenFactory();
}
