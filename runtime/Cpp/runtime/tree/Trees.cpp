/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
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

#include "ErrorNode.h"
#include "Parser.h"
#include "ParserRuleContext.h"
#include "CPPUtils.h"
#include "TerminalNodeImpl.h"

#include "Trees.h"

using namespace org::antlr::v4::runtime;
using namespace org::antlr::v4::runtime::tree;

using namespace antlrcpp;

std::wstring Trees::toStringTree(std::shared_ptr<Tree> t) {
  return toStringTree(t, nullptr);
}

std::wstring Trees::toStringTree(std::shared_ptr<Tree> t, Parser *recog) {
  return toStringTree(t, recog->getRuleNames());
}

std::wstring Trees::toStringTree(std::shared_ptr<Tree> t, const std::vector<std::wstring> &ruleNames) {
  std::wstring tmp = Trees::getNodeText(t, ruleNames);
  std::wstring s = antlrcpp::escapeWhitespace(tmp, false);
  if (t->getChildCount() == 0) {
    return s;
  }

  std::wstringstream ss;
  ss << L"(" << antlrcpp::escapeWhitespace(getNodeText(t, ruleNames), false) << L' ';
  for (size_t i = 0; i < t->getChildCount(); i++) {
    if (i > 0) {
      ss << L' ';
    }
    ss << toStringTree(t->getChild(i), ruleNames);
  }
  ss << L")";
  return ss.str();
}

std::wstring Trees::getNodeText(std::shared_ptr<Tree> t, Parser *recog) {
  return getNodeText(t, recog->getRuleNames());
}

std::wstring Trees::getNodeText(std::shared_ptr<Tree> t, const std::vector<std::wstring> &ruleNames) {
  if (ruleNames.size() > 0) {
    if (is<RuleNode>(t)) {
      ssize_t ruleIndex = (std::static_pointer_cast<RuleNode>(t))->getRuleContext()->getRuleIndex();
      if (ruleIndex < 0)
        return L"Invalid Rule Index";
      std::wstring ruleName = ruleNames[(size_t)ruleIndex];
      return ruleName;
    } else if (is<ErrorNode>(t)) {
      return t->toString();
    } else if (is<TerminalNode>(t)) {
      Token::Ref symbol = (std::static_pointer_cast<TerminalNode>(t))->getSymbol();
      if (symbol != nullptr) {
        std::wstring s = symbol->getText();
        return s;
      }
    }
  }
  // no recog for rule names
  if (is<RuleContext>(t)) {
    return std::static_pointer_cast<RuleContext>(t)->getText();
  }

  if (is<TerminalNodeImpl>(t)) {
    return std::dynamic_pointer_cast<TerminalNodeImpl>(t)->getSymbol()->getText();
  }

  return L"";
}

std::vector<std::shared_ptr<Tree>> Trees::getChildren(std::shared_ptr<Tree> t) {
  std::vector<std::shared_ptr<Tree>> kids;
  for (size_t i = 0; i < t->getChildCount(); i++) {
    kids.push_back(t->getChild(i));
  }
  return kids;
}

std::vector<std::weak_ptr<Tree>> Trees::getAncestors(std::shared_ptr<Tree> t) {
  std::vector<std::weak_ptr<Tree>> ancestors;
  while (!t->getParent().expired()) {
    t = t->getParent().lock();
    ancestors.insert(ancestors.begin(), t); // insert at start
  }
  return ancestors;
}

template<typename T>
static void _findAllNodes(std::shared_ptr<ParseTree> t, int index, bool findTokens, std::vector<T> &nodes) {
  // check this node (the root) first
  if (findTokens && is<TerminalNode>(t)) {
    std::shared_ptr<TerminalNode> tnode = std::dynamic_pointer_cast<TerminalNode>(t);
    if (tnode->getSymbol()->getType() == index) {
      nodes.push_back(t);
    }
  } else if (!findTokens && is<ParserRuleContext>(t)) {
    ParserRuleContext::Ref ctx = std::dynamic_pointer_cast<ParserRuleContext>(t);
    if (ctx->getRuleIndex() == index) {
      nodes.push_back(t);
    }
  }
  // check children
  for (size_t i = 0; i < t->getChildCount(); i++) {
    _findAllNodes(t->getChild(i), index, findTokens, nodes);
  }
}

std::vector<std::shared_ptr<ParseTree>> Trees::findAllTokenNodes(std::shared_ptr<ParseTree> t, int ttype) {
  return findAllNodes(t, ttype, true);
}

std::vector<std::shared_ptr<ParseTree>> Trees::findAllRuleNodes(std::shared_ptr<ParseTree> t, int ruleIndex) {
  return findAllNodes(t, ruleIndex, false);
}

std::vector<std::shared_ptr<ParseTree>> Trees::findAllNodes(std::shared_ptr<ParseTree> t, int index, bool findTokens) {
  std::vector<std::shared_ptr<ParseTree>> nodes;
  _findAllNodes<std::shared_ptr<ParseTree>>(t, index, findTokens, nodes);
  return nodes;
}

std::vector<std::shared_ptr<ParseTree>> Trees::descendants(std::shared_ptr<ParseTree> t) {
  std::vector<std::shared_ptr<ParseTree>> nodes;
  nodes.push_back(t);
  std::size_t n = t->getChildCount();
  for (size_t i = 0 ; i < n ; i++) {
    auto descentants = descendants(t->getChild(i));
    for (auto entry: descentants) {
      nodes.push_back(entry);
    }
  }
  return nodes;
}

Trees::Trees() {
}
