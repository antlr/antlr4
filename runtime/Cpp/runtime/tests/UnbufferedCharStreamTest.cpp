#include <cwchar>
#include <sstream>
#include <string>

#include "gtest/gtest.h"
#include "IntStream.h"
#include "UnbufferedCharStream.h"
#include "misc/Interval.h"

namespace antlr4 {
namespace {

  TEST(UnbufferedCharStreamTest, NoncharacterFFFFIsNotEndOfInput) {
    if (WCHAR_MAX <= 0xFFFF) {
      GTEST_SKIP() << "WEOF and U+FFFF are indistinguishable with a 16-bit wchar_t";
    }

    std::wistringstream input(std::wstring{L'a', static_cast<wchar_t>(0xFFFF), L'b'});
    UnbufferedCharStream stream(input);

    // Lexer::nextToken() holds a marker while matching, so everything consumed stays buffered.
    ssize_t marker = stream.mark();
    EXPECT_EQ(stream.LA(1), static_cast<size_t>('a'));
    stream.consume();
    EXPECT_EQ(stream.LA(1), static_cast<size_t>(0xFFFF));
    stream.consume();
    EXPECT_EQ(stream.LA(1), static_cast<size_t>('b'));
    stream.consume();
    EXPECT_EQ(stream.LA(1), IntStream::EOF);
    EXPECT_EQ(stream.getText(misc::Interval(static_cast<size_t>(0), static_cast<size_t>(2))), "a\xEF\xBF\xBF" "b");
    stream.release(marker);
  }

}  // namespace
}  // namespace antlr4
