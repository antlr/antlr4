/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

/** TODO: make all transitions sets? no, should remove set edges */
public final class AtomTransition extends Transition {
	/** The token type or character value; or, signifies special label. */
	public final int label;

	public AtomTransition(ATNState target, int label) {
		super(target);
		this.label = label;
	}

	@Override
	public int getSerializationType() {
		return ATOM;
	}

	@Override

	public IntervalSet label() { return IntervalSet.of(label); }

	@Override
	public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
		return label == symbol;
	}

	@Override
	public String toString() {
		return String.valueOf(label);
	}
}
