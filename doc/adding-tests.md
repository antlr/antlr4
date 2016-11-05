# Adding unit tests

## Generating Runtime Tests

Because ANTLR supports multiple target languages, the unit tests are broken into two groups: the unit tests that test the tool itself (in `tool-testsuite`)  and the unit tests that test the parser runtimes (in antlr4/runtime-testsuite). To avoid a lot of cut-and-paste, we generate all **runtime** tests from a set of templates using [runtime-testsuite/src/org/antlr/v4/testgen/TestGenerator.java](../runtime-testsuite/src/org/antlr/v4/testgen/TestGenerator.java). The `mvn` command is simple to use:

```
$ cd ~/antlr/code/antlr4/runtime-testsuite
$ mvn -Pgen generate-test-sources
...
rootDir = /Users/parrt/antlr/code/antlr4/runtime-testsuite
outputDir = /Users/parrt/antlr/code/antlr4/runtime-testsuite/test
templates = /Users/parrt/antlr/code/antlr4/runtime-testsuite/resources/org/antlr/v4/test/runtime/templates
target = ALL
browsers = false
viz = false
```

It basically runs the Java program:

```bash
$ java org.antlr.v4.testgen.TestGenerator \
	-root ~/antlr/code/antlr4/runtime-testsuite \
	-outdir ~/antlr/code/antlr4/runtime-testsuite/test \
	-templates ~/antlr/code/antlr4/runtime-testsuite/resources/org/antlr/v4/test/runtime/templates
```

## Adding a runtime test

For each target, you will find an `Index.stg` file with a dictionary of all test groups. E.g., `runtime-testsuite/resources/org/antlr/v4/test/runtime/templates/Index.stg` looks like:

```
TestFolders ::= [
        "CompositeLexers": [],
        "CompositeParsers": [],
        "FullContextParsing": [],
        "LeftRecursion": [],
        "LexerErrors": [],
        "LexerExec": [],
        "Listeners": [],
        "ParserErrors": [],
        "ParserExec": [],
        "ParseTrees": [],
        "Performance": [],
        "SemPredEvalLexer": [],
        "SemPredEvalParser": [],
        "Sets": []
]
```

Then each group has a subdirectory with another index. E.g., `Sets/Index.stg` looks like:

```
TestTemplates ::= [
        "SeqDoesNotBecomeSet": [],
        "ParserSet": [],
        "ParserNotSet": [],
        "ParserNotToken": [],
        "ParserNotTokenWithLabel": [],
        "RuleAsSet": [],
        "NotChar": [],
        "OptionalSingleElement": [],
...
```

For every name mentioned, you will find a `.stg` file with the actual test template. E.g., `Sets/StarSet.stg`.

Each `.stg` file descripes the following mandatory elements for the test:
 - the test type: "Parser" or "Lexer"
 - some ANTLR options, such as "Debug"
 - the grammar 
 - the start rule
 - the input i.e. the text to parse
 - the expected output
 - the expected errors

The grammar can itself contain template expressions such as `<something>`.
The test generator replaces these with the corresponding values from the target language template (see below).
It then generates a unit test in which the grammar, the input and the expected output and errors are inlined.
 
Here is an example test template:

```
TestType() ::= "Parser"

Options ::= [
	"Debug": false
]

Grammar ::= [
	"T": {<grammar("T")>}
]

Input() ::= "abaac"

Rule() ::= "a"

Output() ::= <<
abaac<\n>
>>

Errors() ::= ""

grammar(grammarName) ::= <<
grammar <grammarName>;
a : ('a'|'b')* 'c' {<InputText():writeln()>} ;
>>
```


### Cross-language actions embedded within grammars

To get:

```
System.out.println($set.stop);
```

Use instead the language-neutral:

```
<writeln("$set.stop")>
```

File `runtime-testsuite/resources/org/antlr/v4/test/runtime/java/Java.test.stg` has templates like:

```
writeln(s) ::= <<System.out.println(<s>);>>
```

## Adding an ANTLR tool unit test

Just go into the appropriate Java test class in dir `antlr4/tool-testsuite/test/org/antlr/v4/test/tool` and add your unit test.


