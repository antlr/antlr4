package main
import (
	"antlr4"
	"./parser"
	"os"
)

type TreeShapeListener struct {
	*parser.BaseTListener
}

func NewTreeShapeListener() *TreeShapeListener {
	return new(TreeShapeListener)
}

func (this *TreeShapeListener) EnterEveryRule(ctx antlr4.ParserRuleContext) {
	for i := 0; i<ctx.GetChildCount(); i++ {
		child := ctx.GetChild(i)
		parentR,ok := child.GetParent().(antlr4.RuleNode)
		if !ok || parentR.GetBaseRuleContext() != ctx.GetBaseRuleContext() {
			panic("Invalid parse tree shape detected.")
		}
	}
}

func main() {
	input := antlr4.NewFileStream(os.Args[1])
	lexer := parser.NewTLexer(input)
	stream := antlr4.NewCommonTokenStream(lexer,0)
	p := parser.NewTParser(stream)
	p.BuildParseTrees = true
	tree := p.S()
	antlr4.ParseTreeWalkerDefault.Walk(NewTreeShapeListener(), tree)
}
