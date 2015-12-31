package antlr4

type IntStream interface {
	Consume()
	LA(int) int
	Mark() int
	Release(marker int)
	Index() int
	Seek(index int)
	Size() int
	GetSourceName() string
}
