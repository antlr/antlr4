package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.BlockStartState;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.PlusBlockStartState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1PlusBlock extends LL1Loop {
	/** Token names for each alt 0..n-1 */
	public List<String[]> altLook;

	public Sync iterationSync;	
	public String loopLabel;
	public String loopCounterVar;
	public String[] exitLook;
	public ThrowEarlyExitException earlyExitError;

	public LL1PlusBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);

		PlusBlockStartState plus = (PlusBlockStartState)blkAST.nfaState;
		BlockStartState blkStart = (BlockStartState)plus.transition(0).target;

		this.decision = blkStart.decision;
		DFA dfa = factory.g.decisionDFAs.get(blkStart.decision);
		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		altLook = getAltLookaheadAsStringLists(altLookSets);

		loopLabel = factory.gen.target.getLoopLabel(blkAST);
		loopCounterVar = factory.gen.target.getLoopCounter(blkAST);

		DFA loopbackDFA = factory.g.decisionDFAs.get(plus.loopBackState.decision);
		altLookSets = LinearApproximator.getLL1LookaheadSets(loopbackDFA);
		IntervalSet exitLookSet = altLookSets[1];
		IntervalSet loopBackLook = altLookSets[2];
		this.exitLook = factory.gen.target.getTokenTypesAsTargetLabels(factory.g,
																	   exitLookSet.toArray());

		IntervalSet iterationExpected = (IntervalSet)loopBackLook.or(exitLookSet);
		this.sync = new Sync(factory, blkAST, loopBackLook, decision, "enter");
		this.iterationSync = new Sync(factory, blkAST, iterationExpected, decision, "iter");
		this.earlyExitError = new ThrowEarlyExitException(factory, blkAST, iterationExpected);
	}
}
