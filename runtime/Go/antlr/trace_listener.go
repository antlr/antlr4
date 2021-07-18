// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import "fmt"

// TraceListener listens to walks of the parse tree and prints the walked nodes.
type TraceListener struct {
	parser *BaseParser
}

// NewTraceListener returns a new instance of TraceListener.
func NewTraceListener(parser *BaseParser) *TraceListener {
	return &TraceListener{
		parser: parser,
	}
}

// VisitErrorNode does nothing
func (t *TraceListener) VisitErrorNode(_ ErrorNode) {}

// EnterEveryRule executes before the given node has been walked.
func (t *TraceListener) EnterEveryRule(ctx ParserRuleContext) {
	fmt.Println("enter   " + t.parser.GetRuleNames()[ctx.GetRuleIndex()] + ", LT(1)=" + t.parser.input.LT(1).GetText())
}

// VisitTerminal executes when a terminal node is walked.
func (t *TraceListener) VisitTerminal(node TerminalNode) {
	fmt.Println("consume " + fmt.Sprint(node.GetSymbol()) + " rule " + t.parser.GetRuleNames()[t.parser.ctx.GetRuleIndex()])
}

// ExitEveryRule executes after the given node has been walked.
func (t *TraceListener) ExitEveryRule(ctx ParserRuleContext) {
	fmt.Println("exit    " + t.parser.GetRuleNames()[ctx.GetRuleIndex()] + ", LT(1)=" + t.parser.input.LT(1).GetText())
}
