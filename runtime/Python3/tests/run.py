import sys
import os
src_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'src')
sys.path.insert(0,src_path)
from xpathtest import XPathTest
#from TestTokenStreamRewriter import TestTokenStreamRewriter
from TestFileStream import TestFileStream
from TestInputStream import TestInputStream
from TestIntervalSet import TestIntervalSet
from TestRecognizer import TestRecognizer
import unittest
unittest.main()
