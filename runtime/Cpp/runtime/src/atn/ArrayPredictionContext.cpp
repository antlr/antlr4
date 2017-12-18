/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "support/Arrays.h"
#include "atn/SingletonPredictionContext.h"

#include "atn/ArrayPredictionContext.h"

using namespace antlr4::atn;

PredictionContextItem::PredictionContextItem(const Ref<PredictionContext>& p, size_t s)
  : parent(p),
    returnState(s) { }

bool PredictionContextItem::operator == (const PredictionContextItem& o) const {
    return returnState == o.returnState && *parent == *o.parent;
}

ArrayPredictionContext::ArrayPredictionContext(Ref<SingletonPredictionContext> const& a)
  : PredictionContext(a->cachedHashCode),
    contexts(1, PredictionContextItem(a->parent, a->returnState)) {
   assert(a);
}

ArrayPredictionContext::ArrayPredictionContext(std::vector<PredictionContextItem>&& contexts_)
  : PredictionContext(calculateHashCode(contexts_)),
    contexts(std::move(contexts_)) {
    assert(contexts.size() > 0);
}

ArrayPredictionContext::~ArrayPredictionContext() {
}

bool ArrayPredictionContext::isEmptyContext() const {
  // Difference from Java runtime. Array context must contain at least 2 items.
  return false;
}

bool ArrayPredictionContext::isSingletonContext() const {
  return false;
}

size_t ArrayPredictionContext::size() const {
  return contexts.size();
}

Ref<PredictionContext> ArrayPredictionContext::getParent(size_t index) const {
  return contexts[index].parent;
}

size_t ArrayPredictionContext::getReturnState(size_t index) const {
  return contexts[index].returnState;
}

bool ArrayPredictionContext::operator == (PredictionContext const& o) const {
  if (this == &o) {
    return true;
  }

  if (hashCode() != o.hashCode()) { return false; } // can't be same if hash is different

  const ArrayPredictionContext *other = dynamic_cast<const ArrayPredictionContext*>(&o);
  if (other == nullptr) { return false; }

  return antlrcpp::Arrays::equals(contexts, other->contexts);
}

std::string ArrayPredictionContext::toString() const {
  if (isEmptyContext()) {
    return "[]";
  }

  std::stringstream ss;
  ss << "[";
  for (size_t i = 0; i < contexts.size(); i++) {
    if (i > 0) {
      ss << ", ";
    }
    if (contexts[i].returnState == EMPTY_RETURN_STATE) {
      ss << "$";
      continue;
    }
    ss << contexts[i].returnState;
    if (contexts[i].parent != nullptr) {
      ss << " " << contexts[i].parent->toString();
    } else {
      ss << "nul";
    }
  }
  ss << "]";
  return ss.str();
}
