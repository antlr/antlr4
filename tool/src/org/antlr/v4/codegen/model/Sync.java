package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class Sync extends SrcOp {
	public int decision;
	public BitSetDecl expecting;
	public Sync(OutputModelFactory factory,
				GrammarAST blkOrEbnfRootAST,
				IntervalSet expecting,
				int decision,
				String position)
	{
		super(factory, blkOrEbnfRootAST);
		this.decision = decision;
		this.expecting = factory.createExpectingBitSet(ast, decision, expecting, position);
		factory.defineBitSet(this.expecting);
	}
}
