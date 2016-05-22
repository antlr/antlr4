package antlr4

import (
	"fmt"
	"strconv"
)

type Parser interface {
	Recognizer

	GetInterpreter() *ParserATNSimulator

	GetTokenStream() TokenStream
	GetTokenFactory() TokenFactory
	GetParserRuleContext() ParserRuleContext
	SetParserRuleContext(ParserRuleContext)
	Consume() Token
	GetParseListeners() []ParseTreeListener

	GetErrorHandler() ErrorStrategy
	SetErrorHandler(ErrorStrategy)
	GetInputStream() IntStream
	GetCurrentToken() Token
	GetExpectedTokens() *IntervalSet
	NotifyErrorListeners(string, Token, RecognitionException)
	IsExpectedToken(int) bool
	GetPrecedence() int
	GetRuleInvocationStack(ParserRuleContext) []string
}

type BaseParser struct {
	*BaseRecognizer

	Interpreter     *ParserATNSimulator
	BuildParseTrees bool

	_input           TokenStream
	_errHandler      ErrorStrategy
	_precedenceStack IntStack
	_ctx             ParserRuleContext

	_tracer         *TraceListener
	_parseListeners []ParseTreeListener
	_SyntaxErrors   int
}

// p.is all the parsing support code essentially most of it is error
// recovery stuff.//
func NewBaseParser(input TokenStream) *BaseParser {

	p := new(BaseParser)

	p.BaseRecognizer = NewBaseRecognizer()

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
	p.BuildParseTrees = true
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
	// incremented each time {@link //NotifyErrorListeners} is called.
	p._SyntaxErrors = 0
	p.SetInputStream(input)

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
func (p *BaseParser) reset() {
	if p._input != nil {
		p._input.Seek(0)
	}
	p._errHandler.reset(p)
	p._ctx = nil
	p._SyntaxErrors = 0
	p.SetTrace(nil)
	p._precedenceStack = make([]int, 0)
	p._precedenceStack.Push(0)
	if p.Interpreter != nil {
		p.Interpreter.reset()
	}
}

func (p *BaseParser) GetErrorHandler() ErrorStrategy {
	return p._errHandler
}

func (p *BaseParser) SetErrorHandler(e ErrorStrategy) {
	p._errHandler = e
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
// mismatched symbol

func (p *BaseParser) Match(ttype int) Token {

	if PortDebug {
		fmt.Println("get current token")
	}
	var t = p.GetCurrentToken()

	if PortDebug {
		fmt.Println("TOKEN IS " + t.GetText())
	}

	if t.GetTokenType() == ttype {
		p._errHandler.ReportMatch(p)
		p.Consume()
	} else {
		t = p._errHandler.RecoverInline(p)
		if p.BuildParseTrees && t.GetTokenIndex() == -1 {
			// we must have conjured up a Newtoken during single token
			// insertion
			// if it's not the current symbol
			p._ctx.AddErrorNode(t)
		}
	}

	if PortDebug {
		fmt.Println("match done")
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
// a wildcard and the error strategy could not recover from the mismatched
// symbol

func (p *BaseParser) MatchWildcard() Token {
	var t = p.GetCurrentToken()
	if t.GetTokenType() > 0 {
		p._errHandler.ReportMatch(p)
		p.Consume()
	} else {
		t = p._errHandler.RecoverInline(p)
		if p.BuildParseTrees && t.GetTokenIndex() == -1 {
			// we must have conjured up a Newtoken during single token
			// insertion
			// if it's not the current symbol
			p._ctx.AddErrorNode(t)
		}
	}
	return t
}

func (p *BaseParser) GetParserRuleContext() ParserRuleContext {
	return p._ctx
}

func (p *BaseParser) SetParserRuleContext(v ParserRuleContext)  {
	p._ctx = v
}

func (p *BaseParser) GetParseListeners() []ParseTreeListener {
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
func (p *BaseParser) AddParseListener(listener ParseTreeListener) {
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
func (p *BaseParser) RemoveParseListener(listener ParseTreeListener) {

	if p._parseListeners != nil {

		idx := -1
		for i, v := range p._parseListeners {
			if v == listener {
				idx = i
				break
			}
		}

		if idx == -1 {
			return
		}

		// remove the listener from the slice
		p._parseListeners = append(p._parseListeners[0:idx], p._parseListeners[idx+1:]...)

		if len(p._parseListeners) == 0 {
			p._parseListeners = nil
		}
	}
}

// Remove all parse listeners.
func (p *BaseParser) removeParseListeners() {
	p._parseListeners = nil
}

// Notify any parse listeners of an enter rule event.
func (p *BaseParser) TriggerEnterRuleEvent() {
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
func (p *BaseParser) TriggerExitRuleEvent() {
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

func (this *BaseParser) GetInterpreter() *ParserATNSimulator {
	return this.Interpreter
}

func (this *BaseParser) GetATN() *ATN {
	return this.Interpreter.atn
}

func (p *BaseParser) GetTokenFactory() TokenFactory {
	return p._input.GetTokenSource().GetTokenFactory()
}

// Tell our token source and error strategy about a Newway to create tokens.//
func (p *BaseParser) setTokenFactory(factory TokenFactory) {
	p._input.GetTokenSource().setTokenFactory(factory)
}

// The ATN with bypass alternatives is expensive to create so we create it
// lazily.
//
// @panics UnsupportedOperationException if the current parser does not
// implement the {@link //getSerializedATN()} method.
//
func (p *BaseParser) GetATNWithBypassAlts() {

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

func (p *BaseParser) compileParseTreePattern(pattern, patternRuleIndex, lexer Lexer) {

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

func (p *BaseParser) GetInputStream() IntStream {
	return p.GetTokenStream()
}

func (p *BaseParser) SetInputStream(input TokenStream) {
	p.SetTokenStream(input)
}

func (p *BaseParser) GetTokenStream() TokenStream {
	return p._input
}

// Set the token stream and reset the parser.//
func (p *BaseParser) SetTokenStream(input TokenStream) {
	p._input = nil
	p.reset()
	p._input = input
}

// Match needs to return the current input symbol, which gets put
// into the label for the associated token ref e.g., x=ID.
//
func (p *BaseParser) GetCurrentToken() Token {
	return p._input.LT(1)
}

func (p *BaseParser) NotifyErrorListeners(msg string, offendingToken Token, err RecognitionException) {
	if offendingToken == nil {
		offendingToken = p.GetCurrentToken()
	}
	p._SyntaxErrors += 1
	var line = offendingToken.GetLine()
	var column = offendingToken.GetColumn()
	listener := p.GetErrorListenerDispatch()
	listener.SyntaxError(p, offendingToken, line, column, msg, err)
}

func (p *BaseParser) Consume() Token {
	var o = p.GetCurrentToken()
	if o.GetTokenType() != TokenEOF {
		if PortDebug {
			fmt.Println("Consuming")
		}
		p.GetInputStream().Consume()
		if PortDebug {
			fmt.Println("Done consuming")
		}
	}
	var hasListener = p._parseListeners != nil && len(p._parseListeners) > 0
	if p.BuildParseTrees || hasListener {
		if p._errHandler.inErrorRecoveryMode(p) {
			var node = p._ctx.AddErrorNode(o)
			if p._parseListeners != nil {
				for _, l := range p._parseListeners {
					l.VisitErrorNode(node)
				}
			}

		} else {
			node := p._ctx.AddTokenNode(o)
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

func (p *BaseParser) addContextToParseTree() {
	// add current context to parent if we have a parent
	if p._ctx.GetParent() != nil {
		p._ctx.GetParent().(ParserRuleContext).AddChild(p._ctx)
	}
}

func (p *BaseParser) EnterRule(localctx ParserRuleContext, state, ruleIndex int) {
	p.SetState(state)
	p._ctx = localctx
	p._ctx.SetStart(p._input.LT(1))
	if p.BuildParseTrees {
		p.addContextToParseTree()
	}
	if p._parseListeners != nil {
		p.TriggerEnterRuleEvent()
	}
}

func (p *BaseParser) ExitRule() {
	p._ctx.SetStop(p._input.LT(-1))
	// trigger event on _ctx, before it reverts to parent
	if p._parseListeners != nil {
		p.TriggerExitRuleEvent()
	}
	p.SetState(p._ctx.GetInvokingState())
	if p._ctx.GetParent() != nil {
		p._ctx = p._ctx.GetParent().(ParserRuleContext)
	} else {
		p._ctx = nil
	}
}

func (p *BaseParser) EnterOuterAlt(localctx ParserRuleContext, altNum int) {
	// if we have Newlocalctx, make sure we replace existing ctx
	// that is previous child of parse tree
	if p.BuildParseTrees && p._ctx != localctx {
		if p._ctx.GetParent() != nil {
			p._ctx.GetParent().(ParserRuleContext).RemoveLastChild()
			p._ctx.GetParent().(ParserRuleContext).AddChild(localctx)
		}
	}
	p._ctx = localctx
}

// Get the precedence level for the top-most precedence rule.
//
// @return The precedence level for the top-most precedence rule, or -1 if
// the parser context is not nested within a precedence rule.

func (p *BaseParser) GetPrecedence() int {
	if len(p._precedenceStack) == 0 {
		return -1
	} else {
		return p._precedenceStack[len(p._precedenceStack)-1]
	}
}

func (p *BaseParser) EnterRecursionRule(localctx ParserRuleContext, state, ruleIndex, precedence int) {
	p.SetState(state)
	p._precedenceStack.Push(precedence)
	p._ctx = localctx
	p._ctx.SetStart(p._input.LT(1))
	if p._parseListeners != nil {
		p.TriggerEnterRuleEvent() // simulates rule entry for
		// left-recursive rules
	}
}

//
// Like {@link //EnterRule} but for recursive rules.

func (p *BaseParser) PushNewRecursionContext(localctx ParserRuleContext, state, ruleIndex int) {
	var previous = p._ctx
	previous.SetParent(localctx)
	previous.SetInvokingState(state)
	previous.SetStop(p._input.LT(-1))

	p._ctx = localctx
	p._ctx.SetStart(previous.GetStart())
	if p.BuildParseTrees {
		p._ctx.AddChild(previous)
	}
	if p._parseListeners != nil {
		p.TriggerEnterRuleEvent() // simulates rule entry for
		// left-recursive rules
	}
}

func (p *BaseParser) UnrollRecursionContexts(parentCtx ParserRuleContext) {
	p._precedenceStack.Pop()
	p._ctx.SetStop(p._input.LT(-1))
	var retCtx = p._ctx // save current ctx (return value)
	// unroll so _ctx is as it was before call to recursive method
	if p._parseListeners != nil {
		for p._ctx != parentCtx {
			p.TriggerExitRuleEvent()
			p._ctx = p._ctx.GetParent().(ParserRuleContext)
		}
	} else {
		p._ctx = parentCtx
	}
	// hook into tree
	retCtx.SetParent(parentCtx)
	if p.BuildParseTrees && parentCtx != nil {
		// add return ctx into invoking rule's tree
		parentCtx.AddChild(retCtx)
	}
}

func (p *BaseParser) GetInvokingContext(ruleIndex int) ParserRuleContext {
	var ctx = p._ctx
	for ctx != nil {
		if ctx.GetRuleIndex() == ruleIndex {
			return ctx
		}
		ctx = ctx.GetParent().(ParserRuleContext)
	}
	return nil
}

func (p *BaseParser) Precpred(localctx RuleContext, precedence int) bool {
	return precedence >= p._precedenceStack[len(p._precedenceStack)-1]
}

func (p *BaseParser) inContext(context ParserRuleContext) bool {
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

func (p *BaseParser) IsExpectedToken(symbol int) bool {
	var atn *ATN = p.Interpreter.atn
	var ctx = p._ctx
	var s = atn.states[p.state]
	var following = atn.NextTokens(s, nil)
	if following.contains(symbol) {
		return true
	}
	if !following.contains(TokenEpsilon) {
		return false
	}
	for ctx != nil && ctx.GetInvokingState() >= 0 && following.contains(TokenEpsilon) {
		var invokingState = atn.states[ctx.GetInvokingState()]
		var rt = invokingState.GetTransitions()[0]
		following = atn.NextTokens(rt.(*RuleTransition).followState, nil)
		if following.contains(symbol) {
			return true
		}
		ctx = ctx.GetParent().(ParserRuleContext)
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
func (p *BaseParser) GetExpectedTokens() *IntervalSet {
	return p.Interpreter.atn.getExpectedTokens(p.state, p._ctx)
}

func (p *BaseParser) GetExpectedTokensWithinCurrentRule() *IntervalSet {
	var atn = p.Interpreter.atn
	var s = atn.states[p.state]
	return atn.NextTokens(s, nil)
}

// Get a rule's index (i.e., {@code RULE_ruleName} field) or -1 if not found.//
func (p *BaseParser) GetRuleIndex(ruleName string) int {
	var ruleIndex, ok = p.GetRuleIndexMap()[ruleName]
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

func (this *BaseParser) GetRuleInvocationStack(p ParserRuleContext) []string {
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

		vp := p.GetParent()

		if vp == nil {
			break
		}

		p = vp.(ParserRuleContext)
	}
	return stack
}

// For debugging and other purposes.//
func (p *BaseParser) GetDFAStrings() string {
	return fmt.Sprint(p.Interpreter.DecisionToDFA)
}

// For debugging and other purposes.//
func (p *BaseParser) DumpDFA() {
	var seenOne = false
	for _, dfa := range p.Interpreter.DecisionToDFA {
		if ( len(dfa.GetStates()) > 0) {
			if (seenOne) {
				fmt.Println()
			}
			fmt.Println("Decision " + strconv.Itoa(dfa.decision) + ":")
			fmt.Print(dfa.String(p.LiteralNames, p.SymbolicNames))
			seenOne = true
		}
	}
}

func (p *BaseParser) GetSourceName() string {
	return p.GrammarFileName
}

// During a parse is sometimes useful to listen in on the rule entry and exit
// events as well as token Matches. p.is for quick and dirty debugging.
//
func (p *BaseParser) SetTrace(trace *TraceListener) {
	if trace == nil {
		p.RemoveParseListener(p._tracer)
		p._tracer = nil
	} else {
		if p._tracer != nil {
			p.RemoveParseListener(p._tracer)
		}
		p._tracer = NewTraceListener(p)
		p.AddParseListener(p._tracer)
	}
}
