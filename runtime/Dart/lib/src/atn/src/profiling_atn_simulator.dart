/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:math';

import '../../dfa/dfa.dart';
import '../../parser.dart';
import '../../parser_rule_context.dart';
import '../../token_stream.dart';
import '../../util/bit_set.dart';
import 'atn_config_set.dart';
import 'atn_simulator.dart';
import 'info.dart';
import 'parser_atn_simulator.dart';
import 'semantic_context.dart';

class ProfilingATNSimulator extends ParserATNSimulator {
  late List<DecisionInfo> decisions;
  late int numDecisions;

  late int _sllStopIndex;
  late int _llStopIndex;

  late int currentDecision;
  DFAState? currentState;

  /// At the point of LL failover, we record how SLL would resolve the conflict so that
  ///  we can determine whether or not a decision / input pair is context-sensitive.
  ///  If LL gives a different result than SLL's predicted alternative, we have a
  ///  context sensitivity for sure. The converse is not necessarily true, however.
  ///  It's possible that after conflict resolution chooses minimum alternatives,
  ///  SLL could get the same answer as LL. Regardless of whether or not the result indicates
  ///  an ambiguity, it is not treated as a context sensitivity because LL prediction
  ///  was not required in order to produce a correct prediction for this decision and input sequence.
  ///  It may in fact still be a context sensitivity but we don't know by looking at the
  ///  minimum alternatives for the current input.
  int? conflictingAltResolvedBySLL;

  ProfilingATNSimulator(Parser parser)
      : super(
          parser,
          parser.interpreter!.atn,
          parser.interpreter!.decisionToDFA,
          parser.interpreter!.sharedContextCache,
        ) {
    numDecisions = atn.decisionToState.length;
    decisions = List<DecisionInfo>.generate(
      numDecisions,
      (index) => DecisionInfo(index),
    );
  }

  @override
  int adaptivePredict(
    TokenStream input,
    int decision,
    ParserRuleContext? outerContext,
  ) {
    try {
      _sllStopIndex = -1;
      _llStopIndex = -1;
      currentDecision = decision;

      final start =
          DateTime.now(); // TODO get nano seconds expensive but useful info
      final alt = super.adaptivePredict(input, decision, outerContext);
      final stop = DateTime.now();
      decisions[decision].timeInPrediction +=
          (stop.difference(start)).inMicroseconds;
      decisions[decision].invocations++;

      final SLL_k = _sllStopIndex - startIndex + 1;
      decisions[decision].SLL_TotalLook += SLL_k;
      decisions[decision].SLL_MinLook = decisions[decision].SLL_MinLook == 0
          ? SLL_k
          : min(decisions[decision].SLL_MinLook, SLL_k);
      if (SLL_k > decisions[decision].SLL_MaxLook) {
        decisions[decision].SLL_MaxLook = SLL_k;
        decisions[decision].SLL_MaxLookEvent = LookaheadEventInfo(
          decision,
          null,
          alt,
          input,
          startIndex,
          _sllStopIndex,
          false,
        );
      }

      if (_llStopIndex >= 0) {
        final LL_k = _llStopIndex - startIndex + 1;
        decisions[decision].LL_TotalLook += LL_k;
        decisions[decision].LL_MinLook = decisions[decision].LL_MinLook == 0
            ? LL_k
            : min(decisions[decision].LL_MinLook, LL_k);
        if (LL_k > decisions[decision].LL_MaxLook) {
          decisions[decision].LL_MaxLook = LL_k;
          decisions[decision].LL_MaxLookEvent = LookaheadEventInfo(
              decision, null, alt, input, startIndex, _llStopIndex, true);
        }
      }

      return alt;
    } finally {
      currentDecision = -1;
    }
  }

  @override
  DFAState? getExistingTargetState(DFAState previousD, int t) {
    // this method is called after each time the input position advances
    // during SLL prediction
    _sllStopIndex = input.index;

    final existingTargetState = super.getExistingTargetState(previousD, t);
    if (existingTargetState != null) {
      // count only if we transition over a DFA state
      decisions[currentDecision].SLL_DFATransitions += 1;
      if (existingTargetState == ATNSimulator.ERROR) {
        decisions[currentDecision].errors.add(
              ErrorInfo(
                currentDecision,
                previousD.configs,
                input,
                startIndex,
                _sllStopIndex,
                false,
              ),
            );
      }
    }

    currentState = existingTargetState;
    return existingTargetState;
  }

  @override
  DFAState? computeTargetState(DFA dfa, DFAState previousD, int t) {
    final state = super.computeTargetState(dfa, previousD, t);
    currentState = state;
    return state;
  }

  @override
  ATNConfigSet? computeReachSet(ATNConfigSet closure, int t, bool fullCtx) {
    if (fullCtx) {
      // this method is called after each time the input position advances
      // during full context prediction
      _llStopIndex = input.index;
    }

    final reachConfigs = super.computeReachSet(closure, t, fullCtx);
    if (fullCtx) {
      // count computation even if error
      decisions[currentDecision].LL_ATNTransitions += 1;
      if (reachConfigs != null) {
      } else {
        // no reach on current lookahead symbol. ERROR.
        // TODO: does not handle delayed errors per getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule()
        decisions[currentDecision].errors.add(
              ErrorInfo(
                currentDecision,
                closure,
                input,
                startIndex,
                _llStopIndex,
                true,
              ),
            );
      }
    } else {
      decisions[currentDecision].SLL_ATNTransitions += 1;
      if (reachConfigs != null) {
      } else {
        // no reach on current lookahead symbol. ERROR.
        decisions[currentDecision].errors.add(ErrorInfo(
              currentDecision,
              closure,
              input,
              startIndex,
              _sllStopIndex,
              false,
            ));
      }
    }
    return reachConfigs;
  }

  @override
  bool evalSemanticContextOne(
    SemanticContext pred,
    ParserRuleContext? parserCallStack,
    int alt,
    bool fullCtx,
  ) {
    final result = super.evalSemanticContextOne(
      pred,
      parserCallStack,
      alt,
      fullCtx,
    );
    if (pred is! PrecedencePredicate) {
      final fullContext = _llStopIndex >= 0;
      final stopIndex = fullContext ? _llStopIndex : _sllStopIndex;
      decisions[currentDecision].predicateEvals.add(PredicateEvalInfo(
          currentDecision,
          input,
          startIndex,
          stopIndex,
          pred,
          result,
          alt,
          fullCtx));
    }

    return result;
  }

  @override
  void reportAttemptingFullContext(
    DFA dfa,
    BitSet? conflictingAlts,
    ATNConfigSet configs,
    int startIndex,
    int stopIndex,
  ) {
    if (conflictingAlts != null) {
      conflictingAltResolvedBySLL = conflictingAlts.nextset(0);
    } else {
      conflictingAltResolvedBySLL = configs.alts.nextset(0);
    }

    decisions[currentDecision].LL_Fallback += 1;

    super.reportAttemptingFullContext(
      dfa,
      conflictingAlts,
      configs,
      startIndex,
      stopIndex,
    );
  }

  @override
  void reportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs,
      int startIndex, int stopIndex) {
    if (prediction != conflictingAltResolvedBySLL) {
      decisions[currentDecision].contextSensitivities.add(
          ContextSensitivityInfo(
              currentDecision, configs, input, startIndex, stopIndex));
    }
    super.reportContextSensitivity(
        dfa, prediction, configs, startIndex, stopIndex);
  }

  @override
  void reportAmbiguity(
    DFA dfa,
    DFAState D,
    int startIndex,
    int stopIndex,
    bool exact,
    BitSet? ambigAlts,
    ATNConfigSet configs,
  ) {
    final prediction =
        ambigAlts != null ? ambigAlts.nextset(0) : configs.alts.nextset(0);
    if (configs.fullCtx && prediction != conflictingAltResolvedBySLL) {
      // Even though this is an ambiguity we are reporting, we can
      // still detect some context sensitivities.  Both SLL and LL
      // are showing a conflict, hence an ambiguity, but if they resolve
      // to different minimum alternatives we have also identified a
      // context sensitivity.
      decisions[currentDecision].contextSensitivities.add(
            ContextSensitivityInfo(
              currentDecision,
              configs,
              input,
              startIndex,
              stopIndex,
            ),
          );
    }
    decisions[currentDecision].ambiguities.add(
          AmbiguityInfo(
            currentDecision,
            configs,
            ambigAlts,
            input,
            startIndex,
            stopIndex,
            configs.fullCtx,
          ),
        );
    super.reportAmbiguity(
      dfa,
      D,
      startIndex,
      stopIndex,
      exact,
      ambigAlts,
      configs,
    );
  }

  // ---------------------------------------------------------------------

  List<DecisionInfo> get decisionInfo {
    return decisions;
  }
}
