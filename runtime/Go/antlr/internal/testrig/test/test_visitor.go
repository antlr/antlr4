// Code generated from test.g4 by ANTLR 4.12.0. DO NOT EDIT.

package test // test
import "github.com/antlr/antlr4/runtime/Go/antlr/v4"

// A complete Visitor for a parse tree produced by testParser.
type testVisitor interface {
	antlr.ParseTreeVisitor

	// Visit a parse tree produced by testParser#stat.
	VisitStat(ctx *StatContext) interface{}

	// Visit a parse tree produced by testParser#expression.
	VisitExpression(ctx *ExpressionContext) interface{}
}
