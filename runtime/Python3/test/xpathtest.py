import antlr4
from antlr4 import InputStream, CommonTokenStream, TerminalNode
from antlr4.xpath.XPath import XPath
import unittest
from expr.ExprParser import ExprParser
from expr.ExprLexer  import ExprLexer

def tokenToString(token, ruleNames):
    if isinstance(token, TerminalNode):
        return str(token)
    else:
        return ruleNames[token.getRuleIndex()]

class XPathTest(unittest.TestCase):
    def setUp(self):
        self.input_stream = InputStream(
            "def f(x,y) { x = 3+4; y; ; }\n"
            "def g(x) { return 1+2*x; }\n"
        )

        # Create the Token Stream
        self.lexer = ExprLexer(self.input_stream)
        self.stream = CommonTokenStream(self.lexer)
        self.stream.fill()

        # Create the parser and expression parse tree
        self.parser = ExprParser(self.stream)
        self.tree   = self.parser.prog()

    def testValidPaths(self):
        valid_paths = [
            "/prog/func",		 # all funcs under prog at root
            "/prog/*",			 # all children of prog at root
            "/*/func",			 # all func kids of any root node
            "prog",				 # prog must be root node
            "/prog",			 # prog must be root node
            "/*",				 # any root
            "*",				 # any root
            "//ID",				 # any ID in tree
            "//expr/primary/ID", # any ID child of a primary under any expr
            "//body//ID",		 # any ID under a body
            "//'return'",		 # any 'return' literal in tree, matched by literal name
            "//RETURN",			 # any 'return' literal in tree, matched by symbolic name
            "//primary/*",		 # all kids of any primary
            "//func/*/stat",	 # all stat nodes grandkids of any func node
            "/prog/func/'def'",	 # all def literal kids of func kid of prog
            "//stat/';'",		 # all ';' under any stat node
            "//expr/primary/!ID",# anything but ID under primary under any expr node
            "//expr/!primary",	 # anything but primary under any expr node
            "//!*",				 # nothing anywhere
            "/!*",				 # nothing at root
            "//expr//ID"		 # any ID under any expression (tests antlr/antlr4#370)
        ]

        expected_results = [
            "[func, func]",
            "[func, func]",
            "[func, func]",
            "[prog]",
            "[prog]",
            "[prog]",
            "[prog]",
            "[f, x, y, x, y, g, x, x]",
            "[y, x]",
            "[x, y, x]",
            "[return]",
            "[return]",
            "[3, 4, y, 1, 2, x]",
            "[stat, stat, stat, stat]",
            "[def, def]",
            "[;, ;, ;, ;]",
            "[3, 4, 1, 2]",
            "[expr, expr, expr, expr, expr, expr]",
            "[]",
            "[]",
            "[y, x]",
        ]

        for path, expected in zip(valid_paths, expected_results):
            # Build test string
            res = XPath.findAll(self.tree, path, self.parser)
            res_str = ", ".join([tokenToString(token, self.parser.ruleNames) for token in res])
            res_str = "[%s]" % res_str

            # Test against expected output
            self.assertEqual(res_str, expected, "Failed test %s" % path)

if __name__ == '__main__':
    unittest.main()