/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


public class ParseTreeWalker {
    public static let DEFAULT = ParseTreeWalker()

    public init() {
    }

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

    /// 
    /// The discovery of a rule node, involves sending two events: the generic
    /// _org.antlr.v4.runtime.tree.ParseTreeListener#enterEveryRule_ and a
    /// _org.antlr.v4.runtime.RuleContext_-specific event. First we trigger the generic and then
    /// the rule specific. We to them in reverse order upon finishing the node.
    /// 
    internal func enterRule(_ listener: ParseTreeListener, _ r: RuleNode) throws {
        let ctx = r.getRuleContext() as! ParserRuleContext
        try listener.enterEveryRule(ctx)
        ctx.enterRule(listener)
    }

    internal func exitRule(_ listener: ParseTreeListener, _ r: RuleNode) throws {
        let ctx = r.getRuleContext() as! ParserRuleContext
        ctx.exitRule(listener)
        try listener.exitEveryRule(ctx)
    }
}
