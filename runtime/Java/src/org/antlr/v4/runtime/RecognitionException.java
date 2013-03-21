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

import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Nullable;

/** The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
 *  3 kinds of errors: prediction errors, failed predicate errors, and
 *  mismatched input errors. In each case, the parser knows where it is
 *  in the input, where it is in the ATN, the rule invocation stack,
 *  and what kind of problem occurred.
 */
public class RecognitionException extends RuntimeException {
	/** Who threw the exception? */
	private Recognizer<?, ?> recognizer;

	// TODO: make a dummy recognizer for the interpreter to use?
	// Next two (ctx,input) should be what is in recognizer, but
	// won't work when interpreting

	private RuleContext ctx;

	private IntStream input;

	/** The current Token when an error occurred.  Since not all streams
	 *  can retrieve the ith Token, we have to track the Token object.
	 *  For parsers.  Even when it's a tree parser, token might be set.
	 */
	private Token offendingToken;

	private int offendingState;

	public RecognitionException(@Nullable Recognizer<?, ?> recognizer, IntStream input,
								@Nullable ParserRuleContext ctx)
	{
		this.recognizer = recognizer;
		this.input = input;
		this.ctx = ctx;
		if ( recognizer!=null ) this.offendingState = recognizer.getState();
	}

	public RecognitionException(String message, @Nullable Recognizer<?, ?> recognizer, IntStream input,
								@Nullable ParserRuleContext ctx)
	{
		super(message);
		this.recognizer = recognizer;
		this.input = input;
		this.ctx = ctx;
		if ( recognizer!=null ) this.offendingState = recognizer.getState();
	}

	/** Where was the parser in the ATN when the error occurred?
	 *  For No viable alternative exceptions, this is the decision state number.
	 *  For others, it is the state whose emanating edge we couldn't match.
	 *  This will help us tie into the grammar and syntax diagrams in
	 *  ANTLRWorks v2.
	 */
	public int getOffendingState() {
		return offendingState;
	}

	protected final void setOffendingState(int offendingState) {
		this.offendingState = offendingState;
	}

	public IntervalSet getExpectedTokens() {
		if (recognizer != null) {
			return recognizer.getATN().getExpectedTokens(offendingState, ctx);
		}

		return null;
	}

	public RuleContext getCtx() {
		return ctx;
	}

	public IntStream getInputStream() {
		return input;
	}

	public Token getOffendingToken() {
		return offendingToken;
	}

	protected final void setOffendingToken(Token offendingToken) {
		this.offendingToken = offendingToken;
	}

	public Recognizer<?, ?> getRecognizer() {
		return recognizer;
	}
}
