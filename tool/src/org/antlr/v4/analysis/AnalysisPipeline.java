package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.tool.Grammar;

public class AnalysisPipeline {
	public Grammar g;

	public AnalysisPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		// LEFT-RECURSION CHECK
		LeftRecursionDetector lr = new LeftRecursionDetector(g.nfa);
		lr.check();
		if ( lr.listOfRecursiveCycles.size()>0 ) return; // bail out

		// BUILD DFA FOR EACH DECISION
		for (DecisionState s : g.nfa.decisionToNFAState) {
			DFA dfa = createDFA(s);
			g.setLookaheadDFA(s.decision, dfa);
		}
	}

	public DFA createDFA(DecisionState s) {
		// TRY APPROXIMATE LL(*) ANALYSIS
		NFAToApproxDFAConverter approxConv = new NFAToApproxDFAConverter(g, s);
		DFA dfa = approxConv.createDFA();
		System.out.println("DFA="+dfa);
		
		// REAL LL(*) ANALYSIS IF THAT FAILS
		NFAToExactDFAConverter conv = new NFAToExactDFAConverter(g, s);
//		DFA dfa = conv.createDFA();
//		System.out.println("DFA="+dfa);

		return dfa;
	}
}
