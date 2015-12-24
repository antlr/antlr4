package main

import (
	"antlr4"
	"parser"
)

func main() {

	a := antlr4.NewFileStream("foo.txt")

	l := parser.NewArithmeticLexer(a)

	s := antlr4.NewCommonTokenStream(l, 0)

	p := parser.NewArithmeticParser(s)

	p.Equation()

}




