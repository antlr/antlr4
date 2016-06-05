## ANTLR4 Language Target, Runtime for Go

### Usage

1. `go get http://github.com/pboyer/antlr4`
2. Get [StringTemplate](http://www.stringtemplate.org/)
3. Get [Maven](https://maven.apache.org/download.cgi)
  - Put `mvn` in your PATH (e.g. in `~/.bashrc` add `export PATH=$PATH:/path/to/apache-maven-3.3.9/bin`)
4. From the repo directory, `mvn install` (add `-DskipTests` to skip the tests)
5. Put StringTemplate and ANTLR binaries on your `CLASSPATH`
  - `export CLASSPATH=".:/path/to/ST-4.0.8.jar:$CLASSPATH"`
  - `export CLASSPATH=".:$GOPATH/src/github.com/pboyer/antlr4/tool/target/antlr4-4.5.2-SNAPSHOT.jar:$CLASSPATH"`
5. (Optional) Add an alias for calling antlr
  - `alias antlr='java -jar $GOPATH/src/github.com/pboyer/antlr4/tool/target/antlr4-4.5.2-SNAPSHOT.jar'`
6. Now, `antlr Grammar.g4 -Dlanguage=Go`

### Discuss

[gitter](https://gitter.im/pboyer/antlr4)

# ANTLR v4

**ANTLR** (ANother Tool for Language Recognition) is a powerful parser generator for reading, processing, executing, or translating structured text or binary files. It's widely used to build languages, tools, and frameworks. From a grammar, ANTLR generates a parser that can build parse trees and also generates a listener interface (or visitor) that makes it easy to respond to the recognition of phrases of interest.

*Given day-job constraints, my time working on this project is limited so I'll have to focus first on fixing bugs rather than changing/improving the feature set. Likely I'll do it in bursts every few months. Please do not be offended if your bug or pull request does not yield a response! --parrt*

## Authors and major contributors

* [Terence Parr](http://www.cs.usfca.edu/~parrt/), parrt@cs.usfca.edu
ANTLR project lead and supreme dictator for life
[University of San Francisco](http://www.usfca.edu/)
* [Sam Harwell](http://tunnelvisionlabs.com/) (Tool co-author, Java and C# target)
* Eric Vergnaud (Javascript, Python2, Python3 targets and significant work on C# target)

## Useful information

* [Release notes](https://github.com/antlr/antlr4/releases)
* [Getting started with v4](https://raw.githubusercontent.com/antlr/antlr4/master/doc/getting-started.md)
* [Official site](http://www.antlr.org/)
* [Documentation](https://raw.githubusercontent.com/antlr/antlr4/master/doc/index.md)
* [FAQ](https://raw.githubusercontent.com/antlr/antlr4/master/doc/faq/index.md)
* [API](http://www.antlr.org/api/Java/index.html)
* [ANTLR v3](http://www.antlr3.org/)
* [v3 to v4 Migration, differences](https://raw.githubusercontent.com/antlr/antlr4/master/doc/faq/general.md)

You might also find the following pages useful, particularly if you want to mess around with the various target languages.
 
* [How to build ANTLR itself](https://raw.githubusercontent.com/antlr/antlr4/master/doc/building-antlr.md)
* [How we create and deploy an ANTLR release](https://raw.githubusercontent.com/antlr/antlr4/master/doc/releasing-antlr.md)

## The Definitive ANTLR 4 Reference

Programmers run into parsing problems all the time. Whether it’s a data format like JSON, a network protocol like SMTP, a server configuration file for Apache, a PostScript/PDF file, or a simple spreadsheet macro language—ANTLR v4 and this book will demystify the process. ANTLR v4 has been rewritten from scratch to make it easier than ever to build parsers and the language applications built on top. This completely rewritten new edition of the bestselling Definitive ANTLR Reference shows you how to take advantage of these new features.

You can buy the book [The Definitive ANTLR 4 Reference](http://amzn.com/1934356999) at amazon or an [electronic version at the publisher's site](https://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference).

You will find the [Book source code](http://pragprog.com/titles/tpantlr2/source_code) useful.


## Additional grammars
[This repository](https://github.com/antlr/grammars-v4) is a collection of grammars without actions where the
root directory name is the all-lowercase name of the language parsed
by the grammar. For example, java, cpp, csharp, c, etc...
