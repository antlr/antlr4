# Generated from /Users/lyga/Dropbox/code/python/antlr4-learn/test_grammar/T.g4 by ANTLR 4.5.3
# encoding: utf-8
from __future__ import print_function
from antlr4 import *
from io import StringIO


def serializedATN():
    with StringIO() as buf:
        buf.write(u"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2")
        buf.write(u"\5\17\b\1\4\2\t\2\4\3\t\3\4\4\t\4\3\2\3\2\3\3\3\3\3\4")
        buf.write(u"\3\4\2\2\5\3\3\5\4\7\5\3\2\2\16\2\3\3\2\2\2\2\5\3\2\2")
        buf.write(u"\2\2\7\3\2\2\2\3\t\3\2\2\2\5\13\3\2\2\2\7\r\3\2\2\2\t")
        buf.write(u"\n\7c\2\2\n\4\3\2\2\2\13\f\7d\2\2\f\6\3\2\2\2\r\16\7")
        buf.write(u"e\2\2\16\b\3\2\2\2\3\2\2")
        return buf.getvalue()


class TestLexer(Lexer):
    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [DFA(ds, i) for i, ds in enumerate(atn.decisionToState)]

    A = 1
    B = 2
    C = 3

    modeNames = [u"DEFAULT_MODE"]

    literalNames = [u"<INVALID>",
                    u"'a'", u"'b'", u"'c'"]

    symbolicNames = [u"<INVALID>",
                     u"A", u"B", u"C"]

    ruleNames = [u"A", u"B", u"C"]

    grammarFileName = u"T.g4"

    def __init__(self, input=None):
        super(TestLexer, self).__init__(input)
        self.checkVersion("4.7.2")
        self._interp = LexerATNSimulator(self, self.atn, self.decisionsToDFA, PredictionContextCache())
        self._actions = None
        self._predicates = None



def serializedATN2():
    with StringIO() as buf:
        buf.write(u"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2")
        buf.write(u"\t(\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t")
        buf.write(u"\7\4\b\t\b\3\2\6\2\23\n\2\r\2\16\2\24\3\3\6\3\30\n\3")
        buf.write(u"\r\3\16\3\31\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\6\b")
        buf.write(u"%\n\b\r\b\16\b&\2\2\t\3\3\5\4\7\5\t\6\13\7\r\b\17\t\3")
        buf.write(u"\2\2*\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2")
        buf.write(u"\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\3\22\3\2\2\2\5")
        buf.write(u"\27\3\2\2\2\7\33\3\2\2\2\t\35\3\2\2\2\13\37\3\2\2\2\r")
        buf.write(u"!\3\2\2\2\17$\3\2\2\2\21\23\4c|\2\22\21\3\2\2\2\23\24")
        buf.write(u"\3\2\2\2\24\22\3\2\2\2\24\25\3\2\2\2\25\4\3\2\2\2\26")
        buf.write(u"\30\4\62;\2\27\26\3\2\2\2\30\31\3\2\2\2\31\27\3\2\2\2")
        buf.write(u"\31\32\3\2\2\2\32\6\3\2\2\2\33\34\7=\2\2\34\b\3\2\2\2")
        buf.write(u"\35\36\7?\2\2\36\n\3\2\2\2\37 \7-\2\2 \f\3\2\2\2!\"\7")
        buf.write(u",\2\2\"\16\3\2\2\2#%\7\"\2\2$#\3\2\2\2%&\3\2\2\2&$\3")
        buf.write(u"\2\2\2&\'\3\2\2\2\'\20\3\2\2\2\6\2\24\31&\2")
        return buf.getvalue()


class TestLexer2(Lexer):

    atn = ATNDeserializer().deserialize(serializedATN2())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]


    ID = 1
    INT = 2
    SEMI = 3
    ASSIGN = 4
    PLUS = 5
    MULT = 6
    WS = 7

    modeNames = [ u"DEFAULT_MODE" ]

    literalNames = [ u"<INVALID>",
            u"';'", u"'='", u"'+'", u"'*'" ]

    symbolicNames = [ u"<INVALID>",
            u"ID", u"INT", u"SEMI", u"ASSIGN", u"PLUS", u"MULT", u"WS" ]

    ruleNames = [ u"ID", u"INT", u"SEMI", u"ASSIGN", u"PLUS", u"MULT", u"WS" ]

    grammarFileName = u"T2.g4"

    def __init__(self, input=None):
        super(TestLexer2, self).__init__(input)
        self.checkVersion("4.7.2")
        self._interp = LexerATNSimulator(self, self.atn, self.decisionsToDFA, PredictionContextCache())
        self._actions = None
        self._predicates = None
