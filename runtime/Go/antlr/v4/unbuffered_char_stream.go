package antlr

import (
	"bufio"
	"io"
)

type UnbufferedCharStream struct {
	data                []rune
	n                   int
	p                   int
	numMarkers          int
	lastChar            rune
	lastCharBufferStart rune
	currentCharIndex    int
	input               *bufio.Reader
	name                string
}

var _ CharStream = (*UnbufferedCharStream)(nil)

func NewUnbufferedCharStream(input io.Reader, bufferSize int) *UnbufferedCharStream {
	stream := &UnbufferedCharStream{
		data:  make([]rune, bufferSize),
		n:     0,
		p:     0,
		input: bufio.NewReader(input),
	}

	stream.Fill(1)
	return stream
}

func (ucs *UnbufferedCharStream) Consume() {
	if ucs.LA(1) == TokenEOF {
		panic("cannot consume EOF")
	}
	ucs.lastChar = ucs.data[ucs.p]
	if ucs.p == ucs.n-1 && ucs.numMarkers == 0 {
		ucs.n = 0
		ucs.p = -1
		ucs.lastCharBufferStart = ucs.lastChar
	}
	ucs.p++
	ucs.currentCharIndex++
	ucs.Sync(1)
}

func (ucs *UnbufferedCharStream) Sync(want int) {
	need := (ucs.p + want - 1) - ucs.n + 1
	if need > 0 {
		ucs.Fill(need)
	}
}

func (ucs *UnbufferedCharStream) Fill(n int) int {
	for i := 0; i < n; i++ {
		if ucs.n > 0 && ucs.data[ucs.n-1] == TokenEOF {
			return i
		}

		c, _, err := ucs.input.ReadRune()
		if err != nil {
			if err != io.EOF {
				panic(err)
			}

			c = TokenEOF
		}
		ucs.Add(c)
	}
	return n
}

func (ucs *UnbufferedCharStream) Add(c rune) {
	if ucs.n >= len(ucs.data) {
		newData := make([]rune, len(ucs.data)*2)
		copy(newData, ucs.data)
		ucs.data = newData
	}
	ucs.data[ucs.n] = c
	ucs.n++
}

func (ucs *UnbufferedCharStream) LA(i int) int {
	if i == -1 {
		return int(ucs.lastChar)
	}
	ucs.Sync(i)
	index := ucs.p + i - 1
	if index < 0 {
		panic("index out of range")
	}
	if index >= ucs.n {
		return TokenEOF
	}
	return int(ucs.data[index])
}

func (ucs *UnbufferedCharStream) Mark() int {
	if ucs.numMarkers == 0 {
		ucs.lastCharBufferStart = ucs.lastChar
	}
	mark := -ucs.numMarkers - 1
	ucs.numMarkers++
	return mark
}

func (ucs *UnbufferedCharStream) Release(marker int) {
	expectedMark := -ucs.numMarkers
	if marker != expectedMark {
		panic("release() called with an invalid marker.")
	}
	ucs.numMarkers--
	if ucs.numMarkers == 0 && ucs.p > 0 {
		copy(ucs.data, ucs.data[ucs.p:ucs.n])
		ucs.n -= ucs.p
		ucs.p = 0
		ucs.lastCharBufferStart = ucs.lastChar
	}
}

func (ucs *UnbufferedCharStream) Index() int {
	return ucs.currentCharIndex
}

func (ucs *UnbufferedCharStream) Seek(index int) {
	if index == ucs.currentCharIndex {
		return
	}
	if index > ucs.currentCharIndex {
		ucs.Sync(index - ucs.currentCharIndex)
		index = min(index, ucs.BufferStartIndex()+ucs.n-1)
	}
	i := index - ucs.BufferStartIndex()
	if i < 0 {
		panic("cannot seek to negative index")
	}
	if i >= ucs.n {
		panic("seek to index outside buffer")
	}
	ucs.p = i
	ucs.currentCharIndex = index
	if ucs.p == 0 {
		ucs.lastChar = ucs.lastCharBufferStart
	} else {
		ucs.lastChar = ucs.data[ucs.p-1]
	}
}

func (ucs *UnbufferedCharStream) Size() int {
	panic("Unbuffered stream cannot know its size")
}

func (ucs *UnbufferedCharStream) GetSourceName() string {
	if ucs.name == "" {
		return "Unknown"
	}
	return ucs.name
}

func (ucs *UnbufferedCharStream) GetText(start, stop int) string {
	return ucs.GetTextFromInterval(NewInterval(start, stop))
}

func (ucs *UnbufferedCharStream) GetTextFromTokens(start, end Token) string {
	if start == nil || end == nil {
		return ""
	}

	return ucs.GetTextFromInterval(NewInterval(start.GetTokenIndex(), end.GetTokenIndex()))
}

func (ucs *UnbufferedCharStream) GetTextFromInterval(interval Interval) string {
	if interval.Start < 0 || interval.Stop < interval.Start-1 {
		panic("invalid interval")
	}
	bufferStartIndex := ucs.BufferStartIndex()
	if ucs.n > 0 && ucs.data[ucs.n-1] == TokenEOF {
		if interval.Start+interval.Length() > bufferStartIndex+ucs.n {
			panic("the interval extends past the end of the stream")
		}
	}
	if interval.Start < bufferStartIndex || interval.Stop >= bufferStartIndex+ucs.n {
		panic("interval outside buffer")
	}
	i := interval.Start - bufferStartIndex
	return string(ucs.data[i : i+interval.Length()+1])
}

func (ucs *UnbufferedCharStream) BufferStartIndex() int {
	return ucs.currentCharIndex - ucs.p
}
