package antlr4

type TokenStream interface {
	IntStream

	LT(k int) *Token

	get(index int) *Token
	getTokenSource() TokenSource
	setTokenSource(TokenSource)

	getText() string
	getTextFromInterval(*Interval) string
	getTextFromRuleContext(IRuleContext) string
	getTextFromTokens(*Token, *Token) string
}
