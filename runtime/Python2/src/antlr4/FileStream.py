#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
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

    def __init__(self, fileName, encoding='ascii', errors='strict'):
        self.fileName = fileName
        # read binary to avoid line ending conversion
        with open(fileName, 'rb') as file:
            bytes = file.read()
            data = codecs.decode(bytes, encoding, errors)
            super(type(self), self).__init__(data)


class TestFileStream(unittest.TestCase):

    def testStream(self):
        stream = FileStream("FileStream.py")
        self.assertTrue(stream.size>0)
