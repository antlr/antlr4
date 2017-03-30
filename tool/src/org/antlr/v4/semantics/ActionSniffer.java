/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
