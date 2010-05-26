package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.BlockAST;

import java.util.List;

/** */
public class LLStarOptionalBlock extends Choice {
	public DFADecl dfaDecl;
	public LLStarOptionalBlock(OutputModelFactory factory, BlockAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		dfaDecl = factory.defineDFA(ast, factory.g.decisionDFAs.get(decision));
	}
}
