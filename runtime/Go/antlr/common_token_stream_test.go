// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"testing"
)

type commonTokenStreamTestLexer struct {
	*BaseLexer

	tokens []Token
	i      int
}

func (l *commonTokenStreamTestLexer) NextToken() Token {
	tmp := l.tokens[l.i]
	l.i++
	return tmp
}

func TestCommonTokenStreamOffChannel(t *testing.T) {
	assert := assertNew(t)
	lexEngine := &commonTokenStreamTestLexer{
		tokens: []Token{
			newTestCommonToken(1, " ", LexerHidden),                    // 0
			newTestCommonToken(1, "x", LexerDefaultTokenChannel),       // 1
			newTestCommonToken(1, " ", LexerHidden),                    // 2
			newTestCommonToken(1, "=", LexerDefaultTokenChannel),       // 3
			newTestCommonToken(1, "34", LexerDefaultTokenChannel),      // 4
			newTestCommonToken(1, " ", LexerHidden),                    // 5
			newTestCommonToken(1, " ", LexerHidden),                    // 6
			newTestCommonToken(1, ";", LexerDefaultTokenChannel),       // 7
			newTestCommonToken(1, "\n", LexerHidden),                   // 9
			newTestCommonToken(TokenEOF, "", LexerDefaultTokenChannel), // 10
		},
	}
	tokens := NewCommonTokenStream(lexEngine, TokenDefaultChannel)

	assert.Equal("x", tokens.LT(1).GetText()) // must skip first off channel token
	tokens.Consume()
	assert.Equal("=", tokens.LT(1).GetText())
	assert.Equal("x", tokens.LT(-1).GetText())

	tokens.Consume()
	assert.Equal("34", tokens.LT(1).GetText())
	assert.Equal("=", tokens.LT(-1).GetText())

	tokens.Consume()
	assert.Equal(";", tokens.LT(1).GetText())
	assert.Equal("34", tokens.LT(-1).GetText())

	tokens.Consume()
	assert.Equal(TokenEOF, tokens.LT(1).GetTokenType())
	assert.Equal(";", tokens.LT(-1).GetText())

	assert.Equal("34", tokens.LT(-2).GetText())
	assert.Equal("=", tokens.LT(-3).GetText())
	assert.Equal("x", tokens.LT(-4).GetText())
}

func TestCommonTokenStreamFetchOffChannel(t *testing.T) {
	assert := assertNew(t)
	lexEngine := &commonTokenStreamTestLexer{
		tokens: []Token{
			newTestCommonToken(1, " ", LexerHidden),                    // 0
			newTestCommonToken(1, "x", LexerDefaultTokenChannel),       // 1
			newTestCommonToken(1, " ", LexerHidden),                    // 2
			newTestCommonToken(1, "=", LexerDefaultTokenChannel),       // 3
			newTestCommonToken(1, "34", LexerDefaultTokenChannel),      // 4
			newTestCommonToken(1, " ", LexerHidden),                    // 5
			newTestCommonToken(1, " ", LexerHidden),                    // 6
			newTestCommonToken(1, ";", LexerDefaultTokenChannel),       // 7
			newTestCommonToken(1, " ", LexerHidden),                    // 8
			newTestCommonToken(1, "\n", LexerHidden),                   // 9
			newTestCommonToken(TokenEOF, "", LexerDefaultTokenChannel), // 10
		},
	}
	tokens := NewCommonTokenStream(lexEngine, TokenDefaultChannel)
	tokens.Fill()

	assert.Nil(tokens.GetHiddenTokensToLeft(0, -1))
	assert.Nil(tokens.GetHiddenTokensToRight(0, -1))

	assert.Equal("[[@0,0:0=' ',<1>,channel=1,0:-1]]", tokensToString(tokens.GetHiddenTokensToLeft(1, -1)))
	assert.Equal("[[@2,0:0=' ',<1>,channel=1,0:-1]]", tokensToString(tokens.GetHiddenTokensToRight(1, -1)))

	assert.Nil(tokens.GetHiddenTokensToLeft(2, -1))
	assert.Nil(tokens.GetHiddenTokensToRight(2, -1))

	assert.Equal("[[@2,0:0=' ',<1>,channel=1,0:-1]]", tokensToString(tokens.GetHiddenTokensToLeft(3, -1)))
	assert.Nil(tokens.GetHiddenTokensToRight(3, -1))

	assert.Nil(tokens.GetHiddenTokensToLeft(4, -1))
	assert.Equal("[[@5,0:0=' ',<1>,channel=1,0:-1], [@6,0:0=' ',<1>,channel=1,0:-1]]",
		tokensToString(tokens.GetHiddenTokensToRight(4, -1)))

	assert.Nil(tokens.GetHiddenTokensToLeft(5, -1))
	assert.Equal("[[@6,0:0=' ',<1>,channel=1,0:-1]]",
		tokensToString(tokens.GetHiddenTokensToRight(5, -1)))

	assert.Equal("[[@5,0:0=' ',<1>,channel=1,0:-1]]",
		tokensToString(tokens.GetHiddenTokensToLeft(6, -1)))
	assert.Nil(tokens.GetHiddenTokensToRight(6, -1))

	assert.Equal("[[@5,0:0=' ',<1>,channel=1,0:-1], [@6,0:0=' ',<1>,channel=1,0:-1]]",
		tokensToString(tokens.GetHiddenTokensToLeft(7, -1)))
	assert.Equal("[[@8,0:0=' ',<1>,channel=1,0:-1], [@9,0:0='\\n',<1>,channel=1,0:-1]]",
		tokensToString(tokens.GetHiddenTokensToRight(7, -1)))

	assert.Nil(tokens.GetHiddenTokensToLeft(8, -1))
	assert.Equal("[[@9,0:0='\\n',<1>,channel=1,0:-1]]",
		tokensToString(tokens.GetHiddenTokensToRight(8, -1)))

	assert.Equal("[[@8,0:0=' ',<1>,channel=1,0:-1]]",
		tokensToString(tokens.GetHiddenTokensToLeft(9, -1)))
	assert.Nil(tokens.GetHiddenTokensToRight(9, -1))

}

type commonTokenStreamTestLexerSingleEOF struct {
	*BaseLexer

	tokens []Token
	i      int
}

func (l *commonTokenStreamTestLexerSingleEOF) NextToken() Token {
	return newTestCommonToken(TokenEOF, "", LexerDefaultTokenChannel)
}

func TestCommonTokenStreamSingleEOF(t *testing.T) {
	assert := assertNew(t)
	lexEngine := &commonTokenStreamTestLexerSingleEOF{}
	tokens := NewCommonTokenStream(lexEngine, TokenDefaultChannel)
	tokens.Fill()

	assert.Equal(TokenEOF, tokens.LA(1))
	assert.Equal(0, tokens.index)
	assert.Equal(1, tokens.Size())
}

func TestCommonTokenStreamCannotConsumeEOF(t *testing.T) {
	assert := assertNew(t)
	lexEngine := &commonTokenStreamTestLexerSingleEOF{}
	tokens := NewCommonTokenStream(lexEngine, TokenDefaultChannel)
	tokens.Fill()
	assert.Equal(TokenEOF, tokens.LA(1))
	assert.Equal(0, tokens.index)
	assert.Equal(1, tokens.Size())
	assert.Panics(tokens.Consume)
}
