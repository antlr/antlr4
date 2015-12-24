package antlr4

type CharStream interface {
	IntStream

	GetText(int, int) string
	GetTextFromInterval(*Interval) string
}
