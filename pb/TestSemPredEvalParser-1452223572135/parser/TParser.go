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


var parserATN = []uint16{ 3,1072,54993,33286,44333,17431,44785,36224,43741,
    3,6,21,4,2,9,2,4,3,9,3,3,2,3,2,3,2,3,2,3,2,3,3,3,3,3,3,3,3,3,3,3,3,3,
    3,5,3,19,10,3,3,3,2,2,4,2,4,2,2,20,2,6,3,2,2,2,4,18,3,2,2,2,6,7,8,2,
    1,2,7,8,5,4,3,2,8,9,7,3,2,2,9,10,5,4,3,2,10,3,3,2,2,2,11,12,7,4,2,2,
    12,19,8,3,1,2,13,14,7,4,2,2,14,19,8,3,1,2,15,16,6,3,2,2,16,17,7,4,2,
    2,17,19,8,3,1,2,18,11,3,2,2,2,18,13,3,2,2,2,18,15,3,2,2,2,19,5,3,2,2,
    2,3,18, }

var deserializer = antlr4.NewATNDeserializer(nil)
var deserializedATN = deserializer.DeserializeFromUInt16( parserATN )

var literalNames = []string{ "", "';'" }
var symbolicNames = []string{ "", "", "ID", "INT", "WS" }
var ruleNames =  []string{ "s", "a" }

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
    TParserID = 2
    TParserINT = 3
    TParserWS = 4
)

const (
    TParserRULE_s = 0
    TParserRULE_a = 1
)

// an interface to support dynamic dispatch (subclassing)

type ISContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser

}

type SContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
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


func (s *SContext) A(i int) interface{} {
    if i < 0 {
        return s.GetTypedRuleContexts(reflect.TypeOf((*AContext)(nil)).Elem())
    } else {
        return s.GetTypedRuleContext(reflect.TypeOf((*AContext)(nil)).Elem(),i)
    }
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
    p.Interpreter.SetPredictionMode(antlr4.PredictionModeLLExactAmbigDetection);
    p.SetState(5)
    p.A()
    p.SetState(6)
    p.Match(TParserT__0)
    p.SetState(7)
    p.A()

    return localctx
}

// an interface to support dynamic dispatch (subclassing)

type IAContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser

}

type AContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
}

func NewEmptyAContext() *AContext {
    var p = new(AContext)
    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( nil, -1 )
    p.RuleIndex = TParserRULE_a
    return p
}

func NewAContext(parser antlr4.Parser, parent antlr4.ParserRuleContext, invokingState int) *AContext {

    var p = new(AContext)

    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( parent, invokingState )

    p.parser = parser
    p.RuleIndex = TParserRULE_a

    return p
}

func (s *AContext) getParser() antlr4.Parser { return s.parser }


func (s *AContext) ID() interface{} {
    return s.GetToken(TParserID, 0)
}

func (s *AContext) GetRuleContext() antlr4.RuleContext { return s }

func (s *AContext) EnterRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.EnterA(s)
    }
}
func (s *AContext) ExitRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.ExitA(s)
    }
}



func (p *TParser) A() IAContext {

    var localctx IAContext = NewAContext(p, p.GetParserRuleContext(), p.GetState())
    p.EnterRule(localctx, 2, TParserRULE_a)

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

    p.SetState(16)
    p.GetErrorHandler().Sync(p)
    la_ := p.GetInterpreter().AdaptivePredict(p.GetTokenStream(),0,p.GetParserRuleContext())
    switch la_ {
    case 1:
        p.EnterOuterAlt(localctx, 1)
        p.SetState(9)
        p.Match(TParserID)
        fmt.Println("alt 1")

    case 2:
        p.EnterOuterAlt(localctx, 2)
        p.SetState(11)
        p.Match(TParserID)
        fmt.Println("alt 2")

    case 3:
        p.EnterOuterAlt(localctx, 3)
        p.SetState(13)
        if !( false) {
            panic( antlr4.NewFailedPredicateException(p, "false", ""))
        }
        p.SetState(14)
        p.Match(TParserID)
        fmt.Println("alt 3")

    }

    return localctx
}


func (p *TParser) Sempred(localctx antlr4.RuleContext, ruleIndex, predIndex int) bool {
	switch ruleIndex {
	case 1:
		    var t *AContext = nil
		    if localctx != nil { t = localctx.(*AContext) }
			return p.A_Sempred(t, predIndex)
    default:
        panic("No predicate with index:" + fmt.Sprint(ruleIndex))
   }
}

func (p *TParser) A_Sempred(localctx *AContext, predIndex int) bool {
	switch predIndex {
		case 0:
			return false;
		default:
			panic("No predicate with index:" + fmt.Sprint(predIndex))
	}
}


