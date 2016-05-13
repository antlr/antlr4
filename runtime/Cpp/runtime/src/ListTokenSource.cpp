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

using namespace org::antlr::v4::runtime;

int ListTokenSource::getCharPositionInLine() {
  if (i < tokens.size()) {
    return tokens[i]->getCharPositionInLine();
  } else if (eofToken != nullptr) {
    return eofToken->getCharPositionInLine();
  } else if (tokens.size() > 0) {
    // have to calculate the result from the line/column of the previous
    // token, along with the text of the token.
    Ref<Token> lastToken = tokens.back();
    std::string tokenText = lastToken->getText();
    if (tokenText != "") {
      int lastNewLine = (int)tokenText.rfind('\n');
      if (lastNewLine >= 0) {
        return (int)tokenText.length() - lastNewLine - 1;
      }
    }

    return lastToken->getCharPositionInLine() + lastToken->getStopIndex() - lastToken->getStartIndex() + 1;
  }

  // only reach this if tokens is empty, meaning EOF occurs at the first
  // position in the input
  return 0;
}

Ref<Token> ListTokenSource::nextToken() {
  if (i >= tokens.size()) {
    if (eofToken == nullptr) {
      int start = -1;
      if (tokens.size() > 0) {
        int previousStop = tokens.back()->getStopIndex();
        if (previousStop != -1) {
          start = previousStop + 1;
        }
      }

      int stop = std::max(-1, start - 1);
      eofToken = std::dynamic_pointer_cast<Token>(_factory->create({ this, getInputStream() }, Token::EOF, "EOF",
        Token::DEFAULT_CHANNEL, start, stop, (int)getLine(), getCharPositionInLine()));
    }

    return eofToken;
  }

  Ref<Token> t = tokens[i];
  if (i == tokens.size() - 1 && t->getType() == Token::EOF) {
    eofToken = t;
  }

  i++;
  return t;
}

size_t ListTokenSource::getLine() const {
  if (i < tokens.size()) {
    return (size_t)tokens[i]->getLine();
  } else if (eofToken != nullptr) {
    return (size_t)eofToken->getLine();
  } else if (tokens.size() > 0) {
    // have to calculate the result from the line/column of the previous
    // token, along with the text of the token.
    Ref<Token> lastToken = tokens.back();
    int line = lastToken->getLine();

    std::string tokenText = lastToken->getText();
    if (tokenText != "") {
      for (size_t i = 0; i < tokenText.length(); i++) {
        if (tokenText[i] == '\n') {
          line++;
        }
      }
    }

    // if no text is available, assume the token did not contain any newline characters.
    return (size_t)line;
  }

  // only reach this if tokens is empty, meaning EOF occurs at the first
  // position in the input
  return 1;
}

CharStream *ListTokenSource::getInputStream() {
  if (i < tokens.size()) {
    return tokens[i]->getInputStream();
  } else if (eofToken != nullptr) {
    return eofToken->getInputStream();
  } else if (tokens.size() > 0) {
    return tokens[tokens.size() - 1]->getInputStream();
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
