package antlr4

type CharStream interface {
	IntStream

	GetTextFromInterval(*Interval) string
}
