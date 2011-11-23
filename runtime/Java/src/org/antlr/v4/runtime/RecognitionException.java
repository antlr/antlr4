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

import org.antlr.v4.runtime.misc.IntervalSet;

/** The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
 *  3 kinds of errors: prediction errors, failed predicate errors, and
 *  mismatched input errors. In each case, the parser knows where it is
 *  in the input, where it is in the ATN, the rule invocation stack,
 *  and what kind of problem occurred.
 */
public class RecognitionException extends RuntimeException {
	/** Who threw the exception? */
	protected Recognizer<?, ?> recognizer;

	// TODO: make a dummy recognizer for the interpreter to use?
	// Next two (ctx,input) should be what is in recognizer, but
	// won't work when interpreting

	protected RuleContext ctx;

	protected IntStream input;

	/** What is index of token/char were we looking at when the error occurred? */
//	public int offendingTokenIndex;

	/** The current Token when an error occurred.  Since not all streams
	 *  can retrieve the ith Token, we have to track the Token object.
	 *  For parsers.  Even when it's a tree parser, token might be set.
	 */
	protected Token offendingToken;

	/** If this is a tree parser exception, node is set to the node with
	 *  the problem.
	 */
	protected Object offendingNode;

	protected int offendingState;

	public RecognitionException(Recognizer<?, ?> recognizer, IntStream input,
								RuleContext ctx)
	{
		this.recognizer = recognizer;
		this.input = input;
		this.ctx = ctx;
		if ( ctx!=null ) this.offendingState = ctx.s;
	}

	/** Where was the parser in the ATN when the error occurred?
	 *  For No viable alternative exceptions, this is the decision state number.
	 *  For others, it is the state whose emanating edge we couldn't match.
	 *  This will help us tie into the grammar and syntax diagrams in
	 *  ANTLRWorks v2.
	 */
	public int getOffendingState() { return offendingState; }

	public IntervalSet getExpectedTokens() {
        // TODO: do we really need this type check?
		if ( recognizer!=null && recognizer instanceof BaseRecognizer) {
			return recognizer.getInterpreter().atn.nextTokens(ctx);
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

	public Recognizer<?, ?> getRecognizer() {
		return recognizer;
	}

	//	/** Return the token type or char of the unexpected input element */
//	public int getUnexpectedType() {
//		if ( recognizer==null ) return offendingToken.getType();
//		if ( recognizer.getInputStream() instanceof TokenStream) {
//			return offendingToken.getType();
//		}
//		else if ( recognizer.getInputStream() instanceof ASTNodeStream) {
//			ASTNodeStream nodes = (ASTNodeStream)recognizer.getInputStream();
//			ASTAdaptor adaptor = nodes.getTreeAdaptor();
//			return adaptor.getType(offendingNode);
//		}
//		return Token.INVALID_TYPE;
//	}
}
