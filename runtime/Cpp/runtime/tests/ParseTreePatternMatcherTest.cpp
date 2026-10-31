/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include <cstdint>
#include <memory>
#include <string>
#include <vector>

#include "gtest/gtest.h"
#include "antlr4-runtime.h"

namespace antlr4 {
namespace tree {
namespace pattern {
namespace {

  // Serialized ATNs produced by the ANTLR tool for:
  //
  //   grammar X;
  //   s    : ID '=' expr ';' ;
  //   expr : ID | INT ;
  //   ID   : [a-z]+ ;
  //   INT  : [0-9]+ ;
  //   WS   : [ \t\r\n]+ -> skip ;
  const std::vector<int32_t> lexerATN = {
    4, 0, 5, 32, 6, -1, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 2, 3, 7, 3, 2, 4, 7, 4, 1, 0, 1, 0, 1, 1, 1,
    1, 1, 2, 4, 2, 17, 8, 2, 11, 2, 12, 2, 18, 1, 3, 4, 3, 22, 8, 3, 11, 3, 12, 3, 23, 1, 4, 4, 4, 27, 8,
    4, 11, 4, 12, 4, 28, 1, 4, 1, 4, 0, 0, 5, 1, 1, 3, 2, 5, 3, 7, 4, 9, 5, 1, 0, 3, 1, 0, 97, 122, 1, 0,
    48, 57, 3, 0, 9, 10, 13, 13, 32, 32, 34, 0, 1, 1, 0, 0, 0, 0, 3, 1, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 7,
    1, 0, 0, 0, 0, 9, 1, 0, 0, 0, 1, 11, 1, 0, 0, 0, 3, 13, 1, 0, 0, 0, 5, 16, 1, 0, 0, 0, 7, 21, 1, 0, 0,
    0, 9, 26, 1, 0, 0, 0, 11, 12, 5, 61, 0, 0, 12, 2, 1, 0, 0, 0, 13, 14, 5, 59, 0, 0, 14, 4, 1, 0, 0, 0,
    15, 17, 7, 0, 0, 0, 16, 15, 1, 0, 0, 0, 17, 18, 1, 0, 0, 0, 18, 16, 1, 0, 0, 0, 18, 19, 1, 0, 0, 0,
    19, 6, 1, 0, 0, 0, 20, 22, 7, 1, 0, 0, 21, 20, 1, 0, 0, 0, 22, 23, 1, 0, 0, 0, 23, 21, 1, 0, 0, 0, 23,
    24, 1, 0, 0, 0, 24, 8, 1, 0, 0, 0, 25, 27, 7, 2, 0, 0, 26, 25, 1, 0, 0, 0, 27, 28, 1, 0, 0, 0, 28, 26,
    1, 0, 0, 0, 28, 29, 1, 0, 0, 0, 29, 30, 1, 0, 0, 0, 30, 31, 6, 4, 0, 0, 31, 10, 1, 0, 0, 0, 4, 0, 18,
    23, 28, 1, 6, 0, 0
  };

  const std::vector<int32_t> parserATN = {
    4, 1, 5, 12, 2, 0, 7, 0, 2, 1, 7, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 2, 0, 2, 0,
    1, 1, 0, 3, 4, 9, 0, 4, 1, 0, 0, 0, 2, 9, 1, 0, 0, 0, 4, 5, 5, 3, 0, 0, 5, 6, 5, 1, 0, 0, 6, 7, 3, 2,
    1, 0, 7, 8, 5, 2, 0, 0, 8, 1, 1, 0, 0, 0, 9, 10, 7, 0, 0, 0, 10, 3, 1, 0, 0, 0, 0
  };

  const std::vector<std::string> lexerRuleNames = {"T__0", "T__1", "ID", "INT", "WS"};
  const std::vector<std::string> channelNames = {"DEFAULT_TOKEN_CHANNEL", "HIDDEN"};
  const std::vector<std::string> modeNames = {"DEFAULT_MODE"};
  const std::vector<std::string> parserRuleNames = {"s", "expr"};
  const dfa::Vocabulary vocabulary({"", "'='", "';'"}, {"", "", "", "ID", "INT", "WS"});

  // ParseTreePatternMatcher needs the serialized ATN of the parser to build the one with bypass
  // alternatives, which a plain ParserInterpreter does not provide.
  class XParser final : public ParserInterpreter {
  public:
    XParser(const atn::ATN &atn, TokenStream *input)
      : ParserInterpreter("X.g4", vocabulary, parserRuleNames, atn, input) {}

    atn::SerializedATNView getSerializedATN() const override {
      return atn::SerializedATNView(parserATN);
    }
  };

  class ParseTreePatternMatcherTest : public testing::Test {
  protected:
    void SetUp() override {
      atn::ATNDeserializer deserializer;
      lexerAtn = deserializer.deserialize(lexerATN);
      parserAtn = deserializer.deserialize(parserATN);

      input = std::make_unique<ANTLRInputStream>("x = 1;");
      lexer = std::make_unique<LexerInterpreter>("X.g4", vocabulary, lexerRuleNames, channelNames, modeNames,
                                                 *lexerAtn, input.get());
      tokens = std::make_unique<CommonTokenStream>(lexer.get());
      parser = std::make_unique<XParser>(*parserAtn, tokens.get());
      tree = parser->parse(0);

      patternLexer = std::make_unique<LexerInterpreter>("X.g4", vocabulary, lexerRuleNames, channelNames,
                                                        modeNames, *lexerAtn, nullptr);
      matcher = std::make_unique<ParseTreePatternMatcher>(patternLexer.get(), parser.get());
    }

    std::unique_ptr<atn::ATN> lexerAtn;
    std::unique_ptr<atn::ATN> parserAtn;
    std::unique_ptr<ANTLRInputStream> input;
    std::unique_ptr<LexerInterpreter> lexer;
    std::unique_ptr<CommonTokenStream> tokens;
    std::unique_ptr<XParser> parser;
    ParserRuleContext *tree = nullptr;
    std::unique_ptr<LexerInterpreter> patternLexer;
    std::unique_ptr<ParseTreePatternMatcher> matcher;
  };

  TEST_F(ParseTreePatternMatcherTest, SplitKeepsChunkTypes) {
    std::vector<std::unique_ptr<Chunk>> chunks = matcher->split("<ID> = <e:expr>;");
    ASSERT_EQ(chunks.size(), 4u);

    auto *id = dynamic_cast<TagChunk *>(chunks[0].get());
    ASSERT_NE(id, nullptr);
    EXPECT_EQ(id->getTag(), "ID");
    EXPECT_EQ(id->getLabel(), "");

    auto *assign = dynamic_cast<TextChunk *>(chunks[1].get());
    ASSERT_NE(assign, nullptr);
    EXPECT_EQ(assign->getText(), " = ");

    auto *expr = dynamic_cast<TagChunk *>(chunks[2].get());
    ASSERT_NE(expr, nullptr);
    EXPECT_EQ(expr->getTag(), "expr");
    EXPECT_EQ(expr->getLabel(), "e");

    auto *semicolon = dynamic_cast<TextChunk *>(chunks[3].get());
    ASSERT_NE(semicolon, nullptr);
    EXPECT_EQ(semicolon->getText(), ";");
  }

  TEST_F(ParseTreePatternMatcherTest, SplitUnescapesTextChunks) {
    std::vector<std::unique_ptr<Chunk>> chunks = matcher->split("\\<ID\\> = <expr>;");
    ASSERT_EQ(chunks.size(), 3u);

    auto *text = dynamic_cast<TextChunk *>(chunks[0].get());
    ASSERT_NE(text, nullptr);
    EXPECT_EQ(text->getText(), "<ID> = ");

    auto *expr = dynamic_cast<TagChunk *>(chunks[1].get());
    ASSERT_NE(expr, nullptr);
    EXPECT_EQ(expr->getTag(), "expr");

    auto *semicolon = dynamic_cast<TextChunk *>(chunks[2].get());
    ASSERT_NE(semicolon, nullptr);
    EXPECT_EQ(semicolon->getText(), ";");
  }

  TEST_F(ParseTreePatternMatcherTest, CompiledPatternMatchesTree) {
    ParseTreePattern pattern = matcher->compile("<ID> = <expr>;", 0);
    EXPECT_TRUE(pattern.matches(tree));

    ParseTreeMatch match = pattern.match(tree);
    ASSERT_TRUE(match.succeeded());
    ASSERT_NE(match.get("ID"), nullptr);
    EXPECT_EQ(match.get("ID")->getText(), "x");
    ASSERT_NE(match.get("expr"), nullptr);
    EXPECT_EQ(match.get("expr")->getText(), "1");
  }

  TEST_F(ParseTreePatternMatcherTest, LabelsAndLiteralText) {
    ParseTreeMatch match = matcher->match(tree, "<lhs:ID> = <rhs:expr>;", 0);
    ASSERT_TRUE(match.succeeded());
    EXPECT_EQ(match.getPattern().getPattern(), "<lhs:ID> = <rhs:expr>;");
    ASSERT_NE(match.get("lhs"), nullptr);
    EXPECT_EQ(match.get("lhs")->getText(), "x");
    ASSERT_NE(match.get("rhs"), nullptr);
    EXPECT_EQ(match.get("rhs")->getText(), "1");

    EXPECT_TRUE(matcher->matches(tree, "<ID> = 1;", 0));
    EXPECT_FALSE(matcher->matches(tree, "<ID> = 2;", 0));
    EXPECT_FALSE(matcher->matches(tree, "<ID> = <ID>;", 0));
  }

}  // namespace
}  // namespace pattern
}  // namespace tree
}  // namespace antlr4
