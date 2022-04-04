/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "ConsoleErrorListener.h"
#include "RecognitionException.h"
#include "support/CPPUtils.h"
#include "Token.h"
#include "atn/ATN.h"
#include "atn/ATNSimulator.h"
#include "support/CPPUtils.h"
#include "support/StringUtils.h"

#include "Vocabulary.h"

#include "Recognizer.h"

using namespace antlr4;
using namespace antlr4::atn;

Recognizer::Recognizer() {
  _proxListener.addErrorListener(&ConsoleErrorListener::INSTANCE);
}

const std::unordered_map<std::string_view, size_t>& Recognizer::getTokenTypeMap() const {
  std::call_once(_tokenTypeMapOnce, [this]() mutable {
    const auto& vocabulary = getVocabulary();
    for (size_t i = 0; i <= getATN().maxTokenType; ++i) {
      std::string_view literalName = vocabulary.getLiteralName(i);
      if (!literalName.empty()) {
        _tokenTypeMap[literalName] = i;
      }

      std::string_view symbolicName = vocabulary.getSymbolicName(i);
      if (!symbolicName.empty()) {
        _tokenTypeMap[symbolicName] = i;
      }
    }
  });
  return _tokenTypeMap;
}

const std::unordered_map<std::string_view, size_t>& Recognizer::getRuleIndexMap() const {
  std::call_once(_ruleIndexMapOnce, [this]() mutable {
    auto ruleNames = getRuleNames();
    for (size_t i = 0; i < ruleNames.size(); ++i) {
      _ruleIndexMap[ruleNames[i]] = i;
    }
  });
  return _ruleIndexMap;
}

size_t Recognizer::getTokenType(std::string_view tokenName) const {
  const auto &map = getTokenTypeMap();
  auto iterator = map.find(tokenName);
  if (iterator == map.end())
    return Token::INVALID_TYPE;

  return iterator->second;
}

size_t Recognizer::getRuleIndex(std::string_view ruleName) const {
  const auto &map = getRuleIndexMap();
  auto iterator = map.find(ruleName);
  if (iterator == map.end())
    return INVALID_INDEX;

  return iterator->second;
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
  size_t line = e->getOffendingToken()->getLine();
  size_t charPositionInLine = e->getOffendingToken()->getCharPositionInLine();
  return std::string("line ") + std::to_string(line) + ":" + std::to_string(charPositionInLine);

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

  std::string result;
  result.reserve(s.size() + 2);
  result.push_back('\'');
  antlrcpp::escapeWhitespace(result, s);
  result.push_back('\'');
  result.shrink_to_fit();
  return result;
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

bool Recognizer::sempred(RuleContext * /*localctx*/, size_t /*ruleIndex*/, size_t /*actionIndex*/) {
  return true;
}

bool Recognizer::precpred(RuleContext * /*localctx*/, int /*precedence*/) {
  return true;
}

void Recognizer::action(RuleContext * /*localctx*/, size_t /*ruleIndex*/, size_t /*actionIndex*/) {
}
