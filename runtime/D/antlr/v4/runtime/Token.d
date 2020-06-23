/*
 * Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.Token;

import antlr.v4.runtime.IntStream;
import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.TokenSource;
import std.variant;

/**
 * A token has properties: text, type, line, character position in the line
 * (so we can ignore tabs), token channel, index, and source from which
 * we obtained this token.
 */
interface Token
{

    /**
     * Get the text of the token.
     */
    public Variant getText();

    /**
     * Get the token type of the token
     */
    public int getType();

    /**
     * The line number on which the 1st character of this token was matched,
     * line=1..n
     */
    public int getLine();

    /**
     * The index of the first character of this token relative to the
     * beginning of the line at which it occurs, 0..n-1
     */
    public int getCharPositionInLine();

    /**
     * Return the channel this token. Each token can arrive at the parser
     * on a different channel, but the parser only "tunes" to a single channel.
     * The parser ignores everything not on DEFAULT_CHANNEL.
     */
    public int getChannel();

    /**
     * An index from 0..n-1 of the token object in the input stream.
     * This must be valid in order to print token streams and
     * use TokenRewriteStream.
     *
     * Return -1 to indicate that this token was conjured up since
     * it doesn't have a valid index.
     */
    public size_t getTokenIndex();

    public size_t startIndex();

    public size_t stopIndex();

    /**
     * Gets the {@link TokenSource} which created this token.
     */
    public TokenSource getTokenSource();

    /**
     * Gets the {@link CharStream} from which this token was derived.
     */
    public CharStream getInputStream();

}
