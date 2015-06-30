#
# [The "BSD license"]
# Copyright (c) 2013 Terence Parr
# Copyright (c) 2013 Sam Harwell
# Copyright (c) 2014 Eric Vergnaud
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
# 3. The name of the author may not be used to endorse or promote products
#    derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
# IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
# IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
# INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
# NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
# THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

# A parser simulator that mimics what ANTLR's generated
#  parser code does. A ParserATNSimulator is used to make
#  predictions via adaptivePredict but this class moves a pointer through the
#  ATN to simulate parsing. ParserATNSimulator just
#  makes us efficient rather than having to backtrack, for example.
#
#  This properly creates parse trees even for left recursive rules.
#
#  We rely on the left recursive rule invocation and special predicate
#  transitions to make left recursive rules work.
#
#  See TestParserInterpreter for examples.
#
from antlr4 import PredictionContextCache
from antlr4.dfa.DFA import DFA
from antlr4.Parser import Parser
from antlr4.ParserRuleContext import InterpreterRuleContext
from antlr4.Token import Token
from antlr4.atn.ATNState import StarLoopEntryState, ATNState, LoopEndState
from antlr4.atn.ParserATNSimulator import ParserATNSimulator
from antlr4.atn.Transition import Transition
from antlr4.error.Errors import RecognitionException, UnsupportedOperationException, FailedPredicateException


class ParserInterpreter(Parser):

    def __init__(self, grammarFileName, tokenNames, ruleNames, atn, input):
        super(ParserInterpreter, self).__init__(input)
        self.grammarFileName = grammarFileName
        self.atn = atn
        self.tokenNames = tokenNames
        self.ruleNames = ruleNames
        self.decisionToDFA = [ DFA(state) for state in atn.decisionToState ]
        self.sharedContextCache = PredictionContextCache()
        self._parentContextStack = list()
        # identify the ATN states where pushNewRecursionContext must be called
        self.pushRecursionContextStates = set()
        for state in atn.states:
            if not isinstance(state, StarLoopEntryState):
                continue
            if state.precedenceRuleDecision:
                self.pushRecursionContextStates.add(state.stateNumber)
        # get atn simulator that knows how to do predictions
        self._interp = ParserATNSimulator(self, atn, self.decisionToDFA, self.sharedContextCache)

    # Begin parsing at startRuleIndex#
    def parse(self, startRuleIndex):
        startRuleStartState = self.atn.ruleToStartState[startRuleIndex]
        rootContext = InterpreterRuleContext(None, ATNState.INVALID_STATE_NUMBER, startRuleIndex)
        if startRuleStartState.isPrecedenceRule:
            self.enterRecursionRule(rootContext, startRuleStartState.stateNumber, startRuleIndex, 0)
        else:
            self.enterRule(rootContext, startRuleStartState.stateNumber, startRuleIndex)
        while True:
            p = self.getATNState()
            if p.stateType==ATNState.RULE_STOP :
                # pop; return from rule
                if len(self._ctx)==0:
                    if startRuleStartState.isPrecedenceRule:
                        result = self._ctx
                        parentContext = self._parentContextStack.pop()
                        self.unrollRecursionContexts(parentContext.a)
                        return result
                    else:
                        self.exitRule()
                        return rootContext
                self.visitRuleStopState(p)

            else:
                try:
                    self.visitState(p)
                except RecognitionException as e:
                    self.state = self.atn.ruleToStopState[p.ruleIndex].stateNumber
                    self._ctx.exception = e
                    self._errHandler.reportError(self, e)
                    self._errHandler.recover(self, e)

    def enterRecursionRule(self, localctx, state, ruleIndex, precedence):
        self._parentContextStack.append((self._ctx, localctx.invokingState))
        super(ParserInterpreter, self).enterRecursionRule(localctx, state, ruleIndex, precedence)

    def getATNState(self):
        return self.atn.states[self.state]

    def visitState(self, p):
        edge = 0
        if len(p.transitions) > 1:
            self._errHandler.sync(self)
            edge = self._interp.adaptivePredict(self._input, p.decision, self._ctx)
        else:
            edge = 1

        transition = p.transitions[edge - 1]
        tt = transition.serializationType
        if tt==Transition.EPSILON:

            if self.pushRecursionContextStates[p.stateNumber] and not isinstance(transition.target, LoopEndState):
                t = self._parentContextStack[-1]
                ctx = InterpreterRuleContext(t[0], t[1], self._ctx.ruleIndex)
                self.pushNewRecursionContext(ctx, self.atn.ruleToStartState[p.ruleIndex].stateNumber, self._ctx.ruleIndex)

        elif tt==Transition.ATOM:

            self.match(transition.label)

        elif tt in [ Transition.RANGE, Transition.SET, Transition.NOT_SET]:

            if not transition.matches(self._input.LA(1), Token.MIN_USER_TOKEN_TYPE, 0xFFFF):
                self._errHandler.recoverInline(self)
            self.matchWildcard()

        elif tt==Transition.WILDCARD:

            self.matchWildcard()

        elif tt==Transition.RULE:

            ruleStartState = transition.target
            ruleIndex = ruleStartState.ruleIndex
            ctx = InterpreterRuleContext(self._ctx, p.stateNumber, ruleIndex)
            if ruleStartState.isPrecedenceRule:
                self.enterRecursionRule(ctx, ruleStartState.stateNumber, ruleIndex, transition.precedence)
            else:
                self.enterRule(ctx, transition.target.stateNumber, ruleIndex)

        elif tt==Transition.PREDICATE:

            if not self.sempred(self._ctx, transition.ruleIndex, transition.predIndex):
                raise FailedPredicateException(self)

        elif tt==Transition.ACTION:

            self.action(self._ctx, transition.ruleIndex, transition.actionIndex)

        elif tt==Transition.PRECEDENCE:

            if not self.precpred(self._ctx, transition.precedence):
                msg = "precpred(_ctx, " + str(transition.precedence) + ")"
                raise FailedPredicateException(self, msg)

        else:
            raise UnsupportedOperationException("Unrecognized ATN transition type.")

        self.state = transition.target.stateNumber

    def visitRuleStopState(self, p):
        ruleStartState = self.atn.ruleToStartState[p.ruleIndex]
        if ruleStartState.isPrecedenceRule:
            parentContext = self._parentContextStack.pop()
            self.unrollRecursionContexts(parentContext.a)
            self.state = parentContext[1]
        else:
            self.exitRule()

        ruleTransition = self.atn.states[self.state].transitions[0]
        self.state = ruleTransition.followState.stateNumber
