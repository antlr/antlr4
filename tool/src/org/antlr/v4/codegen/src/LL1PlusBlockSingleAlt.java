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

	public LL1PlusBlockSingleAlt(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts, ((PlusBlockStartState)blkAST.nfaState).loopBackState.decision);
		PlusBlockStartState plus = (PlusBlockStartState)blkAST.nfaState;
		DFA dfa = factory.g.decisionDFAs.get(plus.loopBackState.decision);
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		IntervalSet loopBackLook = altLookSets[2]; // loop exit is alt 1
		addCodeForLookaheadTempVar(loopBackLook);
		this.earlyExitError = new ThrowEarlyExitException(factory, blkAST, expecting);
	}
}
