/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "tree/TerminalNodeWithHidden.h"

namespace antlr4 {
namespace tree {

  /** A version of {@link org.antlr.v4.runtime.tree.TerminalNodeWithHidden} tagged
   *  as an {@link ErrorNode}.
   */
  class ANTLR4CPP_PUBLIC ErrorNodeWithHidden : TerminalNodeWithHidden {
  public:
    ErrorNodeWithHidden(BufferedTokenStream *tokens, int channel, Token *symbol);
  };

} // namespace tree
} // namespace antlr4
