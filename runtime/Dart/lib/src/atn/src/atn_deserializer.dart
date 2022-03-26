/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../interval_set.dart';
import '../../misc/pair.dart';
import '../../token.dart';
import 'atn.dart';
import 'atn_state.dart';
import 'atn_type.dart';
import 'lexer_action.dart';
import 'transition.dart';

class ATNDeserializationOptions {
  static late final ATNDeserializationOptions defaultOptions =
      ATNDeserializationOptions(true);

  bool readOnly;
  late bool verifyATN;
  late bool generateRuleBypassTransitions;

  ATNDeserializationOptions(this.readOnly,
      [ATNDeserializationOptions? options]) {
    if (options == null) {
      verifyATN = true;
      generateRuleBypassTransitions = false;
    } else {
      verifyATN = options.verifyATN;
      generateRuleBypassTransitions = options.generateRuleBypassTransitions;
    }
  }

  bool isReadOnly() {
    return readOnly;
  }

  void makeReadOnly() {
    readOnly = true;
  }

  bool isVerifyATN() {
    return verifyATN;
  }

  void setVerifyATN(bool verifyATN) {
    throwIfReadOnly();
    this.verifyATN = verifyATN;
  }

  bool isGenerateRuleBypassTransitions() {
    return generateRuleBypassTransitions;
  }

  void setGenerateRuleBypassTransitions(bool generateRuleBypassTransitions) {
    throwIfReadOnly();
    this.generateRuleBypassTransitions = generateRuleBypassTransitions;
  }

  void throwIfReadOnly() {
    if (isReadOnly()) {
      throw StateError('The object is read only.');
    }
  }
}

class ATNDeserializer {
  static final SERIALIZED_VERSION = 4;

  late final ATNDeserializationOptions deserializationOptions;
  late List<int> data;
  int pos = 0;

  ATNDeserializer([ATNDeserializationOptions? options]) {
    deserializationOptions =
        options ?? ATNDeserializationOptions.defaultOptions;
  }

  ATN deserialize(List<int> data) {
    this.data = data;
    this.pos = 0;
    checkVersion();
    final atn = readATN();
    readStates(atn);
    readRules(atn);
    readModes(atn);
    final sets = <IntervalSet>[];
    readSets(atn, sets);
    readEdges(atn, sets);
    readDecisions(atn);
    readLexerActions(atn);
    markPrecedenceDecisions(atn);
    verifyATN(atn);
    if (deserializationOptions.generateRuleBypassTransitions &&
        atn.grammarType == ATNType.PARSER) {
      generateRuleBypassTransitions(atn);
      // re-verify after modification
      verifyATN(atn);
    }
    return atn;
  }

  void checkVersion() {
    final version = readInt();
    if (version != SERIALIZED_VERSION) {
      throw ('Could not deserialize ATN with version $version (expected $SERIALIZED_VERSION).');
    }
  }

  ATN readATN() {
    final grammarType = readInt();
    final maxTokenType = readInt();
    return ATN(ATNType.values[grammarType], maxTokenType);
  }

  void readStates(ATN atn) {
    final loopBackStateNumbers = <Pair<LoopEndState, int>>[];
    final endStateNumbers = <Pair<BlockStartState, int>>[];
    final nstates = readInt();
    for (var i = 0; i < nstates; i++) {
      final stype = StateType.values[readInt()];
      // ignore bad type of states
      if (stype == StateType.INVALID_TYPE) {
        atn.addState(null);
        continue;
      }

      var ruleIndex = readInt();

      final s = stateFactory(stype, ruleIndex);
      if (s is LoopEndState) {
        // special case
        final loopBackStateNumber = readInt();
        loopBackStateNumbers.add(Pair(s, loopBackStateNumber));
      } else if (s is BlockStartState) {
        final endStateNumber = readInt();
        endStateNumbers.add(Pair(s, endStateNumber));
      }
      atn.addState(s);
    }

    // delay the assignment of loop back and end states until we know all the state instances have been initialized
    for (final pair in loopBackStateNumbers) {
      pair.a.loopBackState = atn.states[pair.b];
    }

    for (final pair in endStateNumbers) {
      pair.a.endState = atn.states[pair.b] as BlockEndState;
    }

    final numNonGreedyStates = readInt();
    for (var i = 0; i < numNonGreedyStates; i++) {
      final stateNumber = readInt();
      (atn.states[stateNumber] as DecisionState).nonGreedy = true;
    }

    final numPrecedenceStates = readInt();
    for (var i = 0; i < numPrecedenceStates; i++) {
      final stateNumber = readInt();
      (atn.states[stateNumber] as RuleStartState).isLeftRecursiveRule = true;
    }
  }

  void readRules(ATN atn) {
    final nrules = readInt();
    if (atn.grammarType == ATNType.LEXER) {
      atn.ruleToTokenType = <int>[];
    }

    for (var i = 0; i < nrules; i++) {
      final s = readInt();
      final startState = atn.states[s] as RuleStartState;
      atn.ruleToStartState.add(startState);
      if (atn.grammarType == ATNType.LEXER) {
        var tokenType = readInt();

        atn.ruleToTokenType.add(tokenType);
      }
    }

    atn.ruleToStopState = List<RuleStopState>.generate(
        nrules, (int index) => RuleStopState(index));

    for (var state in atn.states) {
      if (state is! RuleStopState) {
        continue;
      }
      atn.ruleToStopState[state.ruleIndex] = state;
      atn.ruleToStartState[state.ruleIndex].stopState = state;
    }
  }

  void readModes(ATN atn) {
    final nmodes = readInt();
    for (var i = 0; i < nmodes; i++) {
      final s = readInt();
      atn.modeToStartState.add(atn.states[s] as TokensStartState);
    }
  }

  void readSets(ATN atn, List<IntervalSet> sets) {
    final nsets = readInt();
    for (var i = 0; i < nsets; i++) {
      final nintervals = readInt();
      final set = IntervalSet();
      sets.add(set);

      final containsEof = readInt() != 0;
      if (containsEof) {
        set.addOne(-1);
      }

      for (var j = 0; j < nintervals; j++) {
        int a = readInt();
        int b = readInt();
        set.addRange(a, b);
      }
    }
  }

  void readEdges(ATN atn, sets) {
    final nedges = readInt();
    for (var i = 0; i < nedges; i++) {
      final src = readInt();
      final trg = readInt();
      final ttype = TransitionType.values[readInt()];
      final arg1 = readInt();
      final arg2 = readInt();
      final arg3 = readInt();
      final trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
//			System.out.println("EDGE "+trans.getClass().getSimpleName()+" "+
//							   src+"->"+trg+
//					   " "+Transition.serializationNames[ttype]+
//					   " "+arg1+","+arg2+","+arg3);
      final srcState = atn.states[src]!;
      srcState.addTransition(trans);
    }

    // edges for rule stop states can be derived, so they aren't serialized
    for (var state in atn.states) {
      if (state == null) {
        continue;
      }
      for (var i = 0; i < state.numberOfTransitions; i++) {
        final t = state.transition(i);
        if (t is RuleTransition) {
          final ruleTransition = t;
          var outermostPrecedenceReturn = -1;
          if (atn.ruleToStartState[ruleTransition.target.ruleIndex]
              .isLeftRecursiveRule) {
            if (ruleTransition.precedence == 0) {
              outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
            }
          }

          final returnTransition = EpsilonTransition(
              ruleTransition.followState, outermostPrecedenceReturn);
          atn.ruleToStopState[ruleTransition.target.ruleIndex]
              .addTransition(returnTransition);
        }
      }
    }

    for (var state in atn.states) {
      if (state is BlockStartState) {
        // we need to know the end state to set its start state
        if (state.endState == null) {
          throw StateError('');
        }

        // block end states can only be associated to a single block start state
        if (state.endState!.startState != null) {
          throw StateError('');
        }

        state.endState!.startState = state;
      }

      if (state is PlusLoopbackState) {
        final loopbackState = state;
        for (var i = 0; i < loopbackState.numberOfTransitions; i++) {
          final target = loopbackState.transition(i).target;
          if (target is PlusBlockStartState) {
            target.loopBackState = loopbackState;
          }
        }
      } else if (state is StarLoopbackState) {
        final loopbackState = state;
        for (var i = 0; i < loopbackState.numberOfTransitions; i++) {
          final target = loopbackState.transition(i).target;
          if (target is StarLoopEntryState) {
            target.loopBackState = loopbackState;
          }
        }
      }
    }
  }

  void readDecisions(ATN atn) {
    final ndecisions = readInt();
    for (var i = 1; i <= ndecisions; i++) {
      final s = readInt();
      final decState = atn.states[s] as DecisionState;
      atn.decisionToState.add(decState);
      decState.decision = i - 1;
    }
  }

  void readLexerActions(ATN atn) {
    if (atn.grammarType == ATNType.LEXER) {
      atn.lexerActions = List<LexerAction>.generate(readInt(), (index) {
        final actionType = LexerActionType.values[readInt()];
        var data1 = readInt();
        var data2 = readInt();
        final lexerAction = lexerActionFactory(actionType, data1, data2);

        return lexerAction;
      });
    }
  }

  void generateRuleBypassTransitions(ATN atn) {
    final length = atn.ruleToStartState.length;
    atn.ruleToTokenType =
        List<int>.generate(length, (index) => atn.maxTokenType + index + 1);

    for (var i = 0; i < atn.ruleToStartState.length; i++) {
      generateRuleBypassTransition(atn, i);
    }
  }

  void generateRuleBypassTransition(ATN atn, int idx) {
    final bypassStart = BasicBlockStartState(idx);
    atn.addState(bypassStart);

    final bypassStop = BlockEndState(idx);
    atn.addState(bypassStop);

    bypassStart.endState = bypassStop;
    atn.defineDecisionState(bypassStart);

    bypassStop.startState = bypassStart;

    ATNState? endState;
    Transition? excludeTransition;
    if (atn.ruleToStartState[idx].isLeftRecursiveRule) {
      // wrap from the beginning of the rule to the StarLoopEntryState
      endState = null;
      for (var state in atn.states) {
        if (state == null) {
          continue;
        }
        if (state.ruleIndex != idx) {
          continue;
        }

        if (state is! StarLoopEntryState) {
          continue;
        }

        final maybeLoopEndState =
            state.transition(state.numberOfTransitions - 1).target;
        if (maybeLoopEndState is! LoopEndState) {
          continue;
        }

        if (maybeLoopEndState.epsilonOnlyTransitions &&
            maybeLoopEndState.transition(0).target is RuleStopState) {
          endState = state;
          break;
        }
      }

      if (endState == null) {
        throw UnsupportedError(
          "Couldn't identify final state of the precedence rule prefix section.",
        );
      }

      excludeTransition =
          (endState as StarLoopEntryState).loopBackState!.transition(0);
    } else {
      endState = atn.ruleToStopState[idx];
    }

    // all non-excluded transitions that currently target end state need to target blockEnd instead
    for (var state in atn.states) {
      if (state == null) {
        continue;
      }
      for (var transition in state.transitions) {
        if (transition == excludeTransition) {
          continue;
        }

        if (transition.target == endState) {
          transition.target = bypassStop;
        }
      }
    }

    // all transitions leaving the rule start state need to leave blockStart instead
    while (atn.ruleToStartState[idx].numberOfTransitions > 0) {
      final transition = atn.ruleToStartState[idx]
          .removeTransition(atn.ruleToStartState[idx].numberOfTransitions - 1);
      bypassStart.addTransition(transition);
    }

    // link the new states
    atn.ruleToStartState[idx].addTransition(EpsilonTransition(bypassStart));
    bypassStop.addTransition(EpsilonTransition(endState));

    ATNState matchState = BasicState(idx);
    atn.addState(matchState);

    matchState.addTransition(AtomTransition(
      bypassStop,
      atn.ruleToTokenType[idx],
    ));
    bypassStart.addTransition(EpsilonTransition(matchState));
  }

  /// Analyze the [StarLoopEntryState] states in the specified ATN to set
  /// the {@link StarLoopEntryState#isPrecedenceDecision} field to the
  /// correct value.
  ///
  /// @param atn The ATN.
  void markPrecedenceDecisions(ATN atn) {
    for (var state in atn.states) {
      if (state is StarLoopEntryState) {
        /* We analyze the ATN to determine if this ATN decision state is the
			 * decision for the closure block that determines whether a
			 * precedence rule should continue or complete.
			 */
        if (atn.ruleToStartState[state.ruleIndex].isLeftRecursiveRule) {
          final maybeLoopEndState =
              state.transition(state.numberOfTransitions - 1).target;
          if (maybeLoopEndState is LoopEndState) {
            if (maybeLoopEndState.epsilonOnlyTransitions &&
                maybeLoopEndState.transition(0).target is RuleStopState) {
              state.isPrecedenceDecision = true;
            }
          }
        }
      }
    }
  }

  void verifyATN(ATN atn) {
    // verify assumptions
    for (var state in atn.states) {
      if (state == null) {
        continue;
      }

      checkCondition(
          state.onlyHasEpsilonTransitions() || state.numberOfTransitions <= 1);

      if (state is PlusBlockStartState) {
        checkCondition(state.loopBackState != null);
      }

      if (state is StarLoopEntryState) {
        final starLoopEntryState = state;
        checkCondition(starLoopEntryState.loopBackState != null);
        checkCondition(starLoopEntryState.numberOfTransitions == 2);

        if (starLoopEntryState.transition(0).target is StarBlockStartState) {
          checkCondition(
              starLoopEntryState.transition(1).target is LoopEndState);
          checkCondition(!starLoopEntryState.nonGreedy);
        } else if (starLoopEntryState.transition(0).target is LoopEndState) {
          checkCondition(
              starLoopEntryState.transition(1).target is StarBlockStartState);
          checkCondition(starLoopEntryState.nonGreedy);
        } else {
          throw StateError('');
        }
      }

      if (state is StarLoopbackState) {
        checkCondition(state.numberOfTransitions == 1);
        checkCondition(state.transition(0).target is StarLoopEntryState);
      }

      if (state is LoopEndState) {
        checkCondition(state.loopBackState != null);
      }

      if (state is RuleStartState) {
        checkCondition(state.stopState != null);
      }

      if (state is BlockStartState) {
        checkCondition(state.endState != null);
      }

      if (state is BlockEndState) {
        checkCondition(state.startState != null);
      }

      if (state is DecisionState) {
        final decisionState = state;
        checkCondition(decisionState.numberOfTransitions <= 1 ||
            decisionState.decision >= 0);
      } else {
        checkCondition(
            state.numberOfTransitions <= 1 || state is RuleStopState);
      }
    }
  }

  void checkCondition(bool condition, [String message = '']) {
    if (!condition) {
      throw StateError(message);
    }
  }

  int readInt() {
    return data[pos++];
  }

  Transition edgeFactory(
    ATN atn,
    TransitionType type,
    int src,
    int trg,
    int arg1,
    int arg2,
    int arg3,
    List<IntervalSet> sets,
  ) {
    final target = atn.states[trg]!;
    switch (type) {
      case TransitionType.EPSILON:
        return EpsilonTransition(target);
      case TransitionType.RANGE:
        return arg3 != 0
            ? RangeTransition(target, Token.EOF, arg2)
            : RangeTransition(target, arg1, arg2);
      case TransitionType.RULE:
        final rt = RuleTransition(
            atn.states[arg1] as RuleStartState, arg2, arg3, target);
        return rt;
      case TransitionType.PREDICATE:
        final pt = PredicateTransition(target, arg1, arg2, arg3 != 0);
        return pt;
      case TransitionType.PRECEDENCE:
        return PrecedencePredicateTransition(target, arg1);
      case TransitionType.ATOM:
        return arg3 != 0
            ? AtomTransition(target, Token.EOF)
            : AtomTransition(target, arg1);
      case TransitionType.ACTION:
        final a = ActionTransition(target, arg1, arg2, arg3 != 0);
        return a;
      case TransitionType.SET:
        return SetTransition(target, sets[arg1]);
      case TransitionType.NOT_SET:
        return NotSetTransition(target, sets[arg1]);
      case TransitionType.WILDCARD:
        return WildcardTransition(target);
      case TransitionType.INVALID:
        throw ArgumentError.value(type, 'transition type', 'not valid.');
      default:
        throw ArgumentError.value(type, 'transition type', 'not valid.');
    }
  }

  ATNState? stateFactory(StateType type, int ruleIndex) {
    ATNState s;
    switch (type) {
      case StateType.INVALID_TYPE:
        return null;
      case StateType.BASIC:
        s = BasicState(ruleIndex);
        break;
      case StateType.RULE_START:
        s = RuleStartState(ruleIndex);
        break;
      case StateType.BLOCK_START:
        s = BasicBlockStartState(ruleIndex);
        break;
      case StateType.PLUS_BLOCK_START:
        s = PlusBlockStartState(ruleIndex);
        break;
      case StateType.STAR_BLOCK_START:
        s = StarBlockStartState(ruleIndex);
        break;
      case StateType.TOKEN_START:
        s = TokensStartState(ruleIndex);
        break;
      case StateType.RULE_STOP:
        s = RuleStopState(ruleIndex);
        break;
      case StateType.BLOCK_END:
        s = BlockEndState(ruleIndex);
        break;
      case StateType.STAR_LOOP_BACK:
        s = StarLoopbackState(ruleIndex);
        break;
      case StateType.STAR_LOOP_ENTRY:
        s = StarLoopEntryState(ruleIndex);
        break;
      case StateType.PLUS_LOOP_BACK:
        s = PlusLoopbackState(ruleIndex);
        break;
      case StateType.LOOP_END:
        s = LoopEndState(ruleIndex);
        break;
      default:
        throw ArgumentError.value(type, 'state type', 'not valid.');
    }

    return s;
  }

  LexerAction lexerActionFactory(LexerActionType type, int data1, int data2) {
    switch (type) {
      case LexerActionType.CHANNEL:
        return LexerChannelAction(data1);

      case LexerActionType.CUSTOM:
        return LexerCustomAction(data1, data2);

      case LexerActionType.MODE:
        return LexerModeAction(data1);

      case LexerActionType.MORE:
        return LexerMoreAction.INSTANCE;

      case LexerActionType.POP_MODE:
        return LexerPopModeAction.INSTANCE;

      case LexerActionType.PUSH_MODE:
        return LexerPushModeAction(data1);

      case LexerActionType.SKIP:
        return LexerSkipAction.INSTANCE;

      case LexerActionType.TYPE:
        return LexerTypeAction(data1);
      default:
        throw ArgumentError.value(type, 'lexer action type', 'not valid.');
    }
  }
}
