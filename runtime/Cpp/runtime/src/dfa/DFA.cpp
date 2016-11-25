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

#include "dfa/DFASerializer.h"
#include "dfa/LexerDFASerializer.h"
#include "support/CPPUtils.h"
#include "atn/StarLoopEntryState.h"
#include "atn/ATNConfigSet.h"

#include "dfa/DFA.h"

using namespace antlr4;
using namespace antlr4::dfa;
using namespace antlrcpp;

DFA::DFA(atn::DecisionState *atnStartState) : DFA(atnStartState, 0) {
}

DFA::DFA(atn::DecisionState *atnStartState, size_t decision)
  : atnStartState(atnStartState), s0(nullptr), decision(decision) {

  _precedenceDfa = false;
  if (is<atn::StarLoopEntryState *>(atnStartState)) {
    if (static_cast<atn::StarLoopEntryState *>(atnStartState)->isPrecedenceDecision) {
      _precedenceDfa = true;
      s0 = new DFAState(std::unique_ptr<atn::ATNConfigSet>(new atn::ATNConfigSet()));
      _s0Shadow = s0;
      s0->isAcceptState = false;
      s0->requiresFullContext = false;
    }
  }
}

DFA::DFA(DFA &&other) : atnStartState(std::move(other.atnStartState)), decision(std::move(other.decision)) {
  // Source states are implicitly cleared by the move.
  states = std::move(other.states);

  // Manually move s0 pointers.
  s0 = other.s0;
  other.s0 = nullptr;
  _s0Shadow = other._s0Shadow;
  other._s0Shadow = nullptr;
  
  _precedenceDfa = other._precedenceDfa;
}

DFA::~DFA() {
  // ml: s0 can be set either in our constructor or by external code, so we need a way to track our own creation.
  // We could use a shared pointer again (and force that on all external assignments etc.) or just shadow it.
  // I hesitate moving s0 to the normal states list as this might conflict with consumers of that list.
  delete _s0Shadow;

  for (auto state : states) {
    delete state;
  }
}

bool DFA::isPrecedenceDfa() const {
  return _precedenceDfa;
}

DFAState* DFA::getPrecedenceStartState(int precedence) const {
  assert(_precedenceDfa); // Only precedence DFAs may contain a precedence start state.

  auto iterator = s0->edges.find(precedence);
  if (iterator == s0->edges.end())
    return nullptr;

  return iterator->second;
}

void DFA::setPrecedenceStartState(int precedence, DFAState *startState, std::recursive_mutex &mutex) {
  if (!isPrecedenceDfa()) {
    throw IllegalStateException("Only precedence DFAs may contain a precedence start state.");
  }

  if (precedence < 0) {
    return;
  }

  {
    std::unique_lock<std::recursive_mutex> lock(mutex);
    s0->edges[precedence] = startState;
  }
}

std::vector<DFAState *> DFA::getStates() const {
  std::vector<DFAState *> result;
  for (auto state : states)
    result.push_back(state);

  std::sort(result.begin(), result.end(), [](DFAState *o1, DFAState *o2) -> bool {
    return o1->stateNumber < o2->stateNumber;
  });

  return result;
}

std::string DFA::toString(const std::vector<std::string> &tokenNames) {
  if (s0 == nullptr) {
    return "";
  }
  DFASerializer serializer(this, tokenNames);

  return serializer.toString();
}

std::string DFA::toString(const Vocabulary &vocabulary) const {
  if (s0 == nullptr) {
    return "";
  }

  DFASerializer serializer(this, vocabulary);
  return serializer.toString();
}

std::string DFA::toLexerString() {
  if (s0 == nullptr) {
    return "";
  }
  LexerDFASerializer serializer(this);

  return serializer.toString();
}

