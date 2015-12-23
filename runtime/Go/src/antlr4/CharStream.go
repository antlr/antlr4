package antlr4

type CharStream interface {
	IntStream

	getTextFromInterval(*Interval) string
}
