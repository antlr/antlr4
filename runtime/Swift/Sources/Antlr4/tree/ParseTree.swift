/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// An interface to access the tree of _org.antlr.v4.runtime.RuleContext_ objects created
/// during a parse that makes the data structure look like a simple parse tree.
/// This node represents both internal nodes, rule invocations,
/// and leaf nodes, token matches.
/// 
/// The payload is either a _org.antlr.v4.runtime.Token_ or a _org.antlr.v4.runtime.RuleContext_ object.
/// 
open class ParseTree: SyntaxTree, CustomStringConvertible, CustomDebugStringConvertible {

    /// The _org.antlr.v4.runtime.tree.ParseTreeVisitor_ needs a double dispatch method.

    open func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
        fatalError(#function + " must be overridden")
    }

    /// Return the combined text of all leaf nodes. Does not get any
    /// off-channel tokens (if any) so won't return whitespace and
    /// comments if they are sent to parser on hidden channel.
    /// 
    open func getText() -> String {
        fatalError(#function + " must be overridden")
    }

    /// Specialize toStringTree so that it can print out more information
    /// based upon the parser.
    ///
    open func toStringTree(_ parser: Parser) -> String {
        fatalError(#function + " must be overridden")
    }

    open func getSourceInterval() -> Interval {
        fatalError(#function + " must be overridden")
    }

    open func getParent() -> Tree? {
        fatalError(#function + " must be overridden")
    }

    open func getPayload() -> AnyObject {
        fatalError(#function + " must be overridden")
    }

    open func getChild(_ i: Int) -> Tree? {
        fatalError(#function + " must be overridden")
    }

    open func getChildCount() -> Int {
        fatalError(#function + " must be overridden")
    }

    open func toStringTree() -> String {
        fatalError(#function + " must be overridden")
    }

    open var description: String {
        fatalError(#function + " must be overridden")
    }

    open var debugDescription: String {
        fatalError(#function + " must be overridden")
    }
}
