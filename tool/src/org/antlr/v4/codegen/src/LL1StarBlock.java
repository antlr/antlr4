package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.StarBlockStartState;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1StarBlock extends LL1Choice {
	public String loopLabel;
	public String[] exitLook;
	public LL1StarBlock(CodeGenerator gen, GrammarAST blkAST, List<CodeBlock> alts) {
		// point at choice block inside outermost enter-exit choice
		super(gen, ((StarBlockStartState)blkAST.nfaState).transition(0).target.ast, alts);
		StarBlockStartState starStart = (StarBlockStartState)blkAST.nfaState;
		int enterExitDecision = starStart.decision;
//		BlockStartState blkStart = (BlockStartState)starStart.transition(0).target;
//		this.decision = blkStart.decision;
		int loopbackDecision = starStart.loopBackState.decision;

		DFA dfa = gen.g.decisionDFAs.get(enterExitDecision);
		IntervalSet exitLook = dfa.startState.edge(1).label;
		this.exitLook = gen.target.getTokenTypesAsTargetLabels(gen.g, exitLook.toArray());

		loopLabel = gen.getLoopLabel(blkAST);
	}

}
