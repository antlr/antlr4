package org.antlr.v4.runtime.atn;

public class ActionTransition extends Transition {
	public int ruleIndex;
	public int actionIndex = -1;

	public ActionTransition(ATNState target) {
		super(target);
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
		return "action_"+ruleIndex+":"+actionIndex;
	}
}
