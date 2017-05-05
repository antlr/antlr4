/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/** A token has properties: text, type, line, character position in the line
 *  (so we can ignore tabs), token channel, index, and source from which
 *  we obtained this token.
 */

public protocol Token: class, CustomStringConvertible {
    //let INVALID_TYPE : Int = 0;

    /** During lookahead operations, this "token" signifies we hit rule end ATN state
     *  and did not follow it despite needing to.
     */
    //let EPSILON : Int = -2;

    //let MIN_USER_TOKEN_TYPE : Int = 1;

    //let EOF : Int = IntStream.EOF;

    /** All tokens go to the parser (unless skip() is called in that rule)
     *  on a particular "channel".  The parser tunes to a particular channel
     *  so that whitespace etc... can go to the parser on a "hidden" channel.
     */
    //let DEFAULT_CHANNEL : Int = 0;

    /** Anything on different channel than DEFAULT_CHANNEL is not parsed
     *  by parser.
     */
    //let HIDDEN_CHANNEL : Int = 1;

    /**
     * This is the minimum constant value which can be assigned to a
     * user-defined token channel.
     *
     * <p>
     * The non-negative numbers less than {@link #MIN_USER_CHANNEL_VALUE} are
     * assigned to the predefined channels {@link #DEFAULT_CHANNEL} and
     * {@link #HIDDEN_CHANNEL}.</p>
     *
     * @see org.antlr.v4.runtime.Token#getChannel()
     */
    //let MIN_USER_CHANNEL_VALUE : Int = 2;

    /**
     * Get the text of the token.
     */
    func getText() -> String?

    /** Get the token type of the token */
    func getType() -> Int

    /** The line number on which the 1st character of this token was matched,
     *  line=1..n
     */
    func getLine() -> Int

    /** The index of the first character of this token relative to the
     *  beginning of the line at which it occurs, 0..n-1
     */
    func getCharPositionInLine() -> Int

    /** Return the channel this token. Each token can arrive at the parser
     *  on a different channel, but the parser only "tunes" to a single channel.
     *  The parser ignores everything not on DEFAULT_CHANNEL.
     */
    func getChannel() -> Int

    /** An index from 0..n-1 of the token object in the input stream.
     *  This must be valid in order to print token streams and
     *  use TokenRewriteStream.
     *
     *  Return -1 to indicate that this token was conjured up since
     *  it doesn't have a valid index.
     */
    func getTokenIndex() -> Int

    /** The starting character index of the token
     *  This method is optional; return -1 if not implemented.
     */
    func getStartIndex() -> Int

    /** The last character index of the token.
     *  This method is optional; return -1 if not implemented.
     */
    func getStopIndex() -> Int

    /** Gets the {@link org.antlr.v4.runtime.TokenSource} which created this token.
     */
    func getTokenSource() -> TokenSource?

    /**
     * Gets the {@link org.antlr.v4.runtime.CharStream} from which this token was derived.
     */
    func getInputStream() -> CharStream?

    var visited: Bool { get set }
}
