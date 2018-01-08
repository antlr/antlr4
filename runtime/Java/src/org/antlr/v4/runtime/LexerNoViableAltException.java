/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;

import java.util.Locale;

public class LexerNoViableAltException extends RecognitionException {
	/** Matching attempted at what input index? */
	private final int startIndex;

	/** Which configurations did we try at input.index() that couldn't match input.LA(1)? */
	private final ATNConfigSet deadEndConfigs;

	public LexerNoViableAltException(Lexer lexer,
									 CharStream input,
									 int startIndex,
									 ATNConfigSet deadEndConfigs) {
		super(lexer, input, null);
		this.startIndex = startIndex;
		this.deadEndConfigs = deadEndConfigs;
	}

	public int getStartIndex() {
		return startIndex;
	}


	public ATNConfigSet getDeadEndConfigs() {
		return deadEndConfigs;
	}

	@Override
	public CharStream getInputStream() {
		return (CharStream)super.getInputStream();
	}

	@Override
	public String toString() {
		String symbol = "";
		if (startIndex >= 0 && startIndex < getInputStream().size()) {
			symbol = getInputStream().getText(Interval.of(startIndex,startIndex));
			symbol = Utils.escapeWhitespace(symbol, false);
		}

		return String.format(Locale.getDefault(), "%s('%s')", LexerNoViableAltException.class.getSimpleName(), symbol);
	}
}
