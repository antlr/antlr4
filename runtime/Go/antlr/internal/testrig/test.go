package main

import (
    "github.com/antlr/antlr4/runtime/Go/antlr/v4"
    "testrig/test"
)

func main() {
    testRun("input")
}

func testRun(inf string) {
    
    // Pre-initialize so that we can distinguish this initialization from the lexing nad parsing rules
    test.TestLexerInit()
    test.TestParserInit()
    
    input, _ := antlr.NewFileStream(inf)
    lexer := test.NewtestLexer(input)
    stream := antlr.NewCommonTokenStream(lexer, 0)
    p := test.NewtestParser(stream)
    p.Stat()
}
