/* Copyright (c) 2012-2026 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include <string>
#include <vector>

#include "gtest/gtest.h"
#include "ANTLRInputStream.h"
#include "Lexer.h"
#include "atn/ATN.h"
#include "atn/ATNType.h"
#include "atn/LexerATNSimulator.h"
#include "atn/PredictionContextCache.h"
#include "dfa/DFA.h"
#include "Vocabulary.h"

namespace antlr4 {
namespace {

class TrackingInputStream final : public ANTLRInputStream {
public:
  explicit TrackingInputStream(std::string_view input) : ANTLRInputStream(input) {}

  void seek(size_t index) override {
    ++seekCalls;
    ANTLRInputStream::seek(index);
  }

  size_t seekCalls = 0;
};

class TestLexer final : public Lexer {
public:
  TestLexer(const atn::ATN& atn, std::vector<dfa::DFA>& decisions,
            atn::PredictionContextCache& contextCache, CharStream* input = nullptr)
      : Lexer(input), _atn(atn) {
    setInterpreter(new atn::LexerATNSimulator(this, _atn, decisions, contextCache));
  }

  const std::vector<std::string>& getRuleNames() const override {
    static const std::vector<std::string> ruleNames;
    return ruleNames;
  }

  const dfa::Vocabulary& getVocabulary() const override {
    static const dfa::Vocabulary vocabulary;
    return vocabulary;
  }

  std::string getGrammarFileName() const override {
    return "TestLexer.g4";
  }

  const atn::ATN& getATN() const override {
    return _atn;
  }

  const std::vector<std::string>& getChannelNames() const override {
    static const std::vector<std::string> channelNames{"DEFAULT_TOKEN_CHANNEL", "HIDDEN"};
    return channelNames;
  }

  const std::vector<std::string>& getModeNames() const override {
    static const std::vector<std::string> modeNames{"DEFAULT_MODE"};
    return modeNames;
  }

private:
  const atn::ATN& _atn;
};

TEST(LexerTest, ReplacingInputDoesNotAccessPreviousStream) {
  atn::ATN atn(atn::ATNType::LEXER, 0);
  std::vector<dfa::DFA> decisions;
  atn::PredictionContextCache contextCache;
  TrackingInputStream previous("previous");
  TrackingInputStream replacement("replacement");
  TestLexer lexer(atn, decisions, contextCache, &previous);

  previous.seek(2);
  const size_t previousSeekCalls = previous.seekCalls;

  lexer.setInputStream(&replacement);

  EXPECT_EQ(previous.seekCalls, previousSeekCalls);
  EXPECT_EQ(lexer.getInputStream(), &replacement);
}

TEST(LexerTest, CanSetInputWhenNoPreviousStreamOrAfterClearingIt) {
  atn::ATN atn(atn::ATNType::LEXER, 0);
  std::vector<dfa::DFA> decisions;
  atn::PredictionContextCache contextCache;
  TrackingInputStream replacement("replacement");
  TestLexer lexer(atn, decisions, contextCache);

  lexer.setInputStream(&replacement);
  EXPECT_EQ(lexer.getInputStream(), &replacement);

  lexer.setInputStream(nullptr);
  EXPECT_EQ(lexer.getInputStream(), nullptr);

  lexer.setInputStream(&replacement);
  EXPECT_EQ(lexer.getInputStream(), &replacement);
}

}
}
