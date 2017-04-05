#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#

import sys
sys.setrecursionlimit(4000)
import antlr4
from parser.cparser import CParser
from parser.clexer import CLexer
from datetime import datetime
import cProfile

class ErrorListener(antlr4.error.ErrorListener.ErrorListener):

    def __init__(self):
        super(ErrorListener, self).__init__()
        self.errored_out = False

    def syntaxError(self, recognizer, offendingSymbol, line, column, msg, e):
        self.errored_out = True


def sub():
    # Parse the input file
    input_stream = antlr4.FileStream("c.c")

    lexer = CLexer(input_stream)
    token_stream = antlr4.CommonTokenStream(lexer)

    parser = CParser(token_stream)


    errors = ErrorListener()
    parser.addErrorListener(errors)
    tree = parser.compilationUnit()

def main():
    before = datetime.now()
    sub()
    after = datetime.now()
    print(str(after-before))
    # before = after
    # sub()
    # after = datetime.now()
    # print(str(after-before))

if __name__ == '__main__':
    cProfile.run("main()", sort='tottime')