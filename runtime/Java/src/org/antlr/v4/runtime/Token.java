/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.runtime;

/** A token has properties: text, type, line, character position in the line
 *  (so we can ignore tabs), token channel, index, and source from which
 *  we obtained this token.
 */
public interface Token {
	public static final int INVALID_TYPE = 0;

    /** During lookahead operations, this "token" signifies we hit rule end ATN state
     *  and did not follow it despite needing to.
     */
    public static final int EPSILON = -2;

	public static final int MIN_USER_TOKEN_TYPE = 1;

    public static final int EOF = IntStream.EOF;

	/** All tokens go to the parser (unless skip() is called in that rule)
	 *  on a particular "channel".  The parser tunes to a particular channel
	 *  so that whitespace etc... can go to the parser on a "hidden" channel.
	 */
	public static final int DEFAULT_CHANNEL = 0;

	/** Anything on different channel than DEFAULT_CHANNEL is not parsed
	 *  by parser.
	 */
	public static final int HIDDEN_CHANNEL = 1;

	/**
	 * This is the minimum constant value which can be assigned to a
	 * user-defined token channel.
	 *
	 * <p>
	 * The non-negative numbers less than {@link #MIN_USER_CHANNEL_VALUE} are
	 * assigned to the predefined channels {@link #DEFAULT_CHANNEL} and
	 * {@link #HIDDEN_CHANNEL}.</p>
	 *
	 * @see Token#getChannel()
	 */
	public static final int MIN_USER_CHANNEL_VALUE = 2;

	/**
	 * Get the text of the token.
	 */
	String getText();

	/** Get the token type of the token */
	int getType();

	/** The line number on which the 1st character of this token was matched,
	 *  line=1..n
	 */
	int getLine();

	/** The index of the first character of this token relative to the
	 *  beginning of the line at which it occurs, 0..n-1
	 */
	int getCharPositionInLine();

	/** Return the channel this token. Each token can arrive at the parser
	 *  on a different channel, but the parser only "tunes" to a single channel.
	 *  The parser ignores everything not on DEFAULT_CHANNEL.
	 */
	int getChannel();

	/** An index from 0..n-1 of the token object in the input stream.
	 *  This must be valid in order to print token streams and
	 *  use TokenRewriteStream.
	 *
	 *  Return -1 to indicate that this token was conjured up since
	 *  it doesn't have a valid index.
	 */
	int getTokenIndex();

	/** The starting character index of the token
	 *  This method is optional; return -1 if not implemented.
	 */
	int getStartIndex();

	/** The last character index of the token.
	 *  This method is optional; return -1 if not implemented.
	 */
	int getStopIndex();

	/** Gets the {@link TokenSource} which created this token.
	 */
	TokenSource getTokenSource();

	/**
	 * Gets the {@link CharStream} from which this token was derived.
	 */
	CharStream getInputStream();
}
