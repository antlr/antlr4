package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.BlockAST;

import java.util.List;

/** */
public class LLStarOptionalBlock extends OptionalBlock {
	public DFADef dfaDef;
	public LLStarOptionalBlock(CodeGenerator gen, BlockAST blkAST, List<CodeBlock> alts) {
		super(gen, blkAST, alts);
		dfaDef = gen.defineDFA(ast, gen.g.decisionDFAs.get(decision));
	}
}
