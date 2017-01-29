/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// This default implementation of {@link org.antlr.v4.runtime.TokenFactory} creates
/// {@link org.antlr.v4.runtime.CommonToken} objects.

public class CommonTokenFactory: TokenFactory {
    /// The default {@link org.antlr.v4.runtime.CommonTokenFactory} instance.
    /// 
    /// <p>
    /// This token factory does not explicitly copy token text when constructing
    /// tokens.</p>
    public static let DEFAULT: TokenFactory = CommonTokenFactory()

    /// Indicates whether {@link org.antlr.v4.runtime.CommonToken#setText} should be called after
    /// constructing tokens to explicitly set the text. This is useful for cases
    /// where the input stream might not be able to provide arbitrary substrings
    /// of text from the input after the lexer creates a token (e.g. the
    /// implementation of {@link org.antlr.v4.runtime.CharStream#getText} in
    /// {@link org.antlr.v4.runtime.UnbufferedCharStream} throws an
    /// {@link UnsupportedOperationException}). Explicitly setting the token text
    /// allows {@link org.antlr.v4.runtime.Token#getText} to be called at any time regardless of the
    /// input stream implementation.
    /// 
    /// <p>
    /// The default value is {@code false} to avoid the performance and memory
    /// overhead of copying text for every token unless explicitly requested.</p>
    internal final var copyText: Bool

    /// Constructs a {@link org.antlr.v4.runtime.CommonTokenFactory} with the specified value for
    /// {@link #copyText}.
    /// 
    /// <p>
    /// When {@code copyText} is {@code false}, the {@link #DEFAULT} instance
    /// should be used instead of constructing a new instance.</p>
    /// 
    /// - parameter copyText: The value for {@link #copyText}.
    public init(_ copyText: Bool) {
        self.copyText = copyText
    }

    /// Constructs a {@link org.antlr.v4.runtime.CommonTokenFactory} with {@link #copyText} set to
    /// {@code false}.
    /// 
    /// <p>
    /// The {@link #DEFAULT} instance should be used instead of calling this
    /// directly.</p>
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
