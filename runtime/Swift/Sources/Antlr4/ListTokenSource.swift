/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

/// Provides an implementation of {@link org.antlr.v4.runtime.TokenSource} as a wrapper around a list
/// of {@link org.antlr.v4.runtime.Token} objects.
/// 
/// <p>If the final token in the list is an {@link org.antlr.v4.runtime.Token#EOF} token, it will be used
/// as the EOF token for every call to {@link #nextToken} after the end of the
/// list is reached. Otherwise, an EOF token will be created.</p>

public class ListTokenSource: TokenSource {
    /// The wrapped collection of {@link org.antlr.v4.runtime.Token} objects to return.
    internal final var tokens: Array<Token>

    /// The name of the input source. If this value is {@code null}, a call to
    /// {@link #getSourceName} should return the source name used to create the
    /// the next token in {@link #tokens} (or the previous token if the end of
    /// the input has been reached).
    private final var sourceName: String?

    /// The index into {@link #tokens} of token to return by the next call to
    /// {@link #nextToken}. The end of the input is indicated by this value
    /// being greater than or equal to the number of items in {@link #tokens}.
    internal var i: Int = 0

    /// This field caches the EOF token for the token source.
    internal var eofToken: Token?

    /// This is the backing field for {@link #getTokenFactory} and
    /// {@link setTokenFactory}.
    private var _factory: TokenFactory = CommonTokenFactory.DEFAULT

    /// Constructs a new {@link org.antlr.v4.runtime.ListTokenSource} instance from the specified
    /// collection of {@link org.antlr.v4.runtime.Token} objects.
    /// 
    /// - parameter tokens: The collection of {@link org.antlr.v4.runtime.Token} objects to provide as a
    /// {@link org.antlr.v4.runtime.TokenSource}.
    /// -  NullPointerException if {@code tokens} is {@code null}
    public convenience init(_ tokens: Array<Token>) {
        self.init(tokens, nil)
    }

    /// Constructs a new {@link org.antlr.v4.runtime.ListTokenSource} instance from the specified
    /// collection of {@link org.antlr.v4.runtime.Token} objects and source name.
    /// 
    /// - parameter tokens: The collection of {@link org.antlr.v4.runtime.Token} objects to provide as a
    /// {@link org.antlr.v4.runtime.TokenSource}.
    /// - parameter sourceName: The name of the {@link org.antlr.v4.runtime.TokenSource}. If this value is
    /// {@code null}, {@link #getSourceName} will attempt to infer the name from
    /// the next {@link org.antlr.v4.runtime.Token} (or the previous token if the end of the input has
    /// been reached).
    /// 
    /// -  NullPointerException if {@code tokens} is {@code null}
    public init(_ tokens: Array<Token>, _ sourceName: String?) {

        self.tokens = tokens
        self.sourceName = sourceName
    }

    /// {@inheritDoc}

    public func getCharPositionInLine() -> Int {
        if i < tokens.count {
            return tokens[i].getCharPositionInLine()
        } else {
            if let eofToken = eofToken {
              return eofToken.getCharPositionInLine()
            } else {
                if tokens.count > 0 {
                    // have to calculate the result from the line/column of the previous
                    // token, along with the text of the token.
                    let lastToken: Token = tokens[tokens.count - 1]

                    if let tokenText = lastToken.getText() {
                        let lastNewLine: Int = tokenText.lastIndexOf("\n")
                        if lastNewLine >= 0 {
                            return tokenText.length - lastNewLine - 1
                        }
                    }
                    var position = lastToken.getCharPositionInLine()
                    position += lastToken.getStopIndex()
                    position -= lastToken.getStartIndex()
                    position += 1
                    return position
                }
            }
        }

        // only reach this if tokens is empty, meaning EOF occurs at the first
        // position in the input
        return 0
    }

    /// {@inheritDoc}

    public func nextToken() -> Token {
        if i >= tokens.count {
            if eofToken == nil {
                var start: Int = -1
                if tokens.count > 0 {
                    let previousStop: Int = tokens[tokens.count - 1].getStopIndex()
                    if previousStop != -1 {
                        start = previousStop + 1
                    }
                }

                let stop: Int = max(-1, start - 1)
                eofToken = _factory.create((self, getInputStream()!), CommonToken.EOF, "EOF", CommonToken.DEFAULT_CHANNEL, start, stop, getLine(), getCharPositionInLine())
            }

            return eofToken!
        }

        let t: Token = tokens[i]
        if i == tokens.count - 1 && t.getType() == CommonToken.EOF {
            eofToken = t
        }

        i += 1
        return t
    }

    /// {@inheritDoc}

    public func getLine() -> Int {
        if i < tokens.count {
            return tokens[i].getLine()
        } else {
            if let eofToken = eofToken {
                return eofToken.getLine()
            } else {
                if tokens.count > 0 {
                    // have to calculate the result from the line/column of the previous
                    // token, along with the text of the token.
                    let lastToken: Token = tokens[tokens.count - 1]
                    var line: Int = lastToken.getLine()

                    if let tokenText = lastToken.getText() {
                        let length = tokenText.length
                        for j in 0..<length {
                            if String(tokenText[j]) == "\n" {
                                line += 1
                            }
                        }
                    }

                    // if no text is available, assume the token did not contain any newline characters.
                    return line
                }
            }
        }

        // only reach this if tokens is empty, meaning EOF occurs at the first
        // position in the input
        return 1
    }

    /// {@inheritDoc}

    public func getInputStream() -> CharStream? {
        if i < tokens.count {
            return tokens[i].getInputStream()
        } else {
            if let eofToken = eofToken{
                return eofToken.getInputStream()
            } else {
                if tokens.count > 0 {
                    return tokens[tokens.count - 1].getInputStream()
                }
            }
        }

        // no input stream information is available
        return nil
    }

    /// {@inheritDoc}

    public func getSourceName() -> String {
        if sourceName != nil {
            return sourceName!
        }

        if let inputStream = getInputStream() {
            return inputStream.getSourceName()
        }

        return "List"
    }

    /// {@inheritDoc}

    public func setTokenFactory(_ factory: TokenFactory) {
        self._factory = factory
    }

    /// {@inheritDoc}

    public func getTokenFactory() -> TokenFactory {
        return _factory
    }
}
