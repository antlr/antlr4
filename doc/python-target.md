# Python (2 and 3)

The examples from the ANTLR 4 book converted to Python are [here](https://github.com/jszheng/py3antlr4book).

There are 2 Python targets: `Python2` and `Python3`. This is because there is only limited compatibility between those 2 versions of the language. Please refer to the [Python documentation](https://wiki.python.org/moin/Python2orPython3) for full details.

How to create a Python lexer or parser?
This is pretty much the same as creating a Java lexer or parser, except you need to specify the language target, for example:

```
$ antlr4 -Dlanguage=Python2 MyGrammar.g4
```

or

```
$ antlr4 -Dlanguage=Python3 MyGrammar.g4
```

For a full list of antlr4 tool options, please visit the tool documentation page.

## Where can I get the runtime?

Once you've generated the lexer and/or parser code, you need to download the runtime. The Python runtimes are available from PyPI:

* https://pypi.python.org/pypi/antlr4-python2-runtime/
* https://pypi.python.org/pypi/antlr4-python3-runtime/

The runtimes are provided in the form of source code, so no additional installation is required.

We will not document here how to refer to the runtime from your Python project, since this would differ a lot depending on your project type and IDE. 

## How do I run the generated lexer and/or parser?

Let's suppose that your grammar is named, as above, "MyGrammar". Let's suppose this parser comprises a rule named "startRule". The tool will have generated for you the following files:

* MyGrammarLexer.py
* MyGrammarParser.py
* MyGrammarListener.py (if you have not activated the -no-listener option)
* MyGrammarVisitor.py (if you have activated the -visitor option)

(Developers used to Java/C# AntLR will notice that there is no base listener or visitor generated, this is because Python having no support for interfaces, the generated listener and visitor are fully fledged classes)

Now a fully functioning script might look like the following:
 
```python
import sys
from antlr4 import *
from MyGrammarLexer import MyGrammarLexer
from MyGrammarParser import MyGrammarParser
 
def main(argv):
    input_stream = FileStream(argv[1])
    lexer = MyGrammarLexer(input_stream)
    stream = CommonTokenStream(lexer)
    parser = MyGrammarParser(stream)
    tree = parser.startRule()
 
if __name__ == '__main__':
    main(sys.argv)
```

This program will work. But it won't be useful unless you do one of the following:

* you visit the parse tree using a custom listener
* you visit the parse tree using a custom visitor
* your grammar comprises production code (like ANTLR3)

(please note that production code is target specific, so you can't have multi target grammars that include production code, except for very limited use cases, see below)
 
## How do I create and run a custom listener?

Let's suppose your MyGrammar grammar comprises 2 rules: "key" and "value". The antlr4 tool will have generated the following listener: 

```python
class MyGrammarListener(ParseTreeListener):
    def enterKey(self, ctx):
        pass
    def exitKey(self, ctx):
        pass
    def enterValue(self, ctx):
        pass
    def exitValue(self, ctx):
        pass
```
 
In order to provide custom behavior, you might want to create the following class:
  
```python
class KeyPrinter(MyGrammarListener):     
    def exitKey(self, ctx):         
        print("Oh, a key!") 
```
 
In order to execute this listener, you would simply add the following lines to the above code:
 
```
       ...
       tree = parser.startRule() - only repeated here for reference
   printer = KeyPrinter()
   walker = ParseTreeWalker()
   walker.walk(printer, tree)
```
 
Further information can be found from the ANTLR 4 definitive guide.

The Python implementation of ANTLR is as close as possible to the Java one, so you shouldn't find it difficult to adapt the examples for Python.

## Target agnostic grammars

If your grammar is targeted to Python only, you may ignore the following. But if your goal is to get your Java parser to also run in Python, then you might find it useful.

1. Do not embed production code inside your grammar. This is not portable and will not be. Move all your code to listeners or visitors.
1. The only production code absolutely required to sit with the grammar should be semantic predicates, like:
```
ID {$text.equals("test")}?
```

Unfortunately, this is not portable, as Java and Python (and other target languages) have different syntaxes for all but the simplest language elements.  But you can work around it. The trick involves:

* deriving your parser from a parser you provide, such as BaseParser
* implementing utility methods, such as "isEqualText", in this BaseParser, in different files for each target language
* invoking your utility methods in the semantic predicate from the `$parser` object

Thanks to the above, you should be able to rewrite the above semantic predicate as follows:

File `MyGrammarParser.g4`:
```
options { superClass = MyGrammarBaseParser; }
...
ID {$parser.isEqualText($text,"test")}?
```

File `MyGrammarBaseParser.py`:
```python
from antlr4 import *

class MyGrammarBaseParser(Parser):

   def isEqualText(a, b):
      return a is b
```

File `MyGrammarBaseParser.java`:
```java
import org.antlr.v4.runtime.*;

public abstract class MyGrammarBaseParser extends Parser {

   public static boolean isEqualText(a, b) {
      return a.equals(b);
   }
}
```
