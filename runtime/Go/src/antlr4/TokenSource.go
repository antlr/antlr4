package antlr4

type TokenSource interface {
	nextToken() *Token
	skip()
	more()
	getLine() int
	getCharPositionInLine() int
	getInputStream() CharStream
	GetSourceName() string
	setTokenFactory(factory TokenFactory)
	GetTokenFactory() TokenFactory
}
