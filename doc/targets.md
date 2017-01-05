# Runtime Libraries and Code Generation Targets

This page lists the available and upcoming ANTLR runtimes. Please note that you won't find here language specific code generators. This is because there is only one tool, written in Java, which is able to generate lexer and parser code for all targets, through command line options. The tool can be invoked from the command line, or any integration plugin to popular IDEs and build systems: Eclipse, IntelliJ, Visual Studio, Maven. So whatever your environment and target is, you should be able to run the tool and produce the code in the targeted language. As of writing, the available targets are the following:

* [Java](java-target.md). The [ANTLR v4 book](http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference) has a decent summary of the runtime library.  We have added a useful XPath feature since the book was printed that lets you select bits of parse trees. See [Runtime API](http://www.antlr.org/api/Java/index.html) and [Getting Started with ANTLR v4](getting-started.md)
* [C#](csharp-target.md)
* [Python](python-target.md) (2 and 3)
* [JavaScript](javascript-target.md)
* [Go](go-target.md)
* [C++](cpp-target.md)
* [Swift](swift-target.md)

## Target feature parity

New features generally appear in the Java target and then migrate to the other targets, but these other targets don't always get updated in the same overall tool release. This section tries to identify features added to Java that have not been added to the other targets.

|Feature|Java|C&sharp;|Python2|Python3|JavaScript|Go|C++|Swift|
|---|---|---|---|---|---|---|---|---|
|Ambiguous tree construction|4.5.1|-|-|-|-|-|-|-|

