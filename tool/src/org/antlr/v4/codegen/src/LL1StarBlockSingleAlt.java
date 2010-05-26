package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.StarBlockStartState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1StarBlockSingleAlt extends LL1Loop {
	public LL1StarBlockSingleAlt(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);

		StarBlockStartState star = (StarBlockStartState)blkAST.nfaState;
		this.decision = star.loopBackState.decision;
		DFA dfa = factory.g.decisionDFAs.get(decision);
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		IntervalSet exitLook = altLookSets[1];
		IntervalSet loopBackLook = altLookSets[2];
		expr = addCodeForLoopLookaheadTempVar(loopBackLook);

		IntervalSet enterExpecting = (IntervalSet)loopBackLook.or(exitLook);
		this.sync = new Sync(factory, blkAST, enterExpecting, decision, "iter");
	}
}
