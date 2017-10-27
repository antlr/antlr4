/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/// 
/// This interface defines the basic notion of a parse tree visitor. Generated
/// visitors implement this interface and the `XVisitor` interface for
/// grammar `X`.
/// 
/// - Parameter <T>: The return type of the visit operation. Use _Void_ for
/// operations with no return type.
/// 


open class ParseTreeVisitor<T> {
    public init() {

    }
    // typealias T
    /// 
    /// Visit a parse tree, and return a user-defined result of the operation.
    /// 
    /// - Parameter tree: The _org.antlr.v4.runtime.tree.ParseTree_ to visit.
    /// - Returns: The result of visiting the parse tree.
    /// 
    open func visit(_ tree: ParseTree) -> T? {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Visit the children of a node, and return a user-defined result of the
    /// operation.
    /// 
    /// - Parameter node: The _org.antlr.v4.runtime.tree.RuleNode_ whose children should be visited.
    /// - Returns: The result of visiting the children of the node.
    /// 
    open func visitChildren(_ node: RuleNode) -> T? {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Visit a terminal node, and return a user-defined result of the operation.
    /// 
    /// - Parameter node: The _org.antlr.v4.runtime.tree.TerminalNode_ to visit.
    /// - Returns: The result of visiting the node.
    /// 
    open func visitTerminal(_ node: TerminalNode) -> T? {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Visit an error node, and return a user-defined result of the operation.
    /// 
    /// - Parameter node: The _org.antlr.v4.runtime.tree.ErrorNode_ to visit.
    /// - Returns: The result of visiting the node.
    /// 
    open func visitErrorNode(_ node: ErrorNode) -> T? {
        fatalError(#function + " must be overridden")
    }
}
