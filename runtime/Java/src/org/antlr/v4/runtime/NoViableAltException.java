/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.misc.OrderedHashSet;

public class NoViableAltException extends RecognitionException {
	/** Prediction began at what input index? */
	public int startIndex;

	/** Which configurations did we try at input.index() that couldn't match input.LT(1)? */
	public OrderedHashSet<ATNConfig> deadEndConfigs;

	/** Used for remote debugger deserialization */
	public NoViableAltException() {;}

	public NoViableAltException(BaseRecognizer recognizer, RuleContext ctx) { // LL(1) error
		super(recognizer, recognizer._input, ctx);
	}

	public NoViableAltException(BaseRecognizer recognizer, IntStream input,
								OrderedHashSet<ATNConfig> deadEndConfigs,
								RuleContext ctx)
	{
		super(recognizer, input, ctx);
		this.deadEndConfigs = deadEndConfigs;
	}

	public String toString() {
		if ( recognizer!=null ) {
			TokenStream tokens = recognizer._input;
			String bad = tokens.toString(startIndex, index);
			return "NoViableAltException(input=\""+bad+"\" last token type is "+getUnexpectedType()+")";
		}
		return "NoViableAltException(last token type is "+getUnexpectedType()+")";
	}
}
