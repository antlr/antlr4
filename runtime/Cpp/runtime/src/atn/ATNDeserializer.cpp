/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/ATNDeserializationOptions.h"

#include "atn/ATNType.h"
#include "atn/ATNState.h"
#include "atn/ATN.h"

#include "atn/LoopEndState.h"
#include "atn/DecisionState.h"
#include "atn/RuleStartState.h"
#include "atn/RuleStopState.h"
#include "atn/TokensStartState.h"
#include "atn/RuleTransition.h"
#include "atn/EpsilonTransition.h"
#include "atn/PlusLoopbackState.h"
#include "atn/PlusBlockStartState.h"
#include "atn/StarLoopbackState.h"
#include "atn/BasicBlockStartState.h"
#include "atn/BasicState.h"
#include "atn/BlockEndState.h"
#include "atn/StarLoopEntryState.h"

#include "atn/AtomTransition.h"
#include "atn/StarBlockStartState.h"
#include "atn/RangeTransition.h"
#include "atn/PredicateTransition.h"
#include "atn/PrecedencePredicateTransition.h"
#include "atn/ActionTransition.h"
#include "atn/SetTransition.h"
#include "atn/NotSetTransition.h"
#include "atn/WildcardTransition.h"
#include "Token.h"

#include "misc/IntervalSet.h"
#include "Exceptions.h"
#include "support/CPPUtils.h"
#include "support/Casts.h"

#include "atn/LexerCustomAction.h"
#include "atn/LexerChannelAction.h"
#include "atn/LexerModeAction.h"
#include "atn/LexerMoreAction.h"
#include "atn/LexerPopModeAction.h"
#include "atn/LexerPushModeAction.h"
#include "atn/LexerSkipAction.h"
#include "atn/LexerTypeAction.h"

#include "atn/ATNDeserializer.h"

#include <mutex>
#include <string>
#include <vector>

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlrcpp;

namespace {

uint32_t deserializeInt32(const std::vector<uint16_t>& data, size_t offset) {
  return static_cast<uint32_t>(data[offset]) | (static_cast<uint32_t>(data[offset + 1]) << 16);
}

ssize_t readUnicodeInt(const std::vector<uint16_t>& data, int& p) {
  return static_cast<ssize_t>(data[p++]);
}

ssize_t readUnicodeInt32(const std::vector<uint16_t>& data, int& p) {
  auto result = deserializeInt32(data, p);
  p += 2;
  return static_cast<ssize_t>(result);
}

// We templatize this on the function type so the optimizer can inline
// the 16- or 32-bit readUnicodeInt/readUnicodeInt32 as needed.
template <typename F>
void deserializeSets(
  const std::vector<uint16_t>& data,
  int& p,
  std::vector<misc::IntervalSet>& sets,
  F readUnicode) {
  size_t nsets = data[p++];
  sets.reserve(sets.size() + nsets);
  for (size_t i = 0; i < nsets; i++) {
    size_t nintervals = data[p++];
    misc::IntervalSet set;

    bool containsEof = data[p++] != 0;
    if (containsEof) {
      set.add(-1);
    }

    for (size_t j = 0; j < nintervals; j++) {
      auto a = readUnicode(data, p);
      auto b = readUnicode(data, p);
      set.add(a, b);
    }
    sets.push_back(set);
  }
}

}

ATNDeserializer::ATNDeserializer(): ATNDeserializer(ATNDeserializationOptions::getDefaultOptions()) {
}

ATNDeserializer::ATNDeserializer(const ATNDeserializationOptions& dso): _deserializationOptions(dso) {
}

ATNDeserializer::~ATNDeserializer() {
}
ATN ATNDeserializer::deserialize(const std::vector<uint16_t>& data) {
  int p = 0;
  int version = data[p++];
  if (version != SERIALIZED_VERSION) {
    std::string reason = "Could not deserialize ATN with version" + std::to_string(version) + "(expected " + std::to_string(SERIALIZED_VERSION) + ").";

    throw UnsupportedOperationException(reason);
  }

  ATNType grammarType = (ATNType)data[p++];
  size_t maxTokenType = data[p++];
  ATN atn(grammarType, maxTokenType);

  //
  // STATES
  //
  {
    std::vector<std::pair<LoopEndState*, size_t>> loopBackStateNumbers;
    std::vector<std::pair<BlockStartState*, size_t>> endStateNumbers;
    size_t nstates = data[p++];
    atn.states.reserve(nstates);
    loopBackStateNumbers.reserve(nstates);  // Reserve worst case size, its short lived.
    endStateNumbers.reserve(nstates);  // Reserve worst case size, its short lived.
    for (size_t i = 0; i < nstates; i++) {
      size_t stype = data[p++];
      // ignore bad type of states
      if (stype == ATNState::ATN_INVALID_TYPE) {
        atn.addState(nullptr);
        continue;
      }

      size_t ruleIndex = data[p++];
      if (ruleIndex == 0xFFFF) {
        ruleIndex = INVALID_INDEX;
      }

      ATNState *s = stateFactory(stype, ruleIndex);
      if (stype == ATNState::LOOP_END) { // special case
        int loopBackStateNumber = data[p++];
        loopBackStateNumbers.push_back({ downCast<LoopEndState*>(s),  loopBackStateNumber });
      } else if (is<BlockStartState*>(s)) {
        int endStateNumber = data[p++];
        endStateNumbers.push_back({ downCast<BlockStartState*>(s), endStateNumber });
      }
      atn.addState(s);
    }

    // delay the assignment of loop back and end states until we know all the state instances have been initialized
    for (auto &pair : loopBackStateNumbers) {
      pair.first->loopBackState = atn.states[pair.second];
    }

    for (auto &pair : endStateNumbers) {
      pair.first->endState = downCast<BlockEndState*>(atn.states[pair.second]);
    }
  }

  size_t numNonGreedyStates = data[p++];
  for (size_t i = 0; i < numNonGreedyStates; i++) {
    size_t stateNumber = data[p++];
    // The serialized ATN must be specifying the right states, so that the
    // cast below is correct.
    downCast<DecisionState*>(atn.states[stateNumber])->nonGreedy = true;
  }

  size_t numPrecedenceStates = data[p++];
  for (size_t i = 0; i < numPrecedenceStates; i++) {
    size_t stateNumber = data[p++];
    downCast<RuleStartState*>(atn.states[stateNumber])->isLeftRecursiveRule = true;
  }

  //
  // RULES
  //
  size_t nrules = data[p++];
  atn.ruleToStartState.reserve(nrules);
  for (size_t i = 0; i < nrules; i++) {
    size_t s = data[p++];
    // Also here, the serialized atn must ensure to point to the correct class type.
    RuleStartState *startState = downCast<RuleStartState*>(atn.states[s]);
    atn.ruleToStartState.push_back(startState);
    if (atn.grammarType == ATNType::LEXER) {
      size_t tokenType = data[p++];
      if (tokenType == 0xFFFF) {
        tokenType = Token::EOF;
      }

      atn.ruleToTokenType.push_back(tokenType);
    }
  }

  atn.ruleToStopState.resize(nrules);
  for (ATNState *state : atn.states) {
    if (!is<RuleStopState*>(state)) {
      continue;
    }

    RuleStopState *stopState = downCast<RuleStopState*>(state);
    atn.ruleToStopState[state->ruleIndex] = stopState;
    atn.ruleToStartState[state->ruleIndex]->stopState = stopState;
  }

  //
  // MODES
  //
  size_t nmodes = data[p++];
  atn.modeToStartState.reserve(nmodes);
  for (size_t i = 0; i < nmodes; i++) {
    size_t s = data[p++];
    atn.modeToStartState.push_back(downCast<TokensStartState*>(atn.states[s]));
  }

  //
  // SETS
  //
  {
    std::vector<misc::IntervalSet> sets;

    // First, deserialize sets with 16-bit arguments <= U+FFFF.
    deserializeSets(data, p, sets, readUnicodeInt);

    // Next, deserialize sets with 32-bit arguments <= U+10FFFF.
    deserializeSets(data, p, sets, readUnicodeInt32);

    sets.shrink_to_fit();

    //
    // EDGES
    //
    int nedges = data[p++];
    for (int i = 0; i < nedges; i++) {
      size_t src = data[p];
      size_t trg = data[p + 1];
      size_t ttype = data[p + 2];
      size_t arg1 = data[p + 3];
      size_t arg2 = data[p + 4];
      size_t arg3 = data[p + 5];
      ConstTransitionPtr trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
      ATNState *srcState = atn.states[src];
      srcState->addTransition(std::move(trans));
      p += 6;
    }
  }
  // edges for rule stop states can be derived, so they aren't serialized
  for (ATNState *state : atn.states) {
    for (size_t i = 0; i < state->transitions.size(); i++) {
      const Transition *t = state->transitions[i].get();
      if (!is<const RuleTransition*>(t)) {
        continue;
      }

      const RuleTransition *ruleTransition = downCast<const RuleTransition*>(t);
      size_t outermostPrecedenceReturn = INVALID_INDEX;
      if (atn.ruleToStartState[ruleTransition->target->ruleIndex]->isLeftRecursiveRule) {
        if (ruleTransition->precedence == 0) {
          outermostPrecedenceReturn = ruleTransition->target->ruleIndex;
        }
      }

      ConstTransitionPtr returnTransition = std::make_unique<EpsilonTransition>(ruleTransition->followState, outermostPrecedenceReturn);
      atn.ruleToStopState[ruleTransition->target->ruleIndex]->addTransition(std::move(returnTransition));
    }
  }

  for (ATNState *state : atn.states) {
    if (is<BlockStartState*>(state)) {
      BlockStartState *startState = downCast<BlockStartState*>(state);

      // we need to know the end state to set its start state
      if (startState->endState == nullptr) {
        throw IllegalStateException();
      }

      // block end states can only be associated to a single block start state
      if (startState->endState->startState != nullptr) {
        throw IllegalStateException();
      }

      startState->endState->startState = downCast<BlockStartState*>(state);
    }

    if (is<PlusLoopbackState*>(state)) {
      PlusLoopbackState *loopbackState = downCast<PlusLoopbackState*>(state);
      for (size_t i = 0; i < loopbackState->transitions.size(); i++) {
        ATNState *target = loopbackState->transitions[i]->target;
        if (is<PlusBlockStartState*>(target)) {
          (downCast<PlusBlockStartState*>(target))->loopBackState = loopbackState;
        }
      }
    } else if (is<StarLoopbackState*>(state)) {
      StarLoopbackState *loopbackState = downCast<StarLoopbackState*>(state);
      for (size_t i = 0; i < loopbackState->transitions.size(); i++) {
        ATNState *target = loopbackState->transitions[i]->target;
        if (is<StarLoopEntryState *>(target)) {
          downCast<StarLoopEntryState*>(target)->loopBackState = loopbackState;
        }
      }
    }
  }

  //
  // DECISIONS
  //
  size_t ndecisions = data[p++];
  atn.decisionToState.reserve(ndecisions);
  for (size_t i = 0; i < ndecisions; i++) {
    size_t s = data[p++];
    DecisionState *decState = downCast<DecisionState*>(atn.states[s]);
    if (decState == nullptr)
      throw IllegalStateException();

    atn.decisionToState.push_back(decState);
    decState->decision = static_cast<int>(i);
  }

  //
  // LEXER ACTIONS
  //
  if (atn.grammarType == ATNType::LEXER) {
    atn.lexerActions.resize(data[p++]);
    for (size_t i = 0; i < atn.lexerActions.size(); i++) {
      LexerActionType actionType = static_cast<LexerActionType>(data[p++]);
      int data1 = data[p++];
      if (data1 == 0xFFFF) {
        data1 = -1;
      }

      int data2 = data[p++];
      if (data2 == 0xFFFF) {
        data2 = -1;
      }

      atn.lexerActions[i] = lexerActionFactory(actionType, data1, data2);
    }
  }

  markPrecedenceDecisions(atn);

  if (_deserializationOptions.isVerifyATN()) {
    verifyATN(atn);
  }

  if (_deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType::PARSER) {
    atn.ruleToTokenType.resize(atn.ruleToStartState.size());
    for (size_t i = 0; i < atn.ruleToStartState.size(); i++) {
      atn.ruleToTokenType[i] = static_cast<int>(atn.maxTokenType + i + 1);
    }

    for (std::vector<RuleStartState*>::size_type i = 0; i < atn.ruleToStartState.size(); i++) {
      BasicBlockStartState *bypassStart = new BasicBlockStartState(); /* mem check: freed in ATN d-tor */
      bypassStart->ruleIndex = static_cast<int>(i);
      atn.addState(bypassStart);

      BlockEndState *bypassStop = new BlockEndState(); /* mem check: freed in ATN d-tor */
      bypassStop->ruleIndex = static_cast<int>(i);
      atn.addState(bypassStop);

      bypassStart->endState = bypassStop;
      atn.defineDecisionState(bypassStart);

      bypassStop->startState = bypassStart;

      ATNState *endState;
      const Transition *excludeTransition = nullptr;
      if (atn.ruleToStartState[i]->isLeftRecursiveRule) {
        // wrap from the beginning of the rule to the StarLoopEntryState
        endState = nullptr;
        for (ATNState *state : atn.states) {
          if (state->ruleIndex != i) {
            continue;
          }

          if (!is<StarLoopEntryState*>(state)) {
            continue;
          }

          ATNState *maybeLoopEndState = state->transitions[state->transitions.size() - 1]->target;
          if (!is<LoopEndState*>(maybeLoopEndState)) {
            continue;
          }

          if (maybeLoopEndState->epsilonOnlyTransitions && is<RuleStopState*>(maybeLoopEndState->transitions[0]->target)) {
            endState = state;
            break;
          }
        }

        if (endState == nullptr) {
          throw UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");

        }

        excludeTransition = (static_cast<StarLoopEntryState*>(endState))->loopBackState->transitions[0].get();
      } else {
        endState = atn.ruleToStopState[i];
      }

      // all non-excluded transitions that currently target end state need to target blockEnd instead
      for (ATNState *state : atn.states) {
        for (auto &transition : state->transitions) {
          if (transition.get() == excludeTransition) {
            continue;
          }

          if (transition->target == endState) {
            const_cast<Transition*>(transition.get())->target = bypassStop;
          }
        }
      }

      // all transitions leaving the rule start state need to leave blockStart instead
      while (atn.ruleToStartState[i]->transitions.size() > 0) {
        ConstTransitionPtr transition = atn.ruleToStartState[i]->removeTransition(atn.ruleToStartState[i]->transitions.size() - 1);
        bypassStart->addTransition(std::move(transition));
      }

      // link the new states
      atn.ruleToStartState[i]->addTransition(std::make_unique<EpsilonTransition>(bypassStart));
      bypassStop->addTransition(std::make_unique<EpsilonTransition>(endState));

      ATNState *matchState = new BasicState(); /* mem check: freed in ATN d-tor */
      atn.addState(matchState);
      matchState->addTransition(std::make_unique<AtomTransition>(bypassStop, atn.ruleToTokenType[i]));
      bypassStart->addTransition(std::make_unique<EpsilonTransition>(matchState));
    }

    if (_deserializationOptions.isVerifyATN()) {
      // reverify after modification
      verifyATN(atn);
    }
  }

  return atn;
}

/**
 * Analyze the {@link StarLoopEntryState} states in the specified ATN to set
 * the {@link StarLoopEntryState#isPrecedenceDecision} field to the
 * correct value.
 *
 * @param atn The ATN.
 */
void ATNDeserializer::markPrecedenceDecisions(const ATN &atn) const {
  for (ATNState *state : atn.states) {
    if (!is<StarLoopEntryState*>(state)) {
      continue;
    }

    /* We analyze the ATN to determine if this ATN decision state is the
     * decision for the closure block that determines whether a
     * precedence rule should continue or complete.
     */
    if (atn.ruleToStartState[state->ruleIndex]->isLeftRecursiveRule) {
      ATNState *maybeLoopEndState = state->transitions[state->transitions.size() - 1]->target;
      if (is<LoopEndState *>(maybeLoopEndState)) {
        if (maybeLoopEndState->epsilonOnlyTransitions && is<RuleStopState*>(maybeLoopEndState->transitions[0]->target)) {
          downCast<StarLoopEntryState*>(state)->isPrecedenceDecision = true;
        }
      }
    }
  }
}

void ATNDeserializer::verifyATN(const ATN &atn) {
  // verify assumptions
  for (ATNState *state : atn.states) {
    if (state == nullptr) {
      continue;
    }

    checkCondition(state->epsilonOnlyTransitions || state->transitions.size() <= 1);

    if (is<PlusBlockStartState*>(state)) {
      checkCondition((downCast<PlusBlockStartState*>(state))->loopBackState != nullptr);
    }

    if (is<StarLoopEntryState*>(state)) {
      StarLoopEntryState *starLoopEntryState = downCast<StarLoopEntryState*>(state);
      checkCondition(starLoopEntryState->loopBackState != nullptr);
      checkCondition(starLoopEntryState->transitions.size() == 2);

      if (is<StarBlockStartState*>(starLoopEntryState->transitions[0]->target)) {
        checkCondition(downCast<LoopEndState*>(starLoopEntryState->transitions[1]->target) != nullptr);
        checkCondition(!starLoopEntryState->nonGreedy);
      } else if (is<LoopEndState*>(starLoopEntryState->transitions[0]->target)) {
        checkCondition(is<StarBlockStartState*>(starLoopEntryState->transitions[1]->target));
        checkCondition(starLoopEntryState->nonGreedy);
      } else {
        throw IllegalStateException();
      }
    }

    if (is<StarLoopbackState*>(state)) {
      checkCondition(state->transitions.size() == 1);
      checkCondition(is<StarLoopEntryState*>(state->transitions[0]->target));
    }

    if (is<LoopEndState*>(state)) {
      checkCondition((downCast<LoopEndState*>(state))->loopBackState != nullptr);
    }

    if (is<RuleStartState*>(state)) {
      checkCondition((downCast<RuleStartState*>(state))->stopState != nullptr);
    }

    if (is<BlockStartState*>(state)) {
      checkCondition((downCast<BlockStartState*>(state))->endState != nullptr);
    }

    if (is<BlockEndState*>(state)) {
      checkCondition((downCast<BlockEndState*>(state))->startState != nullptr);
    }

    if (is<DecisionState*>(state)) {
      DecisionState *decisionState = downCast<DecisionState*>(state);
      checkCondition(decisionState->transitions.size() <= 1 || decisionState->decision >= 0);
    } else {
      checkCondition(state->transitions.size() <= 1 || is<RuleStopState*>(state));
    }
  }
}

void ATNDeserializer::checkCondition(bool condition) {
  checkCondition(condition, "");
}

void ATNDeserializer::checkCondition(bool condition, const std::string &message) {
  if (!condition) {
    throw IllegalStateException(message);
  }
}

ConstTransitionPtr ATNDeserializer::edgeFactory(const ATN &atn, size_t type, size_t /*src*/, size_t trg, size_t arg1,
                                         size_t arg2, size_t arg3,
  const std::vector<misc::IntervalSet> &sets) {

  ATNState *target = atn.states[trg];
  switch (type) {
    case Transition::EPSILON:
      return std::make_unique<EpsilonTransition>(target);
    case Transition::RANGE:
      if (arg3 != 0) {
        return std::make_unique<RangeTransition>(target, Token::EOF, arg2);
      } else {
        return std::make_unique<RangeTransition>(target, arg1, arg2);
      }
    case Transition::RULE:
      return std::make_unique<RuleTransition>(downCast<RuleStartState*>(atn.states[arg1]), arg2, (int)arg3, target);
    case Transition::PREDICATE:
      return std::make_unique<PredicateTransition>(target, arg1, arg2, arg3 != 0);
    case Transition::PRECEDENCE:
      return std::make_unique<PrecedencePredicateTransition>(target, (int)arg1);
    case Transition::ATOM:
      if (arg3 != 0) {
        return std::make_unique<AtomTransition>(target, Token::EOF);
      } else {
        return std::make_unique<AtomTransition>(target, arg1);
      }
    case Transition::ACTION:
      return std::make_unique<ActionTransition>(target, arg1, arg2, arg3 != 0);
    case Transition::SET:
      return std::make_unique<SetTransition>(target, sets[arg1]);
    case Transition::NOT_SET:
      return std::make_unique<NotSetTransition>(target, sets[arg1]);
    case Transition::WILDCARD:
      return std::make_unique<WildcardTransition>(target);
  }

  throw IllegalArgumentException("The specified transition type is not valid.");
}

/* mem check: all created instances are freed in the d-tor of the ATN. */
ATNState* ATNDeserializer::stateFactory(size_t type, size_t ruleIndex) {
  ATNState *s;
  switch (type) {
    case ATNState::ATN_INVALID_TYPE:
      return nullptr;
    case ATNState::BASIC :
      s = new BasicState();
      break;
    case ATNState::RULE_START :
      s = new RuleStartState();
      break;
    case ATNState::BLOCK_START :
      s = new BasicBlockStartState();
      break;
    case ATNState::PLUS_BLOCK_START :
      s = new PlusBlockStartState();
      break;
    case ATNState::STAR_BLOCK_START :
      s = new StarBlockStartState();
      break;
    case ATNState::TOKEN_START :
      s = new TokensStartState();
      break;
    case ATNState::RULE_STOP :
      s = new RuleStopState();
      break;
    case ATNState::BLOCK_END :
      s = new BlockEndState();
      break;
    case ATNState::STAR_LOOP_BACK :
      s = new StarLoopbackState();
      break;
    case ATNState::STAR_LOOP_ENTRY :
      s = new StarLoopEntryState();
      break;
    case ATNState::PLUS_LOOP_BACK :
      s = new PlusLoopbackState();
      break;
    case ATNState::LOOP_END :
      s = new LoopEndState();
      break;
    default :
      std::string message = "The specified state type " + std::to_string(type) + " is not valid.";
      throw IllegalArgumentException(message);
  }

  s->ruleIndex = ruleIndex;
  return s;
}

Ref<LexerAction> ATNDeserializer::lexerActionFactory(LexerActionType type, int data1, int data2) const {
  switch (type) {
    case LexerActionType::CHANNEL:
      return std::make_shared<LexerChannelAction>(data1);

    case LexerActionType::CUSTOM:
      return std::make_shared<LexerCustomAction>(data1, data2);

    case LexerActionType::MODE:
      return std::make_shared< LexerModeAction>(data1);

    case LexerActionType::MORE:
      return LexerMoreAction::getInstance();

    case LexerActionType::POP_MODE:
      return LexerPopModeAction::getInstance();

    case LexerActionType::PUSH_MODE:
      return std::make_shared<LexerPushModeAction>(data1);

    case LexerActionType::SKIP:
      return LexerSkipAction::getInstance();

    case LexerActionType::TYPE:
      return std::make_shared<LexerTypeAction>(data1);

    default:
      throw IllegalArgumentException("The specified lexer action type " + std::to_string(static_cast<size_t>(type)) +
                                     " is not valid.");
  }
}
