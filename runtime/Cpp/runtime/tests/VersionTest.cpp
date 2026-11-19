#include <string>

#include "gtest/gtest.h"
#include "RuntimeMetaData.h"
#include "Version.h"

namespace antlr4 {
namespace {

  static_assert(ANTLRCPP_VERSION == ANTLRCPP_VERSION_MAJOR * 100000 +
                                   ANTLRCPP_VERSION_MINOR * 1000 + ANTLRCPP_VERSION_PATCH,
                "ANTLRCPP_VERSION must be a constant expression matching the version components");

  TEST(VersionTest, StringMatchesVersionComponents) {
    const std::string expected = std::to_string(ANTLRCPP_VERSION_MAJOR) + "." +
                                 std::to_string(ANTLRCPP_VERSION_MINOR) + "." +
                                 std::to_string(ANTLRCPP_VERSION_PATCH);
    EXPECT_EQ(std::string(ANTLRCPP_VERSION_STRING), expected);
    EXPECT_EQ(RuntimeMetaData::VERSION, expected);
    EXPECT_EQ(RuntimeMetaData::getRuntimeVersion(), expected);
  }

}
}
