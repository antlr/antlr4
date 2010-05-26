package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public abstract class OptionalBlock extends Choice {
	public OptionalBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
//		this.alts = alts;
//		this.decision = -999;
//
//		// TODO: use existing lookahead! don't compute
//		LinearApproximator approx = new LinearApproximator(factory.g, decision);
//		NFAState decisionState = ast.nfaState;
//		expecting = approx.FIRST(decisionState);
//		System.out.println(blkAST.toStringTree()+" choice expecting="+expecting);
//		
	}
}
