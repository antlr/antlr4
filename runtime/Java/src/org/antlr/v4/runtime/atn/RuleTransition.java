package org.antlr.v4.runtime.atn;

import org.antlr.v4.tool.Rule;

/** */
public class RuleTransition extends Transition {
	/** Ptr to the rule definition object for this rule ref */
	public Rule rule;
	public int ruleIndex; // no Rule object at runtime

	/** What node to begin computations following ref to rule */
    public ATNState followState;

	public RuleTransition(Rule rule,
						  ATNState ruleStart,
						  ATNState followState)
	{
		super(ruleStart);
		this.rule = rule;
		this.followState = followState;
	}

	public RuleTransition(int ruleIndex,
						  ATNState ruleStart,
						  ATNState followState)
	{
		super(ruleStart);
		this.ruleIndex = ruleIndex;
		this.followState = followState;
	}

	public RuleTransition(ATNState ruleStart) {
		super(ruleStart);
	}

	public boolean isEpsilon() { return true; }

	public int compareTo(Object o) {
		return 0;
	}
}
