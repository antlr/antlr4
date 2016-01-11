// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestParserExec-1452205020397/parser/T.g4 by ANTLR 4.5.1
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
    3,7,52,4,2,9,2,4,3,9,3,4,4,9,4,4,5,9,5,4,6,9,6,3,2,3,2,3,2,3,2,7,2,17,
    10,2,12,2,14,2,20,11,2,3,2,7,2,23,10,2,12,2,14,2,26,11,2,3,2,5,2,29,
    10,2,3,2,3,2,3,2,5,2,34,10,2,3,3,3,3,3,3,3,3,3,3,7,3,41,10,3,12,3,14,
    3,44,11,3,3,4,3,4,3,5,3,5,3,6,3,6,3,6,2,2,7,2,4,6,8,10,2,2,51,2,12,3,
    2,2,2,4,35,3,2,2,2,6,45,3,2,2,2,8,47,3,2,2,2,10,49,3,2,2,2,12,13,7,3,
    2,2,13,33,5,6,4,2,14,18,7,4,2,2,15,17,5,8,5,2,16,15,3,2,2,2,17,20,3,
    2,2,2,18,16,3,2,2,2,18,19,3,2,2,2,19,24,3,2,2,2,20,18,3,2,2,2,21,23,
    5,4,3,2,22,21,3,2,2,2,23,26,3,2,2,2,24,22,3,2,2,2,24,25,3,2,2,2,25,28,
    3,2,2,2,26,24,3,2,2,2,27,29,5,10,6,2,28,27,3,2,2,2,28,29,3,2,2,2,29,
    30,3,2,2,2,30,31,7,5,2,2,31,34,7,3,2,2,32,34,5,8,5,2,33,14,3,2,2,2,33,
    32,3,2,2,2,34,3,3,2,2,2,35,36,7,6,2,2,36,37,7,3,2,2,37,38,5,6,4,2,38,
    42,7,4,2,2,39,41,5,8,5,2,40,39,3,2,2,2,41,44,3,2,2,2,42,40,3,2,2,2,42,
    43,3,2,2,2,43,5,3,2,2,2,44,42,3,2,2,2,45,46,7,7,2,2,46,7,3,2,2,2,47,
    48,7,7,2,2,48,9,3,2,2,2,49,50,7,7,2,2,50,11,3,2,2,2,7,18,24,28,33,42, }

var deserializer = antlr4.NewATNDeserializer(nil)
var deserializedATN = deserializer.DeserializeFromUInt16( parserATN )

var literalNames = []string{ "", "'if'", "'then'", "'end'", "'else'", "'a'" }
var symbolicNames = []string{  }
var ruleNames =  []string{ "ifStatement", "elseIfStatement", "expression", 
                           "executableStatement", "elseStatement" }

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
    TParserT__2 = 3
    TParserT__3 = 4
    TParserT__4 = 5
)

const (
    TParserRULE_ifStatement = 0
    TParserRULE_elseIfStatement = 1
    TParserRULE_expression = 2
    TParserRULE_executableStatement = 3
    TParserRULE_elseStatement = 4
)

// an interface to support dynamic dispatch (subclassing)

type IIfStatementContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser
}

type IfStatementContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
}

func NewEmptyIfStatementContext() *IfStatementContext {
    var p = new(IfStatementContext)
    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( nil, -1 )
    p.RuleIndex = TParserRULE_ifStatement
    return p
}

func NewIfStatementContext(parser antlr4.Parser, parent antlr4.ParserRuleContext, invokingState int) *IfStatementContext {

    var p = new(IfStatementContext)

    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( parent, invokingState )

    p.parser = parser
    p.RuleIndex = TParserRULE_ifStatement

    // TODO initialize list attrs

    return p
}


func (s *IfStatementContext) getParser() antlr4.Parser { return s.parser }

func (s *IfStatementContext) Expression() interface{} {
    return s.GetTypedRuleContext(reflect.TypeOf((*ExpressionContext)(nil)).Elem(),0)
}

func (s *IfStatementContext) ExecutableStatement(i int) interface{} {
    if i < 0 {
        return s.GetTypedRuleContexts(reflect.TypeOf((*ExecutableStatementContext)(nil)).Elem())
    } else {
        return s.GetTypedRuleContext(reflect.TypeOf((*ExecutableStatementContext)(nil)).Elem(),i)
    }
}

func (s *IfStatementContext) ElseIfStatement(i int) interface{} {
    if i < 0 {
        return s.GetTypedRuleContexts(reflect.TypeOf((*ElseIfStatementContext)(nil)).Elem())
    } else {
        return s.GetTypedRuleContext(reflect.TypeOf((*ElseIfStatementContext)(nil)).Elem(),i)
    }
}

func (s *IfStatementContext) ElseStatement() interface{} {
    return s.GetTypedRuleContext(reflect.TypeOf((*ElseStatementContext)(nil)).Elem(),0)
}

func (s *IfStatementContext) GetRuleContext() antlr4.RuleContext {
    // Go does not truly support inheritance nor virtual method calls, so we need to implement this directly
    return s
}

func (s *IfStatementContext) EnterRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.EnterIfStatement(s)
    }
}
func (s *IfStatementContext) ExitRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.ExitIfStatement(s)
    }
}
func (s *IfStatementContext) Accept(visitor antlr4.ParseTreeVisitor) interface{} {
    switch t := visitor.(type) {
    case TVisitor:
        return t.VisitIfStatement(s)
    default:
        return t.VisitChildren(s)
    }
}



func (p *TParser) IfStatement() IIfStatementContext {

    var localctx IIfStatementContext = NewIfStatementContext(p, p.GetParserRuleContext(), p.GetState())
    p.EnterRule(localctx, 0, TParserRULE_ifStatement)
    var _la int

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
    p.SetState(10)
    p.Match(TParserT__0)
    p.SetState(11)
    p.Expression()
    p.SetState(31)
    switch p.GetTokenStream().LA(1) {
    case TParserT__1:
        p.SetState(12)
        p.Match(TParserT__1)
        p.SetState(16)
        p.GetErrorHandler().Sync(p)
        _alt := p.GetInterpreter().AdaptivePredict(p.GetTokenStream(),0,p.GetParserRuleContext())
        for _alt!=2 && _alt!= antlr4.ATNInvalidAltNumber {
            if(_alt==1) {
                p.SetState(13)
                p.ExecutableStatement() 
            }
            p.SetState(18)
            p.GetErrorHandler().Sync(p)
            _alt = p.GetInterpreter().AdaptivePredict(p.GetTokenStream(),0,p.GetParserRuleContext())
        }

        p.SetState(22)
        p.GetErrorHandler().Sync(p)
        _la = p.GetTokenStream().LA(1);
        for _la==TParserT__3 {
            p.SetState(19)
            p.ElseIfStatement()
            p.SetState(24)
            p.GetErrorHandler().Sync(p)
            _la = p.GetTokenStream().LA(1);
        }
        p.SetState(26)
        _la = p.GetTokenStream().LA(1);
        if _la==TParserT__4 {
            p.SetState(25)
            p.ElseStatement()
        }

        p.SetState(28)
        p.Match(TParserT__2)
        p.SetState(29)
        p.Match(TParserT__0)

    case TParserT__4:
        p.SetState(30)
        p.ExecutableStatement()

    default:
        panic(antlr4.NewNoViableAltException(p, nil, nil, nil, nil, nil))
    }

    List<?> __ttt__ = localctx.elseIfStatement();


    return localctx
}

// an interface to support dynamic dispatch (subclassing)

type IElseIfStatementContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser
}

type ElseIfStatementContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
}

func NewEmptyElseIfStatementContext() *ElseIfStatementContext {
    var p = new(ElseIfStatementContext)
    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( nil, -1 )
    p.RuleIndex = TParserRULE_elseIfStatement
    return p
}

func NewElseIfStatementContext(parser antlr4.Parser, parent antlr4.ParserRuleContext, invokingState int) *ElseIfStatementContext {

    var p = new(ElseIfStatementContext)

    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( parent, invokingState )

    p.parser = parser
    p.RuleIndex = TParserRULE_elseIfStatement

    // TODO initialize list attrs

    return p
}


func (s *ElseIfStatementContext) getParser() antlr4.Parser { return s.parser }

func (s *ElseIfStatementContext) Expression() interface{} {
    return s.GetTypedRuleContext(reflect.TypeOf((*ExpressionContext)(nil)).Elem(),0)
}

func (s *ElseIfStatementContext) ExecutableStatement(i int) interface{} {
    if i < 0 {
        return s.GetTypedRuleContexts(reflect.TypeOf((*ExecutableStatementContext)(nil)).Elem())
    } else {
        return s.GetTypedRuleContext(reflect.TypeOf((*ExecutableStatementContext)(nil)).Elem(),i)
    }
}

func (s *ElseIfStatementContext) GetRuleContext() antlr4.RuleContext {
    // Go does not truly support inheritance nor virtual method calls, so we need to implement this directly
    return s
}

func (s *ElseIfStatementContext) EnterRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.EnterElseIfStatement(s)
    }
}
func (s *ElseIfStatementContext) ExitRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.ExitElseIfStatement(s)
    }
}
func (s *ElseIfStatementContext) Accept(visitor antlr4.ParseTreeVisitor) interface{} {
    switch t := visitor.(type) {
    case TVisitor:
        return t.VisitElseIfStatement(s)
    default:
        return t.VisitChildren(s)
    }
}



func (p *TParser) ElseIfStatement() IElseIfStatementContext {

    var localctx IElseIfStatementContext = NewElseIfStatementContext(p, p.GetParserRuleContext(), p.GetState())
    p.EnterRule(localctx, 2, TParserRULE_elseIfStatement)

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
    p.SetState(33)
    p.Match(TParserT__3)
    p.SetState(34)
    p.Match(TParserT__0)
    p.SetState(35)
    p.Expression()
    p.SetState(36)
    p.Match(TParserT__1)
    p.SetState(40)
    p.GetErrorHandler().Sync(p)
    _alt := p.GetInterpreter().AdaptivePredict(p.GetTokenStream(),4,p.GetParserRuleContext())
    for _alt!=2 && _alt!= antlr4.ATNInvalidAltNumber {
        if(_alt==1) {
            p.SetState(37)
            p.ExecutableStatement() 
        }
        p.SetState(42)
        p.GetErrorHandler().Sync(p)
        _alt = p.GetInterpreter().AdaptivePredict(p.GetTokenStream(),4,p.GetParserRuleContext())
    }


    return localctx
}

// an interface to support dynamic dispatch (subclassing)

type IExpressionContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser
}

type ExpressionContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
}

func NewEmptyExpressionContext() *ExpressionContext {
    var p = new(ExpressionContext)
    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( nil, -1 )
    p.RuleIndex = TParserRULE_expression
    return p
}

func NewExpressionContext(parser antlr4.Parser, parent antlr4.ParserRuleContext, invokingState int) *ExpressionContext {

    var p = new(ExpressionContext)

    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( parent, invokingState )

    p.parser = parser
    p.RuleIndex = TParserRULE_expression

    // TODO initialize list attrs

    return p
}


func (s *ExpressionContext) getParser() antlr4.Parser { return s.parser }


func (s *ExpressionContext) GetRuleContext() antlr4.RuleContext {
    // Go does not truly support inheritance nor virtual method calls, so we need to implement this directly
    return s
}

func (s *ExpressionContext) EnterRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.EnterExpression(s)
    }
}
func (s *ExpressionContext) ExitRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.ExitExpression(s)
    }
}
func (s *ExpressionContext) Accept(visitor antlr4.ParseTreeVisitor) interface{} {
    switch t := visitor.(type) {
    case TVisitor:
        return t.VisitExpression(s)
    default:
        return t.VisitChildren(s)
    }
}



func (p *TParser) Expression() IExpressionContext {

    var localctx IExpressionContext = NewExpressionContext(p, p.GetParserRuleContext(), p.GetState())
    p.EnterRule(localctx, 4, TParserRULE_expression)

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
    p.SetState(43)
    p.Match(TParserT__4)

    return localctx
}

// an interface to support dynamic dispatch (subclassing)

type IExecutableStatementContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser
}

type ExecutableStatementContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
}

func NewEmptyExecutableStatementContext() *ExecutableStatementContext {
    var p = new(ExecutableStatementContext)
    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( nil, -1 )
    p.RuleIndex = TParserRULE_executableStatement
    return p
}

func NewExecutableStatementContext(parser antlr4.Parser, parent antlr4.ParserRuleContext, invokingState int) *ExecutableStatementContext {

    var p = new(ExecutableStatementContext)

    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( parent, invokingState )

    p.parser = parser
    p.RuleIndex = TParserRULE_executableStatement

    // TODO initialize list attrs

    return p
}


func (s *ExecutableStatementContext) getParser() antlr4.Parser { return s.parser }


func (s *ExecutableStatementContext) GetRuleContext() antlr4.RuleContext {
    // Go does not truly support inheritance nor virtual method calls, so we need to implement this directly
    return s
}

func (s *ExecutableStatementContext) EnterRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.EnterExecutableStatement(s)
    }
}
func (s *ExecutableStatementContext) ExitRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.ExitExecutableStatement(s)
    }
}
func (s *ExecutableStatementContext) Accept(visitor antlr4.ParseTreeVisitor) interface{} {
    switch t := visitor.(type) {
    case TVisitor:
        return t.VisitExecutableStatement(s)
    default:
        return t.VisitChildren(s)
    }
}



func (p *TParser) ExecutableStatement() IExecutableStatementContext {

    var localctx IExecutableStatementContext = NewExecutableStatementContext(p, p.GetParserRuleContext(), p.GetState())
    p.EnterRule(localctx, 6, TParserRULE_executableStatement)

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
    p.SetState(45)
    p.Match(TParserT__4)

    return localctx
}

// an interface to support dynamic dispatch (subclassing)

type IElseStatementContext interface {
    antlr4.ParserRuleContext

    getParser() antlr4.Parser
}

type ElseStatementContext struct {
    *antlr4.BaseParserRuleContext

    parser antlr4.Parser
}

func NewEmptyElseStatementContext() *ElseStatementContext {
    var p = new(ElseStatementContext)
    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( nil, -1 )
    p.RuleIndex = TParserRULE_elseStatement
    return p
}

func NewElseStatementContext(parser antlr4.Parser, parent antlr4.ParserRuleContext, invokingState int) *ElseStatementContext {

    var p = new(ElseStatementContext)

    p.BaseParserRuleContext = antlr4.NewBaseParserRuleContext( parent, invokingState )

    p.parser = parser
    p.RuleIndex = TParserRULE_elseStatement

    // TODO initialize list attrs

    return p
}


func (s *ElseStatementContext) getParser() antlr4.Parser { return s.parser }


func (s *ElseStatementContext) GetRuleContext() antlr4.RuleContext {
    // Go does not truly support inheritance nor virtual method calls, so we need to implement this directly
    return s
}

func (s *ElseStatementContext) EnterRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.EnterElseStatement(s)
    }
}
func (s *ElseStatementContext) ExitRule(listener antlr4.ParseTreeListener) {
    if listenerT, ok := listener.(TListener); ok {
        listenerT.ExitElseStatement(s)
    }
}
func (s *ElseStatementContext) Accept(visitor antlr4.ParseTreeVisitor) interface{} {
    switch t := visitor.(type) {
    case TVisitor:
        return t.VisitElseStatement(s)
    default:
        return t.VisitChildren(s)
    }
}



func (p *TParser) ElseStatement() IElseStatementContext {

    var localctx IElseStatementContext = NewElseStatementContext(p, p.GetParserRuleContext(), p.GetState())
    p.EnterRule(localctx, 8, TParserRULE_elseStatement)

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
    p.SetState(47)
    p.Match(TParserT__4)

    return localctx
}



