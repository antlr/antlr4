// Package antlr provides the runtime implementation for recognizers generated
// by the antlr4 tool.
//
// Creating Go recognizers
//
// You can easily generate a Go recognizer by passing the -Dlanguage=Go option
// to the command-line tool:
//
//	$ ls
//	JSON5.g4
//	$ antlr4 -Dlanguage=Go JSON5.g4
//	$ ls
//	JSON5.g4      JSON5.tokens            json5_lexer.go     json5_parser.go
//	JSON5Lexer.tokens JSON5.interp  json5_base_listener.go  json5_listener.go
//	JSON5Lexer.interp
//
// Usage
//
// To print the parse tree using the JSON5 parser generated above:
//
//	package main
//
//	import (
//		// Import the antlr runtime using this path
//		"github.com/antlr/antlr4/runtime/Go/antlr"
//
//		// Suppose your parser is in this package
//		"github.com/user/my-project/parser"
//	)
//
//	func main() {
//		is := antlr.NewInputStream(`{"hello": "world"}`)
//		lx := parser.NewJSON5Lexer(is)
//		ts := antlr.NewCommonTokenStream(lx, antlr.TokenDefaultChannel)
//		pr := parser.NewJSON5Parser(ts)
//
//		fmt.Println(pr.Json5().ToStringTree(pr.RuleNames, pr))
//	}
//
// This will print:
//
//	(json5 (value (obj { (pair (key "hello") : (value "world")) })) <EOF>)
//
// Copyright
package antlr
