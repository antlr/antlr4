﻿/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/ATNConfigSet.h"
#include "atn/ATNConfig.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC OrderedATNConfigSet : public ATNConfigSet {
  protected:
    virtual size_t getHash(ATNConfig *c) override;
  };

} // namespace atn
} // namespace antlr4
