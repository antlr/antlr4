// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestParserExec-1452205020397/parser/T.g4 by ANTLR 4.5.1
package parser // T

import "antlr4"


// A complete Visitor for a parse tree produced by TParser.

type TVisitor interface {
    antlr4.ParseTreeVisitor

    // Visit a parse tree produced by TParser#ifStatement.
    VisitIfStatement(ctx *IfStatementContext) interface{}

    // Visit a parse tree produced by TParser#elseIfStatement.
    VisitElseIfStatement(ctx *ElseIfStatementContext) interface{}

    // Visit a parse tree produced by TParser#expression.
    VisitExpression(ctx *ExpressionContext) interface{}

    // Visit a parse tree produced by TParser#executableStatement.
    VisitExecutableStatement(ctx *ExecutableStatementContext) interface{}

    // Visit a parse tree produced by TParser#elseStatement.
    VisitElseStatement(ctx *ElseStatementContext) interface{}

}