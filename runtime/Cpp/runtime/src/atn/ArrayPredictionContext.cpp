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

#include "support/Arrays.h"
#include "atn/SingletonPredictionContext.h"

#include "atn/ArrayPredictionContext.h"

using namespace antlr4::atn;

ArrayPredictionContext::ArrayPredictionContext(Ref<SingletonPredictionContext> const& a)
  : ArrayPredictionContext({ a->parent }, { a->returnState }) {
}

ArrayPredictionContext::ArrayPredictionContext(std::vector<std::weak_ptr<PredictionContext>> parents_,
                                               std::vector<int> const& returnStates)
  : PredictionContext(calculateHashCode(parents_, returnStates)), parents(makeRef(parents_)), returnStates(returnStates) {
    assert(parents.size() > 0);
    assert(returnStates.size() > 0);
}

bool ArrayPredictionContext::isEmpty() const {
  // Since EMPTY_RETURN_STATE can only appear in the last position, we don't need to verify that size == 1.
  return returnStates[0] == EMPTY_RETURN_STATE;
}

size_t ArrayPredictionContext::size() const {
  return returnStates.size();
}

std::weak_ptr<PredictionContext> ArrayPredictionContext::getParent(size_t index) const {
  return parents[index];
}

int ArrayPredictionContext::getReturnState(size_t index) const {
  return returnStates[index];
}

bool ArrayPredictionContext::operator == (PredictionContext const& o) const {
  if (this == &o) {
    return true;
  }

  const ArrayPredictionContext *other = dynamic_cast<const ArrayPredictionContext*>(&o);
  if (other == nullptr || hashCode() != other->hashCode()) {
    return false; // can't be same if hash is different
  }

  return antlrcpp::Arrays::equals(returnStates, other->returnStates) &&
    antlrcpp::Arrays::equals(parents, other->parents);
}

std::string ArrayPredictionContext::toString() {
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

std::vector<Ref<PredictionContext>> ArrayPredictionContext::makeRef(const std::vector<std::weak_ptr<PredictionContext> > &input) {
  std::vector<Ref<PredictionContext>> result;
  for (auto element : input) {
    result.push_back(element.lock());
  }
  return result;
}
