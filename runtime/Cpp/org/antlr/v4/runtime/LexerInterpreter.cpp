#include "LexerInterpreter.h"
#include "ATNType.h"
#include "LexerATNSimulator.h"
#include "ATN.h"
#include "DFA.h"
#include "Exceptions.h"
#include "PredictionContextCache.h"

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
                LexerInterpreter::LexerInterpreter(const std::wstring &grammarFileName, std::vector<std::wstring> *tokenNames, std::vector<std::wstring> *ruleNames, std::vector<std::wstring> *modeNames, atn::ATN *atn, CharStream *input) : Lexer(_input), grammarFileName(grammarFileName), atn(atn), _sharedContextCache(new atn::PredictionContextCache()) {

                    if (atn->grammarType != atn::ATNType::LEXER) {
                        throw new IllegalArgumentException(L"The ATN must be a lexer ATN.");
                    }


                    for (int i = 0; i < (int)_decisionToDFA.size(); i++) {
                        _decisionToDFA[i] = new dfa::DFA(atn->getDecisionState(i), i);
                    }
                    this->_interp = new atn::LexerATNSimulator(atn,_decisionToDFA,_sharedContextCache);
                    if (tokenNames) {
                        _tokenNames = *tokenNames;
                    }
                    if (ruleNames) {
                        _ruleNames = *ruleNames;
                    }
                    if (modeNames) {
                        _modeNames = *modeNames;
                    }
                }
                atn::ATN *LexerInterpreter::getATN() {
                    return atn;
                }

                std::wstring LexerInterpreter::getGrammarFileName() {
                    return grammarFileName;
                }

                const std::vector<std::wstring>& LexerInterpreter::getTokenNames() {
                    return _tokenNames;
                }

                const std::vector<std::wstring>& LexerInterpreter::getRuleNames() {
                    return _ruleNames;
                }

                const std::vector<std::wstring>& LexerInterpreter::getModeNames() {
                    return _modeNames;
                }
            }
        }
    }
}
