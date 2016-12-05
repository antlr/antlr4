/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/** An interface to access the tree of {@link org.antlr.v4.runtime.RuleContext} objects created
 *  during a parse that makes the data structure look like a simple parse tree.
 *  This node represents both internal nodes, rule invocations,
 *  and leaf nodes, token matches.
 *
 *  <p>The payload is either a {@link org.antlr.v4.runtime.Token} or a {@link org.antlr.v4.runtime.RuleContext} object.</p>
 */
//public protocol ParseTree : SyntaxTree {

open class ParseTree: SyntaxTree, CustomStringConvertible , CustomDebugStringConvertible  {

    // the following methods narrow the return type; they are not additional methods

    //func getParent() -> ParseTree?

    //func getChild(i : Int) -> ParseTree?

    /** The {@link org.antlr.v4.runtime.tree.ParseTreeVisitor} needs a double dispatch method. */

    open func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
        RuntimeException(" must overriden !")
        fatalError()
    }

    /** Return the combined text of all leaf nodes. Does not get any
     *  off-channel tokens (if any) so won't return whitespace and
     *  comments if they are sent to parser on hidden channel.
     */
    open func getText() -> String {
        RuntimeException(" must overriden !")
        return ""
    }
    /** Specialize toStringTree so that it can print out more information
     * 	based upon the parser.
     */
    open func toStringTree(_ parser: Parser) -> String {
        RuntimeException(" must overriden !")
        return ""

    }


    open func getSourceInterval() -> Interval {
        RuntimeException(" must overriden !")
        fatalError()
    }


    open func getParent() -> Tree? {
        RuntimeException(" must overriden !")
        fatalError()
    }

    open func getPayload() -> AnyObject {
        RuntimeException(" must overriden !")
        fatalError()
    }

    open func getChild(_ i: Int) -> Tree? {
        RuntimeException(" must overriden !")
        fatalError()
    }


    open func getChildCount() -> Int {
        RuntimeException(" must overriden !")
        fatalError()
    }

    open func toStringTree() -> String {
        RuntimeException(" must overriden !")
        fatalError()
    }


    open var description: String {
        RuntimeException(" must overriden !")
        fatalError()
    }

    open var debugDescription: String {
        RuntimeException(" must overriden !")
        fatalError()
    }
}
