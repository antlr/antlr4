// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestSemPredEvalParser-1452223572135/parser/T.g4 by ANTLR 4.5.1
package parser // T

import "antlr4"


// A complete Visitor for a parse tree produced by TParser.

type TVisitor interface {
    antlr4.ParseTreeVisitor

    // Visit a parse tree produced by TParser#s.
    VisitS(ctx *SContext) interface{}

    // Visit a parse tree produced by TParser#a.
    VisitA(ctx *AContext) interface{}

}