// Code generated from test.g4 by ANTLR 4.12.0. DO NOT EDIT.

package test

import (
	"fmt"
	"sync"
	"unicode"

	"github.com/antlr/antlr4/runtime/Go/antlr/v4"
)

// Suppress unused import error
var _ = fmt.Printf
var _ = sync.Once{}
var _ = unicode.IsLetter

type testLexer struct {
	*antlr.BaseLexer
	channelNames []string
	modeNames    []string
	// TODO: EOF string
}

var testlexerLexerStaticData struct {
	once                   sync.Once
	serializedATN          []int32
	channelNames           []string
	modeNames              []string
	literalNames           []string
	symbolicNames          []string
	ruleNames              []string
	predictionContextCache *antlr.PredictionContextCache
	atn                    *antlr.ATN
	decisionToDFA          []*antlr.DFA
}

func testlexerLexerInit() {
	staticData := &testlexerLexerStaticData
	staticData.channelNames = []string{
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN",
	}
	staticData.modeNames = []string{
		"DEFAULT_MODE",
	}
	staticData.literalNames = []string{
		"", "';'", "'and'",
	}
	staticData.symbolicNames = []string{
		"", "", "AND", "IDENTIFIER", "WS",
	}
	staticData.ruleNames = []string{
		"T__0", "AND", "IDENTIFIER", "WS",
	}
	staticData.predictionContextCache = antlr.NewPredictionContextCache()
	staticData.serializedATN = []int32{
		4, 0, 4, 27, 6, -1, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 2, 3, 7, 3, 1,
		0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 2, 17, 8, 2, 11, 2, 12, 2, 18,
		1, 3, 4, 3, 22, 8, 3, 11, 3, 12, 3, 23, 1, 3, 1, 3, 0, 0, 4, 1, 1, 3, 2,
		5, 3, 7, 4, 1, 0, 2, 3, 0, 65, 90, 95, 95, 97, 122, 3, 0, 9, 10, 13, 13,
		32, 32, 28, 0, 1, 1, 0, 0, 0, 0, 3, 1, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 7,
		1, 0, 0, 0, 1, 9, 1, 0, 0, 0, 3, 11, 1, 0, 0, 0, 5, 16, 1, 0, 0, 0, 7,
		21, 1, 0, 0, 0, 9, 10, 5, 59, 0, 0, 10, 2, 1, 0, 0, 0, 11, 12, 5, 97, 0,
		0, 12, 13, 5, 110, 0, 0, 13, 14, 5, 100, 0, 0, 14, 4, 1, 0, 0, 0, 15, 17,
		7, 0, 0, 0, 16, 15, 1, 0, 0, 0, 17, 18, 1, 0, 0, 0, 18, 16, 1, 0, 0, 0,
		18, 19, 1, 0, 0, 0, 19, 6, 1, 0, 0, 0, 20, 22, 7, 1, 0, 0, 21, 20, 1, 0,
		0, 0, 22, 23, 1, 0, 0, 0, 23, 21, 1, 0, 0, 0, 23, 24, 1, 0, 0, 0, 24, 25,
		1, 0, 0, 0, 25, 26, 6, 3, 0, 0, 26, 8, 1, 0, 0, 0, 3, 0, 18, 23, 1, 6,
		0, 0,
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

// testLexerInit initializes any static state used to implement testLexer. By default the
// static state used to implement the lexer is lazily initialized during the first call to
// NewtestLexer(). You can call this function if you wish to initialize the static state ahead
// of time.
func TestLexerInit() {
	staticData := &testlexerLexerStaticData
	staticData.once.Do(testlexerLexerInit)
}

// NewtestLexer produces a new lexer instance for the optional input antlr.CharStream.
func NewtestLexer(input antlr.CharStream) *testLexer {
	TestLexerInit()
	l := new(testLexer)
	l.BaseLexer = antlr.NewBaseLexer(input)
	staticData := &testlexerLexerStaticData
	l.Interpreter = antlr.NewLexerATNSimulator(l, staticData.atn, staticData.decisionToDFA, staticData.predictionContextCache)
	l.channelNames = staticData.channelNames
	l.modeNames = staticData.modeNames
	l.RuleNames = staticData.ruleNames
	l.LiteralNames = staticData.literalNames
	l.SymbolicNames = staticData.symbolicNames
	l.GrammarFileName = "test.g4"
	// TODO: l.EOF = antlr.TokenEOF

	return l
}

// testLexer tokens.
const (
	testLexerT__0       = 1
	testLexerAND        = 2
	testLexerIDENTIFIER = 3
	testLexerWS         = 4
)
