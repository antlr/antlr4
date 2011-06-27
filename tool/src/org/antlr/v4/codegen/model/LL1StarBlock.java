package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.runtime.atn.StarBlockStartState;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1StarBlock extends LL1Loop {
	/** Token names for each alt 0..n-1 */
	public List<String[]> altLook;
	public String loopLabel;
	public String[] exitLook;

	public LL1StarBlock(OutputModelFactory factory, GrammarAST blkAST, List<SrcOp> alts) {
		super(factory, blkAST, alts);

		StarBlockStartState blkStart = (StarBlockStartState)blkAST.atnState;
		this.decision = blkStart.decision;

		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = factory.getGrammar().decisionLOOK.get(decision);
		IntervalSet lastLook = altLookSets[altLookSets.length-1];
		IntervalSet[] copy = new IntervalSet[altLookSets.length-1];
		System.arraycopy(altLookSets, 0, copy, 0, altLookSets.length-1); // remove last (exit) alt
		altLookSets = copy;
		altLook = getAltLookaheadAsStringLists(altLookSets);
		loopLabel = factory.getGenerator().target.getLoopLabel(blkAST);

		this.exitLook =
			factory.getGenerator().target.getTokenTypesAsTargetLabels(factory.getGrammar(), lastLook.toArray());

//		this.sync = new Sync(factory, blkAST, expecting, decision, "iter");
	}
}
