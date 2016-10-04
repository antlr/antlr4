/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  Copyright (c) 2015 Janyou
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
