package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.NotNull;

/** A special kind of transition where we know what the predicate does.
 *  It's used for testing precedence of operators for rules in which
 *  we have eliminated left recursion. Predicates like {3 >= $_p}?
 *
 *  This is not serialized or deserialize differently than a
 *  regular predicate transition. The precedence field is lost during
 *  serialization.
 */
public class PrecedencePredicateTransition extends PredicateTransition {
	public int precedence;
	public PrecedencePredicateTransition(@NotNull ATNState source,
										 @NotNull ATNState target, int ruleIndex,
										 int predIndex, boolean isCtxDependent,
										 int precedence)
	{
		super(source, target, ruleIndex, predIndex, isCtxDependent);
		this.precedence = precedence;
	}

	@Override
	public int getSerializationType() {
		return Transition.PREC_PREDICATE;
	}
}
