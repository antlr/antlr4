package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.BlockStartState;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.StarBlockStartState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1StarBlock extends LL1Loop {
	/** Token names for each alt 0..n-1 */
	public List<String[]> altLook;
	public String loopLabel;
	public String[] exitLook;
	
	public LL1StarBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);

		StarBlockStartState star = (StarBlockStartState)blkAST.nfaState;
		int enterExitDecision = star.decision;
		BlockStartState blkStart = (BlockStartState)star.transition(0).target;
		this.decision = blkStart.decision;

		DFA dfa = factory.g.decisionDFAs.get(blkStart.decision);
		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		altLook = getAltLookaheadAsStringLists(altLookSets);
		loopLabel = factory.gen.target.getLoopLabel(blkAST);

		dfa = factory.g.decisionDFAs.get(enterExitDecision);
		IntervalSet exitLook = dfa.startState.edge(1).label;
		IntervalSet expecting =
			(IntervalSet)IntervalSet.or(altLookSets).addAll(exitLook);
		this.sync = new Sync(factory, blkAST, expecting, decision, "iter");
		this.exitLook =
			factory.gen.target.getTokenTypesAsTargetLabels(factory.g, exitLook.toArray());
	}
}
