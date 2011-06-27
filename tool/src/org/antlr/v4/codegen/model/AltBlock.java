package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

public class AltBlock extends Choice {
	@ModelElement public ThrowNoViableAlt error;

	public AltBlock(CoreOutputModelFactory factory,
					GrammarAST blkOrEbnfRootAST,
					List<CodeBlock> alts)
	{
		super(factory, blkOrEbnfRootAST, alts);
		decision = ((BlockStartState)blkOrEbnfRootAST.atnState).decision;
		this.error = new ThrowNoViableAlt(factory, blkOrEbnfRootAST, null);
	}
}
