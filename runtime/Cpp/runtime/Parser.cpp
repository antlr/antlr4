#include <locale>
#include <vector>

#include "Parser.h"
#include "ATNSimulator.h"
#include "ATNDeserializationOptions.h"
#include "ATNDeserializer.h"
#include "TokenSource.h"
#include "tree/pattern/ParseTreePatternMatcher.h"
#include "ANTLRErrorListener.h"
#include "ATNState.h"
#include "DFA.h"
#include "ParserRuleContext.h"
#include "Token.h"
#include "TerminalNode.h"
#include "TokenStream.h"
#include "ANTLRErrorStrategy.h"
#include "Exceptions.h"
#include "ParseTreeListener.h"
#include "ParseTree.h"
#include "Lexer.h"
#include "ATN.h"
#include "ParserATNSimulator.h"
#include "IntervalSet.h"
#include "RuleStartState.h"
#include "DefaultErrorStrategy.h"
#include "Strings.h"

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
namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {

                std::map<std::wstring, atn::ATN*> * Parser::bypassAltsAtnCache = new std::map<std::wstring, atn::ATN*>();
                
                Parser::TraceListener::TraceListener(Parser *outerInstance) : outerInstance(outerInstance) {
                }

                void Parser::TraceListener::enterEveryRule(ParserRuleContext *ctx) {
                  std::cout << "enter   "
                            << antlrcpp::ws2s(outerInstance->getRuleNames()[ctx->getRuleIndex()])
                            << ", LT(1)=" << antlrcpp::ws2s(outerInstance->_input->LT(1)->getText())
                            << std::endl;
                }

                void Parser::TraceListener::visitTerminal(tree::TerminalNode *node) {
                    std::cout << "consume "
                              << node->getSymbol() << " rule "
                              << antlrcpp::ws2s(outerInstance->getRuleNames()[outerInstance->_ctx->getRuleIndex()])
                              << std::endl;
                }

                void Parser::TraceListener::visitErrorNode(tree::ErrorNode *node) {
                }

                void Parser::TraceListener::exitEveryRule(ParserRuleContext *ctx) {
                  std::cout << "exit    "
                            << antlrcpp::ws2s(outerInstance->getRuleNames()[ctx->getRuleIndex()])
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
                    // TODO: Need to figure out what type this is going to be.  In Java we expect it to be set by the generator.
                    std::vector<tree::ParseTree*>* tmp = dynamic_cast<std::vector<tree::ParseTree*>*>(&ctx->_children);
                    if (tmp != nullptr) {
                        tmp->shrink_to_fit();
                    }
                }

                 Parser::Parser(TokenStream* input) {
                    InitializeInstanceFields();
                    setInputStream(input);

                    // TODO: Initialize this safely and handle concurrent accesses.
                    // TODO: For now treat this as a member variable but it should be shared across instances for speed.
                    Parser::bypassAltsAtnCache =
                       new std::map<std::wstring, atn::ATN*>();
                }

                void Parser::reset() {
                    if (getInputStream() != nullptr) {
                        getInputStream()->seek(0);
                    }
                    _errHandler->reset(this);

                    delete _ctx;
                    _syntaxErrors = 0;
                    setTrace(false);
                    _precedenceStack.clear();
                    _precedenceStack.push_back(0);
                    atn::ATNSimulator *interpreter = getInterpreter();
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
                            _ctx->addErrorNode(t);
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
                        throw NullPointerException(L"listener");
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
                        listener->enterEveryRule(_ctx);
                        _ctx->enterRule(listener);
                    }
                }

                void Parser::triggerExitRuleEvent() {
                    // reverse order walk of listeners
                    for (auto it = _parseListeners.rbegin(); it != _parseListeners.rend(); ++it) {
                        tree::ParseTreeListener *listener = *it;
                        _ctx->exitRule(*it);
                        listener->exitEveryRule(_ctx);
                    }
                }

                int Parser::getNumberOfSyntaxErrors() {
                    return _syntaxErrors;
                }

                TokenFactory<CommonToken*> *Parser::getTokenFactory() {
                    return _input->getTokenSource()->getTokenFactory();
                }


                atn::ATN *Parser::getATNWithBypassAlts() {
                    std::wstring serializedAtn = getSerializedATN();
                    if (serializedAtn == L"") {
                        throw UnsupportedOperationException(L"The current parser does not support an ATN with bypass alternatives.");
                    }

					if (bypassAltsAtnCache != nullptr) {
						std::lock_guard<std::mutex> lck(mtx);
                        atn::ATN *result = bypassAltsAtnCache->at(serializedAtn);
                        if (result == nullptr) {
                            atn::ATNDeserializationOptions *deserializationOptions = new atn::ATNDeserializationOptions();
                            deserializationOptions->setGenerateRuleBypassTransitions(true);
                            result = (new atn::ATNDeserializer(deserializationOptions))->deserialize(serializedAtn);
                            bypassAltsAtnCache->emplace(serializedAtn, result);
                        }

                        return result;
                    }
                    return nullptr;
                }

                tree::pattern::ParseTreePattern *Parser::compileParseTreePattern(const std::wstring &pattern, int patternRuleIndex) {
                    if (getTokenStream() != nullptr) {
                        TokenSource *tokenSource = getTokenStream()->getTokenSource();
                        if (dynamic_cast<Lexer*>(tokenSource) != nullptr) {
                            Lexer *lexer = static_cast<Lexer*>(tokenSource);
                            return compileParseTreePattern(pattern, patternRuleIndex, lexer);
                        }
                    }
                    throw UnsupportedOperationException(L"Parser can't discover a lexer to use");
                }

                tree::pattern::ParseTreePattern *Parser::compileParseTreePattern(const std::wstring &pattern, int patternRuleIndex, Lexer *lexer) {
                    tree::pattern::ParseTreePatternMatcher *m = new tree::pattern::ParseTreePatternMatcher(lexer, this);
                    return m->compile(pattern, patternRuleIndex);
                }

                ANTLRErrorStrategy *Parser::getErrorHandler() {
                    return _errHandler;
                }

                void Parser::setErrorHandler(ANTLRErrorStrategy *handler) {
                    this->_errHandler = handler;
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
                    listener->syntaxError(this, offendingToken, line, charPositionInLine, msg, e);
                }

                Token *Parser::consume() {
                    Token *o = getCurrentToken();
                    if (o->getType() != EOF) {
                        getInputStream()->consume();
                    }
                    bool hasListener = _parseListeners.size() > 0 && !_parseListeners.empty();
                    if (_buildParseTrees || hasListener) {
                        if (_errHandler->inErrorRecoveryMode(this)) {
                            tree::ErrorNode *node = _ctx->addErrorNode(o);
                            if (_parseListeners.size() > 0) {
                                for (auto listener : _parseListeners) {
                                    listener->visitErrorNode(node);
                                }
                            }
                        } else {
                            tree::TerminalNode *node = _ctx->addChild(o);
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
                    ParserRuleContext *parent = static_cast<ParserRuleContext*>(_ctx->parent);
                    // add current context to parent if we have a parent
                    if (parent != nullptr) {
                        parent->addChild(_ctx);
                    }
                }

                void Parser::enterRule(ParserRuleContext *localctx, int state, int ruleIndex) {
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
                    _ctx->stop = _input->LT(-1);
                    // trigger event on _ctx, before it reverts to parent
                    if (_parseListeners.size() > 0) {
                        triggerExitRuleEvent();
                    }
                    setState(_ctx->invokingState);
                    _ctx = static_cast<ParserRuleContext*>(_ctx->parent);
                }

                void Parser::enterOuterAlt(ParserRuleContext *localctx, int altNum) {
                    // if we have new localctx, make sure we replace existing ctx
                    // that is previous child of parse tree
                    if (_buildParseTrees && _ctx != localctx) {
                        ParserRuleContext *parent = static_cast<ParserRuleContext*>(_ctx->parent);
                        if (parent != nullptr) {
                            parent->removeLastChild();
                            parent->addChild(localctx);
                        }
                    }
                    _ctx = localctx;
                }

                void Parser::enterRecursionRule(ParserRuleContext *localctx, int ruleIndex) {
                    enterRecursionRule(localctx, getATN()->ruleToStartState[ruleIndex]->stateNumber, ruleIndex, 0);
                }

                void Parser::enterRecursionRule(ParserRuleContext *localctx, int state, int ruleIndex, int precedence) {
                    setState(state);
                    _precedenceStack.push_back(precedence);
                    _ctx = localctx;
                    _ctx->start = _input->LT(1);
                    if (_parseListeners.size() > 0) {
                        triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
                    }
                }

                void Parser::pushNewRecursionContext(ParserRuleContext *localctx, int state, int ruleIndex) {
                    ParserRuleContext *previous = _ctx;
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

                void Parser::unrollRecursionContexts(ParserRuleContext *_parentctx) {
                    _precedenceStack.pop_back();
                    _ctx->stop = _input->LT(-1);
                    ParserRuleContext *retctx = _ctx; // save current ctx (return value)

                    // unroll so _ctx is as it was before call to recursive method
                    if (_parseListeners.size() > 0) {
                        while (_ctx != _parentctx) {
                            triggerExitRuleEvent();
                            _ctx = static_cast<ParserRuleContext*>(_ctx->parent);
                        }
                    } else {
                        _ctx = _parentctx;
                    }

                    // hook into tree
                    retctx->parent = _parentctx;

                    if (_buildParseTrees && _parentctx != nullptr) {
                        // add return ctx into invoking rule's tree
                        _parentctx->addChild(retctx);
                    }
                }

                ParserRuleContext *Parser::getInvokingContext(int ruleIndex) {
                    ParserRuleContext *p = _ctx;
                    while (p != nullptr) {
                        if (p->getRuleIndex() == ruleIndex) {
                            return p;
                        }
                        p = static_cast<ParserRuleContext*>(p->parent);
                    }
                    return nullptr;
                }

                ParserRuleContext *Parser::getContext() {
                    return _ctx;
                }

                void Parser::setContext(ParserRuleContext *ctx) {
                    _ctx = ctx;
                }

                bool Parser::precpred(RuleContext *localctx, int precedence) {
                    return precedence >= _precedenceStack.back();
                }

                bool Parser::inContext(const std::wstring &context) {
                    // TODO: useful in parser?
                    return false;
                }

                bool Parser::isExpectedToken(int symbol) {
                                //   		return getInterpreter().atn.nextTokens(_ctx);
                    atn::ATN *atn = getInterpreter()->atn;
                    ParserRuleContext *ctx = _ctx;
                    atn::ATNState *s = atn->states[getState()];
                    misc::IntervalSet *following = atn->nextTokens(s);
                    if (following->contains(symbol)) {
                        return true;
                    }
                                //        System.out.println("following "+s+"="+following);
                    if (!following->contains(Token::EPSILON)) {
                        return false;
                    }

                    while (ctx != nullptr && ctx->invokingState >= 0 && following->contains(Token::EPSILON)) {
                        atn::ATNState *invokingState = atn->states[ctx->invokingState];
                        atn::RuleTransition *rt = static_cast<atn::RuleTransition*>(invokingState->transition(0));
                        following = atn->nextTokens(rt->followState);
                        if (following->contains(symbol)) {
                            return true;
                        }

                        ctx = static_cast<ParserRuleContext*>(ctx->parent);
                    }

                    if (following->contains(Token::EPSILON) && symbol == Token::_EOF) {
                        return true;
                    }

                    return false;
                }

                misc::IntervalSet *Parser::getExpectedTokens() {
                    return getATN()->getExpectedTokens(getState(), getContext());
                }

                misc::IntervalSet *Parser::getExpectedTokensWithinCurrentRule() {
                    atn::ATN *atn = getInterpreter()->atn;
                    atn::ATNState *s = atn->states[getState()];
                       return atn->nextTokens(s);
                }

                int Parser::getRuleIndex(const std::wstring &ruleName) {
                    std::map<std::wstring, int>* m = getRuleIndexMap();
                    if (m->find(ruleName) == m->end()) {
                        return -1;
                    }
                    return m->at(ruleName);
                }

               ParserRuleContext *Parser::getRuleContext() {
                    return _ctx;
                }

                std::vector<std::wstring> Parser::getRuleInvocationStack() {
                    return getRuleInvocationStack(_ctx);
                }

                std::vector<std::wstring> Parser::getRuleInvocationStack(RuleContext *p) {
                    std::vector<std::wstring> ruleNames = getRuleNames();
                    std::vector<std::wstring> stack = std::vector<std::wstring>();
                    while (p != nullptr) {
                        // compute what follows who invoked us
                        int ruleIndex = p->getRuleIndex();
                        if (ruleIndex < 0) {
                            stack.push_back(L"n/a");
                        } else {
                            stack.push_back(ruleNames[ruleIndex]);
                        }
                        p = p->parent;
                    }
                    return stack;
                }

                std::vector<std::wstring> Parser::getDFAStrings() {
					if (!_interpreter->_decisionToDFA.empty()) {
						std::lock_guard<std::mutex> lck(mtx);
                        std::vector<std::wstring> s = std::vector<std::wstring>();
                        for (size_t d = 0; d < _interpreter->_decisionToDFA.size(); d++) {
                            dfa::DFA *dfa = _interpreter->_decisionToDFA[d];
                            s.push_back(dfa->toString(getTokenNames()));
                        }
                        return s;
                    }
                    return std::vector<std::wstring>();
                }

                void Parser::dumpDFA() {

					if (!_interpreter->_decisionToDFA.empty()) {
						std::lock_guard<std::mutex> lck(mtx);
                        bool seenOne = false;
                        for (size_t d = 0; d < _interpreter->_decisionToDFA.size(); d++) {
                            dfa::DFA *dfa = _interpreter->_decisionToDFA[d];
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
                        removeParseListener(_tracer);
                        // TODO
                        //JAVA TO C++ CONVERTER WARNING: Java to C++ Converter converted the original 'null' assignment to a call to 'delete', but you should review memory allocation of all pointer variables in the converted code:
                        delete _tracer;
                    } else {
                        if (_tracer != nullptr) {
                            removeParseListener(_tracer);
                        } else {
                            _tracer = new TraceListener(this);
                        }
                        addParseListener(_tracer);
                    }
                }

                void Parser::InitializeInstanceFields() {
                    _errHandler = new DefaultErrorStrategy();
                    _precedenceStack.clear();
                    _precedenceStack.push_back(0);
                    _buildParseTrees = true;
                    _syntaxErrors = 0;
                }
            }
        }
    }
}
