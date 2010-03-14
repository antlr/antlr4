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
		StackLimitedNFAToDFAConverter approxConv = new StackLimitedNFAToDFAConverter(g, s);
		DFA dfa = approxConv.createDFA();
		System.out.println("DFA="+dfa);
		if ( dfa.isDeterministic() ) {
			System.out.println("deterministic :)");
			return dfa;
		}
		else System.out.println("nondeterministic!!!!!!!");

		// TODO: is it ok to have unreachable alts in approx? maybe we don't need to do full LL(*)
		
		// REAL LL(*) ANALYSIS IF THAT FAILS
		RecursionLimitedNFAToDFAConverter conv = new RecursionLimitedNFAToDFAConverter(g, s);
		dfa = conv.createDFA();
		System.out.println("DFA="+dfa);
		if ( dfa.isDeterministic() ) {
			System.out.println("recursion limited deterministic :)");
			return dfa;
		}
		else System.out.println("recursion limited nondeterministic!!!!!!!");

		return dfa;
	}
}
