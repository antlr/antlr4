package main
import (
	"antlr4"
	"./parser"
	"os"
	"fmt"
)

func main() {
	input := antlr4.NewFileStream(os.Args[1])
	lexer := parser.NewPositionAdjustingLexer(input)
	stream := antlr4.NewCommonTokenStream(lexer,0)
	stream.Fill()
	for _, t := range stream.GetAllTokens() {
		fmt.Println(t)
	}
}

