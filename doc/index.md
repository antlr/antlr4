# ANTLR 4 Documentation

Please check [Frequently asked questions (FAQ)](faq/index.md) before asking questions on stackoverflow or antlr-discussion list.

Notes:
<ul>
<li>To add to or improve this documentation, <a href=https://help.github.com/articles/fork-a-repo>fork</a> the <a href=https://github.com/antlr/antlr4>antlr/antlr4 repo</a> then update this `doc/index.md` or file(s) in that directory.  Submit a <a href=https://help.github.com/articles/creating-a-pull-request>pull request</a> to get your changes incorporated into the main repository. Do not mix code and documentation updates in the sample pull request. <b>You must sign the contributors.txt certificate of origin with your pull request if you've not done so before.</b></li>

<li>Copyright © 2012, The Pragmatic Bookshelf.  Pragmatic Bookshelf grants a nonexclusive, irrevocable, royalty-free, worldwide license to reproduce, distribute, prepare derivative works, and otherwise use this contribution as part of the ANTLR project and associated documentation.</li>

<li>Much of this text was copied with permission from the <a href=http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference>The Definitive ANTLR 4 Reference</a>, though it is being morphed over time as the tool changes.</li>
</ul>

Links in the documentation refer to various sections of the book but have been redirected to the general book page on the publisher's site. There are two excerpts on the publisher's website that might be useful to you without having to purchase the book: [Let's get Meta](http://media.pragprog.com/titles/tpantlr2/picture.pdf) and [Building a Translator with a Listener](http://media.pragprog.com/titles/tpantlr2/listener.pdf). You should also consider reading the following books (the vid describes the reference book):

<a href=""><img src=images/tpantlr2.png width=120></a>
<a href=""><img src=images/tpdsl.png width=120></a>
<a href="https://www.youtube.com/watch?v=OAoA3E-cyug"><img src=images/teronbook.png width=250></a>

This documentation is a reference and summarizes grammar syntax and the key semantics of ANTLR grammars. The source code for all examples in the book, not just this chapter, are free at the publisher's website. The following video is a general tour of ANTLR 4 and includes a description of how to use parse tree listeners to process Java files easily:

<a href="https://vimeo.com/59285751"><img src=images/tertalk.png width=200></a>

For those using Java, here's a great [set of ANTLR in Intellij notes](https://docs.google.com/document/d/1gQ2lsidvN2cDUUsHEkT05L-wGbX5mROB7d70Aaj3R64/edit#heading=h.xr0jj8vcdsgc) by Andreas Stefik.

## Sections

* [Getting Started with ANTLR v4](getting-started.md)

* [Grammar Lexicon](lexicon.md)

* [Grammar Structure](grammars.md)

* [Parser Rules](parser-rules.md)

* [Left-recursive rules](left-recursion.md)

* [Actions and Attributes](actions.md)

* [Lexer Rules](lexer-rules.md)

* [Wildcard Operator and Nongreedy Subrules](wildcard.md)

* [Parse Tree Listeners](listeners.md)

* [Parse Tree Matching and XPath](tree-matching.md)

* [Semantic Predicates](predicates.md)

* [Options](options.md)

* [ANTLR Tool Command Line Options](tool-options.md)

* [Runtime Libraries and Code Generation Targets](targets.md)

* [Unicode U+FFFF, U+10FFFF character streams](unicode.md)

* [Parsing binary streams](parsing-binary-files.md)

* [Parser and lexer interpreters](interpreters.md)

* [Resources](resources.md)

# Building / releasing ANTLR itself

* [Building ANTLR itself](building-antlr.md)

* [Contributing to ANTLR](/CONTRIBUTING.md)

* [Cutting an ANTLR Release](releasing-antlr.md)

* [ANTLR project unit tests](antlr-project-testing.md)

* [Creating an ANTLR Language Target](creating-a-language-target.md)
