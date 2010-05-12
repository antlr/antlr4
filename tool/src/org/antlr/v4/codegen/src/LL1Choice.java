package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** (A | B | C) */
public class LL1Choice extends Choice {
	/** Token names for each alt 0..n-1 */
	public List<String[]> altLook;
	/** Lookahead for each alt 1..n */
	public IntervalSet[] altLookSets;
	public LL1Choice(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		DFA dfa = factory.g.decisionDFAs.get(decision);
		altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		altLook = new ArrayList<String[]>();
		for (int a=1; a<altLookSets.length; a++) {
			IntervalSet s = altLookSets[a];
			altLook.add(factory.gen.target.getTokenTypesAsTargetLabels(factory.g, s.toArray()));
		}
	}
}
