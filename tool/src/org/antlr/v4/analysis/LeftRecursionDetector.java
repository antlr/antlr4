package org.antlr.v4.analysis;

import org.antlr.v4.misc.OrderedHashSet;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.tool.Rule;

import java.util.*;

public class LeftRecursionDetector {
	public ATN atn;

	/** Holds a list of cycles (sets of rule names). */
	public List<Set<Rule>> listOfRecursiveCycles = new ArrayList<Set<Rule>>();

	/** Which rule start states have we visited while looking for a single
	 * 	left-recursion check?
	 */
	Set<RuleStartState> rulesVisitedPerRuleCheck = new HashSet<RuleStartState>();

	public LeftRecursionDetector(ATN atn) {	this.atn = atn; }

	public void check() {
		for (RuleStartState start : atn.ruleToStartState.values()) {
			//System.out.print("check "+start.rule.name);
			rulesVisitedPerRuleCheck.clear();
			rulesVisitedPerRuleCheck.add(start);
			//FASerializer ser = new FASerializer(atn.g, start);
			//System.out.print(":\n"+ser+"\n");

			check(start.rule, start, new HashSet<ATNState>());
		}
		//System.out.println("cycles="+listOfRecursiveCycles);
		if ( listOfRecursiveCycles.size()>0 ) {
			atn.g.tool.errMgr.leftRecursionCycles(atn.g.fileName, listOfRecursiveCycles);
		}
	}

	/** From state s, look for any transition to a rule that is currently
	 *  being traced.  When tracing r, visitedPerRuleCheck has r
	 *  initially.  If you reach a rule stop state, return but notify the
	 *  invoking rule that the called rule is nullable. This implies that
	 *  invoking rule must look at follow transition for that invoking state.
	 *
	 *  The visitedStates tracks visited states within a single rule so
	 *  we can avoid epsilon-loop-induced infinite recursion here.  Keep
	 *  filling the cycles in listOfRecursiveCycles and also, as a
	 *  side-effect, set leftRecursiveRules.
	 */
	public boolean check(Rule enclosingRule, ATNState s, Set<ATNState> visitedStates) {
		if ( s instanceof RuleStopState) return true;
		if ( visitedStates.contains(s) ) return false;
		visitedStates.add(s);

		//System.out.println("visit "+s);
		int n = s.getNumberOfTransitions();
		boolean stateReachesStopState = false;
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition ) {
				RuleTransition rt = (RuleTransition) t;
				Rule r = rt.rule;
				if ( rulesVisitedPerRuleCheck.contains((RuleStartState)t.target) ) {
					addRulesToCycle(enclosingRule, r);
				}
				else {
					// must visit if not already visited; mark target, pop when done
					rulesVisitedPerRuleCheck.add((RuleStartState)t.target);
					// send new visitedStates set per rule invocation
					boolean nullable = check(r, t.target, new HashSet<ATNState>());
					// we're back from visiting that rule
					rulesVisitedPerRuleCheck.remove((RuleStartState)t.target);
					if ( nullable ) {
						stateReachesStopState |= check(enclosingRule, rt.followState, visitedStates);
					}
				}
			}
			else if ( t.isEpsilon() ) {
				stateReachesStopState |= check(enclosingRule, t.target, visitedStates);
			}
			// else ignore non-epsilon transitions
		}
		return stateReachesStopState;
	}

	/** enclosingRule calls targetRule. Find the cycle containing
	 *  the target and add the caller.  Find the cycle containing the caller
	 *  and add the target.  If no cycles contain either, then create a new
	 *  cycle.
	 */
	protected void addRulesToCycle(Rule enclosingRule, Rule targetRule) {
		//System.err.println("left-recursion to "+targetRule.name+" from "+enclosingRule.name);
		boolean foundCycle = false;
		for (int i = 0; i < listOfRecursiveCycles.size(); i++) {
			Set<Rule> rulesInCycle = listOfRecursiveCycles.get(i);
			// ensure both rules are in same cycle
			if ( rulesInCycle.contains(targetRule) ) {
				rulesInCycle.add(enclosingRule);
				foundCycle = true;
			}
			if ( rulesInCycle.contains(enclosingRule) ) {
				rulesInCycle.add(targetRule);
				foundCycle = true;
			}
		}
		if ( !foundCycle ) {
			Set<Rule> cycle = new OrderedHashSet<Rule>();
			cycle.add(targetRule);
			cycle.add(enclosingRule);
			listOfRecursiveCycles.add(cycle);
		}
	}
}
