// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestLexerExec-1452198938271/parser/PositionAdjustingLexer.g4 by ANTLR 4.5.1
package parser

import (
    "antlr4"
    "fmt"
)

// suppress unused import error, many tests
// require fmt.
var _ = fmt.Printf


var serializedLexerAtn = []uint16{ 3,1072,54993,33286,44333,17431,44785,
    36224,43741,2,10,72,8,1,4,2,9,2,4,3,9,3,4,4,9,4,4,5,9,5,4,6,9,6,4,7,
    9,7,4,8,9,8,4,9,9,9,4,10,9,10,3,2,3,2,3,3,3,3,3,3,3,4,3,4,3,5,3,5,3,
    5,3,5,3,5,3,5,3,5,3,5,3,5,3,5,3,6,3,6,3,6,5,6,42,10,6,3,6,3,6,3,7,3,
    7,7,7,48,10,7,12,7,14,7,51,11,7,3,8,7,8,54,10,8,12,8,14,8,57,11,8,3,
    9,6,9,60,10,9,13,9,14,9,61,3,9,3,9,3,10,6,10,67,10,10,13,10,14,10,68,
    3,10,3,10,2,2,11,3,3,5,4,7,5,9,6,11,7,13,8,15,2,17,9,19,10,3,2,7,5,2,
    67,92,97,97,99,124,6,2,50,59,67,92,97,97,99,124,5,2,11,12,15,15,34,34,
    4,2,12,12,15,15,4,2,11,11,34,34,75,2,3,3,2,2,2,2,5,3,2,2,2,2,7,3,2,2,
    2,2,9,3,2,2,2,2,11,3,2,2,2,2,13,3,2,2,2,2,17,3,2,2,2,2,19,3,2,2,2,3,
    21,3,2,2,2,5,23,3,2,2,2,7,26,3,2,2,2,9,28,3,2,2,2,11,38,3,2,2,2,13,45,
    3,2,2,2,15,55,3,2,2,2,17,59,3,2,2,2,19,66,3,2,2,2,21,22,7,63,2,2,22,
    4,3,2,2,2,23,24,7,45,2,2,24,25,7,63,2,2,25,6,3,2,2,2,26,27,7,125,2,2,
    27,8,3,2,2,2,28,29,7,118,2,2,29,30,7,113,2,2,30,31,7,109,2,2,31,32,7,
    103,2,2,32,33,7,112,2,2,33,34,7,117,2,2,34,35,3,2,2,2,35,36,5,15,8,2,
    36,37,7,125,2,2,37,10,3,2,2,2,38,39,5,13,7,2,39,41,5,15,8,2,40,42,7,
    45,2,2,41,40,3,2,2,2,41,42,3,2,2,2,42,43,3,2,2,2,43,44,7,63,2,2,44,12,
    3,2,2,2,45,49,9,2,2,2,46,48,9,3,2,2,47,46,3,2,2,2,48,51,3,2,2,2,49,47,
    3,2,2,2,49,50,3,2,2,2,50,14,3,2,2,2,51,49,3,2,2,2,52,54,9,4,2,2,53,52,
    3,2,2,2,54,57,3,2,2,2,55,53,3,2,2,2,55,56,3,2,2,2,56,16,3,2,2,2,57,55,
    3,2,2,2,58,60,9,5,2,2,59,58,3,2,2,2,60,61,3,2,2,2,61,59,3,2,2,2,61,62,
    3,2,2,2,62,63,3,2,2,2,63,64,8,9,2,2,64,18,3,2,2,2,65,67,9,6,2,2,66,65,
    3,2,2,2,67,68,3,2,2,2,68,66,3,2,2,2,68,69,3,2,2,2,69,70,3,2,2,2,70,71,
    8,10,2,2,71,20,3,2,2,2,8,2,41,49,55,61,68,3,8,2,2, }

var lexerDeserializer = antlr4.NewATNDeserializer(nil)
var lexerAtn = lexerDeserializer.DeserializeFromUInt16( serializedLexerAtn )

var lexerModeNames = []string{ "DEFAULT_MODE" }
var lexerLiteralNames = []string{ "", "'='", "'+='", "'{'" }
var lexerSymbolicNames = []string{ "", "ASSIGN", "PLUS_ASSIGN", "LCURLY", 
                                   "TOKENS", "LABEL", "IDENTIFIER", "NEWLINE", 
                                   "WS" }
var lexerRuleNames = []string{ "ASSIGN", "PLUS_ASSIGN", "LCURLY", "TOKENS", 
                               "LABEL", "IDENTIFIER", "IGNORED", "NEWLINE", 
                               "WS" }

type PositionAdjustingLexer struct {
    *antlr4.BaseLexer

    modeNames []string
    // EOF string
}

func NewPositionAdjustingLexer(input antlr4.CharStream) *PositionAdjustingLexer {

    var lexerDecisionToDFA = make([]*antlr4.DFA,len(lexerAtn.DecisionToState))

    for index, ds := range lexerAtn.DecisionToState {
        lexerDecisionToDFA[index] = antlr4.NewDFA(ds, index)
    }

	lex := new(PositionAdjustingLexer)

	lex.BaseLexer = antlr4.NewBaseLexer(input)

    lex.Interpreter = antlr4.NewLexerATNSimulator(lex, lexerAtn, lexerDecisionToDFA, antlr4.NewPredictionContextCache())

    lex.modeNames = lexerModeNames
    lex.RuleNames = lexerRuleNames
    lex.LiteralNames = lexerLiteralNames
    lex.SymbolicNames = lexerSymbolicNames
    lex.GrammarFileName = "PositionAdjustingLexer.g4"
    //lex.EOF = antlr4.TokenEOF

    return lex
}

const (
    PositionAdjustingLexerASSIGN = 1
    PositionAdjustingLexerPLUS_ASSIGN = 2
    PositionAdjustingLexerLCURLY = 3
    PositionAdjustingLexerTOKENS = 4
    PositionAdjustingLexerLABEL = 5
    PositionAdjustingLexerIDENTIFIER = 6
    PositionAdjustingLexerNEWLINE = 7
    PositionAdjustingLexerWS = 8
)

const (
)


package antlrtest

type PositionAdjustingLexer struct {
	antlr4.*BaseLexer
}

func NewPositionAdjustingLexer(input antlr4.CharStream) *PositionAdjustingLexer {
	l := new(PositionAdjustingLexer)
	l.BaseLexer = antlr4.NewBaseLexer( input )
	return l
}

func (this *PositionAdjustingLexer) NextToken() *Token {

	_,ok := this._interp.(*PositionAdjustingLexerATNSimulator)

	if !ok {
		this._interp = NewPositionAdjustingLexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache)
	}

	return this.BaseLexer.NextToken()
}

func (this *PositionAdjustingLexer) Emit() *Token {
	switch (_type) {
	case TOKENS:
		this.HandleAcceptPositionForKeyword("tokens")

	case LABEL:
		this.HandleAcceptPositionForIdentifier()
	}

	return this.BaseLexer.Emit()
}

func (this *PositionAdjustingLexer) HandleAcceptPositionForIdentifier() bool {
 	tokenText := GetText()
	identifierLength int = 0
	for identifierLength < len(tokenText) && isIdentifierChar(tokenText.charAt(identifierLength)) {
		identifierLength += 1
	}

	if GetInputStream().Index() > _tokenStartCharIndex + identifierLength {
		offset int = identifierLength - 1
		this.GetInterpreter().ResetAcceptPosition(this.GetInputStream(), this.TokenStartCharIndex + offset, this.TokenStartLine, this.TokenStartCharPositionInLine + offset)
		return true
	}

	return false
}

func (this *PositionAdjustingLexer) HandleAcceptPositionForKeyword(keyword string) bool {
	if this.GetInputStream().Index() > this.TokenStartCharIndex + len(keyword) {
		offset := len(keyword) - 1
		this.GetInterpreter().ResetAcceptPosition(this.GetInputStream(), this.TokenStartCharIndex + offset, this.TokenStartLine, this.TokenStartCharPositionInLine + offset)
		return true
	}

	return false
}

func (s *PositionAdjustingLexer) GetInterpreter() *LexerATNSimulator {
	return s // return super.(*PositionAdjustingLexerATNSimulator).GetInterpreter()
}

func isIdentifierChar(c rune) bool {
	return Character.isLetterOrDigit(c) || c == '_'
}

type PositionAdjustingLexerATNSimulator struct {
    *antlr4.LexerATNSimulator
}

func NewPositionAdjustingLexerATNSimulator(recog antlr4.Lexer, atn *antlr4.ATN, decisionToDFA []*antlr4.DFA, sharedContextCache *PredictionContextCache) *PositionAdjustingLexerATNSimulator {

    l := new(PositionAdjustingLexerATNSimulator)

	l.LexerATNSimulator = antlr4.NewLexerATNSimulator(recog, atn, decisionToDFA, sharedContextCache)

	return l
}

func (this *NewPositionAdjustingLexerATNSimulator) ResetAcceptPosition(input CharStream, index, line, charPositionInLine int) {
    this.input.seek(index);
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.consume(input);
}




