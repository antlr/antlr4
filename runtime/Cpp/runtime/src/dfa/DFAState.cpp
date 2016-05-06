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

#include "ATNConfigSet.h"
#include "SemanticContext.h"
#include "ATNConfig.h"
#include "MurmurHash.h"

#include "DFAState.h"

using namespace org::antlr::v4::runtime::dfa;
using namespace org::antlr::v4::runtime::atn;

DFAState::PredPrediction::PredPrediction(Ref<SemanticContext> pred, int alt) : pred(pred) {
  InitializeInstanceFields();
  this->alt = alt;
}

std::wstring DFAState::PredPrediction::toString() {
  return std::wstring(L"(") + pred->toString() + std::wstring(L", ") + std::to_wstring(alt) + std::wstring(L")");
}

void DFAState::PredPrediction::InitializeInstanceFields() {
  alt = 0;
}

DFAState::DFAState() {
  InitializeInstanceFields();
}

DFAState::DFAState(int state) : DFAState() {
  stateNumber = state;
}

DFAState::DFAState(Ref<ATNConfigSet> configs) : DFAState() {
  this->configs = configs;
}

DFAState::~DFAState() {
  for (auto predicate : predicates) {
    delete predicate;
  }
}

std::set<int> DFAState::getAltSet() {
  std::set<int> alts;
  if (configs != nullptr) {
    for (size_t i = 0; i < configs->size(); i++) {
      alts.insert(configs->get(i)->alt);
    }
  }
  return alts;
}

size_t DFAState::hashCode() {
  size_t hash = misc::MurmurHash::initialize(7);
  hash = misc::MurmurHash::update(hash, configs->hashCode());
  hash = misc::MurmurHash::finish(hash, 1);
  return hash;
}

bool DFAState::operator == (const DFAState &o) {
  // compare set of ATN configurations in this set with other
  if (this == &o) {
    return true;
  }

  return configs == o.configs;
}

std::wstring DFAState::toString() {
  std::wstringstream ss;
  ss << stateNumber;
  if (configs) {
    ss << L":" << configs->toString();
  }
  if (isAcceptState) {
    ss << L" => ";
    if (!predicates.empty()) {
      for (size_t i = 0; i < predicates.size(); i++) {
        ss << predicates[i]->toString();
      }
    } else {
      ss << prediction;
    }
  }
  return ss.str();
}

void DFAState::InitializeInstanceFields() {
  stateNumber = -1;
  isAcceptState = false;
  prediction = 0;
  requiresFullContext = false;
}
