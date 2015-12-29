package antlr4

type TokenSource interface {
	nextToken() IToken
	skip()
	more()
	getLine() int
	getCharPositionInLine() int
	GetInputStream() CharStream
	GetSourceName() string
	setTokenFactory(factory TokenFactory)
	GetTokenFactory() TokenFactory
}
