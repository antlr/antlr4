/// 
/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 
/// 

import Foundation

///
/// This field maps from the serialized ATN string to the deserialized _org.antlr.v4.runtime.atn.ATN_ with
/// bypass alternatives.
///
/// - SeeAlso: `ATNDeserializationOptions.generateRuleBypassTransitions`
///
private var bypassAltsAtnCache = [String: ATN]()

///
/// mutex for bypassAltsAtnCache updates
///
private let bypassAltsAtnCacheMutex = Mutex()


///
/// This is all the parsing support code essentially; most of it is error recovery stuff.
/// 
open class Parser: Recognizer<ParserATNSimulator> {
    public static let EOF = -1
    public static var ConsoleError = true

    public class TraceListener: ParseTreeListener {
        var host: Parser
        init(_ host: Parser) {
            self.host = host
        }

        public func enterEveryRule(_ ctx: ParserRuleContext) throws {
            let ruleName = host.getRuleNames()[ctx.getRuleIndex()]
            let lt1 = try host._input.LT(1)!.getText()!
            print("enter   \(ruleName), LT(1)=\(lt1)")
        }

        public func visitTerminal(_ node: TerminalNode) {
            print("consume \(String(describing: node.getSymbol())) rule \(host.getRuleNames()[host._ctx!.getRuleIndex()])")
        }

        public func visitErrorNode(_ node: ErrorNode) {
        }

        public func exitEveryRule(_ ctx: ParserRuleContext) throws {
            let ruleName = host.getRuleNames()[ctx.getRuleIndex()]
            let lt1 = try host._input.LT(1)!.getText()!
            print("exit    \(ruleName), LT(1)=\(lt1)")
        }
    }

    public class TrimToSizeListener: ParseTreeListener {
        public static let INSTANCE = TrimToSizeListener()

        public func enterEveryRule(_ ctx: ParserRuleContext) {
        }

        public func visitTerminal(_ node: TerminalNode) {
        }

        public func visitErrorNode(_ node: ErrorNode) {
        }

        public func exitEveryRule(_ ctx: ParserRuleContext) {
            // TODO: Print exit info.
        }
    }
    
    /// 
    /// The error handling strategy for the parser. The default value is a new
    /// instance of _org.antlr.v4.runtime.DefaultErrorStrategy_.
    /// 
    /// - SeeAlso: #getErrorHandler
    /// - SeeAlso: #setErrorHandler
    /// 
    public var _errHandler: ANTLRErrorStrategy = DefaultErrorStrategy()

    /// 
    /// The input stream.
    /// 
    /// - SeeAlso: #getInputStream
    /// - SeeAlso: #setInputStream
    /// 
    public var _input: TokenStream!

    internal var _precedenceStack: Stack<Int> = {
        var precedenceStack = Stack<Int>()
        precedenceStack.push(0)
        return precedenceStack
    }()


    /// 
    /// The _org.antlr.v4.runtime.ParserRuleContext_ object for the currently executing rule.
    /// This is always non-null during the parsing process.
    /// 
    public var _ctx: ParserRuleContext? = nil

    /// 
    /// Specifies whether or not the parser should construct a parse tree during
    /// the parsing process. The default value is `true`.
    /// 
    /// - SeeAlso: #getBuildParseTree
    /// - SeeAlso: #setBuildParseTree
    /// 
    internal var _buildParseTrees: Bool = true

    /// 
    /// When _#setTrace_`(true)` is called, a reference to the
    /// _org.antlr.v4.runtime.Parser.TraceListener_ is stored here so it can be easily removed in a
    /// later call to _#setTrace_`(false)`. The listener itself is
    /// implemented as a parser listener so this field is not directly used by
    /// other parser methods.
    /// 
    private var _tracer: TraceListener?

    /// 
    /// The list of _org.antlr.v4.runtime.tree.ParseTreeListener_ listeners registered to receive
    /// events during the parse.
    /// 
    /// - SeeAlso: #addParseListener
    /// 
    public var _parseListeners: Array<ParseTreeListener>?

    /// 
    /// The number of syntax errors reported during parsing. This value is
    /// incremented each time _#notifyErrorListeners_ is called.
    /// 
    internal var _syntaxErrors: Int = 0

    public init(_ input: TokenStream) throws {
        self._input = input
        super.init()
        try setInputStream(input)
    }

    /// reset the parser's state
    public func reset() throws {
        if (getInputStream() != nil) {
            try getInputStream()!.seek(0)
        }
        _errHandler.reset(self)
        _ctx = nil
        _syntaxErrors = 0
        setTrace(false)
        _precedenceStack.clear()
        _precedenceStack.push(0)

        //  getInterpreter();
        if let interpreter = _interp {
            interpreter.reset()
        }
    }

    /// 
    /// Match current input symbol against `ttype`. If the symbol type
    /// matches, _org.antlr.v4.runtime.ANTLRErrorStrategy#reportMatch_ and _#consume_ are
    /// called to complete the match process.
    /// 
    /// If the symbol type does not match,
    /// _org.antlr.v4.runtime.ANTLRErrorStrategy#recoverInline_ is called on the current error
    /// strategy to attempt recovery. If _#getBuildParseTree_ is
    /// `true` and the token index of the symbol returned by
    /// _org.antlr.v4.runtime.ANTLRErrorStrategy#recoverInline_ is -1, the symbol is added to
    /// the parse tree by calling _#createErrorNode(ParserRuleContext, Token)_ then
    /// _ParserRuleContext#addErrorNode(ErrorNode)_.
    /// 
    /// - Parameter ttype: the token type to match
    /// - Throws: org.antlr.v4.runtime.RecognitionException if the current input symbol did not match
    /// `ttype` and the error strategy could not recover from the
    /// mismatched symbol
    /// - Returns: the matched symbol
    /// 
    @discardableResult
    public func match(_ ttype: Int) throws -> Token {
        var t = try getCurrentToken()
        if t.getType() == ttype {
            _errHandler.reportMatch(self)
            try consume()
        } else {
            t = try _errHandler.recoverInline(self)
            if _buildParseTrees && t.getTokenIndex() == -1 {
                // we must have conjured up a new token during single token insertion
                // if it's not the current symbol
                _ctx!.addErrorNode(createErrorNode(parent: _ctx!, t: t))
            }
        }
        return t
    }

    /// 
    /// Match current input symbol as a wildcard. If the symbol type matches
    /// (i.e. has a value greater than 0), _org.antlr.v4.runtime.ANTLRErrorStrategy#reportMatch_
    /// and _#consume_ are called to complete the match process.
    /// 
    /// If the symbol type does not match,
    /// _org.antlr.v4.runtime.ANTLRErrorStrategy#recoverInline_ is called on the current error
    /// strategy to attempt recovery. If _#getBuildParseTree_ is
    /// `true` and the token index of the symbol returned by
    /// _org.antlr.v4.runtime.ANTLRErrorStrategy#recoverInline_ is -1, the symbol is added to
    /// the parse tree by calling _#createErrorNode(ParserRuleContext, Token)_ then
    /// _ParserRuleContext#addErrorNode(ErrorNode)_.
    /// 
    /// - Throws: org.antlr.v4.runtime.RecognitionException if the current input symbol did not match
    /// a wildcard and the error strategy could not recover from the mismatched
    /// symbol
    /// - Returns: the matched symbol
    /// 
    @discardableResult
    public func matchWildcard() throws -> Token {
        var t = try getCurrentToken()
        if t.getType() > 0 {
            _errHandler.reportMatch(self)
            try consume()
        } else {
            t = try _errHandler.recoverInline(self)
            if _buildParseTrees && t.getTokenIndex() == -1 {
                // we must have conjured up a new token during single token insertion
                // if it's not the current symbol
                _ctx!.addErrorNode(createErrorNode(parent: _ctx!, t: t))
            }
        }

        return t
    }

    /// 
    /// Track the _org.antlr.v4.runtime.ParserRuleContext_ objects during the parse and hook
    /// them up using the _org.antlr.v4.runtime.ParserRuleContext#children_ list so that it
    /// forms a parse tree. The _org.antlr.v4.runtime.ParserRuleContext_ returned from the start
    /// rule represents the root of the parse tree.
    /// 
    /// Note that if we are not building parse trees, rule contexts only point
    /// upwards. When a rule exits, it returns the context but that gets garbage
    /// collected if nobody holds a reference. It points upwards but nobody
    /// points at it.
    /// 
    /// When we build parse trees, we are adding all of these contexts to
    /// _org.antlr.v4.runtime.ParserRuleContext#children_ list. Contexts are then not candidates
    /// for garbage collection.
    /// 
    public func setBuildParseTree(_ buildParseTrees: Bool) {
        self._buildParseTrees = buildParseTrees
    }

    /// 
    /// Gets whether or not a complete parse tree will be constructed while
    /// parsing. This property is `true` for a newly constructed parser.
    /// 
    /// - Returns: `true` if a complete parse tree will be constructed while
    /// parsing, otherwise `false`
    /// 
    public func getBuildParseTree() -> Bool {
        return _buildParseTrees
    }

    /// 
    /// Trim the internal lists of the parse tree during parsing to conserve memory.
    /// This property is set to `false` by default for a newly constructed parser.
    /// 
    /// - Parameter trimParseTrees: `true` to trim the capacity of the _org.antlr.v4.runtime.ParserRuleContext#children_
    /// list to its size after a rule is parsed.
    /// 
    public func setTrimParseTree(_ trimParseTrees: Bool) {
        if trimParseTrees {
            if getTrimParseTree() {
                return
            }
            addParseListener(TrimToSizeListener.INSTANCE)
        } else {
            removeParseListener(TrimToSizeListener.INSTANCE)
        }
    }

    /// 
    /// - Returns: `true` if the _org.antlr.v4.runtime.ParserRuleContext#children_ list is trimmed
    /// using the default _org.antlr.v4.runtime.Parser.TrimToSizeListener_ during the parse process.
    /// 
    public func getTrimParseTree() -> Bool {
        return !getParseListeners().filter({ $0 === TrimToSizeListener.INSTANCE }).isEmpty
    }

    public func getParseListeners() -> [ParseTreeListener] {
        return _parseListeners ?? [ParseTreeListener]()
    }

    /// 
    /// Registers `listener` to receive events during the parsing process.
    /// 
    /// To support output-preserving grammar transformations (including but not
    /// limited to left-recursion removal, automated left-factoring, and
    /// optimized code generation), calls to listener methods during the parse
    /// may differ substantially from calls made by
    /// _org.antlr.v4.runtime.tree.ParseTreeWalker#DEFAULT_ used after the parse is complete. In
    /// particular, rule entry and exit events may occur in a different order
    /// during the parse than after the parser. In addition, calls to certain
    /// rule entry methods may be omitted.
    /// 
    /// With the following specific exceptions, calls to listener events are
    /// __deterministic__, i.e. for identical input the calls to listener
    /// methods will be the same.
    /// 
    /// * Alterations to the grammar used to generate code may change the
    /// behavior of the listener calls.
    /// * Alterations to the command line options passed to ANTLR 4 when
    /// generating the parser may change the behavior of the listener calls.
    /// * Changing the version of the ANTLR Tool used to generate the parser
    /// may change the behavior of the listener calls.
    /// 
    /// - Parameter listener: the listener to add
    /// 
    public func addParseListener(_ listener: ParseTreeListener) {
        if _parseListeners == nil {
            _parseListeners = [ParseTreeListener]()
        }

        _parseListeners!.append(listener)
    }

    /// 
    /// Remove `listener` from the list of parse listeners.
    /// 
    /// If `listener` is `null` or has not been added as a parse
    /// listener, this method does nothing.
    /// 
    /// - SeeAlso: #addParseListener
    /// 
    /// - Parameter listener: the listener to remove
    /// 

    public func removeParseListener(_ listener: ParseTreeListener?) {
        if _parseListeners != nil {
            if !_parseListeners!.filter({ $0 === listener }).isEmpty {
                _parseListeners = _parseListeners!.filter({
                    $0 !== listener
                })
                if _parseListeners!.isEmpty {
                    _parseListeners = nil
                }
            }
        }
    }

    /// 
    /// Remove all parse listeners.
    /// 
    /// - SeeAlso: #addParseListener
    /// 
    public func removeParseListeners() {
        _parseListeners = nil
    }

    /// 
    /// Notify any parse listeners of an enter rule event.
    /// 
    /// - SeeAlso: #addParseListener
    /// 
    public func triggerEnterRuleEvent() throws {
        if let _parseListeners = _parseListeners, let _ctx = _ctx {
            for listener: ParseTreeListener in _parseListeners {
                try listener.enterEveryRule(_ctx)
                _ctx.enterRule(listener)
            }
        }
    }

    /// 
    /// Notify any parse listeners of an exit rule event.
    /// 
    /// - SeeAlso: #addParseListener
    /// 
    public func triggerExitRuleEvent() throws {
        // reverse order walk of listeners
        if let _parseListeners = _parseListeners, let _ctx = _ctx {
            var i = _parseListeners.count - 1
            while i >= 0 {
                let listener = _parseListeners[i]
                _ctx.exitRule(listener)
                try listener.exitEveryRule(_ctx)
                i -= 1
            }
        }
    }

    /// 
    /// Gets the number of syntax errors reported during parsing. This value is
    /// incremented each time _#notifyErrorListeners_ is called.
    /// 
    /// - SeeAlso: #notifyErrorListeners
    /// 
    public func getNumberOfSyntaxErrors() -> Int {
        return _syntaxErrors
    }

    override
    open func getTokenFactory() -> TokenFactory {
        return _input.getTokenSource().getTokenFactory()
    }

    /// Tell our token source and error strategy about a new way to create tokens.
    override
    open func setTokenFactory(_ factory: TokenFactory) {
        _input.getTokenSource().setTokenFactory(factory)
    }

    /// 
    /// The ATN with bypass alternatives is expensive to create so we create it
    /// lazily.
    ///
    public func getATNWithBypassAlts() -> ATN {
        let serializedAtn = getSerializedATN()

        return bypassAltsAtnCacheMutex.synchronized {
            if let cachedResult = bypassAltsAtnCache[serializedAtn] {
                return cachedResult
            }

            var opts = ATNDeserializationOptions()
            opts.generateRuleBypassTransitions = true
            let result = try! ATNDeserializer(opts).deserialize(Array(serializedAtn))
            bypassAltsAtnCache[serializedAtn] = result
            return result
        }
    }

    /// 
    /// The preferred method of getting a tree pattern. For example, here's a
    /// sample use:
    /// 
    /// 
    /// ParseTree t = parser.expr();
    /// ParseTreePattern p = parser.compileParseTreePattern("&lt;ID&gt;+0", MyParser.RULE_expr);
    /// ParseTreeMatch m = p.match(t);
    /// String id = m.get("ID");
    /// 
    /// 
    public func compileParseTreePattern(_ pattern: String, _ patternRuleIndex: Int) throws -> ParseTreePattern {
        if let tokenStream = getTokenStream() {
            let tokenSource = tokenStream.getTokenSource()
            if let lexer = tokenSource as? Lexer {
                return try compileParseTreePattern(pattern, patternRuleIndex, lexer)
            }
        }
        throw ANTLRError.unsupportedOperation(msg: "Parser can't discover a lexer to use")
    }

    /// 
    /// The same as _#compileParseTreePattern(String, int)_ but specify a
    /// _org.antlr.v4.runtime.Lexer_ rather than trying to deduce it from this parser.
    /// 
    public func compileParseTreePattern(_ pattern: String, _ patternRuleIndex: Int,
                                        _ lexer: Lexer) throws -> ParseTreePattern {
        let m = ParseTreePatternMatcher(lexer, self)
        return try m.compile(pattern, patternRuleIndex)
    }


    public func getErrorHandler() -> ANTLRErrorStrategy {
        return _errHandler
    }

    public func setErrorHandler(_ handler: ANTLRErrorStrategy) {
        self._errHandler = handler
    }

    override
    open func getInputStream() -> IntStream? {
        return getTokenStream()
    }

    override
    public final func setInputStream(_ input: IntStream) throws {
        try setTokenStream(input as! TokenStream)
    }

    public func getTokenStream() -> TokenStream? {
        return _input
    }

    /// Set the token stream and reset the parser.
    public func setTokenStream(_ input: TokenStream) throws {
        //TODO self._input = nil;
        self._input = nil;
        try reset()
        self._input = input
    }

    /// Match needs to return the current input symbol, which gets put
    /// into the label for the associated token ref; e.g., x=ID.
    /// 

    public func getCurrentToken() throws -> Token {
        return try _input.LT(1)!
    }

    public final func notifyErrorListeners(_ msg: String) {
        let token = try? getCurrentToken()
        notifyErrorListeners(token, msg, nil)
    }

    public func notifyErrorListeners(_ offendingToken: Token?, _ msg: String, _ e: AnyObject?) {
        _syntaxErrors += 1
        var line = -1
        var charPositionInLine = -1
        if let offendingToken = offendingToken {
            line = offendingToken.getLine()
            charPositionInLine = offendingToken.getCharPositionInLine()
        }

        let listener = getErrorListenerDispatch()
        listener.syntaxError(self, offendingToken, line, charPositionInLine, msg, e)
    }

    /// 
    /// Consume and return the |: #getCurrentToken current symbol:|.
    /// 
    /// E.g., given the following input with `A` being the current
    /// lookahead symbol, this function moves the cursor to `B` and returns
    /// `A`.
    /// 
    /// 
    /// A B
    /// ^
    /// 
    /// 
    /// If the parser is not in error recovery mode, the consumed symbol is added
    /// to the parse tree using _ParserRuleContext#addChild(TerminalNode)_, and
    /// _org.antlr.v4.runtime.tree.ParseTreeListener#visitTerminal_ is called on any parse listeners.
    /// If the parser __is__ in error recovery mode, the consumed symbol is
    /// added to the parse tree using _#createErrorNode(ParserRuleContext, Token)_ then
    /// _ParserRuleContext#addErrorNode(ErrorNode)_ and
    /// _org.antlr.v4.runtime.tree.ParseTreeListener#visitErrorNode_ is called on any parse
    /// listeners.
    /// 
    @discardableResult
    public func consume() throws -> Token {
        let o = try getCurrentToken()
        if o.getType() != Parser.EOF {
            try getInputStream()!.consume()
        }
        guard let _ctx = _ctx else {
            return o
        }
        let hasListener = _parseListeners != nil && !_parseListeners!.isEmpty

        if _buildParseTrees || hasListener {
            if _errHandler.inErrorRecoveryMode(self) {
                let node = createErrorNode(parent: _ctx, t: o)
                _ctx.addErrorNode(node)
                if let _parseListeners = _parseListeners {
                    for listener in _parseListeners {
                        listener.visitErrorNode(node)
                    }
                }
            } else {
                let node = createTerminalNode(parent: _ctx, t: o)
                _ctx.addChild(node)
                if let _parseListeners = _parseListeners {
                    for listener in _parseListeners {
                        listener.visitTerminal(node)
                    }
                }
            }
        }
        return o
    }

    /// How to create a token leaf node associated with a parent.
    /// Typically, the terminal node to create is not a function of the parent.
    /// 
    /// - Since: 4.7
    /// 
    public func createTerminalNode(parent: ParserRuleContext, t: Token) -> TerminalNode {
        return TerminalNodeImpl(t)
    }

    /// How to create an error node, given a token, associated with a parent.
    /// Typically, the error node to create is not a function of the parent.
    /// 
    /// - Since: 4.7
    /// 
    public func createErrorNode(parent: ParserRuleContext, t: Token) -> ErrorNode {
        return ErrorNode(t)
    }

    internal func addContextToParseTree() {

        // add current context to parent if we have a parent
        if let parent = _ctx?.parent as? ParserRuleContext {
            parent.addChild(_ctx!)
        }
    }

    /// 
    /// Always called by generated parsers upon entry to a rule. Access field
    /// _#_ctx_ get the current context.
    /// 
    public func enterRule(_ localctx: ParserRuleContext, _ state: Int, _ ruleIndex: Int) throws {
        setState(state)
        _ctx = localctx
        _ctx!.start = try _input.LT(1)
        if _buildParseTrees {
            addContextToParseTree()
        }
    }

    public func exitRule() throws {
        guard let ctx = _ctx else {
            return
        }
        ctx.stop = try _input.LT(-1)
        // trigger event on _ctx, before it reverts to parent
        if _parseListeners != nil {
            try triggerExitRuleEvent()
        }
        setState(ctx.invokingState)
        _ctx = ctx.parent as? ParserRuleContext
    }

    public func enterOuterAlt(_ localctx: ParserRuleContext, _ altNum: Int) throws {
        localctx.setAltNumber(altNum)
        // if we have new localctx, make sure we replace existing ctx
        // that is previous child of parse tree
        if _buildParseTrees && _ctx! !== localctx {
            if let parent = _ctx?.parent as? ParserRuleContext {
                parent.removeLastChild()
                parent.addChild(localctx)
            }
        }
        _ctx = localctx
        if _parseListeners != nil {
            try triggerEnterRuleEvent()
        }
    }

    /// 
    /// Get the precedence level for the top-most precedence rule.
    /// 
    /// - Returns: The precedence level for the top-most precedence rule, or -1 if
    /// the parser context is not nested within a precedence rule.
    /// 
    public final func getPrecedence() -> Int {
        if _precedenceStack.isEmpty {
            return -1
        }

        return _precedenceStack.peek() ?? -1
    }

    /// 
    /// Use
    /// _#enterRecursionRule(org.antlr.v4.runtime.ParserRuleContext, int, int, int)_ instead.
    /// 
    /// 
    /// /@Deprecated
    /// 
    public func enterRecursionRule(_ localctx: ParserRuleContext, _ ruleIndex: Int) throws {
        try enterRecursionRule(localctx, getATN().ruleToStartState[ruleIndex].stateNumber, ruleIndex, 0)
    }

    public func enterRecursionRule(_ localctx: ParserRuleContext, _ state: Int, _ ruleIndex: Int, _ precedence: Int) throws {
        setState(state)
        _precedenceStack.push(precedence)
        _ctx = localctx
        _ctx!.start = try _input.LT(1)
        if _parseListeners != nil {
            try triggerEnterRuleEvent() // simulates rule entry for left-recursive rules
        }
    }

    /// Like _#enterRule_ but for recursive rules.
    /// Make the current context the child of the incoming localctx.
    /// 
    public func pushNewRecursionContext(_ localctx: ParserRuleContext, _ state: Int, _ ruleIndex: Int) throws {
        let previous = _ctx!
        previous.parent = localctx
        previous.invokingState = state
        previous.stop = try _input.LT(-1)

        _ctx = localctx
        _ctx!.start = previous.start
        if _buildParseTrees {
            _ctx!.addChild(previous)
        }

        if _parseListeners != nil {
            try triggerEnterRuleEvent() // simulates rule entry for left-recursive rules
        }
    }

    public func unrollRecursionContexts(_ _parentctx: ParserRuleContext?) throws {
        _precedenceStack.pop()
        _ctx!.stop = try _input.LT(-1)
        let retctx = _ctx! // save current ctx (return value)

        // unroll so _ctx is as it was before call to recursive method
        if _parseListeners != nil {
            while let ctxWrap = _ctx, ctxWrap !== _parentctx {
                try triggerExitRuleEvent()
                _ctx = ctxWrap.parent as? ParserRuleContext
            }
        } else {
            _ctx = _parentctx
        }

        // hook into tree
        retctx.parent = _parentctx

        if _buildParseTrees && _parentctx != nil {
            // add return ctx into invoking rule's tree
            _parentctx!.addChild(retctx)
        }
    }

    public func getInvokingContext(_ ruleIndex: Int) -> ParserRuleContext? {
        var p = _ctx
        while let pWrap = p {
            if pWrap.getRuleIndex() == ruleIndex {
                return pWrap
            }
            p = pWrap.parent as? ParserRuleContext
        }
        return nil
    }

    public func getContext() -> ParserRuleContext? {
        return _ctx
    }

    public func setContext(_ ctx: ParserRuleContext) {
        _ctx = ctx
    }

    override
    open func precpred(_ localctx: RuleContext?, _ precedence: Int) -> Bool {
        return precedence >= _precedenceStack.peek()!
    }

    public func inContext(_ context: String) -> Bool {
        // TODO: useful in parser?
        return false
    }

    /// Given an AmbiguityInfo object that contains information about an
    /// ambiguous decision event, return the list of ambiguous parse trees.
    /// An ambiguity occurs when a specific token sequence can be recognized
    /// in more than one way by the grammar. These ambiguities are detected only
    /// at decision points.
    /// 
    /// The list of trees includes the actual interpretation (that for
    /// the minimum alternative number) and all ambiguous alternatives.
    /// The actual interpretation is always first.
    /// 
    /// This method reuses the same physical input token stream used to
    /// detect the ambiguity by the original parser in the first place.
    /// This method resets/seeks within but does not alter originalParser.
    /// The input position is restored upon exit from this method.
    /// Parsers using a _org.antlr.v4.runtime.UnbufferedTokenStream_ may not be able to
    /// perform the necessary save index() / seek(saved_index) operation.
    /// 
    /// The trees are rooted at the node whose start..stop token indices
    /// include the start and stop indices of this ambiguity event. That is,
    /// the trees returns will always include the complete ambiguous subphrase
    /// identified by the ambiguity event.
    /// 
    /// Be aware that this method does NOT notify error or parse listeners as
    /// it would trigger duplicate or otherwise unwanted events.
    /// 
    /// This uses a temporary ParserATNSimulator and a ParserInterpreter
    /// so we don't mess up any statistics, event lists, etc...
    /// The parse tree constructed while identifying/making ambiguityInfo is
    /// not affected by this method as it creates a new parser interp to
    /// get the ambiguous interpretations.
    /// 
    /// Nodes in the returned ambig trees are independent of the original parse
    /// tree (constructed while identifying/creating ambiguityInfo).
    /// 
    /// - Since: 4.5.1
    /// 
    /// - Parameter originalParser: The parser used to create ambiguityInfo; it
    /// is not modified by this routine and can be either
    /// a generated or interpreted parser. It's token
    /// stream *is* reset/seek()'d.
    /// - Parameter ambiguityInfo:  The information about an ambiguous decision event
    /// for which you want ambiguous parse trees.
    /// - Parameter startRuleIndex: The start rule for the entire grammar, not
    /// the ambiguous decision. We re-parse the entire input
    /// and so we need the original start rule.
    /// 
    /// - Throws: org.antlr.v4.runtime.RecognitionException Throws upon syntax error while matching
    /// ambig input.
    /// - Returns:               The list of all possible interpretations of
    /// the input for the decision in ambiguityInfo.
    /// The actual interpretation chosen by the parser
    /// is always given first because this method
    /// retests the input in alternative order and
    /// ANTLR always resolves ambiguities by choosing
    /// the first alternative that matches the input.
    /// 
    /// 
//	public class func getAmbiguousParseTrees(originalParser : Parser,
//																 _ ambiguityInfo : AmbiguityInfo,
//																 _ startRuleIndex : Int) throws -> Array<ParserRuleContext>  //; RecognitionException
//	{
//		var trees : Array<ParserRuleContext> = Array<ParserRuleContext>();
//		var saveTokenInputPosition : Int = originalParser.getTokenStream().index();
//		//try {
//			// Create a new parser interpreter to parse the ambiguous subphrase
//			var parser : ParserInterpreter;
//			if ( originalParser is ParserInterpreter ) {
//				parser = ParserInterpreter( originalParser as! ParserInterpreter);
//			}
//			else {
//				var serializedAtn : [Character] = ATNSerializer.getSerializedAsChars(originalParser.getATN());
//				var deserialized : ATN = ATNDeserializer().deserialize(serializedAtn);
//				parser = ParserInterpreter(originalParser.getGrammarFileName(),
//											   originalParser.getVocabulary(),
//											    originalParser.getRuleNames() ,
//											   deserialized,
//											   originalParser.getTokenStream());
//			}
//
//			// Make sure that we don't get any error messages from using this temporary parser
//			parser.removeErrorListeners();
//			parser.removeParseListeners();
//			parser.getInterpreter()!.setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
//
//			// get ambig trees
//			var alt : Int = ambiguityInfo.ambigAlts.firstSetBit();
//			while  alt>=0  {
//				// re-parse entire input for all ambiguous alternatives
//				// (don't have to do first as it's been parsed, but do again for simplicity
//				//  using this temp parser.)
//				parser.reset();
//				parser.getTokenStream().seek(0); // rewind the input all the way for re-parsing
//				parser.overrideDecision = ambiguityInfo.decision;
//				parser.overrideDecisionInputIndex = ambiguityInfo.startIndex;
//				parser.overrideDecisionAlt = alt;
//				var t : ParserRuleContext = parser.parse(startRuleIndex);
//				var ambigSubTree : ParserRuleContext =
//					Trees.getRootOfSubtreeEnclosingRegion(t, ambiguityInfo.startIndex, ambiguityInfo.stopIndex)!;
//				trees.append(ambigSubTree);
//				alt = ambiguityInfo.ambigAlts.nextSetBit(alt+1);
//			}
//		//}
//		defer {
//			originalParser.getTokenStream().seek(saveTokenInputPosition);
//		}
//
//		return trees;
//	}

    /// 
    /// Checks whether or not `symbol` can follow the current state in the
    /// ATN. The behavior of this method is equivalent to the following, but is
    /// implemented such that the complete context-sensitive follow set does not
    /// need to be explicitly constructed.
    /// 
    /// 
    /// return getExpectedTokens().contains(symbol);
    /// 
    /// 
    /// - Parameter symbol: the symbol type to check
    /// - Returns: `true` if `symbol` can follow the current state in
    /// the ATN, otherwise `false`.
    /// 
    public func isExpectedToken(_ symbol: Int) -> Bool {
        let atn = getInterpreter().atn
        var ctx: ParserRuleContext? = _ctx
        let s = atn.states[getState()]!
        var following = atn.nextTokens(s)
        if following.contains(symbol) {
            return true
        }
//        System.out.println("following "+s+"="+following);
        if !following.contains(CommonToken.EPSILON) {
            return false
        }

        while let ctxWrap = ctx, ctxWrap.invokingState >= 0 && following.contains(CommonToken.EPSILON) {
            let invokingState = atn.states[ctxWrap.invokingState]!
            let rt = invokingState.transition(0) as! RuleTransition
            following = atn.nextTokens(rt.followState)
            if following.contains(symbol) {
                return true
            }

            ctx = ctxWrap.parent as? ParserRuleContext
        }

        if following.contains(CommonToken.EPSILON) && symbol == CommonToken.EOF {
            return true
        }

        return false
    }

    /// 
    /// Computes the set of input symbols which could follow the current parser
    /// state and context, as given by _#getState_ and _#getContext_,
    /// respectively.
    /// 
    /// - SeeAlso: org.antlr.v4.runtime.atn.ATN#getExpectedTokens(int, org.antlr.v4.runtime.RuleContext)
    /// 
    public func getExpectedTokens() throws -> IntervalSet {
        return try getATN().getExpectedTokens(getState(), getContext()!)
    }


    public func getExpectedTokensWithinCurrentRule() -> IntervalSet {
        let atn = getInterpreter().atn
        let s = atn.states[getState()]!
        return atn.nextTokens(s)
    }

    /// Get a rule's index (i.e., `RULE_ruleName` field) or -1 if not found.
    public func getRuleIndex(_ ruleName: String) -> Int {
        return getRuleIndexMap()[ruleName] ?? -1
    }

    public func getRuleContext() -> ParserRuleContext? {
        return _ctx
    }

    /// Return List&lt;String&gt; of the rule names in your parser instance
    /// leading up to a call to the current rule.  You could override if
    /// you want more details such as the file/line info of where
    /// in the ATN a rule is invoked.
    /// 
    /// This is very useful for error messages.
    /// 
    public func getRuleInvocationStack() -> [String] {
        return getRuleInvocationStack(_ctx)
    }

    public func getRuleInvocationStack(_ p: RuleContext?) -> [String] {
        var p = p
        var ruleNames = getRuleNames()
        var stack = [String]()
        while let pWrap = p {
            // compute what follows who invoked us
            let ruleIndex = pWrap.getRuleIndex()
            if ruleIndex < 0 {
                stack.append("n/a")
            } else {
                stack.append(ruleNames[ruleIndex])
            }
            p = pWrap.parent
        }
        return stack
    }

    /// For debugging and other purposes.
    public func getDFAStrings() -> [String] {
        guard let _interp = _interp else {
            return []
        }
        let vocab = getVocabulary()
        return _interp.decisionToDFA.map {
            $0.toString(vocab)
        }
    }

    /// For debugging and other purposes.
    public func dumpDFA() {
        guard let _interp = _interp else {
            return
        }
        var seenOne = false
        let vocab = getVocabulary()
        for dfa in _interp.decisionToDFA {
            if !dfa.states.isEmpty {
                if seenOne {
                    print("")
                }
                print("Decision \(dfa.decision):")
                print(dfa.toString(vocab), terminator: "")
                seenOne = true
            }
        }
    }

    public func getSourceName() -> String {
        return _input.getSourceName()
    }

    override
    open func getParseInfo() -> ParseInfo? {
        let interp = getInterpreter()
        if let interp = interp as? ProfilingATNSimulator {
            return ParseInfo(interp)
        }
        return nil
    }

    /// 
    /// - Since: 4.3
    /// 
    public func setProfile(_ profile: Bool) {
        let interp = getInterpreter()
        let saveMode = interp.getPredictionMode()
        if profile {
            if !(interp is ProfilingATNSimulator) {
                setInterpreter(ProfilingATNSimulator(self))
            }
        }
        else if interp is ProfilingATNSimulator {
            let sim = ParserATNSimulator(self, getATN(), interp.decisionToDFA, interp.getSharedContextCache())
            setInterpreter(sim)
        }
        getInterpreter().setPredictionMode(saveMode)
    }

    /// During a parse is sometimes useful to listen in on the rule entry and exit
    /// events as well as token matches. This is for quick and dirty debugging.
    /// 
    public func setTrace(_ trace: Bool) {
        if !trace {
            removeParseListener(_tracer)
            _tracer = nil
        } else {
            if _tracer != nil {
                removeParseListener(_tracer!)
            } else {
                _tracer = TraceListener(self)
            }
            addParseListener(_tracer!)
        }
    }

    /// 
    /// Gets whether a _org.antlr.v4.runtime.Parser.TraceListener_ is registered as a parse listener
    /// for the parser.
    /// 
    /// - SeeAlso: #setTrace(boolean)
    /// 
    public func isTrace() -> Bool {
        return _tracer != nil
    }
}
