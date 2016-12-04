/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/EmptyPredictionContext.h"

#include "atn/SingletonPredictionContext.h"

using namespace antlr4::atn;

SingletonPredictionContext::SingletonPredictionContext(std::weak_ptr<PredictionContext> parent, size_t returnState)
  : PredictionContext(!parent.expired() ? calculateHashCode(parent, returnState) : calculateEmptyHashCode()),
    parent(parent.lock()), returnState(returnState) {
  assert(returnState != ATNState::INVALID_STATE_NUMBER);
}

Ref<SingletonPredictionContext> SingletonPredictionContext::create(std::weak_ptr<PredictionContext> parent,
  size_t returnState) {

  if (returnState == EMPTY_RETURN_STATE && parent.expired()) {
    // someone can pass in the bits of an array ctx that mean $
    return std::dynamic_pointer_cast<SingletonPredictionContext>(EMPTY);
  }
  return std::make_shared<SingletonPredictionContext>(parent, returnState);
}

size_t SingletonPredictionContext::size() const {
  return 1;
}

std::weak_ptr<PredictionContext> SingletonPredictionContext::getParent(size_t index) const {
  assert(index == 0);
  ((void)(index)); // Make Release build happy.
  return parent;
}

size_t SingletonPredictionContext::getReturnState(size_t index) const {
  assert(index == 0);
  ((void)(index)); // Make Release build happy.
  return returnState;
}

bool SingletonPredictionContext::operator == (const PredictionContext &o) const {
  if (this == &o) {
    return true;
  }

  const SingletonPredictionContext *other = dynamic_cast<const SingletonPredictionContext*>(&o);
  if (other == nullptr) {
    return false;
  }

  if (this->hashCode() != other->hashCode()) {
    return false; // can't be same if hash is different
  }

  //return returnState == other->returnState && (!parent.expired() && parent.lock() == other->parent.lock());
  return returnState == other->returnState && (parent != nullptr && *parent == *other->parent);
}

std::string SingletonPredictionContext::toString() const {
  //std::string up = !parent.expired() ? parent.lock()->toString() : "";
  std::string up = parent != nullptr ? parent->toString() : "";
  if (up.length() == 0) {
    if (returnState == EMPTY_RETURN_STATE) {
      return "$";
    }
    return std::to_string(returnState);
  }
  return std::to_string(returnState) + " " + up;
}
