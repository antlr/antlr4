#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#/

# A DFA walker that knows how to dump them to serialized strings.#/
from io import StringIO
from antlr4.Utils import str_list


class DFASerializer(object):

    def __init__(self, dfa, literalNames=None, symbolicNames=None):
        self.dfa = dfa
        self.literalNames = literalNames
        self.symbolicNames = symbolicNames

    def __str__(self):
        return unicode(self)

    def __unicode__(self):
        if self.dfa.s0 is None:
            return None
        with StringIO() as buf:
            for s in self.dfa.sortedStates():
                n = 0
                if s.edges is not None:
                    n = len(s.edges)
                for i in range(0, n):
                    t = s.edges[i]
                    if t is not None and t.stateNumber != 0x7FFFFFFF:
                        buf.write(self.getStateString(s))
                        label = self.getEdgeLabel(i)
                        buf.write(u"-")
                        buf.write(label)
                        buf.write(u"->")
                        buf.write(self.getStateString(t))
                        buf.write(u'\n')
            output = buf.getvalue()
            if len(output)==0:
                return None
            else:
                return output

    def getEdgeLabel(self, i):
        if i==0:
            return u"EOF"
        if self.literalNames is not None and i<=len(self.literalNames):
            return self.literalNames[i-1]
        elif self.symbolicNames is not None and i<=len(self.symbolicNames):
            return self.symbolicNames[i-1]
        else:
            return unicode(i-1)

    def getStateString(self, s):
        n = s.stateNumber
        baseStateStr = ( u":" if s.isAcceptState else u"") + u"s" + unicode(n) + \
            ( u"^" if s.requiresFullContext else u"")
        if s.isAcceptState:
            if s.predicates is not None:
                return baseStateStr + u"=>" + str_list(s.predicates)
            else:
                return baseStateStr + u"=>" + unicode(s.prediction)
        else:
            return baseStateStr

class LexerDFASerializer(DFASerializer):

    def __init__(self, dfa):
        super(LexerDFASerializer, self).__init__(dfa, None)

    def getEdgeLabel(self, i):
        return u"'" + unichr(i) + u"'"
