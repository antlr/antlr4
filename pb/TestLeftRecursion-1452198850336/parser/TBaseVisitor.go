// Generated from /var/folders/64/k10py5tj6r72zsmq16t6zy3r0000gn/T/TestLeftRecursion-1452198850336/parser/T.g4 by ANTLR 4.5.1
package parser // T

import "antlr4"

type BaseTVisitor struct {
    *antlr4.BaseParseTreeVisitor
}

func (v *BaseTVisitor) VisitS(ctx *SContext) interface{} {
    return v.VisitChildren(ctx)
}
func (v *BaseTVisitor) VisitE(ctx *EContext) interface{} {
    return v.VisitChildren(ctx)
}