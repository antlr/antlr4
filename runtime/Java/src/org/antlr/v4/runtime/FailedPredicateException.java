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

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.misc.Nullable;

/** A semantic predicate failed during validation.  Validation of predicates
 *  occurs when normally parsing the alternative just like matching a token.
 *  Disambiguating predicate evaluation occurs when we test a predicate during
 *  prediction.
 */
public class FailedPredicateException extends RecognitionException {
	public int ruleIndex;
	public int predIndex;
	public String msg;

	public <Symbol extends Token> FailedPredicateException(Parser<Symbol> recognizer) {
		this(recognizer, null);
	}

	public <Symbol extends Token> FailedPredicateException(Parser<Symbol> recognizer, @Nullable String predicate) {
		super(recognizer, recognizer.getInputStream(), recognizer._ctx);
		ATNState s = recognizer.getInterpreter().atn.states.get(recognizer._ctx.s);
		PredicateTransition trans = (PredicateTransition)s.transition(0);
		ruleIndex = trans.ruleIndex;
		predIndex = trans.predIndex;
		this.msg = String.format("failed predicate: {%s}?", predicate);
		Token la = recognizer.getCurrentToken();
		this.offendingToken = la;
	}
}
