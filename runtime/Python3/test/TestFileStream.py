import unittest
from antlr4.FileStream import FileStream


class TestFileStream(unittest.TestCase):
    def testStream(self):
        stream = FileStream(__file__)
        self.assertTrue(stream.size > 0)
