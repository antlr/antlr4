// Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
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

import (
	"fmt"
	"sync"
	"unicode"
)

// Suppress unused import error
var _ = fmt.Printf
var _ = sync.Once{}
var _ = unicode.IsLetter

type LexerB struct {
	*BaseLexer
	channelNames []string
	modeNames    []string
	// TODO: EOF string
}

var lexerbLexerStaticData struct {
	once                   sync.Once
	serializedATN          []int32
	channelNames           []string
	modeNames              []string
	literalNames           []string
	symbolicNames          []string
	ruleNames              []string
	predictionContextCache *PredictionContextCache
	atn                    *ATN
	decisionToDFA          []*DFA
}

func lexerbLexerInit() {
	staticData := &lexerbLexerStaticData
	staticData.channelNames = []string{
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN",
	}
	staticData.modeNames = []string{
		"DEFAULT_MODE",
	}
	staticData.literalNames = []string{
		"", "", "", "';'", "'='", "'+'", "'*'",
	}
	staticData.symbolicNames = []string{
		"", "ID", "INT", "SEMI", "ASSIGN", "PLUS", "MULT", "WS",
	}
	staticData.ruleNames = []string{
		"ID", "INT", "SEMI", "ASSIGN", "PLUS", "MULT", "WS",
	}
	staticData.predictionContextCache = NewPredictionContextCache()
	staticData.serializedATN = []int32{
		4, 0, 7, 38, 6, -1, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 2, 3, 7, 3, 2,
		4, 7, 4, 2, 5, 7, 5, 2, 6, 7, 6, 1, 0, 4, 0, 17, 8, 0, 11, 0, 12, 0, 18,
		1, 1, 4, 1, 22, 8, 1, 11, 1, 12, 1, 23, 1, 2, 1, 2, 1, 3, 1, 3, 1, 4, 1,
		4, 1, 5, 1, 5, 1, 6, 4, 6, 35, 8, 6, 11, 6, 12, 6, 36, 0, 0, 7, 1, 1, 3,
		2, 5, 3, 7, 4, 9, 5, 11, 6, 13, 7, 1, 0, 0, 40, 0, 1, 1, 0, 0, 0, 0, 3,
		1, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 7, 1, 0, 0, 0, 0, 9, 1, 0, 0, 0, 0, 11,
		1, 0, 0, 0, 0, 13, 1, 0, 0, 0, 1, 16, 1, 0, 0, 0, 3, 21, 1, 0, 0, 0, 5,
		25, 1, 0, 0, 0, 7, 27, 1, 0, 0, 0, 9, 29, 1, 0, 0, 0, 11, 31, 1, 0, 0,
		0, 13, 34, 1, 0, 0, 0, 15, 17, 2, 97, 122, 0, 16, 15, 1, 0, 0, 0, 17, 18,
		1, 0, 0, 0, 18, 16, 1, 0, 0, 0, 18, 19, 1, 0, 0, 0, 19, 2, 1, 0, 0, 0,
		20, 22, 2, 48, 57, 0, 21, 20, 1, 0, 0, 0, 22, 23, 1, 0, 0, 0, 23, 21, 1,
		0, 0, 0, 23, 24, 1, 0, 0, 0, 24, 4, 1, 0, 0, 0, 25, 26, 5, 59, 0, 0, 26,
		6, 1, 0, 0, 0, 27, 28, 5, 61, 0, 0, 28, 8, 1, 0, 0, 0, 29, 30, 5, 43, 0,
		0, 30, 10, 1, 0, 0, 0, 31, 32, 5, 42, 0, 0, 32, 12, 1, 0, 0, 0, 33, 35,
		5, 32, 0, 0, 34, 33, 1, 0, 0, 0, 35, 36, 1, 0, 0, 0, 36, 34, 1, 0, 0, 0,
		36, 37, 1, 0, 0, 0, 37, 14, 1, 0, 0, 0, 4, 0, 18, 23, 36, 0,
	}
	deserializer := NewATNDeserializer(nil)
	staticData.atn = deserializer.Deserialize(staticData.serializedATN)
	atn := staticData.atn
	staticData.decisionToDFA = make([]*DFA, len(atn.DecisionToState))
	decisionToDFA := staticData.decisionToDFA
	for index, state := range atn.DecisionToState {
		decisionToDFA[index] = NewDFA(state, index)
	}
}

// LexerBInit initializes any static state used to implement LexerB. By default the
// static state used to implement the lexer is lazily initialized during the first call to
// NewLexerB(). You can call this function if you wish to initialize the static state ahead
// of time.
func LexerBInit() {
	staticData := &lexerbLexerStaticData
	staticData.once.Do(lexerbLexerInit)
}

// NewLexerB produces a new lexer instance for the optional input antlr.CharStream.
func NewLexerB(input CharStream) *LexerB {
	LexerBInit()
	l := new(LexerB)

	l.BaseLexer = NewBaseLexer(input)
	staticData := &lexerbLexerStaticData
	l.Interpreter = NewLexerATNSimulator(l, staticData.atn, staticData.decisionToDFA, staticData.predictionContextCache)
	l.channelNames = staticData.channelNames
	l.modeNames = staticData.modeNames
	l.RuleNames = staticData.ruleNames
	l.LiteralNames = staticData.literalNames
	l.SymbolicNames = staticData.symbolicNames
	l.GrammarFileName = "LexerB.g4"
	// TODO: l.EOF = antlr.TokenEOF

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
