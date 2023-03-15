// Code generated from test.g4 by ANTLR 4.12.0. DO NOT EDIT.

package test // test
import "github.com/antlr/antlr4/runtime/Go/antlr/v4"

type BasetestVisitor struct {
	*antlr.BaseParseTreeVisitor
}

func (v *BasetestVisitor) VisitStat(ctx *StatContext) interface{} {
	return v.VisitChildren(ctx)
}

func (v *BasetestVisitor) VisitExpression(ctx *ExpressionContext) interface{} {
	return v.VisitChildren(ctx)
}
