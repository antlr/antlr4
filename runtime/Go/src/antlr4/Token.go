package antlr4

import (
	"strconv"
	"strings"
)

type TokenSourceCharStreamPair struct {
	tokenSource TokenSource
	charStream  CharStream
}

// A token has properties: text, type, line, character position in the line
// (so we can ignore tabs), token channel, index, and source from which
// we obtained this token.

type Token interface {
	GetSource() *TokenSourceCharStreamPair
	GetTokenType() int
	GetChannel() int
	GetStart() int
	GetStop() int
	GetLine() int
	GetColumn() int

	GetText() string
	SetText(s string)

	GetTokenIndex() int
	SetTokenIndex(v int)

	GetTokenSource() TokenSource
	GetInputStream() CharStream
}

type BaseToken struct {
	source     *TokenSourceCharStreamPair
	tokenType  int    // token type of the token
	channel    int    // The parser ignores everything not on DEFAULT_CHANNEL
	start      int    // optional return -1 if not implemented.
	stop       int    // optional return -1 if not implemented.
	tokenIndex int    // from 0..n-1 of the token object in the input stream
	line       int    // line=1..n of the 1st character
	column     int    // beginning of the line at which it occurs, 0..n-1
	_text      string // text of the token.
	readOnly   bool
}

const (
	TokenInvalidType = 0

	// During lookahead operations, this "token" signifies we hit rule end ATN state
	// and did not follow it despite needing to.
	TokenEpsilon = -2

	TokenMinUserTokenType = 1

	TokenEOF = -1

	// All tokens go to the parser (unless Skip() is called in that rule)
	// on a particular "channel". The parser tunes to a particular channel
	// so that whitespace etc... can go to the parser on a "hidden" channel.

	TokenDefaultChannel = 0

	// Anything on different channel than DEFAULT_CHANNEL is not parsed
	// by parser.

	TokenHiddenChannel = 1
)

func (this *BaseToken) GetChannel() int {
	return this.channel
}

func (this *BaseToken) GetStart() int {
	return this.start
}

func (this *BaseToken) GetStop() int {
	return this.stop
}

func (this *BaseToken) GetLine() int {
	return this.line
}

func (this *BaseToken) GetColumn() int {
	return this.column
}

func (this *BaseToken) GetTokenType() int {
	return this.tokenType
}

func (this *BaseToken) GetSource() *TokenSourceCharStreamPair {
	return this.source
}

func (this *BaseToken) GetTokenIndex() int {
	return this.tokenIndex
}

func (this *BaseToken) SetTokenIndex(v int) {
	this.tokenIndex = v
}

func (this *BaseToken) GetTokenSource() TokenSource {
	return this.source.tokenSource
}

func (this *BaseToken) GetInputStream() CharStream {
	return this.source.charStream
}

type CommonToken struct {
	*BaseToken
}

func NewCommonToken(source *TokenSourceCharStreamPair, tokenType, channel, start, stop int) *CommonToken {

	t := new(CommonToken)

	t.BaseToken = new(BaseToken)

	t.source = source
	t.tokenType = tokenType
	t.channel = channel
	t.start = start
	t.stop = stop
	t.tokenIndex = -1
	if t.source.tokenSource != nil {
		t.line = source.tokenSource.GetLine()
		t.column = source.tokenSource.GetCharPositionInLine()
	} else {
		t.column = -1
	}
	return t
}

// An empty {@link Pair} which is used as the default value of
// {@link //source} for tokens that do not have a source.

//CommonToken.EMPTY_SOURCE = [ nil, nil ]

// Constructs a New{@link CommonToken} as a copy of another {@link Token}.
//
// <p>
// If {@code oldToken} is also a {@link CommonToken} instance, the newly
// constructed token will share a reference to the {@link //text} field and
// the {@link Pair} stored in {@link //source}. Otherwise, {@link //text} will
// be assigned the result of calling {@link //GetText}, and {@link //source}
// will be constructed from the result of {@link Token//GetTokenSource} and
// {@link Token//GetInputStream}.</p>
//
// @param oldToken The token to copy.
//
func (ct *CommonToken) clone() *CommonToken {
	var t = NewCommonToken(ct.source, ct.tokenType, ct.channel, ct.start, ct.stop)
	t.tokenIndex = ct.GetTokenIndex()
	t.line = ct.GetLine()
	t.column = ct.GetColumn()
	t._text = ct.GetText()
	return t
}

func (this *CommonToken) GetText() string {
	if this._text != "" {
		return this._text
	}
	var input = this.GetInputStream()
	if input == nil {
		return ""
	}
	var n = input.Size()
	if this.start < n && this.stop < n {
		return input.GetTextFromInterval(NewInterval(this.start, this.stop))
	} else {
		return "<EOF>"
	}
}

func (this *CommonToken) SetText(text string) {
	this._text = text
}

func (this *CommonToken) String() string {
	var txt = this.GetText()
	if txt != "" {
		txt = strings.Replace(txt, "\n", "", -1)
		txt = strings.Replace(txt, "\r", "", -1)
		txt = strings.Replace(txt, "\t", "", -1)
	} else {
		txt = "<no text>"
	}

	var ch string
	if this.channel > 0 {
		ch = ",channel=" + strconv.Itoa(this.channel)
	} else {
		ch = ""
	}

	return "[@" + strconv.Itoa(this.tokenIndex) + "," + strconv.Itoa(this.start) + ":" + strconv.Itoa(this.stop) + "='" +
		txt + "',<" + strconv.Itoa(this.tokenType) + ">" +
		ch + "," + strconv.Itoa(this.line) + ":" + strconv.Itoa(this.column) + "]"
}
