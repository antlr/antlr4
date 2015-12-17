package antlr4

import (
	"math"
)

// Vacuums all input from a string and then treat it like a buffer.

type InputStream struct {
	name string
	strdata string
	index int
	data []rune
	size int
}

func NewInputStream(data string) *InputStream {

	is := new(InputStream)

	is.name = "<empty>"
	is.strdata = data
	loadString(is)

	return is
}

func loadString(stream *InputStream) {

	stream.index = 0
	stream.data = []rune(stream.strdata)
	stream.size = len(stream.data)

}

// Reset the stream so that it's in the same state it was
// when the object was created *except* the data array is not
// touched.
//
func (is *InputStream) reset() {
	is.index = 0
}

func (is *InputStream) consume() {
	if (is.index >= is.size) {
		// assert is.LA(1) == TokenEOF
		panic ("cannot consume EOF")
	}
	is.index += 1
}

func (is *InputStream) LA(offset int) {
	if (offset == 0) {
		return 0 // nil
	}
	if (offset < 0) {
		offset += 1 // e.g., translate LA(-1) to use offset=0
	}
	var pos = is.index + offset - 1
	if (pos < 0 || pos >= is.size) { // invalid
		return TokenEOF
	}
	return is.data[pos]
}

func (is *InputStream) LT(offset int) {
	return is.LA(offset)
}

// mark/release do nothing we have entire buffer
func (is *InputStream) mark() int {
	return -1
}

func (is *InputStream) release(marker int) {
}

// consume() ahead until p==index can't just set p=index as we must
// update line and column. If we seek backwards, just set p
//
func (is *InputStream) seek(index int) {
	if (index <= is.index) {
		is.index = index // just jump don't update stream state (line,...)
		return
	}
	// seek forward
	is.index = math.Min(index, is.size)
}

func (is *InputStream) getText(start int, stop int) string {
	if (stop >= is.size) {
		stop = is.size - 1
	}
	if (start >= is.size) {
		return ""
	} else {
		return is.strdata.slice(start, stop + 1)
	}
}

func (is *InputStream) toString() string {
	return is.strdata
}


