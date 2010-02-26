package org.antlr.v4.automata;

import org.antlr.v4.tool.Rule;

/** */
public class RuleTransition extends Transition {
	/** Ptr to the rule definition object for this rule ref */
	public Rule rule;

	/** What node to begin computations following ref to rule */
    public NFAState followState;

    public RuleTransition(Rule rule,
						  NFAState ruleStart,
						  NFAState followState)
	{
		super(ruleStart);
		this.rule = rule;
		this.followState = followState;
	}

	public int compareTo(Object o) {
		return 0;
	}
}
