package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** (A B C)? */
public class LL1OptionalBlockSingleAlt extends LL1Choice {
	public OutputModelObject expr;
	public OutputModelObject followExpr;

	public LL1OptionalBlockSingleAlt(OutputModelFactory factory,
									 GrammarAST blkAST,
									 List<CodeBlock> alts)
	{
		super(factory, blkAST, alts);
		this.decision = ((DecisionState)blkAST.atnState).decision;

		/** Lookahead for each alt 1..n */
//		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		IntervalSet[] altLookSets = factory.g.decisionLOOK.get(decision);
		altLook = getAltLookaheadAsStringLists(altLookSets);
		IntervalSet look = altLookSets[1];
		IntervalSet followLook = altLookSets[2];

		IntervalSet expecting = (IntervalSet)look.or(followLook);
		this.error = new ThrowNoViableAlt(factory, blkAST, expecting);
		System.out.println(blkAST.toStringTree()+" LL1OptionalBlockSingleAlt expecting="+expecting);

		expr = addCodeForLookaheadTempVar(look);
		followExpr = factory.getLL1Test(followLook, blkAST);
	}
}
