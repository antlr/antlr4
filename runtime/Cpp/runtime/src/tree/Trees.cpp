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

#include "tree/ErrorNode.h"
#include "Parser.h"
#include "ParserRuleContext.h"
#include "support/CPPUtils.h"
#include "tree/TerminalNodeImpl.h"
#include "atn/ATN.h"
#include "misc/Interval.h"
#include "Token.h"
#include "CommonToken.h"
#include "misc/Predicate.h"

#include "tree/Trees.h"

using namespace antlr4;
using namespace antlr4::misc;
using namespace antlr4::tree;

using namespace antlrcpp;

Trees::Trees() {
}

std::string Trees::toStringTree(Ref<Tree> const& t) {
  return toStringTree(t, nullptr);
}

std::string Trees::toStringTree(Ref<Tree> const& t, Parser *recog) {
  if (recog == nullptr)
    return toStringTree(t, std::vector<std::string>());
  return toStringTree(t, recog->getRuleNames());
}

std::string Trees::toStringTree(Ref<Tree> const& t, const std::vector<std::string> &ruleNames) {
  std::string temp = antlrcpp::escapeWhitespace(Trees::getNodeText(t, ruleNames), false);
  if (t->children.empty()) {
    return temp;
  }

  std::stringstream ss;
  ss << "(" << temp << ' ';

  // Implement the recursive walk as iteration to avoid trouble with deep nesting.
  std::stack<size_t> stack;
  size_t childIndex = 0;
  Ref<Tree> run = t;
  while (childIndex < run->children.size()) {
    if (childIndex > 0) {
      ss << ' ';
    }
    Ref<Tree> child = run->children[childIndex];
    std::weak_ptr<Tree> parent = child->parent;
    temp = antlrcpp::escapeWhitespace(Trees::getNodeText(child, ruleNames), false);
    if (!child->children.empty()) {
      // Go deeper one level.
      stack.push(childIndex);
      run = child;
      childIndex = 0;
      ss << "(" << temp << " ";
    } else {
      ss << temp;
      while (++childIndex == run->children.size()) {
        if (stack.size() > 0) {
          // Reached the end of the current level. See if we can step up from here.
          childIndex = stack.top();
          stack.pop();
          run = run->parent.lock();
          ss << ")";
        } else {
          break;
        }
      }
    }
  }

  ss << ")";
  return ss.str();
}

std::string Trees::getNodeText(Ref<Tree> const& t, Parser *recog) {
  return getNodeText(t, recog->getRuleNames());
}

std::string Trees::getNodeText(Ref<Tree> const& t, const std::vector<std::string> &ruleNames) {
  if (ruleNames.size() > 0) {
    if (is<RuleContext>(t)) {
      ssize_t ruleIndex = std::static_pointer_cast<RuleContext>(t)->getRuleContext()->getRuleIndex();
      if (ruleIndex < 0)
        return "Invalid Rule Index";
      std::string ruleName = ruleNames[ruleIndex];
      int altNumber = std::static_pointer_cast<RuleContext>(t)->getAltNumber();
      if (altNumber != atn::ATN::INVALID_ALT_NUMBER) {
        return ruleName + ":" + std::to_string(altNumber);
      }
      return ruleName;
    } else if (is<ErrorNode>(t)) {
      return t->toString();
    } else if (is<TerminalNode>(t)) {
      Token *symbol = (std::static_pointer_cast<TerminalNode>(t))->getSymbol();
      if (symbol != nullptr) {
        std::string s = symbol->getText();
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

  return "";
}

std::vector<std::weak_ptr<Tree>> Trees::getAncestors(Ref<Tree> const& t) {
  std::vector<std::weak_ptr<Tree>> ancestors;
  std::weak_ptr<Tree> parent = t->parent;
  while (!parent.expired()) {
    ancestors.insert(ancestors.begin(), parent); // insert at start
    parent = parent.lock()->parent;
  }
  return ancestors;
}

template<typename T>
static void _findAllNodes(Ref<ParseTree> const& t, size_t index, bool findTokens, std::vector<T> &nodes) {
  // check this node (the root) first
  if (findTokens && is<TerminalNode>(t)) {
    Ref<TerminalNode> tnode = std::dynamic_pointer_cast<TerminalNode>(t);
    if (tnode->getSymbol()->getType() == index) {
      nodes.push_back(t);
    }
  } else if (!findTokens && is<ParserRuleContext>(t)) {
    Ref<ParserRuleContext> ctx = std::dynamic_pointer_cast<ParserRuleContext>(t);
    if (ctx->getRuleIndex() == index) {
      nodes.push_back(t);
    }
  }
  // check children
  for (size_t i = 0; i < t->children.size(); i++) {
    _findAllNodes(std::dynamic_pointer_cast<ParseTree>(t->children[i]), index, findTokens, nodes);
  }
}

bool Trees::isAncestorOf(Ref<Tree> const& t, Ref<Tree> const& u) {
  if (t == nullptr || u == nullptr || t->parent.expired()) {
    return false;
  }

  Ref<Tree> p = u->parent.lock();
  while (p != nullptr) {
    if (t == p) {
      return true;
    }
    p = p->parent.lock();
  }
  return false;
}

std::vector<Ref<ParseTree>> Trees::findAllTokenNodes(Ref<ParseTree> const& t, size_t ttype) {
  return findAllNodes(t, ttype, true);
}

std::vector<Ref<ParseTree>> Trees::findAllRuleNodes(Ref<ParseTree> const& t, size_t ruleIndex) {
  return findAllNodes(t, ruleIndex, false);
}

std::vector<Ref<ParseTree>> Trees::findAllNodes(Ref<ParseTree> const& t, size_t index, bool findTokens) {
  std::vector<Ref<ParseTree>> nodes;
  _findAllNodes<Ref<ParseTree>>(t, index, findTokens, nodes);
  return nodes;
}

std::vector<Ref<ParseTree>> Trees::getDescendants(Ref<ParseTree> const& t) {
  std::vector<Ref<ParseTree>> nodes;
  nodes.push_back(t);
  std::size_t n = t->children.size();
  for (size_t i = 0 ; i < n ; i++) {
    auto descentants = getDescendants(std::dynamic_pointer_cast<ParseTree>(t->children[i]));
    for (auto entry: descentants) {
      nodes.push_back(entry);
    }
  }
  return nodes;
}

std::vector<Ref<ParseTree>> Trees::descendants(Ref<ParseTree> const& t) {
  return getDescendants(t);
}

Ref<ParserRuleContext> Trees::getRootOfSubtreeEnclosingRegion(Ref<ParseTree> const& t, size_t startTokenIndex,
                                                              size_t stopTokenIndex) {
  size_t n = t->children.size();
  for (size_t i = 0; i < n; i++) {
    Ref<ParseTree> child = std::dynamic_pointer_cast<ParseTree>(t->children[i]);
    Ref<ParserRuleContext> r = getRootOfSubtreeEnclosingRegion(child, startTokenIndex, stopTokenIndex);
    if (r != nullptr) {
      return r;
    }
  }

  if (is<ParserRuleContext>(t)) {
    Ref<ParserRuleContext> r = std::static_pointer_cast<ParserRuleContext>(t);
    if (startTokenIndex >= r->getStart()->getTokenIndex() && // is range fully contained in t?
        (r->getStop() == nullptr || stopTokenIndex <= r->getStop()->getTokenIndex())) {
      // note: r.getStop()==null likely implies that we bailed out of parser and there's nothing to the right
      return r;
    }
  }
  return nullptr;
}

Ref<Tree> Trees::findNodeSuchThat(Ref<Tree> const& t, Ref<Predicate<Tree>> const& pred) {
  if (pred->test(t)) {
    return t;
  }

  size_t n = t->children.size();
  for (size_t i = 0 ; i < n ; ++i) {
    Ref<Tree> u = findNodeSuchThat(t->children[i], pred);
    if (u != nullptr) {
      return u;
    }
  }

  return nullptr;
}

