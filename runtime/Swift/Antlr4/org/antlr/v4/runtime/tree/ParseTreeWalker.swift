/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


public class ParseTreeWalker {
    public static let DEFAULT = ParseTreeWalker()
    public init() {

    }

    public func walk(_ listener: ParseTreeListener, _ t: ParseTree) throws {
        if t is ErrorNode {
            listener.visitErrorNode(t as! ErrorNode)
            return
        } else {
            if t is TerminalNode {
                listener.visitTerminal(t as! TerminalNode)
                return
            }
        }
        let r: RuleNode = t as! RuleNode
        try enterRule(listener, r)
        let n: Int = r.getChildCount()
        for i in 0..<n {
            try   walk(listener, r.getChild(i) as! ParseTree)
        }
        try exitRule(listener, r)
    }

    /**
     * The discovery of a rule node, involves sending two events: the generic
     * {@link org.antlr.v4.runtime.tree.ParseTreeListener#enterEveryRule} and a
     * {@link org.antlr.v4.runtime.RuleContext}-specific event. First we trigger the generic and then
     * the rule specific. We to them in reverse order upon finishing the node.
     */
    internal func enterRule(_ listener: ParseTreeListener, _ r: RuleNode) throws {
        let ctx: ParserRuleContext = r.getRuleContext() as! ParserRuleContext
        try listener.enterEveryRule(ctx)
        ctx.enterRule(listener)
    }

    internal func exitRule(_ listener: ParseTreeListener, _ r: RuleNode) throws {
        let ctx: ParserRuleContext = r.getRuleContext() as! ParserRuleContext
        ctx.exitRule(listener)
        try listener.exitEveryRule(ctx)
    }
}
