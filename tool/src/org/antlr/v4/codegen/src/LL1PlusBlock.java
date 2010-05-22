package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.BlockStartState;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.PlusBlockStartState;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class LL1PlusBlock extends LL1Loop {
	/** Token names for each alt 0..n-1 */
	public List<String[]> altLook;
	/** Lookahead for each alt 1..n */
	public IntervalSet[] altLookSets;
	
	public String loopLabel;
	public String loopCounterVar;
	public String[] exitLook;
	public ThrowEarlyExitException earlyExitError;

	public LL1PlusBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		PlusBlockStartState plus = (PlusBlockStartState)blkAST.nfaState;
		BlockStartState blkStart = (BlockStartState)plus.transition(0).target;

		DFA dfa = factory.g.decisionDFAs.get(blkStart.decision);
		altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		altLook = new ArrayList<String[]>();
		for (int a=1; a<altLookSets.length; a++) {
			IntervalSet s = altLookSets[a];
			altLook.add(factory.gen.target.getTokenTypesAsTargetLabels(factory.g, s.toArray()));
		}

		dfa = factory.g.decisionDFAs.get(plus.loopBackState.decision);
		IntervalSet exitLook = dfa.startState.edge(0).label;
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
