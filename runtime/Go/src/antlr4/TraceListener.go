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

func (this *TraceListener) visitErrorNode(_ ErrorNode) {
}

func (this *TraceListener) enterEveryRule(ctx IParserRuleContext) {
	fmt.Println("enter   " + this.parser.GetRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + this.parser._input.LT(1).text())
}

func (this *TraceListener) visitTerminal(node TerminalNode) {
	fmt.Println("consume " + fmt.Sprint(node.getSymbol()) + " rule " + this.parser.GetRuleNames()[this.parser._ctx.getRuleIndex()])
}

func (this *TraceListener) exitEveryRule(ctx IParserRuleContext) {
	fmt.Println("exit    " + this.parser.GetRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + this.parser._input.LT(1).text())
}
