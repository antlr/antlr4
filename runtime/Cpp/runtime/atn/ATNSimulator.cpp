#include <map>

#include "ATNSimulator.h"
#include "ATNDeserializer.h"
#include "ATNConfigSet.h"
#include "DFAState.h"
#include "limits.h"
#include "ATNType.h"
#include "PredictionContextCache.h"
#include "PredictionContext.h"
#include "ATN.h"


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

                    dfa::DFAState * ATNSimulator::ERROR = new dfa::DFAState();
                    
                    ATNSimulator::ATNSimulator() {
                        ERROR = new dfa::DFAState(new ATNConfigSet());
                        ERROR->stateNumber = INT32_MAX;
                        sharedContextCache = new PredictionContextCache();
                        
                        atn = new ATN(ATNType::LEXER, 0);
                    }

                    ATNSimulator::ATNSimulator(ATN *atn, PredictionContextCache *sharedContextCache) : atn(atn), sharedContextCache(sharedContextCache) {
                    }

                    atn::PredictionContextCache *ATNSimulator::getSharedContextCache() {
                        return sharedContextCache;
                    }

                    atn::PredictionContext *ATNSimulator::getCachedContext(PredictionContext *context) {
                        if (sharedContextCache == nullptr) {
                            return context;
                        }

						{
							std::lock_guard<std::mutex> lck(mtx);
							std::map<PredictionContext*, PredictionContext*> *visited = new std::map<PredictionContext*, PredictionContext*>();
							return PredictionContext::getCachedContext(context, sharedContextCache, visited);
						}
                    }

                    atn::ATN *ATNSimulator::deserialize(wchar_t data[]) {
                        return (new ATNDeserializer())->deserialize(data);
                    }

                    void ATNSimulator::checkCondition(bool condition) {
                        (new ATNDeserializer())->checkCondition(condition);
                    }

                    void ATNSimulator::checkCondition(bool condition, const std::wstring &message) {
                        (new ATNDeserializer())->checkCondition(condition, message);
                    }

                    atn::Transition *ATNSimulator::edgeFactory(ATN *atn, int type, int src, int trg, int arg1, int arg2, int arg3, std::vector<misc::IntervalSet*> &sets) {
                        return (new ATNDeserializer())->edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets);
                    }

                    atn::ATNState *ATNSimulator::stateFactory(int type, int ruleIndex) {
                        return (new ATNDeserializer())->stateFactory(type, ruleIndex);
                    }
                }
            }
        }
    }
}
