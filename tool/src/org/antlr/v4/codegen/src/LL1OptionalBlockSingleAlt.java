package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.BlockStartState;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1OptionalBlockSingleAlt extends LL1Choice {
	public Object expr;
	public LL1OptionalBlockSingleAlt(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		DFA dfa = factory.g.decisionDFAs.get(((BlockStartState)blkAST.nfaState).decision);
		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = LinearApproximator.getLL1LookaheadSets(dfa);
		IntervalSet look = altLookSets[1];
		expr = factory.getLL1Test(look, blkAST);
		if ( expr instanceof TestSetInline ) {
			TestSetInline e = (TestSetInline)expr;
			Decl d = new TokenTypeDecl(factory, e.varName);
			factory.currentRule.peek().addDecl(d);
			CaptureNextTokenType nextType = new CaptureNextTokenType(e.varName);
			addPreambleOp(nextType);
		}
	}
}
