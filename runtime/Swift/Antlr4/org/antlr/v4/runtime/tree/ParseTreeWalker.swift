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
