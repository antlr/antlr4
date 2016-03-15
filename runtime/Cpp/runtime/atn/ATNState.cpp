#include "ATNState.h"
#include "ATN.h"
#include "Transition.h"
#include "IntervalSet.h"
#include <iostream>

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
                    
                    ATNState::~ATNState() {};
                    
                    const int ATNState::INITIAL_NUM_TRANSITIONS;
                    

                    const wchar_t * ATNState::serializationNames[] =  {L"INVALID", L"BASIC", L"RULE_START", L"BLOCK_START",
                        L"PLUS_BLOCK_START", L"STAR_BLOCK_START", L"TOKEN_START", L"RULE_STOP",
                        L"BLOCK_END", L"STAR_LOOP_BACK", L"STAR_LOOP_ENTRY", L"PLUS_LOOP_BACK", L"LOOP_END"};
                    
                    int ATNState::hashCode() {
                        return stateNumber;
                    }
                    
                    bool ATNState::equals(void *o) {
                        // are these states same object?
                        if (o != nullptr) {
                            return stateNumber == (static_cast<ATNState*>(o))->stateNumber;
                        }
                        return false;
                    }
                    
                    bool ATNState::isNonGreedyExitState() {
                        return false;
                    }
                    
                    std::wstring ATNState::toString() const {
                        return antlrcpp::StringConverterHelper::toString(stateNumber);
                    }
                    
                    std::vector<Transition*> ATNState::getTransitions() {
                        std::vector<Transition*> arr(transitions);

                        return arr;
                    }
                    
                    int ATNState::getNumberOfTransitions() {
                        return (int)transitions.size();
                    }
                    
                    void ATNState::addTransition(Transition *e) {
                        addTransition((int)transitions.size(), e);
                    }
                    
                    void ATNState::addTransition(int index, Transition *e) {
                        if (transitions.empty()) {
                            epsilonOnlyTransitions = e->isEpsilon();
                        } else if (epsilonOnlyTransitions != e->isEpsilon()) {
                            std::cerr << L"ATN state %d has both epsilon and non-epsilon transitions.\n" << stateNumber;
                            epsilonOnlyTransitions = false;
                        }
                        
                        transitions[index] = e;
                    }
                    
                    atn::Transition *ATNState::transition(int i) {
                        return transitions[i];
                    }
                    
                    void ATNState::setTransition(int i, Transition *e) {
                        transitions[i] = e;
                    }
                    
                    atn::Transition *ATNState::removeTransition(int index) {
                        transitions.erase(transitions.begin() + index);
                        return nullptr;
                    }
                    
                    bool ATNState::onlyHasEpsilonTransitions() {
                        return epsilonOnlyTransitions;
                    }
                    
                    void ATNState::setRuleIndex(int ruleIndex) {
                        this->ruleIndex = ruleIndex;
                    }
                    
                    void ATNState::InitializeInstanceFields() {
                        atn = 0;
                        stateNumber = INVALID_STATE_NUMBER;
                        ruleIndex = 0;
                        epsilonOnlyTransitions = false;
                    }
                }
            }
        }
    }
}
