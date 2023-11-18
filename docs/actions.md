# Actions and Attributes

In Chapter 10, Attributes and Actions, we learned how to embed actions within grammars and looked at the most common token and rule attributes. This section summarizes the important syntax and semantics from that chapter and provides a complete list of all available attributes. (You can learn more about actions in the grammar from the free excerpt on listeners and actions.)

Actions are blocks of text written in the target language and enclosed in curly braces. The recognizer triggers them according to their locations within the grammar. For example, the following rule emits "found a decl" after the parser has seen a valid declaration:

```
decl: type ID ';' {System.out.println("found a decl");} ;
type: 'int' | 'float' ;
```

Most often, actions access the attributes of tokens and rule references:

```
decl: type ID ';'
      {System.out.println("var "+$ID.text+":"+$type.text+";");}
    | t=ID id=ID ';'
      {System.out.println("var "+$id.text+":"+$t.text+";");}
    ;
```

## Token Attributes

All tokens have a collection of predefined, read-only attributes. The attributes include useful token properties such as the token type and text matched for a token. Actions can access these attributes via `$label.attribute` where label labels a particular instance of a token reference (`a` and `b` in the example below are used in the action code as `$a` and `$b`). Often, a particular token is only referenced once in the rule, in which case the token name itself can be used unambiguously in the action code (token `INT` can be used as `$INT` in the action). The following example illustrates token attribute expression syntax:

```
r : INT {int x = $INT.line;}
    ( ID {if ($INT.line == $ID.line) ...;} )?
    a=FLOAT b=FLOAT {if ($a.line == $b.line) ...;}
  ;
```

The action within the `(...)?` subrule can see the `INT` token matched before it in the outer level.

Because there are two references to the `FLOAT` token, a reference to `$FLOAT` in an action is not unique; you must use labels to specify which token reference you’re interested in.

Token references within different alternatives are unique because only one of them can be matched for any invocation of the rule. For example, in the following rule, actions in both alternatives can reference `$ID` directly without using a label:

```
 	r : ... ID {System.out.println($ID.text);}
 	| ... ID {System.out.println($ID.text);}
 	;
```

To access the tokens matched for literals, you must use a label:

```
 	stat: r='return' expr ';' {System.out.println("line="+$r.line);} ;
```

Most of the time you access the attributes of the token, but sometimes it is useful to access the Token object itself because it aggregates all the attributes. Further, you can use it to test whether an optional subrule matched a token:

```
 	stat: 'if' expr 'then' stat (el='else' stat)?
 	{if ( $el!=null ) System.out.println("found an else");}
 	| ...
 	;
```

`$T` and `$L` evaluate to `Token` objects for token name `T` and token label `L`. `$ll` evaluates to `List<Token>` for list label `ll`. `$T.attr` evaluates to the type and value specified in the following table for attribute `attr`:


|Attribute|Type|Description|
|---------|----|-----------|
|text|String|The text matched for the token; translates to a call to getText. Example: $ID.text.|
|type|int|The token type (nonzero positive integer) of the token such as INT; translates to a call to getType. Example: $ID.type.|
|line|int|The line number on which the token occurs, counting from 1; translates to a call to getLine. Example: $ID.line.|
|pos|int|The character position within the line at which the token’s first character occurs counting from zero; translates to a call togetCharPositionInLine. Example: $ID.pos.|
|index|int|The overall index of this token in the token stream, counting from zero; translates to a call to getTokenIndex. Example: $ID.index.|
|channel|int|The token’s channel number. The parser tunes to only one channel, effectively ignoring off-channel tokens. The default channel is 0 (Token.DEFAULT_CHANNEL), and the default hidden channel is Token.HIDDEN_CHANNEL. Translates to a call to getChannel. Example: $ID.channel.|
|int|int|The integer value of the text held by this token; it assumes that the text is a valid numeric string. Handy for building calculators and so on. Translates to Integer.valueOf(text-of-token). Example: $INT.int.|

## Parser Rule Attributes

ANTLR predefines a number of read-only attributes associated with parser rule references that are available to actions. Actions can access rule attributes only for references that precede the action. The syntax is `$r.attr` for rule name `r` or a label assigned to a rule reference. For example, `$expr.text` returns the complete text matched by a preceding invocation of rule `expr`:

```
returnStat : 'return' expr {System.out.println("matched "+$expr.text);} ;
```

Using a rule label looks like this:

```
returnStat : 'return' e=expr {System.out.println("matched "+e.text);} ;
```

You can also use `$ followed by the name of the attribute to access the value associated with the currently executing rule. For example, `$start` is the starting token of the current rule.

```
returnStat : 'return' expr {System.out.println("first token "+$start.getText());} ;
```

`$r` and `$rl` evaluate to `ParserRuleContext` objects of type `RContext` for rule name `r` and rule label `rl`. `$rll` evaluates to `List<RContext>` for rule list label `rll`. `$r.attr` evaluates to the type and value specified in the following table for attribute `attr`:

|Attribute|Type|Description|
|---------|----|-----------|
|text|String|The text matched for a rule or the text matched from the start of the rule up until the point of the `$text` expression evaluation. Note that this includes the text for all tokens including those on hidden channels, which is what you want because usually that has all the whitespace and comments. When referring to the current rule, this attribute is available in any action including any exception actions.|
|start|Token|The first token to be potentially matched by the rule that is on the main token channel; in other words, this attribute is never a hidden token. For rules that end up matching no tokens, this attribute points at the first token that could have been matched by this rule. When referring to the current rule, this attribute is available to any action within the rule.|
|stop|Token|The last nonhidden channel token to be matched by the rule. When referring to the current rule, this attribute is available only to the after and finally actions.|
|ctx|ParserRuleContext|The rule context object associated with a rule invocation. All of the other attributes are available through this attribute. For example, `$ctx.start` accesses the start field within the current rules context object. It’s the same as `$start`.|

## Dynamically-Scoped Attributes

You can pass information to and from rules using parameters and return values, just like functions in a general-purpose programming language. Programming languages don’t allow functions to access the local variables or parameters of invoking functions, however. For example, the following reference to local variable `x` form a nested method call is illegal in Java:

```java
void f() {
	int x = 0;
	g();
}
void g() {
	h();
}
void h() {
	int y = x; // INVALID reference to f's local variable x
}
```

Variable `x` is available only within the scope of `f`, which is the text lexically delimited by curly brackets. For this reason, Java is said to use lexical scoping. Lexical scoping is the norm for most programming languages. Languages that allow methods further down in the call chain to access local variables defined earlier are said to use dynamic scoping. The term dynamic refers to the fact that a compiler cannot statically determine the set of visible variables. This is because the set of variables visible to a method changes depending on who calls that method.

It turns out that, in the grammar realm, distant rules sometimes need to communicate with each other, mostly to provide context information to rules matched below in the rule invocation chain. (Naturally, this assumes that you are using actions directly in the grammar instead of the parse-tree listener event mechanism.) ANTLR allows dynamic scoping in that actions can access attributes from invoking rules using syntax `$r::x` where `r` is a rule name and `x` is an attribute within that rule. It is up to the programmer to ensure that `r` is in fact an invoking rule of the current rule. A runtime exception occurs if `r` is not in the current call chain when you access `$r::x`.

To illustrate the use of dynamic scoping, consider the real problem of defining variables and ensuring that variables in expressions are defined. The following grammar defines the symbols attribute where it belongs in the block rule but adds variable names to it in rule `decl`. Rule `stat` then consults the list to see whether variables have been defined.

```
grammar DynScope;
 
prog: block ;
 
block
	/* List of symbols defined within this block */
	locals [
	List<String> symbols = new ArrayList<String>()
	]
	: '{' decl* stat+ '}'
	// print out all symbols found in block
	// $block::symbols evaluates to a List as defined in scope
	{System.out.println("symbols="+$symbols);}
	;
 
/** Match a declaration and add identifier name to list of symbols */
decl: 'int' ID {$block::symbols.add($ID.text);} ';' ;
 
/** Match an assignment then test list of symbols to verify
 * that it contains the variable on the left side of the assignment.
 * Method contains() is List.contains() because $block::symbols
 * is a List.
 */
stat: ID '=' INT ';'
	{
	if ( !$block::symbols.contains($ID.text) ) {
	System.err.println("undefined variable: "+$ID.text);
	}
	}
	| block
	;
 
ID : [a-z]+ ;
INT : [0-9]+ ;
WS : [ \t\r\n]+ -> skip ;
```

Here’s a simple build and test sequence:

```bash
$ antlr4 DynScope.g4
$ javac DynScope*.java
$ grun DynScope prog
=> 	{
=> 	int i;
=> 	i = 0;
=> 	j = 3;
=> 	}
=> 	EOF
<= 	undefined variable: j
 	symbols=[i]
```

There’s an important difference between a simple field declaration in a `@members` action and dynamic scoping. symbols is a local variable and so there is a copy for each invocation of rule `block`. That’s exactly what we want for nested blocks so that we can reuse the same input variable name in an inner block. For example, the following nested code block redefines `i` in the inner scope. This new definition must hide the definition in the outer scope.

```
{
	int i;
	int j;
	i = 0;
	{
		int i;
		int x;
		x = 5;
	}
	x = 3;
}
```

Here’s the output generated for that input by DynScope:

```bash
$ grun DynScope prog nested-input
symbols=[i, x]
undefined variable: x
symbols=[i, j]
```

Referencing `$block::symbols` accesses the `symbols` field of the most recently invoked `block`’s rule context object. If you need access to a symbols instance from a rule invocation farther up the call chain, you can walk backwards starting at the current context, `$ctx`. Use `getParent` to walk up the chain.
