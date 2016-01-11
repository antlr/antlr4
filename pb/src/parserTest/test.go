package main

import (
	"antlr4"
	"parser"
	"fmt"
)

type MyListener struct {
	*parser.BaseJSONListener
}

func NewMyPrinter() *MyListener {
	return new(MyListener)
}

func (s *MyListener) EnterValue(ctx *parser.ValueContext) {
	fmt.Println(ctx.GetStart().GetText())
}

func main() {

	a := antlr4.NewFileStream("foo.txt")

	l := parser.NewJSONLexer(a)

	s := antlr4.NewCommonTokenStream(l, 0)

	p := parser.NewJSONParser(s)

	p.BuildParseTrees = true

	tree := p.Json()

	var printer = NewMyPrinter()

	antlr4.ParseTreeWalkerDefault.Walk(printer, tree);

	// fmt.Println( tree.GetChildren()[0].GetChildren()[0].GetChildren()[0].GetChildren()[0].GetChildren()[0].GetChildren()[0].GetChildren()[0] );

}
