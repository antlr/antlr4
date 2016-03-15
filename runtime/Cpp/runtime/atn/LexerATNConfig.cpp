#include "LexerATNConfig.h"
#include "MurmurHash.h"
#include "DecisionState.h"
#include "PredictionContext.h"
#include "SemanticContext.h"

/*
 * [The "BSD license"]
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace atn {

                    LexerATNConfig::LexerATNConfig(ATNState *state, int alt, PredictionContext *context) : ATNConfig(state, alt, context, SemanticContext::NONE), passedThroughNonGreedyDecision(false) {
                        InitializeInstanceFields();
                    }

                    LexerATNConfig::LexerATNConfig(ATNState *state, int alt, PredictionContext *context, int actionIndex) : ATNConfig(state, alt, context, SemanticContext::NONE), passedThroughNonGreedyDecision(false) {
                        InitializeInstanceFields();
                        this->lexerActionIndex = actionIndex;
                    }

                    LexerATNConfig::LexerATNConfig(LexerATNConfig *c, ATNState *state) : ATNConfig(c, state, c->context, c->semanticContext), passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)) {
                        InitializeInstanceFields();
                        this->lexerActionIndex = c->lexerActionIndex;
                    }

                    LexerATNConfig::LexerATNConfig(LexerATNConfig *c, ATNState *state, int actionIndex) : ATNConfig(c, state, c->context, c->semanticContext), passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)) {
                        InitializeInstanceFields();
                        this->lexerActionIndex = actionIndex;
                    }

                    LexerATNConfig::LexerATNConfig(LexerATNConfig *c, ATNState *state, PredictionContext *context) : ATNConfig(c, state, context, c->semanticContext), passedThroughNonGreedyDecision(checkNonGreedyDecision(c, state)) {
                        InitializeInstanceFields();
                        this->lexerActionIndex = c->lexerActionIndex;
                    }

                    bool LexerATNConfig::hasPassedThroughNonGreedyDecision() {
                        return passedThroughNonGreedyDecision;
                    }

                    size_t LexerATNConfig::hashCode() {
                        int hashCode = misc::MurmurHash::initialize(7);
                        hashCode = misc::MurmurHash::update(hashCode, state->stateNumber);
                        hashCode = misc::MurmurHash::update(hashCode, alt);
                        hashCode = misc::MurmurHash::update(hashCode, context);
                        hashCode = misc::MurmurHash::update(hashCode, semanticContext);
                        hashCode = misc::MurmurHash::update(hashCode, passedThroughNonGreedyDecision ? 1 : 0);
                        hashCode = misc::MurmurHash::finish(hashCode, 5);
                        return hashCode;
                    }

                    bool LexerATNConfig::equals(ATNConfig *other) {
                        if (this == other) {
                            return true;
                        } else if (!(dynamic_cast<LexerATNConfig*>(other) != nullptr)) {
                            return false;
                        }

                        LexerATNConfig *lexerOther = static_cast<LexerATNConfig*>(other);
                        if (passedThroughNonGreedyDecision != lexerOther->passedThroughNonGreedyDecision) {
                            return false;
                        }

                        return ATNConfig::equals(other);
                    }

                    bool LexerATNConfig::checkNonGreedyDecision(LexerATNConfig *source, ATNState *target) {
                        return source->passedThroughNonGreedyDecision || (dynamic_cast<DecisionState*>(target) != nullptr && (static_cast<DecisionState*>(target))->nonGreedy);
                    }

                    void LexerATNConfig::InitializeInstanceFields() {
                        lexerActionIndex = -1;
                    }
                }
            }
        }
    }
}
