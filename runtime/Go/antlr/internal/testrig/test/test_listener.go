// Code generated from test.g4 by ANTLR 4.12.0. DO NOT EDIT.

package test // test
import "github.com/antlr/antlr4/runtime/Go/antlr/v4"

// testListener is a complete listener for a parse tree produced by testParser.
type testListener interface {
	antlr.ParseTreeListener

	// EnterStat is called when entering the stat production.
	EnterStat(c *StatContext)

	// EnterExpression is called when entering the expression production.
	EnterExpression(c *ExpressionContext)

	// ExitStat is called when exiting the stat production.
	ExitStat(c *StatContext)

	// ExitExpression is called when exiting the expression production.
	ExitExpression(c *ExpressionContext)
}
