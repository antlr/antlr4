#include <utility>

#include "gtest/gtest.h"
#include "support/BitSet.h"

namespace antlrcpp {
namespace {

  TEST(BitSet, Empty) {
    BitSet bs1, bs2;
    EXPECT_EQ(bs1.count(), 0);
    EXPECT_TRUE(bs1.empty());
    EXPECT_EQ(bs1.toString(), "{}");
    EXPECT_EQ(bs1.find(), std::nullopt);
    EXPECT_EQ(bs1, bs2);
  }

  TEST(BitSet, TestSetClear) {
    BitSet bs;
    EXPECT_FALSE(bs.test(0));
    EXPECT_FALSE(bs[0]);
    EXPECT_EQ(bs.toString(), "{}");
    bs.set(0);
    EXPECT_TRUE(bs.test(0));
    EXPECT_TRUE(bs[0]);
    EXPECT_EQ(bs.toString(), "{0}");
    bs.clear(0);
    EXPECT_FALSE(bs.test(0));
    EXPECT_FALSE(bs[0]);
    EXPECT_EQ(bs.toString(), "{}");
  }

  TEST(BitSet, Or31) {
    BitSet bs1, bs2;
    bs1.set(0);
    bs2.set(31);
    bs1 |= bs2;
    EXPECT_TRUE(bs1.test(0));
    EXPECT_TRUE(bs1[0]);
    EXPECT_TRUE(bs1.test(31));
    EXPECT_TRUE(bs1[31]);
    EXPECT_EQ(bs1.toString(), "{0, 31}");
  }

  TEST(BitSet, Or61) {
    BitSet bs1, bs2;
    bs1.set(0);
    bs2.set(61);
    bs1 |= bs2;
    EXPECT_TRUE(bs1.test(0));
    EXPECT_TRUE(bs1[0]);
    EXPECT_TRUE(bs1.test(61));
    EXPECT_TRUE(bs1[61]);
    EXPECT_EQ(bs1.toString(), "{0, 61}");
  }

  TEST(BitSet, Or127) {
    BitSet bs1, bs2;
    bs1.set(0);
    bs2.set(127);
    bs1 |= bs2;
    EXPECT_TRUE(bs1.test(0));
    EXPECT_TRUE(bs1[0]);
    EXPECT_TRUE(bs1.test(127));
    EXPECT_TRUE(bs1[127]);
    EXPECT_EQ(bs1.toString(), "{0, 127}");
  }

  TEST(BitSet, Find31) {
    BitSet bs;
    bs.set(31);
    EXPECT_EQ(bs.find(), 31);
  }

  TEST(BitSet, Find61) {
    BitSet bs;
    bs.set(61);
    EXPECT_EQ(bs.find(), 61);
  }

  TEST(BitSet, Find127) {
    BitSet bs;
    bs.set(127);
    EXPECT_EQ(bs.find(), 127);
  }

  TEST(BitSet, CopyConstructor) {
    BitSet bs1;
    bs1.set(31);
    BitSet bs2(bs1);
    EXPECT_EQ(bs1, bs2);
    bs1.set(63);
    bs1.set(127);
    BitSet bs3(bs1);
    EXPECT_EQ(bs1, bs3);
  }

  TEST(BitSet, CopyAssignment) {
    BitSet bs1;
    bs1.set(31);
    BitSet bs2;
    bs2.set(127);
    bs2 = bs1;
    EXPECT_EQ(bs1, bs2);
    bs1.set(63);
    bs1.set(127);
    BitSet bs3;
    bs3 = bs1;
    EXPECT_EQ(bs1, bs3);
  }

  TEST(BitSet, MoveConstructor) {
    BitSet bs1;
    bs1.set(31);
    BitSet bs2(std::move(bs1));
    bs2.set(63);
    bs2.set(127);
    BitSet bs3(std::move(bs2));
    BitSet bs4;
    bs4.set(31);
    bs4.set(63);
    bs4.set(127);
    EXPECT_EQ(bs3, bs4);
  }

  TEST(BitSet, MoveAssignment) {
    BitSet bs1;
    bs1.set(31);
    BitSet bs2;
    bs2.set(0);
    bs2 = std::move(bs1);
    bs2.set(63);
    bs2.set(127);
    BitSet bs3;
    bs3 = std::move(bs2);
    BitSet bs4;
    bs4.set(31);
    bs4.set(63);
    bs4.set(127);
    EXPECT_EQ(bs3, bs4);
  }

  TEST(BitSet, Equality) {
    BitSet bs1;
    bs1.set(31);
    bs1.set(63);
    bs1.set(127);
    BitSet bs2;
    bs2.set(31);
    bs2.set(63);
    bs2.set(127);
    EXPECT_EQ(bs1, bs2);
    bs2.reset();
    bs2.set(31);
    EXPECT_NE(bs1, bs2);
    std::swap(bs1, bs2);
    EXPECT_NE(bs1, bs2);
    bs2.reset();
    EXPECT_EQ(bs1, bs2);
  }

} // namespace
} // namespace antlrcpp
