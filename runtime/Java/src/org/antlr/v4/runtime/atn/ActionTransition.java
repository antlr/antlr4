package org.antlr.v4.runtime.atn;

import org.antlr.v4.tool.*;

public class ActionTransition extends Transition {
	public int ruleIndex;
	public int actionIndex = -1;
	public GrammarAST actionAST;

	public ActionTransition(GrammarAST actionAST, ATNState target) {
		super(target);
		this.actionAST = actionAST;
	}

	public ActionTransition(ATNState target, int ruleIndex, int actionIndex) {
		super(target);
		this.ruleIndex = ruleIndex;
		this.actionIndex = actionIndex;
	}

	public boolean isEpsilon() {
		return true; // we are to be ignored by analysis 'cept for predicates
	}

	public String toString() {
		if ( actionAST!=null ) return "{"+actionAST.getText()+"}";
		return "action_"+ruleIndex+":"+actionIndex;
	}

	public String toString(Grammar g) {
		return toString();
	}
}
