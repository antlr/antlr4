# Case-Insensitive Lexing

In some languages, keywords are case insensitive meaning that `BeGiN` means the same thing as `begin` or `BEGIN`. ANTLR has two mechanisms to support building grammars for such languages:

1. build lexical rules that match either upper or lower case.<br>
**Advantage**: no changes required to ANTLR. <br>
**Disadvantage**: might have a small efficiency cost and grammar is a more verbose and more of a hassle to write.

2. build lexical rules that match keywords in all uppercase and then parse with a custom [character stream](https://github.com/antlr/antlr4/blob/master/runtime/Java/src/org/antlr/v4/runtime/CharStream.java) that converts all characters to uppercase before sending them to the lexer (via the `LA()` method). Care must be taken not to convert all characters in the stream to uppercase because characters within strings and comments should be unaffected. All we really want is to trick the lexer into thinking the input is all uppercase.<br>
**Advantage**: Could have a speed advantage depending on implementation, no change required to the grammar.<br>
**Disadvantage**: Requires that the case-insensitive stream and grammar are used in correctly in conjunction with each other, makes all characters appear as uppercase/lowercase to the lexer but some grammars are case sensitive outside of keywords.

For the 4.7.1 release, we discussed both approaches in [detail](https://github.com/antlr/antlr4/pull/2046) and even possibly altering the ANTLR metalanguage to directly support case-insensitive lexing. We discussed including
