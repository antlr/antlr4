package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.*;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.runtime.atn.PlusBlockStartState;
import org.antlr.v4.tool.*;

import java.util.List;

/** */
public class LL1PlusBlock extends LL1Loop {
	/** Token names for each alt 0..n-1 */
	public List<String[]> altLook;

	@ModelElement public Sync iterationSync;
	public String loopLabel;
	public String loopCounterVar;
	public String[] exitLook;

	@ModelElement public SrcOp loopExpr;
	@ModelElement public ThrowNoViableAlt error;

	public LL1PlusBlock(OutputModelFactory factory, GrammarAST plusRoot, List<SrcOp> alts) {
		super(factory, plusRoot, alts);

		PlusBlockStartState blkStart = (PlusBlockStartState)plusRoot.atnState;

		this.decision = blkStart.decision;
		Grammar g = factory.getGrammar();
		CodeGenerator gen = factory.getGenerator();
		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = g.decisionLOOK.get(decision);
		altLook = getAltLookaheadAsStringLists(altLookSets);
		IntervalSet all = new IntervalSet();
		for (IntervalSet s : altLookSets) all.addAll(s);

		this.error = new ThrowNoViableAlt(factory, plusRoot, all);

		loopExpr = addCodeForLoopLookaheadTempVar(all);

		loopLabel = gen.target.getLoopLabel(plusRoot);
		loopCounterVar = gen.target.getLoopCounter(plusRoot);

		IntervalSet exitLookSet = altLookSets[altLookSets.length-1];
		this.exitLook = gen.target.getTokenTypesAsTargetLabels(g,
															   exitLookSet.toArray());

		//IntervalSet iterationExpected = (IntervalSet)loopBackLook.or(exitLookSet);
//		this.sync = new Sync(factory, plusRoot, loopBackLook, decision, "enter");
//		this.iterationSync = new Sync(factory, plusRoot, iterationExpected, decision, "iter");
//		this.earlyExitError = new ThrowEarlyExitException(factory, plusRoot, null);
	}
}
