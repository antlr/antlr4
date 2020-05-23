/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.BufferedTokenStream;

import antlr.v4.runtime.IllegalStateException;
import antlr.v4.runtime.Lexer;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.TokenSource;
import antlr.v4.runtime.TokenStream;
import antlr.v4.runtime.WritableToken;
import antlr.v4.runtime.misc.Interval;
import std.algorithm: canFind;
import std.array;
import std.conv;
import std.format;
import std.variant;
import std.algorithm;

/**
 * This implementation of {@link TokenStream} loads tokens from a
 * {@link TokenSource} on-demand, and places the tokens in a buffer to provide
 * access to any previous token by index.
 *
 * <p>
 * This token stream ignores the value of {@link Token#getChannel}. If your
 * parser requires the token stream filter tokens to only those on a particular
 * channel, such as {@link Token#DEFAULT_CHANNEL} or
 * {@link Token#HIDDEN_CHANNEL}, use a filtering token stream such a
 * {@link CommonTokenStream}.</p>
 */
class BufferedTokenStream : TokenStream
{

    /**
     * The {@link TokenSource} from which tokens for this stream are fetched.
     */
    protected TokenSource tokenSource;

    /**
     * A collection of all tokens fetched from the token source. The list is
     * considered a complete view of the input once {@link #fetchedEOF} is set
     * to {@code true}.
     */
    protected Token[] tokens;

    /**
     * The index into {@link #tokens} of the current token (next token to
     * {@link #consume}). {@link #tokens}{@code [}{@link #p}{@code ]} should be
     * {@link #LT LT(1)}.
     *
     * <p>This field is set to -1 when the stream is first constructed or when
     * {@link #setTokenSource} is called, indicating that the first token has
     * not yet been fetched from the token source. For additional information,
     * see the documentation of {@link IntStream} for a description of
     * Initializing Methods.</p>
     * @uml
     * @read
     */
    private size_t index_ = size_t.max;

    /**
     * Indicates whether the {@link Token#EOF} token has been fetched from
     * {@link #tokenSource} and added to {@link #tokens}. This field improves
     * performance for the following cases:
     *
     * <ul>
     * <li>{@link #consume}: The lookahead check in {@link #consume} to prevent
     * consuming the EOF symbol is optimized by checking the values of
     * {@link #fetchedEOF} and {@link #p} instead of calling {@link #LA}.</li>
     * <li>{@link #fetch}: The check to prevent adding multiple EOF symbols into
     * {@link #tokens} is trivial with this field.</li>
     * <ul>
     */
    protected bool fetchedEOF;

    public this(TokenSource tokenSource)
    in
    {
        assert (tokenSource !is null, "tokenSource cannot be null");
    }
    do
    {
            this.tokenSource = tokenSource;
    }

    /**
     * @uml
     * @override
     */
    public override TokenSource getTokenSource()
    {
        return tokenSource;
    }

    public int mark()
    {
        return 0;
    }

    public void release(int marker)
    {
        // no resources to release
    }

    public void reset()
    {
        seek(0);
    }

    public void seek(size_t index)
    {
        lazyInit;
        index_ = adjustSeekIndex(index);
    }

    public size_t size()
    {
        return tokens.length;
    }

    public void consume()
    {
        bool skipEofCheck;
        if (cast(int)index_ >= 0) {
            if (fetchedEOF) {
                // the last token in tokens is EOF. skip check if p indexes any
                // fetched token except the last.
                skipEofCheck = cast(int)index_ < cast(int)tokens.length - 1;
            }
            else {
                // no EOF token in tokens. skip check if p indexes a fetched token.
                skipEofCheck = index_ < tokens.length;
            }
        }
        else {
            // not yet initialized
            skipEofCheck = false;
        }

        if (!skipEofCheck && LA(1) == TokenConstantDefinition.EOF) {
            throw new IllegalStateException("cannot consume EOF");
        }

        if (sync(index_ + 1)) {
            index_ = adjustSeekIndex(index_ + 1);
        }
    }

    /**
     * Make sure index {@code i} in tokens has a token.
     *
     * @return {@code true} if a token is located at index {@code i}, otherwise
     *    {@code false}.
     * @see #get(int i)
     */
    protected bool sync(size_t i)
    in
    {
        assert (i >= 0);
    }
    do
    {
        auto n = cast(int)i - cast(int)tokens.length + 1; // how many more elements we need?
        if ( n > 0 ) {
            auto fetched = fetch(n);
            return fetched >= n;
        }
        return true;
    }

    /**
     * Add {@code n} elements to buffer.
     *
     *  @return The actual number of elements added to the buffer.
     */
    protected size_t fetch(size_t n)
    {
        if (fetchedEOF) {
            return 0;
        }
        for (int i = 0; i < n; i++) {
            Token t = tokenSource.nextToken();
            if (cast(WritableToken)t) {
                (cast(WritableToken)t).setTokenIndex(to!int(tokens.length));
            }
            tokens ~= t;
            if (t.getType == TokenConstantDefinition.EOF) {
                fetchedEOF = true;
                return i + 1;
            }
        }
        return n;
    }

    /**
     * @uml
     * @override
     */
    public override Token get(size_t i)
    in
    {
        assert( i >= 0 && i < tokens.length, format("token index %1$s out of range 0..%2$s", i, tokens.length-1));
    }
    do
    {
        return tokens[i];
    }

    /**
     * Get all tokens from start..stop inclusively
     */
    public Token[] get(size_t start, size_t stop)
    {
    if (start < 0 || stop < 0 ) return null;
        lazyInit;
        Token[] subset;
        if (stop >= tokens.length) stop = to!int(tokens.length) - 1;
        for (auto i = start; i <= stop; i++) {
            Token t = tokens[i];
            if (t.getType == TokenConstantDefinition.EOF)
                break;
            subset ~= t;
        }
        return subset;
    }

    public dchar LA(int i)
    {
        return LT(i).getType;
    }

    public Token LB(int k)
    {
        if ((cast(int)index_ - k) < 0)
            return null;
        return tokens[index_ - k];
    }

    /**
     * @uml
     * @override
     */
    public override Token LT(int k)
    {
        lazyInit();
        if (k == 0)
            return null;
        if (k < 0)
            return LB(-k);
        auto i = cast(int)index_ + k - 1;
        sync(i);
        if ( i >= tokens.length ) { // return EOF token
            // EOF must be last token
            return tokens[$-1];
        }
        return tokens[i];
    }

    /**
     * Allowed derived classes to modify the behavior of operations which change
     * the current stream position by adjusting the target token index of a seek
     * operation. The default implementation simply returns {@code i}. If an
     * exception is thrown in this method, the current stream index should not be
     * changed.
     *
     * <p>For example, {@link CommonTokenStream} overrides this method to ensure that
     * the seek target is always an on-channel token.</p>
     *
     *  @param i The target token index.
     *  @return The adjusted target token index.
     */
    protected size_t adjustSeekIndex(size_t i)
    {
        return i;
    }

    protected void lazyInit()
    {
        if (index_ == size_t.max) {
            setup;
        }
    }

    protected void setup()
    {
        sync(0);
        index_ = adjustSeekIndex(0);
    }

    /**
     * Reset this token stream by setting its token source.
     */
    public void setTokenSource(TokenSource tokenSource)
    {
        this.tokenSource = tokenSource;
        tokens.length = 0;
        index_ = size_t.max;
        fetchedEOF = false;
    }

    public Token[] getTokens()
    {
        return tokens;
    }

    public Token[] getTokens(size_t start, size_t stop)
    {
        return getTokens(start, stop, null);
    }

    /**
     * Given a start and stop index, return a List of all tokens in
     * the token type BitSet.  Return null if no tokens were found.  This
     * method looks at both on and off channel tokens.
     */
    public Token[] getTokens(size_t start, size_t stop, int[] types)
    in
    {
        lazyInit();
        assert(start >= 0 && stop < tokens.length &&
               stop > 0  && start < tokens.length,
               format("start %1$s or stop %2$s not in 0..%3$s", start, stop, tokens.length - 1));
    }
    do
    {
            if (start > stop)
                return null;

            // list = tokens[start:stop]:{T t, t.getType() in types}
            Token[] filteredTokens;
            for (auto i = start; i<=stop; i++) {
                Token t = tokens[i];
                if (types is null || types.canFind(t.getType()) ) {
                    filteredTokens ~= t;
                }
            }
            if (filteredTokens.length == 0) {
                filteredTokens = null;
            }
            return filteredTokens;
    }

    public Token[] getTokens(size_t start, size_t stop, int ttype)
    {
        int[] s;
        s ~= ttype;
        return getTokens(start, stop, s);
    }

    /**
     * Given a starting index, return the index of the next token on channel.
     * Return {@code i} if {@code tokens[i]} is on channel. Return the index of
     * the EOF token if there are no tokens on channel between {@code i} and
     * EOF.
     */
    protected size_t nextTokenOnChannel(size_t i, int channel)
    {
        sync(i);
        if (i >= size) {
            return size - 1;
        }

        Token token = tokens[i];
        while (token.getChannel != channel) {
            if (token.getType == TokenConstantDefinition.EOF) {
                return i;
            }

            i++;
            sync(i);
            token = tokens[i];
        }

        return i;
    }

    /**
     * Given a starting index, return the index of the previous token on
     * channel. Return {@code i} if {@code tokens[i]} is on channel. Return -1
     * if there are no tokens on channel between {@code i} and 0.
     *
     * <p>
     * If {@code i} specifies an index at or after the EOF token, the EOF token
     * index is returned. This is due to the fact that the EOF token is treated
     * as though it were on every channel.</p>
     */
    protected size_t previousTokenOnChannel(size_t i, int channel)
    {
        sync(i);
        if (i >= size) {
            // the EOF token is on every channel
            return size() - 1;
        }
        while (i >= 0) {
            Token token = tokens[i];
            if (token.getType() == TokenConstantDefinition.EOF || token.getChannel == channel) {
                return i;
            }
            i--;
        }
        return i;
    }

    /**
     * Collect all tokens on specified channel to the right of
     * the current token up until we see a token on DEFAULT_TOKEN_CHANNEL or
     * EOF. If channel is -1, find any non default channel token.
     */
    public Token[] getHiddenTokensToRight(size_t tokenIndex, int channel)
    in
    {
        lazyInit();
        assert(tokenIndex >= 0 && tokenIndex < tokens.length, format("%1$s not in 0..%2$s", tokenIndex, tokens.length-1));
    }
    do
    {
            auto nextOnChannel =
                nextTokenOnChannel(tokenIndex + 1, Lexer.DEFAULT_TOKEN_CHANNEL);
            size_t to;
            auto from = tokenIndex + 1;
            // if none onchannel to right, nextOnChannel=-1 so set to = last token
            if ( nextOnChannel == -1 ) to = size()-1;
            else to = nextOnChannel;
            return filterForChannel(from, to, channel);
    }

    /**
     * Collect all hidden tokens (any off-default channel) to the right of
     * the current token up until we see a token on DEFAULT_TOKEN_CHANNEL
     * of EOF.
     */
    public Token[] getHiddenTokensToRight(size_t tokenIndex)
    {
        return getHiddenTokensToRight(tokenIndex, -1);
    }

    /**
     * Collect all tokens on specified channel to the left of
     * the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
     * If channel is -1, find any non default channel token.
     */
    public Token[] getHiddenTokensToLeft(size_t tokenIndex, int channel)
    in
    {
        lazyInit();
        assert(tokenIndex >= 0 && tokenIndex < tokens.length, format("%1$s not in 0..%2$s", tokenIndex, tokens.length-1));
    }
    do
    {
        if (tokenIndex == 0) {
            // obviously no tokens can appear before the first token
            return null;
        }
        auto prevOnChannel =
            previousTokenOnChannel(tokenIndex - 1, Lexer.DEFAULT_TOKEN_CHANNEL);
        if ( prevOnChannel == tokenIndex - 1 ) return null;
        // if none onchannel to left, prevOnChannel=-1 then from=0
        auto from = prevOnChannel+1;
        auto to = tokenIndex-1;
        return filterForChannel(from, to, channel);
    }

    /**
     * Collect all hidden tokens (any off-default channel) to the left of
     * the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
     */
    public Token[] getHiddenTokensToLeft(size_t tokenIndex)
    {
        return getHiddenTokensToLeft(tokenIndex, -1);
    }

    /**
     * Collect all hidden tokens (any off-default channel) to the left of
     * the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
     */
    public Token[] filterForChannel(size_t from, size_t to, int channel)
    {
        Token[] hidden;
        for (auto i=from; i<=to; i++) {
            Token t = tokens[i];
            if (channel == -1) {
                if (t.getChannel != Lexer.DEFAULT_TOKEN_CHANNEL)
                    hidden ~= t;
            }
            else {
                if (t.getChannel == channel)
                    hidden ~= t;
            }
        }
        if (hidden.length == 0) return null;
        return hidden;
    }

    public string getSourceName()
    {
        return tokenSource.getSourceName();
    }

    /**
     * @uml
     * @override
     */
    public override Variant getText()
    {
        lazyInit;
        fill;
        return getText(Interval.of(0, to!int(size) - 1));
    }

    /**
     * @uml
     * @override
     */
    public override Variant getText(Interval interval)
    {
        int start = interval.a;
        int stop = interval.b;
        if (start < 0 || stop < 0) {
            Variant v;
            return v;
        }
        fill;
        if (stop >= tokens.length)
            stop = to!int(tokens.length) - 1;

        string buf;
        foreach (t; tokens[start..stop+1]) {
            if (t.getType == TokenConstantDefinition.EOF)
                break;
            buf ~= t.getText.get!string;
        }
        return Variant(buf);
    }

    /**
     * @uml
     * @override
     */
    public override Variant getText(RuleContext ctx)
    {
        return getText(ctx.getSourceInterval());
    }

    /**
     * @uml
     * @override
     */
    public override Variant getText(Token start, Token stop)
    {
        if (start !is null && stop !is null) {
            return getText(Interval.of(to!int(start.getTokenIndex()), to!int(stop.getTokenIndex())));
        }
        Variant v = "";
        return v;
    }

    /**
     * Get all tokens from lexer until EOF
     */
    public void fill()
    {
        lazyInit;
        const int blockSize = 1000;
        while (true) {
            auto fetched = fetch(blockSize);
            if (fetched < blockSize) {
                return;
            }
        }
    }

    public final size_t index()
    {
        return this.index_;
    }

}
