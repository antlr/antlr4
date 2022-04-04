/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "Lexer.h"
#include "atn/PredictionContext.h"
#include "Vocabulary.h"

namespace antlr4 {

  class ANTLR4CPP_PUBLIC LexerInterpreter : public Lexer {
  public:
    LexerInterpreter(std::string_view grammarFileName, const Vocabulary &vocabulary,
                     antlrcpp::Span<const std::string_view> ruleNames, antlrcpp::Span<const std::string_view> channelNames,
                     antlrcpp::Span<const std::string_view> modeNames, const atn::ATN &atn, CharStream *input);

    ~LexerInterpreter();

    virtual const atn::ATN& getATN() const override;
    virtual std::string_view getGrammarFileName() const override;
    virtual antlrcpp::Span<const std::string_view> getRuleNames() const override;
    virtual antlrcpp::Span<const std::string_view> getChannelNames() const override;
    virtual antlrcpp::Span<const std::string_view> getModeNames() const override;

    virtual const Vocabulary& getVocabulary() const override;

  protected:
    const std::string_view _grammarFileName;
    const atn::ATN &_atn;

    const antlrcpp::Span<const std::string_view> _ruleNames;
    const antlrcpp::Span<const std::string_view> _channelNames;
    const antlrcpp::Span<const std::string_view> _modeNames;
    std::vector<dfa::DFA> _decisionToDFA;

    atn::PredictionContextCache _sharedContextCache;

  private:
    const Vocabulary &_vocabulary;
  };

} // namespace antlr4
