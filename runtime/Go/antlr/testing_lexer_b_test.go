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

var lexerB_serializedLexerAtn = []int32{
	4, 0, 7, 38, 6, 65535, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 2, 3, 7, 3,
	2, 4, 7, 4, 2, 5, 7, 5, 2, 6, 7, 6, 1, 0, 4, 0, 17, 8, 0, 11, 0, 12, 0,
	18, 1, 1, 4, 1, 22, 8, 1, 11, 1, 12, 1, 23, 1, 2, 1, 2, 1, 3, 1, 3, 1,
	4, 1, 4, 1, 5, 1, 5, 1, 6, 4, 6, 35, 8, 6, 11, 6, 12, 6, 36, 0, 0, 7, 1,
	1, 3, 2, 5, 3, 7, 4, 9, 5, 11, 6, 13, 7, 1, 0, 0, 0, 40, 0, 1, 1, 0, 0,
	0, 0, 3, 1, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 7, 1, 0, 0, 0, 0, 9, 1, 0, 0,
	0, 0, 11, 1, 0, 0, 0, 0, 13, 1, 0, 0, 0, 1, 16, 1, 0, 0, 0, 3, 21, 1, 0,
	0, 0, 5, 25, 1, 0, 0, 0, 7, 27, 1, 0, 0, 0, 9, 29, 1, 0, 0, 0, 11, 31,
	1, 0, 0, 0, 13, 34, 1, 0, 0, 0, 15, 17, 2, 97, 122, 0, 16, 15, 1, 0, 0,
	0, 17, 18, 1, 0, 0, 0, 18, 16, 1, 0, 0, 0, 18, 19, 1, 0, 0, 0, 19, 2, 1,
	0, 0, 0, 20, 22, 2, 48, 57, 0, 21, 20, 1, 0, 0, 0, 22, 23, 1, 0, 0, 0,
	23, 21, 1, 0, 0, 0, 23, 24, 1, 0, 0, 0, 24, 4, 1, 0, 0, 0, 25, 26, 5, 59,
	0, 0, 26, 6, 1, 0, 0, 0, 27, 28, 5, 61, 0, 0, 28, 8, 1, 0, 0, 0, 29, 30,
	5, 43, 0, 0, 30, 10, 1, 0, 0, 0, 31, 32, 5, 42, 0, 0, 32, 12, 1, 0, 0,
	0, 33, 35, 5, 32, 0, 0, 34, 33, 1, 0, 0, 0, 35, 36, 1, 0, 0, 0, 36, 34,
	1, 0, 0, 0, 36, 37, 1, 0, 0, 0, 37, 14, 1, 0, 0, 0, 4, 0, 18, 23, 36, 0,
}

var lexerB_lexerDeserializer = NewATNDeserializer(nil)
var lexerB_lexerAtn = lexerB_lexerDeserializer.Deserialize(lexerB_serializedLexerAtn)

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
