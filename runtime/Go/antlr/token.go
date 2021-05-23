// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"strconv"
	"strings"
)

// TokenSourceCharStreamPair is a tuple (TokenSource, CharStream)
type TokenSourceCharStreamPair struct {
	tokenSource TokenSource
	charStream  CharStream
}

// Token is the result of lexical analysis and the input to the parser.
//
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

// BaseToken is the base implementation of Token.
type BaseToken struct {
	source     *TokenSourceCharStreamPair
	tokenType  int    // token type of the token
	channel    int    // The parser ignores everything not on DEFAULT_CHANNEL
	start      int    // optional return -1 if not implemented.
	stop       int    // optional return -1 if not implemented.
	tokenIndex int    // from 0..n-1 of the token object in the input stream
	line       int    // line=1..n of the 1st character
	column     int    // beginning of the line at which it occurs, 0..n-1
	text       string // text of the token.
	readOnly   bool
}

const (
	// TokenInvalidType represents an error token.
	TokenInvalidType = 0

	// TokenEpsilon is a utility token type. During lookahead operations, this
	// "token" signifies we hit rule end ATN state and did not follow it despite
	// needing to.
	TokenEpsilon = -2

	// TokenMinUserTokenType is the smallest value for a generated token type.
	TokenMinUserTokenType = 1

	// TokenEOF represents an end of input token.
	TokenEOF = -1

	// All tokens go to the parser (unless Skip() is called in that rule)
	// on a particular "channel". The parser tunes to a particular channel
	// so that whitespace etc... can go to the parser on a "hidden" channel.

	// TokenDefaultChannel represents the default channel.
	TokenDefaultChannel = 0

	// TokenHiddenChannel represents the hidden channel.
	TokenHiddenChannel = 1
)

// GetChannel returns the channel this token is sent through.
func (b *BaseToken) GetChannel() int {
	return b.channel
}

// GetStart returns the offset this token starts at.
func (b *BaseToken) GetStart() int {
	return b.start
}

// GetStop returns the offset this token stops at.
func (b *BaseToken) GetStop() int {
	return b.stop
}

// GetLine returns the line this token starts at.
func (b *BaseToken) GetLine() int {
	return b.line
}

// GetColumn returns the column this token starts at.
func (b *BaseToken) GetColumn() int {
	return b.column
}

// GetTokenType returns the kind of token this is.
func (b *BaseToken) GetTokenType() int {
	return b.tokenType
}

// GetSource returns the source and character stream of this token.
func (b *BaseToken) GetSource() *TokenSourceCharStreamPair {
	return b.source
}

// GetTokenIndex returns the index of this token
func (b *BaseToken) GetTokenIndex() int {
	return b.tokenIndex
}

// SetTokenIndex sets the index of this token.
func (b *BaseToken) SetTokenIndex(v int) {
	b.tokenIndex = v
}

// GetTokenSource returns the source of this token.
func (b *BaseToken) GetTokenSource() TokenSource {
	return b.source.tokenSource
}

// GetInputStream returns the character stream of this token.
func (b *BaseToken) GetInputStream() CharStream {
	return b.source.charStream
}

// CommonToken extends BaseToken
type CommonToken struct {
	*BaseToken
}

// NewCommonToken returns a new instance of CommonToken.
func NewCommonToken(source *TokenSourceCharStreamPair, tokenType, channel, start, stop int) *CommonToken {

	t := &CommonToken{
		BaseToken: &BaseToken{
			source:     source,
			tokenType:  tokenType,
			channel:    channel,
			start:      start,
			stop:       stop,
			tokenIndex: -1,
		},
	}
	if t.source.tokenSource != nil {
		t.line = source.tokenSource.GetLine()
		t.column = source.tokenSource.GetCharPositionInLine()
	} else {
		t.column = -1
	}
	return t
}

// An empty Pair which is used as the default value of
// //source for tokens that do not have a source.

//CommonToken.EMPTY_SOURCE = [ nil, nil ]

// Constructs a NewCommonToken as a copy of another Token.
//
// If oldToken is also a CommonToken instance, the newly
// constructed token will share a reference to the //text field and
// the Pair stored in //source. Otherwise, //text will
// be assigned the result of calling //GetText, and //source
// will be constructed from the result of Token//GetTokenSource and
// Token//GetInputStream.
//
// @param oldToken The token to copy.
//
func (c *CommonToken) clone() *CommonToken {
	t := NewCommonToken(c.source, c.tokenType, c.channel, c.start, c.stop)
	t.tokenIndex = c.GetTokenIndex()
	t.line = c.GetLine()
	t.column = c.GetColumn()
	t.text = c.GetText()
	return t
}

// GetText returns the text contained in this token.
func (c *CommonToken) GetText() string {
	if c.text != "" {
		return c.text
	}
	input := c.GetInputStream()
	if input == nil {
		return ""
	}
	n := input.Size()
	if c.start < n && c.stop < n {
		return input.GetTextFromInterval(NewInterval(c.start, c.stop))
	}
	return "<EOF>"
}

// SetText sets the text this token contains.
func (c *CommonToken) SetText(text string) {
	c.text = text
}

// String implements the Stringer interface.
func (c *CommonToken) String() string {
	txt := c.GetText()
	if txt != "" {
		txt = strings.Replace(txt, "\n", "\\n", -1)
		txt = strings.Replace(txt, "\r", "\\r", -1)
		txt = strings.Replace(txt, "\t", "\\t", -1)
	} else {
		txt = "<no text>"
	}

	var ch string
	if c.channel > 0 {
		ch = ",channel=" + strconv.Itoa(c.channel)
	} else {
		ch = ""
	}

	return "[@" + strconv.Itoa(c.tokenIndex) + "," + strconv.Itoa(c.start) + ":" + strconv.Itoa(c.stop) + "='" +
		txt + "',<" + strconv.Itoa(c.tokenType) + ">" +
		ch + "," + strconv.Itoa(c.line) + ":" + strconv.Itoa(c.column) + "]"
}
