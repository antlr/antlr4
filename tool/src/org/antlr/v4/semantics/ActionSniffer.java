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

package org.antlr.v4.semantics;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.List;

/** Find token and rule refs plus refs to them in actions;
 *  side-effect: update Alternatives
 */
public class ActionSniffer extends BlankActionSplitterListener {
	public Grammar g;
	public Rule r;          // null if action outside of rule
	public Alternative alt; // null if action outside of alt; could be in rule
	public ActionAST node;
	public Token actionToken; // token within action
	public ErrorManager errMgr;

	public ActionSniffer(Grammar g, Rule r, Alternative alt, ActionAST node, Token actionToken) {
		this.g = g;
		this.r = r;
		this.alt = alt;
		this.node = node;
		this.actionToken = actionToken;
		this.errMgr = g.tool.errMgr;
	}

	public void examineAction() {
		//System.out.println("examine "+actionToken);
		ANTLRStringStream in = new ANTLRStringStream(actionToken.getText());
		in.setLine(actionToken.getLine());
		in.setCharPositionInLine(actionToken.getCharPositionInLine());
		ActionSplitter splitter = new ActionSplitter(in, this);
		// forces eval, triggers listener methods
		node.chunks = splitter.getActionTokens();
	}

	public void processNested(Token actionToken) {
		ANTLRStringStream in = new ANTLRStringStream(actionToken.getText());
		in.setLine(actionToken.getLine());
		in.setCharPositionInLine(actionToken.getCharPositionInLine());
		ActionSplitter splitter = new ActionSplitter(in, this);
		// forces eval, triggers listener methods
		splitter.getActionTokens();
	}


	@Override
	public void attr(String expr, Token x) { trackRef(x); }

	@Override
	public void qualifiedAttr(String expr, Token x, Token y) { trackRef(x); }

	@Override
	public void setAttr(String expr, Token x, Token rhs) {
		trackRef(x);
		processNested(rhs);
	}

	@Override
	public void setNonLocalAttr(String expr, Token x, Token y, Token rhs) {
		processNested(rhs);
	}

	public void trackRef(Token x) {
		List<TerminalAST> xRefs = alt.tokenRefs.get(x.getText());
		if ( xRefs!=null ) {
			alt.tokenRefsInActions.map(x.getText(), node);
		}
		List<GrammarAST> rRefs = alt.ruleRefs.get(x.getText());
		if ( rRefs!=null ) {
			alt.ruleRefsInActions.map(x.getText(), node);
		}
	}
}
