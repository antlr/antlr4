/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  Copyright (c) 2015 Janyou
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
