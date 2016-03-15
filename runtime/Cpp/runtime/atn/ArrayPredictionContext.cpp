#include <exception>

#include "ArrayPredictionContext.h"
#include "StringBuilder.h"
#include "Arrays.h"

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
#ifdef TODO
                    //the base class hash code is gettings set to 0 here, what do we want?
                    stopffds
#endif
                    ArrayPredictionContext::ArrayPredictionContext(SingletonPredictionContext *a) : PredictionContext(0) {
                    }

                    ArrayPredictionContext::ArrayPredictionContext(std::PredictionContext *parents, int returnStates[]) : PredictionContext(calculateHashCode(parents, *returnStates))/*, parents(parents)*/, returnStates(*returnStates) {
#ifdef TODO
//                        assert(parents != nullptr && sizeof(parents) / sizeof(parents[0]) > 0);
//                        assert(returnStates != nullptr && sizeof(returnStates) / sizeof(returnStates[0]) > 0);
                        // Setup the parents variable correctly since we're not setting it in the constructor line above
                        // std::vector<PredictionContext*>
#endif
                                        //		System.err.println("CREATE ARRAY: "+Arrays.toString(parents)+", "+Arrays.toString(returnStates));
                    }
                    ArrayPredictionContext::ArrayPredictionContext(std::vector<PredictionContext *>parents,
                                                                   const std::vector<int> returnStates) : PredictionContext(0) {
                        throw new TODOException(L"ArrayPredictionContext::ArrayPredictionContext");
                    }
                    bool ArrayPredictionContext::isEmpty() {
                        // since EMPTY_RETURN_STATE can only appear in the last position, we
                        // don't need to verify that size==1
                        return returnStates[0] == EMPTY_RETURN_STATE;
                    }

                    int ArrayPredictionContext::size() {
                        return (int)returnStates.size();
                    }

                    atn::PredictionContext *ArrayPredictionContext::getParent(int index) {
                        return parents->at(index);
                    }

                    int ArrayPredictionContext::getReturnState(int index) {
                        return returnStates[index];
                    }

                    bool ArrayPredictionContext::equals(void *o) {
                        if (this == o) {
                            return true;
                        } else if (!((ArrayPredictionContext*)o/*dynamic_cast<ArrayPredictionContext*>(o)*/ != nullptr)) {
                            return false;
                        }

                        if (this->hashCode() != ((ArrayPredictionContext*)o)->hashCode()) {
                            return false; // can't be same if hash is different
                        }

                        ArrayPredictionContext *a = static_cast<ArrayPredictionContext*>(o);
                        return antlrcpp::Arrays::equals(returnStates, a->returnStates) && antlrcpp::Arrays::equals(&parents, &a->parents);
                    }

                    std::wstring ArrayPredictionContext::toString() {
                        if (isEmpty()) {
                            return L"[]";
                        }
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        buf->append(L"[");
                        for (std::vector<int>::size_type i = 0; i < returnStates.size(); i++) {
                            if (i > 0) {
                                buf->append(L", ");
                            }
                            if (returnStates[i] == EMPTY_RETURN_STATE) {
                                buf->append(L"$");
                                continue;
                            }
                            buf->append(std::to_wstring(returnStates.at(i)));
                            if (parents->at(i) != nullptr) {
                                buf->append(L" ");
                                buf->append(parents->at(i)->toString());
                            } else {
                                buf->append(L"null");
                            }
                        }
                        buf->append(L"]");
                        return buf->toString();
                    }
                }
            }
        }
    }
}
