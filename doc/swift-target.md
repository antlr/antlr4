# ANTLR4 Language Target, Runtime for Swift

### Usage

#### 1. Install ANTLR4

[The getting started guide](getting-started.md) should get you started.

#### 2. create a Swift lexer or parser
This is pretty much the same as creating a Java lexer or parser, except you need to specify the language target, for example:

```
$ antlr4 -Dlanguage=Swift MyGrammar.g4
```
For a full list of antlr4 tool options, please visit the [tool documentation page](tool-options.md).

#### 3. Get the Swift ANTLR runtime
You will find Swift runtime (framework project) in

```
antlr4/runtime/Swift 
```

#### 4. Example

The example from [here](https://github.com/janyou/Antlr-Swift-Runtime/tree/master/Test)

(1). Hello.g4


```
grammar Hello;
r  : 'hello' ID ;          
ID : [a-z]+ ;              
WS : [ \t\r\n]+ -> skip ;  
```

(2). generate lexer/parser/visitor from Hello.g4 file

```
$ antlr4 -Dlanguage=Swift -visitor -o gen Hello.g4
```

in gen folder：

```
Hello.tokens
HelloBaseListener.swift
HelloBaseVisitor.swift
HelloLexer.swift
HelloLexer.tokens
HelloLexerATN.swift
HelloListener.swift
HelloParser.swift
HelloParserATN.swift
HelloVisitor.swift 
```

(3). make a custom listener

```
public class HelloWalker: HelloBaseListener{
    public override func enterR(_ ctx: HelloParser.RContext) {
        print( "enterR: " + ((ctx.ID()?.getText()) ?? ""))
    }

    public override func exitR(_ ctx: HelloParser.RContext) {
        print( "exitR  ")
    }
}

```

(4). call and run



add TestHello.txt

```
hello world
```

run:

```
import Antlr4

....

do {

	let textFileName = "TestHello.txt"
	if let textFilePath = Bundle.main.path(forResource: textFileName, ofType: nil) {
    	let lexer =  HelloLexer(ANTLRFileStream(textFilePath))
    	let tokens =  CommonTokenStream(lexer)
    	let parser = try HelloParser(tokens)
    	let tree = try parser.r()
    	let walker = ParseTreeWalker()
    	try walker.walk(HelloWalker(),tree)
	} else {
    	print("error occur: can not open \(textFileName)")
	}

}catch ANTLRException.cannotInvokeStartRule {
    print("error occur: CannotInvokeStartRule")
}catch ANTLRException.recognition(let e )   {
    print("error occur\(e)")
}catch {
    print("error occur")
}
```

