package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.Edge;

// TODO: don't need at moment but leave; will need for code gen
public class CycleDetector {
	DFA dfa;
	boolean[] busy;

	public CycleDetector(DFA dfa) {
		this.dfa = dfa;
		busy = new boolean[dfa.stateSet.size()+1];
	}

	public boolean isCyclic() {
		return foundCycle(dfa.startState);
	}

	public boolean foundCycle(DFAState d) {
		if ( busy[d.stateNumber] ) return true;
		busy[d.stateNumber] = true;
		for (Edge e : d.edges) {
			if ( foundCycle(e.target) ) return true;
		}
		busy[d.stateNumber] = false;
		return false;
	}
}
