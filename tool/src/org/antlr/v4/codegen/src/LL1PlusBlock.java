package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.PlusBlockStartState;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1PlusBlock extends LL1Choice {
	public String loopLabel;
	public String loopCounterVar;
	public String[] exitLook;
	public LL1PlusBlock(CodeGenerator gen, GrammarAST blkAST, List<CodeBlock> alts) {
		super(gen, blkAST, alts);
		PlusBlockStartState plusStart = (PlusBlockStartState)blkAST.nfaState;
		int enterExitDecision = plusStart.decision;

		DFA dfa = gen.g.decisionDFAs.get(enterExitDecision);
		IntervalSet exitLook = dfa.startState.edge(1).label;
		this.exitLook = gen.target.getTokenTypesAsTargetLabels(gen.g, exitLook.toArray());

		loopLabel = gen.getLoopLabel(blkAST);
		loopCounterVar = gen.getLoopCounter(blkAST);
	}
}
