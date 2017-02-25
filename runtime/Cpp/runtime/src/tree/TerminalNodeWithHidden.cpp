/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "Token.h"
#include "BufferedTokenStream.h"

#include "TerminalNodeWithHidden.h"

using namespace antlr4;
using namespace antlr4::tree;

TerminalNodeWithHidden::TerminalNodeWithHidden(BufferedTokenStream *tokens, int channel, Token *symbol)
  : TerminalNodeImpl(symbol) {
  collectHiddenTokens(tokens, channel, symbol);
}

void TerminalNodeWithHidden::collectHiddenTokens(BufferedTokenStream *tokens, int channel, Token *symbol) {
  if (symbol->getTokenIndex() == INVALID_INDEX) {
    // Must be a token conjured up during error recovery for missing token.
    // Tokens not in tokens buffer never have hidden tokens associated with them.
    return;
  }

  std::vector<Token *> left = tokens->getHiddenTokensToLeft(symbol->getTokenIndex(), channel);

  // Check the EOF special case separately.
  if (symbol->getType() == Token::EOF) {
    if (!left.empty()) {
      Token *firstHiddenLeft = left[0];
      if (firstHiddenLeft->getTokenIndex() == 0) { // EOF only gets hidden stuff if it's the only token.
        _leading = tokens->get(0, symbol->getTokenIndex() - 1);
      }
    }
    return;
  }

  if (!left.empty()) {
    Token *firstHiddenLeft = left[0];
    Token *prevReal = nullptr;
    if (firstHiddenLeft->getTokenIndex() != INVALID_INDEX) {
      prevReal = tokens->get(firstHiddenLeft->getTokenIndex() - 1);
    }

    if (prevReal == nullptr) { // This symbol is the first real token (or EOF token) of the file.
      _leading = tokens->get(0, symbol->getTokenIndex() - 1);
    } else {
      // Collect all tokens on next line after prev real.
      for (Token *t : left) {
        if (t->getLine() > prevReal->getLine()) {
          _leading.push_back(t);
        }
      }
    }
  }

  std::vector<Token *> right = tokens->getHiddenTokensToRight(symbol->getTokenIndex(), channel);
  if (!right.empty()) {
    Token *lastHiddenRight = right.back();
    Token *nextReal = tokens->get(lastHiddenRight->getTokenIndex() + 1);

    // If this is the last real token, collect all hidden to the right.
    if (nextReal->getType() == Token::EOF) {
      _trailing = tokens->get(right[0]->getTokenIndex(), nextReal->getTokenIndex());
    } else {
      // Collect all token text on same line to right.
      size_t tokenLine = symbol->getLine();
      for (Token *t : right) {
        if (t->getLine() == tokenLine) {
          _trailing.push_back(t);
        }
      }
    }
  }
}

std::vector<Token *> TerminalNodeWithHidden::getLeadingHidden() const {
  return _leading;
}

std::vector<Token *> TerminalNodeWithHidden::getTrailingHidden() const {
  return _trailing;
}

void TerminalNodeWithHidden::setLeadingHidden(std::vector<Token *> const &hiddenLeft) {
  _leading = hiddenLeft;
}

void TerminalNodeWithHidden::setTrailingHidden(std::vector<Token *> const &hiddenRight) {
  _trailing = hiddenRight;
}

std::string TerminalNodeWithHidden::getText() const {
  std::string result;
  for (Token *t : _leading) {
    result += t->getText();
  }

  result += TerminalNodeImpl::getText();

  for (Token *t : _trailing) {
    result += t->getText();
  }
  return result;
}
