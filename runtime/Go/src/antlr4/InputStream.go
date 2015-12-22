package antlr4

type InputStream struct {
	name    string
	index   int
	data    []rune
	size    int
}

func NewInputStream(data string) *InputStream {

	is := new(InputStream)

	is.name = "<empty>"
	is.index = 0
	is.data = []rune(data)
	is.size = len(is.data) // number of runes

	return is
}

func (is *InputStream) reset() {
	is.index = 0
}

func (is *InputStream) consume() {
	if is.index >= is.size {
		// assert is.LA(1) == TokenEOF
		panic("cannot consume EOF")
	}
	is.index += 1
}

func (is *InputStream) LA(offset int) int {
	if offset == 0 {
		return 0 // nil
	}
	if offset < 0 {
		offset += 1 // e.g., translate LA(-1) to use offset=0
	}
	var pos = is.index + offset - 1
	if pos < 0 || pos >= is.size { // invalid
		return TokenEOF
	}
	return int(is.data[pos])
}

func (is *InputStream) LT(offset int) int {
	return is.LA(offset)
}

// mark/release do nothing we have entire buffer
func (is *InputStream) mark() int {
	return -1
}

func (is *InputStream) release(marker int) {
}

func (is *InputStream) seek(index int) {
	if index <= is.index {
		is.index = index // just jump don't update stream state (line,...)
		return
	}
	// seek forward
	is.index = intMin(index, is.size)
}

func (is *InputStream) getText(start int, stop int) string {
	if stop >= is.size {
		stop = is.size - 1
	}
	if start >= is.size {
		return ""
	} else {
		return string(is.data[start:stop+1])
	}
}

func (is *InputStream) toString() string {
	return string(is.data)
}
