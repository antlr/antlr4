# Parse Trees

## How do I get the input text for a parse-tree subtree?

In ParseTree, you have this method:

```java
/** Return the combined text of all leaf nodes. Does not get any
 * off-channel tokens (if any) so won't return whitespace and
 * comments if they are sent to parser on hidden channel.
 */
String getText();
```

But, you probably want this method from TokenStream:

```java
/**
 * Return the text of all tokens in the source interval of the specified
 * context. This method behaves like the following code, including potential
 * exceptions from the call to {@link #getText(Interval)}, but may be
 * optimized by the specific implementation.
 *
 * <p>If {@code ctx.getSourceInterval()} does not return a valid interval of
 * tokens provided by this stream, the behavior is unspecified.</p>
 *
 * <pre>
 * TokenStream stream = ...;
 * String text = stream.getText(ctx.getSourceInterval());
 * </pre>
 *
 * @param ctx The context providing the source interval of tokens to get
 * text for.
 * @return The text of all tokens within the source interval of {@code ctx}.
 */
public String getText(RuleContext ctx);
```

That is, do this:

```
mytokens.getText(mySubTree);
```

## What if I need ASTs not parse trees for a compiler, for example?

For writing a compiler, either generate [LLVM-type static-single-assignment](http://llvm.org/docs/LangRef.html) form or construct an AST from the parse tree using a listener or visitor. Or, use actions in grammar, turning off auto-parse-tree construction.

## When do I use listener/visitor vs XPath vs Tree pattern matching?

### XPath

XPath works great when you need to find specific nodes, possibly in certain contexts. The context is limited to the parents on the way to the root of the tree. For example, if you want to find all ID nodes, use path `//ID`. If you want all variable declarations, you might use path `//vardecl`.  If you only want fields declarations, then you can use some context information via path `/classdef/vardecl`, which would only find vardecls that our children of class definitions. You can merge the results of multiple XPath `findAll()`s simulating a set union for XPath. The only caveat is that the order from the original tree is not preserved when you union multiple `findAll()` sets.

### Tree pattern matching

Use tree pattern matching when you want to find specific subtree structures such as all assignments to 0 using pattern `x = 0;`.  (Recall that these are very convenient because you specify the tree structure in the concrete syntax of the language described by the grammar.) If you want to find all assignments of any kind, you can use pattern `x = <expr>;` where `<expr>` will find any expression. This works great for matching particular substructures and therefore gives you a bit more ability to specify context. I.e., instead of just finding all identifiers, you can find all identifiers on the left hand side of an expression.

### Listeners/Visitors

Using the listener or visitor interfaces give you the most power but require implementing more methods. It might be more challenging to discover the emergent behavior of the listener than a simple tree pattern matcher that says *go find me X under node Y*.

Listeners are great when you want to visit many nodes in a tree.

Listeners allow you to compute and save context information necessary for processing at various nodes. For example, when building a symbol table manager for a compiler or translator, you need to compute symbol scopes such as globals, class, function, and code block. When you enter a class or function, you push a new scope and then pop it when you exit that class or function. When you see a symbol, you need to define it or look it up in the proper scope. By having enter/exit listener functions push and pop scopes, listener functions for defining variables simply say something like:

```java
scopeStack.peek().define(new VariableSymbol("foo"))
```

That way each listener function does not have to compute its appropriate scope.

Examples: [DefScopesAndSymbols.java](https://github.com/mantra/compiler/blob/master/src/java/mantra/semantics/DefScopesAndSymbols.java) and [SetScopeListener.java](https://github.com/mantra/compiler/blob/master/src/java/mantra/semantics/SetScopeListener.java) and [VerifyListener.java](https://github.com/mantra/compiler/blob/master/src/java/mantra/semantics/VerifyListener.java)