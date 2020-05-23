/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.ParserATNSimulator;

import antlr.v4.runtime.IntStream;
import antlr.v4.runtime.IntStreamConstant;
import antlr.v4.runtime.NoViableAltException;
import antlr.v4.runtime.Parser;
import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.TokenStream;
import antlr.v4.runtime.Vocabulary;
import antlr.v4.runtime.VocabularyImpl;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.atn.ATNConfig;
import antlr.v4.runtime.atn.ATNConfigSet;
import antlr.v4.runtime.atn.ATNSimulator;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.ActionTransition;
import antlr.v4.runtime.atn.AtomTransition;
import antlr.v4.runtime.atn.DecisionState;
import antlr.v4.runtime.atn.EpsilonTransition;
import antlr.v4.runtime.atn.InterfaceParserATNSimulator;
import antlr.v4.runtime.atn.NotSetTransition;
import antlr.v4.runtime.atn.PrecedencePredicateTransition;
import antlr.v4.runtime.atn.PredicateTransition;
import antlr.v4.runtime.atn.PredictionContext;
import antlr.v4.runtime.atn.PredictionContextCache;
import antlr.v4.runtime.atn.PredictionMode;
import antlr.v4.runtime.atn.PredictionModeConst;
import antlr.v4.runtime.atn.RuleStopState;
import antlr.v4.runtime.atn.RuleTransition;
import antlr.v4.runtime.atn.SemanticContext;
import antlr.v4.runtime.atn.SetTransition;
import antlr.v4.runtime.atn.SingletonPredictionContext;
import antlr.v4.runtime.atn.Transition;
import antlr.v4.runtime.atn.TransitionStates;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.dfa.DFAState;
import antlr.v4.runtime.dfa.PredPrediction;
import antlr.v4.runtime.misc;
import std.algorithm.searching;
import std.conv;
import std.format;
import std.stdio;
import std.typecons;

alias ATNConfigSetATNConfigSetPair = Tuple!(ATNConfigSet, "a", ATNConfigSet, "b");

/**
 * The embodiment of the adaptive LL(*), ALL(*), parsing strategy.
 *
 * <p>
 * The basic complexity of the adaptive strategy makes it harder to understand.
 * We begin with ATN simulation to build paths in a DFA. Subsequent prediction
 * requests go through the DFA first. If they reach a state without an edge for
 * the current symbol, the algorithm fails over to the ATN simulation to
 * complete the DFA path for the current input (until it finds a conflict state
 * or uniquely predicting state).</p>
 *
 * <p>
 * All of that is done without using the outer context because we want to create
 * a DFA that is not dependent upon the rule invocation stack when we do a
 * prediction. One DFA works in all contexts. We avoid using context not
 * necessarily because it's slower, although it can be, but because of the DFA
 * caching problem. The closure routine only considers the rule invocation stack
 * created during prediction beginning in the decision rule. For example, if
 * prediction occurs without invoking another rule's ATN, there are no context
 * stacks in the configurations. When lack of context leads to a conflict, we
 * don't know if it's an ambiguity or a weakness in the strong LL(*) parsing
 * strategy (versus full LL(*)).</p>
 *
 * <p>
 * When SLL yields a configuration set with conflict, we rewind the input and
 * retry the ATN simulation, this time using full outer context without adding
 * to the DFA. Configuration context stacks will be the full invocation stacks
 * from the start rule. If we get a conflict using full context, then we can
 * definitively say we have a true ambiguity for that input sequence. If we
 * don't get a conflict, it implies that the decision is sensitive to the outer
 * context. (It is not context-sensitive in the sense of context-sensitive
 * grammars.)</p>
 *
 * <p>
 * The next time we reach this DFA state with an SLL conflict, through DFA
 * simulation, we will again retry the ATN simulation using full context mode.
 * This is slow because we can't save the results and have to "interpret" the
 * ATN each time we get that input.</p>
 *
 * <p>
 * <strong>CACHING FULL CONTEXT PREDICTIONS</strong></p>
 *
 * <p>
 * We could cache results from full context to predicted alternative easily and
 * that saves a lot of time but doesn't work in presence of predicates. The set
 * of visible predicates from the ATN start state changes depending on the
 * context, because closure can fall off the end of a rule. I tried to cache
 * tuples (stack context, semantic context, predicted alt) but it was slower
 * than interpreting and much more complicated. Also required a huge amount of
 * memory. The goal is not to create the world's fastest parser anyway. I'd like
 * to keep this algorithm simple. By launching multiple threads, we can improve
 * the speed of parsing across a large number of files.</p>
 *
 * <p>
 * There is no strict ordering between the amount of input used by SLL vs LL,
 * which makes it really hard to build a cache for full context. Let's say that
 * we have input A B C that leads to an SLL conflict with full context X. That
 * implies that using X we might only use A B but we could also use A B C D to
 * resolve conflict. Input A B C D could predict alternative 1 in one position
 * in the input and A B C E could predict alternative 2 in another position in
 * input. The conflicting SLL configurations could still be non-unique in the
 * full context prediction, which would lead us to requiring more input than the
 * original A B C.  To make a   prediction cache work, we have to track the exact
 * input    used during the previous prediction. That amounts to a cache that maps
 * X to a specific DFA for that context.</p>
 *
 * <p>
 * Something should be done for left-recursive expression predictions. They are
 * likely LL(1) + pred eval. Easier to do the whole SLL unless error and retry
 * with full LL thing Sam does.</p>
 *
 * <p>
 * <strong>AVOIDING FULL CONTEXT PREDICTION</strong></p>
 *
 * <p>
 * We avoid doing full context retry when the outer context is empty, we did not
 * dip into the outer context by falling off the end of the decision state rule,
 * or when we force SLL mode.</p>
 *
 * <p>
 * As an example of the not dip into outer context case, consider as super
 * constructor calls versus function calls. One grammar might look like
 * this:</p>
 *
 * <pre>
 * ctorBody
 *   : '{' superCall? stat* '}'
 *   ;
 * </pre>
 *
 * <p>
 * Or, you might see something like</p>
 *
 * <pre>
 * stat
 *   : superCall ';'
 *   | expression ';'
 *   | ...
 *   ;
 * </pre>
 *
 * <p>
 * In both cases I believe that no closure operations will dip into the outer
 * context. In the first case ctorBody in the worst case will stop at the '}'.
 * In the 2nd case it should stop at the ';'. Both cases should stay within the
 * entry rule and not dip into the outer context.</p>
 *
 * <p>
 * <strong>PREDICATES</strong></p>
 *
 * <p>
 * Predicates are always evaluated if present in either SLL or LL both. SLL and
 * LL simulation deals with predicates differently. SLL collects predicates as
 * it performs closure operations like ANTLR v3 did. It delays predicate
 * evaluation until it reaches and accept state. This allows us to cache the SLL
 * ATN simulation whereas, if we had evaluated predicates on-the-fly during
 * closure, the DFA state configuration sets would be different and we couldn't
 * build up a suitable DFA.</p>
 *
 * <p>
 * When building a DFA accept state during ATN simulation, we evaluate any
 * predicates and return the sole semantically valid alternative. If there is
 * more than 1 alternative, we report an ambiguity. If there are 0 alternatives,
 * we throw an exception. Alternatives without predicates act like they have
 * true predicates. The simple way to think about it is to strip away all
 * alternatives with false predicates and choose the minimum alternative that
 * remains.</p>
 *
 * <p>
 * When we start in the DFA and reach an accept state that's predicated, we test
 * those and return the minimum semantically viable alternative. If no
 * alternatives are viable, we throw an exception.</p>
 *
 * <p>
 * During full LL ATN simulation, closure always evaluates predicates and
 * on-the-fly. This is crucial to reducing the configuration set size during
 * closure. It hits a landmine when parsing with the Java grammar, for example,
 * without this on-the-fly evaluation.</p>
 *
 * <p>
 * <strong>SHARING DFA</strong></p>
 *
 * <p>
 * All instances of the same parser share the same decision DFAs through a
 * static field. Each instance gets its own ATN simulator but they share the
 * same {@link #decisionToDFA} field. They also share a
 * {@link PredictionContextCache} object that makes sure that all
 * {@link PredictionContext} objects are shared among the DFA states. This makes
 * a big size difference.</p>
 *
 * <p>
 * <strong>THREAD SAFETY</strong></p>
 *
 * <p>
 * The {@link ParserATNSimulator} locks on the {@link #decisionToDFA} field when
 * it adds a new DFA object to that array. {@link #addDFAEdge}
 * locks on the DFA for the current decision when setting the
 * {@link DFAState#edges} field. {@link #addDFAState} locks on
 * the DFA for the current decision when looking up a DFA state to see if it
 * already exists. We must make sure that all requests to add DFA states that
 * are equivalent result in the same shared DFA object. This is because lots of
 * threads will be trying to update the DFA at once. The
 * {@link #addDFAState} method also locks inside the DFA lock
 * but this time on the shared context cache when it rebuilds the
 * configurations' {@link PredictionContext} objects using cached
 * subgraphs/nodes. No other locking occurs, even during DFA simulation. This is
 * safe as long as we can guarantee that all threads referencing
 * {@code s.edge[t]} get the same physical target {@link DFAState}, or
 * {@code null}. Once into the DFA, the DFA simulation does not reference the
 * {@link DFA#states} map. It follows the {@link DFAState#edges} field to new
 * targets. The DFA simulator will either find {@link DFAState#edges} to be
 * {@code null}, to be non-{@code null} and {@code dfa.edges[t]} null, or
 * {@code dfa.edges[t]} to be non-null. The
 * {@link #addDFAEdge} method could be racing to set the field
 * but in either case the DFA simulator works; if {@code null}, and requests ATN
 * simulation. It could also race trying to get {@code dfa.edges[t]}, but either
 * way it will work because it's not doing a test and set operation.</p>
 *
 * <p>
 * <strong>Starting with SLL then failing to combined SLL/LL (Two-Stage
 * Parsing)</strong></p>
 *
 * <p>
 * Sam pointed out that if SLL does not give a syntax error, then there is no
 * point in doing full LL, which is slower. We only have to try LL if we get a
 * syntax error. For maximum speed, Sam starts the parser set to pure SLL
 * mode with the {@link BailErrorStrategy}:</p>
 *
 * <pre>
 * parser.{@link Parser#getInterpreter() getInterpreter()}.{@link #setPredictionMode setPredictionMode}{@code (}{@link PredictionMode#SLL}{@code )};
 * parser.{@link Parser#setErrorHandler setErrorHandler}(new {@link BailErrorStrategy}());
 * </pre>
 *
 * <p>
 * If it does not get a syntax error, then we're done. If it does get a syntax
 * error, we need to retry with the combined SLL/LL strategy.</p>
 *
 * <p>
 * The reason this works is as follows. If there are no SLL conflicts, then the
 * grammar is SLL (at least for that input set). If there is an SLL conflict,
 * the full LL analysis must yield a set of viable alternatives which is a
 * subset of the alternatives reported by SLL. If the LL set is a singleton,
 * then the grammar is LL but not SLL. If the LL set is the same size as the SLL
 * set, the decision is SLL. If the LL set has size &gt; 1, then that decision
 * is truly ambiguous on the current input. If the LL set is smaller, then the
 * SLL conflict resolution might choose an alternative that the full LL would
 * rule out as a possibility based upon better context information. If that's
 * the case, then the SLL parse will definitely get an error because the full LL
 * analysis says it's not viable. If SLL conflict resolution chooses an
 * alternative within the LL set, them both SLL and LL would choose the same
 * alternative because they both choose the minimum of multiple conflicting
 * alternatives.</p>
 *
 * <p>
 * Let's say we have a set of SLL conflicting alternatives {@code {1, 2, 3}} and
 * a smaller LL set called <em>s</em>. If <em>s</em> is {@code {2, 3}}, then SLL
 * parsing will get an error because SLL will pursue alternative 1. If
 * <em>s</em> is {@code {1, 2}} or {@code {1, 3}} then both SLL and LL will
 * choose the same alternative because alternative one is the minimum of either
 * set. If <em>s</em> is {@code {2}} or {@code {3}} then SLL will get a syntax
 * error. If <em>s</em> is {@code {1}} then SLL will succeed.</p>
 *
 * <p>
 * Of course, if the input is invalid, then we will get an error for sure in
 * both SLL and LL parsing. Erroneous input will therefore require 2 passes over
 * the input.</p>
 */
class ParserATNSimulator : ATNSimulator, InterfaceParserATNSimulator
{

    protected Parser parser;

    public DFA[] decisionToDFA;

    public PredictionModeConst mode = PredictionModeConst.LL;

    /**
     * Each prediction operation uses a cache for merge of prediction contexts.
     * Don't keep around as it wastes huge amounts of memory. DoubleKeyMap
     * isn't synchronized but we're ok since two threads shouldn't reuse same
     * parser/atnsim object because it can only handle one input at a time.
     * This maps graphs a and b to merged result c. (a,b)&rarr;c. We can avoid
     * the merge if we ever see a and b again.  Note that (b,a)&rarr;c should
     * also be examined during cache lookup.
     * @uml
     * @__gshared
     */
    public static __gshared DoubleKeyMap!(PredictionContext, PredictionContext, PredictionContext) mergeCache;

    protected DFA _dfa;

    protected TokenStream _input;

    protected size_t _startIndex;

    protected ParserRuleContext _outerContext;

    /**
     * @uml
     * Testing only!
     */
    protected this(ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache)
    {
        this(null, atn, decisionToDFA, sharedContextCache);
    }

    public this(Parser parser, ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache)
    {
        super(atn, sharedContextCache);
        this.parser = parser;
        this.decisionToDFA = decisionToDFA;
        if (mergeCache is null)
            this.mergeCache = new typeof(this.mergeCache);
    }

    /**
     * @uml
     * @override
     */
    public override void reset()
    {
    }

    /**
     * @uml
     * @override
     */
    public override void clearDFA()
    {
    for (int d = 0; d < decisionToDFA.length; d++) {
            decisionToDFA[d] = new DFA(atn.getDecisionState(d), d);
        }
    }

    public int adaptivePredict(TokenStream input, int decision, ParserRuleContext outerContext)
    {
    debug(ParserATNSimulator) {
            writefln("adaptivePredict decision %1$s"~
                     " exec LA(1)==%2$s"~
                     " line %3$s:%4$s",
                     decision, getLookaheadName(input), input.LT(1).getLine,
                     input.LT(1).getCharPositionInLine());
        }

        _input = input;
        _startIndex = input.index();
        _outerContext = outerContext;
        DFA dfa = decisionToDFA[decision];
        _dfa = dfa;

        size_t m = input.mark();
        auto index = _startIndex;
        // Now we are certain to have a specific decision's DFA
        // But, do we still need an initial state?
        try {
            DFAState s0;
            if (dfa.isPrecedenceDfa) {
                // the start state for a precedence DFA depends on the current
                // parser precedence, and is provided by a DFA method.
                s0 = dfa.getPrecedenceStartState(parser.getPrecedence());
            }
            else {
                // the start state for a "regular" DFA is just s0
                s0 = dfa.s0;
            }

            if (!s0) {
                if (!outerContext)
                    outerContext = ParserRuleContext.EMPTY;
                debug(ParserATNSimulator) {
                    writefln("predictATN decision = %1$s"~
                        " exec LA(1)==%2$s"~
                        ", outerContext=%3$s",
                            dfa.decision, getLookaheadName(input),
                             outerContext.toString(parser));
                }

                bool fullCtx = false;
                ATNConfigSet s0_closure =
                    computeStartState(dfa.atnStartState,
                                      ParserRuleContext.EMPTY,
                                      fullCtx);
                if (dfa.isPrecedenceDfa) {
                    /* If this is a precedence DFA, we use applyPrecedenceFilter
                     * to convert the computed start state to a precedence start
                     * state. We then use DFA.setPrecedenceStartState to set the
                     * appropriate start state for the precedence level rather
                     * than simply setting DFA.s0.
                     */
                    dfa.s0.configs = s0_closure; // not used for prediction but useful to know start configs anyway
                    s0_closure = applyPrecedenceFilter(s0_closure);
                    s0 = addDFAState(dfa, new DFAState(s0_closure));
                    dfa.setPrecedenceStartState(parser.getPrecedence(), s0);
                }
                else {
                    s0 = addDFAState(dfa, new DFAState(s0_closure));
                    dfa.s0 = s0;
                }
            }
            int alt = execATN(dfa, s0, input, index, outerContext);
            debug(ParserATNSimulator)
                writefln("DFA after predictATN: %1$s, alt = %s, dfa.states = %s", dfa.toString(parser.getVocabulary), alt, dfa);
            return alt;
        }
        finally {
            this.mergeCache = new typeof(this.mergeCache); // wack cache after each prediction
            _dfa = null;
            input.seek(to!int(index));
            input.release(to!int(m));
        }
    }

    /**
     * There are some key conditions we're looking for after computing a new
     * set of ATN configs (proposed DFA state):
     *  <br>- if the set is empty, there is no viable alternative for current symbol
     *  <br>-  does the state uniquely predict an alternative?
     *  <br>-  does the state have a conflict that would prevent us from
     *         putting it on the work list?
     * <br><br>We also have some key operations to do:
     *  <br>- add an edge from previous DFA state to potentially new DFA state, D,
     *         upon current symbol but only if adding to work list, which means in all
     *         cases except no viable alternative (and possibly non-greedy decisions?)
     *  <br>-  collecting predicates and adding semantic context to DFA accept states
     *  <br>-  adding rule context to context-sensitive DFA accept states
     *  <br>-  consuming an input symbol
     *  <br>-  reporting a conflict
     *  <br>-  reporting an ambiguity
     *  <br>-  reporting a context sensitivity
     *  <br>-  reporting insufficient predicates
     * <br><br>cover these cases:
     *  <br>- dead end
     *  <br>- single alt
     *  <br>- single alt + preds
     *  <br>- conflict
     *  <br>- conflict + preds
     */
    protected int execATN(DFA dfa, DFAState s0, TokenStream input, size_t startIndex, ParserRuleContext outerContext)
    {
    debug(ParserATNSimulator) {
            writefln("execATN decision %1$s"~
                     " exec LA(1)==%2$s line %3$s:%4$s",
                     dfa.decision,
                     getLookaheadName(input),
                     input.LT(1).getLine,
                     input.LT(1).getCharPositionInLine());
        }

        DFAState previousD = s0;

        debug
            writefln("s0 = %1$s", s0);

        int t = input.LA(1);

        while (true) { // while more work
            DFAState D = getExistingTargetState(previousD, t);
            if (D is null) {
                D = computeTargetState(dfa, previousD, t);
            }

            if (D == ERROR) {
                // if any configs in previous dipped into outer context, that
                // means that input up to t actually finished entry rule
                // at least for SLL decision. Full LL doesn't dip into outer
                // so don't need special case.
                // We will get an error no matter what so delay until after
                // decision; better error message. Also, no reachable target
                // ATN states in SLL implies LL will also get nowhere.
                // If conflict in states that dip out, choose min since we
                // will get error no matter what.
                NoViableAltException e = noViableAlt(input, outerContext, previousD.configs, startIndex);
                input.seek(to!int(startIndex));
                int alt = getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previousD.configs, outerContext);
                if (alt != ATN.INVALID_ALT_NUMBER) {
                    return alt;
                }
                throw e;
            }

            if (D.requiresFullContext && mode != PredictionModeConst.SLL) {
                // IF PREDS, MIGHT RESOLVE TO SINGLE ALT => SLL (or syntax error)
                BitSet conflictingAlts = D.configs.conflictingAlts;
                if (D.predicates != null) {
                    debug(ParserATNSimulator)
                        writeln("DFA state has preds in DFA sim LL failover");
                    size_t conflictIndex = input.index();
                    if (conflictIndex != startIndex) {
                        input.seek(to!int(startIndex));
                    }

                    conflictingAlts = evalSemanticContext(D.predicates, outerContext, true);
                    if (conflictingAlts.cardinality ==1) {
                        debug(ParserATNSimulator)
                            writeln("Full LL avoided");
                        return conflictingAlts.nextSetBit(0);
                    }

                    if (conflictIndex != startIndex) {
                        // restore the index so reporting the fallback to full
                        // context occurs with the index at the correct spot
                        input.seek(to!int(conflictIndex));
                    }
                }

                debug(dfa_debug)
                    writefln!"ctx sensitive state %1$s in %2$s"(
                             outerContext, D);
                bool fullCtx = true;
                ATNConfigSet s0_closure =
                    computeStartState(dfa.atnStartState, outerContext,
                                      fullCtx);
                reportAttemptingFullContext(dfa, conflictingAlts, D.configs, startIndex, input.index());
                int alt = execATNWithFullContext(dfa, D, s0_closure,
                                                 input, startIndex,
                                                 outerContext);
                return alt;
            }

            if ( D.isAcceptState ) {
                if (D.predicates == null) {
                    return D.prediction;
                }

                auto stopIndex = input.index();
                input.seek(to!int(startIndex));
                BitSet alts = evalSemanticContext(D.predicates, outerContext, true);
                switch (alts.cardinality) {
                case 0:
                    throw noViableAlt(input, outerContext, D.configs, startIndex);

                case 1:
                    return alts.nextSetBit(0);

                default:
                    // report ambiguity after predicate evaluation to make sure the correct
                    // set of ambig alts is reported.
                    reportAmbiguity(dfa, D, startIndex, stopIndex, false, alts, D.configs);
                    return alts.nextSetBit(0);
                }
            }

            previousD = D;

            if (t != IntStreamConstant.EOF) {
                input.consume();
                t = input.LA(1);
            }
        }

    }

    /**
     * Get an existing target state for an edge in the DFA. If the target state
     * for the edge has not yet been computed or is otherwise not available,
     * this method returns {@code null}.
     *
     * @param previousD The current DFA state
     * @param t The next input symbol
     * @return The existing target DFA state for the given input symbol
     * {@code t}, or {@code null} if the target state for this edge is not
     * already cached
     */
    public DFAState getExistingTargetState(DFAState previousD, int t)
    {
        DFAState[] edges = previousD.edges;
        if (edges is null || t + 1 < 0 || t + 1 >= edges.length) {
            return null;
        }
        return edges[t + 1];
    }

    /**
     * Compute a target state for an edge in the DFA, and attempt to add the
     * computed state and corresponding edge to the DFA.
     *
     * @param dfa The DFA
     * @param previousD The current DFA state
     * @param t The next input symbol
     *
     * @return The computed target DFA state for the given input symbol
     * {@code t}. If {@code t} does not lead to a valid DFA state, this method
     * returns {@link #ERROR}.
     */
    public DFAState computeTargetState(DFA dfa, DFAState previousD, int t)
    {
        ATNConfigSet reach = computeReachSet(previousD.configs, t, false);
        if (reach is null) {
            addDFAEdge(dfa, previousD, t, ERROR);
            return ERROR;
        }
        // create new target state; we'll add to DFA after it's complete
        DFAState D = new DFAState(reach);
        int predictedAlt = getUniqueAlt(reach);

        debug(ParserATNSimulator) {
            BitSet[] altSubSets = PredictionMode.getConflictingAltSubsets(reach);
            writefln("SLL altSubSets=%1$s"~
                     ", configs=%2$s"~
                     ", predict=%3$s, allSubsetsConflict=%4$s, "~
                     "conflictingAlts=%5$s",
                     altSubSets,
                     reach,
                     predictedAlt,
                     PredictionMode.allSubsetsConflict(altSubSets),
                     getConflictingAlts(reach));
        }

        if (predictedAlt != ATN.INVALID_ALT_NUMBER) {
            // NO CONFLICT, UNIQUELY PREDICTED ALT
            D.isAcceptState = true;
            D.configs.uniqueAlt = predictedAlt;
            D.prediction = predictedAlt;
        }
        else if ( PredictionMode.hasSLLConflictTerminatingPrediction(mode, reach)) {
            // MORE THAN ONE VIABLE ALTERNATIVE
            D.configs.conflictingAlts = getConflictingAlts(reach);
            D.requiresFullContext = true;
            // in SLL-only mode, we will stop at this state and return the minimum alt
            D.isAcceptState = true;
            D.prediction = D.configs.conflictingAlts.nextSetBit(0);
        }

        if ( D.isAcceptState && D.configs.hasSemanticContext ) {
            predicateDFAState(D, atn.getDecisionState(dfa.decision));
            if (D.predicates != null) {
                D.prediction = ATN.INVALID_ALT_NUMBER;
            }
        }
        // all adds to dfa are done after we've created full D state
        D = addDFAEdge(dfa, previousD, t, D);
        return D;
    }

    public void predicateDFAState(DFAState dfaState, DecisionState decisionState)
    {
    // We need to test all predicates, even in DFA states that
        // uniquely predict alternative.
        int nalts = decisionState.getNumberOfTransitions();
        // Update DFA so reach becomes accept state with (predicate,alt)
        // pairs if preds found for conflicting alts
        BitSet altsToCollectPredsFrom = getConflictingAltsOrUniqueAlt(dfaState.configs);
        SemanticContext[] altToPred = getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts);
        if ( altToPred !is null ) {
            dfaState.predicates = getPredicatePredictions(altsToCollectPredsFrom, altToPred);
            dfaState.prediction = ATN.INVALID_ALT_NUMBER; // make sure we use preds
        }
        else {
            // There are preds in configs but they might go away
            // when OR'd together like {p}? || NONE == NONE. If neither
            // alt has preds, resolve to min alt
            dfaState.prediction = altsToCollectPredsFrom.nextSetBit(0);
        }
    }

    /**
     * @uml
     * comes back with reach.uniqueAlt set to a valid alt
     */
    protected int execATNWithFullContext(DFA dfa, DFAState D, ATNConfigSet s0, TokenStream input,
                                         size_t startIndex, ParserRuleContext outerContext)
    {
    debug(ParserATNSimulator) {
            writefln("execATNWithFullContext %s", s0);
        }
        bool fullCtx = true;
        bool foundExactAmbig = false;
        ATNConfigSet reach = null;
        ATNConfigSet previous = s0;
        input.seek(to!int(startIndex));
        int t = input.LA(1);
        int predictedAlt;
        while (true) { // while more work
            //          System.out.println("LL REACH "+getLookaheadName(input)+
            //                             " from configs.size="+previous.size()+
            //                             " line "+input.LT(1).getLine()+":"+input.LT(1).getCharPositionInLine());
            reach = computeReachSet(previous, t, fullCtx);
            if (reach is null) {
                // if any configs in previous dipped into outer context, that
                // means that input up to t actually finished entry rule
                // at least for LL decision. Full LL doesn't dip into outer
                // so don't need special case.
                // We will get an error no matter what so delay until after
                // decision; better error message. Also, no reachable target
                // ATN states in SLL implies LL will also get nowhere.
                // If conflict in states that dip out, choose min since we
                // will get error no matter what.
                NoViableAltException e = noViableAlt(input, outerContext, previous, startIndex);
                input.seek(to!int(startIndex));
                int alt = getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previous, outerContext);
                if ( alt!=ATN.INVALID_ALT_NUMBER ) {
                    return alt;
                }
                throw e;
            }

            BitSet[] altSubSets = PredictionMode.getConflictingAltSubsets(reach);
            debug(ParserATNSimulator) {
                writefln("LL altSubSets=%1$s, PredictionMode.getUniqueAlt(altSubSets)" ~
                         ", resolvesToJustOneViableAlt=%3$s",
                         altSubSets,
                         PredictionMode.getUniqueAlt(altSubSets),
                         PredictionMode.resolvesToJustOneViableAlt(altSubSets));
            }

            //          System.out.println("altSubSets: "+altSubSets);
            //          System.err.println("reach="+reach+", "+reach.conflictingAlts);
            reach.uniqueAlt = getUniqueAlt(reach);
            // unique prediction?
            if ( reach.uniqueAlt!=ATN.INVALID_ALT_NUMBER ) {
                predictedAlt = reach.uniqueAlt;
                break;
            }
            if ( mode != PredictionModeConst.LL_EXACT_AMBIG_DETECTION ) {
                predictedAlt = PredictionMode.resolvesToJustOneViableAlt(altSubSets);
                if ( predictedAlt != ATN.INVALID_ALT_NUMBER ) {
                    break;
                }
            }
            else {
                // In exact ambiguity mode, we never try to terminate early.
                // Just keeps scarfing until we know what the conflict is
                if ( PredictionMode.allSubsetsConflict(altSubSets) &&
                     PredictionMode.allSubsetsEqual(altSubSets) )
                    {
                        foundExactAmbig = true;
                        predictedAlt = PredictionMode.getSingleViableAlt(altSubSets);
                        break;
                    }
                // else there are multiple non-conflicting subsets or
                // we're not sure what the ambiguity is yet.
                // So, keep going.
            }

            previous = reach;
            if (t != IntStreamConstant.EOF) {
                input.consume();
                t = input.LA(1);
            }
        }

        // If the configuration set uniquely predicts an alternative,
        // without conflict, then we know that it's a full LL decision
        // not SLL.
        if ( reach.uniqueAlt != ATN.INVALID_ALT_NUMBER ) {
            reportContextSensitivity(dfa, predictedAlt, reach, startIndex, input.index());
            return predictedAlt;
        }

        // We do not check predicates here because we have checked them
        // on-the-fly when doing full context prediction.

        /*
          In non-exact ambiguity detection mode, we might   actually be able to
          detect an exact ambiguity, but I'm not going to spend the cycles
          needed to check. We only emit ambiguity warnings in exact ambiguity
          mode.

          For example, we might know that we have conflicting configurations.
          But, that does not mean that there is no way forward without a
          conflict. It's possible to have nonconflicting alt subsets as in:

          LL altSubSets=[{1, 2}, {1, 2}, {1}, {1, 2}]

          from

          [(17,1,[5 $]), (13,1,[5 10 $]), (21,1,[5 10 $]), (11,1,[$]),
          (13,2,[5 10 $]), (21,2,[5 10 $]), (11,2,[$])]

          In this case, (17,1,[5 $]) indicates there is some next sequence that
          would resolve this without conflict to alternative 1. Any other viable
          next sequence, however, is associated with a conflict.  We stop
          looking for input because no amount of further lookahead will alter
          the fact that we should predict alternative 1.  We just can't say for
          sure that there is an ambiguity without looking further.
        */
        reportAmbiguity(dfa, D, startIndex, input.index(), foundExactAmbig,
                        reach.getAlts(), reach);

        return predictedAlt;
    }

    public ATNConfigSet computeReachSet(ATNConfigSet closure, int t, bool fullCtx)
    {
        debug(ParserATNSimulator)
            writefln("in computeReachSet, starting closure: %s", closure);

        if (mergeCache is null) {
            mergeCache = new DoubleKeyMap!(PredictionContext, PredictionContext, PredictionContext);
        }

        ATNConfigSet intermediate = new ATNConfigSet(fullCtx);

        /* Configurations already in a rule stop state indicate reaching the end
         * of the decision rule (local context) or end of the start rule (full
         * context). Once reached, these configurations are never updated by a
         * closure operation, so they are handled separately for the performance
         * advantage of having a smaller intermediate set when calling closure.
         *
         * For full-context reach operations, separate handling is required to
         * ensure that the alternative matching the longest overall sequence is
         * chosen when multiple such configurations can match the input.
         */
        ATNConfig[] skippedStopStates;

        // First figure out where we can reach on input t
        foreach (c; closure.configs) {
            debug(ParserATNSimulator)
                writefln("testing %1$s at %2$s", getTokenName(t), c.toString);

            if (cast(RuleStopState)c.state) {
                assert(c.context.isEmpty);
                if (fullCtx || t == IntStreamConstant.EOF) {
                    skippedStopStates ~= c;
                }
                continue;
            }

            foreach (trans; c.state.transitions) {
                ATNState target = getReachableTarget(trans, t);
                if (target) {
                    intermediate.add(new ATNConfig(c, target), mergeCache);
                }
            }
        }

        // Now figure out where the reach operation can take us...

        ATNConfigSet reach = null;

        /* This block optimizes the reach operation for intermediate sets which
         * trivially indicate a termination state for the overall
         * adaptivePredict operation.
         *
         * The conditions assume that intermediate
         * contains all configurations relevant to the reach set, but this
         * condition is not true when one or more configurations have been
         * withheld in skippedStopStates, or when the current symbol is EOF.
         */
        if (skippedStopStates is null && t != TokenConstantDefinition.EOF) {
            if (intermediate.size() == 1 ) {
                // Don't pursue the closure if there is just one state.
                // It can only have one alternative; just add to result
                // Also don't pursue the closure if there is unique alternative
                // among the configurations.
                reach = intermediate;
            }
            else if (getUniqueAlt(intermediate)!=ATN.INVALID_ALT_NUMBER ) {
                // Also don't pursue the closure if there is unique alternative
                // among the configurations.
                reach = intermediate;
            }
        }

        /* If the reach set could not be trivially determined, perform a closure
         * operation on the intermediate set to compute its initial value.
         */
        if (reach is null) {
            reach = new ATNConfigSet(fullCtx);
            ATNConfig[] closureBusy;
            bool treatEofAsEpsilon = t == TokenConstantDefinition.EOF;
            foreach (c; intermediate.configs)
                {
                    closureATN(c, reach, closureBusy, false, fullCtx, treatEofAsEpsilon);
                }
        }

        if (t == IntStreamConstant.EOF) {
            /* After consuming EOF no additional input is possible, so we are
             * only interested in configurations which reached the end of the
             * decision rule (local context) or end of the start rule (full
             * context). Update reach to contain only these configurations. This
             * handles both explicit EOF transitions in the grammar and implicit
             * EOF transitions following the end of the decision or start rule.
             *
             * When reach==intermediate, no closure operation was performed. In
             * this case, removeAllConfigsNotInRuleStopState needs to check for
             * reachable rule stop states as well as configurations already in
             * a rule stop state.
             *
             * This is handled before the configurations in skippedStopStates,
             * because any configurations potentially added from that list are
             * already guaranteed to meet this condition whether or not it's
             * required.
             */
            reach = removeAllConfigsNotInRuleStopState(reach, reach.configs == intermediate.configs);
        }

        /* If skippedStopStates is not null, then it contains at least one
         * configuration. For full-context reach operations, these
         * configurations reached the end of the start rule, in which case we
         * only add them back to reach if no configuration during the current
         * closure operation reached such a state. This ensures adaptivePredict
         * chooses an alternative matching the longest overall sequence when
         * multiple alternatives are viable.
         */
        if (skippedStopStates !is null && (!fullCtx || !PredictionMode.hasConfigInRuleStopState(reach))) {
            assert(skippedStopStates.length > 0);
            foreach (c; skippedStopStates) {
                reach.add(c, mergeCache);
            }
        }

        if (reach.isEmpty)
            return null;
        return reach;
    }

    /**
     * Return a configuration set containing only the configurations from
     * {@code configs} which are in a {@link RuleStopState}. If all
     * configurations in {@code configs} are already in a rule stop state, this
     * method simply returns {@code configs}.
     *
     * <p>When {@code lookToEndOfRule} is true, this method uses
     * {@link ATN#nextTokens} for each configuration in {@code configs} which is
     * not already in a rule stop state to see if a rule stop state is reachable
     * from the configuration via epsilon-only transitions.</p>
     *
     * @param configs the configuration set to update
     * @param lookToEndOfRule when true, this method checks for rule stop states
     * reachable by epsilon-only transitions from each configuration in
     * {@code configs}.
     *
     * @return {@code configs} if all configurations in {@code configs} are in a
     * rule stop state, otherwise return a new configuration set containing only
     * the configurations from {@code configs} which are in a rule stop state
     */
    protected ATNConfigSet removeAllConfigsNotInRuleStopState(ATNConfigSet configs, bool lookToEndOfRule)
    {
    if (PredictionMode.allConfigsInRuleStopStates(configs)) {
            return configs;
        }

        ATNConfigSet result = new ATNConfigSet(configs.fullCtx);
        foreach (ATNConfig config; configs.configs) {
            if (cast(RuleStopState)config.state) {
                result.add(config, mergeCache);
                continue;
            }

            if (lookToEndOfRule && config.state.onlyHasEpsilonTransitions()) {
                IntervalSet nextTokens = atn.nextTokens(config.state);
                if (nextTokens.contains(TokenConstantDefinition.EPSILON)) {
                    ATNState endOfRuleState = atn.ruleToStopState[config.state.ruleIndex];
                    result.add(new ATNConfig(config, endOfRuleState), mergeCache);
                }
            }
        }
        return result;
    }

    public ATNConfigSet computeStartState(ATNState p, RuleContext ctx, bool fullCtx)
    {
        // always at least the implicit call to start rule
        PredictionContext initialContext = PredictionContext.fromRuleContext(atn, ctx);
        ATNConfigSet configs = new ATNConfigSet(fullCtx);

        for (int i=0; i<p.getNumberOfTransitions(); i++) {
            ATNState target = p.transition(i).target;
            ATNConfig c = new ATNConfig(target, i+1, initialContext);
            ATNConfig[] closureBusy;
            closureATN(c, configs, closureBusy, true, fullCtx, false);
        }

        return configs;
    }

    public ATNConfigSet applyPrecedenceFilter(ATNConfigSet configs)
    {
    PredictionContext[int] statesFromAlt1;
        ATNConfigSet configSet = new ATNConfigSet(configs.fullCtx);
        foreach (config; configs.configs) {
            // handle alt 1 first
            if (config.alt != 1) {
                continue;
            }

            SemanticContext updatedContext = config.semanticContext.evalPrecedence(parser, _outerContext);
            if (updatedContext is null) {
                // the configuration was eliminated
                continue;
            }

            statesFromAlt1[config.state.stateNumber] = config.context;
            if (updatedContext != config.semanticContext) {
                configSet.add(new ATNConfig(config, updatedContext), mergeCache);
            }
            else {
                configSet.add(config, mergeCache);
            }
        }

        foreach (config; configs.configs) {
            if (config.alt == 1) {
                // already handled
                continue;
            }

            if (!config.isPrecedenceFilterSuppressed()) {
                /* In the future, this elimination step could be updated to also
                 * filter the prediction context for alternatives predicting alt>1
                 * (basically a graph subtraction algorithm).
                 */
                if (config.state.stateNumber in statesFromAlt1 &&
                                                    statesFromAlt1[config.state.stateNumber].opEquals(config.context)) {
                    // eliminated
                    continue;
                }
            }

            configSet.add(config, mergeCache);
        }

        return configSet;

    }

    public ATNState getReachableTarget(Transition trans, int ttype)
    {
    if (trans.matches(ttype, 0, atn.maxTokenType)) {
            return trans.target;
        }
        return null;
    }

    public SemanticContext[] getPredsForAmbigAlts(BitSet ambigAlts, ATNConfigSet configs,
                                                  int nalts)
    {
        // REACH=[1|1|[]|0:0, 1|2|[]|0:1]
        /* altToPred starts as an array of all null contexts. The entry at index i
         * corresponds to alternative i. altToPred[i] may have one of three values:
         *   1. null: no ATNConfig c is found such that c.alt==i
         *   2. SemanticContext.NONE: At least one ATNConfig c exists such that
         *      c.alt==i and c.semanticContext==SemanticContext.NONE. In other words,
         *      alt i has at least one unpredicated config.
         *   3. Non-NONE Semantic Context: There exists at least one, and for all
         *      ATNConfig c such that c.alt==i, c.semanticContext!=SemanticContext.NONE.
         *
         * From this, it is clear that NONE||anything==NONE.
         */
        SemanticContext[] altToPred = new SemanticContext[nalts + 1];
        foreach (ATNConfig c; configs.configs) {
            if ( ambigAlts.get(c.alt) ) {
                altToPred[c.alt] = SemanticContext.or(altToPred[c.alt], c.semanticContext);
            }
        }

        int nPredAlts = 0;
        if (!SemanticContext.NONE) {
            auto sp = new SemanticContext;
            SemanticContext.NONE = sp.new SemanticContext.Predicate;
        }
        for (int i = 1; i <= nalts; i++) {
            if (altToPred[i] is null) {
                altToPred[i] = SemanticContext.NONE;
            }
            else if (altToPred[i] != SemanticContext.NONE) {
                nPredAlts++;
            }
        }
        if (nPredAlts == 0) altToPred = null;
        debug(ParserATNSimulator)
            writefln("getPredsForAmbigAlts result %s", to!string(altToPred));
        return altToPred;
    }

    protected PredPrediction[] getPredicatePredictions(BitSet ambigAlts, SemanticContext[] altToPred)
    {
    PredPrediction[] pairs;
        bool containsPredicate = false;

        for (int i = 1; i < altToPred.length; i++) {
            SemanticContext pred = altToPred[i];

            // unpredicated is indicated by SemanticContext.NONE
            assert(pred !is null);

            if (ambigAlts.length > 0 && ambigAlts.get(i)) {
                pairs ~= new PredPrediction(pred, i);
            }
            if (pred != SemanticContext.NONE)
                containsPredicate = true;
        }

        if (!containsPredicate) {
            return null;
        }

        //      System.out.println(Arrays.toString(altToPred)+"->"+pairs);
        return pairs;

    }

    protected int getAltThatFinishedDecisionEntryRule(ATNConfigSet configs)
    {
    IntervalSet alts = new IntervalSet();
        foreach (ATNConfig c; configs.configs) {
            if ( c.getOuterContextDepth() > 0 || (cast(RuleStopState)c.state && c.context.hasEmptyPath) ) {
                alts.add(c.alt);
            }
        }
        if ( alts.size()==0 ) return ATN.INVALID_ALT_NUMBER;
        return alts.getMinElement();
    }

    /**
     * This method is used to improve the localization of error messages by
     * choosing an alternative rather than throwing a
     * {@link NoViableAltException} in particular prediction scenarios where the
     * {@link #ERROR} state was reached during ATN simulation.
     *
     * <p>
     * The default implementation of this method uses the following
     * algorithm to identify an ATN configuration which successfully parsed the
     * decision entry rule. Choosing such an alternative ensures that the
     * {@link ParserRuleContext} returned by the calling rule will be complete
     * and valid, and the syntax error will be reported later at a more
     * localized location.</p>
     *
     * <ul>
     * <li>If a syntactically valid path or paths reach the end of the decision rule and
     * they are semantically valid if predicated, return the min associated alt.</li>
     * <li>Else, if a semantically invalid but syntactically valid path exist
     * or paths exist, return the minimum associated alt.
     * </li>
     * <li>Otherwise, return {@link ATN#INVALID_ALT_NUMBER}.</li>
     * </ul>
     *
     * <p>
     * In some scenarios, the algorithm described above could predict an
     * alternative which will result in a {@link FailedPredicateException} in
     * the parser. Specifically, this could occur if the <em>only</em> configuration
     * capable of successfully parsing to the end of the decision rule is
     * blocked by a semantic predicate. By choosing this alternative within
     * {@link #adaptivePredict} instead of throwing a
     * {@link NoViableAltException}, the resulting
     * {@link FailedPredicateException} in the parser will identify the specific
     * predicate which is preventing the parser from successfully parsing the
     * decision rule, which helps developers identify and correct logic errors
     * in semantic predicates.
     * </p>
     *
     * @param configs The ATN configurations which were valid immediately before
     * the {@link #ERROR} state was reached
     * @param outerContext The is the \gamma_0 initial parser context from the paper
     * or the parser stack at the instant before prediction commences.
     *
     * @return The value to return from {@link #adaptivePredict}, or
     * {@link ATN#INVALID_ALT_NUMBER} if a suitable alternative was not
     * identified and {@link #adaptivePredict} should report an error instead.
     */
    protected int getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(ATNConfigSet configs,
                                                                          ParserRuleContext outerContext)
    {
    IntervalSet alts = new IntervalSet();
        foreach (ATNConfig c; configs.configs) {
            if (c.getOuterContextDepth > 0 || (cast(RuleStopState)c.state && c.context.hasEmptyPath) ) {
                alts.add(c.alt);
            }
        }
        if (alts.size == 0)
            return ATN.INVALID_ALT_NUMBER;
        return alts.getMinElement();
    }

    protected ATNConfigSetATNConfigSetPair splitAccordingToSemanticValidity(ATNConfigSet configs,
                                                                            ParserRuleContext outerContext)
    {
    ATNConfigSet succeeded = new ATNConfigSet(configs.fullCtx);
        ATNConfigSet failed = new ATNConfigSet(configs.fullCtx);
        foreach (ATNConfig c; configs.configs) {
            if (c.semanticContext != SemanticContext.NONE ) {
                bool predicateEvaluationResult = evalSemanticContext(c.semanticContext, outerContext, c.alt, configs.fullCtx);
                if (predicateEvaluationResult) {
                    succeeded.add(c);
                }
                else {
                    failed.add(c);
                }
            }
            else {
                succeeded.add(c);
            }
        }
        ATNConfigSetATNConfigSetPair res;
        res.a = succeeded;
        res.b = failed;
        return res;
    }

    /**
     * Look through a list of predicate/alt pairs, returning alts for the
     * pairs that win. A {@code NONE} predicate indicates an alt containing an
     * unpredicated config which behaves as "always true." If !complete
     * then we stop at the first predicate that evaluates to true. This
     * includes pairs with null predicates.
     */
    protected BitSet evalSemanticContext(PredPrediction[] predPredictions, ParserRuleContext outerContext,
                                         bool complete)
    {
    BitSet predictions;
        foreach (pair; predPredictions) {
            if (pair.pred == SemanticContext.NONE ) {
                predictions.set(pair.alt, true);
                if (!complete) {
                    break;
                }
                continue;
            }

            bool fullCtx = false; // in dfa
            bool predicateEvaluationResult = evalSemanticContext(pair.pred, outerContext, pair.alt, fullCtx);
            debug(dfa_debug) {
                writefln("eval pred %1$s=%2$s", pair, predicateEvaluationResult);
            }

            if ( predicateEvaluationResult ) {
                debug(dfa_debug)
                    writefln("PREDICT ", pair.alt);
                predictions.set(pair.alt, true);
                if (!complete) {
                    break;
                }
            }
        }

        return predictions;
    }

    public bool evalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack,
                                    int alt, bool fullCtx)
    {
        return pred.eval(parser, parserCallStack);
    }

    protected void closureATN(ATNConfig config, ATNConfigSet configs, ref ATNConfig[] closureBusy,
                              bool collectPredicates, bool fullCtx, bool treatEofAsEpsilon)
    {
        int initialDepth = 0;
        closureCheckingStopState(config, configs, closureBusy, collectPredicates,
                                 fullCtx,
                                 initialDepth, treatEofAsEpsilon);
        assert (!fullCtx || !configs.dipsIntoOuterContext);
    }

    protected void closureCheckingStopState(ATNConfig config, ATNConfigSet configs, ref ATNConfig[] closureBusy,
                                            bool collectPredicates, bool fullCtx, int depth, bool treatEofAsEpsilon)
    {
        debug(ParserATNSimulator)
        {
            import std.stdio : writefln;
            writefln!"closure(%s)"( config);
        }

        if (cast(RuleStopState)config.state) {
            // We hit rule end. If we have context info, use it
            // run thru all possible stack tops in ctx
            if (!config.context.isEmpty) {
                for (int i = 0; i < config.context.size; i++) {
                    if (config.context.getReturnState(i) == PredictionContext.EMPTY_RETURN_STATE) {
                        if (fullCtx) {
                            configs.add(new ATNConfig(config, config.state,
                                                      cast(PredictionContext)PredictionContext.EMPTY), mergeCache);
                            continue;
                        }
                        else {
                            // we have no context info, just chase follow links (if greedy)
                            closure_(config, configs, closureBusy, collectPredicates,
                                     fullCtx, depth, treatEofAsEpsilon);
                        }
                        continue;
                    }
                    ATNState returnState = atn.states[config.context.getReturnState(i)];
                    PredictionContext newContext = config.context.getParent(i); // "pop" return state
                    ATNConfig c = new ATNConfig(returnState, config.alt, newContext,
                                                config.semanticContext);
                    // While we have context to pop back from, we may have
                    // gotten that context AFTER having falling off a rule.
                    // Make sure we track that we are now out of context.
                    //
                    // This assignment also propagates the
                    // isPrecedenceFilterSuppressed() value to the new
                    // configuration.
                    c.reachesIntoOuterContext = config.reachesIntoOuterContext;
                    assert(depth > int.min);
                    closureCheckingStopState(c, configs, closureBusy, collectPredicates,
                                             fullCtx, depth - 1, treatEofAsEpsilon);
                }
                return;
            }
            else if (fullCtx) {
                // reached end of start rule
                configs.add(config, mergeCache);
                return;
            }
            else {
                // else if we have no context info, just chase follow links (if greedy)
                debug(ParserATNSimulator)
                    writefln("FALLING off rule %s",
                             getRuleName(config.state.ruleIndex));
            }
        }
        closure_(config, configs, closureBusy, collectPredicates,
                 fullCtx, depth, treatEofAsEpsilon);
    }

    /**
     * Do the actual work of walking epsilon edges
     */
    protected void closure_(ATNConfig config, ATNConfigSet configs, ref ATNConfig[] closureBusy,
                            bool collectPredicates, bool fullCtx, int depth, bool treatEofAsEpsilon)
    {
    ATNState p = config.state;

        // optimization
        if (!p.onlyHasEpsilonTransitions) {
            configs.add(config, mergeCache);
            // make sure to not return here, because EOF transitions can act as
            // both epsilon transitions and non-epsilon transitions.
            //            if ( debug ) System.out.println("added config "+configs);
        }
        for (int i=0; i<p.getNumberOfTransitions(); i++) {
            if ( i==0 && canDropLoopEntryEdgeInLeftRecursiveRule(config))
                continue;
            Transition t = p.transition(i);
            bool continueCollecting =
                collectPredicates && !(cast(ActionTransition)t);
            ATNConfig c = getEpsilonTarget(config, t, continueCollecting,
                                           depth == 0, fullCtx, treatEofAsEpsilon);
            if (c !is null) {
                int newDepth = depth;
                if (cast(RuleStopState)config.state) {
                    assert (!fullCtx);

                    // target fell off end of rule; mark resulting c as having dipped into outer context
                    // We can't get here if incoming config was rule stop and we had context
                    // track how far we dip into outer context.  Might
                    // come in handy and we avoid evaluating context dependent
                    // preds if this is > 0.
                    if (_dfa !is null && _dfa.isPrecedenceDfa) {
                        int outermostPrecedenceReturn = (cast(EpsilonTransition)t).outermostPrecedenceReturn;
                        if (outermostPrecedenceReturn == _dfa.atnStartState.ruleIndex) {
                            c.setPrecedenceFilterSuppressed(true);
                        }
                    }

                    c.reachesIntoOuterContext++;

                    if (count(closureBusy, c) > 0) {
                        // avoid infinite recursion for right-recursive rules
                        continue;
                    }
                    closureBusy ~= c;

                    configs.dipsIntoOuterContext = true; // TODO: can remove? only care when we add to set per middle of this method
                    assert (newDepth > int.min);
                    newDepth--;
                    debug
                        writefln("dips into outer ctx: %s", c);
                }
                else {
                    if (!t.isEpsilon) {
                        if (count(closureBusy, c) > 0) {
                            // avoid infinite recursion for right-recursive rules
                            continue;
                        }
                        closureBusy ~= c;
                    }
                    if (cast(RuleTransition)t) {
                        // latch when newDepth goes negative - once we step out of the entry context we can't return
                        if (newDepth >= 0) {
                            newDepth++;
                        }
                    }
                }
                closureCheckingStopState(c, configs, closureBusy, continueCollecting,
                                         fullCtx, newDepth, treatEofAsEpsilon);
            }
        }

    }

    public string getRuleName(int index)
    {
        if (parser !is null && index>=0 ) return parser.getRuleNames()[index];
        return format("<rule %s>",index);
    }

    public ATNConfig getEpsilonTarget(ATNConfig config, Transition t, bool collectPredicates,
                                      bool inContext, bool fullCtx, bool treatEofAsEpsilon)
    {
        switch (t.getSerializationType()) {
        case TransitionStates.RULE:
            return ruleTransition(config, cast(RuleTransition)t);

        case TransitionStates.PRECEDENCE:
            return precedenceTransition(config, cast(PrecedencePredicateTransition)t, collectPredicates, inContext, fullCtx);

        case TransitionStates.PREDICATE:
            return predTransition(config, cast(PredicateTransition)t,
                                  collectPredicates,
                                  inContext,
                                  fullCtx);

        case TransitionStates.ACTION:
            return actionTransition(config, cast(ActionTransition)t);

        case TransitionStates.EPSILON:
            return new ATNConfig(config, t.target);

        case TransitionStates.ATOM:
        case TransitionStates.RANGE:
        case TransitionStates.SET:
            // EOF transitions act like epsilon transitions after the first EOF
            // transition is traversed
            if (treatEofAsEpsilon) {
                if (t.matches(TokenConstantDefinition.EOF, 0, 1)) {
                    return new ATNConfig(config, t.target);
                }
            }

            return null;

        default:
            return null;
        }

    }

    protected ATNConfig actionTransition(ATNConfig config, ActionTransition t)
    {
        debug(ParserATNSimulator)
            writefln!"ACTION edge %1$s:%2$s"(t.ruleIndex, t.actionIndex);
        return new ATNConfig(config, t.target);
    }

    public ATNConfig precedenceTransition(ATNConfig config, PrecedencePredicateTransition pt,
                                          bool collectPredicates, bool inContext, bool fullCtx)
    {
        debug(ParserATNSimulator) {
            writefln("PRED (collectPredicates=%1$s) %2$s" ~
                     ">=_p, ctx dependent=true", collectPredicates,  pt.precedence);
            if ( parser !is null ) {
                writefln!"context surrounding pred is %s"(
                         parser.getRuleInvocationStack);
            }
        }

        ATNConfig c = null;
        if (collectPredicates && inContext) {
            if ( fullCtx ) {
                // In full context mode, we can evaluate predicates on-the-fly
                // during closure, which dramatically reduces the size of
                // the config sets. It also obviates the need to test predicates
                // later during conflict resolution.
                size_t currentPosition = _input.index();
                _input.seek(to!int(_startIndex));
                bool predSucceeds = evalSemanticContext(pt.getPredicate(), _outerContext, config.alt, fullCtx);
                _input.seek(to!int(currentPosition));
                if ( predSucceeds ) {
                    c = new ATNConfig(config, pt.target); // no pred context
                }
            }
            else {
                SemanticContext newSemCtx =
                    SemanticContext.and(config.semanticContext, pt.getPredicate());
                c = new ATNConfig(config, pt.target, newSemCtx);
            }
        }
        else {
            c = new ATNConfig(config, pt.target);
        }

        debug(ParserATNSimulator)
            writefln!"precedenceTransition: config from pred transition=%s"(c);
        return c;

    }

    protected ATNConfig predTransition(ATNConfig config, PredicateTransition pt, bool collectPredicates,
                                       bool inContext, bool fullCtx)
    {
        debug(ParserATNSimulator) {
            writefln("PRED (collectPredicates=%1$s) %2$s:%3$s, ctx dependent=%4$s",
                     collectPredicates, pt.ruleIndex,
                     pt.predIndex, pt.isCtxDependent);
            if ( parser !is null ) {
                writefln("context surrounding pred is %s",
                         parser.getRuleInvocationStack());
            }
        }

        ATNConfig c = null;
        if ( collectPredicates &&
             (!pt.isCtxDependent || (pt.isCtxDependent&&inContext)) )
            {
                if ( fullCtx ) {
                    // In full context mode, we can evaluate predicates on-the-fly
                    // during closure, which dramatically reduces the size of
                    // the config sets. It also obviates the need to test predicates
                    // later during conflict resolution.
                    size_t currentPosition = _input.index();
                    _input.seek(to!int(_startIndex));
                    bool predSucceeds = evalSemanticContext(pt.getPredicate(), _outerContext, config.alt, fullCtx);
                    _input.seek(to!int(currentPosition));
                    if (predSucceeds) {
                        c = new ATNConfig(config, pt.target); // no pred context
                    }
                }
                else {
                    SemanticContext newSemCtx =
                        SemanticContext.and(config.semanticContext, pt.getPredicate());
                    c = new ATNConfig(config, pt.target, newSemCtx);
                }
            }
        else {
            c = new ATNConfig(config, pt.target);
        }

        debug(ParserATNSimulator)
        {
            writefln!"config from pred transition=%s"(c);
        }
        return c;
    }

    public ATNConfig ruleTransition(ATNConfig config, RuleTransition t)
    {
        debug(ParserATNSimulator)
        {
            writefln!"CALL rule %1$s, ctx=%2$s"(getRuleName(t.target.ruleIndex), config.context);
        }
        ATNState returnState = t.followState;
        PredictionContext newContext =
            SingletonPredictionContext.create(config.context, returnState.stateNumber);
        return new ATNConfig(config, t.target, newContext);
    }

    /**
     * Gets a {@link BitSet} containing the alternatives in {@code configs}
     * which are part of one or more conflicting alternative subsets.
     *
     * @param configs The {@link ATNConfigSet} to analyze.
     * @return The alternatives in {@code configs} which are part of one or more
     * conflicting alternative subsets. If {@code configs} does not contain any
     * conflicting subsets, this method returns an empty {@link BitSet}.
     */
    public BitSet getConflictingAlts(ATNConfigSet configs)
    {
        BitSet[] altsets = PredictionMode.getConflictingAltSubsets(configs);
        return PredictionMode.getAlts(altsets);
    }

    /**
     * Sam pointed out a problem with the previous definition, v3, of
     * ambiguous states. If we have another state associated with conflicting
     * alternatives, we should keep going. For example, the following grammar
     *
     * s : (ID | ID ID?) ';' ;
     *
     * When the ATN simulation reaches the state before ';', it has a DFA
     * state that looks like: [12|1|[], 6|2|[], 12|2|[]]. Naturally
     * 12|1|[] and 12|2|[] conflict, but we cannot stop processing this node
     * because alternative to has another way to continue, via [6|2|[]].
     * The key is that we have a single state that has config's only associated
     * with a single alternative, 2, and crucially the state transitions
     * among the configurations are all non-epsilon transitions. That means
     * we don't consider any conflicts that include alternative 2. So, we
     * ignore the conflict between alts 1 and 2. We ignore a set of
     * conflicting alts when there is an intersection with an alternative
     * associated with a single alt state in the state&rarr;config-list map.
     *
     * It's also the case that we might have two conflicting configurations but
     * also a 3rd nonconflicting configuration for a different alternative:
     * [1|1|[], 1|2|[], 8|3|[]]. This can come about from grammar:
     *
     * a : A | A | A B ;
     *
     * After matching input A, we reach the stop state for rule A, state 1.
     * State 8 is the state right before B. Clearly alternatives 1 and 2
     * conflict and no amount of further lookahead will separate the two.
     * However, alternative 3 will be able to continue and so we do not
     * stop working on this state. In the previous example, we're concerned
     * with states associated with the conflicting alternatives. Here alt
     * 3 is not associated with the conflicting configs, but since we can continue
     * looking for input reasonably, I don't declare the state done. We
     * ignore a set of conflicting alts when we have an alternative
     * that we still need to pursue.
     */
    public BitSet getConflictingAltsOrUniqueAlt(ATNConfigSet configs)
    {
    auto conflictingAlts = new BitSet(1);
        if (configs.uniqueAlt != ATN.INVALID_ALT_NUMBER) {
            conflictingAlts.set(configs.uniqueAlt, true);
        }
        else {
            *conflictingAlts = configs.conflictingAlts;
        }
        return *conflictingAlts;
    }

    public string getTokenName(int t)
    {
    if (t == TokenConstantDefinition.EOF) {
            return "EOF";
        }

        Vocabulary vocabulary = parser !is null ? parser.getVocabulary() : new VocabularyImpl(null, null, null);
        string displayName = vocabulary.getDisplayName(t);
        if (displayName == to!string(t)) {
            return displayName;
        }

        return displayName ~ "<" ~ to!string(t) ~ ">";
    }

    public string getLookaheadName(TokenStream input)
    {
        return getTokenName(input.LA(1));
    }

    /**
     * Used for debugging in adaptivePredict around execATN but I cut
     * it out for clarity now that alg. works well. We can leave this
     * "dead" code for a bit.
     */
    public void dumpDeadEndConfigs(NoViableAltException nvae)
    {
        debug
            writefln("dead end configs: ");
        foreach (ATNConfig c; nvae.getDeadEndConfigs().configs) {
            string trans = "no edges";
            if (c.state.getNumberOfTransitions > 0) {
                Transition t = c.state.transition(0);
                if (t.classinfo == AtomTransition.classinfo) {
                    AtomTransition at = cast(AtomTransition)t;
                    trans = "Atom " ~ getTokenName(at._label);
                }
                else if (t.classinfo == SetTransition.classinfo) {
                    SetTransition st = cast(SetTransition)t;
                    bool not = (st.classinfo ==  NotSetTransition.classinfo);
                    trans = (not?"~":"") ~ "Set "~ st.set.toString();
                }
            }
            debug
                writefln("%1$s:%2$s", c.toString(parser, true), trans);
        }
    }

    protected NoViableAltException noViableAlt(TokenStream input, ParserRuleContext outerContext,
                                               ATNConfigSet configs, size_t startIndex)
    {
        return new NoViableAltException(parser, input,
                                        input.get(to!int(startIndex)),
                                        input.LT(1),
                                        configs, outerContext);
    }

    protected static int getUniqueAlt(ATNConfigSet configs)
    {
    int alt = ATN.INVALID_ALT_NUMBER;
        foreach (ATNConfig c; configs.configs) {
            if (alt == ATN.INVALID_ALT_NUMBER) {
                alt = c.alt; // found first alt
            }
            else if (c.alt != alt) {
                return ATN.INVALID_ALT_NUMBER;
            }
        }
        return alt;
    }

    /**
     * Add an edge to the DFA, if possible. This method calls
     * {@link #addDFAState} to ensure the {@code to} state is present in the
     * DFA. If {@code from} is {@code null}, or if {@code t} is outside the
     * range of edges that can be represented in the DFA tables, this method
     * returns without adding the edge to the DFA.
     *
     * <p>If {@code to} is {@code null}, this method returns {@code null}.
     * Otherwise, this method returns the {@link DFAState} returned by calling
     * {@link #addDFAState} for the {@code to} state.</p>
     *
     * @param dfa The DFA
     * @param from The source state for the edge
     * @param t The input symbol
     * @param to The target state for the edge
     *
     * @return If {@code to} is {@code null}, this method returns {@code null};
     * otherwise this method returns the result of calling {@link #addDFAState}
     * on {@code to}
     */
    protected DFAState addDFAEdge(ref DFA dfa, DFAState from, size_t t, DFAState to)
    {
        debug(ParserATNSimulator) {
            writefln("\nEDGE %1$s -> %2$s upon %3$s", from, to, getTokenName(cast(int)t));
        }
        if (to is null) {
            return null;
        }
        to = addDFAState(dfa, to); // used existing if possible not incoming
        if (from is null || cast(int)t < -1 || t > atn.maxTokenType) {
            return to;
        }
        synchronized (from) {
            if (from.edges == null) {
                from.edges = new DFAState[atn.maxTokenType+1+1];
            }
            from.edges[t+1] = to; // connect
        }

        debug(ParserATNSimulator) {
            writefln!"DFA =\n%s, dfa.states = %s"(dfa.decision, dfa.states);
        }

        return to;
    }

    /**
     * Add state {@code D} to the DFA if it is not already present, and return
     * the actual instance stored in the DFA. If a state equivalent to {@code D}
     * is already in the DFA, the existing state is returned. Otherwise this
     * method returns {@code D} after adding it to the DFA.
     *
     * <p>If {@code D} is {@link #ERROR}, this method returns {@link #ERROR} and
     * does not change the DFA.</p>
     *
     * @param dfa The dfa
     * @param D The DFA state to add
     * @return The state stored in the DFA. This will be either the existing
     * state if {@code D} is already in the DFA, or {@code D} itself if the
     * state was not already present.
     */
    protected DFAState addDFAState(ref DFA dfa, DFAState D)
    {
        if (D == ERROR)
            return D;
        if (D in dfa.states)
            return dfa.states[D];
        D.stateNumber = to!int(dfa.states.length);
        if (!D.configs.readonly) {
            D.configs.optimizeConfigs(this);
            D.configs.readonly(true);
        }
        dfa.states[D] =  D;
        debug(ParserATNSimulator)
            writefln!"adding new DFA state: %1$s"(D);
        return D;
    }

    protected void reportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, ATNConfigSet configs,
                                               size_t startIndex, size_t stopIndex)
    {
        debug(retry_debug)
        {
            import antlr.v4.runtime.misc.Interval;
            Interval interval = Interval.of(startIndex, stopIndex);
            writefln("reportAttemptingFullContext decision=%1$s:%2$s, input=%3$s",
                     dfa.decision, configs,
                     parser.getTokenStream().getText(interval));
        }
        if (parser)
            parser.getErrorListenerDispatch.reportAttemptingFullContext(parser,
                                                                          dfa,
                                                                          startIndex,
                                                                          stopIndex,
                                                                          conflictingAlts,
                                                                          configs);
    }

    protected void reportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs,
                                            size_t startIndex, size_t stopIndex)
    {
        debug(retry_debug) {
            import antlr.v4.runtime.misc.Interval;
            Interval interval = Interval.of(startIndex, stopIndex);
            writefln("reportContextSensitivity decision=%1$s:%2$s, input=%3$s",
                     dfa.decision, configs, parser.getTokenStream().getText(interval));
        }
        if (parser !is null)
            parser.getErrorListenerDispatch().reportContextSensitivity(parser, dfa, startIndex, stopIndex, prediction, configs);
    }

    protected void reportAmbiguity(DFA dfa, DFAState D, size_t startIndex, size_t stopIndex, bool exact,
                                   BitSet ambigAlts, ATNConfigSet configs)
    {
    debug(retry_debug) {
            import antlr.v4.runtime.misc.Interval;
            Interval interval = Interval.of(startIndex, stopIndex);
            writefln("reportAmbiguity %1$s:%2$s, input=%3$s",
                     ambigAlts, configs, parser.getTokenStream().getText(interval));
        }
        if (parser !is null) parser.getErrorListenerDispatch().reportAmbiguity(parser, dfa, startIndex, stopIndex,
                                                                               exact, ambigAlts, configs);
    }

    public void setPredictionMode(PredictionModeConst mode)
    {
        this.mode = mode;
    }

    public PredictionModeConst getPredictionMode()
    {
        return mode;
    }

    public Parser getParser()
    {
        return parser;
    }

    /**
     * TODO implementation missing
     */
    protected bool canDropLoopEntryEdgeInLeftRecursiveRule(ATNConfig config)
    {
        auto TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT = true;
        if ( TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT )
            return false;
        return false;
    }

}
