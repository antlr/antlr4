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

#include "atn/LexerATNSimulator.h"
#include "Exceptions.h"
#include "misc/Interval.h"
#include "CommonTokenFactory.h"
#include "LexerNoViableAltException.h"
#include "ANTLRErrorListener.h"
#include "support/CPPUtils.h"
#include "CommonToken.h"
#include "support/StringUtils.h"

#include "Lexer.h"

using namespace antlrcpp;
using namespace antlr4;

Lexer::Lexer() : Recognizer() {
  InitializeInstanceFields();
  _input = nullptr;
}

Lexer::Lexer(CharStream *input) : Recognizer(), _input(input) {
  InitializeInstanceFields();
}

void Lexer::reset() {
  // wack Lexer state variables
  _input->seek(0); // rewind the input

  token.reset();
  type = Token::INVALID_TYPE;
  channel = Token::DEFAULT_CHANNEL;
  tokenStartCharIndex = -1;
  tokenStartCharPositionInLine = 0;
  tokenStartLine = 0;
  _text = "";

  hitEOF = false;
  mode = Lexer::DEFAULT_MODE;
  modeStack.clear();

  getInterpreter<atn::LexerATNSimulator>()->reset();
}

std::unique_ptr<Token> Lexer::nextToken() {
  // Mark start location in char stream so unbuffered streams are
  // guaranteed at least have text of current token
  ssize_t tokenStartMarker = _input->mark();

  auto onExit = finally([this, tokenStartMarker]{
    // make sure we release marker after match or
    // unbuffered char stream will keep buffering
    _input->release(tokenStartMarker);
  });

  while (true) {
  outerContinue:
    if (hitEOF) {
      emitEOF();
      return std::move(token);
    }

    token.reset();
    channel = Token::DEFAULT_CHANNEL;
    tokenStartCharIndex = (int)_input->index();
    tokenStartCharPositionInLine = getInterpreter<atn::LexerATNSimulator>()->getCharPositionInLine();
    tokenStartLine = (int)getInterpreter<atn::LexerATNSimulator>()->getLine();
    _text = "";
    do {
      type = Token::INVALID_TYPE;
      int ttype;
      try {
        ttype = getInterpreter<atn::LexerATNSimulator>()->match(_input, mode);
      } catch (LexerNoViableAltException &e) {
        notifyListeners(e); // report error
        recover(e);
        ttype = SKIP;
      }
      if (_input->LA(1) == EOF) {
        hitEOF = true;
      }
      if (type == Token::INVALID_TYPE) {
        type = ttype;
      }
      if (type == SKIP) {
        goto outerContinue;
      }
    } while (type == MORE);
    if (token == nullptr) {
      emit();
    }
    return std::move(token);
  }
}

void Lexer::skip() {
  type = SKIP;
}

void Lexer::more() {
  type = MORE;
}

void Lexer::setMode(size_t m) {
  mode = m;
}

void Lexer::pushMode(size_t m) {
  if (atn::LexerATNSimulator::debug) {
    std::cout << "pushMode " << m << std::endl;
  }
  modeStack.push_back(mode);
  setMode(m);
}

size_t Lexer::popMode() {
  if (modeStack.empty()) {
    throw EmptyStackException();
  }
  if (atn::LexerATNSimulator::debug) {
    std::cout << std::string("popMode back to ") << modeStack.back() << std::endl;
  }
  setMode(modeStack.back());
  modeStack.pop_back();
  return mode;
}


Ref<TokenFactory<CommonToken>> Lexer::getTokenFactory() {
  return _factory;
}

void Lexer::setInputStream(IntStream *input) {
  reset();
  _input = dynamic_cast<CharStream*>(input);
}

std::string Lexer::getSourceName() {
  return _input->getSourceName();
}

CharStream* Lexer::getInputStream() {
  return _input;
}

void Lexer::emit(std::unique_ptr<Token> token) {
  this->token = std::move(token);
}

Token* Lexer::emit() {
  emit(_factory->create({ this, _input }, (int)type, _text, channel,
    tokenStartCharIndex, getCharIndex() - 1, (int)tokenStartLine, tokenStartCharPositionInLine));
  return token.get();
}

Token* Lexer::emitEOF() {
  int cpos = getCharPositionInLine();
  size_t line = getLine();
  emit(_factory->create({ this, _input }, EOF, "", Token::DEFAULT_CHANNEL, (int)_input->index(),
    (int)_input->index() - 1, (int)line, cpos));
  return token.get();
}

size_t Lexer::getLine() const {
  return getInterpreter<atn::LexerATNSimulator>()->getLine();
}

int Lexer::getCharPositionInLine() {
  return getInterpreter<atn::LexerATNSimulator>()->getCharPositionInLine();
}

void Lexer::setLine(size_t line) {
  getInterpreter<atn::LexerATNSimulator>()->setLine(line);
}

void Lexer::setCharPositionInLine(int charPositionInLine) {
  getInterpreter<atn::LexerATNSimulator>()->setCharPositionInLine(charPositionInLine);
}

int Lexer::getCharIndex() {
  return (int)_input->index();
}

std::string Lexer::getText() {
  if (!_text.empty()) {
    return _text;
  }
  return getInterpreter<atn::LexerATNSimulator>()->getText(_input);
}

void Lexer::setText(const std::string &text) {
  _text = text;
}

std::unique_ptr<Token> Lexer::getToken() {
  return std::move(token);
}

void Lexer::setToken(std::unique_ptr<Token> token) {
  this->token = std::move(token);
}

void Lexer::setType(ssize_t ttype) {
  type = ttype;
}

ssize_t Lexer::getType() {
  return type;
}

void Lexer::setChannel(int channel) {
  this->channel = channel;
}

int Lexer::getChannel() {
  return channel;
}

std::vector<std::unique_ptr<Token>> Lexer::getAllTokens() {
  std::vector<std::unique_ptr<Token>> tokens;
  std::unique_ptr<Token> t = nextToken();
  while (t->getType() != EOF) {
    tokens.push_back(std::move(t));
    t = nextToken();
  }
  return tokens;
}

void Lexer::recover(const LexerNoViableAltException &/*e*/) {
  if (_input->LA(1) != EOF) {
    // skip a char and try again
    getInterpreter<atn::LexerATNSimulator>()->consume(_input);
  }
}

void Lexer::notifyListeners(const LexerNoViableAltException & /*e*/) {
  std::string text = _input->getText(misc::Interval(tokenStartCharIndex, (int)_input->index()));
  std::string msg = std::string("token recognition error at: '") + getErrorDisplay(text) + std::string("'");

  ProxyErrorListener &listener = getErrorListenerDispatch();
  listener.syntaxError(this, nullptr, tokenStartLine, tokenStartCharPositionInLine, msg, std::current_exception());
}

std::string Lexer::getErrorDisplay(const std::string &s) {
  std::stringstream ss;
  for (auto c : s) {
    switch (c) {
    case '\n':
      ss << "\\n";
      break;
    case '\t':
      ss << "\\t";
      break;
    case '\r':
      ss << "\\r";
      break;
    default:
      ss << c;
      break;
    }
  }
  return ss.str();
}

void Lexer::recover(RecognitionException * /*re*/) {
  // TO_DO: Do we lose character or line position information?
  _input->consume();
}

void Lexer::InitializeInstanceFields() {
  token = nullptr;
  _factory = CommonTokenFactory::DEFAULT;
  tokenStartCharIndex = -1;
  tokenStartLine = 0;
  tokenStartCharPositionInLine = 0;
  hitEOF = false;
  channel = 0;
  type = 0;
  mode = Lexer::DEFAULT_MODE;
}
