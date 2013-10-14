package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.NotNull;

/** A call to a left recur rule which requires a precedence integer arg.
 *  The ATN factory senses that there is an argument and, if it is an integer,
 *  creates this kind of a call instead of a regular rule transition.
 *  The argument is computed at ATN construction at tool time,
 *  but the information is not passed through serialization to the generated
 *  code. It is only used by ParserInterpreter.
 *
 *  The interpreter pushes the precedence into the locals object
 *  upon the call. The locals object simulates a rule function call
 *  in a recursive-descent parser.
 *
 *  This is not serialized or deserialize differently than a regular
 *  rule call transition. The precedence field is lost during serialization.
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
