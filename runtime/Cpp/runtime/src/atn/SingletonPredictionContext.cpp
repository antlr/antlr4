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

#include "atn/EmptyPredictionContext.h"

#include "atn/SingletonPredictionContext.h"

using namespace antlr4::atn;

SingletonPredictionContext::SingletonPredictionContext(std::weak_ptr<PredictionContext> parent, int returnState)
  : PredictionContext(!parent.expired() ? calculateHashCode(parent, returnState) : calculateEmptyHashCode()),
    parent(parent.lock()), returnState(returnState) {
  assert(returnState != ATNState::INVALID_STATE_NUMBER);
}

Ref<SingletonPredictionContext> SingletonPredictionContext::create(std::weak_ptr<PredictionContext> parent,
                                                                   int returnState) {
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
  ((void)(index)); // Make Release build happy
  return parent;
}

int SingletonPredictionContext::getReturnState(size_t index) const {
  assert(index == 0);
  ((void)(index)); // Make Release build happy
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
