package antlr4

type TokenStream interface {
	IntStream

	LT(k int) *Token

	get(index int) *Token
	GetTokenSource() TokenSource
	setTokenSource(TokenSource)

	GetText() string
	GetTextFromInterval(*Interval) string
	GetTextFromRuleContext(IRuleContext) string
	GetTextFromTokens(*Token, *Token) string
}
