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
#include "DecisionState.h"
#include "PredictionContext.h"
#include "SemanticContext.h"
#include "LexerActionExecutor.h"

#include "CPPUtils.h"

#include "LexerATNConfig.h"

using namespace org::antlr::v4::runtime::atn;
using namespace antlrcpp;

LexerATNConfig::LexerATNConfig(ATNState *state, int alt, Ref<PredictionContext> context)
  : ATNConfig(state, alt, context, SemanticContext::NONE), _passedThroughNonGreedyDecision(false) {
}

LexerATNConfig::LexerATNConfig(ATNState *state, int alt, Ref<PredictionContext> context,
                               Ref<LexerActionExecutor> lexerActionExecutor)
  : ATNConfig(state, alt, context, SemanticContext::NONE), _passedThroughNonGreedyDecision(false),
    _lexerActionExecutor(lexerActionExecutor) {
}

LexerATNConfig::LexerATNConfig(Ref<LexerATNConfig> c, ATNState *state)
  : ATNConfig(c, state, c->context, c->semanticContext), _passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)),
    _lexerActionExecutor(c->_lexerActionExecutor) {
}

LexerATNConfig::LexerATNConfig(Ref<LexerATNConfig> c, ATNState *state, Ref<LexerActionExecutor> lexerActionExecutor)
  : ATNConfig(c, state, c->context, c->semanticContext), _passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)),
    _lexerActionExecutor(lexerActionExecutor) {
}

LexerATNConfig::LexerATNConfig(Ref<LexerATNConfig> c, ATNState *state, Ref<PredictionContext> context)
  : ATNConfig(c, state, context, c->semanticContext), _passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)),
    _lexerActionExecutor(c->_lexerActionExecutor) {
}

Ref<LexerActionExecutor> LexerATNConfig::getLexerActionExecutor() const {
  return _lexerActionExecutor;
}

bool LexerATNConfig::hasPassedThroughNonGreedyDecision() {
  return _passedThroughNonGreedyDecision;
}

size_t LexerATNConfig::hashCode() const {
  size_t hashCode = misc::MurmurHash::initialize(7);
  hashCode = misc::MurmurHash::update(hashCode, (size_t)state->stateNumber);
  hashCode = misc::MurmurHash::update(hashCode, (size_t)alt);
  hashCode = misc::MurmurHash::update(hashCode, context->hashCode());
  hashCode = misc::MurmurHash::update(hashCode, semanticContext->hashCode());
  hashCode = misc::MurmurHash::update(hashCode, _passedThroughNonGreedyDecision ? 1 : 0);
  hashCode = misc::MurmurHash::update(hashCode, _lexerActionExecutor ? _lexerActionExecutor->hashCode() : 0);
  hashCode = misc::MurmurHash::finish(hashCode, 6);
  return hashCode;
}

bool LexerATNConfig::operator == (const LexerATNConfig& other) const
{
  if (_passedThroughNonGreedyDecision != other._passedThroughNonGreedyDecision)
    return false;

  if (_lexerActionExecutor != other._lexerActionExecutor) {
    return false;
  }

  return ATNConfig::operator == (other);
}

bool LexerATNConfig::checkNonGreedyDecision(Ref<LexerATNConfig> source, ATNState *target) {
  return source->_passedThroughNonGreedyDecision ||
    (is<DecisionState*>(target) && (static_cast<DecisionState*>(target))->nonGreedy);
}
