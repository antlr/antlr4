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

#include "ConsoleErrorListener.h"
#include "RecognitionException.h"
#include "support/CPPUtils.h"
#include "support/StringUtils.h"
#include "Token.h"
#include "atn/ATN.h"
#include "atn/ATNSimulator.h"
#include "support/CPPUtils.h"

#include "Vocabulary.h"

#include "Recognizer.h"

using namespace antlr4;

std::map<const dfa::Vocabulary*, std::map<std::string, size_t>> Recognizer::_tokenTypeMapCache;
std::map<std::vector<std::string>, std::map<std::string, size_t>> Recognizer::_ruleIndexMapCache;

Recognizer::Recognizer() {
  InitializeInstanceFields();
  _proxListener.addErrorListener(&ConsoleErrorListener::INSTANCE);
}

dfa::Vocabulary const& Recognizer::getVocabulary() const {
  static dfa::Vocabulary vocabulary = dfa::Vocabulary::fromTokenNames(getTokenNames());
  return vocabulary;
}

std::map<std::string, size_t> Recognizer::getTokenTypeMap() {
  const dfa::Vocabulary& vocabulary = getVocabulary();

  std::lock_guard<std::recursive_mutex> lck(mtx);
  std::map<std::string, size_t> result;
  auto iterator = _tokenTypeMapCache.find(&vocabulary);
  if (iterator != _tokenTypeMapCache.end()) {
    result = iterator->second;
  } else {
    for (size_t i = 0; i < getATN().maxTokenType; ++i) {
      std::string literalName = vocabulary.getLiteralName(i);
      if (!literalName.empty()) {
        result[literalName] = i;
      }

      std::string symbolicName = vocabulary.getSymbolicName(i);
      if (!symbolicName.empty()) {
        result[symbolicName] = i;
      }
				}
    result["EOF"] = EOF;
    _tokenTypeMapCache[&vocabulary] = result;
  }

  return result;
}

std::map<std::string, size_t> Recognizer::getRuleIndexMap() {
  const std::vector<std::string>& ruleNames = getRuleNames();
  if (ruleNames.empty()) {
    throw "The current recognizer does not provide a list of rule names.";
  }

  std::lock_guard<std::recursive_mutex> lck(mtx);
  std::map<std::string, size_t> result;
  auto iterator = _ruleIndexMapCache.find(ruleNames);
  if (iterator != _ruleIndexMapCache.end()) {
    result = iterator->second;
  } else {
    result = antlrcpp::toMap(ruleNames);
    _ruleIndexMapCache[ruleNames] = result;
  }
  return result;
}

size_t Recognizer::getTokenType(const std::string &tokenName) {
  const std::map<std::string, size_t> &map = getTokenTypeMap();
  auto iterator = map.find(tokenName);
  if (iterator == map.end())
    return Token::INVALID_TYPE;

  return iterator->second;
}

Ref<atn::ParseInfo> Recognizer::getParseInfo() const {
  return nullptr;
}

void Recognizer::setInterpreter(atn::ATNSimulator *interpreter) {
  // Usually the interpreter is set by the descendant (lexer or parser (simulator), but can also be exchanged
  // by the profiling ATN simulator.
  delete _interpreter;
  _interpreter = interpreter;
}

std::string Recognizer::getErrorHeader(RecognitionException *e) {
  // We're having issues with cross header dependencies, these two classes will need to be
  // rewritten to remove that.
  int line = e->getOffendingToken()->getLine();
  int charPositionInLine = e->getOffendingToken()->getCharPositionInLine();
  return std::string("line ") + std::to_string(line) + std::string(":") + std::to_string(charPositionInLine);

}

std::string Recognizer::getTokenErrorDisplay(Token *t) {
  if (t == nullptr) {
    return "<no Token>";
  }
  std::string s = t->getText();
  if (s == "") {
    if (t->getType() == EOF) {
      s = "<EOF>";
    } else {
      s = std::string("<") + std::to_string(t->getType()) + std::string(">");
    }
  }

  antlrcpp::replaceAll(s, "\n", "\\n");
  antlrcpp::replaceAll(s, "\r","\\r");
  antlrcpp::replaceAll(s, "\t", "\\t");

  return "'" + s + "'";
}

void Recognizer::addErrorListener(ANTLRErrorListener *listener) {
  _proxListener.addErrorListener(listener);
}

void Recognizer::removeErrorListener(ANTLRErrorListener *listener) {
  _proxListener.removeErrorListener(listener);
}

void Recognizer::removeErrorListeners() {
  _proxListener.removeErrorListeners();
}

ProxyErrorListener& Recognizer::getErrorListenerDispatch() {
  return _proxListener;
}

bool Recognizer::sempred(Ref<RuleContext> const& /*localctx*/, int /*ruleIndex*/, int /*actionIndex*/) {
  return true;
}

bool Recognizer::precpred(Ref<RuleContext> const& /*localctx*/, int /*precedence*/) {
  return true;
}

void Recognizer::action(Ref<RuleContext> const& /*localctx*/, int /*ruleIndex*/, int /*actionIndex*/) {
}

int Recognizer::getState() {
  return _stateNumber;
}

void Recognizer::setState(int atnState) {
  _stateNumber = atnState;
  //		if ( traceATNStates ) _ctx.trace(atnState);
}

void Recognizer::InitializeInstanceFields() {
  _stateNumber = -1;
  _interpreter = nullptr;
}

