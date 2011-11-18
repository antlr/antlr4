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

public class ParseTreeWalker {
    public static final ParseTreeWalker DEFAULT = new ParseTreeWalker();

    public <TSymbol> void walk(ParseTreeListener<TSymbol> listener, ParseTree t) {
		if ( t instanceof ParseTree.TokenNode) {
			visitToken(listener, (ParseTree.TokenNode) t);
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

    protected <TSymbol> void visitToken(ParseTreeListener<TSymbol> listener, ParseTree.TokenNode t) {
        listener.visitToken(t.getToken());
    }

	/** The discovery of a rule node, involves sending two events:
	 *  the generic discoverRule and a RuleContext-specific event.
	 *  First we trigger the generic and then the rule specific.
	 *  We to them in reverse order upon finishing the node.
	 */
    protected <TSymbol> void enterRule(ParseTreeListener<TSymbol> listener, ParseTree.RuleNode r) {
		ParserRuleContext<TSymbol> ctx = (ParserRuleContext<TSymbol>)r.getRuleContext();
		listener.enterEveryRule((ParserRuleContext<TSymbol>) r.getRuleContext());
		ctx.enterRule(listener);
    }

    protected <TSymbol> void exitRule(ParseTreeListener<TSymbol> listener, ParseTree.RuleNode r) {
		ParserRuleContext<TSymbol> ctx = (ParserRuleContext<TSymbol>)r.getRuleContext();
		ctx.exitRule(listener);
		listener.exitEveryRule(ctx);
    }
}
