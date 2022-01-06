/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "misc/MurmurHash.h"
#include "atn/PredictionContext.h"
#include "SemanticContext.h"

#include "atn/ATNConfig.h"

using namespace antlr4::atn;

ATNConfig::ATNConfig(ATNState *state_, size_t alt, Ref<PredictionContext> context)
  : ATNConfig(state_, alt, std::move(context), SemanticContext::NONE) {
}

ATNConfig::ATNConfig(ATNState *state, size_t alt, Ref<PredictionContext> context, Ref<SemanticContext> semanticContext)
  : state(state), alt(alt), context(std::move(context)), semanticContext(std::move(semanticContext)) {
  reachesIntoOuterContext = 0;
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c) : ATNConfig(c, c->state, c->context, c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, ATNState *state_) : ATNConfig(c, state_, c->context, c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, ATNState *state, Ref<SemanticContext> semanticContext)
  : ATNConfig(c, state, c->context, std::move(semanticContext)) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, Ref<SemanticContext> semanticContext)
  : ATNConfig(c, c->state, c->context, std::move(semanticContext)) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, ATNState *state, Ref<PredictionContext> context)
  : ATNConfig(c, state, std::move(context), c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, ATNState *state, Ref<PredictionContext> context,
                     Ref<SemanticContext> semanticContext)
  : state(state), alt(c->alt), context(std::move(context)), reachesIntoOuterContext(c->reachesIntoOuterContext),
    semanticContext(std::move(semanticContext)) {
}

size_t ATNConfig::hashCode() const {
  size_t hashCode = misc::MurmurHash::initialize(7);
  hashCode = misc::MurmurHash::update(hashCode, state->stateNumber);
  hashCode = misc::MurmurHash::update(hashCode, alt);
  hashCode = misc::MurmurHash::update(hashCode, context);
  hashCode = misc::MurmurHash::update(hashCode, semanticContext);
  hashCode = misc::MurmurHash::finish(hashCode, 4);
  return hashCode;
}

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

bool ATNConfig::operator==(const ATNConfig &other) const {
  return state->stateNumber == other.state->stateNumber && alt == other.alt &&
    ((context == other.context) || (*context == *other.context)) &&
    *semanticContext == *other.semanticContext &&
    isPrecedenceFilterSuppressed() == other.isPrecedenceFilterSuppressed();
}

bool ATNConfig::operator!=(const ATNConfig &other) const {
  return !operator==(other);
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
    ss << "," << semanticContext.get();
  }
  if (getOuterContextDepth() > 0) {
    ss << ",up=" << getOuterContextDepth();
  }
  ss << ')';

  return ss.str();
}
