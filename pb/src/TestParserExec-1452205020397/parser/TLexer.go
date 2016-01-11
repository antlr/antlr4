// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestParserExec-1452205020397/parser/T.g4 by ANTLR 4.5.1
package parser

import (
    "antlr4"
    "fmt"
)

// suppress unused import error, many tests
// require fmt.
var _ = fmt.Printf


var serializedLexerAtn = []uint16{ 3,1072,54993,33286,44333,17431,44785,
    36224,43741,2,7,32,8,1,4,2,9,2,4,3,9,3,4,4,9,4,4,5,9,5,4,6,9,6,3,2,3,
    2,3,2,3,3,3,3,3,3,3,3,3,3,3,4,3,4,3,4,3,4,3,5,3,5,3,5,3,5,3,5,3,6,3,
    6,2,2,7,3,3,5,4,7,5,9,6,11,7,3,2,2,31,2,3,3,2,2,2,2,5,3,2,2,2,2,7,3,
    2,2,2,2,9,3,2,2,2,2,11,3,2,2,2,3,13,3,2,2,2,5,16,3,2,2,2,7,21,3,2,2,
    2,9,25,3,2,2,2,11,30,3,2,2,2,13,14,7,107,2,2,14,15,7,104,2,2,15,4,3,
    2,2,2,16,17,7,118,2,2,17,18,7,106,2,2,18,19,7,103,2,2,19,20,7,112,2,
    2,20,6,3,2,2,2,21,22,7,103,2,2,22,23,7,112,2,2,23,24,7,102,2,2,24,8,
    3,2,2,2,25,26,7,103,2,2,26,27,7,110,2,2,27,28,7,117,2,2,28,29,7,103,
    2,2,29,10,3,2,2,2,30,31,7,99,2,2,31,12,3,2,2,2,3,2,2, }

var lexerDeserializer = antlr4.NewATNDeserializer(nil)
var lexerAtn = lexerDeserializer.DeserializeFromUInt16( serializedLexerAtn )

var lexerModeNames = []string{ "DEFAULT_MODE" }
var lexerLiteralNames = []string{ "", "'if'", "'then'", "'end'", "'else'", 
                                  "'a'" }
var lexerSymbolicNames = []string{  }
var lexerRuleNames = []string{ "T__0", "T__1", "T__2", "T__3", "T__4" }

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
    TLexerT__2 = 3
    TLexerT__3 = 4
    TLexerT__4 = 5
)

const (
)



