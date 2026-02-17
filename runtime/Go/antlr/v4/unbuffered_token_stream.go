package antlr

import (
	"fmt"
	"strings"
)

// UnbufferedTokenStream 实现了 ITokenStream 接口
type UnbufferedTokenStream struct {
	tokenSource          TokenSource
	tokens               []Token
	n                    int
	p                    int
	numMarkers           int
	lastToken            Token
	lastTokenBufferStart Token
	currentTokenIndex    int
}

var _ TokenStream = (*UnbufferedTokenStream)(nil)

// NewUnbufferedTokenStream 创建一个新的UnbufferedTokenStream实例
func NewUnbufferedTokenStream(tokenSource TokenSource, bufferSize int) *UnbufferedTokenStream {
	stream := &UnbufferedTokenStream{
		tokenSource: tokenSource,
		tokens:      make([]Token, bufferSize),
		n:           0,
	}
	stream.Fill(1) // prime the pump
	return stream
}

// Get 获取指定索引的token
func (u *UnbufferedTokenStream) Get(i int) Token {
	bufferStartIndex := u.GetBufferStartIndex()
	if i < bufferStartIndex || i >= bufferStartIndex+u.n {
		panic(fmt.Sprintf("get(%d) outside buffer: %d..%d", i, bufferStartIndex, bufferStartIndex+u.n))
	}
	return u.tokens[i-bufferStartIndex]
}

// LT 查看前面的token
func (u *UnbufferedTokenStream) LT(i int) Token {
	if i == -1 {
		return u.lastToken
	}
	u.Sync(i)
	index := u.p + i - 1
	if index < 0 {
		panic(fmt.Sprintf("LT(%d) gives negative index", i))
	}
	if index >= u.n {
		return u.tokens[u.n-1] // return EOF token
	}
	return u.tokens[index]
}

// LA 获取token类型
func (u *UnbufferedTokenStream) LA(i int) int {
	t := u.LT(i)
	if t == nil {
		return TokenEOF
	}
	return t.GetTokenType()
}

// GetTokenSource 获取token源
func (u *UnbufferedTokenStream) GetTokenSource() TokenSource {
	return u.tokenSource
}

func (u *UnbufferedTokenStream) SetTokenSource(tokenSource TokenSource) {
	u.tokenSource = tokenSource
}

// Consume 消费一个token
func (u *UnbufferedTokenStream) Consume() {
	if u.LA(1) == TokenEOF {
		panic("cannot consume EOF")
	}

	u.lastToken = u.tokens[u.p]

	if u.p == u.n-1 && u.numMarkers == 0 {
		u.n = 0
		u.p = -1
		u.lastTokenBufferStart = u.lastToken
	}
	u.p++
	u.currentTokenIndex++
	u.Sync(1)
}

// Sync 确保缓冲区有足够的token
func (u *UnbufferedTokenStream) Sync(want int) {
	need := (u.p + want - 1) - u.n + 1
	if need > 0 {
		u.Fill(need)
	}
}

// Fill 填充token缓冲区
func (u *UnbufferedTokenStream) Fill(n int) int {
	for i := 0; i < n; i++ {
		if u.n > 0 && u.tokens[u.n-1].GetTokenType() == TokenEOF {
			return i
		}
		t := u.tokenSource.NextToken()
		u.Add(t)
	}
	return n
}

// Add 添加token到缓冲区
func (u *UnbufferedTokenStream) Add(t Token) {
	if u.n >= len(u.tokens) {
		newTokens := make([]Token, len(u.tokens)*2)
		copy(newTokens, u.tokens)
		u.tokens = newTokens
	}

	t.SetTokenIndex(u.GetBufferStartIndex() + u.n)

	u.tokens[u.n] = t
	u.n++
}

// Mark 标记当前位置
func (u *UnbufferedTokenStream) Mark() int {
	if u.numMarkers == 0 {
		u.lastTokenBufferStart = u.lastToken
	}
	u.numMarkers++
	return -u.numMarkers
}

// Release 释放标记
func (u *UnbufferedTokenStream) Release(marker int) {
	expectedMark := -u.numMarkers
	if marker != expectedMark {
		panic("release() called with an invalid marker")
	}

	u.numMarkers--
	if u.numMarkers == 0 && u.p > 0 {
		copy(u.tokens, u.tokens[u.p:u.n])
		u.n = u.n - u.p
		u.p = 0
		u.lastTokenBufferStart = u.lastToken
	}
}

// GetIndex 获取当前token索引
func (u *UnbufferedTokenStream) Index() int {
	return u.currentTokenIndex
}

// Seek 跳转到指定位置
func (u *UnbufferedTokenStream) Seek(index int) {
	if index == u.currentTokenIndex {
		return
	}

	if index > u.currentTokenIndex {
		u.Sync(index - u.currentTokenIndex)
		index = min(index, u.GetBufferStartIndex()+u.n-1)
	}

	i := index - u.GetBufferStartIndex()
	if i < 0 {
		panic(fmt.Sprintf("cannot seek to negative index %d", index))
	}
	if i >= u.n {
		panic(fmt.Sprintf("seek to index outside buffer: %d not in %d..%d", index, u.GetBufferStartIndex(), u.GetBufferStartIndex()+u.n))
	}

	u.p = i
	u.currentTokenIndex = index

	if u.p == 0 {
		u.lastToken = u.lastTokenBufferStart
	} else {
		u.lastToken = u.tokens[u.p-1]
	}
}

// GetText 获取文本
func (u *UnbufferedTokenStream) GetTextFromInterval(interval Interval) string {
	bufferStartIndex := u.GetBufferStartIndex()
	bufferStopIndex := bufferStartIndex + len(u.tokens) - 1

	if interval.Start < bufferStartIndex || interval.Stop > bufferStopIndex {
		panic(fmt.Sprintf("interval %v not in token buffer window: %d..%d", interval, bufferStartIndex, bufferStopIndex))
	}

	a := interval.Start - bufferStartIndex
	b := interval.Stop - bufferStartIndex

	var buf strings.Builder
	for i := a; i <= b; i++ {
		t := u.tokens[i]
		buf.WriteString(t.GetText())
	}

	return buf.String()
}

// GetBufferStartIndex 获取缓冲区起始索引
func (u *UnbufferedTokenStream) GetBufferStartIndex() int {
	return u.currentTokenIndex - u.p
}

// GetSourceName 获取源名称
func (u *UnbufferedTokenStream) GetSourceName() string {
	return u.tokenSource.GetSourceName()
}

// Size 获取流大小（不支持）
func (u *UnbufferedTokenStream) Size() int {
	panic("Unbuffered stream cannot know its size")
}

func (u *UnbufferedTokenStream) Reset() {
	panic("cannot reset unbuffered stream")
}

func (u *UnbufferedTokenStream) GetAllText() string {
	return u.GetTextFromInterval(NewInterval(0, len(u.tokens)-1))
}

func (u *UnbufferedTokenStream) GetTextFromRuleContext(interval RuleContext) string {
	return u.GetTextFromInterval(interval.GetSourceInterval())
}

func (u *UnbufferedTokenStream) GetTextFromTokens(start, end Token) string {
	if start == nil || end == nil {
		return ""
	}

	return u.GetTextFromInterval(NewInterval(start.GetTokenIndex(), end.GetTokenIndex()))
}
