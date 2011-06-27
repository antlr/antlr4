package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

public class Loop extends Choice {
	public int exitAlt;
	public Loop(OutputModelFactory factory,
				GrammarAST blkOrEbnfRootAST,
				List<SrcOp> alts)
	{
		super(factory, blkOrEbnfRootAST, alts);
	}
}
