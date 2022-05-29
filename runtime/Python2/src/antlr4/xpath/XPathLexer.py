# Generated from XPathLexer.g4 by ANTLR 4.9.3
from antlr4 import *
from io import StringIO
import sys
if sys.version_info[1] > 5:
    from typing import TextIO
else:
    from typing.io import TextIO


def serializedATN():
    return [
        4,0,8,50,6,-1,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,2,5,7,5,2,
        6,7,6,2,7,7,7,1,0,1,0,1,0,1,1,1,1,1,2,1,2,1,3,1,3,1,4,1,4,5,4,29,
        8,4,10,4,12,4,32,9,4,1,4,1,4,1,5,1,5,3,5,38,8,5,1,6,1,6,1,7,1,7,
        5,7,44,8,7,10,7,12,7,47,9,7,1,7,1,7,1,45,0,8,1,3,3,4,5,5,7,6,9,7,
        11,0,13,0,15,8,1,0,2,5,0,48,57,95,95,183,183,768,879,8255,8256,13,
        0,65,90,97,122,192,214,216,246,248,767,880,893,895,8191,8204,8205,
        8304,8591,11264,12271,12289,55295,63744,64975,65008,65533,50,0,1,
        1,0,0,0,0,3,1,0,0,0,0,5,1,0,0,0,0,7,1,0,0,0,0,9,1,0,0,0,0,15,1,0,
        0,0,1,17,1,0,0,0,3,20,1,0,0,0,5,22,1,0,0,0,7,24,1,0,0,0,9,26,1,0,
        0,0,11,37,1,0,0,0,13,39,1,0,0,0,15,41,1,0,0,0,17,18,5,47,0,0,18,
        19,5,47,0,0,19,2,1,0,0,0,20,21,5,47,0,0,21,4,1,0,0,0,22,23,5,42,
        0,0,23,6,1,0,0,0,24,25,5,33,0,0,25,8,1,0,0,0,26,30,3,13,6,0,27,29,
        3,11,5,0,28,27,1,0,0,0,29,32,1,0,0,0,30,28,1,0,0,0,30,31,1,0,0,0,
        31,33,1,0,0,0,32,30,1,0,0,0,33,34,6,4,0,0,34,10,1,0,0,0,35,38,3,
        13,6,0,36,38,7,0,0,0,37,35,1,0,0,0,37,36,1,0,0,0,38,12,1,0,0,0,39,
        40,7,1,0,0,40,14,1,0,0,0,41,45,5,39,0,0,42,44,9,0,0,0,43,42,1,0,
        0,0,44,47,1,0,0,0,45,46,1,0,0,0,45,43,1,0,0,0,46,48,1,0,0,0,47,45,
        1,0,0,0,48,49,5,39,0,0,49,16,1,0,0,0,4,0,30,37,45,1,1,4,0
    ]

class XPathLexer(Lexer):

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]

    TOKEN_REF = 1
    RULE_REF = 2
    ANYWHERE = 3
    ROOT = 4
    WILDCARD = 5
    BANG = 6
    ID = 7
    STRING = 8

    channelNames = [ u"DEFAULT_TOKEN_CHANNEL", u"HIDDEN" ]

    modeNames = [ "DEFAULT_MODE" ]

    literalNames = [ "<INVALID>",
            "'//'", "'/'", "'*'", "'!'" ]

    symbolicNames = [ "<INVALID>",
            "TOKEN_REF", "RULE_REF", "ANYWHERE", "ROOT", "WILDCARD", "BANG", 
            "ID", "STRING" ]

    ruleNames = [ "ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar", 
                  "NameStartChar", "STRING" ]

    grammarFileName = "XPathLexer.g4"

    def __init__(self, input=None, output:TextIO = sys.stdout):
        super().__init__(input, output)
        self.checkVersion("4.9.3")
        self._interp = LexerATNSimulator(self, self.atn, self.decisionsToDFA, PredictionContextCache())
        self._actions = None
        self._predicates = None


    def action(self, localctx:RuleContext, ruleIndex:int, actionIndex:int):
        if self._actions is None:
            actions = dict()
            actions[4] = self.ID_action 
            self._actions = actions
        action = self._actions.get(ruleIndex, None)
        if action is not None:
            action(localctx, actionIndex)
        else:
            raise Exception("No registered action for:" + str(ruleIndex))


    def ID_action(self, localctx:RuleContext , actionIndex:int):
        if actionIndex == 0:

                            char = self.text[0]
                            if char.isupper():
                                self.type = XPathLexer.TOKEN_REF
                            else:
                                self.type = XPathLexer.RULE_REF
            				
     


