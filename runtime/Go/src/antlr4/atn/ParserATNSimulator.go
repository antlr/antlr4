package atn

import (
    "antlr4"
    "fmt"
)

//
// The embodiment of the adaptive LL(*), ALL(*), parsing strategy.
//
// <p>
// The basic complexity of the adaptive strategy makes it harder to understand.
// We begin with ATN simulation to build paths in a DFA. Subsequent prediction
// requests go through the DFA first. If they reach a state without an edge for
// the current symbol, the algorithm fails over to the ATN simulation to
// complete the DFA path for the current input (until it finds a conflict state
// or uniquely predicting state).</p>
//
// <p>
// All of that is done without using the outer context because we want to create
// a DFA that is not dependent upon the rule invocation stack when we do a
// prediction. One DFA works in all contexts. We avoid using context not
// necessarily because it's slower, although it can be, but because of the DFA
// caching problem. The closure routine only considers the rule invocation stack
// created during prediction beginning in the decision rule. For example, if
// prediction occurs without invoking another rule's ATN, there are no context
// stacks in the configurations. When lack of context leads to a conflict, we
// don't know if it's an ambiguity or a weakness in the strong LL(*) parsing
// strategy (versus full LL(*)).</p>
//
// <p>
// When SLL yields a configuration set with conflict, we rewind the input and
// retry the ATN simulation, this time using full outer context without adding
// to the DFA. Configuration context stacks will be the full invocation stacks
// from the start rule. If we get a conflict using full context, then we can
// definitively say we have a true ambiguity for that input sequence. If we
// don't get a conflict, it implies that the decision is sensitive to the outer
// context. (It is not context-sensitive in the sense of context-sensitive
// grammars.)</p>
//
// <p>
// The next time we reach this DFA state with an SLL conflict, through DFA
// simulation, we will again retry the ATN simulation using full context mode.
// This is slow because we can't save the results and have to "interpret" the
// ATN each time we get that input.</p>
//
// <p>
// <strong>CACHING FULL CONTEXT PREDICTIONS</strong></p>
//
// <p>
// We could cache results from full context to predicted alternative easily and
// that saves a lot of time but doesn't work in presence of predicates. The set
// of visible predicates from the ATN start state changes depending on the
// context, because closure can fall off the end of a rule. I tried to cache
// tuples (stack context, semantic context, predicted alt) but it was slower
// than interpreting and much more complicated. Also required a huge amount of
// memory. The goal is not to create the world's fastest parser anyway. I'd like
// to keep this algorithm simple. By launching multiple threads, we can improve
// the speed of parsing across a large number of files.</p>
//
// <p>
// There is no strict ordering between the amount of input used by SLL vs LL,
// which makes it really hard to build a cache for full context. Let's say that
// we have input A B C that leads to an SLL conflict with full context X. That
// implies that using X we might only use A B but we could also use A B C D to
// resolve conflict. Input A B C D could predict alternative 1 in one position
// in the input and A B C E could predict alternative 2 in another position in
// input. The conflicting SLL configurations could still be non-unique in the
// full context prediction, which would lead us to requiring more input than the
// original A B C.	To make a	prediction cache work, we have to track	the exact
// input	used during the previous prediction. That amounts to a cache that maps
// X to a specific DFA for that context.</p>
//
// <p>
// Something should be done for left-recursive expression predictions. They are
// likely LL(1) + pred eval. Easier to do the whole SLL unless error and retry
// with full LL thing Sam does.</p>
//
// <p>
// <strong>AVOIDING FULL CONTEXT PREDICTION</strong></p>
//
// <p>
// We avoid doing full context retry when the outer context is empty, we did not
// dip into the outer context by falling off the end of the decision state rule,
// or when we force SLL mode.</p>
//
// <p>
// As an example of the not dip into outer context case, consider as super
// constructor calls versus func calls. One grammar might look like
// this:</p>
//
// <pre>
// ctorBody
//   : '{' superCall? stat* '}'
//
// </pre>
//
// <p>
// Or, you might see something like</p>
//
// <pre>
// stat
//   : superCall ''
//   | expression ''
//   | ...
//
// </pre>
//
// <p>
// In both cases I believe that no closure operations will dip into the outer
// context. In the first case ctorBody in the worst case will stop at the '}'.
// In the 2nd case it should stop at the ''. Both cases should stay within the
// entry rule and not dip into the outer context.</p>
//
// <p>
// <strong>PREDICATES</strong></p>
//
// <p>
// Predicates are always evaluated if present in either SLL or LL both. SLL and
// LL simulation deals with predicates differently. SLL collects predicates as
// it performs closure operations like ANTLR v3 did. It delays predicate
// evaluation until it reaches and accept state. This allows us to cache the SLL
// ATN simulation whereas, if we had evaluated predicates on-the-fly during
// closure, the DFA state configuration sets would be different and we couldn't
// build up a suitable DFA.</p>
//
// <p>
// When building a DFA accept state during ATN simulation, we evaluate any
// predicates and return the sole semantically valid alternative. If there is
// more than 1 alternative, we report an ambiguity. If there are 0 alternatives,
// we panic an exception. Alternatives without predicates act like they have
// true predicates. The simple way to think about it is to strip away all
// alternatives with false predicates and choose the minimum alternative that
// remains.</p>
//
// <p>
// When we start in the DFA and reach an accept state that's predicated, we test
// those and return the minimum semantically viable alternative. If no
// alternatives are viable, we panic an exception.</p>
//
// <p>
// During full LL ATN simulation, closure always evaluates predicates and
// on-the-fly. This is crucial to reducing the configuration set size during
// closure. It hits a landmine when parsing with the Java grammar, for example,
// without this on-the-fly evaluation.</p>
//
// <p>
// <strong>SHARING DFA</strong></p>
//
// <p>
// All instances of the same parser share the same decision DFAs through a
// static field. Each instance gets its own ATN simulator but they share the
// same {@link //decisionToDFA} field. They also share a
// {@link PredictionContextCache} object that makes sure that all
// {@link PredictionContext} objects are shared among the DFA states. This makes
// a big size difference.</p>
//
// <p>
// <strong>THREAD SAFETY</strong></p>
//
// <p>
// The {@link ParserATNSimulator} locks on the {@link //decisionToDFA} field when
// it adds a NewDFA object to that array. {@link //addDFAEdge}
// locks on the DFA for the current decision when setting the
// {@link DFAState//edges} field. {@link //addDFAState} locks on
// the DFA for the current decision when looking up a DFA state to see if it
// already exists. We must make sure that all requests to add DFA states that
// are equivalent result in the same shared DFA object. This is because lots of
// threads will be trying to update the DFA at once. The
// {@link //addDFAState} method also locks inside the DFA lock
// but this time on the shared context cache when it rebuilds the
// configurations' {@link PredictionContext} objects using cached
// subgraphs/nodes. No other locking occurs, even during DFA simulation. This is
// safe as long as we can guarantee that all threads referencing
// {@code s.edge[t]} get the same physical target {@link DFAState}, or
// {@code nil}. Once into the DFA, the DFA simulation does not reference the
// {@link DFA//states} map. It follows the {@link DFAState//edges} field to new
// targets. The DFA simulator will either find {@link DFAState//edges} to be
// {@code nil}, to be non-{@code nil} and {@code dfa.edges[t]} nil, or
// {@code dfa.edges[t]} to be non-nil. The
// {@link //addDFAEdge} method could be racing to set the field
// but in either case the DFA simulator works if {@code nil}, and requests ATN
// simulation. It could also race trying to get {@code dfa.edges[t]}, but either
// way it will work because it's not doing a test and set operation.</p>
//
// <p>
// <strong>Starting with SLL then failing to combined SLL/LL (Two-Stage
// Parsing)</strong></p>
//
// <p>
// Sam pointed out that if SLL does not give a syntax error, then there is no
// point in doing full LL, which is slower. We only have to try LL if we get a
// syntax error. For maximum speed, Sam starts the parser set to pure SLL
// mode with the {@link BailErrorStrategy}:</p>
//
// <pre>
// parser.{@link Parser//getInterpreter() getInterpreter()}.{@link //setPredictionMode setPredictionMode}{@code (}{@link PredictionMode//SLL}{@code )}
// parser.{@link Parser//setErrorHandler setErrorHandler}(New{@link BailErrorStrategy}())
// </pre>
//
// <p>
// If it does not get a syntax error, then we're done. If it does get a syntax
// error, we need to retry with the combined SLL/LL strategy.</p>
//
// <p>
// The reason this works is as follows. If there are no SLL conflicts, then the
// grammar is SLL (at least for that input set). If there is an SLL conflict,
// the full LL analysis must yield a set of viable alternatives which is a
// subset of the alternatives reported by SLL. If the LL set is a singleton,
// then the grammar is LL but not SLL. If the LL set is the same size as the SLL
// set, the decision is SLL. If the LL set has size &gt 1, then that decision
// is truly ambiguous on the current input. If the LL set is smaller, then the
// SLL conflict resolution might choose an alternative that the full LL would
// rule out as a possibility based upon better context information. If that's
// the case, then the SLL parse will definitely get an error because the full LL
// analysis says it's not viable. If SLL conflict resolution chooses an
// alternative within the LL set, them both SLL and LL would choose the same
// alternative because they both choose the minimum of multiple conflicting
// alternatives.</p>
//
// <p>
// Let's say we have a set of SLL conflicting alternatives {@code {1, 2, 3}} and
// a smaller LL set called <em>s</em>. If <em>s</em> is {@code {2, 3}}, then SLL
// parsing will get an error because SLL will pursue alternative 1. If
// <em>s</em> is {@code {1, 2}} or {@code {1, 3}} then both SLL and LL will
// choose the same alternative because alternative one is the minimum of either
// set. If <em>s</em> is {@code {2}} or {@code {3}} then SLL will get a syntax
// error. If <em>s</em> is {@code {1}} then SLL will succeed.</p>
//
// <p>
// Of course, if the input is invalid, then we will get an error for sure in
// both SLL and LL parsing. Erroneous input will therefore require 2 passes over
// the input.</p>
//

//var Utils = require('./../Utils')
var Set = Utils.Set
var BitSet = Utils.BitSet
var DoubleDict = Utils.DoubleDict
//var ATN = require('./ATN').ATN
//var ATNConfig = require('./ATNConfig').ATNConfig
//var ATNConfigSet = require('./ATNConfigSet').ATNConfigSet
//var Token = require('./../Token').Token
//var DFAState = require('./../dfa/DFAState').DFAState
//var PredPrediction = require('./../dfa/DFAState').PredPrediction
//var ATNSimulator = require('./ATNSimulator').ATNSimulator
//var PredictionMode = require('./PredictionMode').PredictionMode
//var RuleContext = require('./../RuleContext').RuleContext
//var ParserRuleContext = require('./../ParserRuleContext').ParserRuleContext
//var SemanticContext = require('./SemanticContext').SemanticContext
//var StarLoopEntryState = require('./ATNState').StarLoopEntryState
//var RuleStopState = require('./ATNState').RuleStopState
//var PredictionContext = require('./../PredictionContext').PredictionContext
//var Interval = require('./../IntervalSet').Interval
//var Transitions = require('./Transition')
var Transition = Transitions.Transition
var SetTransition = Transitions.SetTransition
var NotSetTransition = Transitions.NotSetTransition
var RuleTransition = Transitions.RuleTransition
var ActionTransition = Transitions.ActionTransition
//var NoViableAltException = require('./../error/Errors').NoViableAltException

//var SingletonPredictionContext = require('./../PredictionContext').SingletonPredictionContext
//var predictionContextFromRuleContext = require('./../PredictionContext').predictionContextFromRuleContext

func ParserATNSimulator(parser, atn, decisionToDFA, sharedContextCache) {
	ATNSimulator.call(this, atn, sharedContextCache)
    this.parser = parser
    this.decisionToDFA = decisionToDFA
    // SLL, LL, or LL + exact ambig detection?//
    this.predictionMode = PredictionModeLL
    // LAME globals to avoid parameters!!!!! I need these down deep in predTransition
    this._input = nil
    this._startIndex = 0
    this._outerContext = nil
    this._dfa = nil
    // Each prediction operation uses a cache for merge of prediction contexts.
    //  Don't keep around as it wastes huge amounts of memory. DoubleKeyMap
    //  isn't synchronized but we're ok since two threads shouldn't reuse same
    //  parser/atnsim object because it can only handle one input at a time.
    //  This maps graphs a and b to merged result c. (a,b)&rarrc. We can avoid
    //  the merge if we ever see a and b again.  Note that (b,a)&rarrc should
    //  also be examined during cache lookup.
    //
    this.mergeCache = nil
    return this
}

//ParserATNSimulator.prototype = Object.create(ATNSimulator.prototype)
//ParserATNSimulator.prototype.constructor = ParserATNSimulator

ParserATNSimulator.prototype.debug = false
ParserATNSimulator.prototype.debug_list_atn_decisions = false
ParserATNSimulator.prototype.dfa_debug = false
ParserATNSimulator.prototype.retry_debug = false

func (this *ParserATNSimulator) reset() {
}

func (this *ParserATNSimulator) adaptivePredict(input, decision, outerContext) {
    if (this.debug || this.debug_list_atn_decisions) {
        fmt.Println("adaptivePredict decision " + decision +
                               " exec LA(1)==" + this.getLookaheadName(input) +
                               " line " + input.LT(1).line + ":" +
                               input.LT(1).column)
    }
    this._input = input
    this._startIndex = input.index
    this._outerContext = outerContext
    
    var dfa = this.decisionToDFA[decision]
    this._dfa = dfa
    var m = input.mark()
    var index = input.index

    // Now we are certain to have a specific decision's DFA
    // But, do we still need an initial state?
    try {
        var s0
        if (dfa.precedenceDfa) {
            // the start state for a precedence DFA depends on the current
            // parser precedence, and is provided by a DFA method.
            s0 = dfa.getPrecedenceStartState(this.parser.getPrecedence())
        } else {
            // the start state for a "regular" DFA is just s0
            s0 = dfa.s0
        }
        if (s0==nil) {
            if (outerContext==nil) {
                outerContext = RuleContext.EMPTY
            }
            if (this.debug || this.debug_list_atn_decisions) {
                fmt.Println("predictATN decision " + dfa.decision +
                                   " exec LA(1)==" + this.getLookaheadName(input) +
                                   ", outerContext=" + outerContext.toString(this.parser.ruleNames))
            }
            // If this is not a precedence DFA, we check the ATN start state
            // to determine if this ATN start state is the decision for the
            // closure block that determines whether a precedence rule
            // should continue or complete.
            //
            if (!dfa.precedenceDfa && (dfa.atnStartState instanceof StarLoopEntryState)) {
                if (dfa.atnStartState.precedenceRuleDecision) {
                    dfa.setPrecedenceDfa(true)
                }
            }
            var fullCtx = false
            var s0_closure = this.computeStartState(dfa.atnStartState, RuleContext.EMPTY, fullCtx)

            if( dfa.precedenceDfa) {
                // If this is a precedence DFA, we use applyPrecedenceFilter
                // to convert the computed start state to a precedence start
                // state. We then use DFA.setPrecedenceStartState to set the
                // appropriate start state for the precedence level rather
                // than simply setting DFA.s0.
                //
                s0_closure = this.applyPrecedenceFilter(s0_closure)
                s0 = this.addDFAState(dfa, NewDFAState(nil, s0_closure))
                dfa.setPrecedenceStartState(this.parser.getPrecedence(), s0)
            } else {
                s0 = this.addDFAState(dfa, NewDFAState(nil, s0_closure))
                dfa.s0 = s0
            }
        }
        var alt = this.execATN(dfa, s0, input, index, outerContext)
        if (this.debug) {
            fmt.Println("DFA after predictATN: " + dfa.toString(this.parser.literalNames))
        }
        return alt
    } finally {
        this._dfa = nil
        this.mergeCache = nil // wack cache after each prediction
        input.seek(index)
        input.release(m)
    }
}
// Performs ATN simulation to compute a predicted alternative based
//  upon the remaining input, but also updates the DFA cache to avoid
//  having to traverse the ATN again for the same input sequence.

// There are some key conditions we're looking for after computing a new
// set of ATN configs (proposed DFA state):
      // if the set is empty, there is no viable alternative for current symbol
      // does the state uniquely predict an alternative?
      // does the state have a conflict that would prevent us from
      //   putting it on the work list?

// We also have some key operations to do:
      // add an edge from previous DFA state to potentially NewDFA state, D,
      //   upon current symbol but only if adding to work list, which means in all
      //   cases except no viable alternative (and possibly non-greedy decisions?)
      // collecting predicates and adding semantic context to DFA accept states
      // adding rule context to context-sensitive DFA accept states
      // consuming an input symbol
      // reporting a conflict
      // reporting an ambiguity
      // reporting a context sensitivity
      // reporting insufficient predicates

// cover these cases:
//    dead end
//    single alt
//    single alt + preds
//    conflict
//    conflict + preds
//
ParserATNSimulator.prototype.execATN = function(dfa, s0, input, startIndex, outerContext ) {
    if (this.debug || this.debug_list_atn_decisions) {
        fmt.Println("execATN decision " + dfa.decision +
                " exec LA(1)==" + this.getLookaheadName(input) +
                " line " + input.LT(1).line + ":" + input.LT(1).column)
    }
    var alt
    var previousD = s0

    if (this.debug) {
        fmt.Println("s0 = " + s0)
    }
    var t = input.LA(1)
    while(true) { // while more work
        var D = this.getExistingTargetState(previousD, t)
        if(D==nil) {
            D = this.computeTargetState(dfa, previousD, t)
        }
        if(D==ATNSimulator.ERROR) {
            // if any configs in previous dipped into outer context, that
            // means that input up to t actually finished entry rule
            // at least for SLL decision. Full LL doesn't dip into outer
            // so don't need special case.
            // We will get an error no matter what so delay until after
            // decision better error message. Also, no reachable target
            // ATN states in SLL implies LL will also get nowhere.
            // If conflict in states that dip out, choose min since we
            // will get error no matter what.
            var e = this.noViableAlt(input, outerContext, previousD.configs, startIndex)
            input.seek(startIndex)
            alt = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previousD.configs, outerContext)
            if(alt!=ATN.INVALID_ALT_NUMBER) {
                return alt
            } else {
                panic e
            }
        }
        if(D.requiresFullContext && this.predictionMode != PredictionModeSLL) {
            // IF PREDS, MIGHT RESOLVE TO SINGLE ALT => SLL (or syntax error)
            var conflictingAlts = nil
            if (D.predicates!=nil) {
                if (this.debug) {
                    fmt.Println("DFA state has preds in DFA sim LL failover")
                }
                var conflictIndex = input.index
                if(conflictIndex != startIndex) {
                    input.seek(startIndex)
                }
                conflictingAlts = this.evalSemanticContext(D.predicates, outerContext, true)
                if (conflictingAlts.length==1) {
                    if(this.debug) {
                        fmt.Println("Full LL avoided")
                    }
                    return conflictingAlts.minValue()
                }
                if (conflictIndex != startIndex) {
                    // restore the index so reporting the fallback to full
                    // context occurs with the index at the correct spot
                    input.seek(conflictIndex)
                }
            }
            if (this.dfa_debug) {
                fmt.Println("ctx sensitive state " + outerContext +" in " + D)
            }
            var fullCtx = true
            var s0_closure = this.computeStartState(dfa.atnStartState, outerContext, fullCtx)
            this.reportAttemptingFullContext(dfa, conflictingAlts, D.configs, startIndex, input.index)
            alt = this.execATNWithFullContext(dfa, D, s0_closure, input, startIndex, outerContext)
            return alt
        }
        if (D.isAcceptState) {
            if (D.predicates==nil) {
                return D.prediction
            }
            var stopIndex = input.index
            input.seek(startIndex)
            var alts = this.evalSemanticContext(D.predicates, outerContext, true)
            if (alts.length==0) {
                panic this.noViableAlt(input, outerContext, D.configs, startIndex)
            } else if (alts.length==1) {
                return alts.minValue()
            } else {
                // report ambiguity after predicate evaluation to make sure the correct set of ambig alts is reported.
                this.reportAmbiguity(dfa, D, startIndex, stopIndex, false, alts, D.configs)
                return alts.minValue()
            }
        }
        previousD = D

        if (t != TokenEOF) {
            input.consume()
            t = input.LA(1)
        }
    }
}
//
// Get an existing target state for an edge in the DFA. If the target state
// for the edge has not yet been computed or is otherwise not available,
// this method returns {@code nil}.
//
// @param previousD The current DFA state
// @param t The next input symbol
// @return The existing target DFA state for the given input symbol
// {@code t}, or {@code nil} if the target state for this edge is not
// already cached
//
func (this *ParserATNSimulator) getExistingTargetState(previousD, t) {
    var edges = previousD.edges
    if (edges==nil) {
        return nil
    } else {
        return edges[t + 1] || nil
    }
}
//
// Compute a target state for an edge in the DFA, and attempt to add the
// computed state and corresponding edge to the DFA.
//
// @param dfa The DFA
// @param previousD The current DFA state
// @param t The next input symbol
//
// @return The computed target DFA state for the given input symbol
// {@code t}. If {@code t} does not lead to a valid DFA state, this method
// returns {@link //ERROR}.
//
func (this *ParserATNSimulator) computeTargetState(dfa, previousD, t) {
   var reach = this.computeReachSet(previousD.configs, t, false)
    if(reach==nil) {
        this.addDFAEdge(dfa, previousD, t, ATNSimulator.ERROR)
        return ATNSimulator.ERROR
    }
    // create Newtarget state we'll add to DFA after it's complete
    var D = NewDFAState(nil, reach)

    var predictedAlt = this.getUniqueAlt(reach)

    if (this.debug) {
        var altSubSets = PredictionModegetConflictingAltSubsets(reach)
        fmt.Println("SLL altSubSets=" + Utils.arrayToString(altSubSets) +
                    ", previous=" + previousD.configs +
                    ", configs=" + reach +
                    ", predict=" + predictedAlt +
                    ", allSubsetsConflict=" +
                    PredictionModeallSubsetsConflict(altSubSets) + ", conflictingAlts=" +
                    this.getConflictingAlts(reach))
    }
    if (predictedAlt!=ATN.INVALID_ALT_NUMBER) {
        // NO CONFLICT, UNIQUELY PREDICTED ALT
        D.isAcceptState = true
        D.configs.uniqueAlt = predictedAlt
        D.prediction = predictedAlt
    } else if (PredictionModehasSLLConflictTerminatingPrediction(this.predictionMode, reach)) {
        // MORE THAN ONE VIABLE ALTERNATIVE
        D.configs.conflictingAlts = this.getConflictingAlts(reach)
        D.requiresFullContext = true
        // in SLL-only mode, we will stop at this state and return the minimum alt
        D.isAcceptState = true
        D.prediction = D.configs.conflictingAlts.minValue()
    }
    if (D.isAcceptState && D.configs.hasSemanticContext) {
        this.predicateDFAState(D, this.atn.getDecisionState(dfa.decision))
        if( D.predicates!=nil) {
            D.prediction = ATN.INVALID_ALT_NUMBER
        }
    }
    // all adds to dfa are done after we've created full D state
    D = this.addDFAEdge(dfa, previousD, t, D)
    return D
}

func (this *ParserATNSimulator) predicateDFAState(dfaState, decisionState) {
    // We need to test all predicates, even in DFA states that
    // uniquely predict alternative.
    var nalts = decisionState.transitions.length
    // Update DFA so reach becomes accept state with (predicate,alt)
    // pairs if preds found for conflicting alts
    var altsToCollectPredsFrom = this.getConflictingAltsOrUniqueAlt(dfaState.configs)
    var altToPred = this.getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts)
    if (altToPred!=nil) {
        dfaState.predicates = this.getPredicatePredictions(altsToCollectPredsFrom, altToPred)
        dfaState.prediction = ATN.INVALID_ALT_NUMBER // make sure we use preds
    } else {
        // There are preds in configs but they might go away
        // when OR'd together like {p}? || NONE == NONE. If neither
        // alt has preds, resolve to min alt
        dfaState.prediction = altsToCollectPredsFrom.minValue()
    }
}

// comes back with reach.uniqueAlt set to a valid alt
ParserATNSimulator.prototype.execATNWithFullContext = function(dfa, D, // how far we got before failing over
                                     s0,
                                     input,
                                     startIndex,
                                     outerContext) {
    if (this.debug || this.debug_list_atn_decisions) {
        fmt.Println("execATNWithFullContext "+s0)
    }
    var fullCtx = true
    var foundExactAmbig = false
    var reach = nil
    var previous = s0
    input.seek(startIndex)
    var t = input.LA(1)
    var predictedAlt = -1
    for (true) { // while more work
        reach = this.computeReachSet(previous, t, fullCtx)
        if (reach==nil) {
            // if any configs in previous dipped into outer context, that
            // means that input up to t actually finished entry rule
            // at least for LL decision. Full LL doesn't dip into outer
            // so don't need special case.
            // We will get an error no matter what so delay until after
            // decision better error message. Also, no reachable target
            // ATN states in SLL implies LL will also get nowhere.
            // If conflict in states that dip out, choose min since we
            // will get error no matter what.
            var e = this.noViableAlt(input, outerContext, previous, startIndex)
            input.seek(startIndex)
            var alt = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previous, outerContext)
            if(alt!=ATN.INVALID_ALT_NUMBER) {
                return alt
            } else {
                panic e
            }
        }
        var altSubSets = PredictionModegetConflictingAltSubsets(reach)
        if(this.debug) {
            fmt.Println("LL altSubSets=" + altSubSets + ", predict=" +
                  PredictionModegetUniqueAlt(altSubSets) + ", resolvesToJustOneViableAlt=" +
                  PredictionModeresolvesToJustOneViableAlt(altSubSets))
        }
        reach.uniqueAlt = this.getUniqueAlt(reach)
        // unique prediction?
        if(reach.uniqueAlt!=ATN.INVALID_ALT_NUMBER) {
            predictedAlt = reach.uniqueAlt
            break
        } else if (this.predictionMode != PredictionModeLL_EXACT_AMBIG_DETECTION) {
            predictedAlt = PredictionModeresolvesToJustOneViableAlt(altSubSets)
            if(predictedAlt != ATN.INVALID_ALT_NUMBER) {
                break
            }
        } else {
            // In exact ambiguity mode, we never try to terminate early.
            // Just keeps scarfing until we know what the conflict is
            if (PredictionModeallSubsetsConflict(altSubSets) && PredictionModeallSubsetsEqual(altSubSets)) {
                foundExactAmbig = true
                predictedAlt = PredictionModegetSingleViableAlt(altSubSets)
                break
            }
            // else there are multiple non-conflicting subsets or
            // we're not sure what the ambiguity is yet.
            // So, keep going.
        }
        previous = reach
        if( t != TokenEOF) {
            input.consume()
            t = input.LA(1)
        }
    }
    // If the configuration set uniquely predicts an alternative,
    // without conflict, then we know that it's a full LL decision
    // not SLL.
    if (reach.uniqueAlt != ATN.INVALID_ALT_NUMBER ) {
        this.reportContextSensitivity(dfa, predictedAlt, reach, startIndex, input.index)
        return predictedAlt
    }
    // We do not check predicates here because we have checked them
    // on-the-fly when doing full context prediction.

    //
    // In non-exact ambiguity detection mode, we might	actually be able to
    // detect an exact ambiguity, but I'm not going to spend the cycles
    // needed to check. We only emit ambiguity warnings in exact ambiguity
    // mode.
    //
    // For example, we might know that we have conflicting configurations.
    // But, that does not mean that there is no way forward without a
    // conflict. It's possible to have nonconflicting alt subsets as in:

    // altSubSets=[{1, 2}, {1, 2}, {1}, {1, 2}]

    // from
    //
    //    [(17,1,[5 $]), (13,1,[5 10 $]), (21,1,[5 10 $]), (11,1,[$]),
    //     (13,2,[5 10 $]), (21,2,[5 10 $]), (11,2,[$])]
    //
    // In this case, (17,1,[5 $]) indicates there is some next sequence that
    // would resolve this without conflict to alternative 1. Any other viable
    // next sequence, however, is associated with a conflict.  We stop
    // looking for input because no amount of further lookahead will alter
    // the fact that we should predict alternative 1.  We just can't say for
    // sure that there is an ambiguity without looking further.

    this.reportAmbiguity(dfa, D, startIndex, input.index, foundExactAmbig, nil, reach)

    return predictedAlt
}

func (this *ParserATNSimulator) computeReachSet(closure, t, fullCtx) {
    if (this.debug) {
        fmt.Println("in computeReachSet, starting closure: " + closure)
    }
    if( this.mergeCache==nil) {
        this.mergeCache = NewDoubleDict()
    }
    var intermediate = NewATNConfigSet(fullCtx)

    // Configurations already in a rule stop state indicate reaching the end
    // of the decision rule (local context) or end of the start rule (full
    // context). Once reached, these configurations are never updated by a
    // closure operation, so they are handled separately for the performance
    // advantage of having a smaller intermediate set when calling closure.
    //
    // For full-context reach operations, separate handling is required to
    // ensure that the alternative matching the longest overall sequence is
    // chosen when multiple such configurations can match the input.
    
    var skippedStopStates = nil

    // First figure out where we can reach on input t
    for i:=0; i<len(closure.items); i++ {
        var c = closure.items[i]
        if(this.debug) {
            fmt.Println("testing " + this.getTokenName(t) + " at " + c)
        }
        if (c.state instanceof RuleStopState) {
            if (fullCtx || t == TokenEOF) {
                if (skippedStopStates==nil) {
                    skippedStopStates = []
                }
                skippedStopStates.push(c)
                if(this.debug) {
                    fmt.Println("added " + c + " to skippedStopStates")
                }
            }
            continue
        }
        for(var j=0j<c.state.transitions.lengthj++) {
            var trans = c.state.transitions[j]
            var target = this.getReachableTarget(trans, t)
            if (target!=nil) {
                var cfg = NewATNConfig({state:target}, c)
                intermediate.add(cfg, this.mergeCache)
                if(this.debug) {
                    fmt.Println("added " + cfg + " to intermediate")
                }
            }
        }
    }
    // Now figure out where the reach operation can take us...
    var reach = nil

    // This block optimizes the reach operation for intermediate sets which
    // trivially indicate a termination state for the overall
    // adaptivePredict operation.
    //
    // The conditions assume that intermediate
    // contains all configurations relevant to the reach set, but this
    // condition is not true when one or more configurations have been
    // withheld in skippedStopStates, or when the current symbol is EOF.
    //
    if (skippedStopStates==nil && t!=TokenEOF) {
        if (intermediate.items.length==1) {
            // Don't pursue the closure if there is just one state.
            // It can only have one alternative just add to result
            // Also don't pursue the closure if there is unique alternative
            // among the configurations.
            reach = intermediate
        } else if (this.getUniqueAlt(intermediate)!=ATN.INVALID_ALT_NUMBER) {
            // Also don't pursue the closure if there is unique alternative
            // among the configurations.
            reach = intermediate
        }
    }
    // If the reach set could not be trivially determined, perform a closure
    // operation on the intermediate set to compute its initial value.
    //
    if (reach==nil) {
        reach = NewATNConfigSet(fullCtx)
        var closureBusy = NewSet()
        var treatEofAsEpsilon = t == TokenEOF
        for (var k=0 k<intermediate.items.lengthk++) {
            this.closure(intermediate.items[k], reach, closureBusy, false, fullCtx, treatEofAsEpsilon)
        }
    }
    if (t == TokenEOF) {
        // After consuming EOF no additional input is possible, so we are
        // only interested in configurations which reached the end of the
        // decision rule (local context) or end of the start rule (full
        // context). Update reach to contain only these configurations. This
        // handles both explicit EOF transitions in the grammar and implicit
        // EOF transitions following the end of the decision or start rule.
        //
        // When reach==intermediate, no closure operation was performed. In
        // this case, removeAllConfigsNotInRuleStopState needs to check for
        // reachable rule stop states as well as configurations already in
        // a rule stop state.
        //
        // This is handled before the configurations in skippedStopStates,
        // because any configurations potentially added from that list are
        // already guaranteed to meet this condition whether or not it's
        // required.
        //
        reach = this.removeAllConfigsNotInRuleStopState(reach, reach == intermediate)
    }
    // If skippedStopStates!=nil, then it contains at least one
    // configuration. For full-context reach operations, these
    // configurations reached the end of the start rule, in which case we
    // only add them back to reach if no configuration during the current
    // closure operation reached such a state. This ensures adaptivePredict
    // chooses an alternative matching the longest overall sequence when
    // multiple alternatives are viable.
    //
    if (skippedStopStates!=nil && ( (! fullCtx) || (! PredictionModehasConfigInRuleStopState(reach)))) {
        for (var l=0 l<skippedStopStates.lengthl++) {
            reach.add(skippedStopStates[l], this.mergeCache)
        }
    }
    if (reach.items.length==0) {
        return nil
    } else {
        return reach
    }
}
//
// Return a configuration set containing only the configurations from
// {@code configs} which are in a {@link RuleStopState}. If all
// configurations in {@code configs} are already in a rule stop state, this
// method simply returns {@code configs}.
//
// <p>When {@code lookToEndOfRule} is true, this method uses
// {@link ATN//nextTokens} for each configuration in {@code configs} which is
// not already in a rule stop state to see if a rule stop state is reachable
// from the configuration via epsilon-only transitions.</p>
//
// @param configs the configuration set to update
// @param lookToEndOfRule when true, this method checks for rule stop states
// reachable by epsilon-only transitions from each configuration in
// {@code configs}.
//
// @return {@code configs} if all configurations in {@code configs} are in a
// rule stop state, otherwise return a Newconfiguration set containing only
// the configurations from {@code configs} which are in a rule stop state
//
func (this *ParserATNSimulator) removeAllConfigsNotInRuleStopState(configs, lookToEndOfRule) {
    if (PredictionModeallConfigsInRuleStopStates(configs)) {
        return configs
    }
    var result = NewATNConfigSet(configs.fullCtx)
    for(var i=0 i<configs.items.lengthi++) {
        var config = configs.items[i]
        if (config.state instanceof RuleStopState) {
            result.add(config, this.mergeCache)
            continue
        }
        if (lookToEndOfRule && config.state.epsilonOnlyTransitions) {
            var nextTokens = this.atn.nextTokens(config.state)
            if (nextTokens.contains(TokenEpsilon)) {
                var endOfRuleState = this.atn.ruleToStopState[config.state.ruleIndex]
                result.add(NewATNConfig({state:endOfRuleState}, config), this.mergeCache)
            }
        }
    }
    return result
}

func (this *ParserATNSimulator) computeStartState(p, ctx, fullCtx) {
    // always at least the implicit call to start rule
    var initialContext = predictionContextFromRuleContext(this.atn, ctx)
    var configs = NewATNConfigSet(fullCtx)
    for(var i=0i<p.transitions.lengthi++) {
        var target = p.transitions[i].target
        var c = NewATNConfig({ state:target, alt:i+1, context:initialContext }, nil)
        var closureBusy = NewSet()
        this.closure(c, configs, closureBusy, true, fullCtx, false)
    }
    return configs
}

//
// This method transforms the start state computed by
// {@link //computeStartState} to the special start state used by a
// precedence DFA for a particular precedence value. The transformation
// process applies the following changes to the start state's configuration
// set.
//
// <ol>
// <li>Evaluate the precedence predicates for each configuration using
// {@link SemanticContext//evalPrecedence}.</li>
// <li>Remove all configurations which predict an alternative greater than
// 1, for which another configuration that predicts alternative 1 is in the
// same ATN state with the same prediction context. This transformation is
// valid for the following reasons:
// <ul>
// <li>The closure block cannot contain any epsilon transitions which bypass
// the body of the closure, so all states reachable via alternative 1 are
// part of the precedence alternatives of the transformed left-recursive
// rule.</li>
// <li>The "primary" portion of a left recursive rule cannot contain an
// epsilon transition, so the only way an alternative other than 1 can exist
// in a state that is also reachable via alternative 1 is by nesting calls
// to the left-recursive rule, with the outer calls not being at the
// preferred precedence level.</li>
// </ul>
// </li>
// </ol>
//
// <p>
// The prediction context must be considered by this filter to address
// situations like the following.
// </p>
// <code>
// <pre>
// grammar TA
// prog: statement* EOF
// statement: letterA | statement letterA 'b'
// letterA: 'a'
// </pre>
// </code>
// <p>
// If the above grammar, the ATN state immediately before the token
// reference {@code 'a'} in {@code letterA} is reachable from the left edge
// of both the primary and closure blocks of the left-recursive rule
// {@code statement}. The prediction context associated with each of these
// configurations distinguishes between them, and prevents the alternative
// which stepped out to {@code prog} (and then back in to {@code statement}
// from being eliminated by the filter.
// </p>
//
// @param configs The configuration set computed by
// {@link //computeStartState} as the start state for the DFA.
// @return The transformed configuration set representing the start state
// for a precedence DFA at a particular precedence level (determined by
// calling {@link Parser//getPrecedence}).
//
func (this *ParserATNSimulator) applyPrecedenceFilter(configs) {
	var config
	var statesFromAlt1 = []
    var configSet = NewATNConfigSet(configs.fullCtx)
    for(var i=0 i<configs.items.length i++) {
        config = configs.items[i]
        // handle alt 1 first
        if (config.alt != 1) {
            continue
        }
        var updatedContext = config.semanticContext.evalPrecedence(this.parser, this._outerContext)
        if (updatedContext==nil) {
            // the configuration was eliminated
            continue
        }
        statesFromAlt1[config.state.stateNumber] = config.context
        if (updatedContext != config.semanticContext) {
            configSet.add(NewATNConfig({semanticContext:updatedContext}, config), this.mergeCache)
        } else {
            configSet.add(config, this.mergeCache)
        }
    }
    for(i=0 i<configs.items.length i++) {
        config = configs.items[i]
        if (config.alt == 1) {
            // already handled
            continue
        }
        // In the future, this elimination step could be updated to also
        // filter the prediction context for alternatives predicting alt>1
        // (basically a graph subtraction algorithm).
		if (!config.precedenceFilterSuppressed) {
            var context = statesFromAlt1[config.state.stateNumber] || nil
            if (context!=nil && context.equals(config.context)) {
                // eliminated
                continue
            }
		}
        configSet.add(config, this.mergeCache)
    }
    return configSet
}

func (this *ParserATNSimulator) getReachableTarget(trans, ttype) {
    if (trans.matches(ttype, 0, this.atn.maxTokenType)) {
        return trans.target
    } else {
        return nil
    }
}

func (this *ParserATNSimulator) getPredsForAmbigAlts(ambigAlts, configs, nalts) {
    // REACH=[1|1|[]|0:0, 1|2|[]|0:1]
    // altToPred starts as an array of all nil contexts. The entry at index i
    // corresponds to alternative i. altToPred[i] may have one of three values:
    //   1. nil: no ATNConfig c is found such that c.alt==i
    //   2. SemanticContext.NONE: At least one ATNConfig c exists such that
    //      c.alt==i and c.semanticContext==SemanticContext.NONE. In other words,
    //      alt i has at least one unpredicated config.
    //   3. Non-NONE Semantic Context: There exists at least one, and for all
    //      ATNConfig c such that c.alt==i, c.semanticContext!=SemanticContext.NONE.
    //
    // From this, it is clear that NONE||anything==NONE.
    //
    var altToPred = []
    for(var i=0i<configs.items.lengthi++) {
        var c = configs.items[i]
        if(ambigAlts.contains( c.alt )) {
            altToPred[c.alt] = SemanticContext.orContext(altToPred[c.alt] || nil, c.semanticContext)
        }
    }
    var nPredAlts = 0
    for (i =1i< nalts+1i++) {
        var pred = altToPred[i] || nil
        if (pred==nil) {
            altToPred[i] = SemanticContext.NONE
        } else if (pred != SemanticContext.NONE) {
            nPredAlts += 1
        }
    }
    // nonambig alts are nil in altToPred
    if (nPredAlts==0) {
        altToPred = nil
    }
    if (this.debug) {
        fmt.Println("getPredsForAmbigAlts result " + Utils.arrayToString(altToPred))
    }
    return altToPred
}

func (this *ParserATNSimulator) getPredicatePredictions(ambigAlts, altToPred) {
    var pairs = []
    var containsPredicate = false
    for (var i=1 i<altToPred.lengthi++) {
        var pred = altToPred[i]
        // unpredicated is indicated by SemanticContext.NONE
        if( ambigAlts!=nil && ambigAlts.contains( i )) {
            pairs.push(NewPredPrediction(pred, i))
        }
        if (pred != SemanticContext.NONE) {
            containsPredicate = true
        }
    }
    if (! containsPredicate) {
        return nil
    }
    return pairs
}

//
// This method is used to improve the localization of error messages by
// choosing an alternative rather than panicing a
// {@link NoViableAltException} in particular prediction scenarios where the
// {@link //ERROR} state was reached during ATN simulation.
//
// <p>
// The default implementation of this method uses the following
// algorithm to identify an ATN configuration which successfully parsed the
// decision entry rule. Choosing such an alternative ensures that the
// {@link ParserRuleContext} returned by the calling rule will be complete
// and valid, and the syntax error will be reported later at a more
// localized location.</p>
//
// <ul>
// <li>If a syntactically valid path or paths reach the end of the decision rule and
// they are semantically valid if predicated, return the min associated alt.</li>
// <li>Else, if a semantically invalid but syntactically valid path exist
// or paths exist, return the minimum associated alt.
// </li>
// <li>Otherwise, return {@link ATN//INVALID_ALT_NUMBER}.</li>
// </ul>
//
// <p>
// In some scenarios, the algorithm described above could predict an
// alternative which will result in a {@link FailedPredicateException} in
// the parser. Specifically, this could occur if the <em>only</em> configuration
// capable of successfully parsing to the end of the decision rule is
// blocked by a semantic predicate. By choosing this alternative within
// {@link //adaptivePredict} instead of panicing a
// {@link NoViableAltException}, the resulting
// {@link FailedPredicateException} in the parser will identify the specific
// predicate which is preventing the parser from successfully parsing the
// decision rule, which helps developers identify and correct logic errors
// in semantic predicates.
// </p>
//
// @param configs The ATN configurations which were valid immediately before
// the {@link //ERROR} state was reached
// @param outerContext The is the \gamma_0 initial parser context from the paper
// or the parser stack at the instant before prediction commences.
//
// @return The value to return from {@link //adaptivePredict}, or
// {@link ATN//INVALID_ALT_NUMBER} if a suitable alternative was not
// identified and {@link //adaptivePredict} should report an error instead.
//
func (this *ParserATNSimulator) getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(configs, outerContext) {
    var cfgs = this.splitAccordingToSemanticValidity(configs, outerContext)
    var semValidConfigs = cfgs[0]
    var semInvalidConfigs = cfgs[1]
    var alt = this.getAltThatFinishedDecisionEntryRule(semValidConfigs)
    if (alt!=ATN.INVALID_ALT_NUMBER) { // semantically/syntactically viable path exists
        return alt
    }
    // Is there a syntactically valid path with a failed pred?
    if (semInvalidConfigs.items.length>0) {
        alt = this.getAltThatFinishedDecisionEntryRule(semInvalidConfigs)
        if (alt!=ATN.INVALID_ALT_NUMBER) { // syntactically viable path exists
            return alt
        }
    }
    return ATN.INVALID_ALT_NUMBER
}
    
func (this *ParserATNSimulator) getAltThatFinishedDecisionEntryRule(configs) {
    var alts = []
    for(var i=0i<configs.items.length i++) {
        var c = configs.items[i]
        if (c.reachesIntoOuterContext>0 || ((c.state instanceof RuleStopState) && c.context.hasEmptyPath())) {
            if(alts.indexOf(c.alt)<0) {
                alts.push(c.alt)
            }
        }
    }
    if (alts.length==0) {
        return ATN.INVALID_ALT_NUMBER
    } else {
        return Math.min.apply(nil, alts)
    }
}
// Walk the list of configurations and split them according to
//  those that have preds evaluating to true/false.  If no pred, assume
//  true pred and include in succeeded set.  Returns Pair of sets.
//
//  Create a Newset so as not to alter the incoming parameter.
//
//  Assumption: the input stream has been restored to the starting point
//  prediction, which is where predicates need to evaluate.
//
func (this *ParserATNSimulator) splitAccordingToSemanticValidity( configs, outerContext) {
    var succeeded = NewATNConfigSet(configs.fullCtx)
    var failed = NewATNConfigSet(configs.fullCtx)
    for(var i=0i<configs.items.length i++) {
        var c = configs.items[i]
        if (c.semanticContext != SemanticContext.NONE) {
            var predicateEvaluationResult = c.semanticContext.evaluate(this.parser, outerContext)
            if (predicateEvaluationResult) {
                succeeded.add(c)
            } else {
                failed.add(c)
            }
        } else {
            succeeded.add(c)
        }
    }
    return [succeeded, failed]
}

// Look through a list of predicate/alt pairs, returning alts for the
//  pairs that win. A {@code NONE} predicate indicates an alt containing an
//  unpredicated config which behaves as "always true." If !complete
//  then we stop at the first predicate that evaluates to true. This
//  includes pairs with nil predicates.
//
func (this *ParserATNSimulator) evalSemanticContext(predPredictions, outerContext, complete) {
    var predictions = NewBitSet()
    for(var i=0i<predPredictions.lengthi++) {
    	var pair = predPredictions[i]
        if (pair.pred == SemanticContext.NONE) {
            predictions.add(pair.alt)
            if (! complete) {
                break
            }
            continue
        }
        var predicateEvaluationResult = pair.pred.evaluate(this.parser, outerContext)
        if (this.debug || this.dfa_debug) {
            fmt.Println("eval pred " + pair + "=" + predicateEvaluationResult)
        }
        if (predicateEvaluationResult) {
            if (this.debug || this.dfa_debug) {
                fmt.Println("PREDICT " + pair.alt)
            }
            predictions.add(pair.alt)
            if (! complete) {
                break
            }
        }
    }
    return predictions
}

// TODO: If we are doing predicates, there is no point in pursuing
//     closure operations if we reach a DFA state that uniquely predicts
//     alternative. We will not be caching that DFA state and it is a
//     waste to pursue the closure. Might have to advance when we do
//     ambig detection thought :(
//

func (this *ParserATNSimulator) closure(config, configs, closureBusy, collectPredicates, fullCtx, treatEofAsEpsilon) {
    var initialDepth = 0
    this.closureCheckingStopState(config, configs, closureBusy, collectPredicates,
                             fullCtx, initialDepth, treatEofAsEpsilon)
}


func (this *ParserATNSimulator) closureCheckingStopState(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon) {
    if (this.debug) {
        fmt.Println("closure(" + config.toString(this.parser,true) + ")")
        fmt.Println("configs(" + configs.toString() + ")")
        if(config.reachesIntoOuterContext>50) {
            panic "problem"
        }
    }
    if (config.state instanceof RuleStopState) {
        // We hit rule end. If we have context info, use it
        // run thru all possible stack tops in ctx
        if (! config.context.isEmpty()) {
            for ( var i =0 i<config.context.length i++) {
                if (config.context.getReturnState(i) == PredictionContext.EMPTY_RETURN_STATE) {
                    if (fullCtx) {
                        configs.add(NewATNConfig({state:config.state, context:PredictionContext.EMPTY}, config), this.mergeCache)
                        continue
                    } else {
                        // we have no context info, just chase follow links (if greedy)
                        if (this.debug) {
                            fmt.Println("FALLING off rule " + this.getRuleName(config.state.ruleIndex))
                        }
                        this.closure_(config, configs, closureBusy, collectPredicates,
                                 fullCtx, depth, treatEofAsEpsilon)
                    }
                    continue
                }
                returnState = this.atn.states[config.context.getReturnState(i)]
                newContext = config.context.getParent(i) // "pop" return state
                var parms = {state:returnState, alt:config.alt, context:newContext, semanticContext:config.semanticContext}
                c = NewATNConfig(parms, nil)
                // While we have context to pop back from, we may have
                // gotten that context AFTER having falling off a rule.
                // Make sure we track that we are now out of context.
                c.reachesIntoOuterContext = config.reachesIntoOuterContext
                this.closureCheckingStopState(c, configs, closureBusy, collectPredicates, fullCtx, depth - 1, treatEofAsEpsilon)
            }
            return
        } else if( fullCtx) {
            // reached end of start rule
            configs.add(config, this.mergeCache)
            return
        } else {
            // else if we have no context info, just chase follow links (if greedy)
            if (this.debug) {
                fmt.Println("FALLING off rule " + this.getRuleName(config.state.ruleIndex))
            }
        }
    }
    this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon)
}

// Do the actual work of walking epsilon edges//
func (this *ParserATNSimulator) closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon) {
    var p = config.state
    // optimization
    if (! p.epsilonOnlyTransitions) {
        configs.add(config, this.mergeCache)
        // make sure to not return here, because EOF transitions can act as
        // both epsilon transitions and non-epsilon transitions.
    }
    for(var i = 0i<p.transitions.length i++) {
        var t = p.transitions[i]
        var continueCollecting = collectPredicates && !_, ok := t.(ActionTransition); ok
        var c = this.getEpsilonTarget(config, t, continueCollecting, depth == 0, fullCtx, treatEofAsEpsilon)
        if (c!=nil) {
			if (!t.isEpsilon && closureBusy.add(c)!=c){
				// avoid infinite recursion for EOF* and EOF+
				continue
			}
            var newDepth = depth
            if ( config.state instanceof RuleStopState) {
                // target fell off end of rule mark resulting c as having dipped into outer context
                // We can't get here if incoming config was rule stop and we had context
                // track how far we dip into outer context.  Might
                // come in handy and we avoid evaluating context dependent
                // preds if this is > 0.

                if (closureBusy.add(c)!=c) {
                    // avoid infinite recursion for right-recursive rules
                    continue
                }

				if (this._dfa != nil && this._dfa.precedenceDfa) {
					if (t.outermostPrecedenceReturn == this._dfa.atnStartState.ruleIndex) {
						c.precedenceFilterSuppressed = true
					}
				}

                c.reachesIntoOuterContext += 1
                configs.dipsIntoOuterContext = true // TODO: can remove? only care when we add to set per middle of this method
                newDepth -= 1
                if (this.debug) {
                    fmt.Println("dips into outer ctx: " + c)
                }
            } else if _, ok := t.(RuleTransition); ok {
                // latch when newDepth goes negative - once we step out of the entry context we can't return
                if (newDepth >= 0) {
                    newDepth += 1
                }
            }
            this.closureCheckingStopState(c, configs, closureBusy, continueCollecting, fullCtx, newDepth, treatEofAsEpsilon)
        }
    }
}

func (this *ParserATNSimulator) getRuleName( index) {
    if (this.parser!=nil && index>=0) {
        return this.parser.ruleNames[index]
    } else {
        return "<rule " + index + ">"
    }
}

func (this *ParserATNSimulator) getEpsilonTarget(config, t, collectPredicates, inContext, fullCtx, treatEofAsEpsilon) {
    switch(t.serializationType) {
    case Transition.RULE:
        return this.ruleTransition(config, t)
    case Transition.PRECEDENCE:
        return this.precedenceTransition(config, t, collectPredicates, inContext, fullCtx)
    case Transition.PREDICATE:
        return this.predTransition(config, t, collectPredicates, inContext, fullCtx)
    case Transition.ACTION:
        return this.actionTransition(config, t)
    case Transition.EPSILON:
        return NewATNConfig({state:t.target}, config)
    case Transition.ATOM:
    case Transition.RANGE:
    case Transition.SET:
        // EOF transitions act like epsilon transitions after the first EOF
        // transition is traversed
        if (treatEofAsEpsilon) {
            if (t.matches(TokenEOF, 0, 1)) {
                return NewATNConfig({state: t.target}, config)
            }
        }
        return nil
    default:
    	return nil
    }
}

func (this *ParserATNSimulator) actionTransition(config, t) {
    if (this.debug) {
        fmt.Println("ACTION edge " + t.ruleIndex + ":" + t.actionIndex)
    }
    return NewATNConfig({state:t.target}, config)
}

func (this *ParserATNSimulator) precedenceTransition(config, pt,  collectPredicates, inContext, fullCtx) {
    if (this.debug) {
        fmt.Println("PRED (collectPredicates=" + collectPredicates + ") " +
                pt.precedence + ">=_p, ctx dependent=true")
        if (this.parser!=nil) {
        	fmt.Println("context surrounding pred is " + Utils.arrayToString(this.parser.getRuleInvocationStack()))
        }
    }
    var c = nil
    if (collectPredicates && inContext) {
        if (fullCtx) {
            // In full context mode, we can evaluate predicates on-the-fly
            // during closure, which dramatically reduces the size of
            // the config sets. It also obviates the need to test predicates
            // later during conflict resolution.
            var currentPosition = this._input.index
            this._input.seek(this._startIndex)
            var predSucceeds = pt.getPredicate().evaluate(this.parser, this._outerContext)
            this._input.seek(currentPosition)
            if (predSucceeds) {
                c = NewATNConfig({state:pt.target}, config) // no pred context
            }
        } else {
            newSemCtx = SemanticContext.andContext(config.semanticContext, pt.getPredicate())
            c = NewATNConfig({state:pt.target, semanticContext:newSemCtx}, config)
        }
    } else {
        c = NewATNConfig({state:pt.target}, config)
    }
    if (this.debug) {
        fmt.Println("config from pred transition=" + c)
    }
    return c
}

func (this *ParserATNSimulator) predTransition(config, pt, collectPredicates, inContext, fullCtx) {
    if (this.debug) {
        fmt.Println("PRED (collectPredicates=" + collectPredicates + ") " + pt.ruleIndex +
                ":" + pt.predIndex + ", ctx dependent=" + pt.isCtxDependent)
        if (this.parser!=nil) {
            fmt.Println("context surrounding pred is " + Utils.arrayToString(this.parser.getRuleInvocationStack()))
        }
    }
    var c = nil
    if (collectPredicates && ((pt.isCtxDependent && inContext) || ! pt.isCtxDependent)) {
        if (fullCtx) {
            // In full context mode, we can evaluate predicates on-the-fly
            // during closure, which dramatically reduces the size of
            // the config sets. It also obviates the need to test predicates
            // later during conflict resolution.
            var currentPosition = this._input.index
            this._input.seek(this._startIndex)
            var predSucceeds = pt.getPredicate().evaluate(this.parser, this._outerContext)
            this._input.seek(currentPosition)
            if (predSucceeds) {
                c = NewATNConfig({state:pt.target}, config) // no pred context
            }
        } else {
            var newSemCtx = SemanticContext.andContext(config.semanticContext, pt.getPredicate())
            c = NewATNConfig({state:pt.target, semanticContext:newSemCtx}, config)
        }
    } else {
        c = NewATNConfig({state:pt.target}, config)
    }
    if (this.debug) {
        fmt.Println("config from pred transition=" + c)
    }
    return c
}

func (this *ParserATNSimulator) ruleTransition(config, t) {
    if (this.debug) {
        fmt.Println("CALL rule " + this.getRuleName(t.target.ruleIndex) + ", ctx=" + config.context)
    }
    var returnState = t.followState
    var newContext = SingletonPredictionContext.create(config.context, returnState.stateNumber)
    return NewATNConfig({state:t.target, context:newContext}, config )
}

func (this *ParserATNSimulator) getConflictingAlts(configs) {
    var altsets = PredictionModegetConflictingAltSubsets(configs)
    return PredictionModegetAlts(altsets)
}

 // Sam pointed out a problem with the previous definition, v3, of
 // ambiguous states. If we have another state associated with conflicting
 // alternatives, we should keep going. For example, the following grammar
 //
 // s : (ID | ID ID?) ''
 //
 // When the ATN simulation reaches the state before '', it has a DFA
 // state that looks like: [12|1|[], 6|2|[], 12|2|[]]. Naturally
 // 12|1|[] and 12|2|[] conflict, but we cannot stop processing this node
 // because alternative to has another way to continue, via [6|2|[]].
 // The key is that we have a single state that has config's only associated
 // with a single alternative, 2, and crucially the state transitions
 // among the configurations are all non-epsilon transitions. That means
 // we don't consider any conflicts that include alternative 2. So, we
 // ignore the conflict between alts 1 and 2. We ignore a set of
 // conflicting alts when there is an intersection with an alternative
 // associated with a single alt state in the state&rarrconfig-list map.
 //
 // It's also the case that we might have two conflicting configurations but
 // also a 3rd nonconflicting configuration for a different alternative:
 // [1|1|[], 1|2|[], 8|3|[]]. This can come about from grammar:
 //
 // a : A | A | A B
 //
 // After matching input A, we reach the stop state for rule A, state 1.
 // State 8 is the state right before B. Clearly alternatives 1 and 2
 // conflict and no amount of further lookahead will separate the two.
 // However, alternative 3 will be able to continue and so we do not
 // stop working on this state. In the previous example, we're concerned
 // with states associated with the conflicting alternatives. Here alt
 // 3 is not associated with the conflicting configs, but since we can continue
 // looking for input reasonably, I don't declare the state done. We
 // ignore a set of conflicting alts when we have an alternative
 // that we still need to pursue.
//

func (this *ParserATNSimulator) getConflictingAltsOrUniqueAlt(configs) {
    var conflictingAlts = nil
    if (configs.uniqueAlt!= ATN.INVALID_ALT_NUMBER) {
        conflictingAlts = NewBitSet()
        conflictingAlts.add(configs.uniqueAlt)
    } else {
        conflictingAlts = configs.conflictingAlts
    }
    return conflictingAlts
}

func (this *ParserATNSimulator) getTokenName( t) {
    if (t==TokenEOF) {
        return "EOF"
    }
    if( this.parser!=nil && this.parser.literalNames!=nil) {
        if (t >= this.parser.literalNames.length) {
            fmt.Println("" + t + " ttype out of range: " + this.parser.literalNames)
            fmt.Println("" + this.parser.getInputStream().getTokens())
        } else {
            return this.parser.literalNames[t] + "<" + t + ">"
        }
    }
    return "" + t
}

func (this *ParserATNSimulator) getLookaheadName(input) {
    return this.getTokenName(input.LA(1))
}

// Used for debugging in adaptivePredict around execATN but I cut
//  it out for clarity now that alg. works well. We can leave this
//  "dead" code for a bit.
//
func (this *ParserATNSimulator) dumpDeadEndConfigs(nvae) {
    fmt.Println("dead end configs: ")
    var decs = nvae.getDeadEndConfigs()
    for(var i=0 i<decs.length i++) {
    	var c = decs[i]
        var trans = "no edges"
        if (c.state.transitions.length>0) {
            var t = c.state.transitions[0]
            if _, ok := t.(AtomTransition); ok {
                trans = "Atom "+ this.getTokenName(t.label)
            } else if _, ok := t.(SetTransition); ok {
                var neg = _, ok := t.(NotSetTransition); ok
                trans = (neg ? "~" : "") + "Set " + t.set
            }
        }
        console.error(c.toString(this.parser, true) + ":" + trans)
    }
}

func (this *ParserATNSimulator) noViableAlt(input, outerContext, configs, startIndex) {
    return NewNoViableAltException(this.parser, input, input.get(startIndex), input.LT(1), configs, outerContext)
}

func (this *ParserATNSimulator) getUniqueAlt(configs) {
    var alt = ATN.INVALID_ALT_NUMBER
    for(var i=0i<configs.items.lengthi++) {
    	var c = configs.items[i]
        if (alt == ATN.INVALID_ALT_NUMBER) {
            alt = c.alt // found first alt
        } else if( c.alt!=alt) {
            return ATN.INVALID_ALT_NUMBER
        }
    }
    return alt
}

//
// Add an edge to the DFA, if possible. This method calls
// {@link //addDFAState} to ensure the {@code to} state is present in the
// DFA. If {@code from} is {@code nil}, or if {@code t} is outside the
// range of edges that can be represented in the DFA tables, this method
// returns without adding the edge to the DFA.
//
// <p>If {@code to} is {@code nil}, this method returns {@code nil}.
// Otherwise, this method returns the {@link DFAState} returned by calling
// {@link //addDFAState} for the {@code to} state.</p>
//
// @param dfa The DFA
// @param from The source state for the edge
// @param t The input symbol
// @param to The target state for the edge
//
// @return If {@code to} is {@code nil}, this method returns {@code nil}
// otherwise this method returns the result of calling {@link //addDFAState}
// on {@code to}
//
func (this *ParserATNSimulator) addDFAEdge(dfa, from_, t, to) {
    if( this.debug) {
        fmt.Println("EDGE " + from_ + " -> " + to + " upon " + this.getTokenName(t))
    }
    if (to==nil) {
        return nil
    }
    to = this.addDFAState(dfa, to) // used existing if possible not incoming
    if (from_==nil || t < -1 || t > this.atn.maxTokenType) {
        return to
    }
    if (from_.edges==nil) {
        from_.edges = []
    }
    from_.edges[t+1] = to // connect

    if (this.debug) {
        var names = this.parser==nil ? nil : this.parser.literalNames
        fmt.Println("DFA=\n" + dfa.toString(names))
    }
    return to
}
//
// Add state {@code D} to the DFA if it is not already present, and return
// the actual instance stored in the DFA. If a state equivalent to {@code D}
// is already in the DFA, the existing state is returned. Otherwise this
// method returns {@code D} after adding it to the DFA.
//
// <p>If {@code D} is {@link //ERROR}, this method returns {@link //ERROR} and
// does not change the DFA.</p>
//
// @param dfa The dfa
// @param D The DFA state to add
// @return The state stored in the DFA. This will be either the existing
// state if {@code D} is already in the DFA, or {@code D} itself if the
// state was not already present.
//
func (this *ParserATNSimulator) addDFAState(dfa, D) {
    if (D == ATNSimulator.ERROR) {
        return D
    }
    var hash = D.hashString()
    var existing = dfa.states[hash] || nil
    if(existing!=nil) {
        return existing
    }
    D.stateNumber = dfa.states.length
    if (! D.configs.readOnly) {
        D.configs.optimizeConfigs(this)
        D.configs.setReadonly(true)
    }
    dfa.states[hash] = D
    if (this.debug) {
        fmt.Println("adding NewDFA state: " + D)
    }
    return D
}

func (this *ParserATNSimulator) reportAttemptingFullContext(dfa, conflictingAlts, configs, startIndex, stopIndex) {
    if (this.debug || this.retry_debug) {
        var interval = NewInterval(startIndex, stopIndex + 1)
        fmt.Println("reportAttemptingFullContext decision=" + dfa.decision + ":" + configs +
                           ", input=" + this.parser.getTokenStream().getText(interval))
    }
    if (this.parser!=nil) {
        this.parser.getErrorListenerDispatch().reportAttemptingFullContext(this.parser, dfa, startIndex, stopIndex, conflictingAlts, configs)
    }
}

func (this *ParserATNSimulator) reportContextSensitivity(dfa, prediction, configs, startIndex, stopIndex) {
    if (this.debug || this.retry_debug) {
        var interval = NewInterval(startIndex, stopIndex + 1)
        fmt.Println("reportContextSensitivity decision=" + dfa.decision + ":" + configs +
                           ", input=" + this.parser.getTokenStream().getText(interval))
    }
    if (this.parser!=nil) {
        this.parser.getErrorListenerDispatch().reportContextSensitivity(this.parser, dfa, startIndex, stopIndex, prediction, configs)
    }
}
    
// If context sensitive parsing, we know it's ambiguity not conflict//
func (this *ParserATNSimulator) reportAmbiguity(dfa, D, startIndex, stopIndex,
                               exact, ambigAlts, configs ) {
    if (this.debug || this.retry_debug) {
        var interval = NewInterval(startIndex, stopIndex + 1)
        fmt.Println("reportAmbiguity " + ambigAlts + ":" + configs +
                           ", input=" + this.parser.getTokenStream().getText(interval))
    }
    if (this.parser!=nil) {
        this.parser.getErrorListenerDispatch().reportAmbiguity(this.parser, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
    }
}
            
