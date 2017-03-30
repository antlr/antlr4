/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/**
 *
 * @author Sam Harwell
 */
public final class PrecedencePredicateTransition extends AbstractPredicateTransition {
	public final int precedence;

	public PrecedencePredicateTransition(ATNState target, int precedence) {
		super(target);
		this.precedence = precedence;
	}

	@Override
	public int getSerializationType() {
		return PRECEDENCE;
	}

	@Override
	public boolean isEpsilon() {
		return true;
	}

	@Override
	public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
		return false;
	}

	public SemanticContext.PrecedencePredicate getPredicate() {
		return new SemanticContext.PrecedencePredicate(precedence);
	}

	@Override
	public String toString() {
		return precedence + " >= _p";
	}

}
