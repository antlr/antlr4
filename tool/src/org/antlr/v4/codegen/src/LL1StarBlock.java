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
		// point at choice block inside outermost enter-exit choice
		super(factory,
			  ((StarBlockStartState)blkAST.nfaState).transition(0).target.ast,
			  alts,
			  ((StarBlockStartState) blkAST.nfaState).decision);			  
		StarBlockStartState star = (StarBlockStartState)blkAST.nfaState;
		int enterExitDecision = star.decision;
		BlockStartState blkStart = (BlockStartState)star.transition(0).target;

		DFA dfa = factory.g.decisionDFAs.get(blkStart.decision);
		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		altLook = getAltLookaheadAsStringLists(altLookSets);

		dfa = factory.g.decisionDFAs.get(enterExitDecision);
		IntervalSet exitLook = dfa.startState.edge(1).label;
		this.exitLook = factory.gen.target.getTokenTypesAsTargetLabels(factory.g, exitLook.toArray());

		loopLabel = factory.gen.target.getLoopLabel(blkAST);
	}

}
