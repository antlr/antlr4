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
		super(factory, blkAST, alts, ((StarBlockStartState)blkAST.nfaState).loopBackState.decision);
		StarBlockStartState star = (StarBlockStartState)blkAST.nfaState;
		DFA dfa = factory.g.decisionDFAs.get(star.loopBackState.decision);
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		IntervalSet look = altLookSets[2];
		addCodeForLookaheadTempVar(look);
	}
}
