#
# [The "BSD license"]
#  Copyright (c) 2013 Terence Parr
#  Copyright (c) 2013 Sam Harwell
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
from antlr4.PredictionContext import PredictionContextCache, PredictionContext, getCachedPredictionContext
from antlr4.atn.ATN import ATN
from antlr4.atn.ATNConfigSet import ATNConfigSet
from antlr4.dfa.DFAState import DFAState


class ATNSimulator(object):

    # Must distinguish between missing edge and edge we know leads nowhere#/
    ERROR = DFAState(ATNConfigSet())
    ERROR.stateNumber = 0x7FFFFFFF

    # The context cache maps all PredictionContext objects that are ==
    #  to a single cached copy. This cache is shared across all contexts
    #  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
    #  to use only cached nodes/graphs in addDFAState(). We don't want to
    #  fill this during closure() since there are lots of contexts that
    #  pop up but are not used ever again. It also greatly slows down closure().
    #
    #  <p>This cache makes a huge difference in memory and a little bit in speed.
    #  For the Java grammar on java.*, it dropped the memory requirements
    #  at the end from 25M to 16M. We don't store any of the full context
    #  graphs in the DFA because they are limited to local context only,
    #  but apparently there's a lot of repetition there as well. We optimize
    #  the config contexts before storing the config set in the DFA states
    #  by literally rebuilding them with cached subgraphs only.</p>
    #
    #  <p>I tried a cache for use during closure operations, that was
    #  whacked after each adaptivePredict(). It cost a little bit
    #  more time I think and doesn't save on the overall footprint
    #  so it's not worth the complexity.</p>
    #/
    def __init__(self, atn:ATN, sharedContextCache:PredictionContextCache):
        self.atn = atn
        self.sharedContextCache = sharedContextCache

    def getCachedContext(self, context:PredictionContext):
        if self.sharedContextCache is None:
            return context
        visited = dict()
        return getCachedPredictionContext(context, self.sharedContextCache, visited)

