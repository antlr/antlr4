import unittest
from antlr4.Token import Token
from antlr4.InputStream import InputStream


class TestInputStream(unittest.TestCase):

    def testStream(self):
        stream = InputStream("abcde")
        self.assertEqual(0, stream.index)
        self.assertEqual(5, stream.size)
        self.assertEqual(ord("a"), stream.LA(1))
        stream.consume()
        self.assertEqual(1, stream.index)
        stream.seek(5)
        self.assertEqual(Token.EOF, stream.LA(1))
        self.assertEqual("bcd", stream.getText(1, 3))
        stream.reset()
        self.assertEqual(0, stream.index)
