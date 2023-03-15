// Code generated from test.g4 by ANTLR 4.12.0. DO NOT EDIT.

package test // test
import (
	"fmt"
	"strconv"
	"sync"

	"github.com/antlr/antlr4/runtime/Go/antlr/v4"
)

// Suppress unused import errors
var _ = fmt.Printf
var _ = strconv.Itoa
var _ = sync.Once{}

type testParser struct {
	*antlr.BaseParser
}

var testParserStaticData struct {
	once                   sync.Once
	serializedATN          []int32
	literalNames           []string
	symbolicNames          []string
	ruleNames              []string
	predictionContextCache *antlr.PredictionContextCache
	atn                    *antlr.ATN
	decisionToDFA          []*antlr.DFA
}

func testParserInit() {
	staticData := &testParserStaticData
	staticData.literalNames = []string{
		"", "';'", "'and'",
	}
	staticData.symbolicNames = []string{
		"", "", "AND", "IDENTIFIER", "WS",
	}
	staticData.ruleNames = []string{
		"stat", "expression",
	}
	staticData.predictionContextCache = antlr.NewPredictionContextCache()
	staticData.serializedATN = []int32{
		4, 1, 4, 25, 2, 0, 7, 0, 2, 1, 7, 1, 1, 0, 1, 0, 1, 0, 3, 0, 8, 8, 0, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 1, 16, 8, 1, 11, 1, 12, 1, 17, 5, 1,
		20, 8, 1, 10, 1, 12, 1, 23, 9, 1, 1, 1, 0, 1, 2, 2, 0, 2, 0, 0, 25, 0,
		7, 1, 0, 0, 0, 2, 9, 1, 0, 0, 0, 4, 8, 3, 2, 1, 0, 5, 6, 5, 3, 0, 0, 6,
		8, 5, 1, 0, 0, 7, 4, 1, 0, 0, 0, 7, 5, 1, 0, 0, 0, 8, 1, 1, 0, 0, 0, 9,
		10, 6, 1, -1, 0, 10, 11, 5, 3, 0, 0, 11, 21, 1, 0, 0, 0, 12, 15, 10, 2,
		0, 0, 13, 14, 5, 2, 0, 0, 14, 16, 3, 2, 1, 0, 15, 13, 1, 0, 0, 0, 16, 17,
		1, 0, 0, 0, 17, 15, 1, 0, 0, 0, 17, 18, 1, 0, 0, 0, 18, 20, 1, 0, 0, 0,
		19, 12, 1, 0, 0, 0, 20, 23, 1, 0, 0, 0, 21, 19, 1, 0, 0, 0, 21, 22, 1,
		0, 0, 0, 22, 3, 1, 0, 0, 0, 23, 21, 1, 0, 0, 0, 3, 7, 17, 21,
	}
	deserializer := antlr.NewATNDeserializer(nil)
	staticData.atn = deserializer.Deserialize(staticData.serializedATN)
	atn := staticData.atn
	staticData.decisionToDFA = make([]*antlr.DFA, len(atn.DecisionToState))
	decisionToDFA := staticData.decisionToDFA
	for index, state := range atn.DecisionToState {
		decisionToDFA[index] = antlr.NewDFA(state, index)
	}
}

// testParserInit initializes any static state used to implement testParser. By default the
// static state used to implement the parser is lazily initialized during the first call to
// NewtestParser(). You can call this function if you wish to initialize the static state ahead
// of time.
func TestParserInit() {
	staticData := &testParserStaticData
	staticData.once.Do(testParserInit)
}

// NewtestParser produces a new parser instance for the optional input antlr.TokenStream.
func NewtestParser(input antlr.TokenStream) *testParser {
	TestParserInit()
	this := new(testParser)
	this.BaseParser = antlr.NewBaseParser(input)
	staticData := &testParserStaticData
	this.Interpreter = antlr.NewParserATNSimulator(this, staticData.atn, staticData.decisionToDFA, staticData.predictionContextCache)
	this.RuleNames = staticData.ruleNames
	this.LiteralNames = staticData.literalNames
	this.SymbolicNames = staticData.symbolicNames
	this.GrammarFileName = "test.g4"

	return this
}

// testParser tokens.
const (
	testParserEOF        = antlr.TokenEOF
	testParserT__0       = 1
	testParserAND        = 2
	testParserIDENTIFIER = 3
	testParserWS         = 4
)

// testParser rules.
const (
	testParserRULE_stat       = 0
	testParserRULE_expression = 1
)

// IStatContext is an interface to support dynamic dispatch.
type IStatContext interface {
	antlr.ParserRuleContext

	// GetParser returns the parser.
	GetParser() antlr.Parser

	// Getter signatures
	Expression() IExpressionContext
	IDENTIFIER() antlr.TerminalNode

	// IsStatContext differentiates from other interfaces.
	IsStatContext()
}

type StatContext struct {
	*antlr.BaseParserRuleContext
	parser antlr.Parser
}

func NewEmptyStatContext() *StatContext {
	var p = new(StatContext)
	p.BaseParserRuleContext = antlr.NewBaseParserRuleContext(nil, -1)
	p.RuleIndex = testParserRULE_stat
	return p
}

func (*StatContext) IsStatContext() {}

func NewStatContext(parser antlr.Parser, parent antlr.ParserRuleContext, invokingState int) *StatContext {
	var p = new(StatContext)

	p.BaseParserRuleContext = antlr.NewBaseParserRuleContext(parent, invokingState)

	p.parser = parser
	p.RuleIndex = testParserRULE_stat

	return p
}

func (s *StatContext) GetParser() antlr.Parser { return s.parser }

func (s *StatContext) Expression() IExpressionContext {
	var t antlr.RuleContext
	for _, ctx := range s.GetChildren() {
		if _, ok := ctx.(IExpressionContext); ok {
			t = ctx.(antlr.RuleContext)
			break
		}
	}

	if t == nil {
		return nil
	}

	return t.(IExpressionContext)
}

func (s *StatContext) IDENTIFIER() antlr.TerminalNode {
	return s.GetToken(testParserIDENTIFIER, 0)
}

func (s *StatContext) GetRuleContext() antlr.RuleContext {
	return s
}

func (s *StatContext) ToStringTree(ruleNames []string, recog antlr.Recognizer) string {
	return antlr.TreesStringTree(s, ruleNames, recog)
}

func (s *StatContext) EnterRule(listener antlr.ParseTreeListener) {
	if listenerT, ok := listener.(testListener); ok {
		listenerT.EnterStat(s)
	}
}

func (s *StatContext) ExitRule(listener antlr.ParseTreeListener) {
	if listenerT, ok := listener.(testListener); ok {
		listenerT.ExitStat(s)
	}
}

func (s *StatContext) Accept(visitor antlr.ParseTreeVisitor) interface{} {
	switch t := visitor.(type) {
	case testVisitor:
		return t.VisitStat(s)

	default:
		return t.VisitChildren(s)
	}
}

func (p *testParser) Stat() (localctx IStatContext) {
	this := p
	_ = this

	localctx = NewStatContext(p, p.GetParserRuleContext(), p.GetState())
	p.EnterRule(localctx, 0, testParserRULE_stat)

	defer func() {
		p.ExitRule()
	}()

	defer func() {
		if err := recover(); err != nil {
			if v, ok := err.(antlr.RecognitionException); ok {
				localctx.SetException(v)
				p.GetErrorHandler().ReportError(p, v)
				p.GetErrorHandler().Recover(p, v)
			} else {
				panic(err)
			}
		}
	}()

	p.SetState(7)
	p.GetErrorHandler().Sync(p)
	switch p.GetInterpreter().AdaptivePredict(p.GetTokenStream(), 0, p.GetParserRuleContext()) {
	case 1:
		p.EnterOuterAlt(localctx, 1)
		{
			p.SetState(4)
			p.expression(0)
		}

	case 2:
		p.EnterOuterAlt(localctx, 2)
		{
			p.SetState(5)
			p.Match(testParserIDENTIFIER)
		}
		{
			p.SetState(6)
			p.Match(testParserT__0)
		}

	}

	return localctx
}

// IExpressionContext is an interface to support dynamic dispatch.
type IExpressionContext interface {
	antlr.ParserRuleContext

	// GetParser returns the parser.
	GetParser() antlr.Parser

	// Getter signatures
	IDENTIFIER() antlr.TerminalNode
	AllExpression() []IExpressionContext
	Expression(i int) IExpressionContext
	AllAND() []antlr.TerminalNode
	AND(i int) antlr.TerminalNode

	// IsExpressionContext differentiates from other interfaces.
	IsExpressionContext()
}

type ExpressionContext struct {
	*antlr.BaseParserRuleContext
	parser antlr.Parser
}

func NewEmptyExpressionContext() *ExpressionContext {
	var p = new(ExpressionContext)
	p.BaseParserRuleContext = antlr.NewBaseParserRuleContext(nil, -1)
	p.RuleIndex = testParserRULE_expression
	return p
}

func (*ExpressionContext) IsExpressionContext() {}

func NewExpressionContext(parser antlr.Parser, parent antlr.ParserRuleContext, invokingState int) *ExpressionContext {
	var p = new(ExpressionContext)

	p.BaseParserRuleContext = antlr.NewBaseParserRuleContext(parent, invokingState)

	p.parser = parser
	p.RuleIndex = testParserRULE_expression

	return p
}

func (s *ExpressionContext) GetParser() antlr.Parser { return s.parser }

func (s *ExpressionContext) IDENTIFIER() antlr.TerminalNode {
	return s.GetToken(testParserIDENTIFIER, 0)
}

func (s *ExpressionContext) AllExpression() []IExpressionContext {
	children := s.GetChildren()
	len := 0
	for _, ctx := range children {
		if _, ok := ctx.(IExpressionContext); ok {
			len++
		}
	}

	tst := make([]IExpressionContext, len)
	i := 0
	for _, ctx := range children {
		if t, ok := ctx.(IExpressionContext); ok {
			tst[i] = t.(IExpressionContext)
			i++
		}
	}

	return tst
}

func (s *ExpressionContext) Expression(i int) IExpressionContext {
	var t antlr.RuleContext
	j := 0
	for _, ctx := range s.GetChildren() {
		if _, ok := ctx.(IExpressionContext); ok {
			if j == i {
				t = ctx.(antlr.RuleContext)
				break
			}
			j++
		}
	}

	if t == nil {
		return nil
	}

	return t.(IExpressionContext)
}

func (s *ExpressionContext) AllAND() []antlr.TerminalNode {
	return s.GetTokens(testParserAND)
}

func (s *ExpressionContext) AND(i int) antlr.TerminalNode {
	return s.GetToken(testParserAND, i)
}

func (s *ExpressionContext) GetRuleContext() antlr.RuleContext {
	return s
}

func (s *ExpressionContext) ToStringTree(ruleNames []string, recog antlr.Recognizer) string {
	return antlr.TreesStringTree(s, ruleNames, recog)
}

func (s *ExpressionContext) EnterRule(listener antlr.ParseTreeListener) {
	if listenerT, ok := listener.(testListener); ok {
		listenerT.EnterExpression(s)
	}
}

func (s *ExpressionContext) ExitRule(listener antlr.ParseTreeListener) {
	if listenerT, ok := listener.(testListener); ok {
		listenerT.ExitExpression(s)
	}
}

func (s *ExpressionContext) Accept(visitor antlr.ParseTreeVisitor) interface{} {
	switch t := visitor.(type) {
	case testVisitor:
		return t.VisitExpression(s)

	default:
		return t.VisitChildren(s)
	}
}

func (p *testParser) Expression() (localctx IExpressionContext) {
	return p.expression(0)
}

func (p *testParser) expression(_p int) (localctx IExpressionContext) {
	this := p
	_ = this

	var _parentctx antlr.ParserRuleContext = p.GetParserRuleContext()
	_parentState := p.GetState()
	localctx = NewExpressionContext(p, p.GetParserRuleContext(), _parentState)
	var _prevctx IExpressionContext = localctx
	var _ antlr.ParserRuleContext = _prevctx // TODO: To prevent unused variable warning.
	_startState := 2
	p.EnterRecursionRule(localctx, 2, testParserRULE_expression, _p)

	defer func() {
		p.UnrollRecursionContexts(_parentctx)
	}()

	defer func() {
		if err := recover(); err != nil {
			if v, ok := err.(antlr.RecognitionException); ok {
				localctx.SetException(v)
				p.GetErrorHandler().ReportError(p, v)
				p.GetErrorHandler().Recover(p, v)
			} else {
				panic(err)
			}
		}
	}()

	var _alt int

	p.EnterOuterAlt(localctx, 1)
	{
		p.SetState(10)
		p.Match(testParserIDENTIFIER)
	}

	p.GetParserRuleContext().SetStop(p.GetTokenStream().LT(-1))
	p.SetState(21)
	p.GetErrorHandler().Sync(p)
	_alt = p.GetInterpreter().AdaptivePredict(p.GetTokenStream(), 2, p.GetParserRuleContext())

	for _alt != 2 && _alt != antlr.ATNInvalidAltNumber {
		if _alt == 1 {
			if p.GetParseListeners() != nil {
				p.TriggerExitRuleEvent()
			}
			_prevctx = localctx
			localctx = NewExpressionContext(p, _parentctx, _parentState)
			p.PushNewRecursionContext(localctx, _startState, testParserRULE_expression)
			p.SetState(12)

			if !(p.Precpred(p.GetParserRuleContext(), 2)) {
				panic(antlr.NewFailedPredicateException(p, "p.Precpred(p.GetParserRuleContext(), 2)", ""))
			}
			p.SetState(15)
			p.GetErrorHandler().Sync(p)
			_alt = 1
			for ok := true; ok; ok = _alt != 2 && _alt != antlr.ATNInvalidAltNumber {
				switch _alt {
				case 1:
					{
						p.SetState(13)
						p.Match(testParserAND)
					}
					{
						p.SetState(14)
						p.expression(0)
					}

				default:
					panic(antlr.NewNoViableAltException(p, nil, nil, nil, nil, nil))
				}

				p.SetState(17)
				p.GetErrorHandler().Sync(p)
				_alt = p.GetInterpreter().AdaptivePredict(p.GetTokenStream(), 1, p.GetParserRuleContext())
			}

		}
		p.SetState(23)
		p.GetErrorHandler().Sync(p)
		_alt = p.GetInterpreter().AdaptivePredict(p.GetTokenStream(), 2, p.GetParserRuleContext())
	}

	return localctx
}

func (p *testParser) Sempred(localctx antlr.RuleContext, ruleIndex, predIndex int) bool {
	switch ruleIndex {
	case 1:
		var t *ExpressionContext = nil
		if localctx != nil {
			t = localctx.(*ExpressionContext)
		}
		return p.Expression_Sempred(t, predIndex)

	default:
		panic("No predicate with index: " + fmt.Sprint(ruleIndex))
	}
}

func (p *testParser) Expression_Sempred(localctx antlr.RuleContext, predIndex int) bool {
	this := p
	_ = this

	switch predIndex {
	case 0:
		return p.Precpred(p.GetParserRuleContext(), 2)

	default:
		panic("No predicate with index: " + fmt.Sprint(predIndex))
	}
}
