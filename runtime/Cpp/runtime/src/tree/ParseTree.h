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

#include "tree/SyntaxTree.h"

namespace antlr4 {
namespace tree {

  /// <summary>
  /// An interface to access the tree of <seealso cref="RuleContext"/> objects created
  ///  during a parse that makes the data structure look like a simple parse tree.
  ///  This node represents both internal nodes, rule invocations,
  ///  and leaf nodes, token matches.
  /// <p/>
  ///  The payload is either a <seealso cref="Token"/> or a <seealso cref="RuleContext"/> object.
  /// </summary>
  class ANTLR4CPP_PUBLIC ParseTree : public SyntaxTree {
    // the following methods narrow the return type; they are not additional methods
  public:
    std::weak_ptr<ParseTree> getParent() { return std::dynamic_pointer_cast<ParseTree>(getParentReference().lock()); };
    virtual Ref<ParseTree> getChild(size_t i) { return std::dynamic_pointer_cast<ParseTree>(getChildReference(i)); };

    /// <summary>
    /// The <seealso cref="ParseTreeVisitor"/> needs a double dispatch method. </summary>
    template<typename T, typename T1>
    T *accept(ParseTreeVisitor<T1> *visitor);

    /// <summary>
    /// Return the combined text of all leaf nodes. Does not get any
    ///  off-channel tokens (if any) so won't return whitespace and
    ///  comments if they are sent to parser on hidden channel.
    /// </summary>
    virtual std::string getText() = 0;

    /// <summary>
    /// Specialize toStringTree so that it can print out more information
    /// 	based upon the parser.
    /// </summary>
    virtual std::string toStringTree(Parser *parser) = 0;
  };

} // namespace tree
} // namespace antlr4
