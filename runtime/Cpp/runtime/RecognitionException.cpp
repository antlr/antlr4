#include "RecognitionException.h"
#include "ATN.h"
#include "Recognizer.h"

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

                int RecognitionException::getOffendingState() {
                    return offendingState;
                }

                void RecognitionException::setOffendingState(int offendingState) {
                    this->offendingState = offendingState;
                }

                misc::IntervalSet *RecognitionException::getExpectedTokens() {
                    // Terence and Sam used some fancy Java wildcard generics which
                    // cause us trouble here. TODO - can a Recognizer<void*, void*>
                    // substitute for any other type of recognizer?
                    if (recognizer != nullptr) {
                        return ((Recognizer<void*, void*>*)recognizer)->getATN()->getExpectedTokens(offendingState, ctx);
                    }
                    return nullptr;
                }

                RuleContext *RecognitionException::getCtx() {
                    return ctx;
                }

                IntStream *RecognitionException::getInputStream() {
                    return input;
                }

                Token *RecognitionException::getOffendingToken() {
                    return offendingToken;
                }

                void RecognitionException::setOffendingToken(Token *offendingToken) {
                    this->offendingToken = offendingToken;
                }
                
                IRecognizer<void*, void*> *RecognitionException::getRecognizer() {
                    // Terence and Sam used some fancy Java wildcard generics which
                    // cause us trouble here. TODO - can a Recognizer<void*, void*>
                    // substitute for any other type of recognizer?
                    return (IRecognizer<void*, void*> *)recognizer;
                }

                void RecognitionException::InitializeInstanceFields() {
                    offendingState = -1;
                }
            }
        }
    }
}
