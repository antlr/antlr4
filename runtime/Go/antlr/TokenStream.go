package antlr4

type TokenStream interface {
	IntStream

	LT(k int) Token

	Get(index int) Token
	GetTokenSource() TokenSource
	SetTokenSource(TokenSource)

	GetAllText() string
	GetTextFromInterval(*Interval) string
	GetTextFromRuleContext(RuleContext) string
	GetTextFromTokens(Token, Token) string
}
