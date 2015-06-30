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

from antlr4.atn.ATNConfigSet import ATNConfigSet
from antlr4.dfa.DFAState import DFAState
from antlr4.error.Errors import IllegalStateException


class DFA(object):

    def __init__(self, atnStartState, decision=0):
        # From which ATN state did we create this DFA?
        self.atnStartState = atnStartState
        self.decision = decision
        # A set of all DFA states. Use {@link Map} so we can get old state back
        #  ({@link Set} only allows you to see if it's there).
        self._states = dict()
        self.s0 = None
        # {@code true} if this DFA is for a precedence decision; otherwise,
        # {@code false}. This is the backing field for {@link #isPrecedenceDfa},
        # {@link #setPrecedenceDfa}.
        self.precedenceDfa = False


    # Get the start state for a specific precedence value.
    #
    # @param precedence The current precedence.
    # @return The start state corresponding to the specified precedence, or
    # {@code null} if no start state exists for the specified precedence.
    #
    # @throws IllegalStateException if this is not a precedence DFA.
    # @see #isPrecedenceDfa()

    def getPrecedenceStartState(self, precedence):
        if not self.precedenceDfa:
            raise IllegalStateException("Only precedence DFAs may contain a precedence start state.")

        # s0.edges is never null for a precedence DFA
        if precedence < 0 or precedence >= len(self.s0.edges):
            return None
        return self.s0.edges[precedence]

    # Set the start state for a specific precedence value.
    #
    # @param precedence The current precedence.
    # @param startState The start state corresponding to the specified
    # precedence.
    #
    # @throws IllegalStateException if this is not a precedence DFA.
    # @see #isPrecedenceDfa()
    #
    def setPrecedenceStartState(self, precedence, startState):
        if not self.precedenceDfa:
            raise IllegalStateException("Only precedence DFAs may contain a precedence start state.")

        if precedence < 0:
            return

        # synchronization on s0 here is ok. when the DFA is turned into a
        # precedence DFA, s0 will be initialized once and not updated again
        # s0.edges is never null for a precedence DFA
        if precedence >= len(self.s0.edges):
            ext = [None] * (precedence + 1 - len(self.s0.edges))
            self.s0.edges.extend(ext)
        self.s0.edges[precedence] = startState
    #
    # Sets whether this is a precedence DFA. If the specified value differs
    # from the current DFA configuration, the following actions are taken;
    # otherwise no changes are made to the current DFA.
    #
    # <ul>
    # <li>The {@link #states} map is cleared</li>
    # <li>If {@code precedenceDfa} is {@code false}, the initial state
    # {@link #s0} is set to {@code null}; otherwise, it is initialized to a new
    # {@link DFAState} with an empty outgoing {@link DFAState#edges} array to
    # store the start states for individual precedence values.</li>
    # <li>The {@link #precedenceDfa} field is updated</li>
    # </ul>
    #
    # @param precedenceDfa {@code true} if this is a precedence DFA; otherwise,
    # {@code false}

    def setPrecedenceDfa(self, precedenceDfa):
        if self.precedenceDfa != precedenceDfa:
            self._states = dict()
            if precedenceDfa:
                precedenceState = DFAState(ATNConfigSet())
                precedenceState.edges = []
                precedenceState.isAcceptState = False
                precedenceState.requiresFullContext = False
                self.s0 = precedenceState
            else:
                self.s0 = None
            self.precedenceDfa = precedenceDfa

    @property
    def states(self):
        return self._states

    # Return a list of all states in this DFA, ordered by state number.
    def sortedStates(self):
        return sorted(self._states.keys(), key=lambda state: state.stateNumber)

    def __str__(self):
        return unicode(self)

    def __unicode__(self):
        return self.toString(None)

    def toString(self, literalNames=None, symbolicNames=None):
        if self.s0 is None:
            return ""
        from antlr4.dfa.DFASerializer import DFASerializer
        serializer = DFASerializer(self, literalNames, symbolicNames)
        return unicode(serializer)

    def toLexerString(self):
        if self.s0 is None:
            return ""
        from antlr4.dfa.DFASerializer import LexerDFASerializer
        serializer = LexerDFASerializer(self)
        return unicode(serializer)

