﻿/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/ATNState.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC RuleStartState final : public ATNState {
  public:
    RuleStartState();

    RuleStopState *stopState = nullptr;
    bool isLeftRecursiveRule = false;

    virtual size_t getStateType() override;

  };

} // namespace atn
} // namespace antlr4
