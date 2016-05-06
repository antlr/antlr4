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

  _token.reset();
  _type = Token::INVALID_TYPE;
  _channel = Token::DEFAULT_CHANNEL;
  _tokenStartCharIndex = -1;
  _tokenStartCharPositionInLine = -1;
  _tokenStartLine = -1;
  _text = L"";

  _hitEOF = false;
  _mode = Lexer::DEFAULT_MODE;
  _modeStack.clear();

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
    if (_hitEOF) {
      emitEOF();
      return _token;
    }

    _token.reset();
    _channel = Token::DEFAULT_CHANNEL;
    _tokenStartCharIndex = (int)_input->index();
    _tokenStartCharPositionInLine = getInterpreter<atn::LexerATNSimulator>()->getCharPositionInLine();
    _tokenStartLine = (int)getInterpreter<atn::LexerATNSimulator>()->getLine();
    _text = L"";
    do {
      _type = Token::INVALID_TYPE;
      int ttype;
      try {
        ttype = getInterpreter<atn::LexerATNSimulator>()->match(_input, (size_t)_mode);
      } catch (LexerNoViableAltException &e) {
        notifyListeners(e); // report error
        recover(e);
        ttype = SKIP;
      }
      if (_input->LA(1) == EOF) {
        _hitEOF = true;
      }
      if (_type == Token::INVALID_TYPE) {
        _type = ttype;
      }
      if (_type == SKIP) {
        goto outerContinue;
      }
    } while (_type == MORE);
    if (_token == nullptr) {
      emit();
    }
    return _token;
  }
}

void Lexer::skip() {
  _type = SKIP;
}

void Lexer::more() {
  _type = MORE;
}

void Lexer::mode(int m) {
  _mode = m;
}

void Lexer::pushMode(int m) {
  if (atn::LexerATNSimulator::debug) {
    std::wcout << std::wstring(L"pushMode ") << m << std::endl;
  }
  _modeStack.push_back(_mode);
  mode(m);
}

int Lexer::popMode() {
  if (_modeStack.empty()) {
    throw EmptyStackException();
  }
  if (atn::LexerATNSimulator::debug) {
    std::wcout << std::wstring(L"popMode back to ") << _modeStack.back() << std::endl;
  }
  mode(_modeStack.back());
  _modeStack.pop_back();
  return _mode;
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
  _token = token;
}

Ref<Token> Lexer::emit() {
  Ref<Token> t = std::dynamic_pointer_cast<Token>(_factory->create({ this, _input }, _type, _text, _channel,
    _tokenStartCharIndex, getCharIndex() - 1, _tokenStartLine, _tokenStartCharPositionInLine));
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
  if (_text != L"") {
    return _text;
  }
  return getInterpreter<atn::LexerATNSimulator>()->getText(_input);
}

void Lexer::setText(const std::wstring &text) {
  this->_text = text;
}

Ref<Token> Lexer::getToken() {
  return _token;
}

void Lexer::setToken(Ref<Token> token) {
  _token = token;
}

void Lexer::setType(int ttype) {
  _type = ttype;
}

int Lexer::getType() {
  return _type;
}

void Lexer::setChannel(int channel) {
  _channel = channel;
}

int Lexer::getChannel() {
  return _channel;
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
  std::wstring text = _input->getText(misc::Interval(_tokenStartCharIndex, (int)_input->index()));
  std::wstring msg = std::wstring(L"token recognition error at: '") + getErrorDisplay(text) + std::wstring(L"'");

  ProxyErrorListener &listener = getErrorListenerDispatch();
  listener.syntaxError(this, nullptr, (size_t)_tokenStartLine, _tokenStartCharPositionInLine, msg,
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

void Lexer::recover(RecognitionException */*re*/) {
  // TO_DO: Do we lose character or line position information?
  _input->consume();
}

void Lexer::InitializeInstanceFields() {
  _token = nullptr;
  _factory = CommonTokenFactory::DEFAULT;
  _tokenStartCharIndex = -1;
  _tokenStartLine = 0;
  _tokenStartCharPositionInLine = 0;
  _hitEOF = false;
  _channel = 0;
  _type = 0;
  _mode = Lexer::DEFAULT_MODE;
}
