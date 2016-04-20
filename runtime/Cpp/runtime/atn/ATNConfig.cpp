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

#include "ATNConfig.h"

using namespace org::antlr::v4::runtime::atn;

ATNConfig::ATNConfig(ATNState *state, int alt, PredictionContext::Ref context)
  : ATNConfig(state, alt, context, SemanticContext::NONE) {
}

ATNConfig::ATNConfig(ATNState *state, int alt, PredictionContext::Ref context, SemanticContext::Ref semanticContext)
  : state(state), alt(alt), context(context), semanticContext(semanticContext) {
  reachesIntoOuterContext = 0;
}

ATNConfig::ATNConfig(ATNConfig::Ref c) : ATNConfig(c, c->state, c->context, c->semanticContext) {
}

ATNConfig::ATNConfig(ATNConfig::Ref c, ATNState *state) : ATNConfig(c, state, c->context, c->semanticContext) {
}

ATNConfig::ATNConfig(ATNConfig::Ref c, ATNState *state, SemanticContext::Ref semanticContext)
  : ATNConfig(c, state, c->context, semanticContext) {
}

ATNConfig::ATNConfig(ATNConfig::Ref c, SemanticContext::Ref semanticContext)
  : ATNConfig(c, c->state, c->context, semanticContext) {
}

ATNConfig::ATNConfig(ATNConfig::Ref c, ATNState *state, PredictionContext::Ref context)
  : ATNConfig(c, state, context, c->semanticContext) {
}

ATNConfig::ATNConfig(ATNConfig::Ref c, ATNState *state, PredictionContext::Ref context, SemanticContext::Ref semanticContext)
  : state(state), alt(c->alt), context(context), semanticContext(semanticContext), reachesIntoOuterContext(c->reachesIntoOuterContext) {
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

bool ATNConfig::operator == (const ATNConfig& other) const
{
  return this->state->stateNumber == other.state->stateNumber && this->alt == other.alt &&
    (this->context == other.context || (this->context != nullptr && this->context == other.context)) &&
    this->semanticContext == other.semanticContext;
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
  if (reachesIntoOuterContext > 0) {
    ss << L",up=" << reachesIntoOuterContext;
  }
  ss << L')';

  return ss.str();
}
