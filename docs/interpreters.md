# Parser and lexer interpreters

*Since ANTLR 4.2*

For small parsing tasks it is sometimes convenient to use ANTLR in interpreted mode, rather than generating a parser in a particular target, compiling it and running it as part of your application. Here's some sample code that creates lexer and parser Grammar objects and then creates interpreters. Once we have a ParserInterpreter, we can use it to parse starting in any rule we like, given a rule index (which the Grammar can provide).

```java
LexerGrammar lg = new LexerGrammar(
    "lexer grammar L;\n" +
    "A : 'a' ;\n" +
    "B : 'b' ;\n" +
    "C : 'c' ;\n");
Grammar g = new Grammar(
    "parser grammar T;\n" +
    "s : (A|B)* C ;\n",
    lg);   
LexerInterpreter lexEngine =
    lg.createLexerInterpreter(new ANTLRInputStream(input));
CommonTokenStream tokens = new CommonTokenStream(lexEngine);
ParserInterpreter parser = g.createParserInterpreter(tokens);
ParseTree t = parser.parse(g.rules.get(startRule).index);
```

You can also load combined grammars from a file:

```java
public static ParseTree parse(String fileName,
                              String combinedGrammarFileName,
                              String startRule)
    throws IOException
{
    final Grammar g = Grammar.load(combinedGrammarFileName);
    LexerInterpreter lexEngine = g.createLexerInterpreter(new ANTLRFileStream(fileName));
    CommonTokenStream tokens = new CommonTokenStream(lexEngine);
    ParserInterpreter parser = g.createParserInterpreter(tokens);
    ParseTree t = parser.parse(g.getRule(startRule).index);
    System.out.println("parse tree: "+t.toStringTree(parser));
    return t;
}
```

Then:

```java
ParseTree t = parse("T.om",
                    MantraGrammar,
                    "compilationUnit");
```
 
To load separate lexer/parser grammars, do this:

```java
public static ParseTree parse(String fileNameToParse,
                              String lexerGrammarFileName,
                              String parserGrammarFileName,
                              String startRule)
    throws IOException
{
    final LexerGrammar lg = (LexerGrammar) Grammar.load(lexerGrammarFileName);
    final Grammar pg = Grammar.load(parserGrammarFileName, lg);
    ANTLRFileStream input = new ANTLRFileStream(fileNameToParse);
    LexerInterpreter lexEngine = lg.createLexerInterpreter(input);
    CommonTokenStream tokens = new CommonTokenStream(lexEngine);
    ParserInterpreter parser = pg.createParserInterpreter(tokens);
    ParseTree t = parser.parse(pg.getRule(startRule).index);
    System.out.println("parse tree: " + t.toStringTree(parser));
    return t;
}
```

Then:

```java
ParseTree t = parse(fileName, XMLLexerGrammar, XMLParserGrammar, "document");
```

This is also how we will integrate instantaneous parsing into ANTLRWorks2 and development environment plug-ins.

See [TestParserInterpreter.java](https://github.com/antlr/antlr4/blob/master/tool-testsuite/test/org/antlr/v4/test/tool/TestParserInterpreter.java).
