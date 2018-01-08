/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// This default implementation of _org.antlr.v4.runtime.TokenFactory_ creates
/// _org.antlr.v4.runtime.CommonToken_ objects.
/// 

public class CommonTokenFactory: TokenFactory {
    /// 
    /// The default _org.antlr.v4.runtime.CommonTokenFactory_ instance.
    /// 
    /// 
    /// This token factory does not explicitly copy token text when constructing
    /// tokens.
    /// 
    public static let DEFAULT: TokenFactory = CommonTokenFactory()

    /// 
    /// Indicates whether _org.antlr.v4.runtime.CommonToken#setText_ should be called after
    /// constructing tokens to explicitly set the text. This is useful for cases
    /// where the input stream might not be able to provide arbitrary substrings
    /// of text from the input after the lexer creates a token (e.g. the
    /// implementation of _org.antlr.v4.runtime.CharStream#getText_ in
    /// _org.antlr.v4.runtime.UnbufferedCharStream_ throws an
    /// _UnsupportedOperationException_). Explicitly setting the token text
    /// allows _org.antlr.v4.runtime.Token#getText_ to be called at any time regardless of the
    /// input stream implementation.
    /// 
    /// 
    /// The default value is `false` to avoid the performance and memory
    /// overhead of copying text for every token unless explicitly requested.
    /// 
    internal final var copyText: Bool

    /// 
    /// Constructs a _org.antlr.v4.runtime.CommonTokenFactory_ with the specified value for
    /// _#copyText_.
    /// 
    /// 
    /// When `copyText` is `false`, the _#DEFAULT_ instance
    /// should be used instead of constructing a new instance.
    /// 
    /// - parameter copyText: The value for _#copyText_.
    /// 
    public init(_ copyText: Bool) {
        self.copyText = copyText
    }

    /// 
    /// Constructs a _org.antlr.v4.runtime.CommonTokenFactory_ with _#copyText_ set to
    /// `false`.
    /// 
    /// 
    /// The _#DEFAULT_ instance should be used instead of calling this
    /// directly.
    /// 
    public convenience init() {
        self.init(false)
    }


    public func create(_ source: TokenSourceAndStream, _ type: Int, _ text: String?,
                       _ channel: Int, _ start: Int, _ stop: Int,
                       _ line: Int, _ charPositionInLine: Int) -> Token {
        let t = CommonToken(source, type, channel, start, stop)
        t.setLine(line)
        t.setCharPositionInLine(charPositionInLine)
        if let text = text {
            t.setText(text)
        }
        else if let cStream = source.stream, copyText {
            t.setText(try! cStream.getText(Interval.of(start, stop)))
        }

        return t
    }


    public func create(_ type: Int, _ text: String) -> Token {
        return CommonToken(type, text)
    }
}
