package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.StarBlockStartState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1StarBlock extends LL1Loop {
	public String loopLabel;
	public String[] exitLook;
	public LL1StarBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		// point at choice block inside outermost enter-exit choice
		super(factory, ((StarBlockStartState)blkAST.nfaState).transition(0).target.ast, alts);
		StarBlockStartState starStart = (StarBlockStartState)blkAST.nfaState;
		int enterExitDecision = starStart.decision;
//		BlockStartState blkStart = (BlockStartState)starStart.transition(0).target;
//		this.decision = blkStart.decision;
		int loopbackDecision = starStart.loopBackState.decision;

		DFA dfa = factory.g.decisionDFAs.get(enterExitDecision);
		IntervalSet exitLook = dfa.startState.edge(1).label;
		this.exitLook = factory.gen.target.getTokenTypesAsTargetLabels(factory.g, exitLook.toArray());

		loopLabel = factory.getLoopLabel(blkAST);
	}

}
