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

#
# The embodiment of the adaptive LL(*), ALL(*), parsing strategy.
#
# <p>
# The basic complexity of the adaptive strategy makes it harder to understand.
# We begin with ATN simulation to build paths in a DFA. Subsequent prediction
# requests go through the DFA first. If they reach a state without an edge for
# the current symbol, the algorithm fails over to the ATN simulation to
# complete the DFA path for the current input (until it finds a conflict state
# or uniquely predicting state).</p>
#
# <p>
# All of that is done without using the outer context because we want to create
# a DFA that is not dependent upon the rule invocation stack when we do a
# prediction. One DFA works in all contexts. We avoid using context not
# necessarily because it's slower, although it can be, but because of the DFA
# caching problem. The closure routine only considers the rule invocation stack
# created during prediction beginning in the decision rule. For example, if
# prediction occurs without invoking another rule's ATN, there are no context
# stacks in the configurations. When lack of context leads to a conflict, we
# don't know if it's an ambiguity or a weakness in the strong LL(*) parsing
# strategy (versus full LL(*)).</p>
#
# <p>
# When SLL yields a configuration set with conflict, we rewind the input and
# retry the ATN simulation, this time using full outer context without adding
# to the DFA. Configuration context stacks will be the full invocation stacks
# from the start rule. If we get a conflict using full context, then we can
# definitively say we have a true ambiguity for that input sequence. If we
# don't get a conflict, it implies that the decision is sensitive to the outer
# context. (It is not context-sensitive in the sense of context-sensitive
# grammars.)</p>
#
# <p>
# The next time we reach this DFA state with an SLL conflict, through DFA
# simulation, we will again retry the ATN simulation using full context mode.
# This is slow because we can't save the results and have to "interpret" the
# ATN each time we get that input.</p>
#
# <p>
# <strong>CACHING FULL CONTEXT PREDICTIONS</strong></p>
#
# <p>
# We could cache results from full context to predicted alternative easily and
# that saves a lot of time but doesn't work in presence of predicates. The set
# of visible predicates from the ATN start state changes depending on the
# context, because closure can fall off the end of a rule. I tried to cache
# tuples (stack context, semantic context, predicted alt) but it was slower
# than interpreting and much more complicated. Also required a huge amount of
# memory. The goal is not to create the world's fastest parser anyway. I'd like
# to keep this algorithm simple. By launching multiple threads, we can improve
# the speed of parsing across a large number of files.</p>
#
# <p>
# There is no strict ordering between the amount of input used by SLL vs LL,
# which makes it really hard to build a cache for full context. Let's say that
# we have input A B C that leads to an SLL conflict with full context X. That
# implies that using X we might only use A B but we could also use A B C D to
# resolve conflict. Input A B C D could predict alternative 1 in one position
# in the input and A B C E could predict alternative 2 in another position in
# input. The conflicting SLL configurations could still be non-unique in the
# full context prediction, which would lead us to requiring more input than the
# original A B C.	To make a	prediction cache work, we have to track	the exact
# input	used during the previous prediction. That amounts to a cache that maps
# X to a specific DFA for that context.</p>
#
# <p>
# Something should be done for left-recursive expression predictions. They are
# likely LL(1) + pred eval. Easier to do the whole SLL unless error and retry
# with full LL thing Sam does.</p>
#
# <p>
# <strong>AVOIDING FULL CONTEXT PREDICTION</strong></p>
#
# <p>
# We avoid doing full context retry when the outer context is empty, we did not
# dip into the outer context by falling off the end of the decision state rule,
# or when we force SLL mode.</p>
#
# <p>
# As an example of the not dip into outer context case, consider as super
# constructor calls versus function calls. One grammar might look like
# this:</p>
#
# <pre>
# ctorBody
#   : '{' superCall? stat* '}'
#   ;
# </pre>
#
# <p>
# Or, you might see something like</p>
#
# <pre>
# stat
#   : superCall ';'
#   | expression ';'
#   | ...
#   ;
# </pre>
#
# <p>
# In both cases I believe that no closure operations will dip into the outer
# context. In the first case ctorBody in the worst case will stop at the '}'.
# In the 2nd case it should stop at the ';'. Both cases should stay within the
# entry rule and not dip into the outer context.</p>
#
# <p>
# <strong>PREDICATES</strong></p>
#
# <p>
# Predicates are always evaluated if present in either SLL or LL both. SLL and
# LL simulation deals with predicates differently. SLL collects predicates as
# it performs closure operations like ANTLR v3 did. It delays predicate
# evaluation until it reaches and accept state. This allows us to cache the SLL
# ATN simulation whereas, if we had evaluated predicates on-the-fly during
# closure, the DFA state configuration sets would be different and we couldn't
# build up a suitable DFA.</p>
#
# <p>
# When building a DFA accept state during ATN simulation, we evaluate any
# predicates and return the sole semantically valid alternative. If there is
# more than 1 alternative, we report an ambiguity. If there are 0 alternatives,
# we throw an exception. Alternatives without predicates act like they have
# true predicates. The simple way to think about it is to strip away all
# alternatives with false predicates and choose the minimum alternative that
# remains.</p>
#
# <p>
# When we start in the DFA and reach an accept state that's predicated, we test
# those and return the minimum semantically viable alternative. If no
# alternatives are viable, we throw an exception.</p>
#
# <p>
# During full LL ATN simulation, closure always evaluates predicates and
# on-the-fly. This is crucial to reducing the configuration set size during
# closure. It hits a landmine when parsing with the Java grammar, for example,
# without this on-the-fly evaluation.</p>
#
# <p>
# <strong>SHARING DFA</strong></p>
#
# <p>
# All instances of the same parser share the same decision DFAs through a
# static field. Each instance gets its own ATN simulator but they share the
# same {@link #decisionToDFA} field. They also share a
# {@link PredictionContextCache} object that makes sure that all
# {@link PredictionContext} objects are shared among the DFA states. This makes
# a big size difference.</p>
#
# <p>
# <strong>THREAD SAFETY</strong></p>
#
# <p>
# The {@link ParserATNSimulator} locks on the {@link #decisionToDFA} field when
# it adds a new DFA object to that array. {@link #addDFAEdge}
# locks on the DFA for the current decision when setting the
# {@link DFAState#edges} field. {@link #addDFAState} locks on
# the DFA for the current decision when looking up a DFA state to see if it
# already exists. We must make sure that all requests to add DFA states that
# are equivalent result in the same shared DFA object. This is because lots of
# threads will be trying to update the DFA at once. The
# {@link #addDFAState} method also locks inside the DFA lock
# but this time on the shared context cache when it rebuilds the
# configurations' {@link PredictionContext} objects using cached
# subgraphs/nodes. No other locking occurs, even during DFA simulation. This is
# safe as long as we can guarantee that all threads referencing
# {@code s.edge[t]} get the same physical target {@link DFAState}, or
# {@code null}. Once into the DFA, the DFA simulation does not reference the
# {@link DFA#states} map. It follows the {@link DFAState#edges} field to new
# targets. The DFA simulator will either find {@link DFAState#edges} to be
# {@code null}, to be non-{@code null} and {@code dfa.edges[t]} null, or
# {@code dfa.edges[t]} to be non-null. The
# {@link #addDFAEdge} method could be racing to set the field
# but in either case the DFA simulator works; if {@code null}, and requests ATN
# simulation. It could also race trying to get {@code dfa.edges[t]}, but either
# way it will work because it's not doing a test and set operation.</p>
#
# <p>
# <strong>Starting with SLL then failing to combined SLL/LL (Two-Stage
# Parsing)</strong></p>
#
# <p>
# Sam pointed out that if SLL does not give a syntax error, then there is no
# point in doing full LL, which is slower. We only have to try LL if we get a
# syntax error. For maximum speed, Sam starts the parser set to pure SLL
# mode with the {@link BailErrorStrategy}:</p>
#
# <pre>
# parser.{@link Parser#getInterpreter() getInterpreter()}.{@link #setPredictionMode setPredictionMode}{@code (}{@link PredictionMode#SLL}{@code )};
# parser.{@link Parser#setErrorHandler setErrorHandler}(new {@link BailErrorStrategy}());
# </pre>
#
# <p>
# If it does not get a syntax error, then we're done. If it does get a syntax
# error, we need to retry with the combined SLL/LL strategy.</p>
#
# <p>
# The reason this works is as follows. If there are no SLL conflicts, then the
# grammar is SLL (at least for that input set). If there is an SLL conflict,
# the full LL analysis must yield a set of viable alternatives which is a
# subset of the alternatives reported by SLL. If the LL set is a singleton,
# then the grammar is LL but not SLL. If the LL set is the same size as the SLL
# set, the decision is SLL. If the LL set has size &gt; 1, then that decision
# is truly ambiguous on the current input. If the LL set is smaller, then the
# SLL conflict resolution might choose an alternative that the full LL would
# rule out as a possibility based upon better context information. If that's
# the case, then the SLL parse will definitely get an error because the full LL
# analysis says it's not viable. If SLL conflict resolution chooses an
# alternative within the LL set, them both SLL and LL would choose the same
# alternative because they both choose the minimum of multiple conflicting
# alternatives.</p>
#
# <p>
# Let's say we have a set of SLL conflicting alternatives {@code {1, 2, 3}} and
# a smaller LL set called <em>s</em>. If <em>s</em> is {@code {2, 3}}, then SLL
# parsing will get an error because SLL will pursue alternative 1. If
# <em>s</em> is {@code {1, 2}} or {@code {1, 3}} then both SLL and LL will
# choose the same alternative because alternative one is the minimum of either
# set. If <em>s</em> is {@code {2}} or {@code {3}} then SLL will get a syntax
# error. If <em>s</em> is {@code {1}} then SLL will succeed.</p>
#
# <p>
# Of course, if the input is invalid, then we will get an error for sure in
# both SLL and LL parsing. Erroneous input will therefore require 2 passes over
# the input.</p>
#
import sys
from antlr4 import DFA
from antlr4.PredictionContext import PredictionContextCache, PredictionContext, SingletonPredictionContext, \
    PredictionContextFromRuleContext
from antlr4.BufferedTokenStream import TokenStream
from antlr4.Parser import Parser
from antlr4.ParserRuleContext import ParserRuleContext
from antlr4.RuleContext import RuleContext
from antlr4.Token import Token
from antlr4.Utils import str_list
from antlr4.atn.ATN import ATN
from antlr4.atn.ATNConfig import ATNConfig
from antlr4.atn.ATNConfigSet import ATNConfigSet
from antlr4.atn.ATNSimulator import ATNSimulator
from antlr4.atn.ATNState import StarLoopEntryState, DecisionState, RuleStopState, ATNState
from antlr4.atn.PredictionMode import PredictionMode
from antlr4.atn.SemanticContext import SemanticContext, AND, andContext, orContext
from antlr4.atn.Transition import Transition, RuleTransition, ActionTransition, PrecedencePredicateTransition, \
    PredicateTransition, AtomTransition, SetTransition, NotSetTransition
from antlr4.dfa.DFAState import DFAState, PredPrediction
from antlr4.error.Errors import NoViableAltException


class ParserATNSimulator(ATNSimulator):

    debug = False
    debug_list_atn_decisions = False
    dfa_debug = False
    retry_debug = False


    def __init__(self, parser:Parser, atn:ATN, decisionToDFA:list, sharedContextCache:PredictionContextCache):
        super().__init__(atn, sharedContextCache)
        self.parser = parser
        self.decisionToDFA = decisionToDFA
        # SLL, LL, or LL + exact ambig detection?#
        self.predictionMode = PredictionMode.LL
        # LAME globals to avoid parameters!!!!! I need these down deep in predTransition
        self._input = None
        self._startIndex = 0
        self._outerContext = None
        self._dfa = None
        # Each prediction operation uses a cache for merge of prediction contexts.
        #  Don't keep around as it wastes huge amounts of memory. DoubleKeyMap
        #  isn't synchronized but we're ok since two threads shouldn't reuse same
        #  parser/atnsim object because it can only handle one input at a time.
        #  This maps graphs a and b to merged result c. (a,b)&rarr;c. We can avoid
        #  the merge if we ever see a and b again.  Note that (b,a)&rarr;c should
        #  also be examined during cache lookup.
        #
        self.mergeCache = None


    def reset(self):
        pass

    def adaptivePredict(self, input:TokenStream, decision:int, outerContext:ParserRuleContext):
        if self.debug or self.debug_list_atn_decisions:
            print("adaptivePredict decision " + str(decision) +
                                   " exec LA(1)==" + self.getLookaheadName(input) +
                                   " line " + str(input.LT(1).line) + ":" +
                                   str(input.LT(1).column))
        self._input = input
        self._startIndex = input.index
        self._outerContext = outerContext
        
        dfa = self.decisionToDFA[decision]
        self._dfa = dfa
        m = input.mark()
        index = input.index

        # Now we are certain to have a specific decision's DFA
        # But, do we still need an initial state?
        try:
            if dfa.precedenceDfa:
                # the start state for a precedence DFA depends on the current
                # parser precedence, and is provided by a DFA method.
                s0 = dfa.getPrecedenceStartState(self.parser.getPrecedence())
            else:
                # the start state for a "regular" DFA is just s0
                s0 = dfa.s0

            if s0 is None:
                if outerContext is None:
                    outerContext = ParserRuleContext.EMPTY
                if self.debug or self.debug_list_atn_decisions:
                    print("predictATN decision " + str(dfa.decision) +
                                       " exec LA(1)==" + self.getLookaheadName(input) +
                                       ", outerContext=" + outerContext.toString(self.parser))

                # If this is not a precedence DFA, we check the ATN start state
                # to determine if this ATN start state is the decision for the
                # closure block that determines whether a precedence rule
                # should continue or complete.
                #
                if not dfa.precedenceDfa and isinstance(dfa.atnStartState, StarLoopEntryState):
                    if dfa.atnStartState.precedenceRuleDecision:
                        dfa.setPrecedenceDfa(True)

                fullCtx = False
                s0_closure = self.computeStartState(dfa.atnStartState, ParserRuleContext.EMPTY, fullCtx)

                if dfa.precedenceDfa:
                    # If this is a precedence DFA, we use applyPrecedenceFilter
                    # to convert the computed start state to a precedence start
                    # state. We then use DFA.setPrecedenceStartState to set the
                    # appropriate start state for the precedence level rather
                    # than simply setting DFA.s0.
                    #
                    s0_closure = self.applyPrecedenceFilter(s0_closure)
                    s0 = self.addDFAState(dfa, DFAState(configs=s0_closure))
                    dfa.setPrecedenceStartState(self.parser.getPrecedence(), s0)
                else:
                    s0 = self.addDFAState(dfa, DFAState(configs=s0_closure))
                    dfa.s0 = s0

            alt = self.execATN(dfa, s0, input, index, outerContext)
            if self.debug:
                print("DFA after predictATN: " + dfa.toString(self.parser.tokenNames))
            return alt
        finally:
            self._dfa = None
            self.mergeCache = None # wack cache after each prediction
            input.seek(index)
            input.release(m)

    # Performs ATN simulation to compute a predicted alternative based
    #  upon the remaining input, but also updates the DFA cache to avoid
    #  having to traverse the ATN again for the same input sequence.

    # There are some key conditions we're looking for after computing a new
    # set of ATN configs (proposed DFA state):
          # if the set is empty, there is no viable alternative for current symbol
          # does the state uniquely predict an alternative?
          # does the state have a conflict that would prevent us from
          #   putting it on the work list?

    # We also have some key operations to do:
          # add an edge from previous DFA state to potentially new DFA state, D,
          #   upon current symbol but only if adding to work list, which means in all
          #   cases except no viable alternative (and possibly non-greedy decisions?)
          # collecting predicates and adding semantic context to DFA accept states
          # adding rule context to context-sensitive DFA accept states
          # consuming an input symbol
          # reporting a conflict
          # reporting an ambiguity
          # reporting a context sensitivity
          # reporting insufficient predicates

    # cover these cases:
    #    dead end
    #    single alt
    #    single alt + preds
    #    conflict
    #    conflict + preds
    #
    def execATN(self, dfa:DFA, s0:DFAState, input:TokenStream, startIndex:int, outerContext:ParserRuleContext ):
        if self.debug or self.debug_list_atn_decisions:
            print("execATN decision " + str(dfa.decision) +
                    " exec LA(1)==" + self.getLookaheadName(input) +
                    " line " + str(input.LT(1).line) + ":" + str(input.LT(1).column))

        previousD = s0

        if self.debug:
            print("s0 = " + str(s0))

        t = input.LA(1)

        while True: # while more work
            D = self.getExistingTargetState(previousD, t)
            if D is None:
                D = self.computeTargetState(dfa, previousD, t)
            if D is self.ERROR:
                # if any configs in previous dipped into outer context, that
                # means that input up to t actually finished entry rule
                # at least for SLL decision. Full LL doesn't dip into outer
                # so don't need special case.
                # We will get an error no matter what so delay until after
                # decision; better error message. Also, no reachable target
                # ATN states in SLL implies LL will also get nowhere.
                # If conflict in states that dip out, choose min since we
                # will get error no matter what.
                e = self.noViableAlt(input, outerContext, previousD.configs, startIndex)
                input.seek(startIndex)
                alt = self.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previousD.configs, outerContext)
                if alt!=ATN.INVALID_ALT_NUMBER:
                    return alt
                raise e

            if D.requiresFullContext and self.predictionMode != PredictionMode.SLL:
                # IF PREDS, MIGHT RESOLVE TO SINGLE ALT => SLL (or syntax error)
                conflictingAlts = None
                if D.predicates is not None:
                    if self.debug:
                        print("DFA state has preds in DFA sim LL failover")
                    conflictIndex = input.index
                    if conflictIndex != startIndex:
                        input.seek(startIndex)

                    conflictingAlts = self.evalSemanticContext(D.predicates, outerContext, True)
                    if len(conflictingAlts)==1:
                        if self.debug:
                            print("Full LL avoided")
                        return min(conflictingAlts)

                    if conflictIndex != startIndex:
                        # restore the index so reporting the fallback to full
                        # context occurs with the index at the correct spot
                        input.seek(conflictIndex)

                if self.dfa_debug:
                    print("ctx sensitive state " + str(outerContext) +" in " + str(D))
                fullCtx = True
                s0_closure = self.computeStartState(dfa.atnStartState, outerContext, fullCtx)
                self.reportAttemptingFullContext(dfa, conflictingAlts, D.configs, startIndex, input.index)
                alt = self.execATNWithFullContext(dfa, D, s0_closure, input, startIndex, outerContext)
                return alt

            if D.isAcceptState:
                if D.predicates is None:
                    return D.prediction

                stopIndex = input.index
                input.seek(startIndex)
                alts = self.evalSemanticContext(D.predicates, outerContext, True)
                if len(alts)==0:
                    raise self.noViableAlt(input, outerContext, D.configs, startIndex)
                elif len(alts)==1:
                    return min(alts)
                else:
                    # report ambiguity after predicate evaluation to make sure the correct
                    # set of ambig alts is reported.
                    self.reportAmbiguity(dfa, D, startIndex, stopIndex, False, alts, D.configs)
                    return min(alts)

            previousD = D

            if t != Token.EOF:
                input.consume()
                t = input.LA(1)

    #
    # Get an existing target state for an edge in the DFA. If the target state
    # for the edge has not yet been computed or is otherwise not available,
    # this method returns {@code null}.
    #
    # @param previousD The current DFA state
    # @param t The next input symbol
    # @return The existing target DFA state for the given input symbol
    # {@code t}, or {@code null} if the target state for this edge is not
    # already cached
    #
    def getExistingTargetState(self, previousD:DFAState, t:int):
        edges = previousD.edges
        if edges is None or t + 1 < 0 or t + 1 >= len(edges):
            return None
        else:
            return edges[t + 1]

    #
    # Compute a target state for an edge in the DFA, and attempt to add the
    # computed state and corresponding edge to the DFA.
    #
    # @param dfa The DFA
    # @param previousD The current DFA state
    # @param t The next input symbol
    #
    # @return The computed target DFA state for the given input symbol
    # {@code t}. If {@code t} does not lead to a valid DFA state, this method
    # returns {@link #ERROR}.
    #
    def computeTargetState(self, dfa:DFA, previousD:DFAState, t:int):
        reach = self.computeReachSet(previousD.configs, t, False)
        if reach is None:
            self.addDFAEdge(dfa, previousD, t, self.ERROR)
            return self.ERROR

        # create new target state; we'll add to DFA after it's complete
        D = DFAState(configs=reach)

        predictedAlt = self.getUniqueAlt(reach)

        if self.debug:
            altSubSets = PredictionMode.getConflictingAltSubsets(reach)
            print("SLL altSubSets=" + str(altSubSets) + ", configs=" + str(reach) +
                        ", predict=" + str(predictedAlt) + ", allSubsetsConflict=" +
                        str(PredictionMode.allSubsetsConflict(altSubSets)) + ", conflictingAlts=" +
                        str(self.getConflictingAlts(reach)))

        if predictedAlt!=ATN.INVALID_ALT_NUMBER:
            # NO CONFLICT, UNIQUELY PREDICTED ALT
            D.isAcceptState = True
            D.configs.uniqueAlt = predictedAlt
            D.prediction = predictedAlt
        elif PredictionMode.hasSLLConflictTerminatingPrediction(self.predictionMode, reach):
            # MORE THAN ONE VIABLE ALTERNATIVE
            D.configs.conflictingAlts = self.getConflictingAlts(reach)
            D.requiresFullContext = True
            # in SLL-only mode, we will stop at this state and return the minimum alt
            D.isAcceptState = True
            D.prediction = min(D.configs.conflictingAlts)

        if D.isAcceptState and D.configs.hasSemanticContext:
            self.predicateDFAState(D, self.atn.getDecisionState(dfa.decision))
            if D.predicates is not None:
                D.prediction = ATN.INVALID_ALT_NUMBER

        # all adds to dfa are done after we've created full D state
        D = self.addDFAEdge(dfa, previousD, t, D)
        return D

    def predicateDFAState(self, dfaState:DFAState, decisionState:DecisionState):
        # We need to test all predicates, even in DFA states that
        # uniquely predict alternative.
        nalts = len(decisionState.transitions)
        # Update DFA so reach becomes accept state with (predicate,alt)
        # pairs if preds found for conflicting alts
        altsToCollectPredsFrom = self.getConflictingAltsOrUniqueAlt(dfaState.configs)
        altToPred = self.getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts)
        if altToPred is not None:
            dfaState.predicates = self.getPredicatePredictions(altsToCollectPredsFrom, altToPred)
            dfaState.prediction = ATN.INVALID_ALT_NUMBER # make sure we use preds
        else:
            # There are preds in configs but they might go away
            # when OR'd together like {p}? || NONE == NONE. If neither
            # alt has preds, resolve to min alt
            dfaState.prediction = min(altsToCollectPredsFrom)

    # comes back with reach.uniqueAlt set to a valid alt
    def execATNWithFullContext(self, dfa:DFA, D:DFAState, # how far we got before failing over
                                         s0:ATNConfigSet,
                                         input:TokenStream,
                                         startIndex:int,
                                         outerContext:ParserRuleContext):
        if self.debug or self.debug_list_atn_decisions:
            print("execATNWithFullContext "+s0)
        fullCtx = True
        foundExactAmbig = False
        reach = None
        previous = s0
        input.seek(startIndex)
        t = input.LA(1)
        predictedAlt = -1
        while (True): # while more work
            reach = self.computeReachSet(previous, t, fullCtx)
            if reach is None:
                # if any configs in previous dipped into outer context, that
                # means that input up to t actually finished entry rule
                # at least for LL decision. Full LL doesn't dip into outer
                # so don't need special case.
                # We will get an error no matter what so delay until after
                # decision; better error message. Also, no reachable target
                # ATN states in SLL implies LL will also get nowhere.
                # If conflict in states that dip out, choose min since we
                # will get error no matter what.
                e = self.noViableAlt(input, outerContext, previous, startIndex)
                input.seek(startIndex)
                alt = self.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previous, outerContext)
                if alt!=ATN.INVALID_ALT_NUMBER:
                    return alt
                else:
                    raise e

            altSubSets = PredictionMode.getConflictingAltSubsets(reach)
            if self.debug:
                print("LL altSubSets=" + str(altSubSets) + ", predict=" +
                      str(PredictionMode.getUniqueAlt(altSubSets)) + ", resolvesToJustOneViableAlt=" +
                      str(PredictionMode.resolvesToJustOneViableAlt(altSubSets)))

            reach.uniqueAlt = self.getUniqueAlt(reach)
            # unique prediction?
            if reach.uniqueAlt!=ATN.INVALID_ALT_NUMBER:
                predictedAlt = reach.uniqueAlt
                break
            elif self.predictionMode is not PredictionMode.LL_EXACT_AMBIG_DETECTION:
                predictedAlt = PredictionMode.resolvesToJustOneViableAlt(altSubSets)
                if predictedAlt != ATN.INVALID_ALT_NUMBER:
                    break
            else:
                # In exact ambiguity mode, we never try to terminate early.
                # Just keeps scarfing until we know what the conflict is
                if PredictionMode.allSubsetsConflict(altSubSets) and PredictionMode.allSubsetsEqual(altSubSets):
                    foundExactAmbig = True
                    predictedAlt = PredictionMode.getSingleViableAlt(altSubSets)
                    break
                # else there are multiple non-conflicting subsets or
                # we're not sure what the ambiguity is yet.
                # So, keep going.

            previous = reach
            if t != Token.EOF:
                input.consume()
                t = input.LA(1)

        # If the configuration set uniquely predicts an alternative,
        # without conflict, then we know that it's a full LL decision
        # not SLL.
        if reach.uniqueAlt != ATN.INVALID_ALT_NUMBER :
            self.reportContextSensitivity(dfa, predictedAlt, reach, startIndex, input.index)
            return predictedAlt

        # We do not check predicates here because we have checked them
        # on-the-fly when doing full context prediction.

        #
        # In non-exact ambiguity detection mode, we might	actually be able to
        # detect an exact ambiguity, but I'm not going to spend the cycles
        # needed to check. We only emit ambiguity warnings in exact ambiguity
        # mode.
        #
        # For example, we might know that we have conflicting configurations.
        # But, that does not mean that there is no way forward without a
        # conflict. It's possible to have nonconflicting alt subsets as in:

        # altSubSets=[{1, 2}, {1, 2}, {1}, {1, 2}]

        # from
        #
        #    [(17,1,[5 $]), (13,1,[5 10 $]), (21,1,[5 10 $]), (11,1,[$]),
        #     (13,2,[5 10 $]), (21,2,[5 10 $]), (11,2,[$])]
        #
        # In this case, (17,1,[5 $]) indicates there is some next sequence that
        # would resolve this without conflict to alternative 1. Any other viable
        # next sequence, however, is associated with a conflict.  We stop
        # looking for input because no amount of further lookahead will alter
        # the fact that we should predict alternative 1.  We just can't say for
        # sure that there is an ambiguity without looking further.

        self.reportAmbiguity(dfa, D, startIndex, input.index, foundExactAmbig, None, reach)

        return predictedAlt

    def computeReachSet(self, closure:ATNConfigSet, t:int, fullCtx:bool):
        if self.debug:
            print("in computeReachSet, starting closure: " + str(closure))

        if self.mergeCache is None:
            self.mergeCache = dict()

        intermediate = ATNConfigSet(fullCtx)

        # Configurations already in a rule stop state indicate reaching the end
        # of the decision rule (local context) or end of the start rule (full
        # context). Once reached, these configurations are never updated by a
        # closure operation, so they are handled separately for the performance
        # advantage of having a smaller intermediate set when calling closure.
        #
        # For full-context reach operations, separate handling is required to
        # ensure that the alternative matching the longest overall sequence is
        # chosen when multiple such configurations can match the input.
        
        skippedStopStates = None

        # First figure out where we can reach on input t
        for c in closure:
            if self.debug:
                print("testing " + self.getTokenName(t) + " at " + str(c))

            if isinstance(c.state, RuleStopState):
                assert c.context.isEmpty()
                if fullCtx or t == Token.EOF:
                    if skippedStopStates is None:
                        skippedStopStates = list()
                    skippedStopStates.append(c)
                continue

            for trans in c.state.transitions:
                target = self.getReachableTarget(trans, t)
                if target is not None:
                    intermediate.add(ATNConfig(state=target, config=c), self.mergeCache)

        # Now figure out where the reach operation can take us...

        reach = None

        # This block optimizes the reach operation for intermediate sets which
        # trivially indicate a termination state for the overall
        # adaptivePredict operation.
        #
        # The conditions assume that intermediate
        # contains all configurations relevant to the reach set, but this
        # condition is not true when one or more configurations have been
        # withheld in skippedStopStates, or when the current symbol is EOF.
        #
        if skippedStopStates is None and t!=Token.EOF:
            if len(intermediate)==1:
                # Don't pursue the closure if there is just one state.
                # It can only have one alternative; just add to result
                # Also don't pursue the closure if there is unique alternative
                # among the configurations.
                reach = intermediate
            elif self.getUniqueAlt(intermediate)!=ATN.INVALID_ALT_NUMBER:
                # Also don't pursue the closure if there is unique alternative
                # among the configurations.
                reach = intermediate

        # If the reach set could not be trivially determined, perform a closure
        # operation on the intermediate set to compute its initial value.
        #
        if reach is None:
            reach = ATNConfigSet(fullCtx)
            closureBusy = set()
            treatEofAsEpsilon = t == Token.EOF
            for c in intermediate:
                self.closure(c, reach, closureBusy, False, fullCtx, treatEofAsEpsilon)

        if t == Token.EOF:
            # After consuming EOF no additional input is possible, so we are
            # only interested in configurations which reached the end of the
            # decision rule (local context) or end of the start rule (full
            # context). Update reach to contain only these configurations. This
            # handles both explicit EOF transitions in the grammar and implicit
            # EOF transitions following the end of the decision or start rule.
            #
            # When reach==intermediate, no closure operation was performed. In
            # this case, removeAllConfigsNotInRuleStopState needs to check for
            # reachable rule stop states as well as configurations already in
            # a rule stop state.
            #
            # This is handled before the configurations in skippedStopStates,
            # because any configurations potentially added from that list are
            # already guaranteed to meet this condition whether or not it's
            # required.
            #
            reach = self.removeAllConfigsNotInRuleStopState(reach, reach is intermediate)

        # If skippedStopStates is not null, then it contains at least one
        # configuration. For full-context reach operations, these
        # configurations reached the end of the start rule, in which case we
        # only add them back to reach if no configuration during the current
        # closure operation reached such a state. This ensures adaptivePredict
        # chooses an alternative matching the longest overall sequence when
        # multiple alternatives are viable.
        #
        if skippedStopStates is not None and ( (not fullCtx) or (not PredictionMode.hasConfigInRuleStopState(reach))):
            assert len(skippedStopStates)>0
            for c in skippedStopStates:
                reach.add(c, self.mergeCache)
        if len(reach)==0:
            return None
        else:
            return reach

    #
    # Return a configuration set containing only the configurations from
    # {@code configs} which are in a {@link RuleStopState}. If all
    # configurations in {@code configs} are already in a rule stop state, this
    # method simply returns {@code configs}.
    #
    # <p>When {@code lookToEndOfRule} is true, this method uses
    # {@link ATN#nextTokens} for each configuration in {@code configs} which is
    # not already in a rule stop state to see if a rule stop state is reachable
    # from the configuration via epsilon-only transitions.</p>
    #
    # @param configs the configuration set to update
    # @param lookToEndOfRule when true, this method checks for rule stop states
    # reachable by epsilon-only transitions from each configuration in
    # {@code configs}.
    #
    # @return {@code configs} if all configurations in {@code configs} are in a
    # rule stop state, otherwise return a new configuration set containing only
    # the configurations from {@code configs} which are in a rule stop state
    #
    def removeAllConfigsNotInRuleStopState(self, configs:ATNConfigSet, lookToEndOfRule:bool):
        if PredictionMode.allConfigsInRuleStopStates(configs):
            return configs
        result = ATNConfigSet(configs.fullCtx)
        for config in configs:
            if isinstance(config.state, RuleStopState):
                result.add(config, self.mergeCache)
                continue
            if lookToEndOfRule and config.state.epsilonOnlyTransitions:
                nextTokens = self.atn.nextTokens(config.state)
                if Token.EPSILON in nextTokens:
                    endOfRuleState = self.atn.ruleToStopState[config.state.ruleIndex]
                    result.add(ATNConfig(state=endOfRuleState, config=config), self.mergeCache)
        return result

    def computeStartState(self, p:ATNState, ctx:RuleContext, fullCtx:bool):
        # always at least the implicit call to start rule
        initialContext = PredictionContextFromRuleContext(self.atn, ctx)
        configs = ATNConfigSet(fullCtx)

        for i in range(0, len(p.transitions)):
            target = p.transitions[i].target
            c = ATNConfig(target, i+1, initialContext)
            closureBusy = set()
            self.closure(c, configs, closureBusy, True, fullCtx, False)
        return configs

    #
    # This method transforms the start state computed by
    # {@link #computeStartState} to the special start state used by a
    # precedence DFA for a particular precedence value. The transformation
    # process applies the following changes to the start state's configuration
    # set.
    #
    # <ol>
    # <li>Evaluate the precedence predicates for each configuration using
    # {@link SemanticContext#evalPrecedence}.</li>
    # <li>Remove all configurations which predict an alternative greater than
    # 1, for which another configuration that predicts alternative 1 is in the
    # same ATN state with the same prediction context. This transformation is
    # valid for the following reasons:
    # <ul>
    # <li>The closure block cannot contain any epsilon transitions which bypass
    # the body of the closure, so all states reachable via alternative 1 are
    # part of the precedence alternatives of the transformed left-recursive
    # rule.</li>
    # <li>The "primary" portion of a left recursive rule cannot contain an
    # epsilon transition, so the only way an alternative other than 1 can exist
    # in a state that is also reachable via alternative 1 is by nesting calls
    # to the left-recursive rule, with the outer calls not being at the
    # preferred precedence level.</li>
    # </ul>
    # </li>
    # </ol>
    #
    # <p>
    # The prediction context must be considered by this filter to address
    # situations like the following.
    # </p>
    # <code>
    # <pre>
    # grammar TA;
    # prog: statement* EOF;
    # statement: letterA | statement letterA 'b' ;
    # letterA: 'a';
    # </pre>
    # </code>
    # <p>
    # If the above grammar, the ATN state immediately before the token
    # reference {@code 'a'} in {@code letterA} is reachable from the left edge
    # of both the primary and closure blocks of the left-recursive rule
    # {@code statement}. The prediction context associated with each of these
    # configurations distinguishes between them, and prevents the alternative
    # which stepped out to {@code prog} (and then back in to {@code statement}
    # from being eliminated by the filter.
    # </p>
    #
    # @param configs The configuration set computed by
    # {@link #computeStartState} as the start state for the DFA.
    # @return The transformed configuration set representing the start state
    # for a precedence DFA at a particular precedence level (determined by
    # calling {@link Parser#getPrecedence}).
    #
    def applyPrecedenceFilter(self, configs:ATNConfigSet):
        statesFromAlt1 = dict()
        configSet = ATNConfigSet(configs.fullCtx)
        for config in configs:
            # handle alt 1 first
            if config.alt != 1:
                continue
            updatedContext = config.semanticContext.evalPrecedence(self.parser, self._outerContext)
            if updatedContext is None:
                # the configuration was eliminated
                continue

            statesFromAlt1[config.state.stateNumber] = config.context
            if updatedContext is not config.semanticContext:
                configSet.add(ATNConfig(config=config, semantic=updatedContext), self.mergeCache)
            else:
                configSet.add(config, self.mergeCache)

        for config in configs:
            if config.alt == 1:
                # already handled
                continue

            # In the future, this elimination step could be updated to also
            # filter the prediction context for alternatives predicting alt>1
            # (basically a graph subtraction algorithm).
            #
            if not config.precedenceFilterSuppressed:
                context = statesFromAlt1.get(config.state.stateNumber, None)
                if context==config.context:
                    # eliminated
                    continue

            configSet.add(config, self.mergeCache)

        return configSet

    def getReachableTarget(self, trans:Transition, ttype:int):
        if trans.matches(ttype, 0, self.atn.maxTokenType):
            return trans.target
        else:
            return None

    def getPredsForAmbigAlts(self, ambigAlts:set, configs:ATNConfigSet, nalts:int):
        # REACH=[1|1|[]|0:0, 1|2|[]|0:1]
        # altToPred starts as an array of all null contexts. The entry at index i
        # corresponds to alternative i. altToPred[i] may have one of three values:
        #   1. null: no ATNConfig c is found such that c.alt==i
        #   2. SemanticContext.NONE: At least one ATNConfig c exists such that
        #      c.alt==i and c.semanticContext==SemanticContext.NONE. In other words,
        #      alt i has at least one unpredicated config.
        #   3. Non-NONE Semantic Context: There exists at least one, and for all
        #      ATNConfig c such that c.alt==i, c.semanticContext!=SemanticContext.NONE.
        #
        # From this, it is clear that NONE||anything==NONE.
        #
        altToPred = [None] * (nalts + 1)
        for c in configs:
            if c.alt in ambigAlts:
                altToPred[c.alt] = orContext(altToPred[c.alt], c.semanticContext)

        nPredAlts = 0
        for i in range(1, nalts+1):
            if altToPred[i] is None:
                altToPred[i] = SemanticContext.NONE
            elif altToPred[i] is not SemanticContext.NONE:
                nPredAlts += 1

        # nonambig alts are null in altToPred
        if nPredAlts==0:
            altToPred = None
        if self.debug:
            print("getPredsForAmbigAlts result " + str_list(altToPred))
        return altToPred

    def getPredicatePredictions(self, ambigAlts:set, altToPred:list):
        pairs = []
        containsPredicate = False
        for i in range(1, len(altToPred)):
            pred = altToPred[i]
            # unpredicated is indicated by SemanticContext.NONE
            assert pred is not None
            if ambigAlts is not None and i in ambigAlts:
                pairs.append(PredPrediction(pred, i))
            if pred is not SemanticContext.NONE:
                containsPredicate = True

        if not containsPredicate:
            return None

        return pairs

    #
    # This method is used to improve the localization of error messages by
    # choosing an alternative rather than throwing a
    # {@link NoViableAltException} in particular prediction scenarios where the
    # {@link #ERROR} state was reached during ATN simulation.
    #
    # <p>
    # The default implementation of this method uses the following
    # algorithm to identify an ATN configuration which successfully parsed the
    # decision entry rule. Choosing such an alternative ensures that the
    # {@link ParserRuleContext} returned by the calling rule will be complete
    # and valid, and the syntax error will be reported later at a more
    # localized location.</p>
    #
    # <ul>
    # <li>If a syntactically valid path or paths reach the end of the decision rule and
    # they are semantically valid if predicated, return the min associated alt.</li>
    # <li>Else, if a semantically invalid but syntactically valid path exist
    # or paths exist, return the minimum associated alt.
    # </li>
    # <li>Otherwise, return {@link ATN#INVALID_ALT_NUMBER}.</li>
    # </ul>
    #
    # <p>
    # In some scenarios, the algorithm described above could predict an
    # alternative which will result in a {@link FailedPredicateException} in
    # the parser. Specifically, this could occur if the <em>only</em> configuration
    # capable of successfully parsing to the end of the decision rule is
    # blocked by a semantic predicate. By choosing this alternative within
    # {@link #adaptivePredict} instead of throwing a
    # {@link NoViableAltException}, the resulting
    # {@link FailedPredicateException} in the parser will identify the specific
    # predicate which is preventing the parser from successfully parsing the
    # decision rule, which helps developers identify and correct logic errors
    # in semantic predicates.
    # </p>
    #
    # @param configs The ATN configurations which were valid immediately before
    # the {@link #ERROR} state was reached
    # @param outerContext The is the \gamma_0 initial parser context from the paper
    # or the parser stack at the instant before prediction commences.
    #
    # @return The value to return from {@link #adaptivePredict}, or
    # {@link ATN#INVALID_ALT_NUMBER} if a suitable alternative was not
    # identified and {@link #adaptivePredict} should report an error instead.
    #
    def getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(self, configs:ATNConfigSet, outerContext:ParserRuleContext):
        semValidConfigs, semInvalidConfigs = self.splitAccordingToSemanticValidity(configs, outerContext)
        alt = self.getAltThatFinishedDecisionEntryRule(semValidConfigs)
        if alt!=ATN.INVALID_ALT_NUMBER: # semantically/syntactically viable path exists
            return alt
        # Is there a syntactically valid path with a failed pred?
        if len(semInvalidConfigs)>0:
            alt = self.getAltThatFinishedDecisionEntryRule(semInvalidConfigs)
            if alt!=ATN.INVALID_ALT_NUMBER: # syntactically viable path exists
                return alt
        return ATN.INVALID_ALT_NUMBER

    def getAltThatFinishedDecisionEntryRule(self, configs:ATNConfigSet):
        alts = set()
        for c in configs:
            if c.reachesIntoOuterContext>0 or (isinstance(c.state, RuleStopState) and c.context.hasEmptyPath() ):
                alts.add(c.alt)
        if len(alts)==0:
            return ATN.INVALID_ALT_NUMBER
        else:
            return min(alts)

    # Walk the list of configurations and split them according to
    #  those that have preds evaluating to true/false.  If no pred, assume
    #  true pred and include in succeeded set.  Returns Pair of sets.
    #
    #  Create a new set so as not to alter the incoming parameter.
    #
    #  Assumption: the input stream has been restored to the starting point
    #  prediction, which is where predicates need to evaluate.
    #
    def splitAccordingToSemanticValidity(self, configs:ATNConfigSet, outerContext:ParserRuleContext):
        succeeded = ATNConfigSet(configs.fullCtx)
        failed = ATNConfigSet(configs.fullCtx)
        for c in configs:
            if c.semanticContext is not SemanticContext.NONE:
                predicateEvaluationResult = c.semanticContext.eval(self.parser, outerContext)
                if predicateEvaluationResult:
                    succeeded.add(c)
                else:
                    failed.add(c)
            else:
                succeeded.add(c)
        return (succeeded,failed)

    # Look through a list of predicate/alt pairs, returning alts for the
    #  pairs that win. A {@code NONE} predicate indicates an alt containing an
    #  unpredicated config which behaves as "always true." If !complete
    #  then we stop at the first predicate that evaluates to true. This
    #  includes pairs with null predicates.
    #
    def evalSemanticContext(self, predPredictions:list, outerContext:ParserRuleContext, complete:bool):
        predictions = set()
        for pair in predPredictions:
            if pair.pred is SemanticContext.NONE:
                predictions.add(pair.alt)
                if not complete:
                    break
                continue
            predicateEvaluationResult = pair.pred.eval(self.parser, outerContext)
            if self.debug or self.dfa_debug:
                print("eval pred " + str(pair) + "=" + str(predicateEvaluationResult))

            if predicateEvaluationResult:
                if self.debug or self.dfa_debug:
                    print("PREDICT " + str(pair.alt))
                predictions.add(pair.alt)
                if not complete:
                    break
        return predictions


    # TODO: If we are doing predicates, there is no point in pursuing
    #     closure operations if we reach a DFA state that uniquely predicts
    #     alternative. We will not be caching that DFA state and it is a
    #     waste to pursue the closure. Might have to advance when we do
    #     ambig detection thought :(
    #

    def closure(self, config:ATNConfig, configs:ATNConfigSet, closureBusy:set, collectPredicates:bool, fullCtx:bool, treatEofAsEpsilon:bool):
        initialDepth = 0
        self.closureCheckingStopState(config, configs, closureBusy, collectPredicates,
                                 fullCtx, initialDepth, treatEofAsEpsilon)
        assert not fullCtx or not configs.dipsIntoOuterContext


    def closureCheckingStopState(self, config:ATNConfig, configs:ATNConfigSet, closureBusy:set, collectPredicates:bool, fullCtx:bool, depth:int, treatEofAsEpsilon:bool):
        if self.debug:
            print("closure(" + config.toString(self.parser,True) + ")")

        if isinstance(config.state, RuleStopState):
            # We hit rule end. If we have context info, use it
            # run thru all possible stack tops in ctx
            if not config.context.isEmpty():
                for i in range(0, len(config.context)):
                    if config.context.getReturnState(i) is PredictionContext.EMPTY_RETURN_STATE:
                        if fullCtx:
                            configs.add(ATNConfig(state=config.state, context=PredictionContext.EMPTY, config=config), self.mergeCache)
                            continue
                        else:
                            # we have no context info, just chase follow links (if greedy)
                            if self.debug:
                                print("FALLING off rule " + self.getRuleName(config.state.ruleIndex))
                            self.closure_(config, configs, closureBusy, collectPredicates,
                                     fullCtx, depth, treatEofAsEpsilon)
                        continue
                    returnState = self.atn.states[config.context.getReturnState(i)]
                    newContext = config.context.getParent(i) # "pop" return state
                    c = ATNConfig(state=returnState, alt=config.alt, context=newContext, semantic=config.semanticContext)
                    # While we have context to pop back from, we may have
                    # gotten that context AFTER having falling off a rule.
                    # Make sure we track that we are now out of context.
                    c.reachesIntoOuterContext = config.reachesIntoOuterContext
                    assert depth > - 2**63
                    self.closureCheckingStopState(c, configs, closureBusy, collectPredicates, fullCtx, depth - 1, treatEofAsEpsilon)
                return
            elif fullCtx:
                # reached end of start rule
                configs.add(config, self.mergeCache)
                return
            else:
                # else if we have no context info, just chase follow links (if greedy)
                if self.debug:
                    print("FALLING off rule " + self.getRuleName(config.state.ruleIndex))

        self.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon)

    # Do the actual work of walking epsilon edges#
    def closure_(self, config:ATNConfig, configs:ATNConfigSet, closureBusy:set, collectPredicates:bool, fullCtx:bool, depth:int, treatEofAsEpsilon:bool):
        p = config.state
        # optimization
        if not p.epsilonOnlyTransitions:
            configs.add(config, self.mergeCache)
            # make sure to not return here, because EOF transitions can act as
            # both epsilon transitions and non-epsilon transitions.

        for t in p.transitions:
            continueCollecting = collectPredicates and not isinstance(t, ActionTransition)
            c = self.getEpsilonTarget(config, t, continueCollecting, depth == 0, fullCtx, treatEofAsEpsilon)
            if c is not None:
                if not t.isEpsilon:
                    if c in closureBusy:
                        # avoid infinite recursion for EOF* and EOF+
                        continue
                    closureBusy.add(c)
                newDepth = depth
                if isinstance( config.state, RuleStopState):
                    assert not fullCtx
                    # target fell off end of rule; mark resulting c as having dipped into outer context
                    # We can't get here if incoming config was rule stop and we had context
                    # track how far we dip into outer context.  Might
                    # come in handy and we avoid evaluating context dependent
                    # preds if this is > 0.

                    if c in closureBusy:
                        # avoid infinite recursion for right-recursive rules
                        continue
                    closureBusy.add(c)

                    if self._dfa is not None and self._dfa.precedenceDfa:
                        if t.outermostPrecedenceReturn == self._dfa.atnStartState.ruleIndex:
                            c.precedenceFilterSuppressed = True
                    c.reachesIntoOuterContext += 1
                    configs.dipsIntoOuterContext = True # TODO: can remove? only care when we add to set per middle of this method
                    assert newDepth > - 2**63
                    newDepth -= 1
                    if self.debug:
                        print("dips into outer ctx: " + str(c))
                elif isinstance(t, RuleTransition):
                    # latch when newDepth goes negative - once we step out of the entry context we can't return
                    if newDepth >= 0:
                        newDepth += 1

                self.closureCheckingStopState(c, configs, closureBusy, continueCollecting, fullCtx, newDepth, treatEofAsEpsilon)

    def getRuleName(self, index:int):
        if self.parser is not None and index>=0:
            return self.parser.ruleNames[index]
        else:
            return "<rule " + str(index) + ">"

    def getEpsilonTarget(self, config:ATNConfig, t:Transition, collectPredicates:bool, inContext:bool, fullCtx:bool, treatEofAsEpsilon:bool):
        tt = t.serializationType
        if tt==Transition.RULE:
            return self.ruleTransition(config, t)
        elif tt==Transition.PRECEDENCE:
            return self.precedenceTransition(config, t, collectPredicates, inContext, fullCtx)
        elif tt==Transition.PREDICATE:
            return self.predTransition(config, t, collectPredicates, inContext, fullCtx)
        elif tt==Transition.ACTION:
            return self.actionTransition(config, t)
        elif tt==Transition.EPSILON:
            return ATNConfig(state=t.target, config=config)
        elif tt in [ Transition.ATOM, Transition.RANGE, Transition.SET ]:
            # EOF transitions act like epsilon transitions after the first EOF
            # transition is traversed
            if treatEofAsEpsilon:
                if t.matches(Token.EOF, 0, 1):
                    return ATNConfig(state=t.target, config=config)
            return None

        else:
            return None

    def actionTransition(self, config:ATNConfig, t:ActionTransition):
        if self.debug:
            print("ACTION edge " + str(t.ruleIndex) + ":" + str(t.actionIndex))
        return ATNConfig(state=t.target, config=config)

    def precedenceTransition(self, config:ATNConfig, pt:PrecedencePredicateTransition,  collectPredicates:bool, inContext:bool, fullCtx:bool):
        if self.debug:
            print("PRED (collectPredicates=" + str(collectPredicates) + ") " +
                    str(pt.precedence) + ">=_p, ctx dependent=true")
            if self.parser is not None:
                print("context surrounding pred is " + str(self.parser.getRuleInvocationStack()))

        c = None
        if collectPredicates and inContext:
            if fullCtx:
                # In full context mode, we can evaluate predicates on-the-fly
                # during closure, which dramatically reduces the size of
                # the config sets. It also obviates the need to test predicates
                # later during conflict resolution.
                currentPosition = self._input.index
                self._input.seek(self._startIndex)
                predSucceeds = pt.getPredicate().eval(self.parser, self._outerContext)
                self._input.seek(currentPosition)
                if predSucceeds:
                    c = ATNConfig(state=pt.target, config=config) # no pred context
            else:
                newSemCtx = andContext(config.semanticContext, pt.getPredicate())
                c = ATNConfig(state=pt.target, semantic=newSemCtx, config=config)
        else:
            c = ATNConfig(state=pt.target, config=config)

        if self.debug:
            print("config from pred transition=" + str(c))
        return c

    def predTransition(self, config:ATNConfig, pt:PredicateTransition, collectPredicates:bool, inContext:bool, fullCtx:bool):
        if self.debug:
            print("PRED (collectPredicates=" + str(collectPredicates) + ") " + str(pt.ruleIndex) +
                    ":" + str(pt.predIndex) + ", ctx dependent=" + str(pt.isCtxDependent))
            if self.parser is not None:
                print("context surrounding pred is " + str(self.parser.getRuleInvocationStack()))

        c = None
        if collectPredicates and (not pt.isCtxDependent or (pt.isCtxDependent and inContext)):
            if fullCtx:
                # In full context mode, we can evaluate predicates on-the-fly
                # during closure, which dramatically reduces the size of
                # the config sets. It also obviates the need to test predicates
                # later during conflict resolution.
                currentPosition = self._input.index
                self._input.seek(self._startIndex)
                predSucceeds = pt.getPredicate().eval(self.parser, self._outerContext)
                self._input.seek(currentPosition)
                if predSucceeds:
                    c = ATNConfig(state=pt.target, config=config) # no pred context
            else:
                newSemCtx = andContext(config.semanticContext, pt.getPredicate())
                c = ATNConfig(state=pt.target, semantic=newSemCtx, config=config)
        else:
            c = ATNConfig(state=pt.target, config=config)

        if self.debug:
            print("config from pred transition=" + str(c))
        return c

    def ruleTransition(self, config:ATNConfig, t:RuleTransition):
        if self.debug:
            print("CALL rule " + self.getRuleName(t.target.ruleIndex) + ", ctx=" + str(config.context))
        returnState = t.followState
        newContext = SingletonPredictionContext.create(config.context, returnState.stateNumber)
        return ATNConfig(state=t.target, context=newContext, config=config )

    def getConflictingAlts(self, configs:ATNConfigSet):
        altsets = PredictionMode.getConflictingAltSubsets(configs)
        return PredictionMode.getAlts(altsets)

     # Sam pointed out a problem with the previous definition, v3, of
     # ambiguous states. If we have another state associated with conflicting
     # alternatives, we should keep going. For example, the following grammar
     #
     # s : (ID | ID ID?) ';' ;
     #
     # When the ATN simulation reaches the state before ';', it has a DFA
     # state that looks like: [12|1|[], 6|2|[], 12|2|[]]. Naturally
     # 12|1|[] and 12|2|[] conflict, but we cannot stop processing this node
     # because alternative to has another way to continue, via [6|2|[]].
     # The key is that we have a single state that has config's only associated
     # with a single alternative, 2, and crucially the state transitions
     # among the configurations are all non-epsilon transitions. That means
     # we don't consider any conflicts that include alternative 2. So, we
     # ignore the conflict between alts 1 and 2. We ignore a set of
     # conflicting alts when there is an intersection with an alternative
     # associated with a single alt state in the state&rarr;config-list map.
     #
     # It's also the case that we might have two conflicting configurations but
     # also a 3rd nonconflicting configuration for a different alternative:
     # [1|1|[], 1|2|[], 8|3|[]]. This can come about from grammar:
     #
     # a : A | A | A B ;
     #
     # After matching input A, we reach the stop state for rule A, state 1.
     # State 8 is the state right before B. Clearly alternatives 1 and 2
     # conflict and no amount of further lookahead will separate the two.
     # However, alternative 3 will be able to continue and so we do not
     # stop working on this state. In the previous example, we're concerned
     # with states associated with the conflicting alternatives. Here alt
     # 3 is not associated with the conflicting configs, but since we can continue
     # looking for input reasonably, I don't declare the state done. We
     # ignore a set of conflicting alts when we have an alternative
     # that we still need to pursue.
    #

    def getConflictingAltsOrUniqueAlt(self, configs:ATNConfigSet):
        conflictingAlts = None
        if configs.uniqueAlt!= ATN.INVALID_ALT_NUMBER:
            conflictingAlts = set()
            conflictingAlts.add(configs.uniqueAlt)
        else:
            conflictingAlts = configs.conflictingAlts
        return conflictingAlts

    def getTokenName(self, t:int):
        if t==Token.EOF:
            return "EOF"
        if self.parser is not None and self.parser.tokenNames is not None:
            if t >= len(self.parser.tokenNames):
                print(str(t) + " ttype out of range: " + str_list(self.parser.tokenNames))
                print(str_list(self.parser.getInputStream().getTokens()))
            else:
                return self.parser.tokensNames[t] + "<" + str(t) + ">"
        return str(t)

    def getLookaheadName(self, input:TokenStream):
        return self.getTokenName(input.LA(1))

    # Used for debugging in adaptivePredict around execATN but I cut
    #  it out for clarity now that alg. works well. We can leave this
    #  "dead" code for a bit.
    #
    def dumpDeadEndConfigs(self, nvae:NoViableAltException):
        print("dead end configs: ")
        for c in nvae.getDeadEndConfigs():
            trans = "no edges"
            if len(c.state.transitions)>0:
                t = c.state.transitions[0]
                if isinstance(t, AtomTransition):
                    trans = "Atom "+ self.getTokenName(t.label)
                elif isinstance(t, SetTransition):
                    neg = isinstance(t, NotSetTransition)
                    trans = ("~" if neg else "")+"Set "+ str(t.set)
            print(c.toString(self.parser, True) + ":" + trans, file=sys.stderr)

    def noViableAlt(self, input:TokenStream, outerContext:ParserRuleContext, configs:ATNConfigSet, startIndex:int):
        return NoViableAltException(self.parser, input, input.get(startIndex), input.LT(1), configs, outerContext)

    def getUniqueAlt(self, configs:ATNConfigSet):
        alt = ATN.INVALID_ALT_NUMBER
        for c in configs:
            if alt == ATN.INVALID_ALT_NUMBER:
                alt = c.alt # found first alt
            elif c.alt!=alt:
                return ATN.INVALID_ALT_NUMBER
        return alt

    #
    # Add an edge to the DFA, if possible. This method calls
    # {@link #addDFAState} to ensure the {@code to} state is present in the
    # DFA. If {@code from} is {@code null}, or if {@code t} is outside the
    # range of edges that can be represented in the DFA tables, this method
    # returns without adding the edge to the DFA.
    #
    # <p>If {@code to} is {@code null}, this method returns {@code null}.
    # Otherwise, this method returns the {@link DFAState} returned by calling
    # {@link #addDFAState} for the {@code to} state.</p>
    #
    # @param dfa The DFA
    # @param from The source state for the edge
    # @param t The input symbol
    # @param to The target state for the edge
    #
    # @return If {@code to} is {@code null}, this method returns {@code null};
    # otherwise this method returns the result of calling {@link #addDFAState}
    # on {@code to}
    #
    def addDFAEdge(self, dfa:DFA, from_:DFAState, t:int, to:DFAState):
        if self.debug:
            print("EDGE " + str(from_) + " -> " + str(to) + " upon " + self.getTokenName(t))

        if to is None:
            return None

        to = self.addDFAState(dfa, to) # used existing if possible not incoming
        if from_ is None or t < -1 or t > self.atn.maxTokenType:
            return to

        if from_.edges is None:
            from_.edges = [None] * (self.atn.maxTokenType + 2)
        from_.edges[t+1] = to # connect

        if self.debug:
            names = None if self.parser is None else self.parser.tokenNames
            print("DFA=\n" + dfa.toString(names))

        return to

    #
    # Add state {@code D} to the DFA if it is not already present, and return
    # the actual instance stored in the DFA. If a state equivalent to {@code D}
    # is already in the DFA, the existing state is returned. Otherwise this
    # method returns {@code D} after adding it to the DFA.
    #
    # <p>If {@code D} is {@link #ERROR}, this method returns {@link #ERROR} and
    # does not change the DFA.</p>
    #
    # @param dfa The dfa
    # @param D The DFA state to add
    # @return The state stored in the DFA. This will be either the existing
    # state if {@code D} is already in the DFA, or {@code D} itself if the
    # state was not already present.
    #
    def addDFAState(self, dfa:DFA, D:DFAState):
        if D is self.ERROR:
            return D


        existing = dfa.states.get(D, None)
        if existing is not None:
            return existing

        D.stateNumber = len(dfa.states)
        if not D.configs.readonly:
            D.configs.optimizeConfigs(self)
            D.configs.setReadonly(True)
        dfa.states[D] = D
        if self.debug:
            print("adding new DFA state: " + str(D))
        return D

    def reportAttemptingFullContext(self, dfa:DFA, conflictingAlts:set, configs:ATNConfigSet, startIndex:int, stopIndex:int):
        if self.debug or self.retry_debug:
            interval = range(startIndex, stopIndex + 1)
            print("reportAttemptingFullContext decision=" + str(dfa.decision) + ":" + str(configs) +
                               ", input=" + self.parser.getTokenStream().getText(interval))
        if self.parser is not None:
            self.parser.getErrorListenerDispatch().reportAttemptingFullContext(self.parser, dfa, startIndex, stopIndex, conflictingAlts, configs)

    def reportContextSensitivity(self, dfa:DFA, prediction:int, configs:ATNConfigSet, startIndex:int, stopIndex:int):
        if self.debug or self.retry_debug:
            interval = range(startIndex, stopIndex + 1)
            print("reportContextSensitivity decision=" + str(dfa.decision) + ":" + str(configs) +
                               ", input=" + self.parser.getTokenStream().getText(interval))
        if self.parser is not None:
            self.parser.getErrorListenerDispatch().reportContextSensitivity(self.parser, dfa, startIndex, stopIndex, prediction, configs)

    # If context sensitive parsing, we know it's ambiguity not conflict#
    def reportAmbiguity(self, dfa:DFA, D:DFAState, startIndex:int, stopIndex:int,
                                   exact:bool, ambigAlts:set, configs:ATNConfigSet ):
        if self.debug or self.retry_debug:
#			ParserATNPathFinder finder = new ParserATNPathFinder(parser, atn);
#			int i = 1;
#			for (Transition t : dfa.atnStartState.transitions) {
#				print("ALT "+i+"=");
#				print(startIndex+".."+stopIndex+", len(input)="+parser.getInputStream().size());
#				TraceTree path = finder.trace(t.target, parser.getContext(), (TokenStream)parser.getInputStream(),
#											  startIndex, stopIndex);
#				if ( path!=null ) {
#					print("path = "+path.toStringTree());
#					for (TraceTree leaf : path.leaves) {
#						List<ATNState> states = path.getPathToNode(leaf);
#						print("states="+states);
#					}
#				}
#				i++;
#			}
            interval = range(startIndex, stopIndex + 1)
            print("reportAmbiguity " + str(ambigAlts) + ":" + str(configs) +
                               ", input=" + self.parser.getTokenStream().getText(interval))
        if self.parser is not None:
            self.parser.getErrorListenerDispatch().reportAmbiguity(self.parser, dfa, startIndex, stopIndex, exact, ambigAlts, configs)

