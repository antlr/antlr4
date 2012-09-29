ANTLR v4

Terence Parr, parrt at cs usfca edu
ANTLR project lead and supreme dictator for life
University of San Francisco

INTRODUCTION

Hi and welcome to the Honey Badger 4.0b2 release of ANTLR!

INSTALLATION

$ cd /usr/local/lib
$ curl -O --silent http://www.antlr.org/download/antlr-4.0b2-complete.jar

Or just download from http://www.antlr.org/download/antlr-4.0b2-complete.jar
and put it somewhere rational for your operating system.

You can either add to your CLASSPATH:

$ export CLASSPATH=".:/usr/local/lib/antlr-4.0b2-complete.jar:$CLASSPATH"

and launch org.antlr.v4.Tool directly:

$ java org.antlr.v4.Tool
ANTLR Parser Generator Version 4.0b2
    -o ___              specify output directory where all output is generated
    -lib ___            specify location of .tokens files
...

or use -jar option on java:

$ java -jar /usr/local/lib/antlr-4.0b2-complete.jar
ANTLR Parser Generator Version 4.0b2
    -o ___              specify output directory where all output is generated
    -lib ___            specify location of .tokens files
...

You can make a script, /usr/local/bin/antlr4:

#!/bin/sh
java -cp "/usr/local/lib/antlr4-complete.jar:$CLASSPATH" org.antlr.v4.Tool $*

On Windows, you can do something like this (assuming you put the
jar in C:\libraries) for antlr4.bat:

java -cp C:\libraries\antlr-4.0b2-complete.jar;%CLASSPATH% org.antlr.v4.Tool %*

You can also use an alias

$ alias antlr4='java -jar /usr/local/lib/antlr-4.0b2-complete.jar'

Either way, say just antlr4 to run ANTLR now.

The TestRig class is very useful for testing your grammars:

$ alias grun='java org.antlr.v4.runtime.misc.TestRig'

EXAMPLE

In /tmp/Hello.g4, paste this:

// Define a grammar called Hello
// match keyword hello followed by an identifier
// match lower-case identifiers
grammar Hello;
r : 'hello' ID ;
ID : [a-z]+ ;
WS : [ \t\n]+ -> skip ; // skip spaces, tabs, newlines

Then run ANTLR the tool on it:

$ cd /tmp
$ antlr4 Hello.g4
$ javac Hello*.java

Now test it:

$ grun Hello r -tree
hello parrt
^D
(r hello parrt)

(That ^D means EOF on unix; it's ^Z in Windows.) The -tree option prints
the parse tree in LISP notation.

BOOK SOURCE CODE

http://pragprog.com/titles/tpantlr2/source_code
