/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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

/** The set of fields needed by an abstract recognizer to recognize input
 *  and recover from errors etc...  As a separate state object, it can be
 *  shared among multiple grammars; e.g., when one grammar imports another.
 */
public class ParserSharedState extends RecognizerSharedState<TokenStream> {
	/** First on stack is fake a call to start rule from S' : S EOF ;
	 *  Generated start rule does this.
	 */
//	public QStack<RuleContext> ctx;
	public ParserRuleContext ctx; // tracks local _ctx var to see from outside

	/** This is true when we see an error and before having successfully
	 *  matched a token.  Prevents generation of more than one error message
	 *  per error.
	 */
	public boolean errorRecovery = false;

	/** The index into the input stream where the last error occurred.
	 * 	This is used to prevent infinite loops where an error is found
	 *  but no token is consumed during recovery...another error is found,
	 *  ad naseum.  This is a failsafe mechanism to guarantee that at least
	 *  one token/tree node is consumed for two errors.
	 */
	public int lastErrorIndex = -1;

	/** In lieu of a return value, this indicates that a rule or token
	 *  has failed to match.  Reset to false upon valid token match.
	 */
	public boolean failed = false;

	/** Did the recognizer encounter a syntax error?  Track how many. */
	public int syntaxErrors = 0;

	/** If 0, no backtracking is going on.  Safe to exec actions etc...
	 *  If >0 then it's the level of backtracking.
	 */
//	public int backtracking = 0;

	/** An array[size num rules] of Map<Integer,Integer> that tracks
	 *  the stop token index for each rule.  ruleMemo[ruleIndex] is
	 *  the memoization table for ruleIndex.  For key ruleStartIndex, you
	 *  get back the stop token for associated rule or MEMO_RULE_FAILED.
	 *
	 *  This is only used if rule memoization is on (which it is by default).
	 */
//	public Map[] ruleMemo;

	public ParserSharedState() {
//        ctx = new RuleContext(); // implicit call to start rule
	}

	@Override
	public ParserRuleContext getContext() {
		return ctx;
	}

	//    public RecognizerSharedState(RecognizerSharedState state) {
//		this.ctx = state.ctx;
//		this.errorRecovery = state.errorRecovery;
//        this.lastErrorIndex = state.lastErrorIndex;
//        this.failed = state.failed;
//        this.syntaxErrors = state.syntaxErrors;
//        this.backtracking = state.backtracking;
//        if ( state.ruleMemo!=null ) {
//            this.ruleMemo = new Map[state.ruleMemo.length];
//            System.arraycopy(state.ruleMemo, 0, this.ruleMemo, 0, state.ruleMemo.length);
//        }
//    }
}
