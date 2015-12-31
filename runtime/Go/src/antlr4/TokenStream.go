package antlr4

type TokenStream interface {
	IntStream

	LT(k int) IToken

	Get(index int) IToken
	GetTokenSource() TokenSource
	SetTokenSource(TokenSource)

	GetAllText() string
	GetTextFromInterval(*Interval) string
	GetTextFromRuleContext(RuleContext) string
	GetTextFromTokens(IToken, IToken) string
}
