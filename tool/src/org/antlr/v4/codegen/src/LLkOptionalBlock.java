package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.BlockAST;

import java.util.List;

/** */
public class LLkOptionalBlock extends OptionalBlock {
	public LLkOptionalBlock(OutputModelFactory factory, BlockAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
	}
}
