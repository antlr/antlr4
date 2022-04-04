/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "Token.h"

#include "VocabularyImpl.h"

using namespace antlr4;

VocabularyImpl::VocabularyImpl(antlrcpp::Span<const std::string_view> literalNames,
                               antlrcpp::Span<const std::string_view> symbolicNames,
                               antlrcpp::Span<const std::string_view> displayNames)
  : Vocabulary(std::max(displayNames.size(), std::max(literalNames.size(), symbolicNames.size())) - 1),
    _literalNames(literalNames), _symbolicNames(symbolicNames), _displayNames(displayNames) {}

std::string_view VocabularyImpl::getLiteralName(size_t tokenType) const {
  if (tokenType < _literalNames.size()) {
    return _literalNames[tokenType];
  }

  return "";
}

std::string_view VocabularyImpl::getSymbolicName(size_t tokenType) const {
  if (tokenType == Token::EOF) {
    return "EOF";
  }

  if (tokenType < _symbolicNames.size()) {
    return _symbolicNames[tokenType];
  }

  return "";
}

std::string VocabularyImpl::getDisplayName(size_t tokenType) const {
  if (tokenType < _displayNames.size()) {
    std::string_view displayName = _displayNames[tokenType];
    if (!displayName.empty()) {
      return std::string(displayName);
    }
  }

  std::string_view literalName = getLiteralName(tokenType);
  if (!literalName.empty()) {
    return std::string(literalName);
  }

  std::string_view symbolicName = getSymbolicName(tokenType);
  if (!symbolicName.empty()) {
    return std::string(symbolicName);
  }

  return std::to_string(tokenType);
}
