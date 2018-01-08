/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "XPathElement.h"

namespace antlr4 {
namespace tree {
namespace xpath {

  /// Either {@code ID} at start of path or {@code ...//ID} in middle of path.
  class ANTLR4CPP_PUBLIC XPathRuleAnywhereElement : public XPathElement {
  public:
    XPathRuleAnywhereElement(const std::string &ruleName, int ruleIndex);

    virtual std::vector<ParseTree *> evaluate(ParseTree *t) override;

  protected:
    int _ruleIndex = 0;
  };

} // namespace xpath
} // namespace tree
} // namespace antlr4
