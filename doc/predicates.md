# Semantic Predicates

Semantic predicates, `{...}?`, are boolean expressions written in the target language that indicate the validity of continuing the parse along the path "guarded" by the predicate. Predicates can appear anywhere within a parser rule just like actions can, but only those appearing on the left edge of alternatives can affect prediction (choosing between alternatives).  This section provides all of the fine print regarding the use of semantic predicates in parser and lexer rules. Let's start out by digging deeper into how the parser incorporates predicates into parsing decisions.

## Making Predicated Parsing Decisions

ANTLR's general decision-making strategy is to find all viable alternatives and then ignore the alternatives guarded with predicates that currently evaluate to false. (A viable alternative is one that matches the current input.) If more than one viable alternative remains, the parser chooses the alternative specified first in the decision.

Consider a variant of C++ where array references also use parentheses instead of square brackets. If we only predicate one of the alternatives, we still have an ambiguous decision in expr:

```
expr: ID '(' expr ')' // array reference (ANTLR picks this one)
 	| {istype()}? ID '(' expr ')' // ctor-style typecast
 	| ID '(' expr ')' // function call
 	;
```

In this case, all three alternatives are viable for input `x(i)`. When `x` is not a type name, the predicate evaluates to false, leaving only the first and third alternatives as possible matches for expr. ANTLR automatically chooses the first alternative matching the array reference to resolve the ambiguity. Leaving ANTLR with more than one viable alternative because of too few predicates is probably not a good idea. It's best to cover n viable alternatives with at least n-1 predicates. In other words, don't build rules like expr with too few predicates.

Sometimes, the parser finds multiple visible predicates associated with a single choice. No worries. ANTLR just combines the predicates with appropriate logical operators to conjure up a single meta-predicate on-the-fly.

For example, the decision in rule `stat` joins the predicates from both alternatives of expr with the `||` operator to guard the second stat alternative:

```
stat: decl | expr ;
decl: ID ID ;
expr: {istype()}? ID '(' expr ')' // ctor-style typecast
 	| {isfunc()}? ID '(' expr ')' // function call
 	;
```

The parser will only predict an expr from stat when `istype()||isfunc()` evaluates to true. This makes sense because the parser should only choose to match an expression if the upcoming `ID` is a type name or function name. It wouldn't make sense to just test one of the predicates in this case. Note that, when the parser gets to `expr` itself, the parsing decision tests the predicates individually, one for each alternative.

If multiple predicates occur in a sequence, the parser joins them with the `&&` operator. For example, consider changing `stat` to include a predicate before the call `toexpr`:

```
stat: decl | {java5}? expr ;
```

Now, the parser would only predict the second alternative if `java5&&(istype()||isfunc())` evaluated to true.

Turning to the code inside the predicates themselves now, keep in mind the following guidelines.

Even when the parser isn't making decisions, predicates can deactivate alternatives, causing rules to fail. This happens when a rule only has a single alternative. There is no choice to make, but ANTLR evaluates the predicate as part of the normal parsing process, just like it does for actions. That means that the following rule always fails to match.

```
prog: {false}? 'return' INT ; // throws FailedPredicateException
```

ANTLR converts `{false}?` in the grammar to a conditional in the generated parser:

```
if ( !false ) throw new FailedPredicateException(...);
```

So far, all of the predicates we've seen have been visible and available to the prediction process, but that's not always the case.

## Finding Visible Predicates

The parser will not evaluate predicates during prediction that occur after an action or token reference. Let's think about the relationship between actions and predicates first.

ANTLR has no idea what's inside the raw code of an action and so it must assume any predicate could depend on side effects of that action. Imagine an action that computed value `x` and a predicate that tested `x`. Evaluating that predicate before the action executed to create `x` would violate the implied order of operations within the grammar.

More importantly, the parser can't execute actions until it has decided which alternative to match. That's because actions have side effects and we can't undo things like print statements. For example, in the following rule, the parser can't execute the action in front of the `{java5}?` predicate before committing to that alternative.

```
@members {boolean allowgoto=false;}
stat: {System.out.println("goto"); allowgoto=true;} {java5}? 'goto' ID ';'
 	| ...
 	;
```

If we can't execute the action during prediction, we shouldn't evaluate the `{java5}?` predicate because it depends on that action.

The prediction process also can't see through token references. Token references have the side effect of advancing the input one symbol. A predicate that tested the current input symbol would find itself out of sync if the parser shifted it over the token reference. For example, in the following grammar, the predicates expect `getCurrentToken` to return an `ID` token.

```
stat: '{' decl '}'
 	| '{' stat '}'
 	;
decl: {istype(getCurrentToken().getText())}? ID ID ';' ;
expr: {isvar(getCurrentToken().getText())}? ID ;
```

The decision in stat can't test those predicates because, at the start of stat, the current token is a left curly. To preserve the semantics, ANTLR won't test the predicates in that decision.

Visible predicates are those that prediction encounters before encountering an action or token. The prediction process ignores nonvisible predicates, treating them as if they don't exist.

In rare cases, the parser won't be able to use a predicate, even if it's visible to a particular decision. That brings us to our next fine print topic.

## Using Context-Dependent Predicates

A predicate that depends on a parameter or local variable of the surrounding rule, is considered a context-dependent predicate. Clearly, we can only evaluate such predicates within the rules in which they're defined. For example, it makes no sense for the decision in prog below to test context-dependent predicate `{$i<=5}?`. That `$i` local variable is not even defined in `prog`.

```
prog: vec5
 	| ...
 	;
vec5
locals [int i=1]
 	: ( {$i<=5}? INT {$i++;} )* // match 5 INTs
 	;
```

ANTLR ignores context-dependent predicates that it can't evaluate in the proper context. Normally the proper context is simply the rule defining the predicate, but sometimes the parser can't even evaluate a context-dependent predicate from within the same rule! Detecting these cases is done on-the-fly at runtime during adaptive LL(*) prediction.

For example, prediction for the optional branch of the else subrule in stat below "falls off" the end of stat and continues looking for symbols in the invoking prog rule.

```
prog: stat+ ; // stat can follow stat
stat
locals [int i=0]
 	: {$i==0}? 'if' expr 'then' stat {$i=5;} ('else' stat)?
 	| 'break' ';'
 	;
```

The prediction process is trying to figure out what can follow an if statement other than an else clause. Since the input can have multiple stats in a row, the prediction for the optional branch of the else subrule reenters stat. This time, of course, it gets a new copy of `$i` with a value of 0, not 5. ANTLR ignores context-dependent predicate `{$i==0}?` because it knows that the parser isn't in the original stat call. The predicate would test a different version of `$i` so the parser can't evaluate it.

The fine print for predicates in the lexer more or less follow these same guidelines, except of course lexer rules can't have parameters and local variables. Let's look at all of the lexer-specific guidelines in the next section.

## Predicates in Lexer Rules

In parser rules, predicates must appear on the left edge of alternatives to aid in alternative prediction. Lexers, on the other hand, prefer predicates on the right edge of lexer rules because they choose rules after seeing a token's entire text. Predicates in lexer rules can technically be anywhere within the rule. Some positions might be more or less efficient than others; ANTLR makes no guarantees about the optimal spot. A predicate in a lexer rule might be executed multiple times even during a single token match. You can embed multiple predicates per lexer rule and they are evaluated as the lexer reaches them during matching.

Loosely speaking, the lexer's goal is to choose the rule that matches the most input characters. At each character, the lexer decides which rules are still viable. Eventually, only a single rule will be still viable. At that point, the lexer creates a token object according the rule's token type and matched text.

Sometimes the lexer is faced with more than a single viable matching rule. For example, input enum would match an `ENUM` rule and an `ID` rule. If the next character after enum is a space, neither rule can continue. The lexer resolves the ambiguity by choosing the viable rule specified first in the grammar. That's why we have to place keyword rules before an identifier rule like this:

```
ENUM : 'enum' ;
ID : [a-z]+ ;
```

If, on the other hand, the next character after input `enum` is a letter, then only `ID` is viable.

Predicates come into play by pruning the set of viable lexer rules. When the lexer encounters a false predicate, it deactivates that rule just like parsers deactivate alternatives with false predicates.

Like parser predicates, lexer predicates can't depend on side effects from lexer actions. That said, the predicate can depend on a side effect of an action that occured during the recognition of the previous token. That's because actions can only execute after the lexer positively identifies the rule to match. Since predicates are part of the rule selection process, they can't rely on action side effects created by actions in currently-prospective rules. Lexer actions must appear after predicates in lexer rules. As an example, here's another way to match enum as a keyword in the lexer:

```
ENUM: [a-z]+ {getText().equals("enum")}?
	   {System.out.println("enum!");}
    ;
ID  : [a-z]+ {System.out.println("ID "+getText());} ;
```

The print action in `ENUM` appears last and executes only if the current input matches `[a-z]+` and the predicate is true. Let's build and test `Enum3` to see if it distinguishes between enum and an identifier:

```bash
$ antlr4 Enum3.g4
$ javac Enum3.java
$ grun Enum3 tokens
=> 	enum abc
=> 	EOF
<= 	enum!
 	ID abc
```

That works great, but it's really just for instructional purposes. It's easier to understand and more efficient to match enum keywords with a simple rule like this:

```
ENUM : 'enum' ;
```

Here's another example of a predicate.  It's important to note that the predicate is evaluated before the action because actions are only executed if the lexer rule matches. The actions are not executed in line; they are collected and executed en mass later.

```
INDENT : [ \t]+ {System.out.println("INDENT")>} {this.getCharPositionInLine()==0}? ;
```

For more information on how actions and predicates operate in the lexer, see [Lexer actions and semantic predicates are executed out of order](https://github.com/antlr/antlr4/issues/3611) and [Lexer.getCharIndex() return value not behaving as expected](https://github.com/antlr/antlr4/issues/3606). The lexer rule that will not work as expected is:

```
Stuff : ( 'a'+ {count++;} | 'b') 'c' 'd' {count == 3}? ;
```

The `count++` code we'll not execute until after `Stuff` has been recognized (assuming count!=3).