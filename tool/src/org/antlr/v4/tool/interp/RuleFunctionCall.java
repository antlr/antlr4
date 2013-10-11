package org.antlr.v4.tool.interp;

import org.antlr.v4.runtime.ParserRuleContext;

/** track stuff normally tracked by generated parser rule function. For
 * non-recursive funcs, we see:

	public final TContext t() throws RecognitionException {
		 TContext _localctx = new TContext(_ctx, getState());

 For recursive funcs, we see:

	 public final EContext e(int _p) throws RecognitionException {
		   ParserRuleContext _parentctx = _ctx;
		 int _parentState = getState();
		 EContext _localctx = new EContext(_ctx, _parentState, _p);
		 EContext _prevctx = _localctx;
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
