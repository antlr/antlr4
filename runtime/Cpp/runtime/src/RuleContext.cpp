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
  Ref<RuleContext> p = shared_from_this();
  while (true) {
    if (p->parent.expired())
      break;
    p = p->parent.lock();
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

std::weak_ptr<tree::Tree> RuleContext::getParentReference()
{
  return std::dynamic_pointer_cast<tree::Tree>(parent.lock());
}

std::string RuleContext::getText() {
  if (getChildCount() == 0) {
    return "";
  }

  std::stringstream ss;
  for (size_t i = 0; i < getChildCount(); i++) {
    if (i > 0)
      ss << ", ";
    ss << getChild(i)->getText();
  }

  return ss.str();
}

ssize_t RuleContext::getRuleIndex() const {
  return -1;
}

Ref<tree::Tree> RuleContext::getChildReference(size_t /*i*/) {
  return Ref<tree::Tree>();
}


int RuleContext::getAltNumber() const {
  return atn::ATN::INVALID_ALT_NUMBER;
}

void RuleContext::setAltNumber(int /*altNumber*/) {
}

std::size_t RuleContext::getChildCount() {
  return 0;
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

  Ref<RuleContext> parent = shared_from_this();
  ss << "[";
  while (parent != stop) {
    if (ruleNames.empty()) {
      if (!parent->isEmpty()) {
        ss << parent->invokingState;
      }
    } else {
      ssize_t ruleIndex = parent->getRuleIndex();

      std::string ruleName = (ruleIndex >= 0 && ruleIndex < (ssize_t)ruleNames.size()) ? ruleNames[(size_t)ruleIndex] : std::to_string(ruleIndex);
      ss << ruleName;
    }

    if (parent->parent.expired()) // No parent anymore.
      break;
    parent = parent->parent.lock();
    if (!ruleNames.empty() || !parent->isEmpty()) {
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
