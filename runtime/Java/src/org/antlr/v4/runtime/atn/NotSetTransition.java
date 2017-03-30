/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public final class NotSetTransition extends SetTransition {
	public NotSetTransition(ATNState target, IntervalSet set) {
		super(target, set);
	}

	@Override
	public int getSerializationType() {
		return NOT_SET;
	}

	@Override
	public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
		return symbol >= minVocabSymbol
			&& symbol <= maxVocabSymbol
			&& !super.matches(symbol, minVocabSymbol, maxVocabSymbol);
	}

	@Override
	public String toString() {
		return '~'+super.toString();
	}
}
