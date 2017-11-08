/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


open class AbstractParseTreeVisitor<T>: ParseTreeVisitor<T> {
    public override init() {
        super.init()
    }

    /// 
    /// The default implementation calls _org.antlr.v4.runtime.tree.ParseTree#accept_ on the
    /// specified tree.
    /// 
    open override func visit(_ tree: ParseTree) -> T? {
        return tree.accept(self)
    }

    ///
    /// The default implementation initializes the aggregate result to
    /// _#defaultResult defaultResult()_. Before visiting each child, it
    /// calls _#shouldVisitNextChild shouldVisitNextChild_; if the result
    /// is `false` no more children are visited and the current aggregate
    /// result is returned. After visiting a child, the aggregate result is
    /// updated by calling _#aggregateResult aggregateResult_ with the
    /// previous aggregate result and the result of visiting the child.
    /// 
    /// The default implementation is not safe for use in visitors that modify
    /// the tree structure. Visitors that modify the tree should override this
    /// method to behave properly in respect to the specific algorithm in use.
    /// 
    open override func visitChildren(_ node: RuleNode) -> T? {
        var result: T? = defaultResult()
        let n = node.getChildCount()

        for i in 0..<n {
            if !shouldVisitNextChild(node, result) {
                break
            }

            let c = node[i]
            let childResult = c.accept(self)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    ///
    /// The default implementation returns the result of
    /// _#defaultResult defaultResult_.
    /// 
    open override func visitTerminal(_ node: TerminalNode) -> T? {
        return defaultResult()
    }

    ///
    /// The default implementation returns the result of
    /// _#defaultResult defaultResult_.
    /// 
    override
    open func visitErrorNode(_ node: ErrorNode) -> T? {
        return defaultResult()
    }

    /// 
    /// Gets the default value returned by visitor methods. This value is
    /// returned by the default implementations of
    /// _#visitTerminal visitTerminal_, _#visitErrorNode visitErrorNode_.
    /// The default implementation of _#visitChildren visitChildren_
    /// initializes its aggregate result to this value.
    /// 
    /// The base implementation returns `null`.
    /// 
    /// - Returns: The default value returned by visitor methods.
    /// 
    open func defaultResult() -> T? {
        return nil
    }

    /// 
    /// Aggregates the results of visiting multiple children of a node. After
    /// either all children are visited or _#shouldVisitNextChild_ returns
    /// `false`, the aggregate value is returned as the result of
    /// _#visitChildren_.
    /// 
    /// The default implementation returns `nextResult`, meaning
    /// _#visitChildren_ will return the result of the last child visited
    /// (or return the initial value if the node has no children).
    /// 
    /// - Parameter aggregate: The previous aggregate value. In the default
    /// implementation, the aggregate value is initialized to
    /// _#defaultResult_, which is passed as the `aggregate` argument
    /// to this method after the first child node is visited.
    /// - Parameter nextResult: The result of the immediately preceeding call to visit
    /// a child node.
    /// 
    /// - Returns: The updated aggregate result.
    /// 
    open func aggregateResult(_ aggregate: T?, _ nextResult: T?) -> T? {
        return nextResult
    }

    /// 
    /// This method is called after visiting each child in
    /// _#visitChildren_. This method is first called before the first
    /// child is visited; at that point `currentResult` will be the initial
    /// value (in the default implementation, the initial value is returned by a
    /// call to _#defaultResult_. This method is not called after the last
    /// child is visited.
    /// 
    /// The default implementation always returns `true`, indicating that
    /// `visitChildren` should only return after all children are visited.
    /// One reason to override this method is to provide a "short circuit"
    /// evaluation option for situations where the result of visiting a single
    /// child has the potential to determine the result of the visit operation as
    /// a whole.
    /// 
    /// - Parameter node: The _org.antlr.v4.runtime.tree.RuleNode_ whose children are currently being
    /// visited.
    /// - Parameter currentResult: The current aggregate result of the children visited
    /// to the current point.
    /// 
    /// - Returns: `true` to continue visiting children. Otherwise return
    /// `false` to stop visiting children and immediately return the
    /// current aggregate result from _#visitChildren_.
    /// 
    open func shouldVisitNextChild(_ node: RuleNode, _ currentResult: T?) -> Bool {
        return true
    }

}
