package antlr4

type IParser interface {
	IRecognizer

	GetInterpreter() *ParserATNSimulator
	GetErrorHandler() IErrorStrategy
	GetTokenStream() TokenStream
	GetTokenFactory() TokenFactory
	GetParserRuleContext() IParserRuleContext
	Consume() *Token
	GetParseListeners() []ParseTreeListener

	GetInputStream() IntStream
	getCurrentToken() *Token
	getExpectedTokens() *IntervalSet
	notifyErrorListeners(msg string, offendingToken *Token, err IRecognitionException)
	isExpectedToken(symbol int) bool
	getPrecedence() int
	getRuleInvocationStack(IParserRuleContext) []string
}

type Parser struct {
	*Recognizer

	Interpreter *ParserATNSimulator

	_input           TokenStream
	_errHandler      IErrorStrategy
	_precedenceStack IntStack
	_ctx             IParserRuleContext
	buildParseTrees  bool
	_tracer          *TraceListener
	_parseListeners  []ParseTreeListener
	_SyntaxErrors    int

}

// p.is all the parsing support code essentially most of it is error
// recovery stuff.//
func NewParser(input TokenStream) *Parser {

	p := new(Parser)


	p.Recognizer = NewRecognizer()

	// The input stream.
	p._input = nil
	// The error handling strategy for the parser. The default value is a new
	// instance of {@link DefaultErrorStrategy}.
	p._errHandler = NewDefaultErrorStrategy()
	p._precedenceStack = make([]int, 0)
	p._precedenceStack.Push(0)
	// The {@link ParserRuleContext} object for the currently executing rule.
	// p.is always non-nil during the parsing process.
	p._ctx = nil
	// Specifies whether or not the parser should construct a parse tree during
	// the parsing process. The default value is {@code true}.
	p.buildParseTrees = true
	// When {@link //setTrace}{@code (true)} is called, a reference to the
	// {@link TraceListener} is stored here so it can be easily removed in a
	// later call to {@link //setTrace}{@code (false)}. The listener itself is
	// implemented as a parser listener so p.field is not directly used by
	// other parser methods.
	p._tracer = nil
	// The list of {@link ParseTreeListener} listeners registered to receive
	// events during the parse.
	p._parseListeners = nil
	// The number of syntax errors Reported during parsing. p.value is
	// incremented each time {@link //notifyErrorListeners} is called.
	p._SyntaxErrors = 0
	p.setInputStream(input)

	return p
}

// p.field maps from the serialized ATN string to the deserialized {@link
// ATN} with
// bypass alternatives.
//
// @see ATNDeserializationOptions//isGenerateRuleBypassTransitions()
//
var bypassAltsAtnCache = make(map[string]int)

// reset the parser's state//
func (p *Parser) reset() {
	if p._input != nil {
		p._input.Seek(0)
	}
	p._errHandler.reset(p)
	p._ctx = nil
	p._SyntaxErrors = 0
	p.setTrace(nil)
	p._precedenceStack = make([]int, 0)
	p._precedenceStack.Push(0)
	if p.Interpreter != nil {
		p.Interpreter.reset()
	}
}

func (p *Parser) GetErrorHandler() IErrorStrategy {
	return p._errHandler
}

func (p *Parser) GetParseListeners() []ParseTreeListener {
	return p._parseListeners
}

// Match current input symbol against {@code ttype}. If the symbol type
// Matches, {@link ANTLRErrorStrategy//ReportMatch} and {@link //consume} are
// called to complete the Match process.
//
// <p>If the symbol type does not Match,
// {@link ANTLRErrorStrategy//recoverInline} is called on the current error
// strategy to attempt recovery. If {@link //getBuildParseTree} is
// {@code true} and the token index of the symbol returned by
// {@link ANTLRErrorStrategy//recoverInline} is -1, the symbol is added to
// the parse tree by calling {@link ParserRuleContext//addErrorNode}.</p>
//
// @param ttype the token type to Match
// @return the Matched symbol
// @panics RecognitionException if the current input symbol did not Match
// {@code ttype} and the error strategy could not recover from the
// misMatched symbol

func (p *Parser) Match(ttype int) *Token {
	var t = p.getCurrentToken()
	if t.tokenType == ttype {
		p._errHandler.ReportMatch(p)
		p.Consume()
	} else {
		t = p._errHandler.RecoverInline(p)
		if p.buildParseTrees && t.tokenIndex == -1 {
			// we must have conjured up a Newtoken during single token
			// insertion
			// if it's not the current symbol
			p._ctx.addErrorNode(t)
		}
	}
	return t
}

// Match current input symbol as a wildcard. If the symbol type Matches
// (i.e. has a value greater than 0), {@link ANTLRErrorStrategy//ReportMatch}
// and {@link //consume} are called to complete the Match process.
//
// <p>If the symbol type does not Match,
// {@link ANTLRErrorStrategy//recoverInline} is called on the current error
// strategy to attempt recovery. If {@link //getBuildParseTree} is
// {@code true} and the token index of the symbol returned by
// {@link ANTLRErrorStrategy//recoverInline} is -1, the symbol is added to
// the parse tree by calling {@link ParserRuleContext//addErrorNode}.</p>
//
// @return the Matched symbol
// @panics RecognitionException if the current input symbol did not Match
// a wildcard and the error strategy could not recover from the misMatched
// symbol

func (p *Parser) MatchWildcard() *Token {
	var t = p.getCurrentToken()
	if t.tokenType > 0 {
		p._errHandler.ReportMatch(p)
		p.Consume()
	} else {
		t = p._errHandler.RecoverInline(p)
		if p.buildParseTrees && t.tokenIndex == -1 {
			// we must have conjured up a Newtoken during single token
			// insertion
			// if it's not the current symbol
			p._ctx.addErrorNode(t)
		}
	}
	return t
}

func (p *Parser) GetParserRuleContext() IParserRuleContext {
	return p._ctx
}

func (p *Parser) getParseListeners() []ParseTreeListener {
	if p._parseListeners == nil {
		return make([]ParseTreeListener, 0)
	}
	return p._parseListeners
}

// Registers {@code listener} to receive events during the parsing process.
//
// <p>To support output-preserving grammar transformations (including but not
// limited to left-recursion removal, automated left-factoring, and
// optimized code generation), calls to listener methods during the parse
// may differ substantially from calls made by
// {@link ParseTreeWalker//DEFAULT} used after the parse is complete. In
// particular, rule entry and exit events may occur in a different order
// during the parse than after the parser. In addition, calls to certain
// rule entry methods may be omitted.</p>
//
// <p>With the following specific exceptions, calls to listener events are
// <em>deterministic</em>, i.e. for identical input the calls to listener
// methods will be the same.</p>
//
// <ul>
// <li>Alterations to the grammar used to generate code may change the
// behavior of the listener calls.</li>
// <li>Alterations to the command line options passed to ANTLR 4 when
// generating the parser may change the behavior of the listener calls.</li>
// <li>Changing the version of the ANTLR Tool used to generate the parser
// may change the behavior of the listener calls.</li>
// </ul>
//
// @param listener the listener to add
//
// @panics nilPointerException if {@code} listener is {@code nil}
//
func (p *Parser) addParseListener(listener ParseTreeListener) {
	if listener == nil {
		panic("listener")
	}
	if p._parseListeners == nil {
		p._parseListeners = make([]ParseTreeListener, 0)
	}
	p._parseListeners = append(p._parseListeners, listener)
}

//
// Remove {@code listener} from the list of parse listeners.
//
// <p>If {@code listener} is {@code nil} or has not been added as a parse
// listener, p.method does nothing.</p>
// @param listener the listener to remove
//
func (p *Parser) removeParseListener(listener ParseTreeListener) {

	if (p._parseListeners != nil) {

		idx := -1
		for i,v := range p._parseListeners {
			if v == listener {
				idx = i
				break;
			}
		}

		if (idx == -1){
			return
		}

		// remove the listener from the slice
		p._parseListeners = append( p._parseListeners[0:idx], p._parseListeners[idx+1:]... )

		if (len(p._parseListeners) == 0) {
			p._parseListeners = nil
		}
	}
}

// Remove all parse listeners.
func (p *Parser) removeParseListeners() {
	p._parseListeners = nil
}

// Notify any parse listeners of an enter rule event.
func (p *Parser) TriggerEnterRuleEvent() {
	if p._parseListeners != nil {
		var ctx = p._ctx
		for _, listener := range p._parseListeners {
			listener.EnterEveryRule(ctx)
			ctx.EnterRule(listener)
		}
	}
}

//
// Notify any parse listeners of an exit rule event.
//
// @see //addParseListener
//
func (p *Parser) TriggerExitRuleEvent() {
	if p._parseListeners != nil {
		// reverse order walk of listeners
		ctx := p._ctx
		l := len(p._parseListeners) - 1

		for i := range p._parseListeners {
			listener := p._parseListeners[l-i]
			ctx.ExitRule(listener)
			listener.ExitEveryRule(ctx)
		}
	}
}

func (this *Parser) GetInterpreter() *ParserATNSimulator {
	return this.Interpreter
}

func (this *Parser) GetATN() *ATN {
	return this.Interpreter.atn
}

func (p *Parser) GetTokenFactory() TokenFactory {
	return p._input.GetTokenSource().GetTokenFactory()
}

// Tell our token source and error strategy about a Newway to create tokens.//
func (p *Parser) setTokenFactory(factory TokenFactory) {
	p._input.GetTokenSource().setTokenFactory(factory)
}

// The ATN with bypass alternatives is expensive to create so we create it
// lazily.
//
// @panics UnsupportedOperationException if the current parser does not
// implement the {@link //getSerializedATN()} method.
//
func (p *Parser) GetATNWithBypassAlts() {

	// TODO
	panic("Not implemented!")

	//	var serializedAtn = p.getSerializedATN()
	//	if (serializedAtn == nil) {
	//		panic("The current parser does not support an ATN with bypass alternatives.")
	//	}
	//	var result = p.bypassAltsAtnCache[serializedAtn]
	//	if (result == nil) {
	//		var deserializationOptions = NewATNDeserializationOptions(nil)
	//		deserializationOptions.generateRuleBypassTransitions = true
	//		result = NewATNDeserializer(deserializationOptions).deserialize(serializedAtn)
	//		p.bypassAltsAtnCache[serializedAtn] = result
	//	}
	//	return result
}

// The preferred method of getting a tree pattern. For example, here's a
// sample use:
//
// <pre>
// ParseTree t = parser.expr()
// ParseTreePattern p = parser.compileParseTreePattern("&ltID&gt+0",
// MyParser.RULE_expr)
// ParseTreeMatch m = p.Match(t)
// String id = m.Get("ID")
// </pre>

func (p *Parser) compileParseTreePattern(pattern, patternRuleIndex, lexer ILexer) {

	panic("NewParseTreePatternMatcher not implemented!")
	//
	//	if (lexer == nil) {
	//		if (p.GetTokenStream() != nil) {
	//			var tokenSource = p.GetTokenStream().GetTokenSource()
	//			if _, ok := tokenSource.(ILexer); ok {
	//				lexer = tokenSource
	//			}
	//		}
	//	}
	//	if (lexer == nil) {
	//		panic("Parser can't discover a lexer to use")
	//	}

	//	var m = NewParseTreePatternMatcher(lexer, p)
	//	return m.compile(pattern, patternRuleIndex)
}

func (p *Parser) GetInputStream() IntStream {
	return p.GetTokenStream()
}

func (p *Parser) setInputStream(input TokenStream) {
	p.setTokenStream(input)
}

func (p *Parser) GetTokenStream() TokenStream {
	return p._input
}

// Set the token stream and reset the parser.//
func (p *Parser) setTokenStream(input TokenStream) {
	p._input = nil
	p.reset()
	p._input = input
}

// Match needs to return the current input symbol, which gets put
// into the label for the associated token ref e.g., x=ID.
//
func (p *Parser) getCurrentToken() *Token {
	return p._input.LT(1)
}

func (p *Parser) notifyErrorListeners(msg string, offendingToken *Token, err IRecognitionException) {
	if offendingToken == nil {
		offendingToken = p.getCurrentToken()
	}
	p._SyntaxErrors += 1
	var line = offendingToken.line
	var column = offendingToken.column
	listener := p.getErrorListenerDispatch()
	listener.SyntaxError(p, offendingToken, line, column, msg, err)
}

func (p *Parser) Consume() *Token {
	var o = p.getCurrentToken()
	if o.tokenType != TokenEOF {
		p.GetInputStream().Consume()
	}
	var hasListener = p._parseListeners != nil && len(p._parseListeners) > 0
	if p.buildParseTrees || hasListener {
		if p._errHandler.inErrorRecoveryMode(p) {
			var node = p._ctx.addErrorNode(o)
			if p._parseListeners != nil {
				for _, l := range p._parseListeners {
					l.VisitErrorNode(node)
				}
			}

		} else {
			node := p._ctx.addTokenNode(o)
			if p._parseListeners != nil {
				for _, l := range p._parseListeners {
					l.VisitTerminal(node)
				}
			}
		}
		//        node.invokingState = p.state
	}

	return o
}

func (p *Parser) addContextToParseTree() {
	// add current context to parent if we have a parent
	if p._ctx.GetParent() != nil {
		p._ctx.GetParent().setChildren(append(p._ctx.GetParent().getChildren(), p._ctx))
	}
}

func (p *Parser) EnterRule(localctx IParserRuleContext, state, ruleIndex int) {
	p.state = state
	p._ctx = localctx
	p._ctx.setStart(p._input.LT(1))
	if p.buildParseTrees {
		p.addContextToParseTree()
	}
	if p._parseListeners != nil {
		p.TriggerEnterRuleEvent()
	}
}

func (p *Parser) ExitRule() {
	p._ctx.setStop(p._input.LT(-1))
	// trigger event on _ctx, before it reverts to parent
	if p._parseListeners != nil {
		p.TriggerExitRuleEvent()
	}
	p.state = p._ctx.getInvokingState()
	if (p._ctx.GetParent() != nil){
		p._ctx = p._ctx.GetParent().(IParserRuleContext)
	} else {
		p._ctx = nil
	}
}

func (p *Parser) EnterOuterAlt(localctx IParserRuleContext, altNum int) {
	// if we have Newlocalctx, make sure we replace existing ctx
	// that is previous child of parse tree
	if p.buildParseTrees && p._ctx != localctx {
		if p._ctx.GetParent() != nil {
			p._ctx.GetParent().(IParserRuleContext).removeLastChild()
			p._ctx.GetParent().(IParserRuleContext).addChild(localctx)
		}
	}
	p._ctx = localctx
}

// Get the precedence level for the top-most precedence rule.
//
// @return The precedence level for the top-most precedence rule, or -1 if
// the parser context is not nested within a precedence rule.

func (p *Parser) getPrecedence() int {
	if len(p._precedenceStack) == 0 {
		return -1
	} else {
		return p._precedenceStack[len(p._precedenceStack)-1]
	}
}

func (p *Parser) EnterRecursionRule(localctx IParserRuleContext, state, ruleIndex, precedence int) {
	p.state = state
	p._precedenceStack.Push(precedence)
	p._ctx = localctx
	p._ctx.setStart(p._input.LT(1))
	if p._parseListeners != nil {
		p.TriggerEnterRuleEvent() // simulates rule entry for
		// left-recursive rules
	}
}

//
// Like {@link //EnterRule} but for recursive rules.

func (p *Parser) PushNewRecursionContext(localctx IParserRuleContext, state, ruleIndex int) {
	var previous = p._ctx
	previous.setParent(localctx)
	previous.setInvokingState(state)
	previous.setStart(p._input.LT(-1))

	p._ctx = localctx
	p._ctx.setStart(previous.getStart())
	if p.buildParseTrees {
		p._ctx.addChild(previous)
	}
	if p._parseListeners != nil {
		p.TriggerEnterRuleEvent() // simulates rule entry for
		// left-recursive rules
	}
}

func (p *Parser) UnrollRecursionContexts(parentCtx IParserRuleContext) {
	p._precedenceStack.Pop()
	p._ctx.setStop(p._input.LT(-1))
	var retCtx = p._ctx // save current ctx (return value)
	// unroll so _ctx is as it was before call to recursive method
	if p._parseListeners != nil {
		for p._ctx != parentCtx {
			p.TriggerExitRuleEvent()
			p._ctx = p._ctx.GetParent().(IParserRuleContext)
		}
	} else {
		p._ctx = parentCtx
	}
	// hook into tree
	retCtx.setParent(parentCtx)
	if p.buildParseTrees && parentCtx != nil {
		// add return ctx into invoking rule's tree
		parentCtx.addChild(retCtx)
	}
}

func (p *Parser) getInvokingContext(ruleIndex int) IParserRuleContext {
	var ctx = p._ctx
	for ctx != nil {
		if ctx.GetRuleIndex() == ruleIndex {
			return ctx
		}
		ctx = ctx.GetParent().(IParserRuleContext)
	}
	return nil
}

func (p *Parser) Precpred(localctx IRuleContext, precedence int) bool {
	return precedence >= p._precedenceStack[len(p._precedenceStack)-1]
}

func (p *Parser) inContext(context IParserRuleContext) bool {
	// TODO: useful in parser?
	return false
}

//
// Checks whether or not {@code symbol} can follow the current state in the
// ATN. The behavior of p.method is equivalent to the following, but is
// implemented such that the complete context-sensitive follow set does not
// need to be explicitly constructed.
//
// <pre>
// return getExpectedTokens().contains(symbol)
// </pre>
//
// @param symbol the symbol type to check
// @return {@code true} if {@code symbol} can follow the current state in
// the ATN, otherwise {@code false}.

func (p *Parser) isExpectedToken(symbol int) bool {
	var atn *ATN = p.Interpreter.atn
	var ctx = p._ctx
	var s = atn.states[p.state]
	var following = atn.nextTokens(s, nil)
	if following.contains(symbol) {
		return true
	}
	if !following.contains(TokenEpsilon) {
		return false
	}
	for ctx != nil && ctx.getInvokingState() >= 0 && following.contains(TokenEpsilon) {
		var invokingState = atn.states[ctx.getInvokingState()]
		var rt = invokingState.GetTransitions()[0]
		following = atn.nextTokens(rt.(*RuleTransition).followState, nil)
		if following.contains(symbol) {
			return true
		}
		ctx = ctx.GetParent().(IParserRuleContext)
	}
	if following.contains(TokenEpsilon) && symbol == TokenEOF {
		return true
	} else {
		return false
	}
}

// Computes the set of input symbols which could follow the current parser
// state and context, as given by {@link //GetState} and {@link //GetContext},
// respectively.
//
// @see ATN//getExpectedTokens(int, RuleContext)
//
func (p *Parser) getExpectedTokens() *IntervalSet {
	return p.Interpreter.atn.getExpectedTokens(p.state, p._ctx)
}

func (p *Parser) getExpectedTokensWithinCurrentRule() *IntervalSet {
	var atn = p.Interpreter.atn
	var s = atn.states[p.state]
	return atn.nextTokens(s, nil)
}

// Get a rule's index (i.e., {@code RULE_ruleName} field) or -1 if not found.//
func (p *Parser) GetRuleIndex(ruleName string) int {
	var ruleIndex, ok = p.getRuleIndexMap()[ruleName]
	if ok {
		return ruleIndex
	} else {
		return -1
	}
}

// Return List&ltString&gt of the rule names in your parser instance
// leading up to a call to the current rule. You could override if
// you want more details such as the file/line info of where
// in the ATN a rule is invoked.
//
// this very useful for error messages.

func (this *Parser) getRuleInvocationStack(p IParserRuleContext) []string {
	if p == nil {
		p = this._ctx
	}
	var stack = make([]string, 0)
	for p != nil {
		// compute what follows who invoked us
		var ruleIndex = p.GetRuleIndex()
		if ruleIndex < 0 {
			stack = append(stack, "n/a")
		} else {
			stack = append(stack, this.GetRuleNames()[ruleIndex])
		}
		p = p.GetParent().(IParserRuleContext)
	}
	return stack
}

// For debugging and other purposes.//
func (p *Parser) getDFAStrings() {
	panic("dumpDFA Not implemented!")
	//	return p._interp.decisionToDFA.toString()
}

// For debugging and other purposes.//
func (p *Parser) dumpDFA() {
	panic("dumpDFA Not implemented!")

	//	var seenOne = false
	//	for i := 0; i < p._interp.decisionToDFA.length; i++ {
	//		var dfa = p._interp.decisionToDFA[i]
	//		if ( len(dfa.states) > 0) {
	//			if (seenOne) {
	//				fmt.Println()
	//			}
	//			p.printer.println("Decision " + dfa.decision + ":")
	//			p.printer.print(dfa.toString(p.LiteralNames, p.SymbolicNames))
	//			seenOne = true
	//		}
	//	}
}

func (p *Parser) GetSourceName() string {
	return p.GrammarFileName
}

// During a parse is sometimes useful to listen in on the rule entry and exit
// events as well as token Matches. p.is for quick and dirty debugging.
//
func (p *Parser) setTrace(trace *TraceListener) {
	if trace == nil {
		p.removeParseListener(p._tracer)
		p._tracer = nil
	} else {
		if p._tracer != nil {
			p.removeParseListener(p._tracer)
		}
		p._tracer = NewTraceListener(p)
		p.addParseListener(p._tracer)
	}
}
