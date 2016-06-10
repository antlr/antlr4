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

#include "atn/PredicateEvalInfo.h"
#include "atn/LookaheadEventInfo.h"
#include "Parser.h"
#include "atn/ATNConfigSet.h"
#include "support/CPPUtils.h"

#include "atn/ProfilingATNSimulator.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlr4::dfa;
using namespace antlrcpp;

using namespace std::chrono;

ProfilingATNSimulator::ProfilingATNSimulator(Parser *parser)
  : ParserATNSimulator(parser, parser->getInterpreter<ParserATNSimulator>()->atn,
                       parser->getInterpreter<ParserATNSimulator>()->decisionToDFA,
                       parser->getInterpreter<ParserATNSimulator>()->getSharedContextCache()) {
  for (size_t i = 0; i < atn.decisionToState.size(); i++) {
    _decisions.push_back(DecisionInfo(i));
  }
}

int ProfilingATNSimulator::adaptivePredict(TokenStream *input, int decision, Ref<ParserRuleContext> const& outerContext) {
  auto onExit = finally([this](){
    _currentDecision = -1;
  });

  _sllStopIndex = -1;
  _llStopIndex = -1;
  _currentDecision = decision;
  high_resolution_clock::time_point start = high_resolution_clock::now(); // expensive but useful info
  int alt = ParserATNSimulator::adaptivePredict(input, decision, outerContext);
  high_resolution_clock::time_point stop = high_resolution_clock::now();
  _decisions[decision].timeInPrediction += duration_cast<nanoseconds>(stop - start).count();
  _decisions[decision].invocations++;

  long long SLL_k = _sllStopIndex - _startIndex + 1;
  _decisions[decision].SLL_TotalLook += SLL_k;
  _decisions[decision].SLL_MinLook = _decisions[decision].SLL_MinLook == 0 ? SLL_k : std::min(_decisions[decision].SLL_MinLook, SLL_k);
  if (SLL_k > _decisions[decision].SLL_MaxLook) {
    _decisions[decision].SLL_MaxLook = SLL_k;
    _decisions[decision].SLL_MaxLookEvent = std::make_shared<LookaheadEventInfo>(decision, nullptr, alt, input, _startIndex, _sllStopIndex, false);
  }

  if (_llStopIndex >= 0) {
    long long LL_k = _llStopIndex - _startIndex + 1;
    _decisions[decision].LL_TotalLook += LL_k;
    _decisions[decision].LL_MinLook = _decisions[decision].LL_MinLook == 0 ? LL_k : std::min(_decisions[decision].LL_MinLook, LL_k);
    if (LL_k > _decisions[decision].LL_MaxLook) {
      _decisions[decision].LL_MaxLook = LL_k;
      _decisions[decision].LL_MaxLookEvent = std::make_shared<LookaheadEventInfo>(decision, nullptr, alt, input, _startIndex, _llStopIndex, true);
    }
  }

  return alt;
}

DFAState* ProfilingATNSimulator::getExistingTargetState(DFAState *previousD, ssize_t t) {
  // this method is called after each time the input position advances
  // during SLL prediction
  _sllStopIndex = (int)_input->index();

  DFAState *existingTargetState = ParserATNSimulator::getExistingTargetState(previousD, t);
  if (existingTargetState != nullptr) {
    _decisions[_currentDecision].SLL_DFATransitions++; // count only if we transition over a DFA state
    if (existingTargetState == ERROR.get()) {
      _decisions[_currentDecision].errors.push_back(
        ErrorInfo(_currentDecision, previousD->configs.get(), _input, _startIndex, _sllStopIndex, false)
      );
    }
  }

  _currentState = existingTargetState;
  return existingTargetState;
}

DFAState* ProfilingATNSimulator::computeTargetState(DFA &dfa, DFAState *previousD, ssize_t t) {
  DFAState *state = ParserATNSimulator::computeTargetState(dfa, previousD, t);
  _currentState = state;
  return state;
}

std::unique_ptr<ATNConfigSet> ProfilingATNSimulator::computeReachSet(ATNConfigSet *closure, ssize_t t, bool fullCtx) {
  if (fullCtx) {
    // this method is called after each time the input position advances
    // during full context prediction
    _llStopIndex = (int)_input->index();
  }

  std::unique_ptr<ATNConfigSet> reachConfigs = ParserATNSimulator::computeReachSet(closure, t, fullCtx);
  if (fullCtx) {
    _decisions[_currentDecision].LL_ATNTransitions++; // count computation even if error
    if (reachConfigs != nullptr) {
    } else { // no reach on current lookahead symbol. ERROR.
      // TO_DO: does not handle delayed errors per getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule()
      _decisions[_currentDecision].errors.push_back(ErrorInfo(_currentDecision, closure, _input, _startIndex, _llStopIndex, true));
    }
  } else {
    ++_decisions[_currentDecision].SLL_ATNTransitions;
    if (reachConfigs != nullptr) {
    } else { // no reach on current lookahead symbol. ERROR.
      _decisions[_currentDecision].errors.push_back(ErrorInfo(_currentDecision, closure, _input, _startIndex, _sllStopIndex, false));
    }
  }
  return reachConfigs;
}

bool ProfilingATNSimulator::evalSemanticContext(Ref<SemanticContext> const& pred, Ref<ParserRuleContext> const& parserCallStack,
                                                int alt, bool fullCtx) {
  bool result = ParserATNSimulator::evalSemanticContext(pred, parserCallStack, alt, fullCtx);
  if (!(std::dynamic_pointer_cast<SemanticContext::PrecedencePredicate>(pred) != nullptr)) {
    bool fullContext = _llStopIndex >= 0;
    int stopIndex = fullContext ? _llStopIndex : _sllStopIndex;
    _decisions[_currentDecision].predicateEvals.push_back(
      PredicateEvalInfo(_currentDecision, _input, _startIndex, stopIndex, pred, result, alt, fullCtx));
  }

  return result;
}

void ProfilingATNSimulator::reportAttemptingFullContext(DFA &dfa, const BitSet &conflictingAlts, ATNConfigSet *configs,
                                                        size_t startIndex, size_t stopIndex) {
  if (conflictingAlts.count() > 0) {
    conflictingAltResolvedBySLL = conflictingAlts.nextSetBit(0);
  } else {
    conflictingAltResolvedBySLL = configs->getAlts().nextSetBit(0);
  }
  _decisions[_currentDecision].LL_Fallback++;
  ParserATNSimulator::reportAttemptingFullContext(dfa, conflictingAlts, configs, startIndex, stopIndex);
}

void ProfilingATNSimulator::reportContextSensitivity(DFA &dfa, int prediction, ATNConfigSet *configs,
                                                     size_t startIndex, size_t stopIndex) {
  if (prediction != conflictingAltResolvedBySLL) {
    _decisions[_currentDecision].contextSensitivities.push_back(
      ContextSensitivityInfo(_currentDecision, configs, _input, startIndex, stopIndex)
    );
  }
  ParserATNSimulator::reportContextSensitivity(dfa, prediction, configs, startIndex, stopIndex);
}

void ProfilingATNSimulator::reportAmbiguity(DFA &dfa, DFAState *D, size_t startIndex, size_t stopIndex, bool exact,
                                            const BitSet &ambigAlts, ATNConfigSet *configs) {
  int prediction;
  if (ambigAlts.count() > 0) {
    prediction = ambigAlts.nextSetBit(0);
  } else {
    prediction = configs->getAlts().nextSetBit(0);
  }
  if (configs->fullCtx && prediction != conflictingAltResolvedBySLL) {
    // Even though this is an ambiguity we are reporting, we can
    // still detect some context sensitivities.  Both SLL and LL
    // are showing a conflict, hence an ambiguity, but if they resolve
    // to different minimum alternatives we have also identified a
    // context sensitivity.
    _decisions[_currentDecision].contextSensitivities.push_back(
      ContextSensitivityInfo(_currentDecision, configs, _input, startIndex, stopIndex)
    );
  }
  _decisions[_currentDecision].ambiguities.push_back(
    AmbiguityInfo(_currentDecision, configs, ambigAlts, _input, startIndex, stopIndex, configs->fullCtx)
  );
  ParserATNSimulator::reportAmbiguity(dfa, D, startIndex, stopIndex, exact, ambigAlts, configs);
}

std::vector<DecisionInfo> ProfilingATNSimulator::getDecisionInfo() const {
  return _decisions;
}

DFAState* ProfilingATNSimulator::getCurrentState() const {
  return _currentState;
}
