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

#include "atn/LL1Analyzer.h"
#include "Token.h"
#include "atn/RuleTransition.h"
#include "misc/IntervalSet.h"
#include "RuleContext.h"
#include "atn/DecisionState.h"
#include "Recognizer.h"
#include "atn/ATNType.h"
#include "Exceptions.h"
#include "support/CPPUtils.h"

#include "atn/ATN.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlrcpp;

ATN::ATN() : ATN(ATNType::LEXER, 0) {
}

ATN::ATN(ATN &&other) {
  states = std::move(other.states);
  decisionToState = std::move(other.decisionToState);
  ruleToStartState = std::move(other.ruleToStartState);
  ruleToStopState = std::move(other.ruleToStopState);
  grammarType = std::move(other.grammarType);
  maxTokenType = std::move(other.maxTokenType);
  ruleToTokenType = std::move(other.ruleToTokenType);
  lexerActions = std::move(other.lexerActions);
  modeToStartState = std::move(other.modeToStartState);
}

ATN::ATN(ATNType grammarType, size_t maxTokenType) : grammarType(grammarType), maxTokenType(maxTokenType) {
}

ATN::~ATN() {
  for (ATNState *state : states) {
    delete state;
  }
}

/**
 * Required to be defined (even though not used) as we have an explicit move assignment operator.
 */
ATN& ATN::operator = (ATN &other) NOEXCEPT {
  states = other.states;
  decisionToState = other.decisionToState;
  ruleToStartState = other.ruleToStartState;
  ruleToStopState = other.ruleToStopState;
  grammarType = other.grammarType;
  maxTokenType = other.maxTokenType;
  ruleToTokenType = other.ruleToTokenType;
  lexerActions = other.lexerActions;
  modeToStartState = other.modeToStartState;

  return *this;
}

/**
 * Explicit move assignment operator to make this the preferred assignment. With implicit copy/move assignment
 * operators it seems the copy operator is preferred causing trouble when releasing the allocated ATNState instances.
 */
ATN& ATN::operator = (ATN &&other) NOEXCEPT {
  states = std::move(other.states);
  decisionToState = std::move(other.decisionToState);
  ruleToStartState = std::move(other.ruleToStartState);
  ruleToStopState = std::move(other.ruleToStopState);
  grammarType = std::move(other.grammarType);
  maxTokenType = std::move(other.maxTokenType);
  ruleToTokenType = std::move(other.ruleToTokenType);
  lexerActions = std::move(other.lexerActions);
  modeToStartState = std::move(other.modeToStartState);

  return *this;
}

misc::IntervalSet ATN::nextTokens(ATNState *s, Ref<RuleContext> const& ctx) const {
  LL1Analyzer analyzer(*this);
  return analyzer.LOOK(s, ctx);

}

misc::IntervalSet& ATN::nextTokens(ATNState *s) const {
  if (s->nextTokenWithinRule.isEmpty()) {
    s->nextTokenWithinRule = nextTokens(s, nullptr);
    s->nextTokenWithinRule.setReadOnly(true);
  }
  return s->nextTokenWithinRule;
}

void ATN::addState(ATNState *state) {
  if (state != nullptr) {
    //state->atn = this;
    state->stateNumber = (int)states.size();
  }

  states.push_back(state);
}

void ATN::removeState(ATNState *state) {
  delete states.at((size_t)state->stateNumber);// just free mem, don't shift states in list
  states.at((size_t)state->stateNumber) = nullptr;
}

int ATN::defineDecisionState(DecisionState *s) {
  decisionToState.push_back(s);
  s->decision = (int)decisionToState.size() - 1;
  return s->decision;
}

DecisionState *ATN::getDecisionState(int decision) const {
  if (!decisionToState.empty()) {
    return decisionToState[(size_t)decision];
  }
  return nullptr;
}

int ATN::getNumberOfDecisions() const {
  return (int)decisionToState.size();
}

misc::IntervalSet ATN::getExpectedTokens(int stateNumber, Ref<RuleContext> const& context) const {
  if (stateNumber < 0 || stateNumber >= (int)states.size()) {
    throw IllegalArgumentException("Invalid state number.");
  }

  Ref<RuleContext> ctx = context;
  ATNState *s = states.at((size_t)stateNumber);
  misc::IntervalSet following = nextTokens(s);
  if (!following.contains(Token::EPSILON)) {
    return following;
  }

  misc::IntervalSet expected;
  expected.addAll(following);
  expected.remove(Token::EPSILON);
  while (ctx && ctx->invokingState >= 0 && following.contains(Token::EPSILON)) {
    ATNState *invokingState = states.at((size_t)ctx->invokingState);
    RuleTransition *rt = static_cast<RuleTransition*>(invokingState->transition(0));
    following = nextTokens(rt->followState);
    expected.addAll(following);
    expected.remove(Token::EPSILON);

    if (ctx->parent.expired()) {
      break;
    }
    ctx = ctx->parent.lock();
  }

  if (following.contains(Token::EPSILON)) {
    expected.add(Token::EOF);
  }

  return expected;
}

std::string ATN::toString() const {
  std::stringstream ss;
  std::string type;
  switch (grammarType) {
    case ATNType::LEXER:
      type = "LEXER ";
      break;

    case ATNType::PARSER:
      type = "PARSER ";
      break;

    default:
      break;
  }
  ss << "(" << type << "ATN " << std::hex << this << std::dec << ") maxTokenType: " << maxTokenType << std::endl;
  ss << "states (" << states.size() << ") {" << std::endl;

  size_t index = 0;
  for (auto state : states) {
    if (state == nullptr) {
      ss << "  " << index++ << ": nul" << std::endl;
    } else {
      std::string text = state->toString();
      ss << "  " << index++ << ": " << indent(text, "  ", false) << std::endl;
    }
  }

  index = 0;
  for (auto state : decisionToState) {
    if (state == nullptr) {
      ss << "  " << index++ << ": nul" << std::endl;
    } else {
      std::string text = state->toString();
      ss << "  " << index++ << ": " << indent(text, "  ", false) << std::endl;
    }
  }

  ss << "}";

  return ss.str();
}

