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

#
# Specialized {@link Set}{@code <}{@link ATNConfig}{@code >} that can track
# info about the set, with support for combining similar configurations using a
# graph-structured stack.
#/
from io import StringIO
from antlr4.PredictionContext import PredictionContext, merge
from antlr4.Utils import str_list
from antlr4.atn.ATN import ATN
from antlr4.atn.SemanticContext import SemanticContext
from antlr4.error.Errors import UnsupportedOperationException, IllegalStateException

class ATNConfigSet(object):
    #
    # The reason that we need this is because we don't want the hash map to use
    # the standard hash code and equals. We need all configurations with the same
    # {@code (s,i,_,semctx)} to be equal. Unfortunately, this key effectively doubles
    # the number of objects associated with ATNConfigs. The other solution is to
    # use a hash table that lets us specify the equals/hashcode operation.

    def __init__(self, fullCtx=True):
        # All configs but hashed by (s, i, _, pi) not including context. Wiped out
        # when we go readonly as this set becomes a DFA state.
        self.configLookup = set()
        # Indicates that this configuration set is part of a full context
        #  LL prediction. It will be used to determine how to merge $. With SLL
        #  it's a wildcard whereas it is not for LL context merge.
        self.fullCtx = fullCtx
        # Indicates that the set of configurations is read-only. Do not
        #  allow any code to manipulate the set; DFA states will point at
        #  the sets and they must not change. This does not protect the other
        #  fields; in particular, conflictingAlts is set after
        #  we've made this readonly.
        self.readonly = False
        # Track the elements as they are added to the set; supports get(i)#/
        self.configs = []

        # TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
        # TODO: can we track conflicts as they are added to save scanning configs later?
        self.uniqueAlt = 0
        self.conflictingAlts = None

        # Used in parser and lexer. In lexer, it indicates we hit a pred
        # while computing a closure operation.  Don't make a DFA state from this.
        self.hasSemanticContext = False
        self.dipsIntoOuterContext = False

        self.cachedHashCode = -1

    def __iter__(self):
        return self.configs.__iter__()

    # Adding a new config means merging contexts with existing configs for
    # {@code (s, i, pi, _)}, where {@code s} is the
    # {@link ATNConfig#state}, {@code i} is the {@link ATNConfig#alt}, and
    # {@code pi} is the {@link ATNConfig#semanticContext}. We use
    # {@code (s,i,pi)} as key.
    #
    # <p>This method updates {@link #dipsIntoOuterContext} and
    # {@link #hasSemanticContext} when necessary.</p>
    #/
    def add(self, config, mergeCache=None):
        if self.readonly:
            raise Exception("This set is readonly")
        if config.semanticContext is not SemanticContext.NONE:
            self.hasSemanticContext = True
        if config.reachesIntoOuterContext > 0:
            self.dipsIntoOuterContext = True
        existing = self.getOrAdd(config)
        if existing is config:
            self.cachedHashCode = -1
            self.configs.append(config)  # track order here
            return True
        # a previous (s,i,pi,_), merge with it and save result
        rootIsWildcard = not self.fullCtx
        merged = merge(existing.context, config.context, rootIsWildcard, mergeCache)
        # no need to check for existing.context, config.context in cache
        # since only way to create new graphs is "call rule" and here. We
        # cache at both places.
        existing.reachesIntoOuterContext = max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext)
        # make sure to preserve the precedence filter suppression during the merge
        if config.precedenceFilterSuppressed:
            existing.precedenceFilterSuppressed = True
        existing.context = merged # replace context; no need to alt mapping
        return True

    def getOrAdd(self, config):
        for c in self.configLookup:
            if c==config:
                return c
        self.configLookup.add(config)
        return config

    def getStates(self):
        states = set()
        for c in self.configs:
            states.add(c.state)
        return states

    def getPredicates(self):
        preds = list()
        for c in self.configs:
            if c.semanticContext!=SemanticContext.NONE:
                preds.append(c.semanticContext)
        return preds

    def get(self, i):
        return self.configs[i]

    def optimizeConfigs(self, interpreter):
        if self.readonly:
            raise IllegalStateException("This set is readonly")
        if len(self.configLookup)==0:
            return
        for config in self.configs:
            config.context = interpreter.getCachedContext(config.context)

    def addAll(self, coll):
        for c in coll:
            self.add(c)
        return False

    def __eq__(self, other):
        if self is other:
            return True
        elif not isinstance(other, ATNConfigSet):
            return False

        same = self.configs is not None and \
            self.configs==other.configs and \
            self.fullCtx == other.fullCtx and \
            self.uniqueAlt == other.uniqueAlt and \
            self.conflictingAlts == other.conflictingAlts and \
            self.hasSemanticContext == other.hasSemanticContext and \
            self.dipsIntoOuterContext == other.dipsIntoOuterContext

        return same

    def __hash__(self):
        if self.readonly:
            if self.cachedHashCode == -1:
                self.cachedHashCode = self.hashConfigs()
            return self.cachedHashCode
        return self.hashConfigs()

    def hashConfigs(self):
        with StringIO() as buf:
            for cfg in self.configs:
                buf.write(unicode(cfg))
            return hash(buf.getvalue())

    def __len__(self):
        return len(self.configs)

    def isEmpty(self):
        return len(self.configs)==0

    def __contains__(self, item):
        if self.configLookup is None:
            raise UnsupportedOperationException("This method is not implemented for readonly sets.")
        return item in self.configLookup

    def containsFast(self, obj):
        if self.configLookup is None:
            raise UnsupportedOperationException("This method is not implemented for readonly sets.")
        return self.configLookup.containsFast(obj)


    def clear(self):
        if self.readonly:
            raise IllegalStateException("This set is readonly")
        self.configs.clear()
        self.cachedHashCode = -1
        self.configLookup.clear()

    def setReadonly(self, readonly):
        self.readonly = readonly
        self.configLookup = None # can't mod, no need for lookup cache

    def __str__(self):
        return unicode(self)

    def __unicode__(self):
        with StringIO() as buf:
            buf.write(str_list(self.configs))
            if self.hasSemanticContext:
                buf.write(u",hasSemanticContext=")
                buf.write(unicode(self.hasSemanticContext))
            if self.uniqueAlt!=ATN.INVALID_ALT_NUMBER:
                buf.write(u",uniqueAlt=")
                buf.write(unicode(self.uniqueAlt))
            if self.conflictingAlts is not None:
                buf.write(u",conflictingAlts=")
                buf.write(unicode(self.conflictingAlts))
            if self.dipsIntoOuterContext:
                buf.write(u",dipsIntoOuterContext")
            return buf.getvalue()


class OrderedATNConfigSet(ATNConfigSet):

    def __init__(self):
        super(OrderedATNConfigSet, self).__init__()
        # self.configLookup = set()



