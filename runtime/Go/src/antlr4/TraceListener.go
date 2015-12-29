package antlr4

import "fmt"

type TraceListener struct {
	parser *Parser
}

func NewTraceListener(parser *Parser) *TraceListener {
	tl := new(TraceListener)
	tl.parser = parser
	return tl
}

func (this *TraceListener) VisitErrorNode(_ ErrorNode) {
}

func (this *TraceListener) EnterEveryRule(ctx IParserRuleContext) {
	fmt.Println("enter   " + this.parser.GetRuleNames()[ctx.GetRuleIndex()] + ", LT(1)=" + this.parser._input.LT(1).GetText())
}

func (this *TraceListener) VisitTerminal(node TerminalNode) {
	fmt.Println("consume " + fmt.Sprint(node.getSymbol()) + " rule " + this.parser.GetRuleNames()[this.parser._ctx.GetRuleIndex()])
}

func (this *TraceListener) ExitEveryRule(ctx IParserRuleContext) {
	fmt.Println("exit    " + this.parser.GetRuleNames()[ctx.GetRuleIndex()] + ", LT(1)=" + this.parser._input.LT(1).GetText())
}
