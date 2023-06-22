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
We suggest a file that contains the packages that the program needs that are loaded
with pip. The file must include a reference to the Antlr Python3 runtime.
```
antlr4-python3-runtime==4.13.0
```
* A build script, e.g., build.sh:
You should provide a script that builds the program.
```
pip install -r requirements.txt
antlr4 -v 4.13.0 -Dlanguage=Python3 Expr.g4
```
_It is important that the versions of Antlr used to generate the parser and lexer,
and the Antlr Python3 runtime match. E.g., 4.13.0. Using build files will help eliminate
common errors that happen._

_For a full list of antlr4 tool options, please visit the [ANTLR Tool Command Line Options](https://github.com/antlr/antlr4/blob/master/doc/tool-options.md) documentation._
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

Antlr listeners and visitors are implementations that traverse a parse tree in their own unique way, and are
used to implement [static](https://en.wikipedia.org/wiki/Static_program_analysis) or [dynamic](https://en.wikipedia.org/wiki/Dynamic_program_analysis)
program analysis. It's important to understand the similarities and differences of the Antlr listener and visitor code.
For further information, see https://tomassetti.me/listeners-and-visitors/. A visitor can traverse the nodes in the
parse tree in any arbirary order, by default in a bottom-up, left-to-right order. 

To implement a visitor, you will need to add the `-visitor` option when
generating the parser, add a class that subclasses the generated visitor, and code that implements the analysis.

As an example, the following code implements an expression evaluator for the Expr.g4 grammar using a visitor.

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
        print("atom")
        self.result[ctx] = int(ctx.getText())

    def exitExpr(self, ctx:ExprParser.ExprContext):
        print("expr")
        if ctx.getChildCount() == 3:
            if ctx.getChild(0).getText() == "(":
                self.result[ctx] = self.result[ctx.getChild(1)]
                return
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
            return
        elif ctx.getChildCount() == 2:
            opc = ctx.getChild(0).getText()
            if opc == "+":
                v = self.result[ctx.getChild(1)]
                self.result[ctx] = v
                return
            elif opc == "-":
                v = self.result[ctx.getChild(1)]
                self.result[ctx] = - v
                return
            else:
                return
        elif ctx.getChildCount() == 1:
            self.result[ctx] = self.result[ctx.getChild(0)]
            return
        else:
            return

    def exitStart_(self, ctx:ExprParser.Start_Context):
        for i in range(0, ctx.getChildCount(), 2):
            print(self.result[ctx.getChild(i)])
```

Further information can be found from the ANTLR 4 definitive guide.

## Examples

The examples from the ANTLR 4 book converted to Python are [here](https://github.com/jszheng/py3antlr4book).

There are many examples of grammars that target the Python3 target in the
[grammars-v4 Github repository](https://github.com/antlr/grammars-v4).
