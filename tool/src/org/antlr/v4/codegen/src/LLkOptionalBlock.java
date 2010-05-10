package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.BlockAST;

import java.util.List;

/** */
public class LLkOptionalBlock extends OptionalBlock {
	public LLkOptionalBlock(CodeGenerator gen, BlockAST blkAST, List<CodeBlock> alts) {
		super(gen, blkAST, alts);
	}
}
