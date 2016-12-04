/* Copyright (c) 2012 The ANTLR Project Authors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "tree/ParseTree.h"

namespace antlr4 {
namespace tree {

  class ANTLR4CPP_PUBLIC TerminalNode : public ParseTree {
  public:
    virtual Token* getSymbol() = 0;
  };

} // namespace tree
} // namespace antlr4
