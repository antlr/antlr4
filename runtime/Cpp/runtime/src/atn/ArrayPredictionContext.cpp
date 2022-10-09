/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/ArrayPredictionContext.h"

#include <cstring>

#include "atn/SingletonPredictionContext.h"
#include "misc/MurmurHash.h"
#include "support/Casts.h"

using namespace antlr4::atn;
using namespace antlr4::misc;
using namespace antlrcpp;

namespace {

  bool cachedHashCodeEqual(size_t lhs, size_t rhs) {
    return lhs == rhs || lhs == 0 || rhs == 0;
  }

  bool predictionContextEqual(const Ref<const PredictionContext> &lhs, const Ref<const PredictionContext> &rhs) {
    return *lhs == *rhs;
  }

}

ArrayPredictionContext::ArrayPredictionContext(const SingletonPredictionContext &predictionContext)
    : ArrayPredictionContext({ predictionContext.parent }, { predictionContext.returnState }) {}

ArrayPredictionContext::ArrayPredictionContext(std::vector<Ref<const PredictionContext>> parents,
                                               std::vector<size_t> returnStates)
    : PredictionContext(PredictionContextType::ARRAY), parents(std::move(parents)), returnStates(std::move(returnStates)) {
  assert(this->parents.size() > 0);
  assert(this->returnStates.size() > 0);
  assert(this->parents.size() == this->returnStates.size());
}

bool ArrayPredictionContext::isEmpty() const {
  // Since EMPTY_RETURN_STATE can only appear in the last position, we don't need to verify that size == 1.
  return returnStates[0] == EMPTY_RETURN_STATE;
}

size_t ArrayPredictionContext::size() const {
  return returnStates.size();
}

const Ref<const PredictionContext>& ArrayPredictionContext::getParent(size_t index) const {
  return parents[index];
}

size_t ArrayPredictionContext::getReturnState(size_t index) const {
  return returnStates[index];
}

size_t ArrayPredictionContext::hashCodeImpl() const {
  size_t hash = MurmurHash::initialize();
  hash = MurmurHash::update(hash, static_cast<size_t>(getContextType()));
  for (const auto &parent : parents) {
    hash = MurmurHash::update(hash, parent);
  }
  for (const auto &returnState : returnStates) {
    hash = MurmurHash::update(hash, returnState);
  }
  return MurmurHash::finish(hash, 1 + parents.size() + returnStates.size());
}

bool ArrayPredictionContext::equals(const PredictionContext &other) const {
  if (this == std::addressof(other)) {
    return true;
  }
  if (getContextType() != other.getContextType()) {
    return false;
  }
  const auto &array = downCast<const ArrayPredictionContext&>(other);
  return returnStates.size() == array.returnStates.size() &&
         parents.size() == array.parents.size() &&
         cachedHashCodeEqual(cachedHashCode(), array.cachedHashCode()) &&
         std::memcmp(returnStates.data(), array.returnStates.data(), returnStates.size() * sizeof(decltype(returnStates)::value_type)) == 0 &&
         std::equal(parents.begin(), parents.end(), array.parents.begin(), predictionContextEqual);
}

std::string ArrayPredictionContext::toString() const {
  if (isEmpty()) {
    return "[]";
  }

  std::stringstream ss;
  ss << "[";
  for (size_t i = 0; i < returnStates.size(); i++) {
    if (i > 0) {
      ss << ", ";
    }
    if (returnStates[i] == EMPTY_RETURN_STATE) {
      ss << "$";
      continue;
    }
    ss << returnStates[i];
    if (parents[i] != nullptr) {
      ss << " " << parents[i]->toString();
    } else {
      ss << "nul";
    }
  }
  ss << "]";
  return ss.str();
}
