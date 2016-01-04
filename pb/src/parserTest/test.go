package main

import (
	"antlr4"
	"parser"
	"fmt"
)

type MyListener struct {
	*parser.BaseArithmeticListener
}

func NewMyPrinter() *MyListener {
	return new(MyListener)
}

func (k *MyListener) EnterExpression(ctx *parser.ExpressionContext) {
	fmt.Println("Oh, an expression!")
}

func (k *MyListener) EnterAtom(ctx *parser.AtomContext) {
	fmt.Println(ctx)
}

func main() {

	a := antlr4.NewFileStream("foo.txt")

	l := parser.NewArithmeticLexer(a)

	s := antlr4.NewCommonTokenStream(l, 0)

	p := parser.NewArithmeticParser(s)

	p.BuildParseTrees = true

	var tree = p.Equation()

	var printer = NewMyPrinter()
	antlr4.ParseTreeWalkerDefault.Walk(printer, tree);

	// fmt.Println( tree.GetChildren()[0].GetChildren()[0].GetChildren()[0].GetChildren()[0].GetChildren()[0].GetChildren()[0].GetChildren()[0] );

}
