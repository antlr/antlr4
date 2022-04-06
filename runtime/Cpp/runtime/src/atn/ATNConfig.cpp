/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "misc/MurmurHash.h"
#include "atn/PredictionContext.h"
#include "atn/DecisionState.h"
#include "SemanticContext.h"
#include "support/Casts.h"

#include "atn/ATNConfig.h"

using namespace antlr4::atn;
using namespace antlrcpp;

namespace {

  inline constexpr size_t SUPPRESS_PRECEDENCE_FILTER = 0x40000000;

  bool checkNonGreedyDecision(const ATNConfig &other, const ATNState &state) {
    return other.hasPassedThroughNonGreedyDecision() || (DecisionState::is(state) && downCast<const DecisionState&>(state).nonGreedy);
  }

}

ATNConfig::ATNConfig(ATNState *state, size_t alt, Ref<const PredictionContext> context)
    : ATNConfig(state, alt, std::move(context), 0, SemanticContext::NONE, nullptr, false) {}

ATNConfig::ATNConfig(ATNState *state, size_t alt, Ref<const PredictionContext> context, Ref<const LexerActionExecutor> lexerActionExecutor)
    : ATNConfig(state, alt, std::move(context), 0, SemanticContext::NONE, std::move(lexerActionExecutor), false) {}

ATNConfig::ATNConfig(ATNState *state, size_t alt, Ref<const PredictionContext> context, Ref<const SemanticContext> semanticContext)
    : ATNConfig(state, alt, std::move(context), 0, std::move(semanticContext), nullptr, false) {}

ATNConfig::ATNConfig(const ATNConfig &other, Ref<const SemanticContext> semanticContext)
    : ATNConfig(other.state, other.alt, other.context, other.reachesIntoOuterContext, std::move(semanticContext), other._lexerActionExecutor, checkNonGreedyDecision(other, *other.state)) {}

ATNConfig::ATNConfig(const ATNConfig &other, ATNState *state)
    : ATNConfig(state, other.alt, other.context, other.reachesIntoOuterContext, other.semanticContext, other._lexerActionExecutor, checkNonGreedyDecision(other, *state)) {}

ATNConfig::ATNConfig(const ATNConfig &other, ATNState *state, Ref<const LexerActionExecutor> lexerActionExecutor)
    : ATNConfig(state, other.alt, other.context, other.reachesIntoOuterContext, other.semanticContext, std::move(lexerActionExecutor), checkNonGreedyDecision(other, *state)) {}

ATNConfig::ATNConfig(const ATNConfig &other, ATNState *state, Ref<const SemanticContext> semanticContext)
    : ATNConfig(state, other.alt, other.context, other.reachesIntoOuterContext, std::move(semanticContext), other._lexerActionExecutor, checkNonGreedyDecision(other, *state)) {}

ATNConfig::ATNConfig(const ATNConfig &other, ATNState *state, Ref<const PredictionContext> context)
    : ATNConfig(state, other.alt, std::move(context), other.reachesIntoOuterContext, other.semanticContext, other._lexerActionExecutor, checkNonGreedyDecision(other, *state)) {}

ATNConfig::ATNConfig(const ATNConfig &other, ATNState *state, Ref<const PredictionContext> context, Ref<const SemanticContext> semanticContext)
    : ATNConfig(state, other.alt, std::move(context), other.reachesIntoOuterContext, std::move(semanticContext), other._lexerActionExecutor, checkNonGreedyDecision(other, *state)) {}

ATNConfig::ATNConfig(ATNState *state, size_t alt, Ref<const PredictionContext> context,
                     size_t reachesIntoOuterContext, Ref<const SemanticContext> semanticContext,
                     Ref<const LexerActionExecutor> lexerActionExecutor, bool passedThroughNonGreedyDecision)
    : state(state), alt(alt), context(std::move(context)), reachesIntoOuterContext(reachesIntoOuterContext),
      semanticContext(std::move(semanticContext)), _lexerActionExecutor(std::move(lexerActionExecutor)),
      _passedThroughNonGreedyDecision(passedThroughNonGreedyDecision) {}

size_t ATNConfig::getOuterContextDepth() const {
  return reachesIntoOuterContext & ~SUPPRESS_PRECEDENCE_FILTER;
}

bool ATNConfig::isPrecedenceFilterSuppressed() const {
  return (reachesIntoOuterContext & SUPPRESS_PRECEDENCE_FILTER) != 0;
}

void ATNConfig::setPrecedenceFilterSuppressed(bool value) {
  if (value) {
    reachesIntoOuterContext |= SUPPRESS_PRECEDENCE_FILTER;
  } else {
    reachesIntoOuterContext &= ~SUPPRESS_PRECEDENCE_FILTER;
  }
}

size_t ATNConfig::hashCode() const {
  size_t hash = misc::MurmurHash::initialize();
  hash = misc::MurmurHash::update(hash, state->stateNumber);
  hash = misc::MurmurHash::update(hash, alt);
  hash = misc::MurmurHash::update(hash, context);
  hash = misc::MurmurHash::update(hash, semanticContext);
  hash = misc::MurmurHash::update(hash, hasPassedThroughNonGreedyDecision());
  hash = misc::MurmurHash::update(hash, getLexerActionExecutor());
  return misc::MurmurHash::finish(hash, 6);
}

bool ATNConfig::equals(const ATNConfig &other) const {
  if (this == std::addressof(other)) {
    return true;
  }
  return state->stateNumber == other.state->stateNumber &&
         alt == other.alt &&
         isPrecedenceFilterSuppressed() == other.isPrecedenceFilterSuppressed() &&
         hasPassedThroughNonGreedyDecision() == other.hasPassedThroughNonGreedyDecision() &&
         *context == *other.context &&
         *semanticContext == *other.semanticContext &&
         (getLexerActionExecutor() == other.getLexerActionExecutor() || (getLexerActionExecutor() != nullptr && other.getLexerActionExecutor() != nullptr && *getLexerActionExecutor() == *other.getLexerActionExecutor()));
}

std::string ATNConfig::toString() const {
  return toString(true);
}

std::string ATNConfig::toString(bool showAlt) const {
  std::stringstream ss;
  ss << "(";

  ss << state->toString();
  if (showAlt) {
    ss << "," << alt;
  }
  if (context) {
    ss << ",[" << context->toString() << "]";
  }
  if (semanticContext != nullptr && semanticContext != SemanticContext::NONE) {
    ss << ",[" << semanticContext->toString() << "]";
  }
  if (getOuterContextDepth() > 0) {
    ss << ",up=" << getOuterContextDepth();
  }
  ss << ")";

  return ss.str();
}
