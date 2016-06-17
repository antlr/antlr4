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

#include "tree/ErrorNodeImpl.h"
#include "misc/Interval.h"
#include "Parser.h"
#include "Token.h"
#include "support/CPPUtils.h"

#include "ParserRuleContext.h"

using namespace antlr4;
using namespace antlrcpp;

const Ref<ParserRuleContext> ParserRuleContext::EMPTY = std::make_shared<ParserRuleContext>();

ParserRuleContext::ParserRuleContext() {
}

void ParserRuleContext::copyFrom(Ref<ParserRuleContext> const& ctx) {
  // from RuleContext
  this->parent = ctx->parent;
  this->invokingState = ctx->invokingState;

  this->start = ctx->start;
  this->stop = ctx->stop;
}

ParserRuleContext::ParserRuleContext(std::weak_ptr<ParserRuleContext> parent, int invokingStateNumber)
  : RuleContext(parent, invokingStateNumber) {
}

void ParserRuleContext::enterRule(tree::ParseTreeListener * /*listener*/) {
}

void ParserRuleContext::exitRule(tree::ParseTreeListener * /*listener*/) {
}

Ref<tree::TerminalNode> ParserRuleContext::addChild(Ref<tree::TerminalNode> const& t) {
  children.push_back(t);
  return t;
}

Ref<RuleContext> ParserRuleContext::addChild(Ref<RuleContext> const& ruleInvocation) {
  children.push_back(ruleInvocation);
  return ruleInvocation;
}

void ParserRuleContext::removeLastChild() {
  if (!children.empty()) {
    children.pop_back();
  }
}

Ref<tree::TerminalNode> ParserRuleContext::addChild(Token *matchedToken) {
  Ref<tree::TerminalNodeImpl> t = std::make_shared<tree::TerminalNodeImpl>(matchedToken);
  addChild(t);
  t->parent = shared_from_this();
  return t;
}

Ref<tree::ErrorNode> ParserRuleContext::addErrorNode(Token *badToken) {
  Ref<tree::ErrorNodeImpl> t = std::make_shared<tree::ErrorNodeImpl>(badToken);
  addChild(t);
  t->parent = shared_from_this();
  return t;
}

Ref<tree::Tree> ParserRuleContext::getChildReference(std::size_t i) {
  return children[i];
}

Ref<tree::TerminalNode> ParserRuleContext::getToken(int ttype, std::size_t i) {
  if (i >= children.size()) {
    return nullptr;
  }

  size_t j = 0; // what token with ttype have we found?
  for (auto o : children) {
    if (is<tree::TerminalNode>(o)) {
      Ref<tree::TerminalNode> tnode = std::dynamic_pointer_cast<tree::TerminalNode>(o);
      Token *symbol = tnode->getSymbol();
      if (symbol->getType() == ttype) {
        if (j++ == i) {
          return tnode;
        }
      }
    }
  }

  return nullptr;
}

std::vector<Ref<tree::TerminalNode>> ParserRuleContext::getTokens(int ttype) {
  std::vector<Ref<tree::TerminalNode>> tokens;
  for (auto &o : children) {
    if (is<tree::TerminalNode>(o)) {
      Ref<tree::TerminalNode> tnode = std::dynamic_pointer_cast<tree::TerminalNode>(o);
      Token *symbol = tnode->getSymbol();
      if (symbol->getType() == ttype) {
        tokens.push_back(tnode);
      }
    }
  }

  return tokens;
}

std::size_t ParserRuleContext::getChildCount() {
  return children.size();
}

misc::Interval ParserRuleContext::getSourceInterval() {
  if (start == nullptr) {
    return misc::Interval::INVALID;
  }

  if (stop == nullptr || stop->getTokenIndex() < start->getTokenIndex()) {
    return misc::Interval(start->getTokenIndex(), start->getTokenIndex() - 1); // empty
  }
  return misc::Interval(start->getTokenIndex(), stop->getTokenIndex());
}

Token* ParserRuleContext::getStart() {
  return start;
}

Token* ParserRuleContext::getStop() {
  return stop;
}

std::string ParserRuleContext::toInfoString(Parser *recognizer) {
  std::vector<std::string> rules = recognizer->getRuleInvocationStack(shared_from_this());
  std::reverse(rules.begin(), rules.end());
  std::string rulesStr = antlrcpp::arrayToString(rules);
  return "ParserRuleContext" + rulesStr + "{start=" + std::to_string(start->getTokenIndex()) + ", stop=" +
    std::to_string(stop->getTokenIndex()) + '}';
}

