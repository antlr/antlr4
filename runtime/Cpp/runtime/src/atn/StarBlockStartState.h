/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/BlockStartState.h"

namespace antlr4 {
namespace atn {

  /// The block that begins a closure loop.
  class ANTLR4CPP_PUBLIC StarBlockStartState final : public BlockStartState {
  public:
    StarBlockStartState() : BlockStartState(ATNStateType::STAR_BLOCK_START) {}
  };

} // namespace atn
} // namespace antlr4
