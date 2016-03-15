#include "FailedPredicateException.h"
#include "ATNState.h"
#include "AbstractPredicateTransition.h"
#include "PredicateTransition.h"
#include "Parser.h"
#include "ParserATNSimulator.h"
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
                FailedPredicateException::FailedPredicateException(Parser *recognizer) : RecognitionException() {
                }

                FailedPredicateException::FailedPredicateException(Parser *recognizer, const std::wstring &predicate): RecognitionException() {
                }

                FailedPredicateException::FailedPredicateException(Parser *recognizer, const std::wstring &predicate, const std::wstring &message)
#ifdef TODO
                // Huston, a problem. "trans" isn't defined until below
                : RecognitionException(formatMessage(predicate, message), recognizer, recognizer->getInputStream(), recognizer->_ctx), ruleIndex((static_cast<atn::PredicateTransition*>(trans))->ruleIndex), predicateIndex((static_cast<atn::PredicateTransition*>(trans))->predIndex), predicate(predicate)
#endif

                {
                    atn::ATNState *s = recognizer->getInterpreter()->atn->states[recognizer->getState()];

                    atn::AbstractPredicateTransition *trans = static_cast<atn::AbstractPredicateTransition*>(s->transition(0));
                    if (dynamic_cast<atn::PredicateTransition*>(trans) != nullptr) {
                    } else {
                        this->ruleIndex = 0;
                        this->predicateIndex = 0;
                    }

                    this->setOffendingToken(recognizer->getCurrentToken());
                }

                int FailedPredicateException::getRuleIndex() {
                    return ruleIndex;
                }

                int FailedPredicateException::getPredIndex() {
                    return predicateIndex;
                }

                std::wstring FailedPredicateException::getPredicate() {
                    return predicate;
                }

                std::wstring FailedPredicateException::formatMessage(const std::wstring &predicate, const std::wstring &message) {
                    if (message != L"") {
                        return message;
                    }
                    return L"failed predicate: " + predicate + L"?";
                }
            }
        }
    }
}
