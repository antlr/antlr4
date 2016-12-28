/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


public class UnbufferedTokenStream<T>: TokenStream {
    internal var tokenSource: TokenSource

    /**
     * A moving window buffer of the data being scanned. While there's a marker,
     * we keep adding to buffer. Otherwise, {@link #consume consume()} resets so
     * we start filling at index 0 again.
     */
    internal var tokens: [Token]

    /**
     * The number of tokens currently in {@link #tokens tokens}.
     *
     * <p>This is not the buffer capacity, that's {@code tokens.length}.</p>
     */
    internal var n: Int

    /**
     * 0..n-1 index into {@link #tokens tokens} of next token.
     *
     * <p>The {@code LT(1)} token is {@code tokens[p]}. If {@code p == n}, we are
     * out of buffered tokens.</p>
     */
    internal var p: Int = 0

    /**
     * Count up with {@link #mark mark()} and down with
     * {@link #release release()}. When we {@code release()} the last mark,
     * {@code numMarkers} reaches 0 and we reset the buffer. Copy
     * {@code tokens[p]..tokens[n-1]} to {@code tokens[0]..tokens[(n-1)-p]}.
     */
    internal var numMarkers: Int = 0

    /**
     * This is the {@code LT(-1)} token for the current position.
     */
    internal var lastToken: Token!

    /**
     * When {@code numMarkers > 0}, this is the {@code LT(-1)} token for the
     * first token in {@link #tokens}. Otherwise, this is {@code null}.
     */
    internal var lastTokenBufferStart: Token!

    /**
     * Absolute token index. It's the index of the token about to be read via
     * {@code LT(1)}. Goes from 0 to the number of tokens in the entire stream,
     * although the stream size is unknown before the end is reached.
     *
     * <p>This value is used to set the token indexes if the stream provides tokens
     * that implement {@link org.antlr.v4.runtime.WritableToken}.</p>
     */
    internal var currentTokenIndex: Int = 0

    public convenience init(_ tokenSource: TokenSource) throws {
        try self.init(tokenSource, 256)
    }
    //TODO: bufferSize don't be use
    public init(_ tokenSource: TokenSource, _ bufferSize: Int) throws {
        self.tokenSource = tokenSource
        //tokens =   [Token](count: bufferSize, repeatedValue: Token)   ;
        tokens = [Token]()
        n = 0
        try fill(1) // prime the pump
    }


    public func get(_ i: Int) throws -> Token {
        // get absolute index
        let bufferStartIndex: Int = getBufferStartIndex()
        if i < bufferStartIndex || i >= bufferStartIndex + n {
            throw ANTLRError.indexOutOfBounds(msg: "get(\(i)) outside buffer: \(bufferStartIndex)..\(bufferStartIndex + n)")
        }
        return tokens[i - bufferStartIndex]
    }


    public func LT(_ i: Int) throws -> Token? {
        if i == -1 {
            return lastToken
        }

        try sync(i)
        let index: Int = p + i - 1
        if index < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "LT(\(i) gives negative index")
        }

        if index >= n {
            //Token.EOF
            assert(n > 0 && tokens[n - 1].getType() == CommonToken.EOF, "Expected: n>0&&tokens[n-1].getType()==Token.EOF")
            return tokens[n - 1]
        }

        return tokens[index]
    }


    public func LA(_ i: Int) throws -> Int {
        return try  LT(i)!.getType()
    }


    public func getTokenSource() -> TokenSource {
        return tokenSource
    }


    public func getText() -> String {
        return ""
    }


    public func getText(_ ctx: RuleContext) throws -> String {
        return try getText(ctx.getSourceInterval())
    }


    public func getText(_ start: Token?, _ stop: Token?) throws -> String {
        return try getText(Interval.of(start!.getTokenIndex(), stop!.getTokenIndex()))
    }


    public func consume() throws {
        //Token.EOF
        if try  LA(1) == CommonToken.EOF {
            throw ANTLRError.illegalState(msg: "cannot consume EOF")
        }

        // buf always has at least tokens[p==0] in this method due to ctor
        lastToken = tokens[p]   // track last token for LT(-1)

        // if we're at last token and no markers, opportunity to flush buffer
        if p == n - 1 && numMarkers == 0 {
            n = 0
            p = -1 // p++ will leave this at 0
            lastTokenBufferStart = lastToken
        }

        p += 1
        currentTokenIndex += 1
        try sync(1)
    }

    /** Make sure we have 'need' elements from current position {@link #p p}. Last valid
     *  {@code p} index is {@code tokens.length-1}.  {@code p+need-1} is the tokens index 'need' elements
     *  ahead.  If we need 1 element, {@code (p+1-1)==p} must be less than {@code tokens.length}.
     */
    internal func sync(_ want: Int) throws {
        let need: Int = (p + want - 1) - n + 1 // how many more elements we need?
        if need > 0 {
            try fill(need)
        }
    }

    /**
     * Add {@code n} elements to the buffer. Returns the number of tokens
     * actually added to the buffer. If the return value is less than {@code n},
     * then EOF was reached before {@code n} tokens could be added.
     */
    @discardableResult
    internal func fill(_ n: Int) throws -> Int {
        for i in 0..<n {
            if self.n > 0 && tokens[self.n - 1].getType() == CommonToken.EOF {
                return i
            }

            let t: Token = try tokenSource.nextToken()
            add(t)
        }

        return n
    }

    internal func add(_ t: Token) {
        if n >= tokens.count {
            //TODO: array count buffer size
            //tokens = Arrays.copyOf(tokens, tokens.length * 2);
        }

        if t is WritableToken {
            (t as! WritableToken).setTokenIndex(getBufferStartIndex() + n)
        }

        tokens[n] = t
        n += 1
    }

    /**
     * Return a marker that we can release later.
     *
     * <p>The specific marker value used for this class allows for some level of
     * protection against misuse where {@code seek()} is called on a mark or
     * {@code release()} is called in the wrong order.</p>
     */

    public func mark() -> Int {
        if numMarkers == 0 {
            lastTokenBufferStart = lastToken
        }

        let mark: Int = -numMarkers - 1
        numMarkers += 1
        return mark
    }


    public func release(_ marker: Int) throws {
        let expectedMark: Int = -numMarkers
        if marker != expectedMark {
            throw ANTLRError.illegalState(msg: "release() called with an invalid marker.")
        }

        numMarkers -= 1
        if numMarkers == 0 {
            // can we release buffer?
            if p > 0 {
                // Copy tokens[p]..tokens[n-1] to tokens[0]..tokens[(n-1)-p], reset ptrs
                // p is last valid token; move nothing if p==n as we have no valid char
                tokens = Array(tokens[p ... n - 1])
                //System.arraycopy(tokens, p, tokens, 0, n - p); // shift n-p tokens from p to 0
                n = n - p
                p = 0
            }

            lastTokenBufferStart = lastToken
        }
    }


    public func index() -> Int {
        return currentTokenIndex
    }


    public func seek(_ index: Int) throws {
        var index = index
        // seek to absolute index
        if index == currentTokenIndex {
            return
        }

        if index > currentTokenIndex {
            try sync(index - currentTokenIndex)
            index = min(index, getBufferStartIndex() + n - 1)
        }

        let bufferStartIndex: Int = getBufferStartIndex()
        let i: Int = index - bufferStartIndex
        if i < 0 {
            throw ANTLRError.illegalState(msg: "cannot seek to negative index \(index)")

        } else {
            if i >= n {
                throw ANTLRError.unsupportedOperation(msg: "seek to index outside buffer: \(index) not in \(bufferStartIndex)..\(bufferStartIndex + n)")

            }
        }

        p = i
        currentTokenIndex = index
        if p == 0 {
            lastToken = lastTokenBufferStart
        } else {
            lastToken = tokens[p - 1]
        }
    }


    public func size() -> Int {

        RuntimeException("Unbuffered stream cannot know its size")
        fatalError()

    }


    public func getSourceName() -> String {
        return tokenSource.getSourceName()
    }


    public func getText(_ interval: Interval) throws -> String {
        let bufferStartIndex: Int = getBufferStartIndex()
        let bufferStopIndex: Int = bufferStartIndex + tokens.count - 1

        let start: Int = interval.a
        let stop: Int = interval.b
        if start < bufferStartIndex || stop > bufferStopIndex {

            throw ANTLRError.unsupportedOperation(msg: "interval \(interval) not in token buffer window: \(bufferStartIndex)..bufferStopIndex)")

        }

        let a: Int = start - bufferStartIndex
        let b: Int = stop - bufferStartIndex

        let buf: StringBuilder = StringBuilder()
        for i in a...b {
            let t: Token = tokens[i]
            buf.append(t.getText()!)
        }

        return buf.toString()
    }

    internal final func getBufferStartIndex() -> Int {
        return currentTokenIndex - p
    }
}
