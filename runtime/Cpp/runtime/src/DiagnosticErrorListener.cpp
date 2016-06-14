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

#include "atn/PredictionContext.h"
#include "atn/ATNConfig.h"
#include "atn/ATNConfigSet.h"
#include "Parser.h"
#include "misc/Interval.h"
#include "dfa/DFA.h"

#include "DiagnosticErrorListener.h"

using namespace antlr4;

DiagnosticErrorListener::DiagnosticErrorListener() : DiagnosticErrorListener(true) {
}

DiagnosticErrorListener::DiagnosticErrorListener(bool exactOnly) : exactOnly(exactOnly) {
}

void DiagnosticErrorListener::reportAmbiguity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,
   bool exact, const antlrcpp::BitSet &ambigAlts, atn::ATNConfigSet *configs) {
  if (exactOnly && !exact) {
    return;
  }

  std::string decision = getDecisionDescription(recognizer, dfa);
  antlrcpp::BitSet conflictingAlts = getConflictingAlts(ambigAlts, configs);
  std::string text = recognizer->getTokenStream()->getText(misc::Interval((int)startIndex, (int)stopIndex));
  std::string message = "reportAmbiguity d=" + decision + ": ambigAlts=" + conflictingAlts.toString() +
    ", input='" + text + "'";

  recognizer->notifyErrorListeners(message);
}

void DiagnosticErrorListener::reportAttemptingFullContext(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex,
  size_t stopIndex, const antlrcpp::BitSet &/*conflictingAlts*/, atn::ATNConfigSet * /*configs*/) {
  std::string decision = getDecisionDescription(recognizer, dfa);
  std::string text = recognizer->getTokenStream()->getText(misc::Interval((int)startIndex, (int)stopIndex));
  std::string message = "reportAttemptingFullContext d=" + decision + ", input='" + text + "'";
  recognizer->notifyErrorListeners(message);
}

void DiagnosticErrorListener::reportContextSensitivity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex,
  size_t stopIndex, int /*prediction*/, atn::ATNConfigSet * /*configs*/) {
  std::string decision = getDecisionDescription(recognizer, dfa);
  std::string text = recognizer->getTokenStream()->getText(misc::Interval((int)startIndex, (int)stopIndex));
  std::string message = "reportContextSensitivity d=" + decision + ", input='" + text + "'";
  recognizer->notifyErrorListeners(message);
}

std::string DiagnosticErrorListener::getDecisionDescription(Parser *recognizer, const dfa::DFA &dfa) {
  int decision = dfa.decision;
  int ruleIndex = ((atn::ATNState*)dfa.atnStartState)->ruleIndex;

  const std::vector<std::string>& ruleNames = recognizer->getRuleNames();
  if (ruleIndex < 0 || ruleIndex >= (int)ruleNames.size()) {
    return std::to_string(decision);
  }

  std::string ruleName = ruleNames[(size_t)ruleIndex];
  if (ruleName == "" || ruleName.empty())  {
    return std::to_string(decision);
  }

  return std::to_string(decision) + " (" + ruleName + ")";
}

antlrcpp::BitSet DiagnosticErrorListener::getConflictingAlts(const antlrcpp::BitSet &reportedAlts,
                                                             atn::ATNConfigSet *configs) {
  if (reportedAlts.count() > 0) { // Not exactly like the original Java code, but this listener is only used
                                  // in the TestRig (where it never provides a good alt set), so it's probably ok so.
    return reportedAlts;
  }

  antlrcpp::BitSet result;
  for (auto &config : configs->configs) {
    result.set((size_t)config->alt);
  }

  return result;
}
