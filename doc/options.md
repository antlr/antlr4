# Options

There are a number of options that you can specify at the grammar and rule element level. (There are currently no rule options.) These change how ANTLR generates code from your grammar. The general syntax is:

```
options { name1=value1; ... nameN=valueN; } // ANTLR not target language syntax
```

where a value can be an identifier, a qualified identifier (for example, a.b.c), a string, a multi-line string in curly braces `{...}`, and an integer.

## Grammar Options

All grammars can use the following options. In combined grammars, all options except language pertain only to the generated parser. Options may be set either within the grammar file using the options syntax (described above) or when invoking ANTLR on the command line, using the `-D` option. (see Section 15.9, [ANTLR Tool Command Line Options](tool-options.md).) The following examples demonstrate both mechanisms; note that `-D` overrides options within the grammar.

* `superClass`. Set the superclass of the generated parser or lexer. For combined grammars, it sets the superclass of the parser.
```
$ cat Hi.g4
grammar Hi;
a : 'hi' ;
$ antlr4 -DsuperClass=XX Hi.g4
$ grep 'public class' HiParser.java
public class HiParser extends XX {
$ grep 'public class' HiLexer.java
public class HiLexer extends Lexer {
```
* `language` Generate code in the indicated language, if ANTLR is able to do so. Otherwise, you will see an error message like this:
```
$ antlr4 -Dlanguage=C MyGrammar.g4
error(31):  ANTLR cannot generate C code as of version 4.0
```
* `tokenVocab` ANTLR assigns token type numbers to the tokens as it encounters them in a file. To use different token type values, such as with a separate lexer, use this option to have ANTLR pull in the <fileextension>tokens</fileextension> file. ANTLR generates a <fileextension>tokens</fileextension> file from each grammar.
```
$ cat SomeLexer.g4
lexer grammar SomeLexer;
ID : [a-z]+ ;
$ cat R.g4
parser grammar R;
options {tokenVocab=SomeLexer;}
tokens {A,B,C} // normally, these would be token types 1, 2, 3
a : ID ;
$ antlr4 SomeLexer.g4
$ cat SomeLexer.tokens 
ID=1
$ antlr4 R.g4
$ cat R.tokens
A=2
B=3
C=4
ID=1
```
* `TokenLabelType` ANTLR normally uses type <class>Token</class> when it generates variables referencing tokens. If you have passed a <class>TokenFactory</class> to your parser and lexer so that they create custom tokens, you should set this option to your specific type. This ensures that the context objects know your type for fields and method return values.
```
$ cat T2.g4
grammar T2;
options {TokenLabelType=MyToken;}
a : x=ID ;
$ antlr4 T2.g4
$ grep MyToken T2Parser.java
    public MyToken x;
```
* `contextSuperClass`. Specify the super class of parse tree internal nodes. Default is `ParserRuleContext`. Should derive from ultimately `RuleContext` at minimum.
Java target can use `contextSuperClass=org.antlr.v4.runtime.RuleContextWithAltNum` for convenience. It adds a backing field for `altNumber`, the alt matched for the associated rule node. 

## Rule Options

There are currently no valid rule-level options, but the tool still supports the following syntax for future use:

```
rulename
options {...}
 	: ...
 	;
```

## Rule Element Options

Token options have the form `T<name=value>` as we saw in Section 5.4, [Dealing with Precedence, Left Recursion, and Associativity](http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference). The only token option is `assoc`, and it accepts values `left` and `right`. Here’s a sample grammar with a left-recursive expression rule that specifies a token option on the `^` exponent operator token:

```
grammar ExprLR;
 	 
expr : expr '^'<assoc=right> expr
 	| expr '*' expr // match subexpressions joined with '*' operator
 	| expr '+' expr // match subexpressions joined with '+' operator
 	| INT // matches simple integer atom
 	;
 	 
INT : '0'..'9'+ ;
WS : [ \n]+ -> skip ;
```

Semantic predicates also accept an option, per [Catching failed semantic predicates](http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference). The only valid option is the `fail` option, which takes either a string literal in double-quotes or an action that evaluates to a string. The string literal or string result from the action should be the message to emit upon predicate failure.

```
ints[int max]
 	locals [int i=1]
 	: INT ( ',' {$i++;} {$i<=$max}?<fail={"exceeded max "+$max}> INT )*
 	;
```

The action can execute a function as well as compute a string when a predicate fails: `{...}?<fail={doSomethingAndReturnAString()}>`
