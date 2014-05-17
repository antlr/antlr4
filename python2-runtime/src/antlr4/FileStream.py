#
#  [The "BSD license"]
#   Copyright (c) 2012 Terence Parr
#   Copyright (c) 2012 Sam Harwell
#   Copyright (c) 2014 Eric Vergnaud
#   All rights reserved.
# 
#   Redistribution and use in source and binary forms, with or without
#   modification, are permitted provided that the following conditions
#   are met:
# 
#   1. Redistributions of source code must retain the above copyright
#      notice, this list of conditions and the following disclaimer.
#   2. Redistributions in binary form must reproduce the above copyright
#      notice, this list of conditions and the following disclaimer in the
#      documentation and/or other materials provided with the distribution.
#   3. The name of the author may not be used to endorse or promote products
#      derived from this software without specific prior written permission.
# 
#   THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
#   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
#   OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
#   IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
#   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
#   NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
#   THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
# 

#
#  This is an InputStream that is loaded from a file all at once
#  when you construct the object.
# 

import codecs
import unittest
from antlr4.InputStream import InputStream


class FileStream(InputStream):

    def __init__(self, fileName, encoding=None):
        self.fileName = fileName
        # read binary to avoid line ending conversion
        with open(fileName, 'rb') as file:
            bytes = file.read()
            data = codecs.decode(bytes)
            super(type(self), self).__init__(data)


class TestFileStream(unittest.TestCase):

    def testStream(self):
        stream = FileStream("FileStream.py")
        self.assertTrue(stream.size>0)