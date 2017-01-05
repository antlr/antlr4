# ANTLR Tool Command Line Options

If you invoke the ANTLR tool without command line arguments, you’ll get a help message:

```bash
$ antlr4
ANTLR Parser Generator  Version 4.5
 -o ___              specify output directory where all output is generated
 -lib ___            specify location of grammars, tokens files
 -atn                generate rule augmented transition network diagrams
 -encoding ___       specify grammar file encoding; e.g., euc-jp
 -message-format ___ specify output style for messages in antlr, gnu, vs2005
 -long-messages      show exception details when available for errors and warnings
 -listener           generate parse tree listener (default)
 -no-listener        don't generate parse tree listener
 -visitor            generate parse tree visitor
 -no-visitor         don't generate parse tree visitor (default)
 -package ___        specify a package/namespace for the generated code
 -depend             generate file dependencies
 -D<option>=value    set/override a grammar-level option
 -Werror             treat warnings as errors
 -XdbgST             launch StringTemplate visualizer on generated code
 -XdbgSTWait         wait for STViz to close before continuing
 -Xforce-atn         use the ATN simulator for all predictions
 -Xlog               dump lots of logging info to antlr-timestamp.log
```

Here are more details on the options:

## `-o outdir`

ANTLR generates output files in the current directory by default. This option specifies the output directory where ANTLR should generate parsers, listeners, visitors, and tokens files.
 	
```bash
$ antlr4 -o /tmp T.g4
$ ls /tmp/T*
/tmp/T.tokens /tmp/TListener.java
/tmp/TBaseListener.java /tmp/TParser.java
```

## `-lib libdir`

When looking for tokens files and imported grammars, ANTLR normally looks in the current directory. This option specifies which directory to look in instead. It is only used for resolving grammar references for the import statement and the tokenVocab option. The path to the primary grammar must always be fully specified.
 	
$ cat /tmp/B.g4
 	
parser grammar B;
 	
x : ID ;
 	
$ cat A.g4
 	
grammar A;
 	
import B;
 	
s : x ;
 	
ID : [a-z]+ ;
 	
$ antlr4 -lib /tmp A.g4

## `-atn`

Generate DOT graph files that represent the internal ATN (augmented transition network) data structures that ANTLR uses to represent grammars. The files come out as Grammar.rule .dot. If the grammar is a combined grammar, the lexer rules are named Grammar Lexer.rule .dot.
 	
$ cat A.g4
 	
grammar A;
 	
s : b ;
 	
b : ID ;
 	
ID : [a-z]+ ;
 	
$ antlr4 -atn A.g4
 	
$ ls *.dot
 	
A.b.dot A.s.dot ALexer.ID.dot

## `-encoding encodingname`

By default ANTLR loads grammar files using the UTF-8 encoding, which is a very common character file encoding that degenerates to ASCII for characters that fit in one byte. There are many character file encodings from around the world. If that grammar file is not the default encoding for your locale, you need this option so that ANTLR can properly interpret grammar files. This does not affect the input to the generated parsers, just the encoding of the grammars themselves.

## `-message-format format`

ANTLR generates warning and error messages using templates from directory tool/resources/org/antlr/v4/tool/templates/messages/formats. By default, ANTLR uses the antlr.stg (StringTemplate group) file. You can change this to gnu or vs2005 to have ANTLR generate messages appropriate for Emacs or Visual Studio. To make your own called X, create resource org/antlr/v4/tool/templates/messages/formats/ X and place it in the CLASSPATH.

## `-listener`

This option tells ANTLR to generate a parse tree listener and is the default.

## `-no-listener`

This option tells ANTLR not to generate a parse tree listener.

## `-visitor`

ANTLR does not generate parse tree visitors by default. This option turns that feature on. ANTLR can generate both parse tree listeners and visitors; this option and -listener aren’t mutually exclusive.

## `-no-visitor`

Tell ANTLR not to generate a parse tree visitor; this is the default.

## `-package`

Use this option to specify a package or namespace for ANTLR-generated files. Alternatively, you can add a @header {...} action but that ties the grammar to a specific language. If you use this option and @header, make sure that the header action does not contain a package specification otherwise the generated code will have two of them.

## `-depend`

Instead of generating a parser and/or lexer, generate a list of file dependencies, one per line. The output shows what each grammar depends on and what it generates. This is useful for build tools that need to know ANTLR grammar dependencies. Here’s an example:
 	
```bash
$ antlr4 -depend T.g	
T.g: A.tokens
TParser.java : T.g
T.tokens : T.g
TLexer.java : T.g
TListener.java : T.g
TBaseListener.java : T.g
```

If you use -lib libdir with -depend and grammar option tokenVocab=A, then the dependencies include the library path as well: T.g: libdir/A.tokens. The output is also sensitive to the -o outdir option: outdir/TParser.java : T.g.

## `-D<option>=value`

Use this option to override or set a grammar-level option in the specified grammar or grammars. This option is useful for generating parsers in different languages without altering the grammar itself. (I expect to have other targets in the near future.)
 	
```bash
$ antlr4 -Dlanguage=Java T.g4 # default
$ antlr4 -Dlanguage=C T.g4
error(31): ANTLR cannot generate C code as of version 4.0b3
```

## `-Werror`

As part of a large build, ANTLR warning messages could go unnoticed. Turn on this option to have warnings treated as errors, causing the ANTLR tool to report failure back to the invoking commandline shell.
There are also some extended options that are useful mainly for debugging ANTLR itself:

## `-Xsave-lexer`

ANTLR generates both a parser and a lexer from a combined grammar. To create the lexer, ANTLR extracts a lexer grammar from the combined grammar. Sometimes it’s useful to see what that looks like if it’s not clear what token rules ANTLR is creating. This does not affect the generated parsers or lexers.

## `-XdbgST`

For those building a code generation target, this option brings up a window showing the generated code and the templates used to generate that code. It invokes the StringTemplate inspector window.

## `-Xforce-atn`

ANTLR normally builds traditional “switch on token type” decisions where possible (one token of lookahead is sufficient to distinguish between all alternatives in a decision). To force even these simple decisions into the adaptive LL(*) mechanism, use this option.

## `-Xlog`

This option creates a log file containing lots of information messages from ANTLR as it processes your grammar. If you would like to see how ANTLR translates your left-recursive rules, turn on this option and look in the resulting log file.
 	
```bash
$ antlr4 -Xlog T.g4 	
wrote ./antlr-2012-09-06-17.56.19.log
```
