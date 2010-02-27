package org.antlr.v4.automata;

import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarAST;

public class ActionTransition extends Transition {
	public GrammarAST actionAST;

	public ActionTransition(GrammarAST actionAST, NFAState target) {
		super(target);
		this.actionAST = actionAST;
	}

	public boolean isEpsilon() {
		return true; // we are to be ignored by analysis 'cept for predicates
	}

	public int compareTo(Object o) {
		return 0;
	}

	public String toString() {
		return "{"+actionAST+"}";
	}

	public String toString(Grammar g) {
		return toString();
	}
}
