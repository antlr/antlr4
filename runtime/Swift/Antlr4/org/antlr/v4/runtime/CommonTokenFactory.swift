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
 * This default implementation of {@link org.antlr.v4.runtime.TokenFactory} creates
 * {@link org.antlr.v4.runtime.CommonToken} objects.
 */

public class CommonTokenFactory: TokenFactory {
    /**
     * The default {@link org.antlr.v4.runtime.CommonTokenFactory} instance.
     *
     * <p>
     * This token factory does not explicitly copy token text when constructing
     * tokens.</p>
     */
    public static let DEFAULT: TokenFactory = CommonTokenFactory()

    /**
     * Indicates whether {@link org.antlr.v4.runtime.CommonToken#setText} should be called after
     * constructing tokens to explicitly set the text. This is useful for cases
     * where the input stream might not be able to provide arbitrary substrings
     * of text from the input after the lexer creates a token (e.g. the
     * implementation of {@link org.antlr.v4.runtime.CharStream#getText} in
     * {@link org.antlr.v4.runtime.UnbufferedCharStream} throws an
     * {@link UnsupportedOperationException}). Explicitly setting the token text
     * allows {@link org.antlr.v4.runtime.Token#getText} to be called at any time regardless of the
     * input stream implementation.
     *
     * <p>
     * The default value is {@code false} to avoid the performance and memory
     * overhead of copying text for every token unless explicitly requested.</p>
     */
    internal final var copyText: Bool

    /**
     * Constructs a {@link org.antlr.v4.runtime.CommonTokenFactory} with the specified value for
     * {@link #copyText}.
     *
     * <p>
     * When {@code copyText} is {@code false}, the {@link #DEFAULT} instance
     * should be used instead of constructing a new instance.</p>
     *
     * @param copyText The value for {@link #copyText}.
     */
    public init(_ copyText: Bool) {
        self.copyText = copyText
    }

    /**
     * Constructs a {@link org.antlr.v4.runtime.CommonTokenFactory} with {@link #copyText} set to
     * {@code false}.
     *
     * <p>
     * The {@link #DEFAULT} instance should be used instead of calling this
     * directly.</p>
     */
    public convenience init() {
        self.init(false)
    }


    public func create(_ source: (TokenSource?, CharStream?), _ type: Int, _ text: String?,
                       _ channel: Int, _ start: Int, _ stop: Int,
                       _ line: Int, _ charPositionInLine: Int) -> Token {
        let t: CommonToken = CommonToken(source, type, channel, start, stop)
        t.setLine(line)
        t.setCharPositionInLine(charPositionInLine)
        if text != nil {
            t.setText(text!)
        } else {
            if let cStream = source.1 , copyText {
                t.setText(cStream.getText(Interval.of(start, stop)))
            }
        }

        return t
    }


    public func create(_ type: Int, _ text: String) -> Token {
        return CommonToken(type, text)
    }
}
