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

#pragma once

#include "atn/ATNConfig.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC LexerATNConfig : public ATNConfig {
  public:
    LexerATNConfig(ATNState *state, int alt, Ref<PredictionContext> const& context);
    LexerATNConfig(ATNState *state, int alt, Ref<PredictionContext> const& context, Ref<LexerActionExecutor> const& lexerActionExecutor);

    LexerATNConfig(Ref<LexerATNConfig> const& c, ATNState *state);
    LexerATNConfig(Ref<LexerATNConfig> const& c, ATNState *state, Ref<LexerActionExecutor> const& lexerActionExecutor);
    LexerATNConfig(Ref<LexerATNConfig> const& c, ATNState *state, Ref<PredictionContext> const& context);

    /**
     * Gets the {@link LexerActionExecutor} capable of executing the embedded
     * action(s) for the current configuration.
     */
    Ref<LexerActionExecutor> getLexerActionExecutor() const;
    bool hasPassedThroughNonGreedyDecision();

    virtual size_t hashCode() const override;

    bool operator == (const LexerATNConfig& other) const;

  private:
    /**
     * This is the backing field for {@link #getLexerActionExecutor}.
     */
    const Ref<LexerActionExecutor> _lexerActionExecutor;
    const bool _passedThroughNonGreedyDecision;

    static bool checkNonGreedyDecision(Ref<LexerATNConfig> const& source, ATNState *target);
  };

} // namespace atn
} // namespace antlr4
