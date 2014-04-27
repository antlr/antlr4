class Chunk(object):
    pass

class TagChunk(Chunk):

    def __init__(self, tag:str, label:str=None):
        self.tag = tag
        self.label = label

    def __str__(self):
        if self.label is None:
            return self.tag
        else:
            return self.label + ":" + self.tag

class TextChunk(Chunk):

    def __init__(self, text:str):
        self.text = text

    def __str__(self):
        return "'" + self.text + "'"

