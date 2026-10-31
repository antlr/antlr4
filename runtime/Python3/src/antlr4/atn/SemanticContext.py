#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#

# A tree structure used to record the semantic context in which
#  an ATN configuration is valid.  It's either a single predicate,
#  a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
#
#  <p>I have scoped the {@link AND}, {@link OR}, and {@link Predicate} subclasses of
#  {@link SemanticContext} within the scope of this outer class.</p>
#
from ..Recognizer import Recognizer
from ..RuleContext import RuleContext
from io import StringIO


class SemanticContext(object):
    #
    # The default {@link SemanticContext}, which is semantically equivalent to
    # a predicate of the form {@code {true}?}.
    #
    NONE = None

    #
    # For context independent predicates, we evaluate them without a local
    # context (i.e., null context). That way, we can evaluate them without
    # having to create proper rule-specific context during prediction (as
    # opposed to the parser, which creates them naturally). In a practical
    # sense, this avoids a cast exception from RuleContext to myruleContext.
    #
    # <p>For context dependent predicates, we must pass in a local context so that
    # references such as $arg evaluate properly as _localctx.arg. We only
    # capture context dependent predicates in the context in which we begin
    # prediction, so we passed in the outer context here in case of context
    # dependent predicate evaluation.</p>
    #
    def eval(self, parser:Recognizer , outerContext:RuleContext ):
        pass

    #
    # Evaluate the precedence predicates for the context and reduce the result.
    #
    # @param parser The parser instance.
    # @param outerContext The current parser context object.
    # @return The simplified semantic context after precedence predicates are
    # evaluated, which will be one of the following values.
    # <ul>
    # <li>{@link #NONE}: if the predicate simplifies to {@code true} after
    # precedence predicates are evaluated.</li>
    # <li>{@code null}: if the predicate simplifies to {@code false} after
    # precedence predicates are evaluated.</li>
    # <li>{@code this}: if the semantic context is not changed as a result of
    # precedence predicate evaluation.</li>
    # <li>A non-{@code null} {@link SemanticContext}: the new simplified
    # semantic context after precedence predicates are evaluated.</li>
    # </ul>
    #
    def evalPrecedence(self, parser:Recognizer, outerContext:RuleContext):
        return self

# need forward declaration
AND = None

def andContext(a:SemanticContext, b:SemanticContext):
    if a is None or a is SemanticContext.NONE:
        return b
    if b is None or b is SemanticContext.NONE:
        return a
    result = AND(a, b)
    if len(result.opnds) == 1:
        return result.opnds[0]
    else:
        return result

# need forward declaration
OR = None

def orContext(a:SemanticContext, b:SemanticContext):
    if a is None:
        return b
    if b is None:
        return a
    if a is SemanticContext.NONE or b is SemanticContext.NONE:
        return SemanticContext.NONE
    result = OR(a, b)
    if len(result.opnds) == 1:
        return result.opnds[0]
    else:
        return result

def filterPrecedencePredicates(collection:set):
    return [context for context in collection if isinstance(context, PrecedencePredicate)]


class EmptySemanticContext(SemanticContext):
    pass

class Predicate(SemanticContext):
    __slots__ = ('ruleIndex', 'predIndex', 'isCtxDependent')

    def __init__(self, ruleIndex:int=-1, predIndex:int=-1, isCtxDependent:bool=False):
        self.ruleIndex = ruleIndex
        self.predIndex = predIndex
        self.isCtxDependent = isCtxDependent # e.g., $i ref in pred

    def eval(self, parser:Recognizer , outerContext:RuleContext ):
        localctx = outerContext if self.isCtxDependent else None
        return parser.sempred(localctx, self.ruleIndex, self.predIndex)

    def __hash__(self):
        return hash((self.ruleIndex, self.predIndex, self.isCtxDependent))

    def __eq__(self, other):
        if self is other:
            return True
        elif not isinstance(other, Predicate):
            return False
        return self.ruleIndex == other.ruleIndex and \
               self.predIndex == other.predIndex and \
               self.isCtxDependent == other.isCtxDependent

    def __str__(self):
        return "{" + str(self.ruleIndex) + ":" + str(self.predIndex) + "}?"


class PrecedencePredicate(SemanticContext):

    def __init__(self, precedence:int=0):
        self.precedence = precedence

    def eval(self, parser:Recognizer , outerContext:RuleContext ):
        return parser.precpred(outerContext, self.precedence)

    def evalPrecedence(self, parser:Recognizer, outerContext:RuleContext):
        if parser.precpred(outerContext, self.precedence):
            return SemanticContext.NONE
        else:
            return None

    def __lt__(self, other):
        return self.precedence < other.precedence

    def __hash__(self):
        return 31

    def __eq__(self, other):
        if self is other:
            return True
        elif not isinstance(other, PrecedencePredicate):
            return False
        else:
            return self.precedence == other.precedence

    def __str__(self):
        return "{" + str(self.precedence) + ">=prec}?"


def _kind(context:SemanticContext):
    if isinstance(context, Predicate):
        return 0
    if isinstance(context, PrecedencePredicate):
        return 1
    if isinstance(context, AND):
        return 2
    if isinstance(context, OR):
        return 3
    return 4

def _cmp(x, y):
    return (x > y) - (x < y)

# Orders contexts by kind and then by their fields, so an operator holds its
# operands in one order whichever order they were combined in.
def _compare(a:SemanticContext, b:SemanticContext):
    kind = _cmp(_kind(a), _kind(b))
    if kind != 0:
        return kind
    if isinstance(a, Predicate):
        return _cmp((a.ruleIndex, a.predIndex, a.isCtxDependent),
                    (b.ruleIndex, b.predIndex, b.isCtxDependent))
    if isinstance(a, PrecedencePredicate):
        return _cmp(a.precedence, b.precedence)
    if isinstance(a, (AND, OR)):
        if len(a.opnds) != len(b.opnds):
            return _cmp(len(a.opnds), len(b.opnds))
        for x, y in zip(a.opnds, b.opnds):
            c = _compare(x, y)
            if c != 0:
                return c
        return 0
    return _cmp(hash(a), hash(b))

# Merges two operand lists, each already in _compare order, into one list in
# that order without duplicates.
def _merge(x:list, y:list):
    merged = []
    i = 0
    j = 0
    while i < len(x) and j < len(y):
        c = _compare(x[i], y[j])
        # _compare orders contexts of other kinds by hash code, so it can tie two unequal ones
        if c < 0 or c == 0 and x[i] != y[j]:
            merged.append(x[i])
            i += 1
        elif c > 0:
            merged.append(y[j])
            j += 1
        else:
            merged.append(x[i])
            i += 1
            j += 1
    merged.extend(x[i:])
    merged.extend(y[j:])
    return merged

# Removes all precedence predicates but the one with the lowest or highest
# precedence. They sort next to each other, by precedence.
def _keepOnePrecedencePredicate(operands:list, lowest:bool):
    indexes = [i for i, o in enumerate(operands) if isinstance(o, PrecedencePredicate)]
    if len(indexes) == 0:
        return
    first = indexes[0]
    last = indexes[-1]
    operands[first:last + 1] = [operands[first if lowest else last]]


# A semantic context which is true whenever none of the contained contexts
# is false.
del AND
class AND(SemanticContext):
    __slots__ = ('opnds', 'hashCode')

    def __init__(self, a:SemanticContext, b:SemanticContext):
        operands = _merge(a.opnds if isinstance(a, AND) else [a],
                          b.opnds if isinstance(b, AND) else [b])
        # interested in the transition with the lowest precedence
        _keepOnePrecedencePredicate(operands, True)
        self.opnds = operands
        h = 0
        for o in self.opnds:
            h = hash((h, o))
        self.hashCode = hash((h, "AND"))

    def __eq__(self, other):
        if self is other:
            return True
        elif not isinstance(other, AND):
            return False
        else:
            return self.hashCode == other.hashCode and self.opnds == other.opnds

    def __hash__(self):
        return self.hashCode

    #
    # {@inheritDoc}
    #
    # <p>
    # The evaluation of predicates by this context is short-circuiting, but
    # unordered.</p>
    #
    def eval(self, parser:Recognizer, outerContext:RuleContext):
        return all(opnd.eval(parser, outerContext) for opnd in self.opnds)

    def evalPrecedence(self, parser:Recognizer, outerContext:RuleContext):
        differs = False
        operands = []
        for context in self.opnds:
            evaluated = context.evalPrecedence(parser, outerContext)
            differs |= evaluated is not context
            if evaluated is None:
                # The AND context is false if any element is false
                return None
            elif evaluated is not SemanticContext.NONE:
                # Reduce the result by skipping true elements
                operands.append(evaluated)

        if not differs:
            return self

        if len(operands)==0:
            # all elements were true, so the AND context is true
            return SemanticContext.NONE

        result = None
        for o in operands:
            result = o if result is None else andContext(result, o)

        return result

    def __str__(self):
        with StringIO() as buf:
            first = True
            for o in self.opnds:
                if not first:
                    buf.write("&&")
                buf.write(str(o))
                first = False
            return buf.getvalue()

#
# A semantic context which is true whenever at least one of the contained
# contexts is true.
del OR
class OR (SemanticContext):
    __slots__ = ('opnds', 'hashCode')

    def __init__(self, a:SemanticContext, b:SemanticContext):
        operands = _merge(a.opnds if isinstance(a, OR) else [a],
                          b.opnds if isinstance(b, OR) else [b])
        # interested in the transition with the highest precedence
        _keepOnePrecedencePredicate(operands, False)
        self.opnds = operands
        h = 0
        for o in self.opnds:
            h = hash((h, o))
        self.hashCode = hash((h, "OR"))

    def __eq__(self, other):
        if self is other:
            return True
        elif not isinstance(other, OR):
            return False
        else:
            return self.hashCode == other.hashCode and self.opnds == other.opnds

    def __hash__(self):
        return self.hashCode

    # <p>
    # The evaluation of predicates by this context is short-circuiting, but
    # unordered.</p>
    #
    def eval(self, parser:Recognizer, outerContext:RuleContext):
        return any(opnd.eval(parser, outerContext) for opnd in self.opnds)

    def evalPrecedence(self, parser:Recognizer, outerContext:RuleContext):
        differs = False
        operands = []
        for context in self.opnds:
            evaluated = context.evalPrecedence(parser, outerContext)
            differs |= evaluated is not context
            if evaluated is SemanticContext.NONE:
                # The OR context is true if any element is true
                return SemanticContext.NONE
            elif evaluated is not None:
                # Reduce the result by skipping false elements
                operands.append(evaluated)

        if not differs:
            return self

        if len(operands)==0:
            # all elements were false, so the OR context is false
            return None

        result = None
        for o in operands:
            result = o if result is None else orContext(result, o)

        return result

    def __str__(self):
        with StringIO() as buf:
            first = True
            for o in self.opnds:
                if not first:
                    buf.write("||")
                buf.write(str(o))
                first = False
            return buf.getvalue()


SemanticContext.NONE = EmptySemanticContext()
