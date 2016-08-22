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

#include "tree/Trees.h"
#include "misc/Interval.h"
#include "Parser.h"
#include "atn/ATN.h"
#include "tree/ParseTreeVisitor.h"

#include "RuleContext.h"

using namespace antlr4;

RuleContext::RuleContext() {
  InitializeInstanceFields();
}

RuleContext::RuleContext(std::weak_ptr<RuleContext> parent, int invokingState) {
  InitializeInstanceFields();
  this->parent = parent;
  this->invokingState = invokingState;
}

int RuleContext::depth() {
  int n = 1;
  RuleContext *p = this;
  while (true) {
    if (p->parent.expired())
      break;
    p = (RuleContext *)p->parent.lock().get();
    n++;
  }
  return n;
}

bool RuleContext::isEmpty() {
  return invokingState == -1;
}

misc::Interval RuleContext::getSourceInterval() {
  return misc::Interval::INVALID;
}

Ref<RuleContext> RuleContext::getRuleContext() {
  return shared_from_this();
}

std::string RuleContext::getText() {
  if (children.empty()) {
    return "";
  }

  std::stringstream ss;
  for (size_t i = 0; i < children.size(); i++) {
    if (i > 0)
      ss << ", ";

    Ref<ParseTree> tree = std::dynamic_pointer_cast<ParseTree>(children[i]);
    if (tree != nullptr)
      ss << tree->getText();
  }

  return ss.str();
}

ssize_t RuleContext::getRuleIndex() const {
  return -1;
}

int RuleContext::getAltNumber() const {
  return atn::ATN::INVALID_ALT_NUMBER;
}

void RuleContext::setAltNumber(int /*altNumber*/) {
}

antlrcpp::Any RuleContext::accept(tree::ParseTreeVisitor *visitor) {
  return visitor->visitChildren(this);
}

std::string RuleContext::toStringTree(Parser *recog) {
  return tree::Trees::toStringTree(shared_from_this(), recog);
}

std::string RuleContext::toStringTree(std::vector<std::string> &ruleNames) {
  return tree::Trees::toStringTree(shared_from_this(), ruleNames);
}

std::string RuleContext::toStringTree() {
  return toStringTree(nullptr);
}


std::string RuleContext::toString(const std::vector<std::string> &ruleNames) {
  return toString(ruleNames, Ref<RuleContext>());
}


std::string RuleContext::toString(const std::vector<std::string> &ruleNames, Ref<RuleContext> const& stop) {
  std::stringstream ss;

  RuleContext *currentParent = this;
  ss << "[";
  while (currentParent != stop.get()) {
    if (ruleNames.empty()) {
      if (!currentParent->isEmpty()) {
        ss << currentParent->invokingState;
      }
    } else {
      ssize_t ruleIndex = currentParent->getRuleIndex();

      std::string ruleName = (ruleIndex >= 0 && ruleIndex < (ssize_t)ruleNames.size()) ? ruleNames[(size_t)ruleIndex] : std::to_string(ruleIndex);
      ss << ruleName;
    }

    if (currentParent->parent.expired()) // No parent anymore.
      break;
    currentParent = (RuleContext *)currentParent->parent.lock().get();
    if (!ruleNames.empty() || !currentParent->isEmpty()) {
      ss << " ";
    }
  }

  ss << "]";

  return ss.str();
}

std::string RuleContext::toString() {
  return toString(nullptr);
}

std::string RuleContext::toString(Recognizer *recog) {
  return toString(recog, ParserRuleContext::EMPTY);
}

std::string RuleContext::toString(Recognizer *recog, Ref<RuleContext> const& stop) {
  if (recog == nullptr)
    return toString(std::vector<std::string>(), stop); // Don't use an initializer {} here or we end up calling ourselve recursivly.
  return toString(recog->getRuleNames(), stop);
}

void RuleContext::InitializeInstanceFields() {
  invokingState = -1;
}
