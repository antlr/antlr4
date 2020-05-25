/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/ATNConfig.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC LexerATNConfig : public ATNConfig {
  public:
    using Ptr = std::shared_ptr<LexerATNConfig>;

    LexerATNConfig(ATNState *state, int alt, Ref<PredictionContext> const& context);
    LexerATNConfig(ATNState *state, int alt, Ref<PredictionContext> const& context, Ref<LexerActionExecutor> const& lexerActionExecutor);

    LexerATNConfig(LexerATNConfig::Ptr const& c, ATNState *state);
    LexerATNConfig(LexerATNConfig::Ptr const& c, ATNState *state, Ref<LexerActionExecutor> const& lexerActionExecutor);
    LexerATNConfig(LexerATNConfig::Ptr const& c, ATNState *state, Ref<PredictionContext> const& context);

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

    static bool checkNonGreedyDecision(LexerATNConfig::Ptr const& source, ATNState *target);
  };

} // namespace atn
} // namespace antlr4
