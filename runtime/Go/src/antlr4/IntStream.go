package antlr4

type IntStream interface {
	Consume()
	LA(int) int
	mark() int
	release(marker int)
	index() int
	seek(index int)
	size() int
	getSourceName() string
}
