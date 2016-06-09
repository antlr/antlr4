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

#include "misc/MurmurHash.h"
#include "atn/DecisionState.h"
#include "atn/PredictionContext.h"
#include "SemanticContext.h"
#include "atn/LexerActionExecutor.h"

#include "support/CPPUtils.h"

#include "atn/LexerATNConfig.h"

using namespace antlr4::atn;
using namespace antlrcpp;

LexerATNConfig::LexerATNConfig(ATNState *state, int alt, Ref<PredictionContext> const& context)
  : ATNConfig(state, alt, context, SemanticContext::NONE), _passedThroughNonGreedyDecision(false) {
}

LexerATNConfig::LexerATNConfig(ATNState *state, int alt, Ref<PredictionContext> const& context,
                               Ref<LexerActionExecutor> const& lexerActionExecutor)
  : ATNConfig(state, alt, context, SemanticContext::NONE), _lexerActionExecutor(lexerActionExecutor),
    _passedThroughNonGreedyDecision(false) {
}

LexerATNConfig::LexerATNConfig(Ref<LexerATNConfig> const& c, ATNState *state)
  : ATNConfig(c, state, c->context, c->semanticContext), _lexerActionExecutor(c->_lexerActionExecutor),
   _passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)) {
}

LexerATNConfig::LexerATNConfig(Ref<LexerATNConfig> const& c, ATNState *state, Ref<LexerActionExecutor> const& lexerActionExecutor)
  : ATNConfig(c, state, c->context, c->semanticContext), _lexerActionExecutor(lexerActionExecutor),
    _passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)) {
}

LexerATNConfig::LexerATNConfig(Ref<LexerATNConfig> const& c, ATNState *state, Ref<PredictionContext> const& context)
  : ATNConfig(c, state, context, c->semanticContext), _lexerActionExecutor(c->_lexerActionExecutor),
    _passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)) {
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
  hashCode = misc::MurmurHash::update(hashCode, context);
  hashCode = misc::MurmurHash::update(hashCode, semanticContext);
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

bool LexerATNConfig::checkNonGreedyDecision(Ref<LexerATNConfig> const& source, ATNState *target) {
  return source->_passedThroughNonGreedyDecision ||
    (is<DecisionState*>(target) && (static_cast<DecisionState*>(target))->nonGreedy);
}
