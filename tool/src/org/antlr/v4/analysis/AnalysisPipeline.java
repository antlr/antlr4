package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.automata.TokensStartState;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;

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
//		if ( g.isLexer() ) processLexer();
//		else processParserOrTreeParser();
		// TODO: don't do lexers for now; we can add lookahead analysis to help with NFA simulation later
		if ( !g.isLexer() ) processParserOrTreeParser();
	}

	void processLexer() {
		LexerGrammar lg = (LexerGrammar)g;
		for (String modeName : lg.modes.keySet()) {
			LexerNFAToDFAConverter conv = new LexerNFAToDFAConverter(lg);
			DFA dfa = conv.createDFA(modeName);
			TokensStartState startState = g.nfa.modeToStartState.get(modeName);
			g.setLookaheadDFA(startState.decision, dfa);

			if ( g.tool.minimizeDFA ) {
				int before = dfa.stateSet.size();
				DFAMinimizer dmin = new DFAMinimizer(dfa);
				dfa.minimized = dmin.minimize();
				int after = dfa.stateSet.size();
				if ( after < before ) {
					System.out.println("DFA minimized from "+before+" to "+after+" states");
				}
			}
		}
	}

	void processParserOrTreeParser() {
		for (DecisionState s : g.nfa.decisionToNFAState) {
			System.out.println("\nDECISION "+s.decision);

			// TRY LINEAR APPROX FIXED LOOKAHEAD FIRST
			LinearApproximator lin = new LinearApproximator(g);
			DFA dfa = lin.createDFA(s);

			// IF NOT LINEAR APPROX, TRY NFA TO DFA CONVERSION
			if ( dfa==null ) {
				dfa = createDFA(s);
			}
			g.setLookaheadDFA(s.decision, dfa);
		}
	}

	public DFA createDFA(DecisionState s) {
		PredictionDFAFactory conv = new PredictionDFAFactory(g, s);
		DFA dfa = conv.createDFA();
		System.out.print("DFA="+dfa);

 		if ( !dfa.valid() ) {
			System.out.println("invalid DFA");
		}

		conv.issueAmbiguityWarnings();

		// MINIMIZE DFA
		if ( g.tool.minimizeDFA ) {
			System.out.println("MINIMIZE");
			int before = dfa.stateSet.size();
			DFAMinimizer dmin = new DFAMinimizer(dfa);
			dfa.minimized = dmin.minimize();
			int after = dfa.stateSet.size();
			if ( after < before ) {
				System.out.println("DFA minimized from "+before+" to "+after+" states");
			}
		}

		return dfa;
	}
}
