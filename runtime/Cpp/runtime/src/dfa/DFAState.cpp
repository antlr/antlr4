/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include <algorithm>
#include <cstddef>
#include <memory>
#include <sstream>
#include <string>

#include "atn/ATNConfigSet.h"
#include "atn/SemanticContext.h"
#include "atn/ATNConfig.h"
#include "misc/MurmurHash.h"

#include "dfa/DFAState.h"

using namespace antlr4::dfa;
using namespace antlr4::atn;

DFAState::~DFAState() {
  delete[] _edges.load(std::memory_order_relaxed);
}

void DFAState::setEdge(size_t index, size_t minSize, DFAState *target) {
  std::atomic<DFAState *> *edges = _edges.load(std::memory_order_relaxed);
  size_t count = _edgeCount.load(std::memory_order_relaxed);

  if (edges == nullptr || index >= count) {
    size_t newCount = std::max(minSize, index + 1);
    auto *newEdges = new std::atomic<DFAState *>[newCount];
    for (size_t i = 0; i < count; ++i) {
      newEdges[i].store(edges[i].load(std::memory_order_relaxed), std::memory_order_relaxed);
    }
    for (size_t i = count; i < newCount; ++i) {
      newEdges[i].store(nullptr, std::memory_order_relaxed);
    }
    // Publish the (fully initialized) table: a lock-free getEdge() that
    // observes this pointer with acquire is guaranteed to see the slot
    // contents and the updated count.
    _edgeCount.store(newCount, std::memory_order_release);
    _edges.store(newEdges, std::memory_order_release);
    // Only the precedence start state ever reaches this with a non-null
    // `edges`, and it is read exclusively under the owning DFA's edgeMutex()
    // (never via the lock-free getEdge path), so freeing the old table here
    // cannot race a concurrent reader.
    delete[] edges;
    edges = newEdges;
  }

  edges[index].store(target, std::memory_order_release);
}

std::string DFAState::PredPrediction::toString() const {
  return std::string("(") + pred->toString() + ", " + std::to_string(alt) + ")";
}

std::set<size_t> DFAState::getAltSet() const {
  std::set<size_t> alts;
  if (configs != nullptr) {
    for (size_t i = 0; i < configs->size(); i++) {
      alts.insert(configs->get(i)->alt);
    }
  }
  return alts;
}

size_t DFAState::hashCode() const {
  return configs != nullptr ? configs->hashCode() : 0;
}

bool DFAState::equals(const DFAState &other) const {
  if (this == std::addressof(other)) {
    return true;
  }
  return configs == other.configs ||
         (configs != nullptr && other.configs != nullptr && *configs == *other.configs);
}

std::string DFAState::toString() const {
  std::stringstream ss;
  ss << stateNumber;
  if (configs) {
    ss << ":" << configs->toString();
  }
  if (isAcceptState) {
    ss << "=>";
    if (!predicates.empty()) {
      for (size_t i = 0; i < predicates.size(); i++) {
        ss << predicates[i].toString();
      }
    } else {
      ss << prediction;
    }
  }
  return ss.str();
}
