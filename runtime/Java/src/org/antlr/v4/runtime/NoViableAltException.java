/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfigSet;

/** Indicates that the parser could not decide which of two or more paths
 *  to take based upon the remaining input. It tracks the starting token
 *  of the offending input and also knows where the parser was
 *  in the various paths when the error. Reported by reportNoViableAlternative()
 */
public class NoViableAltException extends RecognitionException {
	/** Which configurations did we try at input.index() that couldn't match input.LT(1)? */

	private final ATNConfigSet deadEndConfigs;

	/** The token object at the start index; the input stream might
	 * 	not be buffering tokens so get a reference to it. (At the
	 *  time the error occurred, of course the stream needs to keep a
	 *  buffer all of the tokens but later we might not have access to those.)
	 */

	private final Token startToken;

	public NoViableAltException(Parser recognizer) { // LL(1) error
		this(recognizer,
			 recognizer.getInputStream(),
			 recognizer.getCurrentToken(),
			 recognizer.getCurrentToken(),
			 null,
			 recognizer._ctx);
	}

	public NoViableAltException(Parser recognizer,
								TokenStream input,
								Token startToken,
								Token offendingToken,
								ATNConfigSet deadEndConfigs,
								ParserRuleContext ctx)
	{
		super(recognizer, input, ctx);
		this.deadEndConfigs = deadEndConfigs;
		this.startToken = startToken;
		this.setOffendingToken(offendingToken);
	}


	public Token getStartToken() {
		return startToken;
	}


	public ATNConfigSet getDeadEndConfigs() {
		return deadEndConfigs;
	}

}
