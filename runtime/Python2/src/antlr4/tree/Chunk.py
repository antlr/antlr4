#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#

class Chunk(object):

    def __str__(self):
        return unicode(self)


class TagChunk(Chunk):

    def __init__(self, tag, label=None):
        self.tag = tag
        self.label = label

    def __unicode__(self):
        if self.label is None:
            return self.tag
        else:
            return self.label + ":" + self.tag

class TextChunk(Chunk):

    def __init__(self, text):
        self.text = text

    def __unicode__(self):
        return "'" + self.text + "'"

