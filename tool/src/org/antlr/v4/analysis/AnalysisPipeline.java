package org.antlr.v4.analysis;

import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.tool.Grammar;

public class AnalysisPipeline {
	public void process(Grammar g) {
		// LEFT-RECURSION CHECK
		LeftRecursionDetector lr = new LeftRecursionDetector(g.nfa);
		lr.check();
		if ( lr.listOfRecursiveCycles.size()>0 ) return; // bail out

		// BUILD DFA FOR EACH DECISION
		for (DecisionState s : g.nfa.decisionToNFAState) {
			createDFA(s);
		}
	}

	public void createDFA(DecisionState s) {
		// TRY APPROXIMATE LL(*) ANALYSIS

		// REAL LL(*) ANALYSIS IF THAT FAILS
	}
}
