/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Sam Harwell
 *  Copyright (c) 2013 Terence Parr
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

#include "XPathLexer.h"
#include "XPathLexerErrorListener.h"
#include "XPathElement.h"
#include "XPathWildcardAnywhereElement.h"
#include "XPathWildcardElement.h"
#include "XPathTokenAnywhereElement.h"
#include "XPathTokenElement.h"
#include "XPathRuleAnywhereElement.h"
#include "XPathRuleElement.h"

#include "tree/ParseTree.h"

#include "XPath.h"

using namespace antlr4;
using namespace antlr4::tree;
using namespace antlr4::tree::xpath;

const std::string XPath::WILDCARD = "*";
const std::string XPath::NOT = "!";

XPath::XPath(Parser *parser, const std::string &path) {
  _parser = parser;
  _path = path;
  _elements = split(path);
}

std::vector<XPathElement> XPath::split(const std::string &path) {
  ANTLRFileStream in(path);
  XPathLexer lexer(&in);
  lexer.removeErrorListeners();
  XPathLexerErrorListener listener;
  lexer.addErrorListener(&listener);
  CommonTokenStream tokenStream(&lexer);
  try {
    tokenStream.fill();
  } catch (LexerNoViableAltException &) {
    int pos = lexer.getCharPositionInLine();
    std::string msg = "Invalid tokens or characters at index " + std::to_string(pos) + " in path '" + path + "'";
    throw IllegalArgumentException(msg);
  }

  std::vector<Token *> tokens = tokenStream.getTokens();
  std::vector<XPathElement> elements;
  size_t n = tokens.size();
  size_t i = 0;
  bool done = false;
  while (!done && i < n) {
    Token *el = tokens[i];
    Token *next = nullptr;
    switch (el->getType()) {
      case XPathLexer::ROOT:
      case XPathLexer::ANYWHERE: {
        bool anywhere = el->getType() == XPathLexer::ANYWHERE;
        i++;
        next = tokens[i];
        bool invert = next->getType() == XPathLexer::BANG;
        if (invert) {
          i++;
          next = tokens[i];
        }
        XPathElement pathElement = getXPathElement(next, anywhere);
        pathElement.setInvert(invert);
        elements.push_back(pathElement);
        i++;
        break;

      }
      case XPathLexer::TOKEN_REF:
      case XPathLexer::RULE_REF:
      case XPathLexer::WILDCARD:
        elements.push_back(getXPathElement(el, false));
        i++;
        break;

      case Token::EOF:
        done = true;
        break;

      default :
        throw IllegalArgumentException("Unknow path element " + el->toString());
    }
  }

  return elements;
}

XPathElement XPath::getXPathElement(Token *wordToken, bool anywhere) {
  if (wordToken->getType() == Token::EOF) {
    throw IllegalArgumentException("Missing path element at end of path");
  }
  std::string word = wordToken->getText();
  size_t ttype = _parser->getTokenType(word);
  ssize_t ruleIndex = _parser->getRuleIndex(word);
  switch (wordToken->getType()) {
    case XPathLexer::WILDCARD :
      if (anywhere)
        return XPathWildcardAnywhereElement();
      return XPathWildcardElement();

    case XPathLexer::TOKEN_REF:
    case XPathLexer::STRING :
      if (ttype == Token::INVALID_TYPE) {
        throw IllegalArgumentException(word + " at index " + std::to_string(wordToken->getStartIndex()) + " isn't a valid token name");
      }
      if (anywhere)
        return XPathTokenAnywhereElement(word, (int)ttype);
      return XPathTokenElement(word, (int)ttype);

    default :
      if (ruleIndex == -1) {
        throw IllegalArgumentException(word + " at index " + std::to_string(wordToken->getStartIndex()) + " isn't a valid rule name");
      }
      if (anywhere)
        return XPathRuleAnywhereElement(word, (int)ruleIndex);
      return XPathRuleElement(word, (int)ruleIndex);
  }
}

std::vector<Ref<ParseTree>> XPath::findAll(const Ref<ParseTree> &tree, const std::string &xpath, Parser *parser) {
  Ref<XPath> p = std::make_shared<XPath>(parser, xpath);
  return p->evaluate(tree);
}

std::vector<Ref<ParseTree>> XPath::evaluate(const Ref<ParseTree> &t) {
  std::shared_ptr<ParserRuleContext> dummyRoot = std::make_shared<ParserRuleContext>();
  dummyRoot->children = { t }; // don't set t's parent.

  std::vector<Ref<ParseTree>> work = { dummyRoot };

  size_t i = 0;
  while (i < _elements.size()) {
    std::vector<Ref<ParseTree>> next;
    for (auto node : work) {
      if (node->getChildCount() > 0) {
        // only try to match next element if it has children
        // e.g., //func/*/stat might have a token node for which
        // we can't go looking for stat nodes.
        auto matching = _elements[i].evaluate(node);
        next.insert(next.end(), matching.begin(), matching.end());
      }
    }
    i++;
    work = next;
  }

  return work;
}
