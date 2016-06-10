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

#include "BaseErrorListener.h"

namespace antlr4 {

  /// <summary>
  /// This implementation of <seealso cref="ANTLRErrorListener"/> can be used to identify
  /// certain potential correctness and performance problems in grammars. "Reports"
  /// are made by calling <seealso cref="Parser#notifyErrorListeners"/> with the appropriate
  /// message.
  ///
  /// <ul>
  /// <li><b>Ambiguities</b>: These are cases where more than one path through the
  /// grammar can match the input.</li>
  /// <li><b>Weak context sensitivity</b>: These are cases where full-context
  /// prediction resolved an SLL conflict to a unique alternative which equaled the
  /// minimum alternative of the SLL conflict.</li>
  /// <li><b>Strong (forced) context sensitivity</b>: These are cases where the
  /// full-context prediction resolved an SLL conflict to a unique alternative,
  /// <em>and</em> the minimum alternative of the SLL conflict was found to not be
  /// a truly viable alternative. Two-stage parsing cannot be used for inputs where
  /// this situation occurs.</li>
  /// </ul>
  ///
  /// @author Sam Harwell
  /// </summary>
  class ANTLR4CPP_PUBLIC DiagnosticErrorListener : public BaseErrorListener {
    /// <summary>
    /// When {@code true}, only exactly known ambiguities are reported.
    /// </summary>
  protected:
    const bool exactOnly;

    /// <summary>
    /// Initializes a new instance of <seealso cref="DiagnosticErrorListener"/> which only
    /// reports exact ambiguities.
    /// </summary>
  public:
    DiagnosticErrorListener();

    /// <summary>
    /// Initializes a new instance of <seealso cref="DiagnosticErrorListener"/>, specifying
    /// whether all ambiguities or only exact ambiguities are reported.
    /// </summary>
    /// <param name="exactOnly"> {@code true} to report only exact ambiguities, otherwise
    /// {@code false} to report all ambiguities. </param>
    DiagnosticErrorListener(bool exactOnly);

    virtual void reportAmbiguity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex, bool exact,
      const antlrcpp::BitSet &ambigAlts, atn::ATNConfigSet *configs) override;

    virtual void reportAttemptingFullContext(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,
      const antlrcpp::BitSet &conflictingAlts, atn::ATNConfigSet *configs) override;

    virtual void reportContextSensitivity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,
      int prediction, atn::ATNConfigSet *configs) override;

  protected:
    virtual std::string getDecisionDescription(Parser *recognizer, const dfa::DFA &dfa);

    /// <summary>
    /// Computes the set of conflicting or ambiguous alternatives from a
    /// configuration set, if that information was not already provided by the
    /// parser.
    /// </summary>
    /// <param name="reportedAlts"> The set of conflicting or ambiguous alternatives, as
    /// reported by the parser. </param>
    /// <param name="configs"> The conflicting or ambiguous configuration set. </param>
    /// <returns> Returns {@code reportedAlts} if it is not {@code null}, otherwise
    /// returns the set of alternatives represented in {@code configs}. </returns>
    virtual antlrcpp::BitSet getConflictingAlts(const antlrcpp::BitSet &reportedAlts, atn::ATNConfigSet *configs);
  };

} // namespace antlr4
