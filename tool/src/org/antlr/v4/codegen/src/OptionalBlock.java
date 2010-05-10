package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public abstract class OptionalBlock extends Choice {
	public OptionalBlock(CodeGenerator gen, GrammarAST blkAST, List<CodeBlock> alts) {
		super(gen, blkAST, alts);
	}
}
