package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

public class StarBlock extends Loop {
	public String loopLabel;

	public StarBlock(CoreOutputModelFactory factory,
					 GrammarAST blkOrEbnfRootAST,
					 List<CodeBlock> alts)
	{
		super(factory, blkOrEbnfRootAST, alts);
		loopLabel = factory.gen.target.getLoopLabel(blkOrEbnfRootAST);
		BlockStartState star = (BlockStartState)blkOrEbnfRootAST.atnState;
		decision = star.decision;
		exitAlt = alts.size()+1;
	}
}
