/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class ParseTreeWalker {
    public static final ParseTreeWalker DEFAULT = new ParseTreeWalker();

    public <Symbol extends Token> void walk(ParseTreeListener<? super Symbol> listener, ParseTree<Symbol> t) {
		if ( t instanceof ErrorNode ) {
			listener.visitErrorNode((ErrorNode<Symbol>)t);
			return;
		}
		else if ( t instanceof TerminalNode ) {
			listener.visitTerminal((TerminalNode<Symbol>)t);
			return;
		}
		RuleNode<Symbol> r = (RuleNode<Symbol>)t;
        enterRule(listener, r);
        int n = r.getChildCount();
        for (int i = 0; i<n; i++) {
            walk(listener, r.getChild(i));
        }
		exitRule(listener, r);
    }

	/** The discovery of a rule node, involves sending two events:
	 *  the generic discoverRule and a RuleContext-specific event.
	 *  First we trigger the generic and then the rule specific.
	 *  We to them in reverse order upon finishing the node.
	 */
    protected <Symbol extends Token> void enterRule(ParseTreeListener<? super Symbol> listener, RuleNode<Symbol> r) {
		ParserRuleContext<Symbol> ctx = (ParserRuleContext<Symbol>)r.getRuleContext();
		listener.enterEveryRule(ctx);
		ctx.enterRule(listener);
    }

    protected <Symbol extends Token> void exitRule(ParseTreeListener<? super Symbol> listener, RuleNode<Symbol> r) {
		ParserRuleContext<Symbol> ctx = (ParserRuleContext<Symbol>)r.getRuleContext();
		ctx.exitRule(listener);
		listener.exitEveryRule(ctx);
    }
}
