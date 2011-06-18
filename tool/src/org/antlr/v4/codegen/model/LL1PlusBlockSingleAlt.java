package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.runtime.atn.PlusBlockStartState;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1PlusBlockSingleAlt extends LL1Loop {
	@ModelElement public Sync iterationSync;

	public LL1PlusBlockSingleAlt(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);

		PlusBlockStartState plus = (PlusBlockStartState)blkAST.atnState;
		this.decision = plus.loopBackState.decision;
		IntervalSet[] altLookSets = factory.g.decisionLOOK.get(decision);
		IntervalSet exitLook = altLookSets[altLookSets.length-1];

		IntervalSet loopBackLook = altLookSets[1];
		loopExpr = addCodeForLoopLookaheadTempVar(loopBackLook);

		this.sync = new Sync(factory, blkAST, loopBackLook, decision, "enter");
		IntervalSet iterationExpected = (IntervalSet) loopBackLook.or(exitLook);
		iterationSync = new Sync(factory, blkAST, iterationExpected, decision, "iter");
	}
}
