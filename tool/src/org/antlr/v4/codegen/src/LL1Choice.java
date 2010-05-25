package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** (A | B | C) */
public class LL1Choice extends Choice {
	/** Token names for each alt 0..n-1 */
	public List<String[]> altLook;
	public ThrowNoViableAlt error;

	public LL1Choice(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts, ((DecisionState)blkAST.nfaState).decision);
		DFA dfa = factory.g.decisionDFAs.get(decision);
		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		altLook = getAltLookaheadAsStringLists(altLookSets);
		this.error = new ThrowNoViableAlt(factory, blkAST, expecting);
	}
}
