// Generated from T.g4 by ANTLR 4.5.1
package parser

import (
    "antlr4"
    "fmt"
)

// suppress unused import error, many tests
// require fmt.
var _ = fmt.Printf


var serializedLexerAtn = []uint16{ 3,1072,54993,33286,44333,17431,44785,
    36224,43741,2,7,31,8,1,4,2,9,2,4,3,9,3,4,4,9,4,4,5,9,5,4,6,9,6,3,2,3,
    2,3,3,3,3,3,4,6,4,19,10,4,13,4,14,4,20,3,5,6,5,24,10,5,13,5,14,5,25,
    3,6,3,6,3,6,3,6,2,2,7,3,3,5,4,7,5,9,6,11,7,3,2,3,4,2,12,12,34,34,32,
    2,3,3,2,2,2,2,5,3,2,2,2,2,7,3,2,2,2,2,9,3,2,2,2,2,11,3,2,2,2,3,13,3,
    2,2,2,5,15,3,2,2,2,7,18,3,2,2,2,9,23,3,2,2,2,11,27,3,2,2,2,13,14,7,63,
    2,2,14,4,3,2,2,2,15,16,7,45,2,2,16,6,3,2,2,2,17,19,4,99,124,2,18,17,
    3,2,2,2,19,20,3,2,2,2,20,18,3,2,2,2,20,21,3,2,2,2,21,8,3,2,2,2,22,24,
    4,50,59,2,23,22,3,2,2,2,24,25,3,2,2,2,25,23,3,2,2,2,25,26,3,2,2,2,26,
    10,3,2,2,2,27,28,9,2,2,2,28,29,3,2,2,2,29,30,8,6,2,2,30,12,3,2,2,2,5,
    2,20,25,3,8,2,2, }

var lexerDeserializer = antlr4.NewATNDeserializer(nil)
var lexerAtn = lexerDeserializer.DeserializeFromUInt16( serializedLexerAtn )

var lexerModeNames = []string{ "DEFAULT_MODE" }
var lexerLiteralNames = []string{ "", "'='", "'+'" }
var lexerSymbolicNames = []string{ "", "", "", "ID", "INT", "WS" }
var lexerRuleNames = []string{ "T__0", "T__1", "ID", "INT", "WS" }

type TLexer struct {
    *antlr4.BaseLexer

    modeNames []string
    // EOF string
}

func NewTLexer(input antlr4.CharStream) *TLexer {

    var lexerDecisionToDFA = make([]*antlr4.DFA,len(lexerAtn.DecisionToState))

    for index, ds := range lexerAtn.DecisionToState {
        lexerDecisionToDFA[index] = antlr4.NewDFA(ds, index)
    }

	lex := new(TLexer)

	lex.BaseLexer = antlr4.NewBaseLexer(input)

    lex.Interpreter = antlr4.NewLexerATNSimulator(lex, lexerAtn, lexerDecisionToDFA, antlr4.NewPredictionContextCache())

    lex.modeNames = lexerModeNames
    lex.RuleNames = lexerRuleNames
    lex.LiteralNames = lexerLiteralNames
    lex.SymbolicNames = lexerSymbolicNames
    lex.GrammarFileName = "T.g4"
    //lex.EOF = antlr4.TokenEOF

    return lex
}

const (
    TLexerT__0 = 1
    TLexerT__1 = 2
    TLexerID = 3
    TLexerINT = 4
    TLexerWS = 5
)

const (
)



