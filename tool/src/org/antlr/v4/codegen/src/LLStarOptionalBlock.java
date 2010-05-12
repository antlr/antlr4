package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.BlockAST;

import java.util.List;

/** */
public class LLStarOptionalBlock extends OptionalBlock {
	public DFADef dfaDef;
	public LLStarOptionalBlock(OutputModelFactory factory, BlockAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		dfaDef = factory.defineDFA(ast, factory.g.decisionDFAs.get(decision));
	}
}
