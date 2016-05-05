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

#include "DFAState.h"
#include "DFASerializer.h"
#include "LexerDFASerializer.h"
#include "CPPUtils.h"
#include "StarLoopEntryState.h"
#include "ATNConfigSet.h"

#include "DFA.h"

using namespace org::antlr::v4::runtime;
using namespace org::antlr::v4::runtime::dfa;
using namespace antlrcpp;

DFA::DFA(atn::DecisionState *atnStartState) : DFA(atnStartState, 0) {
}

DFA::DFA(atn::DecisionState *atnStartState, int decision)
  : atnStartState(atnStartState), decision(decision), s0(nullptr) {

  _precedenceDfa = false;
  if (is<atn::StarLoopEntryState *>(atnStartState)) {
    if (static_cast<atn::StarLoopEntryState *>(atnStartState)->isPrecedenceDecision) {
      _precedenceDfa = true;
      DFAState *precedenceState = new DFAState(std::make_shared<atn::ATNConfigSet>()); // TODO: mem leak
      precedenceState->isAcceptState = false;
      precedenceState->requiresFullContext = false;
      s0 = precedenceState;
    }
  }
}

DFA::DFA(DFA &&other) : atnStartState(std::move(other.atnStartState)), decision(std::move(other.decision)) {
  states = std::move(other.states);
  s0 = std::move(other.s0);
  _precedenceDfa = std::move(other._precedenceDfa);
}

DFA::DFA(const DFA &other) : atnStartState(other.atnStartState), decision(other.decision) {
  states = other.states;
  s0 = other.s0;
  _precedenceDfa = other._precedenceDfa;
}

DFA::~DFA() {
  for (auto state : states) {
    delete state.second;
  }
}

bool DFA::isPrecedenceDfa() const {
  return _precedenceDfa;
}

DFAState* DFA::getPrecedenceStartState(int precedence) const {
  if (!isPrecedenceDfa()) {
    throw IllegalStateException("Only precedence DFAs may contain a precedence start state.");
  }

  // s0.edges is never null for a precedence DFA
  if (precedence < 0 || precedence >= (int)s0->edges.size()) {
    return nullptr;
  }

  return s0->edges[precedence];
}

void DFA::setPrecedenceStartState(int precedence, DFAState *startState) {
  if (!isPrecedenceDfa()) {
    throw IllegalStateException("Only precedence DFAs may contain a precedence start state.");
  }

  if (precedence < 0) {
    return;
  }

  // synchronization on s0 here is ok. when the DFA is turned into a
  // precedence DFA, s0 will be initialized once and not updated again
  std::unique_lock<std::recursive_mutex> lock(_lock);
  {
    // s0.edges is never null for a precedence DFA
    if (precedence >= (int)s0->edges.size()) {
      s0->edges.resize(precedence + 1);
    }

    s0->edges[precedence] = startState;
  }
}

std::vector<DFAState *> DFA::getStates() const {
  std::vector<DFAState *> result;
  for (auto state : states)
    result.push_back(state.first);

  std::sort(result.begin(), result.end(), [](DFAState *o1, DFAState *o2) {
    return o1->stateNumber - o2->stateNumber;
  });

  return result;
}

std::wstring DFA::toString(const std::vector<std::wstring> &tokenNames) {
  if (s0 == nullptr) {
    return L"";
  }
  DFASerializer serializer(this, tokenNames);

  return serializer.toString();
}

std::wstring DFA::toString(Ref<Vocabulary> vocabulary) const {
  if (s0 == nullptr) {
    return L"";
  }

  DFASerializer serializer(this, vocabulary);
  return serializer.toString();
}

std::wstring DFA::toLexerString() {
  if (s0 == nullptr) {
    return L"";
  }
  LexerDFASerializer serializer(this);

  return serializer.toString();
}

