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

#include "MurmurHash.h"
#include "PredictionContext.h"
#include "SemanticContext.h"

#include "ATNConfig.h"

using namespace org::antlr::v4::runtime::atn;

const size_t ATNConfig::SUPPRESS_PRECEDENCE_FILTER = 0x40000000;

ATNConfig::ATNConfig(ATNState *state, int alt, Ref<PredictionContext> context)
  : ATNConfig(state, alt, context, SemanticContext::NONE) {
}

ATNConfig::ATNConfig(ATNState *state, int alt, Ref<PredictionContext> context, Ref<SemanticContext> semanticContext)
  : state(state), alt(alt), context(context), semanticContext(semanticContext) {
  reachesIntoOuterContext = 0;
}

ATNConfig::ATNConfig(Ref<ATNConfig> c) : ATNConfig(c, c->state, c->context, c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> c, ATNState *state) : ATNConfig(c, state, c->context, c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> c, ATNState *state, Ref<SemanticContext> semanticContext)
  : ATNConfig(c, state, c->context, semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> c, Ref<SemanticContext> semanticContext)
  : ATNConfig(c, c->state, c->context, semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> c, ATNState *state, Ref<PredictionContext> context)
  : ATNConfig(c, state, context, c->semanticContext) {
}

ATNConfig::ATNConfig(Ref<ATNConfig> c, ATNState *state, Ref<PredictionContext> context, Ref<SemanticContext> semanticContext)
  : state(state), alt(c->alt), context(context), semanticContext(semanticContext), reachesIntoOuterContext(c->reachesIntoOuterContext) {
}

ATNConfig::~ATNConfig() {
}

size_t ATNConfig::hashCode() const {
  size_t hashCode = misc::MurmurHash::initialize(7);
  hashCode = misc::MurmurHash::update(hashCode, (size_t)state->stateNumber);
  hashCode = misc::MurmurHash::update(hashCode, (size_t)alt);
  hashCode = misc::MurmurHash::update(hashCode, (size_t)context.get());
  hashCode = misc::MurmurHash::update(hashCode, (size_t)semanticContext.get());
  hashCode = misc::MurmurHash::finish(hashCode, 4);
  return hashCode;
}

int ATNConfig::getOuterContextDepth() const {
  return reachesIntoOuterContext & ~SUPPRESS_PRECEDENCE_FILTER;
}

bool ATNConfig::isPrecedenceFilterSuppressed() const {
  return (reachesIntoOuterContext & SUPPRESS_PRECEDENCE_FILTER) != 0;
}

void ATNConfig::setPrecedenceFilterSuppressed(bool value) {
  if (value) {
    reachesIntoOuterContext |= 0x40000000;
  }
  else {
    reachesIntoOuterContext &= ~SUPPRESS_PRECEDENCE_FILTER;
  }
}

bool ATNConfig::operator == (const ATNConfig& other) const
{
  return state->stateNumber == other.state->stateNumber && alt == other.alt &&
    (context == other.context || (context != nullptr && context == other.context)) &&
    semanticContext == other.semanticContext &&
    isPrecedenceFilterSuppressed() == other.isPrecedenceFilterSuppressed();
}

std::wstring ATNConfig::toString() {
  return toString(true);
}

std::wstring ATNConfig::toString(bool showAlt) {
  std::wstringstream ss;
  ss << L"(";

  ss << state->toString();
  if (showAlt) {
    ss << L"," << alt;
  }
  if (context) {
    ss << L",[" << context->toString() << L"]";
  }
  if (semanticContext != nullptr && semanticContext != SemanticContext::NONE) {
    ss << L"," << semanticContext.get();
  }
  if (getOuterContextDepth() > 0) {
    ss << L",up=" << getOuterContextDepth();
  }
  ss << L')';

  return ss.str();
}
