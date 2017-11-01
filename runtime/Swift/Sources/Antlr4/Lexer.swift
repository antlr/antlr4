/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// A lexer is recognizer that draws input symbols from a character stream.
/// lexer grammars result in a subclass of this object. A Lexer object
/// uses simplified match() and error recovery mechanisms in the interest
/// of speed.
/// 

import Foundation

open class Lexer: Recognizer<LexerATNSimulator>, TokenSource {
    public static let EOF = -1
    public static let DEFAULT_MODE = 0
    public static let MORE = -2
    public static let SKIP = -3

    public static let DEFAULT_TOKEN_CHANNEL = CommonToken.DEFAULT_CHANNEL
    public static let HIDDEN = CommonToken.HIDDEN_CHANNEL
    public static let MIN_CHAR_VALUE = Character.MIN_VALUE;
    public static let MAX_CHAR_VALUE = Character.MAX_VALUE;

    public var _input: CharStream?
    internal var _tokenFactorySourcePair: TokenSourceAndStream

    /// 
    /// How to create token objects
    /// 
    internal var _factory = CommonTokenFactory.DEFAULT

    /// 
    /// The goal of all lexer rules/methods is to create a token object.
    /// This is an instance variable as multiple rules may collaborate to
    /// create a single token.  nextToken will return this object after
    /// matching lexer rule(s).  If you subclass to allow multiple token
    /// emissions, then set this to the last token to be matched or
    /// something nonnull so that the auto token emit mechanism will not
    /// emit another token.
    /// 
    public var _token: Token?

    /// 
    /// What character index in the stream did the current token start at?
    /// Needed, for example, to get the text for current token.  Set at
    /// the start of nextToken.
    /// 
    public var _tokenStartCharIndex = -1

    /// 
    /// The line on which the first character of the token resides
    /// 
    public var _tokenStartLine = 0

    /// 
    /// The character position of first character within the line
    /// 
    public var _tokenStartCharPositionInLine = 0

    /// 
    /// Once we see EOF on char stream, next token will be EOF.
    /// If you have DONE : EOF ; then you see DONE EOF.
    /// 
    public var _hitEOF = false

    /// 
    /// The channel number for the current token
    /// 
    public var _channel = 0

    /// 
    /// The token type for the current token
    /// 
    public var _type = 0

    public final var _modeStack = Stack<Int>()
    public var _mode = Lexer.DEFAULT_MODE

    /// 
    /// You can set the text for the current token to override what is in
    /// the input char buffer.  Use setText() or can set this instance var.
    /// 
    public var _text: String?

    public override init() {
        self._tokenFactorySourcePair = TokenSourceAndStream()
        super.init()
        self._tokenFactorySourcePair.tokenSource = self
    }

    public required init(_ input: CharStream) {
        self._input = input
        self._tokenFactorySourcePair = TokenSourceAndStream()
        super.init()
        self._tokenFactorySourcePair.tokenSource = self
        self._tokenFactorySourcePair.stream = input
    }

    open func reset() throws {
        // wack Lexer state variables
        if let _input = _input {
            try  _input.seek(0) // rewind the input
        }
        _token = nil
        _type = CommonToken.INVALID_TYPE
        _channel = CommonToken.DEFAULT_CHANNEL
        _tokenStartCharIndex = -1
        _tokenStartCharPositionInLine = -1
        _tokenStartLine = -1
        _text = nil

        _hitEOF = false
        _mode = Lexer.DEFAULT_MODE
        _modeStack.clear()

        getInterpreter().reset()
    }

    /// 
    /// Return a token from this source; i.e., match a token on the char
    /// stream.
    /// 

    open func nextToken() throws -> Token {
        guard let _input = _input else {
            throw ANTLRError.illegalState(msg: "nextToken requires a non-null input stream.")
        }

        // Mark start location in char stream so unbuffered streams are
        // guaranteed at least have text of current token
        var tokenStartMarker = _input.mark()
        defer {
            // make sure we release marker after match or
            // unbuffered char stream will keep buffering
            try! _input.release(tokenStartMarker)
        }
        do {
            outer:
            while true {
                if _hitEOF {
                    emitEOF()
                    return _token!
                }

                _token = nil
                _channel = CommonToken.DEFAULT_CHANNEL
                _tokenStartCharIndex = _input.index()
                _tokenStartCharPositionInLine = getInterpreter().getCharPositionInLine()
                _tokenStartLine = getInterpreter().getLine()
                _text = nil
                repeat {
                    _type = CommonToken.INVALID_TYPE
                    var ttype: Int
                    do {
                        ttype = try getInterpreter().match(_input, _mode)
                    }
                    catch  ANTLRException.recognition(let e) {
                        notifyListeners(e as! LexerNoViableAltException, recognizer: self)
                        try recover(e as! LexerNoViableAltException)
                        ttype = Lexer.SKIP
                    }
                    if try _input.LA(1) == BufferedTokenStream.EOF {
                        _hitEOF = true
                    }
                    if _type == CommonToken.INVALID_TYPE {
                        _type = ttype
                    }
                    if _type == Lexer.SKIP {
                        continue outer
                    }
                } while _type == Lexer.MORE

                if _token == nil {
                    emit()
                }
                return _token!
            }
        }

    }

    /// 
    /// Instruct the lexer to skip creating a token for current lexer rule
    /// and look for another token.  nextToken() knows to keep looking when
    /// a lexer rule finishes with token set to SKIP_TOKEN.  Recall that
    /// if token==null at end of any token rule, it creates one for you
    /// and emits it.
    /// 
    open func skip() {
        _type = Lexer.SKIP
    }

    open func more() {
        _type = Lexer.MORE
    }

    open func mode(_ m: Int) {
        _mode = m
    }

    open func pushMode(_ m: Int) {
        if LexerATNSimulator.debug {
            print("pushMode \(m)")
        }
        _modeStack.push(_mode)
        mode(m)
    }
    @discardableResult
    open func popMode() throws -> Int {
        if _modeStack.isEmpty {
            throw ANTLRError.unsupportedOperation(msg: " EmptyStackException")
        }

        if LexerATNSimulator.debug {
            print("popMode back to \(String(describing: _modeStack.peek()))")
        }
        mode(_modeStack.pop())
        return _mode
    }


    open override func setTokenFactory(_ factory: TokenFactory) {
        self._factory = factory
    }


    open override func getTokenFactory() -> TokenFactory {
        return _factory
    }

    /// 
    /// Set the char stream and reset the lexer
    /// 

    open override func setInputStream(_ input: IntStream) throws {
        self._input = nil
        self._tokenFactorySourcePair = makeTokenSourceAndStream()
        try reset()
        self._input = input as? CharStream
        self._tokenFactorySourcePair = makeTokenSourceAndStream()
    }


    open func getSourceName() -> String {
        return _input!.getSourceName()
    }


    open func getInputStream() -> CharStream? {
        return _input
    }

    /// 
    /// By default does not support multiple emits per nextToken invocation
    /// for efficiency reasons.  Subclass and override this method, nextToken,
    /// and getToken (to push tokens into a list and pull from that list
    /// rather than a single variable as this implementation does).
    /// 
    open func emit(_ token: Token) {
        //System.err.println("emit "+token);
        self._token = token
    }

    /// 
    /// The standard method called to automatically emit a token at the
    /// outermost lexical rule.  The token object should point into the
    /// char buffer start..stop.  If there is a text override in 'text',
    /// use that to set the token's text.  Override this method to emit
    /// custom Token objects or provide a new factory.
    /// 
    @discardableResult
    open func emit() -> Token {
        let t = _factory.create(_tokenFactorySourcePair, _type, _text, _channel, _tokenStartCharIndex, getCharIndex() - 1, _tokenStartLine, _tokenStartCharPositionInLine)
        emit(t)
        return t
    }

    @discardableResult
    open func emitEOF() -> Token {
        let cpos = getCharPositionInLine()
        let line = getLine()
        let idx = _input!.index()
        let eof = _factory.create(
            _tokenFactorySourcePair,
            CommonToken.EOF,
            nil,
            CommonToken.DEFAULT_CHANNEL,
            idx,
            idx - 1,
            line,
            cpos)
        emit(eof)
        return eof
    }


    open func getLine() -> Int {
        return getInterpreter().getLine()
    }


    open func getCharPositionInLine() -> Int {
        return getInterpreter().getCharPositionInLine()
    }

    open func setLine(_ line: Int) {
        getInterpreter().setLine(line)
    }

    open func setCharPositionInLine(_ charPositionInLine: Int) {
        getInterpreter().setCharPositionInLine(charPositionInLine)
    }

    /// 
    /// What is the index of the current character of lookahead?
    /// 
    open func getCharIndex() -> Int {
        return _input!.index()
    }

    /// 
    /// Return the text matched so far for the current token or any
    /// text override.
    /// 
    open func getText() -> String {
        if _text != nil {
            return _text!
        }
        return getInterpreter().getText(_input!)
    }

    /// 
    /// Set the complete text of this token; it wipes any previous
    /// changes to the text.
    /// 
    open func setText(_ text: String) {
        self._text = text
    }

    /// 
    /// Override if emitting multiple tokens.
    /// 
    open func getToken() -> Token {
        return _token!
    }

    open func setToken(_ _token: Token) {
        self._token = _token
    }

    open func setType(_ ttype: Int) {
        _type = ttype
    }

    open func getType() -> Int {
        return _type
    }

    open func setChannel(_ channel: Int) {
        _channel = channel
    }

    open func getChannel() -> Int {
        return _channel
    }

    open func getChannelNames() -> [String]? {
        return nil
    }

    open func getModeNames() -> [String]? {
        return nil
    }

    /// 
    /// Return a list of all Token objects in input char stream.
    /// Forces load of all tokens. Does not include EOF token.
    /// 
    open func getAllTokens() throws -> [Token] {
        var tokens = [Token]()
        var t = try nextToken()
        while t.getType() != CommonToken.EOF {
            tokens.append(t)
            t = try nextToken()
        }
        return tokens
    }

    open func recover(_ e: LexerNoViableAltException) throws {
        if try _input!.LA(1) != BufferedTokenStream.EOF {
            // skip a char and try again
            try getInterpreter().consume(_input!)
        }
    }

    open func notifyListeners<T>(_ e: LexerNoViableAltException, recognizer: Recognizer<T>) {

        let text: String
        do {
            text = try _input!.getText(Interval.of(_tokenStartCharIndex, _input!.index()))
        }
        catch {
            text = "<unknown>"
        }
        let msg = "token recognition error at: '\(getErrorDisplay(text))'"

        let listener = getErrorListenerDispatch()
        listener.syntaxError(recognizer, nil, _tokenStartLine, _tokenStartCharPositionInLine, msg, e)
    }

    open func getErrorDisplay(_ s: String) -> String {
        var buf = ""
        for c in s {
            buf += getErrorDisplay(c)
        }
        return buf
    }

    open func getErrorDisplay(_ c: Character) -> String {
        if c.integerValue == CommonToken.EOF {
            return "<EOF>"
        }
        switch c {
        case "\n":
            return "\\n"
        case "\t":
            return "\\t"
        case "\r":
            return "\\r"
        default:
            return String(c)
        }
    }

    open func getCharErrorDisplay(_ c: Character) -> String {
        let s: String = getErrorDisplay(c)
        return "'\(s)'"
    }

    /// 
    /// Lexers can normally match any char in it's vocabulary after matching
    /// a token, so do the easy thing and just kill a character and hope
    /// it all works out.  You can instead use the rule invocation stack
    /// to do sophisticated error recovery if you are in a fragment rule.
    /// 
    open func recover(_ re: AnyObject) throws {
        // TODO: Do we lose character or line position information?
        try _input!.consume()
    }

    internal func makeTokenSourceAndStream() -> TokenSourceAndStream {
        return TokenSourceAndStream(self, _input)
    }
}
