/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include <sstream>
#include <vector>
#include <string>
#include <cstddef>
#include "dfa/DFA.h"
#include "Vocabulary.h"

#include "dfa/DFASerializer.h"

using namespace antlr4::dfa;

DFASerializer::DFASerializer(const DFA *dfa, const Vocabulary &vocabulary) : _dfa(dfa), _vocabulary(vocabulary) {
}

std::string DFASerializer::toString() const {
  if (_dfa->s0 == nullptr) {
    return "";
  }

  std::stringstream ss;
  std::vector<DFAState *> states = _dfa->getStates();
  for (auto *s : states) {
    for (size_t i = 0; i < s->edgeCount(); i++) {
      DFAState *t = s->getEdge(i);
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
  // Edges are indexed by t + 1 (EOF in slot 0), so shift back to the token
  // type; i == 0 wraps to Token::EOF, which the vocabulary displays as "EOF".
  return _vocabulary.getDisplayName(i - 1);
}

std::string DFASerializer::getStateString(DFAState *s) const {
  size_t n = s->stateNumber;

  const std::string baseStateStr = std::string(s->isAcceptState ? ":" : "") + "s" + std::to_string(n) +
    (s->requiresFullContext ? "^" : "");

  if (s->isAcceptState) {
    if (!s->predicates.empty()) {
      std::string buf;
      for (size_t i = 0; i < s->predicates.size(); i++) {
        buf.append(s->predicates[i].toString());
      }
      return baseStateStr + "=>" + buf;
    } else {
      return baseStateStr + "=>" + std::to_string(s->prediction);
    }
  } else {
    return baseStateStr;
  }
}
