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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.*;

public class LL1Analyzer {
	/** Used during LOOK to detect computation cycles. E.g., ()* causes
	 *  infinite loop without it.  If we get to same state would be infinite
	 *  loop.
	 */

	public ATN atn;

	public LL1Analyzer(ATN atn) { this.atn = atn; }

	/** From an ATN state, s, find the set of all labels reachable from s at
	 *  depth k.  Only for DecisionStates.
	 */
	public IntervalSet[] getDecisionLookahead(ATNState s) {
//		System.out.println("LOOK("+s.stateNumber+")");
		if ( s==null ) return null;
		IntervalSet[] look = new IntervalSet[s.getNumberOfTransitions()+1];
		Set<ATNConfig> lookBusy = new HashSet<ATNConfig>();
		for (int alt=1; alt<=s.getNumberOfTransitions(); alt++) {
			look[alt] = new IntervalSet();
			lookBusy.clear();
			_LOOK(s.transition(alt - 1).target, RuleContext.EMPTY, look[alt], lookBusy);
		}
		return look;
	}

	public IntervalSet LOOK(ATNState s, RuleContext ctx) {
		IntervalSet r = new IntervalSet();
		_LOOK(s, ctx, r, new HashSet<ATNConfig>());
		return r;
	}

	protected void _LOOK(ATNState s, RuleContext ctx, IntervalSet look,
						 Set<ATNConfig> lookBusy) {
//		System.out.println("_LOOK("+s.stateNumber+", ctx="+ctx);
		ATNConfig c = new ATNConfig(s, 0, ctx);
		if ( lookBusy.contains(c) ) return;
		lookBusy.add(c);

		if ( s instanceof RuleStopState && ctx != null && ctx.invokingState!=-1 ) {
			ATNState invokingState = atn.states.get(ctx.invokingState);
			RuleTransition rt = (RuleTransition)invokingState.transition(0);
			ATNState retState = rt.followState;
//			System.out.println("popping back to "+retState);
			_LOOK(retState, ctx.parent, look, lookBusy);
			return;
		}

		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition ) {
				RuleContext newContext =
					new RuleContext(ctx, s.stateNumber,  t.target.stateNumber);
				_LOOK(t.target, newContext, look, lookBusy);
			}
			else if ( t.isEpsilon() ) {
				_LOOK(t.target, ctx, look, lookBusy);
			}
			else {
//				System.out.println("adding "+ t);
				look.addAll(t.label());
			}
		}
	}

}
