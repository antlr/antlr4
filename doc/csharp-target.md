# C&sharp;

## Which frameworks are supported?

The C# runtime is CLS compliant, and only requires a corresponding 3.5 .Net framework.

In practice, the runtime has been extensively tested against:

* Microsoft .Net 3.5 framework
* Mono .Net 3.5 framework

No issue was found, so you should find that the runtime works pretty much against any recent .Net framework.

## How do I get started?

You will find full instructions on the [Git repo page for ANTLR C# runtime](https://github.com/antlr/antlr4/tree/master/runtime/CSharp).
 
## How do I use the runtime from my project?

(i.e., How do I run the generated lexer and/or parser?)

Let's suppose that your grammar is named `MyGrammar`. The tool will generate for you the following files:

*   MyGrammarLexer.cs
*   MyGrammarParser.cs
*   MyGrammarListener.cs (if you have not activated the -no-listener option)
*   MyGrammarBaseListener.cs (if you have not activated the -no-listener option)
*   MyGrammarVisitor.cs (if you have activated the -visitor option)
*   MyGrammarBaseVisitor.cs (if you have activated the -visitor option)

Now a fully functioning code might look like the following for start rule `StartRule`:

```csharp
using Antlr4.Runtime;
using Antlr4.Runtime.Tree;
     
public void MyParseMethod() {
      String input = "your text to parse here";
      ICharStream stream = CharStreams.fromString(input);
      ITokenSource lexer = new MyGrammarLexer(stream);
      ITokenStream tokens = new CommonTokenStream(lexer);
      MyGrammarParser parser = new MyGrammarParser(tokens);
      parser.BuildParseTree = true;
      IParseTree tree = parser.StartRule();
}
```

This program will work. But it won't be useful unless you do one of the following:

* you visit the parse tree using a custom listener
* you visit the parse tree using a custom visitor
* your grammar comprises production code (like AntLR3)

(please note that production code is target specific, so you can't have multi target grammars that include production code)
 
## How do I create and run a custom listener?

Let's suppose your MyGrammar grammar comprises 2 rules: "key" and "value".

The antlr4 tool will have generated the following listener (only partial code shown here): 

```csharp
interface IMyGrammarParserListener : IParseTreeListener {
      void EnterKey (MyGrammarParser.KeyContext context);
      void ExitKey (MyGrammarParser.KeyContext context);
      void EnterValue (MyGrammarParser.ValueContext context);
      void ExitValue (MyGrammarParser.ValueContext context);
}
```
 
In order to provide custom behavior, you might want to create the following class:
 
```csharp
class KeyPrinter : MyGrammarBaseListener {
    // override default listener behavior
    void ExitKey (MyGrammarParser.KeyContext context) {
        Console.WriteLine("Oh, a key!");
    }
}
```
   
In order to execute this listener, you would simply add the following lines to the above code:
 
 
```csharp
...
IParseTree tree = parser.StartRule() - only repeated here for reference
KeyPrinter printer = new KeyPrinter();
ParseTreeWalker.Default.Walk(printer, tree);
```
        
Further information can be found from The Definitive ANTLR Reference book.

The C# implementation of ANTLR is as close as possible to the Java one, so you shouldn't find it difficult to adapt the examples for C#. See also [Sam Harwell's alternative C# target](https://github.com/tunnelvisionlabs/antlr4cs)

