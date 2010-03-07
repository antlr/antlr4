package org.antlr.v4.automata;

import org.antlr.v4.tool.Grammar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** Code that embodies the NFA conversion to DFA. A new object is needed
 *  per DFA (also required for thread safety if multiple conversions
 *  launched).
 */
public class NFAToDFAConverter {
	Grammar g;

	DecisionState nfaStartState;

	/** DFA we are creating */
	DFA dfa;

	/** A list of DFA states we still need to process during NFA conversion */
	List<DFAState> work = new LinkedList<DFAState>();

	public static boolean debug = false;
	
	public NFAToDFAConverter(Grammar g, DecisionState nfaStartState) {
		this.g = g;
		this.nfaStartState = nfaStartState;
		dfa = new DFA(g, nfaStartState);
	}

	public DFA createDFA() {
		dfa.startState = computeStartState();
		dfa.addState(dfa.startState); // make sure dfa knows about this state
		work.add(dfa.startState);

		// while more DFA states to check, process them
		while ( work.size()>0 ) {

		}
		
		return dfa;
	}

	/** From this first NFA state of a decision, create a DFA.
	 *  Walk each alt in decision and compute closure from the start of that
	 *  rule, making sure that the closure does not include other alts within
	 *  that same decision.  The idea is to associate a specific alt number
	 *  with the starting closure so we can trace the alt number for all states
	 *  derived from this.  At a stop state in the DFA, we can return this alt
	 *  number, indicating which alt is predicted.
	 */
	public DFAState computeStartState() {
		DFAState d = dfa.newState();

		// add config for each alt start, then add closure for those states
		for (int altNum=1; altNum<=dfa.nAlts; altNum++) {
			Transition t = nfaStartState.transition(altNum-1);
			NFAState altStart = t.target;
			d.addNFAConfig(altStart, altNum+1, null);

		}

		closure(d);

		return d;
	}

	/** For all NFA states (configurations) merged in d,
	 *  compute the epsilon closure; that is, find all NFA states reachable
	 *  from the NFA states in d via purely epsilon transitions.
	 */
	public void closure(DFAState d) {
		if ( debug ) {
			System.out.println("closure("+d+")");
		}

		List<NFAConfig> configs = new ArrayList<NFAConfig>();
		for (NFAConfig c : d.nfaConfigs) {
			closure(c.state, c.alt, c.context, configs);
		}
		d.nfaConfigs.addAll(configs); // Add new NFA configs to DFA state d

		System.out.println("after closure d="+d);
	}

	/** Where can we get from NFA state s traversing only epsilon transitions?
	 */
	public void closure(NFAState s, int altNum, NFAState context,
						List<NFAConfig> configs)
	{
		NFAConfig proposedNFAConfig =
			new NFAConfig(s, altNum, context);

		// p itself is always in closure
		configs.add(proposedNFAConfig);

		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t.isEpsilon() ) {
				closure(t.target, altNum, context, configs);
			}
		}
	}
}
