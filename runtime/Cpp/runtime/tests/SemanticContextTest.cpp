/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include <memory>

#include "gtest/gtest.h"
#include "atn/SemanticContext.h"

namespace antlr4 {
namespace atn {
namespace {

  const Ref<const SemanticContext> A = std::make_shared<SemanticContext::Predicate>(0, 0, false);
  const Ref<const SemanticContext> B = std::make_shared<SemanticContext::Predicate>(0, 2, false);
  const Ref<const SemanticContext> C = std::make_shared<SemanticContext::Predicate>(0, 3, false);
  const Ref<const SemanticContext> PREC_2 = std::make_shared<SemanticContext::PrecedencePredicate>(2);
  const Ref<const SemanticContext> PREC_5 = std::make_shared<SemanticContext::PrecedencePredicate>(5);

  TEST(SemanticContextTest, AndIgnoresOperandOrder) {
    EXPECT_EQ(*SemanticContext::And(A, B), *SemanticContext::And(B, A));
    EXPECT_EQ(SemanticContext::And(A, B)->hashCode(), SemanticContext::And(B, A)->hashCode());

    EXPECT_EQ("{0:0}? && {0:2}? && ", SemanticContext::And(B, A)->toString());
  }

  TEST(SemanticContextTest, OrIgnoresOperandOrder) {
    EXPECT_EQ(*SemanticContext::Or(A, B), *SemanticContext::Or(B, A));
    EXPECT_EQ(SemanticContext::Or(A, B)->hashCode(), SemanticContext::Or(B, A)->hashCode());
  }

  TEST(SemanticContextTest, AndKeepsLowestPrecedenceOrKeepsHighest) {
    EXPECT_EQ("{0:0}? && {2>=prec}? && ",
              SemanticContext::And(SemanticContext::And(PREC_5, A), PREC_2)->toString());

    EXPECT_EQ("{0:0}? || {5>=prec}? || ",
              SemanticContext::Or(SemanticContext::Or(PREC_2, A), PREC_5)->toString());
  }

  TEST(SemanticContextTest, OrOrdersAndsOperandByOperand) {
    EXPECT_EQ("{0:0}? && {0:2}? &&  || {0:0}? && {0:3}? &&  || ",
              SemanticContext::Or(SemanticContext::And(A, C), SemanticContext::And(A, B))->toString());
  }

}  // namespace
}  // namespace atn
}  // namespace antlr4
