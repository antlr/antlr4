// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

// InputStream implements IntStream to send the input to the lexer.
type InputStream struct {
	name  string
	index int
	data  []rune
	size  int
}

// NewInputStream returns a new instance of InputStream.
func NewInputStream(data string) *InputStream {
	inputData := []rune(data)

	return &InputStream{
		name:  "<empty>",
		index: 0,
		data:  inputData,
		size:  len(inputData),
	}
}

func (is *InputStream) reset() {
	is.index = 0
}

// Consume advances the input to the next rune.
func (is *InputStream) Consume() {
	if is.index >= is.size {
		// assert is.LA(1) == TokenEOF
		panic("cannot consume EOF")
	}
	is.index++
}

// LA returns the rune at the given offset.
func (is *InputStream) LA(offset int) int {

	if offset == 0 {
		return 0 // nil
	}
	if offset < 0 {
		offset++ // e.g., translate LA(-1) to use offset=0
	}
	pos := is.index + offset - 1

	if pos < 0 || pos >= is.size { // invalid
		return TokenEOF
	}

	return int(is.data[pos])
}

// LT returns the rune at the given offset.
func (is *InputStream) LT(offset int) int {
	return is.LA(offset)
}

// Index returns the current index into the input.
func (is *InputStream) Index() int {
	return is.index
}

// Size returns the size of the input.
func (is *InputStream) Size() int {
	return is.size
}

// Mark does nothing
func (is *InputStream) Mark() int {
	return -1
}

// Release does nothing
func (is *InputStream) Release(marker int) {}

// Seek moves the index to the given position. If it is larger than the input
// size, the latter is used instead.
func (is *InputStream) Seek(index int) {
	if index <= is.index {
		is.index = index // just jump don't update stream state (line,...)
		return
	}
	// seek forward
	is.index = intMin(index, is.size)
}

// GetText returns the text between the given offsets.
func (is *InputStream) GetText(start int, stop int) string {
	if stop >= is.size {
		stop = is.size - 1
	}
	if start >= is.size {
		return ""
	}

	return string(is.data[start : stop+1])
}

// GetTextFromTokens returns the text contained between the given tokens.
func (is *InputStream) GetTextFromTokens(start, stop Token) string {
	if start != nil && stop != nil {
		return is.GetTextFromInterval(NewInterval(start.GetTokenIndex(), stop.GetTokenIndex()))
	}

	return ""
}

// GetTextFromInterval returns contained in the given interval.
func (is *InputStream) GetTextFromInterval(i *Interval) string {
	return is.GetText(i.Start, i.Stop)
}

// GetSourceName returns the name of the source
func (*InputStream) GetSourceName() string {
	return "Obtained from string"
}

// String implements the Stringer interface.
func (is *InputStream) String() string {
	return string(is.data)
}
