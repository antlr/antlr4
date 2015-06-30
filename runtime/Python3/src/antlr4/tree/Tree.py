# [The "BSD license"]
#  Copyright (c) 2012 Terence Parr
#  Copyright (c) 2012 Sam Harwell
#  Copyright (c) 2014 Eric Vergnaud
#  All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions
#  are met:
#
#  1. Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#  2. Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#  3. The name of the author may not be used to endorse or promote products
#     derived from this software without specific prior written permission.
#
#  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
#  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
#  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
#  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
#  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
#  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
#  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#/


# The basic notion of a tree has a parent, a payload, and a list of children.
#  It is the most abstract interface for all the trees used by ANTLR.
#/
from antlr4.Token import Token

INVALID_INTERVAL = (-1, -2)

class Tree(object):
    pass

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

ParserRuleContext = None

class ParseTreeListener(object):

    def visitTerminal(self, node:TerminalNode):
        pass

    def visitErrorNode(self, node:ErrorNode):
        pass

    def enterEveryRule(self, ctx:ParserRuleContext):
        pass

    def exitEveryRule(self, ctx:ParserRuleContext):
        pass

del ParserRuleContext

class TerminalNodeImpl(TerminalNode):

    def __init__(self, symbol:Token):
        self.parentCtx = None
        self.symbol = symbol
    def __setattr__(self, key, value):
        super().__setattr__(key, value)

    def getChild(self, i:int):
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

    def accept(self, visitor:ParseTreeVisitor):
        return visitor.visitTerminal(self)

    def getText(self):
        return self.symbol.text

    def __str__(self):
        if self.symbol.type == Token.EOF:
            return "<EOF>"
        else:
            return self.symbol.text

# Represents a token that was consumed during resynchronization
#  rather than during a valid match operation. For example,
#  we will create this kind of a node during single token insertion
#  and deletion as well as during "consume until error recovery set"
#  upon no viable alternative exceptions.

class ErrorNodeImpl(TerminalNodeImpl,ErrorNode):

    def __init__(self, token:Token):
        super().__init__(token)

    def accept(self, visitor:ParseTreeVisitor):
        return visitor.visitErrorNode(self)


class ParseTreeWalker(object):

    DEFAULT = None

    def walk(self, listener:ParseTreeListener, t:ParseTree):
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
    def enterRule(self, listener:ParseTreeListener, r:RuleNode):
        ctx = r.getRuleContext()
        listener.enterEveryRule(ctx)
        ctx.enterRule(listener)

    def exitRule(self, listener:ParseTreeListener, r:RuleNode):
        ctx = r.getRuleContext()
        ctx.exitRule(listener)
        listener.exitEveryRule(ctx)

ParseTreeWalker.DEFAULT = ParseTreeWalker()