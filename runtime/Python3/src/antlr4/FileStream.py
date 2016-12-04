#
# Copyright (c) 2012 The ANTLR Project Authors. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#

#
#  This is an InputStream that is loaded from a file all at once
#  when you construct the object.
#

import codecs
import unittest
from antlr4.InputStream import InputStream


class FileStream(InputStream):

    def __init__(self, fileName:str, encoding:str='ascii'):
        super().__init__(self.readDataFrom(fileName, encoding))
        self.fileName = fileName

    def readDataFrom(self, fileName:str, encoding:str):
        # read binary to avoid line ending conversion
        with open(fileName, 'rb') as file:
            bytes = file.read()
            return codecs.decode(bytes, encoding)


class TestFileStream(unittest.TestCase):

    def testStream(self):
        stream = FileStream(__file__)
        self.assertTrue(stream.size>0)