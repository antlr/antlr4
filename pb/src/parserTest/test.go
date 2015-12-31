package main

import (
	"antlr4"
	"parser"
)

type MyErrorListener struct {
	*MyErrorListener
}

func main() {

	a := antlr4.NewFileStream("foo.txt")

	l := parser.NewArithmeticLexer(a)

	s := antlr4.NewCommonTokenStream(l, 0)

	p := parser.NewArithmeticParser(s)

	p.BuildParseTrees = true

	p.Equation()

}
