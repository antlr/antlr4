/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

/// 
/// Provides an implementation of _org.antlr.v4.runtime.TokenSource_ as a wrapper around a list
/// of _org.antlr.v4.runtime.Token_ objects.
/// 
/// If the final token in the list is an _org.antlr.v4.runtime.Token#EOF_ token, it will be used
/// as the EOF token for every call to _#nextToken_ after the end of the
/// list is reached. Otherwise, an EOF token will be created.
/// 

public class ListTokenSource: TokenSource {
    /// 
    /// The wrapped collection of _org.antlr.v4.runtime.Token_ objects to return.
    /// 
    internal final var tokens: [Token]

    /// 
    /// The name of the input source. If this value is `null`, a call to
    /// _#getSourceName_ should return the source name used to create the
    /// the next token in _#tokens_ (or the previous token if the end of
    /// the input has been reached).
    /// 
    private final var sourceName: String?

    /// 
    /// The index into _#tokens_ of token to return by the next call to
    /// _#nextToken_. The end of the input is indicated by this value
    /// being greater than or equal to the number of items in _#tokens_.
    /// 
    internal var i = 0

    /// 
    /// This field caches the EOF token for the token source.
    /// 
    internal var eofToken: Token?

    /// 
    /// This is the backing field for _#getTokenFactory_ and
    /// _setTokenFactory_.
    /// 
    private var _factory = CommonTokenFactory.DEFAULT

    /// 
    /// Constructs a new _org.antlr.v4.runtime.ListTokenSource_ instance from the specified
    /// collection of _org.antlr.v4.runtime.Token_ objects.
    /// 
    /// - parameter tokens: The collection of _org.antlr.v4.runtime.Token_ objects to provide as a
    /// _org.antlr.v4.runtime.TokenSource_.
    /// 
    public convenience init(_ tokens: [Token]) {
        self.init(tokens, nil)
    }

    /// 
    /// Constructs a new _org.antlr.v4.runtime.ListTokenSource_ instance from the specified
    /// collection of _org.antlr.v4.runtime.Token_ objects and source name.
    /// 
    /// - parameter tokens: The collection of _org.antlr.v4.runtime.Token_ objects to provide as a
    /// _org.antlr.v4.runtime.TokenSource_.
    /// - parameter sourceName: The name of the _org.antlr.v4.runtime.TokenSource_. If this value is
    /// `null`, _#getSourceName_ will attempt to infer the name from
    /// the next _org.antlr.v4.runtime.Token_ (or the previous token if the end of the input has
    /// been reached).
    /// 
    public init(_ tokens: [Token], _ sourceName: String?) {
        self.tokens = tokens
        self.sourceName = sourceName
    }

    public func getCharPositionInLine() -> Int {
        if i < tokens.count {
            return tokens[i].getCharPositionInLine()
        }
        else if let eofToken = eofToken {
            return eofToken.getCharPositionInLine()
        }
        else if !tokens.isEmpty {
            // have to calculate the result from the line/column of the previous
            // token, along with the text of the token.
            let lastToken = tokens.last!

            if let tokenText = lastToken.getText() {
                if let lastNewLine = tokenText.lastIndex(of: "\n") {
                    return tokenText.distance(from: lastNewLine, to: tokenText.endIndex) - 1
                }
            }
            return (lastToken.getCharPositionInLine() +
                    lastToken.getStopIndex() -
                    lastToken.getStartIndex() + 1)
        }
        else {
            // only reach this if tokens is empty, meaning EOF occurs at the first
            // position in the input
            return 0
        }
    }

    public func nextToken() -> Token {
        if i >= tokens.count {
            if eofToken == nil {
                var start = -1
                if tokens.count > 0 {
                    let previousStop = tokens[tokens.count - 1].getStopIndex()
                    if previousStop != -1 {
                        start = previousStop + 1
                    }
                }

                let stop = max(-1, start - 1)
                let source = TokenSourceAndStream(self, getInputStream())
                eofToken = _factory.create(source, CommonToken.EOF, "EOF", CommonToken.DEFAULT_CHANNEL, start, stop, getLine(), getCharPositionInLine())
            }

            return eofToken!
        }

        let t = tokens[i]
        if i == tokens.count - 1 && t.getType() == CommonToken.EOF {
            eofToken = t
        }

        i += 1
        return t
    }

    public func getLine() -> Int {
        if i < tokens.count {
            return tokens[i].getLine()
        }
        else if let eofToken = eofToken {
            return eofToken.getLine()
        }
        else if !tokens.isEmpty {
            // have to calculate the result from the line/column of the previous
            // token, along with the text of the token.
            let lastToken = tokens.last!
            var line = lastToken.getLine()

            if let tokenText = lastToken.getText() {
                for c in tokenText {
                    if c == "\n" {
                        line += 1
                    }
                }
            }

            // if no text is available, assume the token did not contain any newline characters.
            return line
        }
        else {
            // only reach this if tokens is empty, meaning EOF occurs at the first
            // position in the input
            return 1
        }
    }

    public func getInputStream() -> CharStream? {
        if i < tokens.count {
            return tokens[i].getInputStream()
        }
        else if let eofToken = eofToken {
            return eofToken.getInputStream()
        }
        else if !tokens.isEmpty {
            return tokens.last!.getInputStream()
        }

        // no input stream information is available
        return nil
    }

    public func getSourceName() -> String {
        if let sourceName = sourceName {
            return sourceName
        }

        if let inputStream = getInputStream() {
            return inputStream.getSourceName()
        }

        return "List"
    }

    public func setTokenFactory(_ factory: TokenFactory) {
        self._factory = factory
    }

    public func getTokenFactory() -> TokenFactory {
        return _factory
    }
}
