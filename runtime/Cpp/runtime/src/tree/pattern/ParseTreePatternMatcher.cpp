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

#include "ParseTreePattern.h"
#include "ParseTreeMatch.h"
#include "TerminalNode.h"
#include "CommonTokenStream.h"
#include "ParserInterpreter.h"
#include "TokenTagToken.h"
#include "ParserRuleContext.h"
#include "RuleTagToken.h"
#include "TagChunk.h"
#include "ATN.h"
#include "Lexer.h"
#include "BailErrorStrategy.h"

#include "ListTokenSource.h"
#include "TextChunk.h"
#include "ANTLRInputStream.h"
#include "Arrays.h"
#include "Exceptions.h"
#include "Strings.h"
#include "CPPUtils.h"

#include "ParseTreePatternMatcher.h"

using namespace org::antlr::v4::runtime;
using namespace org::antlr::v4::runtime::tree;
using namespace org::antlr::v4::runtime::tree::pattern;
using namespace antlrcpp;

ParseTreePatternMatcher::CannotInvokeStartRule::CannotInvokeStartRule(const RuntimeException &e) : RuntimeException(e.what()) {
}

ParseTreePatternMatcher::ParseTreePatternMatcher(Lexer *lexer, Parser *parser) : _lexer(lexer), _parser(parser) {
  InitializeInstanceFields();
}

void ParseTreePatternMatcher::setDelimiters(const std::wstring &start, const std::wstring &stop, const std::wstring &escapeLeft) {
  if (start.empty()) {
    throw IllegalArgumentException("start cannot be null or empty");
  }

  if (stop.empty()) {
    throw IllegalArgumentException("stop cannot be null or empty");
  }

 _start = start;
  _stop = stop;
  _escape = escapeLeft;
}

bool ParseTreePatternMatcher::matches(Ref<ParseTree> tree, const std::wstring &pattern, int patternRuleIndex) {
  ParseTreePattern p = compile(pattern, patternRuleIndex);
  return matches(tree, p);
}

bool ParseTreePatternMatcher::matches(Ref<ParseTree> tree, const ParseTreePattern &pattern) {
  std::map<std::wstring, std::vector<Ref<ParseTree>>> labels;
  Ref<ParseTree> mismatchedNode = matchImpl(tree, pattern.getPatternTree(), labels);
  return mismatchedNode == nullptr;
}

ParseTreeMatch ParseTreePatternMatcher::match(Ref<ParseTree> tree, const std::wstring &pattern, int patternRuleIndex) {
  ParseTreePattern p = compile(pattern, patternRuleIndex);
  return match(tree, p);
}

ParseTreeMatch ParseTreePatternMatcher::match(Ref<ParseTree> tree, const ParseTreePattern &pattern) {
  std::map<std::wstring, std::vector<Ref<ParseTree>>> labels;
  Ref<tree::ParseTree> mismatchedNode = matchImpl(tree, pattern.getPatternTree(), labels);
  return ParseTreeMatch(tree, pattern, labels, mismatchedNode);
}

ParseTreePattern ParseTreePatternMatcher::compile(const std::wstring &pattern, int patternRuleIndex) {
  std::vector<Ref<Token>> tokenList = tokenize(pattern);

  ListTokenSource *tokenSrc = new ListTokenSource(tokenList); /* mem-check: deleted in finally block */
  CommonTokenStream *tokens = new CommonTokenStream(tokenSrc); /* mem-check: deleted in finally block */
  auto onExit = finally([tokenSrc, tokens]() {
    delete tokenSrc;
    delete tokens;
  });

  ParserInterpreter parserInterp(_parser->getGrammarFileName(), _parser->getVocabulary(),
                                 _parser->getRuleNames(), _parser->getATNWithBypassAlts(), tokens);

  Ref<ParserRuleContext> tree;
  try {
    parserInterp.setErrorHandler(std::make_shared<BailErrorStrategy>());
    tree = parserInterp.parse(patternRuleIndex);
  } catch (ParseCancellationException &e) {
#if defined(_MSC_FULL_VER) && _MSC_FULL_VER < 190023026
    // rethrow_if_nested is not available before VS 2015.
    throw e;
#else
    std::rethrow_if_nested(e); // Unwrap the nested exception.
#endif
  } catch (RecognitionException &re) {
    throw re;
  } catch (std::exception &e) {
#if defined(_MSC_FULL_VER) && _MSC_FULL_VER < 190023026
    // throw_with_nested is not available before VS 2015.
    throw e;
#else
    std::throw_with_nested("Cannot invoke start rule"); // Wrap any other exception. We should however probably use one of the ANTLR exceptions here.
#endif
  }

  // Make sure tree pattern compilation checks for a complete parse
  if (tokens->LA(1) != EOF) {
    throw StartRuleDoesNotConsumeFullPattern();
  }
  
  return ParseTreePattern(this, pattern, patternRuleIndex, tree);
}

Lexer* ParseTreePatternMatcher::getLexer() {
  return _lexer;
}

Parser* ParseTreePatternMatcher::getParser() {
  return _parser;
}

Ref<ParseTree> ParseTreePatternMatcher::matchImpl(Ref<ParseTree> tree,
  Ref<ParseTree> patternTree, std::map<std::wstring, std::vector<Ref<ParseTree>>> &labels) {
  if (tree == nullptr) {
    throw IllegalArgumentException("tree cannot be null");
  }

  if (patternTree == nullptr) {
    throw IllegalArgumentException("patternTree cannot be null");
  }

  // x and <ID>, x and y, or x and x; or could be mismatched types
  if (is<TerminalNode>(tree) && is<TerminalNode>(patternTree)) {
    Ref<TerminalNode> t1 = std::static_pointer_cast<TerminalNode>(tree);
    Ref<TerminalNode> t2 = std::static_pointer_cast<TerminalNode>(patternTree);

    Ref<ParseTree> mismatchedNode;
    // both are tokens and they have same type
    if (t1->getSymbol()->getType() == t2->getSymbol()->getType()) {
      if (is<TokenTagToken>(t2->getSymbol())) { // x and <ID>
        Ref<TokenTagToken> tokenTagToken = std::dynamic_pointer_cast<TokenTagToken>(t2->getSymbol());

        // track label->list-of-nodes for both token name and label (if any)
        labels[tokenTagToken->getTokenName()].push_back(tree);
        if (tokenTagToken->getLabel() != L"") {
          labels[tokenTagToken->getLabel()].push_back(tree);
        }
      } else if (t1->getText() == t2->getText()) {
        // x and x
      } else {
        // x and y
        if (mismatchedNode == nullptr) {
          mismatchedNode = t1;
        }
      }
    } else {
      if (mismatchedNode == nullptr) {
        mismatchedNode = t1;
      }
    }

    return mismatchedNode;
  }

  if (is<ParserRuleContext>(tree) && is<ParserRuleContext>(patternTree)) {
    Ref<ParserRuleContext> r1 = std::dynamic_pointer_cast<ParserRuleContext>(tree);
    Ref<ParserRuleContext> r2 = std::dynamic_pointer_cast<ParserRuleContext>(patternTree);
    Ref<ParseTree> mismatchedNode;

    // (expr ...) and <expr>
    Ref<RuleTagToken> ruleTagToken = getRuleTagToken(r2);
    if (ruleTagToken != nullptr) {
      //ParseTreeMatch *m = nullptr; // unused?
      if (r1->RuleContext::getRuleContext()->getRuleIndex() == r2->RuleContext::getRuleContext()->getRuleIndex()) {
        // track label->list-of-nodes for both rule name and label (if any)
        labels[ruleTagToken->getRuleName()].push_back(tree);
        if (ruleTagToken->getLabel() != L"") {
          labels[ruleTagToken->getLabel()].push_back(tree);
        }
      } else {
        if (!mismatchedNode) {
          mismatchedNode = r1;
        }
      }

      return mismatchedNode;
    }

    // (expr ...) and (expr ...)
    if (r1->getChildCount() != r2->getChildCount()) {
      if (mismatchedNode == nullptr) {
        mismatchedNode = r1;
      }

      return mismatchedNode;
    }

    std::size_t n = r1->getChildCount();
    for (size_t i = 0; i < n; i++) {
      Ref<ParseTree> childMatch = matchImpl(r1->getChild(i), patternTree->getChild(i), labels);
      if (childMatch) {
        return childMatch;
      }
    }

    return mismatchedNode;
  }

  // if nodes aren't both tokens or both rule nodes, can't match
  return tree;
}

Ref<RuleTagToken> ParseTreePatternMatcher::getRuleTagToken(Ref<ParseTree> t) {
  if (is<RuleNode>(t)) {
    Ref<RuleNode> r = std::dynamic_pointer_cast<RuleNode>(t);
    if (r->getChildCount() == 1 && is<TerminalNode>(r->getChild(0))) {
      Ref<TerminalNode> c = std::dynamic_pointer_cast<TerminalNode>(r->getChild(0));
      if (is<RuleTagToken>(c->getSymbol())) {
        return std::dynamic_pointer_cast<RuleTagToken>(c->getSymbol());
      }
    }
  }
  return nullptr;
}

std::vector<Ref<Token>> ParseTreePatternMatcher::tokenize(const std::wstring &pattern) {
  // split pattern into chunks: sea (raw input) and islands (<ID>, <expr>)
  std::vector<Chunk> chunks = split(pattern);

  // create token stream from text and tags
  std::vector<Ref<Token>> tokens;
  for (auto chunk : chunks) {
    if (is<TagChunk>(chunk)) {
      TagChunk &tagChunk = (TagChunk&)chunk;
      // add special rule token or conjure up new token from name
      if (isupper(tagChunk.getTag()[0])) {
        size_t ttype = _parser->getTokenType(tagChunk.getTag());
        if (ttype == Token::INVALID_TYPE) {
          throw IllegalArgumentException("Unknown token " + antlrcpp::ws2s(tagChunk.getTag()) +
                                         " in pattern: " + antlrcpp::ws2s(pattern));
        }
        Ref<TokenTagToken> t = std::make_shared<TokenTagToken>(tagChunk.getTag(), (int)ttype, tagChunk.getLabel());
        tokens.push_back(t);
      } else if (islower(tagChunk.getTag()[0])) {
        ssize_t ruleIndex = _parser->getRuleIndex(tagChunk.getTag());
        if (ruleIndex == -1) {
          throw IllegalArgumentException(std::string("Unknown rule ") + antlrcpp::ws2s(tagChunk.getTag()) + " in pattern: " + antlrcpp::ws2s(pattern));
        }
        int ruleImaginaryTokenType = _parser->getATNWithBypassAlts().ruleToTokenType[(size_t)ruleIndex];
        tokens.push_back(std::make_shared<RuleTagToken>(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel()));
      } else {
        throw IllegalArgumentException(std::string("invalid tag: ") + antlrcpp::ws2s(tagChunk.getTag()) + " in pattern: " + antlrcpp::ws2s(pattern));
      }
    } else {
      TextChunk &textChunk = (TextChunk&)chunk;
      ANTLRInputStream input(textChunk.getText());
      _lexer->setInputStream(&input);
      Ref<Token> t = _lexer->nextToken();
      while (t->getType() != EOF) {
        tokens.push_back(t);
        t = _lexer->nextToken();
      }
      _lexer->setInputStream(nullptr);
    }
  }

  return tokens;
}

std::vector<Chunk> ParseTreePatternMatcher::split(const std::wstring &pattern) {
  size_t p = 0;
  size_t n = pattern.length();
  std::vector<Chunk> chunks;
  
  // find all start and stop indexes first, then collect
  std::vector<size_t> starts;
  std::vector<size_t> stops;
  while (p < n) {
    if (p == pattern.find(_escape + _start,p)) {
      p += _escape.length() + _start.length();
    } else if (p == pattern.find(_escape + _stop,p)) {
      p += _escape.length() + _stop.length();
    } else if (p == pattern.find(_start,p)) {
      starts.push_back(p);
      p += _start.length();
    } else if (p == pattern.find(_stop,p)) {
      stops.push_back(p);
      p += _stop.length();
    } else {
      p++;
    }
  }

  if (starts.size() > stops.size()) {
    throw IllegalArgumentException(std::string("unterminated tag in pattern: ") + antlrcpp::ws2s(pattern));
  }

  if (starts.size() < stops.size()) {
    throw IllegalArgumentException(std::string("missing start tag in pattern: ") + antlrcpp::ws2s(pattern));
  }

  size_t ntags = starts.size();
  for (size_t i = 0; i < ntags; i++) {
    if (starts[i] >= stops[i]) {
      throw IllegalArgumentException(std::string("tag delimiters out of order in pattern: ") + antlrcpp::ws2s(pattern));
    }
  }

  // collect into chunks now
  if (ntags == 0) {
    std::wstring text = pattern.substr(0, n);
    chunks.push_back(TextChunk(text));
  }

  if (ntags > 0 && starts[0] > 0) { // copy text up to first tag into chunks
    std::wstring text = pattern.substr(0, starts[0]);
    chunks.push_back(TextChunk(text));
  }
  for (size_t i = 0; i < ntags; i++) {
    // copy inside of <tag>
    std::wstring tag = pattern.substr(starts[i] + _start.length(), stops[i] - (starts[i] + _start.length()));
    std::wstring ruleOrToken = tag;
    std::wstring label = L"";
    size_t colon = tag.find(L':');
    if (colon != std::wstring::npos) {
      label = tag.substr(0,colon);
      ruleOrToken = tag.substr(colon + 1, tag.length() - (colon + 1));
    }
    chunks.push_back(TagChunk(label, ruleOrToken));
    if (i + 1 < ntags) {
      // copy from end of <tag> to start of next
      std::wstring text = pattern.substr(stops[i] + _stop.length(), starts[i + 1] - (stops[i] + _stop.length()));
      chunks.push_back(TextChunk(text));
    }
  }
  if (ntags > 0) {
    size_t afterLastTag = stops[ntags - 1] + _stop.length();
    if (afterLastTag < n) { // copy text from end of last tag to end
      std::wstring text = pattern.substr(afterLastTag, n - afterLastTag);
      chunks.push_back(TextChunk(text));
    }
  }

  // strip out all backslashes from text chunks but not tags
  for (size_t i = 0; i < chunks.size(); i++) {
    Chunk &c = chunks[i];
    if (is<TextChunk>(c)) {
      TextChunk &tc = (TextChunk&)c;
      std::wstring unescaped = tc.getText();
      unescaped.erase(std::remove(unescaped.begin(), unescaped.end(), L'\\'), unescaped.end());
      if (unescaped.length() < tc.getText().length()) {
        chunks[i] = TextChunk(unescaped);
      }
    }
  }

  return chunks;
}

void ParseTreePatternMatcher::InitializeInstanceFields() {
  _start = L"<";
  _stop = L">";
  _escape = L"\\";
}
