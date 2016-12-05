/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/**
 * A source of tokens must provide a sequence of tokens via {@link #nextToken()}
 * and also must reveal it's source of characters; {@link org.antlr.v4.runtime.CommonToken}'s text is
 * computed from a {@link org.antlr.v4.runtime.CharStream}; it only store indices into the char
 * stream.
 *
 * <p>Errors from the lexer are never passed to the parser. Either you want to keep
 * going or you do not upon token recognition error. If you do not want to
 * continue lexing then you do not want to continue parsing. Just throw an
 * exception not under {@link org.antlr.v4.runtime.RecognitionException} and Java will naturally toss
 * you all the way out of the recognizers. If you want to continue lexing then
 * you should not throw an exception to the parser--it has already requested a
 * token. Keep lexing until you get a valid one. Just report errors and keep
 * going, looking for a valid token.</p>
 */

public protocol TokenSource: class {
    /**
     * Return a {@link org.antlr.v4.runtime.Token} object from your input stream (usually a
     * {@link org.antlr.v4.runtime.CharStream}). Do not fail/return upon lexing error; keep chewing
     * on the characters until you get a good one; errors are not passed through
     * to the parser.
     */
    func nextToken() throws -> Token

    /**
     * Get the line number for the current position in the input stream. The
     * first line in the input is line 1.
     *
     * @return The line number for the current position in the input stream, or
     * 0 if the current token source does not track line numbers.
     */
    func getLine() -> Int

    /**
     * Get the index into the current line for the current position in the input
     * stream. The first character on a line has position 0.
     *
     * @return The line number for the current position in the input stream, or
     * -1 if the current token source does not track character positions.
     */
    func getCharPositionInLine() -> Int

    /**
     * Get the {@link org.antlr.v4.runtime.CharStream} from which this token source is currently
     * providing tokens.
     *
     * @return The {@link org.antlr.v4.runtime.CharStream} associated with the current position in
     * the input, or {@code null} if no input stream is available for the token
     * source.
     */
    func getInputStream() -> CharStream?

    /**
     * Gets the name of the underlying input source. This method returns a
     * non-null, non-empty string. If such a name is not known, this method
     * returns {@link org.antlr.v4.runtime.IntStream#UNKNOWN_SOURCE_NAME}.
     */
    func getSourceName() -> String

    /**
     * Set the {@link org.antlr.v4.runtime.TokenFactory} this token source should use for creating
     * {@link org.antlr.v4.runtime.Token} objects from the input.
     *
     * @param factory The {@link org.antlr.v4.runtime.TokenFactory} to use for creating tokens.
     */
    func setTokenFactory(_ factory: TokenFactory)

    /**
     * Gets the {@link org.antlr.v4.runtime.TokenFactory} this token source is currently using for
     * creating {@link org.antlr.v4.runtime.Token} objects from the input.
     *
     * @return The {@link org.antlr.v4.runtime.TokenFactory} currently used by this token source.
     */
    func getTokenFactory() -> TokenFactory
}
