import codecs
import sys

from antlr4.InputStream import InputStream


class StdinStream(InputStream):
    def __init__(self, encoding:str='ascii', errors:str='strict') -> None:
        bytes = sys.stdin.buffer.read()
        data = codecs.decode(bytes, encoding, errors)
        super().__init__(data)
