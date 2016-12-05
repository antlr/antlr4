/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/**
 * This interface defines the basic notion of a parse tree visitor. Generated
 * visitors implement this interface and the {@code XVisitor} interface for
 * grammar {@code X}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */


open class ParseTreeVisitor<T> {
    public init() {

    }
    // typealias T
    /**
     * Visit a parse tree, and return a user-defined result of the operation.
     *
     * @param tree The {@link org.antlr.v4.runtime.tree.ParseTree} to visit.
     * @return The result of visiting the parse tree.
     */
    open func visit(_ tree: ParseTree) -> T? {
        RuntimeException(" must overriden !")
        return nil

    }

    /**
     * Visit the children of a node, and return a user-defined result of the
     * operation.
     *
     * @param node The {@link org.antlr.v4.runtime.tree.RuleNode} whose children should be visited.
     * @return The result of visiting the children of the node.
     */
    open func visitChildren(_ node: RuleNode) -> T? {
        RuntimeException(" must overriden !")
        return nil

    }

    /**
     * Visit a terminal node, and return a user-defined result of the operation.
     *
     * @param node The {@link org.antlr.v4.runtime.tree.TerminalNode} to visit.
     * @return The result of visiting the node.
     */
    open func visitTerminal(_ node: TerminalNode) -> T? {
        RuntimeException(" must overriden !")
        return nil

    }

    /**
     * Visit an error node, and return a user-defined result of the operation.
     *
     * @param node The {@link org.antlr.v4.runtime.tree.ErrorNode} to visit.
     * @return The result of visiting the node.
     */
    open func visitErrorNode(_ node: ErrorNode) -> T? {
        RuntimeException(" must overriden !")
        return nil
    }
}
