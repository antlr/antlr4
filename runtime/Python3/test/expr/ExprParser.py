# Generated from expr/Expr.g4 by ANTLR 4.7.2
# encoding: utf-8
from antlr4 import *
from io import StringIO
from typing.io import TextIO
import sys

def serializedATN():
    with StringIO() as buf:
        buf.write("\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\23")
        buf.write("S\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b")
        buf.write("\t\b\3\2\6\2\22\n\2\r\2\16\2\23\3\3\3\3\3\3\3\3\3\3\3")
        buf.write("\3\7\3\34\n\3\f\3\16\3\37\13\3\3\3\3\3\3\3\3\4\3\4\6\4")
        buf.write("&\n\4\r\4\16\4\'\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3")
        buf.write("\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6;\n\6\3\7\3\7\3\7\3")
        buf.write("\7\3\7\3\7\3\7\3\7\3\7\7\7F\n\7\f\7\16\7I\13\7\3\b\3\b")
        buf.write("\3\b\3\b\3\b\3\b\5\bQ\n\b\3\b\2\3\f\t\2\4\6\b\n\f\16\2")
        buf.write("\4\3\2\13\f\3\2\r\16\2U\2\21\3\2\2\2\4\25\3\2\2\2\6#\3")
        buf.write("\2\2\2\b+\3\2\2\2\n:\3\2\2\2\f<\3\2\2\2\16P\3\2\2\2\20")
        buf.write("\22\5\4\3\2\21\20\3\2\2\2\22\23\3\2\2\2\23\21\3\2\2\2")
        buf.write("\23\24\3\2\2\2\24\3\3\2\2\2\25\26\7\3\2\2\26\27\7\20\2")
        buf.write("\2\27\30\7\4\2\2\30\35\5\b\5\2\31\32\7\5\2\2\32\34\5\b")
        buf.write("\5\2\33\31\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2\35\36\3")
        buf.write("\2\2\2\36 \3\2\2\2\37\35\3\2\2\2 !\7\6\2\2!\"\5\6\4\2")
        buf.write("\"\5\3\2\2\2#%\7\7\2\2$&\5\n\6\2%$\3\2\2\2&\'\3\2\2\2")
        buf.write("\'%\3\2\2\2\'(\3\2\2\2()\3\2\2\2)*\7\b\2\2*\7\3\2\2\2")
        buf.write("+,\7\20\2\2,\t\3\2\2\2-.\5\f\7\2./\7\t\2\2/;\3\2\2\2\60")
        buf.write("\61\7\20\2\2\61\62\7\n\2\2\62\63\5\f\7\2\63\64\7\t\2\2")
        buf.write("\64;\3\2\2\2\65\66\7\17\2\2\66\67\5\f\7\2\678\7\t\2\2")
        buf.write("8;\3\2\2\29;\7\t\2\2:-\3\2\2\2:\60\3\2\2\2:\65\3\2\2\2")
        buf.write(":9\3\2\2\2;\13\3\2\2\2<=\b\7\1\2=>\5\16\b\2>G\3\2\2\2")
        buf.write("?@\f\5\2\2@A\t\2\2\2AF\5\f\7\6BC\f\4\2\2CD\t\3\2\2DF\5")
        buf.write("\f\7\5E?\3\2\2\2EB\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2")
        buf.write("\2H\r\3\2\2\2IG\3\2\2\2JQ\7\21\2\2KQ\7\20\2\2LM\7\4\2")
        buf.write("\2MN\5\f\7\2NO\7\6\2\2OQ\3\2\2\2PJ\3\2\2\2PK\3\2\2\2P")
        buf.write("L\3\2\2\2Q\17\3\2\2\2\t\23\35\':EGP")
        return buf.getvalue()


class ExprParser ( Parser ):

    grammarFileName = "Expr.g4"

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]

    sharedContextCache = PredictionContextCache()

    literalNames = [ "<INVALID>", "'def'", "'('", "','", "')'", "'{'", "'}'", 
                     "';'", "'='", "'*'", "'/'", "'+'", "'-'", "'return'" ]

    symbolicNames = [ "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "<INVALID>", "<INVALID>", "<INVALID>", 
                      "<INVALID>", "MUL", "DIV", "ADD", "SUB", "RETURN", 
                      "ID", "INT", "NEWLINE", "WS" ]

    RULE_prog = 0
    RULE_func = 1
    RULE_body = 2
    RULE_arg = 3
    RULE_stat = 4
    RULE_expr = 5
    RULE_primary = 6

    ruleNames =  [ "prog", "func", "body", "arg", "stat", "expr", "primary" ]

    EOF = Token.EOF
    T__0=1
    T__1=2
    T__2=3
    T__3=4
    T__4=5
    T__5=6
    T__6=7
    T__7=8
    MUL=9
    DIV=10
    ADD=11
    SUB=12
    RETURN=13
    ID=14
    INT=15
    NEWLINE=16
    WS=17

    def __init__(self, input:TokenStream, output:TextIO = sys.stdout):
        super().__init__(input, output)
        self.checkVersion("4.8")
        self._interp = ParserATNSimulator(self, self.atn, self.decisionsToDFA, self.sharedContextCache)
        self._predicates = None



    class ProgContext(ParserRuleContext):

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def func(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(ExprParser.FuncContext)
            else:
                return self.getTypedRuleContext(ExprParser.FuncContext,i)


        def getRuleIndex(self):
            return ExprParser.RULE_prog




    def prog(self):

        localctx = ExprParser.ProgContext(self, self._ctx, self.state)
        self.enterRule(localctx, 0, self.RULE_prog)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 15 
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while True:
                self.state = 14
                self.func()
                self.state = 17 
                self._errHandler.sync(self)
                _la = self._input.LA(1)
                if not (_la==ExprParser.T__0):
                    break

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx

    class FuncContext(ParserRuleContext):

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def ID(self):
            return self.getToken(ExprParser.ID, 0)

        def arg(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(ExprParser.ArgContext)
            else:
                return self.getTypedRuleContext(ExprParser.ArgContext,i)


        def body(self):
            return self.getTypedRuleContext(ExprParser.BodyContext,0)


        def getRuleIndex(self):
            return ExprParser.RULE_func




    def func(self):

        localctx = ExprParser.FuncContext(self, self._ctx, self.state)
        self.enterRule(localctx, 2, self.RULE_func)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 19
            self.match(ExprParser.T__0)
            self.state = 20
            self.match(ExprParser.ID)
            self.state = 21
            self.match(ExprParser.T__1)
            self.state = 22
            self.arg()
            self.state = 27
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while _la==ExprParser.T__2:
                self.state = 23
                self.match(ExprParser.T__2)
                self.state = 24
                self.arg()
                self.state = 29
                self._errHandler.sync(self)
                _la = self._input.LA(1)

            self.state = 30
            self.match(ExprParser.T__3)
            self.state = 31
            self.body()
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx

    class BodyContext(ParserRuleContext):

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def stat(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(ExprParser.StatContext)
            else:
                return self.getTypedRuleContext(ExprParser.StatContext,i)


        def getRuleIndex(self):
            return ExprParser.RULE_body




    def body(self):

        localctx = ExprParser.BodyContext(self, self._ctx, self.state)
        self.enterRule(localctx, 4, self.RULE_body)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 33
            self.match(ExprParser.T__4)
            self.state = 35 
            self._errHandler.sync(self)
            _la = self._input.LA(1)
            while True:
                self.state = 34
                self.stat()
                self.state = 37 
                self._errHandler.sync(self)
                _la = self._input.LA(1)
                if not ((((_la) & ~0x3f) == 0 and ((1 << _la) & ((1 << ExprParser.T__1) | (1 << ExprParser.T__6) | (1 << ExprParser.RETURN) | (1 << ExprParser.ID) | (1 << ExprParser.INT))) != 0)):
                    break

            self.state = 39
            self.match(ExprParser.T__5)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx

    class ArgContext(ParserRuleContext):

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser

        def ID(self):
            return self.getToken(ExprParser.ID, 0)

        def getRuleIndex(self):
            return ExprParser.RULE_arg




    def arg(self):

        localctx = ExprParser.ArgContext(self, self._ctx, self.state)
        self.enterRule(localctx, 6, self.RULE_arg)
        try:
            self.enterOuterAlt(localctx, 1)
            self.state = 41
            self.match(ExprParser.ID)
        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx

    class StatContext(ParserRuleContext):

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser


        def getRuleIndex(self):
            return ExprParser.RULE_stat

     
        def copyFrom(self, ctx:ParserRuleContext):
            super().copyFrom(ctx)



    class RetContext(StatContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.StatContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def RETURN(self):
            return self.getToken(ExprParser.RETURN, 0)
        def expr(self):
            return self.getTypedRuleContext(ExprParser.ExprContext,0)



    class BlankContext(StatContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.StatContext
            super().__init__(parser)
            self.copyFrom(ctx)



    class PrintExprContext(StatContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.StatContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def expr(self):
            return self.getTypedRuleContext(ExprParser.ExprContext,0)



    class AssignContext(StatContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.StatContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def ID(self):
            return self.getToken(ExprParser.ID, 0)
        def expr(self):
            return self.getTypedRuleContext(ExprParser.ExprContext,0)




    def stat(self):

        localctx = ExprParser.StatContext(self, self._ctx, self.state)
        self.enterRule(localctx, 8, self.RULE_stat)
        try:
            self.state = 56
            self._errHandler.sync(self)
            la_ = self._interp.adaptivePredict(self._input,3,self._ctx)
            if la_ == 1:
                localctx = ExprParser.PrintExprContext(self, localctx)
                self.enterOuterAlt(localctx, 1)
                self.state = 43
                self.expr(0)
                self.state = 44
                self.match(ExprParser.T__6)
                pass

            elif la_ == 2:
                localctx = ExprParser.AssignContext(self, localctx)
                self.enterOuterAlt(localctx, 2)
                self.state = 46
                self.match(ExprParser.ID)
                self.state = 47
                self.match(ExprParser.T__7)
                self.state = 48
                self.expr(0)
                self.state = 49
                self.match(ExprParser.T__6)
                pass

            elif la_ == 3:
                localctx = ExprParser.RetContext(self, localctx)
                self.enterOuterAlt(localctx, 3)
                self.state = 51
                self.match(ExprParser.RETURN)
                self.state = 52
                self.expr(0)
                self.state = 53
                self.match(ExprParser.T__6)
                pass

            elif la_ == 4:
                localctx = ExprParser.BlankContext(self, localctx)
                self.enterOuterAlt(localctx, 4)
                self.state = 55
                self.match(ExprParser.T__6)
                pass


        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx

    class ExprContext(ParserRuleContext):

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser


        def getRuleIndex(self):
            return ExprParser.RULE_expr

     
        def copyFrom(self, ctx:ParserRuleContext):
            super().copyFrom(ctx)


    class PrimContext(ExprContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.ExprContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def primary(self):
            return self.getTypedRuleContext(ExprParser.PrimaryContext,0)



    class MulDivContext(ExprContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.ExprContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def expr(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(ExprParser.ExprContext)
            else:
                return self.getTypedRuleContext(ExprParser.ExprContext,i)

        def MUL(self):
            return self.getToken(ExprParser.MUL, 0)
        def DIV(self):
            return self.getToken(ExprParser.DIV, 0)


    class AddSubContext(ExprContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.ExprContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def expr(self, i:int=None):
            if i is None:
                return self.getTypedRuleContexts(ExprParser.ExprContext)
            else:
                return self.getTypedRuleContext(ExprParser.ExprContext,i)

        def ADD(self):
            return self.getToken(ExprParser.ADD, 0)
        def SUB(self):
            return self.getToken(ExprParser.SUB, 0)



    def expr(self, _p:int=0):
        _parentctx = self._ctx
        _parentState = self.state
        localctx = ExprParser.ExprContext(self, self._ctx, _parentState)
        _prevctx = localctx
        _startState = 10
        self.enterRecursionRule(localctx, 10, self.RULE_expr, _p)
        self._la = 0 # Token type
        try:
            self.enterOuterAlt(localctx, 1)
            localctx = ExprParser.PrimContext(self, localctx)
            self._ctx = localctx
            _prevctx = localctx

            self.state = 59
            self.primary()
            self._ctx.stop = self._input.LT(-1)
            self.state = 69
            self._errHandler.sync(self)
            _alt = self._interp.adaptivePredict(self._input,5,self._ctx)
            while _alt!=2 and _alt!=ATN.INVALID_ALT_NUMBER:
                if _alt==1:
                    if self._parseListeners is not None:
                        self.triggerExitRuleEvent()
                    _prevctx = localctx
                    self.state = 67
                    self._errHandler.sync(self)
                    la_ = self._interp.adaptivePredict(self._input,4,self._ctx)
                    if la_ == 1:
                        localctx = ExprParser.MulDivContext(self, ExprParser.ExprContext(self, _parentctx, _parentState))
                        self.pushNewRecursionContext(localctx, _startState, self.RULE_expr)
                        self.state = 61
                        if not self.precpred(self._ctx, 3):
                            from antlr4.error.Errors import FailedPredicateException
                            raise FailedPredicateException(self, "self.precpred(self._ctx, 3)")
                        self.state = 62
                        _la = self._input.LA(1)
                        if not(_la==ExprParser.MUL or _la==ExprParser.DIV):
                            self._errHandler.recoverInline(self)
                        else:
                            self._errHandler.reportMatch(self)
                            self.consume()
                        self.state = 63
                        self.expr(4)
                        pass

                    elif la_ == 2:
                        localctx = ExprParser.AddSubContext(self, ExprParser.ExprContext(self, _parentctx, _parentState))
                        self.pushNewRecursionContext(localctx, _startState, self.RULE_expr)
                        self.state = 64
                        if not self.precpred(self._ctx, 2):
                            from antlr4.error.Errors import FailedPredicateException
                            raise FailedPredicateException(self, "self.precpred(self._ctx, 2)")
                        self.state = 65
                        _la = self._input.LA(1)
                        if not(_la==ExprParser.ADD or _la==ExprParser.SUB):
                            self._errHandler.recoverInline(self)
                        else:
                            self._errHandler.reportMatch(self)
                            self.consume()
                        self.state = 66
                        self.expr(3)
                        pass

             
                self.state = 71
                self._errHandler.sync(self)
                _alt = self._interp.adaptivePredict(self._input,5,self._ctx)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.unrollRecursionContexts(_parentctx)
        return localctx

    class PrimaryContext(ParserRuleContext):

        def __init__(self, parser, parent:ParserRuleContext=None, invokingState:int=-1):
            super().__init__(parent, invokingState)
            self.parser = parser


        def getRuleIndex(self):
            return ExprParser.RULE_primary

     
        def copyFrom(self, ctx:ParserRuleContext):
            super().copyFrom(ctx)



    class ParensContext(PrimaryContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.PrimaryContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def expr(self):
            return self.getTypedRuleContext(ExprParser.ExprContext,0)



    class IdContext(PrimaryContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.PrimaryContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def ID(self):
            return self.getToken(ExprParser.ID, 0)


    class IntContext(PrimaryContext):

        def __init__(self, parser, ctx:ParserRuleContext): # actually a ExprParser.PrimaryContext
            super().__init__(parser)
            self.copyFrom(ctx)

        def INT(self):
            return self.getToken(ExprParser.INT, 0)



    def primary(self):

        localctx = ExprParser.PrimaryContext(self, self._ctx, self.state)
        self.enterRule(localctx, 12, self.RULE_primary)
        try:
            self.state = 78
            self._errHandler.sync(self)
            token = self._input.LA(1)
            if token in [ExprParser.INT]:
                localctx = ExprParser.IntContext(self, localctx)
                self.enterOuterAlt(localctx, 1)
                self.state = 72
                self.match(ExprParser.INT)
                pass
            elif token in [ExprParser.ID]:
                localctx = ExprParser.IdContext(self, localctx)
                self.enterOuterAlt(localctx, 2)
                self.state = 73
                self.match(ExprParser.ID)
                pass
            elif token in [ExprParser.T__1]:
                localctx = ExprParser.ParensContext(self, localctx)
                self.enterOuterAlt(localctx, 3)
                self.state = 74
                self.match(ExprParser.T__1)
                self.state = 75
                self.expr(0)
                self.state = 76
                self.match(ExprParser.T__3)
                pass
            else:
                raise NoViableAltException(self)

        except RecognitionException as re:
            localctx.exception = re
            self._errHandler.reportError(self, re)
            self._errHandler.recover(self, re)
        finally:
            self.exitRule()
        return localctx



    def sempred(self, localctx:RuleContext, ruleIndex:int, predIndex:int):
        if self._predicates == None:
            self._predicates = dict()
        self._predicates[5] = self.expr_sempred
        pred = self._predicates.get(ruleIndex, None)
        if pred is None:
            raise Exception("No predicate with index:" + str(ruleIndex))
        else:
            return pred(localctx, predIndex)

    def expr_sempred(self, localctx:ExprContext, predIndex:int):
            if predIndex == 0:
                return self.precpred(self._ctx, 3)
         

            if predIndex == 1:
                return self.precpred(self._ctx, 2)
         




