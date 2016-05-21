package antlr

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

	Interpreter         *LexerATNSimulator
	TokenStartCharIndex int
	TokenStartLine      int
	TokenStartColumn    int
	ActionType          int

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

func (b *BaseLexer) reset() {
	// wack Lexer state variables
	if b._input != nil {
		b._input.Seek(0) // rewind the input
	}
	b._token = nil
	b._type = TokenInvalidType
	b._channel = TokenDefaultChannel
	b.TokenStartCharIndex = -1
	b.TokenStartColumn = -1
	b.TokenStartLine = -1
	b._text = ""

	b._hitEOF = false
	b._mode = LexerDefaultMode
	b._modeStack = make([]int, 0)

	b.Interpreter.reset()
}

func (b *BaseLexer) GetInterpreter() *LexerATNSimulator {
	return b.Interpreter
}

func (b *BaseLexer) GetInputStream() CharStream {
	return b._input
}

func (b *BaseLexer) GetSourceName() string {
	return b.GrammarFileName
}

func (b *BaseLexer) setChannel(v int) {
	b._channel = v
}

func (b *BaseLexer) GetTokenFactory() TokenFactory {
	return b._factory
}

func (b *BaseLexer) setTokenFactory(f TokenFactory) {
	b._factory = f
}

func (b *BaseLexer) safeMatch() (ret int) {

	// previously in catch block
	defer func() {
		if e := recover(); e != nil {
			if re, ok := e.(RecognitionException); ok {
				b.notifyListeners(re) // Report error
				b.Recover(re)
				ret = LexerSkip // default
			}
		}
	}()

	return b.Interpreter.Match(b._input, b._mode)
}

// Return a token from l source i.e., Match a token on the char stream.
func (b *BaseLexer) NextToken() Token {
	if b._input == nil {
		panic("NextToken requires a non-nil input stream.")
	}

	var tokenStartMarker = b._input.Mark()

	// previously in finally block
	defer func() {
		// make sure we release marker after Match or
		// unbuffered char stream will keep buffering
		b._input.Release(tokenStartMarker)
	}()

	for true {
		if b._hitEOF {
			b.emitEOF()
			return b._token
		}
		b._token = nil
		b._channel = TokenDefaultChannel
		b.TokenStartCharIndex = b._input.Index()
		b.TokenStartColumn = b.Interpreter.column
		b.TokenStartLine = b.Interpreter.line
		b._text = ""
		var continueOuter = false
		for true {
			b._type = TokenInvalidType
			var ttype = LexerSkip

			ttype = b.safeMatch()

			if b._input.LA(1) == TokenEOF {
				b._hitEOF = true
			}
			if b._type == TokenInvalidType {
				b._type = ttype
			}
			if b._type == LexerSkip {
				continueOuter = true
				break
			}
			if b._type != LexerMore {
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
		if b._token == nil {
			b.emit()
		}
		return b._token
	}

	return nil
}

// Instruct the lexer to Skip creating a token for current lexer rule
// and look for another token. NextToken() knows to keep looking when
// a lexer rule finishes with token set to SKIP_TOKEN. Recall that
// if token==nil at end of any token rule, it creates one for you
// and emits it.
// /
func (b *BaseLexer) Skip() {
	b._type = LexerSkip
}

func (b *BaseLexer) More() {
	b._type = LexerMore
}

func (b *BaseLexer) mode(m int) {
	b._mode = m
}

func (b *BaseLexer) pushMode(m int) {
	if LexerATNSimulatorDebug {
		fmt.Println("pushMode " + strconv.Itoa(m))
	}
	b._modeStack.Push(b._mode)
	b.mode(m)
}

func (b *BaseLexer) popMode() int {
	if len(b._modeStack) == 0 {
		panic("Empty Stack")
	}
	if LexerATNSimulatorDebug {
		fmt.Println("popMode back to " + fmt.Sprint(b._modeStack[0:len(b._modeStack)-1]))
	}
	i, _ := b._modeStack.Pop()
	b.mode(i)
	return b._mode
}

func (b *BaseLexer) inputStream() CharStream {
	return b._input
}

func (b *BaseLexer) setInputStream(input CharStream) {
	b._input = nil
	b._tokenFactorySourcePair = &TokenSourceCharStreamPair{b, b._input}
	b.reset()
	b._input = input
	b._tokenFactorySourcePair = &TokenSourceCharStreamPair{b, b._input}
}

// By default does not support multiple emits per NextToken invocation
// for efficiency reasons. Subclass and override l method, NextToken,
// and GetToken (to push tokens into a list and pull from that list
// rather than a single variable as l implementation does).
// /
func (b *BaseLexer) emitToken(token Token) {
	b._token = token
}

// The standard method called to automatically emit a token at the
// outermost lexical rule. The token object should point into the
// char buffer start..stop. If there is a text override in 'text',
// use that to set the token's text. Override l method to emit
// custom Token objects or provide a Newfactory.
// /
func (b *BaseLexer) emit() Token {
	if PortDebug {
		fmt.Println("emit")
	}
	var t = b._factory.Create(b._tokenFactorySourcePair, b._type, b._text, b._channel, b.TokenStartCharIndex, b.getCharIndex()-1, b.TokenStartLine, b.TokenStartColumn)
	b.emitToken(t)
	return t
}

func (b *BaseLexer) emitEOF() Token {
	cpos := b.GetCharPositionInLine()
	lpos := b.GetLine()
	if PortDebug {
		fmt.Println("emitEOF")
	}
	var eof = b._factory.Create(b._tokenFactorySourcePair, TokenEOF, "", TokenDefaultChannel, b._input.Index(), b._input.Index()-1, lpos, cpos)
	b.emitToken(eof)
	return eof
}

func (b *BaseLexer) GetCharPositionInLine() int {
	return b.Interpreter.column
}

func (b *BaseLexer) GetLine() int {
	return b.Interpreter.line
}

func (b *BaseLexer) getType() int {
	return b._type
}

func (b *BaseLexer) setType(t int) {
	b._type = t
}

// What is the index of the current character of lookahead?///
func (b *BaseLexer) getCharIndex() int {
	return b._input.Index()
}

// Return the text Matched so far for the current token or any text override.
//Set the complete text of l token it wipes any previous changes to the text.
func (b *BaseLexer) GetText() string {
	if b._text != "" {
		return b._text
	}

	return b.Interpreter.GetText(b._input)
}

func (b *BaseLexer) SetText(text string) {
	b._text = text
}

func (b *BaseLexer) GetATN() *ATN {
	return b.Interpreter.atn
}

// Return a list of all Token objects in input char stream.
// Forces load of all tokens. Does not include EOF token.
// /
func (b *BaseLexer) getAllTokens() []Token {
	if PortDebug {
		fmt.Println("getAllTokens")
	}
	var tokens = make([]Token, 0)
	var t = b.NextToken()
	for t.GetTokenType() != TokenEOF {
		tokens = append(tokens, t)
		if PortDebug {
			fmt.Println("getAllTokens")
		}
		t = b.NextToken()
	}
	return tokens
}

func (b *BaseLexer) notifyListeners(e RecognitionException) {
	var start = b.TokenStartCharIndex
	var stop = b._input.Index()
	var text = b._input.GetTextFromInterval(NewInterval(start, stop))
	var msg = "token recognition error at: '" + text + "'"
	var listener = b.GetErrorListenerDispatch()
	listener.SyntaxError(b, nil, b.TokenStartLine, b.TokenStartColumn, msg, e)
}

func (b *BaseLexer) getErrorDisplayForChar(c rune) string {
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

func (b *BaseLexer) getCharErrorDisplay(c rune) string {
	return "'" + b.getErrorDisplayForChar(c) + "'"
}

// Lexers can normally Match any char in it's vocabulary after Matching
// a token, so do the easy thing and just kill a character and hope
// it all works out. You can instead use the rule invocation stack
// to do sophisticated error recovery if you are in a fragment rule.
// /
func (b *BaseLexer) Recover(re RecognitionException) {
	if b._input.LA(1) != TokenEOF {
		if _, ok := re.(*LexerNoViableAltException); ok {
			// Skip a char and try again
			b.Interpreter.consume(b._input)
		} else {
			// TODO: Do we lose character or line position information?
			b._input.Consume()
		}
	}
}
