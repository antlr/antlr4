// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

/*
LexerB is a lexer for testing purpose.

This file is generated from this grammer.

lexer grammar LexerB;

ID : 'a'..'z'+;
INT : '0'..'9'+;
SEMI : ';';
ASSIGN : '=';
PLUS : '+';
MULT : '*';
WS : ' '+;
*/

var lexerB_serializedLexerAtn = []uint16{
	3, 24715, 42794, 33075, 47597, 16764, 15335, 30598, 22884, 2, 9, 40, 8,
	1, 4, 2, 9, 2, 4, 3, 9, 3, 4, 4, 9, 4, 4, 5, 9, 5, 4, 6, 9, 6, 4, 7, 9,
	7, 4, 8, 9, 8, 3, 2, 6, 2, 19, 10, 2, 13, 2, 14, 2, 20, 3, 3, 6, 3, 24,
	10, 3, 13, 3, 14, 3, 25, 3, 4, 3, 4, 3, 5, 3, 5, 3, 6, 3, 6, 3, 7, 3, 7,
	3, 8, 6, 8, 37, 10, 8, 13, 8, 14, 8, 38, 2, 2, 9, 3, 3, 5, 4, 7, 5, 9,
	6, 11, 7, 13, 8, 15, 9, 3, 2, 2, 2, 42, 2, 3, 3, 2, 2, 2, 2, 5, 3, 2, 2,
	2, 2, 7, 3, 2, 2, 2, 2, 9, 3, 2, 2, 2, 2, 11, 3, 2, 2, 2, 2, 13, 3, 2,
	2, 2, 2, 15, 3, 2, 2, 2, 3, 18, 3, 2, 2, 2, 5, 23, 3, 2, 2, 2, 7, 27, 3,
	2, 2, 2, 9, 29, 3, 2, 2, 2, 11, 31, 3, 2, 2, 2, 13, 33, 3, 2, 2, 2, 15,
	36, 3, 2, 2, 2, 17, 19, 4, 99, 124, 2, 18, 17, 3, 2, 2, 2, 19, 20, 3, 2,
	2, 2, 20, 18, 3, 2, 2, 2, 20, 21, 3, 2, 2, 2, 21, 4, 3, 2, 2, 2, 22, 24,
	4, 50, 59, 2, 23, 22, 3, 2, 2, 2, 24, 25, 3, 2, 2, 2, 25, 23, 3, 2, 2,
	2, 25, 26, 3, 2, 2, 2, 26, 6, 3, 2, 2, 2, 27, 28, 7, 61, 2, 2, 28, 8, 3,
	2, 2, 2, 29, 30, 7, 63, 2, 2, 30, 10, 3, 2, 2, 2, 31, 32, 7, 45, 2, 2,
	32, 12, 3, 2, 2, 2, 33, 34, 7, 44, 2, 2, 34, 14, 3, 2, 2, 2, 35, 37, 7,
	34, 2, 2, 36, 35, 3, 2, 2, 2, 37, 38, 3, 2, 2, 2, 38, 36, 3, 2, 2, 2, 38,
	39, 3, 2, 2, 2, 39, 16, 3, 2, 2, 2, 6, 2, 20, 25, 38, 2,
}

var lexerB_lexerDeserializer = NewATNDeserializer(nil)
var lexerB_lexerAtn = lexerB_lexerDeserializer.DeserializeFromUInt16(lexerB_serializedLexerAtn)

var lexerB_lexerChannelNames = []string{
	"DEFAULT_TOKEN_CHANNEL", "HIDDEN",
}

var lexerB_lexerModeNames = []string{
	"DEFAULT_MODE",
}

var lexerB_lexerLiteralNames = []string{
	"", "", "", "';'", "'='", "'+'", "'*'",
}

var lexerB_lexerSymbolicNames = []string{
	"", "ID", "INT", "SEMI", "ASSIGN", "PLUS", "MULT", "WS",
}

var lexerB_lexerRuleNames = []string{
	"ID", "INT", "SEMI", "ASSIGN", "PLUS", "MULT", "WS",
}

type LexerB struct {
	*BaseLexer
	channelNames []string
	modeNames    []string
	// TODO: EOF string
}

var lexerB_lexerDecisionToDFA = make([]*DFA, len(lexerB_lexerAtn.DecisionToState))

func init() {
	for index, ds := range lexerB_lexerAtn.DecisionToState {
		lexerB_lexerDecisionToDFA[index] = NewDFA(ds, index)
	}
}

func NewLexerB(input CharStream) *LexerB {
	l := new(LexerB)

	l.BaseLexer = NewBaseLexer(input)
	l.Interpreter = NewLexerATNSimulator(l, lexerB_lexerAtn, lexerB_lexerDecisionToDFA, NewPredictionContextCache())

	l.channelNames = lexerB_lexerChannelNames
	l.modeNames = lexerB_lexerModeNames
	l.RuleNames = lexerB_lexerRuleNames
	l.LiteralNames = lexerB_lexerLiteralNames
	l.SymbolicNames = lexerB_lexerSymbolicNames
	l.GrammarFileName = "LexerB.g4"
	// TODO: l.EOF = TokenEOF

	return l
}

// LexerB tokens.
const (
	LexerBID     = 1
	LexerBINT    = 2
	LexerBSEMI   = 3
	LexerBASSIGN = 4
	LexerBPLUS   = 5
	LexerBMULT   = 6
	LexerBWS     = 7
)
