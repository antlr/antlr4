// Generated from T.g4 by ANTLR 4.5.1
package parser // T

import (
    "antlr4"
    "reflect"
    "fmt"
    "strconv"
)

// Stopgap to suppress unused import error. We aren't certain
// to have these imports used in the generated code below

var _ = fmt.Printf
var _ = reflect.Copy
var _ = strconv.Itoa

// Stopgap to shadow the Java types of the same name

type String string


var parserATN = []uint16{ 3,1072,54993,33286,44333,17431,44785,36224,43741,
    3,7,30,4,2,9,2,4,3,9,3,3,2,3,2,3,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,5,
    3,18,10,3,3,3,3,3,3,3,3,3,3,3,7,3,25,10,3,12,3,14,3,28,11,3,3,3,2,3,
    4,4,2,4,2,2,29,2,6,3,2,2,2,4,17,3,2,2,2,6,7,5,4,3,2,7,8,8,2,1,2,8,3,
    3,2,2,2,9,10,8,3,1,2,10,11,7,5,2,2,11,12,7,3,2,2,12,13,5,4,3,5,13,14,
    8,3,1,2,14,18,3,2,2,2,15,16,7,5,2,2,16,18,8,3,1,2,17,9,3,2,2,2,17,15,
    3,2,2,2,18,26,3,2,2,2,19,20,12,3,2,2,20,21,7,4,2,2,21,22,5,4,3,4,22,
    23,8,3,1,2,23,25,3,2,2,2,24,19,3,2,2,2,25,28,3,2,2,2,26,24,3,2,2,2,26,
    27,3,2,2,2,27,5,3,2,2,2,28,26,3,2,2,2,4,17,26, }

var deserializer = antlr4.NewATNDeserializer(nil)
var deserializedATN = deserializer.DeserializeFromUInt16( parserATN )

var literalNames = []string{ "", "'='", "'+'" }
var symbolicNames = []string{ "", "", "", "ID", "INT", "WS" }
var ruleNames =  []string{ "s", "e" }

type TParser struct {
    *antlr4.BaseParser
}

func NewTParser(input antlr4.TokenStream) *TParser {

    var decisionToDFA = make([]*antlr4.DFA,len(deserializedATN.DecisionToState))
    var sharedContextCache = antlr4.NewPredictionContextCache()

    for index, ds := range deserializedATN.DecisionToState {
        decisionToDFA[index] = antlr4.NewDFA(ds, index)
    }

    parser := new(TParser)

    parser.BaseParser = antlr4.NewBaseParser(input)

    parser.Interpreter = antlr4.NewParserATNSimulator(parser, deserializedATN, decisionToDFA, sharedContextCache)
    parser.RuleNames = ruleNames
    parser.LiteralNames = literalNames
    parser.SymbolicNames = symbolicNames
    parser.GrammarFileName = "T.g4"

    return parser
}


const(
    TParserEOF = antlr4.TokenEOF
    TParserT__0 = 1
    TParserT__1 = 2
    TParserID = 3
    TParserINT = 4
    TParserWS = 5
)

const (
    TParserRULE_s = 0
    TParserRULE_e = 1
)

// an interface to support dynamic dispatch (subclassing)

type ISContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser

    get_e() IEContext
    set_e(IEContext)
}

type SContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
    _e IEContext
}

func NewEmptySContext() *SContext {
    var p = new(SContext)
    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( nil, -1 )
    p.RuleIndex = TParserRULE_s
    return p
}

func NewSContext(parser antlr4.Parser, parent antlr4.ParserRuleContext, invokingState int) *SContext {

    var p = new(SContext)

    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( parent, invokingState )

    p.parser = parser
    p.RuleIndex = TParserRULE_s

    return p
}

func (s *SContext) getParser() antlr4.Parser { return s.parser }

func (s *SContext) get_e() IEContext  { return s._e } 
func (s *SContext) set_e(v IEContext) { s._e = v }

func (s *SContext) E() interface{} {
    return s.GetTypedRuleContext(reflect.TypeOf((*EContext)(nil)).Elem(),0)
}

func (s *SContext) GetRuleContext() antlr4.RuleContext { return s }

func (s *SContext) EnterRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.EnterS(s)
    }
}
func (s *SContext) ExitRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.ExitS(s)
    }
}



func (p *TParser) S() ISContext {

    var localctx ISContext = NewSContext(p, p.GetParserRuleContext(), p.GetState())
    p.EnterRule(localctx, 0, TParserRULE_s)

    defer func(){
        p.ExitRule()
    }()

    defer func() {
        if err := recover(); err != nil {
            if v, ok := err.(antlr4.RecognitionException); ok {
                localctx.SetException( v )
                p.GetErrorHandler().ReportError(p, v)
                p.GetErrorHandler().Recover(p, v)
            } else {
                panic(err)
            }
        }
    }()

    p.EnterOuterAlt(localctx, 1)
    p.SetState(4)
    localctx.(*SContext)._e = p.E(0)
    fmt.Println(localctx.(*SContext).get_e().getResult())

    return localctx
}

// an interface to support dynamic dispatch (subclassing)

type IEContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser

    get_ID() antlr4.Token 
    set_ID(antlr4.Token) 
    getE1() IEContext
    getE2() IEContext
    setE1(IEContext)
    setE2(IEContext)
    getResult() string
    setResult(string)
}

type EContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
    result string
    e1 IEContext
    _ID antlr4.Token
    e2 IEContext
}

func NewEmptyEContext() *EContext {
    var p = new(EContext)
    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( nil, -1 )
    p.RuleIndex = TParserRULE_e
    return p
}

func NewEContext(parser antlr4.Parser, parent antlr4.ParserRuleContext, invokingState int) *EContext {

    var p = new(EContext)

    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( parent, invokingState )

    p.parser = parser
    p.RuleIndex = TParserRULE_e

    return p
}

func (s *EContext) getParser() antlr4.Parser { return s.parser }

func (s *EContext) get_ID() antlr4.Token { return s._ID } 
func (s *EContext) set_ID(v antlr4.Token) { s._ID = v } 
func (s *EContext) getE1() IEContext  { return s.e1 } 
func (s *EContext) getE2() IEContext  { return s.e2 } 
func (s *EContext) setE1(v IEContext) { s.e1 = v }
func (s *EContext) setE2(v IEContext) { s.e2 = v }
func (s *EContext) getResult() string  { return s.result } 
func (s *EContext) setResult(v string) { s.result = v }

func (s *EContext) ID() interface{} {
    return s.GetToken(TParserID, 0)
}

func (s *EContext) E(i int) interface{} {
    if i < 0 {
        return s.GetTypedRuleContexts(reflect.TypeOf((*EContext)(nil)).Elem())
    } else {
        return s.GetTypedRuleContext(reflect.TypeOf((*EContext)(nil)).Elem(),i)
    }
}

func (s *EContext) GetRuleContext() antlr4.RuleContext { return s }

func (s *EContext) EnterRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.EnterE(s)
    }
}
func (s *EContext) ExitRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.ExitE(s)
    }
}


func (p *TParser) E(_p int) IEContext {

    var _parentctx antlr4.ParserRuleContext = p.GetParserRuleContext()
    _parentState := p.GetState()
    var localctx IEContext = NewEContext(p, p.GetParserRuleContext(), _parentState)
    var _prevctx IEContext = localctx
    var _ antlr4.ParserRuleContext = _prevctx // to prevent unused variable warning
    _startState := 2
    p.EnterRecursionRule(localctx, 2, TParserRULE_e, _p)

    defer func(){
        p.UnrollRecursionContexts(_parentctx)
    }()

    defer func(){
        if err := recover(); err != nil {
            if v, ok := err.(antlr4.RecognitionException); ok {
                localctx.SetException(v)
                p.GetErrorHandler().ReportError(p, v)
                p.GetErrorHandler().Recover(p, v)
            } else {
                panic(err)
            }
        }
    }()

    p.EnterOuterAlt(localctx, 1)
    p.SetState(15)
    p.GetErrorHandler().Sync(p)
    la_ := p.GetInterpreter().AdaptivePredict(p.GetTokenStream(),0,p.GetParserRuleContext())
    switch la_ {
    case 1:
        p.SetState(8)
        localctx.(*EContext)._ID = p.Match(TParserID)
        p.SetState(9)
        p.Match(TParserT__0)
        p.SetState(10)
        localctx.(*EContext).e1 = p.E(3)
        localctx.(*EContext).setResult( "(" + (func() string { if localctx.(*EContext).get_ID() == nil { return "" } else { return localctx.(*EContext).get_ID().GetText() }}()) + "=" + localctx.(*EContext).getE1().getResult() + ")")

    case 2:
        p.SetState(13)
        localctx.(*EContext)._ID = p.Match(TParserID)
        localctx.(*EContext).setResult( (func() string { if localctx.(*EContext).get_ID() == nil { return "" } else { return localctx.(*EContext).get_ID().GetText() }}()))

    }
    p.GetParserRuleContext().SetStop( p.GetTokenStream().LT(-1) )
    p.SetState(24)
    p.GetErrorHandler().Sync(p)
    _alt := p.GetInterpreter().AdaptivePredict(p.GetTokenStream(),1,p.GetParserRuleContext())
    for _alt!=2 && _alt!= antlr4.ATNInvalidAltNumber {
        if(_alt==1) {
            if p.GetParseListeners()!=nil {
                p.TriggerExitRuleEvent()
            }
            _prevctx = localctx
            localctx = NewEContext(p, _parentctx, _parentState)
            localctx.(*EContext).e1 = _prevctx
            p.PushNewRecursionContext(localctx, _startState, TParserRULE_e)
            p.SetState(17)
            if !( p.Precpred(p.GetParserRuleContext(), 1)) {
                panic( antlr4.NewFailedPredicateException(p, "p.Precpred(p.GetParserRuleContext(), 1)", ""))
            }
            p.SetState(18)
            p.Match(TParserT__1)
            p.SetState(19)
            localctx.(*EContext).e2 = p.E(2)
            localctx.(*EContext).setResult( "(" + localctx.(*EContext).getE1().getResult() + "+" + localctx.(*EContext).getE2().getResult() + ")") 
        }
        p.SetState(26)
        p.GetErrorHandler().Sync(p)
        _alt = p.GetInterpreter().AdaptivePredict(p.GetTokenStream(),1,p.GetParserRuleContext())
    }


    return localctx
}


func (p *TParser) Sempred(localctx antlr4.RuleContext, ruleIndex, predIndex int) bool {
	switch ruleIndex {
	case 1:
			return p.E_Sempred(localctx.(*EContext), predIndex)
    default:
        panic("No predicate with index:" + fmt.Sprint(ruleIndex))
   }
}

func (p *TParser) E_Sempred(localctx *EContext, predIndex int) bool {
	switch predIndex {
		case 0:
			return p.Precpred(p.GetParserRuleContext(), 1);
		default:
			panic("No predicate with index:" + fmt.Sprint(predIndex))
	}
}


