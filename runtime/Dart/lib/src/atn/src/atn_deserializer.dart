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
  static final ATNDeserializationOptions defaultOptions =
      ATNDeserializationOptions()..makeReadOnly();

  bool readOnly;
  bool verifyATN;
  bool generateRuleBypassTransitions;

  ATNDeserializationOptions([ATNDeserializationOptions options]) {
    if (options == null) {
      verifyATN = true;
      generateRuleBypassTransitions = false;
    } else {
      verifyATN = options.verifyATN;
      generateRuleBypassTransitions =
          options.generateRuleBypassTransitions;
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
  /// This value should never change. Updates following this version are
  /// reflected as change in the unique ID SERIALIZED_UUID.
  static final SERIALIZED_VERSION = 3;

  /** WARNING: DO NOT MERGE THESE LINES. If UUIDs differ during a merge,
   * resolve the conflict by generating a new ID!
   */
  /// This is the earliest supported serialized UUID.
  static final BASE_SERIALIZED_UUID = '33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3';

  /// This UUID indicates an extension of {@link BASE_SERIALIZED_UUID} for the
  /// addition of precedence predicates.
  static final ADDED_PRECEDENCE_TRANSITIONS =
      '1DA0C57D-6C06-438A-9B27-10BCB3CE0F61';

  /// This UUID indicates an extension of {@link #ADDED_PRECEDENCE_TRANSITIONS}
  /// for the addition of lexer actions encoded as a sequence of
  /// [LexerAction] instances.
  static final ADDED_LEXER_ACTIONS = 'AADB8D7E-AEEF-4415-AD2B-8204D6CF042E';

  /// This UUID indicates the serialized ATN contains two sets of
  /// IntervalSets, where the second set's values are encoded as
  /// 32-bit integers to support the full Unicode SMP range up to U+10FFFF.
  static final ADDED_UNICODE_SMP = '59627784-3BE5-417A-B9EB-8131A7286089';

  /// This list contains all of the currently supported UUIDs, ordered by when
  /// the feature first appeared in this branch.
  static final SUPPORTED_UUIDS = [
    BASE_SERIALIZED_UUID,
    ADDED_PRECEDENCE_TRANSITIONS,
    ADDED_LEXER_ACTIONS,
    ADDED_UNICODE_SMP
  ];

  /// This is the current serialized UUID.
  static final SERIALIZED_UUID = ADDED_UNICODE_SMP;

  ATNDeserializationOptions deserializationOptions;
  List<int> data;
  var pos;
  String uuid;

  ATNDeserializer([options]) {
    deserializationOptions =
        options ?? ATNDeserializationOptions.defaultOptions;
  }

  /// Determines if a particular serialized representation of an ATN supports
  /// a particular feature, identified by the [UUID] used for serializing
  /// the ATN at the time the feature was first introduced.
  ///
  /// @param feature The [UUID] marking the first time the feature was
  /// supported in the serialized ATN.
  /// @param actualUuid The [UUID] of the actual serialized ATN which is
  /// currently being deserialized.
  /// @return [true] if the [actualUuid] value represents a
  /// serialized ATN at or after the feature identified by [feature] was
  /// introduced; otherwise, [false].
  bool isFeatureSupported(feature, actualUuid) {
    final idx1 = SUPPORTED_UUIDS.indexOf(feature);
    if (idx1 < 0) {
      return false;
    }
    final idx2 = SUPPORTED_UUIDS.indexOf(actualUuid);
    return idx2 >= idx1;
  }

  ATN deserialize(List<int> data) {
    reset(data);
    checkVersion();
    checkUUID();
    final atn = readATN();
    readStates(atn);
    readRules(atn);
    readModes(atn);
    final sets = <IntervalSet>[];
    // First, deserialize sets with 16-bit arguments <= U+FFFF.
    readSets(atn, sets, () => readInt());
    // Next, if the ATN was serialized with the Unicode SMP feature,
    // deserialize sets with 32-bit arguments <= U+10FFFF.
    if (isFeatureSupported(ADDED_UNICODE_SMP, uuid)) {
      readSets(atn, sets, () => readInt32());
    }
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

  /// Each char value in data is shifted by +2 at the entry to this method.
  /// This is an encoding optimization targeting the serialized values 0
  /// and -1 (serialized to 0xFFFF), each of which are very common in the
  /// serialized form of the ATN. In the modified UTF-8 that Java uses for
  /// compiled string literals, these two character values have multi-byte
  /// forms. By shifting each value by +2, they become characters 2 and 1
  /// prior to writing the string, each of which have single-byte
  /// representations. Since the shift occurs in the tool during ATN
  /// serialization, each target is responsible for adjusting the values
  /// during deserialization.
  ///
  /// As a special case, note that the first element of data is not
  /// adjusted because it contains the major version number of the
  /// serialized ATN, which was fixed at 3 at the time the value shifting
  /// was implemented.
  void reset(List<int> data) {
    final adjust = (int c) {
      final v = c;
      return v > 1 ? v - 2 : v + 65534;
    };
    final temp = data.map(adjust).toList();
    // don't adjust the first value since that's the version number
    temp[0] = data[0];
    this.data = temp;
    pos = 0;
  }

  void checkVersion() {
    final version = readInt();
    if (version != SERIALIZED_VERSION) {
      throw ('Could not deserialize ATN with version $version (expected $SERIALIZED_VERSION).');
    }
  }

  void checkUUID() {
    final uuid = readUUID();
    if (!SUPPORTED_UUIDS.contains(uuid)) {
      throw ('Could not deserialize ATN with UUID: $uuid (expected $SERIALIZED_UUID or a legacy UUID).');
    }
    this.uuid = uuid;
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
      if (ruleIndex == 0xFFFF) {
        ruleIndex = -1;
      }

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
    if (isFeatureSupported(ADDED_PRECEDENCE_TRANSITIONS, uuid)) {
      final numPrecedenceStates = readInt();
      for (var i = 0; i < numPrecedenceStates; i++) {
        final stateNumber = readInt();
        (atn.states[stateNumber] as RuleStartState).isLeftRecursiveRule = true;
      }
    }
  }

  void readRules(ATN atn) {
    final nrules = readInt();
    if (atn.grammarType == ATNType.LEXER) {
      atn.ruleToTokenType = List<int>(nrules);
    }

    atn.ruleToStartState = List<RuleStartState>(nrules);
    for (var i = 0; i < nrules; i++) {
      final s = readInt();
      RuleStartState startState = atn.states[s];
      atn.ruleToStartState[i] = startState;
      if (atn.grammarType == ATNType.LEXER) {
        var tokenType = readInt();
        if (tokenType == 0xFFFF) {
          tokenType = Token.EOF;
        }

        atn.ruleToTokenType[i] = tokenType;

        if (!isFeatureSupported(ADDED_LEXER_ACTIONS, uuid)) {
          // this piece of unused metadata was serialized prior to the
          // addition of LexerAction
          final actionIndexIgnored = readInt();
        }
      }
    }

    atn.ruleToStopState = List<RuleStopState>(nrules);
    for (var state in atn.states) {
      if (!(state is RuleStopState)) {
        continue;
      }

      RuleStopState stopState = state;
      atn.ruleToStopState[state.ruleIndex] = stopState;
      atn.ruleToStartState[state.ruleIndex].stopState = stopState;
    }
  }

  void readModes(ATN atn) {
    final nmodes = readInt();
    for (var i = 0; i < nmodes; i++) {
      final s = readInt();
      atn.modeToStartState.add(atn.states[s] as TokensStartState);
    }
  }

  void readSets(ATN atn, List<IntervalSet> sets, readUnicode) {
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
        int a = readUnicode();
        int b = readUnicode();
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
      final trans =
          edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
//			System.out.println("EDGE "+trans.getClass().getSimpleName()+" "+
//							   src+"->"+trg+
//					   " "+Transition.serializationNames[ttype]+
//					   " "+arg1+","+arg2+","+arg3);
      final srcState = atn.states[src];
      srcState.addTransition(trans);
    }

    // edges for rule stop states can be derived, so they aren't serialized
    for (var state in atn.states) {
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
        if (state.endState.startState != null) {
          throw StateError('');
        }

        state.endState.startState = state;
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
      DecisionState decState = atn.states[s];
      atn.decisionToState.add(decState);
      decState.decision = i - 1;
    }
  }

  void readLexerActions(ATN atn) {
    if (atn.grammarType == ATNType.LEXER) {
      if (isFeatureSupported(ADDED_LEXER_ACTIONS, uuid)) {
        atn.lexerActions = List<LexerAction>(readInt());
        for (var i = 0; i < atn.lexerActions.length; i++) {
          final actionType = LexerActionType.values[readInt()];
          var data1 = readInt();
          if (data1 == 0xFFFF) {
            data1 = -1;
          }

          var data2 = readInt();
          if (data2 == 0xFFFF) {
            data2 = -1;
          }
          final lexerAction =
              lexerActionFactory(actionType, data1, data2);

          atn.lexerActions[i] = lexerAction;
        }
      } else {
        // for compatibility with older serialized ATNs, convert the old
        // serialized action index for action transitions to the new
        // form, which is the index of a LexerCustomAction
        final legacyLexerActions = <LexerAction>[];
        for (var state in atn.states) {
          for (var i = 0; i < state.numberOfTransitions; i++) {
            final transition = state.transition(i);
            if (transition is ActionTransition) {
              final ruleIndex = transition.ruleIndex;
              final actionIndex = transition.actionIndex;
              final lexerAction =
                  LexerCustomAction(ruleIndex, actionIndex);
              state.setTransition(
                  i,
                  ActionTransition(transition.target, ruleIndex,
                      legacyLexerActions.length, false));
              legacyLexerActions.add(lexerAction);
            }
          }
        }

        atn.lexerActions = legacyLexerActions;
      }
    }
  }

  void generateRuleBypassTransitions(ATN atn) {
    for (var i = 0; i < atn.ruleToStartState.length; i++) {
      atn.ruleToTokenType[i] = atn.maxTokenType + i + 1;
    }
    for (var i = 0; i < atn.ruleToStartState.length; i++) {
      generateRuleBypassTransition(atn, i);
    }
  }

  void generateRuleBypassTransition(ATN atn, int idx) {
    final bypassStart = BasicBlockStartState();
    bypassStart.ruleIndex = idx;
    atn.addState(bypassStart);

    final bypassStop = BlockEndState();
    bypassStop.ruleIndex = idx;
    atn.addState(bypassStop);

    bypassStart.endState = bypassStop;
    atn.defineDecisionState(bypassStart);

    bypassStop.startState = bypassStart;

    ATNState endState;
    Transition excludeTransition;
    if (atn.ruleToStartState[idx].isLeftRecursiveRule) {
      // wrap from the beginning of the rule to the StarLoopEntryState
      endState = null;
      for (var state in atn.states) {
        if (state.ruleIndex != idx) {
          continue;
        }

        if (!(state is StarLoopEntryState)) {
          continue;
        }

        final maybeLoopEndState =
            state.transition(state.numberOfTransitions - 1).target;
        if (!(maybeLoopEndState is LoopEndState)) {
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
            "Couldn't identify final state of the precedence rule prefix section.");
      }

      excludeTransition =
          (endState as StarLoopEntryState).loopBackState.transition(0);
    } else {
      endState = atn.ruleToStopState[idx];
    }

    // all non-excluded transitions that currently target end state need to target blockEnd instead
    for (var state in atn.states) {
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
      final transition = atn.ruleToStartState[idx].removeTransition(
          atn.ruleToStartState[idx].numberOfTransitions - 1);
      bypassStart.addTransition(transition);
    }

    // link the new states
    atn.ruleToStartState[idx].addTransition(EpsilonTransition(bypassStart));
    bypassStop.addTransition(EpsilonTransition(endState));

    ATNState matchState = BasicState();
    atn.addState(matchState);
    matchState.addTransition(
        AtomTransition(bypassStop, atn.ruleToTokenType[idx]));
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

      checkCondition(state.onlyHasEpsilonTransitions() ||
          state.numberOfTransitions <= 1);

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

  int readInt32() {
    final low = readInt();
    final high = readInt();
    return low | (high << 16);
  }

  int readLong() {
    final low = readInt32();
    final high = readInt32();
    return (low & 0x00000000FFFFFFFF) | (high << 32);
  }

  static final byteToHex  = List.generate(256, (i) => i.toRadixString(16).padLeft(2, '0').toUpperCase());

  String readUUID() {
    final bb = List<int>(16);
    for (var i = 7; i >= 0; i--) {
      final int = readInt();
      /* jshint bitwise: false */
      bb[(2 * i) + 1] = int & 0xFF;
      bb[2 * i] = (int >> 8) & 0xFF;
    }
    return byteToHex[bb[0]] + byteToHex[bb[1]] +
        byteToHex[bb[2]] + byteToHex[bb[3]] + '-' +
        byteToHex[bb[4]] + byteToHex[bb[5]] + '-' +
        byteToHex[bb[6]] + byteToHex[bb[7]] + '-' +
        byteToHex[bb[8]] + byteToHex[bb[9]] + '-' +
        byteToHex[bb[10]] + byteToHex[bb[11]] +
        byteToHex[bb[12]] + byteToHex[bb[13]] +
        byteToHex[bb[14]] + byteToHex[bb[15]];
  }

  Transition edgeFactory(ATN atn, TransitionType type, int src, int trg,
      int arg1, int arg2, int arg3, List<IntervalSet> sets) {
    final target = atn.states[trg];
    switch (type) {
      case TransitionType.EPSILON:
        return EpsilonTransition(target);
      case TransitionType.RANGE:
        return arg3 != 0
            ? RangeTransition(target, Token.EOF, arg2)
            : RangeTransition(target, arg1, arg2);
      case TransitionType.RULE:
        final rt =
            RuleTransition(atn.states[arg1], arg2, arg3, target);
        return rt;
      case TransitionType.PREDICATE:
        final pt =
            PredicateTransition(target, arg1, arg2, arg3 != 0);
        return pt;
      case TransitionType.PRECEDENCE:
        return PrecedencePredicateTransition(target, arg1);
      case TransitionType.ATOM:
        return arg3 != 0
            ? AtomTransition(target, Token.EOF)
            : AtomTransition(target, arg1);
      case TransitionType.ACTION:
        final a =
            ActionTransition(target, arg1, arg2, arg3 != 0);
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

  ATNState stateFactory(StateType type, int ruleIndex) {
    ATNState s;
    switch (type) {
      case StateType.INVALID_TYPE:
        return null;
      case StateType.BASIC:
        s = BasicState();
        break;
      case StateType.RULE_START:
        s = RuleStartState();
        break;
      case StateType.BLOCK_START:
        s = BasicBlockStartState();
        break;
      case StateType.PLUS_BLOCK_START:
        s = PlusBlockStartState();
        break;
      case StateType.STAR_BLOCK_START:
        s = StarBlockStartState();
        break;
      case StateType.TOKEN_START:
        s = TokensStartState();
        break;
      case StateType.RULE_STOP:
        s = RuleStopState();
        break;
      case StateType.BLOCK_END:
        s = BlockEndState();
        break;
      case StateType.STAR_LOOP_BACK:
        s = StarLoopbackState();
        break;
      case StateType.STAR_LOOP_ENTRY:
        s = StarLoopEntryState();
        break;
      case StateType.PLUS_LOOP_BACK:
        s = PlusLoopbackState();
        break;
      case StateType.LOOP_END:
        s = LoopEndState();
        break;
      default:
        throw ArgumentError.value(type, 'state type', 'not valid.');
    }

    s.ruleIndex = ruleIndex;
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
