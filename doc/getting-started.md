# Getting Started with ANTLR v4

Hi and welcome to the version 4 release of ANTLR! See [Why do we need ANTLR v4?](faq/general.md) and the [preface of the ANTLR v4 book](http://media.pragprog.com/titles/tpantlr2/preface.pdf).

## Getting started the easy way using antlr4-tools

To play around with ANTLR without having to worry about installing it and the Java needed to execute it, use [antlr4-tools](https://github.com/antlr/antlr4-tools). The only requirement is Python3, which is typically installed on all developer machines on all operating systems. (See below for Windows issue.)

```bash
$ pip install antlr4-tools
```

That command creates `antlr4` and `antlr4-parse` executables that, if necessary, will download and install Java 11 plus the latest ANTLR jar:

```bash
$ antlr4 
Downloading antlr4-4.11.1-complete.jar
ANTLR tool needs Java to run; install Java JRE 11 yes/no (default yes)? y
Installed Java in /Users/parrt/.jre/jdk-11.0.15+10-jre; remove that dir to uninstall
ANTLR Parser Generator  Version 4.11.1
 -o ___              specify output directory where all output is generated
 -lib ___            specify location of grammars, tokens files
...
```

Let's play with a simple grammar:

```
grammar Expr;		
prog:	expr EOF ;
expr:	expr ('*'|'/') expr
    |	expr ('+'|'-') expr
    |	INT
    |	'(' expr ')'
    ;
NEWLINE : [\r\n]+ -> skip;
INT     : [0-9]+ ;
```

### Windows-specific issues

On Windows, the `pip` command doesn't just work---you need to add the `...\local-packages\python38\scripts` dir to your `PATH`, which itself might require a fun reboot.  If you use WSL on Windows, then the pip install will also properly at the scripts directly (if you run from bash shell).


1. Go to the Microsoft Store
2. Search in Microsoft Store for Python
3. Select the newest version of Python (3.10).
4. Click the "Get" button. Store installs python and pip at "c:\Users...\AppData\Local\Microsoft\WindowsApps\python.exe" and "c:\Users...\AppData\Local\Microsoft\WindowsApps\pip.exe", respectively. And, it updates the search path immediately with the install.
5. Open a "cmd" terminal.
6. You can now type "python" and "pip", and "pip install antlr4-tools". 7. Unfortunately, it does not add that to the search path.
7. Update the search path to contain `c:\Users...\AppData\Local\Packages\PythonSoftwareFoundation.Python.3.10_qbz5n2kfra8p8\LocalCache\local-packages\Python310\Scripts`. You may need to install MSYS2, then do a `find /c/ -name antlr4.exe 2> /dev/null` and enter that path.
8. Or, you can set up an alias to antlr4.exe on that path.

The good news is that the ANTLR4 Python tool downloads the ANTLR jar in a standard location, and you don't need to do that manually. It's also possible to go in a browser, go to python.org, and download the python package. But, it's likely you will need to update the path for antlr4.exe as before.

### Try parsing with a sample grammar

To parse and get the parse tree in text form, use:

```bash
$ antlr4-parse Expr.g4 prog -tree
10+20*30
^D
(prog:1 (expr:2 (expr:3 10) + (expr:1 (expr:3 20) * (expr:3 30))) <EOF>)
```
(Note: `^D` means control-D and indicates "end of input" on Unix; use `^Z` on Windows.)

Here's how to get the tokens and trace through the parse:

```bash
$ antlr4-parse Expr.g4 prog -tokens -trace
10+20*30
^D
[@0,0:1='10',<INT>,1:0]
[@1,2:2='+',<'+'>,1:2]
[@2,3:4='20',<INT>,1:3]
[@3,5:5='*',<'*'>,1:5]
[@4,6:7='30',<INT>,1:6]
[@5,9:8='<EOF>',<EOF>,2:0]
enter   prog, LT(1)=10
enter   expr, LT(1)=10
consume [@0,0:1='10',<8>,1:0] rule expr
enter   expr, LT(1)=+
consume [@1,2:2='+',<3>,1:2] rule expr
enter   expr, LT(1)=20
consume [@2,3:4='20',<8>,1:3] rule expr
enter   expr, LT(1)=*
consume [@3,5:5='*',<1>,1:5] rule expr
enter   expr, LT(1)=30
consume [@4,6:7='30',<8>,1:6] rule expr
exit    expr, LT(1)=<EOF>
exit    expr, LT(1)=<EOF>
exit    expr, LT(1)=<EOF>
consume [@5,9:8='<EOF>',<-1>,2:0] rule prog
exit    prog, LT(1)=<EOF>
```

Here's how to get a visual tree view:

```bash
$ antlr4-parse Expr.g4 prog -gui
10+20*30
^D
```

The following will pop up in a Java-based GUI window:

<img src="https://github.com/antlr/antlr4-tools/blob/master/images/parse-tree.png?raw=true" width="300">

### Generating parser code

The previous section used a built-in ANTLR interpreter but typically you will ask ANTLR to generate code in the language used by your project (there are about 10 languages to choose from as of 4.11).  Here's how to generate Java code from a grammar:

```bash
$ antlr4 Expr.g4
$ ls Expr*.java
ExprBaseListener.java  ExprLexer.java         ExprListener.java      ExprParser.java
```

And, here's how to generate C++ code from the same grammar:

```bash
$ antlr4 -Dlanguage=Cpp Expr.g4
$ ls Expr*.cpp Expr*.h
ExprBaseListener.cpp  ExprLexer.cpp         ExprListener.cpp      ExprParser.cpp
ExprBaseListener.h    ExprLexer.h           ExprListener.h        ExprParser.h
```

## Installation

ANTLR is really two things: a tool written in Java that translates your grammar to a parser/lexer in Java (or other target language) and the runtime library needed by the generated parsers/lexers. Even if you are using the ANTLR Intellij plug-in or ANTLRWorks to run the ANTLR tool, the generated code will still need the runtime library. 

The first thing you should do is probably download and install a development tool plug-in. Even if you only use such tools for editing, they are great. Then, follow the instructions below to get the runtime environment available to your system to run generated parsers/lexers.  In what follows, I talk about antlr-4.11.1-complete.jar, which has the tool and the runtime and any other support libraries (e.g., ANTLR v4 is written in v3).

If you are going to integrate ANTLR into your existing build system using mvn, ant, or want to get ANTLR into your IDE such as eclipse or intellij, see [Integrating ANTLR into Development Systems](https://github.com/antlr/antlr4/blob/master/doc/IDEs.md).

### UNIX

0. Install Java (version 11 or higher)
1. Download
```
$ cd /usr/local/lib
$ curl -O https://www.antlr.org/download/antlr-4.11.1-complete.jar
```
Or just download in browser from website:
    [https://www.antlr.org/download.html](https://www.antlr.org/download.html)
and put it somewhere rational like `/usr/local/lib`.

if you are using lower version jdk, just download from [website download](https://github.com/antlr/website-antlr4/tree/gh-pages/download) for previous version, and antlr version before 4.11.1 support jdk 1.8  

2. Add `antlr-4.11.1-complete.jar` to your `CLASSPATH`:
```
$ export CLASSPATH=".:/usr/local/lib/antlr-4.11.1-complete.jar:$CLASSPATH"
```
It's also a good idea to put this in your `.bash_profile` or whatever your startup script is.

3. Create aliases for the ANTLR Tool, and `TestRig`.
```
$ alias antlr4='java -Xmx500M -cp "/usr/local/lib/antlr-4.11.1-complete.jar:$CLASSPATH" org.antlr.v4.Tool'
$ alias grun='java -Xmx500M -cp "/usr/local/lib/antlr-4.11.1-complete.jar:$CLASSPATH" org.antlr.v4.gui.TestRig'
```

### WINDOWS

(*Thanks to Graham Wideman*)

0. Install Java (version 1.7 or higher)
1. Download antlr-4.11.1-complete.jar (or whatever version) from [https://www.antlr.org/download.html](https://www.antlr.org/download.html)
Save to your directory for 3rd party Java libraries, say `C:\Javalib`
2. Add `antlr-4.11.1-complete.jar` to CLASSPATH, either:
  * Permanently: Using System Properties dialog > Environment variables > Create or append to `CLASSPATH` variable
  * Temporarily, at command line:
```
SET CLASSPATH=.;C:\Javalib\antlr-4.11.1-complete.jar;%CLASSPATH%
```
3. Create short convenient commands for the ANTLR Tool, and TestRig, using batch files or doskey commands:
  * Batch files (in directory in system PATH) antlr4.bat and grun.bat
```
java org.antlr.v4.Tool %*
```
```
@ECHO OFF
SET TEST_CURRENT_DIR=%CLASSPATH:.;=%
if "%TEST_CURRENT_DIR%" == "%CLASSPATH%" ( SET CLASSPATH=.;%CLASSPATH% )
@ECHO ON
java org.antlr.v4.gui.TestRig %*
```
  * Or, use doskey commands:
```
doskey antlr4=java org.antlr.v4.Tool $*
doskey grun =java org.antlr.v4.gui.TestRig $*
```

### Testing the installation

Either launch org.antlr.v4.Tool directly:

```
$ java org.antlr.v4.Tool
ANTLR Parser Generator Version 4.11.1
-o ___ specify output directory where all output is generated
-lib ___ specify location of .tokens files
...
```

or use -jar option on java:

```
$ java -jar /usr/local/lib/antlr-4.11.1-complete.jar
ANTLR Parser Generator Version 4.11.1
-o ___ specify output directory where all output is generated
-lib ___ specify location of .tokens files
...
```

## A First Example

In a temporary directory, put the following grammar inside file Hello.g4:
Hello.g4

```
// Define a grammar called Hello
grammar Hello;
r  : 'hello' ID ;         // match keyword hello followed by an identifier
ID : [a-z]+ ;             // match lower-case identifiers
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
```

Then run ANTLR the tool on it:

```
$ cd /tmp
$ antlr4 Hello.g4
$ javac Hello*.java
```

Now test it:

```
$ grun Hello r -tree
(Now enter something like the string below)
hello parrt
(now,do:)
^D
(The output:)
(r hello parrt)
(That ^D means EOF on unix; it's ^Z in Windows.) The -tree option prints the parse tree in LISP notation.
It's nicer to look at parse trees visually.
$ grun Hello r -gui
hello parrt
^D
```

That pops up a dialog box showing that rule `r` matched keyword `hello` followed by identifier `parrt`.

![](images/hello-parrt.png)

## Book source code

The book has lots and lots of examples that should be useful too. You can download them here for free:

[ANTLR reference book examples in Java](https://media.pragprog.com/titles/tpantlr2/code/tpantlr2-code.zip)<br>
[ANTLR reference book examples in C#](https://github.com/Philippe-Laval/tpantlr2)


[Language implementation patterns book examples in Java](https://media.pragprog.com/titles/tpdsl/code/tpdsl-code.zip)<br>
[Language implementation patterns book examples in C#](https://github.com/Philippe-Laval/tpdsl)

Also, there is a large collection of grammars for v4 at github:

[https://github.com/antlr/grammars-v4](https://github.com/antlr/grammars-v4)
