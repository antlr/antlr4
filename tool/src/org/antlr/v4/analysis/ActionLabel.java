package org.antlr.v4.analysis;

import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarAST;

/** */
public class ActionLabel extends Label {
	public GrammarAST actionAST;

	public ActionLabel(GrammarAST actionAST) {
		this.actionAST = actionAST;
	}

	public boolean isEpsilon() {
		return true; // we are to be ignored by analysis 'cept for predicates
	}

	public String toString() {
		return "{"+actionAST+"}";
	}

	public String toString(Grammar g) {
		return toString();
	}
}
