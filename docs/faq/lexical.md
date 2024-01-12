# Lexical analysis

## How can I parse non-ASCII text and use characters in token rules?

See [Using non-ASCII characters in token rules](http://stackoverflow.com/questions/28126507/antlr4-using-non-ascii-characters-in-token-rules/28129510#28129510).

## How do I replace escape characters in string tokens?

Unfortunately, manipulating the text of the token matched by a lexical rule is cumbersome (as of 4.2).  You have to build up a buffer and then set the text at the end. Actions in the lexer execute at the associated position in the input just like they do in the parser. Here's an example that does escape character replacement in strings. It's not pretty but it works.

```
grammar Foo;
 
@members {
StringBuilder buf = new StringBuilder(); // can't make locals in lexer rules
}
 
STR :   '"'
        (   '\\'
            (   'r'     {buf.append('\r');}
            |   'n'     {buf.append('\n');}
            |   't'     {buf.append('\t');}
            |   '\\'    {buf.append('\\');}
            |   '\"'   {buf.append('"');}
            )
        |   ~('\\'|'"') {buf.append((char)_input.LA(-1));}
        )*
        '"'
        {setText(buf.toString()); buf.setLength(0); System.out.println(getText());}
    ;
```

It's easier and more efficient to return original input string and then use a small function to rewrite the string later during a parse tree walk or whatever. But, here's how to do it from within the lexer.

Lexer actions don't work in the interpreter, which includes xpath and tree patterns.

For more on the argument against doing complicated things in the lexer, see the [related lexer-action issue at github](https://github.com/antlr/antlr4/issues/483#issuecomment-37326067).

## Why are my keywords treated as identifiers?

Keywords such as `begin` are also valid identifiers lexically and so that input is ambiguous. To resolve ambiguities, ANTLR gives precedence to the lexical rules specified first. That implies that you must put the identifier rule after all of your keywords:

```
grammar T;
 
decl : DEF 'int' ID ';'
 
DEF : 'def' ;   // ambiguous with ID as is 'int'
ID  : [a-z]+ ;
```

Notice that literal `'int'` is also physically before the ID rule and will also get precedence.

## Why are there no whitespace tokens in the token stream?

The lexer is not sending white space to the parser, which means that the rewrite stream doesn't have access to the tokens either. It is because of the skip lexer command:

```
WS : [ \t\r\n\u000C]+ -> skip
   ;
```

You have to change all those to `-> channel(HIDDEN)` which will send them to the parser on a different channel, making them available in the token stream, but invisible to the parser.