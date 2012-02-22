/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ParseTreeWalker {
    public static final ParseTreeWalker DEFAULT = new ParseTreeWalker();

    @SuppressWarnings("unchecked")
    public <Symbol extends Token> void walk(ParseTreeListener<Symbol> listener, ParseTree t) {
		if ( t instanceof ParseTree.TerminalNode) {
			visitTerminal(listener, (ParseTree.TerminalNode<Symbol>) t);
			return;
		}
		ParseTree.RuleNode r = (ParseTree.RuleNode)t;
        enterRule(listener, r);
        int n = r.getChildCount();
        for (int i = 0; i<n; i++) {
            walk(listener, r.getChild(i));
        }
		exitRule(listener, r);
    }

    @SuppressWarnings("unchecked")
    protected <Symbol extends Token> void visitTerminal(ParseTreeListener<Symbol> listener,
										  ParseTree.TerminalNode<Symbol> t)
	{
		ParseTree.RuleNode r = (ParseTree.RuleNode)t.getParent();
		ParserRuleContext<Symbol> ctx = null;
		if ( r != null && r.getRuleContext() instanceof ParserRuleContext<?> ) {
			ctx = (ParserRuleContext<Symbol>)r.getRuleContext();
		}
        listener.visitTerminal(ctx, t.getSymbol());
    }

	/** The discovery of a rule node, involves sending two events:
	 *  the generic discoverRule and a RuleContext-specific event.
	 *  First we trigger the generic and then the rule specific.
	 *  We to them in reverse order upon finishing the node.
	 */
    protected <Symbol extends Token> void enterRule(ParseTreeListener<Symbol> listener, ParseTree.RuleNode r) {
		@SuppressWarnings("unchecked")
		ParserRuleContext<Symbol> ctx = (ParserRuleContext<Symbol>)r.getRuleContext();
		listener.enterEveryRule(ctx);
		ctx.enterRule(listener);
    }

    protected <Symbol extends Token> void exitRule(ParseTreeListener<Symbol> listener, ParseTree.RuleNode r) {
		@SuppressWarnings("unchecked")
		ParserRuleContext<Symbol> ctx = (ParserRuleContext<Symbol>)r.getRuleContext();
		ctx.exitRule(listener);
		listener.exitEveryRule(ctx);
    }
}
