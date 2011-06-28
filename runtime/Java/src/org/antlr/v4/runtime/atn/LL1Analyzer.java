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
