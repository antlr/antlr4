# General

## Why do we need ANTLR v4?

*Oliver Zeigermann asked me some questions about v4. Here is our conversation.*

*See the [preface from the book](http://media.pragprog.com/titles/tpantlr2/preface.pdf)*

**Q: Why is the new version of ANTLR also called “honey badger”?**

ANTLR v4 is called the honey badger release after the fearless hero of the YouTube sensation, The Crazy Nastyass Honey Badger.

**Q: Why did you create a new version of ANTLR?**

Well, I start creating a new version because v3 had gotten very messy on the inside and also relied on grammars written in ANTLR v2. Unfortunately, v2's open-source license was unclear and so projects such as Eclipse could not include v3 because of its dependency on v2. In the end, Sam Harwell converted all of the v2 grammars into v3 so that v3 was written in itself. Because v3 has a very clean BSD license, the Eclipse project okayed for inclusion in that project in the summer of 2011.

As I was rewriting ANTLR, I wanted to experiment with a new variation of the LL(\*) parsing algorithm. As luck would have it, I came up with a cool new version called adaptive LL(\*) that pushes all of the grammar analysis effort to runtime. The parser warms up like Java does with its JIT on-the-fly compiler; the code gets faster and faster the longer it runs. The benefit is that the adaptive algorithm is much stronger than the static LL(\*) grammar analysis algorithm in v3. Honey Badger takes any grammar that you give it; it just doesn't give a damn. (v4 accepts even left recursive grammars, except for indirectly left recursive grammars where x calls y which calls x).

v4 is the culmination of 25 years of research into parsers and parser generators. I think I finally know what I want to build. :)

**Q: What makes you excited about ANTLR4?**

The biggest thing is the new adaptive parsing strategy, which lets us accept any grammar we care to write. That gives us a huge productivity boost because we can now write much more natural expression rules (which occur in almost every grammar). For example, bottom-up parser generators such as yacc let you write very natural grammars like this:

```
e : e '*' e
  | e '+' e
  | INT
  ;
```

ANTLR v4 will also take that grammar now, translating it secretly to a non-left recursive version.

Another big thing with v4 is that my goal has shifted from performance to ease-of-use. For example, ANTLR automatically can build parse trees for you and generate listeners and visitors. This is not only a huge productivity win, but also an important step forward in building grammars that don't depend on embedded actions. Those embedded actions (raw Java code or whatever) locked the grammar into use with only one language. If we keep all of the actions out of the grammar and put them into external visitors, we can reuse the same grammar to generate code in any language for which we have an ANTLR target.

**Q: What do you think are the things people had problems with in ANTLR3?**

The biggest problem was figuring out why ANTLR did not like their grammar. The static analysis often could not figure out how to generate a parser for the grammar. This problem totally goes away with the honey badger because it will take just about anything you give it without a whimper.

**Q: And what with other compiler generator tools?**

The biggest problem for the average practitioner is that most parser generators do not produce code you can load into a debugger and step through. This immediately removes bottom-up parser generators and the really powerful GLR parser generators from consideration by the average programmer. There are a few other tools that generate source code like ANTLR does, but they don't have v4's adaptive LL(\*) parsers. You will be stuck with contorting your grammar to fit the needs of the tool's weaker, say, LL(k) parsing strategy. PEG-based tools have a number of weaknesses, but to mention one, they have essentially no error recovery because they cannot report an error and until they have parsed the entire input.

**Q: What are the main design decisions in ANTLR4?**

Ease-of-use over performance. I will worry about performance later. Simplicity over complexity. For example, I have taken out explicit/manual AST construction facilities and the tree grammar facilities. For 20 years I've been trying to get people to go that direction, but I've since decided that it was a mistake. It's much better to give people a parser generator that can automatically build trees and then let them use pure code to do whatever tree walking they want. People are extremely familiar and comfortable with visitors, for example.

**Q: What do you think people will like most on ANTLR4?**

The lack of errors when you run your grammar through ANTLR. The automatic tree construction and listener/visitor generation.

**What do you think are the problems people will try to solve with ANTLR4?**

In my experience, almost no one uses parser generators to build commercial compilers. So, people are using ANTLR for their everyday work, building everything from configuration files to little scripting languages.

In response to a question about this entry from stackoverflow.com: I believe that compiler developers are very concerned with parsing speed, error reporting, and error recovery. For that, they want absolute control over their parser. Also, some languages are so complicated, such as C++, that parser generators might build parsers slower than compiler developers want. The compiler developers also like the control of a recursive-descent parser for predicating the parse to handle context-sensitive constructs such as `T(i)` in C++.

There is also likely a sense that parsing is the easy part of building a compiler so they don't immediately jump automatically to parser generators. I think this is also a function of previous generation parser generators. McPeak's Elkhound GLR-based parser generator is powerful enough and fast enough, in the hands of someone that knows what they're doing, to be suitable for compilers. I can also attest to the fact that ANTLR v4 is now powerful enough and fast enough to compete well with handbuilt parsers. E.g., after warm-up, it's now taking just 1s to parse the entire JDK java/\* library.

## What is the difference between ANTLR 3 and 4?

The biggest difference between ANTLR 3 and 4 is that ANTLR 4 takes any grammar you give it unless the grammar had indirect left recursion. That means we don't need syntactic predicates or backtracking so ANTLR 4 does not support that syntax; you will get a warning for using it. ANTLR 4 allows direct left recursion so that expressing things like arithmetic expression syntax is very easy and natural:

```
expr : expr '*' expr
     | expr '+' expr
     | INT
     ;
```

ANTLR 4 automatically constructs parse trees for you and abstract syntax tree (AST) construction is no longer an option. See also [What if I need ASTs not parse trees for a compiler, for example?](https://github.com/antlr/antlr4/blob/master/doc/faq/parse-trees.md#what-if-i-need-asts-not-parse-trees-for-a-compiler-for-example).

Another big difference is that we discourage the use of actions directly within the grammar because ANTLR 4 automatically generates [listeners and visitors](https://github.com/antlr/antlr4/blob/master/doc/listeners.md) for you to use that trigger method calls when some phrases of interest are recognized during a tree walk after parsing. See also [Parse Tree Matching and XPath](https://github.com/antlr/antlr4/blob/master/doc/tree-matching.md).

Semantic predicates are still allowed in both the parser and lexer rules as our actions.  For efficiency sake keep semantic predicates to the right edge of lexical rules.

There are no tree grammars because we use listeners and visitors instead.

## Why is my expression parser slow?

Make sure to use two-stage parsing. See example in [bug report](https://github.com/antlr/antlr4/issues/374).

```Java

CharStream input = CharStreams.fromPath(Paths.get(args[0]));
ExprLexer lexer = new ExprLexer(input);
CommonTokenStream tokens = new CommonTokenStream(lexer);
ExprParser parser = new ExprParser(tokens);
parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
try {
    parser.stat();  // STAGE 1
}
catch (Exception ex) {
    tokens.reset(); // rewind input stream
    parser.reset();
    parser.getInterpreter().setPredictionMode(PredictionMode.LL);
    parser.stat();  // STAGE 2
    // if we parse ok, it's LL not SLL
}
```
