package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** (A | B | C) */
public class LL1AltBlock extends LL1Choice {
	public LL1AltBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		this.decision = ((DecisionState)blkAST.nfaState).decision;

		DFA dfa = factory.g.decisionDFAs.get(decision);
		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		altLook = getAltLookaheadAsStringLists(altLookSets);

		IntervalSet expecting = IntervalSet.or(altLookSets); // combine alt sets		
		this.error = new ThrowNoViableAlt(factory, blkAST, expecting);
		System.out.println(blkAST.toStringTree()+" LL1AltBlock expecting="+expecting);
	}
}
