# Options

There are a number of options that you can specify at the grammar and rule element level. (There are currently no rule options.) These change how ANTLR generates code from your grammar. The general syntax is:

```
options { name1=value1; ... nameN=valueN; } // ANTLR not target language syntax
```

where a value can be an identifier, a qualified identifier (for example, a.b.c), a string, a multi-line string in curly braces `{...}`, and an integer.

## Grammar Options

All grammars can use the following options. In combined grammars, all options except language pertain only to the generated parser. Options may be set either within the grammar file using the options syntax (described above) or when invoking ANTLR on the command line, using the `-D` option. (see Section 15.9, [ANTLR Tool Command Line Options](tool-options.md).) The following examples demonstrate both mechanisms; note that `-D` overrides options within the grammar.

## Rule Options

There are currently no valid rule-level options, but the tool still supports the following syntax for future use:

```
rulename
options {...}
 	: ...
 	;
```

## Rule Element Options

Token options have the form `T<name=value>` as we saw in Section 5.4, [Dealing with Precedence, Left Recursion, and Associativity](http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference). The only token option is assocand it accepts values left and right. Here’s a sample grammar with a left-recursive expression rule that specifies a token option on the `^` exponent operator token:

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

Semantic predicates also accept an option, per [Catching failed semantic predicates](http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference). The only valid option is the fail option, which takes either a string literal in double-quotes or an action that evaluates to a string. The string literal or string result from the action should be the message to emit upon predicate failure.

```
ints[int max]
 	locals [int i=1]
 	: INT ( ',' {$i++;} {$i<=$max}?<fail={"exceeded max "+$max}> INT )*
 	;
```

The action can execute a function as well as compute a string when a predicate fails: `{...}?<fail={doSomethingAndReturnAString()}>`
