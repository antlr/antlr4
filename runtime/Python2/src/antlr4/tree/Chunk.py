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

