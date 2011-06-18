package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

public class PlusBlock extends Loop {
	@ModelElement public ThrowNoViableAlt error;

	public PlusBlock(OutputModelFactory factory,
					 GrammarAST ebnfRootAST,
					 List<CodeBlock> alts)
	{
		super(factory, ebnfRootAST, alts);
		PlusLoopbackState loop = ((PlusBlockStartState)ebnfRootAST.atnState).loopBackState;
		this.error = new ThrowNoViableAlt(factory, ebnfRootAST, null);
		decision = loop.decision;
		exitAlt = alts.size()+1;
	}
}
