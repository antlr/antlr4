#pragma once

#include <string>

#include "ANTLRErrorListener.h"
#include "IRecognizer.h"

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
                /// <summary>
                /// @author Sam Harwell
                /// </summary>
                class BaseErrorListener : public ANTLRErrorListener {
                                
                    template<typename T1, typename T2>
                    void syntaxError(IRecognizer<T1, T2> *recognizer, void *offendingSymbol, int line, int charPositionInLine, const std::wstring &msg, RecognitionException *e) { }

                    virtual void reportAmbiguity(Parser *recognizer, dfa::DFA *dfa, int startIndex, int stopIndex, bool exact, antlrcpp::BitSet *ambigAlts, atn::ATNConfigSet *configs) override;

                    virtual void reportAttemptingFullContext(Parser *recognizer, dfa::DFA *dfa, int startIndex, int stopIndex, antlrcpp::BitSet *conflictingAlts, atn::ATNConfigSet *configs);

                    virtual void reportContextSensitivity(Parser *recognizer, dfa::DFA *dfa, int startIndex, int stopIndex, int prediction, atn::ATNConfigSet *configs);
                };

            }
        }
    }
}
