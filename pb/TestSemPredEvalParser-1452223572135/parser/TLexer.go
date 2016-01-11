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
    36224,43741,2,6,27,8,1,4,2,9,2,4,3,9,3,4,4,9,4,4,5,9,5,3,2,3,2,3,3,6,
    3,15,10,3,13,3,14,3,16,3,4,6,4,20,10,4,13,4,14,4,21,3,5,3,5,3,5,3,5,
    2,2,6,3,3,5,4,7,5,9,6,3,2,3,4,2,12,12,34,34,28,2,3,3,2,2,2,2,5,3,2,2,
    2,2,7,3,2,2,2,2,9,3,2,2,2,3,11,3,2,2,2,5,14,3,2,2,2,7,19,3,2,2,2,9,23,
    3,2,2,2,11,12,7,61,2,2,12,4,3,2,2,2,13,15,4,99,124,2,14,13,3,2,2,2,15,
    16,3,2,2,2,16,14,3,2,2,2,16,17,3,2,2,2,17,6,3,2,2,2,18,20,4,50,59,2,
    19,18,3,2,2,2,20,21,3,2,2,2,21,19,3,2,2,2,21,22,3,2,2,2,22,8,3,2,2,2,
    23,24,9,2,2,2,24,25,3,2,2,2,25,26,8,5,2,2,26,10,3,2,2,2,5,2,16,21,3,
    8,2,2, }

var lexerDeserializer = antlr4.NewATNDeserializer(nil)
var lexerAtn = lexerDeserializer.DeserializeFromUInt16( serializedLexerAtn )

var lexerModeNames = []string{ "DEFAULT_MODE" }
var lexerLiteralNames = []string{ "", "';'" }
var lexerSymbolicNames = []string{ "", "", "ID", "INT", "WS" }
var lexerRuleNames = []string{ "T__0", "ID", "INT", "WS" }

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
    TLexerID = 2
    TLexerINT = 3
    TLexerWS = 4
)

const (
)



