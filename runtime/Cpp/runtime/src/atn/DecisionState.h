/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/ATNState.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC DecisionState : public ATNState {
  public:
    int decision = -1;
    bool nonGreedy = false;

    DecisionState() = default;

    virtual std::string toString() const override;
  };

} // namespace atn
} // namespace antlr4
