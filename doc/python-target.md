# Python 3

## Requirements

You will need to install the minimum requirements for Python and Pip.
See https://www.python.org/downloads/, version 3.6 or better, 
and https://www.geeksforgeeks.org/how-to-install-pip-on-windows/.

## A minimal example

An example of a parser with the Python3 target consists of the following files.
* An Antlr4 grammar, e.g., Expr.g4:
```
grammar Expr;
start_ : expr (';' expr)* EOF;
expr : atom | ('+' | '-') expr | expr '**' expr | expr ('*' | '/') expr | expr ('+' | '-') expr | '(' expr ')' | atom ;
atom : INT ;
INT : [0-9]+ ;
WS : [ \t\n\r]+ -> skip ;
```
* Driver.py:
The driver code consists at a minimum code to open a file, create a lexer, token stream,
and parser, then call the parser.
```python
import sys
from antlr4 import *
from ExprLexer import ExprLexer
from ExprParser import ExprParser
from VisitorInterp import VisitorInterp

def main(argv):
    input_stream = FileStream(argv[1])
    lexer = ExprLexer(input_stream)
    stream = CommonTokenStream(lexer)
    parser = ExprParser(stream)
    tree = parser.start_()
 
if __name__ == '__main__':
    main(sys.argv)
```
* requirements.txt:
This file contains a list of the
required packages for the program. Required
packages are downloaded by `pip`. The file
must include a reference to the Antlr Python3 runtime.
```
antlr4-python3-runtime==4.13.0
```
* A build script, e.g., build.sh:
You should provide a script that builds the program.
```
pip install -r requirements.txt
antlr4 -v 4.13.0 -Dlanguage=Python3 Expr.g4
```
_It is vital that the versions for the
Antlr tool used to generate the parser
and the Antlr Python3 runtime match.
E.g., 4.13.0. Using build files will help
eliminate common errors that happen._

_For a list of antlr4 tool options, please visit the [ANTLR Tool Command Line Options](https://github.com/antlr/antlr4/blob/master/doc/tool-options.md) documentation._
* Input, e.g., input.txt:
```
-(1 + 2)/3;
1;
2+3;
8*9
```
* Run script
You should provide a script to run your program.
```
python Driver.py input.txt
```

## Visitors

Antlr listeners and visitors are implementations that traverse a parse tree in unique ways. But both are used to implement
[static](https://en.wikipedia.org/wiki/Static_program_analysis) or [dynamic](https://en.wikipedia.org/wiki/Dynamic_program_analysis)
program analysis. It is essential to understand when to choose a listener versus a visitor.
For further information, see https://tomassetti.me/listeners-and-visitors/.
A visitor is the best choice when computing a synthesized attribute.
Alternatively, a listener is the best choice when computing both synthesized
and inherited attributes.

To implement a visitor, add the `-visitor` option to the `antlr4` command.
Add a class that inherits from the generated visitor
with code that implements the analysis.

For example, the following code implements an expression evaluator for the Expr.g4 grammar using a visitor.

* Driver.py:
```python
import sys
from antlr4 import *
from ExprLexer import ExprLexer
from ExprParser import ExprParser
from VisitorInterp import VisitorInterp

def main(argv):
    input_stream = FileStream(argv[1])
    lexer = ExprLexer(input_stream)
    stream = CommonTokenStream(lexer)
    parser = ExprParser(stream)
    tree = parser.start_()
    if parser.getNumberOfSyntaxErrors() > 0:
        print("syntax errors")
    else:
        vinterp = VisitorInterp()
        vinterp.visit(tree)
 
if __name__ == '__main__':
    main(sys.argv)
```
* VisitorInterp.py:
```python
import sys
from antlr4 import *
from ExprParser import ExprParser
from ExprVisitor import ExprVisitor

class VisitorInterp(ExprVisitor):
    def visitAtom(self, ctx:ExprParser.AtomContext):
        return int(ctx.getText())

    def visitExpr(self, ctx:ExprParser.ExprContext):
        if ctx.getChildCount() == 3:
            if ctx.getChild(0).getText() == "(":
                return self.visit(ctx.getChild(1))
            op = ctx.getChild(1).getText()
            v1 = self.visit(ctx.getChild(0))
            v2 = self.visit(ctx.getChild(2))
            if op == "+":
                return v1 + v2
            if op == "-":
                return v1 - v2
            if op == "*":
                return v1 * v2
            if op == "/":
                return v1 / v2
            return 0
        if ctx.getChildCount() == 2:
            opc = ctx.getChild(0).getText()
            if opc == "+":
                return self.visit(ctx.getChild(1))
            if opc == "-":
                return - self.visit(ctx.getChild(1))
            return 0
        if ctx.getChildCount() == 1:
            return self.visit(ctx.getChild(0))
        return 0

    def visitStart_(self, ctx:ExprParser.Start_Context):
        for i in range(0, ctx.getChildCount(), 2):
            print(self.visit(ctx.getChild(i)))
        return 0
```

## Listeners
 
Antlr listeners are an alternative to implement program analysis. It differs from a visitor in that
there are `enter` and `exit` methods called during the LR tranversal. You can use this tree walker
to implement both [inherited](https://en.wikipedia.org/wiki/Attribute_grammar#Inherited_attributes)
and [synthesized attribute](https://en.wikipedia.org/wiki/Attribute_grammar#Synthesized_attributes)
analysis.

To implement a listener, add the `-listener` option to the `antlr4` command.
Add a class that inherits from the generated listener
with code that implements the analysis.

The following example implements an expression evaluator using a listener.

* Driver.py:
```python
import sys
from antlr4 import *
from ExprLexer import ExprLexer
from ExprParser import ExprParser
from ListenerInterp import ListenerInterp

def main(argv):
    input_stream = FileStream(argv[1])
    lexer = ExprLexer(input_stream)
    stream = CommonTokenStream(lexer)
    parser = ExprParser(stream)
    tree = parser.start_()
    if parser.getNumberOfSyntaxErrors() > 0:
        print("syntax errors")
    else:
        linterp = ListenerInterp()
        walker = ParseTreeWalker()
        walker.walk(linterp, tree)
 
if __name__ == '__main__':
    main(sys.argv)
```
* ListenerInterp.py:
```python
import sys
from antlr4 import *
from ExprParser import ExprParser
from ExprListener import ExprListener

class ListenerInterp(ExprListener):
    def __init__(self):
        self.result = {}

    def exitAtom(self, ctx:ExprParser.AtomContext):
        self.result[ctx] = int(ctx.getText())

    def exitExpr(self, ctx:ExprParser.ExprContext):
        if ctx.getChildCount() == 3:
            if ctx.getChild(0).getText() == "(":
                self.result[ctx] = self.result[ctx.getChild(1)]
            else:
                opc = ctx.getChild(1).getText()
                v1 = self.result[ctx.getChild(0)]
                v2 = self.result[ctx.getChild(2)]
                if opc == "+":
                    self.result[ctx] = v1 + v2
                elif opc == "-":
                    self.result[ctx] = v1 - v2
                elif opc == "*":
                    self.result[ctx] = v1 * v2
                elif opc == "/":
                    self.result[ctx] = v1 / v2
                else:
                    ctx.result[ctx] = 0
        elif ctx.getChildCount() == 2:
            opc = ctx.getChild(0).getText()
            if opc == "+":
                v = self.result[ctx.getChild(1)]
                self.result[ctx] = v
            elif opc == "-":
                v = self.result[ctx.getChild(1)]
                self.result[ctx] = - v
        elif ctx.getChildCount() == 1:
            self.result[ctx] = self.result[ctx.getChild(0)]

    def exitStart_(self, ctx:ExprParser.Start_Context):
        for i in range(0, ctx.getChildCount(), 2):
            print(self.result[ctx.getChild(i)])
```

Further information can be found from the ANTLR 4 definitive guide.

## Examples

The examples from the ANTLR 4 book converted to Python are [here](https://github.com/jszheng/py3antlr4book).

There are many examples of grammars that target the Python3 target in the
[grammars-v4 Github repository](https://github.com/antlr/grammars-v4).
