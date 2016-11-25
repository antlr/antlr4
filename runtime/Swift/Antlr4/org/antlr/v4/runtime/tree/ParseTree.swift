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
