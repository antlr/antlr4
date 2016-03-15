#pragma once

#include <string>
#include <deque>

#include "Parser.h"
#include "Declarations.h"


/*
 * [The "BSD license"]
 * Copyright (c) 2016 Mike Lischke
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Dan McLaughlin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {

                /// <summary>
                /// A parser simulator that mimics what ANTLR's generated
                ///  parser code does. A ParserATNSimulator is used to make
                ///  predictions via adaptivePredict but this class moves a pointer through the
                ///  ATN to simulate parsing. ParserATNSimulator just
                ///  makes us efficient rather than having to backtrack, for example.
                /// 
                ///  This properly creates parse trees even for left recursive rules.
                /// 
                ///  We rely on the left recursive rule invocation and special predicate
                ///  transitions to make left recursive rules work.
                /// 
                ///  See TestParserInterpreter for examples.
                /// </summary>
                class ParserInterpreter : public Parser {
                protected:
					//TODO check this
                    static const int DEFAULT_BITSET_SIZE = 1024; // atn->states.size() ideally
                    
                    const std::wstring grammarFileName;
                    std::vector<std::wstring> _tokenNames;
                    atn::ATN *const atn;

                    std::vector<std::wstring> _ruleNames;
                    antlrcpp::BitSet *const pushRecursionContextStates;

                    std::vector<dfa::DFA *> _decisionToDFA; // not shared like it is for generated parsers
                    atn::PredictionContextCache *const sharedContextCache;



                    std::deque<std::pair<ParserRuleContext*, int>*> *const _parentContextStack;

                public:
                    ParserInterpreter(const std::wstring &grammarFileName, const std::vector<std::wstring>& tokenNames, const std::vector<std::wstring>& ruleNames, atn::ATN *atn, TokenStream *input);

                    virtual atn::ATN *getATN() const override;

                    virtual const std::vector<std::wstring>& getTokenNames() const override;

                    virtual const std::vector<std::wstring>& getRuleNames() const override;

                    virtual std::wstring getGrammarFileName() const override;

                    /// <summary>
                    /// Begin parsing at startRuleIndex </summary>
                    virtual ParserRuleContext *parse(int startRuleIndex);

                    virtual void enterRecursionRule(ParserRuleContext *localctx, int state, int ruleIndex, int precedence) override;

                protected:
                    virtual atn::ATNState *getATNState();

                    virtual void visitState(atn::ATNState *p);

                    virtual void visitRuleStopState(atn::ATNState *p);
                };

            }
        }
    }
}
