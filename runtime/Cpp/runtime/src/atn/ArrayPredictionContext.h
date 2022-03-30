
/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/PredictionContext.h"

namespace antlr4 {
namespace atn {

  class SingletonPredictionContext;

  class ANTLR4CPP_PUBLIC ArrayPredictionContext final : public PredictionContext {
  public:
    static bool is(const PredictionContext &predictionContext) { return predictionContext.getContextType() == PredictionContextType::ARRAY; }

    static bool is(const PredictionContext *predictionContext) { return predictionContext != nullptr && is(*predictionContext); }

    /// Parent can be empty only if full ctx mode and we make an array
    /// from EMPTY and non-empty. We merge EMPTY by using null parent and
    /// returnState == EMPTY_RETURN_STATE.
    // Also here: we use a strong reference to our parents to avoid having them freed prematurely.
    //            See also SinglePredictionContext.
    const std::vector<Ref<const PredictionContext>> parents;

    /// Sorted for merge, no duplicates; if present, EMPTY_RETURN_STATE is always last.
    const std::vector<size_t> returnStates;

    explicit ArrayPredictionContext(Ref<const SingletonPredictionContext> const &a);

    ArrayPredictionContext(std::vector<Ref<const PredictionContext>> parents, std::vector<size_t> returnStates);

    virtual bool isEmpty() const override;
    virtual size_t size() const override;
    virtual Ref<const PredictionContext> getParent(size_t index) const override;
    virtual size_t getReturnState(size_t index) const override;
    bool operator == (const PredictionContext &o) const override;

    virtual std::string toString() const override;
  };

} // namespace atn
} // namespace antlr4

