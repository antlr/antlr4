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

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

/** A semantic predicate failed during validation.  Validation of predicates
 *  occurs when normally parsing the alternative just like matching a token.
 *  Disambiguating predicate evaluation occurs when we test a predicate during
 *  prediction.
 */
public class FailedPredicateException extends RecognitionException {
	private static final long serialVersionUID = 5379330841495778709L;

	private final int ruleIndex;
	private final int predicateIndex;
	private final String predicate;

	public <Symbol extends Token> FailedPredicateException(@NotNull Parser<Symbol> recognizer) {
		this(recognizer, null);
	}

	public <Symbol extends Token> FailedPredicateException(@NotNull Parser<Symbol> recognizer, @Nullable String predicate) {
		this(recognizer, predicate, null);
	}

	public <Symbol extends Token> FailedPredicateException(@NotNull Parser<Symbol> recognizer,
														   @Nullable String predicate,
														   @Nullable String message)
	{
		super(formatMessage(predicate, message), recognizer, recognizer.getInputStream(), recognizer._ctx);
		ATNState s = recognizer.getInterpreter().atn.states.get(recognizer.getState());
		PredicateTransition trans = (PredicateTransition)s.transition(0);
		this.ruleIndex = trans.ruleIndex;
		this.predicateIndex = trans.predIndex;
		this.predicate = predicate;
		this.setOffendingToken(recognizer, recognizer.getCurrentToken());
	}

	public int getRuleIndex() {
		return ruleIndex;
	}

	public int getPredIndex() {
		return predicateIndex;
	}

	@Nullable
	public String getPredicate() {
		return predicate;
	}

	@NotNull
	private static String formatMessage(@Nullable String predicate, @Nullable String message) {
		if (message != null) {
			return message;
		}

		return String.format("failed predicate: {%s}?", predicate);
	}
}
