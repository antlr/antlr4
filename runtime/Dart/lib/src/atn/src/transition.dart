/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../interval_set.dart';
import '../../token.dart';
import 'atn_state.dart';
import 'semantic_context.dart';

enum TransitionType {
  INVALID, // 0 is not used
  EPSILON,
  RANGE,
  RULE,
  PREDICATE, // e.g., {isType(input.LT(1))}?
  ATOM,
  ACTION,
  SET, // ~(A|B) or ~atom, wildcard, which convert to next 2
  NOT_SET,
  WILDCARD,
  PRECEDENCE,
}

/// An ATN transition between any two ATN states.  Subclasses define
///  atom, set, epsilon, action, predicate, rule transitions.
///
///  <p>This is a one way link.  It emanates from a state (usually via a list of
///  transitions) and has a target state.</p>
///
///  <p>Since we never have to change the ATN transitions once we construct it,
///  we can fix these transitions as specific classes. The DFA transitions
///  on the other hand need to update the labels as it adds transitions to
///  the states. We'll use the term Edge for the DFA to distinguish them from
///  ATN transitions.</p>
abstract class Transition {
  /// The target of this transition. */
  ATNState target;

  Transition(this.target);

  TransitionType get type;

  /// Determines if the transition is an "epsilon" transition.
  ///
  /// <p>The default implementation returns [false].</p>
  ///
  /// @return [true] if traversing this transition in the ATN does not
  /// consume an input symbol; otherwise, [false] if traversing this
  /// transition consumes (matches) an input symbol.
  bool get isEpsilon => false;

  IntervalSet? get label => null;

  bool matches(int symbol, int minVocabSymbol, int maxVocabSymbol);
}

class EpsilonTransition extends Transition {
  /// @return the rule index of a precedence rule for which this transition is
  /// returning from, where the precedence value is 0; otherwise, -1.
  ///
  /// @see ATNConfig#isPrecedenceFilterSuppressed()
  /// @see ParserATNSimulator#applyPrecedenceFilter(ATNConfigSet)
  /// @since 4.4.1
  final int outermostPrecedenceReturn;

  EpsilonTransition(ATNState target, [this.outermostPrecedenceReturn = -1])
      : super(target);

  @override
  bool get isEpsilon => true;

  @override
  bool matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
    return false;
  }

  @override
  String toString() {
    return 'epsilon';
  }

  @override
  TransitionType get type => TransitionType.EPSILON;
}

class RangeTransition extends Transition {
  final int from;
  final int to;

  RangeTransition(ATNState target, this.from, this.to) : super(target);

  @override
  IntervalSet get label {
    return IntervalSet.ofRange(from, to);
  }

  @override
  bool matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
    return symbol >= from && symbol <= to;
  }

  @override
  String toString() {
    return "'$from..$to'";
  }

  @override
  TransitionType get type => TransitionType.RANGE;
}

class RuleTransition extends Transition {
  /// Ptr to the rule definition object for this rule ref */
  final int ruleIndex; // no Rule object at runtime

  final int precedence;

  /// What node to begin computations following ref to rule */
  ATNState followState;

  RuleTransition(
    RuleStartState ruleStart,
    this.ruleIndex,
    this.precedence,
    this.followState,
  ) : super(ruleStart);

  @override
  bool get isEpsilon => true;

  @override
  bool matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
    return false;
  }

  @override
  TransitionType get type => TransitionType.RULE;
}

abstract class AbstractPredicateTransition extends Transition {
  AbstractPredicateTransition(ATNState target) : super(target);
}

class PredicateTransition extends AbstractPredicateTransition {
  final int ruleIndex;
  final int predIndex;
  final bool isCtxDependent; // e.g., $i ref in pred

  PredicateTransition(
      target, this.ruleIndex, this.predIndex, this.isCtxDependent)
      : super(target);

  @override
  bool get isEpsilon => true;

  @override
  bool matches(symbol, minVocabSymbol, maxVocabSymbol) {
    return false;
  }

  Predicate get predicate => Predicate(ruleIndex, predIndex, isCtxDependent);

  @override
  String toString() {
    return 'pred_$ruleIndex:$predIndex';
  }

  @override
  TransitionType get type => TransitionType.PREDICATE;
}

/// TODO: make all transitions sets? no, should remove set edges */
class AtomTransition extends Transition {
  /// The token type or character value; or, signifies special label. */
  final int atomLabel;

  AtomTransition(ATNState target, this.atomLabel) : super(target);

  @override
  IntervalSet get label {
    return IntervalSet.ofOne(atomLabel);
  }

  @override
  bool matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
    return atomLabel == symbol;
  }

  @override
  String toString() {
    return label.toString();
  }

  @override
  TransitionType get type => TransitionType.ATOM;
}

class ActionTransition extends Transition {
  final int ruleIndex;
  final int actionIndex;
  final bool isCtxDependent; // e.g., $i ref in pred

  ActionTransition(target, this.ruleIndex,
      [this.actionIndex = -1, this.isCtxDependent = false])
      : super(target);

  @override
  bool get isEpsilon =>
      true; // we are to be ignored by analysis 'cept for predicates

  @override
  bool matches(symbol, minVocabSymbol, maxVocabSymbol) => false;

  @override
  String toString() {
    return 'action_$ruleIndex:$actionIndex';
  }

  @override
  TransitionType get type => TransitionType.ACTION;
}

// A transition containing a set of values.
class SetTransition extends Transition {
  @override
  late IntervalSet label;

  SetTransition(ATNState target, [IntervalSet? st]) : super(target) {
    label = st ?? IntervalSet.ofOne(Token.INVALID_TYPE);
  }

  @override
  bool matches(symbol, minVocabSymbol, maxVocabSymbol) {
    return label.contains(symbol);
  }

  @override
  String toString() {
    return label.toString();
  }

  @override
  TransitionType get type => TransitionType.SET;
}

class NotSetTransition extends SetTransition {
  NotSetTransition(target, st) : super(target, st);

  @override
  bool matches(symbol, minVocabSymbol, maxVocabSymbol) {
    return symbol >= minVocabSymbol &&
        symbol <= maxVocabSymbol &&
        !super.matches(symbol, minVocabSymbol, maxVocabSymbol);
  }

  @override
  String toString() {
    return '~' + super.toString();
  }

  @override
  TransitionType get type => TransitionType.NOT_SET;
}

class WildcardTransition extends Transition {
  WildcardTransition(target) : super(target);

  @override
  bool matches(symbol, minVocabSymbol, maxVocabSymbol) {
    return symbol >= minVocabSymbol && symbol <= maxVocabSymbol;
  }

  @override
  String toString() {
    return '.';
  }

  @override
  TransitionType get type => TransitionType.WILDCARD;
}

class PrecedencePredicateTransition extends AbstractPredicateTransition {
  final int precedence;

  PrecedencePredicateTransition(target, this.precedence) : super(target);

  @override
  bool get isEpsilon => true;

  @override
  bool matches(symbol, minVocabSymbol, maxVocabSymbol) => false;

  PrecedencePredicate get predicate {
    return PrecedencePredicate(precedence);
  }

  @override
  String toString() => '$precedence >= _p';

  @override
  TransitionType get type => TransitionType.PRECEDENCE;
}
