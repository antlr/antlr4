/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "Exceptions.h"

#include "tree/pattern/ParseTreeMatch.h"

using namespace antlr4::tree;
using namespace antlr4::tree::pattern;

ParseTreeMatch::ParseTreeMatch(Ref<ParseTree> tree, const ParseTreePattern &pattern,
                               const std::map<std::string, std::vector<Ref<ParseTree>>> &labels,
                               Ref<ParseTree> const& mismatchedNode)
  : _tree(tree), _pattern(pattern), _labels(labels), _mismatchedNode(mismatchedNode) {
  if (tree == nullptr) {
    throw IllegalArgumentException("tree cannot be nul");
  }
}

Ref<ParseTree> ParseTreeMatch::get(const std::string &label) {
  auto iterator = _labels.find(label);
  if (iterator == _labels.end() || iterator->second.empty()) {
    return nullptr;
  }

  return iterator->second.back(); // return last if multiple
}

std::vector<Ref<ParseTree>> ParseTreeMatch::getAll(const std::string &label) {
  auto iterator = _labels.find(label);
  if (iterator == _labels.end()) {
    return std::vector<Ref<ParseTree>>();
  }

  return iterator->second;
}

std::map<std::string, std::vector<Ref<ParseTree>>>& ParseTreeMatch::getLabels() {
  return _labels;
}

Ref<ParseTree> ParseTreeMatch::getMismatchedNode() {
  return _mismatchedNode;
}

bool ParseTreeMatch::succeeded() {
  return _mismatchedNode == nullptr;
}

const ParseTreePattern& ParseTreeMatch::getPattern() {
  return _pattern;
}

Ref<ParseTree>  ParseTreeMatch::getTree() {
  return _tree;
}

std::string ParseTreeMatch::toString() {
  if (succeeded()) {
    return "Match succeeded; found " + std::to_string(_labels.size()) + " labels";
  } else {
    return "Match failed; found " + std::to_string(_labels.size()) + " labels";
  }
}
