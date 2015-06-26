#
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
#


# A set of utility routines useful for all kinds of ANTLR trees.#
from io import StringIO
from antlr4.Token import Token
from antlr4.Utils import escapeWhitespace
from antlr4.tree.Tree import RuleNode, ErrorNode, TerminalNode, Tree, ParseTree

# need forward declaration
Parser  = None

class Trees(object):

     # Print out a whole tree in LISP form. {@link #getNodeText} is used on the
    #  node payloads to get the text for the nodes.  Detect
    #  parse trees and extract data appropriately.
    @classmethod
    def toStringTree(cls, t:Tree, ruleNames:list=None, recog:Parser=None):
        if recog is not None:
            ruleNames = recog.ruleNames
        s = escapeWhitespace(cls.getNodeText(t, ruleNames), False)
        if t.getChildCount()==0:
            return s
        with StringIO() as buf:
            buf.write("(")
            buf.write(s)
            buf.write(' ')
            for i in range(0, t.getChildCount()):
                if i > 0:
                    buf.write(' ')
                buf.write(cls.toStringTree(t.getChild(i), ruleNames))
            buf.write(")")
            return buf.getvalue()

    @classmethod
    def getNodeText(cls, t:Tree, ruleNames:list=None, recog:Parser=None):
        if recog is not None:
            ruleNames = recog.ruleNames
        if ruleNames is not None:
            if isinstance(t, RuleNode):
                return ruleNames[t.getRuleContext().getRuleIndex()]
            elif isinstance( t, ErrorNode):
                return str(t)
            elif isinstance(t, TerminalNode):
                if t.symbol is not None:
                    return t.symbol.text
        # no recog for rule names
        payload = t.getPayload()
        if isinstance(payload, Token ):
            return payload.text
        return str(t.getPayload())


    # Return ordered list of all children of this node
    @classmethod
    def getChildren(cls, t:Tree):
        return [ t.getChild(i) for i in range(0, t.getChildCount()) ]

    # Return a list of all ancestors of this node.  The first node of
    #  list is the root and the last is the parent of this node.
    #
    @classmethod
    def getAncestors(cls, t:Tree):
        ancestors = []
        t = t.getParent()
        while t is not None:
            ancestors.insert(0, t) # insert at start
            t = t.getParent()
        return ancestors

    @classmethod
    def findAllTokenNodes(cls, t:ParseTree, ttype:int):
        return cls.findAllNodes(t, ttype, True)

    @classmethod
    def findAllRuleNodes(cls, t:ParseTree, ruleIndex:int):
        return cls.findAllNodes(t, ruleIndex, False)

    @classmethod
    def findAllNodes(cls, t:ParseTree, index:int, findTokens:bool):
        nodes = []
        cls._findAllNodes(t, index, findTokens, nodes)
        return nodes

    @classmethod
    def _findAllNodes(cls, t:ParseTree, index:int, findTokens:bool, nodes:list):
        from antlr4.ParserRuleContext import ParserRuleContext
        # check this node (the root) first
        if findTokens and isinstance(t, TerminalNode):
            if t.symbol.type==index:
                nodes.append(t)
        elif not findTokens and isinstance(t, ParserRuleContext):
            if t.ruleIndex == index:
                nodes.append(t)
        # check children
        for i in range(0, t.getChildCount()):
            cls._findAllNodes(t.getChild(i), index, findTokens, nodes)

    @classmethod
    def descendants(cls, t:ParseTree):
        nodes = []
        nodes.append(t)
        for i in range(0, t.getChildCount()):
            nodes.extend(cls.descendants(t.getChild(i)))
        return nodes
