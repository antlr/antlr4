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
 * This class extends {@link org.antlr.v4.runtime.BufferedTokenStream} with functionality to filter
 * token streams to tokens on a particular channel (tokens where
 * {@link org.antlr.v4.runtime.Token#getChannel} returns a particular value).
 *
 * <p>
 * This token stream provides access to all tokens by index or when calling
 * methods like {@link #getText}. The channel filtering is only used for code
 * accessing tokens via the lookahead methods {@link #LA}, {@link #LT}, and
 * {@link #LB}.</p>
 *
 * <p>
 * By default, tokens are placed on the default channel
 * ({@link org.antlr.v4.runtime.Token#DEFAULT_CHANNEL}), but may be reassigned by using the
 * {@code ->channel(HIDDEN)} lexer command, or by using an embedded action to
 * call {@link org.antlr.v4.runtime.Lexer#setChannel}.
 * </p>
 *
 * <p>
 * Note: lexer rules which use the {@code ->skip} lexer command or call
 * {@link org.antlr.v4.runtime.Lexer#skip} do not produce tokens at all, so input text matched by
 * such a rule will not be available as part of the token stream, regardless of
 * channel.</p>
 */

public class CommonTokenStream: BufferedTokenStream {
    /**
     * Specifies the channel to use for filtering tokens.
     *
     * <p>
     * The default value is {@link org.antlr.v4.runtime.Token#DEFAULT_CHANNEL}, which matches the
     * default channel assigned to tokens created by the lexer.</p>
     */
    internal var channel: Int = CommonToken.DEFAULT_CHANNEL

    /**
     * Constructs a new {@link org.antlr.v4.runtime.CommonTokenStream} using the specified token
     * source and the default token channel ({@link org.antlr.v4.runtime.Token#DEFAULT_CHANNEL}).
     *
     * @param tokenSource The token source.
     */
    public override init(_ tokenSource: TokenSource) {
        super.init(tokenSource)
    }

    /**
     * Constructs a new {@link org.antlr.v4.runtime.CommonTokenStream} using the specified token
     * source and filtering tokens to the specified channel. Only tokens whose
     * {@link org.antlr.v4.runtime.Token#getChannel} matches {@code channel} or have the
     * {@link org.antlr.v4.runtime.Token#getType} equal to {@link org.antlr.v4.runtime.Token#EOF} will be returned by the
     * token stream lookahead methods.
     *
     * @param tokenSource The token source.
     * @param channel The channel to use for filtering tokens.
     */
    public convenience init(_ tokenSource: TokenSource, _ channel: Int) {
        self.init(tokenSource)
        self.channel = channel
    }

    override
    internal func adjustSeekIndex(_ i: Int) throws -> Int {
        return try nextTokenOnChannel(i, channel)
    }

    override
    internal func LB(_ k: Int) throws -> Token? {
        if k == 0 || (p - k) < 0 {
            return nil
        }

        var i: Int = p
        var n: Int = 1
        // find k good tokens looking backwards
        while n <= k {
            // skip off-channel tokens
            try i = previousTokenOnChannel(i - 1, channel)
            n += 1
        }
        if i < 0 {
            return nil
        }
        return tokens[i]
    }

    override
    public func LT(_ k: Int) throws -> Token? {
        //System.out.println("enter LT("+k+")");
        try lazyInit()
        if k == 0 {
            return nil
        }
        if k < 0 {
            return try LB(-k)
        }
        var i: Int = p
        var n: Int = 1 // we know tokens[p] is a good one
        // find k good tokens
        while n < k {
            // skip off-channel tokens, but make sure to not look past EOF
            if try sync(i + 1) {
                i = try nextTokenOnChannel(i + 1, channel)
            }
            n += 1
        }
//		if ( i>range ) range = i;
        return tokens[i]
    }

    /** Count EOF just once. */
    public func getNumberOfOnChannelTokens() throws -> Int {
        var n: Int = 0
        try fill()
        let length = tokens.count
        for i in 0..<length {
            let t: Token = tokens[i]
            if t.getChannel() == channel {
                n += 1
            }
            if t.getType() == CommonToken.EOF {
                break
            }
        }
        return n
    }
}
