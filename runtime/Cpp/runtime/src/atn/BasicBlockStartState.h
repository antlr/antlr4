﻿/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "antlr4-common.h"
#include "atn/BlockStartState.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC BasicBlockStartState final : public BlockStartState {

  public:
    virtual size_t getStateType() override;

  };

} // namespace atn
} // namespace antlr4
