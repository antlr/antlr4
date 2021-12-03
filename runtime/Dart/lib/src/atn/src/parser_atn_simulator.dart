/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:collection';
import 'dart:developer';

import 'package:logging/logging.dart';

import '../../dfa/dfa.dart';
import '../../error/error.dart';
import '../../input_stream.dart';
import '../../interval_set.dart';
import '../../misc/pair.dart';
import '../../parser.dart';
import '../../parser_rule_context.dart';
import '../../prediction_context.dart';
import '../../rule_context.dart';
import '../../token.dart';
import '../../token_stream.dart';
import '../../util/bit_set.dart';
import '../../util/murmur_hash.dart';
import 'atn.dart';
import 'atn_config.dart';
import 'atn_config_set.dart';
import 'atn_simulator.dart';
import 'atn_state.dart';
import 'semantic_context.dart';
import 'transition.dart';

/// The embodiment of the adaptive LL(*), ALL(*), parsing strategy.
///
/// <p>
/// The basic complexity of the adaptive strategy makes it harder to understand.
/// We begin with ATN simulation to build paths in a DFA. Subsequent prediction
/// requests go through the DFA first. If they reach a state without an edge for
/// the current symbol, the algorithm fails over to the ATN simulation to
/// complete the DFA path for the current input (until it finds a conflict state
/// or uniquely predicting state).</p>
///
/// <p>
/// All of that is done without using the outer context because we want to create
/// a DFA that is not dependent upon the rule invocation stack when we do a
/// prediction. One DFA works in all contexts. We avoid using context not
/// necessarily because it's slower, although it can be, but because of the DFA
/// caching problem. The closure routine only considers the rule invocation stack
/// created during prediction beginning in the decision rule. For example, if
/// prediction occurs without invoking another rule's ATN, there are no context
/// stacks in the configurations. When lack of context leads to a conflict, we
/// don't know if it's an ambiguity or a weakness in the strong LL(*) parsing
/// strategy (versus full LL(*)).</p>
///
/// <p>
/// When SLL yields a configuration set with conflict, we rewind the input and
/// retry the ATN simulation, this time using full outer context without adding
/// to the DFA. Configuration context stacks will be the full invocation stacks
/// from the start rule. If we get a conflict using full context, then we can
/// definitively say we have a true ambiguity for that input sequence. If we
/// don't get a conflict, it implies that the decision is sensitive to the outer
/// context. (It is not context-sensitive in the sense of context-sensitive
/// grammars.)</p>
///
/// <p>
/// The next time we reach this DFA state with an SLL conflict, through DFA
/// simulation, we will again retry the ATN simulation using full context mode.
/// This is slow because we can't save the results and have to "interpret" the
/// ATN each time we get that input.</p>
///
/// <p>
/// <strong>CACHING FULL CONTEXT PREDICTIONS</strong></p>
///
/// <p>
/// We could cache results from full context to predicted alternative easily and
/// that saves a lot of time but doesn't work in presence of predicates. The set
/// of visible predicates from the ATN start state changes depending on the
/// context, because closure can fall off the end of a rule. I tried to cache
/// tuples (stack context, semantic context, predicted alt) but it was slower
/// than interpreting and much more complicated. Also required a huge amount of
/// memory. The goal is not to create the world's fastest parser anyway. I'd like
/// to keep this algorithm simple. By launching multiple threads, we can improve
/// the speed of parsing across a large number of files.</p>
///
/// <p>
/// There is no strict ordering between the amount of input used by SLL vs LL,
/// which makes it really hard to build a cache for full context. Let's say that
/// we have input A B C that leads to an SLL conflict with full context X. That
/// implies that using X we might only use A B but we could also use A B C D to
/// resolve conflict. Input A B C D could predict alternative 1 in one position
/// in the input and A B C E could predict alternative 2 in another position in
/// input. The conflicting SLL configurations could still be non-unique in the
/// full context prediction, which would lead us to requiring more input than the
/// original A B C.	To make a	prediction cache work, we have to track	the exact
/// input	used during the previous prediction. That amounts to a cache that maps
/// X to a specific DFA for that context.</p>
///
/// <p>
/// Something should be done for left-recursive expression predictions. They are
/// likely LL(1) + pred eval. Easier to do the whole SLL unless error and retry
/// with full LL thing Sam does.</p>
///
/// <p>
/// <strong>AVOIDING FULL CONTEXT PREDICTION</strong></p>
///
/// <p>
/// We avoid doing full context retry when the outer context is empty, we did not
/// dip into the outer context by falling off the end of the decision state rule,
/// or when we force SLL mode.</p>
///
/// <p>
/// As an example of the not dip into outer context case, consider as super
/// constructor calls versus function calls. One grammar might look like
/// this:</p>
///
/// <pre>
/// ctorBody
///   : '{' superCall? stat* '}'
///   ;
/// </pre>
///
/// <p>
/// Or, you might see something like</p>
///
/// <pre>
/// stat
///   : superCall ';'
///   | expression ';'
///   | ...
///   ;
/// </pre>
///
/// <p>
/// In both cases I believe that no closure operations will dip into the outer
/// context. In the first case ctorBody in the worst case will stop at the '}'.
/// In the 2nd case it should stop at the ';'. Both cases should stay within the
/// entry rule and not dip into the outer context.</p>
///
/// <p>
/// <strong>PREDICATES</strong></p>
///
/// <p>
/// Predicates are always evaluated if present in either SLL or LL both. SLL and
/// LL simulation deals with predicates differently. SLL collects predicates as
/// it performs closure operations like ANTLR v3 did. It delays predicate
/// evaluation until it reaches and accept state. This allows us to cache the SLL
/// ATN simulation whereas, if we had evaluated predicates on-the-fly during
/// closure, the DFA state configuration sets would be different and we couldn't
/// build up a suitable DFA.</p>
///
/// <p>
/// When building a DFA accept state during ATN simulation, we evaluate any
/// predicates and return the sole semantically valid alternative. If there is
/// more than 1 alternative, we report an ambiguity. If there are 0 alternatives,
/// we throw an exception. Alternatives without predicates act like they have
/// true predicates. The simple way to think about it is to strip away all
/// alternatives with false predicates and choose the minimum alternative that
/// remains.</p>
///
/// <p>
/// When we start in the DFA and reach an accept state that's predicated, we test
/// those and return the minimum semantically viable alternative. If no
/// alternatives are viable, we throw an exception.</p>
///
/// <p>
/// During full LL ATN simulation, closure always evaluates predicates and
/// on-the-fly. This is crucial to reducing the configuration set size during
/// closure. It hits a landmine when parsing with the Java grammar, for example,
/// without this on-the-fly evaluation.</p>
///
/// <p>
/// <strong>SHARING DFA</strong></p>
///
/// <p>
/// All instances of the same parser share the same decision DFAs through a
/// static field. Each instance gets its own ATN simulator but they share the
/// same {@link #decisionToDFA} field. They also share a
/// [PredictionContextCache] object that makes sure that all
/// [PredictionContext] objects are shared among the DFA states. This makes
/// a big size difference.</p>
///
/// <p>
/// <strong>THREAD SAFETY</strong></p>
///
/// <p>
/// The [ParserATNSimulator] locks on the {@link #decisionToDFA} field when
/// it adds a new DFA object to that array. {@link #addDFAEdge}
/// locks on the DFA for the current decision when setting the
/// {@link DFAState#edges} field. {@link #addDFAState} locks on
/// the DFA for the current decision when looking up a DFA state to see if it
/// already exists. We must make sure that all requests to add DFA states that
/// are equivalent result in the same shared DFA object. This is because lots of
/// threads will be trying to update the DFA at once. The
/// {@link #addDFAState} method also locks inside the DFA lock
/// but this time on the shared context cache when it rebuilds the
/// configurations' [PredictionContext] objects using cached
/// subgraphs/nodes. No other locking occurs, even during DFA simulation. This is
/// safe as long as we can guarantee that all threads referencing
/// {@code s.edge[t]} get the same physical target [DFAState], or
/// null. Once into the DFA, the DFA simulation does not reference the
/// {@link DFA#states} map. It follows the {@link DFAState#edges} field to new
/// targets. The DFA simulator will either find {@link DFAState#edges} to be
/// null, to be non-null and {@code dfa.edges[t]} null, or
/// {@code dfa.edges[t]} to be non-null. The
/// {@link #addDFAEdge} method could be racing to set the field
/// but in either case the DFA simulator works; if null, and requests ATN
/// simulation. It could also race trying to get {@code dfa.edges[t]}, but either
/// way it will work because it's not doing a test and set operation.</p>
///
/// <p>
/// <strong>Starting with SLL then failing to combined SLL/LL (Two-Stage
/// Parsing)</strong></p>
///
/// <p>
/// Sam pointed out that if SLL does not give a syntax error, then there is no
/// point in doing full LL, which is slower. We only have to try LL if we get a
/// syntax error. For maximum speed, Sam starts the parser set to pure SLL
/// mode with the [BailErrorStrategy]:</p>
///
/// <pre>
/// parser.{@link Parser#interpreter interpreter}.{@link #setPredictionMode setPredictionMode}{@code (}{@link PredictionMode#SLL}{@code )};
/// parser.{@link Parser#setErrorHandler setErrorHandler}(new [BailErrorStrategy]());
/// </pre>
///
/// <p>
/// If it does not get a syntax error, then we're done. If it does get a syntax
/// error, we need to retry with the combined SLL/LL strategy.</p>
///
/// <p>
/// The reason this works is as follows. If there are no SLL conflicts, then the
/// grammar is SLL (at least for that input set). If there is an SLL conflict,
/// the full LL analysis must yield a set of viable alternatives which is a
/// subset of the alternatives reported by SLL. If the LL set is a singleton,
/// then the grammar is LL but not SLL. If the LL set is the same size as the SLL
/// set, the decision is SLL. If the LL set has size &gt; 1, then that decision
/// is truly ambiguous on the current input. If the LL set is smaller, then the
/// SLL conflict resolution might choose an alternative that the full LL would
/// rule out as a possibility based upon better context information. If that's
/// the case, then the SLL parse will definitely get an error because the full LL
/// analysis says it's not viable. If SLL conflict resolution chooses an
/// alternative within the LL set, them both SLL and LL would choose the same
/// alternative because they both choose the minimum of multiple conflicting
/// alternatives.</p>
///
/// <p>
/// Let's say we have a set of SLL conflicting alternatives {@code {1, 2, 3}} and
/// a smaller LL set called <em>s</em>. If <em>s</em> is {@code {2, 3}}, then SLL
/// parsing will get an error because SLL will pursue alternative 1. If
/// <em>s</em> is {@code {1, 2}} or {@code {1, 3}} then both SLL and LL will
/// choose the same alternative because alternative one is the minimum of either
/// set. If <em>s</em> is {@code {2}} or {@code {3}} then SLL will get a syntax
/// error. If <em>s</em> is {@code {1}} then SLL will succeed.</p>
///
/// <p>
/// Of course, if the input is invalid, then we will get an error for sure in
/// both SLL and LL parsing. Erroneous input will therefore require 2 passes over
/// the input.</p>
class ParserATNSimulator extends ATNSimulator {
  static const bool debug = bool.fromEnvironment(
    'ANTLR_PARSER_DEBUG',
    defaultValue: false,
  );
  static const bool debug_list_atn_decisions = bool.fromEnvironment(
    'ANTLR_PARSER_LIST_ATN_DECISIONS_DEBUG',
    defaultValue: false,
  );
  static const bool dfa_debug = bool.fromEnvironment(
    'ANTLR_PARSER_DFA_DEBUG',
    defaultValue: false,
  );
  static const bool retry_debug = bool.fromEnvironment(
    'ANTLR_PARSER_RETRY_DEBUG',
    defaultValue: false,
  );

  /// Just in case this optimization is bad, add an ENV variable to turn it off */
  static const bool TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT =
      bool.fromEnvironment('TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT');

  final Parser parser;

  final List<DFA> decisionToDFA;

  /// SLL, LL, or LL + exact ambig detection? */

  PredictionMode predictionMode = PredictionMode.LL;

  /// Each prediction operation uses a cache for merge of prediction contexts.
  ///  Don't keep around as it wastes huge amounts of memory. DoubleKeyMap
  ///  isn't synchronized but we're ok since two threads shouldn't reuse same
  ///  parser/atnsim object because it can only handle one input at a time.
  ///  This maps graphs a and b to merged result c. (a,b)&rarr;c. We can avoid
  ///  the merge if we ever see a and b again.  Note that (b,a)&rarr;c should
  ///  also be examined during cache lookup.
  Map<Pair<PredictionContext, PredictionContext>, PredictionContext>?
      mergeCache;

  // LAME globals to avoid parameters!!!!! I need these down deep in predTransition
  late TokenStream input;
  int startIndex = 0;
  ParserRuleContext? _outerContext;
  DFA? _dfa;

  ParserATNSimulator(
    this.parser,
    ATN atn,
    this.decisionToDFA,
    PredictionContextCache? sharedContextCache,
  ) : super(atn, sharedContextCache);

  @override
  void reset() {}

  @override
  void clearDFA() {
    for (var d = 0; d < decisionToDFA.length; d++) {
      decisionToDFA[d] = DFA(atn.getDecisionState(d), d);
    }
  }

  int adaptivePredict(
    TokenStream input_,
    int decision,
    ParserRuleContext? outerContext,
  ) {
    if (debug || debug_list_atn_decisions) {
      log('adaptivePredict decision $decision' ' exec LA(1)==' +
          getLookaheadName(input_) +
          ' line ${input_.LT(1)!.line}:${input_.LT(1)!.charPositionInLine}');
    }

    input = input_;
    startIndex = input_.index;
    _outerContext = outerContext;
    final dfa = decisionToDFA[decision];
    _dfa = dfa;

    final m = input_.mark();
    final index = startIndex;

    // Now we are certain to have a specific decision's DFA
    // But, do we still need an initial state?
    try {
      DFAState? s0;
      if (dfa.isPrecedenceDfa()) {
        // the start state for a precedence DFA depends on the current
        // parser precedence, and is provided by a DFA method.
        s0 = dfa.getPrecedenceStartState(parser.precedence);
      } else {
        // the start state for a "regular" DFA is just s0
        s0 = dfa.s0;
      }

      if (s0 == null) {
        outerContext ??= ParserRuleContext.EMPTY;
        if (debug || debug_list_atn_decisions) {
          log('predictATN decision ${dfa.decision}' ' exec LA(1)==' +
              getLookaheadName(input_) +
              ', outerContext=' +
              outerContext.toString(recog: parser));
        }

        final fullCtx = false;
        var s0_closure = computeStartState(
          dfa.atnStartState!,
          ParserRuleContext.EMPTY,
          fullCtx,
        );

        if (dfa.isPrecedenceDfa()) {
          /* If this is a precedence DFA, we use applyPrecedenceFilter
					 * to convert the computed start state to a precedence start
					 * state. We then use DFA.setPrecedenceStartState to set the
					 * appropriate start state for the precedence level rather
					 * than simply setting DFA.s0.
					 */
          // not used for prediction but useful to know start configs anyway
          dfa.s0!.configs = s0_closure;
          s0_closure = applyPrecedenceFilter(s0_closure);
          s0 = addDFAState(dfa, DFAState(configs: s0_closure));
          dfa.setPrecedenceStartState(parser.precedence, s0);
        } else {
          s0 = addDFAState(dfa, DFAState(configs: s0_closure));
          dfa.s0 = s0;
        }
      }

      final alt = execATN(dfa, s0, input_, index, outerContext!);
      if (debug) {
        log('DFA after predictATN: ' + dfa.toString(parser.vocabulary));
      }
      return alt;
    } finally {
      mergeCache = null; // wack cache after each prediction
      _dfa = null;
      input_.seek(index);
      input_.release(m);
    }
  }

  /// Performs ATN simulation to compute a predicted alternative based
  /// upon the remaining input, but also updates the DFA cache to avoid
  /// having to traverse the ATN again for the same input sequence.
  ///
  /// There are some key conditions we're looking for after computing a new
  /// set of ATN configs (proposed DFA state):
  /// if the set is empty, there is no viable alternative for current symbol
  /// does the state uniquely predict an alternative?
  /// does the state have a conflict that would prevent us from
  /// putting it on the work list?
  ///
  /// We also have some key operations to do:
  /// add an edge from previous DFA state to potentially new DFA state, D,
  ///  upon current symbol but only if adding to work list, which means in all
  ///  cases except no viable alternative (and possibly non-greedy decisions?)
  /// collecting predicates and adding semantic context to DFA accept states
  /// adding rule context to context-sensitive DFA accept states
  /// consuming an input symbol
  /// reporting a conflict
  /// reporting an ambiguity
  /// reporting a context sensitivity
  /// reporting insufficient predicates
  ///
  /// cover these cases:
  /// dead end
  /// single alt
  /// single alt + preds
  /// conflict
  /// conflict + preds
  ///
  int execATN(DFA dfa, DFAState s0, TokenStream input, int startIndex,
      ParserRuleContext outerContext) {
    if (debug || debug_list_atn_decisions) {
      log('execATN decision ${dfa.decision}' ' exec LA(1)==' +
          getLookaheadName(input) +
          ' line ${input.LT(1)!.line}' +
          ':${input.LT(1)!.charPositionInLine}');
    }

    var previousD = s0;

    if (debug) log('s0 = $s0');

    var t = input.LA(1)!;

    while (true) {
      // while more work
      var D = getExistingTargetState(previousD, t);
      D ??= computeTargetState(dfa, previousD, t);
      D = D as DFAState;
      if (D == ATNSimulator.ERROR) {
        // if any configs in previous dipped into outer context, that
        // means that input up to t actually finished entry rule
        // at least for SLL decision. Full LL doesn't dip into outer
        // so don't need special case.
        // We will get an error no matter what so delay until after
        // decision; better error message. Also, no reachable target
        // ATN states in SLL implies LL will also get nowhere.
        // If conflict in states that dip out, choose min since we
        // will get error no matter what.
        final e = noViableAlt(
          input,
          outerContext,
          previousD.configs,
          startIndex,
        );
        input.seek(startIndex);
        final alt = getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(
            previousD.configs, outerContext);
        if (alt != ATN.INVALID_ALT_NUMBER) {
          return alt;
        }
        throw e;
      }

      if (D.requiresFullContext && predictionMode != PredictionMode.SLL) {
        // IF PREDS, MIGHT RESOLVE TO SINGLE ALT => SLL (or syntax error)
        var conflictingAlts = D.configs.conflictingAlts;
        if (D.predicates != null) {
          if (debug) log('DFA state has preds in DFA sim LL failover');
          final conflictIndex = input.index;
          if (conflictIndex != startIndex) {
            input.seek(startIndex);
          }

          conflictingAlts =
              evalSemanticContext(D.predicates!, outerContext, true);
          if (conflictingAlts.cardinality == 1) {
            if (debug) log('Full LL avoided');
            return conflictingAlts.nextset(0);
          }

          if (conflictIndex != startIndex) {
            // restore the index so reporting the fallback to full
            // context occurs with the index at the correct spot
            input.seek(conflictIndex);
          }
        }

        if (dfa_debug) log('ctx sensitive state $outerContext in $D');
        final fullCtx = true;
        final s0_closure = computeStartState(
          dfa.atnStartState!,
          outerContext,
          fullCtx,
        );
        reportAttemptingFullContext(
          dfa,
          conflictingAlts,
          D.configs,
          startIndex,
          input.index,
        );
        final alt = execATNWithFullContext(
          dfa,
          D,
          s0_closure,
          input,
          startIndex,
          outerContext,
        );
        return alt;
      }

      if (D.isAcceptState) {
        if (D.predicates == null) {
          return D.prediction;
        }

        final stopIndex = input.index;
        input.seek(startIndex);
        final alts = evalSemanticContext(D.predicates!, outerContext, true);
        switch (alts.cardinality) {
          case 0:
            throw noViableAlt(input, outerContext, D.configs, startIndex);

          case 1:
            return alts.nextset(0);

          default:
            // report ambiguity after predicate evaluation to make sure the correct
            // set of ambig alts is reported.
            reportAmbiguity(
              dfa,
              D,
              startIndex,
              stopIndex,
              false,
              alts,
              D.configs,
            );
            return alts.nextset(0);
        }
      }

      previousD = D;

      if (t != IntStream.EOF) {
        input.consume();
        t = input.LA(1)!;
      }
    }
  }

  /// Get an existing target state for an edge in the DFA. If the target state
  /// for the edge has not yet been computed or is otherwise not available,
  /// this method returns null.
  ///
  /// @param previousD The current DFA state
  /// @param t The next input symbol
  /// @return The existing target DFA state for the given input symbol
  /// [t], or null if the target state for this edge is not
  /// already cached
  DFAState? getExistingTargetState(DFAState previousD, int t) {
    final edges = previousD.edges;
    if (edges == null || t + 1 < 0 || t + 1 >= edges.length) {
      return null;
    }

    return edges[t + 1];
  }

  /// Compute a target state for an edge in the DFA, and attempt to add the
  /// computed state and corresponding edge to the DFA.
  ///
  /// @param dfa The DFA
  /// @param previousD The current DFA state
  /// @param t The next input symbol
  ///
  /// @return The computed target DFA state for the given input symbol
  /// [t]. If [t] does not lead to a valid DFA state, this method
  /// returns {@link #ERROR}.
  DFAState? computeTargetState(DFA dfa, DFAState previousD, int t) {
    final reach = computeReachSet(previousD.configs, t, false);
    if (reach == null) {
      addDFAEdge(dfa, previousD, t, ATNSimulator.ERROR);
      return ATNSimulator.ERROR;
    }

    // create new target state; we'll add to DFA after it's complete
    DFAState? D = DFAState(configs: reach);

    final predictedAlt = getUniqueAlt(reach);

    if (debug) {
      final altSubSets =
          PredictionModeExtension.getConflictingAltSubsets(reach);
      log('SLL altSubSets=$altSubSets'
          ', configs=$reach'
          ', predict=$predictedAlt, allSubsetsConflict=${PredictionModeExtension.allSubsetsConflict(altSubSets)}, conflictingAlts=${getConflictingAlts(reach)}');
    }

    if (predictedAlt != ATN.INVALID_ALT_NUMBER) {
      // NO CONFLICT, UNIQUELY PREDICTED ALT
      D.isAcceptState = true;
      D.configs.uniqueAlt = predictedAlt;
      D.prediction = predictedAlt;
    } else if (PredictionModeExtension.hasSLLConflictTerminatingPrediction(
        predictionMode, reach)) {
      // MORE THAN ONE VIABLE ALTERNATIVE
      D.configs.conflictingAlts = getConflictingAlts(reach);
      D.requiresFullContext = true;
      // in SLL-only mode, we will stop at this state and return the minimum alt
      D.isAcceptState = true;
      D.prediction = D.configs.conflictingAlts!.nextset(0);
    }

    if (D.isAcceptState && D.configs.hasSemanticContext) {
      predicateDFAState(D, atn.getDecisionState(dfa.decision));
      if (D.predicates != null) {
        D.prediction = ATN.INVALID_ALT_NUMBER;
      }
    }

    // all adds to dfa are done after we've created full D state
    D = addDFAEdge(dfa, previousD, t, D);
    return D;
  }

  void predicateDFAState(DFAState dfaState, DecisionState? decisionState) {
    // Todo: this if was added due to a possible null pointer error
    if (decisionState == null) return;

    // We need to test all predicates, even in DFA states that
    // uniquely predict alternative.
    final nalts = decisionState.numberOfTransitions;
    // Update DFA so reach becomes accept state with (predicate,alt)
    // pairs if preds found for conflicting alts
    final altsToCollectPredsFrom = getConflictingAltsOrUniqueAlt(
      dfaState.configs,
    );
    final altToPred = getPredsForAmbigAlts(
      altsToCollectPredsFrom,
      dfaState.configs,
      nalts,
    );
    if (altToPred != null) {
      dfaState.predicates =
          getPredicatePredictions(altsToCollectPredsFrom, altToPred);
      dfaState.prediction = ATN.INVALID_ALT_NUMBER; // make sure we use preds
    } else {
      // There are preds in configs but they might go away
      // when OR'd together like {p}? || NONE == NONE. If neither
      // alt has preds, resolve to min alt
      dfaState.prediction = altsToCollectPredsFrom.nextset(0);
    }
  }

  // comes back with reach.uniqueAlt set to a valid alt
  int execATNWithFullContext(
      DFA dfa,
      DFAState D, // how far we got in SLL DFA before failing over
      ATNConfigSet s0,
      TokenStream input,
      int startIndex,
      ParserRuleContext outerContext) {
    if (debug || debug_list_atn_decisions) {
      log('execATNWithFullContext $s0');
    }
    final fullCtx = true;
    var foundExactAmbig = false;
    ATNConfigSet? reach;
    var previous = s0;
    input.seek(startIndex);
    var t = input.LA(1)!;
    int predictedAlt;
    while (true) {
      // while more work
//			log("LL REACH "+getLookaheadName(input)+
//							   " from configs.size="+previous.length+
//							   " line "+input.LT(1).getLine()+":"+input.LT(1).getCharPositionInLine());
      reach = computeReachSet(previous, t, fullCtx);
      if (reach == null) {
        // if any configs in previous dipped into outer context, that
        // means that input up to t actually finished entry rule
        // at least for LL decision. Full LL doesn't dip into outer
        // so don't need special case.
        // We will get an error no matter what so delay until after
        // decision; better error message. Also, no reachable target
        // ATN states in SLL implies LL will also get nowhere.
        // If conflict in states that dip out, choose min since we
        // will get error no matter what.
        final e = noViableAlt(input, outerContext, previous, startIndex);
        input.seek(startIndex);
        final alt = getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(
          previous,
          outerContext,
        );
        if (alt != ATN.INVALID_ALT_NUMBER) {
          return alt;
        }
        throw e;
      }

      final altSubSets =
          PredictionModeExtension.getConflictingAltSubsets(reach);
      if (debug) {
        log('LL altSubSets=$altSubSets'
            ', predict=${PredictionModeExtension.getUniqueAlt(altSubSets)}'
            ', resolvesToJustOneViableAlt=${PredictionModeExtension.resolvesToJustOneViableAlt(altSubSets)}');
      }

//			log("altSubSets: "+altSubSets);
//			log("reach="+reach+", "+reach.conflictingAlts, level: Level.SEVERE.value);
      reach.uniqueAlt = getUniqueAlt(reach);
      // unique prediction?
      if (reach.uniqueAlt != ATN.INVALID_ALT_NUMBER) {
        predictedAlt = reach.uniqueAlt;
        break;
      }
      if (predictionMode != PredictionMode.LL_EXACT_AMBIG_DETECTION) {
        predictedAlt =
            PredictionModeExtension.resolvesToJustOneViableAlt(altSubSets);
        if (predictedAlt != ATN.INVALID_ALT_NUMBER) {
          break;
        }
      } else {
        // In exact ambiguity mode, we never try to terminate early.
        // Just keeps scarfing until we know what the conflict is
        if (PredictionModeExtension.allSubsetsConflict(altSubSets) &&
            PredictionModeExtension.allSubsetsEqual(altSubSets)) {
          foundExactAmbig = true;
          predictedAlt = PredictionModeExtension.getSingleViableAlt(altSubSets);
          break;
        }
        // else there are multiple non-conflicting subsets or
        // we're not sure what the ambiguity is yet.
        // So, keep going.
      }

      previous = reach;
      if (t != IntStream.EOF) {
        input.consume();
        t = input.LA(1)!;
      }
    }

    // If the configuration set uniquely predicts an alternative,
    // without conflict, then we know that it's a full LL decision
    // not SLL.
    if (reach.uniqueAlt != ATN.INVALID_ALT_NUMBER) {
      reportContextSensitivity(
          dfa, predictedAlt, reach, startIndex, input.index);
      return predictedAlt;
    }

    // We do not check predicates here because we have checked them
    // on-the-fly when doing full context prediction.

    /*
		In non-exact ambiguity detection mode, we might	actually be able to
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
    reportAmbiguity(
        dfa, D, startIndex, input.index, foundExactAmbig, reach.alts, reach);

    return predictedAlt;
  }

  ATNConfigSet? computeReachSet(ATNConfigSet config, int t, bool fullCtx) {
    if (debug) log('in computeReachSet, starting closure: $config');

    mergeCache ??= {};

    final intermediate = ATNConfigSet(fullCtx);

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
    List<ATNConfig>? skippedStopStates;

    // First figure out where we can reach on input t
    for (var c in config) {
      if (debug) log('testing ' + getTokenName(t) + ' at ' + c.toString());

      if (c.state is RuleStopState) {
        assert(c.context!.isEmpty);
        if (fullCtx || t == IntStream.EOF) {
          skippedStopStates ??= [];

          skippedStopStates.add(c);
        }

        continue;
      }

      final n = c.state.numberOfTransitions;
      for (var ti = 0; ti < n; ti++) {
        // for each transition
        final trans = c.state.transition(ti);
        final target = getReachableTarget(trans, t);
        if (target != null) {
          intermediate.add(ATNConfig.dup(c, state: target), mergeCache);
        }
      }
    }

    // Now figure out where the reach operation can take us...

    ATNConfigSet? reach;

    /* This block optimizes the reach operation for intermediate sets which
		 * trivially indicate a termination state for the overall
		 * adaptivePredict operation.
		 *
		 * The conditions assume that intermediate
		 * contains all configurations relevant to the reach set, but this
		 * condition is not true when one or more configurations have been
		 * withheld in skippedStopStates, or when the current symbol is EOF.
		 */
    if (skippedStopStates == null && t != Token.EOF) {
      if (intermediate.length == 1) {
        // Don't pursue the closure if there is just one state.
        // It can only have one alternative; just add to result
        // Also don't pursue the closure if there is unique alternative
        // among the configurations.
        reach = intermediate;
      } else if (getUniqueAlt(intermediate) != ATN.INVALID_ALT_NUMBER) {
        // Also don't pursue the closure if there is unique alternative
        // among the configurations.
        reach = intermediate;
      }
    }

    /* If the reach set could not be trivially determined, perform a closure
		 * operation on the intermediate set to compute its initial value.
		 */
    if (reach == null) {
      reach = ATNConfigSet(fullCtx);
      final closureBusy = <ATNConfig>{};
      final treatEofAsEpsilon = t == Token.EOF;
      for (var c in intermediate) {
        closure(c, reach, closureBusy, false, fullCtx, treatEofAsEpsilon);
      }
    }

    if (t == IntStream.EOF) {
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
      reach = removeAllConfigsNotInRuleStopState(reach, reach == intermediate);
    }

    /* If skippedStopStates is not null, then it contains at least one
		 * configuration. For full-context reach operations, these
		 * configurations reached the end of the start rule, in which case we
		 * only add them back to reach if no configuration during the current
		 * closure operation reached such a state. This ensures adaptivePredict
		 * chooses an alternative matching the longest overall sequence when
		 * multiple alternatives are viable.
		 */
    if (skippedStopStates != null &&
        (!fullCtx ||
            !PredictionModeExtension.hasConfigInRuleStopState(reach))) {
      assert(skippedStopStates.isNotEmpty);
      for (var c in skippedStopStates) {
        reach.add(c, mergeCache);
      }
    }

    if (reach.isEmpty) return null;
    return reach;
  }

  /// Return a configuration set containing only the configurations from
  /// [configs] which are in a [RuleStopState]. If all
  /// configurations in [configs] are already in a rule stop state, this
  /// method simply returns [configs].
  ///
  /// <p>When [lookToEndOfRule] is true, this method uses
  /// {@link ATN#nextTokens} for each configuration in [configs] which is
  /// not already in a rule stop state to see if a rule stop state is reachable
  /// from the configuration via epsilon-only transitions.</p>
  ///
  /// @param configs the configuration set to update
  /// @param lookToEndOfRule when true, this method checks for rule stop states
  /// reachable by epsilon-only transitions from each configuration in
  /// [configs].
  ///
  /// @return [configs] if all configurations in [configs] are in a
  /// rule stop state, otherwise return a new configuration set containing only
  /// the configurations from [configs] which are in a rule stop state
  ATNConfigSet removeAllConfigsNotInRuleStopState(
      ATNConfigSet configs, bool lookToEndOfRule) {
    if (PredictionModeExtension.allConfigsInRuleStopStates(configs)) {
      return configs;
    }

    final result = ATNConfigSet(configs.fullCtx);
    for (var config in configs) {
      if (config.state is RuleStopState) {
        result.add(config, mergeCache);
        continue;
      }

      if (lookToEndOfRule && config.state.onlyHasEpsilonTransitions()) {
        final nextTokens = atn.nextTokens(config.state);
        if (nextTokens.contains(Token.EPSILON)) {
          ATNState endOfRuleState = atn.ruleToStopState[config.state.ruleIndex];
          result.add(ATNConfig.dup(config, state: endOfRuleState), mergeCache);
        }
      }
    }

    return result;
  }

  ATNConfigSet computeStartState(ATNState p, RuleContext ctx, bool fullCtx) {
    // always at least the implicit call to start rule
    final initialContext = PredictionContext.fromRuleContext(atn, ctx);
    final configs = ATNConfigSet(fullCtx);

    for (var i = 0; i < p.numberOfTransitions; i++) {
      final target = p.transition(i).target;
      final c = ATNConfig(target, i + 1, initialContext);
      final closureBusy = <ATNConfig>{};
      closure(c, configs, closureBusy, true, fullCtx, false);
    }

    return configs;
  }

  /* parrt internal source braindump that doesn't mess up
	 * external API spec.
		context-sensitive in that they can only be properly evaluated
		in the context of the proper prec argument. Without pruning,
		these predicates are normal predicates evaluated when we reach
		conflict state (or unique prediction). As we cannot evaluate
		these predicates out of context, the resulting conflict leads
		to full LL evaluation and nonlinear prediction which shows up
		very clearly with fairly large expressions.

		Example grammar:

		e : e '*' e
		  | e '+' e
		  | INT
		  ;

		We convert that to the following:

		e[int prec]
			:   INT
				( {3>=prec}? '*' e[4]
				| {2>=prec}? '+' e[3]
				)*
			;

		The (..)* loop has a decision for the inner block as well as
		an enter or exit decision, which is what concerns us here. At
		the 1st + of input 1+2+3, the loop entry sees both predicates
		and the loop exit also sees both predicates by falling off the
		edge of e.  This is because we have no stack information with
		SLL and find the follow of e, which will hit the return states
		inside the loop after e[4] and e[3], which brings it back to
		the enter or exit decision. In this case, we know that we
		cannot evaluate those predicates because we have fallen off
		the edge of the stack and will in general not know which prec
		parameter is the right one to use in the predicate.

		Because we have special information, that these are precedence
		predicates, we can resolve them without failing over to full
		LL despite their context sensitive nature. We make an
		assumption that prec[-1] <= prec[0], meaning that the current
		precedence level is greater than or equal to the precedence
		level of recursive invocations above us in the stack. For
		example, if predicate {3>=prec}? is true of the current prec,
		then one option is to enter the loop to match it now. The
		other option is to exit the loop and the left recursive rule
		to match the current operator in rule invocation further up
		the stack. But, we know that all of those prec are lower or
		the same value and so we can decide to enter the loop instead
		of matching it later. That means we can strip out the other
		configuration for the exit branch.

		So imagine we have (14,1,$,{2>=prec}?) and then
		(14,2,$-dipsIntoOuterContext,{2>=prec}?). The optimization
		allows us to collapse these two configurations. We know that
		if {2>=prec}? is true for the current prec parameter, it will
		also be true for any prec from an invoking e call, indicated
		by dipsIntoOuterContext. As the predicates are both true, we
		have the option to evaluate them early in the decision start
		state. We do this by stripping both predicates and choosing to
		enter the loop as it is consistent with the notion of operator
		precedence. It's also how the full LL conflict resolution
		would work.

		The solution requires a different DFA start state for each
		precedence level.

		The basic filter mechanism is to remove configurations of the
		form (p, 2, pi) if (p, 1, pi) exists for the same p and pi. In
		other words, for the same ATN state and predicate context,
		remove any configuration associated with an exit branch if
		there is a configuration associated with the enter branch.

		It's also the case that the filter evaluates precedence
		predicates and resolves conflicts according to precedence
		levels. For example, for input 1+2+3 at the first +, we see
		prediction filtering

		[(11,1,[$],{3>=prec}?), (14,1,[$],{2>=prec}?), (5,2,[$],up=1),
		 (11,2,[$],up=1), (14,2,[$],up=1)],hasSemanticContext=true,dipsIntoOuterContext

		to

		[(11,1,[$]), (14,1,[$]), (5,2,[$],up=1)],dipsIntoOuterContext

		This filters because {3>=prec}? evals to true and collapses
		(11,1,[$],{3>=prec}?) and (11,2,[$],up=1) since early conflict
		resolution based upon rules of operator precedence fits with
		our usual match first alt upon conflict.

		We noticed a problem where a recursive call resets precedence
		to 0. Sam's fix: each config has flag indicating if it has
		returned from an expr[0] call. then just don't filter any
		config with that flag set. flag is carried along in
		closure(). so to avoid adding field, set bit just under sign
		bit of dipsIntoOuterContext (SUPPRESS_PRECEDENCE_FILTER).
		With the change you filter "unless (p, 2, pi) was reached
		after leaving the rule stop state of the LR rule containing
		state p, corresponding to a rule invocation with precedence
		level 0"
	 */

  /// This method transforms the start state computed by
  /// {@link #computeStartState} to the special start state used by a
  /// precedence DFA for a particular precedence value. The transformation
  /// process applies the following changes to the start state's configuration
  /// set.
  ///
  /// <ol>
  /// <li>Evaluate the precedence predicates for each configuration using
  /// {@link SemanticContext#evalPrecedence}.</li>
  /// <li>When {@link ATNConfig#isPrecedenceFilterSuppressed} is [false],
  /// remove all configurations which predict an alternative greater than 1,
  /// for which another configuration that predicts alternative 1 is in the
  /// same ATN state with the same prediction context. This transformation is
  /// valid for the following reasons:
  /// <ul>
  /// <li>The closure block cannot contain any epsilon transitions which bypass
  /// the body of the closure, so all states reachable via alternative 1 are
  /// part of the precedence alternatives of the transformed left-recursive
  /// rule.</li>
  /// <li>The "primary" portion of a left recursive rule cannot contain an
  /// epsilon transition, so the only way an alternative other than 1 can exist
  /// in a state that is also reachable via alternative 1 is by nesting calls
  /// to the left-recursive rule, with the outer calls not being at the
  /// preferred precedence level. The
  /// {@link ATNConfig#isPrecedenceFilterSuppressed} property marks ATN
  /// configurations which do not meet this condition, and therefore are not
  /// eligible for elimination during the filtering process.</li>
  /// </ul>
  /// </li>
  /// </ol>
  ///
  /// <p>
  /// The prediction context must be considered by this filter to address
  /// situations like the following.
  /// </p>
  /// <code>
  /// <pre>
  /// grammar TA;
  /// prog: statement* EOF;
  /// statement: letterA | statement letterA 'b' ;
  /// letterA: 'a';
  /// </pre>
  /// </code>
  /// <p>
  /// If the above grammar, the ATN state immediately before the token
  /// reference {@code 'a'} in [letterA] is reachable from the left edge
  /// of both the primary and closure blocks of the left-recursive rule
  /// [statement]. The prediction context associated with each of these
  /// configurations distinguishes between them, and prevents the alternative
  /// which stepped out to [prog] (and then back in to [statement]
  /// from being eliminated by the filter.
  /// </p>
  ///
  /// @param configs The configuration set computed by
  /// {@link #computeStartState} as the start state for the DFA.
  /// @return The transformed configuration set representing the start state
  /// for a precedence DFA at a particular precedence level (determined by
  /// calling {@link Parser#getPrecedence}).
  ATNConfigSet applyPrecedenceFilter(ATNConfigSet configs) {
    final statesFromAlt1 = <int, PredictionContext>{};
    final configSet = ATNConfigSet(configs.fullCtx);
    for (var config in configs) {
      // handle alt 1 first
      if (config.alt != 1) {
        continue;
      }

      final updatedContext = config.semanticContext.evalPrecedence(
        parser,
        _outerContext,
      );
      if (updatedContext == null) {
        // the configuration was eliminated
        continue;
      }
      assert(config.context != null);
      final configContext = config.context!;

      statesFromAlt1[config.state.stateNumber] = configContext;
      if (updatedContext != config.semanticContext) {
        configSet.add(
            ATNConfig.dup(config, semanticContext: updatedContext), mergeCache);
      } else {
        configSet.add(config, mergeCache);
      }
    }

    for (var config in configs) {
      if (config.alt == 1) {
        // already handled
        continue;
      }

      if (!config.isPrecedenceFilterSuppressed()) {
        /* In the future, this elimination step could be updated to also
				 * filter the prediction context for alternatives predicting alt>1
				 * (basically a graph subtraction algorithm).
				 */
        assert(config.context != null);
        final configContext = config.context!;
        final context = statesFromAlt1[config.state.stateNumber];
        if (context != null && context == configContext) {
          // eliminated
          continue;
        }
      }

      configSet.add(config, mergeCache);
    }

    return configSet;
  }

  ATNState? getReachableTarget(Transition trans, int ttype) {
    if (trans.matches(ttype, 0, atn.maxTokenType)) {
      return trans.target;
    }

    return null;
  }

  List<SemanticContext?>? getPredsForAmbigAlts(
    BitSet ambigAlts,
    ATNConfigSet configs,
    int nalts,
  ) {
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
    final altToPred = List<SemanticContext?>.filled(nalts + 1, null);

    for (var c in configs) {
      if (ambigAlts[c.alt]) {
        altToPred[c.alt] =
            SemanticContext.or(altToPred[c.alt], c.semanticContext);
      }
    }

    var nPredAlts = 0;
    for (var i = 1; i <= nalts; i++) {
      if (altToPred[i] == null) {
        altToPred[i] = SemanticContext.NONE;
      } else if (altToPred[i] != SemanticContext.NONE) {
        nPredAlts++;
      }
    }

//		// Optimize away p||p and p&&p TODO: optimize() was a no-op
//		for (int i = 0; i < altToPred.length; i++) {
//			altToPred[i] = altToPred[i].optimize();
//		}

    if (debug) log('getPredsForAmbigAlts result $altToPred');
    // nonambig alts are null in altToPred
    if (nPredAlts == 0) return null;
    return altToPred;
  }

  List<PredPrediction>? getPredicatePredictions(
    BitSet? ambigAlts,
    List<SemanticContext?> altToPred,
  ) {
    final pairs = <PredPrediction>[];
    var containsPredicate = false;
    for (var i = 1; i < altToPred.length; i++) {
      final pred = altToPred[i];

      // unpredicated is indicated by SemanticContext.NONE
      assert(pred != null);

      if (ambigAlts != null && ambigAlts[i]) {
        pairs.add(PredPrediction(pred!, i));
      }
      if (pred != SemanticContext.NONE) containsPredicate = true;
    }

    if (!containsPredicate) {
      return null;
    }

//		log(Arrays.toString(altToPred)+"->"+pairs);
    return pairs;
  }

  /// This method is used to improve the localization of error messages by
  /// choosing an alternative rather than throwing a
  /// [NoViableAltException] in particular prediction scenarios where the
  /// {@link #ERROR} state was reached during ATN simulation.
  ///
  /// <p>
  /// The default implementation of this method uses the following
  /// algorithm to identify an ATN configuration which successfully parsed the
  /// decision entry rule. Choosing such an alternative ensures that the
  /// [ParserRuleContext] returned by the calling rule will be complete
  /// and valid, and the syntax error will be reported later at a more
  /// localized location.</p>
  ///
  /// <ul>
  /// <li>If a syntactically valid path or paths reach the end of the decision rule and
  /// they are semantically valid if predicated, return the min associated alt.</li>
  /// <li>Else, if a semantically invalid but syntactically valid path exist
  /// or paths exist, return the minimum associated alt.
  /// </li>
  /// <li>Otherwise, return {@link ATN#INVALID_ALT_NUMBER}.</li>
  /// </ul>
  ///
  /// <p>
  /// In some scenarios, the algorithm described above could predict an
  /// alternative which will result in a [FailedPredicateException] in
  /// the parser. Specifically, this could occur if the <em>only</em> configuration
  /// capable of successfully parsing to the end of the decision rule is
  /// blocked by a semantic predicate. By choosing this alternative within
  /// {@link #adaptivePredict} instead of throwing a
  /// [NoViableAltException], the resulting
  /// [FailedPredicateException] in the parser will identify the specific
  /// predicate which is preventing the parser from successfully parsing the
  /// decision rule, which helps developers identify and correct logic errors
  /// in semantic predicates.
  /// </p>
  ///
  /// @param configs The ATN configurations which were valid immediately before
  /// the {@link #ERROR} state was reached
  /// @param outerContext The is the \gamma_0 initial parser context from the paper
  /// or the parser stack at the instant before prediction commences.
  ///
  /// @return The value to return from {@link #adaptivePredict}, or
  /// {@link ATN#INVALID_ALT_NUMBER} if a suitable alternative was not
  /// identified and {@link #adaptivePredict} should report an error instead.
  int getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(
      ATNConfigSet configs, ParserRuleContext outerContext) {
    final sets = splitAccordingToSemanticValidity(configs, outerContext);
    final semValidConfigs = sets.a;
    final semInvalidConfigs = sets.b;
    var alt = getAltThatFinishedDecisionEntryRule(semValidConfigs);
    if (alt != ATN.INVALID_ALT_NUMBER) {
      // semantically/syntactically viable path exists
      return alt;
    }
    // Is there a syntactically valid path with a failed pred?
    if (semInvalidConfigs.isNotEmpty) {
      alt = getAltThatFinishedDecisionEntryRule(semInvalidConfigs);
      if (alt != ATN.INVALID_ALT_NUMBER) {
        // syntactically viable path exists
        return alt;
      }
    }
    return ATN.INVALID_ALT_NUMBER;
  }

  int getAltThatFinishedDecisionEntryRule(ATNConfigSet configs) {
    final alts = IntervalSet();
    for (var c in configs) {
      assert(c.context != null);
      if (c.outerContextDepth > 0 ||
          (c.state is RuleStopState && c.context!.hasEmptyPath())) {
        alts.addOne(c.alt);
      }
    }
    if (alts.length == 0) return ATN.INVALID_ALT_NUMBER;
    return alts.minElement;
  }

  /// Walk the list of configurations and split them according to
  ///  those that have preds evaluating to true/false.  If no pred, assume
  ///  true pred and include in succeeded set.  Returns Pair of sets.
  ///
  ///  Create a new set so as not to alter the incoming parameter.
  ///
  ///  Assumption: the input stream has been restored to the starting point
  ///  prediction, which is where predicates need to evaluate.
  Pair<ATNConfigSet, ATNConfigSet> splitAccordingToSemanticValidity(
    ATNConfigSet configs,
    ParserRuleContext outerContext,
  ) {
    final succeeded = ATNConfigSet(configs.fullCtx);
    final failed = ATNConfigSet(configs.fullCtx);
    for (var c in configs) {
      if (c.semanticContext != SemanticContext.NONE) {
        final predicateEvaluationResult = evalSemanticContextOne(
          c.semanticContext,
          outerContext,
          c.alt,
          configs.fullCtx,
        );
        if (predicateEvaluationResult) {
          succeeded.add(c);
        } else {
          failed.add(c);
        }
      } else {
        succeeded.add(c);
      }
    }
    return Pair<ATNConfigSet, ATNConfigSet>(succeeded, failed);
  }

  /// Look through a list of predicate/alt pairs, returning alts for the
  ///  pairs that win. A [NONE] predicate indicates an alt containing an
  ///  unpredicated config which behaves as "always true." If !complete
  ///  then we stop at the first predicate that evaluates to true. This
  ///  includes pairs with null predicates.
  BitSet evalSemanticContext(
    List<PredPrediction> predPredictions,
    ParserRuleContext outerContext,
    bool complete,
  ) {
    final predictions = BitSet();
    for (var pair in predPredictions) {
      if (pair.pred == SemanticContext.NONE) {
        predictions.set(pair.alt);
        if (!complete) {
          break;
        }
        continue;
      }

      final fullCtx = false; // in dfa
      final predicateEvaluationResult =
          evalSemanticContextOne(pair.pred, outerContext, pair.alt, fullCtx);
      if (debug || dfa_debug) {
        log('eval pred $pair=$predicateEvaluationResult');
      }

      if (predicateEvaluationResult) {
        if (debug || dfa_debug) log('PREDICT ' + pair.alt.toString());
        predictions.set(pair.alt);
        if (!complete) {
          break;
        }
      }
    }

    return predictions;
  }

  /// Evaluate a semantic context within a specific parser context.
  ///
  /// <p>
  /// This method might not be called for every semantic context evaluated
  /// during the prediction process. In particular, we currently do not
  /// evaluate the following but it may change in the future:</p>
  ///
  /// <ul>
  /// <li>Precedence predicates (represented by
  /// {@link SemanticContext.PrecedencePredicate}) are not currently evaluated
  /// through this method.</li>
  /// <li>Operator predicates (represented by {@link SemanticContext.AND} and
  /// {@link SemanticContext.OR}) are evaluated as a single semantic
  /// context, rather than evaluating the operands individually.
  /// Implementations which require evaluation results from individual
  /// predicates should override this method to explicitly handle evaluation of
  /// the operands within operator predicates.</li>
  /// </ul>
  ///
  /// @param pred The semantic context to evaluate
  /// @param parserCallStack The parser context in which to evaluate the
  /// semantic context
  /// @param alt The alternative which is guarded by [pred]
  /// @param fullCtx [true] if the evaluation is occurring during LL
  /// prediction; otherwise, [false] if the evaluation is occurring
  /// during SLL prediction
  ///
  /// @since 4.3
  bool evalSemanticContextOne(
    SemanticContext pred,
    ParserRuleContext? parserCallStack,
    int alt,
    bool fullCtx,
  ) {
    return pred.eval(parser, parserCallStack);
  }

  /* TODO: If we are doing predicates, there is no point in pursuing
		 closure operations if we reach a DFA state that uniquely predicts
		 alternative. We will not be caching that DFA state and it is a
		 waste to pursue the closure. Might have to advance when we do
		 ambig detection thought :(
		  */

  void closure(
      ATNConfig config,
      ATNConfigSet configs,
      Set<ATNConfig> closureBusy,
      bool collectPredicates,
      bool fullCtx,
      bool treatEofAsEpsilon) {
    final initialDepth = 0;
    closureCheckingStopState(config, configs, closureBusy, collectPredicates,
        fullCtx, initialDepth, treatEofAsEpsilon);
    assert(!fullCtx || !configs.dipsIntoOuterContext);
  }

  void closureCheckingStopState(
      ATNConfig config,
      ATNConfigSet configs,
      Set<ATNConfig> closureBusy,
      bool collectPredicates,
      bool fullCtx,
      int depth,
      bool treatEofAsEpsilon) {
    if (debug) log('closure(' + config.toString(parser, true) + ')');

    assert(config.context != null);

    final configContext = config.context!;

    if (config.state is RuleStopState) {
      // We hit rule end. If we have context info, use it
      // run thru all possible stack tops in ctx
      if (!configContext.isEmpty) {
        for (var i = 0; i < configContext.length; i++) {
          if (configContext.getReturnState(i) ==
              PredictionContext.EMPTY_RETURN_STATE) {
            if (fullCtx) {
              configs.add(
                  ATNConfig.dup(
                    config,
                    state: config.state,
                    context: PredictionContext.EMPTY,
                  ),
                  mergeCache);
              continue;
            } else {
              // we have no context info, just chase follow links (if greedy)
              if (debug) {
                log('FALLING off rule ' + getRuleName(config.state.ruleIndex));
              }
              closure_(
                config,
                configs,
                closureBusy,
                collectPredicates,
                fullCtx,
                depth,
                treatEofAsEpsilon,
              );
            }
            continue;
          }
          final returnState = atn.states[configContext.getReturnState(i)]!;
          final newContext = configContext.getParent(i); // "pop" return state
          final c = ATNConfig(
            returnState,
            config.alt,
            newContext,
            config.semanticContext,
          );
          // While we have context to pop back from, we may have
          // gotten that context AFTER having falling off a rule.
          // Make sure we track that we are now out of context.
          //
          // This assignment also propagates the
          // isPrecedenceFilterSuppressed() value to the new
          // configuration.
          c.reachesIntoOuterContext = config.reachesIntoOuterContext;
//          assert(depth > int.MIN_VALUE);
          closureCheckingStopState(c, configs, closureBusy, collectPredicates,
              fullCtx, depth - 1, treatEofAsEpsilon);
        }
        return;
      } else if (fullCtx) {
        // reached end of start rule
        configs.add(config, mergeCache);
        return;
      } else {
        // else if we have no context info, just chase follow links (if greedy)
        if (debug) {
          log('FALLING off rule ' + getRuleName(config.state.ruleIndex));
        }
      }
    }

    closure_(
      config,
      configs,
      closureBusy,
      collectPredicates,
      fullCtx,
      depth,
      treatEofAsEpsilon,
    );
  }

  /// Do the actual work of walking epsilon edges */
  void closure_(
      ATNConfig config,
      ATNConfigSet configs,
      Set<ATNConfig> closureBusy,
      bool collectPredicates,
      bool fullCtx,
      int depth,
      bool treatEofAsEpsilon) {
    final p = config.state;
    // optimization
    if (!p.onlyHasEpsilonTransitions()) {
      configs.add(config, mergeCache);
      // make sure to not return here, because EOF transitions can act as
      // both epsilon transitions and non-epsilon transitions.
//            if ( debug ) log("added config "+configs);
    }

    for (var i = 0; i < p.numberOfTransitions; i++) {
      if (i == 0 && canDropLoopEntryEdgeInLeftRecursiveRule(config)) continue;

      final t = p.transition(i);
      final continueCollecting = (t is! ActionTransition) && collectPredicates;
      final c = getEpsilonTarget(config, t, continueCollecting, depth == 0,
          fullCtx, treatEofAsEpsilon);
      if (c != null) {
        var newDepth = depth;
        if (config.state is RuleStopState) {
          assert(!fullCtx);
          // target fell off end of rule; mark resulting c as having dipped into outer context
          // We can't get here if incoming config was rule stop and we had context
          // track how far we dip into outer context.  Might
          // come in handy and we avoid evaluating context dependent
          // preds if this is > 0.

          if (_dfa != null && _dfa!.isPrecedenceDfa()) {
            final outermostPrecedenceReturn =
                (t as EpsilonTransition).outermostPrecedenceReturn;
            if (outermostPrecedenceReturn == _dfa!.atnStartState!.ruleIndex) {
              c.setPrecedenceFilterSuppressed(true);
            }
          }

          c.reachesIntoOuterContext++;

          if (!closureBusy.add(c)) {
            // avoid infinite recursion for right-recursive rules
            continue;
          }

          // TODO: can remove? only care when we add to set per middle of this method
          configs.dipsIntoOuterContext = true;
//          assert(newDepth > int.MIN_VALUE);
          newDepth--;
          if (debug) log('dips into outer ctx: $c');
        } else {
          if (!t.isEpsilon && !closureBusy.add(c)) {
            // avoid infinite recursion for EOF* and EOF+
            continue;
          }

          if (t is RuleTransition) {
            // latch when newDepth goes negative - once we step out of the entry context we can't return
            if (newDepth >= 0) {
              newDepth++;
            }
          }
        }

        closureCheckingStopState(
          c,
          configs,
          closureBusy,
          continueCollecting,
          fullCtx,
          newDepth,
          treatEofAsEpsilon,
        );
      }
    }
  }

  /// Implements first-edge (loop entry) elimination as an optimization
  ///  during closure operations.  See antlr/antlr4#1398.
  ///
  /// The optimization is to avoid adding the loop entry config when
  /// the exit path can only lead back to the same
  /// StarLoopEntryState after popping context at the rule end state
  /// (traversing only epsilon edges, so we're still in closure, in
  /// this same rule).
  ///
  /// We need to detect any state that can reach loop entry on
  /// epsilon w/o exiting rule. We don't have to look at FOLLOW
  /// links, just ensure that all stack tops for config refer to key
  /// states in LR rule.
  ///
  /// To verify we are in the right situation we must first check
  /// closure is at a StarLoopEntryState generated during LR removal.
  /// Then we check that each stack top of context is a return state
  /// from one of these cases:
  ///
  ///   1. 'not' expr, '(' type ')' expr. The return state points at loop entry state
  ///   2. expr op expr. The return state is the block end of internal block of (...)*
  ///   3. 'between' expr 'and' expr. The return state of 2nd expr reference.
  ///      That state points at block end of internal block of (...)*.
  ///   4. expr '?' expr ':' expr. The return state points at block end,
  ///      which points at loop entry state.
  ///
  /// If any is true for each stack top, then closure does not add a
  /// config to the current config set for edge[0], the loop entry branch.
  ///
  ///  Conditions fail if any context for the current config is:
  ///
  ///   a. empty (we'd fall out of expr to do a global FOLLOW which could
  ///      even be to some weird spot in expr) or,
  ///   b. lies outside of expr or,
  ///   c. lies within expr but at a state not the BlockEndState
  ///   generated during LR removal
  ///
  /// Do we need to evaluate predicates ever in closure for this case?
  ///
  /// No. Predicates, including precedence predicates, are only
  /// evaluated when computing a DFA start state. I.e., only before
  /// the lookahead (but not parser) consumes a token.
  ///
  /// There are no epsilon edges allowed in LR rule alt blocks or in
  /// the "primary" part (ID here). If closure is in
  /// StarLoopEntryState any lookahead operation will have consumed a
  /// token as there are no epsilon-paths that lead to
  /// StarLoopEntryState. We do not have to evaluate predicates
  /// therefore if we are in the generated StarLoopEntryState of a LR
  /// rule. Note that when making a prediction starting at that
  /// decision point, decision d=2, compute-start-state performs
  /// closure starting at edges[0], edges[1] emanating from
  /// StarLoopEntryState. That means it is not performing closure on
  /// StarLoopEntryState during compute-start-state.
  ///
  /// How do we know this always gives same prediction answer?
  ///
  /// Without predicates, loop entry and exit paths are ambiguous
  /// upon remaining input +b (in, say, a+b). Either paths lead to
  /// valid parses. Closure can lead to consuming + immediately or by
  /// falling out of this call to expr back into expr and loop back
  /// again to StarLoopEntryState to match +b. In this special case,
  /// we choose the more efficient path, which is to take the bypass
  /// path.
  ///
  /// The lookahead language has not changed because closure chooses
  /// one path over the other. Both paths lead to consuming the same
  /// remaining input during a lookahead operation. If the next token
  /// is an operator, lookahead will enter the choice block with
  /// operators. If it is not, lookahead will exit expr. Same as if
  /// closure had chosen to enter the choice block immediately.
  ///
  /// Closure is examining one config (some loopentrystate, some alt,
  /// context) which means it is considering exactly one alt. Closure
  /// always copies the same alt to any derived configs.
  ///
  /// How do we know this optimization doesn't mess up precedence in
  /// our parse trees?
  ///
  /// Looking through expr from left edge of stat only has to confirm
  /// that an input, say, a+b+c; begins with any valid interpretation
  /// of an expression. The precedence actually doesn't matter when
  /// making a decision in stat seeing through expr. It is only when
  /// parsing rule expr that we must use the precedence to get the
  /// right interpretation and, hence, parse tree.
  ///
  /// @since 4.6
  bool canDropLoopEntryEdgeInLeftRecursiveRule(ATNConfig config) {
    if (TURN_OFF_LR_LOOP_ENTRY_BRANCH_OPT) return false;
    final p = config.state;

    assert(config.context != null);
    // First check to see if we are in StarLoopEntryState generated during
    // left-recursion elimination. For efficiency, also check if
    // the context has an empty stack case. If so, it would mean
    // global FOLLOW so we can't perform optimization
    if (p.stateType != StateType.STAR_LOOP_ENTRY ||
        !(p as StarLoopEntryState)
            .isPrecedenceDecision || // Are we the special loop entry/exit state?
        config.context!.isEmpty || // If SLL wildcard
        config.context!.hasEmptyPath()) {
      return false;
    }

    final configContext = config.context!;

    // Require all return states to return back to the same rule
    // that p is in.
    final numCtxs = configContext.length;
    for (var i = 0; i < numCtxs; i++) {
      // for each stack context
      final returnState = atn.states[configContext.getReturnState(i)]!;
      if (returnState.ruleIndex != p.ruleIndex) return false;
    }

    final decisionStartState = p.transition(0).target as BlockStartState;
    final blockEndStateNum = decisionStartState.endState!.stateNumber;
    final blockEndState = atn.states[blockEndStateNum] as BlockEndState;

    // Verify that the top of each stack context leads to loop entry/exit
    // state through epsilon edges and w/o leaving rule.
    for (var i = 0; i < numCtxs; i++) {
      // for each stack context
      final returnStateNumber = configContext.getReturnState(i);
      final returnState = atn.states[returnStateNumber]!;
      // all states must have single outgoing epsilon edge
      if (returnState.numberOfTransitions != 1 ||
          !returnState.transition(0).isEpsilon) {
        return false;
      }
      // Look for prefix op case like 'not expr', (' type ')' expr
      final returnStateTarget = returnState.transition(0).target;
      if (returnState.stateType == StateType.BLOCK_END &&
          returnStateTarget == p) {
        continue;
      }
      // Look for 'expr op expr' or case where expr's return state is block end
      // of (...)* internal block; the block end points to loop back
      // which points to p but we don't need to check that
      if (returnState == blockEndState) {
        continue;
      }
      // Look for ternary expr ? expr : expr. The return state points at block end,
      // which points at loop entry state
      if (returnStateTarget == blockEndState) {
        continue;
      }
      // Look for complex prefix 'between expr and expr' case where 2nd expr's
      // return state points at block end state of (...)* internal block
      if (returnStateTarget.stateType == StateType.BLOCK_END &&
          returnStateTarget.numberOfTransitions == 1 &&
          returnStateTarget.transition(0).isEpsilon &&
          returnStateTarget.transition(0).target == p) {
        continue;
      }

      // anything else ain't conforming
      return false;
    }

    return true;
  }

  String getRuleName(int index) {
    if (index >= 0) return parser.ruleNames[index];
    return '<rule $index>';
  }

  ATNConfig? getEpsilonTarget(
      ATNConfig config,
      Transition t,
      bool collectPredicates,
      bool inContext,
      bool fullCtx,
      bool treatEofAsEpsilon) {
    switch (t.type) {
      case TransitionType.RULE:
        return ruleTransition(config, t as RuleTransition);

      case TransitionType.PRECEDENCE:
        return precedenceTransition(
          config,
          t as PrecedencePredicateTransition,
          collectPredicates,
          inContext,
          fullCtx,
        );

      case TransitionType.PREDICATE:
        return predTransition(
          config,
          t as PredicateTransition,
          collectPredicates,
          inContext,
          fullCtx,
        );
      case TransitionType.ACTION:
        return actionTransition(config, t as ActionTransition);

      case TransitionType.EPSILON:
        return ATNConfig.dup(config, state: t.target);

      case TransitionType.ATOM:
      case TransitionType.RANGE:
      case TransitionType.SET:
        // EOF transitions act like epsilon transitions after the first EOF
        // transition is traversed
        if (treatEofAsEpsilon) {
          if (t.matches(Token.EOF, 0, 1)) {
            return ATNConfig.dup(config, state: t.target);
          }
        }

        return null;

      default:
        return null;
    }
  }

  ATNConfig actionTransition(ATNConfig config, ActionTransition t) {
    if (debug) log('ACTION edge ${t.ruleIndex}:${t.actionIndex}');
    return ATNConfig.dup(config, state: t.target);
  }

  ATNConfig? precedenceTransition(
      ATNConfig config,
      PrecedencePredicateTransition pt,
      bool collectPredicates,
      bool inContext,
      bool fullCtx) {
    if (debug) {
      log('PRED (collectPredicates=$collectPredicates) ${pt.precedence}>=_p, ctx dependent=true');

      log('context surrounding pred is ${parser.getRuleInvocationStack()}');
    }

    ATNConfig? c;
    if (collectPredicates && inContext) {
      if (fullCtx) {
        // In full context mode, we can evaluate predicates on-the-fly
        // during closure, which dramatically reduces the size of
        // the config sets. It also obviates the need to test predicates
        // later during conflict resolution.
        final currentPosition = input.index;
        input.seek(startIndex);
        final predSucceeds = evalSemanticContextOne(
            pt.predicate, _outerContext, config.alt, fullCtx);
        input.seek(currentPosition);
        if (predSucceeds) {
          c = ATNConfig.dup(config, state: pt.target); // no pred context
        }
      } else {
        final newSemCtx = SemanticContext.and(
          config.semanticContext,
          pt.predicate,
        );
        c = ATNConfig.dup(config, state: pt.target, semanticContext: newSemCtx);
      }
    } else {
      c = ATNConfig.dup(config, state: pt.target);
    }

    if (debug) log('config from pred transition=$c');
    return c;
  }

  ATNConfig? predTransition(
    ATNConfig config,
    PredicateTransition pt,
    bool collectPredicates,
    bool inContext,
    bool fullCtx,
  ) {
    if (debug) {
      log('PRED (collectPredicates=$collectPredicates) '
          '${pt.ruleIndex}:${pt.predIndex}'
          ', ctx dependent=${pt.isCtxDependent}');

      log('context surrounding pred is ${parser.getRuleInvocationStack()}');
    }

    ATNConfig? c;
    if (collectPredicates &&
        (!pt.isCtxDependent || (pt.isCtxDependent && inContext))) {
      if (fullCtx) {
        // In full context mode, we can evaluate predicates on-the-fly
        // during closure, which dramatically reduces the size of
        // the config sets. It also obviates the need to test predicates
        // later during conflict resolution.
        final currentPosition = input.index;
        input.seek(startIndex);
        final predSucceeds = evalSemanticContextOne(
          pt.predicate,
          _outerContext,
          config.alt,
          fullCtx,
        );
        input.seek(currentPosition);
        if (predSucceeds) {
          c = ATNConfig.dup(config, state: pt.target); // no pred context
        }
      } else {
        final newSemCtx =
            SemanticContext.and(config.semanticContext, pt.predicate);
        c = ATNConfig.dup(config, state: pt.target, semanticContext: newSemCtx);
      }
    } else {
      c = ATNConfig.dup(config, state: pt.target);
    }

    if (debug) log('config from pred transition=$c');
    return c;
  }

  ATNConfig ruleTransition(ATNConfig config, RuleTransition t) {
    if (debug) {
      log('CALL rule ' +
          getRuleName(t.target.ruleIndex) +
          ', ctx=${config.context}');
    }

    final returnState = t.followState;
    PredictionContext newContext = SingletonPredictionContext.create(
        config.context, returnState.stateNumber);
    return ATNConfig.dup(config, state: t.target, context: newContext);
  }

  /// Gets a [BitSet] containing the alternatives in [configs]
  /// which are part of one or more conflicting alternative subsets.
  ///
  /// @param configs The [ATNConfigSet] to analyze.
  /// @return The alternatives in [configs] which are part of one or more
  /// conflicting alternative subsets. If [configs] does not contain any
  /// conflicting subsets, this method returns an empty [BitSet].
  BitSet getConflictingAlts(ATNConfigSet configs) {
    final altsets = PredictionModeExtension.getConflictingAltSubsets(configs);
    return PredictionModeExtension.getAlts(altsets);
  }

  /// Sam pointed out a problem with the previous definition, v3, of
  /// ambiguous states. If we have another state associated with conflicting
  /// alternatives, we should keep going. For example, the following grammar
  ///
  /// s : (ID | ID ID?) ';' ;
  ///
  /// When the ATN simulation reaches the state before ';', it has a DFA
  /// state that looks like: [12|1|[], 6|2|[], 12|2|[]]. Naturally
  /// 12|1|[] and 12|2|[] conflict, but we cannot stop processing this node
  /// because alternative to has another way to continue, via [6|2|[]].
  /// The key is that we have a single state that has config's only associated
  /// with a single alternative, 2, and crucially the state transitions
  /// among the configurations are all non-epsilon transitions. That means
  /// we don't consider any conflicts that include alternative 2. So, we
  /// ignore the conflict between alts 1 and 2. We ignore a set of
  /// conflicting alts when there is an intersection with an alternative
  /// associated with a single alt state in the state&rarr;config-list map.
  ///
  /// It's also the case that we might have two conflicting configurations but
  /// also a 3rd nonconflicting configuration for a different alternative:
  /// [1|1|[], 1|2|[], 8|3|[]]. This can come about from grammar:
  ///
  /// a : A | A | A B ;
  ///
  /// After matching input A, we reach the stop state for rule A, state 1.
  /// State 8 is the state right before B. Clearly alternatives 1 and 2
  /// conflict and no amount of further lookahead will separate the two.
  /// However, alternative 3 will be able to continue and so we do not
  /// stop working on this state. In the previous example, we're concerned
  /// with states associated with the conflicting alternatives. Here alt
  /// 3 is not associated with the conflicting configs, but since we can continue
  /// looking for input reasonably, I don't declare the state done. We
  /// ignore a set of conflicting alts when we have an alternative
  /// that we still need to pursue.
  BitSet getConflictingAltsOrUniqueAlt(ATNConfigSet configs) {
    BitSet? conflictingAlts;
    if (configs.uniqueAlt != ATN.INVALID_ALT_NUMBER) {
      conflictingAlts = BitSet();
      conflictingAlts.set(configs.uniqueAlt);
    } else {
      conflictingAlts = configs.conflictingAlts;
    }
    return conflictingAlts!;
  }

  String getTokenName(int t) {
    if (t == Token.EOF) {
      return 'EOF';
    }

    final vocabulary = parser.vocabulary;
    final displayName = vocabulary.getDisplayName(t);
    if (displayName == t.toString()) {
      return displayName;
    }

    return displayName + '<$t>';
  }

  String getLookaheadName(TokenStream input) {
    return getTokenName(input.LA(1)!);
  }

  /// Used for debugging in adaptivePredict around execATN but I cut
  ///  it out for clarity now that alg. works well. We can leave this
  ///  "dead" code for a bit.
  void dumpDeadEndConfigs(NoViableAltException nvae) {
    log('dead end configs: ', level: Level.SEVERE.value);
    for (var c in nvae.deadEndConfigs!) {
      var trans = 'no edges';
      if (c.state.numberOfTransitions > 0) {
        final t = c.state.transition(0);
        if (t is AtomTransition) {
          final at = t;
          trans = 'Atom ' + getTokenName(at.atomLabel);
        } else if (t is SetTransition) {
          final st = t;
          final not = st is NotSetTransition;
          trans = (not ? '~' : '') + 'Set ' + st.label.toString();
        }
      }
      log(c.toString(parser, true) + ':' + trans, level: Level.SEVERE.value);
    }
  }

  NoViableAltException noViableAlt(
    TokenStream input,
    ParserRuleContext outerContext,
    ATNConfigSet configs,
    int startIndex,
  ) {
    return NoViableAltException(
      parser,
      input,
      input.get(startIndex),
      input.LT(1),
      configs,
      outerContext,
    );
  }

  static int getUniqueAlt(ATNConfigSet configs) {
    var alt = ATN.INVALID_ALT_NUMBER;
    for (var c in configs) {
      if (alt == ATN.INVALID_ALT_NUMBER) {
        alt = c.alt; // found first alt
      } else if (c.alt != alt) {
        return ATN.INVALID_ALT_NUMBER;
      }
    }
    return alt;
  }

  /// Add an edge to the DFA, if possible. This method calls
  /// {@link #addDFAState} to ensure the [to] state is present in the
  /// DFA. If [from] is null, or if [t] is outside the
  /// range of edges that can be represented in the DFA tables, this method
  /// returns without adding the edge to the DFA.
  ///
  /// <p>If [to] is null, this method returns null.
  /// Otherwise, this method returns the [DFAState] returned by calling
  /// {@link #addDFAState} for the [to] state.</p>
  ///
  /// @param dfa The DFA
  /// @param from The source state for the edge
  /// @param t The input symbol
  /// @param to The target state for the edge
  ///
  /// @return If [to] is null, this method returns null;
  /// otherwise this method returns the result of calling {@link #addDFAState}
  /// on [to]
  DFAState? addDFAEdge(DFA dfa, DFAState? from, int t, DFAState to) {
    if (debug) {
      log('EDGE $from -> $to upon ' + getTokenName(t));
    }

    to = addDFAState(dfa, to); // used existing if possible not incoming
    if (from == null || t < -1 || t > atn.maxTokenType) {
      return to;
    }

    from.edges ??= List.filled(atn.maxTokenType + 1 + 1, null);

    from.edges![t + 1] = to; // connect

    if (debug) {
      log('DFA=\n' + dfa.toString(parser.vocabulary));
    }

    return to;
  }

  /// Add state [D] to the DFA if it is not already present, and return
  /// the actual instance stored in the DFA. If a state equivalent to [D]
  /// is already in the DFA, the existing state is returned. Otherwise this
  /// method returns [D] after adding it to the DFA.
  ///
  /// <p>If [D] is {@link #ERROR}, this method returns {@link #ERROR} and
  /// does not change the DFA.</p>
  ///
  /// @param dfa The dfa
  /// @param D The DFA state to add
  /// @return The state stored in the DFA. This will be either the existing
  /// state if [D] is already in the DFA, or [D] itself if the
  /// state was not already present.
  DFAState addDFAState(DFA dfa, DFAState D) {
    if (D == ATNSimulator.ERROR) {
      return D;
    }

    final existing = dfa.states[D];
    if (existing != null) return existing;

    D.stateNumber = dfa.states.length;
    if (!D.configs.readOnly) {
      D.configs.optimizeConfigs(this);
      D.configs.readOnly = true;
    }
    dfa.states[D] = D;
    if (debug) log('adding new DFA state: $D');
    return D;
  }

  void reportAttemptingFullContext(
    DFA dfa,
    BitSet? conflictingAlts,
    ATNConfigSet configs,
    int startIndex,
    int stopIndex,
  ) {
    if (debug || retry_debug) {
      final interval = Interval.of(startIndex, stopIndex);
      log(
        'reportAttemptingFullContext decision=${dfa.decision}:$configs'
                ', input=' +
            parser.tokenStream.getText(interval),
      );
    }

    parser.errorListenerDispatch.reportAttemptingFullContext(
      parser,
      dfa,
      startIndex,
      stopIndex,
      conflictingAlts,
      configs,
    );
  }

  void reportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs,
      int startIndex, int stopIndex) {
    if (debug || retry_debug) {
      final interval = Interval.of(startIndex, stopIndex);
      log(
        'reportContextSensitivity decision=${dfa.decision}:$configs'
                ', input=' +
            parser.tokenStream.getText(interval),
      );
    }

    parser.errorListenerDispatch.reportContextSensitivity(
      parser,
      dfa,
      startIndex,
      stopIndex,
      prediction,
      configs,
    );
  }

  /// If context sensitive parsing, we know it's ambiguity not conflict */
  void reportAmbiguity(
    DFA dfa,
    DFAState D, // the DFA state from execATN() that had SLL conflicts
    int startIndex,
    int stopIndex,
    bool exact,
    BitSet? ambigAlts,
    ATNConfigSet configs,
  ) // configs that LL not SLL considered conflicting
  {
    if (debug || retry_debug) {
      final interval = Interval.of(startIndex, stopIndex);
      log(
        'reportAmbiguity $ambigAlts:$configs' ', input=' +
            parser.tokenStream.getText(interval),
      );
    }

    parser.errorListenerDispatch.reportAmbiguity(
      parser,
      dfa,
      startIndex,
      stopIndex,
      exact,
      ambigAlts,
      configs,
    );
  }
}

/// This enumeration defines the prediction modes available in ANTLR 4 along with
/// utility methods for analyzing configuration sets for conflicts and/or
/// ambiguities.
enum PredictionMode {
  /// The SLL(*) prediction mode. This prediction mode ignores the current
  /// parser context when making predictions. This is the fastest prediction
  /// mode, and provides correct results for many grammars. This prediction
  /// mode is more powerful than the prediction mode provided by ANTLR 3, but
  /// may result in syntax errors for grammar and input combinations which are
  /// not SLL.
  ///
  /// <p>
  /// When using this prediction mode, the parser will either return a correct
  /// parse tree (i.e. the same parse tree that would be returned with the
  /// {@link #LL} prediction mode), or it will report a syntax error. If a
  /// syntax error is encountered when using the {@link #SLL} prediction mode,
  /// it may be due to either an actual syntax error in the input or indicate
  /// that the particular combination of grammar and input requires the more
  /// powerful {@link #LL} prediction abilities to complete successfully.</p>
  ///
  /// <p>
  /// This prediction mode does not provide any guarantees for prediction
  /// behavior for syntactically-incorrect inputs.</p>
  SLL,

  /// The LL(*) prediction mode. This prediction mode allows the current parser
  /// context to be used for resolving SLL conflicts that occur during
  /// prediction. This is the fastest prediction mode that guarantees correct
  /// parse results for all combinations of grammars with syntactically correct
  /// inputs.
  ///
  /// <p>
  /// When using this prediction mode, the parser will make correct decisions
  /// for all syntactically-correct grammar and input combinations. However, in
  /// cases where the grammar is truly ambiguous this prediction mode might not
  /// report a precise answer for <em>exactly which</em> alternatives are
  /// ambiguous.</p>
  ///
  /// <p>
  /// This prediction mode does not provide any guarantees for prediction
  /// behavior for syntactically-incorrect inputs.</p>
  LL,

  /// The LL(*) prediction mode with exact ambiguity detection. In addition to
  /// the correctness guarantees provided by the {@link #LL} prediction mode,
  /// this prediction mode instructs the prediction algorithm to determine the
  /// complete and exact set of ambiguous alternatives for every ambiguous
  /// decision encountered while parsing.
  ///
  /// <p>
  /// This prediction mode may be used for diagnosing ambiguities during
  /// grammar development. Due to the performance overhead of calculating sets
  /// of ambiguous alternatives, this prediction mode should be avoided when
  /// the exact results are not necessary.</p>
  ///
  /// <p>
  /// This prediction mode does not provide any guarantees for prediction
  /// behavior for syntactically-incorrect inputs.</p>
  LL_EXACT_AMBIG_DETECTION,
}

extension PredictionModeExtension on PredictionMode {
  /// Computes the SLL prediction termination condition.
  ///
  /// <p>
  /// This method computes the SLL prediction termination condition for both of
  /// the following cases.</p>
  ///
  /// <ul>
  /// <li>The usual SLL+LL fallback upon SLL conflict</li>
  /// <li>Pure SLL without LL fallback</li>
  /// </ul>
  ///
  /// <p><strong>COMBINED SLL+LL PARSING</strong></p>
  ///
  /// <p>When LL-fallback is enabled upon SLL conflict, correct predictions are
  /// ensured regardless of how the termination condition is computed by this
  /// method. Due to the substantially higher cost of LL prediction, the
  /// prediction should only fall back to LL when the additional lookahead
  /// cannot lead to a unique SLL prediction.</p>
  ///
  /// <p>Assuming combined SLL+LL parsing, an SLL configuration set with only
  /// conflicting subsets should fall back to full LL, even if the
  /// configuration sets don't resolve to the same alternative (e.g.
  /// {@code {1,2}} and {@code {3,4}}. If there is at least one non-conflicting
  /// configuration, SLL could continue with the hopes that more lookahead will
  /// resolve via one of those non-conflicting configurations.</p>
  ///
  /// <p>Here's the prediction termination rule them: SLL (for SLL+LL parsing)
  /// stops when it sees only conflicting configuration subsets. In contrast,
  /// full LL keeps going when there is uncertainty.</p>
  ///
  /// <p><strong>HEURISTIC</strong></p>
  ///
  /// <p>As a heuristic, we stop prediction when we see any conflicting subset
  /// unless we see a state that only has one alternative associated with it.
  /// The single-alt-state thing lets prediction continue upon rules like
  /// (otherwise, it would admit defeat too soon):</p>
  ///
  /// <p>{@code [12|1|[], 6|2|[], 12|2|[]]. s : (ID | ID ID?) ';' ;}</p>
  ///
  /// <p>When the ATN simulation reaches the state before {@code ';'}, it has a
  /// DFA state that looks like: {@code [12|1|[], 6|2|[], 12|2|[]]}. Naturally
  /// {@code 12|1|[]} and {@code 12|2|[]} conflict, but we cannot stop
  /// processing this node because alternative to has another way to continue,
  /// via {@code [6|2|[]]}.</p>
  ///
  /// <p>It also let's us continue for this rule:</p>
  ///
  /// <p>{@code [1|1|[], 1|2|[], 8|3|[]] a : A | A | A B ;}</p>
  ///
  /// <p>After matching input A, we reach the stop state for rule A, state 1.
  /// State 8 is the state right before B. Clearly alternatives 1 and 2
  /// conflict and no amount of further lookahead will separate the two.
  /// However, alternative 3 will be able to continue and so we do not stop
  /// working on this state. In the previous example, we're concerned with
  /// states associated with the conflicting alternatives. Here alt 3 is not
  /// associated with the conflicting configs, but since we can continue
  /// looking for input reasonably, don't declare the state done.</p>
  ///
  /// <p><strong>PURE SLL PARSING</strong></p>
  ///
  /// <p>To handle pure SLL parsing, all we have to do is make sure that we
  /// combine stack contexts for configurations that differ only by semantic
  /// predicate. From there, we can do the usual SLL termination heuristic.</p>
  ///
  /// <p><strong>PREDICATES IN SLL+LL PARSING</strong></p>
  ///
  /// <p>SLL decisions don't evaluate predicates until after they reach DFA stop
  /// states because they need to create the DFA cache that works in all
  /// semantic situations. In contrast, full LL evaluates predicates collected
  /// during start state computation so it can ignore predicates thereafter.
  /// This means that SLL termination detection can totally ignore semantic
  /// predicates.</p>
  ///
  /// <p>Implementation-wise, [ATNConfigSet] combines stack contexts but not
  /// semantic predicate contexts so we might see two configurations like the
  /// following.</p>
  ///
  /// <p>{@code (s, 1, x, {}), (s, 1, x', {p})}</p>
  ///
  /// <p>Before testing these configurations against others, we have to merge
  /// [x] and {@code x'} (without modifying the existing configurations).
  /// For example, we test {@code (x+x')==x''} when looking for conflicts in
  /// the following configurations.</p>
  ///
  /// <p>{@code (s, 1, x, {}), (s, 1, x', {p}), (s, 2, x'', {})}</p>
  ///
  /// <p>If the configuration set has predicates (as indicated by
  /// {@link ATNConfigSet#hasSemanticContext}), this algorithm makes a copy of
  /// the configurations to strip out all of the predicates so that a standard
  /// [ATNConfigSet] will merge everything ignoring predicates.</p>
  static bool hasSLLConflictTerminatingPrediction(
      PredictionMode mode, ATNConfigSet configs) {
/* Configs in rule stop states indicate reaching the end of the decision
		 * rule (local context) or end of start rule (full context). If all
		 * configs meet this condition, then none of the configurations is able
		 * to match additional input so we terminate prediction.
		 */
    if (allConfigsInRuleStopStates(configs)) {
      return true;
    }

// pure SLL mode parsing
    if (mode == PredictionMode.SLL) {
// Don't bother with combining configs from different semantic
// contexts if we can fail over to full LL; costs more time
// since we'll often fail over anyway.
      if (configs.hasSemanticContext) {
// dup configs, tossing out semantic predicates
        final dup = ATNConfigSet();
        for (var c in configs) {
          c = ATNConfig.dup(c, semanticContext: SemanticContext.NONE);
          dup.add(c);
        }
        configs = dup;
      }
// now we have combined contexts for configs with dissimilar preds
    }

// pure SLL or combined SLL+LL mode parsing

    final altsets = getConflictingAltSubsets(configs);
    final heuristic =
        hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(configs);
    return heuristic;
  }

  /// Checks if any configuration in [configs] is in a
  /// [RuleStopState]. Configurations meeting this condition have reached
  /// the end of the decision rule (local context) or end of start rule (full
  /// context).
  ///
  /// @param configs the configuration set to test
  /// @return [true] if any configuration in [configs] is in a
  /// [RuleStopState], otherwise [false]
  static bool hasConfigInRuleStopState(ATNConfigSet configs) {
    for (var c in configs) {
      if (c.state is RuleStopState) {
        return true;
      }
    }

    return false;
  }

  /// Checks if all configurations in [configs] are in a
  /// [RuleStopState]. Configurations meeting this condition have reached
  /// the end of the decision rule (local context) or end of start rule (full
  /// context).
  ///
  /// @param configs the configuration set to test
  /// @return [true] if all configurations in [configs] are in a
  /// [RuleStopState], otherwise [false]
  static bool allConfigsInRuleStopStates(ATNConfigSet configs) {
    for (var config in configs) {
      if (config.state is! RuleStopState) {
        return false;
      }
    }

    return true;
  }

  /// Full LL prediction termination.
  ///
  /// <p>Can we stop looking ahead during ATN simulation or is there some
  /// uncertainty as to which alternative we will ultimately pick, after
  /// consuming more input? Even if there are partial conflicts, we might know
  /// that everything is going to resolve to the same minimum alternative. That
  /// means we can stop since no more lookahead will change that fact. On the
  /// other hand, there might be multiple conflicts that resolve to different
  /// minimums. That means we need more look ahead to decide which of those
  /// alternatives we should predict.</p>
  ///
  /// <p>The basic idea is to split the set of configurations [C], into
  /// conflicting subsets {@code (s, _, ctx, _)} and singleton subsets with
  /// non-conflicting configurations. Two configurations conflict if they have
  /// identical {@link ATNConfig#state} and {@link ATNConfig#context} values
  /// but different {@link ATNConfig#alt} value, e.g. {@code (s, i, ctx, _)}
  /// and {@code (s, j, ctx, _)} for {@code i!=j}.</p>
  ///
  /// <p>Reduce these configuration subsets to the set of possible alternatives.
  /// You can compute the alternative subsets in one pass as follows:</p>
  ///
  /// <p>{@code A_s,ctx = {i | (s, i, ctx, _)}} for each configuration in
  /// [C] holding [s] and [ctx] fixed.</p>
  ///
  /// <p>Or in pseudo-code, for each configuration [c] in [C]:</p>
  ///
  /// <pre>
  /// map[c] U= c.{@link ATNConfig#alt alt} # map hash/equals uses s and x, not
  /// alt and not pred
  /// </pre>
  ///
  /// <p>The values in [map] are the set of {@code A_s,ctx} sets.</p>
  ///
  /// <p>If {@code |A_s,ctx|=1} then there is no conflict associated with
  /// [s] and [ctx].</p>
  ///
  /// <p>Reduce the subsets to singletons by choosing a minimum of each subset. If
  /// the union of these alternative subsets is a singleton, then no amount of
  /// more lookahead will help us. We will always pick that alternative. If,
  /// however, there is more than one alternative, then we are uncertain which
  /// alternative to predict and must continue looking for resolution. We may
  /// or may not discover an ambiguity in the future, even if there are no
  /// conflicting subsets this round.</p>
  ///
  /// <p>The biggest sin is to terminate early because it means we've made a
  /// decision but were uncertain as to the eventual outcome. We haven't used
  /// enough lookahead. On the other hand, announcing a conflict too late is no
  /// big deal; you will still have the conflict. It's just inefficient. It
  /// might even look until the end of file.</p>
  ///
  /// <p>No special consideration for semantic predicates is required because
  /// predicates are evaluated on-the-fly for full LL prediction, ensuring that
  /// no configuration contains a semantic context during the termination
  /// check.</p>
  ///
  /// <p><strong>CONFLICTING CONFIGS</strong></p>
  ///
  /// <p>Two configurations {@code (s, i, x)} and {@code (s, j, x')}, conflict
  /// when {@code i!=j} but {@code x=x'}. Because we merge all
  /// {@code (s, i, _)} configurations together, that means that there are at
  /// most [n] configurations associated with state [s] for
  /// [n] possible alternatives in the decision. The merged stacks
  /// complicate the comparison of configuration contexts [x] and
  /// {@code x'}. Sam checks to see if one is a subset of the other by calling
  /// merge and checking to see if the merged result is either [x] or
  /// {@code x'}. If the [x] associated with lowest alternative [i]
  /// is the superset, then [i] is the only possible prediction since the
  /// others resolve to {@code min(i)} as well. However, if [x] is
  /// associated with {@code j>i} then at least one stack configuration for
  /// [j] is not in conflict with alternative [i]. The algorithm
  /// should keep going, looking for more lookahead due to the uncertainty.</p>
  ///
  /// <p>For simplicity, I'm doing a equality check between [x] and
  /// {@code x'} that lets the algorithm continue to consume lookahead longer
  /// than necessary. The reason I like the equality is of course the
  /// simplicity but also because that is the test you need to detect the
  /// alternatives that are actually in conflict.</p>
  ///
  /// <p><strong>CONTINUE/STOP RULE</strong></p>
  ///
  /// <p>Continue if union of resolved alternative sets from non-conflicting and
  /// conflicting alternative subsets has more than one alternative. We are
  /// uncertain about which alternative to predict.</p>
  ///
  /// <p>The complete set of alternatives, {@code [i for (_,i,_)]}, tells us which
  /// alternatives are still in the running for the amount of input we've
  /// consumed at this point. The conflicting sets let us to strip away
  /// configurations that won't lead to more states because we resolve
  /// conflicts to the configuration with a minimum alternate for the
  /// conflicting set.</p>
  ///
  /// <p><strong>CASES</strong></p>
  ///
  /// <ul>
  ///
  /// <li>no conflicts and more than 1 alternative in set =&gt; continue</li>
  ///
  /// <li> {@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s, 3, z)},
  /// {@code (s', 1, y)}, {@code (s', 2, y)} yields non-conflicting set
  /// {@code {3}} U conflicting sets {@code min({1,2})} U {@code min({1,2})} =
  /// {@code {1,3}} =&gt; continue
  /// </li>
  ///
  /// <li>{@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s', 1, y)},
  /// {@code (s', 2, y)}, {@code (s'', 1, z)} yields non-conflicting set
  /// {@code {1}} U conflicting sets {@code min({1,2})} U {@code min({1,2})} =
  /// {@code {1}} =&gt; stop and predict 1</li>
  ///
  /// <li>{@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s', 1, y)},
  /// {@code (s', 2, y)} yields conflicting, reduced sets {@code {1}} U
  /// {@code {1}} = {@code {1}} =&gt; stop and predict 1, can announce
  /// ambiguity {@code {1,2}}</li>
  ///
  /// <li>{@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s', 2, y)},
  /// {@code (s', 3, y)} yields conflicting, reduced sets {@code {1}} U
  /// {@code {2}} = {@code {1,2}} =&gt; continue</li>
  ///
  /// <li>{@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s', 3, y)},
  /// {@code (s', 4, y)} yields conflicting, reduced sets {@code {1}} U
  /// {@code {3}} = {@code {1,3}} =&gt; continue</li>
  ///
  /// </ul>
  ///
  /// <p><strong>EXACT AMBIGUITY DETECTION</strong></p>
  ///
  /// <p>If all states report the same conflicting set of alternatives, then we
  /// know we have the exact ambiguity set.</p>
  ///
  /// <p><code>|A_<em>i</em>|&gt;1</code> and
  /// <code>A_<em>i</em> = A_<em>j</em></code> for all <em>i</em>, <em>j</em>.</p>
  ///
  /// <p>In other words, we continue examining lookahead until all {@code A_i}
  /// have more than one alternative and all {@code A_i} are the same. If
  /// {@code A={{1,2}, {1,3}}}, then regular LL prediction would terminate
  /// because the resolved set is {@code {1}}. To determine what the real
  /// ambiguity is, we have to know whether the ambiguity is between one and
  /// two or one and three so we keep going. We can only stop prediction when
  /// we need exact ambiguity detection when the sets look like
  /// {@code A={{1,2}}} or {@code {{1,2},{1,2}}}, etc...</p>
  static int resolvesToJustOneViableAlt(List<BitSet> altsets) {
    return getSingleViableAlt(altsets);
  }

  /// Determines if every alternative subset in [altsets] contains more
  /// than one alternative.
  ///
  /// @param altsets a collection of alternative subsets
  /// @return [true] if every [BitSet] in [altsets] has
  /// {@link BitSet#cardinality cardinality} &gt; 1, otherwise [false]
  static bool allSubsetsConflict(List<BitSet> altsets) {
    return !hasNonConflictingAltSet(altsets);
  }

  /// Determines if any single alternative subset in [altsets] contains
  /// exactly one alternative.
  ///
  /// @param altsets a collection of alternative subsets
  /// @return [true] if [altsets] contains a [BitSet] with
  /// {@link BitSet#cardinality cardinality} 1, otherwise [false]
  static bool hasNonConflictingAltSet(List<BitSet> altsets) {
    for (var alts in altsets) {
      if (alts.cardinality == 1) {
        return true;
      }
    }
    return false;
  }

  /// Determines if any single alternative subset in [altsets] contains
  /// more than one alternative.
  ///
  /// @param altsets a collection of alternative subsets
  /// @return [true] if [altsets] contains a [BitSet] with
  /// {@link BitSet#cardinality cardinality} &gt; 1, otherwise [false]
  static bool hasConflictingAltSet(List<BitSet> altsets) {
    for (var alts in altsets) {
      if (alts.cardinality > 1) {
        return true;
      }
    }
    return false;
  }

  /// Determines if every alternative subset in [altsets] is equivalent.
  ///
  /// @param altsets a collection of alternative subsets
  /// @return [true] if every member of [altsets] is equal to the
  /// others, otherwise [false]
  static bool allSubsetsEqual(List<BitSet> altsets) {
    final first = altsets.first;
    return altsets.every((e) => e == first);
  }

  /// Returns the unique alternative predicted by all alternative subsets in
  /// [altsets]. If no such alternative exists, this method returns
  /// {@link ATN#INVALID_ALT_NUMBER}.
  ///
  /// @param altsets a collection of alternative subsets
  static int getUniqueAlt(List<BitSet> altsets) {
    final all = getAlts(altsets);
    if (all.cardinality == 1) return all.nextset(0);
    return ATN.INVALID_ALT_NUMBER;
  }

  /// Gets the complete set of represented alternatives for a collection of
  /// alternative subsets. This method returns the union of each [BitSet]
  /// in [altsets].
  ///
  /// @param altsets a collection of alternative subsets
  /// @return the set of represented alternatives in [altsets]
  static BitSet getAlts(List<BitSet> altsets) {
    final all = BitSet();
    for (var alts in altsets) {
      all.or(alts);
    }
    return all;
  }

  /// Get union of all alts from configs.
  ///
  /// @since 4.5.1
  static BitSet getAltsFromConfigs(ATNConfigSet configs) {
    final alts = BitSet();
    for (var config in configs) {
      alts.set(config.alt);
    }
    return alts;
  }

  /// This function gets the conflicting alt subsets from a configuration set.
  /// For each configuration [c] in [configs]:
  ///
  /// <pre>
  /// map[c] U= c.{@link ATNConfig#alt alt} # map hash/equals uses s and x, not
  /// alt and not pred
  /// </pre>
  static List<BitSet> getConflictingAltSubsets(ATNConfigSet configs) {
    final configToAlts =
        HashMap<ATNConfig, BitSet>(equals: (ATNConfig? a, ATNConfig? b) {
      if (identical(a, b)) return true;
      if (a == null || b == null) return false;
      return a.state.stateNumber == b.state.stateNumber &&
          a.context == b.context;
    }, hashCode: (ATNConfig o) {
      /**
       * The hash code is only a function of the {@link ATNState#stateNumber}
       * and {@link ATNConfig#context}.
       */
      var hashCode = MurmurHash.initialize(7);
      hashCode = MurmurHash.update(hashCode, o.state.stateNumber);
      hashCode = MurmurHash.update(hashCode, o.context);
      hashCode = MurmurHash.finish(hashCode, 2);
      return hashCode;
    });
    for (var c in configs) {
      var alts = configToAlts[c];
      if (alts == null) {
        alts = BitSet();
        configToAlts[c] = alts;
      }
      alts.set(c.alt);
    }
    return configToAlts.values.toList();
  }

  /// Get a map from state to alt subset from a configuration set. For each
  /// configuration [c] in [configs]:
  ///
  /// <pre>
  /// map[c.{@link ATNConfig#state state}] U= c.{@link ATNConfig#alt alt}
  /// </pre>
  static Map<ATNState, BitSet> getStateToAltMap(ATNConfigSet configs) {
    final m = <ATNState, BitSet>{};
    for (var c in configs) {
      var alts = m[c.state];
      if (alts == null) {
        alts = BitSet();
        m[c.state] = alts;
      }
      alts.set(c.alt);
    }
    return m;
  }

  static bool hasStateAssociatedWithOneAlt(ATNConfigSet configs) {
    final x = getStateToAltMap(configs);
    for (var alts in x.values) {
      if (alts.cardinality == 1) return true;
    }
    return false;
  }

  static int getSingleViableAlt(List<BitSet> altsets) {
    final viableAlts = BitSet();
    for (var alts in altsets) {
      final minAlt = alts.nextset(0);
      viableAlts.set(minAlt);
      if (viableAlts.cardinality > 1) {
        // more than 1 viable alt
        return ATN.INVALID_ALT_NUMBER;
      }
    }
    return viableAlts.nextset(0);
  }
}
