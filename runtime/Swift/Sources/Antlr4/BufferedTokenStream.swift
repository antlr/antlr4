/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// This implementation of _org.antlr.v4.runtime.TokenStream_ loads tokens from a
/// _org.antlr.v4.runtime.TokenSource_ on-demand, and places the tokens in a buffer to provide
/// access to any previous token by index.
/// 
/// 
/// This token stream ignores the value of _org.antlr.v4.runtime.Token#getChannel_. If your
/// parser requires the token stream filter tokens to only those on a particular
/// channel, such as _org.antlr.v4.runtime.Token#DEFAULT_CHANNEL_ or
/// _org.antlr.v4.runtime.Token#HIDDEN_CHANNEL_, use a filtering token stream such a
/// _org.antlr.v4.runtime.CommonTokenStream_.
///

public class BufferedTokenStream: TokenStream {
    /// 
    /// The _org.antlr.v4.runtime.TokenSource_ from which tokens for this stream are fetched.
    /// 
    internal var tokenSource: TokenSource

    /// 
    /// A collection of all tokens fetched from the token source. The list is
    /// considered a complete view of the input once _#fetchedEOF_ is set
    /// to `true`.
    /// 
    internal var tokens = [Token]()

    /// 
    /// The index into _#tokens_ of the current token (next token to
    /// _#consume_). _#tokens_`[`_#p_`]` should be
    /// _#LT LT(1)_.
    /// 
    /// This field is set to -1 when the stream is first constructed or when
    /// _#setTokenSource_ is called, indicating that the first token has
    /// not yet been fetched from the token source. For additional information,
    /// see the documentation of _org.antlr.v4.runtime.IntStream_ for a description of
    /// Initializing Methods.
    /// 
    internal var p = -1

    /// 
    /// Indicates whether the _org.antlr.v4.runtime.Token#EOF_ token has been fetched from
    /// _#tokenSource_ and added to _#tokens_. This field improves
    /// performance for the following cases:
    /// 
    /// * _#consume_: The lookahead check in _#consume_ to prevent
    /// consuming the EOF symbol is optimized by checking the values of
    /// _#fetchedEOF_ and _#p_ instead of calling _#LA_.
    /// 
    /// * _#fetch_: The check to prevent adding multiple EOF symbols into
    /// _#tokens_ is trivial with this field.
    /// 
    internal var fetchedEOF = false


    public init(_ tokenSource: TokenSource) {
        self.tokenSource = tokenSource
    }


    public func getTokenSource() -> TokenSource {
        return tokenSource
    }


    public func index() -> Int {
        return p
    }


    public func mark() -> Int {
        return 0
    }

    public func release(_ marker: Int) {
        // no resources to release
    }

    public func reset() throws {
        try seek(0)
    }


    public func seek(_ index: Int) throws {
        try lazyInit()
        p = try adjustSeekIndex(index)
    }


    public func size() -> Int {
        return tokens.count
    }


    public func consume() throws {
        var skipEofCheck: Bool
        if p >= 0 {
            if fetchedEOF {
                // the last token in tokens is EOF. skip check if p indexes any
                // fetched token except the last.
                skipEofCheck = p < tokens.count - 1
            } else {
                // no EOF token in tokens. skip check if p indexes a fetched token.
                skipEofCheck = p < tokens.count
            }
        } else {
            // not yet initialized
            skipEofCheck = false
        }

        if try !skipEofCheck && LA(1) == BufferedTokenStream.EOF {
            throw ANTLRError.illegalState(msg: "cannot consume EOF")
        }

        if try sync(p + 1) {
            p = try adjustSeekIndex(p + 1)
        }
    }

    /// 
    /// Make sure index `i` in tokens has a token.
    /// 
    /// - returns: `true` if a token is located at index `i`, otherwise
    /// `false`.
    /// - seealso: #get(int i)
    /// 
    @discardableResult
    internal func sync(_ i: Int) throws -> Bool {
        assert(i >= 0, "Expected: i>=0")
        let n = i - tokens.count + 1 // how many more elements we need?
        //print("sync("+i+") needs "+n);
        if n > 0 {
            let fetched = try fetch(n)
            return fetched >= n
        }

        return true
    }

    /// 
    /// Add `n` elements to buffer.
    /// 
    /// - returns: The actual number of elements added to the buffer.
    /// 
    internal func fetch(_ n: Int) throws -> Int {
        if fetchedEOF {
            return 0
        }

        for i in 0..<n {
            let t = try tokenSource.nextToken()
            if let wt = t as? WritableToken {
                wt.setTokenIndex(tokens.count)
            }

            tokens.append(t)
            if t.getType() == BufferedTokenStream.EOF {
                fetchedEOF = true
                return i + 1
            }
        }

        return n
    }

    public func get(_ i: Int) throws -> Token {
        if i < 0 || i >= tokens.count {
            throw ANTLRError.indexOutOfBounds(msg: "token index \(i) out of range 0 ..< \(tokens.count)")
        }
        return tokens[i]
    }

    /// 
    /// Get all tokens from start...stop inclusively
    /// 
    public func get(_ start: Int,_ stop: Int) throws -> Array<Token>? {
        var stop = stop
        if start < 0 || stop < 0 {
            return nil
        }
        try lazyInit()
        var subset = [Token]()
        if stop >= tokens.count {
            stop = tokens.count - 1
        }
        for i in start...stop {
            let t = tokens[i]
            if t.getType() == BufferedTokenStream.EOF {
                break
            }
            subset.append(t)
        }
        return subset
    }

    public func LA(_ i: Int) throws -> Int {
        return try LT(i)!.getType()
    }

    internal func LB(_ k: Int) throws -> Token? {
        if (p - k) < 0 {
            return nil
        }
        return tokens[p - k]
    }


    public func LT(_ k: Int) throws -> Token? {
        try lazyInit()
        if k == 0 {
            return nil
        }
        if k < 0 {
            return try LB(-k)
        }

        let i = p + k - 1
        try sync(i)
        if i >= tokens.count {
            // return EOF token
            // EOF must be last token
            return tokens.last!
        }
        return tokens[i]
    }

    /// 
    /// Allowed derived classes to modify the behavior of operations which change
    /// the current stream position by adjusting the target token index of a seek
    /// operation. The default implementation simply returns `i`. If an
    /// exception is thrown in this method, the current stream index should not be
    /// changed.
    /// 
    /// For example, _org.antlr.v4.runtime.CommonTokenStream_ overrides this method to ensure that
    /// the seek target is always an on-channel token.
    /// 
    /// - parameter i: The target token index.
    /// - returns: The adjusted target token index.
    /// 
    internal func adjustSeekIndex(_ i: Int) throws -> Int {
        return i
    }

    internal final func lazyInit() throws {
        if p == -1 {
            try setup()
        }
    }

    internal func setup() throws {
        try sync(0)
        p = try adjustSeekIndex(0)
    }

    /// 
    /// Reset this token stream by setting its token source.
    /// 
    public func setTokenSource(_ tokenSource: TokenSource) {
        self.tokenSource = tokenSource
        tokens.removeAll()
        p = -1
        fetchedEOF = false
    }

    public func getTokens() -> [Token] {
        return tokens
    }

    public func getTokens(_ start: Int, _ stop: Int) throws -> [Token]? {
        return try getTokens(start, stop, nil)
    }

    /// 
    /// Given a start and stop index, return a List of all tokens in
    /// the token type BitSet.  Return null if no tokens were found.  This
    /// method looks at both on and off channel tokens.
    /// 
    public func getTokens(_ start: Int, _ stop: Int, _ types: Set<Int>?) throws -> [Token]? {
        try lazyInit()
        if start < 0 || start >= tokens.count ||
            stop < 0 || stop >= tokens.count {
            throw ANTLRError.indexOutOfBounds(msg: "start \(start) or stop \(stop) not in 0 ..< \(tokens.count)")

        }
        if start > stop {
            return nil
        }

        var filteredTokens = [Token]()
        for i in start...stop {
            let t = tokens[i]
            if let types = types, !types.contains(t.getType()) {
            }
            else {
                filteredTokens.append(t)
            }
        }
        if filteredTokens.isEmpty {
            return nil
        }
        return filteredTokens
    }

    public func getTokens(_ start: Int, _ stop: Int, _ ttype: Int) throws -> [Token]? {
        var s = Set<Int>()
        s.insert(ttype)
        return try getTokens(start, stop, s)
    }

    /// 
    /// Given a starting index, return the index of the next token on channel.
    /// Return `i` if `tokens[i]` is on channel. Return the index of
    /// the EOF token if there are no tokens on channel between `i` and
    /// EOF.
    /// 
    internal func nextTokenOnChannel(_ i: Int, _ channel: Int) throws -> Int {
        var i = i
        try sync(i)
        if i >= size() {
            return size() - 1
        }

        var token = tokens[i]
        while token.getChannel() != channel {
            if token.getType() == BufferedTokenStream.EOF {
                return i
            }

            i += 1
            try sync(i)
            token = tokens[i]
        }

        return i
    }

    /// 
    /// Given a starting index, return the index of the previous token on
    /// channel. Return `i` if `tokens[i]` is on channel. Return -1
    /// if there are no tokens on channel between `i` and 0.
    /// 
    /// 
    /// If `i` specifies an index at or after the EOF token, the EOF token
    /// index is returned. This is due to the fact that the EOF token is treated
    /// as though it were on every channel.
    /// 
    internal func previousTokenOnChannel(_ i: Int, _ channel: Int) throws -> Int {
        var i = i
        try sync(i)
        if i >= size() {
            // the EOF token is on every channel
            return size() - 1
        }

        while i >= 0 {
            let token = tokens[i]
            if token.getType() == BufferedTokenStream.EOF || token.getChannel() == channel {
                return i
            }

            i -= 1
        }

        return i
    }

    /// 
    /// Collect all tokens on specified channel to the right of
    /// the current token up until we see a token on DEFAULT_TOKEN_CHANNEL or
    /// EOF. If channel is -1, find any non default channel token.
    /// 
    public func getHiddenTokensToRight(_ tokenIndex: Int, _ channel: Int = -1) throws -> [Token]? {
        try lazyInit()
        if tokenIndex < 0 || tokenIndex >= tokens.count {
            throw ANTLRError.indexOutOfBounds(msg: "\(tokenIndex) not in 0 ..< \(tokens.count)")
        }

        let nextOnChannel = try nextTokenOnChannel(tokenIndex + 1, Lexer.DEFAULT_TOKEN_CHANNEL)
        let from = tokenIndex + 1
        let to: Int
        // if none onchannel to right, nextOnChannel=-1 so set to = last token
        if nextOnChannel == -1 {
            to = size() - 1
        }
        else {
            to = nextOnChannel
        }

        return filterForChannel(from, to, channel)
    }

    ///
    /// Collect all tokens on specified channel to the left of
    /// the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
    /// If channel is -1, find any non default channel token.
    /// 
    public func getHiddenTokensToLeft(_ tokenIndex: Int, _ channel: Int = -1) throws -> [Token]? {
        try lazyInit()
        if tokenIndex < 0 || tokenIndex >= tokens.count {
            throw ANTLRError.indexOutOfBounds(msg: "\(tokenIndex) not in 0 ..< \(tokens.count)")
        }

        if tokenIndex == 0 {
            // obviously no tokens can appear before the first token
            return nil
        }

        let prevOnChannel = try previousTokenOnChannel(tokenIndex - 1, Lexer.DEFAULT_TOKEN_CHANNEL)
        if prevOnChannel == tokenIndex - 1 {
            return nil
        }
        // if none onchannel to left, prevOnChannel=-1 then from=0
        let from = prevOnChannel + 1
        let to = tokenIndex - 1
        return filterForChannel(from, to, channel)
    }

    internal func filterForChannel(_ from: Int, _ to: Int, _ channel: Int) -> [Token]? {
        var hidden = [Token]()
        for t in tokens[from...to] {
            if channel == -1 {
                if t.getChannel() != Lexer.DEFAULT_TOKEN_CHANNEL {
                    hidden.append(t)
                }
            } else {
                if t.getChannel() == channel {
                    hidden.append(t)
                }
            }
        }
        if hidden.isEmpty {
            return nil
        }
        return hidden
    }


    public func getSourceName() -> String {
        return tokenSource.getSourceName()
    }

    /// 
    /// Get the text of all tokens in this buffer.
    /// 
    public func getText() throws -> String {
        return try getText(Interval.of(0, size() - 1))
    }

    public func getText(_ interval: Interval) throws -> String {
        let start = interval.a
        if start < 0 {
            return ""
        }
        try fill()
        let stop = min(tokens.count, interval.b + 1)
        var buf = ""
        for t in tokens[start ..< stop] {
            if t.getType() == BufferedTokenStream.EOF {
                break
            }
            buf += t.getText()!
        }
        return buf
    }


    public func getText(_ ctx: RuleContext) throws -> String {
        return try getText(ctx.getSourceInterval())
    }


    public func getText(_ start: Token?, _ stop: Token?) throws -> String {
        if let start = start, let stop = stop {
            return try getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()))
        }

        return ""
    }

    /// 
    /// Get all tokens from lexer until EOF
    /// 
    public func fill() throws {
        try lazyInit()
        let blockSize = 1000
        while true {
            let fetched = try fetch(blockSize)
            if fetched < blockSize {
                return
            }
        }
    }
}
