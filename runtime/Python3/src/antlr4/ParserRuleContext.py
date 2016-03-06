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

#* A rule invocation record for parsing.
#
#  Contains all of the information about the current rule not stored in the
#  RuleContext. It handles parse tree children list, Any ATN state
#  tracing, and the default values available for rule indications:
#  start, stop, rule index, current alt number, current
#  ATN state.
#
#  Subclasses made for each rule and grammar track the parameters,
#  return values, locals, and labels specific to that rule. These
#  are the objects that are returned from rules.
#
#  Note text is not an actual field of a rule return value; it is computed
#  from start and stop using the input stream's toString() method.  I
#  could add a ctor to this so that we can pass in and store the input
#  stream, but I'm not sure we want to do that.  It would seem to be undefined
#  to get the .text property anyway if the rule matches tokens from multiple
#  input streams.
#
#  I do not use getters for fields of objects that are used simply to
#  group values such as this aggregate.  The getters/setters are there to
#  satisfy the superclass interface.

from antlr4.RuleContext import RuleContext
from antlr4.Token import Token
from antlr4.tree.Tree import ParseTreeListener, ParseTree, TerminalNodeImpl, ErrorNodeImpl, TerminalNode, \
    INVALID_INTERVAL

# need forward declaration
ParserRuleContext = None

class ParserRuleContext(RuleContext):

    def __init__(self, parent:ParserRuleContext = None, invokingStateNumber:int = None ):
        super().__init__(parent, invokingStateNumber)
        #* If we are debugging or building a parse tree for a visitor,
        #  we need to track all of the tokens and rule invocations associated
        #  with this rule's context. This is empty for parsing w/o tree constr.
        #  operation because we don't the need to track the details about
        #  how we parse this rule.
        #/
        self.children = None
        self.start = None
        self.stop = None
        # The exception that forced this rule to return. If the rule successfully
        # completed, this is {@code null}.
        self.exception = None

    #* COPY a ctx (I'm deliberately not using copy constructor)#/
    def copyFrom(self, ctx:ParserRuleContext):
        # from RuleContext
        self.parentCtx = ctx.parentCtx
        self.invokingState = ctx.invokingState
        self.children = None
        self.start = ctx.start
        self.stop = ctx.stop

    # Double dispatch methods for listeners
    def enterRule(self, listener:ParseTreeListener):
        pass

    def exitRule(self, listener:ParseTreeListener):
        pass

    #* Does not set parent link; other add methods do that#/
    def addChild(self, child:ParseTree):
        if self.children is None:
            self.children = []
        self.children.append(child)
        return child

    #* Used by enterOuterAlt to toss out a RuleContext previously added as
    #  we entered a rule. If we have # label, we will need to remove
    #  generic ruleContext object.
    #/
    def removeLastChild(self):
        if self.children is not None:
            del self.children[len(self.children)-1]

    def addTokenNode(self, token:Token):
        node = TerminalNodeImpl(token)
        self.addChild(node)
        node.parentCtx = self
        return node

    def addErrorNode(self, badToken:Token):
        node = ErrorNodeImpl(badToken)
        self.addChild(node)
        node.parentCtx = self
        return node

    def getChild(self, i:int, ttype:type = None):
        if ttype is None:
            return self.children[i] if len(self.children)>=i else None
        else:
            for child in self.getChildren():
                if not isinstance(child, ttype):
                    continue
                if i==0:
                    return child
                i -= 1
            return None

    def getChildren(self, predicate = None):
        if self.children is not None:
            for child in self.children:
                if predicate is not None and not predicate(child):
                    continue
                yield child

    def getToken(self, ttype:int, i:int):
        for child in self.getChildren():
            if not isinstance(child, TerminalNode):
                continue
            if child.symbol.type != ttype:
                continue
            if i==0:
                return child
            i -= 1
        return None

    def getTokens(self, ttype:int ):
        if self.getChildren() is None:
            return []
        tokens = []
        for child in self.getChildren():
            if not isinstance(child, TerminalNode):
                continue
            if child.symbol.type != ttype:
                continue
            tokens.append(child)
        return tokens

    def getTypedRuleContext(self, ctxType:type, i:int):
        return self.getChild(i, ctxType)

    def getTypedRuleContexts(self, ctxType:type):
        children = self.getChildren()
        if children is None:
            return []
        contexts = []
        for child in children:
            if not isinstance(child, ctxType):
                continue
            contexts.append(child)
        return contexts

    def getChildCount(self):
        return len(self.children) if self.children else 0

    def getSourceInterval(self):
        if self.start is None or self.stop is None:
            return INVALID_INTERVAL
        else:
            return (self.start.tokenIndex, self.stop.tokenIndex)


RuleContext.EMPTY = ParserRuleContext()

class InterpreterRuleContext(ParserRuleContext):

    def __init__(self, parent:ParserRuleContext, invokingStateNumber:int, ruleIndex:int):
        super().__init__(parent, invokingStateNumber)
        self.ruleIndex = ruleIndex
