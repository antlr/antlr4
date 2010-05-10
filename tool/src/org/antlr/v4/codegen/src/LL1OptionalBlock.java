package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class LL1OptionalBlock extends LL1Choice {
	public LL1OptionalBlock(CodeGenerator gen, GrammarAST blkAST, List<CodeBlock> alts) {
		super(gen, blkAST, alts);
	}
}
