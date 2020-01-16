# Generated from expr/Expr.g4 by ANTLR 4.7.2
from antlr4 import *
from io import StringIO
from typing.io import TextIO
import sys


def serializedATN():
    with StringIO() as buf:
        buf.write("\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23")
        buf.write("^\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7")
        buf.write("\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16")
        buf.write("\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22\3\2\3\2")
        buf.write("\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3")
        buf.write("\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16")
        buf.write("\3\16\3\16\3\16\3\16\3\16\3\17\6\17H\n\17\r\17\16\17I")
        buf.write("\3\20\6\20M\n\20\r\20\16\20N\3\21\5\21R\n\21\3\21\3\21")
        buf.write("\3\21\3\21\3\22\6\22Y\n\22\r\22\16\22Z\3\22\3\22\2\2\23")
        buf.write("\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31")
        buf.write("\16\33\17\35\20\37\21!\22#\23\3\2\5\4\2C\\c|\3\2\62;\4")
        buf.write("\2\13\13\"\"\2a\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2")
        buf.write("\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21")
        buf.write("\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3")
        buf.write("\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2")
        buf.write("\2\2#\3\2\2\2\3%\3\2\2\2\5)\3\2\2\2\7+\3\2\2\2\t-\3\2")
        buf.write("\2\2\13/\3\2\2\2\r\61\3\2\2\2\17\63\3\2\2\2\21\65\3\2")
        buf.write("\2\2\23\67\3\2\2\2\259\3\2\2\2\27;\3\2\2\2\31=\3\2\2\2")
        buf.write("\33?\3\2\2\2\35G\3\2\2\2\37L\3\2\2\2!Q\3\2\2\2#X\3\2\2")
        buf.write("\2%&\7f\2\2&\'\7g\2\2\'(\7h\2\2(\4\3\2\2\2)*\7*\2\2*\6")
        buf.write("\3\2\2\2+,\7.\2\2,\b\3\2\2\2-.\7+\2\2.\n\3\2\2\2/\60\7")
        buf.write("}\2\2\60\f\3\2\2\2\61\62\7\177\2\2\62\16\3\2\2\2\63\64")
        buf.write("\7=\2\2\64\20\3\2\2\2\65\66\7?\2\2\66\22\3\2\2\2\678\7")
        buf.write(",\2\28\24\3\2\2\29:\7\61\2\2:\26\3\2\2\2;<\7-\2\2<\30")
        buf.write("\3\2\2\2=>\7/\2\2>\32\3\2\2\2?@\7t\2\2@A\7g\2\2AB\7v\2")
        buf.write("\2BC\7w\2\2CD\7t\2\2DE\7p\2\2E\34\3\2\2\2FH\t\2\2\2GF")
        buf.write("\3\2\2\2HI\3\2\2\2IG\3\2\2\2IJ\3\2\2\2J\36\3\2\2\2KM\t")
        buf.write("\3\2\2LK\3\2\2\2MN\3\2\2\2NL\3\2\2\2NO\3\2\2\2O \3\2\2")
        buf.write("\2PR\7\17\2\2QP\3\2\2\2QR\3\2\2\2RS\3\2\2\2ST\7\f\2\2")
        buf.write("TU\3\2\2\2UV\b\21\2\2V\"\3\2\2\2WY\t\4\2\2XW\3\2\2\2Y")
        buf.write("Z\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2\\]\b\22\2\2]")
        buf.write("$\3\2\2\2\7\2INQZ\3\b\2\2")
        return buf.getvalue()


class ExprLexer(Lexer):

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]

    T__0 = 1
    T__1 = 2
    T__2 = 3
    T__3 = 4
    T__4 = 5
    T__5 = 6
    T__6 = 7
    T__7 = 8
    MUL = 9
    DIV = 10
    ADD = 11
    SUB = 12
    RETURN = 13
    ID = 14
    INT = 15
    NEWLINE = 16
    WS = 17

    channelNames = [ u"DEFAULT_TOKEN_CHANNEL", u"HIDDEN" ]

    modeNames = [ "DEFAULT_MODE" ]

    literalNames = [ "<INVALID>",
            "'def'", "'('", "','", "')'", "'{'", "'}'", "';'", "'='", "'*'", 
            "'/'", "'+'", "'-'", "'return'" ]

    symbolicNames = [ "<INVALID>",
            "MUL", "DIV", "ADD", "SUB", "RETURN", "ID", "INT", "NEWLINE", 
            "WS" ]

    ruleNames = [ "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", 
                  "T__7", "MUL", "DIV", "ADD", "SUB", "RETURN", "ID", "INT", 
                  "NEWLINE", "WS" ]

    grammarFileName = "Expr.g4"

    def __init__(self, input=None, output:TextIO = sys.stdout):
        super().__init__(input, output)
        self.checkVersion("4.8")
        self._interp = LexerATNSimulator(self, self.atn, self.decisionsToDFA, PredictionContextCache())
        self._actions = None
        self._predicates = None


