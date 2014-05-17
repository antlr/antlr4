#
# [The "BSD license"]
#  Copyright (c) 2012 Terence Parr
#  Copyright (c) 2012 Sam Harwell
#  Copyright (c) 2014 Eric Vergnaud
#  All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions
#  are met:
#
#  1. Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#  2. Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#  3. The name of the author may not be used to endorse or promote products
#     derived from this software without specific prior written permission.
#
#  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
#  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
#  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
#  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
#  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
#  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
#  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#/

# A DFA walker that knows how to dump them to serialized strings.#/
from io import StringIO
from antlr4.Utils import str_list


class DFASerializer(object):

    def __init__(self, dfa, tokenNames=None):
        self.dfa = dfa
        self.tokenNames = tokenNames

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
        if self.tokenNames is not None:
            return self.tokenNames[i-1]
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
