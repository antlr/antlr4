package antlr4

import (
	"fmt"
	"strconv"
)

// A lexer is recognizer that draws input symbols from a character stream.
//  lexer grammars result in a subclass of this object. A Lexer object
//  uses simplified Match() and error recovery mechanisms in the interest
//  of speed.
///

type Lexer interface {
	TokenSource
	Recognizer

	setChannel(int)
	pushMode(int)
	popMode() int
	setType(int)
	mode(int)
}

type BaseLexer struct {
	*BaseRecognizer

	Interpreter *LexerATNSimulator
	TokenStartCharIndex int
	TokenStartLine int
	TokenStartColumn int
	ActionType int

	_input                  CharStream
	_factory                TokenFactory
	_tokenFactorySourcePair *TokenSourceCharStreamPair
	_token                  Token
	_hitEOF                 bool
	_channel                int
	_type                   int
	_modeStack              IntStack
	_mode                   int
	_text                   string

}

func NewBaseLexer(input CharStream) *BaseLexer {

	lexer := new(BaseLexer)

	lexer.BaseRecognizer = NewBaseRecognizer()

	lexer._input = input
	lexer._factory = CommonTokenFactoryDEFAULT
	lexer._tokenFactorySourcePair = &TokenSourceCharStreamPair{lexer, input}

	lexer.Interpreter = nil // child classes must populate it

	// The goal of all lexer rules/methods is to create a token object.
	// l is an instance variable as multiple rules may collaborate to
	// create a single token. NextToken will return l object after
	// Matching lexer rule(s). If you subclass to allow multiple token
	// emissions, then set l to the last token to be Matched or
	// something nonnil so that the auto token emit mechanism will not
	// emit another token.
	lexer._token = nil

	// What character index in the stream did the current token start at?
	// Needed, for example, to get the text for current token. Set at
	// the start of NextToken.
	lexer.TokenStartCharIndex = -1

	// The line on which the first character of the token resides///
	lexer.TokenStartLine = -1

	// The character position of first character within the line///
	lexer.TokenStartColumn = -1

	// Once we see EOF on char stream, next token will be EOF.
	// If you have DONE : EOF  then you see DONE EOF.
	lexer._hitEOF = false

	// The channel number for the current token///
	lexer._channel = TokenDefaultChannel

	// The token type for the current token///
	lexer._type = TokenInvalidType

	lexer._modeStack = make([]int, 0)
	lexer._mode = LexerDefaultMode

	// You can set the text for the current token to override what is in
	// the input char buffer. Use setText() or can set l instance var.
	// /
	lexer._text = ""

	return lexer
}

const (
	LexerDefaultMode = 0
	LexerMore        = -2
	LexerSkip        = -3
)

const (
	LexerDefaultTokenChannel = TokenDefaultChannel
	LexerHidden              = TokenHiddenChannel
	LexerMinCharValue        = '\u0000'
	LexerMaxCharValue        = '\uFFFE'
)

func (l *BaseLexer) reset() {
	// wack Lexer state variables
	if l._input != nil {
		l._input.Seek(0) // rewind the input
	}
	l._token = nil
	l._type = TokenInvalidType
	l._channel = TokenDefaultChannel
	l.TokenStartCharIndex = -1
	l.TokenStartColumn = -1
	l.TokenStartLine = -1
	l._text = ""

	l._hitEOF = false
	l._mode = LexerDefaultMode
	l._modeStack = make([]int, 0)

	l.Interpreter.reset()
}

func (l *BaseLexer) GetInputStream() CharStream {
	return l._input
}

func (l *BaseLexer) GetSourceName() string {
	return l.GrammarFileName
}

func (l *BaseLexer) setChannel(v int) {
	l._channel = v
}

func (l *BaseLexer) GetTokenFactory() TokenFactory {
	return l._factory
}

func (l *BaseLexer) setTokenFactory(f TokenFactory) {
	l._factory = f
}

func (l *BaseLexer) safeMatch() (ret int) {

	// previously in catch block
	defer func() {
		if e := recover(); e != nil {
			if re, ok := e.(RecognitionException); ok {
				l.notifyListeners(re) // Report error
				l.Recover(re)
				ret = LexerSkip // default
			}
		}
	}()

	return l.Interpreter.Match(l._input, l._mode)
}

// Return a token from l source i.e., Match a token on the char stream.
func (l *BaseLexer) NextToken() Token {
	if l._input == nil {
		panic("NextToken requires a non-nil input stream.")
	}

	var tokenStartMarker = l._input.Mark()

	// previously in finally block
	defer func() {
		// make sure we release marker after Match or
		// unbuffered char stream will keep buffering
		l._input.Release(tokenStartMarker)
	}()

	for true {
		if l._hitEOF {
			l.emitEOF()
			return l._token
		}
		l._token = nil
		l._channel = TokenDefaultChannel
		l.TokenStartCharIndex = l._input.Index()
		l.TokenStartColumn = l.Interpreter.column
		l.TokenStartLine = l.Interpreter.line
		l._text = ""
		var continueOuter = false
		for true {
			l._type = TokenInvalidType
			var ttype = LexerSkip

			ttype = l.safeMatch()

			if l._input.LA(1) == TokenEOF {
				l._hitEOF = true
			}
			if l._type == TokenInvalidType {
				l._type = ttype
			}
			if l._type == LexerSkip {
				continueOuter = true
				break
			}
			if l._type != LexerMore {
				break
			}
			if PortDebug {
				fmt.Println("lex inner loop")
			}
		}

		if PortDebug {
			fmt.Println("lex loop")
		}
		if continueOuter {
			continue
		}
		if l._token == nil {
			l.emit()
		}
		return l._token
	}

	return nil
}

// Instruct the lexer to Skip creating a token for current lexer rule
// and look for another token. NextToken() knows to keep looking when
// a lexer rule finishes with token set to SKIP_TOKEN. Recall that
// if token==nil at end of any token rule, it creates one for you
// and emits it.
// /
func (l *BaseLexer) Skip() {
	l._type = LexerSkip
}

func (l *BaseLexer) More() {
	l._type = LexerMore
}

func (l *BaseLexer) mode(m int) {
	l._mode = m
}

func (l *BaseLexer) pushMode(m int) {
	if LexerATNSimulatorDebug {
		fmt.Println("pushMode " + strconv.Itoa(m))
	}
	l._modeStack.Push(l._mode)
	l.mode(m)
}

func (l *BaseLexer) popMode() int {
	if len(l._modeStack) == 0 {
		panic("Empty Stack")
	}
	if LexerATNSimulatorDebug {
		fmt.Println("popMode back to " + fmt.Sprint(l._modeStack[0:len(l._modeStack)-1]))
	}
	i, _ := l._modeStack.Pop()
	l.mode(i)
	return l._mode
}

func (l *BaseLexer) inputStream() CharStream {
	return l._input
}

func (l *BaseLexer) setInputStream(input CharStream) {
	l._input = nil
	l._tokenFactorySourcePair = &TokenSourceCharStreamPair{l, l._input}
	l.reset()
	l._input = input
	l._tokenFactorySourcePair = &TokenSourceCharStreamPair{l, l._input}
}

// By default does not support multiple emits per NextToken invocation
// for efficiency reasons. Subclass and override l method, NextToken,
// and GetToken (to push tokens into a list and pull from that list
// rather than a single variable as l implementation does).
// /
func (l *BaseLexer) emitToken(token Token) {
	l._token = token
}

// The standard method called to automatically emit a token at the
// outermost lexical rule. The token object should point into the
// char buffer start..stop. If there is a text override in 'text',
// use that to set the token's text. Override l method to emit
// custom Token objects or provide a Newfactory.
// /
func (l *BaseLexer) emit() Token {
	if PortDebug {
		fmt.Println("emit")
	}
	var t = l._factory.Create(l._tokenFactorySourcePair, l._type, l._text, l._channel, l.TokenStartCharIndex, l.getCharIndex()-1, l.TokenStartLine, l.TokenStartColumn)
	l.emitToken(t)
	return t
}

func (l *BaseLexer) emitEOF() Token {
	cpos := l.GetCharPositionInLine()
	lpos := l.GetLine()
	if PortDebug {
		fmt.Println("emitEOF")
	}
	var eof = l._factory.Create(l._tokenFactorySourcePair, TokenEOF, "", TokenDefaultChannel, l._input.Index(), l._input.Index()-1, lpos, cpos)
	l.emitToken(eof)
	return eof
}

func (l *BaseLexer) GetCharPositionInLine() int {
	return l.Interpreter.column
}

func (l *BaseLexer) GetLine() int {
	return l.Interpreter.line
}

func (l *BaseLexer) getType() int {
	return l._type
}

func (l *BaseLexer) setType(t int) {
	l._type = t
}

// What is the index of the current character of lookahead?///
func (l *BaseLexer) getCharIndex() int {
	return l._input.Index()
}

// Return the text Matched so far for the current token or any text override.
//Set the complete text of l token it wipes any previous changes to the text.
func (l *BaseLexer) text() string {
	if l._text != "" {
		return l._text
	} else {
		return l.Interpreter.GetText(l._input)
	}
}

func (l *BaseLexer) setText(text string) {
	l._text = text
}

func (this *BaseLexer) GetATN() *ATN {
	return this.Interpreter.atn
}

// Return a list of all Token objects in input char stream.
// Forces load of all tokens. Does not include EOF token.
// /
func (l *BaseLexer) getAllTokens() []Token {
	if PortDebug {
		fmt.Println("getAllTokens")
	}
	var tokens = make([]Token, 0)
	var t = l.NextToken()
	for t.GetTokenType() != TokenEOF {
		tokens = append(tokens, t)
		if PortDebug {
			fmt.Println("getAllTokens")
		}
		t = l.NextToken()
	}
	return tokens
}

func (l *BaseLexer) notifyListeners(e RecognitionException) {
	var start = l.TokenStartCharIndex
	var stop = l._input.Index()
	var text = l._input.GetTextFromInterval(NewInterval(start, stop))
	var msg = "token recognition error at: '" + text + "'"
	var listener = l.GetErrorListenerDispatch()
	listener.SyntaxError(l, nil, l.TokenStartLine, l.TokenStartColumn, msg, e)
}

func (l *BaseLexer) getErrorDisplayForChar(c rune) string {
	if c == TokenEOF {
		return "<EOF>"
	} else if c == '\n' {
		return "\\n"
	} else if c == '\t' {
		return "\\t"
	} else if c == '\r' {
		return "\\r"
	} else {
		return string(c)
	}
}

func (l *BaseLexer) getCharErrorDisplay(c rune) string {
	return "'" + l.getErrorDisplayForChar(c) + "'"
}

// Lexers can normally Match any char in it's vocabulary after Matching
// a token, so do the easy thing and just kill a character and hope
// it all works out. You can instead use the rule invocation stack
// to do sophisticated error recovery if you are in a fragment rule.
// /
func (l *BaseLexer) Recover(re RecognitionException) {
	if l._input.LA(1) != TokenEOF {
		if _, ok := re.(*LexerNoViableAltException); ok {
			// Skip a char and try again
			l.Interpreter.consume(l._input)
		} else {
			// TODO: Do we lose character or line position information?
			l._input.Consume()
		}
	}
}
