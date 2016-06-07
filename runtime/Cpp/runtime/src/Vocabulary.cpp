/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Dan McLaughlin
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "Token.h"

#include "Vocabulary.h"

using namespace antlr4::dfa;

const std::vector<std::string> Vocabulary::EMPTY_NAMES;
const Vocabulary Vocabulary::EMPTY_VOCABULARY = Vocabulary(EMPTY_NAMES, EMPTY_NAMES, EMPTY_NAMES);

Vocabulary::Vocabulary(const std::vector<std::string> &literalNames, const std::vector<std::string> &symbolicNames)
: Vocabulary(literalNames, symbolicNames, {}) {
}

Vocabulary::Vocabulary(const std::vector<std::string> &literalNames,
  const std::vector<std::string> &symbolicNames, const std::vector<std::string> &displayNames)
  : _literalNames(!literalNames.empty() ? literalNames : EMPTY_NAMES),
    _symbolicNames(!symbolicNames.empty() ? symbolicNames : EMPTY_NAMES),
    _displayNames(!displayNames.empty() ? displayNames : EMPTY_NAMES),
    _maxTokenType(std::max((int)_displayNames.size(), std::max((int)_literalNames.size(), (int)_symbolicNames.size())) - 1) {
  // See note here on -1 part: https://github.com/antlr/antlr4/pull/1146
}

Vocabulary Vocabulary::fromTokenNames(const std::vector<std::string> &tokenNames) {
  if (tokenNames.empty()) {
    return EMPTY_VOCABULARY;
  }

  std::vector<std::string> literalNames = tokenNames;
  std::vector<std::string> symbolicNames = tokenNames;
  std::locale locale;
  for (size_t i = 0; i < tokenNames.size(); i++) {
    std::string tokenName = tokenNames[i];
    if (tokenName == "") {
      continue;
    }

    if (!tokenName.empty()) {
      char firstChar = tokenName[0];
      if (firstChar == '\'') {
        symbolicNames[i] = "";
        continue;
      } else if (std::isupper(firstChar, locale)) {
        literalNames[i] = "";
        continue;
      }
    }

    // wasn't a literal or symbolic name
    literalNames[i] = "";
    symbolicNames[i] = "";
  }

  return Vocabulary(literalNames, symbolicNames, tokenNames);
}

int Vocabulary::getMaxTokenType() const {
  return _maxTokenType;
}

std::string Vocabulary::getLiteralName(ssize_t tokenType) const {
  if (tokenType >= 0 && tokenType < (int)_literalNames.size()) {
    return _literalNames[tokenType];
  }

  return "";
}

std::string Vocabulary::getSymbolicName(ssize_t tokenType) const {
  if (tokenType >= 0 && tokenType < (int)_symbolicNames.size()) {
    return _symbolicNames[tokenType];
  }

  if (tokenType == Token::EOF) {
    return "EOF";
  }

  return "";
}

std::string Vocabulary::getDisplayName(ssize_t tokenType) const {
  if (tokenType >= 0 && tokenType < (int)_displayNames.size()) {
    std::string displayName = _displayNames[tokenType];
    if (!displayName.empty()) {
      return displayName;
    }
  }

  std::string literalName = getLiteralName(tokenType);
  if (!literalName.empty()) {
    return literalName;
  }

  std::string symbolicName = getSymbolicName(tokenType);
  if (!symbolicName.empty()) {
    return symbolicName;
  }

  return std::to_string(tokenType);
}
