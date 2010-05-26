package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.PlusBlockStartState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1PlusBlockSingleAlt extends LL1Loop {
	public ThrowEarlyExitException earlyExitError;
	public Sync iterationSync;

	public LL1PlusBlockSingleAlt(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);

		PlusBlockStartState plus = (PlusBlockStartState)blkAST.nfaState;
		this.decision = plus.loopBackState.decision;
		DFA dfa = factory.g.decisionDFAs.get(decision);
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		IntervalSet exitLook = altLookSets[1];
		IntervalSet loopBackLook = altLookSets[2];
		expr = addCodeForLoopLookaheadTempVar(loopBackLook);

		this.sync = new Sync(factory, blkAST, loopBackLook, decision, "enter");
		this.earlyExitError = new ThrowEarlyExitException(factory, blkAST, loopBackLook);
		IntervalSet iterationExpected = (IntervalSet) loopBackLook.or(exitLook);
		iterationSync = new Sync(factory, blkAST, iterationExpected, decision, "iter");		
	}
}
