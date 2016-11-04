# ANTLR4 Language Target, Runtime for Go

### Getting started

1. Get the runtime and install it on your GOPATH: `go get github.com/antlr/antlr4`
2. Generate the parser/lexer code: `antlr MyGrammar.g4 -Dlanguage=Go`

### Referencing in your code

Reference the go package like this:

```go
import "github.com/pboyer/antlr4/runtime/Go/antlr"
```