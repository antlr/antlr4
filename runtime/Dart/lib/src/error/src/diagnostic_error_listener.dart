/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../atn/atn.dart';
import '../../dfa/dfa.dart';
import '../../interval_set.dart';
import '../../parser.dart';
import '../../util/bit_set.dart';
import 'error_listener.dart';

/// This implementation of [ANTLRErrorListener] can be used to identify
/// certain potential correctness and performance problems in grammars. "Reports"
/// are made by calling {@link Parser#notifyErrorListeners} with the appropriate
/// message.
///
/// <ul>
/// <li><b>Ambiguities</b>: These are cases where more than one path through the
/// grammar can match the input.</li>
/// <li><b>Weak context sensitivity</b>: These are cases where full-context
/// prediction resolved an SLL conflict to a unique alternative which equaled the
/// minimum alternative of the SLL conflict.</li>
/// <li><b>Strong (forced) context sensitivity</b>: These are cases where the
/// full-context prediction resolved an SLL conflict to a unique alternative,
/// <em>and</em> the minimum alternative of the SLL conflict was found to not be
/// a truly viable alternative. Two-stage parsing cannot be used for inputs where
/// this situation occurs.</li>
/// </ul>
class DiagnosticErrorListener extends BaseErrorListener {
  /// When [true], only exactly known ambiguities are reported.
  final bool exactOnly;

  /// Initializes a new instance of [DiagnosticErrorListener], specifying
  /// whether all ambiguities or only exact ambiguities are reported.
  ///
  /// @param exactOnly [true] to report only exact ambiguities, otherwise
  /// [false] to report all ambiguities.
  DiagnosticErrorListener([this.exactOnly = true]);

  @override
  void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
      int stopIndex, bool exact, BitSet? ambigAlts, ATNConfigSet configs) {
    if (exactOnly && !exact) {
      return;
    }

    final decision = getDecisionDescription(recognizer, dfa);
    final conflictingAlts = getConflictingAlts(ambigAlts, configs);
    final text =
        recognizer.tokenStream.getText(Interval.of(startIndex, stopIndex));
    final message =
        "reportAmbiguity d=$decision: ambigAlts=$conflictingAlts, input='$text'";
    recognizer.notifyErrorListeners(message);
  }

  @override
  void reportAttemptingFullContext(
    Parser recognizer,
    DFA dfa,
    int startIndex,
    int stopIndex,
    BitSet? conflictingAlts,
    ATNConfigSet configs,
  ) {
    final decision = getDecisionDescription(recognizer, dfa);
    final text = recognizer.tokenStream.getText(
      Interval.of(startIndex, stopIndex),
    );
    final message = "reportAttemptingFullContext d=$decision, input='$text'";
    recognizer.notifyErrorListeners(message);
  }

  @override
  void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex,
      int stopIndex, int prediction, ATNConfigSet configs) {
    final decision = getDecisionDescription(recognizer, dfa);
    final text =
        recognizer.tokenStream.getText(Interval.of(startIndex, stopIndex));
    final message = "reportContextSensitivity d=$decision, input='$text'";
    recognizer.notifyErrorListeners(message);
  }

  String getDecisionDescription(Parser recognizer, DFA dfa) {
    final decision = dfa.decision;
    final ruleIndex = dfa.atnStartState?.ruleIndex;

    final ruleNames = recognizer.ruleNames;
    if (ruleIndex == null || ruleIndex < 0 || ruleIndex >= ruleNames.length) {
      return decision.toString();
    }

    final ruleName = ruleNames[ruleIndex];
    if (ruleName.isEmpty) {
      return decision.toString();
    }

    return '$decision ($ruleName)';
  }

  /// Computes the set of conflicting or ambiguous alternatives from a
  /// configuration set, if that information was not already provided by the
  /// parser.
  ///
  /// @param reportedAlts The set of conflicting or ambiguous alternatives, as
  /// reported by the parser.
  /// @param configs The conflicting or ambiguous configuration set.
  /// @return Returns [reportedAlts] if it is not null, otherwise
  /// returns the set of alternatives represented in [configs].
  BitSet getConflictingAlts(BitSet? reportedAlts, ATNConfigSet configs) {
    if (reportedAlts != null) {
      return reportedAlts;
    }

    final result = BitSet();
    for (var config in configs) {
      result.set(config.alt);
    }

    return result;
  }
}
