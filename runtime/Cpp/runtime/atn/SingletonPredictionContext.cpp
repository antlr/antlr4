#include "SingletonPredictionContext.h"
#include "ATNState.h"
#include <assert.h>

/*
 * [The "BSD license"]
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace atn {

                    SingletonPredictionContext::SingletonPredictionContext(PredictionContext *parent, int returnState) : PredictionContext(parent != nullptr ? calculateHashCode(parent, returnState) : calculateEmptyHashCode()), parent(parent), returnState(returnState) {
                        assert(returnState != ATNState::INVALID_STATE_NUMBER);
                    }

                    atn::SingletonPredictionContext *SingletonPredictionContext::create(PredictionContext *parent, int returnState) {
                        if (returnState == EMPTY_RETURN_STATE && parent == nullptr) {
                            // someone can pass in the bits of an array ctx that mean $
                            return (atn::SingletonPredictionContext *)EMPTY;
                        }
                        return new SingletonPredictionContext(parent, returnState);
                    }

                    int SingletonPredictionContext::size() {
                        return 1;
                    }

                    atn::PredictionContext *SingletonPredictionContext::getParent(int index) {
                        assert(index == 0);
                        return parent;
                    }

                    int SingletonPredictionContext::getReturnState(int index) {
                        assert(index == 0);
                        return returnState;
                    }

                    bool SingletonPredictionContext::equals(void *o) {
                        if (this == o) {
                            return true;
                        } else if (!(/*dynamic_cast<SingletonPredictionContext*>(o)*/o != nullptr)) {
                            return false;
                        }

                        if (this->hashCode() != ((SingletonPredictionContext*)o)->hashCode()) {
                            return false; // can't be same if hash is different
                        }

                        SingletonPredictionContext *s = static_cast<SingletonPredictionContext*>(o);
                        return returnState == s->returnState && (parent != nullptr && parent->equals(s->parent));
                    }

                    std::wstring SingletonPredictionContext::toString() {
                        std::wstring up = parent != nullptr ? parent->toString() : L"";
                        if (up.length() == 0) {
                            if (returnState == EMPTY_RETURN_STATE) {
                                return L"$";
                            }
                            return antlrcpp::StringConverterHelper::toString(returnState);
                        }
                        return antlrcpp::StringConverterHelper::toString(returnState) + std::wstring(L" ") + up;
                    }
                }
            }
        }
    }
}
