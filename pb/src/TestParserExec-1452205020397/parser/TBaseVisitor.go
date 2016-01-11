// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestParserExec-1452205020397/parser/T.g4 by ANTLR 4.5.1
package parser // T

import "antlr4"

type BaseTVisitor struct {
    *antlr4.BaseParseTreeVisitor
}

func (v *BaseTVisitor) VisitIfStatement(ctx *IfStatementContext) interface{} {
    return v.VisitChildren(ctx)
}
func (v *BaseTVisitor) VisitElseIfStatement(ctx *ElseIfStatementContext) interface{} {
    return v.VisitChildren(ctx)
}
func (v *BaseTVisitor) VisitExpression(ctx *ExpressionContext) interface{} {
    return v.VisitChildren(ctx)
}
func (v *BaseTVisitor) VisitExecutableStatement(ctx *ExecutableStatementContext) interface{} {
    return v.VisitChildren(ctx)
}
func (v *BaseTVisitor) VisitElseStatement(ctx *ElseStatementContext) interface{} {
    return v.VisitChildren(ctx)
}