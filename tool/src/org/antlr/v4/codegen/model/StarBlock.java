package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

public class StarBlock extends Loop {
	public String loopLabel;

	public StarBlock(OutputModelFactory factory,
					 GrammarAST blkOrEbnfRootAST,
					 List<SrcOp> alts)
	{
		super(factory, blkOrEbnfRootAST, alts);
		loopLabel = factory.getGenerator().target.getLoopLabel(blkOrEbnfRootAST);
		BlockStartState star = (BlockStartState)blkOrEbnfRootAST.atnState;
		decision = star.decision;
		exitAlt = alts.size()+1;
	}
}
