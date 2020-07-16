# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.

import unittest
from antlr4 import version

class TestVersion(unittest.TestCase):
    def testVersion(self):
        old = version.__version__
        try:
            version.__version__ = "1.2.3"
            self.assertEqual(version.major(), "1")
            self.assertEqual(version.minor(), "1.2")
            self.assertEqual(version.patch(), "1.2.3")
        finally:
            version.__version__ = old

if __name__ == '__main__':
    unittest.main()

