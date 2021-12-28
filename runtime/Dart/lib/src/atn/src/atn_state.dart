/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:developer';

import 'package:logging/logging.dart';

import '../../interval_set.dart';
import 'atn.dart';
import 'transition.dart';

var INITIAL_NUM_TRANSITIONS = 4;

enum StateType {
  INVALID_TYPE,
  BASIC,
  RULE_START,
  BLOCK_START,
  PLUS_BLOCK_START,
  STAR_BLOCK_START,
  TOKEN_START,
  RULE_STOP,
  BLOCK_END,
  STAR_LOOP_BACK,
  STAR_LOOP_ENTRY,
  PLUS_LOOP_BACK,
  LOOP_END,
}

/// The following images show the relation of states and
/// {@link ATNState#transitions} for various grammar constructs.
///
/// <ul>
///
/// <li>Solid edges marked with an &#0949; indicate a required
/// [EpsilonTransition].</li>
///
/// <li>Dashed edges indicate locations where any transition derived from
/// [Transition] might appear.</li>
///
/// <li>Dashed nodes are place holders for either a sequence of linked
/// [BasicState] states or the inclusion of a block representing a nested
/// construct in one of the forms below.</li>
///
/// <li>Nodes showing multiple outgoing alternatives with a {@code ...} support
/// any number of alternatives (one or more). Nodes without the {@code ...} only
/// support the exact number of alternatives shown in the diagram.</li>
///
/// </ul>
///
/// <h2>Basic Blocks</h2>
///
/// <h3>Rule</h3>
///
/// <embed src="images/Rule.svg" type="image/svg+xml"/>
///
/// <h3>Block of 1 or more alternatives</h3>
///
/// <embed src="images/Block.svg" type="image/svg+xml"/>
///
/// <h2>Greedy Loops</h2>
///
/// <h3>Greedy Closure: {@code (...)*}</h3>
///
/// <embed src="images/ClosureGreedy.svg" type="image/svg+xml"/>
///
/// <h3>Greedy Positive Closure: {@code (...)+}</h3>
///
/// <embed src="images/PositiveClosureGreedy.svg" type="image/svg+xml"/>
///
/// <h3>Greedy Optional: {@code (...)?}</h3>
///
/// <embed src="images/OptionalGreedy.svg" type="image/svg+xml"/>
///
/// <h2>Non-Greedy Loops</h2>
///
/// <h3>Non-Greedy Closure: {@code (...)*?}</h3>
///
/// <embed src="images/ClosureNonGreedy.svg" type="image/svg+xml"/>
///
/// <h3>Non-Greedy Positive Closure: {@code (...)+?}</h3>
///
/// <embed src="images/PositiveClosureNonGreedy.svg" type="image/svg+xml"/>
///
/// <h3>Non-Greedy Optional: {@code (...)??}</h3>
///
/// <embed src="images/OptionalNonGreedy.svg" type="image/svg+xml"/>
abstract class ATNState {
  static final int INITIAL_NUM_TRANSITIONS = 4;

  static final int INVALID_STATE_NUMBER = -1;

  /// Which ATN are we in? */
  late ATN atn;

  int stateNumber = INVALID_STATE_NUMBER;

  int ruleIndex; // at runtime, we don't have Rule objects

  bool epsilonOnlyTransitions = false;

  /// Track the transitions emanating from this ATN state. */
  List<Transition> transitions = [];

  /// Used to cache lookahead during parsing, not used during construction */
  IntervalSet? nextTokenWithinRule;

  ATNState(this.ruleIndex);

  @override
  int get hashCode {
    return stateNumber;
  }

  @override
  bool operator ==(Object o) {
    // are these states same object?
    if (o is ATNState) return stateNumber == o.stateNumber;
    return false;
  }

  bool isNonGreedyExitState() {
    return false;
  }

  @override
  String toString() {
    return stateNumber.toString();
  }

  int get numberOfTransitions {
    return transitions.length;
  }

  void addTransition(Transition e) {
    addTransitionAt(transitions.length, e);
  }

  void addTransitionAt(int index, Transition e) {
    if (transitions.isEmpty) {
      epsilonOnlyTransitions = e.isEpsilon;
    } else if (epsilonOnlyTransitions != e.isEpsilon) {
      log('ATN state $stateNumber has both epsilon and non-epsilon transitions.\n',
          level: Level.SEVERE.value);
      epsilonOnlyTransitions = false;
    }

    var alreadyPresent = false;
    for (var t in transitions) {
      if (t.target.stateNumber == e.target.stateNumber) {
        if (t.label != null && e.label != null && t.label == e.label) {
//					System.err.println("Repeated transition upon "+e.label()+" from "+stateNumber+"->"+t.target.stateNumber);
          alreadyPresent = true;
          break;
        } else if (t.isEpsilon && e.isEpsilon) {
//					System.err.println("Repeated epsilon transition from "+stateNumber+"->"+t.target.stateNumber);
          alreadyPresent = true;
          break;
        }
      }
    }
    if (!alreadyPresent) {
      transitions.insert(index, e);
    }
  }

  Transition transition(int i) {
    return transitions[i];
  }

  void setTransition(int i, Transition e) {
    transitions[i] = e;
  }

  Transition removeTransition(int index) {
    return transitions.removeAt(index);
  }

  StateType get stateType;

  bool onlyHasEpsilonTransitions() => epsilonOnlyTransitions;

  void setRuleIndex(int ruleIndex) {
    this.ruleIndex = ruleIndex;
  }
}

class BasicState extends ATNState {
  BasicState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.BASIC;
}

class RuleStartState extends ATNState {
  RuleStopState? stopState;
  bool isLeftRecursiveRule = false;

  RuleStartState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.RULE_START;
}

abstract class DecisionState extends ATNState {
  int decision = 0;
  bool nonGreedy = false;

  DecisionState(int ruleIndex) : super(ruleIndex);
}

//  The start of a regular {@code (...)} block.
abstract class BlockStartState extends DecisionState {
  BlockEndState? endState;

  BlockStartState(int ruleIndex) : super(ruleIndex);
}

class BasicBlockStartState extends BlockStartState {
  BasicBlockStartState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.BLOCK_START;
}

/// Start of {@code (A|B|...)+} loop. Technically a decision state, but
///  we don't use for code generation; somebody might need it, so I'm defining
///  it for completeness. In reality, the [PlusLoopbackState] node is the
///  real decision-making note for {@code A+}.
class PlusBlockStartState extends BlockStartState {
  PlusLoopbackState? loopBackState;

  PlusBlockStartState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.PLUS_BLOCK_START;
}

/// The block that begins a closure loop.
class StarBlockStartState extends BlockStartState {
  StarBlockStartState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.STAR_BLOCK_START;
}

/// The Tokens rule start state linking to each lexer rule start state */
class TokensStartState extends DecisionState {
  TokensStartState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.TOKEN_START;
}

/// The last node in the ATN for a rule, unless that rule is the start symbol.
///  In that case, there is one transition to EOF. Later, we might encode
///  references to all calls to this rule to compute FOLLOW sets for
///  error handling.
class RuleStopState extends ATNState {
  RuleStopState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.RULE_STOP;
}

/// Terminal node of a simple {@code (a|b|c)} block.
class BlockEndState extends ATNState {
  BlockStartState? startState;

  BlockEndState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.BLOCK_END;
}

class StarLoopbackState extends ATNState {
  StarLoopbackState(int ruleIndex) : super(ruleIndex);

  StarLoopEntryState get loopEntryState {
    return transition(0).target as StarLoopEntryState;
  }

  @override
  StateType get stateType => StateType.STAR_LOOP_BACK;
}

class StarLoopEntryState extends DecisionState {
  StarLoopbackState? loopBackState;

  /// Indicates whether this state can benefit from a precedence DFA during SLL
  /// decision making.
  ///
  /// <p>This is a computed property that is calculated during ATN deserialization
  /// and stored for use in [ParserATNSimulator] and
  /// [ParserInterpreter].</p>
  ///
  /// @see DFA#isPrecedenceDfa()
  bool isPrecedenceDecision = false;

  StarLoopEntryState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.STAR_LOOP_ENTRY;
}

/// Decision state for {@code A+} and {@code (A|B)+}.  It has two transitions:
///  one to the loop back to start of the block and one to exit.
class PlusLoopbackState extends DecisionState {
  PlusLoopbackState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.PLUS_LOOP_BACK;
}

/// Mark the end of a * or + loop.
class LoopEndState extends ATNState {
  ATNState? loopBackState;

  LoopEndState(int ruleIndex) : super(ruleIndex);

  @override
  StateType get stateType => StateType.LOOP_END;
}
