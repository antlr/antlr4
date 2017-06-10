/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "misc/MurmurHash.h"
#include "atn/PredictionContext.h"
#include "SemanticContext.h"

#include "atn/ATNConfig.h"

using namespace antlr4::atn;

const size_t ATNConfig::SUPPRESS_PRECEDENCE_FILTER = 0x40000000;

ATNConfig::ATNConfig(ATNState *state_in, size_t alt_in, Ref<PredictionContext> const& context_in)
  : ATNConfig(state_in, alt_in, context_in, SemanticContext::NONE) {
}

ATNConfig::ATNConfig(ATNState *state_in, size_t alt_in, Ref<PredictionContext> const& context_in, Ref<SemanticContext> const& semanticContext_in)
  : state(state_in), alt(alt_in), context(context_in), semanticContext(semanticContext_in) {
  reachesIntoOuterContext = 0;
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c) : ATNConfig(c, c->state, c->context, c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, ATNState *state_in) : ATNConfig(c, state_in, c->context, c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, ATNState *state, Ref<SemanticContext> const& semanticContext)
  : ATNConfig(c, state, c->context, semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, Ref<SemanticContext> const& semanticContext)
  : ATNConfig(c, c->state, c->context, semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, ATNState *state, Ref<PredictionContext> const& context)
  : ATNConfig(c, state, context, c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> const& c, ATNState *state, Ref<PredictionContext> const& context,
                     Ref<SemanticContext> const& semanticContext)
  : state(state), alt(c->alt), context(context), reachesIntoOuterContext(c->reachesIntoOuterContext),
    semanticContext(semanticContext) {
}

ATNConfig::~ATNConfig() {
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

bool ATNConfig::operator == (const ATNConfig &other) const {
  return state->stateNumber == other.state->stateNumber && alt == other.alt &&
    ((context == other.context) || (*context == *other.context)) &&
    *semanticContext == *other.semanticContext &&
    isPrecedenceFilterSuppressed() == other.isPrecedenceFilterSuppressed();
}

bool ATNConfig::operator != (const ATNConfig &other) const {
  return !operator==(other);
}

std::string ATNConfig::toString() {
  return toString(true);
}

std::string ATNConfig::toString(bool showAlt) {
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
