
/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/PredictionContext.h"

namespace antlr4 {
namespace atn {

  class SingletonPredictionContext;
  class ANTLR4CPP_PUBLIC PredictionContextItem {
  public:
      Ref<PredictionContext> parent;
      size_t returnState;

      PredictionContextItem(const Ref<PredictionContext>& p, size_t s);
      bool operator == (const PredictionContextItem& o) const;
  };

  class ANTLR4CPP_PUBLIC ArrayPredictionContext : public PredictionContext {
  public:
    /// Parent can be empty only if full ctx mode and we make an array
    /// from EMPTY and non-empty. We merge EMPTY by using null parent and
    /// returnState == EMPTY_RETURN_STATE.
    // Also here: we use a strong reference to our parents to avoid having them freed prematurely.
    //            See also SinglePredictionContext.

    /// Sorted for merge, no duplicates; if present, the SingletonPredictionContext with returnState EMPTY_RETURN_STATE is always last.
    const std::vector<PredictionContextItem> contexts;

    ArrayPredictionContext(Ref<SingletonPredictionContext> const& a);
    ArrayPredictionContext(std::vector<PredictionContextItem>&& contexts_);
    virtual ~ArrayPredictionContext();

    virtual bool isEmptyContext() const override;
    virtual bool isSingletonContext() const final;
    virtual size_t size() const override;
    virtual Ref<PredictionContext> getParent(size_t index) const override;
    virtual size_t getReturnState(size_t index) const override;
    bool operator == (const PredictionContext &o) const override;

    virtual std::string toString() const override;
  };

} // namespace atn
} // namespace antlr4

