/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'package:antlr4/src/atn/src/atn_simulator.dart';
import 'package:collection/collection.dart';

import '../../recognizer.dart';
import '../../rule_context.dart';
import '../../util/murmur_hash.dart';

/// A tree structure used to record the semantic context in which
///  an ATN configuration is valid.  It's either a single predicate,
///  a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
///
///  <p>I have scoped the [AND], [OR], and [Predicate] subclasses of
///  [SemanticContext] within the scope of this outer class.</p>
abstract class SemanticContext {
  const SemanticContext();

  /// For context independent predicates, we evaluate them without a local
  /// context (i.e., null context). That way, we can evaluate them without
  /// having to create proper rule-specific context during prediction (as
  /// opposed to the parser, which creates them naturally). In a practical
  /// sense, this avoids a cast exception from RuleContext to myruleContext.
  ///
  /// <p>For context dependent predicates, we must pass in a local context so that
  /// references such as $arg evaluate properly as _localctx.arg. We only
  /// capture context dependent predicates in the context in which we begin
  /// prediction, so we passed in the outer context here in case of context
  /// dependent predicate evaluation.</p>
  bool eval(Recognizer parser, RuleContext? parserCallStack);

  /// Evaluate the precedence predicates for the context and reduce the result.
  ///
  /// @param parser The parser instance.
  /// @param parserCallStack
  /// @return The simplified semantic context after precedence predicates are
  /// evaluated, which will be one of the following values.
  /// <ul>
  /// <li>{@link #NONE}in if the predicate simplifies to [true] after
  /// precedence predicates are evaluated.</li>
  /// <li>nullin if the predicate simplifies to [false] after
  /// precedence predicates are evaluated.</li>
  /// <li>[this]in if the semantic context is not changed as a result of
  /// precedence predicate evaluation.</li>
  /// <li>A non-null [SemanticContext]in the new simplified
  /// semantic context after precedence predicates are evaluated.</li>
  /// </ul>
  SemanticContext? evalPrecedence(
    Recognizer parser,
    RuleContext? parserCallStack,
  ) {
    return this;
  }

  static SemanticContext? and(SemanticContext? a, SemanticContext? b) {
    if (a == null || a == EmptySemanticContext.Instance) return b;
    if (b == null || b == EmptySemanticContext.Instance) return a;
    final result = AND(a, b);
    if (result.opnds.length == 1) {
      return result.opnds[0];
    }

    return result;
  }

  ///
  ///  @see ParserATNSimulator#getPredsForAmbigAlts
  static SemanticContext? or(SemanticContext? a, SemanticContext? b) {
    if (a == null) return b;
    if (b == null) return a;
    if (a == EmptySemanticContext.Instance || b == EmptySemanticContext.Instance) return EmptySemanticContext.Instance;
    final result = OR(a, b);
    if (result.opnds.length == 1) {
      return result.opnds[0];
    }

    return result;
  }

  static Iterable<PrecedencePredicate> filterPrecedencePredicates(
      Iterable<SemanticContext> collection) {
    return collection.whereType<PrecedencePredicate>();
  }

  static Iterable<SemanticContext> filterNonPrecedencePredicates(
      Iterable<SemanticContext> collection) {
    return collection.where((e) => e is! PrecedencePredicate);
  }
}

class EmptySemanticContext extends SemanticContext {
  /// The default [SemanticContext], which is semantically equivalent to
  /// a predicate of the form {@code {true}?}.
  static const SemanticContext Instance = Predicate();

  @override
  bool eval(Recognizer<ATNSimulator> parser, RuleContext? parserCallStack) {
    return false;
  }
}

class Predicate extends SemanticContext {
  final int ruleIndex;
  final int predIndex;
  final bool isCtxDependent; // e.g., $i ref in pred

  const Predicate(
      [this.ruleIndex = -1, this.predIndex = -1, this.isCtxDependent = false]);

  @override
  bool eval(Recognizer parser, RuleContext? parserCallStack) {
    final localctx = isCtxDependent ? parserCallStack : null;
    return parser.sempred(localctx, ruleIndex, predIndex);
  }

  @override
  int get hashCode {
    var hashCode = MurmurHash.initialize();
    hashCode = MurmurHash.update(hashCode, ruleIndex);
    hashCode = MurmurHash.update(hashCode, predIndex);
    hashCode = MurmurHash.update(hashCode, isCtxDependent ? 1 : 0);
    hashCode = MurmurHash.finish(hashCode, 3);
    return hashCode;
  }

  @override
  bool operator ==(Object obj) {
    return obj is Predicate &&
        ruleIndex == obj.ruleIndex &&
        predIndex == obj.predIndex &&
        isCtxDependent == obj.isCtxDependent;
  }

  @override
  String toString() {
    return '{$ruleIndex:$predIndex}?';
  }
}

class PrecedencePredicate extends SemanticContext
    implements Comparable<PrecedencePredicate> {
  final int precedence;

  PrecedencePredicate([this.precedence = 0]);

  @override
  bool eval(Recognizer parser, RuleContext? parserCallStack) {
    return parser.precpred(parserCallStack, precedence);
  }

  @override
  SemanticContext? evalPrecedence(
    Recognizer parser,
    RuleContext? parserCallStack,
  ) {
    if (parser.precpred(parserCallStack, precedence)) {
      return EmptySemanticContext.Instance;
    } else {
      return null;
    }
  }

  @override
  int compareTo(PrecedencePredicate o) {
    return precedence - o.precedence;
  }

  @override
  int get hashCode {
    var hashCode = 1;
    hashCode = 31 * hashCode + precedence;
    return hashCode;
  }

  @override
  bool operator ==(Object other) {
    if (other is! PrecedencePredicate) {
      return false;
    }
    return precedence == other.precedence;
  }

// precedence >= _precedenceStack.peek()
  @override
  String toString() {
    return '{$precedence>=prec}?';
  }
}

/// This is the base class for semantic context "operators", which operate on
/// a collection of semantic context "operands".
///
/// @since 4.3
abstract class Operator extends SemanticContext {
  /// Gets the operands for the semantic context operator.
  ///
  /// @return a collection of [SemanticContext] operands for the
  /// operator.
  ///
  /// @since 4.3
  List<SemanticContext> get operands;
}

/// A semantic context which is true whenever none of the contained contexts
/// is false.

class AND extends Operator {
  late final List<SemanticContext> opnds;

  AND(SemanticContext a, SemanticContext b) {
    var operands = <SemanticContext>{};
    if (a is AND) {
      operands.addAll(a.opnds);
    } else {
      operands.add(a);
    }
    if (b is AND) {
      operands.addAll(b.opnds);
    } else {
      operands.add(b);
    }

    final precedencePredicates =
        SemanticContext.filterPrecedencePredicates(operands);

    operands = SemanticContext.filterNonPrecedencePredicates(operands).toSet();
    if (precedencePredicates.isNotEmpty) {
      // interested in the transition with the lowest precedence
      final reduced =
          precedencePredicates.reduce((a, b) => a.compareTo(b) <= 0 ? a : b);
      operands.add(reduced);
    }

    opnds = operands.toList();
  }

  @override
  List<SemanticContext> get operands {
    return opnds;
  }

  @override
  bool operator ==(Object other) {
    if (other is! AND) return false;
    return ListEquality().equals(opnds, other.opnds);
  }

  @override
  int get hashCode {
    return MurmurHash.getHashCode(opnds, runtimeType.hashCode);
  }

  /// {@inheritDoc}
  ///
  /// <p>
  /// The evaluation of predicates by this context is short-circuiting, but
  /// unordered.</p>

  @override
  bool eval(Recognizer parser, RuleContext? parserCallStack) {
    for (var opnd in opnds) {
      if (!opnd.eval(parser, parserCallStack)) return false;
    }
    return true;
  }

  @override
  SemanticContext? evalPrecedence(
    Recognizer parser,
    RuleContext? parserCallStack,
  ) {
    var differs = false;
    final operands = <SemanticContext>[];
    for (var context in opnds) {
      final evaluated = context.evalPrecedence(parser, parserCallStack);
      differs |= (evaluated != context);
      if (evaluated == null) {
        // The AND context is false if any element is false
        return null;
      } else if (evaluated != EmptySemanticContext.Instance) {
        // Reduce the result by skipping true elements
        operands.add(evaluated);
      }
    }

    if (!differs) {
      return this;
    }

    if (operands.isEmpty) {
      // all elements were true, so the AND context is true
      return EmptySemanticContext.Instance;
    }

    SemanticContext? result = operands[0];
    for (var i = 1; i < operands.length; i++) {
      result = SemanticContext.and(result, operands[i]);
    }

    return result;
  }

  @override
  String toString() {
    return opnds.join('&&');
  }
}

/// A semantic context which is true whenever at least one of the contained
/// contexts is true.
class OR extends Operator {
  late final List<SemanticContext> opnds;

  OR(SemanticContext a, SemanticContext b) {
    var operands = <SemanticContext>{};
    if (a is OR) {
      operands.addAll(a.opnds);
    } else {
      operands.add(a);
    }
    if (b is OR) {
      operands.addAll(b.opnds);
    } else {
      operands.add(b);
    }

    final precedencePredicates =
        SemanticContext.filterPrecedencePredicates(operands);

    operands = SemanticContext.filterNonPrecedencePredicates(operands).toSet();
    if (precedencePredicates.isNotEmpty) {
      // interested in the transition with the highest precedence
      final reduced =
          precedencePredicates.reduce((a, b) => a.compareTo(b) >= 0 ? a : b);
      operands.add(reduced);
    }

    opnds = operands.toList();
  }

  @override
  List<SemanticContext> get operands {
    return opnds;
  }

  @override
  bool operator ==(Object other) {
    if (other is! OR) return false;
    return ListEquality().equals(opnds, other.opnds);
  }

  @override
  int get hashCode {
    return MurmurHash.getHashCode(opnds, runtimeType.hashCode);
  }

  /// {@inheritDoc}
  ///
  /// <p>
  /// The evaluation of predicates by this context is short-circuiting, but
  /// unordered.</p>

  @override
  bool eval(Recognizer parser, RuleContext? parserCallStack) {
    for (var opnd in opnds) {
      if (opnd.eval(parser, parserCallStack)) return true;
    }
    return false;
  }

  @override
  SemanticContext? evalPrecedence(
    Recognizer parser,
    RuleContext? parserCallStack,
  ) {
    var differs = false;
    final operands = <SemanticContext>[];
    for (var context in opnds) {
      final evaluated = context.evalPrecedence(parser, parserCallStack);
      differs |= (evaluated != context);
      if (evaluated == EmptySemanticContext.Instance) {
        // The OR context is true if any element is true
        return EmptySemanticContext.Instance;
      } else if (evaluated != null) {
        // Reduce the result by skipping false elements
        operands.add(evaluated);
      }
    }

    if (!differs) {
      return this;
    }

    if (operands.isEmpty) {
      // all elements were false, so the OR context is false
      return null;
    }

    SemanticContext? result = operands[0];
    for (var i = 1; i < operands.length; i++) {
      result = SemanticContext.or(result, operands[i]);
    }

    return result;
  }

  @override
  String toString() {
    return opnds.join('||');
  }
}
