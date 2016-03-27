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

#include "stringconverter.h"

#include "SingletonPredictionContext.h"

using namespace org::antlr::v4::runtime::atn;

SingletonPredictionContext::SingletonPredictionContext(PredictionContext *parent, int returnState) : PredictionContext(parent != nullptr ? calculateHashCode(parent, returnState) : calculateEmptyHashCode()), parent(parent), returnState(returnState) {
  assert(returnState != ATNState::INVALID_STATE_NUMBER);
}

SingletonPredictionContext *SingletonPredictionContext::create(PredictionContext *parent, int returnState) {
  if (returnState == EMPTY_RETURN_STATE && parent == nullptr) {
    // someone can pass in the bits of an array ctx that mean $
    return (atn::SingletonPredictionContext *)EMPTY;
  }
  return new SingletonPredictionContext(parent, returnState);
}

size_t SingletonPredictionContext::size() const {
  return 1;
}

PredictionContext *SingletonPredictionContext::getParent(size_t index) const {
  assert(index == 0);
  return parent;
}

int SingletonPredictionContext::getReturnState(size_t index) const {
  assert(index == 0);
  return returnState;
}

bool SingletonPredictionContext::operator == (PredictionContext *o) const {
  if (this == o) {
    return true;
  }

  SingletonPredictionContext *other = dynamic_cast<SingletonPredictionContext*>(o);
  if (other == nullptr) {
    return false;
  }

  if (this->hashCode() != other->hashCode()) {
    return false; // can't be same if hash is different
  }

  return returnState == other->returnState && (parent != nullptr && parent == other->parent);
}

std::wstring SingletonPredictionContext::toString() const {
  std::wstring up = parent != nullptr ? parent->toString() : L"";
  if (up.length() == 0) {
    if (returnState == EMPTY_RETURN_STATE) {
      return L"$";
    }
    return antlrcpp::StringConverterHelper::toString(returnState);
  }
  return antlrcpp::StringConverterHelper::toString(returnState) + std::wstring(L" ") + up;
}
