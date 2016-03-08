#pragma once

#include <string>
#include <cctype>
#include "Lexer.h"
#include "Declarations.h"

// Generated from XPathLexer.g4 by ANTLR 4.1
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


class XPathLexer : public org::antlr::v4::runtime::Lexer {
protected:
    //ORIGINAL LINE: protected static final org.antlr.v4.runtime.dfa.DFA[] _decisionToDFA;
    static const org::antlr::v4::runtime::dfa::DFA *_decisionToDFA;
    static org::antlr::v4::runtime::atn::PredictionContextCache *const _sharedContextCache;
public:
    static const int TOKEN_REF = 1, RULE_REF = 2, ANYWHERE = 3, ROOT = 4, WILDCARD = 5, BANG = 6, ID = 7, STRING = 8;
    static std::vector<std::wstring> _modeNames;

    static const std::vector<std::wstring> _tokenNames;
    static const std::vector<std::wstring> _ruleNames;


    XPathLexer(org::antlr::v4::runtime::CharStream *input);

    virtual std::wstring getGrammarFileName() override;
    virtual const std::vector<std::wstring>& getTokenNames() override;
    virtual const std::vector<std::wstring>& getRuleNames() override;
    virtual const std::vector<std::wstring>& getModeNames() override;
    virtual org::antlr::v4::runtime::atn::ATN *getATN() override;
    virtual void action(org::antlr::v4::runtime::RuleContext *_localctx, int ruleIndex, int actionIndex) override;
private:
    void ID_action(org::antlr::v4::runtime::RuleContext *_localctx, int actionIndex);

public:
    static const std::wstring _serializedATN;
    static org::antlr::v4::runtime::atn::ATN *const _ATN;

    XPathLexer();
};
