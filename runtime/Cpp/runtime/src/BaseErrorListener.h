/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <exception>
#include <string>
#include <cstddef>
#include "antlr4-common.h"
#include "Token.h"
#include "ANTLRErrorListener.h"

namespace antlrcpp {
  class BitSet;
}

namespace antlr4 {

  /**
   * Provides an empty default implementation of {@link ANTLRErrorListener}. The
   * default implementation of each method does nothing, but can be overridden as
   * necessary.
   */
  class ANTLR4CPP_PUBLIC BaseErrorListener : public ANTLRErrorListener {

    void syntaxError(Recognizer *recognizer, Token * offendingSymbol, size_t line, size_t charPositionInLine,
      const std::string &msg, std::exception_ptr e) override;

    void reportAmbiguity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex, bool exact,
      const antlrcpp::BitSet &ambigAlts, atn::ATNConfigSet *configs) override;

    void reportAttemptingFullContext(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,
      const antlrcpp::BitSet &conflictingAlts, atn::ATNConfigSet *configs) override;

    void reportContextSensitivity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,
      size_t prediction, atn::ATNConfigSet *configs) override;
  };

} // namespace antlr4
