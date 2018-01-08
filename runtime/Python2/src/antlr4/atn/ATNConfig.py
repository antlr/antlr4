#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#/

# A tuple: (ATN state, predicted alt, syntactic, semantic context).
#  The syntactic context is a graph-structured stack node whose
#  path(s) to the root is the rule invocation(s)
#  chain used to arrive at the state.  The semantic context is
#  the tree of semantic predicates encountered before reaching
#  an ATN state.
#/
from io import StringIO
from antlr4.atn.ATNState import ATNState, DecisionState
from antlr4.atn.SemanticContext import SemanticContext

class ATNConfig(object):

    def __init__(self, state=None, alt=None, context=None, semantic=None, config=None):
        if config is not None:
            if state is None:
                state = config.state
            if alt is None:
                alt = config.alt
            if context is None:
                context = config.context
            if semantic is None:
                semantic = config.semanticContext
        if semantic is None:
            semantic = SemanticContext.NONE

        # The ATN state associated with this configuration#/
        self.state = state
        # What alt (or lexer rule) is predicted by this configuration#/
        self.alt = alt
        # The stack of invoking states leading to the rule/states associated
        #  with this config.  We track only those contexts pushed during
        #  execution of the ATN simulator.
        self.context = context
        self.semanticContext = semantic
        # We cannot execute predicates dependent upon local context unless
        # we know for sure we are in the correct context. Because there is
        # no way to do this efficiently, we simply cannot evaluate
        # dependent predicates unless we are in the rule that initially
        # invokes the ATN simulator.
        #
        # closure() tracks the depth of how far we dip into the
        # outer context: depth &gt; 0.  Note that it may not be totally
        # accurate depth since I don't ever decrement. TODO: make it a boolean then
        self.reachesIntoOuterContext = 0 if config is None else config.reachesIntoOuterContext
        self.precedenceFilterSuppressed = False if config is None else config.precedenceFilterSuppressed

    # An ATN configuration is equal to another if both have
    #  the same state, they predict the same alternative, and
    #  syntactic/semantic contexts are the same.
    #/
    def __eq__(self, other):
        if self is other:
            return True
        elif not isinstance(other, ATNConfig):
            return False
        else:
            return self.state.stateNumber==other.state.stateNumber \
                and self.alt==other.alt \
                and ((self.context is other.context) or (self.context==other.context)) \
                and self.semanticContext==other.semanticContext \
                and self.precedenceFilterSuppressed==other.precedenceFilterSuppressed

    def __hash__(self):
        return hash((self.state.stateNumber, self.alt, self.context, self.semanticContext))

    def hashCodeForConfigSet(self):
        return hash((self.state.stateNumber, self.alt, hash(self.semanticContext)))

    def equalsForConfigSet(self, other):
        if self is other:
            return True
        elif not isinstance(other, ATNConfig):
            return False
        else:
            return self.state.stateNumber==other.state.stateNumber \
                and self.alt==other.alt \
                and self.semanticContext==other.semanticContext

    def __str__(self):
        return unicode(self)

    def __unicode__(self):
        with StringIO() as buf:
            buf.write(u"(")
            buf.write(unicode(self.state))
            buf.write(u",")
            buf.write(unicode(self.alt))
            if self.context is not None:
                buf.write(u",[")
                buf.write(unicode(self.context))
                buf.write(u"]")
            if self.semanticContext is not None and self.semanticContext is not SemanticContext.NONE:
                buf.write(u",")
                buf.write(unicode(self.semanticContext))
            if self.reachesIntoOuterContext>0:
                buf.write(u",up=")
                buf.write(unicode(self.reachesIntoOuterContext))
            buf.write(u')')
            return buf.getvalue()

class LexerATNConfig(ATNConfig):

    def __init__(self, state, alt=None, context=None, semantic=SemanticContext.NONE, lexerActionExecutor=None, config=None):
        super(LexerATNConfig, self).__init__(state=state, alt=alt, context=context, semantic=semantic, config=config)
        if config is not None:
            if lexerActionExecutor is None:
                lexerActionExecutor = config.lexerActionExecutor
        # This is the backing field for {@link #getLexerActionExecutor}.
        self.lexerActionExecutor = lexerActionExecutor
        self.passedThroughNonGreedyDecision = False if config is None else self.checkNonGreedyDecision(config, state)

    def __hash__(self):
        return hash((self.state.stateNumber, self.alt, self.context,
                self.semanticContext, self.passedThroughNonGreedyDecision,
                self.lexerActionExecutor))

    def __eq__(self, other):
        if self is other:
            return True
        elif not isinstance(other, LexerATNConfig):
            return False
        if self.passedThroughNonGreedyDecision != other.passedThroughNonGreedyDecision:
            return False
        if not(self.lexerActionExecutor==other.lexerActionExecutor):
            return False
        return super(LexerATNConfig, self).__eq__(other)



    def hashCodeForConfigSet(self):
        return hash(self)



    def equalsForConfigSet(self, other):
        return self==other



    def checkNonGreedyDecision(self, source, target):
        return source.passedThroughNonGreedyDecision \
            or isinstance(target, DecisionState) and target.nonGreedy
