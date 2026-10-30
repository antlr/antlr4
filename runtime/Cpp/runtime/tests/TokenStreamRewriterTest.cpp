#include <memory>
#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "CommonToken.h"
#include "CommonTokenStream.h"
#include "ListTokenSource.h"
#include "Token.h"
#include "TokenStreamRewriter.h"

namespace antlr4 {
namespace {

  std::vector<std::unique_ptr<Token>> singleCharTokens(size_t count) {
    std::vector<std::unique_ptr<Token>> tokens;
    tokens.reserve(count);
    for (size_t i = 0; i < count; i++) {
      tokens.push_back(std::make_unique<CommonToken>(1, std::string(1, static_cast<char>('a' + i))));
    }
    return tokens;
  }

  // An insert that gets folded into a replace at the same index must not be
  // inspected against the replaces that follow it.
  TEST(TokenStreamRewriterTest, InsertBeforeFoldedIntoReplaceWithLaterReplace) {
    ListTokenSource source(singleCharTokens(6));
    CommonTokenStream tokens(&source);
    tokens.fill();

    TokenStreamRewriter rewriter(&tokens);
    rewriter.replace(1, 1, "X");
    rewriter.replace(4, 4, "Y");
    rewriter.insertBefore(1, "Z");

    EXPECT_EQ(rewriter.getText(), "aZXcdYf");
  }

}
}
