package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.NotNull;

public class PrecedencePredicateTransition extends PredicateTransition {
	public int precedence;
	public PrecedencePredicateTransition(@NotNull ATNState target, int ruleIndex,
										 int predIndex, boolean isCtxDependent,
										 int precedence)
	{
		super(target, ruleIndex, predIndex, isCtxDependent);
		this.precedence = precedence;
	}
}
