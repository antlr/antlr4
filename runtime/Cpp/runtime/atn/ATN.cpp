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

#include "LL1Analyzer.h"
#include "Token.h"
#include "RuleTransition.h"
#include "IntervalSet.h"
#include "RuleContext.h"
#include "DecisionState.h"
#include "Recognizer.h"
#include "ATNType.h"
#include "Exceptions.h"

#include "ATN.h"

using namespace org::antlr::v4::runtime;
using namespace org::antlr::v4::runtime::atn;

ATN::ATN() : ATN(ATNType::LEXER, 0) {
}

ATN::ATN(ATNType grammarType, int maxTokenType) : grammarType(grammarType), maxTokenType(maxTokenType) {
}

misc::IntervalSet ATN::nextTokens(ATNState *s, RuleContext *ctx) const {
  LL1Analyzer analyzer(*this);
  return analyzer.LOOK(s, ctx);

}

misc::IntervalSet ATN::nextTokens(ATNState *s) const {
  if (s->nextTokenWithinRule.isEmpty()) {
    s->nextTokenWithinRule = nextTokens(s, nullptr);
    s->nextTokenWithinRule.setReadOnly(true);
  }
  return s->nextTokenWithinRule;
}

void ATN::addState(ATNState *state) {
  if (state != nullptr) {
    state->atn = this;
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
    return decisionToState.at((size_t)decision);
  }
  return nullptr;
}

int ATN::getNumberOfDecisions() const {
  return (int)decisionToState.size();
}

misc::IntervalSet ATN::getExpectedTokens(int stateNumber, RuleContext *context) const {
  if (stateNumber < 0 || stateNumber >= (int)states.size()) {
    throw IllegalArgumentException("Invalid state number.");
  }

  RuleContext *ctx = context;
  ATNState *s = states.at((size_t)stateNumber);
  misc::IntervalSet following = nextTokens(s);
  if (!following.contains(Token::EPSILON)) {
    return following;
  }

  misc::IntervalSet expected;
  expected.addAll(following);
  expected.remove(Token::EPSILON);
  while (ctx != nullptr && ctx->invokingState >= 0 && following.contains(Token::EPSILON)) {
    ATNState *invokingState = states.at((size_t)ctx->invokingState);
    RuleTransition *rt = static_cast<RuleTransition*>(invokingState->transition(0));
    following = nextTokens(rt->followState);
    expected.addAll(following);
    expected.remove(Token::EPSILON);
    ctx = ctx->parent;
  }

  if (following.contains(Token::EPSILON)) {
    expected.add(EOF);
  }

  return expected;
}
