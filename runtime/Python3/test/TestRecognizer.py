import unittest
from antlr4.Recognizer import Recognizer


class TestRecognizer(unittest.TestCase):
    def testVersion(self):
        major, minor = Recognizer().extractVersion("1.2")
        self.assertEqual("1", major)
        self.assertEqual("2", minor)
        major, minor = Recognizer().extractVersion("1.2.3")
        self.assertEqual("1", major)
        self.assertEqual("2", minor)
        major, minor = Recognizer().extractVersion("1.2-snapshot")
        self.assertEqual("1", major)
        self.assertEqual("2", minor)
