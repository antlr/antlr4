#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#

#
# Represent a subset of XPath XML path syntax for use in identifying nodes in
# parse trees.
#
# <p>
# Split path into words and separators {@code /} and {@code //} via ANTLR
# itself then walk path elements from left to right. At each separator-word
# pair, find set of nodes. Next stage uses those as work list.</p>
#
# <p>
# The basic interface is
# {@link XPath#findAll ParseTree.findAll}{@code (tree, pathString, parser)}.
# But that is just shorthand for:</p>
#
# <pre>
# {@link XPath} p = new {@link XPath#XPath XPath}(parser, pathString);
# return p.{@link #evaluate evaluate}(tree);
# </pre>
#
# <p>
# See {@code org.antlr.v4.test.TestXPath} for descriptions. In short, this
# allows operators:</p>
#
# <dl>
# <dt>/</dt> <dd>root</dd>
# <dt>//</dt> <dd>anywhere</dd>
# <dt>!</dt> <dd>invert; this must appear directly after root or anywhere
# operator</dd>
# </dl>
#
# <p>
# and path elements:</p>
#
# <dl>
# <dt>ID</dt> <dd>token name</dd>
# <dt>'string'</dt> <dd>any string literal token from the grammar</dd>
# <dt>expr</dt> <dd>rule name</dd>
# <dt>*</dt> <dd>wildcard matching any node</dd>
# </dl>
#
# <p>
# Whitespace is not allowed.</p>
#
from antlr4 import CommonTokenStream, DFA, PredictionContextCache, Lexer, LexerATNSimulator, ParserRuleContext, TerminalNode
from antlr4.atn.ATNDeserializer import ATNDeserializer
from antlr4.InputStream import InputStream
from antlr4.Token import Token
from antlr4.error.ErrorListener import ErrorListener
from antlr4.error.Errors import LexerNoViableAltException
from antlr4.tree.Trees import Trees

from io import StringIO


def serializedATN():
    with StringIO() as buf:
        buf.write(u"\4\0\b\62\6\uffff\2\0\7\0\2\1\7\1\2\2\7\2\2\3\7\3\2\4")
        buf.write(u"\7\4\2\5\7\5\2\6\7\6\2\7\7\7\1\0\1\0\1\0\1\1\1\1\1\2")
        buf.write(u"\1\2\1\3\1\3\1\4\1\4\5\4\35\b\4\n\4\f\4 \t\4\1\4\1\4")
        buf.write(u"\1\5\1\5\3\5&\b\5\1\6\1\6\1\7\1\7\5\7,\b\7\n\7\f\7/\t")
        buf.write(u"\7\1\7\1\7\1-\0\b\1\3\3\4\5\5\7\6\t\7\13\0\r\0\17\b\1")
        buf.write(u"\0\2\5\0\609__\u00b7\u00b7\u0300\u036f\u203f\u2040\r")
        buf.write(u"\0AZaz\u00c0\u00d6\u00d8\u00f6\u00f8\u02ff\u0370\u037d")
        buf.write(u"\u037f\u1fff\u200c\u200d\u2070\u218f\u2c00\u2fef\u3001")
        buf.write(u"\ud7ff\uf900\ufdcf\ufdf0\uffff\0\62\0\1\1\0\0\0\0\3\1")
        buf.write(u"\0\0\0\0\5\1\0\0\0\0\7\1\0\0\0\0\t\1\0\0\0\0\17\1\0\0")
        buf.write(u"\0\1\21\1\0\0\0\3\24\1\0\0\0\5\26\1\0\0\0\7\30\1\0\0")
        buf.write(u"\0\t\32\1\0\0\0\13%\1\0\0\0\r\'\1\0\0\0\17)\1\0\0\0\21")
        buf.write(u"\22\5/\0\0\22\23\5/\0\0\23\2\1\0\0\0\24\25\5/\0\0\25")
        buf.write(u"\4\1\0\0\0\26\27\5*\0\0\27\6\1\0\0\0\30\31\5!\0\0\31")
        buf.write(u"\b\1\0\0\0\32\36\3\r\6\0\33\35\3\13\5\0\34\33\1\0\0\0")
        buf.write(u"\35 \1\0\0\0\36\34\1\0\0\0\36\37\1\0\0\0\37!\1\0\0\0")
        buf.write(u" \36\1\0\0\0!\"\6\4\0\0\"\n\1\0\0\0#&\3\r\6\0$&\7\0\0")
        buf.write(u"\0%#\1\0\0\0%$\1\0\0\0&\f\1\0\0\0\'(\7\1\0\0(\16\1\0")
        buf.write(u"\0\0)-\5\'\0\0*,\t\0\0\0+*\1\0\0\0,/\1\0\0\0-.\1\0\0")
        buf.write(u"\0-+\1\0\0\0.\60\1\0\0\0/-\1\0\0\0\60\61\5\'\0\0\61\20")
        buf.write(u"\1\0\0\0\4\0\36%-\1\1\4\0")
        return buf.getvalue()


class XPathLexer(Lexer):

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]


    TOKEN_REF = 1
    RULE_REF = 2
    ANYWHERE = 3
    ROOT = 4
    WILDCARD = 5
    BANG = 6
    ID = 7
    STRING = 8

    modeNames = [ u"DEFAULT_MODE" ]

    literalNames = [ u"<INVALID>",
            u"'//'", u"'/'", u"'*'", u"'!'" ]

    symbolicNames = [ u"<INVALID>",
            u"TOKEN_REF", u"RULE_REF", u"ANYWHERE", u"ROOT", u"WILDCARD",
            u"BANG", u"ID", u"STRING" ]

    ruleNames = [ u"ANYWHERE", u"ROOT", u"WILDCARD", u"BANG", u"ID", u"NameChar",
                  u"NameStartChar", u"STRING" ]

    grammarFileName = u"XPathLexer.g4"

    def __init__(self, input=None):
        super(XPathLexer, self).__init__(input)
        self.checkVersion("4.5")
        self._interp = LexerATNSimulator(self, self.atn, self.decisionsToDFA, PredictionContextCache())
        self._actions = None
        self._predicates = None


    def action(self, localctx, ruleIndex, actionIndex):
        if self._actions is None:
            actions = dict()
            actions[4] = self.ID_action
            self._actions = actions
        action = self._actions.get(ruleIndex, None)
        if action is not None:
            action(localctx, actionIndex)
        else:
            raise Exception("No registered action for:" + str(ruleIndex))

    def ID_action(self, localctx , actionIndex):
        if actionIndex == 0:
            char = self.text[0]
            if char.isupper():
                self.type = XPathLexer.TOKEN_REF
            else:
                self.type = XPathLexer.RULE_REF

class XPath(object):

    WILDCARD = "*" # word not operator/separator
    NOT = "!" # word for invert operator

    def __init__(self, parser, path):
        self.parser = parser
        self.path = path
        self.elements = self.split(path)

    def split(self, path):
        input = InputStream(path)
        lexer = XPathLexer(input)
        def recover(self, e):
            raise e
        lexer.recover = recover
        lexer.removeErrorListeners()
        lexer.addErrorListener(ErrorListener()) # XPathErrorListener does no more
        tokenStream = CommonTokenStream(lexer)
        try:
            tokenStream.fill()
        except LexerNoViableAltException as e:
            pos = lexer.getColumn()
            msg = "Invalid tokens or characters at index " + str(pos) + " in path '" + path + "'"
            raise Exception(msg, e)

        tokens = tokenStream.getTokens()
        elements = list()
        n = len(tokens)
        i=0
        while i < n :
            el = tokens[i]
            next = None
            if el.type in [XPathLexer.ROOT, XPathLexer.ANYWHERE]:
                    anywhere = el.type == XPathLexer.ANYWHERE
                    i += 1
                    next = tokens[i]
                    invert = next.type==XPathLexer.BANG
                    if invert:
                        i += 1
                        next = tokens[i]
                    pathElement = self.getXPathElement(next, anywhere)
                    pathElement.invert = invert
                    elements.append(pathElement)
                    i += 1

            elif el.type in [XPathLexer.TOKEN_REF, XPathLexer.RULE_REF, XPathLexer.WILDCARD] :
                    elements.append( self.getXPathElement(el, False) )
                    i += 1

            elif el.type==Token.EOF :
                    break

            else:
                    raise Exception("Unknown path element " + str(el))

        return elements

    #
    # Convert word like {@code#} or {@code ID} or {@code expr} to a path
    # element. {@code anywhere} is {@code true} if {@code //} precedes the
    # word.
    #
    def getXPathElement(self, wordToken, anywhere):
        if wordToken.type==Token.EOF:
            raise Exception("Missing path element at end of path")
        word = wordToken.text
        ttype = self.parser.getTokenType(word)
        ruleIndex = self.parser.getRuleIndex(word)

        if wordToken.type==XPathLexer.WILDCARD :

            return XPathWildcardAnywhereElement() if anywhere else XPathWildcardElement()

        elif wordToken.type in [XPathLexer.TOKEN_REF, XPathLexer.STRING]:

            if ttype==Token.INVALID_TYPE:
                raise Exception( word + " at index " + str(wordToken.startIndex) + " isn't a valid token name")
            return XPathTokenAnywhereElement(word, ttype) if anywhere else XPathTokenElement(word, ttype)

        else:

            if ruleIndex==-1:
                raise Exception( word + " at index " + str(wordToken.getStartIndex()) + " isn't a valid rule name")
            return XPathRuleAnywhereElement(word, ruleIndex) if anywhere else XPathRuleElement(word, ruleIndex)


    def findAll(self, tree, xpath, parser):
        p = XPath(parser, xpath)
        return p.evaluate(tree)

    #
    # Return a list of all nodes starting at {@code t} as root that satisfy the
    # path. The root {@code /} is relative to the node passed to
    # {@link #evaluate}.
    #
    def evaluate(self, t):
        dummyRoot = ParserRuleContext()
        dummyRoot.children = [t] # don't set t's parent.

        work = [dummyRoot]

        for i in range(0, len(self.elements)):
            next = set()
            for node in work:
                if len( node.children) > 0 :
                    # only try to match next element if it has children
                    # e.g., //func/*/stat might have a token node for which
                    # we can't go looking for stat nodes.
                    matching = self.elements[i].evaluate(node)
                    next |= matching
            i += 1
            work = next

        return work


class XPathElement(object):

    def __init__(self, nodeName):
        self.nodeName = nodeName
        self.invert = False

    def __str__(self):
        return unicode(self)

    def __unicode__(self):
        return type(self).__name__ + "[" + ("!" if self.invert else "") + self.nodeName + "]"



#
# Either {@code ID} at start of path or {@code ...//ID} in middle of path.
#
class XPathRuleAnywhereElement(XPathElement):

    def __init__(self, ruleName, ruleIndex):
        super(XPathRuleAnywhereElement, self).__init__(ruleName)
        self.ruleIndex = ruleIndex

    def evaluate(self, t):
        return Trees.findAllRuleNodes(t, self.ruleIndex)


class XPathRuleElement(XPathElement):

    def __init__(self, ruleName, ruleIndex):
        super(XPathRuleElement, self).__init__(ruleName)
        self.ruleIndex = ruleIndex

    def evaluate(self, t):
        # return all children of t that match nodeName
        return [c for c in Trees.getChildren(t) if isinstance(c, ParserRuleContext) and (c.ruleIndex == self.ruleIndex) == (not self.invert)]

class XPathTokenAnywhereElement(XPathElement):

    def __init__(self, ruleName, tokenType):
        super(XPathTokenAnywhereElement, self).__init__(ruleName)
        self.tokenType = tokenType

    def evaluate(self, t):
        return Trees.findAllTokenNodes(t, self.tokenType)


class XPathTokenElement(XPathElement):

    def __init__(self, ruleName, tokenType):
        super(XPathTokenElement, self).__init__(ruleName)
        self.tokenType = tokenType

    def evaluate(self, t):
        # return all children of t that match nodeName
        return [c for c in Trees.getChildren(t) if isinstance(c, TerminalNode) and (c.symbol.type == self.tokenType) == (not self.invert)]


class XPathWildcardAnywhereElement(XPathElement):

    def __init__(self):
        super(XPathWildcardAnywhereElement, self).__init__(XPath.WILDCARD)

    def evaluate(self, t):
        if self.invert:
            return list() # !* is weird but valid (empty)
        else:
            return Trees.descendants(t)


class XPathWildcardElement(XPathElement):

    def __init__(self):
        super(XPathWildcardElement, self).__init__(XPath.WILDCARD)


    def evaluate(self, t):
        if self.invert:
            return list() # !* is weird but valid (empty)
        else:
            return Trees.getChildren(t)
