package antlr4

type TokenSource interface {
	nextToken() *Token
	skip()
	more()
	getLine() int
	getCharPositionInLine() int
	getInputStream() CharStream
	getSourceName() string
	setTokenFactory(factory TokenFactory)
	getTokenFactory() TokenFactory
}
