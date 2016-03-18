#include "DFAState.h"
#include "StringBuilder.h"
#include "ATNConfigSet.h"
#include "SemanticContext.h"
#include "ATNConfig.h"

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

                    DFAState::PredPrediction::PredPrediction(atn::SemanticContext *pred, int alt) {
                        InitializeInstanceFields();
                        this->alt = alt;
                        this->pred = pred;
                    }

                    std::wstring DFAState::PredPrediction::toString() {
                        return std::wstring(L"(") + pred->toString() + std::wstring(L", ") + std::to_wstring(alt) + std::wstring(L")");
                    }

                    void DFAState::PredPrediction::InitializeInstanceFields() {
                        alt = 0;
                    }

                    DFAState::DFAState() {
                        InitializeInstanceFields();
                    }

                    DFAState::DFAState(int stateNumber) {
                        InitializeInstanceFields();
                        this->stateNumber = stateNumber;
                    }

                    DFAState::DFAState(atn::ATNConfigSet *configs) {
                        InitializeInstanceFields();
                        this->configs = configs;
                    }

                    std::set<int> *DFAState::getAltSet() {
                        std::set<int> *alts = new std::set<int>();
                        if (configs != nullptr) {
                            for (size_t i = 0; i < configs->size(); i++) {
                                alts->insert(configs->get((int)i)->alt);
                            }
                        }
                        if (alts->size() == 0) {
                            return nullptr;
                        }
                        return alts;
                    }

                    int DFAState::hashCode() {
                        int hash = misc::MurmurHash::initialize(7);
                        hash = misc::MurmurHash::update(hash, configs->hashCode());
                        hash = misc::MurmurHash::finish(hash, 1);
                        return hash;
                    }

                    bool DFAState::equals(void *o) {
                        // compare set of ATN configurations in this set with other
                        if (this == o) {
                            return true;
                        }

                        if ((DFAState*)o == nullptr) {
                            return false;
                        }

                        DFAState *other = static_cast<DFAState*>(o);
                        // TODO (sam): what to do when configs==null?
                        bool sameSet = this->configs->equals(other->configs);
                                        //		System.out.println("DFAState.equals: "+configs+(sameSet?"==":"!=")+other.configs);
                        return sameSet;
                    }

                    std::wstring DFAState::toString() {
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        buf->append(std::to_wstring(stateNumber)); buf->append(L":"); buf->append(configs->toString());
                        if (isAcceptState) {
                            buf->append(L"=>");
                            if (predicates.size() != 0) {
                                std::wstring tmp;
                                for (size_t i = 0; i < predicates.size(); i++) {
                                    tmp.append(predicates[i]->toString());
                                }
                                buf->append(tmp);
                            } else {
                                buf->append(std::to_wstring(prediction));
                            }
                        }
                        return buf->toString();
                    }

                    void DFAState::InitializeInstanceFields() {
                        stateNumber = -1;
                        configs = new org::antlr::v4::runtime::atn::ATNConfigSet();
                        isAcceptState = false;
                        prediction = 0;
                        lexerRuleIndex = -1;
                        lexerActionIndex = -1;
                        requiresFullContext = false;
                    }
                }
            }
        }
    }
}
