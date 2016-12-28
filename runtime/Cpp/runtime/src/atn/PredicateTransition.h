﻿/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/AbstractPredicateTransition.h"
#include "SemanticContext.h"

namespace antlr4 {
namespace atn {

  /// TO_DO: this is old comment:
  ///  A tree of semantic predicates from the grammar AST if label==SEMPRED.
  ///  In the ATN, labels will always be exactly one predicate, but the DFA
  ///  may have to combine a bunch of them as it collects predicates from
  ///  multiple ATN configurations into a single DFA state.
  class ANTLR4CPP_PUBLIC PredicateTransition final : public AbstractPredicateTransition {
  public:
    const size_t ruleIndex;
    const size_t predIndex;
    const bool isCtxDependent; // e.g., $i ref in pred

    PredicateTransition(ATNState *target, size_t ruleIndex, size_t predIndex, bool isCtxDependent);

    virtual SerializationType getSerializationType() const override;

    virtual bool isEpsilon() const override;
    virtual bool matches(size_t symbol, size_t minVocabSymbol, size_t maxVocabSymbol) const override;

    Ref<SemanticContext::Predicate> getPredicate() const;

    virtual std::string toString() const override;

  };

} // namespace atn
} // namespace antlr4
