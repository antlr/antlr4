/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;

/** A transition containing a set of values. */
public class SetTransition extends Transition {
	public final IntervalSet set;

	// TODO (sam): should we really allow null here?
	public SetTransition(ATNState target, IntervalSet set) {
		super(target);
		if ( set == null ) set = IntervalSet.of(Token.INVALID_TYPE);
		this.set = set;
	}

	@Override
	public int getSerializationType() {
		return SET;
	}

	@Override

	public IntervalSet label() { return set; }

	@Override
	public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
		return set.contains(symbol);
	}

	@Override

	public String toString() {
		return set.toString();
	}
}
