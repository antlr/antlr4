/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


open class AbstractParseTreeVisitor<T>: ParseTreeVisitor<T> {
    public override init() {
        super.init()

    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation calls {@link org.antlr.v4.runtime.tree.ParseTree#accept} on the
     * specified tree.</p>
     */

    open override func visit(_ tree: ParseTree) -> T? {
        return tree.accept(self)
    }

    /**
     * {@inheritDoc}
     *
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

    open override func visitChildren(_ node: RuleNode) -> T? {
        var result: T? = defaultResult()
        let n: Int = node.getChildCount()

        for i in 0..<n {
            if !shouldVisitNextChild(node, result) {
                break
            }

            let c: ParseTree? = node.getChild(i) as? ParseTree
            let childResult: T? = c?.accept(self)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of
     * {@link #defaultResult defaultResult}.</p>
     */

    open override func visitTerminal(_ node: TerminalNode) -> T? {
        return defaultResult()
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of
     * {@link #defaultResult defaultResult}.</p>
     */
    override
    open func visitErrorNode(_ node: ErrorNode) -> T? {
        return defaultResult()
    }

    /**
     * Gets the default value returned by visitor methods. This value is
     * returned by the default implementations of
     * {@link #visitTerminal visitTerminal}, {@link #visitErrorNode visitErrorNode}.
     * The default implementation of {@link #visitChildren visitChildren}
     * initializes its aggregate result to this value.
     *
     * <p>The base implementation returns {@code null}.</p>
     *
     * @return The default value returned by visitor methods.
     */
    internal func defaultResult() -> T? {
        return nil
    }

    /**
     * Aggregates the results of visiting multiple children of a node. After
     * either all children are visited or {@link #shouldVisitNextChild} returns
     * {@code false}, the aggregate value is returned as the result of
     * {@link #visitChildren}.
     *
     * <p>The default implementation returns {@code nextResult}, meaning
     * {@link #visitChildren} will return the result of the last child visited
     * (or return the initial value if the node has no children).</p>
     *
     * @param aggregate The previous aggregate value. In the default
     * implementation, the aggregate value is initialized to
     * {@link #defaultResult}, which is passed as the {@code aggregate} argument
     * to this method after the first child node is visited.
     * @param nextResult The result of the immediately preceeding call to visit
     * a child node.
     *
     * @return The updated aggregate result.
     */
    internal func aggregateResult(_ aggregate: T?, _ nextResult: T?) -> T? {
        return nextResult
    }

    /**
     * This method is called after visiting each child in
     * {@link #visitChildren}. This method is first called before the first
     * child is visited; at that point {@code currentResult} will be the initial
     * value (in the default implementation, the initial value is returned by a
     * call to {@link #defaultResult}. This method is not called after the last
     * child is visited.
     *
     * <p>The default implementation always returns {@code true}, indicating that
     * {@code visitChildren} should only return after all children are visited.
     * One reason to override this method is to provide a "short circuit"
     * evaluation option for situations where the result of visiting a single
     * child has the potential to determine the result of the visit operation as
     * a whole.</p>
     *
     * @param node The {@link org.antlr.v4.runtime.tree.RuleNode} whose children are currently being
     * visited.
     * @param currentResult The current aggregate result of the children visited
     * to the current point.
     *
     * @return {@code true} to continue visiting children. Otherwise return
     * {@code false} to stop visiting children and immediately return the
     * current aggregate result from {@link #visitChildren}.
     */
    internal func shouldVisitNextChild(_ node: RuleNode, _ currentResult: T?) -> Bool {
        return true
    }

}
