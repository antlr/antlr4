lexer grammar PositionAdjustingLexer;

@members {
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

}

ASSIGN : '=' ;
PLUS_ASSIGN : '+=' ;
LCURLY:	'{';

// 'tokens' followed by '{'
TOKENS : 'tokens' IGNORED '{';

// IDENTIFIER followed by '+=' or '='
LABEL
	:	IDENTIFIER IGNORED '+'? '='
	;

IDENTIFIER
	:	[a-zA-Z_] [a-zA-Z0-9_]*
	;

fragment
IGNORED
	:	[ \t\r\n]*
	;

NEWLINE
	:	[\r\n]+ -> skip
	;

WS
	:	[ \t]+ -> skip
	;