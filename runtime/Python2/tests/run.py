import sys
import os
src_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'src')
sys.path.insert(0,src_path)
from TestTokenStreamRewriter import TestTokenStreamRewriter
from versiontest import TestVersion
import unittest
unittest.main()
