#include "DiagnosticErrorListener.h"
#include "ATNConfig.h"
#include "ATNConfigSet.h"
#include "Parser.h"
#include "Interval.h"
#include "DFA.h"
#include "DecisionState.h"
#include "stdio.h"
#include "StringBuilder.h"
#include "ATNState.h"

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
                
                
                //JAVA TO C++ CONVERTER TODO TASK: Calls to same-class constructors are not supported in C++ prior to C++11:
                DiagnosticErrorListener::DiagnosticErrorListener() : exactOnly(true) {
                }
                
                DiagnosticErrorListener::DiagnosticErrorListener(bool exactOnly) : exactOnly(exactOnly) {
                }
                
                void DiagnosticErrorListener::reportAmbiguity(Parser *recognizer, dfa::DFA *dfa, int startIndex, int stopIndex, bool exact, antlrcpp::BitSet *ambigAlts, atn::ATNConfigSet *configs) {
                    if (exactOnly && !exact) {
                        return;
                    }
                    wchar_t buf[16];
                    std::wstring decision = getDecisionDescription(recognizer, dfa);
                    antlrcpp::BitSet *conflictingAlts = getConflictingAlts(ambigAlts, configs);
                    std::wstring text = recognizer->getTokenStream()->getText(misc::Interval::of(startIndex, stopIndex));
                    std::wstring message = L"reportAmbiguity d=" + decision + L": ambigAlts=" + conflictingAlts->toString() + L", input='" + text + L"'";
                    swprintf(buf, sizeof(buf) / sizeof(*buf), L"%d", 5);
                    recognizer->notifyErrorListeners(message);
                }
                
                void DiagnosticErrorListener::reportAttemptingFullContext(Parser *recognizer, dfa::DFA *dfa, int startIndex, int stopIndex, antlrcpp::BitSet *conflictingAlts, atn::ATNConfigSet *configs) {
                    std::wstring decision = getDecisionDescription(recognizer, dfa);
                    std::wstring text = recognizer->getTokenStream()->getText(misc::Interval::of(startIndex, stopIndex));
                    std::wstring message = L"reportAttemptingFullContext d=" + decision + L", input='" + text + L"'";
                    recognizer->notifyErrorListeners(message);
                }
                
                void DiagnosticErrorListener::reportContextSensitivity(Parser *recognizer, dfa::DFA *dfa, int startIndex, int stopIndex, int prediction, atn::ATNConfigSet *configs) {
                    std::wstring decision = getDecisionDescription(recognizer, dfa);
                    std::wstring text = recognizer->getTokenStream()->getText(misc::Interval::of(startIndex, stopIndex));
                    std::wstring message = L"reportContextSensitivity d=" + decision + L", input='" + text + L"'";
                    recognizer->notifyErrorListeners(message);
                }
                
                std::wstring DiagnosticErrorListener::getDecisionDescription(Parser *recognizer, dfa::DFA *dfa) {
                    int decision = dfa->decision;
                    int ruleIndex = ((atn::ATNState*)dfa->atnStartState)->ruleIndex;
                    
                    const std::vector<std::wstring>& ruleNames = recognizer->getRuleNames();
                    if (ruleIndex < 0 || ruleIndex >= (int)ruleNames.size()) {
                        return antlrcpp::StringConverterHelper::toString(decision);
                    }
                    
                    std::wstring ruleName = ruleNames[ruleIndex];
                    if (ruleName == L"" || ruleName.empty())  {
                        return antlrcpp::StringConverterHelper::toString(decision);
                    }
                    
                    return std::to_wstring(decision) + L"(" + ruleName + L")";
                }
                
                antlrcpp::BitSet *DiagnosticErrorListener::getConflictingAlts(antlrcpp::BitSet *reportedAlts, atn::ATNConfigSet *configs) {
                    if (reportedAlts != nullptr) {
                        return reportedAlts;
                    }
                    
                    antlrcpp::BitSet *result = new antlrcpp::BitSet();
                    for (size_t i = 0; i < configs->size(); i++) {
                        atn::ATNConfig *config = configs->get((int)i);
                        result->set(config->alt);
                    }
                    
                    return result;
                }
            }
        }
    }
}
