# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#/


# The basic notion of a tree has a parent, a payload, and a list of children.
#  It is the most abstract interface for all the trees used by ANTLR.
#/
from antlr4.Token import Token

INVALID_INTERVAL = (-1, -2)

class Tree(object):

    def __str__(self):
        return unicode(self)

class SyntaxTree(Tree):
    pass

class ParseTree(SyntaxTree):
    pass

class RuleNode(ParseTree):
    pass

class TerminalNode(ParseTree):
    pass

class ErrorNode(TerminalNode):
    pass

class ParseTreeVisitor(object):
    def visit(self, tree):
        return tree.accept(self)

    def visitChildren(self, node):
        result = self.defaultResult()
        n = node.getChildCount()
        for i in range(n):
            if not self.shouldVisitNextChild(node, result):
                return

            c = node.getChild(i)
            childResult = c.accept(self)
            result = self.aggregateResult(result, childResult)

        return result

    def visitTerminal(self, node):
        return self.defaultResult()

    def visitErrorNode(self, node):
        return self.defaultResult()

    def defaultResult(self):
        return None

    def aggregateResult(self, aggregate, nextResult):
        return nextResult

    def shouldVisitNextChild(self, node, currentResult):
        return True

class ParseTreeListener(object):

    def visitTerminal(self, node):
        pass

    def visitErrorNode(self, node):
        pass

    def enterEveryRule(self, ctx):
        pass

    def exitEveryRule(self, ctx):
        pass

class TerminalNodeImpl(TerminalNode):

    def __init__(self, symbol):
        self.parentCtx = None
        self.symbol = symbol

    def getChild(self, i):
        return None

    def getSymbol(self):
        return self.symbol

    def getParent(self):
        return self.parentCtx

    def getPayload(self):
        return self.symbol

    def getSourceInterval(self):
        if self.symbol is None:
            return INVALID_INTERVAL
        tokenIndex = self.symbol.tokenIndex
        return (tokenIndex, tokenIndex)

    def getChildCount(self):
        return 0

    def accept(self, visitor):
        return visitor.visitTerminal(self)

    def getText(self):
        return unicode(self.symbol.text)

    def __unicode__(self):
        if self.symbol.type == Token.EOF:
            return u"<EOF>"
        else:
            return unicode(self.symbol.text)

# Represents a token that was consumed during resynchronization
#  rather than during a valid match operation. For example,
#  we will create this kind of a node during single token insertion
#  and deletion as well as during "consume until error recovery set"
#  upon no viable alternative exceptions.

class ErrorNodeImpl(TerminalNodeImpl,ErrorNode):

    def __init__(self, token):
        super(ErrorNodeImpl, self).__init__(token)

    def accept(self, visitor):
        return visitor.visitErrorNode(self)


class ParseTreeWalker(object):

    DEFAULT = None

    def walk(self, listener, t):
        if isinstance(t, ErrorNode):
            listener.visitErrorNode(t)
            return
        elif isinstance(t, TerminalNode):
            listener.visitTerminal(t)
            return
        self.enterRule(listener, t)
        for child in t.getChildren():
            self.walk(listener, child)
        self.exitRule(listener, t)

    #
    # The discovery of a rule node, involves sending two events: the generic
    # {@link ParseTreeListener#enterEveryRule} and a
    # {@link RuleContext}-specific event. First we trigger the generic and then
    # the rule specific. We to them in reverse order upon finishing the node.
    #
    def enterRule(self, listener, r):
        ctx = r.getRuleContext()
        listener.enterEveryRule(ctx)
        ctx.enterRule(listener)

    def exitRule(self, listener, r):
        ctx = r.getRuleContext()
        ctx.exitRule(listener)
        listener.exitEveryRule(ctx)

ParseTreeWalker.DEFAULT = ParseTreeWalker()