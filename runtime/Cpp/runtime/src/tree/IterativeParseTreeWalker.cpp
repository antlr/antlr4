/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

#include "support/CPPUtils.h"

#include "tree/ParseTreeListener.h"
#include "tree/ParseTree.h"
#include "tree/ErrorNode.h"

#include "IterativeParseTreeWalker.h"

using namespace antlr4::tree;

void IterativeParseTreeWalker::walk(ParseTreeListener *listener, ParseTree *t) const {

  std::vector<ParseTree *> nodeStack;
  std::vector<size_t> indexStack;

  ParseTree *currentNode = t;
  size_t currentIndex = 0;

  while (currentNode != nullptr) {
    // pre-order visit
    if (antlrcpp::is<ErrorNode *>(currentNode)) {
      listener->visitErrorNode(dynamic_cast<ErrorNode *>(currentNode));
    } else if (antlrcpp::is<TerminalNode *>(currentNode)) {
      listener->visitTerminal((TerminalNode *)currentNode);
    } else {
      enterRule(listener, currentNode);
    }

    // Move down to first child, if it exists.
    if (!currentNode->children.empty()) {
      nodeStack.push_back(currentNode);
      indexStack.push_back(currentIndex);
      currentIndex = 0;
      currentNode = currentNode->children[0];
      continue;
    }

    // No child nodes, so walk tree.
    do {
      // post-order visit
      if (!antlrcpp::is<TerminalNode *>(currentNode)) {
        exitRule(listener, currentNode);
      }

      // No parent, so no siblings.
      if (nodeStack.empty()) {
        currentNode = nullptr;
        currentIndex = 0;
        break;
      }

      // Move to next sibling if possible.
      if (nodeStack.back()->children.size() > ++currentIndex) {
        currentNode = nodeStack.back()->children[currentIndex];
        break;
      }

      // No next sibling, so move up.
      currentNode = nodeStack.back();
      nodeStack.pop_back();
      currentIndex = indexStack.back();
      indexStack.pop_back();

    } while (currentNode != nullptr);
  }
}
