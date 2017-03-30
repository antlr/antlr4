/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

public final class EpsilonTransition extends Transition {

	private final int outermostPrecedenceReturn;

	public EpsilonTransition(ATNState target) {
		this(target, -1);
	}

	public EpsilonTransition(ATNState target, int outermostPrecedenceReturn) {
		super(target);
		this.outermostPrecedenceReturn = outermostPrecedenceReturn;
	}

	/**
	 * @return the rule index of a precedence rule for which this transition is
	 * returning from, where the precedence value is 0; otherwise, -1.
	 *
	 * @see ATNConfig#isPrecedenceFilterSuppressed()
	 * @see ParserATNSimulator#applyPrecedenceFilter(ATNConfigSet)
	 * @since 4.4.1
	 */
	public int outermostPrecedenceReturn() {
		return outermostPrecedenceReturn;
	}

	@Override
	public int getSerializationType() {
		return EPSILON;
	}

	@Override
	public boolean isEpsilon() { return true; }

	@Override
	public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
		return false;
	}

	@Override

	public String toString() {
		return "epsilon";
	}
}
