#include <memory>
#include <string>
#include <unordered_set>
#include <utility>
#include <vector>

#include "gtest/gtest.h"
#include "CommonToken.h"
#include "ListTokenSource.h"
#include "Token.h"
#include "UnbufferedTokenStream.h"

namespace antlr4 {
namespace {

  // Tracks which tokens are still alive so the test can tell whether LT(-1)
  // points at a token the stream has already destroyed.
  std::unordered_set<const Token*> liveTokens;

  class TrackedToken final : public CommonToken {
  public:
    TrackedToken(size_t type, const std::string &text) : CommonToken(type, text) {
      liveTokens.insert(this);
    }

    ~TrackedToken() override {
      liveTokens.erase(this);
    }
  };

  std::vector<std::unique_ptr<Token>> makeTokens() {
    std::vector<std::unique_ptr<Token>> tokens;
    tokens.push_back(std::make_unique<TrackedToken>(1, "a"));
    tokens.push_back(std::make_unique<TrackedToken>(2, "b"));
    tokens.push_back(std::make_unique<TrackedToken>(3, "c"));
    tokens.push_back(std::make_unique<TrackedToken>(Token::EOF, "<EOF>"));
    return tokens;
  }

  TEST(UnbufferedTokenStreamTest, LastTokenSurvivesBufferFlush) {
    ListTokenSource source(makeTokens());
    UnbufferedTokenStream stream(&source);

    stream.consume();
    Token *last = stream.LT(-1);
    ASSERT_NE(last, nullptr);
    ASSERT_EQ(liveTokens.count(last), 1u);
    EXPECT_EQ(last->getText(), "a");

    stream.consume();
    last = stream.LT(-1);
    ASSERT_EQ(liveTokens.count(last), 1u);
    EXPECT_EQ(last->getText(), "b");
  }

  TEST(UnbufferedTokenStreamTest, LastTokenSurvivesRelease) {
    ListTokenSource source(makeTokens());
    UnbufferedTokenStream stream(&source);

    ssize_t marker = stream.mark();
    stream.consume();
    stream.consume();
    stream.release(marker);

    Token *last = stream.LT(-1);
    ASSERT_NE(last, nullptr);
    ASSERT_EQ(liveTokens.count(last), 1u);
    EXPECT_EQ(last->getText(), "b");
  }

}
}
