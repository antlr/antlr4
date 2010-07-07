package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.automata.NFA;
import org.antlr.v4.misc.BitSet;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.semantics.UseDefAnalyzer;
import org.antlr.v4.tool.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		if ( g.isLexer() ) processLexer();
		else processParserOrTreeParser();
	}

	void processLexer() {
		LexerGrammar lg = (LexerGrammar)g;
		for (String modeName : lg.modes.keySet()) {

			Set<Rule> rulesInNFA = getRulesInNFA(lg, modeName);

			LexerNFAToDFAConverter conv = new LexerNFAToDFAConverter(lg);
			DFA dfa = conv.createDFA(modeName);
			lg.modeToDFA.put(modeName, dfa);
			//TokensStartState startState = g.nfa.modeToStartState.get(modeName);
			//g.setLookaheadDFA(startState.decision, dfa);

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

	public Set<Rule> getRulesInNFA(LexerGrammar lg, String modeName) {
		Set<Rule> rulesInNFA = getRulesTooComplexForDFA(lg, modeName);
		System.out.println("rules in NFA: "+rulesInNFA);

		IntervalSet charsPredictingNFARules = new IntervalSet();
		for (Rule r : rulesInNFA) {
			if ( !r.isFragment() ) {
				LinearApproximator approx = new LinearApproximator(lg, NFA.INVALID_DECISION_NUMBER);
				IntervalSet fset = approx.FIRST(lg.nfa.ruleToStartState.get(r));
				System.out.println("first of "+r.name+"="+fset);
				charsPredictingNFARules.addAll(fset);
			}
		}
		System.out.println("charsPredictingNFARules="+charsPredictingNFARules);
		// now find any other rules that start with that set
		for (Rule r : lg.modes.get(modeName)) {
			if ( !r.isFragment() && !rulesInNFA.contains(r) ) {
				LinearApproximator approx = new LinearApproximator(lg, NFA.INVALID_DECISION_NUMBER);
				IntervalSet fset = approx.FIRST(lg.nfa.ruleToStartState.get(r));
				if ( !fset.and(charsPredictingNFARules).isNil() ) {
					System.out.println("rule "+r.name+" collides");
					rulesInNFA.add(r);
				}
			}
		}
		return rulesInNFA;
	}

	// TODO: oops. find all nongreedy loops too!	
	public Set<Rule> getRulesTooComplexForDFA(LexerGrammar lg, String modeName) {
		Set<Rule> complexRules = new HashSet<Rule>();
		Map<Rule, Set<Rule>> dep = UseDefAnalyzer.getRuleDependencies(lg, modeName);
		System.out.println("dep="+dep);
		for (Rule r : lg.modes.get(modeName)) {
			if ( dep.containsKey(r) ) { complexRules.add(r); continue; }
			BitSet labelTypes = BitSet.of(ANTLRParser.ASSIGN);
			labelTypes.add(ANTLRParser.PLUS_ASSIGN);
			List<GrammarAST> labels = r.ast.getNodesWithType(labelTypes);
			if ( labels.size()>0 ) { complexRules.add(r); continue; }
			List<GrammarAST> actions = r.ast.getNodesWithType(ANTLRParser.ACTION);
			ActionAST actionOnFarRight = r.ast.getLexerAction();
			for (GrammarAST action : actions) {
				if ( action != actionOnFarRight ) complexRules.add(r);
			}
		}
		return complexRules;
	}	
}
