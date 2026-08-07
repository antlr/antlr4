# Generated from TestLexer.g4 by ANTLR 4.13.2
from antlr4 import *
from io import StringIO
import sys
if sys.version_info >= (3, 6):
    from typing import TextIO
else:
    from typing.io import TextIO


def serializedATN():
    return [
        4,0,3,13,6,-1,2,0,7,0,2,1,7,1,2,2,7,2,1,0,1,0,1,1,1,1,1,2,1,2,0,
        0,3,1,1,3,2,5,3,1,0,0,12,0,1,1,0,0,0,0,3,1,0,0,0,0,5,1,0,0,0,1,7,
        1,0,0,0,3,9,1,0,0,0,5,11,1,0,0,0,7,8,5,97,0,0,8,2,1,0,0,0,9,10,5,
        98,0,0,10,4,1,0,0,0,11,12,5,99,0,0,12,6,1,0,0,0,1,0,0
    ]

class TestLexer(Lexer):

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]

    A = 1
    B = 2
    C = 3

    channelNames = [ u"DEFAULT_TOKEN_CHANNEL", u"HIDDEN" ]

    modeNames = [ "DEFAULT_MODE" ]

    literalNames = [ "<INVALID>",
            "'a'", "'b'", "'c'" ]

    symbolicNames = [ "<INVALID>",
            "A", "B", "C" ]

    ruleNames = [ "A", "B", "C" ]

    grammarFileName = "TestLexer.g4"

    def __init__(self, input=None, output:TextIO = sys.stdout):
        super().__init__(input, output)
        self.checkVersion("4.13.2")
        self._interp = LexerATNSimulator(self, self.atn, self.decisionsToDFA, PredictionContextCache())
        self._actions = None
        self._predicates = None


