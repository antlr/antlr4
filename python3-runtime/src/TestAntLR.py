import unittest
import sys

class TestAntLR(unittest.TestCase):

    def test(self):
        testDir = "/Users/ericvergnaud/Development/antlr4/antlr4-master/antlr4-python3-target/tmp"
        sys.path.append(testDir)
        from Test import main
        args = [ "", testDir + "/input"]
        main(args)
