
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
from antlr4.atn.ATNSimulator import ATNSimulator
from antlr4.error.ErrorListener import ErrorListener
from antlr4.error.Errors import LexerNoViableAltException
from antlr4.tree.Tree import ParseTree
from antlr4.tree.Trees import Trees


class XPathLexer(Lexer):

    _serializedATN = \
        "\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\n\64\b\1\4\2\t\2" + \
        "\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3" + \
        "\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\7\6\37\n\6\f\6\16\6\"\13\6\3\6\3\6\3\7" + \
        "\3\7\5\7(\n\7\3\b\3\b\3\t\3\t\7\t.\n\t\f\t\16\t\61\13\t\3\t\3\t\3/\n\3" + \
        "\5\1\5\6\1\7\7\1\t\b\1\13\t\2\r\2\1\17\2\1\21\n\1\3\2\4\7\2\62;aa\u00b9" + \
        "\u00b9\u0302\u0371\u2041\u2042\17\2C\\c|\u00c2\u00d8\u00da\u00f8\u00fa" + \
        "\u0301\u0372\u037f\u0381\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1\u3003" + \
        "\ud801\uf902\ufdd1\ufdf2\uffff\64\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2" + \
        "\2\t\3\2\2\2\2\13\3\2\2\2\2\21\3\2\2\2\3\23\3\2\2\2\5\26\3\2\2\2\7\30" + \
        "\3\2\2\2\t\32\3\2\2\2\13\34\3\2\2\2\r\'\3\2\2\2\17)\3\2\2\2\21+\3\2\2" + \
        "\2\23\24\7\61\2\2\24\25\7\61\2\2\25\4\3\2\2\2\26\27\7\61\2\2\27\6\3\2" + \
        "\2\2\30\31\7,\2\2\31\b\3\2\2\2\32\33\7#\2\2\33\n\3\2\2\2\34 \5\17\b\2" + \
        "\35\37\5\r\7\2\36\35\3\2\2\2\37\"\3\2\2\2 \36\3\2\2\2 !\3\2\2\2!#\3\2" + \
        "\2\2\" \3\2\2\2#$\b\6\2\2$\f\3\2\2\2%(\5\17\b\2&(\t\2\2\2\'%\3\2\2\2\'" + \
        "&\3\2\2\2(\16\3\2\2\2)*\t\3\2\2*\20\3\2\2\2+/\7)\2\2,.\13\2\2\2-,\3\2" + \
        "\2\2.\61\3\2\2\2/\60\3\2\2\2/-\3\2\2\2\60\62\3\2\2\2\61/\3\2\2\2\62\63" + \
        "\7)\2\2\63\22\3\2\2\2\6\2 \'/"

    _ATN = ATNSimulator.deserialize(_serializedATN)

    _decisionToDFA = [ DFA(s) for s in _ATN.decisionToState ]

    _sharedContextCache = PredictionContextCache()

    TOKEN_REF=1
    RULE_REF=2
    ANYWHERE=3
    ROOT=4
    WILDCARD=5
    BANG=6
    ID=7
    STRING=8

    modeNames = [ "DEFAULT_MODE" ]

    tokenNames = ["<INVALID>", "TOKEN_REF", "RULE_REF", "'//'", "'/'", "'*'", "'!'", "ID", "STRING" ]

    ruleNames = [ "ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar", "NameStartChar", "STRING" ]

    def __init__(self, input):
        super(XPathLexer, self).__init__(input)
        self._interp = LexerATNSimulator(self, self._ATN, self._decisionToDFA, self._sharedContextCache)
        self.grammarFileName = "XPathLexer.g4"


    def action(self, localctx, ruleIndex, actionIndex):
        if ruleIndex==4:
            self.ID_action(localctx, actionIndex)

    def ID_action(self, localctx, actionIndex):
        if actionIndex==0:
            text = self.text
            if text[0].isuppercase():
                self._type = self.TOKEN_REF
            else:
                self._type = self.RULE_REF

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
            tokenStream.fill();
        except LexerNoViableAltException as e:
            pos = lexer.getColumn();
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
        nodes = []
        for c in Trees.getChildren(t):
            if isinstance(c, ParserRuleContext ):
                if (c.ruleIndex == self.ruleIndex ) == (not self.invert):
                    nodes.append(c)
        return nodes

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
        nodes = []
        for c in Trees.getChildren(t):
            if isinstance(c, TerminalNode):
                if (c.symbol.type == self.tokenType ) == (not self.invert):
                    nodes.append(c)
        return nodes

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