/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <string>
#include "antlr4-common.h"
#include "tree/ParseTreeType.h"
#include "misc/Interval.h"
#include "Token.h"
#include "tree/TerminalNode.h"

namespace antlr4 {
namespace tree {

  class ANTLR4CPP_PUBLIC TerminalNodeImpl : public TerminalNode {
  public:
    Token *symbol;

    explicit TerminalNodeImpl(Token *symbol) : TerminalNode(ParseTreeType::TERMINAL), symbol(symbol) {}

    Token* getSymbol() const override;
    void setParent(RuleContext *parent) override;
    misc::Interval getSourceInterval() override;

    std::any accept(ParseTreeVisitor *visitor) override;

    std::string getText() override;
    std::string toStringTree(Parser *parser, bool pretty = false) override;
    std::string toString() override;
    std::string toStringTree(bool pretty = false) override;
  };

} // namespace tree
} // namespace antlr4
