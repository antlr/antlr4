/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../vocabulary.dart';
import '../../atn/atn.dart';
import 'dfa_serializer.dart';
import 'dfa_state.dart';

class DFA {
  /// A set of all DFA states. Use [Map] so we can get old state back
  ///  ([Set] only allows you to see if it's there).

  Map<DFAState, DFAState> states = {};

  DFAState? s0;

  final int? decision;

  /// From which ATN state did we create this DFA? */

  DecisionState? atnStartState;

  /// [true] if this DFA is for a precedence decision; otherwise,
  /// [false]. This is the backing field for [isPrecedenceDfa].
  late bool precedenceDfa;

  DFA(this.atnStartState, [this.decision]) {
    var precedenceDfa = false;
    if (atnStartState is StarLoopEntryState) {
      if ((atnStartState as StarLoopEntryState).isPrecedenceDecision) {
        precedenceDfa = true;
        final precedenceState = DFAState(configs: ATNConfigSet());
        precedenceState.edges = [];
        precedenceState.isAcceptState = false;
        precedenceState.requiresFullContext = false;
        s0 = precedenceState;
      }
    }

    this.precedenceDfa = precedenceDfa;
  }

  /// Gets whether this DFA is a precedence DFA. Precedence DFAs use a special
  /// start state {@link #s0} which is not stored in [states]. The
  /// [DFAState.edges] array for this start state contains outgoing edges
  /// supplying individual start states corresponding to specific precedence
  /// values.
  ///
  /// @return [true] if this is a precedence DFA; otherwise,
  /// [false].
  /// @see Parser#getPrecedence()
  bool isPrecedenceDfa() {
    return precedenceDfa;
  }

  /// Get the start state for a specific precedence value.
  ///
  /// @param precedence The current precedence.
  /// @return The start state corresponding to the specified precedence, or
  /// null if no start state exists for the specified precedence.
  ///
  /// @throws IllegalStateException if this is not a precedence DFA.
  /// @see #isPrecedenceDfa()
  DFAState? getPrecedenceStartState(int precedence) {
    if (!isPrecedenceDfa()) {
      throw StateError(
          'Only precedence DFAs may contain a precedence start state.');
    }

    // s0.edges is never null for a precedence DFA
    if (precedence < 0 || precedence >= s0!.edges!.length) {
      return null;
    }

    return s0!.edges![precedence];
  }

  /// Set the start state for a specific precedence value.
  ///
  /// @param precedence The current precedence.
  /// @param startState The start state corresponding to the specified
  /// precedence.
  ///
  /// @throws IllegalStateException if this is not a precedence DFA.
  /// @see #isPrecedenceDfa()
  void setPrecedenceStartState(int precedence, DFAState startState) {
    if (!isPrecedenceDfa()) {
      throw StateError(
          'Only precedence DFAs may contain a precedence start state.');
    }

    if (precedence < 0) {
      return;
    }

    // synchronization on s0 here is ok. when the DFA is turned into a
    // precedence DFA, s0 will be initialized once and not updated again
    // s0.edges is never null for a precedence DFA
    if (precedence >= s0!.edges!.length) {
      final original = s0!.edges!;
      s0!.edges = List.filled(precedence + 1, null);
      List.copyRange(s0!.edges!, 0, original);
    }

    s0!.edges![precedence] = startState;
  }

  /// Return a list of all states in this DFA, ordered by state number.

  List<DFAState> getStates() {
    final result = states.keys.toList();
    result.sort((DFAState o1, DFAState o2) {
      return o1.stateNumber - o2.stateNumber;
    });

    return result;
  }

  @override
  String toString([Vocabulary? vocabulary]) {
    vocabulary = vocabulary ?? VocabularyImpl.EMPTY_VOCABULARY;
    if (s0 == null) {
      return '';
    }

    final serializer = DFASerializer(this, vocabulary);
    return serializer.toString();
  }

  String toLexerString() {
    if (s0 == null) return '';
    DFASerializer serializer = LexerDFASerializer(this);
    return serializer.toString();
  }
}
