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
from antlr4.InputStream import InputStream
from antlr4.Parser import Parser
from antlr4.RuleContext import RuleContext
from antlr4.Token import Token
from antlr4.atn.ATNDeserializer import ATNDeserializer
from antlr4.error.ErrorListener import ErrorListener
from antlr4.error.Errors import LexerNoViableAltException
from antlr4.tree.Tree import ParseTree
from antlr4.tree.Trees import Trees
from io import StringIO


def serializedATN():
    with StringIO() as buf:
        buf.write("\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\n")
        buf.write("\64\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t")
        buf.write("\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5")
        buf.write("\3\6\3\6\7\6\37\n\6\f\6\16\6\"\13\6\3\6\3\6\3\7\3\7\5")
        buf.write("\7(\n\7\3\b\3\b\3\t\3\t\7\t.\n\t\f\t\16\t\61\13\t\3\t")
        buf.write("\3\t\3/\2\n\3\5\5\6\7\7\t\b\13\t\r\2\17\2\21\n\3\2\4\7")
        buf.write("\2\62;aa\u00b9\u00b9\u0302\u0371\u2041\u2042\17\2C\\c")
        buf.write("|\u00c2\u00d8\u00da\u00f8\u00fa\u0301\u0372\u037f\u0381")
        buf.write("\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1\u3003\ud801")
        buf.write("\uf902\ufdd1\ufdf2\uffff\64\2\3\3\2\2\2\2\5\3\2\2\2\2")
        buf.write("\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\21\3\2\2\2\3\23")
        buf.write("\3\2\2\2\5\26\3\2\2\2\7\30\3\2\2\2\t\32\3\2\2\2\13\34")
        buf.write("\3\2\2\2\r\'\3\2\2\2\17)\3\2\2\2\21+\3\2\2\2\23\24\7\61")
        buf.write("\2\2\24\25\7\61\2\2\25\4\3\2\2\2\26\27\7\61\2\2\27\6\3")
        buf.write("\2\2\2\30\31\7,\2\2\31\b\3\2\2\2\32\33\7#\2\2\33\n\3\2")
        buf.write("\2\2\34 \5\17\b\2\35\37\5\r\7\2\36\35\3\2\2\2\37\"\3\2")
        buf.write("\2\2 \36\3\2\2\2 !\3\2\2\2!#\3\2\2\2\" \3\2\2\2#$\b\6")
        buf.write("\2\2$\f\3\2\2\2%(\5\17\b\2&(\t\2\2\2\'%\3\2\2\2\'&\3\2")
        buf.write("\2\2(\16\3\2\2\2)*\t\3\2\2*\20\3\2\2\2+/\7)\2\2,.\13\2")
        buf.write("\2\2-,\3\2\2\2.\61\3\2\2\2/\60\3\2\2\2/-\3\2\2\2\60\62")
        buf.write("\3\2\2\2\61/\3\2\2\2\62\63\7)\2\2\63\22\3\2\2\2\6\2 \'")
        buf.write("/\3\3\6\2")
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

    modeNames = [ "DEFAULT_MODE" ]

    literalNames = [ "<INVALID>",
            "'//'", "'/'", "'*'", "'!'" ]

    symbolicNames = [ "<INVALID>",
            "TOKEN_REF", "RULE_REF", "ANYWHERE", "ROOT", "WILDCARD", "BANG",
            "ID", "STRING" ]

    ruleNames = [ "ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar",
                  "NameStartChar", "STRING" ]

    grammarFileName = "XPathLexer.g4"

    def __init__(self, input=None):
        super().__init__(input)
        self.checkVersion("4.9.1")
        self._interp = LexerATNSimulator(self, self.atn, self.decisionsToDFA, PredictionContextCache())
        self._actions = None
        self._predicates = None


    def action(self, localctx:RuleContext, ruleIndex:int, actionIndex:int):
        if self._actions is None:
            actions = dict()
            actions[4] = self.ID_action
            self._actions = actions
        _action = self._actions.get(ruleIndex, None)
        if _action is not None:
            _action(localctx, actionIndex)
        else:
            raise Exception("No registered action for: %d" % ruleIndex)

    def ID_action(self, localctx:RuleContext , actionIndex:int):
        if actionIndex == 0:
            char = self.text[0]
            if char.isupper():
                self.type = XPathLexer.TOKEN_REF
            else:
                self.type = XPathLexer.RULE_REF

class XPath(object):

    WILDCARD = "*" # word not operator/separator
    NOT = "!" # word for invert operator

    def __init__(self, parser:Parser, path:str):
        self.parser = parser
        self.path = path
        self.elements = self.split(path)

    def split(self, path:str):
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
            pos = lexer.column
            msg = "Invalid tokens or characters at index %d in path '%s'" % (pos, path)
            raise Exception(msg, e)

        tokens = iter(tokenStream.tokens)
        elements = list()
        for el in tokens:
            invert = False
            anywhere = False
            # Check for path separators, if none assume root
            if el.type in [XPathLexer.ROOT, XPathLexer.ANYWHERE]:
                anywhere = el.type == XPathLexer.ANYWHERE
                next_el = next(tokens, None)
                if not next_el:
                    raise Exception('Missing element after %s' % el.getText())
                else:
                    el = next_el
            # Check for bangs
            if el.type == XPathLexer.BANG:
                invert = True
                next_el = next(tokens, None)
                if not next_el:
                    raise Exception('Missing element after %s' % el.getText())
                else:
                    el = next_el
            # Add searched element
            if el.type in [XPathLexer.TOKEN_REF, XPathLexer.RULE_REF, XPathLexer.WILDCARD, XPathLexer.STRING]:
                element = self.getXPathElement(el, anywhere)
                element.invert = invert
                elements.append(element)
            elif el.type==Token.EOF:
                break
            else:
                raise Exception("Unknown path element %s" % lexer.symbolicNames[el.type])
        return elements

    #
    # Convert word like {@code#} or {@code ID} or {@code expr} to a path
    # element. {@code anywhere} is {@code true} if {@code //} precedes the
    # word.
    #
    def getXPathElement(self, wordToken:Token, anywhere:bool):
        if wordToken.type==Token.EOF:
            raise Exception("Missing path element at end of path")

        word = wordToken.text
        if wordToken.type==XPathLexer.WILDCARD :
            return XPathWildcardAnywhereElement() if anywhere else XPathWildcardElement()

        elif wordToken.type in [XPathLexer.TOKEN_REF, XPathLexer.STRING]:
            tsource = self.parser.getTokenStream().tokenSource

            ttype = Token.INVALID_TYPE
            if wordToken.type == XPathLexer.TOKEN_REF:
                if word in tsource.ruleNames:
                    ttype = tsource.ruleNames.index(word) + 1
            else:
                if word in tsource.literalNames:
                    ttype = tsource.literalNames.index(word)

            if ttype == Token.INVALID_TYPE:
                raise Exception("%s at index %d isn't a valid token name" % (word, wordToken.tokenIndex))
            return XPathTokenAnywhereElement(word, ttype) if anywhere else XPathTokenElement(word, ttype)

        else:
            ruleIndex = self.parser.ruleNames.index(word) if word in self.parser.ruleNames else -1

            if ruleIndex == -1:
                raise Exception("%s at index %d isn't a valid rule name" % (word, wordToken.tokenIndex))
            return XPathRuleAnywhereElement(word, ruleIndex) if anywhere else XPathRuleElement(word, ruleIndex)


    @staticmethod
    def findAll(tree:ParseTree, xpath:str, parser:Parser):
        p = XPath(parser, xpath)
        return p.evaluate(tree)

    #
    # Return a list of all nodes starting at {@code t} as root that satisfy the
    # path. The root {@code /} is relative to the node passed to
    # {@link #evaluate}.
    #
    def evaluate(self, t:ParseTree):
        dummyRoot = ParserRuleContext()
        dummyRoot.children = [t] # don't set t's parent.

        work = [dummyRoot]
        for element in self.elements:
            work_next = list()
            for node in work:
                if not isinstance(node, TerminalNode) and node.children:
                    # only try to match next element if it has children
                    # e.g., //func/*/stat might have a token node for which
                    # we can't go looking for stat nodes.
                    matching = element.evaluate(node)

                    # See issue antlr#370 - Prevents XPath from returning the 
                    # same node multiple times
                    matching = filter(lambda m: m not in work_next, matching)
                    
                    work_next.extend(matching)
            work = work_next

        return work


class XPathElement(object):

    def __init__(self, nodeName:str):
        self.nodeName = nodeName
        self.invert = False

    def __str__(self):
        return type(self).__name__ + "[" + ("!" if self.invert else "") + self.nodeName + "]"



#
# Either {@code ID} at start of path or {@code ...//ID} in middle of path.
#
class XPathRuleAnywhereElement(XPathElement):

    def __init__(self, ruleName:str, ruleIndex:int):
        super().__init__(ruleName)
        self.ruleIndex = ruleIndex

    def evaluate(self, t:ParseTree):
        # return all ParserRuleContext descendants of t that match ruleIndex (or do not match if inverted)
        return filter(lambda c: isinstance(c, ParserRuleContext) and (self.invert ^ (c.getRuleIndex() == self.ruleIndex)), Trees.descendants(t))

class XPathRuleElement(XPathElement):

    def __init__(self, ruleName:str, ruleIndex:int):
        super().__init__(ruleName)
        self.ruleIndex = ruleIndex

    def evaluate(self, t:ParseTree):
        # return all ParserRuleContext children of t that match ruleIndex (or do not match if inverted)
        return filter(lambda c: isinstance(c, ParserRuleContext) and (self.invert ^ (c.getRuleIndex() == self.ruleIndex)), Trees.getChildren(t))

class XPathTokenAnywhereElement(XPathElement):

    def __init__(self, ruleName:str, tokenType:int):
        super().__init__(ruleName)
        self.tokenType = tokenType

    def evaluate(self, t:ParseTree):
        # return all TerminalNode descendants of t that match tokenType (or do not match if inverted)
        return filter(lambda c: isinstance(c, TerminalNode) and (self.invert ^ (c.symbol.type == self.tokenType)), Trees.descendants(t))

class XPathTokenElement(XPathElement):

    def __init__(self, ruleName:str, tokenType:int):
        super().__init__(ruleName)
        self.tokenType = tokenType

    def evaluate(self, t:ParseTree):
        # return all TerminalNode children of t that match tokenType (or do not match if inverted)
        return filter(lambda c: isinstance(c, TerminalNode) and (self.invert ^ (c.symbol.type == self.tokenType)), Trees.getChildren(t))


class XPathWildcardAnywhereElement(XPathElement):

    def __init__(self):
        super().__init__(XPath.WILDCARD)

    def evaluate(self, t:ParseTree):
        if self.invert:
            return list() # !* is weird but valid (empty)
        else:
            return Trees.descendants(t)


class XPathWildcardElement(XPathElement):

    def __init__(self):
        super().__init__(XPath.WILDCARD)


    def evaluate(self, t:ParseTree):
        if self.invert:
            return list() # !* is weird but valid (empty)
        else:
            return Trees.getChildren(t)
