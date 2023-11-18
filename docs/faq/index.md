# Frequently-Asked Questions (FAQ)

This is the main landing page for the ANTLR 4 FAQ. The links below will take you to the appropriate file containing all answers for that subcategory.

*To add to or improve this FAQ, [fork](https://help.github.com/articles/fork-a-repo/) the [antlr/antlr4 repo](https://github.com/antlr/antlr4) then update this `doc/faq/index.md` or file(s) in that directory.  Submit a [pull request](https://help.github.com/articles/creating-a-pull-request/) to get your changes incorporated into the main repository. Do not mix code and FAQ updates in the sample pull request.* **You must sign the contributors.txt certificate of origin with your pull request if you've not done so before.**

## Getting Started

* [How to I install and run a simple grammar?](getting-started.md)
* [Why does my parser test program hang?](getting-started.md)

## Installation

* [Why can't ANTLR (grun) find my lexer or parser?](installation.md)
* [Why can't I run the ANTLR tool?](installation.md)
* [Why doesn't my parser compile?](installation.md)

## General

* [Why do we need ANTLR v4?](general.md)
* [What is the difference between ANTLR 3 and 4?](general.md)
* [Why is my expression parser slow?](general.md)

## Grammar syntax

## Lexical analysis

* [How can I parse non-ASCII text and use characters in token rules?](lexical.md)
* [How do I replace escape characters in string tokens?](lexical.md)
* [Why are my keywords treated as identifiers?](lexical.md)
* [Why are there no whitespace tokens in the token stream?](lexical.md)

## Parse Trees

* [How do I get the input text for a parse-tree subtree?](parse-trees.md)
* [What if I need ASTs not parse trees for a compiler, for example?](parse-trees.md)
* [When do I use listener/visitor vs XPath vs Tree pattern matching?](parse-trees.md)

## Translation

* [ASTs vs parse trees](parse-trees.md)
* [Decoupling input walking from output generation](parse-trees.md)

## Actions and semantic predicates

* [How do I test if an optional rule was matched?](actions-preds.md)

## Error handling

* [How do I perform semantic checking with ANTLR?](error-handling.md)
