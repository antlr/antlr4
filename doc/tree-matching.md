# Parse Tree Matching and XPath

*Since ANTLR 4.2*

ANTLR 4 introduced a visitor and listener mechanism that lets you implement DOM visiting or SAX-analogous event processing of tree nodes. This works great. For example, if all you care about is looking at Java method declarations, grab the `Java.g4` file and then override methodDeclaration in `JavaBaseListener`. From there, a `ParseTreeWalker` can trigger calls to your overridden method as it walks the tree. Easy things are easy.

This mechanism works more or less on a node-level basis. In other words, for every method declaration subtree root, your `methodDeclaration()` would get called. There are many situations where we care more about subtrees not just nodes. We might want to:

* Collect method declarations within a particular context (i.e., nested within another method) or methods with specific structure or specific types (e.g., `void <ID>() { }`). We'll combine `XPath` and tree pattern matching for this.
* Group translation operations by patterns in the tree rather than spreading operations across listener event methods.
* Get a list of all assignments anywhere in the tree. It's much easier to say *go find me all "... = ... ;" subtrees* rather than creating a class just to get a listener method for rule assignment and then passing the listener to the parse tree walker.

The other important idea here is that, since we're talking about parse trees not abstract syntax trees, we can use concrete patterns instead of tree syntax. For example, we can say `x = 0;` instead of AST `(= x 0)` where the `;` would probably stripped before it went into the AST.

## Parse tree patterns

To test a subtree to see if it has a particular structure, we use a tree pattern. We also often want to extract descendents from the subtree based upon the structure. A very simple example is checking to see if a subtree matches an assignment statement. The pattern might look like the following in your language:

```
<ID> = <expr>;
```

where "tags" in angle brackets represent either token or rule references in the associated grammar. ANTLR converts that string to a parse tree with special nodes that represent any token `ID` and rule `expr` subtree. To create this parse tree, the pattern matching compiler needs to know which rule in the grammar the pattern conforms to. In this case it might be statement. Here is how we could test a tree, `t`, to see if it matches that pattern:

```java
ParseTree t = ...; // assume t is a statement
ParseTreePattern p = parser.compileParseTreePattern("<ID> = <expr>;", MyParser.RULE_statement);
ParseTreeMatch m = p.match(t);
if ( m.succeeded() ) {...}
```

We can also test for specific expressions or token values. For example, the following checks to see if `t` is an expression consisting of an identifier added to 0:

```java
ParseTree t = ...; // assume t is an expression
ParseTreePattern p = parser.compileParseTreePattern("<ID>+0", MyParser.RULE_expr);
ParseTreeMatch m = p.match(t);
```

We can also ask the `ParseTreeMatch` result to pull out the token matched to the `<ID>` tag:

```java
String id = m.get("ID");
```

You can change the tag delimiters using a method on the pattern matcher:

```java
ParseTreePatternMatcher m = new ParseTreePatternMatcher();
m.setDelimiters("<<", ">>", "$"); // $ is the escape character
```

This would allow pattern `<<ID>> = <<expr>> ;$<< ick $>>` to be interpreted as elements: `ID`, ` = `, `expr`, and ` ;<< ick >>`.

### Pattern labels

The tree pattern matcher tracks the nodes in the tree at matches against the tags in a tree pattern. That way we can use the `get()` and `getAll()` methods to retrieve components of the matched subtree. For example, for pattern `<ID>`, `get("ID")` returns the node matched for that `ID`. If more than one node matched the specified token or rule tag, only the first match is returned. If there is no node associated with the label, this returns null.

You can also label the tags with identifiers. If the label is the name of a parser rule or token in the grammar, the resulting list from `getAll()` (or node from `get()`) will contain both the parse trees matching rule or tags explicitly labeled with the label and the complete set of parse trees matching the labeled and unlabeled tags in the pattern for the parser rule or token. For example, if label is `foo`, the result will contain all of the following.

* Parse tree nodes matching tags of the form `<foo:anyRuleName>` and `<foo:AnyTokenName>`.
* Parse tree nodes matching tags of the form `<anyLabel:foo>`.
* Parse tree nodes matching tags of the form `<foo>`.

### Creating parse trees with the pattern matcher

You can use the parse tree pattern compiler to create parse trees for partial input fragments. Just use method `ParseTreePattern.getPatternTree()`.

See [TestParseTreeMatch.java](https://github.com/antlr/antlr4/blob/master/tool-testsuite/test/org/antlr/v4/test/tool/TestParseTreeMatcher.java).

## Using XPath to identify parse tree node sets

XPath paths are strings representing nodes or subtrees you would like to select within a parse tree. It's useful to collect subsets of the parse tree to process. For example you might want to know where all assignments are in a method or all variable declarations that are initialized.

A path is a series of node names with the following separators.

| Expression |Description|
|---------|-----------|
|nodename|	Nodes with the token or rule name nodename
|/|	The root node but `/X` is the same as `X` since the tree you pass to xpath is assumed to be the root. Because it looks better, start all of your patterns with `/` (or `//` below).|
|//|	All nodes in the tree that match the next element in the path. E.g., `//ID` finds all `ID` token nodes in the tree.|
|!|	Any node except for the next element in the path. E.g., `/classdef/!field` should find all children of `classdef` root node that are not `field` subtrees.|

Examples:

```
/prog/func, -> all funcs under prog at root
/prog/*, -> all children of prog at root
/*/func, -> all func kids of any root node
prog, -> prog must be root node
/prog, -> prog must be root node
/*, -> any root
*, -> any root
//ID, -> any ID in tree
//expr/primary/ID, -> any ID child of a primary under any expr
//body//ID, -> any ID under a body
//'return', -> any 'return' literal in tree
//primary/*, -> all kids of any primary
//func/*/stat, -> all stat nodes grandkids of any func node
/prog/func/'def', -> all def literal kids of func kid of prog
//stat/';', -> all ';' under any stat node
//expr/primary/!ID, -> anything but ID under primary under any expr node
//expr/!primary, -> anything but primary under any expr node
//!*, -> nothing anywhere
/!*, -> nothing at root
```

Given a parse tree, the typical mechanism for visiting those nodes is the following loop:

```java
for (ParseTree t : XPath.findAll(tree, xpath, parser) ) {
    ... process t ...
}
```

E.g., here is a general formula for making a list of the text associated with every node identified by a path specification:

```java
List<String> nodes = new ArrayList<String>();
for (ParseTree t : XPath.findAll(tree, xpath, parser) ) {
    if ( t instanceof RuleContext) {
        RuleContext r = (RuleContext)t;
        nodes.add(parser.getRuleNames()[r.getRuleIndex()]);    }      
    else { 
        TerminalNode token = (TerminalNode)t;
        nodes.add(token.getText());
    }      
}
```

## Combining XPath and tree pattern matching

Naturally you can combine the use of XPath to find a set of root nodes and then use tree pattern matching to identify a certain subset of those and extract component nodes.

```java
// assume we are parsing Java
ParserRuleContext tree = parser.compilationUnit();
String xpath = "//blockStatement/*"; // get children of blockStatement
String treePattern = "int <Identifier> = <expression>;";
ParseTreePattern p =
    parser.compileParseTreePattern(treePattern,   
        ExprParser.RULE_localVariableDeclarationStatement);
List<ParseTreeMatch> matches = p.findAll(tree, xpath);
System.out.println(matches);
```

See [TestXPath.java](https://github.com/antlr/antlr4/blob/master/tool-testsuite/test/org/antlr/v4/test/tool/TestXPath.java).
