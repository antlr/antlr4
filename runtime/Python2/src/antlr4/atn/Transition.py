#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#

#  An ATN transition between any two ATN states.  Subclasses define
#  atom, set, epsilon, action, predicate, rule transitions.
#
#  <p>This is a one way link.  It emanates from a state (usually via a list of
#  transitions) and has a target state.</p>
#
#  <p>Since we never have to change the ATN transitions once we construct it,
#  we can fix these transitions as specific classes. The DFA transitions
#  on the other hand need to update the labels as it adds transitions to
#  the states. We'll use the term Edge for the DFA to distinguish them from
#  ATN transitions.</p>
#
from __builtin__ import unicode
from antlr4.IntervalSet import IntervalSet, Interval
from antlr4.Token import Token
from antlr4.atn.SemanticContext import Predicate, PrecedencePredicate

class Transition (object):
    # constants for serialization
    EPSILON			= 1
    RANGE			= 2
    RULE			= 3
    PREDICATE		= 4 # e.g., {isType(input.LT(1))}?
    ATOM			= 5
    ACTION			= 6
    SET				= 7 # ~(A|B) or ~atom, wildcard, which convert to next 2
    NOT_SET			= 8
    WILDCARD		= 9
    PRECEDENCE		= 10

    serializationNames = [
            u"INVALID",
            u"EPSILON",
            u"RANGE",
            u"RULE",
            u"PREDICATE",
            u"ATOM",
            u"ACTION",
            u"SET",
            u"NOT_SET",
            u"WILDCARD",
            u"PRECEDENCE"
        ]

    serializationTypes = dict()

    def __init__(self, target):
        # The target of this transition.
        if target is None:
            raise Exception("target cannot be null.")
        self.target = target
        # Are we epsilon, action, sempred?
        self.isEpsilon = False
        self.label = None

    def __str__(self):
        return unicode(self)


# TODO: make all transitions sets? no, should remove set edges
class AtomTransition(Transition):

    def __init__(self, target, label):
        super(AtomTransition, self).__init__(target)
        self.label_ = label # The token type or character value; or, signifies special label.
        self.label = self.makeLabel()
        self.serializationType = self.ATOM

    def makeLabel(self):
        s = IntervalSet()
        s.addOne(self.label_)
        return s

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return self.label_ == symbol

    def __unicode__(self):
        return unicode(self.label_)

class RuleTransition(Transition):

    def __init__(self, ruleStart, ruleIndex, precedence, followState):
        super(RuleTransition, self).__init__(ruleStart)
        self.ruleIndex = ruleIndex # ptr to the rule definition object for this rule ref
        self.precedence = precedence
        self.followState = followState # what node to begin computations following ref to rule
        self.serializationType = self.RULE
        self.isEpsilon = True

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return False


class EpsilonTransition(Transition):

    def __init__(self, target, outermostPrecedenceReturn=-1):
        super(EpsilonTransition, self).__init__(target)
        self.serializationType = self.EPSILON
        self.isEpsilon = True
        self.outermostPrecedenceReturn = outermostPrecedenceReturn

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return False

    def __unicode__(self):
        return "epsilon"

class RangeTransition(Transition):

    def __init__(self, target, start, stop):
        super(RangeTransition, self).__init__(target)
        self.serializationType = self.RANGE
        self.start = start
        self.stop = stop
        self.label = self.makeLabel()

    def makeLabel(self):
        s = IntervalSet()
        s.addRange(Interval(self.start, self.stop + 1))
        return s

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return symbol >= self.start and symbol <= self.stop

    def __unicode__(self):
        return "'" + chr(self.start) + "'..'" + chr(self.stop) + "'"

class AbstractPredicateTransition(Transition):

    def __init__(self, target):
        super(AbstractPredicateTransition, self).__init__(target)


class PredicateTransition(AbstractPredicateTransition):

    def __init__(self, target, ruleIndex, predIndex, isCtxDependent):
        super(PredicateTransition, self).__init__(target)
        self.serializationType = self.PREDICATE
        self.ruleIndex = ruleIndex
        self.predIndex = predIndex
        self.isCtxDependent = isCtxDependent # e.g., $i ref in pred
        self.isEpsilon = True

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return False

    def getPredicate(self):
        return Predicate(self.ruleIndex, self.predIndex, self.isCtxDependent)

    def __unicode__(self):
        return u"pred_" + unicode(self.ruleIndex) + u":" + unicode(self.predIndex)

class ActionTransition(Transition):

    def __init__(self, target, ruleIndex, actionIndex=-1, isCtxDependent=False):
        super(ActionTransition, self).__init__(target)
        self.serializationType = self.ACTION
        self.ruleIndex = ruleIndex
        self.actionIndex = actionIndex
        self.isCtxDependent = isCtxDependent # e.g., $i ref in pred
        self.isEpsilon = True

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return False

    def __unicode__(self):
        return u"action_" + unicode(self.ruleIndex) + u":" + unicode(self.actionIndex)

# A transition containing a set of values.
class SetTransition(Transition):

    def __init__(self, target, set):
        super(SetTransition, self).__init__(target)
        self.serializationType = self.SET
        if set is not None:
            self.label = set
        else:
            self.label = IntervalSet()
            self.label.addRange(Interval(Token.INVALID_TYPE, Token.INVALID_TYPE + 1))

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return symbol in self.label

    def __unicode__(self):
        return unicode(self.label)

class NotSetTransition(SetTransition):

    def __init__(self, target, set):
        super(NotSetTransition, self).__init__(target, set)
        self.serializationType = self.NOT_SET

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return symbol >= minVocabSymbol \
            and symbol <= maxVocabSymbol \
            and not super(type(self), self).matches(symbol, minVocabSymbol, maxVocabSymbol)

    def __unicode__(self):
        return u'~' + super(type(self), self).__unicode__()


class WildcardTransition(Transition):

    def __init__(self, target):
        super(WildcardTransition, self).__init__(target)
        self.serializationType = self.WILDCARD

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return symbol >= minVocabSymbol and symbol <= maxVocabSymbol

    def __unicode__(self):
        return u"."


class PrecedencePredicateTransition(AbstractPredicateTransition):

    def __init__(self, target, precedence):
        super(PrecedencePredicateTransition, self).__init__(target)
        self.serializationType = self.PRECEDENCE
        self.precedence = precedence
        self.isEpsilon = True

    def matches( self, symbol, minVocabSymbol,  maxVocabSymbol):
        return False


    def getPredicate(self):
        return PrecedencePredicate(self.precedence)

    def __unicode__(self):
        return self.precedence + " >= _p"


Transition.serializationTypes = {
             EpsilonTransition: Transition.EPSILON,
             RangeTransition: Transition.RANGE,
             RuleTransition: Transition.RULE,
             PredicateTransition: Transition.PREDICATE,
             AtomTransition: Transition.ATOM,
             ActionTransition: Transition.ACTION,
             SetTransition: Transition.SET,
             NotSetTransition: Transition.NOT_SET,
             WildcardTransition: Transition.WILDCARD,
             PrecedencePredicateTransition: Transition.PRECEDENCE
         }

