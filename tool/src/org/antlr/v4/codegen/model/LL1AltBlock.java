package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** (A | B | C) */
public class LL1AltBlock extends LL1Choice {
	public LL1AltBlock(OutputModelFactory factory, GrammarAST blkAST, List<SrcOp> alts) {
		super(factory, blkAST, alts);
		this.decision = ((DecisionState)blkAST.atnState).decision;

		/** Lookahead for each alt 1..n */
//		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		IntervalSet[] altLookSets = factory.getGrammar().decisionLOOK.get(decision);
		altLook = getAltLookaheadAsStringLists(altLookSets);

		IntervalSet expecting = IntervalSet.or(altLookSets); // combine alt sets
		this.error = new ThrowNoViableAlt(factory, blkAST, expecting);
		System.out.println(blkAST.toStringTree()+" LL1AltBlock expecting="+expecting);
	}
}
