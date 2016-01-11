// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestParserExec-1452205020397/parser/T.g4 by ANTLR 4.5.1
package parser // T

import "antlr4"

// A complete listener for a parse tree produced by TParser

type TListener interface {
    antlr4.ParseTreeListener

    EnterIfStatement(*IfStatementContext)
    ExitIfStatement(*IfStatementContext)

    EnterElseIfStatement(*ElseIfStatementContext)
    ExitElseIfStatement(*ElseIfStatementContext)

    EnterExpression(*ExpressionContext)
    ExitExpression(*ExpressionContext)

    EnterExecutableStatement(*ExecutableStatementContext)
    ExitExecutableStatement(*ExecutableStatementContext)

    EnterElseStatement(*ElseStatementContext)
    ExitElseStatement(*ElseStatementContext)

}
