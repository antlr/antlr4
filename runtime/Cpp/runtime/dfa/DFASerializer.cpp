#include "DFASerializer.h"
#include "DFA.h"
#include "StringBuilder.h"
#include "DFAState.h"
#include <limits.h>
#include <stdint.h>

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
                namespace dfa {

                    // TODO -- Make sure this reference doesn't go away prematurely.
                    DFASerializer::DFASerializer(DFA *dfa, const std::vector<std::wstring>& tokenNames) : dfa(dfa), tokenNames_(tokenNames) {
                    }

                    std::wstring DFASerializer::toString() {
                        if (dfa->s0 == nullptr) {
                            return L"";
                        }
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        std::vector<DFAState*> states = dfa->getStates();
                        for (auto s : states) {
                            int n = 0;
                            n = (int)s->edges.size();
                            for (int i = 0; i < n; i++) {
                                DFAState *t = s->edges[i];
                                if (t != nullptr && t->stateNumber != INT16_MAX) {
                                    buf->append(getStateString(s));
                                    std::wstring label = getEdgeLabel(i);
                                    buf->append(L"-"); buf->append(label); buf->append(L"->"); buf->append(getStateString(t)); buf->append(L"\n");
                                }
                            }
                        }

                        std::wstring output = buf->toString();
                        if (output.length() == 0) {
                            return L"";
                        }
                        //return Utils.sortLinesInString(output);
                        return output;
                    }

                    std::wstring DFASerializer::getEdgeLabel(int i) {
                        std::wstring label;
                        if (i == 0) {
                            return L"EOF";
                        }
                        if (!tokenNames_.empty()) {
                            label = tokenNames_[i - 1];
                        } else {
                            label = antlrcpp::StringConverterHelper::toString(i - 1);
                        }
                        return label;
                    }

                    std::wstring DFASerializer::getStateString(DFAState *s) {
		        size_t n = (size_t)s->stateNumber;
                        
                        const std::wstring baseStateStr = (s->isAcceptState ? L":" : L"") + std::wstring(L"s") + std::to_wstring(n) + (s->requiresFullContext ? L"^" : L"");
                        if (s->isAcceptState) {
                            if (s->predicates.size() != 0) {
                                std::wstring buf;
                                for (size_t i = 0; i < s->predicates.size(); i++) {
                                    buf.append(s->predicates[i]->toString());
                                }
                                return baseStateStr + std::wstring(L"=>") + buf;
                            } else {
                                return baseStateStr + std::wstring(L"=>") + std::to_wstring(s->prediction);
                            }
                        } else {
                            return baseStateStr;
                        }
                    }
                }
            }
        }
    }
}
