package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.tool.ErrorManager;
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
			System.out.println("\nDECISION "+s.decision);
			DFA dfa = createDFA(s);
			g.setLookaheadDFA(s.decision, dfa);
		}
	}

	public DFA createDFA(DecisionState s) {
		// TRY APPROXIMATE LL(*) ANALYSIS
		StackLimitedNFAToDFAConverter conv = new StackLimitedNFAToDFAConverter(g, s);
		DFA dfa = conv.createDFA();
		System.out.print("DFA="+dfa);
		if ( dfa.isAmbiguous() ) System.out.println("ambiguous");
		else System.out.println("NOT ambiguous");

		if ( dfa.valid() ) System.out.println("stack limited valid");

		if ( dfa.valid() ) {
			conv.issueAmbiguityWarnings(); // ambig / unreachable errors

			System.out.println("MINIMIZE");
			DFAMinimizer dmin = new DFAMinimizer(dfa);
			dmin.minimize();

			return dfa;
		}
		
		// Only do recursion limited version if we get dangling states in stack
		// limited version.  Ambiguities are ok because if the approx version
		// gets an ambiguity it's defin
		
		// REAL LL(*) ANALYSIS IF THAT FAILS
		conv = new RecursionLimitedNFAToDFAConverter(g, s);
		try {
			dfa = conv.createDFA();
			System.out.print("DFA="+dfa);
		}
		catch (RecursionOverflowSignal ros) {
			ErrorManager.recursionOverflow(g.fileName, dfa, ros.state, ros.altNum, ros.depth);
		}
		catch (MultipleRecursiveAltsSignal mras) {
			ErrorManager.multipleRecursiveAlts(g.fileName, dfa, mras.recursiveAltSet);
		}
		catch (AnalysisTimeoutSignal at) {// TODO: nobody throws yet
			ErrorManager.analysisTimeout();
		}

		conv.issueAmbiguityWarnings(); // ambig / unreachable errors
		//conv.issueRecursionWarnings();
		if ( !dfa.valid() ) {
			System.out.println("non-LL(*)");
			System.out.println("recursion limited NOT valid");
		}
		else System.out.println("recursion limited valid");

		System.out.println("MINIMIZE");
		DFAMinimizer dmin = new DFAMinimizer(dfa);
		dmin.minimize();
		
		return dfa;
	}

}
