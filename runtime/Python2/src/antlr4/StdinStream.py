#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#

#
#  This is an InputStream that is loaded from stdin all at once
#  when you construct the object.
#

import codecs
import sys
from antlr4.InputStream import InputStream

class StdinStream(InputStream):

	def __init__(self, encoding='ascii', errors='strict'):
		bytes = sys.stdin.read()
		data = codecs.decode(bytes, encoding, errors)
		super(type(self), self).__init__(data)
