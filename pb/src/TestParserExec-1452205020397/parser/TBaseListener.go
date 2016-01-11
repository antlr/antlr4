// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestParserExec-1452205020397/parser/T.g4 by ANTLR 4.5.1
package parser // T

import "antlr4"

// A complete base listener for a parse tree produced by TParser

type BaseTListener struct {
}

func (s *BaseTListener) VisitTerminal(node antlr4.TerminalNode){}

func (s *BaseTListener) VisitErrorNode(node antlr4.ErrorNode){}

func (s *BaseTListener) EnterEveryRule(ctx antlr4.ParserRuleContext){}

func (s *BaseTListener) ExitEveryRule(ctx antlr4.ParserRuleContext){}

func (s *BaseTListener) EnterIfStatement(ctx *IfStatementContext) {}

func (s *BaseTListener) ExitIfStatement(ctx *IfStatementContext){}

func (s *BaseTListener) EnterElseIfStatement(ctx *ElseIfStatementContext) {}

func (s *BaseTListener) ExitElseIfStatement(ctx *ElseIfStatementContext){}

func (s *BaseTListener) EnterExpression(ctx *ExpressionContext) {}

func (s *BaseTListener) ExitExpression(ctx *ExpressionContext){}

func (s *BaseTListener) EnterExecutableStatement(ctx *ExecutableStatementContext) {}

func (s *BaseTListener) ExitExecutableStatement(ctx *ExecutableStatementContext){}

func (s *BaseTListener) EnterElseStatement(ctx *ElseStatementContext) {}

func (s *BaseTListener) ExitElseStatement(ctx *ElseStatementContext){}

