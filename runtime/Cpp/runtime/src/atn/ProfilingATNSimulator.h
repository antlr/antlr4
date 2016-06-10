/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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

#include "atn/ParserATNSimulator.h"
#include "atn/DecisionInfo.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC ProfilingATNSimulator : public ParserATNSimulator {
  public:
    ProfilingATNSimulator(Parser *parser);

    virtual int adaptivePredict(TokenStream *input, int decision, Ref<ParserRuleContext> const& outerContext) override;

    virtual std::vector<DecisionInfo> getDecisionInfo() const;
    virtual dfa::DFAState* getCurrentState() const;

  protected:
    std::vector<DecisionInfo> _decisions;
    int _numDecisions = 0;

    int _sllStopIndex = 0;
    int _llStopIndex = 0;

    int _currentDecision = 0;
    dfa::DFAState *_currentState;

    /// <summary>
    /// At the point of LL failover, we record how SLL would resolve the conflict so that
    ///  we can determine whether or not a decision / input pair is context-sensitive.
    ///  If LL gives a different result than SLL's predicted alternative, we have a
    ///  context sensitivity for sure. The converse is not necessarily true, however.
    ///  It's possible that after conflict resolution chooses minimum alternatives,
    ///  SLL could get the same answer as LL. Regardless of whether or not the result indicates
    ///  an ambiguity, it is not treated as a context sensitivity because LL prediction
    ///  was not required in order to produce a correct prediction for this decision and input sequence.
    ///  It may in fact still be a context sensitivity but we don't know by looking at the
    ///  minimum alternatives for the current input.
    /// </summary>
    int conflictingAltResolvedBySLL = 0;

    virtual dfa::DFAState* getExistingTargetState(dfa::DFAState *previousD, ssize_t t) override;
    virtual dfa::DFAState* computeTargetState(dfa::DFA &dfa, dfa::DFAState *previousD, ssize_t t) override;
    virtual std::unique_ptr<ATNConfigSet> computeReachSet(ATNConfigSet *closure, ssize_t t, bool fullCtx) override;
    virtual bool evalSemanticContext(Ref<SemanticContext> const& pred, Ref<ParserRuleContext> const& parserCallStack,
                                     int alt, bool fullCtx) override;
    virtual void reportAttemptingFullContext(dfa::DFA &dfa, const antlrcpp::BitSet &conflictingAlts, ATNConfigSet *configs,
                                             size_t startIndex, size_t stopIndex) override;
    virtual void reportContextSensitivity(dfa::DFA &dfa, int prediction, ATNConfigSet *configs,
                                          size_t startIndex, size_t stopIndex) override;
    virtual void reportAmbiguity(dfa::DFA &dfa, dfa::DFAState *D, size_t startIndex, size_t stopIndex, bool exact,
                                 const antlrcpp::BitSet &ambigAlts, ATNConfigSet *configs) override;
  };

} // namespace atn
} // namespace antlr4
