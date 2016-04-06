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

#include "ATNDeserializationOptions.h"
#include "ParseTreePatternMatcher.h"
#include "DFA.h"
#include "ParserRuleContext.h"
#include "TerminalNode.h"
#include "Lexer.h"
#include "ParserATNSimulator.h"
#include "IntervalSet.h"
#include "RuleStartState.h"
#include "DefaultErrorStrategy.h"
#include "ATNDeserializer.h"
#include "RuleTransition.h"
#include "ATN.h"
#include "Strings.h"
#include "Exceptions.h"
#include "ANTLRErrorListener.h"

#include "Parser.h"

using namespace org::antlr::v4::runtime;

std::map<std::wstring, atn::ATN> Parser::bypassAltsAtnCache;

Parser::TraceListener::TraceListener(Parser *outerInstance) : outerInstance(outerInstance) {
}

void Parser::TraceListener::enterEveryRule(ParserRuleContext *ctx) {
  std::cout << "enter   "
  << antlrcpp::ws2s(outerInstance->getRuleNames()[(size_t)ctx->getRuleIndex()])
  << ", LT(1)=" << antlrcpp::ws2s(outerInstance->_input->LT(1)->getText())
  << std::endl;
}

void Parser::TraceListener::visitTerminal(tree::TerminalNode *node) {
  std::cout << "consume "
  << node->getSymbol() << " rule "
  << antlrcpp::ws2s(outerInstance->getRuleNames()[(size_t)outerInstance->ctx->getRuleIndex()])
  << std::endl;
}

void Parser::TraceListener::visitErrorNode(tree::ErrorNode *node) {
}

void Parser::TraceListener::exitEveryRule(ParserRuleContext *ctx) {
  std::cout << "exit    "
  << antlrcpp::ws2s(outerInstance->getRuleNames()[(size_t)ctx->getRuleIndex()])
  << ", LT(1)=" << antlrcpp::ws2s(outerInstance->_input->LT(1)->getText())
  << std::endl;
}

Parser::TrimToSizeListener *const Parser::TrimToSizeListener::INSTANCE = new Parser::TrimToSizeListener();

void Parser::TrimToSizeListener::enterEveryRule(ParserRuleContext *ctx) {
}

void Parser::TrimToSizeListener::visitTerminal(tree::TerminalNode *node) {
}

void Parser::TrimToSizeListener::visitErrorNode(tree::ErrorNode *node) {
}

void Parser::TrimToSizeListener::exitEveryRule(ParserRuleContext *ctx) {
  ctx->children.shrink_to_fit();
}

Parser::Parser(TokenStream* input) : _tracer(this) {
  InitializeInstanceFields();
  setInputStream(input);
}

Parser::~Parser() {
}

void Parser::reset() {
  if (getInputStream() != nullptr) {
    getInputStream()->seek(0);
  }
  _errHandler->reset(this);

  delete ctx;
  _syntaxErrors = 0;
  setTrace(false);
  _precedenceStack.clear();
  _precedenceStack.push_back(0);
  atn::ATNSimulator *interpreter = getInterpreter<atn::ParserATNSimulator>();
  if (interpreter != nullptr) {
    interpreter->reset();
  }
}

Token *Parser::match(int ttype) {
  Token *t = getCurrentToken();
  if (t->getType() == ttype) {
    _errHandler->reportMatch(this);
    consume();
  } else {
    t = _errHandler->recoverInline(this);
    if (_buildParseTrees && t->getTokenIndex() == -1) {
      // we must have conjured up a new token during single token insertion
      // if it's not the current symbol
      ctx->addErrorNode(t);
    }
  }
  return t;
}

Token *Parser::matchWildcard() {
  Token *t = getCurrentToken();
  if (t->getType() > 0) {
    _errHandler->reportMatch(this);
    consume();
  } else {
    t = _errHandler->recoverInline(this);
    if (_buildParseTrees && t->getTokenIndex() == -1) {
      // we must have conjured up a new token during single token insertion
      // if it's not the current symbol
      ctx->addErrorNode(t);
    }
  }

  return t;
}

void Parser::setBuildParseTree(bool buildParseTrees) {
  this->_buildParseTrees = buildParseTrees;
}

bool Parser::getBuildParseTree() {
  return _buildParseTrees;
}

void Parser::setTrimParseTree(bool trimParseTrees) {
  if (trimParseTrees) {
    if (getTrimParseTree()) {
      return;
    }
    addParseListener(TrimToSizeListener::INSTANCE);
  } else {
    removeParseListener(TrimToSizeListener::INSTANCE);
  }
}

bool Parser::getTrimParseTree() {
  return std::find(getParseListeners().begin(), getParseListeners().end(), TrimToSizeListener::INSTANCE) != getParseListeners().end();
}

std::vector<tree::ParseTreeListener*> Parser::getParseListeners() {
  std::vector<tree::ParseTreeListener*> listeners = _parseListeners;
  if (listeners.empty()) {
    std::vector<tree::ParseTreeListener*> emptyList;
    return emptyList;
  }

  return listeners;
}

void Parser::addParseListener(tree::ParseTreeListener *listener) {
  if (listener == nullptr) {
    throw NullPointerException("listener");
  }

  if (_parseListeners.empty()) {
    _parseListeners = std::vector<tree::ParseTreeListener*>();
  }

  this->_parseListeners.push_back(listener);
}

void Parser::removeParseListener(tree::ParseTreeListener *listener) {
  if (!_parseListeners.empty()) {
    auto it = std::find(_parseListeners.begin(), _parseListeners.end(), listener);
    if (it != _parseListeners.end()) {
      _parseListeners.erase(it);
    }
  }
}

void Parser::removeParseListeners() {
  _parseListeners.clear();
}

void Parser::triggerEnterRuleEvent() {
  for (auto listener : _parseListeners) {
    listener->enterEveryRule(ctx);
    ctx->enterRule(listener);
  }
}

void Parser::triggerExitRuleEvent() {
  // reverse order walk of listeners
  for (auto it = _parseListeners.rbegin(); it != _parseListeners.rend(); ++it) {
    tree::ParseTreeListener *listener = *it;
    ctx->exitRule(*it);
    listener->exitEveryRule(ctx);
  }
}

int Parser::getNumberOfSyntaxErrors() {
  return _syntaxErrors;
}

TokenFactory<CommonToken*> *Parser::getTokenFactory() {
  return _input->getTokenSource()->getTokenFactory();
}


const atn::ATN& Parser::getATNWithBypassAlts() {
  std::wstring serializedAtn = getSerializedATN();
  if (serializedAtn.empty()) {
    throw UnsupportedOperationException("The current parser does not support an ATN with bypass alternatives.");
  }

  std::lock_guard<std::mutex> lck(mtx);

  // XXX: using the entire serialized ATN as key into the map is a big resource waste.
  //      How large can that thing become?
  if (bypassAltsAtnCache.find(serializedAtn) == bypassAltsAtnCache.end())
  {
    atn::ATNDeserializationOptions deserializationOptions;
    deserializationOptions.setGenerateRuleBypassTransitions(true);

    atn::ATNDeserializer deserializer(deserializationOptions);
    bypassAltsAtnCache[serializedAtn] = deserializer.deserialize(serializedAtn);
  }

  return bypassAltsAtnCache[serializedAtn];
}

tree::pattern::ParseTreePattern *Parser::compileParseTreePattern(const std::wstring &pattern, int patternRuleIndex) {
  if (getTokenStream() != nullptr) {
    TokenSource *tokenSource = getTokenStream()->getTokenSource();
    if (dynamic_cast<Lexer*>(tokenSource) != nullptr) {
      Lexer *lexer = static_cast<Lexer*>(tokenSource);
      return compileParseTreePattern(pattern, patternRuleIndex, lexer);
    }
  }
  throw UnsupportedOperationException("Parser can't discover a lexer to use");
}

tree::pattern::ParseTreePattern *Parser::compileParseTreePattern(const std::wstring &pattern, int patternRuleIndex, Lexer *lexer) {
  tree::pattern::ParseTreePatternMatcher *m = new tree::pattern::ParseTreePatternMatcher(lexer, this);
  return m->compile(pattern, patternRuleIndex);
}

std::shared_ptr<ANTLRErrorStrategy> Parser::getErrorHandler() {
  return _errHandler;
}

void Parser::setErrorHandler(std::shared_ptr<ANTLRErrorStrategy> handler) {
  _errHandler = handler;
}

TokenStream *Parser::getInputStream() {
  return getTokenStream();
}

void Parser::setInputStream(IntStream *input) {
  setTokenStream(static_cast<TokenStream*>(input));
}

TokenStream *Parser::getTokenStream() {
  return _input;
}

void Parser::setTokenStream(TokenStream *input) {
  delete _input;
  _input = nullptr;
  reset();
  _input = input;
}

Token *Parser::getCurrentToken() {
  return _input->LT(1);
}

void Parser::notifyErrorListeners(const std::wstring &msg) {
  notifyErrorListeners(getCurrentToken(), msg, nullptr);
}

void Parser::notifyErrorListeners(Token *offendingToken, const std::wstring &msg, RecognitionException *e) {
  _syntaxErrors++;
  int line = -1;
  int charPositionInLine = -1;
  line = offendingToken->getLine();
  charPositionInLine = offendingToken->getCharPositionInLine();

  ANTLRErrorListener *listener = getErrorListenerDispatch();
  listener->syntaxError(this, offendingToken, (size_t)line, charPositionInLine, msg, e);
}

Token *Parser::consume() {
  Token *o = getCurrentToken();
  if (o->getType() != EOF) {
    getInputStream()->consume();
  }
  bool hasListener = _parseListeners.size() > 0 && !_parseListeners.empty();
  if (_buildParseTrees || hasListener) {
    if (_errHandler->inErrorRecoveryMode(this)) {
      tree::ErrorNode *node = ctx->addErrorNode(o);
      if (_parseListeners.size() > 0) {
        for (auto listener : _parseListeners) {
          listener->visitErrorNode(node);
        }
      }
    } else {
      tree::TerminalNode *node = ctx->addChild(o);
      if (_parseListeners.size() > 0) {
        for (auto listener : _parseListeners) {
          listener->visitTerminal(node);
        }
      }
    }
  }
  return o;
}

void Parser::addContextToParseTree() {
  ParserRuleContext *parent = static_cast<ParserRuleContext*>(ctx->parent);
  // add current context to parent if we have a parent
  if (parent != nullptr) {
    parent->addChild(ctx);
  }
}

void Parser::enterRule(ParserRuleContext *localctx, int state, int ruleIndex) {
  setState(state);
  ctx = localctx;
  ctx->start = _input->LT(1);
  if (_buildParseTrees) {
    addContextToParseTree();
  }
  if (_parseListeners.size() > 0) {
    triggerEnterRuleEvent();
  }
}

void Parser::exitRule() {
  ctx->stop = _input->LT(-1);
  // trigger event on ctx, before it reverts to parent
  if (_parseListeners.size() > 0) {
    triggerExitRuleEvent();
  }
  setState(ctx->invokingState);
  ctx = static_cast<ParserRuleContext*>(ctx->parent);
}

void Parser::enterOuterAlt(ParserRuleContext *localctx, int altNum) {
  // if we have new localctx, make sure we replace existing ctx
  // that is previous child of parse tree
  if (_buildParseTrees && ctx != localctx) {
    ParserRuleContext *parent = static_cast<ParserRuleContext*>(ctx->parent);
    if (parent != nullptr) {
      parent->removeLastChild();
      parent->addChild(localctx);
    }
  }
  ctx = localctx;
}

void Parser::enterRecursionRule(ParserRuleContext *localctx, int ruleIndex) {
  enterRecursionRule(localctx, getATN().ruleToStartState[(size_t)ruleIndex]->stateNumber, ruleIndex, 0);
}

void Parser::enterRecursionRule(ParserRuleContext *localctx, int state, int ruleIndex, int precedence) {
  setState(state);
  _precedenceStack.push_back(precedence);
  ctx = localctx;
  ctx->start = _input->LT(1);
  if (_parseListeners.size() > 0) {
    triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
  }
}

void Parser::pushNewRecursionContext(ParserRuleContext *localctx, int state, int ruleIndex) {
  ParserRuleContext *previous = ctx;
  previous->parent = localctx;
  previous->invokingState = state;
  previous->stop = _input->LT(-1);

  ctx = localctx;
  ctx->start = previous->start;
  if (_buildParseTrees) {
    ctx->addChild(previous);
  }

  if (_parseListeners.size() > 0) {
    triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
  }
}

void Parser::unrollRecursionContexts(ParserRuleContext *_parentctx) {
  _precedenceStack.pop_back();
  ctx->stop = _input->LT(-1);
  ParserRuleContext *retctx = ctx; // save current ctx (return value)

  // unroll so ctx is as it was before call to recursive method
  if (_parseListeners.size() > 0) {
    while (ctx != _parentctx) {
      triggerExitRuleEvent();
      ctx = static_cast<ParserRuleContext*>(ctx->parent);
    }
  } else {
    ctx = _parentctx;
  }

  // hook into tree
  retctx->parent = _parentctx;

  if (_buildParseTrees && _parentctx != nullptr) {
    // add return ctx into invoking rule's tree
    _parentctx->addChild(retctx);
  }
}

ParserRuleContext *Parser::getInvokingContext(int ruleIndex) {
  ParserRuleContext *p = ctx;
  while (p != nullptr) {
    if (p->getRuleIndex() == ruleIndex) {
      return p;
    }
    p = static_cast<ParserRuleContext*>(p->parent);
  }
  return nullptr;
}

ParserRuleContext *Parser::getContext() {
  return ctx;
}

void Parser::setContext(ParserRuleContext *ctx) {
  ctx = ctx;
}

bool Parser::precpred(RuleContext *localctx, int precedence) {
  return precedence >= _precedenceStack.back();
}

bool Parser::inContext(const std::wstring &context) {
  // TO_DO: useful in parser?
  return false;
}

bool Parser::isExpectedToken(int symbol) {
  const atn::ATN &atn = getInterpreter<atn::ParserATNSimulator>()->atn;
  ParserRuleContext *ctx = ctx;
  atn::ATNState *s = atn.states[(size_t)getState()];
  misc::IntervalSet following = atn.nextTokens(s);

  if (following.contains(symbol)) {
    return true;
  }

  if (!following.contains(Token::EPSILON)) {
    return false;
  }

  while (ctx != nullptr && ctx->invokingState >= 0 && following.contains(Token::EPSILON)) {
    atn::ATNState *invokingState = atn.states[(size_t)ctx->invokingState];
    atn::RuleTransition *rt = static_cast<atn::RuleTransition*>(invokingState->transition(0));
    following = atn.nextTokens(rt->followState);
    if (following.contains(symbol)) {
      return true;
    }

    ctx = static_cast<ParserRuleContext*>(ctx->parent);
  }

  if (following.contains(Token::EPSILON) && symbol == EOF) {
    return true;
  }

  return false;
}

misc::IntervalSet Parser::getExpectedTokens() {
  return getATN().getExpectedTokens(getState(), getContext());
}

misc::IntervalSet Parser::getExpectedTokensWithinCurrentRule() {
  const atn::ATN &atn = getInterpreter<atn::ParserATNSimulator>()->atn;
  atn::ATNState *s = atn.states[(size_t)getState()];
  return atn.nextTokens(s);
}

int Parser::getRuleIndex(const std::wstring &ruleName) {
  const std::map<std::wstring, int> &m = getRuleIndexMap();
  if (m.find(ruleName) == m.end()) {
    return -1;
  }
  return m.at(ruleName);
}

ParserRuleContext *Parser::getRuleContext() {
  return ctx;
}

std::vector<std::wstring> Parser::getRuleInvocationStack() {
  return getRuleInvocationStack(ctx);
}

std::vector<std::wstring> Parser::getRuleInvocationStack(RuleContext *p) {
  std::vector<std::wstring> ruleNames = getRuleNames();
  std::vector<std::wstring> stack = std::vector<std::wstring>();
  while (p != nullptr) {
    // compute what follows who invoked us
    ssize_t ruleIndex = p->getRuleIndex();
    if (ruleIndex < 0) {
      stack.push_back(L"n/a");
    } else {
      stack.push_back(ruleNames[(size_t)ruleIndex]);
    }
    p = p->parent;
  }
  return stack;
}

std::vector<std::wstring> Parser::getDFAStrings() {
  atn::ParserATNSimulator *simulator = getInterpreter<atn::ParserATNSimulator>();
  if (!simulator->_decisionToDFA.empty()) {
    std::lock_guard<std::mutex> lck(mtx);

    std::vector<std::wstring> s;
    for (size_t d = 0; d < simulator->_decisionToDFA.size(); d++) {
      dfa::DFA *dfa = simulator->_decisionToDFA[d];
      s.push_back(dfa->toString(getTokenNames()));
    }
    return s;
  }
  return std::vector<std::wstring>();
}

void Parser::dumpDFA() {
  atn::ParserATNSimulator *simulator = getInterpreter<atn::ParserATNSimulator>();
  if (!simulator->_decisionToDFA.empty()) {
    std::lock_guard<std::mutex> lck(mtx);
    bool seenOne = false;
    for (size_t d = 0; d < simulator->_decisionToDFA.size(); d++) {
      dfa::DFA *dfa = simulator->_decisionToDFA[d];
      if (!dfa->states->empty()) {
        if (seenOne) {
          std::cout << std::endl;
        }
        std::cout << L"Decision " << dfa->decision << L":" << std::endl;
								dfa->toString(getTokenNames());
        std::wcout << dfa->toString(getTokenNames());
        seenOne = true;
      }
    }
  }
}

std::string Parser::getSourceName() {
  return _input->getSourceName();
}

void Parser::setTrace(bool trace) {
  if (!trace) {
    removeParseListener(&_tracer);
  } else {
    removeParseListener(&_tracer); // Just in case this is triggered multiple times.
    addParseListener(&_tracer);
  }
}

void Parser::InitializeInstanceFields() {
  _errHandler.reset(new DefaultErrorStrategy());
  _precedenceStack.clear();
  _precedenceStack.push_back(0);
  _buildParseTrees = true;
  _syntaxErrors = 0;
}

