/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
#include "support/StringUtils.h"

#include "atn/LexerCustomAction.h"
#include "atn/LexerChannelAction.h"
#include "atn/LexerModeAction.h"
#include "atn/LexerMoreAction.h"
#include "atn/LexerPopModeAction.h"
#include "atn/LexerPushModeAction.h"
#include "atn/LexerSkipAction.h"
#include "atn/LexerTypeAction.h"

#include "atn/ATNDeserializer.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlrcpp;

const size_t ATNDeserializer::SERIALIZED_VERSION = 3;

ATNDeserializer::ATNDeserializer(): ATNDeserializer(ATNDeserializationOptions::getDefaultOptions()) {
}

ATNDeserializer::ATNDeserializer(const ATNDeserializationOptions& dso): deserializationOptions(dso) {
}

/**
 * This value should never change. Updates following this version are
 * reflected as change in the unique ID SERIALIZED_UUID.
 */
Guid ATNDeserializer::ADDED_PRECEDENCE_TRANSITIONS() {
  return Guid("1DA0C57D-6C06-438A-9B27-10BCB3CE0F61");
}

Guid ATNDeserializer::ADDED_LEXER_ACTIONS() {
  return Guid("AADB8D7E-AEEF-4415-AD2B-8204D6CF042E");
}

Guid ATNDeserializer::SERIALIZED_UUID() {
  return ADDED_LEXER_ACTIONS();
}

Guid ATNDeserializer::BASE_SERIALIZED_UUID() {
  return Guid("33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3");
}

std::vector<Guid>& ATNDeserializer::SUPPORTED_UUIDS() {
  static std::vector<Guid> singleton = { BASE_SERIALIZED_UUID(), ADDED_PRECEDENCE_TRANSITIONS(), ADDED_LEXER_ACTIONS() };
  return singleton;
}

bool ATNDeserializer::isFeatureSupported(const Guid &feature, const Guid &actualUuid) {
  auto featureIterator = std::find(SUPPORTED_UUIDS().begin(), SUPPORTED_UUIDS().end(), feature);
  if (featureIterator == SUPPORTED_UUIDS().end()) {
    return false;
  }
  auto actualIterator = std::find(SUPPORTED_UUIDS().begin(), SUPPORTED_UUIDS().end(), actualUuid);
  if (actualIterator == SUPPORTED_UUIDS().end()) {
    return false;
  }

  return std::distance(featureIterator, actualIterator) >= 0;
}

ATN ATNDeserializer::deserialize(const std::vector<uint16_t>& input) {
  // Don't adjust the first value since that's the version number.
  std::vector<uint16_t> data(input.size());
  data[0] = input[0];
  for (size_t i = 1; i < input.size(); ++i) {
    data[i] = input[i] - 2;
  }

  int p = 0;
  int version = data[p++];
  if (version != SERIALIZED_VERSION) {
    std::string reason = "Could not deserialize ATN with version" + std::to_string(version) + "(expected " + std::to_string(SERIALIZED_VERSION) + ").";

    throw UnsupportedOperationException(reason);
  }

  Guid uuid = toUUID(data.data(), p);
  p += 8;
  auto uuidIterator = std::find(SUPPORTED_UUIDS().begin(), SUPPORTED_UUIDS().end(), uuid);
  if (uuidIterator == SUPPORTED_UUIDS().end()) {
    std::string reason = "Could not deserialize ATN with UUID " + uuid.toString() + " (expected " +
      SERIALIZED_UUID().toString() + " or a legacy UUID).";

    throw UnsupportedOperationException(reason);
  }

  bool supportsPrecedencePredicates = isFeatureSupported(ADDED_PRECEDENCE_TRANSITIONS(), uuid);
  bool supportsLexerActions = isFeatureSupported(ADDED_LEXER_ACTIONS(), uuid);

  ATNType grammarType = (ATNType)data[p++];
  size_t maxTokenType = data[p++];
  ATN atn(grammarType, maxTokenType);

  //
  // STATES
  //
  std::vector<std::pair<LoopEndState*, int>> loopBackStateNumbers;
  std::vector<std::pair<BlockStartState*, int>> endStateNumbers;
  int nstates = data[p++];
  for (int i = 0; i < nstates; i++) {
    int stype = data[p++];
    // ignore bad type of states
    if (stype == ATNState::ATN_INVALID_TYPE) {
      atn.addState(nullptr);
      continue;
    }

    int ruleIndex = data[p++];
    if (ruleIndex == 0xFFFF) { // Max Unicode char limit imposed by ANTLR.
      ruleIndex = -1;
    }

    ATNState *s = stateFactory(stype, ruleIndex);
    if (stype == ATNState::LOOP_END) { // special case
      int loopBackStateNumber = data[p++];
      loopBackStateNumbers.push_back({ (LoopEndState*)s,  loopBackStateNumber });
    } else if (is<BlockStartState*>(s)) {
      int endStateNumber = data[p++];
      endStateNumbers.push_back({ (BlockStartState*)s, endStateNumber });
    }
    atn.addState(s);
  }

  // delay the assignment of loop back and end states until we know all the state instances have been initialized
  for (auto &pair : loopBackStateNumbers) {
    pair.first->loopBackState = atn.states[(size_t)pair.second];
  }

  for (auto &pair : endStateNumbers) {
    pair.first->endState = (BlockEndState*)atn.states[(size_t)pair.second];
  }

  int numNonGreedyStates = data[p++];
  for (int i = 0; i < numNonGreedyStates; i++) {
    int stateNumber = data[p++];
    // The serialized ATN must be specifying the right states, so that the
    // cast below is correct.
    ((DecisionState *)atn.states[(size_t)stateNumber])->nonGreedy = true;
  }

  if (supportsPrecedencePredicates) {
    int numPrecedenceStates = data[p++];
    for (int i = 0; i < numPrecedenceStates; i++) {
      int stateNumber = data[p++];
      ((RuleStartState *)atn.states[(size_t)stateNumber])->isLeftRecursiveRule = true;
    }
  }

  //
  // RULES
  //
  size_t nrules = (size_t)data[p++];
  if (atn.grammarType == ATNType::LEXER) {
    atn.ruleToTokenType.resize(nrules);
  }

  for (size_t i = 0; i < nrules; i++) {
    size_t s = (size_t)data[p++];
    // Also here, the serialized atn must ensure to point to the correct class type.
    RuleStartState *startState = (RuleStartState*)atn.states[s];
    atn.ruleToStartState.push_back(startState);
    if (atn.grammarType == ATNType::LEXER) {
      int tokenType = data[p++];
      if (tokenType == 0xFFFF) {
        tokenType = Token::EOF;
      }

      atn.ruleToTokenType[i] = tokenType;

      if (!isFeatureSupported(ADDED_LEXER_ACTIONS(), uuid)) {
        // this piece of unused metadata was serialized prior to the
        // addition of LexerAction
        //int actionIndexIgnored = data[p++];
        p++;
      }
    }
  }

  atn.ruleToStopState.resize(nrules);
  for (ATNState *state : atn.states) {
    if (!is<RuleStopState*>(state)) {
      continue;
    }

    RuleStopState *stopState = static_cast<RuleStopState*>(state);
    atn.ruleToStopState[(size_t)state->ruleIndex] = stopState;
    atn.ruleToStartState[(size_t)state->ruleIndex]->stopState = stopState;
  }

  //
  // MODES
  //
  int nmodes = data[p++];
  for (int i = 0; i < nmodes; i++) {
    size_t s = (size_t)data[p++];
    atn.modeToStartState.push_back(static_cast<TokensStartState*>(atn.states[s]));
  }

  //
  // SETS
  //
  std::vector<misc::IntervalSet> sets;
  int nsets = data[p++];
  for (int i = 0; i < nsets; i++) {
    int nintervals = data[p++];
    misc::IntervalSet set;

    bool containsEof = data[p++] != 0;
    if (containsEof) {
      set.add(-1);
    }

    for (int j = 0; j < nintervals; j++) {
      set.add(data[p], data[p + 1]);
      p += 2;
    }
    sets.push_back(set);
  }

  //
  // EDGES
  //
  int nedges = data[p++];
  for (int i = 0; i < nedges; i++) {
    int src = data[p];
    int trg = data[p + 1];
    int ttype = data[p + 2];
    int arg1 = data[p + 3];
    int arg2 = data[p + 4];
    int arg3 = data[p + 5];
    Transition *trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
    ATNState *srcState = atn.states[(size_t)src];
    srcState->addTransition(trans);
    p += 6;
  }

  // edges for rule stop states can be derived, so they aren't serialized
  for (ATNState *state : atn.states) {
    for (size_t i = 0; i < state->getNumberOfTransitions(); i++) {
      Transition *t = state->transition(i);
      if (!is<RuleTransition*>(t)) {
        continue;
      }

      RuleTransition *ruleTransition = static_cast<RuleTransition*>(t);
      int outermostPrecedenceReturn = -1;
      if (atn.ruleToStartState[ruleTransition->target->ruleIndex]->isLeftRecursiveRule) {
        if (ruleTransition->precedence == 0) {
          outermostPrecedenceReturn = ruleTransition->target->ruleIndex;
        }
      }

      EpsilonTransition *returnTransition = new EpsilonTransition(ruleTransition->followState, outermostPrecedenceReturn); /* mem check: freed in ANTState d-tor */
      atn.ruleToStopState[ruleTransition->target->ruleIndex]->addTransition(returnTransition);
    }
  }

  for (ATNState *state : atn.states) {
    if (is<BlockStartState *>(state)) {
      BlockStartState *startState = static_cast<BlockStartState *>(state);

      // we need to know the end state to set its start state
      if (startState->endState == nullptr) {
        throw IllegalStateException();
      }

      // block end states can only be associated to a single block start state
      if (startState->endState->startState != nullptr) {
        throw IllegalStateException();
      }

      startState->endState->startState = static_cast<BlockStartState*>(state);
    }

    if (is<PlusLoopbackState*>(state)) {
      PlusLoopbackState *loopbackState = static_cast<PlusLoopbackState *>(state);
      for (size_t i = 0; i < loopbackState->getNumberOfTransitions(); i++) {
        ATNState *target = loopbackState->transition(i)->target;
        if (is<PlusBlockStartState *>(target)) {
          (static_cast<PlusBlockStartState *>(target))->loopBackState = loopbackState;
        }
      }
    } else if (is<StarLoopbackState *>(state)) {
      StarLoopbackState *loopbackState = static_cast<StarLoopbackState *>(state);
      for (size_t i = 0; i < loopbackState->getNumberOfTransitions(); i++) {
        ATNState *target = loopbackState->transition(i)->target;
        if (is<StarLoopEntryState *>(target)) {
          (static_cast<StarLoopEntryState*>(target))->loopBackState = loopbackState;
        }
      }
    }
  }

  //
  // DECISIONS
  //
  size_t ndecisions = (size_t)data[p++];
  for (size_t i = 1; i <= ndecisions; i++) {
    size_t s = data[p++];
    DecisionState *decState = dynamic_cast<DecisionState*>(atn.states[s]);
    if (decState == nullptr)
      throw IllegalStateException();

    atn.decisionToState.push_back(decState);
    decState->decision = (int)i - 1;
  }

  //
  // LEXER ACTIONS
  //
  if (atn.grammarType == ATNType::LEXER) {
    if (supportsLexerActions) {
      atn.lexerActions.resize(data[p++]);
      for (size_t i = 0; i < atn.lexerActions.size(); i++) {
        LexerActionType actionType = (LexerActionType)data[p++];
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
    } else {
      // for compatibility with older serialized ATNs, convert the old
      // serialized action index for action transitions to the new
      // form, which is the index of a LexerCustomAction
      for (ATNState *state : atn.states) {
        for (size_t i = 0; i < state->getNumberOfTransitions(); i++) {
          Transition *transition = state->transition(i);
          if (!is<ActionTransition *>(transition)) {
            continue;
          }

          int ruleIndex = static_cast<ActionTransition *>(transition)->ruleIndex;
          int actionIndex = static_cast<ActionTransition *>(transition)->actionIndex;
          Ref<LexerCustomAction> lexerAction = std::make_shared<LexerCustomAction>(ruleIndex, actionIndex);
          state->setTransition(i, new ActionTransition(transition->target, ruleIndex, (int)atn.lexerActions.size(), false)); /* mem-check freed in ATNState d-tor */
          atn.lexerActions.push_back(lexerAction);
        }
      }
    }
  }

  markPrecedenceDecisions(atn);

  if (deserializationOptions.isVerifyATN()) {
    verifyATN(atn);
  }

  if (deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType::PARSER) {
    atn.ruleToTokenType.resize(atn.ruleToStartState.size());
    for (size_t i = 0; i < atn.ruleToStartState.size(); i++) {
      atn.ruleToTokenType[i] = int(atn.maxTokenType + i + 1);
    }

    for (std::vector<RuleStartState*>::size_type i = 0; i < atn.ruleToStartState.size(); i++) {
      BasicBlockStartState *bypassStart = new BasicBlockStartState(); /* mem check: freed in ATN d-tor */
      bypassStart->ruleIndex = (int)i;
      atn.addState(bypassStart);

      BlockEndState *bypassStop = new BlockEndState(); /* mem check: freed in ATN d-tor */
      bypassStop->ruleIndex = (int)i;
      atn.addState(bypassStop);

      bypassStart->endState = bypassStop;
      atn.defineDecisionState(bypassStart);

      bypassStop->startState = bypassStart;

      ATNState *endState;
      Transition *excludeTransition = nullptr;
      if (atn.ruleToStartState[i]->isLeftRecursiveRule) {
        // wrap from the beginning of the rule to the StarLoopEntryState
        endState = nullptr;
        for (ATNState *state : atn.states) {
          if (state->ruleIndex != (int)i) {
            continue;
          }

          if (!is<StarLoopEntryState*>(state)) {
            continue;
          }

          ATNState *maybeLoopEndState = state->transition(state->getNumberOfTransitions() - 1)->target;
          if (!is<LoopEndState*>(maybeLoopEndState)) {
            continue;
          }

          if (maybeLoopEndState->epsilonOnlyTransitions && is<RuleStopState*>(maybeLoopEndState->transition(0)->target)) {
            endState = state;
            break;
          }
        }

        if (endState == nullptr) {
          throw UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");

        }

        excludeTransition = (static_cast<StarLoopEntryState*>(endState))->loopBackState->transition(0);
      } else {
        endState = atn.ruleToStopState[i];
      }

      // all non-excluded transitions that currently target end state need to target blockEnd instead
      for (ATNState *state : atn.states) {
        for (Transition *transition : state->getTransitions()) {
          if (transition == excludeTransition) {
            continue;
          }

          if (transition->target == endState) {
            transition->target = bypassStop;
          }
        }
      }

      // all transitions leaving the rule start state need to leave blockStart instead
      while (atn.ruleToStartState[i]->getNumberOfTransitions() > 0) {
        Transition *transition = atn.ruleToStartState[i]->removeTransition((int)atn.ruleToStartState[i]->getNumberOfTransitions() - 1);
        bypassStart->addTransition(transition);
      }

      // link the new states
      atn.ruleToStartState[i]->addTransition(new EpsilonTransition(bypassStart));  /* mem check: freed in ATNState d-tor */
      bypassStop->addTransition(new EpsilonTransition(endState)); /* mem check: freed in ATNState d-tor */

      ATNState *matchState = new BasicState(); /* mem check: freed in ATN d-tor */
      atn.addState(matchState);
      matchState->addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i])); /* mem check: freed in ATNState d-tor */
      bypassStart->addTransition(new EpsilonTransition(matchState)); /* mem check: freed in ATNState d-tor */
    }

    if (deserializationOptions.isVerifyATN()) {
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
void ATNDeserializer::markPrecedenceDecisions(const ATN &atn) {
  for (ATNState *state : atn.states) {
    if (!is<StarLoopEntryState *>(state)) {
      continue;
    }

    /* We analyze the ATN to determine if this ATN decision state is the
     * decision for the closure block that determines whether a
     * precedence rule should continue or complete.
     */
    if (atn.ruleToStartState[state->ruleIndex]->isLeftRecursiveRule) {
      ATNState *maybeLoopEndState = state->transition(state->getNumberOfTransitions() - 1)->target;
      if (is<LoopEndState *>(maybeLoopEndState)) {
        if (maybeLoopEndState->epsilonOnlyTransitions && is<RuleStopState *>(maybeLoopEndState->transition(0)->target)) {
          static_cast<StarLoopEntryState *>(state)->isPrecedenceDecision = true;
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

    checkCondition(state->onlyHasEpsilonTransitions() || state->getNumberOfTransitions() <= 1);

    if (is<PlusBlockStartState *>(state)) {
      checkCondition((static_cast<PlusBlockStartState *>(state))->loopBackState != nullptr);
    }

    if (is<StarLoopEntryState *>(state)) {
      StarLoopEntryState *starLoopEntryState = static_cast<StarLoopEntryState*>(state);
      checkCondition(starLoopEntryState->loopBackState != nullptr);
      checkCondition(starLoopEntryState->getNumberOfTransitions() == 2);

      if (is<StarBlockStartState *>(starLoopEntryState->transition(0)->target)) {
        checkCondition(static_cast<LoopEndState *>(starLoopEntryState->transition(1)->target) != nullptr);
        checkCondition(!starLoopEntryState->nonGreedy);
      } else if (is<LoopEndState *>(starLoopEntryState->transition(0)->target)) {
        checkCondition(is<StarBlockStartState *>(starLoopEntryState->transition(1)->target));
        checkCondition(starLoopEntryState->nonGreedy);
      } else {
        throw IllegalStateException();

      }
    }

    if (is<StarLoopbackState *>(state)) {
      checkCondition(state->getNumberOfTransitions() == 1);
      checkCondition(is<StarLoopEntryState *>(state->transition(0)->target));
    }

    if (is<LoopEndState *>(state)) {
      checkCondition((static_cast<LoopEndState *>(state))->loopBackState != nullptr);
    }

    if (is<RuleStartState *>(state)) {
      checkCondition((static_cast<RuleStartState *>(state))->stopState != nullptr);
    }

    if (is<BlockStartState *>(state)) {
      checkCondition((static_cast<BlockStartState *>(state))->endState != nullptr);
    }

    if (is<BlockEndState *>(state)) {
      checkCondition((static_cast<BlockEndState *>(state))->startState != nullptr);
    }

    if (is<DecisionState *>(state)) {
      DecisionState *decisionState = static_cast<DecisionState *>(state);
      checkCondition(decisionState->getNumberOfTransitions() <= 1 || decisionState->decision >= 0);
    } else {
      checkCondition(state->getNumberOfTransitions() <= 1 || is<RuleStopState *>(state));
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

Guid ATNDeserializer::toUUID(const unsigned short *data, int offset) {
  return Guid((uint16_t *)data + offset, true);
}

/* mem check: all created instances are freed in the d-tor of the ATNState they are added to. */
Transition *ATNDeserializer::edgeFactory(const ATN &atn, int type, int /*src*/, int trg, int arg1, int arg2, int arg3,
  const std::vector<misc::IntervalSet> &sets) {
  
  ATNState *target = atn.states[(size_t)trg];
  switch (type) {
    case Transition::EPSILON :
      return new EpsilonTransition(target);
    case Transition::RANGE :
      if (arg3 != 0) {
        return new RangeTransition(target, Token::EOF, arg2);
      } else {
        return new RangeTransition(target, arg1, arg2);
      }
    case Transition::RULE :
      return new RuleTransition(static_cast<RuleStartState*>(atn.states[(size_t)arg1]), arg2, arg3, target);
    case Transition::PREDICATE :
      return new PredicateTransition(target, arg1, arg2, arg3 != 0);
    case Transition::PRECEDENCE:
      return new PrecedencePredicateTransition(target, arg1);
    case Transition::ATOM :
      if (arg3 != 0) {
        return new AtomTransition(target, Token::EOF);
      } else {
        return new AtomTransition(target, arg1);
      }
    case Transition::ACTION :
      return new ActionTransition(target, arg1, arg2, arg3 != 0);
    case Transition::SET :
      return new SetTransition(target, sets[(size_t)arg1]);
    case Transition::NOT_SET :
      return new NotSetTransition(target, sets[(size_t)arg1]);
    case Transition::WILDCARD :
      return new WildcardTransition(target);
  }

  throw IllegalArgumentException("The specified transition type is not valid.");
}

/* mem check: all created instances are freed in the d-tor of the ATN. */
ATNState* ATNDeserializer::stateFactory(int type, int ruleIndex) {
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

Ref<LexerAction> ATNDeserializer::lexerActionFactory(LexerActionType type, int data1, int data2) {
  switch (type) {
    case LexerActionType::CHANNEL:
      return std::make_shared<LexerChannelAction>(data1);

    case LexerActionType::CUSTOM:
      return std::make_shared<LexerCustomAction>(data1, data2);

    case LexerActionType::MODE:
      return std::make_shared< LexerModeAction>(data1);

    case LexerActionType::MORE:
      return LexerMoreAction::INSTANCE;

    case LexerActionType::POP_MODE:
      return LexerPopModeAction::INSTANCE;

    case LexerActionType::PUSH_MODE:
      return std::make_shared<LexerPushModeAction>(data1);

    case LexerActionType::SKIP:
      return LexerSkipAction::INSTANCE;

    case LexerActionType::TYPE:
      return std::make_shared<LexerTypeAction>(data1);

    default:
      throw IllegalArgumentException("The specified lexer action type " + std::to_string((size_t)type) + " is not valid.");
  }
}
