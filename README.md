ANTLR v4

Terence Parr, parrt@cs.usfca.edu
ANTLR project lead and supreme dictator for life
University of San Francisco

INTRODUCTION

Hi and welcome to the Honey Badger 4.1 release of ANTLR!

INSTALLATION

UNIX

0. Install Java (version 1.6 or higher)

1. Download

   $ cd /usr/local/lib
   $ curl -O http://www.antlr4.org/download/antlr-4.1-complete.jar

   Or just download in browser using URL:

       http://www.antlr4.org/download/antlr-4.1-complete.jar

   and put it somewhere rational like /usr/local/lib.

2. Add antlr-4.1-complete.jar to your CLASSPATH:

   $ export CLASSPATH=".:/usr/local/lib/antlr-4.1-complete.jar:$CLASSPATH"

   Is also a good idea to put this in your .bash_profile or whatever your
   startup script is.

3. Create aliases for the ANTLR Tool, and TestRig.

   $ alias antlr4='java -jar /usr/local/lib/antlr-4.1-complete.jar'
   $ alias grun='java org.antlr.v4.runtime.misc.TestRig'

WINDOWS (Thanks to Graham Wideman)

0. Install Java (version 1.6 or higher)

1. Download http://antlr.org/download/antlr-4.1-complete.jar
   Save to your directory for 3rd party Java libraries, say C:\Javalib

2. Add antlr-4.1-complete.jar to CLASSPATH, either:

 * Permanently: Using System Properties dialog > Environment variables >
   Create or append to CLASSPATH variable

 * Temporarily, at command line:
   SET CLASSPATH=C:\Javalib\antlr-4.1-complete.jar;%CLASSPATH%

3. Create short convenient commands for the ANTLR Tool, and TestRig,
   using batch files or doskey commands:

 * Batch files (in directory in system PATH)

   antlr4.bat: java org.antlr.v4.Tool %*
   run.bat:   java org.antlr.v4.runtime.misc.TestRig %*

 * Or, use doskey commands:

   doskey antlr4=java org.antlr.v4.Tool $*
   doskey grun  =java org.antlr.v4.runtime.misc.TestRig $*

TESTING INSTALLATION

Either launch org.antlr.v4.Tool directly:

$ java org.antlr.v4.Tool
ANTLR Parser Generator Version 4.1
    -o ___              specify output directory where all output is generated
    -lib ___            specify location of .tokens files
...

or use -jar option on java:

$ java -jar /usr/local/lib/antlr-4.1-complete.jar
ANTLR Parser Generator Version 4.1
    -o ___              specify output directory where all output is generated
    -lib ___            specify location of .tokens files
...


EXAMPLE

In a temporary directory, put the following grammar inside file Hello.g4:

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

GRAMMARS

https://github.com/antlr/grammars-v4
