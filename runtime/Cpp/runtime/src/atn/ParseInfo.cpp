/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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

#include "atn/ProfilingATNSimulator.h"
#include "dfa/DFA.h"

#include "atn/ParseInfo.h"

using namespace antlr4::atn;

ParseInfo::ParseInfo(ProfilingATNSimulator *atnSimulator) : _atnSimulator(atnSimulator) {
}

std::vector<DecisionInfo> ParseInfo::getDecisionInfo() {
  return _atnSimulator->getDecisionInfo();
}

std::vector<size_t> ParseInfo::getLLDecisions() {
  std::vector<DecisionInfo> decisions = _atnSimulator->getDecisionInfo();
  std::vector<size_t> LL;
  for (size_t i = 0; i < decisions.size(); ++i) {
    long long fallBack = decisions[i].LL_Fallback;
    if (fallBack > 0) {
      LL.push_back(i);
    }
  }
  return LL;
}

long long ParseInfo::getTotalTimeInPrediction() {
  std::vector<DecisionInfo> decisions = _atnSimulator->getDecisionInfo();
  long long t = 0;
  for (size_t i = 0; i < decisions.size(); ++i) {
    t += decisions[i].timeInPrediction;
  }
  return t;
}

long long ParseInfo::getTotalSLLLookaheadOps() {
  std::vector<DecisionInfo> decisions = _atnSimulator->getDecisionInfo();
  long long k = 0;
  for (size_t i = 0; i < decisions.size(); ++i) {
    k += decisions[i].SLL_TotalLook;
  }
  return k;
}

long long ParseInfo::getTotalLLLookaheadOps() {
  std::vector<DecisionInfo> decisions = _atnSimulator->getDecisionInfo();
  long long k = 0;
  for (size_t i = 0; i < decisions.size(); i++) {
    k += decisions[i].LL_TotalLook;
  }
  return k;
}

long long ParseInfo::getTotalSLLATNLookaheadOps() {
  std::vector<DecisionInfo> decisions = _atnSimulator->getDecisionInfo();
  long long k = 0;
  for (size_t i = 0; i < decisions.size(); ++i) {
    k += decisions[i].SLL_ATNTransitions;
  }
  return k;
}

long long ParseInfo::getTotalLLATNLookaheadOps() {
  std::vector<DecisionInfo> decisions = _atnSimulator->getDecisionInfo();
  long long k = 0;
  for (size_t i = 0; i < decisions.size(); ++i) {
    k += decisions[i].LL_ATNTransitions;
  }
  return k;
}

long long ParseInfo::getTotalATNLookaheadOps() {
  std::vector<DecisionInfo> decisions = _atnSimulator->getDecisionInfo();
  long long k = 0;
  for (size_t i = 0; i < decisions.size(); ++i) {
    k += decisions[i].SLL_ATNTransitions;
    k += decisions[i].LL_ATNTransitions;
  }
  return k;
}

size_t ParseInfo::getDFASize() {
  size_t n = 0;
  std::vector<dfa::DFA> decisionToDFA = _atnSimulator->decisionToDFA;
  for (size_t i = 0; i < decisionToDFA.size(); ++i) {
    n += getDFASize(i);
  }
  return n;
}

size_t ParseInfo::getDFASize(size_t decision) {
  dfa::DFA &decisionToDFA = _atnSimulator->decisionToDFA[decision];
  return decisionToDFA.states.size();
}
