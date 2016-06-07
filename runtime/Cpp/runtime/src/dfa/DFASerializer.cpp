/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Dan McLaughlin
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

#include "dfa/DFA.h"
#include "Vocabulary.h"

#include "dfa/DFASerializer.h"

using namespace antlr4::dfa;

DFASerializer::DFASerializer(const DFA *dfa, const std::vector<std::string>& tokenNames)
  : DFASerializer(dfa, Vocabulary::fromTokenNames(tokenNames)) {
}

DFASerializer::DFASerializer(const DFA *dfa, const Vocabulary &vocabulary) : _dfa(dfa), _vocabulary(vocabulary) {
}

std::string DFASerializer::toString() const {
  if (_dfa->s0 == nullptr) {
    return "";
  }

  std::stringstream ss;
  std::vector<DFAState *> states = _dfa->getStates();
  for (auto s : states) {
    for (size_t i = 0; i < s->edges.size(); i++) {
      DFAState *t = s->edges[i];
      if (t != nullptr && t->stateNumber != INT32_MAX) {
        ss << getStateString(s);
        std::string label = getEdgeLabel(i);
        ss << "-" << label << "->" << getStateString(t) << "\n";
      }
    }
  }

  return ss.str();
}

std::string DFASerializer::getEdgeLabel(size_t i) const {
  return _vocabulary.getDisplayName((int)i - 1);
}

std::string DFASerializer::getStateString(DFAState *s) const {
  size_t n = (size_t)s->stateNumber;

  const std::string baseStateStr = std::string(s->isAcceptState ? ":" : "") + "s" + std::to_string(n) +
    (s->requiresFullContext ? "^" : "");
  
  if (s->isAcceptState) {
    if (!s->predicates.empty()) {
      std::string buf;
      for (size_t i = 0; i < s->predicates.size(); i++) {
        buf.append(s->predicates[i]->toString());
      }
      return baseStateStr + "=>" + buf;
    } else {
      return baseStateStr + "=>" + std::to_string(s->prediction);
    }
  } else {
    return baseStateStr;
  }
}
