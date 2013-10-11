package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.NotNull;

/** A call to a left recur rule which requires a precedence integer arg */
public class LeftRecursiveRuleTransition extends RuleTransition {
	public int precedence;

	public LeftRecursiveRuleTransition(@NotNull RuleStartState ruleStart,
									   int ruleIndex,
									   @NotNull ATNState followState,
									   int precedence) {
		super(ruleStart, ruleIndex, followState);
		this.precedence = precedence;
	}

	@Override
	public int getSerializationType() {
		return LEFT_RECUR_RULE;
	}
}
