/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Dan McLaughlin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "Exceptions.h"

#include "RuleTagToken.h"

using namespace org::antlr::v4::runtime::tree::pattern;

RuleTagToken::RuleTagToken(const std::wstring &ruleName, int _bypassTokenType) : bypassTokenType(_bypassTokenType) {
}

RuleTagToken::RuleTagToken(const std::wstring &ruleName, int bypassTokenType, const std::wstring &label) : ruleName(ruleName), bypassTokenType(bypassTokenType), label(label) {
  if (ruleName.empty()) {
    throw IllegalArgumentException("ruleName cannot be null or empty.");
  }

}

std::wstring RuleTagToken::getRuleName() {
  return ruleName;
}

std::wstring RuleTagToken::getLabel() {
  return label;
}

size_t RuleTagToken::getChannel() {
  return DEFAULT_CHANNEL;
}

std::wstring RuleTagToken::getText() {
  if (label != L"") {
    return std::wstring(L"<") + label + std::wstring(L":") + ruleName + std::wstring(L">");
  }

  return std::wstring(L"<") + ruleName + std::wstring(L">");
}

int RuleTagToken::getType() const {
  return bypassTokenType;
}

int RuleTagToken::getLine() {
  return 0;
}

int RuleTagToken::getCharPositionInLine() {
  return -1;
}

int RuleTagToken::getTokenIndex() {
  return -1;
}

int RuleTagToken::getStartIndex() {
  return -1;
}

int RuleTagToken::getStopIndex() {
  return -1;
}

org::antlr::v4::runtime::TokenSource *RuleTagToken::getTokenSource() {
  return nullptr;
}

org::antlr::v4::runtime::CharStream *RuleTagToken::getInputStream() {
  return nullptr;
}

std::wstring RuleTagToken::toString() {
  return ruleName + std::wstring(L":") + std::to_wstring(bypassTokenType);
}
