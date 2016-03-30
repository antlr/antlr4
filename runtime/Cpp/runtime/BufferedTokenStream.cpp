/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
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

#include "WritableToken.h"
#include "Lexer.h"
#include "RuleContext.h"
#include "Interval.h"
#include "StringBuilder.h"

#include "BufferedTokenStream.h"

using namespace org::antlr::v4::runtime;

BufferedTokenStream::BufferedTokenStream(TokenSource *tokenSource) {
  InitializeInstanceFields();
  if (tokenSource == nullptr) {
    throw new NullPointerException(L"tokenSource cannot be null");
  }
  this->tokenSource = tokenSource;
}

TokenSource *BufferedTokenStream::getTokenSource() const {
  return tokenSource;
}

size_t BufferedTokenStream::index() {
  return p;
}

ssize_t BufferedTokenStream::mark() {
  return 0;
}

void BufferedTokenStream::release(ssize_t marker) {
  // no resources to release
}

void BufferedTokenStream::reset() {
  seek(0);
}

void BufferedTokenStream::seek(size_t index) {
  lazyInit();
  p = adjustSeekIndex(index);
}

size_t BufferedTokenStream::size() {
  return tokens.size();
}

void BufferedTokenStream::consume() {
  if (LA(1) == _EOF) {
    throw new IllegalStateException(L"cannot consume EOF");
  }

  if (sync(p + 1)) {
    p = adjustSeekIndex(p + 1);
  }
}

bool BufferedTokenStream::sync(size_t i) {
  size_t n = i - tokens.size() + 1; // how many more elements we need?

  if (n > 0) {
    size_t fetched = fetch(n);
    return fetched >= n;
  }

  return true;
}

size_t BufferedTokenStream::fetch(size_t n) {
  if (fetchedEOF) {
    return 0;
  }

  for (size_t i = 0; i < n; i++) {
    Token *t = tokenSource->nextToken();
    if (dynamic_cast<WritableToken*>(t) != nullptr) {
      (static_cast<WritableToken*>(t))->setTokenIndex((int)tokens.size());
    }
    tokens.push_back(t);
    if (t->getType() == Token::_EOF) {
      fetchedEOF = true;
      return i + 1;
    }
  }

  return n;
}

Token *BufferedTokenStream::get(size_t i) const {
  if (i >= tokens.size()) {
    throw IndexOutOfBoundsException(std::wstring(L"token index ") +
                                    std::to_wstring(i) +
                                    std::wstring(L" out of range 0..") +
                                    std::to_wstring(tokens.size() - 1));
  }
  return tokens[i];
}

std::vector<Token*> BufferedTokenStream::get(size_t start, size_t stop) {
  std::vector<Token*> subset;

  lazyInit();

  if (tokens.empty()) {
    return subset;
  }

  if (stop >= tokens.size()) {
    stop = tokens.size() - 1;
  }
  for (size_t i = start; i <= stop; i++) {
    Token *t = tokens[i];
    if (t->getType() == Token::_EOF) {
      break;
    }
    subset.push_back(t);
  }
  return subset;
}

size_t BufferedTokenStream::LA(ssize_t i) {
  return (size_t)LT(i)->getType();
}

Token *BufferedTokenStream::LB(size_t k) {
  if (k > p) {
    return nullptr;
  }
  return tokens[(size_t)(p - k)];
}

Token *BufferedTokenStream::LT(ssize_t k) {
  lazyInit();
  if (k == 0) {
    return nullptr;
  }
  if (k < 0) {
    return LB((size_t)-k);
  }

  size_t i = (size_t)((ssize_t)p + k - 1);
  sync(i);
  if (i >= tokens.size()) { // return EOF token
                                 // EOF must be last token
    return tokens.back();
  }

  return tokens[i];
}

size_t BufferedTokenStream::adjustSeekIndex(size_t i) {
  return i;
}

void BufferedTokenStream::lazyInit() {
  if (_needSetup) {
    setup();
  }
}

void BufferedTokenStream::setup() {
  _needSetup = false;
  sync(0);
  p = adjustSeekIndex(0);
}

void BufferedTokenStream::setTokenSource(TokenSource *tokenSource) {
  this->tokenSource = tokenSource;
  tokens.clear();
  _needSetup = true;
}

std::vector<Token*> BufferedTokenStream::getTokens() {
  return tokens;
}

std::vector<Token*> BufferedTokenStream::getTokens(int start, int stop) {
  return getTokens(start, stop, nullptr);
}

std::vector<Token*> BufferedTokenStream::getTokens(int start, int stop, std::vector<int> *types) {
  lazyInit();
  if (start < 0 || stop >= (int)tokens.size() || stop < 0 || (int)start >= (int)tokens.size()) {
    throw new IndexOutOfBoundsException(std::wstring(L"start ") +
                                        std::to_wstring(start) +
                                        std::wstring(L" or stop ") +
                                        std::to_wstring(stop) +
                                        std::wstring(L" not in 0..") +
                                        std::to_wstring(tokens.size() - 1));
  }
  if (start > stop) {
    return std::vector<Token*>();
  }

  // list = tokens[start:stop]:{T t, t.getType() in types}
  std::vector<Token*> filteredTokens = std::vector<Token*>();
  for (size_t i = (size_t)start; i <= (size_t)stop; i++) {
    Token *tok = tokens[i];

    if (types == nullptr) {
      filteredTokens.push_back(tok);
    } else {
      if (types == nullptr || std::find(types->begin(), types->end(), tok->getType()) != types->end()) {
        filteredTokens.push_back(tok);
      }
    }
  }
  if (filteredTokens.empty()) {
    filteredTokens.clear();
  }
  return filteredTokens;
}

std::vector<Token*> BufferedTokenStream::getTokens(int start, int stop, int ttype) {
  std::vector<int> *s = new std::vector<int>();
  s->insert(s->begin(), ttype);
  return getTokens(start,stop, s);
}

ssize_t BufferedTokenStream::nextTokenOnChannel(size_t i, int channel) {
  sync(i);
  if (i >= size()) {
    return -1;
  }

  Token *token = tokens[i];
  while (token->getChannel() != channel) {
    if (token->getType() == Token::_EOF) {
      return -1;
    }
    i++;
    sync(i);
    token = tokens[i];
  }
  return (ssize_t)i;
}

ssize_t BufferedTokenStream::previousTokenOnChannel(size_t i, int channel) const {
  do {
    if (tokens[i]->getChannel() == channel)
      return (ssize_t)i;
    if (i == 0)
      return -1;
    i--;
  } while (true);
  return -1;
}

std::vector<Token*> BufferedTokenStream::getHiddenTokensToRight(size_t tokenIndex, int channel) {
  lazyInit();
  if (tokenIndex >= tokens.size()) {
    throw new IndexOutOfBoundsException(std::to_wstring(tokenIndex) +
                                        std::wstring(L" not in 0..") +
                                        std::to_wstring(tokens.size() - 1));
  }

  ssize_t nextOnChannel = nextTokenOnChannel(tokenIndex + 1, Lexer::DEFAULT_TOKEN_CHANNEL);
  ssize_t to;
  size_t from = tokenIndex + 1;
  // if none onchannel to right, nextOnChannel=-1 so set to = last token
  if (nextOnChannel == -1) {
    to = (ssize_t)size() - 1;
  } else {
    to = nextOnChannel;
  }

  return filterForChannel(from, (size_t)to, channel);
}

std::vector<Token*> BufferedTokenStream::getHiddenTokensToRight(size_t tokenIndex) {
  return getHiddenTokensToRight(tokenIndex, -1);
}

std::vector<Token*> BufferedTokenStream::getHiddenTokensToLeft(size_t tokenIndex, int channel) {
  lazyInit();
  if (tokenIndex >= tokens.size()) {
    throw new IndexOutOfBoundsException(std::to_wstring(tokenIndex) +
                                        std::wstring(L" not in 0..") +
                                        std::to_wstring(tokens.size() - 1));
  }

  ssize_t prevOnChannel = previousTokenOnChannel(tokenIndex - 1, Lexer::DEFAULT_TOKEN_CHANNEL);
  if (prevOnChannel == (ssize_t)tokenIndex - 1) {
    return std::vector<Token*>();
  }
  // if none onchannel to left, prevOnChannel=-1 then from=0
  size_t from = (size_t)(prevOnChannel + 1);
  size_t to = tokenIndex - 1;

  return filterForChannel(from, to, channel);
}

std::vector<Token*> BufferedTokenStream::getHiddenTokensToLeft(size_t tokenIndex) {
  return getHiddenTokensToLeft(tokenIndex, -1);
}

std::vector<Token*> BufferedTokenStream::filterForChannel(size_t from, size_t to, int channel) {
  std::vector<Token*> hidden = std::vector<Token*>();
  for (size_t i = from; i <= to; i++) {
    Token *t = tokens[i];
    if (channel == -1) {
      if (t->getChannel() != Lexer::DEFAULT_TOKEN_CHANNEL) {
        hidden.push_back(t);
      }
    } else {
      if (t->getChannel() == channel) {
        hidden.push_back(t);
      }
    }
  }
  if (hidden.empty()) {
    return std::vector<Token*>();
  }
  return hidden;
}

/**
 * Get the text of all tokens in this buffer.
 */
std::string BufferedTokenStream::getSourceName()
{
  return tokenSource->getSourceName();
}

std::wstring BufferedTokenStream::getText() {
  lazyInit();
  fill();
  return getText(misc::Interval(0, (int)size() - 1));
}

std::wstring BufferedTokenStream::getText(const misc::Interval &interval) {
  int start = interval.a;
  int stop = interval.b;
  if (start < 0 || stop < 0) {
    return L"";
  }
  lazyInit();
  if (stop >= (int)tokens.size()) {
    stop = (int)tokens.size() - 1;
  }

  antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
  for (size_t i = (size_t)start; i <= (size_t)stop; i++) {
    Token *t = tokens[i];
    if (t->getType() == Token::_EOF) {
      break;
    }
    buf->append(t->getText());
  }
  return buf->toString();
}

std::wstring BufferedTokenStream::getText(RuleContext *ctx) {
  return getText(ctx->getSourceInterval());
}

std::wstring BufferedTokenStream::getText(Token *start, Token *stop) {
  if (start != nullptr && stop != nullptr) {
    return getText(misc::Interval(start->getTokenIndex(), stop->getTokenIndex()));
  }

  return L"";
}

void BufferedTokenStream::fill() {
  lazyInit();
  const size_t blockSize = 1000;
  while (true) {
    size_t fetched = fetch(blockSize);
    if (fetched < blockSize) {
      return;
    }
  }
}

void BufferedTokenStream::InitializeInstanceFields() {
  tokens.reserve(100);
  _needSetup = true;
  fetchedEOF = false;
}
