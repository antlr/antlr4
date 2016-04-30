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

#pragma once

#include "TerminalNode.h"
#include "ParserRuleContext.h"
#include "Recognizer.h"
#include "Token.h"

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace tree {

  /// A set of utility routines useful for all kinds of ANTLR trees.
  class Trees {
  public:
    /// Print out a whole tree in LISP form. getNodeText is used on the
    /// node payloads to get the text for the nodes.  Detect
    /// parse trees and extract data appropriately.
    static std::wstring toStringTree(Ref<Tree> t);

    /// Print out a whole tree in LISP form. getNodeText is used on the
    ///  node payloads to get the text for the nodes.  Detect
    ///  parse trees and extract data appropriately.
    static std::wstring toStringTree(Ref<Tree> t, Parser *recog);

    /// Print out a whole tree in LISP form. getNodeText is used on the
    /// node payloads to get the text for the nodes.  Detect
    /// parse trees and extract data appropriately.
    static std::wstring toStringTree(Ref<Tree> t, const std::vector<std::wstring> &ruleNames);
    static std::wstring getNodeText(Ref<Tree> t, Parser *recog);
    static std::wstring getNodeText(Ref<Tree> t, const std::vector<std::wstring> &ruleNames);

    /// Return ordered list of all children of this node.
    static std::vector<Ref<Tree>> getChildren(Ref<Tree> t);

    /// Return a list of all ancestors of this node.  The first node of
    ///  list is the root and the last is the parent of this node.
    static std::vector<std::weak_ptr<Tree>> getAncestors(Ref<Tree> t);
    static std::vector<Ref<ParseTree>> findAllTokenNodes(Ref<ParseTree> t, int ttype);
    static std::vector<Ref<ParseTree>> findAllRuleNodes(Ref<ParseTree> t, int ruleIndex);
    static std::vector<Ref<ParseTree>> findAllNodes(Ref<ParseTree> t, int index, bool findTokens);

    static std::vector<Ref<ParseTree>> descendants(Ref<ParseTree> t);

  private:
    Trees();
  };

} // namespace tree
} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org
