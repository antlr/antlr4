package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class OptionalBlock extends AltBlock {
	public OptionalBlock(OutputModelFactory factory,
						 GrammarAST questionAST,
						 List<CodeBlock> alts)
	{
		super(factory, questionAST, alts);
	}
}
