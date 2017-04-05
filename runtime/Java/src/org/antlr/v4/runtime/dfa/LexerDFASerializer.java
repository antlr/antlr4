/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.VocabularyImpl;

public class LexerDFASerializer extends DFASerializer {
	public LexerDFASerializer(DFA dfa) {
		super(dfa, VocabularyImpl.EMPTY_VOCABULARY);
	}

	@Override

	protected String getEdgeLabel(int i) {
		return new StringBuilder("'")
				.appendCodePoint(i)
				.append("'")
				.toString();
	}
}
