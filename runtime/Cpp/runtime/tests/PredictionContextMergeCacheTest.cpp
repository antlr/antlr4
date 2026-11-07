/* Copyright (c) 2026 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/PredictionContext.h"
#include "atn/PredictionContextMergeCache.h"
#include "atn/PredictionContextMergeCacheOptions.h"
#include "atn/SingletonPredictionContext.h"

#include "gtest/gtest.h"

namespace antlr4::atn {
namespace {

  class PredictionContextMergeCacheTest : public testing::Test {
   protected:
    // Keys are compared structurally, so each context here takes a distinct
    // return state to keep them distinct as keys.
    Ref<const PredictionContext> Ctx(size_t returnState) {
      return SingletonPredictionContext::create(PredictionContext::EMPTY, returnState);
    }

    PredictionContextMergeCache cache;
    Ref<const PredictionContext> a = Ctx(1);
    Ref<const PredictionContext> b = Ctx(2);
    Ref<const PredictionContext> merged = Ctx(3);
  };

  TEST_F(PredictionContextMergeCacheTest, PutThenGetReturnsStoredValue) {
    cache.put(a, b, merged);

    EXPECT_EQ(cache.get(a, b), merged);
  }

  TEST_F(PredictionContextMergeCacheTest, GetOnAbsentKeyReturnsNull) {
    EXPECT_EQ(cache.get(a, b), nullptr);
  }

  // Equal keys are equal contexts, not equal addresses.
  TEST_F(PredictionContextMergeCacheTest, StructurallyEqualKeysAreOneEntry) {
    const Ref<const PredictionContext> sameAsA = Ctx(1);
    const Ref<const PredictionContext> sameAsB = Ctx(2);
    ASSERT_NE(a.get(), sameAsA.get());

    cache.put(a, b, merged);

    EXPECT_EQ(cache.get(sameAsA.get(), sameAsB.get()), merged);
  }

  // An entry holds its own keys, so they stay readable once the caller lets go.
  TEST_F(PredictionContextMergeCacheTest, CachedKeysOutliveTheCallersRefs) {
    {
      const Ref<const PredictionContext> scopedA = Ctx(1);
      const Ref<const PredictionContext> scopedB = Ctx(2);
      cache.put(scopedA, scopedB, merged);
    }

    EXPECT_EQ(cache.get(a.get(), b.get()), merged);
  }

  TEST_F(PredictionContextMergeCacheTest, GetRefreshesRecency) {
    PredictionContextMergeCache bounded(PredictionContextMergeCacheOptions().setMaxSize(2));
    const Ref<const PredictionContext> c = Ctx(4);
    const Ref<const PredictionContext> d = Ctx(5);

    bounded.put(a, b, a);
    bounded.put(b, c, b);
    bounded.get(a, b);
    bounded.put(c, d, c);

    EXPECT_EQ(bounded.get(a, b), a) << "the entry read most recently should stay";
    EXPECT_EQ(bounded.get(b, c), nullptr) << "the entry left untouched should go";
  }

  TEST_F(PredictionContextMergeCacheTest, MaxSizeZeroDisablesTheCache) {
    PredictionContextMergeCache disabled(PredictionContextMergeCacheOptions().setMaxSize(0));

    EXPECT_EQ(disabled.put(a, b, merged), merged);
    EXPECT_EQ(disabled.get(a, b), nullptr);
  }

}  // namespace
}  // namespace antlr4::atn
