package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

public class Loop extends Choice {
	public int exitAlt;
	public Loop(CoreOutputModelFactory factory,
				GrammarAST blkOrEbnfRootAST,
				List<CodeBlock> alts)
	{
		super(factory, blkOrEbnfRootAST, alts);
	}
}
