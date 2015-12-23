package antlr4

import (
	"fmt"
	"strconv"
)

// A lexer is recognizer that draws input symbols from a character stream.
//  lexer grammars result in a subclass of this object. A Lexer object
//  uses simplified match() and error recovery mechanisms in the interest
//  of speed.
///

type ILexer interface {
	TokenSource
	IRecognizer

	setChannel(int)
	pushMode(int)
	popMode() int
	setType(int)
	mode(int)
}

type Lexer struct {
	Recognizer

	Interpreter *LexerATNSimulator

	_input                  CharStream
	_factory                TokenFactory
	_tokenFactorySourcePair *TokenSourceCharStreamPair
	_token                  *Token
	_tokenStartCharIndex    int
	_tokenStartLine         int
	_tokenStartColumn       int
	_hitEOF                 bool
	_channel                int
	_type                   int
	_modeStack              IntStack
	_mode                   int
	_text                   *string
	actionType              int
}

func NewLexer(input CharStream) *Lexer {

	lexer := new(Lexer)

	lexer.InitRecognizer()
	lexer.InitLexer(input)

	return lexer
}

func (l *Lexer) InitLexer(input CharStream) {

	l._input = input
	l._factory = CommonTokenFactoryDEFAULT
	l._tokenFactorySourcePair = &TokenSourceCharStreamPair{l, input}

	l.Interpreter = nil // child classes must populate it

	// The goal of all lexer rules/methods is to create a token object.
	// l is an instance variable as multiple rules may collaborate to
	// create a single token. nextToken will return l object after
	// matching lexer rule(s). If you subclass to allow multiple token
	// emissions, then set l to the last token to be matched or
	// something nonnil so that the auto token emit mechanism will not
	// emit another token.
	l._token = nil

	// What character index in the stream did the current token start at?
	// Needed, for example, to get the text for current token. Set at
	// the start of nextToken.
	l._tokenStartCharIndex = -1

	// The line on which the first character of the token resides///
	l._tokenStartLine = -1

	// The character position of first character within the line///
	l._tokenStartColumn = -1

	// Once we see EOF on char stream, next token will be EOF.
	// If you have DONE : EOF  then you see DONE EOF.
	l._hitEOF = false

	// The channel number for the current token///
	l._channel = TokenDefaultChannel

	// The token type for the current token///
	l._type = TokenInvalidType

	l._modeStack = make([]int, 0)
	l._mode = LexerDefaultMode

	// You can set the text for the current token to override what is in
	// the input char buffer. Use setText() or can set l instance var.
	// /
	l._text = nil

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

func (l *Lexer) reset() {
	// wack Lexer state variables
	if l._input != nil {
		l._input.seek(0) // rewind the input
	}
	l._token = nil
	l._type = TokenInvalidType
	l._channel = TokenDefaultChannel
	l._tokenStartCharIndex = -1
	l._tokenStartColumn = -1
	l._tokenStartLine = -1
	l._text = nil

	l._hitEOF = false
	l._mode = LexerDefaultMode
	l._modeStack = make([]int, 0)

	l.Interpreter.reset()
}

func (l *Lexer) getInputStream() CharStream {
	return l._input
}

func (l *Lexer) getSourceName() string {
	return l._input.getSourceName()
}

func (l *Lexer) setChannel(v int) {
	l._channel = v
}

func (l *Lexer) getTokenFactory() TokenFactory {
	return l._factory
}

func (l *Lexer) setTokenFactory(f TokenFactory) {
	l._factory = f
}

func (l *Lexer) safeMatch() (ret int) {

	// previously in catch block
	defer func() {
		if e := recover(); e != nil {
			if re, ok := e.(IRecognitionException); ok {
				l.notifyListeners(re) // report error
				l.recover(re)
				ret = LexerSkip // default
			}
		}
	}()

	return l.Interpreter.match(l._input, l._mode)
}

// Return a token from l source i.e., match a token on the char stream.
func (l *Lexer) nextToken() *Token {
	if l._input == nil {
		panic("nextToken requires a non-nil input stream.")
	}

	// do this when done consuming
	var tokenStartMarker = l._input.mark()

	// previously in finally block
	defer func() {
		// make sure we release marker after match or
		// unbuffered char stream will keep buffering
		l._input.release(tokenStartMarker)
	}()

	for true {
		if l._hitEOF {
			l.emitEOF()
			return l._token
		}
		l._token = nil
		l._channel = TokenDefaultChannel
		l._tokenStartCharIndex = l._input.index()
		l._tokenStartColumn = l.Interpreter.column
		l._tokenStartLine = l.Interpreter.line
		l._text = nil
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

// Instruct the lexer to skip creating a token for current lexer rule
// and look for another token. nextToken() knows to keep looking when
// a lexer rule finishes with token set to SKIP_TOKEN. Recall that
// if token==nil at end of any token rule, it creates one for you
// and emits it.
// /
func (l *Lexer) skip() {
	l._type = LexerSkip
}

func (l *Lexer) more() {
	l._type = LexerMore
}

func (l *Lexer) mode(m int) {
	l._mode = m
}

func (l *Lexer) pushMode(m int) {
	if LexerATNSimulatordebug {
		fmt.Println("pushMode " + strconv.Itoa(m))
	}
	l._modeStack.Push(l._mode)
	l.mode(m)
}

func (l *Lexer) popMode() int {
	if len(l._modeStack) == 0 {
		panic("Empty Stack")
	}
	if LexerATNSimulatordebug {
		fmt.Println("popMode back to " + fmt.Sprint(l._modeStack[0:len(l._modeStack)-1]))
	}
	i, _ := l._modeStack.Pop()
	l.mode(i)
	return l._mode
}

func (l *Lexer) inputStream() CharStream {
	return l._input
}

func (l *Lexer) setInputStream(input CharStream) {
	l._input = nil
	l._tokenFactorySourcePair = &TokenSourceCharStreamPair{l, l._input}
	l.reset()
	l._input = input
	l._tokenFactorySourcePair = &TokenSourceCharStreamPair{l, l._input}
}

// By default does not support multiple emits per nextToken invocation
// for efficiency reasons. Subclass and override l method, nextToken,
// and getToken (to push tokens into a list and pull from that list
// rather than a single variable as l implementation does).
// /
func (l *Lexer) emitToken(token *Token) {
	l._token = token
}

// The standard method called to automatically emit a token at the
// outermost lexical rule. The token object should point into the
// char buffer start..stop. If there is a text override in 'text',
// use that to set the token's text. Override l method to emit
// custom Token objects or provide a Newfactory.
// /
func (l *Lexer) emit() *Token {
	var t = l._factory.create(l._tokenFactorySourcePair, l._type, *l._text, l._channel, l._tokenStartCharIndex, l.getCharIndex()-1, l._tokenStartLine, l._tokenStartColumn)
	l.emitToken(t)
	return t
}

func (l *Lexer) emitEOF() *Token {
	cpos := l.getCharPositionInLine()
	lpos := l.getLine()
	var eof = l._factory.create(l._tokenFactorySourcePair, TokenEOF, "", TokenDefaultChannel, l._input.index(), l._input.index()-1, lpos, cpos)
	l.emitToken(eof)
	return eof
}

func (l *Lexer) getCharPositionInLine() int {
	return l.Interpreter.column
}

func (l *Lexer) getLine() int {
	return l.Interpreter.line
}

func (l *Lexer) getType() int {
	return l._type
}

func (l *Lexer) setType(t int) {
	l._type = t
}

// What is the index of the current character of lookahead?///
func (l *Lexer) getCharIndex() int {
	return l._input.index()
}

// Return the text matched so far for the current token or any text override.
//Set the complete text of l token it wipes any previous changes to the text.
func (l *Lexer) text() string {
	if l._text != nil {
		return *l._text
	} else {
		return l.Interpreter.getText(l._input)
	}
}

func (l *Lexer) setText(text string) {
	*l._text = text
}

func (this *Lexer) getATN() *ATN {
	return this.Interpreter.atn
}

// Return a list of all Token objects in input char stream.
// Forces load of all tokens. Does not include EOF token.
// /
func (l *Lexer) getAllTokens() []*Token {
	var tokens = make([]*Token, 0)
	var t = l.nextToken()
	for t.tokenType != TokenEOF {
		tokens = append(tokens, t)
		t = l.nextToken()
	}
	return tokens
}

func (l *Lexer) notifyListeners(e IRecognitionException) {
	var start = l._tokenStartCharIndex
	var stop = l._input.index()
	var text = l._input.getTextFromInterval(NewInterval(start, stop))
	var msg = "token recognition error at: '" + text + "'"
	var listener = l.getErrorListenerDispatch()
	listener.syntaxError(l, nil, l._tokenStartLine, l._tokenStartColumn, msg, e)
}

func (l *Lexer) getErrorDisplayForChar(c rune) string {
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

func (l *Lexer) getCharErrorDisplay(c rune) string {
	return "'" + l.getErrorDisplayForChar(c) + "'"
}

// Lexers can normally match any char in it's vocabulary after matching
// a token, so do the easy thing and just kill a character and hope
// it all works out. You can instead use the rule invocation stack
// to do sophisticated error recovery if you are in a fragment rule.
// /
func (l *Lexer) recover(re IRecognitionException) {
	if l._input.LA(1) != TokenEOF {
		if _, ok := re.(*LexerNoViableAltException); ok {
			// skip a char and try again
			l.Interpreter.consume(l._input)
		} else {
			// TODO: Do we lose character or line position information?
			l._input.consume()
		}
	}
}
