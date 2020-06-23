module antlr.v4.runtime.TokenSource;

import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.CommonToken;
import antlr.v4.runtime.TokenFactory;

/**
 * TODO add interface description
 */
interface TokenSource
{

    /**
     * @uml
     * Get the index into the current line for the current position in the input
     *  stream. The first character on a line has position 0.
     *
     *  @return The line number for the current position in the input stream, or
     * -1 if the current token source does not track character positions.
     */
    public int getCharPositionInLine();

    /**
     * @uml
     * Return a {@link Token} object from your input stream (usually a
     *  {@link CharStream}). Do not fail/return upon lexing error; keep chewing
     * on the characters until you get a good one; errors are not passed through
     * to the parser.
     */
    public Token nextToken();

    /**
     * @uml
     * Get the line number for the current position in the input stream. The
     * first line in the input is line 1.
     *
     *  @return The line number for the current position in the input stream, or
     * 0 if the current token source does not track line numbers.
     */
    public int getLine();

    /**
     * @uml
     * Get the {@link CharStream} from which this token source is currently
     * providing tokens.
     *
     *  @return The {@link CharStream} associated with the current position in
     * the input, or {@code null} if no input stream is available for the token
     * source.
     */
    public CharStream getInputStream();

    /**
     * @uml
     * Gets the name of the underlying input source. This method returns a
     * non-null, non-empty string. If such a name is not known, this method
     * returns {@link IntStream#UNKNOWN_SOURCE_NAME}.
     */
    public string getSourceName();

    /**
     * @uml
     * Set the {@link TokenFactory} this token source should use for creating
     * {@link Token} objects from the input.
     *
     *  @param factory The {@link TokenFactory} to use for creating tokens.
     */
    public void tokenFactory(TokenFactory!CommonToken factory);

    /**
     * @uml
     * Gets the {@link TokenFactory} this token source is currently using for
     * creating {@link Token} objects from the input.
     *
     *  @return The {@link TokenFactory} currently used by this token source.
     */
    public TokenFactory!CommonToken tokenFactory();

}
