/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../prediction_context.dart';
import '../../util/murmur_hash.dart';
import 'atn_state.dart';
import 'lexer_action_executor.dart';
import 'semantic_context.dart';

Map<String, dynamic> checkParams(params, isCfg) {
  if (params == null) {
    final result = <String, dynamic>{
      'state': null,
      'alt': null,
      'context': null,
      'semanticContext': null
    };
    if (isCfg) {
      result['reachesIntoOuterContext'] = 0;
    }
    return result;
  } else {
    final props = <String, dynamic>{};
    props['state'] = params.state;
    props['alt'] = (params.alt == null) ? null : params.alt;
    props['context'] = params.context;
    props['semanticContext'] = params.semanticContext;
    if (isCfg) {
      props['reachesIntoOuterContext'] = params.reachesIntoOuterContext ?? 0;
      props['precedenceFilterSuppressed'] =
          params.precedenceFilterSuppressed ?? false;
    }
    return props;
  }
}

/// A tuple: (ATN state, predicted alt, syntactic, semantic context).
///  The syntactic context is a graph-structured stack node whose
///  path(s) to the root is the rule invocation(s)
///  chain used to arrive at the state.  The semantic context is
///  the tree of semantic predicates encountered before reaching
///  an ATN state.
class ATNConfig {
  /// This field stores the bit mask for implementing the
  /// {@link #isPrecedenceFilterSuppressed} property as a bit within the
  /// existing {@link #reachesIntoOuterContext} field.
  static final int SUPPRESS_PRECEDENCE_FILTER = 0x40000000;

  /// The ATN state associated with this configuration */
  ATNState state;

  /// What alt (or lexer rule) is predicted by this configuration */
  int alt;

  /// The stack of invoking states leading to the rule/states associated
  ///  with this config.  We track only those contexts pushed during
  ///  execution of the ATN simulator.
  PredictionContext? context;

  /// We cannot execute predicates dependent upon local context unless
  /// we know for sure we are in the correct context. Because there is
  /// no way to do this efficiently, we simply cannot evaluate
  /// dependent predicates unless we are in the rule that initially
  /// invokes the ATN simulator.
  ///
  /// <p>
  /// closure() tracks the depth of how far we dip into the outer context:
  /// depth &gt; 0.  Note that it may not be totally accurate depth since I
  /// don't ever decrement. TODO: make it a bool then</p>
  ///
  /// <p>
  /// For memory efficiency, the {@link #isPrecedenceFilterSuppressed} method
  /// is also backed by this field. Since the field is ly accessible, the
  /// highest bit which would not cause the value to become negative is used to
  /// store this field. This choice minimizes the risk that code which only
  /// compares this value to 0 would be affected by the new purpose of the
  /// flag. It also ensures the performance of the existing [ATNConfig]
  /// constructors as well as certain operations like
  /// {@link ATNConfigSet#add(ATNConfig, DoubleKeyMap)} method are
  /// <em>completely</em> unaffected by the change.</p>
  int reachesIntoOuterContext;

  SemanticContext semanticContext;

  ATNConfig(
    this.state,
    this.alt,
    this.context, [
    this.semanticContext = EmptySemanticContext.Instance,
  ]) : reachesIntoOuterContext = 0;

  ATNConfig.dup(
    ATNConfig c, {
    ATNState? state,
    int? alt,
    PredictionContext? context,
    SemanticContext? semanticContext,
  })  : state = state ?? c.state,
        alt = alt ?? c.alt,
        context = context ?? c.context,
        semanticContext = semanticContext ?? c.semanticContext,
        reachesIntoOuterContext = c.reachesIntoOuterContext;

  /// This method gets the value of the {@link #reachesIntoOuterContext} field
  /// as it existed prior to the introduction of the
  /// {@link #isPrecedenceFilterSuppressed} method.
  int get outerContextDepth {
    return reachesIntoOuterContext & ~SUPPRESS_PRECEDENCE_FILTER;
  }

  bool isPrecedenceFilterSuppressed() {
    return (reachesIntoOuterContext & SUPPRESS_PRECEDENCE_FILTER) != 0;
  }

  void setPrecedenceFilterSuppressed(bool value) {
    if (value) {
      reachesIntoOuterContext |= 0x40000000;
    } else {
      reachesIntoOuterContext &= ~SUPPRESS_PRECEDENCE_FILTER;
    }
  }

  /// An ATN configuration is equal to another if both have
  ///  the same state, they predict the same alternative, and
  ///  syntactic/semantic contexts are the same.
  @override
  bool operator ==(Object other) {
    if (other is ATNConfig) {
      return state.stateNumber == other.state.stateNumber &&
          alt == other.alt &&
          (context == other.context ||
              (context != null && context == other.context)) &&
          semanticContext == other.semanticContext &&
          isPrecedenceFilterSuppressed() ==
              other.isPrecedenceFilterSuppressed();
    }
    return false;
  }

  @override
  int get hashCode {
    var hashCode = MurmurHash.initialize(7);
    hashCode = MurmurHash.update(hashCode, state.stateNumber);
    hashCode = MurmurHash.update(hashCode, alt);
    hashCode = MurmurHash.update(hashCode, context);
    hashCode = MurmurHash.update(hashCode, semanticContext);
    hashCode = MurmurHash.finish(hashCode, 4);
    return hashCode;
  }

  @override
  String toString([_, bool showAlt = true]) {
    final buf = StringBuffer();
    // if ( state.ruleIndex>=0 ) {
    //  if ( recog!=null ) buf.write(recog.ruleNames[state.ruleIndex]+":");
    //  else buf.write(state.ruleIndex+":");
    // }
    buf.write('(');
    buf.write(state);
    if (showAlt) {
      buf.write(',');
      buf.write(alt);
    }
    if (context != null) {
      buf.write(',[');
      buf.write(context.toString());
      buf.write(']');
    }
    if (semanticContext != EmptySemanticContext.Instance) {
      buf.write(',');
      buf.write(semanticContext);
    }
    if (outerContextDepth > 0) {
      buf.write(',up=');
      buf.write(outerContextDepth);
    }
    buf.write(')');
    return buf.toString();
  }
}

class LexerATNConfig extends ATNConfig {
  /// Gets the [LexerActionExecutor] capable of executing the embedded
  /// action(s) for the current configuration.
  LexerActionExecutor? lexerActionExecutor;

  bool passedThroughNonGreedyDecision = false;

  LexerATNConfig(
    ATNState state,
    int alt,
    PredictionContext context, [
    this.lexerActionExecutor,
  ]) : super(state, alt, context, EmptySemanticContext.Instance) {
    passedThroughNonGreedyDecision = false;
  }

  LexerATNConfig.dup(
    LexerATNConfig c,
    ATNState state, {
    this.lexerActionExecutor,
    PredictionContext? context,
  }) : super.dup(c, state: state, context: context) {
    lexerActionExecutor = lexerActionExecutor ?? c.lexerActionExecutor;
    passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
  }

  bool hasPassedThroughNonGreedyDecision() {
    return passedThroughNonGreedyDecision;
  }

  @override
  int get hashCode {
    var hashCode = MurmurHash.initialize(7);
    hashCode = MurmurHash.update(hashCode, state.stateNumber);
    hashCode = MurmurHash.update(hashCode, alt);
    hashCode = MurmurHash.update(hashCode, context);
    hashCode = MurmurHash.update(hashCode, semanticContext);
    hashCode =
        MurmurHash.update(hashCode, passedThroughNonGreedyDecision ? 1 : 0);
    hashCode = MurmurHash.update(hashCode, lexerActionExecutor);
    hashCode = MurmurHash.finish(hashCode, 6);
    return hashCode;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    } else if (other is LexerATNConfig) {
      final lexerOther = other;
      if (passedThroughNonGreedyDecision !=
          lexerOther.passedThroughNonGreedyDecision) {
        return false;
      }

      if (lexerActionExecutor != lexerOther.lexerActionExecutor) {
        return false;
      }

      return super == other;
    }
    return false;
  }

  static bool checkNonGreedyDecision(LexerATNConfig source, ATNState target) {
    return source.passedThroughNonGreedyDecision ||
        target is DecisionState && target.nonGreedy;
  }
}
