package antlr4

import (
	"strings"
	"fmt"
)

// A lexer is recognizer that draws input symbols from a character stream.
//  lexer grammars result in a subclass of this object. A Lexer object
//  uses simplified match() and error recovery mechanisms in the interest
//  of speed.
///

type TokenSource interface {
	nextToken() *Token
	getLine() int
	getCharPositionInLine() int
	getInputStream() *InputStream
	getSourceName() string
	setTokenFactory(factory *TokenFactory)
	getTokenFactory() *TokenFactory
}

type TokenFactorySourcePair struct {
	factory *TokenFactory
	inputStream *InputStream
}

type Lexer struct {
	Recognizer

	_input *InputStream
	_factory *TokenFactory
	_tokenFactorySourcePair *TokenFactorySourcePair
	_interp *LexerATNSimulator
	_token int
	_tokenStartCharIndex int
	_tokenStartLine int
	_tokenStartColumn int
	_hitEOF int
	_channel int
	_type int
	_modeStack IntStack
	_mode int
	_text string
}

func NewLexer(input *InputStream) *Lexer {

	lexer := new(Lexer)

	lexer.initRecognizer()
	lexer.initLexer(input)

	return lexer
}

func (l *Lexer) initLexer(input *InputStream){

	l._input = input
	l._factory = CommonTokenFactoryDEFAULT
	l._tokenFactorySourcePair = TokenFactorySourcePair{l, input}

	l._interp = nil // child classes must populate l

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

	l._modeStack = make([]int,0)
	l._mode = LexerDefaultMode

	// You can set the text for the current token to override what is in
	// the input char buffer. Use setText() or can set l instance var.
	// /
	l._text = nil

}

const (
	LexerDefaultMode = 0
	LexerMore = -2
	LexerSkip = -3
)

const (
	LexerDefaultTokenChannel = TokenDefaultChannel
	LexerHidden = TokenHiddenChannel
	LexerMinCharValue = '\u0000'
	LexerMaxCharValue = '\uFFFE'
)

func (l *Lexer) reset() {
	// wack Lexer state variables
	if (l._input != nil) {
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

	l._interp.reset()
}

// Return a token from l source i.e., match a token on the char stream.
func (l *Lexer) nextToken() Token {
	if (l._input == nil) {
		panic("nextToken requires a non-nil input stream.")
	}

	// Mark start location in char stream so unbuffered streams are
	// guaranteed at least have text of current token
	// var tokenStartMarker = l._input.mark()
	try {
		for (true) {
			if (l._hitEOF) {
				l.emitEOF()
				return l._token
			}
			l._token = nil
			l._channel = TokenDefaultChannel
			l._tokenStartCharIndex = l._input.index
			l._tokenStartColumn = l._interp.column
			l._tokenStartLine = l._interp.line
			l._text = nil
			var continueOuter = false
			for (true) {
				l._type = TokenInvalidType
				var ttype = LexerSkip
				try {
					ttype = l._interp.match(l._input, l._mode)
				} catch (e) {
					l.notifyListeners(e) // report error
					l.recover(e)
				}
				if (l._input.LA(1) == TokenEOF) {
					l._hitEOF = true
				}
				if (l._type == TokenInvalidType) {
					l._type = ttype
				}
				if (l._type == LexerSkip) {
					continueOuter = true
					break
				}
				if (l._type != LexerMore) {
					break
				}
			}
			if (continueOuter) {
				continue
			}
			if (l._token == nil) {
				l.emit()
			}
			return l._token
		}
	} finally {
		// make sure we release marker after match or
		// unbuffered char stream will keep buffering
		l._input.release(tokenStartMarker)
	}
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
	if (l._interp.debug) {
		fmt.Println("pushMode " + m)
	}
	l._modeStack.Push(l._mode)
	l.mode(m)
}

func (l *Lexer) popMode() {
	if ( len(l._modeStack) == 0) {
		panic("Empty Stack")
	}
	if (l._interp.debug) {
		fmt.Println("popMode back to " + l._modeStack.slice(0, -1))
	}
	i, _ := l._modeStack.Pop()
	l.mode(i)
	return l._mode
}


func (l *Lexer) inputStream() *InputStream {
	return l._input
}

func (l *Lexer) setInputStream(input *InputStream) {
	l._input = nil
	l._tokenFactorySourcePair = TokenFactorySourcePair{l, l._input}
	l.reset()
	l._input = input
	l._tokenFactorySourcePair = TokenFactorySourcePair{l, l._input}
}


func (l *Lexer) sourceName() string {
	return l._input.sourceName
}

// By default does not support multiple emits per nextToken invocation
// for efficiency reasons. Subclass and override l method, nextToken,
// and getToken (to push tokens into a list and pull from that list
// rather than a single variable as l implementation does).
// /
func (l *Lexer) emitToken(token int) {
	l._token = token
}

// The standard method called to automatically emit a token at the
// outermost lexical rule. The token object should point into the
// char buffer start..stop. If there is a text override in 'text',
// use that to set the token's text. Override l method to emit
// custom Token objects or provide a Newfactory.
// /
func (l *Lexer) emit() {
	var t = l._factory.create(l._tokenFactorySourcePair, l._type, l._text, l._channel, l._tokenStartCharIndex, l.getCharIndex() - 1, l._tokenStartLine, l._tokenStartColumn)
	l.emitToken(t)
	return t
}

func (l *Lexer) emitEOF() int {
	var cpos = l.column()
	var lpos = l.line()
	var eof = l._factory.create(l._tokenFactorySourcePair, TokenEOF, nil, TokenDefaultChannel, l._input.index,  l._input.index - 1, lpos, cpos)
	l.emitToken(eof)
	return eof
}


func (l *Lexer) getType() {
	return l._type
}

func (l *Lexer) setType(t int) {
	l._type = t
}

// What is the index of the current character of lookahead?///
func (l *Lexer) getCharIndex() {
	return l._input.index
}

// Return the text matched so far for the current token or any text override.
//Set the complete text of l token it wipes any previous changes to the text.
func (l *Lexer) text() string {
	if (l._text != nil) {
		return l._text
	} else {
		return l._interp.getText(l._input)
	}
}

func (l *Lexer) setText(text string) {
	l._text = text
}

// Return a list of all Token objects in input char stream.
// Forces load of all tokens. Does not include EOF token.
// /
func (l *Lexer) getAllTokens() []Token {
	var tokens = make([]Token, 0)
	var t = l.nextToken()
	for (t.tokenType != TokenEOF) {
		tokens = append(tokens, t)
		t = l.nextToken()
	}
	return tokens
}

func (l *Lexer) notifyListeners(e error) {
	var start = l._tokenStartCharIndex
	var stop = l._input.index
	var text = l._input.getText(start, stop)
	var msg = "token recognition error at: '" + l.getErrorDisplay(text) + "'"
	var listener = l.getErrorListenerDispatch()
	listener.syntaxError(l, nil, l._tokenStartLine, l._tokenStartColumn, msg, e)
}

func (l *Lexer) getErrorDisplay(s []string) string {
	var d = make([]string,len(s))
	for i := 0; i < len(s); i++ {
		d[i] = s[i]
	}
	return strings.Join(d, "")
}

func (l *Lexer) getErrorDisplayForChar(c rune) string {
	if (c == TokenEOF) {
		return "<EOF>"
	} else if (c == '\n') {
		return "\\n"
	} else if (c == '\t') {
		return "\\t"
	} else if (c == '\r') {
		return "\\r"
	} else {
		return c
	}
}

func (l *Lexer) getCharErrorDisplay(c) string {
	return "'" + l.getErrorDisplayForChar(c) + "'"
}

// Lexers can normally match any char in it's vocabulary after matching
// a token, so do the easy thing and just kill a character and hope
// it all works out. You can instead use the rule invocation stack
// to do sophisticated error recovery if you are in a fragment rule.
// /
func (l *Lexer) recover(re error) {
	if (l._input.LA(1) != TokenEOF) {
		if _, ok := re.(LexerNoViableAltException); ok {
			// skip a char and try again
			l._interp.consume(l._input)
		} else {
			// TODO: Do we lose character or line position information?
			l._input.consume()
		}
	}
}


