/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "XPathElement.h"

namespace antlr4 {
namespace tree {
namespace xpath {

  class ANTLR4CPP_PUBLIC XPathTokenElement : public XPathElement {
  public:
    XPathTokenElement(const std::string &tokenName, size_t tokenType);

    virtual std::vector<ParseTree *> evaluate(ParseTree *t) override;

  protected:
    size_t _tokenType = 0;
  };

} // namespace xpath
} // namespace tree
} // namespace antlr4
