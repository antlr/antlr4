/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool.interp;

import org.antlr.v4.runtime.ParserRuleContext;

/** These objects are like stack activation records for a recursive-descent
 *  parser.  It tracks the locals used by the generated rule function code
 *  and also includes the integer precedence argument for left recursive
 *  rules. For non-recursive funcs, we see:

	  public final TContext t() throws RecognitionException {
		 TContext _localctx = new TContext(_ctx, getState());

    For recursive funcs, we see:

	  public final EContext e(int _p) throws RecognitionException {
		 ParserRuleContext _parentctx = _ctx;
		 int _parentState = getState();
		 EContext _localctx = new EContext(_ctx, _parentState, _p);
		 EContext _prevctx = _localctx; <-- not tracked; only for labeled alts
		 int _startState = 0;

 Cannot merge with InterpreterRuleContext due to recursive rule functions.
 They need one RuleFunctionCall but construct lots of InterpreterRuleContext
 to simulate recursive calls.

 Push one of these when we truly enter a rule function by traversing
 a call edge.
 */
public class RuleFunctionCall {
	/** To pop, we set ptr to parent */
	RuleFunctionCall parent;
	public RuleFunctionCall(RuleFunctionCall parent) { this.parent = parent; }

	// needed for both kinds of rule functions
	InterpreterRuleContext _localctx;

	// needed for recursive rule functions
	ParserRuleContext _parentctx;
	int _parentState;
	//ParserRuleContext _prevctx; // needed for labeled alts
	int _startState;
	int _prec; // precedence argument
}
