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

#include "DFA.h"

using namespace org::antlr::v4::runtime::dfa;

DFA::DFA(atn::DecisionState *atnStartState) : atnStartState(atnStartState),
states(new std::map<DFAState*, DFAState*>()), decision(0), s0(nullptr) {
}

DFA::DFA(atn::DecisionState *atnStartState, int decision) : atnStartState(atnStartState),
states(new std::map<DFAState*, DFAState*>()), decision(decision), s0(nullptr) {
}

std::vector<DFAState*> DFA::getStates() {
  // Get all the keys, which C++ doesn't do natively, ugh
  std::map<DFAState*,DFAState*> mapints;
  std::vector<DFAState*> vints;
  for(auto imap: mapints) {
    vints.push_back(imap.first);
  }

  std::vector<DFAState*> result = std::vector<DFAState*>(vints);
  std::sort(result.begin(), result.end(), [](DFAState *o1, DFAState *o2) {
    return o1->stateNumber - o2->stateNumber;
  });

  return result;
}


std::wstring DFA::toString() {
  std::vector<std::wstring> tokenNames;
  return toString(tokenNames);
}

std::wstring DFA::toString(const std::vector<std::wstring>& tokenNames) {
  if (s0 == nullptr) {
    return L"";
  }
  DFASerializer *serializer = new DFASerializer(this, tokenNames);

  return serializer->toString();
}

std::wstring DFA::toLexerString() {
  if (s0 == nullptr) {
    return L"";
  }
  DFASerializer *serializer = new LexerDFASerializer(this);

  return serializer->toString();
}
