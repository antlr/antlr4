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

#include "misc/Interval.h"
#include "TokenSource.h"
#include "support/StringUtils.h"
#include "CharStream.h"
#include "support/CPPUtils.h"

#include "CommonToken.h"

using namespace antlr4;
using namespace antlrcpp;

const std::pair<TokenSource*, CharStream*> CommonToken::EMPTY_SOURCE;

CommonToken::CommonToken(int type) {
  InitializeInstanceFields();
  _type = type;
}

CommonToken::CommonToken(std::pair<TokenSource*, CharStream*> source, int type, int channel, int start, int stop) {
  InitializeInstanceFields();
  _source = source;
  _type = type;
  _channel = channel;
  _start = start;
  _stop = stop;
  if (_source.first != nullptr) {
    _line = (int)source.first->getLine();
    _charPositionInLine = source.first->getCharPositionInLine();
  }
}

CommonToken::CommonToken(int type, const std::string &text) {
  InitializeInstanceFields();
  _type = type;
  _channel = DEFAULT_CHANNEL;
  _text = text;
  _source = EMPTY_SOURCE;
}

CommonToken::CommonToken(Token *oldToken) {
  InitializeInstanceFields();
  _type = oldToken->getType();
  _line = oldToken->getLine();
  _index = oldToken->getTokenIndex();
  _charPositionInLine = oldToken->getCharPositionInLine();
  _channel = oldToken->getChannel();
  _start = oldToken->getStartIndex();
  _stop = oldToken->getStopIndex();

  if (is<CommonToken *>(oldToken)) {
    _text = (static_cast<CommonToken *>(oldToken))->_text;
    _source = (static_cast<CommonToken *>(oldToken))->_source;
  } else {
    _text = oldToken->getText();
    _source = { oldToken->getTokenSource(), oldToken->getInputStream() };
  }
}

int CommonToken::getType() const {
  return _type;
}

void CommonToken::setLine(int line) {
  _line = line;
}

std::string CommonToken::getText() const {
  if (!_text.empty()) {
    return _text;
  }

  CharStream *input = getInputStream();
  if (input == nullptr) {
    return "";
  }
  size_t n = input->size();
  if ((size_t)_start < n && (size_t)_stop < n) {
    return input->getText(misc::Interval(_start, _stop));
  } else {
    return "<EOF>";
  }
}

void CommonToken::setText(const std::string &text) {
  _text = text;
}

int CommonToken::getLine() const {
  return _line;
}

int CommonToken::getCharPositionInLine() const {
  return _charPositionInLine;
}

void CommonToken::setCharPositionInLine(int charPositionInLine) {
  _charPositionInLine = charPositionInLine;
}

size_t CommonToken::getChannel() const {
  return _channel;
}

void CommonToken::setChannel(int channel) {
  _channel = channel;
}

void CommonToken::setType(int type) {
  _type = type;
}

int CommonToken::getStartIndex() const {
  return _start;
}

void CommonToken::setStartIndex(int start) {
  _start = start;
}

int CommonToken::getStopIndex() const {
  return _stop;
}

void CommonToken::setStopIndex(int stop) {
  _stop = stop;
}

int CommonToken::getTokenIndex() const {
  return _index;
}

void CommonToken::setTokenIndex(int index) {
  _index = index;
}

antlr4::TokenSource *CommonToken::getTokenSource() const {
  return _source.first;
}

antlr4::CharStream *CommonToken::getInputStream() const {
  return _source.second;
}

std::string CommonToken::toString() const {
  std::stringstream ss;

  std::string channelStr;
  if (_channel > 0) {
    channelStr = ",channel=" + std::to_string(_channel);
  }
  std::string txt = getText();
  if (!txt.empty()) {
    antlrcpp::replaceAll(txt, "\n", "\\n");
    antlrcpp::replaceAll(txt, "\r", "\\r");
    antlrcpp::replaceAll(txt, "\t", "\\t");
  } else {
    txt = "<no text>";
  }

  ss << "[@" << getTokenIndex() << "," << _start << ":" << _stop << "='" << txt << "',<" << _type << ">" << channelStr
    << "," << _line << ":" << getCharPositionInLine() << "]";

  return ss.str();
}

void CommonToken::InitializeInstanceFields() {
  _type = 0;
  _line = 0;
  _charPositionInLine = -1;
  _channel = DEFAULT_CHANNEL;
  _index = -1;
  _start = 0;
  _stop = 0;
  _source = EMPTY_SOURCE;
}
