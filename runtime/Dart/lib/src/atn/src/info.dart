/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../token_stream.dart';
import '../../util/bit_set.dart';
import 'atn_config_set.dart';
import 'profiling_atn_simulator.dart';
import 'semantic_context.dart';

/// This class represents profiling event information for a context sensitivity.
/// Context sensitivities are decisions where a particular input resulted in an
/// SLL conflict, but LL prediction produced a single unique alternative.
///
/// <p>
/// In some cases, the unique alternative identified by LL prediction is not
/// equal to the minimum represented alternative in the conflicting SLL
/// configuration set. Grammars and inputs which result in this scenario are
/// unable to use {@link PredictionMode#SLL}, which in turn means they cannot use
/// the two-stage parsing strategy to improve parsing performance for that
/// input.</p>
///
/// @see ParserATNSimulator#reportContextSensitivity
/// @see ANTLRErrorListener#reportContextSensitivity
///
/// @since 4.3
class ContextSensitivityInfo extends DecisionEventInfo {
  /// Constructs a new instance of the [ContextSensitivityInfo] class
  /// with the specified detailed context sensitivity information.
  ///
  /// @param decision The decision number
  /// @param configs The final configuration set containing the unique
  /// alternative identified by full-context prediction
  /// @param input The input token stream
  /// @param startIndex The start index for the current prediction
  /// @param stopIndex The index at which the context sensitivity was
  /// identified during full-context prediction
  ContextSensitivityInfo(int decision, ATNConfigSet configs, TokenStream input,
      int startIndex, int stopIndex)
      : super(decision, configs, input, startIndex, stopIndex, true);
}

/// This is the base class for gathering detailed information about prediction
/// events which occur during parsing.
///
/// Note that we could record the parser call stack at the time this event
/// occurred but in the presence of left recursive rules, the stack is kind of
/// meaningless. It's better to look at the individual configurations for their
/// individual stacks. Of course that is a [PredictionContext] object
/// not a parse tree node and so it does not have information about the extent
/// (start...stop) of the various subtrees. Examining the stack tops of all
/// configurations provide the return states for the rule invocations.
/// From there you can get the enclosing rule.
///
/// @since 4.3
class DecisionEventInfo {
  /// The invoked decision number which this event is related to.
  ///
  /// @see ATN#decisionToState
  final int decision;

  /// The configuration set containing additional information relevant to the
  /// prediction state when the current event occurred, or null if no
  /// additional information is relevant or available.
  final ATNConfigSet? configs;

  /// The input token stream which is being parsed.
  final TokenStream input;

  /// The token index in the input stream at which the current prediction was
  /// originally invoked.
  final int startIndex;

  /// The token index in the input stream at which the current event occurred.
  final int stopIndex;

  /// [true] if the current event occurred during LL prediction;
  /// otherwise, [false] if the input occurred during SLL prediction.
  final bool fullCtx;

  DecisionEventInfo(
    this.decision,
    this.configs,
    this.input,
    this.startIndex,
    this.stopIndex,
    this.fullCtx,
  );
}

/// This class contains profiling gathered for a particular decision.
///
/// <p>
/// Parsing performance in ANTLR 4 is heavily influenced by both static factors
/// (e.g. the form of the rules in the grammar) and dynamic factors (e.g. the
/// choice of input and the state of the DFA cache at the time profiling
/// operations are started). For best results, gather and use aggregate
/// statistics from a large sample of inputs representing the inputs expected in
/// production before using the results to make changes in the grammar.</p>
///
/// @since 4.3
class DecisionInfo {
  /// The decision number, which is an index into {@link ATN#decisionToState}.
  final int decision;

  /// The total number of times {@link ParserATNSimulator#adaptivePredict} was
  /// invoked for this decision.
  int invocations = 0;

  /// The total time spent in {@link ParserATNSimulator#adaptivePredict} for
  /// this decision, in nanoseconds.
  ///
  /// <p>
  /// The value of this field contains the sum of differential results obtained
  /// by {@link System#nanoTime()}, and is not adjusted to compensate for JIT
  /// and/or garbage collection overhead. For best accuracy, use a modern JVM
  /// implementation that provides precise results from
  /// {@link System#nanoTime()}, and perform profiling in a separate process
  /// which is warmed up by parsing the input prior to profiling. If desired,
  /// call {@link ATNSimulator#clearDFA} to reset the DFA cache to its initial
  /// state before starting the profiling measurement pass.</p>
  int timeInPrediction = 0;

  /// The sum of the lookahead required for SLL prediction for this decision.
  /// Note that SLL prediction is used before LL prediction for performance
  /// reasons even when {@link PredictionMode#LL} or
  /// {@link PredictionMode#LL_EXACT_AMBIG_DETECTION} is used.
  int SLL_TotalLook = 0;

  /// Gets the minimum lookahead required for any single SLL prediction to
  /// complete for this decision, by reaching a unique prediction, reaching an
  /// SLL conflict state, or encountering a syntax error.
  int SLL_MinLook = 0;

  /// Gets the maximum lookahead required for any single SLL prediction to
  /// complete for this decision, by reaching a unique prediction, reaching an
  /// SLL conflict state, or encountering a syntax error.
  int SLL_MaxLook = 0;

  /// Gets the [LookaheadEventInfo] associated with the event where the
  /// {@link #SLL_MaxLook} value was set.
  LookaheadEventInfo? SLL_MaxLookEvent;

  /// The sum of the lookahead required for LL prediction for this decision.
  /// Note that LL prediction is only used when SLL prediction reaches a
  /// conflict state.
  int LL_TotalLook = 0;

  /// Gets the minimum lookahead required for any single LL prediction to
  /// complete for this decision. An LL prediction completes when the algorithm
  /// reaches a unique prediction, a conflict state (for
  /// {@link PredictionMode#LL}, an ambiguity state (for
  /// {@link PredictionMode#LL_EXACT_AMBIG_DETECTION}, or a syntax error.
  int LL_MinLook = 0;

  /// Gets the maximum lookahead required for any single LL prediction to
  /// complete for this decision. An LL prediction completes when the algorithm
  /// reaches a unique prediction, a conflict state (for
  /// {@link PredictionMode#LL}, an ambiguity state (for
  /// {@link PredictionMode#LL_EXACT_AMBIG_DETECTION}, or a syntax error.
  int LL_MaxLook = 0;

  /// Gets the [LookaheadEventInfo] associated with the event where the
  /// {@link #LL_MaxLook} value was set.
  LookaheadEventInfo? LL_MaxLookEvent;

  /// A collection of [ContextSensitivityInfo] instances describing the
  /// context sensitivities encountered during LL prediction for this decision.
  ///
  /// @see ContextSensitivityInfo
  final List<ContextSensitivityInfo> contextSensitivities = [];

  /// A collection of [ErrorInfo] instances describing the parse errors
  /// identified during calls to {@link ParserATNSimulator#adaptivePredict} for
  /// this decision.
  ///
  /// @see ErrorInfo
  final List<ErrorInfo> errors = [];

  /// A collection of [AmbiguityInfo] instances describing the
  /// ambiguities encountered during LL prediction for this decision.
  ///
  /// @see AmbiguityInfo
  final List<AmbiguityInfo> ambiguities = [];

  /// A collection of [PredicateEvalInfo] instances describing the
  /// results of evaluating individual predicates during prediction for this
  /// decision.
  ///
  /// @see PredicateEvalInfo
  final List<PredicateEvalInfo> predicateEvals = [];

  /// The total number of ATN transitions required during SLL prediction for
  /// this decision. An ATN transition is determined by the number of times the
  /// DFA does not contain an edge that is required for prediction, resulting
  /// in on-the-fly computation of that edge.
  ///
  /// <p>
  /// If DFA caching of SLL transitions is employed by the implementation, ATN
  /// computation may cache the computed edge for efficient lookup during
  /// future parsing of this decision. Otherwise, the SLL parsing algorithm
  /// will use ATN transitions exclusively.</p>
  ///
  /// @see #SLL_ATNTransitions
  /// @see ParserATNSimulator#computeTargetState
  /// @see LexerATNSimulator#computeTargetState
  int SLL_ATNTransitions = 0;

  /// The total number of DFA transitions required during SLL prediction for
  /// this decision.
  ///
  /// <p>If the ATN simulator implementation does not use DFA caching for SLL
  /// transitions, this value will be 0.</p>
  ///
  /// @see ParserATNSimulator#getExistingTargetState
  /// @see LexerATNSimulator#getExistingTargetState
  int SLL_DFATransitions = 0;

  /// Gets the total number of times SLL prediction completed in a conflict
  /// state, resulting in fallback to LL prediction.
  ///
  /// <p>Note that this value is not related to whether or not
  /// {@link PredictionMode#SLL} may be used successfully with a particular
  /// grammar. If the ambiguity resolution algorithm applied to the SLL
  /// conflicts for this decision produce the same result as LL prediction for
  /// this decision, {@link PredictionMode#SLL} would produce the same overall
  /// parsing result as {@link PredictionMode#LL}.</p>
  int LL_Fallback = 0;

  /// The total number of ATN transitions required during LL prediction for
  /// this decision. An ATN transition is determined by the number of times the
  /// DFA does not contain an edge that is required for prediction, resulting
  /// in on-the-fly computation of that edge.
  ///
  /// <p>
  /// If DFA caching of LL transitions is employed by the implementation, ATN
  /// computation may cache the computed edge for efficient lookup during
  /// future parsing of this decision. Otherwise, the LL parsing algorithm will
  /// use ATN transitions exclusively.</p>
  ///
  /// @see #LL_DFATransitions
  /// @see ParserATNSimulator#computeTargetState
  /// @see LexerATNSimulator#computeTargetState
  int LL_ATNTransitions = 0;

  /// The total number of DFA transitions required during LL prediction for
  /// this decision.
  ///
  /// <p>If the ATN simulator implementation does not use DFA caching for LL
  /// transitions, this value will be 0.</p>
  ///
  /// @see ParserATNSimulator#getExistingTargetState
  /// @see LexerATNSimulator#getExistingTargetState
  int LL_DFATransitions = 0;

  /// Constructs a new instance of the [DecisionInfo] class to contain
  /// statistics for a particular decision.
  ///
  /// @param decision The decision number
  DecisionInfo(this.decision);

  @override
  String toString() {
    return '{'
        'decision=$decision'
        ', contextSensitivities=${contextSensitivities.length}'
        ', errors=${errors.length}'
        ', ambiguities=${ambiguities.length}'
        ', SLL_lookahead=$SLL_TotalLook'
        ', SLL_ATNTransitions=$SLL_ATNTransitions, SLL_DFATransitions=$SLL_DFATransitions, LL_Fallback=$LL_Fallback, LL_lookahead=$LL_TotalLook, LL_ATNTransitions=$LL_ATNTransitions}';
  }
}

/// This class represents profiling event information for an ambiguity.
/// Ambiguities are decisions where a particular input resulted in an SLL
/// conflict, followed by LL prediction also reaching a conflict state
/// (indicating a true ambiguity in the grammar).
///
/// <p>
/// This event may be reported during SLL prediction in cases where the
/// conflicting SLL configuration set provides sufficient information to
/// determine that the SLL conflict is truly an ambiguity. For example, if none
/// of the ATN configurations in the conflicting SLL configuration set have
/// traversed a global follow transition (i.e.
/// {@link ATNConfig#reachesIntoOuterContext} is 0 for all configurations), then
/// the result of SLL prediction for that input is known to be equivalent to the
/// result of LL prediction for that input.</p>
///
/// <p>
/// In some cases, the minimum represented alternative in the conflicting LL
/// configuration set is not equal to the minimum represented alternative in the
/// conflicting SLL configuration set. Grammars and inputs which result in this
/// scenario are unable to use {@link PredictionMode#SLL}, which in turn means
/// they cannot use the two-stage parsing strategy to improve parsing performance
/// for that input.</p>
///
/// @see ParserATNSimulator#reportAmbiguity
/// @see ANTLRErrorListener#reportAmbiguity
///
/// @since 4.3
class AmbiguityInfo extends DecisionEventInfo {
  /// The set of alternative numbers for this decision event that lead to a valid parse. */
  BitSet? ambigAlts;

  /// Constructs a new instance of the [AmbiguityInfo] class with the
  /// specified detailed ambiguity information.
  ///
  /// @param decision The decision number
  /// @param configs The final configuration set identifying the ambiguous
  /// alternatives for the current input
  /// @param ambigAlts The set of alternatives in the decision that lead to a valid parse.
  ///                  The predicted alt is the min(ambigAlts)
  /// @param input The input token stream
  /// @param startIndex The start index for the current prediction
  /// @param stopIndex The index at which the ambiguity was identified during
  /// prediction
  /// @param fullCtx [true] if the ambiguity was identified during LL
  /// prediction; otherwise, [false] if the ambiguity was identified
  /// during SLL prediction
  AmbiguityInfo(int decision, ATNConfigSet configs, this.ambigAlts,
      TokenStream input, int startIndex, int stopIndex, bool fullCtx)
      : super(decision, configs, input, startIndex, stopIndex, fullCtx);
}

/// This class represents profiling event information for a syntax error
/// identified during prediction. Syntax errors occur when the prediction
/// algorithm is unable to identify an alternative which would lead to a
/// successful parse.
///
/// @see Parser#notifyErrorListeners(Token, String, RecognitionException)
/// @see ANTLRErrorListener#syntaxError
///
/// @since 4.3
class ErrorInfo extends DecisionEventInfo {
  /// Constructs a new instance of the [ErrorInfo] class with the
  /// specified detailed syntax error information.
  ///
  /// @param decision The decision number
  /// @param configs The final configuration set reached during prediction
  /// prior to reaching the {@link ATNSimulator#ERROR} state
  /// @param input The input token stream
  /// @param startIndex The start index for the current prediction
  /// @param stopIndex The index at which the syntax error was identified
  /// @param fullCtx [true] if the syntax error was identified during LL
  /// prediction; otherwise, [false] if the syntax error was identified
  /// during SLL prediction
  ErrorInfo(
    int decision,
    ATNConfigSet? configs,
    TokenStream input,
    int startIndex,
    int stopIndex,
    bool fullCtx,
  ) : super(decision, configs, input, startIndex, stopIndex, fullCtx);
}

/// This class represents profiling event information for tracking the lookahead
/// depth required in order to make a prediction.
///
/// @since 4.3
class LookaheadEventInfo extends DecisionEventInfo {
  /// The alternative chosen by adaptivePredict(), not necessarily
  ///  the outermost alt shown for a rule; left-recursive rules have
  ///  user-level alts that differ from the rewritten rule with a (...) block
  ///  and a (..)* loop.
  int predictedAlt;

  /// Constructs a new instance of the [LookaheadEventInfo] class with
  /// the specified detailed lookahead information.
  ///
  /// @param decision The decision number
  /// @param configs The final configuration set containing the necessary
  /// information to determine the result of a prediction, or null if
  /// the final configuration set is not available
  /// @param input The input token stream
  /// @param startIndex The start index for the current prediction
  /// @param stopIndex The index at which the prediction was finally made
  /// @param fullCtx [true] if the current lookahead is part of an LL
  /// prediction; otherwise, [false] if the current lookahead is part of
  /// an SLL prediction
  LookaheadEventInfo(
    int decision,
    ATNConfigSet? configs,
    this.predictedAlt,
    TokenStream input,
    int startIndex,
    int stopIndex,
    bool fullCtx,
  ) : super(decision, configs, input, startIndex, stopIndex, fullCtx);
}

/// This class represents profiling event information for semantic predicate
/// evaluations which occur during prediction.
///
/// @see ParserATNSimulator#evalSemanticContext
///
/// @since 4.3
class PredicateEvalInfo extends DecisionEventInfo {
  /// The semantic context which was evaluated.
  final SemanticContext semctx;

  /// The alternative number for the decision which is guarded by the semantic
  /// context {@link #semctx}. Note that other ATN
  /// configurations may predict the same alternative which are guarded by
  /// other semantic contexts and/or {@link SemanticContext#NONE}.
  final int predictedAlt;

  /// The result of evaluating the semantic context {@link #semctx}.
  final bool evalResult;

  /// Constructs a new instance of the [PredicateEvalInfo] class with the
  /// specified detailed predicate evaluation information.
  ///
  /// @param decision The decision number
  /// @param input The input token stream
  /// @param startIndex The start index for the current prediction
  /// @param stopIndex The index at which the predicate evaluation was
  /// triggered. Note that the input stream may be reset to other positions for
  /// the actual evaluation of individual predicates.
  /// @param semctx The semantic context which was evaluated
  /// @param evalResult The results of evaluating the semantic context
  /// @param predictedAlt The alternative number for the decision which is
  /// guarded by the semantic context [semctx]. See {@link #predictedAlt}
  /// for more information.
  /// @param fullCtx [true] if the semantic context was
  /// evaluated during LL prediction; otherwise, [false] if the semantic
  /// context was evaluated during SLL prediction
  ///
  /// @see ParserATNSimulator#evalSemanticContext(SemanticContext, ParserRuleContext, int, boolean)
  /// @see SemanticContext#eval(Recognizer, RuleContext)
  PredicateEvalInfo(
      int decision,
      TokenStream input,
      int startIndex,
      int stopIndex,
      this.semctx,
      this.evalResult,
      this.predictedAlt,
      bool fullCtx)
      : super(decision, ATNConfigSet(), input, startIndex, stopIndex, fullCtx);
}

/// This class provides access to specific and aggregate statistics gathered
/// during profiling of a parser.
///
/// @since 4.3
class ParseInfo {
  final ProfilingATNSimulator atnSimulator;

  ParseInfo(this.atnSimulator);

  /// Gets an array of [DecisionInfo] instances containing the profiling
  /// information gathered for each decision in the ATN.
  ///
  /// @return An array of [DecisionInfo] instances, indexed by decision
  /// number.
  List<DecisionInfo> get decisionInfo {
    return atnSimulator.decisionInfo;
  }

  /// Gets the decision numbers for decisions that required one or more
  /// full-context predictions during parsing. These are decisions for which
  /// {@link DecisionInfo#LL_Fallback} is non-zero.
  ///
  /// @return A list of decision numbers which required one or more
  /// full-context predictions during parsing.
  List<int> get llDecisions {
    final decisions = atnSimulator.decisionInfo;
    final LL = <int>[];
    for (var i = 0; i < decisions.length; i++) {
      final fallBack = decisions[i].LL_Fallback;
      if (fallBack > 0) LL.add(i);
    }
    return LL;
  }

  /// Gets the total time spent during prediction across all decisions made
  /// during parsing. This value is the sum of
  /// {@link DecisionInfo#timeInPrediction} for all decisions.
  int get totalTimeInPrediction {
    final decisions = atnSimulator.decisionInfo;
    var t = 0;
    for (var i = 0; i < decisions.length; i++) {
      t += decisions[i].timeInPrediction;
    }
    return t;
  }

  /// Gets the total number of SLL lookahead operations across all decisions
  /// made during parsing. This value is the sum of
  /// {@link DecisionInfo#SLL_TotalLook} for all decisions.
  int get totalSLLLookaheadOps {
    final decisions = atnSimulator.decisionInfo;
    var k = 0;
    for (var i = 0; i < decisions.length; i++) {
      k += decisions[i].SLL_TotalLook;
    }
    return k;
  }

  /// Gets the total number of LL lookahead operations across all decisions
  /// made during parsing. This value is the sum of
  /// {@link DecisionInfo#LL_TotalLook} for all decisions.
  int get totalLLLookaheadOps {
    final decisions = atnSimulator.decisionInfo;
    var k = 0;
    for (var i = 0; i < decisions.length; i++) {
      k += decisions[i].LL_TotalLook;
    }
    return k;
  }

  /// Gets the total number of ATN lookahead operations for SLL prediction
  /// across all decisions made during parsing.
  int get totalSLLATNLookaheadOps {
    final decisions = atnSimulator.decisionInfo;
    var k = 0;
    for (var i = 0; i < decisions.length; i++) {
      k += decisions[i].SLL_ATNTransitions;
    }
    return k;
  }

  /// Gets the total number of ATN lookahead operations for LL prediction
  /// across all decisions made during parsing.
  int get totalLLATNLookaheadOps {
    final decisions = atnSimulator.decisionInfo;
    var k = 0;
    for (var i = 0; i < decisions.length; i++) {
      k += decisions[i].LL_ATNTransitions;
    }
    return k;
  }

  /// Gets the total number of ATN lookahead operations for SLL and LL
  /// prediction across all decisions made during parsing.
  ///
  /// <p>
  /// This value is the sum of {@link #getTotalSLLATNLookaheadOps} and
  /// {@link #getTotalLLATNLookaheadOps}.</p>
  int get totalATNLookaheadOps {
    final decisions = atnSimulator.decisionInfo;
    var k = 0;
    for (var i = 0; i < decisions.length; i++) {
      k += decisions[i].SLL_ATNTransitions;
      k += decisions[i].LL_ATNTransitions;
    }
    return k;
  }

  /// Gets the total number of DFA states stored in the DFA cache for all
  /// decisions in the ATN.
  int get dfaSize {
    var n = 0;
    final decisionToDFA = atnSimulator.decisionToDFA;
    for (var i = 0; i < decisionToDFA.length; i++) {
      n += getDFASizeAt(i);
    }
    return n;
  }

  /// Gets the total number of DFA states stored in the DFA cache for a
  /// particular decision.
  int getDFASizeAt(int decision) {
    final decisionToDFA = atnSimulator.decisionToDFA[decision];
    return decisionToDFA.states.length;
  }
}
