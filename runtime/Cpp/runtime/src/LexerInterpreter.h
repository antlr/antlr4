/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#pragma once

#include "Lexer.h"

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {

  class ANTLR4CPP_PUBLIC LexerInterpreter : public Lexer {
  public:
    // @deprecated
    LexerInterpreter(const std::wstring &grammarFileName, const std::vector<std::wstring> &tokenNames,
                     const std::vector<std::wstring> &ruleNames, const std::vector<std::wstring> &modeNames,
                     const atn::ATN &atn, CharStream *input);
    LexerInterpreter(const std::wstring &grammarFileName, Ref<dfa::Vocabulary> vocabulary,
                     const std::vector<std::wstring> &ruleNames, const std::vector<std::wstring> &modeNames,
                     const atn::ATN &atn, CharStream *input);

    ~LexerInterpreter();

    virtual const atn::ATN& getATN() const override;
    virtual std::wstring getGrammarFileName() const override;
    virtual const std::vector<std::wstring>& getTokenNames() const override;
    virtual const std::vector<std::wstring>& getRuleNames() const override;
    virtual const std::vector<std::wstring>& getModeNames() const override;
    
    virtual Ref<dfa::Vocabulary> getVocabulary() const override;

  protected:
    const std::wstring _grammarFileName;
    const atn::ATN &_atn;

    // @deprecated
    std::vector<std::wstring> _tokenNames;
    const std::vector<std::wstring> &_ruleNames;
    const std::vector<std::wstring> &_modeNames;
    std::vector<dfa::DFA> _decisionToDFA;

    Ref<atn::PredictionContextCache> _sharedContextCache;

  private:
    Ref<dfa::Vocabulary> _vocabulary;
  };

} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org
