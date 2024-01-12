# Lexer Rules

A lexer grammar is composed of lexer rules, optionally broken into multiple modes. Lexical modes allow us to split a single lexer grammar into multiple sublexers. The lexer can only return tokens matched by rules from the current mode.

Lexer rules specify token definitions and more or less follow the syntax of parser rules except that lexer rules cannot have arguments, return values, or local variables. Lexer rule names must begin with an uppercase letter, which distinguishes them from parser rule names:

```
/** Optional document comment */
TokenName : alternative1 | ... | alternativeN ;
```

You can also define rules that are not tokens but rather aid in the recognition of tokens. These fragment rules do not result in tokens visible to the parser:

```
fragment
HelperTokenRule : alternative1 | ... | alternativeN ;
```

For example, `DIGIT` is a pretty common fragment rule:

```
INT : DIGIT+ ; // references the DIGIT helper rule
fragment DIGIT : [0-9] ; // not a token by itself
```

## Lexical Modes

Modes allow you to group lexical rules by context, such as inside and outside of XML tags. It’s like having multiple sublexers, one for context. The lexer can only return tokens matched by entering a rule in the current mode. Lexers start out in the so-called default mode. All rules are considered to be within the default mode unless you specify a mode command. Modes are not allowed within combined grammars, just lexer grammars. (See grammar `XMLLexer` from [Tokenizing XML](http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference).)

```
rules in default mode
...
mode MODE1;
rules in MODE1
...
mode MODEN;
rules in MODEN
...
```

## Lexer Rule Elements

Lexer rules allow two constructs that are unavailable to parser rules: the .. range operator and the character set notation enclosed in square brackets, [characters]. Don’t confuse character sets with arguments to parser rules. [characters] only means character set in a lexer. Here’s a summary of all lexer rule elements:

<table>
<tr>
<th>Syntax</th><th>Description</th>
</tr>
<tr>
<td>T</td><td>
Match token T at the current input position. Tokens always begin with a capital letter.</td>
</tr>

<tr>
<td>’literal’</td><td>
Match that character or sequence of characters. E.g., ’while’ or ’=’.</td>
</tr>

<tr>
<td>[char set]</td><td>
Match one of the characters specified in the character set. Interpret x-y as set of characters between range x and y, inclusively. The following escaped characters are interpreted as single special characters: \n, \r, \b, \t, and \f. To get ], \, or - you must escape them with \. You can also use Unicode character specifications: \uXXXX. Here are a few examples:

<pre>
WS : [ \n\u000D] -> skip ; // same as [ \n\r]
 	
ID : [a-zA-Z] [a-zA-Z0-9]* ; // match usual identifier spec
 	
DASHBRACK : [\-\]]+ ; // match - or ] one or more times
</pre>
</td>
</tr>

<tr>
<td>’x’..’y’</td><td>
Match any single character between range x and y, inclusively. E.g., ’a’..’z’. ’a’..’z’ is identical to [a-z].</td>
</tr>

<tr>
<td>T</td><td>
Invoke lexer rule T; recursion is allowed in general, but not left recursion. T can be a regular token or fragment rule.
 	
<pre>
ID : LETTER (LETTER|'0'..'9')* ;
 	
fragment
LETTER : [a-zA-Z\u0080-\u00FF_] ;
</pre>
</td>
</tr>

<tr>
<td>.</td><td>
The dot is a single-character wildcard that matches any single character. Example:
<pre>
ESC : '\\' . ; // match any escaped \x character
</pre>
</td>
</tr>

<tr>
<td>{«action»}</td><td>
Lexer actions can appear anywhere as of 4.2, not just at the end of the outermost alternative. The lexer executes the actions at the appropriate input position, according to the placement of the action within the rule. To execute a single action for a role that has multiple alternatives, you can enclose the alts in parentheses and put the action afterwards:
 	
<pre>
END : ('endif'|'end') {System.out.println("found an end");} ;
</pre>

<p>The action conforms to the syntax of the target language. ANTLR copies the action’s contents into the generated code verbatim; there is no translation of expressions like $x.y as there is in parser actions.</p>
<p>
Only actions within the outermost token rule are executed. In other words, if STRING calls ESC_CHAR and ESC_CHAR has an action, that action is not executed when the lexer starts matching in STRING.</p></td>
</tr>

<tr>
<td>{«p»}?</td><td>
Evaluate semantic predicate «p». If «p» evaluates to false at runtime, the surrounding rule becomes “invisible” (nonviable). Expression «p» conforms to the target language syntax. While semantic predicates can appear anywhere within a lexer rule, it is most efficient to have them at the end of the rule. The one caveat is that semantic predicates must precede lexer actions. See Predicates in Lexer Rules.</td>
</tr>

<tr>
<td>~x</td><td>
Match any single character not in the set described by x. Set x can be a single character literal, a range, or a subrule set like ~(’x’|’y’|’z’) or ~[xyz]. Here is a rule that uses ~ to match any character other than characters using ~[\r\n]*:
<pre> 	
COMMENT : '#' ~[\r\n]* '\r'? '\n' -> skip ;
</pre>
</td>
</tr>
</table>

Just as with parser rules, lexer rules allow subrules in parentheses and EBNF operators: `?`, `*`, `+`. The `COMMENT` rule illustrates the `*` and `?` operators. A common use of `+` is `[0-9]+` to match integers. Lexer subrules can also use the nongreedy `?` suffix on those EBNF operators.

## Recursive Lexer Rules

ANTLR lexer rules can be recursive, unlike most lexical grammar tools. This comes in really handy when you want to match nested tokens like nested action blocks: `{...{...}...}`.

```
lexer grammar Recur;
 
ACTION : '{' ( ACTION | ~[{}] )* '}' ;
 
WS : [ \r\t\n]+ -> skip ;
```

## Redundant String Literals

Be careful that you don’t specify the same string literal on the right-hand side of multiple lexer rules. Such literals are ambiguous and could match multiple token types. ANTLR makes this literal unavailable to the parser. The same is true for rules across modes. For example, the following lexer grammar defines two tokens with the same character sequence:

```
lexer grammar L;
AND : '&' ;
mode STR;
MASK : '&' ;
```

A parser grammar cannot reference literal ’&’, but it can reference the name of the tokens:

```
parser grammar P;
options { tokenVocab=L; }
a : '&' // results in a tool error: no such token
    AND // no problem
    MASK // no problem
  ;
```

Here’s a build and test sequence:

```bash
$ antlr4 L.g4 # yields L.tokens file needed by tokenVocab option in P.g4
$ antlr4 P.g4
error(126): P.g4:3:4: cannot create implicit token for string literal '&' in non-combined grammar
```

## Lexer Rule Actions

An ANTLR lexer creates a Token object after matching a lexical rule. Each request for a token starts in `Lexer.nextToken`, which calls `emit` once it has identified a token. `emit` collects information from the current state of the lexer to build the token. It accesses fields `_type`, `_text`, `_channel`, `_tokenStartCharIndex`, `_tokenStartLine`, and `_tokenStartCharPositionInLine`. You can set the state of these with the various setter methods such as `setType`. For example, the following rule turns `enum` into an identifier if `enumIsKeyword` is false.

```
ENUM : 'enum' {if (!enumIsKeyword) setType(Identifier);} ;
```

ANTLR does no special `$x` attribute translations in lexer actions (unlike v3).

There can be at most a single action for a lexical rule, regardless of how many alternatives there are in that rule.

## Lexer Commands

To avoid tying a grammar to a particular target language, ANTLR supports lexer commands. Unlike arbitrary embedded actions, these commands follow specific syntax and are limited to a few common commands. Lexer commands appear at the end of the outermost alternative of a lexer rule definition. Like arbitrary actions, there can only be one per token rule. A lexer command consists of the `->` operator followed by one or more command names that can optionally take parameters:

```
TokenName : «alternative» -> command-name
TokenName : «alternative» -> command-name («identifier or integer»)
```

An alternative can have more than one command separated by commas. Here are the valid command names:

* skip
* more
* popMode
* mode( x )
* pushMode( x )
* type( x )
* channel( x )

See the book source code for usage, some examples of which are shown here:

### skip

A 'skip' command tells the lexer to get another token and throw out the current text.

```
ID : [a-zA-Z]+ ; // match identifiers
INT : [0-9]+ ; // match integers
NEWLINE:'\r'? '\n' ; // return newlines to parser (is end-statement signal)
WS : [ \t]+ -> skip ; // toss out whitespace
```

### mode(), pushMode(), popMode, and more

The mode commands alter the mode stack and hence the mode of the lexer. The 'more' command forces the lexer to get another token but without throwing out the current text. The token type will be that of the "final" rule matched (i.e., the one without a more or skip command).

```
// Default "mode": Everything OUTSIDE of a tag
COMMENT : '<!--' .*? '-->' ;
CDATA   : '<![CDATA[' .*? ']]>' ;OPEN : '<' -> pushMode(INSIDE) ;
 ...
XMLDeclOpen : '<?xml' S -> pushMode(INSIDE) ;
SPECIAL_OPEN: '<?' Name -> more, pushMode(PROC_INSTR) ;
// ----------------- Everything INSIDE of a tag ---------------------
mode INSIDE;
CLOSE        : '>' -> popMode ;
SPECIAL_CLOSE: '?>' -> popMode ; // close <?xml...?>
SLASH_CLOSE  : '/>' -> popMode ;
```

Also check out:

```
lexer grammar Strings;
LQUOTE : '"' -> more, mode(STR) ;
WS : [ \r\t\n]+ -> skip ;
mode STR;
STRING : '"' -> mode(DEFAULT_MODE) ; // token we want parser to see
TEXT : . -> more ; // collect more text for string
```

Popping the bottom layer of a mode stack will result in an exception. Switching modes with `mode` changes the current stack top.  More than one `more` is the same as just one and the position does not matter.

### type()

```
lexer grammar SetType;
tokens { STRING }
DOUBLE : '"' .*? '"'   -> type(STRING) ;
SINGLE : '\'' .*? '\'' -> type(STRING) ;
WS     : [ \r\t\n]+    -> skip ;
```

For multiple 'type()' commands, only the rightmost has an effect.

### channel()

```
BLOCK_COMMENT
	: '/*' .*? '*/' -> channel(HIDDEN)
	;
LINE_COMMENT
	: '//' ~[\r\n]* -> channel(HIDDEN)
	;
... 
// ----------
// Whitespace
//
// Characters and character constructs that are of no import
// to the parser and are used to make the grammar easier to read
// for humans.
//
WS : [ \t\r\n\f]+ -> channel(HIDDEN) ;
```

As of 4.5, you can also define channel names like enumerations with the following construct above the lexer rules:

```
channels { WSCHANNEL, MYHIDDEN }
```
