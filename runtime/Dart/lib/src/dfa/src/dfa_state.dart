/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../atn/atn.dart';
import '../../util/murmur_hash.dart';
import '../../util/utils.dart';

/// Map a predicate to a predicted alternative. */
class PredPrediction {
  final SemanticContext pred;
  final int alt;

  PredPrediction(this.pred, this.alt);

  @override
  String toString() {
    return '($pred, $alt)';
  }
}

/// A DFA state represents a set of possible ATN configurations.
///  As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
///  to keep track of all possible states the ATN can be in after
///  reading each input symbol.  That is to say, after reading
///  input a1a2..an, the DFA is in a state that represents the
///  subset T of the states of the ATN that are reachable from the
///  ATN's start state along some path labeled a1a2..an."
///  In conventional NFA&rarr;DFA conversion, therefore, the subset T
///  would be a bitset representing the set of states the
///  ATN could be in.  We need to track the alt predicted by each
///  state as well, however.  More importantly, we need to maintain
///  a stack of states, tracking the closure operations as they
///  jump from rule to rule, emulating rule invocations (method calls).
///  I have to add a stack to simulate the proper lookahead sequences for
///  the underlying LL grammar from which the ATN was derived.
///
///  <p>I use a set of ATNConfig objects not simple states.  An ATNConfig
///  is both a state (ala normal conversion) and a RuleContext describing
///  the chain of rules (if any) followed to arrive at that state.</p>
///
///  <p>A DFA state may have multiple references to a particular state,
///  but with different ATN contexts (with same or different alts)
///  meaning that state was reached via a different set of rule invocations.</p>
class DFAState {
  int stateNumber;

  ATNConfigSet configs = ATNConfigSet();

  /// {@code edges[symbol]} points to target of symbol. Shift up by 1 so (-1)
  ///  {@link Token#EOF} maps to {@code edges[0]}.

  List<DFAState?>? edges;

  bool isAcceptState = false;

  /// if accept state, what ttype do we match or alt do we predict?
  ///  This is set to {@link ATN#INVALID_ALT_NUMBER} when {@link #predicates}{@code !=null} or
  ///  {@link #requiresFullContext}.
  int prediction = 0;

  LexerActionExecutor? lexerActionExecutor;

  /// Indicates that this state was created during SLL prediction that
  /// discovered a conflict between the configurations in the state. Future
  /// {@link ParserATNSimulator#execATN} invocations immediately jumped doing
  /// full context prediction if this field is true.
  bool requiresFullContext = false;

  /// During SLL parsing, this is a list of predicates associated with the
  ///  ATN configurations of the DFA state. When we have predicates,
  ///  {@link #requiresFullContext} is [false] since full context prediction evaluates predicates
  ///  on-the-fly. If this is not null, then {@link #prediction} is
  ///  {@link ATN#INVALID_ALT_NUMBER}.
  ///
  ///  <p>We only use these for non-{@link #requiresFullContext} but conflicting states. That
  ///  means we know from the context (it's $ or we don't dip into outer
  ///  context) that it's an ambiguity not a conflict.</p>
  ///
  ///  <p>This list is computed by {@link ParserATNSimulator#predicateDFAState}.</p>

  List<PredPrediction>? predicates;

  DFAState({this.stateNumber = -1, required this.configs});

  /// Get the set of all alts mentioned by all ATN configurations in this
  ///  DFA state.
  Set<int>? get altSet {
    final alts = <int>{};

    for (var c in configs) {
      alts.add(c.alt);
    }

    if (alts.isEmpty) return null;
    return alts;
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize(7);
    hash = MurmurHash.update(hash, configs.hashCode);
    hash = MurmurHash.finish(hash, 1);
    return hash;
  }

  /// Two [DFAState] instances are equal if their ATN configuration sets
  /// are the same. This method is used to see if a state already exists.
  ///
  /// <p>Because the number of alternatives and number of ATN configurations are
  /// finite, there is a finite number of DFA states that can be processed.
  /// This is necessary to show that the algorithm terminates.</p>
  ///
  /// <p>Cannot test the DFA state numbers here because in
  /// {@link ParserATNSimulator#addDFAState} we need to know if any other state
  /// exists that has this exact set of ATN configurations. The
  /// {@link #stateNumber} is irrelevant.</p>

  @override
  bool operator ==(Object other) {
    // compare set of ATN configurations in this set with other
    if (identical(this, other)) return true;

    if (other is! DFAState) {
      return false;
    }

    return configs == other.configs;
  }

  @override
  String toString() {
    final buf = StringBuffer();
    buf.write('$stateNumber:$configs');
    if (isAcceptState) {
      buf.write('=>');
      if (predicates != null) {
        buf.write(arrayToString(predicates));
      } else {
        buf.write(prediction);
      }
    }
    return buf.toString();
  }
}
