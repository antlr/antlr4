package antlr

type TokenSource interface {
	NextToken() Token
	Skip()
	More()
	GetLine() int
	GetCharPositionInLine() int
	GetInputStream() CharStream
	GetSourceName() string
	setTokenFactory(factory TokenFactory)
	GetTokenFactory() TokenFactory
}
