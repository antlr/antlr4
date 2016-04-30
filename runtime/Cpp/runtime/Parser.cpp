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
#include "ParseTreePattern.h"

#include "ProfilingATNSimulator.h"
#include "ParseInfo.h"

#include "Parser.h"

using namespace org::antlr::v4::runtime;
using namespace antlrcpp;

std::map<std::wstring, atn::ATN> Parser::bypassAltsAtnCache;

Parser::TraceListener::TraceListener(Parser *outerInstance) : outerInstance(outerInstance) {
}

void Parser::TraceListener::enterEveryRule(Ref<ParserRuleContext> ctx) {
  std::cout << "enter   "
  << antlrcpp::ws2s(outerInstance->getRuleNames()[(size_t)ctx->getRuleIndex()])
  << ", LT(1)=" << antlrcpp::ws2s(outerInstance->_input->LT(1)->getText())
  << std::endl;
}

void Parser::TraceListener::visitTerminal(Ref<tree::TerminalNode> node) {
  std::cout << "consume "
  << node->getSymbol() << " rule "
  << antlrcpp::ws2s(outerInstance->getRuleNames()[(size_t)outerInstance->getContext()->getRuleIndex()])
  << std::endl;
}

void Parser::TraceListener::visitErrorNode(Ref<tree::ErrorNode> node) {
}

void Parser::TraceListener::exitEveryRule(Ref<ParserRuleContext> ctx) {
  std::cout << "exit    "
  << antlrcpp::ws2s(outerInstance->getRuleNames()[(size_t)ctx->getRuleIndex()])
  << ", LT(1)=" << antlrcpp::ws2s(outerInstance->_input->LT(1)->getText())
  << std::endl;
}

const Ref<Parser::TrimToSizeListener> Parser::TrimToSizeListener::INSTANCE =
  std::make_shared<Parser::TrimToSizeListener>();

void Parser::TrimToSizeListener::enterEveryRule(Ref<ParserRuleContext> ctx) {
}

void Parser::TrimToSizeListener::visitTerminal(Ref<tree::TerminalNode> node) {
}

void Parser::TrimToSizeListener::visitErrorNode(Ref<tree::ErrorNode> node) {
}

void Parser::TrimToSizeListener::exitEveryRule(Ref<ParserRuleContext> ctx) {
  ctx->children.shrink_to_fit();
}

Parser::Parser(TokenStream *input) {
  InitializeInstanceFields();
  setInputStream(input);
}

Parser::~Parser() {
}

void Parser::reset() {
  if (getInputStream() != nullptr) {
    getInputStream()->seek(0);
  }
  _errHandler->reset(this); // Watch out, this is not shared_ptr.reset().

  _syntaxErrors = 0;
  setTrace(false);
  _precedenceStack.clear();
  _precedenceStack.push_back(0);
  atn::ATNSimulator *interpreter = getInterpreter<atn::ParserATNSimulator>();
  if (interpreter != nullptr) {
    interpreter->reset();
  }
}

Ref<Token> Parser::match(int ttype) {
  Ref<Token> t = getCurrentToken();
  if (t->getType() == ttype) {
    if (ttype == EOF) {
      _matchedEOF = true;
    }
    _errHandler->reportMatch(this);
    consume();
  } else {
    t = _errHandler->recoverInline(this);
    if (_buildParseTrees && t->getTokenIndex() == -1) {
      // we must have conjured up a new token during single token insertion
      // if it's not the current symbol
      _ctx->addErrorNode(t);
    }
  }
  return t;
}

Ref<Token> Parser::matchWildcard() {
  Ref<Token> t = getCurrentToken();
  if (t->getType() > 0) {
    _errHandler->reportMatch(this);
    consume();
  } else {
    t = _errHandler->recoverInline(this);
    if (_buildParseTrees && t->getTokenIndex() == -1) {
      // we must have conjured up a new token during single token insertion
      // if it's not the current symbol
      _ctx->addErrorNode(t);
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

std::vector<Ref<tree::ParseTreeListener>> Parser::getParseListeners() {
  return _parseListeners;
}

void Parser::addParseListener(Ref<tree::ParseTreeListener> listener) {
  if (!listener) {
    throw NullPointerException("listener");
  }

  this->_parseListeners.push_back(listener);
}

void Parser::removeParseListener(Ref<tree::ParseTreeListener> listener) {
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
    listener->enterEveryRule(_ctx);
    _ctx->enterRule(listener);
  }
}

void Parser::triggerExitRuleEvent() {
  // reverse order walk of listeners
  for (auto it = _parseListeners.rbegin(); it != _parseListeners.rend(); ++it) {
    _ctx->exitRule(*it);
    (*it)->exitEveryRule(_ctx);
  }
}

int Parser::getNumberOfSyntaxErrors() {
  return _syntaxErrors;
}

Ref<TokenFactory<CommonToken>> Parser::getTokenFactory() {
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

tree::pattern::ParseTreePattern Parser::compileParseTreePattern(const std::wstring &pattern, int patternRuleIndex) {
  if (getTokenStream() != nullptr) {
    TokenSource *tokenSource = getTokenStream()->getTokenSource();
    if (is<Lexer*>(tokenSource)) {
      Lexer *lexer = dynamic_cast<Lexer *>(tokenSource);
      return compileParseTreePattern(pattern, patternRuleIndex, lexer);
    }
  }
  throw UnsupportedOperationException("Parser can't discover a lexer to use");
}

tree::pattern::ParseTreePattern Parser::compileParseTreePattern(const std::wstring &pattern, int patternRuleIndex,
  Lexer *lexer) {
  tree::pattern::ParseTreePatternMatcher m(lexer, this);
  return m.compile(pattern, patternRuleIndex);
}

Ref<ANTLRErrorStrategy> Parser::getErrorHandler() {
  return _errHandler;
}

void Parser::setErrorHandler(Ref<ANTLRErrorStrategy> handler) {
  _errHandler = handler;
}

IntStream* Parser::getInputStream() {
  return getTokenStream();
}

void Parser::setInputStream(IntStream *input) {
  setTokenStream(static_cast<TokenStream*>(input));
}

TokenStream* Parser::getTokenStream() {
  return _input;
}

void Parser::setTokenStream(TokenStream *input) {
  _input = nullptr; // Just a reference we don't own.
  reset();
  _input = input;
}

Ref<Token> Parser::getCurrentToken() {
  return _input->LT(1);
}

void Parser::notifyErrorListeners(const std::wstring &msg) {
  notifyErrorListeners(getCurrentToken(), msg, nullptr);
}

void Parser::notifyErrorListeners(Ref<Token> offendingToken, const std::wstring &msg, std::exception_ptr e) {
  _syntaxErrors++;
  int line = -1;
  int charPositionInLine = -1;
  line = offendingToken->getLine();
  charPositionInLine = offendingToken->getCharPositionInLine();

  ProxyErrorListener &listener = getErrorListenerDispatch();
  listener.syntaxError(this, offendingToken, (size_t)line, charPositionInLine, msg, e);
}

Ref<Token> Parser::consume() {
  Ref<Token> o = getCurrentToken();
  if (o->getType() != EOF) {
    getInputStream()->consume();
  }
  bool hasListener = _parseListeners.size() > 0 && !_parseListeners.empty();
  if (_buildParseTrees || hasListener) {
    if (_errHandler->inErrorRecoveryMode(this)) {
      Ref<tree::ErrorNode> node = _ctx->addErrorNode(o);
      if (_parseListeners.size() > 0) {
        for (auto listener : _parseListeners) {
          listener->visitErrorNode(node);
        }
      }
    } else {
      Ref<tree::TerminalNode> node = _ctx->addChild(o);
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
  // Add current context to parent if we have a parent.
  if (_ctx->parent.expired())
    return;

  Ref<ParserRuleContext> parent = std::dynamic_pointer_cast<ParserRuleContext>(_ctx->parent.lock());
  parent->addChild(_ctx);
}

void Parser::enterRule(Ref<ParserRuleContext> localctx, int state, int ruleIndex) {
  setState(state);
  _ctx = localctx;
  _ctx->start = _input->LT(1);
  if (_buildParseTrees) {
    addContextToParseTree();
  }
  if (_parseListeners.size() > 0) {
    triggerEnterRuleEvent();
  }
}

void Parser::exitRule() {
  if (_matchedEOF) {
    // if we have matched EOF, it cannot consume past EOF so we use LT(1) here
    _ctx->stop = _input->LT(1); // LT(1) will be end of file
  } else {
    _ctx->stop = _input->LT(-1); // stop node is what we just matched
  }

  _ctx->stop = _input->LT(-1);
  // trigger event on ctx, before it reverts to parent
  if (_parseListeners.size() > 0) {
    triggerExitRuleEvent();
  }
  setState(_ctx->invokingState);
  _ctx = std::dynamic_pointer_cast<ParserRuleContext>(_ctx->parent.lock());
}

void Parser::enterOuterAlt(Ref<ParserRuleContext> localctx, int altNum) {
  localctx->setAltNumber(altNum);

  // if we have new localctx, make sure we replace existing ctx
  // that is previous child of parse tree
  if (_buildParseTrees && _ctx != localctx) {
    if (!_ctx->parent.expired()) {
      Ref<ParserRuleContext> parent = std::dynamic_pointer_cast<ParserRuleContext>(_ctx->parent.lock());
      parent->removeLastChild();
      parent->addChild(localctx);
    }
  }
  _ctx = localctx;
}

int Parser::getPrecedence() const {
  if (_precedenceStack.empty()) {
    return -1;
  }

  return _precedenceStack.back();
}

void Parser::enterRecursionRule(Ref<ParserRuleContext> localctx, int ruleIndex) {
  enterRecursionRule(localctx, getATN().ruleToStartState[(size_t)ruleIndex]->stateNumber, ruleIndex, 0);
}

void Parser::enterRecursionRule(Ref<ParserRuleContext> localctx, int state, int ruleIndex, int precedence) {
  setState(state);
  _precedenceStack.push_back(precedence);
  _ctx = localctx;
  _ctx->start = _input->LT(1);
  if (_parseListeners.size() > 0) {
    triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
  }
}

void Parser::pushNewRecursionContext(Ref<ParserRuleContext> localctx, int state, int ruleIndex) {
  Ref<ParserRuleContext> previous = _ctx;
  previous->parent = localctx;
  previous->invokingState = state;
  previous->stop = _input->LT(-1);

  _ctx = localctx;
  _ctx->start = previous->start;
  if (_buildParseTrees) {
    _ctx->addChild(previous);
  }

  if (_parseListeners.size() > 0) {
    triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
  }
}

void Parser::unrollRecursionContexts(Ref<ParserRuleContext> parentctx) {
  _precedenceStack.pop_back();
  _ctx->stop = _input->LT(-1);
  Ref<ParserRuleContext> retctx = _ctx; // save current ctx (return value)

  // unroll so ctx is as it was before call to recursive method
  if (_parseListeners.size() > 0) {
    while (_ctx != parentctx) {
      triggerExitRuleEvent();
      _ctx = std::dynamic_pointer_cast<ParserRuleContext>(_ctx->parent.lock());
    }
  } else {
    _ctx = parentctx;
  }

  // hook into tree
  retctx->parent = parentctx;

  if (_buildParseTrees && parentctx != nullptr) {
    // add return ctx into invoking rule's tree
    parentctx->addChild(retctx);
  }
}

Ref<ParserRuleContext> Parser::getInvokingContext(int ruleIndex) {
  Ref<ParserRuleContext> p = _ctx;
  while (p) {
    if (p->getRuleIndex() == ruleIndex) {
      return p;
    }
    if (p->parent.expired())
      break;
    p = std::dynamic_pointer_cast<ParserRuleContext>(p->parent.lock());
  }
  return Ref<ParserRuleContext>();
}

Ref<ParserRuleContext> Parser::getContext() {
  return _ctx;
}

void Parser::setContext(Ref<ParserRuleContext> ctx) {
  _ctx = ctx;
}

bool Parser::precpred(Ref<RuleContext> localctx, int precedence) {
  return precedence >= _precedenceStack.back();
}

bool Parser::inContext(const std::wstring &context) {
  // TO_DO: useful in parser?
  return false;
}

bool Parser::isExpectedToken(int symbol) {
  const atn::ATN &atn = getInterpreter<atn::ParserATNSimulator>()->atn;
  Ref<ParserRuleContext> ctx = _ctx;
  atn::ATNState *s = atn.states[(size_t)getState()];
  misc::IntervalSet following = atn.nextTokens(s);

  if (following.contains(symbol)) {
    return true;
  }

  if (!following.contains(Token::EPSILON)) {
    return false;
  }

  while (ctx && ctx->invokingState >= 0 && following.contains(Token::EPSILON)) {
    atn::ATNState *invokingState = atn.states[(size_t)ctx->invokingState];
    atn::RuleTransition *rt = static_cast<atn::RuleTransition*>(invokingState->transition(0));
    following = atn.nextTokens(rt->followState);
    if (following.contains(symbol)) {
      return true;
    }

    ctx = std::dynamic_pointer_cast<ParserRuleContext>(ctx->parent.lock());
  }

  if (following.contains(Token::EPSILON) && symbol == EOF) {
    return true;
  }

  return false;
}

bool Parser::isMatchedEOF() const {
  return _matchedEOF;
}

misc::IntervalSet Parser::getExpectedTokens() {
  return getATN().getExpectedTokens(getState(), getContext());
}

misc::IntervalSet Parser::getExpectedTokensWithinCurrentRule() {
  const atn::ATN &atn = getInterpreter<atn::ParserATNSimulator>()->atn;
  atn::ATNState *s = atn.states[(size_t)getState()];
  return atn.nextTokens(s);
}

ssize_t Parser::getRuleIndex(const std::wstring &ruleName) {
  const std::map<std::wstring, size_t> &m = getRuleIndexMap();
  auto iterator = m.find(ruleName);
  if (iterator == m.end()) {
    return -1;
  }
  return iterator->second;
}

Ref<ParserRuleContext> Parser::getRuleContext() {
  return _ctx;
}

std::vector<std::wstring> Parser::getRuleInvocationStack() {
  return getRuleInvocationStack(_ctx);
}

std::vector<std::wstring> Parser::getRuleInvocationStack(Ref<RuleContext> p) {
  std::vector<std::wstring> ruleNames = getRuleNames();
  std::vector<std::wstring> stack = std::vector<std::wstring>();
  while (p) {
    // compute what follows who invoked us
    ssize_t ruleIndex = p->getRuleIndex();
    if (ruleIndex < 0) {
      stack.push_back(L"n/a");
    } else {
      stack.push_back(ruleNames[(size_t)ruleIndex]);
    }
    if (p->parent.expired())
      break;
    p = p->parent.lock();
  }
  return stack;
}

std::vector<std::wstring> Parser::getDFAStrings() {
  atn::ParserATNSimulator *simulator = getInterpreter<atn::ParserATNSimulator>();
  if (!simulator->decisionToDFA.empty()) {
    std::lock_guard<std::mutex> lck(mtx);

    std::vector<std::wstring> s;
    for (size_t d = 0; d < simulator->decisionToDFA.size(); d++) {
      dfa::DFA &dfa = simulator->decisionToDFA[d];
      s.push_back(dfa.toString(getVocabulary()));
    }
    return s;
  }
  return std::vector<std::wstring>();
}

void Parser::dumpDFA() {
  atn::ParserATNSimulator *simulator = getInterpreter<atn::ParserATNSimulator>();
  if (!simulator->decisionToDFA.empty()) {
    std::lock_guard<std::mutex> lck(mtx);
    bool seenOne = false;
    for (size_t d = 0; d < simulator->decisionToDFA.size(); d++) {
      dfa::DFA &dfa = simulator->decisionToDFA[d];
      if (!dfa.states.empty()) {
        if (seenOne) {
          std::cout << std::endl;
        }
        std::cout << L"Decision " << dfa.decision << L":" << std::endl;
								dfa.toString(getTokenNames());
        std::wcout << dfa.toString(getVocabulary());
        seenOne = true;
      }
    }
  }
}

std::string Parser::getSourceName() {
  return _input->getSourceName();
}

Ref<atn::ParseInfo> Parser::getParseInfo() const {
  atn::ProfilingATNSimulator *interp = getInterpreter<atn::ProfilingATNSimulator>();
  if (interp != nullptr) {
    return std::make_shared<atn::ParseInfo>(interp);
  }
  return nullptr;
}

void Parser::setProfile(bool profile) {
  atn::ParserATNSimulator *interp = getInterpreter<atn::ProfilingATNSimulator>();
  atn::PredictionMode saveMode = interp->getPredictionMode();
  if (profile) {
    if (!is<atn::ProfilingATNSimulator *>(interp)) {
      setInterpreter(new atn::ProfilingATNSimulator(this)); /* mem-check: replacing existing interpreter which gets deleted. */
    }
  } else if (is<atn::ProfilingATNSimulator *>(interp)) {
    /* mem-check: replacing existing interpreter which gets deleted. */
    atn::ParserATNSimulator *sim = new atn::ParserATNSimulator(this, getATN(), interp->decisionToDFA, interp->getSharedContextCache());
    setInterpreter(sim);
  }
  getInterpreter<atn::ParserATNSimulator>()->setPredictionMode(saveMode);
}

void Parser::setTrace(bool trace) {
  if (!trace) {
    if (_tracer)
      removeParseListener(_tracer);
    _tracer.reset();
  } else {
    if (_tracer)
      removeParseListener(_tracer); // Just in case this is triggered multiple times.
    _tracer = std::make_shared<TraceListener>(this);
    addParseListener(_tracer);
  }
}

bool Parser::isTrace() const {
  return _tracer != nullptr;
}

void Parser::InitializeInstanceFields() {
  _errHandler = std::make_shared<DefaultErrorStrategy>();
  _precedenceStack.clear();
  _precedenceStack.push_back(0);
  _buildParseTrees = true;
  _syntaxErrors = 0;
  _matchedEOF = false;
  _input = nullptr;
}

