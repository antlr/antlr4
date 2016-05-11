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

#include "LexerATNSimulator.h"
#include "Exceptions.h"
#include "Interval.h"
#include "CommonTokenFactory.h"
#include "LexerNoViableAltException.h"
#include "ANTLRErrorListener.h"
#include "CPPUtils.h"
#include "CommonToken.h"

#include "Lexer.h"

using namespace antlrcpp;
using namespace org::antlr::v4::runtime;

Lexer::Lexer(CharStream *input) : _input(input) {
  InitializeInstanceFields();
}

void Lexer::reset() {
  // wack Lexer state variables
  _input->seek(0); // rewind the input

  token.reset();
  type = Token::INVALID_TYPE;
  channel = Token::DEFAULT_CHANNEL;
  tokenStartCharIndex = -1;
  tokenStartCharPositionInLine = -1;
  tokenStartLine = -1;
  text = L"";

  hitEOF = false;
  mode = Lexer::DEFAULT_MODE;
  modeStack.clear();

  getInterpreter<atn::LexerATNSimulator>()->reset();
}

Ref<Token> Lexer::nextToken() {
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
      return token;
    }

    token.reset();
    channel = Token::DEFAULT_CHANNEL;
    tokenStartCharIndex = (int)_input->index();
    tokenStartCharPositionInLine = getInterpreter<atn::LexerATNSimulator>()->getCharPositionInLine();
    tokenStartLine = (int)getInterpreter<atn::LexerATNSimulator>()->getLine();
    text = L"";
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
    return token;
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
    std::wcout << std::wstring(L"pushMode ") << m << std::endl;
  }
  modeStack.push_back(mode);
  setMode(m);
}

size_t Lexer::popMode() {
  if (modeStack.empty()) {
    throw EmptyStackException();
  }
  if (atn::LexerATNSimulator::debug) {
    std::wcout << std::wstring(L"popMode back to ") << modeStack.back() << std::endl;
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

void Lexer::emit(Ref<Token> token) {
  this->token = token;
}

Ref<Token> Lexer::emit() {
  Ref<Token> t = std::dynamic_pointer_cast<Token>(_factory->create({ this, _input }, (int)type, text, channel,
    tokenStartCharIndex, getCharIndex() - 1, (int)tokenStartLine, tokenStartCharPositionInLine));
  emit(t);
  return t;
}

Ref<Token> Lexer::emitEOF() {
  int cpos = getCharPositionInLine();
  size_t line = getLine();
  Ref<Token> eof = std::dynamic_pointer_cast<Token>(_factory->create({ this, _input }, EOF, L"", Token::DEFAULT_CHANNEL,
    (int)_input->index(), (int)_input->index() - 1, (int)line, cpos));
  emit(eof);
  return eof;
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

std::wstring Lexer::getText() {
  if (!text.empty()) {
    return text;
  }
  return getInterpreter<atn::LexerATNSimulator>()->getText(_input);
}

void Lexer::setText(const std::wstring &text) {
  this->text = text;
}

Ref<Token> Lexer::getToken() {
  return token;
}

void Lexer::setToken(Ref<Token> token) {
  this->token = token;
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

std::vector<Ref<Token>> Lexer::getAllTokens() {
  std::vector<Ref<Token>> tokens;
  Ref<Token> t = nextToken();
  while (t->getType() != EOF) {
    tokens.push_back(t);
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

void Lexer::notifyListeners(const LexerNoViableAltException &e) {
  std::wstring text = _input->getText(misc::Interval(tokenStartCharIndex, (int)_input->index()));
  std::wstring msg = std::wstring(L"token recognition error at: '") + getErrorDisplay(text) + std::wstring(L"'");

  ProxyErrorListener &listener = getErrorListenerDispatch();
  listener.syntaxError(this, nullptr, tokenStartLine, tokenStartCharPositionInLine, msg,
                       std::make_exception_ptr(e));
}

std::wstring Lexer::getErrorDisplay(const std::wstring &s) {
  std::wstringstream ss;
  for (auto c : s) {
    ss << getErrorDisplay(c);
  }
  return ss.str();
}

std::wstring Lexer::getErrorDisplay(int c) {
  std::wstring s;
  switch (c) {
    case EOF :
      s = L"<EOF>";
      break;
    case L'\n' :
      s = L"\\n";
      break;
    case L'\t' :
      s = L"\\t";
      break;
    case L'\r' :
      s = L"\\r";
      break;
    default:
      s = c;
      break;
  }
  return s;
}

std::wstring Lexer::getCharErrorDisplay(int c) {
  std::wstring s = getErrorDisplay(c);
  return std::wstring(L"'") + s + std::wstring(L"'");
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
