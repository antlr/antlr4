/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:collection';

import 'atn/atn.dart';
import 'dfa/dfa.dart';
import 'error/error.dart';
import 'misc/pair.dart';
import 'parser.dart';
import 'parser_rule_context.dart';
import 'token.dart';
import 'token_stream.dart';
import 'vocabulary.dart';

/// A parser simulator that mimics what ANTLR's generated
///  parser code does. A ParserATNSimulator is used to make
///  predictions via adaptivePredict but this class moves a pointer through the
///  ATN to simulate parsing. ParserATNSimulator just
///  makes us efficient rather than having to backtrack, for example.
///
///  This properly creates parse trees even for left recursive rules.
///
///  We rely on the left recursive rule invocation and special predicate
///  transitions to make left recursive rules work.
///
///  See TestParserInterpreter for examples.
class ParserInterpreter extends Parser {
  @override
  final String grammarFileName;
  final ATN atn;

  late List<DFA> decisionToDFA; // not shared like it is for generated parsers
  final PredictionContextCache sharedContextCache = PredictionContextCache();

  @override
  final List<String> ruleNames;

  @override
  final Vocabulary vocabulary;

  /// This stack corresponds to the _parentctx, _parentState pair of locals
  ///  that would exist on call stack frames with a recursive descent parser;
  ///  in the generated function for a left-recursive rule you'd see:
  ///
  ///   EContext e(int _p) throws RecognitionException {
  ///      ParserRuleContext _parentctx = context;    // Pair.a
  ///      int _parentState = state;          // Pair.b
  ///      ...
  ///  }
  ///
  ///  Those values are used to create new recursive rule invocation contexts
  ///  associated with left operand of an alt like "expr '*' expr".
  final DoubleLinkedQueue<Pair<ParserRuleContext?, int>> _parentContextStack =
      DoubleLinkedQueue();

  /// We need a map from (decision,inputIndex)->forced alt for computing ambiguous
  ///  parse trees. For now, we allow exactly one override.
  int overrideDecision = -1;
  int overrideDecisionInputIndex = -1;
  int overrideDecisionAlt = -1;
  bool overrideDecisionReached =
      false; // latch and only override once; error might trigger infinite loop

  /// What is the current context when we override a decisions?  This tells
  ///  us what the root of the parse tree is when using override
  ///  for an ambiguity/lookahead check.
  InterpreterRuleContext? overrideDecisionRoot;

  /// Return the root of the parse, which can be useful if the parser
  ///  bails out. You still can access the top node. Note that,
  ///  because of the way left recursive rules add children, it's possible
  ///  that the root will not have any children if the start rule immediately
  ///  called and left recursive rule that fails.
  ///
  /// @since 4.5.1
  late InterpreterRuleContext rootContext;

  ParserInterpreter(
    this.grammarFileName,
    this.vocabulary,
    this.ruleNames,
    this.atn,
    TokenStream input,
  ) : super(input) {
    // init decision DFA
    final numberOfDecisions = atn.numberOfDecisions;
    decisionToDFA = List<DFA>.generate(numberOfDecisions, (n) {
      final decisionState = atn.getDecisionState(n);
      return DFA(decisionState, n);
    });

    // get atn simulator that knows how to do predictions
    interpreter = ParserATNSimulator(
      this,
      atn,
      decisionToDFA,
      sharedContextCache,
    );
  }

  @override
  void reset([bool resetInput = true]) {
    super.reset(resetInput);
    overrideDecisionReached = false;
    overrideDecisionRoot = null;
  }

  @override
  ATN getATN() {
    return atn;
  }

  /// Begin parsing at startRuleIndex */
  ParserRuleContext parse(int startRuleIndex) {
    final startRuleStartState = atn.ruleToStartState[startRuleIndex];

    rootContext = createInterpreterRuleContext(
      null,
      ATNState.INVALID_STATE_NUMBER,
      startRuleIndex,
    );
    if (startRuleStartState.isLeftRecursiveRule) {
      enterRecursionRule(
          rootContext, startRuleStartState.stateNumber, startRuleIndex, 0);
    } else {
      enterRule(rootContext, startRuleStartState.stateNumber, startRuleIndex);
    }

    while (true) {
      final p = atnState;
      switch (p.stateType) {
        case StateType.RULE_STOP:
          // pop; return from rule
          if (context!.isEmpty) {
            if (startRuleStartState.isLeftRecursiveRule) {
              final result = context!;
              final parentContext = _parentContextStack.removeLast();
              unrollRecursionContexts(parentContext.a);
              return result;
            } else {
              exitRule();
              return rootContext;
            }
          }

          visitRuleStopState(p);
          break;

        default:
          try {
            visitState(p);
          } on RecognitionException catch (e) {
            state = atn.ruleToStopState[p.ruleIndex].stateNumber;
            context!.exception = e;
            errorHandler.reportError(this, e);
            recover(e);
          }

          break;
      }
    }
  }

  @override
  void enterRecursionRule(
    ParserRuleContext localctx,
    int state,
    int ruleIndex,
    int precedence,
  ) {
    final pair = Pair<ParserRuleContext?, int>(context, localctx.invokingState);
    _parentContextStack.add(pair);
    super.enterRecursionRule(localctx, state, ruleIndex, precedence);
  }

  ATNState get atnState {
    return atn.states[state]!;
  }

  void visitState(ATNState p) {
    assert(context != null);
//		System.out.println("visitState "+p.stateNumber);
    var predictedAlt = 1;
    if (p is DecisionState) {
      predictedAlt = visitDecisionState(p);
    }

    final transition = p.transition(predictedAlt - 1);
    switch (transition.type) {
      case TransitionType.EPSILON:
        if (p.stateType == StateType.STAR_LOOP_ENTRY &&
            (p as StarLoopEntryState).isPrecedenceDecision &&
            (transition.target is! LoopEndState)) {
          // We are at the start of a left recursive rule's (...)* loop
          // and we're not taking the exit branch of loop.
          final localctx = createInterpreterRuleContext(
            _parentContextStack.last.a,
            _parentContextStack.last.b,
            context!.ruleIndex,
          );
          pushNewRecursionContext(
            localctx,
            atn.ruleToStartState[p.ruleIndex].stateNumber,
            context!.ruleIndex,
          );
        }
        break;

      case TransitionType.ATOM:
        match((transition as AtomTransition).atomLabel);
        break;

      case TransitionType.RANGE:
      case TransitionType.SET:
      case TransitionType.NOT_SET:
        if (!transition.matches(
          inputStream.LA(1)!,
          Token.MIN_USER_TOKEN_TYPE,
          65535,
        )) {
          recoverInline();
        }
        matchWildcard();
        break;

      case TransitionType.WILDCARD:
        matchWildcard();
        break;

      case TransitionType.RULE:
        final ruleStartState = transition.target as RuleStartState;
        final ruleIndex = ruleStartState.ruleIndex;
        final newctx =
            createInterpreterRuleContext(context, p.stateNumber, ruleIndex);
        if (ruleStartState.isLeftRecursiveRule) {
          enterRecursionRule(newctx, ruleStartState.stateNumber, ruleIndex,
              (transition as RuleTransition).precedence);
        } else {
          enterRule(newctx, transition.target.stateNumber, ruleIndex);
        }
        break;

      case TransitionType.PREDICATE:
        final predicateTransition = transition as PredicateTransition;
        if (!sempred(context, predicateTransition.ruleIndex,
            predicateTransition.predIndex)) {
          throw FailedPredicateException(this);
        }

        break;

      case TransitionType.ACTION:
        final actionTransition = transition as ActionTransition;
        action(
          context,
          actionTransition.ruleIndex,
          actionTransition.actionIndex,
        );
        break;

      case TransitionType.PRECEDENCE:
        if (!precpred(
          context,
          (transition as PrecedencePredicateTransition).precedence,
        )) {
          throw FailedPredicateException(
              this, 'precpred(context, ${(transition).precedence})');
        }
        break;

      default:
        throw UnsupportedError('Unrecognized ATN transition type.');
    }

    state = transition.target.stateNumber;
  }

  /// Method visitDecisionState() is called when the interpreter reaches
  ///  a decision state (instance of DecisionState). It gives an opportunity
  ///  for subclasses to track interesting things.
  int visitDecisionState(DecisionState p) {
    var predictedAlt = 1;
    assert(context != null);
    if (p.numberOfTransitions > 1) {
      errorHandler.sync(this);
      final decision = p.decision;
      if (decision == overrideDecision &&
          inputStream.index == overrideDecisionInputIndex &&
          !overrideDecisionReached) {
        predictedAlt = overrideDecisionAlt;
        overrideDecisionReached = true;
      } else {
        predictedAlt = interpreter!.adaptivePredict(
          inputStream,
          decision,
          context!,
        );
      }
    }
    return predictedAlt;
  }

  /// Provide simple "factory" for InterpreterRuleContext's.
  ///  @since 4.5.1
  InterpreterRuleContext createInterpreterRuleContext(
    ParserRuleContext? parent,
    int invokingStateNumber,
    int ruleIndex,
  ) {
    return InterpreterRuleContext(parent, invokingStateNumber, ruleIndex);
  }

  void visitRuleStopState(ATNState p) {
    final ruleStartState = atn.ruleToStartState[p.ruleIndex];
    if (ruleStartState.isLeftRecursiveRule) {
      final parentContext = _parentContextStack.removeLast();
      unrollRecursionContexts(parentContext.a);
      state = parentContext.b;
    } else {
      exitRule();
    }

    final ruleTransition = atn.states[state]!.transition(0) as RuleTransition;
    state = ruleTransition.followState.stateNumber;
  }

  /// Override this parser interpreters normal decision-making process
  ///  at a particular decision and input token index. Instead of
  ///  allowing the adaptive prediction mechanism to choose the
  ///  first alternative within a block that leads to a successful parse,
  ///  force it to take the alternative, 1..n for n alternatives.
  ///
  ///  As an implementation limitation right now, you can only specify one
  ///  override. This is sufficient to allow construction of different
  ///  parse trees for ambiguous input. It means re-parsing the entire input
  ///  in general because you're never sure where an ambiguous sequence would
  ///  live in the various parse trees. For example, in one interpretation,
  ///  an ambiguous input sequence would be matched completely in expression
  ///  but in another it could match all the way back to the root.
  ///
  ///  s : e '!'? ;
  ///  e : ID
  ///    | ID '!'
  ///    ;
  ///
  ///  Here, x! can be matched as (s (e ID) !) or (s (e ID !)). In the first
  ///  case, the ambiguous sequence is fully contained only by the root.
  ///  In the second case, the ambiguous sequences fully contained within just
  ///  e, as in: (e ID !).
  ///
  ///  Rather than trying to optimize this and make
  ///  some intelligent decisions for optimization purposes, I settled on
  ///  just re-parsing the whole input and then using
  ///  {link Trees#getRootOfSubtreeEnclosingRegion} to find the minimal
  ///  subtree that contains the ambiguous sequence. I originally tried to
  ///  record the call stack at the point the parser detected and ambiguity but
  ///  left recursive rules create a parse tree stack that does not reflect
  ///  the actual call stack. That impedance mismatch was enough to make
  ///  it it challenging to restart the parser at a deeply nested rule
  ///  invocation.
  ///
  ///  Only parser interpreters can override decisions so as to avoid inserting
  ///  override checking code in the critical ALL(*) prediction execution path.
  ///
  ///  @since 4.5.1
  void addDecisionOverride(int decision, int tokenIndex, int forcedAlt) {
    overrideDecision = decision;
    overrideDecisionInputIndex = tokenIndex;
    overrideDecisionAlt = forcedAlt;
  }

  /// Rely on the error handler for this parser but, if no tokens are consumed
  ///  to recover, add an error node. Otherwise, nothing is seen in the parse
  ///  tree.
  void recover(RecognitionException e) {
    final i = inputStream.index;
    errorHandler.recover(this, e);
    assert(this.context != null);
    final context = this.context as ParserRuleContext;
    if (inputStream.index == i) {
      // no input consumed, better add an error node
      if (e is InputMismatchException) {
        final ime = e;
        final tok = e.offendingToken;
        var expectedTokenType = Token.INVALID_TYPE;
        if (ime.expectedTokens != null && !ime.expectedTokens!.isNil) {
          expectedTokenType = ime.expectedTokens!.minElement; // get any element
        }
        final errToken = tokenFactory.create(
          expectedTokenType,
          tok.text,
          Pair(tok.tokenSource, tok.tokenSource?.inputStream),
          Token.DEFAULT_CHANNEL,
          -1,
          -1,
          // invalid start/stop
          tok.line,
          tok.charPositionInLine,
        );
        context.addErrorNode(createErrorNode(context, errToken));
      } else {
        // NoViableAlt
        final tok = e.offendingToken;
        final errToken = tokenFactory.create(
          Token.INVALID_TYPE,
          tok.text,
          Pair(tok.tokenSource, tok.tokenSource?.inputStream),
          Token.DEFAULT_CHANNEL,
          -1,
          -1,
          // invalid start/stop
          tok.line,
          tok.charPositionInLine,
        );
        context.addErrorNode(createErrorNode(context, errToken));
      }
    }
  }

  Token recoverInline() {
    return errorHandler.recoverInline(this);
  }
}
