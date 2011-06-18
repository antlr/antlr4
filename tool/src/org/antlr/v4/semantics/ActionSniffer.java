package org.antlr.v4.semantics;

import org.antlr.runtime.*;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.tool.*;

import java.util.List;

/** Find token and rule refs, side-effect: update Alternatives */
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
		System.out.println(node.chunks);
	}

	public void attr(String expr, Token x) {
		List<TerminalAST> xRefs = alt.tokenRefs.get(x.getText());
		if ( alt!=null && xRefs!=null ) {
			alt.tokenRefsInActions.map(x.getText(), node);
		}
		List<GrammarAST> rRefs = alt.ruleRefs.get(x.getText());
		if ( alt!=null && rRefs!=null ) {
			alt.ruleRefsInActions.map(x.getText(), node);
		}
	}

	public void qualifiedAttr(String expr, Token x, Token y) {
		attr(expr, x);
	}
}
