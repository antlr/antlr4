/*
 * Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.CommonTokenStream;

import std.conv;
import std.stdio;
import antlr.v4.runtime.BufferedTokenStream;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.TokenSource;

/**
 * This class extends {@link BufferedTokenStream} with functionality to filter
 * token streams to tokens on a particular channel (tokens where
 * {@link Token#getChannel} returns a particular value).
 * <p>
 * This token stream provides access to all tokens by index or when calling
 * methods like {@link #getText}. The channel filtering is only used for code
 * accessing tokens via the lookahead methods {@link #LA}, {@link #LT}, and
 * {@link #LB}.</p>
 *
 * <p>
 * By default, tokens are placed on the default channel
 * ({@link Token#DEFAULT_CHANNEL}), but may be reassigned by using the
 * {@code ->channel(HIDDEN)} lexer command, or by using an embedded action to
 * call {@link Lexer#setChannel}.
 * </p>
 *
 * <p>
 * Note: lexer rules which use the {@code ->skip} lexer command or call
 * {@link Lexer#skip} do not produce tokens at all, so input text matched by
 * such a rule will not be available as part of the token stream, regardless of
 * channel.</p>
 */
class CommonTokenStream : BufferedTokenStream
{

    /**
     * Specifies the channel to use for filtering tokens.
     *
     * <p>
     * The default value is {@link Token#DEFAULT_CHANNEL}, which matches the
     * default channel assigned to tokens created by the lexer.</p>
     */
    protected int channel = TokenConstantDefinition.DEFAULT_CHANNEL;

    /**
     * Constructs a new {@link CommonTokenStream} using the specified token
     * source and the default token channel ({@link Token#DEFAULT_CHANNEL}).
     *
     *  @param tokenSource The token source.
     */
    public this(TokenSource tokenSource)
    {
        super(tokenSource);
    }

    /**
     * Constructs a new {@link CommonTokenStream} using the specified token
     * source and filtering tokens to the specified channel. Only tokens whose
     * {@link Token#getChannel} matches {@code channel} or have the
     * {@link Token#getType} equal to {@link Token#EOF} will be returned by the
     * token stream lookahead methods.
     *
     * @param tokenSource The token source.
     * @param channel The channel to use for filtering tokens.
     */
    public this(TokenSource tokenSource, int channel)
    {
        this(tokenSource);
        this.channel = channel;
    }

    /**
     * @uml
     * @override
     */
    protected override size_t adjustSeekIndex(size_t i)
    {
        return nextTokenOnChannel(i, channel);
    }

    /**
     * @uml
     * @override
     */
    protected override Token LB(int k)
    {
        if (k == 0 || (index - k) < 0 ) return null;

        auto i = index;
        int n = 1;
        // find k good tokens looking backwards
        while (n <= k && i > 0) {
            // skip off-channel tokens
            i = previousTokenOnChannel(i - 1, channel);
            n++;
        }
        if (i < 0) return null;
        return tokens[i];
    }

    /**
     * @uml
     * @override
     */
    public override Token LT(int k)
    {
        debug
            writefln("enter LT(%s) on channel = %s, p = %s", k, channel, index);
        lazyInit;
        if (k == 0 ) return null;
        if (k < 0) return LB(-k);
        auto i = index;
        int n = 1; // we know tokens[p] is a good one
        // find k good tokens
        while (n < k) {
            // skip off-channel tokens, but make sure to not look past EOF
            if (sync(i + 1)) {
                i = nextTokenOnChannel(i + 1, channel);
            }
            n++;
        }
        //   if ( i>range ) range = i;
        debug
            writefln("enter end LT(%s): %s", i, tokens[i]);
        return tokens[i];
    }

    /**
     * Count EOF just once.
     */
    public int getNumberOfOnChannelTokens()
    {
        int n = 0;
        fill;
        foreach (t; tokens) {
            if (t.getChannel == channel)
                n++;
            if (t.getType == TokenConstantDefinition.EOF)
                break;
        }
        return n;
    }

}
