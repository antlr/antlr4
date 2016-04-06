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

#include "Trees.h"
#include "Interval.h"
#include "Parser.h"

#include "RuleContext.h"

using namespace org::antlr::v4::runtime;

ParserRuleContext *const RuleContext::EMPTY = new ParserRuleContext();

RuleContext::RuleContext() {
  InitializeInstanceFields();
}

RuleContext::RuleContext(RuleContext *parent, int invokingState) {
  InitializeInstanceFields();
  this->parent = parent;
  this->invokingState = invokingState;
}

int RuleContext::depth() {
  int n = 0;
  RuleContext *p = this;
  while (p != nullptr) {
    p = p->parent;
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

RuleContext *RuleContext::getRuleContext() {
  return this;
}

RuleContext *RuleContext::getParent() {
  return parent;
}

void *RuleContext::getPayload()
/// Return the combined text of all child nodes. This method only considers
/// tokens which have been added to the parse tree.
/// <para>
/// Since tokens on hidden channels (e.g. whitespace or comments) are not
/// added to the parse trees, they will not appear in the output of this
/// method.
{
  return this;
}

std::wstring RuleContext::getText() {
  if (getChildCount() == 0) {
    return L"";
  }

  std::wstringstream ss;
  for (size_t i = 0; i < getChildCount(); i++) {
    if (i > 0)
      ss << L", ";
    ss << getChild(i)->getText();
  }

  return ss.str();
}

ssize_t RuleContext::getRuleIndex() const {
  return -1;
}

tree::ParseTree *RuleContext::getChild(std::size_t i) {
  return nullptr;
}

std::size_t RuleContext::getChildCount() {
  return 0;
}

void RuleContext::save(Parser *parser, const std::wstring &fileName) {
  std::vector<std::wstring> ruleNames;
  if (parser != nullptr) {
    ruleNames = parser->getRuleNames();
  }
  save(ruleNames, fileName);
}

void RuleContext::save(Parser *parser, const std::wstring &fileName, const std::wstring &fontName, int fontSize) {
  std::vector<std::wstring> ruleNames;
  if (parser != nullptr) {
    ruleNames = parser->getRuleNames();
  }
  save(ruleNames, fileName, fontName, fontSize);
}

void RuleContext::save(std::vector<std::wstring> &ruleNames, const std::wstring &fileName) {
#ifdef TODO
  tree::Trees::writePS(this, ruleNames, fileName);
#endif
}

void RuleContext::save(std::vector<std::wstring> &ruleNames, const std::wstring &fileName, const std::wstring &fontName, int fontSize) {
#ifdef TODO
  tree::Trees::writePS(this, ruleNames, fileName, fontName, fontSize);
#endif
}

std::wstring RuleContext::toStringTree(Parser *recog) {
  return tree::Trees::toStringTree(this, recog);
}

std::wstring RuleContext::toStringTree(std::vector<std::wstring> &ruleNames) {
  return tree::Trees::toStringTree(this, ruleNames);
}

std::wstring RuleContext::toStringTree() {
  return toStringTree(nullptr);
}


std::wstring RuleContext::toString(const std::vector<std::wstring> &ruleNames) {
  return toString(ruleNames, static_cast<RuleContext*>(nullptr));
}


std::wstring RuleContext::toString(const std::vector<std::wstring> &ruleNames, RuleContext *stop) {
  std::wstringstream ss;

  RuleContext *p = this;
  ss << L"[";
  while (p != nullptr && p != stop) {
    if (ruleNames.empty()) {
      if (!p->isEmpty()) {
        ss << p->invokingState;
      }
    } else {
      ssize_t ruleIndex = p->getRuleIndex();

      std::wstring ruleName = (ruleIndex >= 0 && ruleIndex < (ssize_t)ruleNames.size()) ? ruleNames[(size_t)ruleIndex] : std::to_wstring(ruleIndex);
      ss << ruleName;
    }

    if (p->parent != nullptr && (ruleNames.size() > 0 || !p->parent->isEmpty())) {
      ss << L" ";
    }

    p = p->parent;
  }

  ss << L"]";

  return ss.str();
}

std::wstring RuleContext::toString() {
#ifdef TODO
#endif
  return L"TODO";
};

std::wstring RuleContext::toString(Recognizer *recog) {
  return toString(recog, ParserRuleContext::EMPTY);
}

std::wstring RuleContext::toString(Recognizer *recog, RuleContext *stop) {
  return toString(recog->getRuleNames(), stop);
}

std::wstring RuleContext::toString(Token *, atn::ParserATNSimulator *) {
  return L"TODO";
}

void RuleContext::InitializeInstanceFields() {
  invokingState = -1;
}
