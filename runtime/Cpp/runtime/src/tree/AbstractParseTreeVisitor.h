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

#include "tree/ParseTreeVisitor.h"
#include "tree/RuleNode.h"

namespace antlr4 {
namespace tree {

  template<typename T>
  class ANTLR4CPP_PUBLIC AbstractParseTreeVisitor : public ParseTreeVisitor<T> {
    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The default implementation calls <seealso cref="ParseTree#accept"/> on the
    /// specified tree.
    /// </summary>
  public:
    virtual T* visit(ParseTree *tree) override {
      return tree->accept(this);
    }

    /**
     * <p>The default implementation initializes the aggregate result to
     * {@link #defaultResult defaultResult()}. Before visiting each child, it
     * calls {@link #shouldVisitNextChild shouldVisitNextChild}; if the result
     * is {@code false} no more children are visited and the current aggregate
     * result is returned. After visiting a child, the aggregate result is
     * updated by calling {@link #aggregateResult aggregateResult} with the
     * previous aggregate result and the result of visiting the child.</p>
     *
     * <p>The default implementation is not safe for use in visitors that modify
     * the tree structure. Visitors that modify the tree should override this
     * method to behave properly in respect to the specific algorithm in use.</p>
     */
    virtual T* visitChildren(RuleNode *node) override {
      T* result = defaultResult();
      size_t n = node->getChildCount();
      for (size_t i = 0; i < n; i++) {
        if (!shouldVisitNextChild(node, result)) {
          break;
        }

        Ref<ParseTree> c = node->getChild(i);
        T childResult = c->accept(this);
        result = aggregateResult(result, childResult);
      }

      return result;
    }

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The default implementation returns the result of
    /// <seealso cref="#defaultResult defaultResult"/>.
    /// </summary>
    virtual T* visitTerminal(TerminalNode * /*node*/) override {
      return defaultResult();
    }

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The default implementation returns the result of
    /// <seealso cref="#defaultResult defaultResult"/>.
    /// </summary>
    virtual T* visitErrorNode(ErrorNode * /*node*/) override {
      return defaultResult();
    }

    /// <summary>
    /// Gets the default value returned by visitor methods. This value is
    /// returned by the default implementations of
    /// <seealso cref="#visitTerminal visitTerminal"/>, <seealso cref="#visitErrorNode visitErrorNode"/>.
    /// The default implementation of <seealso cref="#visitChildren visitChildren"/>
    /// initializes its aggregate result to this value.
    /// <p/>
    /// The base implementation returns {@code null}.
    /// </summary>
    /// <returns> The default value returned by visitor methods. </returns>
  protected:
    virtual T* defaultResult() {
      return nullptr;
    }

    /// <summary>
    /// Aggregates the results of visiting multiple children of a node. After
    /// either all children are visited or <seealso cref="#shouldVisitNextChild"/> returns
    /// {@code false}, the aggregate value is returned as the result of
    /// <seealso cref="#visitChildren"/>.
    /// <p/>
    /// The default implementation returns {@code nextResult}, meaning
    /// <seealso cref="#visitChildren"/> will return the result of the last child visited
    /// (or return the initial value if the node has no children).
    /// </summary>
    /// <param name="aggregate"> The previous aggregate value. In the default
    /// implementation, the aggregate value is initialized to
    /// <seealso cref="#defaultResult"/>, which is passed as the {@code aggregate} argument
    /// to this method after the first child node is visited. </param>
    /// <param name="nextResult"> The result of the immediately preceeding call to visit
    /// a child node.
    /// </param>
    /// <returns> The updated aggregate result. </returns>
    virtual T* aggregateResult(T* /*aggregate*/, T* nextResult) {
      return nextResult;
    }

    /// <summary>
    /// This method is called after visiting each child in
    /// <seealso cref="#visitChildren"/>. This method is first called before the first
    /// child is visited; at that point {@code currentResult} will be the initial
    /// value (in the default implementation, the initial value is returned by a
    /// call to <seealso cref="#defaultResult"/>. This method is not called after the last
    /// child is visited.
    /// <p/>
    /// The default implementation always returns {@code true}, indicating that
    /// {@code visitChildren} should only return after all children are visited.
    /// One reason to override this method is to provide a "short circuit"
    /// evaluation option for situations where the result of visiting a single
    /// child has the potential to determine the result of the visit operation as
    /// a whole.
    /// </summary>
    /// <param name="node"> The <seealso cref="RuleNode"/> whose children are currently being
    /// visited. </param>
    /// <param name="currentResult"> The current aggregate result of the children visited
    /// to the current point.
    /// </param>
    /// <returns> {@code true} to continue visiting children. Otherwise return
    /// {@code false} to stop visiting children and immediately return the
    /// current aggregate result from <seealso cref="#visitChildren"/>. </returns>
    virtual bool shouldVisitNextChild(RuleNode * /*node*/, T /*currentResult*/) {
      return true;
    }

  };

} // namespace tree
} // namespace antlr4
