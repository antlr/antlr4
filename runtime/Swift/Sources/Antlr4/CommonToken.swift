/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



public class CommonToken: WritableToken {
    /// 
    /// This is the backing field for _#getType_ and _#setType_.
    /// 
    internal var type: Int

    /// 
    /// This is the backing field for _#getLine_ and _#setLine_.
    /// 
    internal var line = 0

    /// 
    /// This is the backing field for _#getCharPositionInLine_ and
    /// _#setCharPositionInLine_.
    /// 
    internal var charPositionInLine = -1
    // set to invalid position

    /// 
    /// This is the backing field for _#getChannel_ and
    /// _#setChannel_.
    /// 
    internal var channel = DEFAULT_CHANNEL

    /// 
    /// This is the backing field for _#getTokenSource_ and
    /// _#getInputStream_.
    /// 
    /// 
    /// These properties share a field to reduce the memory footprint of
    /// _org.antlr.v4.runtime.CommonToken_. Tokens created by a _org.antlr.v4.runtime.CommonTokenFactory_ from
    /// the same source and input stream share a reference to the same
    /// _org.antlr.v4.runtime.misc.Pair_ containing these values.
    /// 

    internal let source: TokenSourceAndStream

    /// 
    /// This is the backing field for _#getText_ when the token text is
    /// explicitly set in the constructor or via _#setText_.
    /// 
    /// - seealso: #getText()
    /// 
    internal var text: String?

    /// 
    /// This is the backing field for _#getTokenIndex_ and
    /// _#setTokenIndex_.
    /// 
    internal var index = -1

    /// 
    /// This is the backing field for _#getStartIndex_ and
    /// _#setStartIndex_.
    /// 
    internal var start = 0

    /// 
    /// This is the backing field for _#getStopIndex_ and
    /// _#setStopIndex_.
    /// 
    internal var stop = 0

    /// 
    /// Constructs a new _org.antlr.v4.runtime.CommonToken_ with the specified token type.
    /// 
    /// - parameter type: The token type.
    /// 

    private var _visited: Bool = false

    public init(_ type: Int) {
        self.type = type
        self.source = TokenSourceAndStream.EMPTY
    }

    public init(_ source: TokenSourceAndStream, _ type: Int, _ channel: Int, _ start: Int, _ stop: Int) {
        self.source = source
        self.type = type
        self.channel = channel
        self.start = start
        self.stop = stop
        if let tsource = source.tokenSource {
            self.line = tsource.getLine()
            self.charPositionInLine = tsource.getCharPositionInLine()
        }
    }

    /// 
    /// Constructs a new _org.antlr.v4.runtime.CommonToken_ with the specified token type and
    /// text.
    /// 
    /// - parameter type: The token type.
    /// - parameter text: The text of the token.
    /// 
    public init(_ type: Int, _ text: String?) {
        self.type = type
        self.channel = CommonToken.DEFAULT_CHANNEL
        self.text = text
        self.source = TokenSourceAndStream.EMPTY
    }

    /// 
    /// Constructs a new _org.antlr.v4.runtime.CommonToken_ as a copy of another _org.antlr.v4.runtime.Token_.
    ///
    /// - parameter oldToken: The token to copy.
    /// 
    public init(_ oldToken: Token) {
        type = oldToken.getType()
        line = oldToken.getLine()
        index = oldToken.getTokenIndex()
        charPositionInLine = oldToken.getCharPositionInLine()
        channel = oldToken.getChannel()
        start = oldToken.getStartIndex()
        stop = oldToken.getStopIndex()
        text = oldToken.getText()
        source = oldToken.getTokenSourceAndStream()
    }


    public func getType() -> Int {
        return type
    }


    public func setLine(_ line: Int) {
        self.line = line
    }


    public func getText() -> String? {
        if let text = text {
            return text
        }

        if let input = getInputStream() {
            let n = input.size()
            if start < n && stop < n {
                do {
                    return try input.getText(Interval.of(start, stop))
                }
                catch {
                    return nil
                }
            } else {
                return "<EOF>"
            }
        }

        return nil

    }

    /// 
    /// Explicitly set the text for this token. If {code text} is not
    /// `null`, then _#getText_ will return this value rather than
    /// extracting the text from the input.
    /// 
    /// - parameter text: The explicit text of the token, or `null` if the text
    /// should be obtained from the input along with the start and stop indexes
    /// of the token.
    /// 

    public func setText(_ text: String) {
        self.text = text
    }

    public func getLine() -> Int {
        return line
    }


    public func getCharPositionInLine() -> Int {
        return charPositionInLine
    }


    public func setCharPositionInLine(_ charPositionInLine: Int) {
        self.charPositionInLine = charPositionInLine
    }


    public func getChannel() -> Int {
        return channel
    }


    public func setChannel(_ channel: Int) {
        self.channel = channel
    }


    public func setType(_ type: Int) {
        self.type = type
    }


    public func getStartIndex() -> Int {
        return start
    }

    public func setStartIndex(_ start: Int) {
        self.start = start
    }


    public func getStopIndex() -> Int {
        return stop
    }

    public func setStopIndex(_ stop: Int) {
        self.stop = stop
    }


    public func getTokenIndex() -> Int {
        return index
    }


    public func setTokenIndex(_ index: Int) {
        self.index = index
    }


    public func getTokenSource() -> TokenSource? {
        return source.tokenSource
    }


    public func getInputStream() -> CharStream? {
        return source.stream
    }

    public func getTokenSourceAndStream() -> TokenSourceAndStream {
        return source
    }

    public var description: String {
        return toString(nil)
    }

    public func toString(_ r: Recognizer<ATNSimulator>?) -> String {
        let channelStr = (channel > 0 ? ",channel=\(channel)" : "")

        var txt: String
        if let tokenText = getText() {
            txt = tokenText.replacingOccurrences(of: "\n", with: "\\n")
            txt = txt.replacingOccurrences(of: "\r", with: "\\r")
            txt = txt.replacingOccurrences(of: "\t", with: "\\t")
        } else {
            txt = "<no text>"
        }
        let typeString: String
        if let r = r {
            typeString = r.getVocabulary().getDisplayName(type)
        }
        else {
            typeString = "\(type)"
        }
       return "[@\(getTokenIndex()),\(start):\(stop)='\(txt)',<\(typeString)>\(channelStr),\(line):\(getCharPositionInLine())]"
    }

    public var visited: Bool {
        get {
            return _visited
        }

        set {
            _visited = newValue
        }
    }
}
