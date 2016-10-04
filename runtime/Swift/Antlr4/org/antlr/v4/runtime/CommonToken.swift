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



public class CommonToken: WritableToken {
    /**
     * An empty {@link org.antlr.v4.runtime.misc.Pair} which is used as the default value of
     * {@link #source} for tokens that do not have a source.
     */
    internal static let EMPTY_SOURCE: (TokenSource?, CharStream?) = (nil, nil)

    /**
     * This is the backing field for {@link #getType} and {@link #setType}.
     */
    internal var type: Int

    /**
     * This is the backing field for {@link #getLine} and {@link #setLine}.
     */
    internal var line: Int = 0

    /**
     * This is the backing field for {@link #getCharPositionInLine} and
     * {@link #setCharPositionInLine}.
     */
    internal var charPositionInLine: Int = -1
    // set to invalid position

    /**
     * This is the backing field for {@link #getChannel} and
     * {@link #setChannel}.
     */
    internal var channel: Int = DEFAULT_CHANNEL

    /**
     * This is the backing field for {@link #getTokenSource} and
     * {@link #getInputStream}.
     *
     * <p>
     * These properties share a field to reduce the memory footprint of
     * {@link org.antlr.v4.runtime.CommonToken}. Tokens created by a {@link org.antlr.v4.runtime.CommonTokenFactory} from
     * the same source and input stream share a reference to the same
     * {@link org.antlr.v4.runtime.misc.Pair} containing these values.</p>
     */

    internal var source: (TokenSource?, CharStream?)

    /**
     * This is the backing field for {@link #getText} when the token text is
     * explicitly set in the constructor or via {@link #setText}.
     *
     * @see #getText()
     */
    internal var text: String?

    /**
     * This is the backing field for {@link #getTokenIndex} and
     * {@link #setTokenIndex}.
     */
    internal var index: Int = -1

    /**
     * This is the backing field for {@link #getStartIndex} and
     * {@link #setStartIndex}.
     */
    internal var start: Int = 0

    /**
     * This is the backing field for {@link #getStopIndex} and
     * {@link #setStopIndex}.
     */
    internal var stop: Int = 0

    /**
     * Constructs a new {@link org.antlr.v4.runtime.CommonToken} with the specified token type.
     *
     * @param type The token type.
     */

    private var _visited: Bool = false

    public init(_ type: Int) {
        self.type = type
        self.source = CommonToken.EMPTY_SOURCE
    }

    public init(_ source: (TokenSource?, CharStream?), _ type: Int, _ channel: Int, _ start: Int, _ stop: Int) {
        self.source = source
        self.type = type
        self.channel = channel
        self.start = start
        self.stop = stop
        if let tsource = source.0 {
            self.line = tsource.getLine()
            self.charPositionInLine = tsource.getCharPositionInLine()
        }
    }

    /**
     * Constructs a new {@link org.antlr.v4.runtime.CommonToken} with the specified token type and
     * text.
     *
     * @param type The token type.
     * @param text The text of the token.
     */
    public init(_ type: Int, _ text: String?) {
        self.type = type
        self.channel = CommonToken.DEFAULT_CHANNEL
        self.text = text
        self.source = CommonToken.EMPTY_SOURCE
    }

    /**
     * Constructs a new {@link org.antlr.v4.runtime.CommonToken} as a copy of another {@link org.antlr.v4.runtime.Token}.
     *
     * <p>
     * If {@code oldToken} is also a {@link org.antlr.v4.runtime.CommonToken} instance, the newly
     * constructed token will share a reference to the {@link #text} field and
     * the {@link org.antlr.v4.runtime.misc.Pair} stored in {@link #source}. Otherwise, {@link #text} will
     * be assigned the result of calling {@link #getText}, and {@link #source}
     * will be constructed from the result of {@link org.antlr.v4.runtime.Token#getTokenSource} and
     * {@link org.antlr.v4.runtime.Token#getInputStream}.</p>
     *
     * @param oldToken The token to copy.
     */
    public init(_ oldToken: Token) {
        type = oldToken.getType()
        line = oldToken.getLine()
        index = oldToken.getTokenIndex()
        charPositionInLine = oldToken.getCharPositionInLine()
        channel = oldToken.getChannel()
        start = oldToken.getStartIndex()
        stop = oldToken.getStopIndex()

        if oldToken is CommonToken {
            text = (oldToken as! CommonToken).text
            source = (oldToken as! CommonToken).source
        } else {
            text = oldToken.getText()
            source = (oldToken.getTokenSource(), oldToken.getInputStream())
        }
    }


    public func getType() -> Int {
        return type
    }


    public func setLine(_ line: Int) {
        self.line = line
    }


    public func getText() -> String? {
        if text != nil {
            return text!
        }

        if let input = getInputStream() {
            let n: Int = input.size()
            if start < n && stop < n {
                return input.getText(Interval.of(start, stop))
            } else {
                return "<EOF>"
            }
        }
        
        return nil
        
    }

    /**
     * Explicitly set the text for this token. If {code text} is not
     * {@code null}, then {@link #getText} will return this value rather than
     * extracting the text from the input.
     *
     * @param text The explicit text of the token, or {@code null} if the text
     * should be obtained from the input along with the start and stop indexes
     * of the token.
     */

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
        return source.0
    }


    public func getInputStream() -> CharStream? {
        return source.1
    }

    public var description: String {
        var channelStr: String = ""
        if channel > 0 {
            channelStr = "channel=\(channel)"
        }
        var txt: String
        if let tokenText = getText() {
            txt = tokenText.replaceAll("\n", replacement: "\\n")
            txt = txt.replaceAll("\r", replacement: "\\r")
            txt = txt.replaceAll("\t", replacement: "\\t")
        } else {
            txt = "<no text>"
        }
        let desc: StringBuilder = StringBuilder()
        desc.append("[@\(getTokenIndex()),")
        desc.append("\(start):\(stop)='\(txt)',")
        desc.append("<\(type)>\(channelStr),")
        desc.append("\(line):\(getCharPositionInLine())]")
   
        return desc.toString()
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
