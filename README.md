# ANTLR v4

**ANTLR** (ANother Tool for Language Recognition) is a powerful parser generator for reading, processing, executing, or translating structured text or binary files. It's widely used to build languages, tools, and frameworks. From a grammar, ANTLR generates a parser that can build and walk parse trees.

* [Wikipedia](https://en.wikipedia.org/wiki/ANTLR)
* [Official site](http://www.antlr.org/)
* [ANTLR v3](http://www.antlr3.org/)
* [v3 to v4 Migration guide, differences](https://theantlrguy.atlassian.net/wiki/pages/viewpage.action?pageId=1900596)

## INTRODUCTION

Hi and welcome to the Honey Badger 4.2 release (February 3, 2014) of ANTLR!

## INSTALLATION

### UNIX

0. [Install Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (version 1.6 or higher)

1. Download
   ```sh
   $ cd /usr/local/lib
   $ curl -O http://www.antlr4.org/download/antlr-4.2-complete.jar
   ```
   Or just [download in browser](http://www.antlr4.org/download/antlr-4.2-complete.jar) and put it somewhere rational like `/usr/local/lib`.

2. Add `antlr-4.2-complete.jar` to your `CLASSPATH`:
   ```sh
   $ export CLASSPATH=".:/usr/local/lib/antlr-4.2-complete.jar:$CLASSPATH"
   ```
   Is also a good idea to put this in your .bash_profile or whatever your
   startup script is.

3. Create aliases for the ANTLR Tool, and TestRig.
   ```sh
   $ alias antlr4='java -jar /usr/local/lib/antlr-4.2-complete.jar'
   $ alias grun='java org.antlr.v4.runtime.misc.TestRig'
   ```

### Windows (Thanks to Graham Wideman)

0. [Install Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (version 1.6 or higher)

1. Download [antlr-4.2-complete.jar](http://www.antlr.org/download/antlr-4.2-complete.jar)
   Save to your directory for 3rd party Java libraries, say `C:\Javalib`

2. Add `antlr-4.2-complete.jar` to `CLASSPATH`, either:

 * Permanently: Using System Properties dialog > Environment variables >
   Create or append to `CLASSPATH` variable

 * Temporarily, at command line:
   `SET CLASSPATH=C:\Javalib\antlr-4.2-complete.jar;%CLASSPATH%`

3. Create short convenient commands for the ANTLR Tool, and TestRig,
   using batch files or doskey commands:

 * Batch files (in directory in system `PATH`)
   ```
   antlr4.bat: java org.antlr.v4.Tool %*
   run.bat:   java org.antlr.v4.runtime.misc.TestRig %*
   ```

 * Or, use doskey commands:
   ```bat
   doskey antlr4=java org.antlr.v4.Tool $*
   doskey grun  =java org.antlr.v4.runtime.misc.TestRig $*
   ```

## Testing installation

Either launch `org.antlr.v4.Tool` directly:
```sh
$ java org.antlr.v4.Tool
ANTLR Parser Generator Version 4.2
    -o ___              specify output directory where all output is generated
    -lib ___            specify location of .tokens files
...
```
or use `-jar` option on java:
```sh
$ java -jar /usr/local/lib/antlr-4.2-complete.jar
ANTLR Parser Generator Version 4.2
    -o ___              specify output directory where all output is generated
    -lib ___            specify location of .tokens files
...
```

## Example

In a temporary directory, put the following grammar inside file `Hello.g4`:
```g
// Define a grammar called Hello
// match keyword hello followed by an identifier
// match lower-case identifiers
grammar Hello;
r : 'hello' ID ;
ID : [a-z]+ ;
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
```
Then run ANTLR the tool on it:
```sh
$ cd /tmp
$ antlr4 Hello.g4
$ javac Hello*.java
```
Now test it:
```sh
$ grun Hello r -tree
hello parrt
^D
(r hello parrt)
```
(That `^D` means `EOF` on unix; it's `^Z` in Windows.) The `-tree` option prints the parse tree in LISP notation.

## The Definitive ANTLR 4 Reference
You can buy a book [The Definitive ANTLR 4 Reference](http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference)

Programmers run into parsing problems all the time. Whether it’s a data format like JSON, a network protocol like SMTP, a server configuration file for Apache, a PostScript/PDF file, or a simple spreadsheet macro language—ANTLR v4 and this book will demystify the process. ANTLR v4 has been rewritten from scratch to make it easier than ever to build parsers and the language applications built on top. This completely rewritten new edition of the bestselling Definitive ANTLR Reference shows you how to take advantage of these new features.

[Book source code](http://pragprog.com/titles/tpantlr2/source_code)

## Additional grammars
[This repository](https://github.com/antlr/grammars-v4) is a collection of grammars without actions where the
root directory name is the all-lowercase name of the language parsed
by the grammar. For example, java, cpp, csharp, c, etc...

## Authors

[Terence Parr](http://www.cs.usfca.edu/~parrt/), parrt@cs.usfca.edu
ANTLR project lead and supreme dictator for life<br/>
[University of San Francisco](http://www.usfca.edu/)

[Sam Harwell](http://tunnelvisionlabs.com/)
