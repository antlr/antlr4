# Parse Tree Listeners

*Partially taken from publically visible [excerpt from ANTLR 4 book](http://media.pragprog.com/titles/tpantlr2/picture.pdf)*

By default, ANTLR-generated parsers build a data structure called a parse tree or syntax tree that records how the parser recognized the structure of the input sentence and component phrases.

<img src=images/process.png>

The interior nodes of the parse tree are phrase names that group and identify their children. The root node is the most abstract phrase name, in this case `stat` (short for statement). The leaves of a parse tree are always the input tokens. Parse trees sit between a language recognizer and an interpreter or translator implementation. They are extremely effective data structures because they contain all of the input and complete knowledge of how the parser grouped the symbols into phrases. Better yet, they are easy to understand and the parser generates them automatically (unless you turn them off with `parser.setBuildParseTree(false)`).

Because we specify phrase structure with a set of rules, parse tree subtree roots correspond to grammar rule names. ANTLR has a ParseTreeWalker that knows how to walk these parse trees and trigger events in listener implementation objects that you can create. The ANTLR tool generates listener interfaces for you also, unless you turn that off with a commandline option. You can also have it generate visitors. For example from a Java.g4 grammar, ANTLR generates:

```java
public interface JavaListener extends ParseTreeListener<Token> {
  void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx);
  void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx);
  void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx);
 ...
}
```

where there is an enter and exit method for each rule in the parser grammar. ANTLR also generates a base listener with the fall empty implementations of all listener interface methods, in this case called JavaBaseListener. You can build your listener by subclassing this base and overriding the methods of interest.

Assuming you've created a listener object called `MyListener`, here is how to call the Java parser and walk the parse tree:

```java
JavaLexer lexer = new JavaLexer(input);
CommonTokenStream tokens = new CommonTokenStream(lexer);
JavaParser parser = new JavaParser(tokens);
JavaParser.CompilationUnitContext tree = parser.compilationUnit(); // parse a compilationUnit

MyListener extractor = new MyListener(parser);
ParseTreeWalker.DEFAULT.walk(extractor, tree); // initiate walk of tree with listener in use of default walker
```

Listeners and visitors are great because they keep application-specific code out of grammars, making grammars easier to read and preventing them from getting entangled with a particular application.

See the book for more information on listeners and to learn how to use visitors. (The biggest difference between the listener and visitor mechanisms is that listener methods are called independently by an ANTLR-provided walker object, whereas visitor methods must walk their children with explicit visit calls.  Forgetting to invoke visitor methods on a node’s children, means those subtrees don’t get visited.)

## Listening during the parse

We can also use listeners to execute code during the parse instead of waiting for a tree walker walks the resulting parse tree. Let's say we have the following simple expression grammar.

```
grammar CalcNoLR;

s : expr EOF ;

expr:	add ((MUL | DIV) add)* ;

add :   atom ((ADD | SUB) atom)* ;

atom : INT ;

INT : [0-9]+;
MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';
WS : [ \t]+ -> channel(HIDDEN);
```

We can create a listener that executes during the parse by implementing the listener interface as before:


```java
class CountListener extends CalcNoLRBaseListener {
	public int nums = 0;
	public boolean execExitS = false;

	@Override
	public void exitS(CalcNoLRParser.SContext ctx) {
		execExitS = true;
	}

	@Override
	public void exitAtom(CalcNoLRParser.AtomContext ctx) {
		nums++;
	}
}
```

And then passing it to `addParseListener()`:

```java
String input = "2 + 8 / 2";
CalcNoLRLexer lexer = new CalcNoLRLexer(new ANTLRInputStream(input));
CalcNoLRParser parser = new CalcNoLRParser(new CommonTokenStream(lexer));
CountListener counter = new CountListener();
parser.addParseListener(counter);

// Check that the purses valid first
CalcNoLRParser.SContext context = parser.s();
String parseTreeS = context.toStringTree(parser);
assertEquals("(s (expr (add (atom 2) + (atom 8)) / (add (atom 2))) <EOF>)", parseTreeS);
assertEquals(3, counter.nums);
assertEquals(true, counter.execExitS);
```

One should not do very complicated work during the parse because the parser is throwing exception to handle syntax tears. If you're complicated code throws different kind of exception it will screw up the parsing and things will go nuts. If you want to catch and properly handle exceptions in your listener code during the parse, you should override this method from `Parser`:

```java
protected boolean listenerExceptionOccurred = false;

/**
 * Notify any parse listeners of an exit rule event.
 *
 * @see #addParseListener
 */
@override
protected void triggerExitRuleEvent() {
	if ( listenerExceptionOccurred ) return;
	try {
		// reverse order walk of listeners
		for (int i = _parseListeners.size() - 1; i >= 0; i--) {
			ParseTreeListener listener = _parseListeners.get(i);
			_ctx.exitRule(listener);
			listener.exitEveryRule(_ctx);
		}
	}
	catch (Throwable e) {
		// If an exception is thrown in the user's listener code, we need to bail out
		// completely out of the parser, without executing anymore user code. We
		// must also stop the parse otherwise other listener actions will attempt to execute
		// almost certainly with invalid results. So, record the fact an exception occurred
		listenerExceptionOccurred = true;
		throw e;
	}
}
```

Now, if you throw an exception inside one of the listener methods:

```java
// Now throw an exception in the listener
class ErrorListener extends CalcNoLRBaseListener {
	public boolean execExitS = false;
	public boolean execExitAtom = false;

	@Override
	public void exitS(CalcNoLRParser.SContext ctx) {
		execExitS = true;
	}

	@Override
	public void exitAtom(CalcNoLRParser.AtomContext ctx) {
		execExitAtom = true;
		throw new NullPointerException("bail out");
	}
}
```

then the exception will properly cause the parser to bailout and the exception will not be thrown out:

```
java.lang.NullPointerException: bail out

	at org.antlr.v4.test.runtime.java.api.TestParseListener$2ErrorListener.exitAtom(TestParseListener.java:102)
	at org.antlr.v4.test.runtime.java.api.CalcNoLRParser$AtomContext.exitRule(CalcNoLRParser.java:311)
	at org.antlr.v4.runtime.Parser.triggerExitRuleEvent(Parser.java:412)
	at org.antlr.v4.runtime.Parser.exitRule(Parser.java:654)
	at org.antlr.v4.test.runtime.java.api.CalcNoLRParser.atom(CalcNoLRParser.java:336)
	at org.antlr.v4.test.runtime.java.api.CalcNoLRParser.add(CalcNoLRParser.java:261)
	at org.antlr.v4.test.runtime.java.api.CalcNoLRParser.expr(CalcNoLRParser.java:181)
	at org.antlr.v4.test.runtime.java.api.CalcNoLRParser.s(CalcNoLRParser.java:123)
```