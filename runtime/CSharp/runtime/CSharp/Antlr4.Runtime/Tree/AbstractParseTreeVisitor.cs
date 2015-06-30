/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;

namespace Antlr4.Runtime.Tree
{
    public abstract class AbstractParseTreeVisitor<Result> : IParseTreeVisitor<Result>
    {
        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation calls
        /// <see cref="IParseTree.Accept{T}(IParseTreeVisitor{T})"/>
        /// on the
        /// specified tree.</p>
        /// </summary>
        public virtual Result Visit(IParseTree tree)
        {
            return tree.Accept(this);
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation initializes the aggregate result to
        /// <see cref="AbstractParseTreeVisitor{Result}.DefaultResult()">defaultResult()</see>
        /// . Before visiting each child, it
        /// calls
        /// <see cref="AbstractParseTreeVisitor{Result}.ShouldVisitNextChild(IRuleNode, Result)">shouldVisitNextChild</see>
        /// ; if the result
        /// is
        /// <see langword="false"/>
        /// no more children are visited and the current aggregate
        /// result is returned. After visiting a child, the aggregate result is
        /// updated by calling
        /// <see cref="AbstractParseTreeVisitor{Result}.AggregateResult(Result, Result)">aggregateResult</see>
        /// with the
        /// previous aggregate result and the result of visiting the child.</p>
        /// <p>The default implementation is not safe for use in visitors that modify
        /// the tree structure. Visitors that modify the tree should override this
        /// method to behave properly in respect to the specific algorithm in use.</p>
        /// </summary>
        public virtual Result VisitChildren(IRuleNode node)
        {
            Result result = DefaultResult;
            int n = node.ChildCount;
            for (int i = 0; i < n; i++)
            {
                if (!ShouldVisitNextChild(node, result))
                {
                    break;
                }
                IParseTree c = node.GetChild(i);
                Result childResult = c.Accept(this);
                result = AggregateResult(result, childResult);
            }
            return result;
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation returns the result of
        /// <see cref="AbstractParseTreeVisitor{Result}.DefaultResult()">defaultResult</see>
        /// .</p>
        /// </summary>
        public virtual Result VisitTerminal(ITerminalNode node)
        {
            return DefaultResult;
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation returns the result of
        /// <see cref="AbstractParseTreeVisitor{Result}.DefaultResult()">defaultResult</see>
        /// .</p>
        /// </summary>
        public virtual Result VisitErrorNode(IErrorNode node)
        {
            return DefaultResult;
        }

        /// <summary>Gets the default value returned by visitor methods.</summary>
        /// <remarks>
        /// Gets the default value returned by visitor methods. This value is
        /// returned by the default implementations of
        /// <see cref="AbstractParseTreeVisitor{Result}.VisitTerminal(ITerminalNode)">visitTerminal</see>
        /// ,
        /// <see cref="AbstractParseTreeVisitor{Result}.VisitErrorNode(IErrorNode)">visitErrorNode</see>
        /// .
        /// The default implementation of
        /// <see cref="AbstractParseTreeVisitor{Result}.VisitChildren(IRuleNode)">visitChildren</see>
        /// initializes its aggregate result to this value.
        /// <p>The base implementation returns
        /// <see langword="null"/>
        /// .</p>
        /// </remarks>
        /// <returns>The default value returned by visitor methods.</returns>
        protected internal virtual Result DefaultResult
        {
            get
            {
                return default(Result);
            }
        }

        /// <summary>Aggregates the results of visiting multiple children of a node.</summary>
        /// <remarks>
        /// Aggregates the results of visiting multiple children of a node. After
        /// either all children are visited or
        /// <see cref="AbstractParseTreeVisitor{Result}.ShouldVisitNextChild(IRuleNode, Result)"/>
        /// returns
        /// <see langword="false"/>
        /// , the aggregate value is returned as the result of
        /// <see cref="AbstractParseTreeVisitor{Result}.VisitChildren(IRuleNode)"/>
        /// .
        /// <p>The default implementation returns
        /// <paramref name="nextResult"/>
        /// , meaning
        /// <see cref="AbstractParseTreeVisitor{Result}.VisitChildren(IRuleNode)"/>
        /// will return the result of the last child visited
        /// (or return the initial value if the node has no children).</p>
        /// </remarks>
        /// <param name="aggregate">
        /// The previous aggregate value. In the default
        /// implementation, the aggregate value is initialized to
        /// <see cref="AbstractParseTreeVisitor{Result}.DefaultResult()"/>
        /// , which is passed as the
        /// <paramref name="aggregate"/>
        /// argument
        /// to this method after the first child node is visited.
        /// </param>
        /// <param name="nextResult">
        /// The result of the immediately preceeding call to visit
        /// a child node.
        /// </param>
        /// <returns>The updated aggregate result.</returns>
        protected internal virtual Result AggregateResult(Result aggregate, Result nextResult)
        {
            return nextResult;
        }

        /// <summary>
        /// This method is called after visiting each child in
        /// <see cref="AbstractParseTreeVisitor{Result}.VisitChildren(IRuleNode)"/>
        /// . This method is first called before the first
        /// child is visited; at that point
        /// <paramref name="currentResult"/>
        /// will be the initial
        /// value (in the default implementation, the initial value is returned by a
        /// call to
        /// <see cref="AbstractParseTreeVisitor{Result}.DefaultResult()"/>
        /// . This method is not called after the last
        /// child is visited.
        /// <p>The default implementation always returns
        /// <see langword="true"/>
        /// , indicating that
        /// <c>visitChildren</c>
        /// should only return after all children are visited.
        /// One reason to override this method is to provide a "short circuit"
        /// evaluation option for situations where the result of visiting a single
        /// child has the potential to determine the result of the visit operation as
        /// a whole.</p>
        /// </summary>
        /// <param name="node">
        /// The
        /// <see cref="IRuleNode"/>
        /// whose children are currently being
        /// visited.
        /// </param>
        /// <param name="currentResult">
        /// The current aggregate result of the children visited
        /// to the current point.
        /// </param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// to continue visiting children. Otherwise return
        /// <see langword="false"/>
        /// to stop visiting children and immediately return the
        /// current aggregate result from
        /// <see cref="AbstractParseTreeVisitor{Result}.VisitChildren(IRuleNode)"/>
        /// .
        /// </returns>
        protected internal virtual bool ShouldVisitNextChild(IRuleNode node, Result currentResult)
        {
            return true;
        }
    }
}
