/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

public final class WildcardTransition extends Transition {
	public WildcardTransition(ATNState target) { super(target); }

	@Override
	public int getSerializationType() {
		return WILDCARD;
	}

	@Override
	public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
		return symbol >= minVocabSymbol && symbol <= maxVocabSymbol;
	}

	@Override
	public String toString() {
		return ".";
	}
}
