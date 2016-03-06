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
import unittest


# 
#  Vacuum all input from a string and then treat it like a buffer. 
#
from antlr4.Token import Token


class InputStream (object):
    
    def __init__(self, data: str):
        self.name = "<empty>"
        self.strdata = data
        self._loadString()

    def _loadString(self):    
        self._index = 0
        self.data = [ord(c) for c in self.strdata]
        self._size = len(self.data)

    @property
    def index(self):
        return self._index

    @property
    def size(self):
        return self._size

    # Reset the stream so that it's in the same state it was
    #  when the object was created *except* the data array is not
    #  touched.
    #
    def reset(self):
        self._index = 0

    def consume(self):
        if self._index >= self._size:
            assert self.LA(1) == Token.EOF
            raise Exception("cannot consume EOF")
        self._index += 1

    def LA(self, offset: int):
        if offset==0:
            return 0 # undefined
        if offset<0:
            offset += 1 # e.g., translate LA(-1) to use offset=0
        pos = self._index + offset - 1
        if pos < 0 or pos >= self._size: # invalid
            return Token.EOF
        return self.data[pos]

    def LT(self, offset: int):
        return self.LA(offset)

    # mark/release do nothing; we have entire buffer
    def mark(self):
        return -1

    def release(self, marker: int):
        pass

    # consume() ahead until p==_index; can't just set p=_index as we must
    # update line and column. If we seek backwards, just set p
    #
    def seek(self, _index: int):
        if _index<=self._index:
            self._index = _index # just jump; don't update stream state (line, ...)
            return
        # seek forward
        self._index = min(_index, self._size)

    def getText(self, start :int, stop: int):
        if stop >= self._size:
            stop = self._size-1
        if start >= self._size:
            return ""
        else:
            return self.strdata[start:stop+1]
    
    def __str__(self):
        return self.strdata


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
        
        