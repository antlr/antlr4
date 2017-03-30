# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#/
from antlr4.atn.Transition import PredicateTransition


class UnsupportedOperationException(Exception):

    def __init__(self, msg):
        super(UnsupportedOperationException, self).__init__(msg)

class IllegalStateException(Exception):

    def __init__(self, msg):
        super(IllegalStateException, self).__init__(msg)

class CancellationException(IllegalStateException):

    def __init__(self, msg):
        super(CancellationException, self).__init__(msg)

# The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
#  3 kinds of errors: prediction errors, failed predicate errors, and
#  mismatched input errors. In each case, the parser knows where it is
#  in the input, where it is in the ATN, the rule invocation stack,
#  and what kind of problem occurred.

from antlr4.InputStream import InputStream
from antlr4.ParserRuleContext import ParserRuleContext
from antlr4.Recognizer import Recognizer

class RecognitionException(Exception):


    def __init__(self, message=None, recognizer=None, input=None, ctx=None):
        super(RecognitionException, self).__init__(message)
        self.recognizer = recognizer
        self.input = input
        self.ctx = ctx
        # The current {@link Token} when an error occurred. Since not all streams
        # support accessing symbols by index, we have to track the {@link Token}
        # instance itself.
        self.offendingToken = None
        # Get the ATN state number the parser was in at the time the error
        # occurred. For {@link NoViableAltException} and
        # {@link LexerNoViableAltException} exceptions, this is the
        # {@link DecisionState} number. For others, it is the state whose outgoing
        # edge we couldn't match.
        self.offendingState = -1
        if recognizer is not None:
            self.offendingState = recognizer.state

    # <p>If the state number is not known, this method returns -1.</p>

    #
    # Gets the set of input symbols which could potentially follow the
    # previously matched symbol at the time this exception was thrown.
    #
    # <p>If the set of expected tokens is not known and could not be computed,
    # this method returns {@code null}.</p>
    #
    # @return The set of token types that could potentially follow the current
    # state in the ATN, or {@code null} if the information is not available.
    #/
    def getExpectedTokens(self):
        if self.recognizer is not None:
            return self.recognizer.atn.getExpectedTokens(self.offendingState, self.ctx)
        else:
            return None

    def __str__(self):
        return unicode(self)


class LexerNoViableAltException(RecognitionException):

    def __init__(self, lexer, input, startIndex, deadEndConfigs):
        super(LexerNoViableAltException, self).__init__(message=None, recognizer=lexer, input=input, ctx=None)
        self.startIndex = startIndex
        self.deadEndConfigs = deadEndConfigs

    def __unicode__(self):
        symbol = ""
        if self.startIndex >= 0 and self.startIndex < self.input.size():
            symbol = self.input.getText((self.startIndex,self.startIndex))
            # TODO symbol = Utils.escapeWhitespace(symbol, false);
        return u"LexerNoViableAltException" + symbol

# Indicates that the parser could not decide which of two or more paths
#  to take based upon the remaining input. It tracks the starting token
#  of the offending input and also knows where the parser was
#  in the various paths when the error. Reported by reportNoViableAlternative()
#
class NoViableAltException(RecognitionException):

    def __init__(self, recognizer, input=None, startToken=None, offendingToken=None, deadEndConfigs=None, ctx=None):
        if ctx is None:
            ctx = recognizer._ctx
        if offendingToken is None:
            offendingToken = recognizer.getCurrentToken()
        if startToken is None:
            startToken = recognizer.getCurrentToken()
        if input is None:
            input = recognizer.getInputStream()
        super(NoViableAltException, self).__init__(recognizer=recognizer, input=input, ctx=ctx)
        # Which configurations did we try at input.index() that couldn't match input.LT(1)?#
        self.deadEndConfigs = deadEndConfigs
        # The token object at the start index; the input stream might
        # 	not be buffering tokens so get a reference to it. (At the
        #  time the error occurred, of course the stream needs to keep a
        #  buffer all of the tokens but later we might not have access to those.)
        self.startToken = startToken
        self.offendingToken = offendingToken

# This signifies any kind of mismatched input exceptions such as
#  when the current input does not match the expected token.
#
class InputMismatchException(RecognitionException):

    def __init__(self, recognizer):
        super(InputMismatchException, self).__init__(recognizer=recognizer, input=recognizer.getInputStream(), ctx=recognizer._ctx)
        self.offendingToken = recognizer.getCurrentToken()


# A semantic predicate failed during validation.  Validation of predicates
#  occurs when normally parsing the alternative just like matching a token.
#  Disambiguating predicate evaluation occurs when we test a predicate during
#  prediction.

class FailedPredicateException(RecognitionException):

    def __init__(self, recognizer, predicate=None, message=None):
        super(FailedPredicateException, self).__init__(message=self.formatMessage(predicate,message), recognizer=recognizer,
                         input=recognizer.getInputStream(), ctx=recognizer._ctx)
        s = recognizer._interp.atn.states[recognizer.state]
        trans = s.transitions[0]
        if isinstance(trans, PredicateTransition):
            self.ruleIndex = trans.ruleIndex
            self.predicateIndex = trans.predIndex
        else:
            self.ruleIndex = 0
            self.predicateIndex = 0
        self.predicate = predicate
        self.offendingToken = recognizer.getCurrentToken()

    def formatMessage(self, predicate, message):
        if message is not None:
            return message
        else:
            return "failed predicate: {" + predicate + "}?"

class ParseCancellationException(CancellationException):

    pass

