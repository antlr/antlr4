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

#include "antlr4-common.h"

namespace antlr4 {
namespace tree {

  /// <summary>
  /// This interface defines the basic notion of a parse tree visitor. Generated
  /// visitors implement this interface and the {@code XVisitor} interface for
  /// grammar {@code X}.
  /// </summary>
  /// @param <T> The return type of the visit operation. Use <seealso cref="Void"/> for
  /// operations with no return type. </param>
  template<typename T>
  class ANTLR4CPP_PUBLIC ParseTreeVisitor {

    /// <summary>
    /// Visit a parse tree, and return a user-defined result of the operation.
    /// </summary>
    /// <param name="tree"> The <seealso cref="ParseTree"/> to visit. </param>
    /// <returns> The result of visiting the parse tree. </returns>
  public:
    virtual T* visit(ParseTree *tree) = 0;

    /// <summary>
    /// Visit the children of a node, and return a user-defined result of the
    /// operation.
    /// </summary>
    /// <param name="node"> The <seealso cref="RuleNode"/> whose children should be visited. </param>
    /// <returns> The result of visiting the children of the node. </returns>
    virtual T* visitChildren(RuleNode *node) = 0;

    /// <summary>
    /// Visit a terminal node, and return a user-defined result of the operation.
    /// </summary>
    /// <param name="node"> The <seealso cref="TerminalNode"/> to visit. </param>
    /// <returns> The result of visiting the node. </returns>
    virtual T* visitTerminal(TerminalNode *node) = 0;

    /// <summary>
    /// Visit an error node, and return a user-defined result of the operation.
    /// </summary>
    /// <param name="node"> The <seealso cref="ErrorNode"/> to visit. </param>
    /// <returns> The result of visiting the node. </returns>
    virtual T* visitErrorNode(ErrorNode *node) = 0;

  };

} // namespace tree
} // namespace antlr4
