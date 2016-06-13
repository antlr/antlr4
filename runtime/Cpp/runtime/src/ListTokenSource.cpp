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

#include "Token.h"
#include "CommonToken.h"
#include "CharStream.h"

#include "ListTokenSource.h"

using namespace antlr4;

ListTokenSource::ListTokenSource(std::vector<std::unique_ptr<Token>> tokens) : ListTokenSource(std::move(tokens), "") {
}

ListTokenSource::ListTokenSource(std::vector<std::unique_ptr<Token>> tokens_, const std::string &sourceName_)
  : tokens(std::move(tokens_)), sourceName(sourceName_) {
  InitializeInstanceFields();
  if (tokens.empty()) {
    throw "tokens cannot be null";
  }

  // Check if there is an eof token and create one if not.
  if (tokens.back()->getType() != Token::EOF) {
    Token *lastToken = tokens.back().get();
    int start = -1;
    int previousStop = lastToken->getStopIndex();
    if (previousStop != -1) {
      start = previousStop + 1;
    }

    int stop = std::max(-1, start - 1);
    tokens.emplace_back((_factory->create({ this, getInputStream() }, Token::EOF, "EOF",
      Token::DEFAULT_CHANNEL, start, stop, (int)lastToken->getLine(), lastToken->getCharPositionInLine())));
  }
}

int ListTokenSource::getCharPositionInLine() {
  if (i < tokens.size()) {
    return tokens[i]->getCharPositionInLine();
  }
  return 0;
}

std::unique_ptr<Token> ListTokenSource::nextToken() {
  if (i < tokens.size()) {
    return std::move(tokens[i++]);
  }
  return nullptr;
}

size_t ListTokenSource::getLine() const {
  if (i < tokens.size()) {
    return (size_t)tokens[i]->getLine();
  }

  return 1;
}

CharStream *ListTokenSource::getInputStream() {
  if (i < tokens.size()) {
    return tokens[i]->getInputStream();
  } else if (!tokens.empty()) {
    return tokens.back()->getInputStream();
  }

  // no input stream information is available
  return nullptr;
}

std::string ListTokenSource::getSourceName() {
  if (sourceName != "") {
    return sourceName;
  }

  CharStream *inputStream = getInputStream();
  if (inputStream != nullptr) {
    return inputStream->getSourceName();
  }

  return "List";
}

Ref<TokenFactory<CommonToken>> ListTokenSource::getTokenFactory() {
  return _factory;
}

void ListTokenSource::InitializeInstanceFields() {
  i = 0;
  _factory = CommonTokenFactory::DEFAULT;
}
