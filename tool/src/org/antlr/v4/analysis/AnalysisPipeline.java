package org.antlr.v4.analysis;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;

import java.util.*;

public class AnalysisPipeline {
	public Grammar g;

	public AnalysisPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		// LEFT-RECURSION CHECK
		LeftRecursionDetector lr = new LeftRecursionDetector(g, g.atn);
		lr.check();
		if ( lr.listOfRecursiveCycles.size()>0 ) return; // bail out

		// BUILD DFA FOR EACH DECISION
		if ( !g.isLexer() ) processParserOrTreeParser();
	}

	void processParserOrTreeParser() {
		g.decisionLOOK =
			new Vector<IntervalSet[]>(g.atn.getNumberOfDecisions()+1);
		for (DecisionState s : g.atn.decisionToATNState) {
			System.out.println("\nDECISION "+s.decision+" in rule "+s.rule.name);

			LL1Analyzer anal = new LL1Analyzer(g.atn);
			IntervalSet[] look = anal.getDecisionLookahead(s);
			System.out.println("look="+ Arrays.toString(look));
			g.decisionLOOK.setSize(s.decision+1);
			g.decisionLOOK.set(s.decision, look);
			System.out.println("LL(1)? "+disjoint(look));
		}
	}

	/** Return lookahead depth at which lookahead sets are disjoint or return 0 */
	public static boolean disjoint(IntervalSet[] altLook) {
		boolean collision = false;
		IntervalSet combined = new IntervalSet();
		for (int a=1; a<altLook.length; a++) {
			IntervalSet look = altLook[a];
			if ( !look.and(combined).isNil() ) {
				System.out.println("alt "+a+" not disjoint with "+combined+"; look = "+look);
				collision = true;
				break;
			}
			combined.addAll(look);
		}
		return !collision;
	}
}
