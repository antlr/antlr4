package org.antlr.v4.codegen.src;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.PlusBlockStartState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1PlusBlock extends LL1Loop {
	public String loopLabel;
	public String loopCounterVar;
	public String[] exitLook;
	public ThrowEarlyExitException earlyExitError;

	public LL1PlusBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		PlusBlockStartState plusStart = (PlusBlockStartState)blkAST.nfaState;
		int enterExitDecision = plusStart.decision;

		DFA dfa = factory.g.decisionDFAs.get(enterExitDecision);
		IntervalSet exitLook = dfa.startState.edge(1).label;
		this.exitLook = factory.gen.target.getTokenTypesAsTargetLabels(factory.g, exitLook.toArray());

		loopLabel = factory.gen.target.getLoopLabel(blkAST);
		loopCounterVar = factory.gen.target.getLoopCounter(blkAST);

		this.earlyExitError = new ThrowEarlyExitException(factory, blkAST, expecting);
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{
//			if ( sup!=null ) addAll(sup); add("earlyExitError");
//		}};
//	}	
}
