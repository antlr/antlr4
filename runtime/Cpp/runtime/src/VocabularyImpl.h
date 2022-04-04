/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "Vocabulary.h"
#include "support/Span.h"

namespace antlr4 {

  class ANTLR4CPP_PUBLIC VocabularyImpl final : public Vocabulary {
  public:
    VocabularyImpl() : VocabularyImpl({}, {}) {}

    VocabularyImpl(antlrcpp::Span<const std::string_view> literalNames,
                   antlrcpp::Span<const std::string_view> symbolicNames)
        : VocabularyImpl(literalNames, symbolicNames, {}) {}

    VocabularyImpl(antlrcpp::Span<const std::string_view> literalNames,
                   antlrcpp::Span<const std::string_view> symbolicNames,
                   antlrcpp::Span<const std::string_view> displayNames);

    std::string_view getLiteralName(size_t tokenType) const override;

    std::string_view getSymbolicName(size_t tokenType) const override;

    std::string getDisplayName(size_t tokenType) const override;

  private:
    antlrcpp::Span<const std::string_view> _literalNames;
    antlrcpp::Span<const std::string_view> _symbolicNames;
    antlrcpp::Span<const std::string_view> _displayNames;
  };

}  // namespace antlr4
