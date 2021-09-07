/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


public class ParseTreeWalker {
    public static let DEFAULT = ParseTreeWalker()

    public init() {
    }

    /**
	 * Performs a walk on the given parse tree starting at the root and going down recursively
	 * with depth-first search. On each node, ParseTreeWalker.enterRule is called before
	 * recursively walking down into child nodes, then
	 * ParseTreeWalker.exitRule is called after the recursive call to wind up.
	 * - Parameter listener: The listener used by the walker to process grammar rules
	 * - Parameter t: The parse tree to be walked on
	 */
    public func walk(_ listener: ParseTreeListener, _ t: ParseTree) throws {
        if let errNode = t as? ErrorNode {
            listener.visitErrorNode(errNode)
        }
        else if let termNode = t as? TerminalNode {
            listener.visitTerminal(termNode)
        }
        else if let r = t as? RuleNode {
            try enterRule(listener, r)
            let n = r.getChildCount()
            for i in 0..<n {
                try walk(listener, r[i])
            }
            try exitRule(listener, r)
        }
        else {
            preconditionFailure()
        }
    }

    /**
	 * Enters a grammar rule by first triggering the generic event ParseTreeListener.enterEveryRule
	 * then by triggering the event specific to the given parse tree node
	 * - Parameter listener: The listener responding to the trigger events
	 * - Parameter r: The grammar rule containing the rule context
	 */
    internal func enterRule(_ listener: ParseTreeListener, _ r: RuleNode) throws {
        let ctx = r.getRuleContext() as! ParserRuleContext
        try listener.enterEveryRule(ctx)
        ctx.enterRule(listener)
    }

    /**
	 * Exits a grammar rule by first triggering the event specific to the given parse tree node
	 * then by triggering the generic event ParseTreeListener.exitEveryRule
	 * - Parameter listener: The listener responding to the trigger events
	 * - Parameter r: The grammar rule containing the rule context
	 */
    internal func exitRule(_ listener: ParseTreeListener, _ r: RuleNode) throws {
        let ctx = r.getRuleContext() as! ParserRuleContext
        ctx.exitRule(listener)
        try listener.exitEveryRule(ctx)
    }
}
