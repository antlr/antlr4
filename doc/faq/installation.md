# Installation

Please read carefully: [Getting Started with ANTLR v4](https://raw.githubusercontent.com/antlr/antlr4/master/doc/getting-started.md).

## Why can't ANTLR (grun) find my lexer or parser?

If you see "Can't load Hello as lexer or parser", it's because you don't have '.' (current directory) in your CLASSPATH.

```bash
$ alias antlr4='java -jar /usr/local/lib/antlr-4.2.2-complete.jar'
$ alias grun='java org.antlr.v4.runtime.misc.TestRig'
$ export CLASSPATH="/usr/local/lib/antlr-4.2.2-complete.jar"
$ antlr4 Hello.g4
$ javac Hello*.java
$ grun Hello r -tree
Can't load Hello as lexer or parser
$
```

For mac/linux, use:

```bash
export CLASSPATH=".:/usr/local/lib/antlr-4.2.2-complete.jar:$CLASSPATH"
```

or for Windows:

```
SET CLASSPATH=.;C:\Javalib\antlr4-complete.jar;%CLASSPATH%
```

**See the dot at the beginning?** It's critical.

## Why can't I run the ANTLR tool?

If you get a no class definition found error, you are missing the ANTLR jar in your `CLASSPATH` (or you might only have the runtime jar):

```bash
/tmp $ java org.antlr.v4.Tool Hello.g4
Exception in thread "main" java.lang.NoClassDefFoundError: org/antlr/v4/Tool
Caused by: java.lang.ClassNotFoundException: org.antlr.v4.Tool
    at java.net.URLClassLoader$1.run(URLClassLoader.java:202)
    at java.security.AccessController.doPrivileged(Native Method)
    at java.net.URLClassLoader.findClass(URLClassLoader.java:190)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:306)
    at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:301)
    at java.lang.ClassLoader.loadClass(ClassLoader.java:247)
```

## Why doesn't my parser compile?

If you see these kinds of errors, it's because you don't have the runtime or complete ANTLR library in your CLASSPATH.

```bash
/tmp $ javac Hello*.java
HelloBaseListener.java:3: package org.antlr.v4.runtime does not exist
import org.antlr.v4.runtime.ParserRuleContext;
                           ^
...
```
