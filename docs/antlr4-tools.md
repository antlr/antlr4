# antlr4-tools

Tools to run antlr4 w/o needing to install java or antlr4! The only requirement is Python3, which is typically installed on all developer machines on all operating systems. 
 
## Install

```bash
$ pip install antlr4-tools
```

That creates `antlr4` and `antlr4-parse` executables. On Windows, of course, this doesn't just work. You need to add the `...\local-packages\python38\scripts` dir to your `PATH`, which itself might require a fun reboot or perhaps reinstall of the OS. haha.

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

## First run will install Java and ANTLR

If needed, `antlr4` will download and install Java 11 and the latest ANTLR jar:

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

To override the version of ANTLR jar used, you can pass a `-v <version>` argument or set `ANTLR4_TOOLS_ANTLR_VERSION` environment variable:

```bash
$ antlr4 -v 4.9.3
ANTLR Parser Generator  Version 4.9.3
 -o ___              specify output directory where all output is generated
 -lib ___            specify location of grammars, tokens files
...
$ ANTLR4_TOOLS_ANTLR_VERSION=4.10.1 antlr4
ANTLR Parser Generator  Version 4.10.1
 -o ___              specify output directory where all output is generated
 -lib ___            specify location of grammars, tokens files
...
```

## Running ANTLR tool on grammars

The `antlr4` command forwards all arguments (besides `-v` mentioned above) to the actual ANTLR tool command:

```bash
$ antlr4 JSON.g4 
$ ls JSON*.java
JSONBaseListener.java  JSONLexer.java         JSONListener.java      JSONParser.java
$ antlr4 -Dlanguage=Python3 -visitor JSON.g4
$ ls JSON*.py
JSONLexer.py     JSONListener.py  JSONParser.py    JSONVisitor.py
```

## Parsing using interpreter

The `antlr4-parse` command requires ANTLR 4.11 and above (but any version of ANTLR works for the plain `antlr4` command).  It accepts the same `-v` argument or environment variable to override the ANTLR jar version used.  (Note: `^D` means control-D and indicates "end of input" on Unix but use `^Z` on Windows.)

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

To parse and get the parse tree in text form, use:

```bash
$ antlr4-parse Expr.g4 prog -tree
10+20*30
^D
(prog:1 (expr:2 (expr:3 10) + (expr:1 (expr:3 20) * (expr:3 30))) <EOF>)
```

Here's how to get the tokens and trace through the parse:

```bash
$ antlr4-parse Expr.g4 prog -tokens -trace
10+20*30
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
```

The following will pop up in a Java-based GUI window:

<img src="images/parse-tree.png" width="300">

On real grammars, it can be useful to get decision-making profiling info:

```bash
$ antlr4-parse JavaLexer.g4 JavaParser.g4 compilationUnit -profile dump.csv T.java
$ open /tmp/dump.csv 
$ head -5 /tmp/dump.csv 
Rule,Invocations,Time (ms),Total k,Max k,Ambiguities,DFA cache miss
compilationUnit:0,1,0.164791,1,1,0,1
compilationUnit:1,42,1.106583,42,1,0,2
compilationUnit:2,2,1.73675,2,1,0,2
compilationUnit:3,1,3.969,1,1,0,1
```
