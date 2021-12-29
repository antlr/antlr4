#include <string>
#include <string_view>

#include "gtest/gtest.h"
#include "support/Utf8.h"

namespace antlrcpp {
namespace {

  struct Utf8EncodeTestCase final {
    char32_t code_point;
    std::string_view code_units;
  };

  using Utf8EncodeTest = testing::TestWithParam<Utf8EncodeTestCase>;

  TEST_P(Utf8EncodeTest, Compliance) {
    const Utf8EncodeTestCase& test_case = GetParam();
    std::string result;
    EXPECT_EQ(Utf8::encode(&result, test_case.code_point), test_case.code_units);
  }

  INSTANTIATE_TEST_SUITE_P(Utf8EncodeTest, Utf8EncodeTest,
                          testing::ValuesIn<Utf8EncodeTestCase>({
                              {0x0000, std::string_view("\x00", 1)},
                              {0x0001, "\x01"},
                              {0x007e, "\x7e"},
                              {0x007f, "\x7f"},
                              {0x0080, "\xc2\x80"},
                              {0x0081, "\xc2\x81"},
                              {0x00bf, "\xc2\xbf"},
                              {0x00c0, "\xc3\x80"},
                              {0x00c1, "\xc3\x81"},
                              {0x00c8, "\xc3\x88"},
                              {0x00d0, "\xc3\x90"},
                              {0x00e0, "\xc3\xa0"},
                              {0x00f0, "\xc3\xb0"},
                              {0x00f8, "\xc3\xb8"},
                              {0x00ff, "\xc3\xbf"},
                              {0x0100, "\xc4\x80"},
                              {0x07ff, "\xdf\xbf"},
                              {0x0400, "\xd0\x80"},
                              {0x0800, "\xe0\xa0\x80"},
                              {0x0801, "\xe0\xa0\x81"},
                              {0x1000, "\xe1\x80\x80"},
                              {0xd000, "\xed\x80\x80"},
                              {0xd7ff, "\xed\x9f\xbf"},
                              {0xe000, "\xee\x80\x80"},
                              {0xfffe, "\xef\xbf\xbe"},
                              {0xffff, "\xef\xbf\xbf"},
                              {0x10000, "\xf0\x90\x80\x80"},
                              {0x10001, "\xf0\x90\x80\x81"},
                              {0x40000, "\xf1\x80\x80\x80"},
                              {0x10fffe, "\xf4\x8f\xbf\xbe"},
                              {0x10ffff, "\xf4\x8f\xbf\xbf"},
                              {0xFFFD, "\xef\xbf\xbd"},
                          }));

  struct Utf8DecodeTestCase final {
    char32_t code_point;
    std::string_view code_units;
  };

  using Utf8DecodeTest = testing::TestWithParam<Utf8DecodeTestCase>;

  TEST_P(Utf8DecodeTest, Compliance) {
    const Utf8DecodeTestCase& test_case = GetParam();
    auto [code_point, code_units] = Utf8::decode(test_case.code_units);
    EXPECT_EQ(code_units, test_case.code_units.size());
    EXPECT_EQ(code_point, test_case.code_point);
  }

  INSTANTIATE_TEST_SUITE_P(Utf8DecodeTest, Utf8DecodeTest,
                          testing::ValuesIn<Utf8DecodeTestCase>({
                              {0x0000, std::string_view("\x00", 1)},
                              {0x0001, "\x01"},
                              {0x007e, "\x7e"},
                              {0x007f, "\x7f"},
                              {0x0080, "\xc2\x80"},
                              {0x0081, "\xc2\x81"},
                              {0x00bf, "\xc2\xbf"},
                              {0x00c0, "\xc3\x80"},
                              {0x00c1, "\xc3\x81"},
                              {0x00c8, "\xc3\x88"},
                              {0x00d0, "\xc3\x90"},
                              {0x00e0, "\xc3\xa0"},
                              {0x00f0, "\xc3\xb0"},
                              {0x00f8, "\xc3\xb8"},
                              {0x00ff, "\xc3\xbf"},
                              {0x0100, "\xc4\x80"},
                              {0x07ff, "\xdf\xbf"},
                              {0x0400, "\xd0\x80"},
                              {0x0800, "\xe0\xa0\x80"},
                              {0x0801, "\xe0\xa0\x81"},
                              {0x1000, "\xe1\x80\x80"},
                              {0xd000, "\xed\x80\x80"},
                              {0xd7ff, "\xed\x9f\xbf"},
                              {0xe000, "\xee\x80\x80"},
                              {0xfffe, "\xef\xbf\xbe"},
                              {0xffff, "\xef\xbf\xbf"},
                              {0x10000, "\xf0\x90\x80\x80"},
                              {0x10001, "\xf0\x90\x80\x81"},
                              {0x40000, "\xf1\x80\x80\x80"},
                              {0x10fffe, "\xf4\x8f\xbf\xbe"},
                              {0x10ffff, "\xf4\x8f\xbf\xbf"},
                              {0xFFFD, "\xef\xbf\xbd"},
                          }));

}
}
