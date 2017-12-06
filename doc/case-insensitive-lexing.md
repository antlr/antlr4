# Case-Insensitive Lexing

In some languages, keywords are case insensitive meaning that `BeGiN` means the same thing as `begin` or `BEGIN`. ANTLR has two mechanisms to support building grammars for such languages:

1. Build lexical rules that match either upper or lower case.
  * **Advantage**: no changes required to ANTLR, makes it clear in the grammar that the language in this case insensitive.
  * **Disadvantage**: might have a small efficiency cost and grammar is a more verbose and more of a hassle to write.

2. Build lexical rules that match keywords in all uppercase and then parse with a custom [character stream](https://github.com/antlr/antlr4/blob/master/runtime/Java/src/org/antlr/v4/runtime/CharStream.java) that converts all characters to uppercase before sending them to the lexer (via the `LA()` method). Care must be taken not to convert all characters in the stream to uppercase because characters within strings and comments should be unaffected. All we really want is to trick the lexer into thinking the input is all uppercase.
  * **Advantage**: Could have a speed advantage depending on implementation, no change required to the grammar.
  * **Disadvantage**: Requires that the case-insensitive stream and grammar are used in correctly in conjunction with each other, makes all characters appear as uppercase/lowercase to the lexer but some grammars are case sensitive outside of keywords, errors new case insensitive streams and language output targets (java, C#, C++, ...).

For the 4.7.1 release, we discussed both approaches in [detail](https://github.com/antlr/antlr4/pull/2046) and even possibly altering the ANTLR metalanguage to directly support case-insensitive lexing. We discussed including the case insensitive streams into the runtime but not all would be immediately supported. I decided to simply make documentation that clearly states how to handle this and include the appropriate snippets that people can cut-and-paste into their grammars.

## Case-insensitive grammars

As a prime example of a grammar that specifically describes case insensitive keywords, see the 
[SQLite grammar](https://github.com/antlr/grammars-v4/blob/master/sqlite/SQLite.g4).  To match a case insensitive keyword, there are rules such as

```
K_UPDATE : U P D A T E;
```

that will match `UpdaTE` and `upDATE` etc... as the `update` keyword. This rule makes use of some generically useful fragment rules that you can cut-and-paste into your grammars:

```
fragment A : [aA]; // match either an 'a' or 'A'
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];
```

No special streams are required to use this mechanism for case insensitivity.

## Custom character streams approach

The other approach is to use lexical rules that match either all uppercase or all lowercase, such as:

```
K_UPDATE : 'UPDATE';
```

Then, when creating the character stream to parse from, we need a custom class that overrides methods used by the lexer. Below you will find custom character streams for a number of the targets that you can copy into your projects, but here is how to use the streams in Java as an example:

```java
CharStream s = CharStreams.fromPath(Paths.get('test.sql'));
CaseChangingCharStream upper = new CaseChangingCharStream(s, true);
Lexer lexer = new SomeSQLLexer(upper);
```

Here are implementations of `CaseChangingCharStream` in various target languages:

* [Java](https://github.com/parrt/antlr4/blob/case-insensitivity-doc/doc/resources/CaseChangingCharStream.java)
* [JavaScript](https://github.com/parrt/antlr4/blob/case-insensitivity-doc/doc/resources/CaseInsensitiveInputStream.js)
* [Go](https://github.com/parrt/antlr4/blob/case-insensitivity-doc/doc/resources/case_changing_stream.go)
* [C#](https://github.com/parrt/antlr4/blob/case-insensitivity-doc/doc/resources/CaseChangingCharStream.cs)