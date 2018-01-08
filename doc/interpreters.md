# Parser and Lexer Interpreters

*Since ANTLR 4.2*

For small parsing tasks it is sometimes convenient to use ANTLR in interpreted mode, rather than generating a parser in a particular target, compiling it and running it as part of your application. Here's some sample code that creates lexer and parser Grammar objects and then creates interpreters. Once we have a ParserInterpreter, we can use it to parse starting in any rule we like, given a rule index (which the grammar + the parser can provide).

## Action Code

Since interpreters don't use generated parsers + lexers they cannot execute any action code (including predicates). That means the interpreter runs as if there were no predicates at all. If your grammar requires action code in order to parse correctly you will not be able to test it using this approach.

## Java Target Interpreter Setup

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
    LexerInterpreter lexEngine = g.createLexerInterpreter(CharStreams.fromPath(Paths.get(fileName)));
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
    CharStream input = CharStreams.fromPath(Paths.get(fileNameToParse));
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

See [TestParserInterpreter.java](../tool-testsuite/test/org/antlr/v4/test/tool/TestParserInterpreter.java).

## Non-Java Target Interpreter Setup
The ANTLR4 runtimes do not contain any grammar parsing classes (they are in the ANTLR4 tool  jar). Hence we cannot use `LexerGrammar` and `Grammar` to parse grammars for the interpreter. Instead we directly instantiate `LexerInterpreter` and `ParserInterpreter` objects. They require some data (namely symbol information and the ATNs) which only the ANTLR4 tool can give us. However, on each generation run ANTLR not only produces your parser + lexer files but also interpreter data files (*.interp) which contain all you need to feed the interpreters.

A support class (`InterpreterDataReader`) is used to load the data for your convenience, which makes this very easy to use. Btw. even the Java target go this route instead of using the non-runtime classes `Grammar` and `LexerGrammar`. Sometimes it might not be feasible to use the tool jar for whatever reason.

Here's how the setup looks like (C++ example):

```cpp
/**
 * sourceFileName - name of the file with content to parse
 * lexerName - the name of your lexer (arbitrary, that's what is used in error messages)
 * parserName - ditto for the parser
 * lexerDataFileName - the lexer interpeter data file name (e.g. `<path>/ExprLexer.interp`)
 * parserDataFileName - ditto for the parser (e.g. `<path>/Expr.interp`)
 * startRule - the name of the rule to start parsing at
 */
void parse(std::string const& sourceFileName,
  std::string const& lexerName, std::string const& parserName,
  std::string const& lexerDataFileName, std::string const& parserDataFileName,
  std::string const& startRule) {
  
    InterpreterData lexerData = InterpreterDataReader::parseFile(lexerDataFileName);
    InterpreterData parserData = InterpreterDataReader::parseFile(parserDataFileName);

    ANTLRFileStream input(sourceFileName);
    LexerInterpreter lexEngine(lexerName, lexerData.vocabulary, lexerData.ruleNames,
      lexerData.channels, lexerData.modes, lexerData.atn, &input);
    CommonTokenStream tokens(&lexEngine);

    /* Remove comment to print the tokens.
    tokens.fill();
    std::cout << "INPUT:" << std::endl;
    for (auto token : tokens.getTokens()) {
      std::cout << token->toString() << std::endl;
    }
    */

    ParserInterpreter parser(parserName, parserData.vocabulary, parserData.ruleNames,
      parserData.atn, &tokens);
    tree::ParseTree *tree = parser.parse(parser.getRuleIndex(startRule));

    std::cout << "parse tree: " << tree->toStringTree(&parser) << std::endl;
}
```
