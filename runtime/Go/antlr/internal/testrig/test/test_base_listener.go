// Code generated from test.g4 by ANTLR 4.12.0. DO NOT EDIT.

package test // test
import "github.com/antlr/antlr4/runtime/Go/antlr/v4"

// BasetestListener is a complete listener for a parse tree produced by testParser.
type BasetestListener struct{}

var _ testListener = &BasetestListener{}

// VisitTerminal is called when a terminal node is visited.
func (s *BasetestListener) VisitTerminal(node antlr.TerminalNode) {}

// VisitErrorNode is called when an error node is visited.
func (s *BasetestListener) VisitErrorNode(node antlr.ErrorNode) {}

// EnterEveryRule is called when any rule is entered.
func (s *BasetestListener) EnterEveryRule(ctx antlr.ParserRuleContext) {}

// ExitEveryRule is called when any rule is exited.
func (s *BasetestListener) ExitEveryRule(ctx antlr.ParserRuleContext) {}

// EnterStat is called when production stat is entered.
func (s *BasetestListener) EnterStat(ctx *StatContext) {}

// ExitStat is called when production stat is exited.
func (s *BasetestListener) ExitStat(ctx *StatContext) {}

// EnterExpression is called when production expression is entered.
func (s *BasetestListener) EnterExpression(ctx *ExpressionContext) {}

// ExitExpression is called when production expression is exited.
func (s *BasetestListener) ExitExpression(ctx *ExpressionContext) {}
