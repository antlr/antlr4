package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.parse.ANTLRParser;
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

		if ( g.getType() == ANTLRParser.LEXER ) {
			LexerNFAToDFAConverter conv = new LexerNFAToDFAConverter(g);
			DFA dfa = conv.createDFA();
			g.setLookaheadDFA(0, dfa); // only one decision			

			System.out.println("MINIMIZE");
			int before = dfa.stateSet.size();
			DFAMinimizer dmin = new DFAMinimizer(dfa);
			dfa.minimized = dmin.minimize();
			int after = dfa.stateSet.size();
			if ( after < before ) {
				System.out.println("DFA minimized from "+before+" to "+after+" states");
			}
			return;
		}

		// BUILD DFA FOR EACH DECISION IN NONLEXER
		for (DecisionState s : g.nfa.decisionToNFAState) {
			System.out.println("\nDECISION "+s.decision);

			// TRY LINEAR APPROX FIXED LOOKAHEAD FIRST
			LinearApproximator lin = new LinearApproximator(g, s.decision);
			DFA dfa = lin.createDFA(s);

			// IF NOT LINEAR APPROX, TRY NFA TO DFA CONVERSION
			if ( dfa==null ) {
				dfa = createDFA(s);
			}
			g.setLookaheadDFA(s.decision, dfa);
		}
	}

	public DFA createDFA(DecisionState s) {
		// TRY STACK LIMITED LL(*) ANALYSIS
		PredictionDFAFactory conv = new PredictionDFAFactory(g, s);
		DFA dfa = conv.createDFA();
		System.out.print("DFA="+dfa);

 		if ( !dfa.valid() ) {
			System.out.print("invalid DFA");
		}

		conv.issueAmbiguityWarnings();

		// MINIMIZE DFA
		System.out.println("MINIMIZE");
		int before = dfa.stateSet.size();
		DFAMinimizer dmin = new DFAMinimizer(dfa);
		dfa.minimized = dmin.minimize();
		int after = dfa.stateSet.size();
		if ( after < before ) {
			System.out.println("DFA minimized from "+before+" to "+after+" states");
		}

		return dfa;
	}
}
