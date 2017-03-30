/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/** */
public final class RuleTransition extends Transition {
	/** Ptr to the rule definition object for this rule ref */
	public final int ruleIndex;     // no Rule object at runtime

	public final int precedence;

	/** What node to begin computations following ref to rule */
	public ATNState followState;

	/**
	 * @deprecated Use
	 * {@link #RuleTransition(RuleStartState, int, int, ATNState)} instead.
	 */
	@Deprecated
	public RuleTransition(RuleStartState ruleStart,
						  int ruleIndex,
						  ATNState followState)
	{
		this(ruleStart, ruleIndex, 0, followState);
	}

	public RuleTransition(RuleStartState ruleStart,
						  int ruleIndex,
						  int precedence,
						  ATNState followState)
	{
		super(ruleStart);
		this.ruleIndex = ruleIndex;
		this.precedence = precedence;
		this.followState = followState;
	}

	@Override
	public int getSerializationType() {
		return RULE;
	}

	@Override
	public boolean isEpsilon() { return true; }

	@Override
	public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
		return false;
	}
}
