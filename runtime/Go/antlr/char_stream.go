package antlr

type CharStream interface {
	IntStream
	GetText(int, int) string
	GetTextFromTokens(start, end Token) string
	GetTextFromInterval(*Interval) string
}
