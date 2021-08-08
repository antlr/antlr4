/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import './util/bit_set.dart';
import 'atn/atn.dart';
import 'interval_set.dart';
import 'prediction_context.dart';
import 'rule_context.dart';
import 'token.dart';
import 'util/bit_set.dart';

class LL1Analyzer {
  /// Special value added to the lookahead sets to indicate that we hit
  ///  a predicate during analysis if {@code seeThruPreds==false}.
  static final int HIT_PRED = Token.INVALID_TYPE;

  final ATN atn;

  LL1Analyzer(this.atn);

  /// Calculates the SLL(1) expected lookahead set for each outgoing transition
  /// of an [ATNState]. The returned array has one element for each
  /// outgoing transition in [s]. If the closure from transition
  /// <em>i</em> leads to a semantic predicate before matching a symbol, the
  /// element at index <em>i</em> of the result will be null.
  ///
  /// @param s the ATN state
  /// @return the expected symbols for each outgoing transition of [s].
  List<IntervalSet?> getDecisionLookahead(ATNState s) {
//		System.out.println("LOOK("+s.stateNumber+")");
    return List<IntervalSet?>.generate(s.numberOfTransitions, (n) {
      final lookAlt = IntervalSet();
      final lookBusy = <ATNConfig>{};
      final seeThruPreds = false; // fail to get lookahead upon pred
      _LOOK(
        s.transition(n).target,
        null,
        PredictionContext.EMPTY,
        lookAlt,
        lookBusy,
        BitSet(),
        seeThruPreds,
        false,
      );

      // Wipe out lookahead for this alternative if we found nothing
      // or we had a predicate when we !seeThruPreds
      if (lookAlt.length == 0 || lookAlt.contains(HIT_PRED)) {
        return null;
      }
      return lookAlt;
    });
  }

  /// Compute set of tokens that can follow [s] in the ATN in the
  /// specified [ctx].
  ///
  /// <p>If [ctx] is null and the end of the rule containing
  /// [s] is reached, {@link Token#EPSILON} is added to the result set.
  /// If [ctx] is not null and the end of the outermost rule is
  /// reached, {@link Token#EOF} is added to the result set.</p>
  ///
  /// @param s the ATN state
  /// @param stopState the ATN state to stop at. This can be a
  /// [BlockEndState] to detect epsilon paths through a closure.
  /// @param ctx the complete parser context, or null if the context
  /// should be ignored
  ///
  /// @return The set of tokens that can follow [s] in the ATN in the
  /// specified [ctx].

  IntervalSet LOOK(
    ATNState s,
    RuleContext? ctx, [
    ATNState? stopState,
  ]) {
    final r = IntervalSet();
    final seeThruPreds = true; // ignore preds; get all lookahead
    final lookContext =
        ctx != null ? PredictionContext.fromRuleContext(s.atn, ctx) : null;
    _LOOK(
      s,
      stopState,
      lookContext,
      r,
      <ATNConfig>{},
      BitSet(),
      seeThruPreds,
      true,
    );
    return r;
  }

  /// Compute set of tokens that can follow [s] in the ATN in the
  /// specified [ctx].
  ///
  /// <p>If [ctx] is null and [stopState] or the end of the
  /// rule containing [s] is reached, {@link Token#EPSILON} is added to
  /// the result set. If [ctx] is not null and [addEOF] is
  /// [true] and [stopState] or the end of the outermost rule is
  /// reached, {@link Token#EOF} is added to the result set.</p>
  ///
  /// @param s the ATN state.
  /// @param stopState the ATN state to stop at. This can be a
  /// [BlockEndState] to detect epsilon paths through a closure.
  /// @param ctx The outer context, or null if the outer context should
  /// not be used.
  /// @param look The result lookahead set.
  /// @param lookBusy A set used for preventing epsilon closures in the ATN
  /// from causing a stack overflow. Outside code should pass
  /// {@code new HashSet<ATNConfig>} for this argument.
  /// @param calledRuleStack A set used for preventing left recursion in the
  /// ATN from causing a stack overflow. Outside code should pass
  /// {@code new BitSet()} for this argument.
  /// @param seeThruPreds [true] to true semantic predicates as
  /// implicitly [true] and "see through them", otherwise [false]
  /// to treat semantic predicates as opaque and add {@link #HIT_PRED} to the
  /// result if one is encountered.
  /// @param addEOF Add {@link Token#EOF} to the result if the end of the
  /// outermost context is reached. This parameter has no effect if [ctx]
  /// is null.
  void _LOOK(
      ATNState s,
      ATNState? stopState,
      PredictionContext? ctx,
      IntervalSet look,
      Set<ATNConfig> lookBusy,
      BitSet calledRuleStack,
      bool seeThruPreds,
      bool addEOF) {
//		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
    final c = ATNConfig(s, 0, ctx);
    if (!lookBusy.add(c)) return;

    if (s == stopState) {
      if (ctx == null) {
        look.addOne(Token.EPSILON);
        return;
      } else if (ctx.isEmpty && addEOF) {
        look.addOne(Token.EOF);
        return;
      }
    }

    if (s is RuleStopState) {
      if (ctx == null) {
        look.addOne(Token.EPSILON);
        return;
      } else if (ctx.isEmpty && addEOF) {
        look.addOne(Token.EOF);
        return;
      }

      if (ctx != PredictionContext.EMPTY) {
        // run thru all possible stack tops in ctx
        final removed = calledRuleStack[s.ruleIndex];
        try {
          calledRuleStack.clear(s.ruleIndex);
          for (var i = 0; i < ctx.length; i++) {
            final returnState = atn.states[ctx.getReturnState(i)]!;
//					    System.out.println("popping back to "+retState);
            _LOOK(returnState, stopState, ctx.getParent(i), look, lookBusy,
                calledRuleStack, seeThruPreds, addEOF);
          }
        } finally {
          if (removed) {
            calledRuleStack.set(s.ruleIndex);
          }
        }
        return;
      }
    }

    for (var i = 0; i < s.numberOfTransitions; i++) {
      final t = s.transition(i);
      if (t is RuleTransition) {
        if (calledRuleStack[t.target.ruleIndex]) {
          continue;
        }

        PredictionContext newContext = SingletonPredictionContext.create(
          ctx,
          t.followState.stateNumber,
        );

        try {
          calledRuleStack.set(t.target.ruleIndex);
          _LOOK(t.target, stopState, newContext, look, lookBusy,
              calledRuleStack, seeThruPreds, addEOF);
        } finally {
          calledRuleStack.clear(t.target.ruleIndex);
        }
      } else if (t is AbstractPredicateTransition) {
        if (seeThruPreds) {
          _LOOK(
            t.target,
            stopState,
            ctx,
            look,
            lookBusy,
            calledRuleStack,
            seeThruPreds,
            addEOF,
          );
        } else {
          look.addOne(HIT_PRED);
        }
      } else if (t.isEpsilon) {
        _LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack,
            seeThruPreds, addEOF);
      } else if (t is WildcardTransition) {
        look.addAll(
          IntervalSet.ofRange(Token.MIN_USER_TOKEN_TYPE, atn.maxTokenType),
        );
      } else {
//				System.out.println("adding "+ t);
        var set = t.label;
        if (set != null) {
          if (t is NotSetTransition) {
            set = set.complement(
              IntervalSet.ofRange(Token.MIN_USER_TOKEN_TYPE, atn.maxTokenType),
            );
          }
          look.addAll(set);
        }
      }
    }
  }
}
