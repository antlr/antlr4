package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.NotNull;

/** A call to a left recur rule which requires a precedence integer arg.
 *  The ATN factory senses that there is an argument and, if it is an integer,
 *  creates this kind of a call instead of a regular rule transition.
 *
 *  The interpreter pushes the precedents into the locals object
 *  upon the call. The locals object simulates a rule function call
 *  in a recursive descent parser.
 */
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
