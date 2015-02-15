/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
